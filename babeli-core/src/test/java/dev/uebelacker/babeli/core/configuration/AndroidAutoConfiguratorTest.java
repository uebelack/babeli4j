package dev.uebelacker.babeli.core.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import dev.uebelacker.babeli.core.Configuration;
import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AndroidAutoConfiguratorTest {
  @Test
  @DisplayName("should return false if working directory does not contain an android project")
  void shouldReturnFalsIfWorkingDirectoryDoesNotContainAndroidProject() {
    var androidAutoConfigurator = new AndroidAutoConfigurator();
    assertThat(androidAutoConfigurator.matches(new Configuration())).isFalse();
  }

  @Test
  @DisplayName(
      "should return true and update configuration if working directory contains an android project")
  void shouldReturnTrueAndUpdateConfigurationIfWorkingDirectoryContainsAndroidProject() {
    var androidAutoConfigurator = new AndroidAutoConfigurator();
    var configuration = new Configuration();
    configuration.setWorkingDirectory(new File("src/test/resources/auto/android"));
    assertThat(androidAutoConfigurator.matches(configuration)).isTrue();
    assertThat(androidAutoConfigurator.configure(configuration).getFirst().getFiles()).hasSize(2);
  }
}
