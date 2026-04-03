package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.GlossaryException;
import dev.uebelacker.babeli.core.model.*;
import dev.uebelacker.babeli.core.model.Error;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GlossaryAction implements Action {

  private final Configuration configuration;

  public GlossaryAction(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public String name() {
    return "glossary";
  }

  @Override
  public List<Error> validateSingleLanguageTranslationFile(
      List<SingleLanguageTranslationFile> translationFiles) {
    return validateGlossary(
        Translations.fromTranslations(
            translationFiles.stream()
                .map(SingleLanguageTranslationFile::translations)
                .flatMap(List::stream)
                .toList()));
  }

  @Override
  public List<Error> validate(MultiLanguageTranslationFile translationFile) {
    return validateGlossary(Translations.fromTranslations(translationFile.translations()));
  }

  @Override
  public List<SingleLanguageTranslationFile> update(
      List<SingleLanguageTranslationFile> translationFiles) {
    updateGlossary(
        Translations.fromTranslations(
            translationFiles.stream()
                .map(SingleLanguageTranslationFile::translations)
                .flatMap(List::stream)
                .toList()));
    return translationFiles;
  }

  @Override
  public MultiLanguageTranslationFile update(MultiLanguageTranslationFile translationFile) {
    updateGlossary(Translations.fromTranslations(translationFile.translations()));
    return translationFile;
  }

  private List<Error> validateGlossary(Translations translations) {
    var errors = new ArrayList<Error>();

    return errors;
  }

  private void updateGlossary(Translations translations) {}

  //
  //    var jsonFileReader = new JsonFileReader();
  //    var glossaryFile = jsonFileReader.readFile(configuration.glossary().file());
  //    var glossary = Translations.fromTranslations(glossaryFile.translations());
  //
  //    var glossary = MultiLanguageTranslationFile.read(configuration.glossary().file());
  //    var baseLanguage = configuration.baseLanguage();
  //    var stopWords = getStopWords(baseLanguage);
  //
  //    translations
  //        .getKeys()
  //        .forEach(
  //            key -> {
  //              var words = translations.getTranslation(key, baseLanguage).split(" ");
  //              Stream.of(words)
  //                  .map(String::trim)
  //                  .map(s -> s.replaceAll("\\W", ""))
  //                  .filter(s -> !s.isBlank())
  //                  .filter(word -> !stopWords.contains(word.toLowerCase()))
  //                  .forEach(word -> updateGlossaryEntry(word, glossary, baseLanguage,
  // translations));
  //            });
  //  }
  //
  //  private void updateGlossaryEntry(
  //      String word, Glossary glossary, String baseLanguage, Translations translations) {
  //
  //    var glossaryEntry =
  //        glossary
  //            .getEntry(baseLanguage, word)
  //            .or(
  //                () -> {
  //                  var entry = new GlossaryEntry(List.of(new Translation(baseLanguage, word,
  // word)));
  //                  glossary.add(entry);
  //                  return Optional.of(entry);
  //                });
  //  }

  //  private Translation createGlossaryTranslation(
  //      String language,
  //      String baseLanguage,
  //      String word,) {
  //    if (language.equals(baseLanguage)) {
  //      return new Translation(language, word, word);
  //    }
  //
  //    var translation = Trans
  //
  //    if (translation.value().split(" ").length == 1) {
  //      return new Translation(translation.language(), word, translation.value());
  //    }
  //
  //    var translatedWord =
  //        TranslationServiceRegistry.getTranslationService()
  //            .translate(
  //                word,
  //                baseLanguage,
  //                translation.language(),
  //                "Please use the translation used in this term
  // '%s'".formatted(translation.value()));
  //
  //    return new Translation(translation.language(), word, translatedWord);
  //  }
  //
  //  private SingleLanguageTranslationFile findBaseLanguageTranslationFile(
  //      List<SingleLanguageTranslationFile> translationFiles, String baseLanguage) {
  //    return translationFiles.stream()
  //        .filter(translationFile -> translationFile.language().equals(baseLanguage))
  //        .findFirst()
  //        .orElseThrow(
  //            () ->
  //                new GlossaryException(
  //                    "Base language '%s' not found in translation
  // files".formatted(baseLanguage)));
  //  }
  //
  //
  //
  //  public Set<String> extractGlossaryWords(SingleLanguageTranslationFile translationFile) {
  //    var stopWords = getStopWords(translationFile.language());
  //
  //    return new LinkedHashSet<>(
  //        translationFile.translations().stream()
  //            .map(translation -> translation.value().split(" "))
  //            .flatMap(Stream::of)
  //            .map(String::trim)
  //            .map(s -> s.replaceAll("\\W", ""))
  //            .filter(s -> !s.isBlank())
  //            .filter(word -> !stopWords.contains(word.toLowerCase()))
  //            .sorted()
  //            .toList());
  //  }

  private List<String> getStopWords(String language) {
    List<String> stopWords;
    try (InputStream is =
        GlossaryAction.class
            .getClassLoader()
            .getResourceAsStream("stop-words/%s.txt".formatted(language))) {
      assert is != null;
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
        stopWords = reader.lines().toList();
      }
    } catch (Exception ex) {
      throw new GlossaryException(
          "Glossary creation for language '%s' not supported".formatted(language));
    }
    return stopWords;
  }
}
