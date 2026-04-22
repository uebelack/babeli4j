package dev.uebelacker.babeli.core.services;

import com.google.gson.Gson;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.Glossary;
import dev.uebelacker.babeli.core.model.GlossaryEntry;
import java.io.File;
import java.util.List;
import java.util.Map;

public class GlossaryService {

  private final Configuration configuration;
  private Glossary glossary;

  public GlossaryService(Configuration configuration) {
    this.configuration = configuration;
    init();
  }

  private void init() {}

  public void updateWith(String key, Map<String, String> languageMap) {}

  public List<GlossaryEntry> findRelevantEntries(String term, String language, int maxResults) {
    return List.of();
  }

  private Glossary load(File file) {
    try {
      var gson = new Gson();
      return gson.fromJson(new java.io.FileReader(file), Glossary.class);
    } catch (Exception e) {
      return new Glossary();
    }
  }
}
