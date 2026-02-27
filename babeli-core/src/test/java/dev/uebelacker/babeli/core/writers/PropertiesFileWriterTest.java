package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.model.Translation;
import dev.uebelacker.babeli.core.model.TranslationFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(new File("target/test/properties/test_de.properties")).exists();
    }
}