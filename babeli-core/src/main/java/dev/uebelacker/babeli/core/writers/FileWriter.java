package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;

public interface FileWriter {
  String extension();

  void writeFile(SingleLanguageTranslationFile file);

  void writeFile(MultiLanguageTranslationFile file);
}
