package dev.uebelacker.babeli.core.ai.actions;

import dev.uebelacker.babeli.core.actions.MissingAction;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.util.List;

public class CompleteAction extends MissingAction {
  @Override
  public String name() {
    return "complete-ai";
  }

  @Override
  public List<SingleLanguageTranslationFile> update(
      List<SingleLanguageTranslationFile> translationFiles) {
    return super.update(translationFiles);
  }
}
