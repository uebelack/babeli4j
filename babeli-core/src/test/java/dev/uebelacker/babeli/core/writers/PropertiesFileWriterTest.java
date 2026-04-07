package dev.uebelacker.babeli.core.writers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PropertiesFileWriterTest {

  private PropertiesFileWriter propertiesFileWriter;

  @BeforeEach
  void setUp() {
    propertiesFileWriter = new PropertiesFileWriter();
  }

  @Test
  @DisplayName("should write properties file")
  void shouldWritePropertiesFile() {
    var translations =
        List.of(
            new Translation(
                "de", "error.message.notfound", "Die angeforderte Ressource wurde nicht gefunden."),
            new Translation("de", "error.message.unauthorized", "Zugriff verweigert."));

    propertiesFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "de", new File("target/test/properties/test_de.properties"), translations));

    assertThat(new File("target/test/properties/test_de.properties")).exists();
  }

  @Test
  @DisplayName("should throw exception when writing multi language properties file")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionWhenWritingMultiLanguagePropertiesFile() {
    assertThatThrownBy(
            () ->
                propertiesFileWriter.writeFile(
                    new MultiLanguageTranslationFile(
                        new File("target/test/properties/test.properties"), List.of())))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining(
            "Multi-language translation files are not supported for properties files.");
  }

  @Test
  @DisplayName("should throw exception when writing properties file fails")
  void shouldThrowExceptionWhenWritingPropertiesFileFails() {
    assertThatThrownBy(
            () ->
                propertiesFileWriter.writeFile(
                    new SingleLanguageTranslationFile(
                        "de",
                        new File("/notallowed/test_de.properties"),
                        List.of(new Translation("de", "key", "value")))))
        .isInstanceOf(FileWriterException.class);
  }
}
