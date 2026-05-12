package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.exceptions.UnexpectedErrorException;
import java.util.HashMap;
import java.util.Map;

public class FileWriterRegistry {
  private static final Map<String, Class<? extends FileWriter>> fileWriters = new HashMap<>();

  static {
    registerFileWriter("json", JsonFileWriter.class);
    registerFileWriter("properties", PropertiesFileWriter.class);
    registerFileWriter("xml", XmlFileWriter.class);
  }

  private FileWriterRegistry() {}

  public static void registerFileWriter(String extension, Class<? extends FileWriter> writer) {
    fileWriters.put(extension, writer);
  }

  public static FileWriter getFileWriter(Configuration configuration) {
    var extension = configuration.getFileExtension();
    var fileWriterClass = fileWriters.get(extension);

    if (fileWriterClass == null) {
      throw new ConfigurationException("No FileWriter registered for file extension: " + extension);
    }

    try {
      return fileWriterClass.getConstructor(Configuration.class).newInstance(configuration);
    } catch (Exception e) {
      throw new UnexpectedErrorException(
          "Failed to instantiate file writer: " + fileWriterClass.getSimpleName(), e);
    }
  }
}
