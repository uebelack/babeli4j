package dev.uebelacker.babeli.core.writers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import dev.uebelacker.babeli.core.model.Translation;
import dev.uebelacker.babeli.core.model.TranslationFile;

class JsonFileWriterTest {

    @Test
    @DisplayName("should write json file")
    void shouldWriteJsonFile() {
        var jsonFileWriter = new JsonFileWriter();
        var translations = List.of(
                new Translation("de", "error.message.notfound", "Die angeforderte Ressource wurde nicht gefunden."),
                new Translation("de", "error.message.unauthorized", "Zugriff verweigert.")
        );

        jsonFileWriter.writeFile(
                new TranslationFile("de", new File("target/test/json/test_de.json"), translations)
        );

        assertThat(new File("target/test/json/test_de.json")).exists();
    }
}