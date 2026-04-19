package dev.uebelacker.babeli.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OllamaChatModelProviderTest {

  @Test
  @DisplayName("should create chat model")
  void shouldCreateChatModel() {
    EnvUtils.set("BABELI_OLLAMA_MODEL", "test-model");
    var provider = new OllamaChatModelProvider();
    var chatModel = provider.create();
    assertThat(chatModel).isNotNull();
  }

  @Test
  @DisplayName("should throw exception if BABELI_OLLAMA_MODEL is not set")
  void shouldThrowExceptionIfModelIsNotSet() {
    EnvUtils.remove("BABELI_OLLAMA_MODEL");
    var provider = new OllamaChatModelProvider();

    assertThatExceptionOfType(ConfigurationException.class)
        .isThrownBy(provider::create)
        .withMessage("Missing required environment variable: BABELI_OLLAMA_MODEL");
  }
}
