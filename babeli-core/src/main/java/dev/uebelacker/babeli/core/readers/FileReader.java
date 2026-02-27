package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.model.TranslationFile;

public interface FileReader {
    String extension();

    TranslationFile readFile(TranslationFile file);
}
