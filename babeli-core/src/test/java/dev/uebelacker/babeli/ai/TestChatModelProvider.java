package dev.uebelacker.babeli.ai;

import static org.mockito.Mockito.mock;

import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.ChatModelProvider;

public class TestChatModelProvider implements ChatModelProvider {

  @Override
  public ChatModel create(Configuration configuration) {
    return mock(ChatModel.class);
  }
}
