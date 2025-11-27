package dev.uebelacker.readers;

import dev.uebelacker.model.Translation;
import dev.uebelacker.model.TranslationFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesFileReaderTest {

    @Test
    @DisplayName("should read properties files")
    void shouldReadPropertiesFiles() {
        var propertiesFileReader = new PropertiesFileReader();
        var translationFile = propertiesFileReader.readFile((
                new TranslationFile("de", new File("src/test/resources/properties/test_de.properties"), null)
        ));

        assertEquals(3, translationFile.translations().size());

        var translation = translationFile.translations().stream().filter(t -> t.key().equals("error.message.notfound") && t.language().equals("de")).findFirst();

        assertEquals("Die angeforderte Ressource wurde nicht gefunden.", translation.map(Translation::value).orElse(null));
    }
}