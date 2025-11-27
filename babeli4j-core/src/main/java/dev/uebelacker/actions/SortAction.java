package dev.uebelacker.actions;

import dev.uebelacker.model.Translation;
import dev.uebelacker.model.TranslationFile;

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
    public List<String> validate(List<TranslationFile> translationFiles) {
        var errors = new ArrayList<String>();

        for (TranslationFile translationFile : translationFiles) {
            var sortedTranslations = translationFile.translations().stream()
                    .sorted(Comparator.comparing(Translation::key))
                    .toList();
            if (!translationFile.translations().equals(sortedTranslations)) {
                errors.add("Translations in file " + translationFile.file().getName() + " are not sorted.");
            }
        }

        return errors;
    }

    @Override
    public List<TranslationFile> update(List<TranslationFile> translationFiles) {
        for (TranslationFile translationFile : translationFiles) {
            translationFile.translations().sort(Comparator.comparing(Translation::key));
        }
        return translationFiles;
    }
}
