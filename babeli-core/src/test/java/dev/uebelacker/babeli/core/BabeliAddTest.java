package dev.uebelacker.babeli.core;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.uebelacker.babeli.core.ai.ChatModelFactory;
import dev.uebelacker.babeli.core.exceptions.MultipleResourceBundlesFoundException;
import dev.uebelacker.babeli.core.exceptions.ResourceBundleNotFoundException;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import dev.uebelacker.babeli.core.readers.JsonFileReader;
import dev.uebelacker.babeli.core.util.EnvUtils;
import dev.uebelacker.babeli.core.writers.JsonFileWriter;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BabeliAddTest {

  static final File FILE = new File("target/test/babeli/messages.json");

  Configuration configuration;

  @BeforeEach
  void setUp() {
    new JsonFileWriter(new Configuration())
        .writeFile(
            new MultiLanguageTranslationFile(
                FILE, Fixtures.multiLanguageTranslationFile().translations()));

    configuration = new Configuration().setName("messages").setFile(FILE);
    configuration.setModelProvider("test");

    EnvUtils.set(BABELI_MODEL_PROVIDER, "test");
    var chatModel = ChatModelFactory.createChatModel(configuration);
    lenient()
        .when(chatModel.chat(any(SystemMessage.class), any(UserMessage.class)))
        .thenReturn(
            ChatResponse.builder()
                .aiMessage(AiMessage.builder().text("AI Translation").build())
                .build());
  }

  @AfterEach
  void tearDown() {
    EnvUtils.reset();
  }

  @Test
  @DisplayName("should add a key using the only available resource bundle")
  void shouldAddKeyUsingTheOnlyBundle() {
    Babeli.add(null, "new.key", Map.of("en", "New", "de", "Neu", "fr", "Nouveau"), configuration);

    var translations = translationsFromFile();
    assertThat(translations.getTranslation("new.key", "en")).contains("New");
    assertThat(translations.getTranslation("new.key", "de")).contains("Neu");
    assertThat(translations.getTranslation("new.key", "fr")).contains("Nouveau");
  }

  @Test
  @DisplayName("should add a key to the resource bundle matching the given name")
  void shouldAddKeyToBundleMatchingName() {
    var other = new Configuration().setName("other").setFile(new File("target/test/babeli/nope"));
    var root = mock(Configuration.class);
    when(root.autoConfigure()).thenReturn(List.of(other, configuration));

    Babeli.add("messages", "new.key", Map.of("en", "New", "de", "Neu", "fr", "Nouveau"), root);

    assertThat(translationsFromFile().getTranslation("new.key", "en")).contains("New");
  }

  @Test
  @DisplayName("should fail if multiple resource bundles are found and none was specified")
  void shouldFailIfMultipleBundlesAndNoneSpecified() {
    var root = mock(Configuration.class);
    when(root.autoConfigure())
        .thenReturn(
            List.of(
                new Configuration().setName("messages"), new Configuration().setName("errors")));

    assertThatExceptionOfType(MultipleResourceBundlesFoundException.class)
        .isThrownBy(() -> Babeli.add(null, "new.key", Map.of("en", "New"), root))
        .withMessage(
            "Multiple resource bundles found, please specify which one to use: messages, errors");
  }

  @Test
  @DisplayName("should fail if the specified resource bundle does not exist")
  void shouldFailIfSpecifiedBundleDoesNotExist() {
    var root = mock(Configuration.class);
    when(root.autoConfigure())
        .thenReturn(
            List.of(
                new Configuration().setName("messages"), new Configuration().setName("errors")));

    assertThatExceptionOfType(ResourceBundleNotFoundException.class)
        .isThrownBy(() -> Babeli.add("missing", "new.key", Map.of("en", "New"), root))
        .withMessage("Resource bundle not found: missing");
  }

  @Test
  @DisplayName("should fill in missing translations when updating")
  void shouldFillInMissingTranslationsWhenUpdating() {
    Babeli.update(configuration);

    assertThat(Babeli.validate(configuration)).isEmpty();
  }

  @Test
  @DisplayName("should report missing translations when validating")
  void shouldReportMissingTranslationsWhenValidating() {
    var errors = Babeli.validate(configuration);

    assertThat(errors).isNotEmpty();
    assertThat(errors).allSatisfy(error -> assertThat(error.message()).contains(FILE.getPath()));
  }

  private Translations translationsFromFile() {
    return Translations.fromTranslations(
        new JsonFileReader(new Configuration()).readFile(FILE).translations());
  }
}
