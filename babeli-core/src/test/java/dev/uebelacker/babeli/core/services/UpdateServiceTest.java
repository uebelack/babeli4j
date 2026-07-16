package dev.uebelacker.babeli.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static java.nio.file.Paths.get;
import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.ai.AiFactory;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.util.EnvUtils;
import dev.uebelacker.babeli.core.writers.JsonFileWriter;
import dev.uebelacker.babeli.core.writers.PropertiesFileWriter;

@ExtendWith(MockitoExtension.class)
class UpdateServiceTest {

    static final File FILE = new File("target/test/test.json");
    static final Set<LanguageFileConfiguration> FILES =
            Set.of(
                    new LanguageFileConfiguration("en", new File("target/test/properties/messages_en.properties")),
                    new LanguageFileConfiguration("fr", new File("target/test/properties/messages_fr.properties")),
                    new LanguageFileConfiguration("de", new File("target/test/properties/messages_de.properties")));

    Configuration configuration;

    @BeforeEach
    void setUp() throws IOException {
        var propertiesFileWriter = new PropertiesFileWriter(new Configuration().setActions(Set.of()));

        try (var stream = Files.list(get("target/test/properties"))) {
            stream.filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
        }

        propertiesFileWriter.writeFile(new SingleLanguageTranslationFile(
                "en", new File("target/test/properties/messages_en.properties"),
                Fixtures.singleLanguageTranslationFileEn().translations()));
        propertiesFileWriter.writeFile(new SingleLanguageTranslationFile(
                "fr", new File("target/test/properties/messages_fr.properties"),
                Fixtures.singleLanguageTranslationFileFr().translations()));
        propertiesFileWriter.writeFile(new SingleLanguageTranslationFile(
                "de", new File("target/test/properties/messages_de.properties"),
                Fixtures.singleLanguageTranslationFileDe().translations()));

        var jsonFileWriter = new JsonFileWriter(new Configuration());
        jsonFileWriter.writeFile(new MultiLanguageTranslationFile(FILE, Fixtures.multiLanguageTranslationFile().translations()));

        configuration = new Configuration();
        configuration.setModelProvider("test");

        EnvUtils.set(BABELI_MODEL_PROVIDER, "test");
        var chatModel = AiFactory.createChatModel(configuration);
        lenient()
                .when(chatModel.chat(any(SystemMessage.class), any(UserMessage.class)))
                .thenReturn(ChatResponse.builder()
                        .aiMessage(AiMessage.builder().text("Test Translation").build())
                        .build());
    }

    @AfterEach
    void tearDown() {
        EnvUtils.reset();
    }

    @Test
    @DisplayName("should update single language translation files")
    void shouldUpdateSingleLanguageTranslationFiles() {
        configuration.setFiles(FILES);
        assertThat(new ValidateService(configuration).validate()).hasSize(5);
        new UpdateService(configuration).update();
        assertThat(new ValidateService(configuration).validate()).isEmpty();
    }

    @Test
    @DisplayName("should update multi language translation file")
    void shouldUpdateMultiLanguageTranslationFile() {
        configuration.setFile(FILE);
        assertThat(new ValidateService(configuration).validate()).hasSize(3);
        new UpdateService(configuration).update();
        assertThat(new ValidateService(configuration).validate()).isEmpty();
    }
}
