package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.configuration.GlossaryConfiguration;

public class ConfigurationFactory {
  public static Configuration createConfigurationWithTranslationService(String translationService) {
    return new Configuration(
        "test", "en", translationService, new GlossaryConfiguration("test", null));
  }

  public static Configuration createConfiguration() {
    return new Configuration("test", "en", "test", new GlossaryConfiguration("test", null));
  }
}
