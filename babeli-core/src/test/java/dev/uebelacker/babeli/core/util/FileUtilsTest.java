package dev.uebelacker.babeli.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

  @Test
  @DisplayName("should return relative path")
  void shouldReturnRelativePath() {
    var workingDirectory = new File("/test/home");
    var file = new File("/test/home/test/file.txt");

    assertThat(FileUtils.relativePath(workingDirectory, file)).isEqualTo("test/file.txt");
  }
}
