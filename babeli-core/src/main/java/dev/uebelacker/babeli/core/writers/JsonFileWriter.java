package dev.uebelacker.babeli.core.writers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class JsonFileWriter implements FileWriter {

  private final Configuration configuration;

  public JsonFileWriter(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void writeFile(SingleLanguageTranslationFile file) {
    ensureDirectory(file.file());

    try (var outputStream = new FileOutputStream(file.file())) {
      var writer = new OutputStreamWriter(outputStream, configuration.getCharset());
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
    ensureDirectory(file.file());

    var keyLanguageMap = Translations.fromTranslations(file.translations()).getKeyLanguageMap();

    try (var writer = new java.io.FileWriter(file.file())) {
      new GsonBuilder().setPrettyPrinting().create().toJson(keyLanguageMap, writer);
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }
  }
}
