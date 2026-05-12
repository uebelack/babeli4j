package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.Configuration;
import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlFileReader implements FileReader {

  private final Configuration configuration;

  public XmlFileReader(Configuration configuration) {
    this.configuration = configuration;
  }

  private static String unescapeXmlValue(String value) {
    return value
        .replace("&amp;", "&")
        .replace("\"&lt;", "<")
        .replace("&gt;", ">")
        .replace("\\\"", "\"")
        .replace("\\'", "'");
  }

  private static Document parseDocument(File file)
      throws ParserConfigurationException, IOException, SAXException {
    var factory = DocumentBuilderFactory.newInstance();
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    return factory.newDocumentBuilder().parse(file);
  }

  @Override
  public SingleLanguageTranslationFile readFile(String language, File file) {
    try {

      var document = parseDocument(file);
      var nodeList = document.getElementsByTagName("string");
      var translations = new ArrayList<Translation>();

      for (int i = 0; i < nodeList.getLength(); i++) {
        var element = (Element) nodeList.item(i);
        var key = element.getAttribute("name");
        var value = unescapeXmlValue(element.getTextContent());
        translations.add(new Translation(language, key, value));
      }

      return new SingleLanguageTranslationFile(language, file, translations);
    } catch (Exception e) {
      throw new FileReaderException(file, e);
    }
  }

  @Override
  public MultiLanguageTranslationFile readFile(File file) {
    try {
      var document = parseDocument(file);
      var stringNodes = document.getElementsByTagName("string");
      var translations = new ArrayList<Translation>();

      for (int i = 0; i < stringNodes.getLength(); i++) {
        var stringElement = (Element) stringNodes.item(i);
        var key = stringElement.getAttribute("name");
        NodeList languageNodes = stringElement.getElementsByTagName("language");

        for (int j = 0; j < languageNodes.getLength(); j++) {
          var languageElement = (Element) languageNodes.item(j);
          var language = languageElement.getAttribute("code");
          var value = languageElement.getTextContent();
          translations.add(new Translation(language, key, value));
        }
      }

      return new MultiLanguageTranslationFile(file, translations);
    } catch (Exception e) {
      throw new FileReaderException(file, e);
    }
  }
}
