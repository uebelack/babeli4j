package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.TranslationFile;

import java.util.List;

public interface Action {
    String name();

    List<Error> validate(List<TranslationFile> translationFiles);

    default List<TranslationFile> update(List<TranslationFile> translationFiles) {
        return translationFiles;
    }
}
