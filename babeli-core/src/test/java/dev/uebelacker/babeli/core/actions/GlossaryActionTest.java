package dev.uebelacker.babeli.core.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.uebelacker.babeli.core.BabeliContext;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.services.GlossaryService;
import dev.uebelacker.babeli.core.services.TranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GlossaryActionTest {

  @Mock TranslationService translationService;

  @Mock GlossaryService glossaryService;

  GlossaryAction glossaryAction;

  @BeforeEach
  void setUp() {
    glossaryAction =
        new GlossaryAction(
            new BabeliContext(new Configuration(), translationService, glossaryService));
  }

  @Test
  @DisplayName("should validate glossary with single language translation files")
  void shouldValidateGlossaryWithSingleLanguageTranslationFiles() {
    assertThat(glossaryAction.validate(Fixtures.singleLanguageTranslationFiles()).isEmpty())
        .isTrue();
  }

  @Test
  @DisplayName("should validate glossary with multi language translation file")
  void shouldValidateGlossaryWithMultiLanguageTranslationFiles() {
    assertThat(glossaryAction.validate(Fixtures.singleLanguageTranslationFiles()).isEmpty())
        .isTrue();
  }

  @Test
  @DisplayName("should update glossary with single language translation files")
  void shouldUpdateGlossaryWithSingleLanguageTranslationFiles() {
    glossaryAction.update(Fixtures.singleLanguageTranslationFiles());
    verify(glossaryService, times(4)).updateWith(anyString(), anyMap());
  }

  @Test
  @DisplayName("should update glossary with multi language translation file")
  void shouldUpdateGlossaryWithMultiLanguageTranslationFiles() {
    glossaryAction.update(Fixtures.multiLanguageTranslationFile());
    verify(glossaryService, times(4)).updateWith(anyString(), anyMap());
  }
}
