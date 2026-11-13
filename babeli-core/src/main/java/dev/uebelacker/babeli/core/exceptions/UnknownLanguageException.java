package dev.uebelacker.babeli.core.exceptions;

import java.util.Collection;

public class UnknownLanguageException extends RuntimeException {
  public UnknownLanguageException(Collection<String> unknown, Collection<String> available) {
    super(
        "Unknown language(s): %s. Available languages: %s."
            .formatted(String.join(", ", unknown), String.join(", ", available)));
  }
}
