package dev.uebelacker.babeli.core.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;

public class AndroidAutoConfigurator implements AutoConfigurator {

    private static final String STRINGS_XML = "app/src/main/res/values/strings.xml";
    private static final String RES_DIRECTORY = "app/src/main/res";

    @Override
    public boolean matches(Configuration configuration) {
        return getStringXml(configuration).exists();
    }

    @Override
    public List<Configuration> configure(Configuration configuration) {
        var files = new LinkedHashSet<LanguageFileConfiguration>();
        files.add(new LanguageFileConfiguration(configuration.getBaseLanguage(), getStringXml(configuration)));
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
            throw new ConfigurationException("Unable to read Android resource directory", e);
        }

        configuration.setFiles(files);

        return List.of(configuration);
    }

    private File getStringXml(Configuration configuration) {
        return Path.of(configuration.getWorkingDirectory().getAbsolutePath(), STRINGS_XML).toFile();
    }
}
