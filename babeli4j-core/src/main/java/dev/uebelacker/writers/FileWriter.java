package dev.uebelacker.writers;

import dev.uebelacker.model.TranslationFile;

public interface FileWriter {
    String extension();

    void writeFile(TranslationFile file);
}
