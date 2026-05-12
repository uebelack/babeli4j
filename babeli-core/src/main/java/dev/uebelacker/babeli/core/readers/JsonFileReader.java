package dev.uebelacker.babeli.core.readers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class JsonFileReader implements FileReader {
  private final Configuration configuration;

  public JsonFileReader(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public SingleLanguageTranslationFile readFile(String language, File file) {
    try {
      var gson = new Gson();
      var type = new TypeToken<Map<String, String>>() {}.getType();
      Map<String, String> map =
          gson.fromJson(
              new InputStreamReader(new FileInputStream(file), configuration.getCharset()), type);
      return SingleLanguageTranslationFile.fromMap(language, file, map);
    } catch (Exception e) {
      throw new FileReaderException(file, e);
    }
  }

  @Override
  public MultiLanguageTranslationFile readFile(File file) {
    try {
      var gson = new Gson();
      var type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
      Map<String, Map<String, String>> keyLanguageMap =
          gson.fromJson(
              new InputStreamReader(new FileInputStream(file), configuration.getCharset()), type);

      return new MultiLanguageTranslationFile(
          file, Translations.fromKeyLanguageMap(keyLanguageMap).getTranslationsMapForKey());
    } catch (Exception e) {
      throw new FileReaderException(file, e);
    }
  }
}
