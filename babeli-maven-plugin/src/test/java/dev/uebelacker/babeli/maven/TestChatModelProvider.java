package dev.uebelacker.babeli.maven;

import static org.mockito.Mockito.mock;

import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.ChatModelProvider;

public class TestChatModelProvider implements ChatModelProvider {
  public static final ChatModel TEST_CHAT_MODEL = mock(ChatModel.class);

  @Override
  public ChatModel create(Configuration configuration) {
    return TEST_CHAT_MODEL;
  }
}
