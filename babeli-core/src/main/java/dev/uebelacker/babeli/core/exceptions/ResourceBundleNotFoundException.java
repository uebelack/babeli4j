package dev.uebelacker.babeli.core.exceptions;

public class ResourceBundleNotFoundException extends RuntimeException {
  public ResourceBundleNotFoundException(String name) {
    super("Resource bundle not found: " + name);
  }
}
