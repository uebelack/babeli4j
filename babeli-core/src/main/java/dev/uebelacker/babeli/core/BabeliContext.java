package dev.uebelacker.babeli.core;

import dev.uebelacker.babeli.core.services.GlossaryService;
import dev.uebelacker.babeli.core.services.TranslationService;

public class BabeliContext {
  private Configuration configuration;
  private TranslationService translationService;
  private GlossaryService glossaryService;

  public BabeliContext(Configuration configuration) {
    this(configuration, new TranslationService(configuration), new GlossaryService(configuration));
  }

  public BabeliContext(
      Configuration configuration,
      TranslationService translationService,
      GlossaryService glossaryService) {
    this.configuration = configuration;
    this.translationService = translationService;
    this.glossaryService = glossaryService;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public TranslationService getTranslationService() {
    return translationService;
  }

  public GlossaryService getGlossaryService() {
    return glossaryService;
  }
}
