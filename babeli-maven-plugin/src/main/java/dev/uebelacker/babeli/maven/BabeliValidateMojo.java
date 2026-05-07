package dev.uebelacker.babeli.maven;

import dev.uebelacker.babeli.core.Babeli;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "validate", defaultPhase = LifecyclePhase.VERIFY)
public class BabeliValidateMojo extends AbstractBabeliMojo {

  @Override
  public void execute() throws MojoFailureException {

    var configuration = createConfiguration();
    var errors = Babeli.validate(configuration);

    if (!errors.isEmpty()) {
      for (var error : errors) {
        getLog().error(error.message());
      }
      throw new MojoFailureException("Validation failed with " + errors.size() + " error(s).");
    }
  }
}
