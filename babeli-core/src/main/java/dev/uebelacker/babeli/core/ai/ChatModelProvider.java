package dev.uebelacker.babeli.core.ai;

import dev.langchain4j.model.chat.ChatModel;

public interface ChatModelProvider {
  ChatModel create();
}
