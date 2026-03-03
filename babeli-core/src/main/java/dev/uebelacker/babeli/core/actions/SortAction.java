package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortAction implements Action {
  static {
    ActionRegistry.registerAction(new SortAction());
  }

  @Override
  public String name() {
    return "sort";
  }

  @Override
  public List<Error> validate(List<SingleLanguageTranslationFile> translationFiles) {
    var errors = new ArrayList<Error>();

    for (SingleLanguageTranslationFile translationFile : translationFiles) {
      var sortedTranslations =
          translationFile.translations().stream()
              .sorted(Comparator.comparing(Translation::key))
              .toList();
      if (!translationFile.translations().equals(sortedTranslations)) {
        errors.add(
            new Error(
                name(),
                translationFile.language(),
                translationFile.file().toString(),
                "Translations in file " + translationFile.file().getName() + " are not sorted."));
      }
    }

    return errors;
  }

  @Override
  public List<SingleLanguageTranslationFile> update(
      List<SingleLanguageTranslationFile> inputTranslationFiles) {
    var outputTranslationFiles = new ArrayList<SingleLanguageTranslationFile>();
    for (SingleLanguageTranslationFile translationFile : inputTranslationFiles) {
      outputTranslationFiles.add(
          new SingleLanguageTranslationFile(
              translationFile.language(),
              translationFile.file(),
              translationFile.translations().stream()
                  .sorted(Comparator.comparing(Translation::key))
                  .toList()));
    }
    return outputTranslationFiles;
  }

  @Override
  public List<Error> validate(MultiLanguageTranslationFile translationFile) {
    var errors = new ArrayList<Error>();
    var sortedTranslations =
        translationFile.translations().stream()
            .sorted(Comparator.comparing(Translation::key))
            .toList();

    if (!translationFile.translations().equals(sortedTranslations)) {
      errors.add(
          new Error(
              name(),
              null,
              translationFile.file().toString(),
              "Translations in file " + translationFile.file().getName() + " are not sorted."));
    }

    return errors;
  }

  @Override
  public MultiLanguageTranslationFile update(MultiLanguageTranslationFile translationFile) {
    return new MultiLanguageTranslationFile(
        translationFile.file(),
        translationFile.translations().stream()
            .sorted(Comparator.comparing(Translation::key))
            .toList());
  }
}
