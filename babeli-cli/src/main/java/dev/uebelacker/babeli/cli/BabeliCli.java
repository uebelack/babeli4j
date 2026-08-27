package dev.uebelacker.babeli.cli;

import dev.uebelacker.babeli.cli.commands.Add;
import dev.uebelacker.babeli.cli.commands.Update;
import dev.uebelacker.babeli.cli.commands.Validate;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "babeli",
    subcommands = {Validate.class, Update.class, Add.class})
@SuppressWarnings("java:S106")
public class BabeliCli implements Callable<Integer> {
  public static void main(String... args) {
    System.exit(execute(args));
  }

  public static Integer execute(String... args) {
    var commandLine = new CommandLine(new BabeliCli());
    if (args.length == 0) {
      commandLine.usage(System.out);
      return 1;
    } else {
      return commandLine.execute(args);
    }
  }

  @Override
  public Integer call() {
    CommandLine.usage(this, System.out);
    return 0;
  }
}
