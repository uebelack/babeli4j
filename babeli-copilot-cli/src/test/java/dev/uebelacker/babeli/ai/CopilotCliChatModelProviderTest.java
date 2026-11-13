package dev.uebelacker.babeli.ai;

import static org.assertj.core.api.Assertions.assertThat;

import dev.uebelacker.babeli.core.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CopilotCliChatModelProviderTest {

  @Test
  @DisplayName("should create a copilot cli model")
  void shouldCreateCopilotCliModel() {
    var chatModel = new CopilotCliChatModelProvider().create(new Configuration());

    assertThat(chatModel).isInstanceOf(CopilotCliModel.class);
  }
}
