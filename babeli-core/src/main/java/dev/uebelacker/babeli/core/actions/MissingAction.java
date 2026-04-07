package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.model.*;
import dev.uebelacker.babeli.core.model.Error;
import java.util.ArrayList;
import java.util.List;

public class MissingAction implements Action {

  private static final String NAME = "missing";

  static {
    ActionRegistry.registerAction(NAME, MissingAction.class);
  }

  @Override
  public List<Error> validateSingleLanguageTranslationFile(
      List<SingleLanguageTranslationFile> translationFiles) {
    var keys =
        translationFiles.stream()
            .flatMap(tf -> tf.translations().stream())
            .map(Translation::key)
            .distinct()
            .toList();

    var errors = new ArrayList<Error>();

    for (SingleLanguageTranslationFile translationFile : translationFiles) {
      var missingKeys =
          keys.stream()
              .filter(
                  key ->
                      translationFile.translations().stream().noneMatch(t -> t.key().equals(key)))
              .toList();
      if (!missingKeys.isEmpty()) {
        errors.addAll(
            missingKeys.stream()
                .map(
                    key ->
                        new Error(
                            NAME,
                            translationFile.language(),
                            key,
                            "Missing translation for '%s' in file %s"
                                .formatted(key, translationFile.file().getName())))
                .toList());
      }
    }
    return errors;
  }

  @Override
  public List<Error> validate(MultiLanguageTranslationFile translationFile) {
    var keyLanguageMap =
        Translations.fromTranslations(translationFile.translations()).getKeyLanguageMap();
    var languages =
        translationFile.translations().stream().map(Translation::language).distinct().toList();
    var errors = new ArrayList<Error>();

    keyLanguageMap
        .keySet()
        .forEach(
            key -> {
              languages.forEach(
                  language -> {
                    if (!keyLanguageMap.get(key).containsKey(language)) {
                      errors.add(
                          new Error(
                              NAME,
                              language,
                              key,
                              "Missing translation for key '%s' and language '%s' in file '%s'"
                                  .formatted(key, language, translationFile.file().getName())));
                    }
                  });
            });

    return errors;
  }
}
