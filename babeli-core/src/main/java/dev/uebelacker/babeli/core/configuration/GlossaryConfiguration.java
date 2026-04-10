package dev.uebelacker.babeli.core.configuration;

import java.io.File;

public class GlossaryConfiguration {
  private String service = "ai";
  private File file;

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }
}
