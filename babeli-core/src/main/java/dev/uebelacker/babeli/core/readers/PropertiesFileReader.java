package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.model.Translation;
import dev.uebelacker.babeli.core.model.TranslationFile;

import java.io.File;
import java.util.Properties;

public class PropertiesFileReader implements FileReader {
    public static TranslationFile readFile(String language, File file) {
        return new PropertiesFileReader().readFile(new TranslationFile(language, file, null));
    }

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

