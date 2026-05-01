package dev.uebelacker.babeli.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnvUtils {

  private static final Map<String, String> ENVS = new HashMap<>();

  private EnvUtils() {}

  public static String get(String envVarName) {
    return get(envVarName, null);
  }

  public static String get(String envVarName, String defaultValue1, String defaultValue2) {
    return Optional.ofNullable(System.getenv(envVarName))
        .orElse(
            Optional.ofNullable(ENVS.get(envVarName))
                .orElse(defaultValue1 != null ? defaultValue1 : defaultValue2));
  }

  public static String get(String envVarName, String defaultValue) {
    return Optional.ofNullable(System.getenv(envVarName))
        .orElse(Optional.ofNullable(ENVS.get(envVarName)).orElse(defaultValue));
  }

  public static void set(String envVarName, String value) {
    ENVS.put(envVarName, value);
  }

  public static void remove(String envVarName) {
    ENVS.remove(envVarName);
  }
}
