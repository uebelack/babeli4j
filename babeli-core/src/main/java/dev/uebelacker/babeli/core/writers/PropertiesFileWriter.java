package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.io.FileOutputStream;

public class PropertiesFileWriter implements FileWriter {
  @Override
  public void writeFile(SingleLanguageTranslationFile file) {
    ensureDirectory(file.file());

    var properties = new java.util.Properties();
    file.translations()
        .forEach(translation -> properties.setProperty(translation.key(), translation.value()));

    try (var outputStream = new FileOutputStream(file.file())) {
      properties.store(outputStream, null);
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }
  }

  @Override
  public void writeFile(dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile file) {
    throw new UnsupportedOperationException(
        "Multi-language translation files are not supported for properties files.");
  }
}
