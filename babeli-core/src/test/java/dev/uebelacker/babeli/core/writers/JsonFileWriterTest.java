package dev.uebelacker.babeli.core.writers;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonFileWriterTest {

  private JsonFileWriter jsonFileWriter;

  @BeforeEach
  void setUp() {
    jsonFileWriter = new JsonFileWriter();
  }

  @Test
  @DisplayName("should write single language json file")
  void shouldWriteSingleLanguageJsonFile() {
    jsonFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "de",
            new File("target/test/json/test_de.json"),
            Fixtures.singleLanguageTranslationFileDe().translations()));

    assertThat(new File("target/test/json/test_de.json")).exists();
  }

  @Test
  @DisplayName("should throw exception when writing single language json file fails")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionWhenWritingSingleLanguageJsonFileFails() {
    assertThatExceptionOfType(FileWriterException.class)
        .isThrownBy(
            () ->
                jsonFileWriter.writeFile(
                    new SingleLanguageTranslationFile(
                        "de",
                        new File("/notallowed/test_de.json"),
                        Fixtures.singleLanguageTranslationFileDe().translations())));
  }

  @Test
  @DisplayName("should write multi language json file")
  void shouldWriteMultiLanguageJsonFile() {
    jsonFileWriter.writeFile(
        new MultiLanguageTranslationFile(
            new File("target/test/json/test.json"),
            Fixtures.multiLanguageTranslationFile().translations()));

    assertThat(new File("target/test/json/test.json")).exists();
  }

  @Test
  @DisplayName("should throw exception when writing multi language json file fails")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionWhenWritingMultiLanguageJsonFileFails() {
    assertThatExceptionOfType(FileWriterException.class)
        .isThrownBy(
            () ->
                jsonFileWriter.writeFile(
                    new MultiLanguageTranslationFile(
                        new File("/notallowed/test.json"),
                        Fixtures.multiLanguageTranslationFile().translations())));
  }
}
