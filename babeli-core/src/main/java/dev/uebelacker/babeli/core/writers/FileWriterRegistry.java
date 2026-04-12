package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import java.util.HashMap;
import java.util.Map;

public class FileWriterRegistry {
  private static final Map<String, Map<String, FileWriter>> fileWriters = new HashMap<>();

  static {
    registerFileWriter(Configuration.DEFAULT, "json", new JsonFileWriter());
    registerFileWriter(Configuration.DEFAULT, "properties", new PropertiesFileWriter());
    registerFileWriter(Configuration.DEFAULT, "xml", new XmlFileWriter());
  }

  private FileWriterRegistry() {}

  public static void registerFileWriter(String name, String extension, FileWriter writer) {
    fileWriters.computeIfAbsent(name, k -> new HashMap<>()).put(extension, writer);
  }

  public static FileWriter getFileWriter(Configuration configuration) {
    var extension = configuration.getFileExtension();
    var fileWriter =
        fileWriters
            .computeIfAbsent(configuration.getFileWriterType(), k -> new HashMap<>())
            .get(extension);

    if (fileWriter == null) {
      throw new ConfigurationException(
          "No FileWriter registered for file writer type: "
              + configuration.getFileWriterType()
              + " and extension: "
              + extension);
    }

    return fileWriter;
  }
}
