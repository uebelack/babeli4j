package dev.uebelacker.babeli.ai;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_COPILOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.logging.LoggingProvider;
import dev.uebelacker.babeli.core.util.EnvUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CopilotCliModelTest {

  @Mock LoggingProvider loggingProvider;

  Configuration configuration;

  @BeforeEach
  void setUp() {
    EnvUtils.reset();
    configuration = new Configuration();
    configuration.setLoggingProvider(loggingProvider);
  }

  @AfterEach
  void tearDown() {
    EnvUtils.reset();
  }

  @Test
  @DisplayName("should return the copilot output as ai message")
  void shouldReturnCopilotOutputAsAiMessage() {
    var model = new StubModel(configuration, process("Bonjour", "", 0));

    var response = model.doChat(request(UserMessage.from("Translate hello")));

    assertThat(response.aiMessage().text()).isEqualTo("Bonjour");
  }

  @Test
  @DisplayName("should build the prompt from system and user messages")
  void shouldBuildPromptFromSystemAndUserMessages() {
    var model = new StubModel(configuration, process("ok", "", 0));

    model.doChat(
        request(SystemMessage.from("You are a translator"), UserMessage.from("Translate hello")));

    assertThat(model.prompts)
        .containsExactly("System:\nYou are a translator\n\nUser:\nTranslate hello");
  }

  @Test
  @DisplayName("should ignore message types other than system and user")
  void shouldIgnoreOtherMessageTypes() {
    var model = new StubModel(configuration, process("ok", "", 0));

    model.doChat(request(AiMessage.from("previous answer"), UserMessage.from("Translate hello")));

    assertThat(model.prompts).containsExactly("User:\nTranslate hello");
  }

  @Test
  @DisplayName("should use the executable configured via BABELI_COPILOT_PATH")
  void shouldUseConfiguredExecutable() {
    EnvUtils.set(BABELI_COPILOT_PATH, "/usr/local/bin/copilot");

    var command = new CopilotCliModel(configuration).command("a prompt");

    assertThat(command).containsExactly("/usr/local/bin/copilot", "-p", "a prompt");
  }

  @Test
  @DisplayName("should fall back to resolving copilot from the PATH")
  void shouldFallBackToCopilotOnPath() {
    var command = new CopilotCliModel(configuration).command("a prompt");

    assertThat(command).containsExactly("copilot", "-p", "a prompt");
  }

  @Test
  @DisplayName("should execute the configured executable and read its output")
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void shouldExecuteConfiguredExecutable() {
    EnvUtils.set(BABELI_COPILOT_PATH, "/bin/echo");

    var response =
        new CopilotCliModel(configuration).doChat(request(UserMessage.from("Translate hello")));

    assertThat(response.aiMessage().text()).contains("User:", "Translate hello");
  }

  @Test
  @DisplayName("should fail with the stderr output when copilot exits with a non zero code")
  void shouldFailWithStderrWhenExitCodeIsNonZero() {
    var model = new StubModel(configuration, process("", "not logged in", 1));

    var chatRequest = request(UserMessage.from("Translate hello"));

    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> model.doChat(chatRequest))
        .withMessage("copilot command failed with exit code 1: not logged in");
  }

  @Test
  @DisplayName("should fail without details when copilot exits with a non zero code and no stderr")
  void shouldFailWithoutDetailsWhenStderrIsBlank() {
    var model = new StubModel(configuration, process("", "  ", 2));

    var chatRequest = request(UserMessage.from("Translate hello"));

    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> model.doChat(chatRequest))
        .withMessage("copilot command failed with exit code 2");
  }

  @Test
  @DisplayName("should wrap an io exception raised while starting copilot")
  void shouldWrapIoException() {
    var model =
        new CopilotCliModel(configuration) {
          @Override
          protected Process startProcess(String prompt) throws IOException {
            throw new IOException("copilot not installed");
          }
        };

    var chatRequest = request(UserMessage.from("Translate hello"));

    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> model.doChat(chatRequest))
        .withMessage("Failed to execute copilot command")
        .withCauseInstanceOf(IOException.class);
  }

  @Test
  @DisplayName("should restore the interrupt flag when waiting for copilot is interrupted")
  void shouldRestoreInterruptFlagWhenInterrupted() throws InterruptedException {
    var process = mock(Process.class);
    when(process.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
    when(process.getErrorStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
    when(process.waitFor()).thenThrow(new InterruptedException("interrupted"));

    var model = new StubModel(configuration, process);

    try {
      var chatRequest = request(UserMessage.from("Translate hello"));

      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() -> model.doChat(chatRequest))
          .withMessage("Execution of copilot command was interrupted")
          .withCauseInstanceOf(InterruptedException.class);

      assertThat(Thread.currentThread().isInterrupted()).isTrue();
    } finally {
      // clear the flag so it does not leak into other tests
      Thread.interrupted();
    }
  }

  private ChatRequest request(ChatMessage... messages) {
    return ChatRequest.builder().messages(messages).build();
  }

  private Process process(String output, String error, int exitCode) {
    var process = mock(Process.class);
    when(process.getInputStream())
        .thenReturn(new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8)));
    when(process.getErrorStream())
        .thenReturn(new ByteArrayInputStream(error.getBytes(StandardCharsets.UTF_8)));
    try {
      when(process.waitFor()).thenReturn(exitCode);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
    return process;
  }

  /** Captures the prompt and returns a canned process instead of executing the copilot CLI. */
  private static class StubModel extends CopilotCliModel {

    private final Process process;
    private final List<String> prompts = new ArrayList<>();

    StubModel(Configuration configuration, Process process) {
      super(configuration);
      this.process = process;
    }

    @Override
    protected Process startProcess(String prompt) {
      prompts.add(prompt);
      return process;
    }
  }
}
