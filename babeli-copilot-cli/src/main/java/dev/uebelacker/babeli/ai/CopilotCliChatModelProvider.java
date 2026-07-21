package dev.uebelacker.babeli.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.ChatModelProvider;

public class CopilotCliChatModelProvider implements ChatModelProvider {

    @Override
    public ChatModel create(Configuration configuration) {
        return new CopilotCliModel(configuration);
    }
}
