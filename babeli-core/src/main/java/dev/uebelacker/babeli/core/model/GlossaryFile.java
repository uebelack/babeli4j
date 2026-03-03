package dev.uebelacker.babeli.core.model;

import java.io.File;
import java.util.List;

public record GlossaryFile(File file, List<GlossaryEntry> entries) {}
