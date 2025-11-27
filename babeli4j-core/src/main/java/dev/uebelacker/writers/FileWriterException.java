package dev.uebelacker.writers;

import java.io.File;

public class FileWriterException extends RuntimeException {
    public FileWriterException(File file, Throwable cause) {
        super("Error writing file: " + file.getAbsolutePath(), cause);
    }
}
