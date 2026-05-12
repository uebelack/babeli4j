package dev.uebelacker.babeli.cli.commands;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;
import picocli.CommandLine;

public abstract class AbstractCommand implements Callable<Integer> {
  @CommandLine.Option(
      names = {"-f", "--file", "--files"},
      arity = "1..*",
      description =
          "Translation files. If multiple files please prefix every file with language, e.g.: de:resources/values_de.properties.")
  private String[] files;

  @CommandLine.Option(
      names = {"-c", "--charset"},
      description = "Character set to use when reading and writing files (default: UTF-8).")
  private String charset;

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
      names = {"-p", "--model-provider"},
      description = "AI model provider to use.")
  private String modelProvider;

  @CommandLine.Option(
      names = {"-m", "--model"},
      description = "AI model to use.")
  private String model;

  @CommandLine.Option(
      names = {"-k", "--api-key"},
      description = "Api-Key for the model provider.")
  private String apiKey;

  @CommandLine.Option(
      names = {"-u", "--url"},
      description = "Api-Url to use for the model provider.")
  private String apiUrl;

  @CommandLine.Option(
      names = {"-v", "--verbose"},
      description = "Enable verbose output for debugging purposes.")
  private boolean debug;

  protected Configuration createConfiguration() {
    var configuration = new Configuration();

    if (files != null && files.length == 1) {
      configuration.setFile(new File(files[0]));
    }

    if (files != null && files.length > 1) {
      configuration.setFiles(
          Arrays.stream(files)
              .map(
                  f -> {
                    var parts = f.split(":", 2);
                    if (parts.length != 2) {
                      throw new IllegalArgumentException(
                          "Invalid file format: " + f + ". Expected format: language:path");
                    }
                    return new LanguageFileConfiguration(parts[0], new File(parts[1]));
                  })
              .collect(java.util.stream.Collectors.toSet()));
    }

    if (charset != null) {
      configuration.setCharset(charset);
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

    if (modelProvider != null) {
      configuration.setModelProvider(modelProvider);
    }

    if (model != null) {
      configuration.setModel(model);
    }

    if (apiKey != null) {
      configuration.setApiKey(apiKey);
    }

    if (apiUrl != null) {
      configuration.setApiUrl(apiUrl);
    }

    configuration.setDebug(debug);

    return configuration;
  }
}
