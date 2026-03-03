package dev.uebelacker.babeli.core.model;

import java.util.List;

public record GlossaryEntry(List<Translation> translations, String description) {}
