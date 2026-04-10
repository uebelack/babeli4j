package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.actions.ActionRegistry;
import dev.uebelacker.babeli.core.configuration.GlossaryConfiguration;
import java.io.File;
import java.util.Set;

public class Configuration {
  private String identifier = "default";
  private String baseLanguage = "en";
  private File file;
  private Set<File> files;
  private Set<String> actions;
  private String translationService = "ai";
  private GlossaryConfiguration glossary;

  public Configuration() {
    this.actions = ActionRegistry.getActionNames();
    this.glossary = new GlossaryConfiguration();
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

  public Set<File> getFiles() {
    return files;
  }

  public void setFiles(Set<File> files) {
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
}
