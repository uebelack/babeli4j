package dev.uebelacker.babeli.core.services;

import java.util.Map;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.logging.Logger;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import dev.uebelacker.babeli.core.readers.FileReaderRegistry;
import dev.uebelacker.babeli.core.writers.FileWriterRegistry;

public class AddService {

    private final Configuration configuration;

    public AddService(Configuration configuration) {
        this.configuration = configuration;
    }

    public void add(String key, Map<String, String> translationValues) {
        configuration.validate();

        var fileReader = FileReaderRegistry.getFileReader(configuration);
        var fileWriter = FileWriterRegistry.getFileWriter(configuration);
        var log = new Logger(configuration);

        if (configuration.getFile() != null) {
            var translationFile = fileReader.readFile(configuration.getFile());
            var translations = Translations.fromTranslations(translationFile.translations());
            var translationService = new TranslationService(configuration, translations);

            for (var language : translations.getLanguages()) {
                var value = resolveValue(key, language, translationValues, translationService, log);
                if (value != null) {
                    translations.add(key, language, value);
                }
            }

            fileWriter.writeFile(new MultiLanguageTranslationFile(translationFile.file(), translations.getTranslations()));
        }

        if (configuration.getFiles() != null) {
            var translationFiles = configuration.getFiles().stream()
                    .map(f -> fileReader.readFile(f.getLanguage(), f.getFile()))
                    .toList();

            var translations = Translations.fromTranslations(
                    translationFiles.stream().flatMap(tf -> tf.translations().stream()).toList());

            var translationService = new TranslationService(configuration, translations);

            for (var translationFile : translationFiles) {
                var language = translationFile.language();
                var value = resolveValue(key, language, translationValues, translationService, log);
                if (value != null) {
                    translations.add(key, language, value);
                }
            }

            translationFiles.stream()
                    .map(tf -> new SingleLanguageTranslationFile(
                            tf.language(), tf.file(),
                            translations.getTranslationsForLanguage(tf.language())))
                    .forEach(fileWriter::writeFile);
        }
    }

    private String resolveValue(String key, String language, Map<String, String> translationValues,
                                TranslationService translationService, Logger log) {
        var provided = translationValues.get(language);
        if (provided != null) {
            return provided;
        }

        // Find a source language+value to translate from: prefer base language, then any other provided value
        var baseLanguage = configuration.getBaseLanguage();
        var sourceLanguage = translationValues.containsKey(baseLanguage) && translationValues.get(baseLanguage) != null
                ? baseLanguage
                : translationValues.entrySet().stream()
                        .filter(e -> e.getValue() != null && !e.getKey().equals(language))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);

        if (sourceLanguage == null) {
            log.warn("No source translation available to derive value for language '%s' and key '%s'. Skipping.".formatted(language, key));
            return null;
        }

        var sourceValue = translationValues.get(sourceLanguage);
        log.info("Generating translation for key '%s' and language '%s' from '%s'.".formatted(key, language, sourceLanguage));
        var translation = translationService.translate(sourceValue, sourceLanguage, language);
        log.info("Generated translation for key '%s' and language '%s': '%s'".formatted(key, language, translation));
        return translation;
    }
}
