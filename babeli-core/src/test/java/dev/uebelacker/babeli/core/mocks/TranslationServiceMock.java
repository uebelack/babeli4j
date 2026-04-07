package dev.uebelacker.babeli.core.mocks;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.services.TranslationService;
import org.mockito.Mockito;

public class TranslationServiceMock implements TranslationService {
  private static TranslationService mock;

  public TranslationServiceMock(Configuration configuration) {
    mock = Mockito.mock(TranslationService.class);
  }

  public static TranslationService getMock() {
    return mock;
  }

  @Override
  public String translate(
      String value, String sourceLanguage, String targetLanguage, String instructions) {
    return mock.translate(value, sourceLanguage, targetLanguage, instructions);
  }
}
