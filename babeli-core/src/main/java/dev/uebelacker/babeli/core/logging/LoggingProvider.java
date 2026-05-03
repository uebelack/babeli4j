package dev.uebelacker.babeli.core.logging;

public interface LoggingProvider {
  void info(String message);

  void error(String message);

  void debug(String message);
}
