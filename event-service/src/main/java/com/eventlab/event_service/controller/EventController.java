package com.eventlab.event_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventlab.event_service.entity.Event;
import com.eventlab.event_service.service.EventService;


@RestController 
@RequestMapping("/api/events")
public class EventController {
    
    public final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("")
    List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    Event getEventById(@PathVariable Long id) {
        return eventService.getById(id);
    }
    
    @PostMapping("/add")
    Event addEvent(@RequestBody Event eventToAdd) {      
        return eventService.add(eventToAdd);
    }

    @PostMapping("/{id}")
    Event updateEvent(@PathVariable Long id, @RequestBody Event eventToUpdate) {       
        return eventService.update(id, eventToUpdate);
    }
            
}
