package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.Babeli;
import dev.uebelacker.babeli.core.model.Error;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Validation should always run")
public abstract class BabeliValidateTask extends DefaultTask {

  @Internal
  public abstract Property<BabeliExtension> getExtension();

  @Classpath
  public abstract ConfigurableFileCollection getClasspath();

  @TaskAction
  public void validate() {
    var extension = getExtension().get();
    var configuration = extension.toConfiguration(getProject());

    List<Error> errors;
    var classLoader = createClassLoader();
    if (classLoader != null) {
      var previousClassLoader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(classLoader);
        errors = Babeli.validate(configuration);
      } finally {
        Thread.currentThread().setContextClassLoader(previousClassLoader);
      }
    } else {
      errors = Babeli.validate(configuration);
    }

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

  private URLClassLoader createClassLoader() {
    var files = getClasspath().getFiles();
    if (files.isEmpty()) {
      return null;
    }
    var urls = files.stream().map(this::toURL).toArray(URL[]::new);
    return new URLClassLoader(urls, getClass().getClassLoader());
  }

  private URL toURL(File file) {
    try {
      return file.toURI().toURL();
    } catch (Exception e) {
      throw new GradleException("Failed to convert file to URL: " + file, e);
    }
  }
}
