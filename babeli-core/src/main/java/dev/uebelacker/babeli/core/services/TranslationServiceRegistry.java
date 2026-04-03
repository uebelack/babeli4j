package dev.uebelacker.babeli.core.services;

import java.util.HashMap;
import java.util.Map;

public class TranslationServiceRegistry {
  private static final Map<String, TranslationService> translationServices = new HashMap<>();

  public static TranslationService getTranslationService() {
    return null;
  }

  public static void registerTranslationService(TranslationService translationService) {}
}
