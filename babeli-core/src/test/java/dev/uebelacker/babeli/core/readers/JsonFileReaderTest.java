package dev.uebelacker.babeli.core.readers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonFileReaderTest {

  private final JsonFileReader jsonFileReader = new JsonFileReader(new Configuration());

  @Test
  @DisplayName("should read single language json file")
  void shouldReadSingleLanguageJsonFile() {
    var translationFile =
        jsonFileReader.readFile("de", new java.io.File("src/test/resources/json/test_de.json"));

    assertThat(translationFile.translations()).hasSize(2);

    var translation =
        translationFile.translations().stream()
            .filter(t -> t.key().equals("error.message.notfound") && t.language().equals("de"))
            .findFirst();

    assertThat(translation.map(Translation::language).orElse(null)).isEqualTo("de");
    assertThat(translation.map(Translation::key).orElse(null)).isEqualTo("error.message.notfound");
    assertThat(translation.map(Translation::value).orElse(null))
        .isEqualTo("Die angeforderte Ressource wurde nicht gefunden.");
  }

  @Test
  @DisplayName("should throw exception when file does not exist")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionWhenFileDoesNotExist() {
    assertThatExceptionOfType(FileReaderException.class)
        .isThrownBy(
            () ->
                jsonFileReader.readFile(
                    "de", new File("src/test/resources/json/nonexistent_de.json")));

    assertThatExceptionOfType(FileReaderException.class)
        .isThrownBy(
            () -> jsonFileReader.readFile(new File("src/test/resources/json/nonexistent.json")));
  }

  @Test
  @DisplayName("should read multi language json file")
  void shouldReadMultiLanguageJsonFile() {
    var translationFile =
        jsonFileReader.readFile(new java.io.File("src/test/resources/json/test.json"));

    assertThat(translationFile.translations()).hasSize(10);

    var translation =
        translationFile.translations().stream()
            .filter(t -> t.key().equals("error.message.notfound") && t.language().equals("de"))
            .findFirst();

    assertThat(translation.map(Translation::language).orElse(null)).isEqualTo("de");
    assertThat(translation.map(Translation::key).orElse(null)).isEqualTo("error.message.notfound");
    assertThat(translation.map(Translation::value).orElse(null))
        .isEqualTo("Die angeforderte Ressource wurde nicht gefunden.");
  }
}
