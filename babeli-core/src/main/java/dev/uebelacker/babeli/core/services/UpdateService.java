package dev.uebelacker.babeli.core.services;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.readers.FileReaderRegistry;
import dev.uebelacker.babeli.core.writers.FileWriterRegistry;

public class UpdateService {

    private final Configuration configuration;

    public UpdateService(Configuration configuration) {
        this.configuration = configuration;
    }

    public void update() {
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
