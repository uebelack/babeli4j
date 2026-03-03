package dev.uebelacker.babeli.core.writers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonFileWriterTest {

  @Test
  @DisplayName("should write single language json file")
  void shouldWriteSingleLanguageJsonFile() {
    var jsonFileWriter = new JsonFileWriter();
    var translations =
        List.of(
            new Translation(
                "de", "error.message.notfound", "Die angeforderte Ressource wurde nicht gefunden."),
            new Translation("de", "error.message.unauthorized", "Zugriff verweigert."));

    jsonFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "de", new File("target/test/json/test_de.json"), translations));

    assertThat(new File("target/test/json/test_de.json")).exists();
  }

  @Test
  @DisplayName("should write multi language json file")
  void shouldWriteMultiLanguageJsonFile() {
    var translations =
        Fixtures.singleLanguageTranslationFiles().stream()
            .map(SingleLanguageTranslationFile::translations)
            .flatMap(List::stream)
            .toList();
    var jsonFileWriter = new JsonFileWriter();
    jsonFileWriter.writeFile(
        new MultiLanguageTranslationFile(new File("target/test/json/test.json"), translations));

    assertThat(new File("target/test/json/test.json")).exists();
  }
}
