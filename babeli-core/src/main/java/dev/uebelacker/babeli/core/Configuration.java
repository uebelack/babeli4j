package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.configuration.GlossaryConfiguration;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import java.io.File;
import java.util.Set;

public class Configuration {
  public static final String DEFAULT = "default";

  private String identifier = DEFAULT;
  private Operation operation;
  private String baseLanguage = "en";
  private String fileReader = DEFAULT;
  private String fileWriter = DEFAULT;
  private File file;
  private Set<LanguageFileConfiguration> files;
  private Set<String> actions;
  private String translationService = "ai";
  private GlossaryConfiguration glossary;

  public Configuration() {
    this.actions = ActionRegistry.getActionNames();
    this.glossary = new GlossaryConfiguration();
    this.operation = System.getenv("CI") != null ? Operation.VALIDATE : Operation.UPDATE;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
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

  public String getTranslationService() {
    return translationService;
  }

  public void setTranslationService(String translationService) {
    this.translationService = translationService;
  }

  public GlossaryConfiguration getGlossary() {
    return glossary;
  }

  public void setGlossary(GlossaryConfiguration glossary) {
    this.glossary = glossary;
  }

  public String getFileReaderType() {
    return fileReader;
  }

  public void setFileReader(String fileReader) {
    this.fileReader = fileReader;
  }

  public String getFileWriterType() {
    return fileWriter;
  }

  public void setFileWriter(String fileWriter) {
    this.fileWriter = fileWriter;
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
