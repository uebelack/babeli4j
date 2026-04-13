package dev.uebelacker.babeli.core.services;

public class TranslationService {
  public String translate(String value, String sourceLanguage, String targetLanguage) {
    return translate(value, sourceLanguage, targetLanguage, null);
  }

  public String translate(
      String value, String sourceLanguage, String targetLanguage, String instructions) {
    return value;
  }
}
