package de.openfabtwin;

import lombok.Getter;

@Getter
public class BcfApiException extends RuntimeException {

    private final int statusCode;

    /** Logic error — no HTTP status involved */
    public BcfApiException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public BcfApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}