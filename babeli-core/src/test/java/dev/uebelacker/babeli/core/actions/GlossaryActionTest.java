package dev.uebelacker.babeli.core.actions;

import static dev.uebelacker.babeli.core.Fixtures.translationFiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.GlossaryConfiguration;

class GlossaryActionTest {

    private Configuration configuration;

    private GlossaryAction glossaryAction;

    @BeforeEach
    void setUp() {
        configuration = new Configuration(new GlossaryConfiguration(new File("target/test/glossary/glossary.json")));
        glossaryAction = new GlossaryAction(configuration);

    }

    @Test
    void shouldCreateGlossary() {
        glossaryAction.update(translationFiles());
    }

}