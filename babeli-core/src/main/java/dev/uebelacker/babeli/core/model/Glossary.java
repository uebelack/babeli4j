package dev.uebelacker.babeli.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Glossary {
  private Set<String> indexedKeys;
  private List<GlossaryEntry> entries;

  public Glossary() {
    this.indexedKeys = new HashSet<>();
    this.entries = new ArrayList<>();
  }

  public Set<String> getIndexedKeys() {
    return indexedKeys;
  }

  public void setIndexedKeys(Set<String> indexedKeys) {
    this.indexedKeys = indexedKeys;
  }

  public List<GlossaryEntry> getEntries() {
    return entries;
  }

  public void setEntries(List<GlossaryEntry> entries) {
    this.entries = entries;
  }

  public void addEntry(GlossaryEntry entry) {
    this.entries.add(entry);
  }
}
