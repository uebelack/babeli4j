package dev.uebelacker.babeli.core.readers;

import de.poiu.apron.PropertyFile;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.nio.charset.Charset;

public class PropertiesFileReader implements FileReader {
  private final Configuration configuration;

  public PropertiesFileReader(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public SingleLanguageTranslationFile readFile(String language, File file) {
    try {
      var properties = PropertyFile.from(file, Charset.forName(configuration.getCharset()));
      return new SingleLanguageTranslationFile(
          language,
          file,
          properties.keys().stream()
              .map(key -> new Translation(language, key, properties.get(key)))
              .toList());
    } catch (Exception e) {
      throw new FileReaderException(file, e);
    }
  }

  @Override
  public MultiLanguageTranslationFile readFile(File file) {
    throw new UnsupportedOperationException(
        "Multi-language translation files are not supported for properties files.");
  }
}
