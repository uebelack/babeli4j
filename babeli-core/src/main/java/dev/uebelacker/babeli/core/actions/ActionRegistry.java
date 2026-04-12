package dev.uebelacker.babeli.core.actions;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ActionNotFoundException;
import dev.uebelacker.babeli.core.exceptions.UnexpectedErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActionRegistry {
  private static Map<String, Class<? extends Action>> actionClasses = new HashMap<>();

  static {
    registerAction(MissingAction.NAME, MissingAction.class);
    registerAction(GlossaryAction.NAME, GlossaryAction.class);
    registerAction(SortAction.NAME, SortAction.class);
  }

  private ActionRegistry() {}

  public static void registerAction(String name, Class<? extends Action> actionClass) {
    actionClasses.put(name, actionClass);
  }

  public static Set<String> getActionNames() {
    return actionClasses.keySet();
  }

  public static Action createAction(String name, Configuration configuration) {
    var actionClass = actionClasses.get(name);
    if (actionClass == null) {
      throw new ActionNotFoundException(name);
    }
    try {
      return actionClass.getConstructor(Configuration.class).newInstance(configuration);
    } catch (Exception e) {
      try {
        return actionClass.getConstructor().newInstance();
      } catch (Exception ex) {
        throw new UnexpectedErrorException("Failed to instantiate Action: " + name, ex);
      }
    }
  }
}
