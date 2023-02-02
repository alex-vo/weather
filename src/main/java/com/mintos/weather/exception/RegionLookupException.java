package com.mintos.weather.exception;

public class RegionLookupException extends RuntimeException {

    public RegionLookupException(String message) {
        super(message);
    }

    public RegionLookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
