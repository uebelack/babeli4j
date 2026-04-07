package dev.uebelacker.babeli.core.actions;

import static org.assertj.core.api.Assertions.assertThat;

import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.model.Error;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MissingActionTest {
  @Test
  @DisplayName("should return a list of all missing keys in single translation files")
  void shouldReturnAListOfAllMissingKeysInSingleLanguageTranslationFiles() {
    var action = new MissingAction();
    assertThat(action.validate(Fixtures.singleLanguageTranslationFiles()))
        .isEqualTo(
            List.of(
                new Error(
                    "missing",
                    "de",
                    "common.button.no",
                    "Missing translation for 'common.button.no' in file test_de.properties"),
                new Error(
                    "missing",
                    "en",
                    "common.button.perhaps",
                    "Missing translation for 'common.button.perhaps' in file test_en.properties")));
  }

  @Test
  @DisplayName("should return a list of all missing keys in multi language translation files")
  void shouldReturnAListOfAllMissingKeysInMultiLanguageTranslationFiles() {
    var action = new MissingAction();
    assertThat(action.validate(Fixtures.multiLanguageTranslationFile()))
        .isEqualTo(
            List.of(
                new Error(
                    "missing",
                    "en",
                    "common.button.perhaps",
                    "Missing translation for key 'common.button.perhaps' and language 'en' in file 'test.json'"),
                new Error(
                    "missing",
                    "de",
                    "common.button.no",
                    "Missing translation for key 'common.button.no' and language 'de' in file 'test.json'")));
  }
}
