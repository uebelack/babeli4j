package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.configuration.AndroidAutoConfigurator;
import dev.uebelacker.babeli.core.configuration.AutoConfigurator;
import dev.uebelacker.babeli.core.configuration.JavaPropertiesAutoConfigurator;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
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
  private File glossaryFile;
  private String modelProvider;

  public Configuration() {
    this.actions = ActionRegistry.getActionNames();
    this.operation = System.getenv("CI") != null ? Operation.VALIDATE : Operation.UPDATE;

    this.workingDirectory = new File(EnvUtils.get("BABELI_WORKING_DIRECTORY", "."));
    this.baseLanguage = EnvUtils.get("BABELI_BASE_LANGUAGE", "en");
    this.glossaryFile = new File(EnvUtils.get("BABELI_GLOSSARY_FILE", "glossary.json"));
  }

  public File getWorkingDirectory() {
    return workingDirectory;
  }

  public void setWorkingDirectory(File workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public String getBaseLanguage() {
    return baseLanguage;
  }

  public void setBaseLanguage(String baseLanguage) {
    this.baseLanguage = baseLanguage;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public Set<LanguageFileConfiguration> getFiles() {
    return files;
  }

  public void setFiles(Set<LanguageFileConfiguration> files) {
    this.files = files;
  }

  public Set<String> getActions() {
    return actions;
  }

  public void setActions(Set<String> actions) {
    this.actions = actions;
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

  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  public String getModelProvider() {
    return modelProvider;
  }

  public void setModelProvider(String modelProvider) {
    this.modelProvider = modelProvider;
  }

  public void autoConfigure() {
    for (var autoConfigurator : autoConfigurators) {
      if (autoConfigurator.configure(this)) {
        return;
      }
    }
  }

  public void validate() throws ConfigurationException {
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
}
