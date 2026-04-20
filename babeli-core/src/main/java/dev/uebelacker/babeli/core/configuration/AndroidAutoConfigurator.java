package dev.uebelacker.babeli.core.configuration;

import dev.uebelacker.babeli.core.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

public class AndroidAutoConfigurator implements AutoConfigurator {

  private static final String STRINGS_XML = "app/src/main/res/values/strings.xml";
  private static final String RES_DIRECTORY = "app/src/main/res";

  @Override
  public boolean configure(Configuration configuration) {
    var stringXml =
        Path.of(configuration.getWorkingDirectory().getAbsolutePath(), STRINGS_XML).toFile();

    if (stringXml.exists()) {
      if (configuration.getFile() == null && configuration.getFiles() == null) {
        var files = new LinkedHashSet<LanguageFileConfiguration>();
        files.add(new LanguageFileConfiguration(configuration.getBaseLanguage(), stringXml));
        try (Stream<Path> stream =
            Files.list(
                Path.of(configuration.getWorkingDirectory().getAbsolutePath(), RES_DIRECTORY))) {
          stream
              .filter(Files::isDirectory)
              .filter(p -> p.getFileName().toString().startsWith("values-"))
              .forEach(
                  p -> {
                    var language = p.getFileName().toString().substring("values-".length());
                    var file = p.resolve("strings.xml").toFile();
                    if (file.exists()) {
                      files.add(new LanguageFileConfiguration(language, file));
                    }
                  });
        } catch (IOException e) {
          return false;
        }

        configuration.setFiles(files);
      }

      return true;
    }

    return false;
  }
}
