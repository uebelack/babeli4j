package dev.uebelacker.babeli.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnvUtilsTest {

  @AfterAll
  static void tearDown() {
    EnvUtils.reset();
  }

  @BeforeEach
  void setUp() {
    EnvUtils.reset();
  }

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

    value = EnvUtils.get("NON_EXISTENT_ENV_VAR", null, defaultValue);
    assertThat(value).isEqualTo(defaultValue);

    value = EnvUtils.get("NON_EXISTENT_ENV_VAR", null, null);
    assertThat(value).isNull();
  }

  @Test
  @DisplayName("should ignore environment variable")
  void shouldIgnoreEnvironmentVariable() {
    EnvUtils.ignore("PATH");
    assertThat(EnvUtils.get("PATH")).isNull();
    assertThat(EnvUtils.get("PATH", "test")).isNull();
    assertThat(EnvUtils.get("PATH", null, "test")).isNull();
  }

  @Test
  @DisplayName("should allow to set environment variables")
  void shouldAllowToSetEnvironmentVariables() {
    assertThat(EnvUtils.get("NOT_EXISTING_ENV_VAR")).isNull();
    EnvUtils.set("NOT_EXISTING_ENV_VAR", "NOT_EXISTING_VALUE");
    assertThat(EnvUtils.get("NOT_EXISTING_ENV_VAR")).isEqualTo("NOT_EXISTING_VALUE");
  }
}
