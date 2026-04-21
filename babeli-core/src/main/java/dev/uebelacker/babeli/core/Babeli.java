package dev.uebelacker.babeli.core;

import java.util.ArrayList;
import java.util.List;

import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.readers.FileReaderRegistry;
import dev.uebelacker.babeli.core.writers.FileWriterRegistry;

public class Babeli {
    private Babeli() {
    }

    public static List<Error> execute(BabeliContext context) {
        switch (context.getConfiguration().getOperation()) {
            case VALIDATE -> {
                return validate(context);
            }
            case UPDATE -> update(context);
        }

        return List.of();
    }

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
        return validate(new BabeliContext(configuration));
    }

    public static List<Error> validate(BabeliContext context) {
        var errors = new ArrayList<Error>();
        context.getConfiguration().autoConfigure().forEach(configuration -> {
            configuration.validate();

            var fileReader = FileReaderRegistry.getFileReader(configuration);

            if (configuration.getFile() != null) {
                var translationFile = fileReader.readFile(configuration.getFile());
                errors.addAll(configuration.getActions().stream()
                        .map(action -> ActionRegistry.createAction(action, context).validate(translationFile))
                        .flatMap(List::stream)
                        .toList());
            }

            if (configuration.getFiles() != null) {
                var translationFiles =
                        configuration.getFiles().stream()
                                .map(f -> fileReader.readFile(f.getLanguage(), f.getFile()))
                                .toList();

                errors.addAll(configuration.getActions().stream()
                        .map(action -> ActionRegistry.createAction(action, context).validate(translationFiles))
                        .flatMap(List::stream)
                        .toList());
            }
        });

        return errors;
    }

    public static void update(Configuration configuration) {
        update(new BabeliContext(configuration));
    }

    public static void update(BabeliContext context) {
        context.getConfiguration().autoConfigure().forEach(configuration -> {
            configuration.validate();
            var fileReader = FileReaderRegistry.getFileReader(configuration);
            var fileWriter = FileWriterRegistry.getFileWriter(configuration);

            if (configuration.getFile() != null) {
                var translationFile = fileReader.readFile(configuration.getFile());
                for (var action : configuration.getActions()) {
                    translationFile = ActionRegistry.createAction(action, context).update(translationFile);
                }
                fileWriter.writeFile(translationFile);
            }

            if (configuration.getFiles() != null) {
                var translationFiles =
                        configuration.getFiles().stream()
                                .map(f -> fileReader.readFile(f.getLanguage(), f.getFile()))
                                .toList();

                for (var action : configuration.getActions()) {
                    translationFiles = ActionRegistry.createAction(action, context).update(translationFiles);
                }

                translationFiles.forEach(fileWriter::writeFile);
            }
        });
    }
}
