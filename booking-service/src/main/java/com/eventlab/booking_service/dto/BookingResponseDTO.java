package com.eventlab.booking_service.dto;

import java.time.LocalDateTime;

public class BookingResponseDTO {

    private Long id;
    private Long eventId;
    private String participantName;
    private String participantEmail;
    private LocalDateTime createdAt;

    public BookingResponseDTO() {
    }

    public BookingResponseDTO(Long id, Long eventId, String participantName, String participantEmail, LocalDateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}