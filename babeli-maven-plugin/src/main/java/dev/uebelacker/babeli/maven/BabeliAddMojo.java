package dev.uebelacker.babeli.maven;

import dev.uebelacker.babeli.core.Babeli;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Adds a translation key to the translation files.
 *
 * <p>Unlike the interactive {@code add} command of the CLI this goal takes the key and its
 * translations as parameters, for example:
 *
 * <pre>mvn babeli:add -Dbabeli.key=common.button.ok -Dbabeli.translations=en=OK,de=Okay</pre>
 */
@Mojo(name = "add")
public class BabeliAddMojo extends AbstractBabeliMojo {

  @Parameter(property = "babeli.key")
  private String key;

  /** Translations as {@code language=value} pairs. */
  @Parameter(property = "babeli.translations")
  private String[] translations;

  @Parameter(property = "babeli.bundle")
  private String bundle;

  @Override
  public void execute() throws MojoFailureException {
    if (key == null || key.isBlank()) {
      throw new MojoFailureException("No translation key given. Use -Dbabeli.key=<key>.");
    }

    var translationValues = parseTranslations();
    if (translationValues.isEmpty()) {
      throw new MojoFailureException(
          "No translation given. Use -Dbabeli.translations=<language>=<value>.");
    }

    var configuration = createConfiguration();

    try {
      Babeli.add(bundle, key, translationValues, configuration);
    } catch (RuntimeException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }

    getLog().info("Added translation key '" + key + "'.");
  }

  private Map<String, String> parseTranslations() throws MojoFailureException {
    var translationValues = new LinkedHashMap<String, String>();

    if (translations == null) {
      return translationValues;
    }

    for (var translation : translations) {
      var separator = translation.indexOf('=');
      if (separator < 1) {
        throw new MojoFailureException(
            "Invalid translation '%s'. Expected format: <language>=<value>."
                .formatted(translation));
      }
      translationValues.put(
          translation.substring(0, separator), translation.substring(separator + 1));
    }

    return translationValues;
  }
}
