package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.Translation;
import dev.uebelacker.babeli.core.model.TranslationFile;

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
    public List<Error> validate(List<TranslationFile> translationFiles) {
        var errors = new ArrayList<Error>();

        for (TranslationFile translationFile : translationFiles) {
            var sortedTranslations = translationFile.translations().stream()
                    .sorted(Comparator.comparing(Translation::key))
                    .toList();
            if (!translationFile.translations().equals(sortedTranslations)) {
                errors.add(new Error(name(),
                        translationFile.language(),
                        translationFile.file().toString(),
                        "Translations in file " + translationFile.file().getName() + " are not sorted."));
            }
        }

        return errors;
    }

    @Override
    public List<TranslationFile> update(List<TranslationFile> inputTranslationFiles) {
        var outputTranslationFiles = new ArrayList<TranslationFile>();
        for (TranslationFile translationFile : inputTranslationFiles) {
            outputTranslationFiles.add(new TranslationFile(
                    translationFile.language(),
                    translationFile.file(),
                    translationFile.translations().stream().sorted(Comparator.comparing(Translation::key)).toList()
            ));
        }
        return outputTranslationFiles;
    }
}
