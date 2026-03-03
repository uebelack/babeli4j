package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;

public interface FileReader {
  String extension();

  SingleLanguageTranslationFile readFile(SingleLanguageTranslationFile file);
}
