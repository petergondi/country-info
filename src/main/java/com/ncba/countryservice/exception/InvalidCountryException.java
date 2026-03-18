package com.ncba.countryservice.exception;

public class InvalidCountryException extends RuntimeException {
    public InvalidCountryException(String message) {
        super(message);
    }
}
