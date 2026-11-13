package dev.uebelacker.babeli.cli.commands;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.ChatModelProvider;

/** Answers every translation request with {@value #TRANSLATION}. */
public class TestChatModelProvider implements ChatModelProvider {
  public static final String TRANSLATION = "AI Translation";

  @Override
  public ChatModel create(Configuration configuration) {
    return new ChatModel() {
      @Override
      public ChatResponse doChat(ChatRequest chatRequest) {
        return ChatResponse.builder().aiMessage(AiMessage.from(TRANSLATION)).build();
      }
    };
  }
}
