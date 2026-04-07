package dev.uebelacker.babeli.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import dev.uebelacker.babeli.core.ConfigurationFactory;
import dev.uebelacker.babeli.core.mocks.GlossaryServiceMock;
import dev.uebelacker.babeli.core.mocks.TranslationServiceMock;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServiceRegistryTest {

  @BeforeEach
  void setUp() {
    ServiceRegistry.clearCache();
    ServiceRegistry.registerGlossaryService("test", GlossaryServiceMock.class);
    ServiceRegistry.registerTranslationService("test", TranslationServiceMock.class);
  }

  @Test
  @DisplayName("should return translation service")
  void shouldReturnTranslationService() {
    var service = ServiceRegistry.getTranslationService(ConfigurationFactory.createConfiguration());
    assertThat(service).isNotNull();

    service.translate("test", "en", "de");
    verify(TranslationServiceMock.getMock()).translate("test", "en", "de", null);
  }

  @Test
  @DisplayName("should return glossary service")
  void shouldReturnGlossaryService() {
    var service = ServiceRegistry.getGlossaryService(ConfigurationFactory.createConfiguration());
    assertThat(service).isNotNull();
    service.findRelevantEntries("test", "en", 100);
    service.updateWith("test", Map.of("en", "test"));

    verify(GlossaryServiceMock.getMock()).findRelevantEntries("test", "en", 100);
    verify(GlossaryServiceMock.getMock()).updateWith("test", Map.of("en", "test"));
  }

  @Test
  @DisplayName("should throw exception if translation service class is not registered")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionIfTranslationServiceClassIsNotRegistered() {
    assertThatThrownBy(
            () ->
                ServiceRegistry.getTranslationService(
                    ConfigurationFactory.createConfigurationWithTranslationService("unknown")))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to create TranslationService instance for unknown");
  }
}
