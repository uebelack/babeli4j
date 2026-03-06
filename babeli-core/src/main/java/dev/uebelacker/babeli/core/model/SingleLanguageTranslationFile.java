package dev.uebelacker.babeli.core.model;

import java.io.File;
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
}
