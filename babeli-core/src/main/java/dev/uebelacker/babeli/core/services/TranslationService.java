package dev.uebelacker.babeli.core.services;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.input.PromptTemplate;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.ChatModelFactory;
import dev.uebelacker.babeli.core.model.Translations;
import java.util.Map;

public class TranslationService {

  private final Configuration configuration;
  private final Translations translations;

  public TranslationService(Configuration configuration, Translations translations) {
    this.configuration = configuration;
    this.translations = translations;
  }

  public String translate(String value, String sourceLanguage, String targetLanguage) {
    var chatModel = ChatModelFactory.createChatModel(configuration);

    var prompt =
        PromptTemplate.from(
"""
You are a professional translation assistant. Your task is to translate text from {{sourceLanguage}} to {{targetLanguage}}.

Guidelines:
- Produce accurate, natural-sounding translations that a native speaker would write
- Preserve the tone, register, and style of the original (formal, casual, technical, literary, etc.)
- Maintain the original formatting, including line breaks, lists, and punctuation
- Keep proper nouns, brand names, and technical terms in their original form unless a well-established translation exists
- For idioms and culturally specific expressions, use the closest natural equivalent in the target language rather than a literal translation
- When a term is ambiguous, choose the meaning that best fits the surrounding context
- Do not add explanations, commentary, or notes unless explicitly asked
- Do not translate code blocks, URLs, email addresses, or file paths
- If the input contains placeholders (e.g., {variable}, %s, ${name}), preserve them exactly

Output only the best translation, never multiple alternatives.
Do not include the original text in the output.
Output only the translated text, nothing else.
""")
            .apply(Map.of("sourceLanguage", sourceLanguage, "targetLanguage", targetLanguage));

    var response = chatModel.chat(prompt.toSystemMessage(), new UserMessage(value));

    return response.aiMessage().text();
  }
}
