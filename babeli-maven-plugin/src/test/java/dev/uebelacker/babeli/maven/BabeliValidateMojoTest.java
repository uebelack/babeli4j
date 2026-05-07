package dev.uebelacker.babeli.maven;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@MojoTest
class BabeliValidateMojoTest {

  @Test
  @InjectMojo(goal = "validate", pom = "src/test/resources/valid/plugin-pom.xml")
  @DisplayName("should validate translation files successfully")
  void shouldValidateTranslationFilesSuccessfully(BabeliValidateMojo mojo) {
    assertThatNoException().isThrownBy(mojo::execute);
  }

  @Test
  @InjectMojo(goal = "validate", pom = "src/test/resources/invalid/plugin-pom.xml")
  @DisplayName("should validate translation files unsuccessfully")
  void shouldValidateTranslationFilesUnsuccessfully(BabeliValidateMojo mojo) {
    assertThatThrownBy(mojo::execute).isInstanceOf(MojoFailureException.class);
  }
}
