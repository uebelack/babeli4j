package dev.uebelacker.babeli.core.readers;

import java.io.File;

public class FileReaderException extends RuntimeException {
    public FileReaderException(File file, Throwable cause) {
        super("Error reading file: " + file.getAbsolutePath(), cause);
    }
}
