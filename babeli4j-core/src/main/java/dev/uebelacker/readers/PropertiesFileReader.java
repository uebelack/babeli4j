package dev.uebelacker.readers;

import dev.uebelacker.model.Translation;
import dev.uebelacker.model.TranslationFile;

import java.util.Properties;

public class PropertiesFileReader implements FileReader {
    @Override
    public String extension() {
        return "properties";
    }

    @Override
    public TranslationFile readFile(TranslationFile translationFile) {
        Properties properties = new Properties();
        try (var inputStream = translationFile.file().toURI().toURL().openStream()) {
            properties.load(inputStream);
        } catch (Exception e) {
            throw new FileReaderException(translationFile.file(), e);
        }

        return new TranslationFile(translationFile.language(), translationFile.file(), properties.entrySet().stream()
                .map(entry -> new Translation(translationFile.language(), entry.getKey().toString(), entry.getValue().toString()))
                .toList());
    }
}

