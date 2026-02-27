package dev.uebelacker.babeli.core.ai.actions;

import java.util.List;

import dev.uebelacker.babeli.core.actions.MissingAction;
import dev.uebelacker.babeli.core.model.TranslationFile;

public class CompleteAction extends MissingAction {
    @Override
    public String name() {
        return "complete-ai";
    }

    @Override
    public List<TranslationFile> update(List<TranslationFile> translationFiles) {
        return super.update(translationFiles);
    }
}
