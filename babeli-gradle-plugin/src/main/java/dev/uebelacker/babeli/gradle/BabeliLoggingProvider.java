package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.logging.LoggingProvider;
import org.gradle.api.logging.Logger;

public class BabeliLoggingProvider implements LoggingProvider {

  public static final String BABELI = "babeli: {}";
  private final Logger logger;

  public BabeliLoggingProvider(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void info(String s) {
    logger.lifecycle(BABELI, s);
  }

  @Override
  public void error(String s) {
    logger.error(BABELI, s);
  }

  @Override
  public void debug(String s) {
    logger.debug(BABELI, s);
  }

  @Override
  public void warn(String s) {
    logger.warn(BABELI, s);
  }
}
