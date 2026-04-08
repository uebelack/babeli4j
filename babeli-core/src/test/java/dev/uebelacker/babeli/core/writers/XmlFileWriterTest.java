package dev.uebelacker.babeli.core.writers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.readers.XmlFileReader;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class XmlFileWriterTest {

  private XmlFileWriter xmlFileWriter;

  @BeforeEach
  void setUp() {
    xmlFileWriter = new XmlFileWriter();
  }

  @Test
  @DisplayName("should write single language xml file")
  void shouldWriteSingleLanguageXmlFile() {
    xmlFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "de",
            new File("target/test/xml/test_de.xml"),
            Fixtures.singleLanguageTranslationFileDe().translations()));

    assertThat(new File("target/test/xml/test_de.xml")).exists();
  }

  @Test
  @DisplayName("should write and read back single language xml file")
  void shouldWriteAndReadBackSingleLanguageXmlFile() {
    var outputFile = new File("target/test/xml/roundtrip_de.xml");
    xmlFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "de", outputFile, Fixtures.singleLanguageTranslationFileDe().translations()));

    var reader = new XmlFileReader();
    var result = reader.readFile("de", outputFile);

    assertThat(result.translations()).hasSize(3);
    assertThat(result.language()).isEqualTo("de");
  }

  @Test
  @DisplayName("should write multi language xml file")
  void shouldWriteMultiLanguageXmlFile() {
    xmlFileWriter.writeFile(
        new MultiLanguageTranslationFile(
            new File("target/test/xml/test.xml"),
            Fixtures.multiLanguageTranslationFile().translations()));

    assertThat(new File("target/test/xml/test.xml")).exists();
  }

  @Test
  @DisplayName("should write and read back multi language xml file")
  void shouldWriteAndReadBackMultiLanguageXmlFile() {
    var outputFile = new File("target/test/xml/roundtrip.xml");
    xmlFileWriter.writeFile(
        new MultiLanguageTranslationFile(
            outputFile, Fixtures.multiLanguageTranslationFile().translations()));

    var reader = new XmlFileReader();
    var result = reader.readFile(outputFile);

    assertThat(result.translations()).hasSize(10);
  }

  @Test
  @DisplayName("should throw exception when writing single language xml file fails")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionWhenWritingSingleLanguageXmlFileFails() {
    assertThatExceptionOfType(FileWriterException.class)
        .isThrownBy(
            () ->
                xmlFileWriter.writeFile(
                    new SingleLanguageTranslationFile(
                        "de",
                        new File("/notallowed/test_de.xml"),
                        Fixtures.singleLanguageTranslationFileDe().translations())));
  }

  @Test
  @DisplayName("should throw exception when writing multi language xml file fails")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionWhenWritingMultiLanguageXmlFileFails() {
    assertThatExceptionOfType(FileWriterException.class)
        .isThrownBy(
            () ->
                xmlFileWriter.writeFile(
                    new MultiLanguageTranslationFile(
                        new File("/notallowed/test.xml"),
                        Fixtures.multiLanguageTranslationFile().translations())));
  }
}
