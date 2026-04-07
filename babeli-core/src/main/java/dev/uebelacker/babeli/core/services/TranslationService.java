package dev.uebelacker.babeli.core.services;

public interface TranslationService {

  String name();

  default String translate(String value, String sourceLanguage, String targetLanguage) {
    return translate(value, sourceLanguage, targetLanguage, null);
  }

  String translate(String value, String sourceLanguage, String targetLanguage, String instructions);
}
