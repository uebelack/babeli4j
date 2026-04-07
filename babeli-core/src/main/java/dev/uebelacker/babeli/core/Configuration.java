package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.configuration.GlossaryConfiguration;

public record Configuration(
    String identifier,
    String baseLanguage,
    String translationService,
    GlossaryConfiguration glossary) {}
