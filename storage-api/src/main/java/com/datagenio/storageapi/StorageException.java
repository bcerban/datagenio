package com.datagenio.storageapi;

public class StorageException extends Exception {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable e) {
        super(message, e);
    }
}
