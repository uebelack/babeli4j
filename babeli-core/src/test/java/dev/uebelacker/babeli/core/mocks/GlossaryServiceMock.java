package dev.uebelacker.babeli.core.mocks;

import dev.uebelacker.babeli.core.model.GlossaryEntry;
import dev.uebelacker.babeli.core.services.GlossaryService;
import java.util.List;
import java.util.Map;
import org.mockito.Mockito;

public class GlossaryServiceMock implements GlossaryService {
  private static GlossaryService mock;

  public GlossaryServiceMock() {
    mock = Mockito.mock(GlossaryService.class);
  }

  public static GlossaryService getMock() {
    return mock;
  }

  @Override
  public void updateWith(String key, Map<String, String> languageMap) {
    new GlossaryEntry(key, languageMap, "description");
    mock.updateWith(key, languageMap);
  }

  @Override
  public List<GlossaryEntry> findRelevantEntries(String term, String language, int maxResults) {
    return mock.findRelevantEntries(term, language, maxResults);
  }
}
