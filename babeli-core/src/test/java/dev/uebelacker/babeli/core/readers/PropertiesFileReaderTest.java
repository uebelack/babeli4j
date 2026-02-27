package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.model.Translation;
import dev.uebelacker.babeli.core.model.TranslationFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesFileReaderTest {

    @Test
    @DisplayName("should read properties files")
    void shouldReadPropertiesFiles() {
        var propertiesFileReader = new PropertiesFileReader();
        var translationFile = propertiesFileReader.readFile((
                new TranslationFile("de", new File("src/test/resources/properties/test_de.properties"), null)
        ));

        assertThat(translationFile.translations()).hasSize(3);

        var translation = translationFile.translations().stream().filter(t -> t.key().equals("error.message.notfound") && t.language().equals("de")).findFirst();

        assertThat(translation.map(Translation::language).orElse(null)).isEqualTo("de");
        assertThat(translation.map(Translation::key).orElse(null)).isEqualTo("error.message.notfound");
        assertThat(translation.map(Translation::value).orElse(null)).isEqualTo("Die angeforderte Ressource wurde nicht gefunden.");
    }
}