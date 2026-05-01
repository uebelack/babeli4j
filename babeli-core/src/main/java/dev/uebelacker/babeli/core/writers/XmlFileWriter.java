package dev.uebelacker.babeli.core.writers;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translations;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class XmlFileWriter implements FileWriter {
  private static String escapeXmlValue(String value) {
    return value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "\\\"")
        .replace("'", "\\'");
  }

  private static Document createNewDocument() throws ParserConfigurationException {
    var factory = DocumentBuilderFactory.newInstance();
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    return factory.newDocumentBuilder().newDocument();
  }

  private static void writeDocument(org.w3c.dom.Document document, java.io.File file)
      throws TransformerException {
    var factory = TransformerFactory.newInstance();
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

    var transformer = factory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.transform(new DOMSource(document), new StreamResult(file));
  }

  @Override
  public void writeFile(SingleLanguageTranslationFile file) {
    ensureDirectory(file.file());

    try {
      var document = createNewDocument();
      var resources = document.createElement("resources");
      document.appendChild(resources);

      for (var translation : file.translations()) {
        var element = document.createElement("string");
        element.setAttribute("name", translation.key());
        element.setTextContent(escapeXmlValue(translation.value()));
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
      var document = createNewDocument();
      var resources = document.createElement("resources");
      document.appendChild(resources);

      var translations = Translations.fromTranslations(file.translations());

      for (var key : translations.getKeys()) {
        var stringElement = document.createElement("string");
        stringElement.setAttribute("name", key);

        translations
            .getTranslationsMapForKey(key)
            .forEach(
                (language, value) -> {
                  var languageElement = document.createElement("language");
                  languageElement.setAttribute("code", language);
                  languageElement.setTextContent(escapeXmlValue(value));
                  stringElement.appendChild(languageElement);
                });

        resources.appendChild(stringElement);
      }

      writeDocument(document, file.file());
    } catch (Exception e) {
      throw new FileWriterException(file.file(), e);
    }
  }
}
