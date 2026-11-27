package com.eventlab.booking_service.service;

import com.eventlab.booking_service.dto.BookingRequestDTO;
import com.eventlab.booking_service.dto.BookingResponseDTO;
import com.eventlab.booking_service.dto.EventDTO;
import com.eventlab.booking_service.entity.Booking;
import com.eventlab.booking_service.exception.BookingConflictException;
import com.eventlab.booking_service.exception.BookingNotFoundException;
import com.eventlab.booking_service.exception.EventNotFoundException;
import com.eventlab.booking_service.exception.EventServiceUnavailableException;
import com.eventlab.booking_service.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventClientService eventClientService;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDTO validRequest;
    private EventDTO availableEvent;

    @BeforeEach
    void setUp() {
        validRequest = new BookingRequestDTO();
        validRequest.setEventId(1L);
        validRequest.setParticipantName("  Giuseppe Berardi  ");
        validRequest.setParticipantEmail("  GIUSEPPE@Test.it  ");

        availableEvent = new EventDTO();
        availableEvent.setId(1L);
        availableEvent.setTitle("Workshop Spring Boot");
        availableEvent.setMaxSeats(10);
    }

    @Test
    @DisplayName("createBooking - Crea e salva la prenotazione normalizzando nome ed email")
    void createBooking_Success() {
        when(eventClientService.getEventById(1L)).thenReturn(availableEvent);
        when(bookingRepository.existsByEventIdAndParticipantEmail(1L, "giuseppe@test.it")).thenReturn(false);
        when(bookingRepository.countByEventId(1L)).thenReturn(3L);

        Booking savedEntity = new Booking(1L, "Giuseppe Berardi", "giuseppe@test.it");
        savedEntity.setId(100L);
        savedEntity.setCreatedAt(LocalDateTime.now());
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedEntity);

        BookingResponseDTO response = bookingService.createBooking(validRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getEventId());
        assertEquals("Giuseppe Berardi", response.getParticipantName());
        assertEquals("giuseppe@test.it", response.getParticipantEmail());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals("Giuseppe Berardi", bookingCaptor.getValue().getParticipantName());
        assertEquals("giuseppe@test.it", bookingCaptor.getValue().getParticipantEmail());
    }

    @Test
    @DisplayName("createBooking - Lancia EventNotFoundException se l'evento non esiste")
    void createBooking_EventNotFound() {
        when(eventClientService.getEventById(1L))
                .thenThrow(new EventNotFoundException("Evento con ID 1 non trovato"));

        assertThrows(EventNotFoundException.class, () -> bookingService.createBooking(validRequest));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBooking - Lancia EventServiceUnavailableException se event-service non risponde")
    void createBooking_EventServiceUnavailable() {
        when(eventClientService.getEventById(1L))
                .thenThrow(new EventServiceUnavailableException("Servizio eventi non disponibile"));

        assertThrows(EventServiceUnavailableException.class, () -> bookingService.createBooking(validRequest));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBooking - Lancia BookingConflictException se l'email è già prenotata")
    void createBooking_DuplicateEmail() {
        when(eventClientService.getEventById(1L)).thenReturn(availableEvent);
        when(bookingRepository.existsByEventIdAndParticipantEmail(1L, "giuseppe@test.it")).thenReturn(true);

        BookingConflictException ex = assertThrows(
                BookingConflictException.class,
                () -> bookingService.createBooking(validRequest)
        );

        assertEquals("Questa email risulta già prenotata per questo evento", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBooking - Lancia BookingConflictException se i posti sono esauriti")
    void createBooking_SoldOut() {
        availableEvent.setMaxSeats(5);
        when(eventClientService.getEventById(1L)).thenReturn(availableEvent);
        when(bookingRepository.existsByEventIdAndParticipantEmail(1L, "giuseppe@test.it")).thenReturn(false);
        when(bookingRepository.countByEventId(1L)).thenReturn(5L);

        BookingConflictException ex = assertThrows(
                BookingConflictException.class,
                () -> bookingService.createBooking(validRequest)
        );

        assertEquals("Posti esauriti per questo evento", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAllBookings - Restituisce la lista mappata di tutte le prenotazioni")
    void getAllBookings_Success() {
        Booking b1 = new Booking(1L, "Giuseppe", "giuseppe@test.it");
        b1.setId(1L);
        Booking b2 = new Booking(2L, "Gabriele", "gabriele@test.it");
        b2.setId(2L);

        when(bookingRepository.findAll()).thenReturn(List.of(b1, b2));

        List<BookingResponseDTO> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        assertEquals("giuseppe@test.it", result.get(0).getParticipantEmail());
        assertEquals("gabriele@test.it", result.get(1).getParticipantEmail());
    }

    @Test
    @DisplayName("getBookingsByEventId - Verifica esistenza evento e restituisce le prenotazioni")
    void getBookingsByEventId_Success() {
        when(eventClientService.getEventById(1L)).thenReturn(availableEvent);
        Booking b1 = new Booking(1L, "Giuseppe", "giuseppe@test.it");
        b1.setId(1L);
        when(bookingRepository.findByEventId(1L)).thenReturn(List.of(b1));

        List<BookingResponseDTO> result = bookingService.getBookingsByEventId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEventId());
        verify(eventClientService).getEventById(1L);
    }

    @Test
    @DisplayName("deleteBooking - Elimina una prenotazione esistente")
    void deleteBooking_Success() {
        when(bookingRepository.existsById(10L)).thenReturn(true);

        assertDoesNotThrow(() -> bookingService.deleteBooking(10L));
        verify(bookingRepository).deleteById(10L);
    }

    @Test
    @DisplayName("deleteBooking - Lancia BookingNotFoundException se l'ID non esiste")
    void deleteBooking_NotFound() {
        when(bookingRepository.existsById(999L)).thenReturn(false);

        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBooking(999L));
        verify(bookingRepository, never()).deleteById(any());
    }
}