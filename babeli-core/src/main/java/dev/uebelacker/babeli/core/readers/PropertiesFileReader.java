package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.util.Properties;

public class PropertiesFileReader implements FileReader {
  public static SingleLanguageTranslationFile readFile(String language, File file) {
    return new PropertiesFileReader()
        .readFile(new SingleLanguageTranslationFile(language, file, null));
  }

  @Override
  public String extension() {
    return "properties";
  }

  @Override
  public SingleLanguageTranslationFile readFile(SingleLanguageTranslationFile translationFile) {
    Properties properties = new Properties();
    try (var inputStream = translationFile.file().toURI().toURL().openStream()) {
      properties.load(inputStream);
    } catch (Exception e) {
      throw new FileReaderException(translationFile.file(), e);
    }

    return new SingleLanguageTranslationFile(
        translationFile.language(),
        translationFile.file(),
        properties.entrySet().stream()
            .map(
                entry ->
                    new Translation(
                        translationFile.language(),
                        entry.getKey().toString(),
                        entry.getValue().toString()))
            .toList());
  }
}
