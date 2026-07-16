package dev.uebelacker.babeli.core.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.Configuration;

public interface AiProvider {
    ChatModel create(Configuration configuration);
}
