package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import dev.uebelacker.babeli.core.services.ServiceRegistry;
import java.util.List;

public class GlossaryAction implements Action {
  public static final String NAME = "glossary";

  private Configuration configuration;

  public GlossaryAction(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public List<Error> validate(List<SingleLanguageTranslationFile> translationFiles) {
    return List.of();
  }

  @Override
  public List<Error> validate(MultiLanguageTranslationFile translationFile) {
    return List.of();
  }

  @Override
  public List<SingleLanguageTranslationFile> update(
      List<SingleLanguageTranslationFile> translationFiles) {
    update(
        Translations.fromTranslations(
            translationFiles.stream().flatMap(tf -> tf.translations().stream()).toList()));
    return translationFiles;
  }

  @Override
  public MultiLanguageTranslationFile update(MultiLanguageTranslationFile translationFile) {
    update(Translations.fromTranslations(translationFile.translations()));
    return translationFile;
  }

  private void update(Translations translations) {
    var glossaryService = ServiceRegistry.getGlossaryService(configuration);

    translations
        .getKeys()
        .forEach(
            key -> glossaryService.updateWith(key, translations.getTranslationsMapForKey(key)));
  }
}
