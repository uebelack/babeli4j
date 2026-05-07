package dev.uebelacker.babeli.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class BabeliPlugin implements Plugin<Project> {
  public static final String CONFIGURATION_NAME = "babeli";

  @Override
  public void apply(Project project) {
    var extension = project.getExtensions().create("babeli", BabeliExtension.class);

    var babeliConfig =
        project
            .getConfigurations()
            .create(
                CONFIGURATION_NAME,
                config -> {
                  config.setDescription(
                      "Additional dependencies for the Babeli plugin (e.g., model providers).");
                  config.setVisible(false);
                  config.setCanBeConsumed(false);
                  config.setCanBeResolved(true);
                });

    var validateTask =
        project
            .getTasks()
            .register(
                "babeliValidate",
                BabeliValidateTask.class,
                task -> {
                  task.setGroup("babeli");
                  task.setDescription("Validates translation files using configured actions.");
                  task.getExtension().set(extension);
                  task.getClasspath().from(babeliConfig);
                });

    var updateTask =
        project
            .getTasks()
            .register(
                "babeliUpdate",
                BabeliUpdateTask.class,
                task -> {
                  task.setGroup("babeli");
                  task.setDescription("Updates translation files using configured actions.");
                  task.getExtension().set(extension);
                  task.getClasspath().from(babeliConfig);
                });

    project.afterEvaluate(
        p -> {
          p.getTasks()
              .matching(task -> task.getName().equals("check"))
              .configureEach(task -> task.dependsOn(validateTask));

          p.getTasks()
              .matching(
                  task ->
                      task.getName().equals("processResources")
                          || task.getName().equals("preBuild"))
              .configureEach(task -> task.dependsOn(updateTask));
        });
  }
}
