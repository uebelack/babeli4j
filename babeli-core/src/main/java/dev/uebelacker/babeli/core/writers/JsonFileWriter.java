package dev.uebelacker.babeli.core.writers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import dev.uebelacker.babeli.core.model.TranslationFile;

public class JsonFileWriter implements FileWriter {

    @Override
    public String extension() {
        return "json";
    }

    @Override
    public void writeFile(TranslationFile file) {

        if (!file.file().getParentFile().exists()) {
            file.file().getParentFile().mkdirs();
        }

        try (var writer = new java.io.FileWriter(file.file())) {
            var jsonObject = new JsonObject();
            file.translations().forEach(translation -> jsonObject.addProperty(translation.key(), translation.value()));
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, writer);
        } catch (Exception e) {
            throw new FileWriterException(file.file(), e);
        }

    }
}
