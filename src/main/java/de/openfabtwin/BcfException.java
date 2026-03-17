package de.openfabtwin;

import lombok.Getter;

@Getter
public class BcfException extends RuntimeException {

    private final int statusCode;

    public BcfException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public BcfException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}