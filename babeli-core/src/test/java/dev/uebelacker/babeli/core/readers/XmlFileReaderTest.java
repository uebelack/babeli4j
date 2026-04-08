package dev.uebelacker.babeli.core.readers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class XmlFileReaderTest {

  private XmlFileReader xmlFileReader;

  @BeforeEach
  void setUp() {
    xmlFileReader = new XmlFileReader();
  }

  @Test
  @DisplayName("should read single language xml file")
  void shouldReadSingleLanguageXmlFile() {
    var translationFile =
        xmlFileReader.readFile("de", new File("src/test/resources/xml/test_de.xml"));

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
  @DisplayName("should read multi language xml file")
  void shouldReadMultiLanguageXmlFile() {
    var translationFile = xmlFileReader.readFile(new File("src/test/resources/xml/test.xml"));

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

  @Test
  @DisplayName("should throw exception when file is not found")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionWhenFileIsNotFound() {
    assertThatExceptionOfType(FileReaderException.class)
        .isThrownBy(
            () -> xmlFileReader.readFile("de", new File("src/test/resources/xml/nonexistent.xml")));

    assertThatExceptionOfType(FileReaderException.class)
        .isThrownBy(
            () -> xmlFileReader.readFile(new File("src/test/resources/xml/nonexistent.xml")));
  }
}
