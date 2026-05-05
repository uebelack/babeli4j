package dev.uebelacker.babeli.maven;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BabeliLoggingProviderTest {

  Log log;
  BabeliLoggingProvider babeliLoggingProvider;

  @BeforeEach
  void setUp() {
    log = mock(Log.class);
    babeliLoggingProvider = new BabeliLoggingProvider(log);
  }

  @Test
  @DisplayName("should log debug message")
  void shouldLogDebugMessage() {
    babeliLoggingProvider.debug("debug message");
    verify(log).debug("babeli: debug message");
  }

  @Test
  @DisplayName("should log info message")
  void shouldLogInfoMessage() {
    babeliLoggingProvider.info("info message");
    verify(log).info("babeli: info message");
  }

  @Test
  @DisplayName("should log error message")
  void shouldLogErrorMessage() {
    babeliLoggingProvider.error("error message");
    verify(log).error("babeli: error message");
  }
}
