package dev.uebelacker.babeli.core.actions;

import static dev.uebelacker.babeli.core.Fixtures.singleLanguageTranslationFiles;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.GlossaryConfiguration;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GlossaryActionTest {

  private Configuration configuration;

  private GlossaryAction glossaryAction;

  @BeforeEach
  void setUp() {
    configuration =
        new Configuration(
            new GlossaryConfiguration(new File("target/test/glossary/glossary.json")));
    glossaryAction = new GlossaryAction(configuration);
  }

  @Test
  void shouldCreateGlossary() {
    glossaryAction.update(singleLanguageTranslationFiles());
  }
}
