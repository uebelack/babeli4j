package dev.uebelacker.babeli.core.model;

import dev.uebelacker.babeli.core.exceptions.TranslationFileNotFoundException;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record SingleLanguageTranslationFile(
    String language, File file, List<Translation> translations) {
  public static SingleLanguageTranslationFile fromMap(
      String language, File file, Map<String, String> map) {
    return new SingleLanguageTranslationFile(
        language,
        file,
        map.keySet().stream().map(key -> new Translation(language, key, map.get(key))).toList());
  }

  public static Map<String, Map<String, String>> toKeyLanguageMap(
      List<SingleLanguageTranslationFile> translationFiles) {

    var translations =
        translationFiles.stream()
            .map(SingleLanguageTranslationFile::translations)
            .flatMap(List::stream)
            .toList();

    var result = new LinkedHashMap<String, Map<String, String>>();

    for (var translation : translations) {
      var key = translation.key();
      var language = translation.language();
      var value = translation.value();

      result.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(language, value);
    }

    return result;
  }

  public SingleLanguageTranslationFile findByLanguage(
      List<SingleLanguageTranslationFile> translationFiles, String language) {
    return translationFiles.stream()
        .filter(translationFile -> translationFile.language().equals(language))
        .findFirst()
        .orElseThrow(
            () ->
                new TranslationFileNotFoundException(
                    "No translation file found for language: " + language));
  }
}
