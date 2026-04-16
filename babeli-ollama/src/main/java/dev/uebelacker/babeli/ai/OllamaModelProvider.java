package dev.uebelacker.babeli.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.uebelacker.babeli.core.ai.ModelProvider;
import dev.uebelacker.babeli.core.util.EnvUtils;

public class OllamaModelProvider implements ModelProvider {
  @Override
  public ChatModel getChatModel() {
    var model = EnvUtils.get("BABELI_OLLAMA_MODEL");
    var url = EnvUtils.get("BABELI_OLLAMA_URL", "http://localhost:11434");

    return OllamaChatModel.builder().modelName(model).baseUrl(url).build();
  }
}
