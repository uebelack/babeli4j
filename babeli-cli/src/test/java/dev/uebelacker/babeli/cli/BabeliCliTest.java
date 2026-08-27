package dev.uebelacker.babeli.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.uebelacker.babeli.cli.commands.Add;
import java.nio.file.Files;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class BabeliCliTest {
  @Test
  @DisplayName("should show usage")
  void shouldShowUsage() {
    assertThat(BabeliCli.execute(new String[] {})).isEqualTo(1);
  }

  @Test
  @DisplayName("should run validate")
  void shouldRunValidate() {
    assertThat(BabeliCli.execute(new String[] {"validate", "-f", "src/test/resources/test.json"}))
        .isZero();
  }

  @Test
  @DisplayName("should run update")
  void shouldRunValidateWithArgs() {
    assertThat(BabeliCli.execute(new String[] {"update", "-f", "src/test/resources/test.json"}))
        .isZero();
  }

  @Test
  @DisplayName("should run add")
  void shouldRunAddWithArgs() throws Exception {
    var file = Files.createTempFile("babeli-cli-add-test", ".json");
    Files.writeString(file, "{\n  \"no\": {\n    \"de\": \"Nein\",\n    \"en\": \"No\"\n  }\n}\n");
    try {
      var lineReader = mock(LineReader.class);
      when(lineReader.readLine(anyString())).thenReturn("yes", "Ja", "Yes");
      var terminal = mock(Terminal.class);
      var command =
          new Add() {
            @Override
            protected Terminal createTerminal() {
              return terminal;
            }

            @Override
            protected LineReader createLineReader(Terminal ignoredTerminal) {
              return lineReader;
            }
          };

      assertThat(new CommandLine(command).execute("-f", file.toString())).isZero();
      assertThat(Files.readString(file)).contains("\"yes\"");
    } finally {
      Files.deleteIfExists(file);
    }
  }
}
