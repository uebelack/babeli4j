package dev.uebelacker.babeli.core.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ActionNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ActionRegistryTest {
  @Test
  @DisplayName("should creation for given name and context")
  void shouldCreateForGivenNameAndContext() {
    var action = ActionRegistry.createAction(MissingAction.NAME, new Configuration());

    assertThat(action).isInstanceOf(MissingAction.class);
  }

  @Test
  @DisplayName("should throw action not found")
  @SuppressWarnings("java:S5778")
  void shouldThrowActionNotFound() {
    assertThatExceptionOfType(ActionNotFoundException.class)
        .isThrownBy(() -> ActionRegistry.createAction("nonExistingAction", new Configuration()))
        .withMessage("Action nonExistingAction not found");
  }
}
