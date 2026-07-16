package dev.uebelacker.babeli.core;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;
import static org.mockito.Mockito.verify;

import dev.uebelacker.babeli.core.ai.ChatModelFactory;
import dev.uebelacker.babeli.core.logging.LoggingProvider;
import dev.uebelacker.babeli.core.util.EnvUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BabeliTest {

  @Mock LoggingProvider loggingProvider;

  Configuration configuration;

  @BeforeEach
  void setUp() {
    configuration = new Configuration();
    configuration.setModelProvider("test");
    configuration.setLoggingProvider(loggingProvider);
    EnvUtils.set(BABELI_MODEL_PROVIDER, "test");
    ChatModelFactory.createChatModel(configuration);
  }

  @AfterEach
  void tearDown() {
    EnvUtils.reset();
  }

  @Test
  @DisplayName("should skip validation execution")
  void shouldSkipValidationExecution() {
    configuration.setSkip(true);
    Babeli.validate(configuration);
    verify(loggingProvider).info("Babeli is skipped.");
  }

  @Test
  @DisplayName("should skip update execution")
  void shouldSkipUpdateExecution() {
    configuration.setSkip(true);
    Babeli.update(configuration);
    verify(loggingProvider).info("Babeli is skipped.");
  }

  @Test
  @DisplayName("should skip update execution if no model provider was provided")
  void shouldSkipUpdateExecutionIfNoModelProviderWasProvided() {
    configuration.setModelProvider(null);
    EnvUtils.ignore(BABELI_MODEL_PROVIDER);
    Babeli.update(configuration);
    verify(loggingProvider)
        .warn(
            "No model provider specified. Babeli requires a model provider to function. Please specify a model provider using 'modelProvider' in the configuration or specify it as environment variable BABELI_MODEL_PROVIDER. Skipping execution.");
  }
}

