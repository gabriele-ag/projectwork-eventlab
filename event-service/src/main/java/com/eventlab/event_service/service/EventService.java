package com.eventlab.event_service.service;

import java.util.List;
import java.time.LocalDateTime;

import com.eventlab.event_service.entity.Event;
import com.eventlab.event_service.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service 
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getById(Long id) {

        Event eventById = eventRepository.findById(id).orElse(null);

        if (eventById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento con Id" + id + "non trovato. Riprovare");
        }

        return eventById;

    }

    public Event add(Event event) {
        validateEvent(event);

        Event eventToSave = new Event();
        eventToSave.setTitle(event.getTitle().trim());
        eventToSave.setDescription(eventDescription(event.getDescription()));
        eventToSave.setEventDate(event.getEventDate());
        eventToSave.setLocation(event.getLocation().trim());
        eventToSave.setMaxSeats(event.getMaxSeats());

        return eventRepository.save(eventToSave);
    }

    public Event update(Long id, Event updatedEvent) {
        validateEvent(updatedEvent);

        Event eventToUpdate = getById(id);
        eventToUpdate.setTitle(updatedEvent.getTitle().trim());
        eventToUpdate.setDescription(eventDescription(updatedEvent.getDescription()));
        eventToUpdate.setEventDate(updatedEvent.getEventDate());
        eventToUpdate.setLocation(updatedEvent.getLocation().trim());
        eventToUpdate.setMaxSeats(updatedEvent.getMaxSeats());

        return eventRepository.save(eventToUpdate);

    }

    public String eventDescription(String description) {
        if (description == null) {
            return null;
        }

        return description.trim().isEmpty() ? null : description.trim();
    }

    private void validateEvent(Event event) {

        // Gestione del null
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dati mancanti dell'evento");
        }

        //validazione su title
        if (event.getTitle() == null || event.getTitle().isBlank() || event.getTitle().trim().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il titolo è obbligatorio e deve essere al massimo di 255 caratteri."); 
        }

        //validazione su location
        if (event.getLocation() == null || event.getLocation().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il luogo è obbligatorio");
        }

        //validazione su event_date
        if (event.getEventDate() == null || !event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La data dell'evento deve essere futura.");
        }

        //validazione su max_seats
        if (event.getMaxSeats() == null || event.getMaxSeats() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La capienza massima deve essere maggiore di zero");
        }

    }
}
