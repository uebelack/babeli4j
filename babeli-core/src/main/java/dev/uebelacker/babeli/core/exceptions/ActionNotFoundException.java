package dev.uebelacker.babeli.core.exceptions;

public class ActionNotFoundException extends RuntimeException {
  public ActionNotFoundException(String actionName) {
    super("Action " + actionName + " not found");
  }
}
