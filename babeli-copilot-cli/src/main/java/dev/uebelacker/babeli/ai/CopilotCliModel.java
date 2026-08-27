package dev.uebelacker.babeli.ai;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.uebelacker.babeli.core.Configuration;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class CopilotCliModel implements ChatModel {

  private final Configuration configuration;

  public CopilotCliModel(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public ChatResponse doChat(ChatRequest chatRequest) {
    var prompt = toPrompt(chatRequest);

    configuration.getLoggingProvider().debug("Executing copilot command with prompt:\n" + prompt);
    try {
      var process = Runtime.getRuntime().exec(new String[] {"copilot", "-p", prompt});
      var output =
          new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
      var error =
          new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();
      var exitCode = process.waitFor();

      if (exitCode != 0) {
        throw new IllegalStateException(
            "copilot command failed with exit code "
                + exitCode
                + (error.isBlank() ? "" : ": " + error));
      }

      return ChatResponse.builder().aiMessage(AiMessage.from(output)).build();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to execute copilot command", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Execution of copilot command was interrupted", e);
    }
  }

  private String toPrompt(ChatRequest chatRequest) {
    return chatRequest.messages().stream()
        .map(this::toPromptLine)
        .filter(line -> !line.isBlank())
        .collect(Collectors.joining("\n\n"));
  }

  private String toPromptLine(ChatMessage message) {
    if (message.type() == ChatMessageType.SYSTEM) {
      return "System:\n" + ((SystemMessage) message).text();
    }
    if (message.type() == ChatMessageType.USER) {
      return "User:\n" + ((UserMessage) message).singleText();
    }
    return "";
  }
}
