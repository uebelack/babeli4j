package dev.uebelacker.babeli.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.exceptions.TranslationNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TranslationsTest {
  Translations translations;

  @BeforeEach
  void setUp() {
    translations =
        Translations.fromTranslations(Fixtures.multiLanguageTranslationFile().translations());
  }

  @Test
  @DisplayName("should return languages")
  void shouldReturnLanguages() {
    assertThat(translations.getLanguages()).isEqualTo(List.of("de", "en", "fr"));
  }

  @Test
  @DisplayName("should return translation map")
  void shouldReturnTranslationMap() {
    assertThat(translations.getKeyLanguageMap()).isNotNull();
    assertThat(translations.getTranslations("common.button.no")).isNotNull();
    assertThat(translations.getTranslations("nothing")).isNull();
  }

  @Test
  @DisplayName("should return keys")
  void shouldReturnKeys() {
    assertThat(translations.getKeys().stream().sorted().toList())
        .isEqualTo(
            List.of(
                "common.button.no",
                "common.button.perhaps",
                "common.button.yes",
                "error.message.notfound"));
  }

  @Test
  @DisplayName("should return translation for key and language")
  void shouldReturnTranslationForKeyAndLanguage() {
    assertThat(translations.getTranslation("common.button.yes", "de")).isEqualTo("Ja");
    assertThat(translations.getTranslation("common.button.yes", "en")).isEqualTo("Yes");
    assertThat(translations.getTranslation("common.button.yes", "fr")).isEqualTo("Oui");
  }

  @Test
  @DisplayName("should throw exception when translation not found for key and language")
  void shouldThrowExceptionWhenTranslationNotFoundForKeyAndLanguage() {
    assertThrows(
        TranslationNotFoundException.class,
        () -> translations.getTranslation("common.button.yes", "es"));
  }

  @Test
  @DisplayName("should add translation")
  void shouldAddTranslation() {
    translations.add("common.button.maybe", "de", "Vielleicht");
    translations.add("common.button.maybe", "en", "Maybe");

    translations.getTranslations().stream()
        .filter(t -> t.key().equals("common.button.maybe") && t.language().equals("de"))
        .map(Translation::value)
        .forEach(value -> assertThat(value).isEqualTo("Vielleicht"));
  }
}
