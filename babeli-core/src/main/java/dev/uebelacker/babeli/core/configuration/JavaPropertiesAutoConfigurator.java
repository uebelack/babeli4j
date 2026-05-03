package dev.uebelacker.babeli.core.configuration;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class JavaPropertiesAutoConfigurator implements AutoConfigurator {

  private static final String RESOURCES_DIRECTORY = "src/main/resources";
  private static final String PROPERTIES_SUFFIX = ".properties";

  private Map<String, Set<LanguageFileConfiguration>> bundles;

  @Override
  public boolean matches(Configuration configuration) {
    var logger = new Logger(configuration);
    if (!Path.of(configuration.getWorkingDirectory().getAbsolutePath(), RESOURCES_DIRECTORY)
        .toFile()
        .exists()) {
      logger.debug(
          "Doesn't look like a java project, resources directory does not exist: "
              + configuration.getWorkingDirectory().getAbsolutePath());
      return false;
    }
    bundles = findResourceBundles(configuration.getWorkingDirectory());

    if (bundles.isEmpty()) {
      logger.debug("Looks like there are no bundles in the working directory");
    } else {
      logger.debug(
          "Found this bundles in the working directory: " + String.join(", ", bundles.keySet()));
    }

    return !bundles.isEmpty();
  }

  @Override
  public List<Configuration> configure(Configuration configuration) {
    return bundles.keySet().stream()
        .map(
            prefix -> {
              var languageFileConfigurations = bundles.get(prefix);
              return configuration.copy().setFiles(languageFileConfigurations);
            })
        .toList();
  }

  private Map<String, Set<LanguageFileConfiguration>> findResourceBundles(File baseDirectory) {
    var propertiesFiles = findPropertiesFiles(baseDirectory);
    var tmpBundles = new LinkedHashMap<String, Set<LanguageFileConfiguration>>();

    propertiesFiles.forEach(
        p -> {
          // split by last _
          var fileName = p.getFileName().toString();
          var fileNameWithoutExtension =
              fileName.substring(0, fileName.length() - PROPERTIES_SUFFIX.length());
          var prefix =
              fileNameWithoutExtension.substring(0, fileNameWithoutExtension.lastIndexOf('_'));
          var locale =
              fileNameWithoutExtension.substring(fileNameWithoutExtension.lastIndexOf('_') + 1);
          tmpBundles
              .computeIfAbsent(prefix, k -> new LinkedHashSet<>())
              .add(new LanguageFileConfiguration(locale, p.toFile()));
        });

    return tmpBundles;
  }

  private List<Path> findPropertiesFiles(File baseDirectory) {
    try (Stream<Path> stream =
        Files.walk(Path.of(baseDirectory.getAbsolutePath(), RESOURCES_DIRECTORY))) {
      return stream
          .filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().endsWith(PROPERTIES_SUFFIX))
          .filter(p -> p.getFileName().toString().contains("_"))
          .toList();

    } catch (IOException e) {
      throw new ConfigurationException("Unable to read resource directory", e);
    }
  }
}
