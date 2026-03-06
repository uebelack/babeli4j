package dev.uebelacker.babeli.core.readers;

import com.google.gson.Gson;
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

    return null;
  }

  @Override
  public MultiLanguageTranslationFile readFile(File file) {

    try {
      var gson = new Gson();
      var keyLanguageMap = gson.fromJson(new java.io.FileReader(file), Map.class);
      return MultiLanguageTranslationFile.fromKeyLanguageMap(file, keyLanguageMap);
    } catch (Exception e) {
      throw new FileReaderException(file, e);
    }
  }
}
