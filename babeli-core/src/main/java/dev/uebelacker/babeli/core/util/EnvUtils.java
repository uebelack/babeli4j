package dev.uebelacker.babeli.core.util;

import dev.uebelacker.babeli.core.exceptions.ConfigurationException;

public class EnvUtils {
  private EnvUtils() {}

  public static String get(String envVarName) {
    var value = System.getenv(envVarName);
    if (value == null) {
      throw new ConfigurationException("Missing required environment variable: " + envVarName);
    }
    return value;
  }

  public static String get(String envVarName, String defaultValue) {
    var value = System.getenv(envVarName);
    return value != null ? value : defaultValue;
  }
}
