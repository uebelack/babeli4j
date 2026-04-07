package dev.uebelacker.babeli.core.services;

import dev.uebelacker.babeli.core.Configuration;
import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
  private static final Map<String, Class<TranslationService>> translationServiceClasses =
      new HashMap<>();
  private static final Map<String, Class<GlossaryService>> glossaryServiceClasses = new HashMap<>();

  private static final Map<String, TranslationService> translationServices = new HashMap<>();
  private static final Map<String, GlossaryService> glossaryServices = new HashMap<>();

  private static <T> T createServiceInstance(Configuration configuration, Class<T> serviceClass) {
    try {

      try {
        return serviceClass.getConstructor(Configuration.class).newInstance(configuration);
      } catch (NoSuchMethodException e) {
        return serviceClass.getConstructor().newInstance();
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to create service instance for " + serviceClass.getName(), e);
    }
  }

  public static TranslationService getTranslationService(Configuration configuration) {
    return translationServices.computeIfAbsent(
        configuration.identifier(),
        k ->
            createServiceInstance(
                configuration, translationServiceClasses.get(configuration.translationService())));
  }

  public static GlossaryService getGlossaryService(Configuration configuration) {
    return glossaryServices.computeIfAbsent(
        configuration.identifier(),
        k ->
            createServiceInstance(
                configuration, glossaryServiceClasses.get(configuration.glossary().service())));
  }

  public static void registerTranslationService(
      String name, Class<TranslationService> translationServiceClass) {
    translationServiceClasses.put(name, translationServiceClass);
  }

  public static void registerGlossaryService(
      String name, Class<GlossaryService> glossaryServiceClass) {
    glossaryServiceClasses.put(name, glossaryServiceClass);
  }
}
