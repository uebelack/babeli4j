package dev.uebelacker.babeli.core.readers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import java.io.File;
import java.util.Map;

public class JsonFileReader implements FileReader {
  @Override
  public String extension() {
    return "json";
  }

  @Override
  public SingleLanguageTranslationFile readFile(String language, File file) {
    try {
      var gson = new Gson();
      var type = new TypeToken<Map<String, String>>() {}.getType();
      Map<String, String> map = gson.fromJson(new java.io.FileReader(file), type);
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
          gson.fromJson(new java.io.FileReader(file), type);
      return MultiLanguageTranslationFile.fromKeyLanguageMap(file, keyLanguageMap);
    } catch (Exception e) {
      throw new FileReaderException(file, e);
    }
  }
}
