package dev.uebelacker.babeli.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import dev.uebelacker.babeli.core.Babeli;
import dev.uebelacker.babeli.core.Configuration;

class OllamaAiProviderIntegrationTest {
    @Test
    @DisplayName("should update translations using ollama")
    void shouldUpdateTranslationsUsingOllama() throws IOException {
        if (System.getenv("BABELI_MODEL") == null) {
            return;
        }

        var configuration = new Configuration();
        configuration.setFile(new File("target/test.json"));
        configuration.setModelProvider("ollama");

        Files.copy(
                new File("src/test/resources/test.json").toPath(),
                configuration.getFile().toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        assertThat(Babeli.validate(configuration)).hasSize(5);
        Babeli.update(configuration);
        assertThat(Babeli.validate(configuration)).isEmpty();
    }
}
