package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.model.TranslationFile;
import dev.uebelacker.babeli.core.readers.PropertiesFileReader;

import java.io.File;
import java.util.List;

public class Fixtures {
    public static TranslationFile translationFileDe() {
        return PropertiesFileReader.readFile("de", new File("src/test/resources/properties/test_de.properties"));
    }

    public static TranslationFile translationFileFr() {
        return PropertiesFileReader.readFile("fr", new File("src/test/resources/properties/test_fr.properties"));
    }

    public static TranslationFile translationFileEn() {
        return PropertiesFileReader.readFile("en", new File("src/test/resources/properties/test_en.properties"));
    }

    public static List<TranslationFile> translationFiles() {
        return List.of(
                translationFileDe(), translationFileEn(), translationFileFr()
        );
    }
}
