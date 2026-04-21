package dev.uebelacker.babeli.cli;

import java.util.concurrent.Callable;

import dev.uebelacker.babeli.cli.commands.Update;
import dev.uebelacker.babeli.cli.commands.Validate;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "babeli", subcommands = {Validate.class, Update.class})
public class BabeliCli implements Callable<Integer> {
    public static void main(String... args) {
        var commandLine = new CommandLine(new BabeliCli());
        if (args.length == 0) {
            commandLine.usage(System.out);
            System.exit(1);
        } else {
            System.exit(commandLine.execute(args));
        }
    }

    @Override
    public Integer call() throws Exception {
        CommandLine.usage(this, System.out);

        return 0;
    }
}
