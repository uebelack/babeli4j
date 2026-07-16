package dev.uebelacker.babeli.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;

class AnthropicAiProviderTest {

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
        var provider = new AnthropicAiProvider();
        var chatModel = provider.create(new Configuration());
        assertThat(chatModel).isNotNull();
    }

    @Test
    @DisplayName("should throw exception if BABELI_ANTHROPIC_API_KEY is not set")
    @SuppressWarnings("java:S5778")
    void shouldThrowExceptionIfApiKeyIsNotSet() {
        EnvUtils.ignore("ANTHROPIC_API_KEY", "BABELI_ANTHROPIC_API_KEY");

        var provider = new AnthropicAiProvider();

        assertThatExceptionOfType(ConfigurationException.class)
                .isThrownBy(() -> provider.create(new Configuration()))
                .withMessage(
                        "Missing required api key for anthropic chat model provider, please provide apiKey in configuration or define it as environment variable BABELI_ANTHROPIC_API_KEY or ANTHROPIC_API_KEY");
    }
}
