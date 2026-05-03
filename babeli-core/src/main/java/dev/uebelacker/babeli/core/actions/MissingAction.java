package dev.uebelacker.babeli.core.actions;

import static dev.uebelacker.babeli.core.util.FileUtils.relativePath;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.logging.Logger;
import dev.uebelacker.babeli.core.model.*;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.services.TranslationService;
import java.util.ArrayList;
import java.util.List;

public class MissingAction implements Action {
  public static final String NAME = "missing";
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
                                .formatted(
                                    key,
                                    relativePath(
                                        configuration.getWorkingDirectory(),
                                        translationFile.file()))))
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
                                    .formatted(
                                        key,
                                        language,
                                        relativePath(
                                            configuration.getWorkingDirectory(),
                                            translationFile.file()))));
                      }
                    }));

    return errors;
  }

  @Override
  public MultiLanguageTranslationFile update(MultiLanguageTranslationFile translationFile) {
    var translations = Translations.fromTranslations(translationFile.translations());
    var translationService = new TranslationService(configuration, translations);
    var logger = new Logger(configuration);
    translations
        .getLanguages()
        .forEach(
            language ->
                translations.getKeys().stream()
                    .filter(key -> translations.getTranslation(key, language).isEmpty())
                    .forEach(
                        key -> {
                          logger.info(
                              "Missing translation for key '%s' and language '%s' in file '%s'. Generating translation."
                                  .formatted(
                                      key,
                                      language,
                                      relativePath(
                                          configuration.getWorkingDirectory(),
                                          translationFile.file())));
                          var translation =
                              translate(translationService, key, language, translations);
                          logger.info(
                              "Generated translation for key '%s' and language '%s' in file '%s': '%s'"
                                  .formatted(
                                      key,
                                      language,
                                      relativePath(
                                          configuration.getWorkingDirectory(),
                                          translationFile.file()),
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

    var translationService = new TranslationService(configuration, translations);
    var logger = new Logger(configuration);

    var outputTranslationFiles = new ArrayList<SingleLanguageTranslationFile>();
    for (SingleLanguageTranslationFile translationFile : inputTranslationFiles) {
      keys.stream()
          .filter(
              key -> translationFile.translations().stream().noneMatch(t -> t.key().equals(key)))
          .forEach(
              key -> {
                logger.info(
                    "Missing translation for key '%s' and language '%s' in file '%s'. Generating translation."
                        .formatted(
                            key,
                            translationFile.language(),
                            relativePath(
                                configuration.getWorkingDirectory(), translationFile.file())));
                var translation =
                    translate(translationService, key, translationFile.language(), translations);
                logger.info(
                    "Generated translation for key '%s' and language '%s' in file '%s': '%s'"
                        .formatted(
                            key,
                            translationFile.language(),
                            relativePath(
                                configuration.getWorkingDirectory(), translationFile.file()),
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

  private String translate(
      TranslationService translationService,
      String key,
      String language,
      Translations translations) {
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
