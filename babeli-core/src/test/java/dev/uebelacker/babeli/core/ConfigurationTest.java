package dev.uebelacker.babeli.core;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import java.io.File;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfigurationTest {

  Configuration configuration;

  @BeforeEach
  void setUp() {
    configuration = new Configuration();
  }

  @Test
  @DisplayName("should validate file configuration to have file or files specified")
  void shouldValidateFileConfigurationToHaveFileOrFiles() {
    assertThatThrownBy(configuration::validate)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "No files specified in the configuration. Please specify either 'file' or 'files'.");

    configuration.setFiles(Set.of());

    assertThatThrownBy(configuration::validate)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "No files specified in the configuration. Please specify either 'file' or 'files'.");
  }

  @Test
  @DisplayName("should validate file configuration to not have both file and files specified")
  void shouldValidateFileConfigurationToNotHaveBothFileAndFiles() {
    configuration.setFile(new File("test.json"));
    configuration.setFiles(
        Set.of(new LanguageFileConfiguration("en", new File("test.properties"))));

    assertThatThrownBy(configuration::validate)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "Both 'file' and 'files' are specified in the configuration. Please specify only one of them.");
  }

  @Test
  @DisplayName("should validate file configuration to have at least one file specified")
  void shouldValidateFileConfigurationToHaveAtLeastOneFileSpecified() {
    configuration.setFiles(Set.of());

    assertThatThrownBy(configuration::validate)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "No files specified in the configuration. Please specify either 'file' or 'files'.");
  }

  @Test
  @DisplayName("should validate and ensure that all files have the same file extension")
  void shouldValidateAndEnsureThatAllFilesHaveTheSameFileExtension() {
    configuration.setFiles(
        Set.of(
            new LanguageFileConfiguration("en", new File("test.properties")),
            new LanguageFileConfiguration("de", new File("test.json"))));

    assertThatThrownBy(configuration::validate)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "All files in 'files' must have the same extension. Please ensure all files have the same extension.");
  }

  @Test
  @DisplayName("should validate successfully when file configuration with one file is correct")
  void shouldValidateSuccessfullyWhenFileConfigurationWithOneFileIsCorrect() {
    configuration.setFile(new File("test.json"));
    assertThatNoException().isThrownBy(configuration::validate);
  }

  @Test
  @DisplayName(
      "should validate successfully when file configuration with multiple files is correct")
  void shouldValidateSuccessfullyWhenFileConfigurationWithMultipleFilesIsCorrect() {
    configuration.setFiles(
        Set.of(
            new LanguageFileConfiguration("en", new File("test_en.properties")),
            new LanguageFileConfiguration("de", new File("test_de.properties"))));
    assertThatNoException().isThrownBy(configuration::validate);
  }
}
