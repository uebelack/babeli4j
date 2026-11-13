package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.Babeli;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Validation should always run")
public abstract class BabeliValidateTask extends AbstractBabeliTask {

  @TaskAction
  public void validate() {
    var configuration = createConfiguration();

    var errors = withBabeliClassLoader(() -> Babeli.validate(configuration));

    if (errors.isEmpty()) {
      getLogger().lifecycle("Validation passed.");
    } else {
      for (var error : errors) {
        if (getLogger().isErrorEnabled()) {
          getLogger().error("> {}", error.message());
        }
      }
      throw new GradleException("Validation failed with " + errors.size() + " error(s).");
    }
  }
}
