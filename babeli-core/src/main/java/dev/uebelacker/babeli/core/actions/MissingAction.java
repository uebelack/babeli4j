package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.Translation;
import dev.uebelacker.babeli.core.model.TranslationFile;

import java.util.ArrayList;
import java.util.List;

public class MissingAction implements Action {
    @Override
    public String name() {
        return "missing";
    }

    @Override
    public List<Error> validate(List<TranslationFile> translationFiles) {
        var keys = translationFiles.stream()
                .flatMap(tf -> tf.translations().stream())
                .map(Translation::key)
                .distinct()
                .toList();

        var errors = new ArrayList<Error>();

        for (TranslationFile translationFile : translationFiles) {
            var missingKeys = keys.stream()
                    .filter(key -> translationFile.translations().stream().noneMatch(t -> t.key().equals(key)))
                    .toList();
            if (!missingKeys.isEmpty()) {
                errors.addAll(missingKeys.stream()
                        .map(key -> new Error(
                                name(),
                                translationFile.language(),
                                key,
                                "Missing key '%s' in file %s".formatted(key, translationFile.file().getName())))
                        .toList());
            }
        }
        return errors;
    }
}
