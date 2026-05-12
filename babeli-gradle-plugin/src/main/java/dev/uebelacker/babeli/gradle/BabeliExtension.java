package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.gradle.api.Project;

public class BabeliExtension {
  private final Map<String, File> files = new LinkedHashMap<>();
  private File file;
  private String charset;
  private File workingDirectory;
  private String baseLanguage;
  private List<String> actions;
  private String modelProvider;
  private String model;
  private String apiKey;
  private String apiUrl;

  public BabeliExtension(Project project) {}

  public File getMultiLanguageFile() {
    return file;
  }

  public void setMultiLanguageFile(File file) {
    this.file = file;
  }

  public Map<String, File> getFiles() {
    return files;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public void translationFile(String language, File file) {
    files.put(language, file);
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

  public List<String> getActions() {
    return actions;
  }

  public void setActions(List<String> actions) {
    this.actions = actions;
  }

  public String getModelProvider() {
    return modelProvider;
  }

  public void setModelProvider(String modelProvider) {
    this.modelProvider = modelProvider;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  public Configuration toConfiguration(Project project) {
    var configuration = new Configuration();
    configuration.setLoggingProvider(new BabeliLoggingProvider(project.getLogger()));
    configuration.setDebug(project.getLogger().isDebugEnabled());
    configuration.setWorkingDirectory(project.getProjectDir());

    if (file != null) {
      configuration.setFile(file);
    }
    if (!files.isEmpty()) {
      configuration.setFiles(
          files.keySet().stream()
              .map(language -> new LanguageFileConfiguration(language, files.get(language)))
              .collect(Collectors.toSet()));
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
      configuration.setActions(new HashSet<>(actions));
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

    return configuration;
  }
}
