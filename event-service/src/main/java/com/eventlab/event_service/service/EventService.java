package com.eventlab.event_service.service;

import com.eventlab.event_service.dto.EventDTO;
import com.eventlab.event_service.entity.Event;
import com.eventlab.event_service.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public EventDTO getEventById(Long id) {
        Event event = findEventOrThrow(id);
        return toDTO(event);
    }

    public EventDTO createEvent(EventDTO dto) {
        validateEventDTO(dto);
        Event entity = toEntity(dto);
        Event saved = eventRepository.save(entity);
        return toDTO(saved);
    }

    public EventDTO updateEvent(Long id, EventDTO dto) {
        Event existing = findEventOrThrow(id);
        validateEventDTO(dto);

        existing.setTitle(dto.getTitle().trim());
        existing.setDescription(normalizeDescription(dto.getDescription()));
        existing.setEventDate(dto.getEventDate());
        existing.setLocation(dto.getLocation().trim());
        existing.setMaxSeats(dto.getMaxSeats());

        Event updated = eventRepository.save(existing);
        return toDTO(updated);
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Evento con id " + id + " non trovato"
                ));
    }

    private void validateEventDTO(EventDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dati evento mancanti");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il titolo è obbligatorio");
        }
        if (dto.getTitle().trim().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il titolo non può superare i 100 caratteri");
        }
        if (dto.getEventDate() == null || !dto.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La data dell'evento deve essere futura");
        }
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il luogo è obbligatorio");
        }
        if (dto.getMaxSeats() == null || dto.getMaxSeats() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La capienza massima deve essere maggiore di zero");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private EventDTO toDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setMaxSeats(event.getMaxSeats());
        return dto;
    }

    private Event toEntity(EventDTO dto) {
        Event event = new Event();
        event.setTitle(dto.getTitle().trim());
        event.setDescription(normalizeDescription(dto.getDescription()));
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation().trim());
        event.setMaxSeats(dto.getMaxSeats());
        return event;
    }
}