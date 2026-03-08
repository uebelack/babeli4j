package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.io.File;

public interface FileWriter {
  void writeFile(SingleLanguageTranslationFile file);

  void writeFile(MultiLanguageTranslationFile file);

  default void ensureDirectory(File file) {
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
  }
}
