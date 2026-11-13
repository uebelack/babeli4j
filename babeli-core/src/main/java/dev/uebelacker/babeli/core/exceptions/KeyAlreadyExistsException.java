package dev.uebelacker.babeli.core.exceptions;

public class KeyAlreadyExistsException extends RuntimeException {
  public KeyAlreadyExistsException(String key) {
    super("Key '%s' already exists.".formatted(key));
  }
}
