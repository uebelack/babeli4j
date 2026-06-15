package dev.uebelacker.babeli.cli.commands;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import dev.uebelacker.babeli.core.readers.FileReaderRegistry;
import dev.uebelacker.babeli.core.services.TranslationService;
import dev.uebelacker.babeli.core.util.EnvUtils;
import dev.uebelacker.babeli.core.writers.FileWriterRegistry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine.Command;

@Command(name = "add", description = "Adds a translation key interactively.")
@SuppressWarnings("java:S106")
public class Add extends AbstractCommand {

  @Override
  public Integer call() {
    try (var terminal = createTerminal()) {
      var reader = createLineReader(terminal);
      var configuration = createConfiguration();

      for (var subConfiguration : configuration.autoConfigure()) {
        subConfiguration.validate();
        var fileReader = FileReaderRegistry.getFileReader(subConfiguration);
        var fileWriter = FileWriterRegistry.getFileWriter(subConfiguration);

        if (subConfiguration.getFile() != null) {
          var translationFile = fileReader.readFile(subConfiguration.getFile());
          var updatedTranslationFile = addToMultiLanguageFile(reader, subConfiguration, translationFile);
          fileWriter.writeFile(updatedTranslationFile);
        }

        if (subConfiguration.getFiles() != null) {
          var translationFiles =
              subConfiguration.getFiles().stream()
                  .map(file -> fileReader.readFile(file.getLanguage(), file.getFile()))
                  .toList();

          var updatedTranslationFiles =
              addToSingleLanguageFiles(reader, subConfiguration, translationFiles);

          updatedTranslationFiles.forEach(fileWriter::writeFile);
        }
      }

      return 0;
    } catch (UserInterruptException | EndOfFileException e) {
      System.err.println("Aborted.");
      return 1;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return 1;
    }
  }

  protected Terminal createTerminal() throws Exception {
    return TerminalBuilder.builder().system(true).build();
  }

  protected LineReader createLineReader(Terminal terminal) {
    return LineReaderBuilder.builder().terminal(terminal).build();
  }

  private MultiLanguageTranslationFile addToMultiLanguageFile(
      LineReader reader,
      Configuration configuration,
      MultiLanguageTranslationFile translationFile) {
    var translations = Translations.fromTranslations(translationFile.translations());
    var valuesByLanguage =
        promptForTranslations(
            reader, translations, "file '%s'".formatted(translationFile.file().getPath()), configuration);

    var key = valuesByLanguage.key();
    var values = valuesByLanguage.values();

    values.forEach((language, value) -> translations.add(key, language, value));
    return new MultiLanguageTranslationFile(translationFile.file(), translations.getTranslations());
  }

  private List<SingleLanguageTranslationFile> addToSingleLanguageFiles(
      LineReader reader,
      Configuration configuration,
      List<SingleLanguageTranslationFile> translationFiles) {
    if (translationFiles.isEmpty()) {
      throw new IllegalStateException("No languages found in configured files.");
    }

    var translations =
        Translations.fromTranslations(
            translationFiles.stream().flatMap(file -> file.translations().stream()).toList());

    var valuesByLanguage =
        promptForTranslations(
            reader, translations, "configured language files", configuration);

    var key = valuesByLanguage.key();
    var values = valuesByLanguage.values();

    values.forEach((language, value) -> translations.add(key, language, value));

    return translationFiles.stream()
        .map(
            file ->
                new SingleLanguageTranslationFile(
                    file.language(), file.file(), translations.getTranslationsForLanguage(file.language())))
        .toList();
  }

  private KeyTranslations promptForTranslations(
      LineReader reader,
      Translations existingTranslations,
      String targetDescription,
      Configuration configuration) {
    var languages = existingTranslations.getLanguages();
    if (languages.isEmpty()) {
      throw new IllegalStateException("No languages found in %s.".formatted(targetDescription));
    }

    var key = readRequired(reader, "Translation key for %s: ".formatted(targetDescription));
    if (existingTranslations.getKeys().contains(key)) {
      throw new IllegalArgumentException("Key '%s' already exists.".formatted(key));
    }

    var values = new LinkedHashMap<String, String>();
    for (var language : languages) {
      var entered =
          Optional.ofNullable(
                  reader.readLine(
                      "Translation for '%s' (leave empty for auto-translation): ".formatted(language)))
              .map(String::trim)
              .orElse("");
      if (!entered.isEmpty()) {
        values.put(language, entered);
      }
    }

    if (values.isEmpty()) {
      throw new IllegalArgumentException(
          "At least one translation must be provided to generate missing languages.");
    }

    var missingLanguages = languages.stream().filter(language -> !values.containsKey(language)).toList();
    if (!missingLanguages.isEmpty()) {
      if (EnvUtils.get(BABELI_MODEL_PROVIDER, configuration.getModelProvider()) == null) {
        throw new IllegalArgumentException(
            "No model provider specified. Please set '--model-provider' or BABELI_MODEL_PROVIDER for auto-translation.");
      }

      var contextTranslations = Translations.fromTranslations(existingTranslations.getTranslations());
      values.forEach((language, value) -> contextTranslations.add(key, language, value));
      var translationService = new TranslationService(configuration, contextTranslations);

      for (var language : missingLanguages) {
        var reference = findReference(values, configuration.getBaseLanguage(), language);
        var translated = translationService.translate(reference.value(), reference.language(), language);
        values.put(language, translated);
      }
    }

    return new KeyTranslations(key, values);
  }

  private ReferenceTranslation findReference(
      Map<String, String> valuesByLanguage, String baseLanguage, String targetLanguage) {
    if (!targetLanguage.equals(baseLanguage) && valuesByLanguage.containsKey(baseLanguage)) {
      return new ReferenceTranslation(baseLanguage, valuesByLanguage.get(baseLanguage));
    }

    return valuesByLanguage.entrySet().stream()
        .filter(entry -> !entry.getKey().equals(targetLanguage))
        .findFirst()
        .map(entry -> new ReferenceTranslation(entry.getKey(), entry.getValue()))
        .orElseThrow(() -> new IllegalStateException("No reference translation available."));
  }

  private String readRequired(LineReader reader, String prompt) {
    var value = Optional.ofNullable(reader.readLine(prompt)).map(String::trim).orElse("");
    if (value.isEmpty()) {
      throw new IllegalArgumentException("Translation key must not be empty.");
    }
    return value;
  }

  private record ReferenceTranslation(String language, String value) {}

  private record KeyTranslations(String key, Map<String, String> values) {}
}
