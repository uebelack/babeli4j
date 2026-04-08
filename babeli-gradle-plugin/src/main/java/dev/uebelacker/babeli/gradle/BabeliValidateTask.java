package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.Babeli;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Validation should always run")
public abstract class BabeliValidateTask extends DefaultTask {

  @Internal
  public abstract Property<BabeliExtension> getExtension();

  @TaskAction
  public void validate() {
    var extension = getExtension().get();
    var errors = Babeli.validate(extension.toConfiguration());
    if (errors.isEmpty()) {
      getLogger().lifecycle("Validation passed.");
    } else {
      for (var error : errors) {
        getLogger().error(error.toString());
      }
      throw new GradleException("Validation failed with " + errors.size() + " error(s).");
    }
  }
}
