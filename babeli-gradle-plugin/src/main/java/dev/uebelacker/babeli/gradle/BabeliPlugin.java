package dev.uebelacker.babeli.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class BabeliPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    var extension = project.getExtensions().create("babeli", BabeliExtension.class);

    project.getTasks().register("babeliValidate", BabeliValidateTask.class, task -> {
      task.setGroup("babeli");
      task.setDescription("Validates translation files using configured actions.");
      task.getExtension().set(extension);
    });

    project.getTasks().register("babeliUpdate", BabeliUpdateTask.class, task -> {
      task.setGroup("babeli");
      task.setDescription("Updates translation files using configured actions.");
      task.getExtension().set(extension);
    });
  }
}
