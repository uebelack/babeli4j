package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.services.TranslationService;
import java.util.HashMap;
import java.util.Map;

public class TestTranslationService implements TranslationService {

  private static final Map<String, String> translations = new HashMap<>();

  static {
    translations.put("No-en-de", "Nein");
    translations.put("Peut-être-fr-en", "perhaps");
    translations.put("Vielleicht-de-en", "perhaps");
  }

  @Override
  public String translate(
      String value, String sourceLanguage, String targetLanguage, String instructions) {
    var key = "%s-%s-%s".formatted(value, sourceLanguage, targetLanguage);
    var result = translations.get(key);

    if (result == null) {
      throw new IllegalArgumentException("No translation found for key '%s'!".formatted(key));
    }

    return result;
  }
}
