package dev.uebelacker.babeli.core;

public class BabeliContextTestFactory {
  public static BabeliContext createBabeliContext() {
    return createBabeliContext(new Configuration());
  }

  public static BabeliContext createBabeliContext(Configuration configuration) {
    return new BabeliContext(
        configuration, new TestTranslationService(), new TestGlossaryService());
  }
}
