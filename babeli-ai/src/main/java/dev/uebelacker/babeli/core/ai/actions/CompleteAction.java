package dev.uebelacker.babeli.core.ai.actions;

import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.actions.MissingAction;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.util.List;

public class CompleteAction extends MissingAction {

  public static final String NAME = "complete";

  static {
    ActionRegistry.registerAction(NAME, CompleteAction.class);
  }

  @Override
  public List<SingleLanguageTranslationFile> update(
      List<SingleLanguageTranslationFile> translationFiles) {
    return super.update(translationFiles);
  }
}
