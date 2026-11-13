package dev.uebelacker.babeli.cli.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class AbstractCommandTest {

  @Test
  @DisplayName("should map all options to the configuration")
  void shouldMapAllOptions() {
    var configuration =
        configurationFor(
            "-f",
            "messages.json",
            "-c",
            "ISO-8859-1",
            "-d",
            "project",
            "-b",
            "de",
            "-a",
            "sort,missing",
            "-p",
            "ollama",
            "-m",
            "mistral",
            "-k",
            "secret",
            "-u",
            "http://localhost:11434",
            "-v");

    assertThat(configuration.getFile()).isEqualTo(new File("messages.json"));
    assertThat(configuration.getCharset()).isEqualTo("ISO-8859-1");
    assertThat(configuration.getWorkingDirectory()).isEqualTo(new File("project"));
    assertThat(configuration.getBaseLanguage()).isEqualTo("de");
    assertThat(configuration.getActions()).containsExactlyInAnyOrder("sort", "missing");
    assertThat(configuration.getModelProvider()).isEqualTo("ollama");
    assertThat(configuration.getModel()).isEqualTo("mistral");
    assertThat(configuration.getApiKey()).isEqualTo("secret");
    assertThat(configuration.getApiUrl()).isEqualTo("http://localhost:11434");
    assertThat(configuration.isDebug()).isTrue();
  }

  @Test
  @DisplayName("should keep the defaults if no options are given")
  void shouldKeepDefaultsWithoutOptions() {
    var defaults = new Configuration();

    var configuration = configurationFor();

    assertThat(configuration.getFile()).isNull();
    assertThat(configuration.getFiles()).isEqualTo(defaults.getFiles());
    assertThat(configuration.getBaseLanguage()).isEqualTo(defaults.getBaseLanguage());
    assertThat(configuration.getActions()).isEqualTo(defaults.getActions());
    assertThat(configuration.isDebug()).isFalse();
  }

  @Test
  @DisplayName("should configure one file per language if several files are given")
  void shouldConfigureLanguageFiles() {
    var configuration =
        configurationFor("-f", "en:messages_en.properties", "de:messages_de.properties");

    assertThat(configuration.getFiles())
        .extracting(LanguageFileConfiguration::getLanguage, LanguageFileConfiguration::getFile)
        .containsExactlyInAnyOrder(
            tuple("en", new File("messages_en.properties")),
            tuple("de", new File("messages_de.properties")));
  }

  @Test
  @DisplayName("should fail if one of several files has no language prefix")
  void shouldFailIfLanguagePrefixIsMissing() {
    var command = parse("-f", "en:messages_en.properties", "messages_de.properties");

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(command::createConfiguration)
        .withMessage("Invalid file format: messages_de.properties. Expected format: language:path");
  }

  private Configuration configurationFor(String... args) {
    return parse(args).createConfiguration();
  }

  private TestCommand parse(String... args) {
    var command = new TestCommand();
    new CommandLine(command).parseArgs(args);
    return command;
  }

  private static class TestCommand extends AbstractCommand {
    @Override
    public Integer call() {
      return 0;
    }
  }
}
