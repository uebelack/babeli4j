package dev.uebelacker.babeli.core.exceptions;

import dev.uebelacker.babeli.core.Configuration;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleResourceBundlesFoundException extends RuntimeException {

  public MultipleResourceBundlesFoundException(List<Configuration> configurations) {
    super(
        "Multiple resource bundles found, please specify which one to use: "
            + configurations.stream()
                .map(Configuration::getName)
                .collect(Collectors.joining(", ")));
  }
}
