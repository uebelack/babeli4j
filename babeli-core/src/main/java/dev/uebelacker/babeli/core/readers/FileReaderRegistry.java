package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.exceptions.UnexpectedErrorException;
import java.util.HashMap;
import java.util.Map;

public class FileReaderRegistry {
  private static final Map<String, Class<? extends FileReader>> fileReaders = new HashMap<>();

  static {
    registerFileReader("json", JsonFileReader.class);
    registerFileReader("properties", PropertiesFileReader.class);
    registerFileReader("xml", XmlFileReader.class);
  }

  private FileReaderRegistry() {}

  public static void registerFileReader(String extension, Class<? extends FileReader> readerClass) {
    fileReaders.put(extension, readerClass);
  }

  public static FileReader getFileReader(Configuration configuration) {
    var extension = configuration.getFileExtension();
    var fileReaderClass = fileReaders.get(extension);

    if (fileReaderClass == null) {
      throw new ConfigurationException("No FileReader registered for file extension: " + extension);
    }

    try {
      return fileReaderClass.getConstructor(Configuration.class).newInstance(configuration);
    } catch (Exception e) {
      throw new UnexpectedErrorException(
          "Failed to instantiate file reader: " + fileReaderClass.getSimpleName(), e);
    }
  }
}
