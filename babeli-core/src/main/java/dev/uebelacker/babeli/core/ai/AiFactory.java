package dev.uebelacker.babeli.core.ai;

import static dev.uebelacker.babeli.core.Constants.EnvironmentVariables.BABELI_MODEL_PROVIDER;

import org.apache.commons.lang3.StringUtils;

import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;

public class AiFactory {
    private static ChatModel chatModel;

    private AiFactory() {
    }

    public static ChatModel createChatModel(Configuration configuration) {
        if (chatModel == null) {
            var modelProviderName = EnvUtils.get(BABELI_MODEL_PROVIDER, configuration.getModelProvider());
            var modelProviderClass =
                    modelProviderName.contains(".")
                            ? modelProviderName
                            : "dev.uebelacker.babeli.ai."
                              + StringUtils.capitalize(modelProviderName)
                              + "AiProvider";
            try {

                chatModel =
                        ((AiProvider)
                                Thread.currentThread()
                                        .getContextClassLoader()
                                        .loadClass(modelProviderClass)
                                        .getConstructor()
                                        .newInstance())
                                .create(configuration);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException(
                        "Model provider class not found: " + modelProviderClass, e);
            } catch (NoSuchMethodException e) {
                throw new ConfigurationException(
                        "Model provider class does not have a default constructor: " + modelProviderClass, e);
            } catch (Exception e) {
                throw new ConfigurationException(
                        "Failed to instantiate model provider " + modelProviderClass + ": " + e.getMessage(),
                        e);
            }
        }

        return chatModel;
    }
}
