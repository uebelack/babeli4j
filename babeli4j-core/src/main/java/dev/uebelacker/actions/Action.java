package dev.uebelacker.actions;

import dev.uebelacker.model.TranslationFile;

import java.util.List;

public interface Action {
    String name();

    List<String> validate(List<TranslationFile> translationFiles);

    List<TranslationFile> update(List<TranslationFile> translationFiles);
}
