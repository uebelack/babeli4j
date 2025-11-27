package dev.uebelacker.readers;

import java.io.File;

public class FileReaderException extends RuntimeException {
    public FileReaderException(File file, Throwable cause) {
        super("Error reading file: " + file.getAbsolutePath(), cause);
    }
}
