package dev.uebelacker.babeli.cli.commands;

import dev.uebelacker.babeli.core.Babeli;
import picocli.CommandLine.Command;

@Command(
        name = "validate",
        description = "Validates the translation files.")
public class Validate extends AbstractCommand {
    @Override
    public Integer call() {
        try {
            var errors = Babeli.validate(createConfiguration());

            errors.forEach(System.err::println);

            return errors.isEmpty() ? 0 : 1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 1;
        }
    }
}

