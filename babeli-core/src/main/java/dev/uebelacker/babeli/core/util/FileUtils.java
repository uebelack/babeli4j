package dev.uebelacker.babeli.core.util;

import java.io.File;

public class FileUtils {
  private FileUtils() {}

  public static String relativePath(File workingDirectory, File file) {
    var absolutePath = file.getAbsolutePath();
    var absolutWorkingDirectory = workingDirectory.getAbsolutePath();

    if (absolutWorkingDirectory.endsWith(".")) {
      absolutWorkingDirectory =
          absolutWorkingDirectory.substring(0, absolutWorkingDirectory.length() - 1);
    }

    if (absolutWorkingDirectory.endsWith("/") || absolutWorkingDirectory.endsWith("\\")) {
      absolutWorkingDirectory =
          absolutWorkingDirectory.substring(0, absolutWorkingDirectory.length() - 1);
    }

    return absolutePath.substring(absolutWorkingDirectory.length() + 1);
  }
}
