package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlFileWriter implements FileWriter {
  @Override
  public void writeFile(SingleLanguageTranslationFile file) {
    ensureDirectory(file.file());

    try {
      var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      var resources = document.createElement("resources");
      document.appendChild(resources);

      for (var translation : file.translations()) {
        var element = document.createElement("string");
        element.setAttribute("name", translation.key());
        element.setTextContent(translation.value());
        resources.appendChild(element);
      }

      writeDocument(document, file.file());
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }
  }

  @Override
  public void writeFile(MultiLanguageTranslationFile file) {
    ensureDirectory(file.file());

    try {
      var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      var resources = document.createElement("resources");
      document.appendChild(resources);

      var translations = Translations.fromTranslations(file.translations());

      for (var key : translations.getKeys()) {
        var stringElement = document.createElement("string");
        stringElement.setAttribute("name", key);

        translations
            .getTranslations(key)
            .forEach(
                (language, value) -> {
                  var languageElement = document.createElement("language");
                  languageElement.setAttribute("code", language);
                  languageElement.setTextContent(value);
                  stringElement.appendChild(languageElement);
                });

        resources.appendChild(stringElement);
      }

      writeDocument(document, file.file());
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }
  }

  private void writeDocument(org.w3c.dom.Document document, java.io.File file) throws Exception {
    var transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.transform(new DOMSource(document), new StreamResult(file));
  }
}
