package com.ncba.countryservice.exception;

public class DuplicateCountryException extends RuntimeException {
    public DuplicateCountryException(String message) {
        super(message);
    }
}
