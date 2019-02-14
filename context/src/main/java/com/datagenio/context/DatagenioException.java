package com.datagenio.context;

public class DatagenioException extends Exception {

    public DatagenioException(String message) {
        super(message);
    }

    public DatagenioException(Throwable e) {
        super(e);
    }

    public DatagenioException(String message, Throwable e) {
        super(message, e);
    }
}
