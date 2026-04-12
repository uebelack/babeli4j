package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.readers.FileReaderRegistry;
import dev.uebelacker.babeli.core.writers.FileWriterRegistry;
import java.util.List;

public class Babeli {
  private Babeli() {}

  public static List<Error> execute(Configuration configuration) {
    switch (configuration.getOperation()) {
      case VALIDATE -> {
        return validate(configuration);
      }
      case UPDATE -> update(configuration);
    }

    return List.of();
  }

  public static List<Error> validate(Configuration configuration) {
    configuration.validate();
    var fileReader = FileReaderRegistry.getFileReader(configuration);

    if (configuration.getFile() != null) {
      var translationFile = fileReader.readFile(configuration.getFile());
      return configuration.getActions().stream()
          .map(
              action ->
                  ActionRegistry.createAction(action, configuration).validate(translationFile))
          .flatMap(List::stream)
          .toList();
    }

    if (configuration.getFiles() != null) {
      var translationFiles =
          configuration.getFiles().stream()
              .map(f -> fileReader.readFile(f.getLanguage(), f.getFile()))
              .toList();

      return configuration.getActions().stream()
          .map(
              action ->
                  ActionRegistry.createAction(action, configuration).validate(translationFiles))
          .flatMap(List::stream)
          .toList();
    }

    return List.of();
  }

  public static void update(Configuration configuration) {
    configuration.validate();
    var fileReader = FileReaderRegistry.getFileReader(configuration);
    var fileWriter = FileWriterRegistry.getFileWriter(configuration);

    if (configuration.getFile() != null) {
      var translationFile = fileReader.readFile(configuration.getFile());
      for (var action : configuration.getActions()) {
        translationFile =
            ActionRegistry.createAction(action, configuration).update(translationFile);
      }
      fileWriter.writeFile(translationFile);
    }

    if (configuration.getFiles() != null) {
      var translationFiles =
          configuration.getFiles().stream()
              .map(f -> fileReader.readFile(f.getLanguage(), f.getFile()))
              .toList();

      for (var action : configuration.getActions()) {
        translationFiles =
            ActionRegistry.createAction(action, configuration).update(translationFiles);
      }

      translationFiles.forEach(fileWriter::writeFile);
    }
  }
}
