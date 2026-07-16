package dev.uebelacker.babeli.core;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;
import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_SKIP;

import java.util.List;
import java.util.Map;

import dev.uebelacker.babeli.core.exceptions.MultipleResourceBundlesFoundException;
import dev.uebelacker.babeli.core.exceptions.ResourceBundleNotFoundException;
import dev.uebelacker.babeli.core.logging.Logger;
import dev.uebelacker.babeli.core.model.Error;
import dev.uebelacker.babeli.core.services.AddService;
import dev.uebelacker.babeli.core.services.UpdateService;
import dev.uebelacker.babeli.core.services.ValidateService;
import dev.uebelacker.babeli.core.util.EnvUtils;

public class Babeli {
    private Babeli() {
    }

    public static List<Error> validate(Configuration configuration) {
        var log = new Logger(configuration);

        if (skip(configuration)) {
            log.info("Babeli is skipped.");
            return List.of();
        }

        return new ValidateService(configuration).validate();
    }

    public static void update(Configuration configuration) {
        var log = new Logger(configuration);

        if (skip(configuration)) {
            log.info("Babeli is skipped.");
            return;
        }

        if (EnvUtils.get(BABELI_MODEL_PROVIDER, configuration.getModelProvider()) == null) {
            log.warn(
                    "No model provider specified. Babeli requires a model provider to function. Please specify a model provider using 'modelProvider' in the configuration or specify it as environment variable BABELI_MODEL_PROVIDER. Skipping execution.");
            return;
        }

        new UpdateService(configuration).update();
    }

    public static void add(String bundleName, String key, Map<String, String> translations, Configuration configuration) {
        new AddService(findRelevantConfiguration(bundleName, configuration)).add(key, translations);
    }

    private static Configuration findRelevantConfiguration(String bundleName, Configuration configuration) {
        var configurations =
                configuration
                        .autoConfigure();

        if (configurations.size() == 1) {
            return configurations.getFirst();
        }

        if (bundleName == null) {
            throw new MultipleResourceBundlesFoundException(configurations);
        }

        return configurations.stream().filter(c -> c.getName().equals(bundleName)).findFirst().orElseThrow(() -> new ResourceBundleNotFoundException(bundleName));
    }

    private static boolean skip(Configuration configuration) {
        return (Boolean.TRUE
                .toString()
                .equals(EnvUtils.get(BABELI_SKIP, Boolean.toString(configuration.isSkip()))));
    }
}
