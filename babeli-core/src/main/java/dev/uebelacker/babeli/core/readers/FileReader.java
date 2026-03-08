package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.io.File;

public interface FileReader {
  SingleLanguageTranslationFile readFile(String language, File file);

  MultiLanguageTranslationFile readFile(File file);
}
