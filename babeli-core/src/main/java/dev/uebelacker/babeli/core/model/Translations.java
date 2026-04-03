package dev.uebelacker.babeli.core.model;

import dev.uebelacker.babeli.core.exceptions.TranslationNotFoundException;
import java.util.*;

public class Translations {
  private final Map<String, Map<String, String>> keyLanguageMap;
  private final List<String> languages;

  private Translations(List<Translation> translations) {
    this.languages = translations.stream().map(Translation::language).distinct().toList();

    keyLanguageMap = new LinkedHashMap<String, Map<String, String>>();
    for (var translation : translations) {
      var key = translation.key();
      var language = translation.language();
      var value = translation.value();

      keyLanguageMap.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(language, value);
    }
  }

  private Translations(Map<String, Map<String, String>> keyLanguageMap) {
    this.keyLanguageMap = keyLanguageMap;
    this.languages =
        keyLanguageMap.values().stream()
            .flatMap(languageMap -> languageMap.keySet().stream())
            .distinct()
            .toList();
  }

  public static Translations fromKeyLanguageMap(Map<String, Map<String, String>> keyLanguageMap) {
    return new Translations(keyLanguageMap);
  }

  public static Translations fromTranslations(List<Translation> translations) {
    return new Translations(translations);
  }

  public List<String> getLanguages() {
    return languages;
  }

  public Map<String, Map<String, String>> getKeyLanguageMap() {
    return keyLanguageMap;
  }

  public Set<String> getKeys() {
    return keyLanguageMap.keySet();
  }

  public String getTranslation(String key, String baseLanguage) {
    return Optional.ofNullable(keyLanguageMap.get(key))
        .map(languageMap -> languageMap.get(baseLanguage))
        .orElseThrow(
            () ->
                new TranslationNotFoundException(
                    "No translation found for key '%s' and language '%s'"
                        .formatted(key, baseLanguage)));
  }

  public List<Translation> getTranslations() {
    return keyLanguageMap.keySet().stream()
        .map(
            key ->
                keyLanguageMap.get(key).keySet().stream()
                    .map(
                        language ->
                            new Translation(language, key, keyLanguageMap.get(key).get(language)))
                    .toList())
        .flatMap(List::stream)
        .toList();
  }
}
