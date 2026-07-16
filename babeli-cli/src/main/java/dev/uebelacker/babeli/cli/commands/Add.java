package dev.uebelacker.babeli.cli.commands;

import dev.uebelacker.babeli.core.Babeli;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.Translations;
import dev.uebelacker.babeli.core.readers.FileReaderRegistry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "add", description = "Adds a translation key interactively.")
@SuppressWarnings("java:S106")
public class Add extends AbstractCommand {

  @Option(
      names = {"--bundle"},
      description = "Name of the resource bundle to add the key to.")
  private String bundle;

  @Override
  public Integer call() {
    try (var terminal = createTerminal()) {
      var reader = createLineReader(terminal);
      var bundles = createConfiguration().autoConfigure();

      var selectedBundle = selectBundle(reader, bundles);
      var existingTranslations = readExistingTranslations(selectedBundle);
      var keyTranslations =
          promptForTranslations(reader, existingTranslations, targetDescription(selectedBundle));

      if (keyTranslations == null) {
        return 0;
      }

      Babeli.add(
          selectedBundle.getName(),
          keyTranslations.key(),
          keyTranslations.values(),
          selectedBundle);

      return 0;
    } catch (UserInterruptException | EndOfFileException e) {
      System.err.println("Aborted.");
      return 1;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return 1;
    }
  }

  protected Terminal createTerminal() throws Exception {
    return TerminalBuilder.builder().system(true).build();
  }

  protected LineReader createLineReader(Terminal terminal) {
    return LineReaderBuilder.builder().terminal(terminal).build();
  }

  private Configuration selectBundle(LineReader reader, List<Configuration> bundles) {
    if (bundles.isEmpty()) {
      throw new IllegalStateException("No bundles found.");
    }

    if (bundle != null) {
      return bundles.stream()
          .filter(b -> bundle.equals(b.getName()))
          .findFirst()
          .orElseThrow(
              () ->
                  new IllegalArgumentException(
                      "Bundle '%s' not found. Available bundles: %s"
                          .formatted(
                              bundle,
                              bundles.stream()
                                  .map(Configuration::getName)
                                  .filter(java.util.Objects::nonNull)
                                  .collect(java.util.stream.Collectors.joining(", ")))));
    }

    if (bundles.size() == 1) {
      return bundles.get(0);
    }

    System.out.println("Available bundles:");
    for (var i = 0; i < bundles.size(); i++) {
      var name = bundles.get(i).getName();
      System.out.println("  %d) %s".formatted(i + 1, name != null ? name : "bundle " + (i + 1)));
    }

    var input =
        Optional.ofNullable(reader.readLine("Select bundle (1-%d): ".formatted(bundles.size())))
            .map(String::trim)
            .orElse("");

    try {
      var index = Integer.parseInt(input) - 1;
      if (index >= 0 && index < bundles.size()) {
        return bundles.get(index);
      }
    } catch (NumberFormatException e) {
      // fall through
    }

    throw new IllegalArgumentException(
        "Invalid selection. Please specify a number between 1 and %d.".formatted(bundles.size()));
  }

  private String targetDescription(Configuration configuration) {
    if (configuration.getFile() != null) {
      return "file '%s'".formatted(configuration.getFile().getPath());
    }

    return "configured language files";
  }

  private KeyTranslations promptForTranslations(
      LineReader reader, Translations existingTranslations, String targetDescription) {
    var languages = existingTranslations.getLanguages();
    if (languages.isEmpty()) {
      throw new IllegalStateException("No languages found in %s.".formatted(targetDescription));
    }

    var key =
        readKey(
            reader, "Translation key for %s (or empty to finish): ".formatted(targetDescription));
    if (key == null) {
      return null;
    }
    if (existingTranslations.getKeys().contains(key)) {
      throw new IllegalArgumentException("Key '%s' already exists.".formatted(key));
    }

    var values = new LinkedHashMap<String, String>();
    for (var language : languages) {
      var entered =
          Optional.ofNullable(
                  reader.readLine(
                      "Translation for '%s' (leave empty for auto-translation): "
                          .formatted(language)))
              .map(String::trim)
              .orElse("");
      if (!entered.isEmpty()) {
        values.put(language, entered);
      }
    }

    if (values.isEmpty()) {
      throw new IllegalArgumentException(
          "At least one translation must be provided to generate missing languages.");
    }

    return new KeyTranslations(key, values);
  }

  private Translations readExistingTranslations(Configuration configuration) {
    configuration.validate();

    var fileReader = FileReaderRegistry.getFileReader(configuration);
    if (configuration.getFile() != null) {
      return Translations.fromTranslations(
          fileReader.readFile(configuration.getFile()).translations());
    }

    if (configuration.getFiles() != null) {
      return Translations.fromTranslations(
          configuration.getFiles().stream()
              .map(file -> fileReader.readFile(file.getLanguage(), file.getFile()))
              .flatMap(file -> file.translations().stream())
              .toList());
    }

    throw new IllegalStateException("No files configured.");
  }

  private String readKey(LineReader reader, String prompt) {
    var value = Optional.ofNullable(reader.readLine(prompt)).map(String::trim).orElse("");
    if (value.isEmpty()) {
      return null;
    }
    return value;
  }

  private record KeyTranslations(String key, Map<String, String> values) {}
}
