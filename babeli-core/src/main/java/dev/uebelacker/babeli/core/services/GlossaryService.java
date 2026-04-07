package dev.uebelacker.babeli.core.services;

import dev.uebelacker.babeli.core.model.GlossaryEntry;
import java.util.List;
import java.util.Map;

public interface GlossaryService {

  void updateWith(String key, Map<String, String> languageMap);

  List<GlossaryEntry> findRelevantEntries(String term, String language, int maxResults);
}
