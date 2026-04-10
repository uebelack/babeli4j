package dev.uebelacker.babeli.core.services;

import dev.uebelacker.babeli.core.Configuration;
import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
  private static final Map<String, Class<? extends TranslationService>> translationServiceClasses =
      new HashMap<>();
  private static final Map<String, Class<? extends GlossaryService>> glossaryServiceClasses =
      new HashMap<>();

  private static final Map<String, TranslationService> translationServices = new HashMap<>();
  private static final Map<String, GlossaryService> glossaryServices = new HashMap<>();

  private ServiceRegistry() {}

  @SuppressWarnings({"java:S1141", "java:S112"})
  private static <T> T createServiceInstance(
      Configuration configuration,
      Class<T> serviceClass,
      String serviceName,
      Class<?> serviceInterface) {
    try {
      try {
        return serviceClass.getConstructor(Configuration.class).newInstance(configuration);
      } catch (NoSuchMethodException e) {
        return serviceClass.getConstructor().newInstance();
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to create " + serviceInterface.getSimpleName() + " instance for " + serviceName,
          e);
    }
  }

  public static TranslationService getTranslationService(Configuration configuration) {
    var name = configuration.getTranslationService();
    return translationServices.computeIfAbsent(
        configuration.getIdentifier(),
        k ->
            createServiceInstance(
                configuration,
                translationServiceClasses.get(name),
                name,
                TranslationService.class));
  }

  public static GlossaryService getGlossaryService(Configuration configuration) {
    var name = configuration.getGlossary().getService();
    return glossaryServices.computeIfAbsent(
        configuration.getIdentifier(),
        k ->
            createServiceInstance(
                configuration, glossaryServiceClasses.get(name), name, GlossaryService.class));
  }

  public static void registerTranslationService(
      String name, Class<? extends TranslationService> translationServiceClass) {
    translationServiceClasses.put(name, translationServiceClass);
  }

  public static void registerGlossaryService(
      String name, Class<? extends GlossaryService> glossaryServiceClass) {
    glossaryServiceClasses.put(name, glossaryServiceClass);
  }

  public static void clearCache() {
    translationServices.clear();
    glossaryServices.clear();
  }
}
