package com.datagenio.crawler.exception;

public class FileTypeException extends Exception {

    public FileTypeException(String message) {
        super(message);
    }

    public FileTypeException(String message, Throwable e) {
        super(message, e);
    }

    public FileTypeException(Throwable e) {
        super(e);
    }
}
