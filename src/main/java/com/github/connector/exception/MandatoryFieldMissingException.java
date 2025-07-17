package com.github.connector.exception;

public class MandatoryFieldMissingException extends RuntimeException{

    public MandatoryFieldMissingException(String msg) {
        super(msg);
    }
}
