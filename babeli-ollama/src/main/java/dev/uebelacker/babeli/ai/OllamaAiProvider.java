package dev.uebelacker.babeli.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.AiProvider;
import dev.uebelacker.babeli.core.util.EnvUtils;

public class OllamaAiProvider implements AiProvider {
    @Override
    public ChatModel create(Configuration configuration) {
        var model = EnvUtils.get("BABELI_MODEL", configuration.getModel(), "qwen3.5");
        var url = EnvUtils.get("BABELI_API_URL", configuration.getApiUrl(), "http://localhost:11434");

        return OllamaChatModel.builder().modelName(model).baseUrl(url).build();
    }
}
