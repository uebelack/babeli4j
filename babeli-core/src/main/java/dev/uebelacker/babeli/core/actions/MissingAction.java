package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.util.ArrayList;
import java.util.List;

public class MissingAction implements Action {
  @Override
  public String name() {
    return "missing";
  }

  @Override
  public List<Error> validate(List<SingleLanguageTranslationFile> translationFiles) {
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
                            name(),
                            translationFile.language(),
                            key,
                            "Missing key '%s' in file %s"
                                .formatted(key, translationFile.file().getName())))
                .toList());
      }
    }
    return errors;
  }

  @Override
  public List<Error> validate(MultiLanguageTranslationFile translationFile) {
    return List.of();
  }
}
