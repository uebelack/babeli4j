package dev.uebelacker.babeli.core.writers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import java.io.File;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileWriterRegistryTest {
  @Test
  @DisplayName("should return right file writer for given configuration")
  void shouldReturnRightFileWriterForGivenConfiguration() {
    var configuration = new Configuration();

    configuration.setFile(new File("test.json"));
    assertThat(FileWriterRegistry.getFileWriter(configuration)).isInstanceOf(JsonFileWriter.class);

    configuration.setFile(new File("test.xml"));
    assertThat(FileWriterRegistry.getFileWriter(configuration)).isInstanceOf(XmlFileWriter.class);

    configuration.setFile(null);
    configuration.setFiles(
        Set.of(new LanguageFileConfiguration("en", new File("test.properties"))));
    assertThat(FileWriterRegistry.getFileWriter(configuration))
        .isInstanceOf(PropertiesFileWriter.class);
  }

  @Test
  @DisplayName("should throw exception if no file writer is registered for given configuration")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionIfNoFileWriterIsRegisteredForGivenConfiguration() {
    var configuration = new Configuration();
    configuration.setFile(new File("test.unknown"));
    assertThatException().isThrownBy(() -> FileWriterRegistry.getFileWriter(configuration));
  }
}
