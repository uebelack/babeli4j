package dev.uebelacker.babeli.core.logging;

@SuppressWarnings("java:S106")
public class DefaultLoggingProvider implements LoggingProvider {
  @Override
  public void info(String message) {
    System.out.println(message);
  }

  @Override
  public void error(String message) {
    System.out.println(message);
  }

  @Override
  public void debug(String message) {
    System.out.println(message);
  }

  @Override
  public void warn(String message) {
    System.out.println(message);
  }
}
