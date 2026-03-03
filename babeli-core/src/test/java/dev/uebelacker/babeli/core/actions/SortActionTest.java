package dev.uebelacker.babeli.core.actions;

import static org.assertj.core.api.Assertions.assertThat;

import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.model.Error;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SortActionTest {

  @Test
  @DisplayName("should return name sort")
  void shouldReturnNameSort() {
    var action = new SortAction();
    assertThat(action.name()).isEqualTo("sort");
  }

  @Test
  @DisplayName("should validate unsorted single language translation files")
  void validateUnsortedSingleLanguageTranslationFiles() {
    var action = new SortAction();
    assertThat(action.validate(Fixtures.singleLanguageTranslationFiles()))
        .isEqualTo(
            List.of(
                new Error(
                    "sort",
                    "de",
                    "src/test/resources/properties/test_de.properties",
                    "Translations in file test_de.properties are not sorted."),
                new Error(
                    "sort",
                    "en",
                    "src/test/resources/properties/test_en.properties",
                    "Translations in file test_en.properties are not sorted."),
                new Error(
                    "sort",
                    "fr",
                    "src/test/resources/properties/test_fr.properties",
                    "Translations in file test_fr.properties are not sorted.")));
  }

  @Test
  @DisplayName("should update single language translation files to be sorted")
  void shouldUpdateSingleLanguageTranslationFilesToBeSorted() {
    var action = new SortAction();
    var translationFiles = Fixtures.singleLanguageTranslationFiles();

    assertThat(action.validate(Fixtures.singleLanguageTranslationFiles()))
        .isEqualTo(
            List.of(
                new Error(
                    "sort",
                    "de",
                    "src/test/resources/properties/test_de.properties",
                    "Translations in file test_de.properties are not sorted."),
                new Error(
                    "sort",
                    "en",
                    "src/test/resources/properties/test_en.properties",
                    "Translations in file test_en.properties are not sorted."),
                new Error(
                    "sort",
                    "fr",
                    "src/test/resources/properties/test_fr.properties",
                    "Translations in file test_fr.properties are not sorted.")));

    var sortedTranslationFiles = action.update(translationFiles);

    assertThat(action.validate(sortedTranslationFiles)).isEmpty();
  }
}
