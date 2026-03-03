package dev.uebelacker.babeli.core.model;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record MultiLanguageTranslationFile(File file, List<Translation> translations) {

  public Map<String, Map<String, String>> toKeyLanguageMap() {
    var result = new LinkedHashMap<String, Map<String, String>>();
    for (var translation : translations) {
      var key = translation.key();
      var language = translation.language();
      var value = translation.value();

      result.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(language, value);
    }
    return result;
  }
}
