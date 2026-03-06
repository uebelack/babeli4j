package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.readers.JsonFileReader;
import dev.uebelacker.babeli.core.readers.PropertiesFileReader;
import java.io.File;
import java.util.List;

public class Fixtures {
  private static final PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
  private static final JsonFileReader jsonFileReader = new JsonFileReader();

  public static SingleLanguageTranslationFile singleLanguageTranslationFileDe() {
    return propertiesFileReader.readFile(
        "de", new File("src/test/resources/properties/test_de.properties"));
  }

  public static SingleLanguageTranslationFile singleLanguageTranslationFileFr() {
    return propertiesFileReader.readFile(
        "fr", new File("src/test/resources/properties/test_fr.properties"));
  }

  public static SingleLanguageTranslationFile singleLanguageTranslationFileEn() {
    return propertiesFileReader.readFile(
        "en", new File("src/test/resources/properties/test_en.properties"));
  }

  public static List<SingleLanguageTranslationFile> singleLanguageTranslationFiles() {
    return List.of(
        singleLanguageTranslationFileDe(),
        singleLanguageTranslationFileEn(),
        singleLanguageTranslationFileFr());
  }

  public static MultiLanguageTranslationFile multiLanguageTranslationFile() {
    return jsonFileReader.readFile(new File("src/test/resources/json/test.json"));
  }
}
