package com.eventlab.event_service.exception;

public class NoSeatsAvailableException extends RuntimeException {

    public NoSeatsAvailableException(Long id) {
        super("Nessun posto disponibile per l'evento con id: " + id);
    }
}