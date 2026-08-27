package dev.uebelacker.babeli.core.writers;

import de.poiu.apron.ApronOptions;
import de.poiu.apron.PropertyFile;
import de.poiu.apron.UnicodeHandling;
import de.poiu.apron.entry.PropertyEntry;
import de.poiu.apron.reformatting.AttachCommentsTo;
import de.poiu.apron.reformatting.ReformatOptions;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.nio.charset.Charset;
import java.util.HashSet;

public class PropertiesFileWriter implements FileWriter {

  private final Configuration configuration;

  public PropertiesFileWriter(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void writeFile(SingleLanguageTranslationFile file) {
    try {
      ensureFile(file.file());

      var properties = PropertyFile.from(file.file());
      var keys = new HashSet<String>();

      file.translations()
          .forEach(
              translation -> {
                keys.add(translation.key());
                if (properties.get(translation.key()) == null) {
                  properties.appendEntry(
                      new PropertyEntry("", translation.key(), "=", translation.value(), "\n"));
                } else if (!properties.get(translation.key()).equals(translation.value())) {
                  properties.set(translation.key(), translation.value());
                }
              });

      properties
          .keys()
          .forEach(
              key -> {
                if (!keys.contains(key)) {
                  properties.remove(key);
                }
              });

      if (configuration.getActions().contains("sort")) {
        // Keep comments on their original lines, so a comment header stays at the top of the file
        // instead of being dragged along with the entry that used to follow it.
        properties.reorderByKey(ReformatOptions.create().with(AttachCommentsTo.ORIG_LINE));
      }

      properties.overwrite(
          file.file(),
          ApronOptions.create()
              .with(Charset.forName(configuration.getCharset()))
              .with(UnicodeHandling.BY_CHARSET));
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
