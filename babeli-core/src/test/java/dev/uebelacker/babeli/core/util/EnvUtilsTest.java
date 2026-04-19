package dev.uebelacker.babeli.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnvUtilsTest {

  @Test
  @DisplayName("should return environment variable value if it exists")
  void shouldReturnEnvironmentVariableValueIfItExists() {
    assertThat(EnvUtils.get("PATH")).isNotEmpty();
  }

  @Test
  @DisplayName("should return default value if environment variable is missing")
  void shouldReturnDefaultValueIfEnvironmentVariableIsMissing() {
    var defaultValue = "default";
    var value = EnvUtils.get("NON_EXISTENT_ENV_VAR", defaultValue);
    assertThat(value).isEqualTo(defaultValue);
  }
}
