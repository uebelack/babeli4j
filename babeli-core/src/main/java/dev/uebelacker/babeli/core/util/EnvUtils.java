package dev.uebelacker.babeli.core.util;

import java.util.*;

public class EnvUtils {

  private static final Map<String, String> ENVS = new HashMap<>();
  private static final Set<String> IGNORED_KEYS = new HashSet<>();

  private EnvUtils() {}

  public static String get(String envVarName) {
    return get(envVarName, null);
  }

  public static String get(String envVarName, String defaultValue1, String defaultValue2) {
    if (IGNORED_KEYS.contains(envVarName)) {
      return null;
    }
    return Optional.ofNullable(ENVS.get(envVarName))
        .orElse(
            Optional.ofNullable(System.getenv(envVarName))
                .orElse(defaultValue1 != null ? defaultValue1 : defaultValue2));
  }

  public static String get(String envVarName, String defaultValue) {
    if (IGNORED_KEYS.contains(envVarName)) {
      return null;
    }
    return Optional.ofNullable(ENVS.get(envVarName))
        .orElse(Optional.ofNullable(System.getenv(envVarName)).orElse(defaultValue));
  }

  public static void set(String envVarName, String value) {
    ENVS.put(envVarName, value);
  }

  public static void reset() {
    ENVS.clear();
    IGNORED_KEYS.clear();
  }

  public static void ignore(String... envVarName) {
    IGNORED_KEYS.addAll(Arrays.asList(envVarName));
  }
}
