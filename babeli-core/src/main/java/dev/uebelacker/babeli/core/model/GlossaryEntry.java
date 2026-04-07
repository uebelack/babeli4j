package dev.uebelacker.babeli.core.model;

import java.util.Map;

public record GlossaryEntry(String key, Map<String, String> translations, String description) {}
