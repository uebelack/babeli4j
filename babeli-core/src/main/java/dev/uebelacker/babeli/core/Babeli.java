package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.readers.FileReaderRegistry;
import dev.uebelacker.babeli.core.writers.FileWriterRegistry;
import java.util.ArrayList;
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
    var errors = new ArrayList<Error>();
    configuration
        .autoConfigure()
        .forEach(
            subConfiguration -> {
              subConfiguration.validate();

              var fileReader = FileReaderRegistry.getFileReader(subConfiguration);

              if (subConfiguration.getFile() != null) {
                var translationFile = fileReader.readFile(subConfiguration.getFile());
                errors.addAll(
                    subConfiguration.getActions().stream()
                        .map(
                            action ->
                                ActionRegistry.createAction(action, subConfiguration)
                                    .validate(translationFile))
                        .flatMap(List::stream)
                        .toList());
              }

              if (subConfiguration.getFiles() != null) {
                var translationFiles =
                    subConfiguration.getFiles().stream()
                        .map(f -> fileReader.readFile(f.getLanguage(), f.getFile()))
                        .toList();

                errors.addAll(
                    subConfiguration.getActions().stream()
                        .map(
                            action ->
                                ActionRegistry.createAction(action, subConfiguration)
                                    .validate(translationFiles))
                        .flatMap(List::stream)
                        .toList());
              }
            });

    return errors;
  }

  public static void update(Configuration configuration) {
    configuration
        .autoConfigure()
        .forEach(
            subConfiguration -> {
              subConfiguration.validate();
              var fileReader = FileReaderRegistry.getFileReader(subConfiguration);
              var fileWriter = FileWriterRegistry.getFileWriter(subConfiguration);

              if (subConfiguration.getFile() != null) {
                var translationFile = fileReader.readFile(subConfiguration.getFile());
                for (var action : subConfiguration.getActions()) {
                  translationFile =
                      ActionRegistry.createAction(action, subConfiguration).update(translationFile);
                }
                fileWriter.writeFile(translationFile);
              }

              if (subConfiguration.getFiles() != null) {
                var translationFiles =
                    subConfiguration.getFiles().stream()
                        .map(f -> fileReader.readFile(f.getLanguage(), f.getFile()))
                        .toList();

                for (var action : subConfiguration.getActions()) {
                  translationFiles =
                      ActionRegistry.createAction(action, subConfiguration)
                          .update(translationFiles);
                }

                translationFiles.forEach(fileWriter::writeFile);
              }
            });
  }
}
