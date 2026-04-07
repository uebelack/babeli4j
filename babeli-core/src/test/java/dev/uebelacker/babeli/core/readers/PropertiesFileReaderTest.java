package dev.uebelacker.babeli.core.readers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PropertiesFileReaderTest {

  private PropertiesFileReader propertiesFileReader;

  @BeforeEach
  void setUp() {
    propertiesFileReader = new PropertiesFileReader();
  }

  @Test
  @DisplayName("should read properties files")
  void shouldReadPropertiesFiles() {
    var translationFile =
        propertiesFileReader.readFile(
            "de", new File("src/test/resources/properties/test_de.properties"));

    assertThat(translationFile.translations()).hasSize(3);

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
  @DisplayName("should throw exception when file is not found")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionWhenFileIsNotFound() {
    assertThatExceptionOfType(FileReaderException.class)
        .isThrownBy(
            () ->
                propertiesFileReader.readFile(
                    "de", new File("src/test/resources/properties/nonexistent.properties")));
  }

  @Test
  @DisplayName("should throw when trying to read multi-language file")
  @SuppressWarnings("java:S5778")
  void shouldThrowWhenTryingToReadMultiLanguageFile() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(
            () ->
                propertiesFileReader.readFile(
                    new File("src/test/resources/properties/nonexistent.properties")));
  }
}
