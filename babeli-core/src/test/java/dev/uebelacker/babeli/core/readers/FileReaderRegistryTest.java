package dev.uebelacker.babeli.core.readers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.configuration.LanguageFileConfiguration;
import java.io.File;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileReaderRegistryTest {
  @Test
  @DisplayName("should return right file reader for given configuration")
  void shouldReturnRightFileReaderForGivenConfiguration() {
    var configuration = new Configuration();

    configuration.setFile(new File("test.json"));
    assertThat(FileReaderRegistry.getFileReader(configuration)).isInstanceOf(JsonFileReader.class);

    configuration.setFile(new File("test.xml"));
    assertThat(FileReaderRegistry.getFileReader(configuration)).isInstanceOf(XmlFileReader.class);

    configuration.setFile(null);
    configuration.setFiles(
        Set.of(new LanguageFileConfiguration("en", new File("test.properties"))));
    assertThat(FileReaderRegistry.getFileReader(configuration))
        .isInstanceOf(PropertiesFileReader.class);
  }

  @Test
  @DisplayName("should throw exception if no file reader is registered for given configuration")
  @SuppressWarnings("java:S5778")
  void shouldThrowExceptionIfNoFileReaderIsRegisteredForGivenConfiguration() {
    var configuration = new Configuration();
    configuration.setFile(new File("test.unknown"));
    assertThatException().isThrownBy(() -> FileReaderRegistry.getFileReader(configuration));
  }
}
