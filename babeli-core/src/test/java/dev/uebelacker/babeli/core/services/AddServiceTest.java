package dev.uebelacker.babeli.core.services;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;
import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.ai.ChatModelFactory;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import dev.uebelacker.babeli.core.readers.JsonFileReader;
import dev.uebelacker.babeli.core.readers.PropertiesFileReader;
import dev.uebelacker.babeli.core.util.EnvUtils;
import dev.uebelacker.babeli.core.writers.JsonFileWriter;
import dev.uebelacker.babeli.core.writers.PropertiesFileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddServiceTest {

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
        var chatModel = ChatModelFactory.createChatModel(configuration);
        lenient()
                .when(chatModel.chat(any(SystemMessage.class), any(UserMessage.class)))
                .thenReturn(ChatResponse.builder()
                        .aiMessage(AiMessage.builder().text("AI Translation").build())
                        .build());
    }

    @AfterEach
    void tearDown() {
        EnvUtils.reset();
    }

    @Test
    @DisplayName("should add key with all translations to multi language translation file")
    void shouldAddKeyWithAllTranslationsToMultiLanguageFile() {
        configuration.setFile(FILE);

        new AddService(configuration).add("new.key", Map.of("en", "New", "de", "Neu", "fr", "Nouveau"));

        var translations = translationsFromFile(FILE);
        assertThat(translations.getTranslation("new.key", "en")).contains("New");
        assertThat(translations.getTranslation("new.key", "de")).contains("Neu");
        assertThat(translations.getTranslation("new.key", "fr")).contains("Nouveau");
    }

    @Test
    @DisplayName("should add key with partial translations to multi language translation file using AI for missing languages")
    void shouldAddKeyWithPartialTranslationsToMultiLanguageFileUsingAI() {
        configuration.setFile(FILE);

        new AddService(configuration).add("new.key", Map.of("en", "New Value"));

        var translations = translationsFromFile(FILE);
        assertThat(translations.getTranslation("new.key", "en")).contains("New Value");
        assertThat(translations.getTranslation("new.key", "de")).contains("AI Translation");
        assertThat(translations.getTranslation("new.key", "fr")).contains("AI Translation");
    }

    @Test
    @DisplayName("should add key with all translations to single language translation files")
    void shouldAddKeyWithAllTranslationsToSingleLanguageFiles() {
        configuration.setFiles(FILES);

        new AddService(configuration).add("new.key", Map.of("en", "New", "de", "Neu", "fr", "Nouveau"));

        assertThat(translationFromPropertiesFile("en", new File("target/test/properties/messages_en.properties"), "new.key")).isEqualTo("New");
        assertThat(translationFromPropertiesFile("de", new File("target/test/properties/messages_de.properties"), "new.key")).isEqualTo("Neu");
        assertThat(translationFromPropertiesFile("fr", new File("target/test/properties/messages_fr.properties"), "new.key")).isEqualTo("Nouveau");
    }

    @Test
    @DisplayName("should add key with partial translations to single language translation files using AI for missing languages")
    void shouldAddKeyWithPartialTranslationsToSingleLanguageFilesUsingAI() {
        configuration.setFiles(FILES);

        new AddService(configuration).add("new.key", Map.of("en", "New Value"));

        assertThat(translationFromPropertiesFile("en", new File("target/test/properties/messages_en.properties"), "new.key")).isEqualTo("New Value");
        assertThat(translationFromPropertiesFile("de", new File("target/test/properties/messages_de.properties"), "new.key")).isEqualTo("AI Translation");
        assertThat(translationFromPropertiesFile("fr", new File("target/test/properties/messages_fr.properties"), "new.key")).isEqualTo("AI Translation");
    }

    private Translations translationsFromFile(File file) {
        return Translations.fromTranslations(new JsonFileReader(configuration).readFile(file).translations());
    }

    private String translationFromPropertiesFile(String language, File file, String key) {
        return new PropertiesFileReader(configuration).readFile(language, file)
                .translations().stream()
                .filter(t -> t.key().equals(key))
                .map(t -> t.value())
                .findFirst()
                .orElse(null);
    }
}
