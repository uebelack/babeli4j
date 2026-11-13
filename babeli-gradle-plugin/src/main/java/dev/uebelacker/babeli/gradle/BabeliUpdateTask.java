package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.Babeli;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Translation files may have changed")
public abstract class BabeliUpdateTask extends AbstractBabeliTask {

  @TaskAction
  public void update() {
    var configuration = createConfiguration();

    withBabeliClassLoader(
        () -> {
          Babeli.update(configuration);
          return null;
        });
  }
}
