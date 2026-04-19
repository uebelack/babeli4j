package dev.uebelacker.babeli.core.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;
import org.apache.commons.lang3.StringUtils;

public class ChatModelFactory {
  private static ChatModel chatModel;

  private ChatModelFactory() {}

  public static ChatModel createChatModel(Configuration configuration) {
    if (chatModel == null) {
      var modelProviderName =
          EnvUtils.get("BABELI_MODEL_PROVIDER", configuration.getModelProvider());

      if (modelProviderName == null) {
        throw new ConfigurationException(
            "No model provider specified in the configuration. Please specify a model provider using 'modelProvider'.");
      }

      var modelProviderClass =
          modelProviderName.contains(".")
              ? modelProviderName
              : "dev.uebelacker.babeli.ai."
                  + StringUtils.capitalize(modelProviderName)
                  + "ChatModelProvider";
      try {
        chatModel =
            ((ChatModelProvider) Class.forName(modelProviderClass).getConstructor().newInstance())
                .create();
      } catch (ClassNotFoundException e) {
        throw new ConfigurationException(
            "Model provider class not found: " + modelProviderClass, e);
      } catch (NoSuchMethodException e) {
        throw new ConfigurationException(
            "Model provider class does not have a default constructor: " + modelProviderClass, e);
      } catch (Exception e) {
        throw new ConfigurationException(
            "Failed to instantiate model provider: " + modelProviderClass, e);
      }
    }

    return chatModel;
  }
}
