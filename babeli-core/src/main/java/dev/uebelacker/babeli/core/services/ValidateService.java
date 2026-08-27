package dev.uebelacker.babeli.core.services;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.readers.FileReaderRegistry;
import java.util.ArrayList;
import java.util.List;

public class ValidateService {

  private final Configuration configuration;

  public ValidateService(Configuration configuration) {
    this.configuration = configuration;
  }

  public List<Error> validate() {
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
}
