package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.commons.collections4.properties.OrderedProperties;

public class PropertiesFileReader implements FileReader {
  private final Configuration configuration;

  public PropertiesFileReader(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public SingleLanguageTranslationFile readFile(String language, File file) {
    var properties = new OrderedProperties();
    try (var inputStream = file.toURI().toURL().openStream()) {
      properties.load(new InputStreamReader(inputStream, configuration.getCharset()));
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
