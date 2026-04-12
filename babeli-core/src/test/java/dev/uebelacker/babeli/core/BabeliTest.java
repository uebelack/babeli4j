package dev.uebelacker.babeli.core;

import static org.assertj.core.api.Assertions.assertThat;

import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.services.ServiceRegistry;
import dev.uebelacker.babeli.core.writers.JsonFileWriter;
import dev.uebelacker.babeli.core.writers.PropertiesFileWriter;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BabeliTest {
  static final File FILE = new File("target/test/test.json");
  static final Set<LanguageFileConfiguration> FILES =
      Set.of(
          new LanguageFileConfiguration("en", new File("target/test/properties/en.properties")),
          new LanguageFileConfiguration("fr", new File("target/test/properties/fr.properties")),
          new LanguageFileConfiguration("de", new File("target/test/properties/de.properties")));

  Configuration configuration;

  @BeforeEach
  void setUp() {
    var propertiesFileWriter = new PropertiesFileWriter();
    propertiesFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "en",
            new File("target/test/properties/en.properties"),
            Fixtures.singleLanguageTranslationFileEn().translations()));

    propertiesFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "fr",
            new File("target/test/properties/fr.properties"),
            Fixtures.singleLanguageTranslationFileFr().translations()));

    propertiesFileWriter.writeFile(
        new SingleLanguageTranslationFile(
            "de",
            new File("target/test/properties/de.properties"),
            Fixtures.singleLanguageTranslationFileDe().translations()));

    var jsonFileWriter = new JsonFileWriter();
    jsonFileWriter.writeFile(
        new MultiLanguageTranslationFile(
            FILE, Fixtures.multiLanguageTranslationFile().translations()));

    ServiceRegistry.clearCache();
    ServiceRegistry.registerTranslationService("test", TestTranslationService.class);
    ServiceRegistry.registerGlossaryService("test", TestGlossaryService.class);

    configuration = new Configuration();
    configuration.setTranslationService("test");
    configuration.getGlossary().setService("test");
  }

  @Test
  @DisplayName("should validate single language translation files")
  void validateSingleLanguageTranslationFiles() {
    configuration.setFiles(FILES);
    configuration.setOperation(Operation.VALIDATE);
    var errors = Babeli.execute(configuration);

    assertThat(errors.stream().map(Error::toString).sorted().toList())
        .isEqualTo(
            List.of(
                "Error[action=missing, language=de, value=common.button.no, message=Missing translation for 'common.button.no' in file de.properties]",
                "Error[action=missing, language=en, value=common.button.perhaps, message=Missing translation for 'common.button.perhaps' in file en.properties]",
                "Error[action=sort, language=de, value=target/test/properties/de.properties, message=Translations in file de.properties are not sorted.]",
                "Error[action=sort, language=en, value=target/test/properties/en.properties, message=Translations in file en.properties are not sorted.]",
                "Error[action=sort, language=fr, value=target/test/properties/fr.properties, message=Translations in file fr.properties are not sorted.]"));
  }

  @Test
  @DisplayName("should validate multi language translation file")
  void validateMultiLanguageTranslationFiles() {
    configuration.setFile(FILE);
    configuration.setOperation(Operation.VALIDATE);
    var errors = Babeli.execute(configuration);

    assertThat(errors.stream().map(Error::toString).sorted().toList())
        .isEqualTo(
            List.of(
                "Error[action=missing, language=de, value=common.button.no, message=Missing translation for key 'common.button.no' and language 'de' in file 'test.json']",
                "Error[action=missing, language=en, value=common.button.perhaps, message=Missing translation for key 'common.button.perhaps' and language 'en' in file 'test.json']",
                "Error[action=sort, language=null, value=target/test/test.json, message=Translations in file test.json are not sorted.]"));
  }

  @Test
  @DisplayName("should update single language translation files")
  void shouldUpdateSingleLanguageTranslationFiles() {
    configuration.setFiles(FILES);
    configuration.setOperation(Operation.UPDATE);
    assertThat(Babeli.validate(configuration)).hasSize(5);
    Babeli.execute(configuration);
    assertThat(Babeli.validate(configuration)).isEmpty();
  }

  @Test
  @DisplayName("should update multi language translation file")
  void shouldUpdateMultiLanguageTranslationFiles() {
    configuration.setFile(FILE);
    configuration.setOperation(Operation.UPDATE);
    assertThat(Babeli.validate(configuration)).hasSize(3);
    Babeli.execute(configuration);
    assertThat(Babeli.validate(configuration)).isEmpty();
  }
}
