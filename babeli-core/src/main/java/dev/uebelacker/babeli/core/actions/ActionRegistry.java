package dev.uebelacker.babeli.core.actions;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {
  private static Map<String, Class<? extends Action>> actionClasses = new HashMap<>();

  private ActionRegistry() {}

  public static void registerAction(String name, Class<? extends Action> actionClass) {
    actionClasses.put(name, actionClass);
  }
}
