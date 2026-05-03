package dev.uebelacker.babeli.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;
import org.junit.jupiter.api.*;

class AnthropicChatModelProviderTest {

  @AfterAll
  static void tearDown() {
    EnvUtils.reset();
  }

  @BeforeEach
  void setUp() {
    EnvUtils.reset();
  }

  @Test
  @DisplayName("should create chat model")
  void shouldCreateChatModel() {
    EnvUtils.set("BABELI_ANTHROPIC_API_KEY", "test-api-key");
    var provider = new AnthropicChatModelProvider();
    var chatModel = provider.create(new Configuration());
    assertThat(chatModel).isNotNull();
  }

  @Test
  @DisplayName("should throw exception if BABELI_ANTHROPIC_API_KEY is not set")
  void shouldThrowExceptionIfApiKeyIsNotSet() {
    EnvUtils.ignore("ANTHROPIC_API_KEY", "BABELI_ANTHROPIC_API_KEY");

    var provider = new AnthropicChatModelProvider();

    assertThatExceptionOfType(ConfigurationException.class)
        .isThrownBy(() -> provider.create(new Configuration()))
        .withMessage(
            "Missing required api key for anthropic chat model provider, please provide apiKey in configuration or define it as environment variable BABELI_ANTHROPIC_API_KEY or ANTHROPIC_API_KEY");
  }
}
