package dev.uebelacker.babeli.core.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;

@SuppressWarnings("java:S5778")
class AiFactoryTest {

    @BeforeEach
    void setUp() {
        EnvUtils.reset();
        // Reset the singleton instance before each test
        try {
            var chatModelField = AiFactory.class.getDeclaredField("chatModel");
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
                .isThrownBy(() -> AiFactory.createChatModel(configuration))
                .withMessage(
                        "Model provider class not found: dev.uebelacker.babeli.ai.NonexistentAiProvider");
    }

    @Test
    @DisplayName("should throw exception if model provider class does not have default constructor")
    void shouldThrowExceptionIfModelProviderClassDoesNotHaveDefaultConstructor() {
        EnvUtils.set(BABELI_MODEL_PROVIDER, AiFactoryTest.class.getName());
        var configuration = new Configuration();
        configuration.setModelProvider(AiFactoryTest.class.getName());
        assertThatExceptionOfType(ConfigurationException.class)
                .isThrownBy(() -> AiFactory.createChatModel(configuration))
                .withMessage(
                        "Model provider class does not have a default constructor: dev.uebelacker.babeli.core.ai.AiFactoryTest");
    }

    @Test
    @DisplayName("should return the same chat model instance on multiple calls")
    void shouldReturnTheSameChatModelInstanceOnMultipleCalls() {
        EnvUtils.set(BABELI_MODEL_PROVIDER, "test");
        var configuration = new Configuration();
        configuration.setModelProvider("test");
        var chatModel1 = AiFactory.createChatModel(configuration);
        var chatModel2 = AiFactory.createChatModel(configuration);

        assertThat(chatModel1).isEqualTo(chatModel2);
    }
}
