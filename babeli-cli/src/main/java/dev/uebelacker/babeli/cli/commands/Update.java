package dev.uebelacker.babeli.cli.commands;

import dev.uebelacker.babeli.core.Babeli;
import picocli.CommandLine.Command;

@Command(name = "update", description = "Updates the translation files.")
public class Update extends AbstractCommand {
    @Override
    public Integer call() {
        try {
            Babeli.update(createConfiguration());
            return 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 1;
        }
    }
}
