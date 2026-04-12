package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import java.util.HashMap;
import java.util.Map;

public class FileReaderRegistry {
  private static final Map<String, FileReader> fileReaders = new HashMap<>();

  static {
    registerFileReader("json", new JsonFileReader());
    registerFileReader("properties", new PropertiesFileReader());
    registerFileReader("xml", new XmlFileReader());
  }

  private FileReaderRegistry() {}

  public static void registerFileReader(String extension, FileReader reader) {
    fileReaders.put(extension, reader);
  }

  public static FileReader getFileReader(Configuration configuration) {
    var extension = configuration.getFileExtension();
    var fileReader = fileReaders.get(extension);

    if (fileReader == null) {
      throw new ConfigurationException("No FileReader registered for file extension: " + extension);
    }

    return fileReader;
  }
}
