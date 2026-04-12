package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import java.util.HashMap;
import java.util.Map;

public class FileReaderRegistry {
  private static final Map<String, Map<String, FileReader>> fileReaders = new HashMap<>();

  static {
    registerFileReader(Configuration.DEFAULT, "json", new JsonFileReader());
    registerFileReader(Configuration.DEFAULT, "properties", new PropertiesFileReader());
    registerFileReader(Configuration.DEFAULT, "xml", new XmlFileReader());
  }

  private FileReaderRegistry() {}

  public static void registerFileReader(String name, String extension, FileReader reader) {
    fileReaders.computeIfAbsent(name, k -> new HashMap<>()).put(extension, reader);
  }

  public static FileReader getFileReader(Configuration configuration) {
    var extension = configuration.getFileExtension();
    var fileReader =
        fileReaders
            .computeIfAbsent(configuration.getFileReaderType(), k -> new HashMap<>())
            .get(extension);

    if (fileReader == null) {
      throw new ConfigurationException(
          "No FileReader registered for file reader type: "
              + configuration.getFileReaderType()
              + " and extension: "
              + extension);
    }

    return fileReader;
  }
}
