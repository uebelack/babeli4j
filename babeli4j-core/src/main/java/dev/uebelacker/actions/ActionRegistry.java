package dev.uebelacker.actions;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {
    private static Map<String, Action> actions = new HashMap<>();

    private ActionRegistry() {
    }

    public static Map<String, Action> getActions() {
        return actions;
    }

    public static void registerAction(Action action) {
        actions.put(action.name(), action);
    }
}
