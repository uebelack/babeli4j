package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesFileWriter implements FileWriter {

  private static final Pattern KEY_VALUE_PATTERN =
      Pattern.compile("^((?:[\\\\].|[^=:\\s])+)\\s*([=:\\s])\\s*(.*)$");

  private final Configuration configuration;

  public PropertiesFileWriter(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void writeFile(SingleLanguageTranslationFile file) {
    ensureDirectory(file.file());

    Map<String, String> translationMap = new LinkedHashMap<>();
    for (Translation translation : file.translations()) {
      translationMap.put(translation.key(), translation.value());
    }

    try {
      List<String> outputLines;
      if (file.file().exists()) {
        outputLines = mergeWithExisting(file.file(), translationMap);
      } else {
        outputLines = buildNew(translationMap);
      }

      try (var writer =
          new BufferedWriter(
              new OutputStreamWriter(
                  new FileOutputStream(file.file()),
                  Charset.forName(configuration.getCharset())))) {
        for (int i = 0; i < outputLines.size(); i++) {
          writer.write(outputLines.get(i));
          if (i < outputLines.size() - 1) {
            writer.newLine();
          }
        }
      }
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }
  }

  private List<String> mergeWithExisting(
      java.io.File existingFile, Map<String, String> translationMap) throws Exception {
    var charset = Charset.forName(configuration.getCharset());
    var originalLines = Files.readAllLines(existingFile.toPath(), charset);
    var outputLines = new ArrayList<String>();
    var writtenKeys = new java.util.LinkedHashSet<String>();

    for (String line : originalLines) {
      String trimmed = line.trim();
      if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
        outputLines.add(line);
        continue;
      }

      Matcher matcher = KEY_VALUE_PATTERN.matcher(trimmed);
      if (matcher.matches()) {
        String key = unescapeKey(matcher.group(1));
        String separator = matcher.group(2);
        if (translationMap.containsKey(key)) {
          writtenKeys.add(key);
          String newValue = escapeValue(translationMap.get(key));
          String leadingWhitespace =
              line.substring(0, line.length() - line.stripLeading().length());
          outputLines.add(leadingWhitespace + escapeKey(key) + separator + newValue);
        } else {
          outputLines.add(line);
        }
      } else {
        outputLines.add(line);
      }
    }

    for (Map.Entry<String, String> entry : translationMap.entrySet()) {
      if (!writtenKeys.contains(entry.getKey())) {
        outputLines.add(escapeKey(entry.getKey()) + "=" + escapeValue(entry.getValue()));
      }
    }

    return outputLines;
  }

  private List<String> buildNew(Map<String, String> translationMap) {
    var lines = new ArrayList<String>();
    for (Map.Entry<String, String> entry : translationMap.entrySet()) {
      lines.add(escapeKey(entry.getKey()) + "=" + escapeValue(entry.getValue()));
    }
    return lines;
  }

  private static String unescapeKey(String key) {
    return key.replace("\\ ", " ").replace("\\=", "=").replace("\\:", ":");
  }

  private static String escapeKey(String key) {
    return key.replace(" ", "\\ ").replace("=", "\\=").replace(":", "\\:");
  }

  private static String escapeValue(String value) {
    return value;
  }

  @Override
  public void writeFile(dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile file) {
    throw new UnsupportedOperationException(
        "Multi-language translation files are not supported for properties files.");
  }
}
