package dev.uebelacker.babeli.maven;

import dev.uebelacker.babeli.core.Babeli;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "update", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class BabeliUpdateMojo extends AbstractBabeliMojo {

  @Override
  public void execute() throws MojoExecutionException {
    var configuration = createConfiguration();
    Babeli.update(configuration);
    getLog().info("Translation files updated.");
  }
}
