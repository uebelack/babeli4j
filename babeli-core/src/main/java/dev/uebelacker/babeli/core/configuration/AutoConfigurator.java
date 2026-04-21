package dev.uebelacker.babeli.core.configuration;

import java.util.List;

import dev.uebelacker.babeli.core.Configuration;

public interface AutoConfigurator {
    boolean matches(Configuration configuration);

    List<Configuration> configure(Configuration configuration);
}
