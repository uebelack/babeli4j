package dev.uebelacker.babeli.maven;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import dev.uebelacker.babeli.core.util.EnvUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@MojoTest
class BabeliAddMojoTest {

  private static final File TEST_DIRECTORY = new File("target/babeli-add-test-project");

  @AfterAll
  static void tearDown() {
    EnvUtils.reset();
  }

  @BeforeEach
  void setUp() throws IOException {
    EnvUtils.reset();
    EnvUtils.set("BABELI_MODEL_PROVIDER", "dev.uebelacker.babeli.maven.TestChatModelProvider");
    FileUtils.deleteDirectory(TEST_DIRECTORY);
    FileUtils.forceMkdir(TEST_DIRECTORY);
    FileUtils.copyFileToDirectory(
        new File("src/test/resources/add/messages_en.properties"), TEST_DIRECTORY);
    FileUtils.copyFileToDirectory(
        new File("src/test/resources/add/messages_de.properties"), TEST_DIRECTORY);
  }

  @Test
  @InjectMojo(goal = "add", pom = "src/test/resources/add/plugin-pom.xml")
  @DisplayName("should add the key with the given translations")
  void shouldAddKeyWithGivenTranslations(BabeliAddMojo mojo) throws IOException {
    var log = mock(Log.class);
    mojo.setLog(log);

    assertThatNoException().isThrownBy(mojo::execute);

    assertThat(translationFile("en")).contains("common.button.no=No");
    assertThat(translationFile("de")).contains("common.button.no=Nein");
    verify(log).info("Added translation key 'common.button.no'.");
  }

  @Test
  @InjectMojo(goal = "add", pom = "src/test/resources/add/no-key-pom.xml")
  @DisplayName("should fail if no key was given")
  void shouldFailIfNoKeyGiven(BabeliAddMojo mojo) {
    mojo.setLog(mock(Log.class));

    assertThatExceptionOfType(MojoFailureException.class)
        .isThrownBy(mojo::execute)
        .withMessage("No translation key given. Use -Dbabeli.key=<key>.");
  }

  @Test
  @InjectMojo(goal = "add", pom = "src/test/resources/add/no-translations-pom.xml")
  @DisplayName("should fail if no translation was given")
  void shouldFailIfNoTranslationGiven(BabeliAddMojo mojo) {
    mojo.setLog(mock(Log.class));

    assertThatExceptionOfType(MojoFailureException.class)
        .isThrownBy(mojo::execute)
        .withMessage("No translation given. Use -Dbabeli.translations=<language>=<value>.");
  }

  @Test
  @InjectMojo(goal = "add", pom = "src/test/resources/add/invalid-translation-pom.xml")
  @DisplayName("should fail if a translation has no language prefix")
  void shouldFailIfTranslationHasNoLanguagePrefix(BabeliAddMojo mojo) {
    mojo.setLog(mock(Log.class));

    assertThatExceptionOfType(MojoFailureException.class)
        .isThrownBy(mojo::execute)
        .withMessage("Invalid translation 'oops'. Expected format: <language>=<value>.");
  }

  private String translationFile(String language) throws IOException {
    return Files.readString(
        new File(TEST_DIRECTORY, "messages_%s.properties".formatted(language)).toPath());
  }
}
