package dev.uebelacker.babeli.cli.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import picocli.CommandLine;

public abstract class AbstractCommand implements Callable<Integer> {
    @CommandLine.Option(
            names = {"-f", "--file", "--files"},
            arity = "1..*",
            description = "Translation files. If multiple files please prefix every file with language, e.g.: de:resources/values_de.properties.")
    private String[] files;

    @CommandLine.Option(
            names = {"-d", "--directory"},
            description = "Working directory (default: current directory).")
    private File workingDirectory;

    @CommandLine.Option(
            names = {"-b", "--base-language"},
            description = "Base language code (default: en).")
    private String baseLanguage;

    @CommandLine.Option(
            names = {"-a", "--actions"},
            description = "Comma-separated list of actions to perform (default: all).")
    private String actions;

    @CommandLine.Option(
            names = {"-g", "--glossary"},
            description = "Path to glossary file (default: glossary.json).")
    private File glossaryFile;

    @CommandLine.Option(
            names = {"-m", "--model-provider"},
            description = "AI model provider to use.")
    private String modelProvider;

    protected Configuration createConfiguration() {
        var configuration = new Configuration();

        if (files != null && files.length == 1) {
            configuration.setFile(new File(files[0]));
        }

        if (files != null && files.length > 1) {
            configuration.setFiles(Arrays.stream(files).map(f -> {
                var parts = f.split(":", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                            "Invalid file format: " + f + ". Expected format: language:path");
                }
                return new LanguageFileConfiguration(parts[0], new File(parts[1]));
            }).collect(java.util.stream.Collectors.toSet()));
        }

        if (workingDirectory != null) {
            configuration.setWorkingDirectory(workingDirectory);
        }

        if (baseLanguage != null) {
            configuration.setBaseLanguage(baseLanguage);
        }

        if (actions != null) {
            configuration.setActions(Set.of(actions.split(",")));
        }

        if (glossaryFile != null) {
            configuration.setGlossaryFile(glossaryFile);
        }

        if (modelProvider != null) {
            configuration.setModelProvider(modelProvider);
        }

        return configuration;
    }
}
