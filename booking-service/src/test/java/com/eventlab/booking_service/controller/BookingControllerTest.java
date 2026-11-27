package com.eventlab.booking_service.controller;

import com.eventlab.booking_service.dto.BookingResponseDTO;
import com.eventlab.booking_service.exception.BookingConflictException;
import com.eventlab.booking_service.exception.BookingNotFoundException;
import com.eventlab.booking_service.exception.EventNotFoundException;
import com.eventlab.booking_service.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Test
    @DisplayName("POST /api/bookings - Restituisce 201 Created con payload valido")
    void createBooking_Returns201() throws Exception {
        BookingResponseDTO response = new BookingResponseDTO(
                1L, 1L, "Giuseppe Berardi", "giuseppe@test.it", LocalDateTime.now()
        );
        when(bookingService.createBooking(any())).thenReturn(response);

        String requestBody = """
                {
                  "eventId": 1,
                  "participantName": "Giuseppe Berardi",
                  "participantEmail": "giuseppe@test.it"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.participantEmail").value("giuseppe@test.it"));
    }

    @Test
    @DisplayName("POST /api/bookings - Restituisce 400 Bad Request se la validazione fallisce")
    void createBooking_InvalidPayload_Returns400() throws Exception {
        String invalidBody = """
                {
                  "eventId": null,
                  "participantName": "",
                  "participantEmail": "email-non-valida"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bookings - Restituisce 409 Conflict se posti esauriti o email duplicata")
    void createBooking_Conflict_Returns409() throws Exception {
        when(bookingService.createBooking(any()))
                .thenThrow(new BookingConflictException("Posti esauriti per questo evento"));

        String requestBody = """
                {
                  "eventId": 1,
                  "participantName": "Giuseppe Berardi",
                  "participantEmail": "giuseppe@test.it"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/bookings - Restituisce 404 Not Found se evento inesistente")
    void createBooking_EventNotFound_Returns404() throws Exception {
        when(bookingService.createBooking(any()))
                .thenThrow(new EventNotFoundException("Evento non trovato"));

        String requestBody = """
                {
                  "eventId": 999,
                  "participantName": "Giuseppe Berardi",
                  "participantEmail": "giuseppe@test.it"
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/bookings - Restituisce 200 OK e lista prenotazioni")
    void getAllBookings_Returns200() throws Exception {
        BookingResponseDTO response = new BookingResponseDTO(
                1L, 1L, "Giuseppe Berardi", "giuseppe@test.it", LocalDateTime.now()
        );
        when(bookingService.getAllBookings()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("DELETE /api/bookings/{id} - Restituisce 204 No Content se cancellata")
    void deleteBooking_Returns204() throws Exception {
        doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/bookings/{id} - Restituisce 404 Not Found se ID inesistente")
    void deleteBooking_NotFound_Returns404() throws Exception {
        doThrow(new BookingNotFoundException("Prenotazione non trovata"))
                .when(bookingService).deleteBooking(999L);

        mockMvc.perform(delete("/api/bookings/999"))
                .andExpect(status().isNotFound());
    }
}