package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.configuration.AndroidAutoConfigurator;
import dev.uebelacker.babeli.core.configuration.AutoConfigurator;
import dev.uebelacker.babeli.core.configuration.JavaPropertiesAutoConfigurator;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.logging.DefaultLoggingProvider;
import dev.uebelacker.babeli.core.logging.Logger;
import dev.uebelacker.babeli.core.logging.LoggingProvider;
import dev.uebelacker.babeli.core.util.EnvUtils;
import java.io.File;
import java.util.List;
import java.util.Set;

public class Configuration {
  private static final List<AutoConfigurator> autoConfigurators =
      List.of(new AndroidAutoConfigurator(), new JavaPropertiesAutoConfigurator());

  private File workingDirectory;
  private Operation operation;
  private String baseLanguage;
  private File file;
  private Set<LanguageFileConfiguration> files;
  private Set<String> actions;
  private String modelProvider;
  private LoggingProvider loggingProvider = new DefaultLoggingProvider();
  private String model;
  private String apiKey;
  private String apiUrl;
  private boolean debug;

  public Configuration() {
    this.actions = ActionRegistry.getActionNames();
    this.operation = System.getenv("CI") != null ? Operation.VALIDATE : Operation.UPDATE;

    this.workingDirectory = new File(EnvUtils.get("BABELI_WORKING_DIRECTORY", "."));
    this.baseLanguage = EnvUtils.get("BABELI_BASE_LANGUAGE", "en");
  }

  public File getWorkingDirectory() {
    return workingDirectory;
  }

  public Configuration setWorkingDirectory(File workingDirectory) {
    this.workingDirectory = workingDirectory;
    return this;
  }

  public String getBaseLanguage() {
    return baseLanguage;
  }

  public Configuration setBaseLanguage(String baseLanguage) {
    this.baseLanguage = baseLanguage;
    return this;
  }

  public File getFile() {
    return file;
  }

  public Configuration setFile(File file) {
    this.file = file;
    return this;
  }

  public Set<LanguageFileConfiguration> getFiles() {
    return files;
  }

  public Configuration setFiles(Set<LanguageFileConfiguration> files) {
    this.files = files;
    return this;
  }

  public Set<String> getActions() {
    return actions;
  }

  public Configuration setActions(Set<String> actions) {
    this.actions = actions;
    return this;
  }

  public String getFileExtension() {
    if (file != null) {
      return file.getName().substring(file.getName().lastIndexOf('.') + 1);
    }

    return files.stream()
        .findFirst()
        .map(LanguageFileConfiguration::getFile)
        .map(File::getName)
        .map(name -> name.substring(name.lastIndexOf('.') + 1))
        .orElseThrow();
  }

  public Operation getOperation() {
    return operation;
  }

  public Configuration setOperation(Operation operation) {
    this.operation = operation;
    return this;
  }

  public String getModelProvider() {
    return modelProvider;
  }

  public Configuration setModelProvider(String modelProvider) {
    this.modelProvider = modelProvider;
    return this;
  }

  public String getModel() {
    return model;
  }

  public Configuration setModel(String model) {
    this.model = model;
    return this;
  }

  public String getApiKey() {
    return apiKey;
  }

  public Configuration setApiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public Configuration setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
    return this;
  }

  public LoggingProvider getLoggingProvider() {
    return loggingProvider;
  }

  public Configuration setLoggingProvider(LoggingProvider loggingProvider) {
    this.loggingProvider = loggingProvider;
    return this;
  }

  public boolean isDebug() {
    return debug;
  }

  public Configuration setDebug(boolean debug) {
    this.debug = debug;
    return this;
  }

  public List<Configuration> autoConfigure() {
    if (file == null && (files == null || files.isEmpty())) {
      for (var autoConfigurator : autoConfigurators) {
        if (autoConfigurator.matches(this)) {
          return autoConfigurator.configure(this);
        }
      }
    }

    return List.of(this);
  }

  public void validate() throws ConfigurationException {

    if (isDebug()) {
      var logger = new Logger(this);
      logger.debug("Configuration: ");

      logger.debug("Working directory: " + workingDirectory.getAbsolutePath());

      logger.debug("Base language: " + baseLanguage);

      if (file != null) {
        logger.debug("File: " + file);
      }

      if (files != null) {
        logger.debug("File(s): " + files);
      }

      if (actions != null) {
        logger.debug("Action(s): " + actions);
      }

      if (modelProvider != null) {
        logger.debug("Model: " + modelProvider);
      }

      if (model != null) {
        logger.debug("Model: " + model);
      }

      if (apiKey != null) {
        logger.debug("API key: " + apiKey);
      }

      if (apiUrl != null) {
        logger.debug("API URL: " + apiUrl);
      }
    }

    if (file != null && (files != null && !files.isEmpty())) {
      throw new ConfigurationException(
          "Both 'file' and 'files' are specified in the configuration. Please specify only one of them.");
    }

    if (file == null && (files == null || files.isEmpty())) {
      throw new ConfigurationException(
          "No files specified in the configuration. Please specify either 'file' or 'files'.");
    }

    if (files != null
        && files.stream()
                .map(LanguageFileConfiguration::getFile)
                .map(File::getName)
                .map(name -> name.substring(name.lastIndexOf('.') + 1))
                .distinct()
                .count()
            > 1) {
      throw new ConfigurationException(
          "All files in 'files' must have the same extension. Please ensure all files have the same extension.");
    }
  }

  public Configuration copy() {
    return new Configuration()
        .setDebug(debug)
        .setLoggingProvider(loggingProvider)
        .setActions(actions)
        .setApiKey(apiKey)
        .setApiUrl(apiUrl)
        .setBaseLanguage(baseLanguage)
        .setFile(file)
        .setFiles(files)
        .setModel(model)
        .setModelProvider(modelProvider)
        .setModelProvider(modelProvider)
        .setOperation(operation)
        .setWorkingDirectory(workingDirectory);
  }
}
