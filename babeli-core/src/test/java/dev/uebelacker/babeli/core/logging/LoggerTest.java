package dev.uebelacker.babeli.core.logging;

import static org.mockito.Mockito.*;

import dev.uebelacker.babeli.core.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoggerTest {

  Configuration configuration;
  Logger logger;
  LoggingProvider loggingProvider;

  @BeforeEach
  void setUp() {
    loggingProvider = mock(LoggingProvider.class);
    configuration = new Configuration();
    configuration.setLoggingProvider(loggingProvider);
    logger = new Logger(configuration);
  }

  @Test
  @DisplayName("should log error message")
  void shouldLogErrorMessage() {
    logger.error("error message");
    verify(loggingProvider).error("error message");
  }

  @Test
  @DisplayName("should log warn message")
  void shouldLogWarnMessage() {
    logger.error("warn message");
    verify(loggingProvider).error("warn message");
  }

  @Test
  @DisplayName("should log info message")
  void shouldLogInfoMessage() {
    logger.info("info message");
    verify(loggingProvider).info("info message");
  }

  @Test
  @DisplayName("should log debug message")
  void shouldLogDebugMessage() {
    configuration.setDebug(true);
    logger.debug("debug message");
    verify(loggingProvider).debug("debug message");
  }

  @Test
  @DisplayName("should not log debug message")
  void shouldNotLogDebugMessage() {
    configuration.setDebug(false);
    logger.debug("debug message");
    verify(loggingProvider, never()).debug(any());
  }
}
