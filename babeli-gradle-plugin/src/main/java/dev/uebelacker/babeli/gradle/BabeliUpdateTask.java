package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.Babeli;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Translation files may have changed")
public abstract class BabeliUpdateTask extends DefaultTask {

  @Internal
  public abstract Property<BabeliExtension> getExtension();

  @TaskAction
  public void update() {
    var extension = getExtension().get();
    Babeli.update(extension.toConfiguration());
    getLogger().lifecycle("Translation files updated.");
  }
}
