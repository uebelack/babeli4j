package dev.uebelacker.babeli.maven;

import dev.uebelacker.babeli.core.logging.LoggingProvider;
import org.apache.maven.plugin.logging.Log;

public class BabeliLoggingProvider implements LoggingProvider {

  public static final String BABELI = "babeli: ";
  private final Log log;

  public BabeliLoggingProvider(Log log) {
    this.log = log;
  }

  @Override
  public void info(String message) {
    log.info(BABELI + message);
  }

  @Override
  public void error(String message) {
    log.error(BABELI + message);
  }

  @Override
  public void debug(String message) {
    log.debug(BABELI + message);
  }
}
