package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import java.util.HashMap;
import java.util.Map;

public class FileWriterRegistry {
  private static final Map<String, FileWriter> fileWriters = new HashMap<>();

  static {
    registerFileWriter("json", new JsonFileWriter());
    registerFileWriter("properties", new PropertiesFileWriter());
    registerFileWriter("xml", new XmlFileWriter());
  }

  private FileWriterRegistry() {}

  public static void registerFileWriter(String extension, FileWriter writer) {
    fileWriters.put(extension, writer);
  }

  public static FileWriter getFileWriter(Configuration configuration) {
    var extension = configuration.getFileExtension();
    var fileWriter = fileWriters.get(extension);

    if (fileWriter == null) {
      throw new ConfigurationException("No FileWriter registered for file extension: " + extension);
    }

    return fileWriter;
  }
}
