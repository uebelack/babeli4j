package dev.uebelacker.babeli.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnthropicChatModelProviderTest {

  @Test
  @DisplayName("should create chat model")
  void shouldCreateChatModel() {
    EnvUtils.set("BABELI_ANTHROPIC_API_KEY", "test-api-key");
    var provider = new AnthropicChatModelProvider();
    var chatModel = provider.create();
    assertThat(chatModel).isNotNull();
  }

  @Test
  @DisplayName("should throw exception if BABELI_ANTHROPIC_API_KEY is not set")
  void shouldThrowExceptionIfApiKeyIsNotSet() {
    EnvUtils.remove("BABELI_ANTHROPIC_API_KEY");
    var provider = new AnthropicChatModelProvider();

    assertThatExceptionOfType(ConfigurationException.class)
        .isThrownBy(provider::create)
        .withMessage("Missing required environment variable: BABELI_ANTHROPIC_API_KEY");
  }
}
