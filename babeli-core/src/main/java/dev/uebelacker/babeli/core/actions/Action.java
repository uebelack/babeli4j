package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.util.List;

public interface Action {
  String name();

  List<Error> validate(List<SingleLanguageTranslationFile> translationFiles);

  default List<SingleLanguageTranslationFile> update(
      List<SingleLanguageTranslationFile> translationFiles) {
    return translationFiles;
  }

  List<Error> validate(MultiLanguageTranslationFile translationFile);

  default MultiLanguageTranslationFile update(MultiLanguageTranslationFile translationFile) {
    return translationFile;
  }
}
