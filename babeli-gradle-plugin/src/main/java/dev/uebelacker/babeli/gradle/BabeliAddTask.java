package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.Babeli;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gradle.api.GradleException;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Adds a translation key on demand")
public abstract class BabeliAddTask extends AbstractBabeliTask {

  @Input
  @Optional
  @Option(option = "key", description = "Translation key to add.")
  public abstract Property<String> getKey();

  @Input
  @Optional
  @Option(
      option = "translation",
      description = "Translation as language=value. Repeat the option for every language.")
  public abstract ListProperty<String> getTranslations();

  @Input
  @Optional
  @Option(
      option = "bundle",
      description = "Name of the resource bundle to add the key to, if the project has several.")
  public abstract Property<String> getBundle();

  @TaskAction
  public void add() {
    if (!getKey().isPresent() || getKey().get().isBlank()) {
      throw new GradleException("No translation key given. Use --key=<key>.");
    }

    var translations = parseTranslations(getTranslations().getOrElse(List.of()));
    if (translations.isEmpty()) {
      throw new GradleException(
          "No translation given. Use --translation=<language>=<value> at least once.");
    }

    var key = getKey().get();
    var configuration = createConfiguration();

    withBabeliClassLoader(
        () -> {
          Babeli.add(getBundle().getOrNull(), key, translations, configuration);
          return null;
        });

    getLogger().lifecycle("Added translation key '{}'.", key);
  }

  private Map<String, String> parseTranslations(List<String> values) {
    var translations = new LinkedHashMap<String, String>();

    for (var value : values) {
      var separator = value.indexOf('=');
      if (separator < 1) {
        throw new GradleException(
            "Invalid translation '%s'. Expected format: <language>=<value>.".formatted(value));
      }
      translations.put(value.substring(0, separator), value.substring(separator + 1));
    }

    return translations;
  }
}
