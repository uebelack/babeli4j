package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.GlossaryException;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

public class GlossaryAction implements Action {

  private Configuration configuration;

  public GlossaryAction(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public String name() {
    return "glossary";
  }

  @Override
  public List<Error> validate(List<SingleLanguageTranslationFile> translationFiles) {
    return List.of();
  }

  @Override
  public List<SingleLanguageTranslationFile> update(
      List<SingleLanguageTranslationFile> translationFiles) {

    return translationFiles;
  }

  @Override
  public List<Error> validate(MultiLanguageTranslationFile translationFile) {
    return List.of();
  }

  public List<String> extractGlossaryWords(SingleLanguageTranslationFile translationFile) {
    var stopWords = getStopWords(translationFile.language());

    return translationFile.translations().stream()
        .map(translation -> translation.value().split(" "))
        .flatMap(Stream::of)
        .filter(word -> !stopWords.contains(word))
        .toList();
  }

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
