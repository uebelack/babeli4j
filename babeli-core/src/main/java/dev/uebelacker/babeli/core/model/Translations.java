package dev.uebelacker.babeli.core.model;

import java.util.*;

public class Translations {
  private final Map<String, Map<String, String>> keyLanguageMap;
  private final Map<String, Map<String, String>> languageKeyMap;
  private final List<String> languages;

  private Translations(List<Translation> translations) {
    this.languages = translations.stream().map(Translation::language).distinct().sorted().toList();

    keyLanguageMap = new LinkedHashMap<>();
    languageKeyMap = new LinkedHashMap<>();
    for (var translation : translations) {
      var key = translation.key();
      var language = translation.language();
      var value = translation.value();

      keyLanguageMap.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(language, value);
      languageKeyMap.computeIfAbsent(language, l -> new LinkedHashMap<>()).put(key, value);
    }
  }

  private Translations(Map<String, Map<String, String>> keyLanguageMap) {
    this(keyLanguageMapToTranslations(keyLanguageMap));
  }

  private static List<Translation> keyLanguageMapToTranslations(
      Map<String, Map<String, String>> keyLanguageMap) {
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

  public Optional<String> getTranslation(String key, String baseLanguage) {
    return Optional.ofNullable(keyLanguageMap.get(key))
        .map(languageMap -> languageMap.get(baseLanguage));
  }

  public Map<String, String> getTranslationsMapForKey(String key) {
    return keyLanguageMap.get(key);
  }

  public List<Translation> getTranslationsForLanguage(String language) {
    var translations = new ArrayList<Translation>();
    languageKeyMap
        .computeIfAbsent(language, l -> new LinkedHashMap<>())
        .forEach((key, value) -> translations.add(new Translation(language, key, value)));

    return translations;
  }

  public List<Translation> getTranslations() {
    var translations = new ArrayList<Translation>();
    keyLanguageMap.forEach(
        (key, languageMap) ->
            languageMap.forEach(
                (language, value) -> translations.add(new Translation(language, key, value))));

    return translations;
  }

  public List<Translation> getTranslationsMapForKey() {
    return keyLanguageMapToTranslations(keyLanguageMap);
  }

  public Translation add(String key, String language, String value) {
    var translation = new Translation(language, key, value);
    keyLanguageMap.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(language, value);
    languageKeyMap.computeIfAbsent(language, l -> new LinkedHashMap<>()).put(key, value);
    return translation;
  }

  public String getTranslationForValue(String text, String sourceLanguage, String targetLanguage) {
    var sourceMap = languageKeyMap.get(sourceLanguage);
    var targetMap = languageKeyMap.get(targetLanguage);
    if (sourceMap == null || targetMap == null) {
      return null;
    }
    for (var entry : sourceMap.entrySet()) {
      if (entry.getValue().equals(text)) {
        var translation = targetMap.get(entry.getKey());
        if (translation != null) {
          return translation;
        }
      }
    }
    return null;
  }
}
