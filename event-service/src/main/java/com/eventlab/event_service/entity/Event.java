package com.eventlab.event_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "events")

public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String description;
    private LocalDateTime event_date;
    private String location;
    private Integer max_seats;

    public Event() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEvent_date() {
        return event_date;
    }

    public void setEvent_date(LocalDateTime event_date) {
        this.event_date = event_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMax_seats() {
        return max_seats;
    }

    public void setMax_seats(Integer max_seats) {
        this.max_seats = max_seats;
    }

    





}
