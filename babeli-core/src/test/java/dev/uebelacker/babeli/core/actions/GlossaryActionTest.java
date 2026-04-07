package dev.uebelacker.babeli.core.actions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.uebelacker.babeli.core.ConfigurationFactory;
import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.mocks.GlossaryServiceMock;
import dev.uebelacker.babeli.core.services.ServiceRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GlossaryActionTest {

  GlossaryAction glossaryAction;

  @BeforeEach
  void setUp() {
    ServiceRegistry.clearCache();
    ServiceRegistry.registerGlossaryService("test", GlossaryServiceMock.class);
    glossaryAction = new GlossaryAction(ConfigurationFactory.createConfiguration());
  }

  @Test
  @DisplayName("should validate glossary with single language translation files")
  void shouldValidateGlossaryWithSingleLanguageTranslationFiles() {
    assertTrue(glossaryAction.validate(Fixtures.singleLanguageTranslationFiles()).isEmpty());
  }

  @Test
  @DisplayName("should validate glossary with multi language translation file")
  void shouldValidateGlossaryWithMultiLanguageTranslationFiles() {
    assertTrue(glossaryAction.validate(Fixtures.singleLanguageTranslationFiles()).isEmpty());
  }

  @Test
  @DisplayName("should update glossary with single language translation files")
  void shouldUpdateGlossaryWithSingleLanguageTranslationFiles() {
    glossaryAction.update(Fixtures.singleLanguageTranslationFiles());
    verify(GlossaryServiceMock.getMock(), times(4)).updateWith(anyString(), anyMap());
  }

  @Test
  @DisplayName("should update glossary with multi language translation file")
  void shouldUpdateGlossaryWithMultiLanguageTranslationFiles() {
    glossaryAction.update(Fixtures.multiLanguageTranslationFile());
    verify(GlossaryServiceMock.getMock(), times(4)).updateWith(anyString(), anyMap());
  }
}
