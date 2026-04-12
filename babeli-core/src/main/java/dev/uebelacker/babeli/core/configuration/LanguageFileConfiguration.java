package dev.uebelacker.babeli.core.configuration;

import java.io.File;

public class LanguageFileConfiguration {
  private String language;
  private File file;

  public LanguageFileConfiguration(String language, File file) {
    this.language = language;
    this.file = file;
  }

  public String getLanguage() {
    return language;
  }

  public File getFile() {
    return file;
  }
}
