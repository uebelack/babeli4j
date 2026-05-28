package dev.uebelacker.babeli.core;

import static java.nio.file.Paths.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.uebelacker.babeli.core.ai.ChatModelFactory;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import dev.uebelacker.babeli.core.logging.LoggingProvider;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.util.EnvUtils;
import dev.uebelacker.babeli.core.writers.JsonFileWriter;
import dev.uebelacker.babeli.core.writers.PropertiesFileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BabeliTest {
  static final File FILE = new File("target/test/test.json");
  static final Set<LanguageFileConfiguration> FILES =
      Set.of(
          new LanguageFileConfiguration(
              "en", new File("target/test/properties/messages_en.properties")),
          new LanguageFileConfiguration(
              "fr", new File("target/test/properties/messages_fr.properties")),
          new LanguageFileConfiguration(
              "de", new File("target/test/properties/messages_de.properties")));

  @Mock LoggingProvider loggingProvider;

  Configuration configuration;

  @BeforeEach
  void setUp() throws IOException {
    var propertiesFileWriter = new PropertiesFileWriter(new Configuration().setActions(Set.of()));

    try (var stream = Files.list(get("target/test/properties"))) {
      stream.filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
    }

    propertiesFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "en",
            new File("target/test/properties/messages_en.properties"),
            Fixtures.singleLanguageTranslationFileEn().translations()));

    propertiesFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "fr",
            new File("target/test/properties/messages_fr.properties"),
            Fixtures.singleLanguageTranslationFileFr().translations()));

    propertiesFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "de",
            new File("target/test/properties/messages_de.properties"),
            Fixtures.singleLanguageTranslationFileDe().translations()));

    var jsonFileWriter = new JsonFileWriter(new Configuration());
    jsonFileWriter.writeFile(
        new MultiLanguageTranslationFile(
            FILE, Fixtures.multiLanguageTranslationFile().translations()));
    configuration = new Configuration();
    configuration.setModelProvider("test");
    configuration.setLoggingProvider(loggingProvider);

    var chatModel = ChatModelFactory.createChatModel(configuration);

    lenient()
        .when(chatModel.chat(any(SystemMessage.class), any(UserMessage.class)))
        .thenReturn(
            ChatResponse.builder()
                .aiMessage(AiMessage.builder().text("Test Translation").build())
                .build());
  }

  @AfterEach
  void tearDown() {
    EnvUtils.reset();
  }

  @Test
  @DisplayName("should validate single language translation files")
  void validateSingleLanguageTranslationFiles() {
    configuration.setFiles(FILES);
    var errors = Babeli.validate(configuration);

    assertThat(errors.stream().map(Error::toString).sorted().toList())
        .isEqualTo(
            List.of(
                "Error[action=missing, language=de, value=common.button.no, message=Missing translation for 'common.button.no' in file target/test/properties/messages_de.properties]",
                "Error[action=missing, language=en, value=common.button.perhaps, message=Missing translation for 'common.button.perhaps' in file target/test/properties/messages_en.properties]",
                "Error[action=sort, language=de, value=target/test/properties/messages_de.properties, message=Translations in file target/test/properties/messages_de.properties are not sorted.]",
                "Error[action=sort, language=en, value=target/test/properties/messages_en.properties, message=Translations in file target/test/properties/messages_en.properties are not sorted.]",
                "Error[action=sort, language=fr, value=target/test/properties/messages_fr.properties, message=Translations in file target/test/properties/messages_fr.properties are not sorted.]"));
  }

  @Test
  @DisplayName("should validate multi language translation file")
  void validateMultiLanguageTranslationFiles() {
    configuration.setFile(FILE);
    var errors = Babeli.validate(configuration);

    assertThat(errors.stream().map(Error::toString).sorted().toList())
        .isEqualTo(
            List.of(
                "Error[action=missing, language=de, value=common.button.no, message=Missing translation for key 'common.button.no' and language 'de' in file 'target/test/test.json']",
                "Error[action=missing, language=en, value=common.button.perhaps, message=Missing translation for key 'common.button.perhaps' and language 'en' in file 'target/test/test.json']",
                "Error[action=sort, language=null, value=target/test/test.json, message=Translations in file target/test/test.json are not sorted.]"));
  }

  @Test
  @DisplayName("should update single language translation files")
  void shouldUpdateSingleLanguageTranslationFiles() {
    configuration.setFiles(FILES);
    assertThat(Babeli.validate(configuration)).hasSize(5);
    Babeli.update(configuration);
    assertThat(Babeli.validate(configuration)).isEmpty();
  }

  @Test
  @DisplayName("should update multi language translation file")
  void shouldUpdateMultiLanguageTranslationFiles() {
    configuration.setFile(FILE);
    assertThat(Babeli.validate(configuration)).hasSize(3);
    Babeli.update(configuration);
    assertThat(Babeli.validate(configuration)).isEmpty();
  }

  @Test
  @DisplayName("should skip validation execution")
  void shouldSkipValidationExecution() {
    configuration.setSkip(true);
    assertThat(Babeli.validate(configuration)).isEmpty();
    verify(loggingProvider).info("Babeli is skipped.");
  }

  @Test
  @DisplayName("should skip update execution")
  void shouldSkipUpdateExecution() {
    configuration.setSkip(true);
    Babeli.update(configuration);
    verify(loggingProvider).info("Babeli is skipped.");
  }

  @Test
  @DisplayName("should skip update execution if no model provider was provided")
  void shouldSkipUpdateExecutionIfNoModelProviderWasProvided() {
    configuration.setModelProvider(null);
    Babeli.update(configuration);
    verify(loggingProvider)
        .warn(
            "No model provider specified. Babeli requires a model provider to function. Please specify a model provider using 'modelProvider' in the configuration or specify it as environment variable BABELI_MODEL_PROVIDER. Skipping execution.");
  }
}
