package dev.uebelacker.babeli.core.writers;

import static org.assertj.core.api.Assertions.assertThat;

import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PropertiesFileWriterTest {

  @Test
  @DisplayName("should write properties file")
  void shouldWritePropertiesFile() {
    var propertiesFileWriter = new PropertiesFileWriter();
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
}
