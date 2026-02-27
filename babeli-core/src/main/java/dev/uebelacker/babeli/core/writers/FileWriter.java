package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.model.TranslationFile;

public interface FileWriter {
    String extension();

    void writeFile(TranslationFile file);


}
