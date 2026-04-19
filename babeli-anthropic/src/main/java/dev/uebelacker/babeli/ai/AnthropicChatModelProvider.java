package dev.uebelacker.babeli.ai;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.ai.ChatModelProvider;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;

public class AnthropicChatModelProvider implements ChatModelProvider {
  @Override
  public ChatModel create() {
    var apiKey = EnvUtils.get("BABELI_ANTHROPIC_API_KEY");

    if (apiKey == null) {
      throw new ConfigurationException(
          "Missing required environment variable: BABELI_ANTHROPIC_API_KEY");
    }

    var model = EnvUtils.get("BABELI_ANTHROPIC_MODEL", "claude-sonnet-4-20250514");
    return AnthropicChatModel.builder().apiKey(apiKey).modelName(model).build();
  }
}
