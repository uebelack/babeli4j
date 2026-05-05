package dev.uebelacker.babeli.maven;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.uebelacker.babeli.core.util.EnvUtils;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@MojoTest
class BabeliUpdateMojoTest {

  private static final File TEST_DIRECTORY = new File("target/babeli-test-project");

  @AfterAll
  static void tearDown() {
    EnvUtils.reset();
  }

  @BeforeEach
  void setUp() throws IOException {
    EnvUtils.reset();
    FileUtils.deleteDirectory(TEST_DIRECTORY);
    FileUtils.forceMkdir(TEST_DIRECTORY);
    FileUtils.copyFileToDirectory(
        new File("src/test/resources/update/messages_en.properties"), TEST_DIRECTORY);
    FileUtils.copyFileToDirectory(
        new File("src/test/resources/update/messages_de.properties"), TEST_DIRECTORY);
  }

  @Test
  @InjectMojo(goal = "update", pom = "src/test/resources/update/plugin-pom.xml")
  @DisplayName("should update translation files")
  void shouldUpdateTranslationFiles(BabeliUpdateMojo mojo) {
    EnvUtils.ignore("CI");
    var log = mock(Log.class);
    mojo.setLog(log);
    when(TestChatModelProvider.TEST_CHAT_MODEL.chat(
            any(SystemMessage.class), any(UserMessage.class)))
        .thenReturn(
            ChatResponse.builder()
                .aiMessage(AiMessage.builder().text("Test Translation").build())
                .build());
    assertThatNoException().isThrownBy(mojo::execute);

    verify(log).info("Translation files updated.");
  }

  @Test
  @InjectMojo(goal = "update", pom = "src/test/resources/update/plugin-pom.xml")
  @DisplayName("should skip update on ci")
  void shouldSkipUpdateOnCi(BabeliUpdateMojo mojo) {
    EnvUtils.set("CI", "true");

    var log = mock(Log.class);
    mojo.setLog(log);
    assertThatNoException().isThrownBy(mojo::execute);
    verify(log).info("Babeli update skipped.");
  }
}
