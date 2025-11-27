package dev.uebelacker.readers;

import dev.uebelacker.model.TranslationFile;

public interface FileReader {
    String extension();

    TranslationFile readFile(TranslationFile file);
}
