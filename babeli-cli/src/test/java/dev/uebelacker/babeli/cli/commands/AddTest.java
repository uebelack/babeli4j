package dev.uebelacker.babeli.cli.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.uebelacker.babeli.core.Configuration;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class AddTest {

  static final Path PROJECT = Path.of("target/test/cli/project");
  static final Path RESOURCES = PROJECT.resolve("src/main/resources");
  static final Pattern BUNDLE_ENTRY = Pattern.compile("^ {2}(\\d+)\\) (.+)$");

  LineReader lineReader;
  PrintStream originalOut;
  PrintStream originalErr;
  ByteArrayOutputStream out;
  ByteArrayOutputStream err;

  @BeforeEach
  void setUp() throws IOException {
    deleteRecursively(PROJECT);
    Files.createDirectories(RESOURCES);
    writeBundle("messages", "greeting", "Hello", "Hallo");
    writeBundle("errors", "notfound", "Not found", "Nicht gefunden");

    lineReader = mock(LineReader.class);

    out = new ByteArrayOutputStream();
    err = new ByteArrayOutputStream();
    originalOut = System.out;
    originalErr = System.err;
    System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  @DisplayName("should add the key to the bundle selected with --bundle")
  void shouldAddKeyToBundleSelectedByName() throws IOException {
    when(lineReader.readLine(anyString())).thenReturn("farewell", "Bye", "Bye");

    assertThat(execute("-d", PROJECT.toString(), "--bundle", "messages")).isZero();

    assertThat(bundleContent("messages", "en")).contains("farewell");
    assertThat(bundleContent("messages", "de")).contains("farewell");
    assertThat(bundleContent("errors", "en")).doesNotContain("farewell");
  }

  @Test
  @DisplayName("should fail if the bundle given with --bundle does not exist")
  void shouldFailIfNamedBundleDoesNotExist() {
    assertThat(execute("-d", PROJECT.toString(), "--bundle", "nope")).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8))
        .contains("Bundle 'nope' not found. Available bundles:")
        .contains("messages")
        .contains("errors");
  }

  @Test
  @DisplayName("should list the bundles and add the key to the one selected by number")
  void shouldListBundlesAndAddKeyToSelectedOne() throws IOException {
    when(lineReader.readLine(anyString())).thenReturn("2", "farewell", "Bye", "Bye");

    assertThat(execute("-d", PROJECT.toString())).isZero();

    var listed = listedBundles();
    assertThat(listed).hasSize(2);

    // the entry printed as "2)" must be the one that received the key
    var selected = listed.get(1);
    var notSelected = listed.get(0);
    assertThat(bundleContent(selected, "en")).contains("farewell");
    assertThat(bundleContent(notSelected, "en")).doesNotContain("farewell");
  }

  @Test
  @DisplayName("should fail if the selected bundle number is out of range")
  void shouldFailIfSelectionIsOutOfRange() {
    when(lineReader.readLine(anyString())).thenReturn("5");

    assertThat(execute("-d", PROJECT.toString())).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8))
        .contains("Invalid selection. Please specify a number between 1 and 2.");
  }

  @Test
  @DisplayName("should fail if the selected bundle number is zero")
  void shouldFailIfSelectionIsZero() {
    when(lineReader.readLine(anyString())).thenReturn("0");

    assertThat(execute("-d", PROJECT.toString())).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8)).contains("Invalid selection.");
  }

  @Test
  @DisplayName("should fail if the bundle selection is not a number")
  void shouldFailIfSelectionIsNotANumber() {
    when(lineReader.readLine(anyString())).thenReturn("not a number");

    assertThat(execute("-d", PROJECT.toString())).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8)).contains("Invalid selection.");
  }

  @Test
  @DisplayName("should fail if no resource bundle was found")
  void shouldFailIfNoBundlesFound() {
    var configuration = mock(Configuration.class);
    when(configuration.autoConfigure()).thenReturn(List.of());

    var command =
        new TestableAdd() {
          @Override
          protected Configuration createConfiguration() {
            return configuration;
          }
        };

    assertThat(new CommandLine(command).execute("-d", PROJECT.toString())).isEqualTo(1);
    assertThat(err.toString(StandardCharsets.UTF_8)).contains("No bundles found.");
  }

  @Test
  @DisplayName("should fail if the key already exists")
  void shouldFailIfKeyAlreadyExists() {
    when(lineReader.readLine(anyString())).thenReturn("greeting");

    assertThat(execute("-d", PROJECT.toString(), "--bundle", "messages")).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8)).contains("Key 'greeting' already exists.");
  }

  @Test
  @DisplayName("should fail if no translation at all was provided")
  void shouldFailIfNoTranslationProvided() {
    when(lineReader.readLine(anyString())).thenReturn("farewell", "", "");

    assertThat(execute("-d", PROJECT.toString(), "--bundle", "messages")).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8))
        .contains("At least one translation must be provided to generate missing languages.");
  }

  @Test
  @DisplayName("should fail if the translation file contains no languages")
  void shouldFailIfNoLanguagesFound() throws IOException {
    var empty = PROJECT.resolve("empty.json");
    Files.writeString(empty, "{}");

    assertThat(execute("-f", empty.toString())).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8)).contains("No languages found in");
  }

  @Test
  @DisplayName("should exit without changes if an empty key is entered")
  void shouldExitWithoutChangesIfKeyIsEmpty() throws IOException {
    var before = bundleContent("messages", "en");
    when(lineReader.readLine(anyString())).thenReturn("");

    assertThat(execute("-d", PROJECT.toString(), "--bundle", "messages")).isZero();

    assertThat(bundleContent("messages", "en")).isEqualTo(before);
  }

  @Test
  @DisplayName("should abort if the user interrupts the prompt")
  void shouldAbortOnUserInterrupt() {
    when(lineReader.readLine(anyString())).thenThrow(new UserInterruptException(""));

    assertThat(execute("-d", PROJECT.toString(), "--bundle", "messages")).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8)).contains("Aborted.");
  }

  @Test
  @DisplayName("should abort if the input stream is closed")
  void shouldAbortOnEndOfFile() {
    when(lineReader.readLine(anyString())).thenThrow(new EndOfFileException());

    assertThat(execute("-d", PROJECT.toString(), "--bundle", "messages")).isEqualTo(1);

    assertThat(err.toString(StandardCharsets.UTF_8)).contains("Aborted.");
  }

  private int execute(String... args) {
    return new CommandLine(new TestableAdd()).execute(args);
  }

  /** Names of the bundles in the order the command printed them. */
  private List<String> listedBundles() {
    return out.toString(StandardCharsets.UTF_8)
        .lines()
        .map(BUNDLE_ENTRY::matcher)
        .filter(java.util.regex.Matcher::matches)
        .sorted(Comparator.comparingInt(m -> Integer.parseInt(m.group(1))))
        .map(m -> m.group(2))
        .toList();
  }

  private String bundleContent(String bundle, String language) throws IOException {
    return Files.readString(RESOURCES.resolve("%s_%s.properties".formatted(bundle, language)));
  }

  private void writeBundle(String bundle, String key, String english, String german)
      throws IOException {
    Files.writeString(
        RESOURCES.resolve(bundle + "_en.properties"), "%s=%s%n".formatted(key, english));
    Files.writeString(
        RESOURCES.resolve(bundle + "_de.properties"), "%s=%s%n".formatted(key, german));
  }

  private static void deleteRecursively(Path path) throws IOException {
    if (!Files.exists(path)) {
      return;
    }
    try (var stream = Files.walk(path)) {
      stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(java.io.File::delete);
    }
  }

  /** Replaces the interactive terminal and line reader with test doubles. */
  private class TestableAdd extends Add {

    @Override
    protected Terminal createTerminal() {
      return mock(Terminal.class);
    }

    @Override
    protected LineReader createLineReader(Terminal terminal) {
      return lineReader;
    }
  }
}
