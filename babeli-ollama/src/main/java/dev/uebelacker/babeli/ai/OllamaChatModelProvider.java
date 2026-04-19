package dev.uebelacker.babeli.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.uebelacker.babeli.core.ai.ChatModelProvider;
import dev.uebelacker.babeli.core.exceptions.ConfigurationException;
import dev.uebelacker.babeli.core.util.EnvUtils;

public class OllamaChatModelProvider implements ChatModelProvider {
  @Override
  public ChatModel create() {
    var model = EnvUtils.get("BABELI_OLLAMA_MODEL");

    if (model == null) {
      throw new ConfigurationException(
          "Missing required environment variable: BABELI_OLLAMA_MODEL");
    }

    var url = EnvUtils.get("BABELI_OLLAMA_URL", "http://localhost:11434");
    return OllamaChatModel.builder().modelName(model).baseUrl(url).build();
  }
}
