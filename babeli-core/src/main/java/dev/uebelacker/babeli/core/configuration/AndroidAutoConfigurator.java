package dev.uebelacker.babeli.core.configuration;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

public class AndroidAutoConfigurator implements AutoConfigurator {

  private static final String STRINGS_XML = "src/main/res/values/strings.xml";
  private static final String RES_DIRECTORY = "src/main/res";

  @Override
  public boolean matches(Configuration configuration) {
    var stringXml = getStringXml(configuration);
    var logger = new Logger(configuration);
    if (stringXml.exists()) {
      logger.debug("Found %s. This looks like an Android project.".formatted(stringXml));
      return true;
    }

    logger.debug(
        "Did not find %s. This does not look like an Android project.".formatted(stringXml));
    return false;
  }

  @Override
  public List<Configuration> configure(Configuration configuration) {
    var logger = new Logger(configuration);

    logger.debug("AndroidAutoConfigurator matched. Configuring for Android project ...");

    var files = new LinkedHashSet<LanguageFileConfiguration>();
    files.add(
        new LanguageFileConfiguration(
            configuration.getBaseLanguage(), getStringXml(configuration)));
    try (Stream<Path> stream =
        Files.list(Path.of(configuration.getWorkingDirectory().getAbsolutePath(), RES_DIRECTORY))) {
      stream
          .filter(Files::isDirectory)
          .filter(p -> p.getFileName().toString().startsWith("values-"))
          .forEach(
              p -> {
                var language = p.getFileName().toString().substring("values-".length());
                var file = p.resolve("strings.xml").toFile();
                if (file.exists()) {
                  logger.debug(
                      "Found language '%s' in file %s".formatted(language, file.getAbsolutePath()));
                  files.add(new LanguageFileConfiguration(language, file));
                }
              });
    } catch (IOException e) {
      throw new ConfigurationException("Unable to read Android resource directory", e);
    }

    configuration.setName("strings");
    configuration.setFiles(files);

    return List.of(configuration);
  }

  private File getStringXml(Configuration configuration) {
    return Path.of(configuration.getWorkingDirectory().getAbsolutePath(), STRINGS_XML).toFile();
  }
}
