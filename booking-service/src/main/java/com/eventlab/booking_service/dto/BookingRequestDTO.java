package com.eventlab.booking_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookingRequestDTO {

    @NotNull(message = "L'ID dell'evento è obbligatorio")
    private Long eventId;

    @NotBlank(message = "Il nome del partecipante è obbligatorio")
    @Size(max = 100, message = "Il nome non può superare i 100 caratteri")
    private String participantName;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    @Size(max = 150, message = "L'email non può superare i 150 caratteri")
    private String participantEmail;

    public BookingRequestDTO() {
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
}