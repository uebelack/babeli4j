package dev.uebelacker.babeli.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BabeliPluginFunctionalTest {

  @TempDir File projectDir;

  @BeforeEach
  void setUp() throws IOException {
    writeFile(new File(projectDir, "settings.gradle"), "rootProject.name = 'test-project'");
  }

  @Test
  @DisplayName("should validate sorted xml files successfully")
  void shouldValidateSortedXmlFiles() throws IOException {
    writeFile(
        new File(projectDir, "messages_en.xml"),
        """
                        <?xml version="1.0" encoding="utf-8"?>
                        <resources>
                            <string name="common.button.no">No</string>
                            <string name="common.button.yes">Yes</string>
                            <string name="error.message.notfound">Not found</string>
                        </resources>
                        """);
    writeFile(
        new File(projectDir, "messages_de.xml"),
        """
                        <?xml version="1.0" encoding="utf-8"?>
                        <resources>
                            <string name="common.button.no">Nein</string>
                            <string name="common.button.yes">Ja</string>
                            <string name="error.message.notfound">Nicht gefunden</string>
                        </resources>
                        """);

    writeFile(
        new File(projectDir, "build.gradle"),
        """
                        plugins {
                            id 'dev.uebelacker.babeli'
                        }
                        babeli {
                            actions = ['sort']
                            translationFile 'en', file('messages_en.xml')
                            translationFile 'de', file('messages_de.xml')
                        }
                        """);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("babeliValidate", "--stacktrace")
            .build();

    assertThat(result.task(":babeliValidate").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    assertThat(result.getOutput()).contains("Validation passed.");
  }

  @Test
  @DisplayName("should fail validation for unsorted properties files")
  void shouldFailValidationForUnsortedPropertiesFiles() throws IOException {
    writeFile(
        new File(projectDir, "messages_en.properties"),
        "error.message.notfound=Not found\ncommon.button.yes=Yes\n");

    writeFile(
        new File(projectDir, "build.gradle"),
        """
                        plugins {
                            id 'dev.uebelacker.babeli'
                        }
                        babeli {
                            actions = ['sort']
                            translationFile 'en', file('messages_en.properties')
                        }
                        """);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("babeliValidate", "--stacktrace")
            .buildAndFail();

    assertThat(result.task(":babeliValidate").getOutcome()).isEqualTo(TaskOutcome.FAILED);
    assertThat(result.getOutput()).contains("Validation failed");
  }

  @Test
  @DisplayName("should update unsorted properties files")
  void shouldUpdateUnsortedPropertiesFiles() throws IOException {
    writeFile(
        new File(projectDir, "messages_en.properties"),
        "error.message.notfound=Not found\ncommon.button.yes=Yes\n");

    writeFile(
        new File(projectDir, "build.gradle"),
        """
                        plugins {
                            id 'dev.uebelacker.babeli'
                        }
                        babeli {
                            actions = ['sort']
                            translationFile 'en', file('messages_en.properties')
                        }
                        """);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("babeliUpdate", "--stacktrace")
            .build();

    assertThat(result.task(":babeliUpdate").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  @DisplayName("should validate multi-language json file")
  void shouldValidateMultiLanguageJsonFile() throws IOException {
    writeFile(
        new File(projectDir, "translations.json"),
        """
                        {
                          "common.button.yes": {
                            "de": "Ja",
                            "en": "Yes"
                          },
                          "error.message.notfound": {
                            "de": "Nicht gefunden",
                            "en": "Not found"
                          }
                        }
                        """);

    writeFile(
        new File(projectDir, "build.gradle"),
        """
                        plugins {
                            id 'dev.uebelacker.babeli'
                        }
                        babeli {
                            actions = ['sort', 'missing']
                            multiLanguageFile = file('translations.json')
                        }
                        """);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("babeliValidate", "--stacktrace")
            .build();

    assertThat(result.task(":babeliValidate").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
  }

  @Test
  @DisplayName("should detect missing translations")
  void shouldDetectMissingTranslations() throws IOException {
    writeFile(
        new File(projectDir, "messages_en.properties"),
        "common.button.yes=Yes\nerror.message.notfound=Not found\n");
    writeFile(new File(projectDir, "messages_de.properties"), "common.button.yes=Ja\n");

    writeFile(
        new File(projectDir, "build.gradle"),
        """
                        plugins {
                            id 'dev.uebelacker.babeli'
                        }
                        babeli {
                            actions = ['missing']
                            translationFile 'en', file('messages_en.properties')
                            translationFile 'de', file('messages_de.properties')
                        }
                        """);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("babeliValidate", "--stacktrace")
            .buildAndFail();

    assertThat(result.task(":babeliValidate").getOutcome()).isEqualTo(TaskOutcome.FAILED);
    assertThat(result.getOutput()).contains("Missing translation");
  }

  @Test
  @DisplayName("should validate and update xml files")
  void shouldValidateAndUpdateXmlFiles() throws IOException {
    writeFile(
        new File(projectDir, "strings_en.xml"),
        """
                        <?xml version="1.0" encoding="utf-8"?>
                        <resources>
                            <string name="error.notfound">Not found</string>
                            <string name="button.yes">Yes</string>
                        </resources>
                        """);

    writeFile(
        new File(projectDir, "build.gradle"),
        """
                        plugins {
                            id 'dev.uebelacker.babeli'
                        }
                        babeli {
                            actions = ['sort']
                            modelProvider = 'test'
                            translationFile 'en', file('strings_en.xml')
                        }
                        """);

    var validateResult =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("babeliValidate", "--stacktrace")
            .buildAndFail();

    assertThat(validateResult.task(":babeliValidate").getOutcome()).isEqualTo(TaskOutcome.FAILED);

    var updateResult =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("babeliUpdate", "--stacktrace")
            .build();

    assertThat(updateResult.task(":babeliUpdate").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);

    var revalidateResult =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments("babeliValidate", "--stacktrace")
            .build();

    assertThat(revalidateResult.task(":babeliValidate").getOutcome())
        .isEqualTo(TaskOutcome.SUCCESS);
  }

  private void writeFile(File file, String content) throws IOException {
    Files.writeString(file.toPath(), content);
  }
}
