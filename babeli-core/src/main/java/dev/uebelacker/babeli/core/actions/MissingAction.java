package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.*;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.services.TranslationService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MissingAction implements Action {
  public static final String NAME = "missing";
  private static final Logger LOG = Logger.getLogger(MissingAction.class.getName());
  private final Configuration configuration;

  public MissingAction(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public List<Error> validate(List<SingleLanguageTranslationFile> translationFiles) {
    var keys =
        translationFiles.stream()
            .flatMap(tf -> tf.translations().stream())
            .map(Translation::key)
            .distinct()
            .toList();

    var errors = new ArrayList<Error>();

    for (SingleLanguageTranslationFile translationFile : translationFiles) {
      var missingKeys =
          keys.stream()
              .filter(
                  key ->
                      translationFile.translations().stream().noneMatch(t -> t.key().equals(key)))
              .toList();
      if (!missingKeys.isEmpty()) {
        errors.addAll(
            missingKeys.stream()
                .map(
                    key ->
                        new Error(
                            NAME,
                            translationFile.language(),
                            key,
                            "Missing translation for '%s' in file %s"
                                .formatted(key, translationFile.file().getName())))
                .toList());
      }
    }
    return errors;
  }

  @Override
  public List<Error> validate(MultiLanguageTranslationFile translationFile) {
    var keyLanguageMap =
        Translations.fromTranslations(translationFile.translations()).getKeyLanguageMap();
    var languages =
        translationFile.translations().stream().map(Translation::language).distinct().toList();
    var errors = new ArrayList<Error>();

    keyLanguageMap
        .keySet()
        .forEach(
            key ->
                languages.forEach(
                    language -> {
                      if (!keyLanguageMap.get(key).containsKey(language)) {
                        errors.add(
                            new Error(
                                NAME,
                                language,
                                key,
                                "Missing translation for key '%s' and language '%s' in file '%s'"
                                    .formatted(key, language, translationFile.file().getName())));
                      }
                    }));

    return errors;
  }

  @Override
  public MultiLanguageTranslationFile update(MultiLanguageTranslationFile translationFile) {
    var translations = Translations.fromTranslations(translationFile.translations());

    translations
        .getLanguages()
        .forEach(
            language ->
                translations.getKeys().stream()
                    .filter(key -> translations.getTranslation(key, language).isEmpty())
                    .forEach(
                        key -> {
                          LOG.info(
                              "Missing translation for key '%s' and language '%s' in file '%s'. Generating translation."
                                  .formatted(key, language, translationFile.file().getName()));
                          var translation = translate(key, language, translations);
                          LOG.info(
                              "Generated translation for key '%s' and language '%s' in file '%s': '%s'"
                                  .formatted(
                                      key,
                                      language,
                                      translationFile.file().getName(),
                                      translation));
                          translations.add(key, language, translation);
                        }));

    return new MultiLanguageTranslationFile(translationFile.file(), translations.getTranslations());
  }

  @Override
  public List<SingleLanguageTranslationFile> update(
      List<SingleLanguageTranslationFile> inputTranslationFiles) {

    var keys =
        inputTranslationFiles.stream()
            .flatMap(tf -> tf.translations().stream())
            .map(Translation::key)
            .distinct()
            .toList();

    var translations =
        Translations.fromTranslations(
            inputTranslationFiles.stream().flatMap(tf -> tf.translations().stream()).toList());

    var outputTranslationFiles = new ArrayList<SingleLanguageTranslationFile>();
    for (SingleLanguageTranslationFile translationFile : inputTranslationFiles) {
      keys.stream()
          .filter(
              key -> translationFile.translations().stream().noneMatch(t -> t.key().equals(key)))
          .forEach(
              key -> {
                LOG.info(
                    "Missing translation for key '%s' and language '%s' in file '%s'. Generating translation."
                        .formatted(
                            key, translationFile.language(), translationFile.file().getName()));
                var translation = translate(key, translationFile.language(), translations);
                LOG.info(
                    "Generated translation for key '%s' and language '%s' in file '%s': '%s'"
                        .formatted(
                            key,
                            translationFile.language(),
                            translationFile.file().getName(),
                            translation));
                translations.add(key, translationFile.language(), translation);
              });

      outputTranslationFiles.add(
          new SingleLanguageTranslationFile(
              translationFile.language(),
              translationFile.file(),
              translations.getTranslationsForLanguage(translationFile.language())));
    }

    return outputTranslationFiles;
  }

  private String translate(String key, String language, Translations translations) {
    var translationService = new TranslationService(configuration, translations);
    if (!language.equals(configuration.getBaseLanguage())) {
      var translation = translations.getTranslation(key, configuration.getBaseLanguage());
      if (translation.isPresent()) {
        return translationService.translate(
            translation.get(), configuration.getBaseLanguage(), language);
      }
    }

    var referenceLanguage =
        translations.getTranslationsMapForKey(key).keySet().stream()
            .filter(l -> !l.equals(language))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("No other translation found for key " + key));

    var referenceValue =
        translations
            .getTranslation(key, referenceLanguage)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "No translation found for key "
                            + key
                            + " and language "
                            + referenceLanguage));

    return translationService.translate(referenceValue, referenceLanguage, language);
  }
}
