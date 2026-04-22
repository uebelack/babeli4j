package dev.uebelacker.babeli.core.configuration;

import dev.uebelacker.babeli.core.Configuration;
import java.util.List;

public interface AutoConfigurator {
  boolean matches(Configuration configuration);

  List<Configuration> configure(Configuration configuration);
}
