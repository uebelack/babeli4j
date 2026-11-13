package dev.uebelacker.babeli.core.services;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.ChatModelFactory;
import dev.uebelacker.babeli.core.model.Translations;
import dev.uebelacker.babeli.core.util.EnvUtils;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

  @AfterEach
  void tearDown() {
    EnvUtils.reset();
  }

  @Test
  @DisplayName("should translate text from en to de")
  void shouldTranslateTextFromEnglishToGerman() {
    var configuration = new Configuration();
    configuration.setModelProvider("test");
    EnvUtils.set(BABELI_MODEL_PROVIDER, "test");

    var chatModel = ChatModelFactory.createChatModel(configuration);

    var systemMessageArgumentCaptor = ArgumentCaptor.forClass(SystemMessage.class);
    var userMessageArgumentCaptor = ArgumentCaptor.forClass(UserMessage.class);

    when(chatModel.chat(systemMessageArgumentCaptor.capture(), userMessageArgumentCaptor.capture()))
        .thenReturn(
            ChatResponse.builder()
                .aiMessage(AiMessage.builder().text("Hallo, wie geht es dir?").build())
                .build());

    var translationService =
        new TranslationService(configuration, Translations.fromTranslations(List.of()));
    var result = translationService.translate("Hello, how are you?", "en", "de");

    assertThat(result).isEqualTo("Hallo, wie geht es dir?");
    assertThat(systemMessageArgumentCaptor.getValue().text())
        .contains("translate text from en to de");
    assertThat(userMessageArgumentCaptor.getValue().singleText()).isEqualTo("Hello, how are you?");
  }
}
