package dev.uebelacker.babeli.core.ai;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S5778")
class ChatModelFactoryTest {

  @BeforeEach
  void setUp() {
    EnvUtils.reset();
    // Reset the singleton instance before each test
    try {
      var chatModelField = ChatModelFactory.class.getDeclaredField("chatModel");
      chatModelField.setAccessible(true);
      chatModelField.set(null, null);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to reset ChatModelFactory singleton instance", e);
    }
  }

  @AfterEach
  void tearDown() {
    EnvUtils.reset();
  }

  @Test
  @DisplayName("should throw exception if model provider class does not exist")
  void shouldThrowExceptionIfModelProviderClassDoesNotExist() {
    EnvUtils.set(BABELI_MODEL_PROVIDER, "nonexistent");
    var configuration = new Configuration();
    configuration.setModelProvider("nonexistent");
    assertThatExceptionOfType(ConfigurationException.class)
        .isThrownBy(() -> ChatModelFactory.createChatModel(configuration))
        .withMessage(
            "Model provider class not found: dev.uebelacker.babeli.ai.NonexistentChatModelProvider");
  }

  @Test
  @DisplayName("should throw exception if model provider class does not have default constructor")
  void shouldThrowExceptionIfModelProviderClassDoesNotHaveDefaultConstructor() {
    EnvUtils.set(BABELI_MODEL_PROVIDER, ChatModelFactoryTest.class.getName());
    var configuration = new Configuration();
    configuration.setModelProvider(ChatModelFactoryTest.class.getName());
    assertThatExceptionOfType(ConfigurationException.class)
        .isThrownBy(() -> ChatModelFactory.createChatModel(configuration))
        .withMessage(
            "Model provider class does not have a default constructor: dev.uebelacker.babeli.core.ai.ChatModelFactoryTest");
  }

  @Test
  @DisplayName("should return the same chat model instance on multiple calls")
  void shouldReturnTheSameChatModelInstanceOnMultipleCalls() {
    EnvUtils.set(BABELI_MODEL_PROVIDER, "test");
    var configuration = new Configuration();
    configuration.setModelProvider("test");
    var chatModel1 = ChatModelFactory.createChatModel(configuration);
    var chatModel2 = ChatModelFactory.createChatModel(configuration);

    assertThat(chatModel1).isEqualTo(chatModel2);
  }
}
