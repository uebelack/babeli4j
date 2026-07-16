package dev.uebelacker.babeli.core.services;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.input.PromptTemplate;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.ai.AiFactory;
import dev.uebelacker.babeli.core.model.Translation;
import dev.uebelacker.babeli.core.model.Translations;

public class TranslationService {

    private static final String SOURCE_LANGUAGE = "sourceLanguage";
    private static final String TARGET_LANGUAGE = "targetLanguage";
    private static final String SIMILAR_TRANSLATIONS = "similarTranslations";

    private final Configuration configuration;
    private final Translations translations;

    public TranslationService(Configuration configuration, Translations translations) {
        this.configuration = configuration;
        this.translations = translations;
    }

    private Map<String, String> findSimilarTranslations(
            String text, String sourceLanguage, String targetLanguage) {

        var similarity = new JaroWinklerSimilarity();
        var candidates =
                translations.getTranslationsForLanguage(sourceLanguage).stream()
                        .map(Translation::value)
                        .sorted(Comparator.comparingDouble((String s) -> similarity.apply(text, s)).reversed())
                        .limit(26)
                        .toList();

        var result = new LinkedHashMap<String, String>();
        for (var candidate : candidates) {
            var translation =
                    translations.getTranslationForValue(candidate, sourceLanguage, targetLanguage);
            if (translation != null) {
                result.put(candidate, translation);
            }
        }

        return result;
    }

    public String translate(String value, String sourceLanguage, String targetLanguage) {

        var existingTranslation =
                translations.getTranslationForValue(value, sourceLanguage, targetLanguage);

        if (existingTranslation != null) {
            return existingTranslation;
        }

        var similarTranslations = findSimilarTranslations(value, sourceLanguage, targetLanguage);
        var similarTranslationsString =
                !similarTranslations.isEmpty()
                        ? "\nHere are some other translations as a reference, please try to reuse the terms of this translations if any match the text to be translated."
                          + similarTranslations.entrySet().stream()
                        .map(entry -> "- %s -> %s".formatted(entry.getKey(), entry.getValue()))
                        .reduce((a, b) -> a + "\n" + b)
                        .orElse("")
                          + "\n"
                        : "";

        var prompt =
                PromptTemplate.from(
                                """
                                        You are a professional translation assistant. Your task is to translate text from {{sourceLanguage}} to {{targetLanguage}}.
                                        {{similarTranslations}}
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
                        .apply(
                                Map.of(
                                        SOURCE_LANGUAGE,
                                        sourceLanguage,
                                        TARGET_LANGUAGE,
                                        targetLanguage,
                                        SIMILAR_TRANSLATIONS,
                                        similarTranslationsString));

        var chatModel = AiFactory.createChatModel(configuration);
        var response = chatModel.chat(prompt.toSystemMessage(), new UserMessage(value));

        return response.aiMessage().text();
    }
}
