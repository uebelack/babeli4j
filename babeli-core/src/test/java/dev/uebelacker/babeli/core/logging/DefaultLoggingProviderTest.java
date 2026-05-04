package dev.uebelacker.babeli.core.logging;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultLoggingProviderTest {

  @Test
  @DisplayName("should work")
  void shouldWork() {
    var provider = new DefaultLoggingProvider();

    Assertions.assertThatNoException()
        .isThrownBy(
            () -> {
              provider.debug("debug");
              provider.info("info");
              provider.error("error");
            });
  }
}
