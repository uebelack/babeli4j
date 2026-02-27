package dev.uebelacker.babeli.core.model;

import java.io.File;
import java.util.List;

public record TranslationFile(String language, File file, List<Translation> translations) {
}
