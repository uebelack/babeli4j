package dev.uebelacker.babeli.maven;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractBabeliMojo extends AbstractMojo {

  @Parameter(property = "babeli.skip", defaultValue = "false")
  protected boolean skip;

  @Parameter(property = "babeli.file")
  private File file;

  @Parameter(property = "babeli.charset")
  private String charset;

  @Parameter private Map<String, String> files;

  @Parameter(property = "babeli.workingDirectory", defaultValue = "${project.basedir}")
  private File workingDirectory;

  @Parameter(property = "babeli.baseLanguage", defaultValue = "en")
  private String baseLanguage;

  @Parameter private List<String> actions;

  @Parameter(property = "babeli.modelProvider")
  private String modelProvider;

  @Parameter(property = "babeli.model")
  private String model;

  @Parameter(property = "babeli.apiKey")
  private String apiKey;

  @Parameter(property = "babeli.apiUrl")
  private String apiUrl;

  Configuration createConfiguration() {
    var configuration = new Configuration();
    configuration.setLoggingProvider(new BabeliLoggingProvider(getLog()));
    configuration.setDebug(getLog().isDebugEnabled());
    configuration.setWorkingDirectory(workingDirectory);

    if (file != null) {
      configuration.setFile(file);
    }

    if (files != null && !files.isEmpty()) {
      configuration.setFiles(
          files.entrySet().stream()
              .map(
                  entry ->
                      new LanguageFileConfiguration(
                          entry.getKey(), new File(workingDirectory, entry.getValue())))
              .collect(Collectors.toSet()));
    }

    if (charset != null) {
      configuration.setCharset(charset);
    }

    if (baseLanguage != null) {
      configuration.setBaseLanguage(baseLanguage);
    }

    if (actions != null && !actions.isEmpty()) {
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

    configuration.setSkip(skip);

    return configuration;
  }
}
