package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.readers.PropertiesFileReader;
import java.io.File;
import java.util.List;

public class Fixtures {
  public static SingleLanguageTranslationFile singleLanguageTranslationFileDe() {
    return PropertiesFileReader.readFile(
        "de", new File("src/test/resources/properties/test_de.properties"));
  }

  public static SingleLanguageTranslationFile singleLanguageTranslationFileFr() {
    return PropertiesFileReader.readFile(
        "fr", new File("src/test/resources/properties/test_fr.properties"));
  }

  public static SingleLanguageTranslationFile singleLanguageTranslationFileEn() {
    return PropertiesFileReader.readFile(
        "en", new File("src/test/resources/properties/test_en.properties"));
  }

  public static List<SingleLanguageTranslationFile> singleLanguageTranslationFiles() {
    return List.of(
        singleLanguageTranslationFileDe(),
        singleLanguageTranslationFileEn(),
        singleLanguageTranslationFileFr());
  }
}
