package dev.uebelacker.babeli.core.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.uebelacker.babeli.core.Configuration;

class JavaPropertiesAutoConfiguratorTest {
    @Test
    @DisplayName(
            "should return false if working directory does not contain java properties files for internationalization")
    void
    shouldReturnFalseIfWorkingDirectoryDoesNotContainJavaPropertiesFilesForInternationalization() {
        var javaPropertiesAutoConfigurator = new JavaPropertiesAutoConfigurator();
        assertThat(javaPropertiesAutoConfigurator.matches(new Configuration())).isFalse();
    }

    @Test
    @DisplayName(
            "should return true and update configuration if working directory contains java properties files for internationalization")
    void
    shouldReturnTrueAndUpdateConfigurationIfWorkingDirectoryContainsJavaPropertiesFilesForInternationalization() {
        var javaPropertiesAutoConfigurator = new JavaPropertiesAutoConfigurator();
        var configuration = new Configuration();
        configuration.setWorkingDirectory(new java.io.File("src/test/resources/auto/java"));
        assertThat(javaPropertiesAutoConfigurator.matches(configuration)).isTrue();
        assertThat(configuration.getFiles()).hasSize(2);
    }
}
