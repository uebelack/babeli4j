package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Locale;
import org.apache.commons.collections4.properties.OrderedProperties;

public class PropertiesFileWriter implements FileWriter {

  private final Configuration configuration;

  public PropertiesFileWriter(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void writeFile(SingleLanguageTranslationFile file) {
    ensureDirectory(file.file());

    var properties = new OrderedProperties();
    file.translations()
        .forEach(translation -> properties.setProperty(translation.key(), translation.value()));
    try (var outputStream = new FileOutputStream(file.file())) {
      properties.store(new OutputStreamWriter(outputStream, configuration.getCharset()), null);
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }

    var currentDate =
        LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("EEE MMM dd", Locale.ENGLISH));

    try {
      var lines =
          Files.readAllLines(file.file().toPath()).stream()
              .filter(line -> !line.startsWith("#%s".formatted(currentDate)))
              .toList();

      Files.write(file.file().toPath(), lines);
    } catch (IOException e) {
      throw new FileWriterException(file.file(), e);
    }
  }

  @Override
  public void writeFile(dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile file) {
    throw new UnsupportedOperationException(
        "Multi-language translation files are not supported for properties files.");
  }
}
