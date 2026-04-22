package dev.uebelacker.babeli.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BabeliCliTest {
  @Test
  @DisplayName("should show usage")
  void shouldShowUsage() {
    assertThat(BabeliCli.execute(new String[] {})).isEqualTo(1);
  }

  @Test
  @DisplayName("should run validate")
  void shouldRunValidate() {
    assertThat(BabeliCli.execute(new String[] {"validate", "-f", "src/test/resources/test.json"}))
        .isZero();
  }

  @Test
  @DisplayName("should run update")
  void shouldRunValidateWithArgs() {
    assertThat(BabeliCli.execute(new String[] {"update", "-f", "src/test/resources/test.json"}))
        .isZero();
  }
}
