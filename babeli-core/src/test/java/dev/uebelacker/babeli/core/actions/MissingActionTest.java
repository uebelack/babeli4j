package dev.uebelacker.babeli.core.actions;


import dev.uebelacker.babeli.core.Fixtures;
import dev.uebelacker.babeli.core.model.Error;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MissingActionTest {
    @Test
    @DisplayName("should return a list of all missing keys")
    void shouldReturnAListOfAllMissingKeys() {
        var action = new MissingAction();
        assertThat(action.validate(Fixtures.translationFiles())).isEqualTo(List.of(
                new Error("missing", "de", "common.button.no", "Missing key 'common.button.no' in file test_de.properties"),
                new Error("missing", "en", "common.button.perhaps", "Missing key 'common.button.perhaps' in file test_en.properties")));
    }
}
