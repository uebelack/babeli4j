package dev.uebelacker.babeli.core.configuration;

import dev.uebelacker.babeli.core.Configuration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class JavaPropertiesAutoConfigurator implements AutoConfigurator {

  private static final String RESOURCES_DIRECTORY = "src/main/resources";
  private static final String PROPERTIES_SUFFIX = ".properties";
  private static final Set<String> ISO_LANGUAGES = Set.of(Locale.getISOLanguages());

  @Override
  public boolean configure(Configuration configuration) {
    var resourcesDir =
        Path.of(configuration.getWorkingDirectory().getAbsolutePath(), RESOURCES_DIRECTORY);

    if (!Files.isDirectory(resourcesDir)) {
      return false;
    }

    if (configuration.getFile() != null || configuration.getFiles() != null) {
      return true;
    }

    var baseLanguage = configuration.getBaseLanguage();
    var bundles = new LinkedHashMap<String, LinkedHashMap<String, File>>();

    try (Stream<Path> stream = Files.walk(resourcesDir)) {
      stream
          .filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().endsWith(PROPERTIES_SUFFIX))
          .forEach(p -> addToBundle(bundles, p, baseLanguage));
    } catch (IOException e) {
      return false;
    }

    var files = new LinkedHashSet<LanguageFileConfiguration>();
    bundles.values().stream()
        .filter(bundle -> bundle.containsKey(baseLanguage))
        .forEach(
            bundle -> {
              files.add(new LanguageFileConfiguration(baseLanguage, bundle.get(baseLanguage)));
              bundle.forEach(
                  (lang, file) -> {
                    if (!lang.equals(baseLanguage)) {
                      files.add(new LanguageFileConfiguration(lang, file));
                    }
                  });
            });

    if (files.isEmpty()) {
      return false;
    }

    configuration.setFiles(files);
    return true;
  }

  private void addToBundle(
      Map<String, LinkedHashMap<String, File>> bundles, Path p, String baseLanguage) {
    var name = p.getFileName().toString();
    var nameWithoutExt = name.substring(0, name.length() - PROPERTIES_SUFFIX.length());
    var parent = p.getParent().toString();

    String bundleKey;
    String language;

    // Case 1: filename IS a language tag (e.g. de.properties, de_CH.properties)
    if (isLanguageTag(nameWithoutExt)) {
      bundleKey = parent;
      language = nameWithoutExt;
    } else {
      // Case 2: filename has a _language suffix (e.g. messages_de.properties)
      var split = splitBaseAndLanguage(nameWithoutExt);
      if (split != null) {
        bundleKey = parent + "/" + split[0];
        language = split[1];
      } else {
        // Case 3: no language info - treat as the base language file
        bundleKey = parent + "/" + nameWithoutExt;
        language = baseLanguage;
      }
    }

    bundles.computeIfAbsent(bundleKey, k -> new LinkedHashMap<>()).put(language, p.toFile());
  }

  private boolean isLanguageTag(String s) {
    var firstSeg = s.contains("_") ? s.substring(0, s.indexOf('_')) : s;
    return ISO_LANGUAGES.contains(firstSeg);
  }

  private String[] splitBaseAndLanguage(String nameWithoutExt) {
    var idx = nameWithoutExt.indexOf('_');
    while (idx != -1) {
      var candidate = nameWithoutExt.substring(idx + 1);
      if (isLanguageTag(candidate)) {
        return new String[] {nameWithoutExt.substring(0, idx), candidate};
      }
      idx = nameWithoutExt.indexOf('_', idx + 1);
    }
    return null;
  }
}
