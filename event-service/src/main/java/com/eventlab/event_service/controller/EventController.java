package com.eventlab.event_service.controller;

import com.eventlab.event_service.dto.EventDTO;
import com.eventlab.event_service.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO dto) {
        EventDTO created = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody EventDTO dto) {
        EventDTO updated = eventService.updateEvent(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<EventDTO> reserveSeat(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.reserveSeat(id));
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<EventDTO> releaseSeat(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.releaseSeat(id));
    }
}