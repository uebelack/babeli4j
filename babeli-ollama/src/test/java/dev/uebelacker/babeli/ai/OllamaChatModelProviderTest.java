package dev.uebelacker.babeli.ai;

import static org.assertj.core.api.Assertions.assertThat;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.util.EnvUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OllamaChatModelProviderTest {

  @Test
  @DisplayName("should create chat model")
  void shouldCreateChatModel() {
    EnvUtils.set("BABELI_MODEL", "test-model");
    var provider = new OllamaChatModelProvider();
    var chatModel = provider.create(new Configuration());
    assertThat(chatModel).isNotNull();
  }
}
