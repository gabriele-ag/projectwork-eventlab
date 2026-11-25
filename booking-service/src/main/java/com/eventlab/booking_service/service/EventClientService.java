package com.eventlab.booking_service.service;

import com.eventlab.booking_service.dto.EventDTO;
import com.eventlab.booking_service.exception.EventNotFoundException;
import com.eventlab.booking_service.exception.EventServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
public class EventClientService {

    private final RestClient eventServiceRestClient;

    public EventClientService(RestClient eventServiceRestClient) {
        this.eventServiceRestClient = eventServiceRestClient;
    }

    public EventDTO getEventById(Long eventId) {
        try {
            return eventServiceRestClient.get()
                    .uri("/api/events/{id}", eventId)
                    .retrieve()
                    .body(EventDTO.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EventNotFoundException("Evento con ID " + eventId + " non trovato");
            }
            throw new EventServiceUnavailableException("Errore nella comunicazione con event-service: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new EventServiceUnavailableException("Il servizio eventi (event-service) non è raggiungibile");
        }
    }
}