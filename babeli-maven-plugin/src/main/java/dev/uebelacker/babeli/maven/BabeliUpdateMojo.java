package dev.uebelacker.babeli.maven;

import dev.uebelacker.babeli.core.Babeli;
import dev.uebelacker.babeli.core.util.EnvUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "update", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class BabeliUpdateMojo extends AbstractBabeliMojo {

  @Override
  public void execute() throws MojoExecutionException {
    if (skip || isCi()) {
      getLog().info("Babeli update skipped.");
      return;
    }

    var configuration = createConfiguration();

    try {
      Babeli.update(configuration);
      getLog().info("Translation files updated.");
    } catch (Exception e) {
      throw new MojoExecutionException("Failed to update translation files.", e);
    }
  }

  boolean isCi() {
    return EnvUtils.get("CI") != null;
  }
}
