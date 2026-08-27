package dev.uebelacker.babeli.gradle;

import dev.uebelacker.babeli.core.Configuration;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Supplier;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Internal;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Translation files may have changed")
public abstract class AbstractBabeliTask extends DefaultTask {

  @Internal
  public abstract Property<BabeliExtension> getExtension();

  @Classpath
  public abstract ConfigurableFileCollection getClasspath();

  protected Configuration createConfiguration() {
    return getExtension().get().toConfiguration(getProject());
  }

  /**
   * Runs the given action with the babeli configuration on the context class loader, so model
   * providers added to the {@code babeli} configuration can be resolved by name.
   */
  protected <T> T withBabeliClassLoader(Supplier<T> action) {
    var classLoader = createClassLoader();
    if (classLoader == null) {
      return action.get();
    }

    var previousClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(classLoader);
      return action.get();
    } finally {
      Thread.currentThread().setContextClassLoader(previousClassLoader);
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
