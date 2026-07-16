package dev.uebelacker.babeli.ai;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.AiProvider;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;

public class AnthropicAiProvider implements AiProvider {
    @Override
    public ChatModel create(Configuration configuration) {
        var apiKey =
                EnvUtils.get(
                        "BABELI_ANTHROPIC_API_KEY",
                        configuration.getApiKey(),
                        EnvUtils.get("ANTHROPIC_API_KEY"));

        if (apiKey == null) {
            throw new ConfigurationException(
                    "Missing required api key for anthropic chat model provider, please provide apiKey in configuration or define it as environment variable BABELI_ANTHROPIC_API_KEY or ANTHROPIC_API_KEY");
        }

        var model = EnvUtils.get("BABELI_MODEL", configuration.getModel(), "claude-sonnet-4-20250514");
        return AnthropicChatModel.builder().apiKey(apiKey).modelName(model).build();
    }
}
