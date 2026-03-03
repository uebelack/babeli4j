package dev.uebelacker.babeli.core.ai.actions;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompleteActionTest {

  @Test
  @DisplayName("should add missing translations")
  void shouldAddMissingTranslations() {
    CompleteAction completeAction = new CompleteAction();
    completeAction.update(List.of());
  }
}
