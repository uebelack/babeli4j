package dev.uebelacker.babeli.core.logging;

import dev.uebelacker.babeli.core.Configuration;

public class Logger {
  private final Configuration configuration;
  private final LoggingProvider loggingProvider;

  public Logger(Configuration configuration) {
    this.configuration = configuration;
    this.loggingProvider = configuration.getLoggingProvider();
  }

  public void debug(String message) {
    if (configuration.isDebug()) {
      loggingProvider.debug(message);
    }
  }

  public void info(String message) {
    loggingProvider.info(message);
  }

  public void error(String message) {
    loggingProvider.error(message);
  }
}
