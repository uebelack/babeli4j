package dev.uebelacker.babeli.core.services;

import dev.langchain4j.model.chat.ChatModel;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.ModelProvider;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

public class ModelService {
  private ModelProvider modelProvider;

  public ModelService(Configuration configuration) {
    var modelProviderName = configuration.getModelProvider();
    var modelProviderClass =
        modelProviderName.contains(".")
            ? modelProviderName
            : "dev.uebelacker.ai." + StringUtils.capitalize(modelProviderName) + "ModelProvider";
    try {
      this.modelProvider =
          (ModelProvider) Class.forName(modelProviderClass).getConstructor().newInstance();
    } catch (ClassNotFoundException e) {
      throw new ConfigurationException("Model provider class not found: " + modelProviderClass, e);
    } catch (NoSuchMethodException e) {
      throw new ConfigurationException(
          "Model provider class does not have a default constructor: " + modelProviderClass, e);
    } catch (Exception e) {
      throw new ConfigurationException(
          "Failed to instantiate model provider: " + modelProviderClass, e);
    }
  }

  public ChatModel getChatModel() {
    return modelProvider.getChatModel();
  }
}
