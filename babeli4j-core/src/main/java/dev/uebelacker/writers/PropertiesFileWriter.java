package dev.uebelacker.writers;

import dev.uebelacker.model.TranslationFile;

import java.io.FileOutputStream;

public class PropertiesFileWriter implements FileWriter {
    @Override
    public String extension() {
        return "properties";
    }

    @Override
    public void writeFile(TranslationFile file) {
        var properties = new java.util.Properties();
        file.translations().forEach(translation -> properties.setProperty(translation.key(), translation.value()));

        if (!file.file().getParentFile().exists()) {
            file.file().getParentFile().mkdirs();
        }

        try (var outputStream = new FileOutputStream(file.file())) {
            properties.store(outputStream, null);
        } catch (Exception e) {
            throw new FileWriterException(file.file(), e);
        }
    }
}
