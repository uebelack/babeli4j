package dev.uebelacker.babeli.core.readers;

import dev.uebelacker.babeli.core.model.MultiLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.SingleLanguageTranslationFile;
import dev.uebelacker.babeli.core.model.Translation;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlFileReader implements FileReader {
  @Override
  public SingleLanguageTranslationFile readFile(String language, File file) {
    try {
      var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
      var nodeList = document.getElementsByTagName("string");
      var translations = new ArrayList<Translation>();

      for (int i = 0; i < nodeList.getLength(); i++) {
        var element = (Element) nodeList.item(i);
        var key = element.getAttribute("name");
        var value = element.getTextContent();
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
      var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
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
