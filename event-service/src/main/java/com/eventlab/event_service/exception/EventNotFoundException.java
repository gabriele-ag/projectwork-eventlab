package com.eventlab.event_service.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(Long id) {
        super("Evento non trovato con id: " + id);
    }
}