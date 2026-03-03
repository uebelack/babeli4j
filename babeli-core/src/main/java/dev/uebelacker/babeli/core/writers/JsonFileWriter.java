package dev.uebelacker.babeli.core.writers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;

public class JsonFileWriter implements FileWriter {

  @Override
  public String extension() {
    return "json";
  }

  @Override
  public void writeFile(SingleLanguageTranslationFile file) {
    if (!file.file().getParentFile().exists()) {
      file.file().getParentFile().mkdirs();
    }

    try (var writer = new java.io.FileWriter(file.file())) {
      var jsonObject = new JsonObject();
      file.translations()
          .forEach(translation -> jsonObject.addProperty(translation.key(), translation.value()));
      new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, writer);
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }
  }

  @Override
  public void writeFile(MultiLanguageTranslationFile file) {
    if (!file.file().getParentFile().exists()) {
      file.file().getParentFile().mkdirs();
    }

    try (var writer = new java.io.FileWriter(file.file())) {
      new GsonBuilder().setPrettyPrinting().create().toJson(file.toKeyLanguageMap(), writer);
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }
  }
}
