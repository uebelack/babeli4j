package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.util.Properties;

public class PropertiesFileReader implements FileReader {
  @Override
  public String extension() {
    return "properties";
  }

  @Override
  public SingleLanguageTranslationFile readFile(String language, File file) {
    Properties properties = new Properties();
    try (var inputStream = file.toURI().toURL().openStream()) {
      properties.load(inputStream);
    } catch (Exception e) {
      throw new FileReaderException(file, e);
    }

    return new SingleLanguageTranslationFile(
        language,
        file,
        properties.entrySet().stream()
            .map(
                entry ->
                    new Translation(
                        language, entry.getKey().toString(), entry.getValue().toString()))
            .toList());
  }

  @Override
  public MultiLanguageTranslationFile readFile(File file) {
    throw new UnsupportedOperationException(
        "Multi-language translation files are not supported for properties files.");
  }
}
