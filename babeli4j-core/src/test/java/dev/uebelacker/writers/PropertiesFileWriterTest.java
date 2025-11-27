package dev.uebelacker.writers;

import dev.uebelacker.model.Translation;
import dev.uebelacker.model.TranslationFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

class PropertiesFileWriterTest {

    @Test
    @DisplayName("should write properties file")
    void shouldWritePropertiesFile() {
        var propertiesFileWriter = new PropertiesFileWriter();
        var translations = List.of(
                new Translation("de", "error.message.notfound", "Die angeforderte Ressource wurde nicht gefunden."),
                new Translation("de", "error.message.unauthorized", "Zugriff verweigert.")
        );

        propertiesFileWriter.writeFile(
                new TranslationFile("de", new File("target/test/properties/test_de.properties"), translations)
        );
    }
}