package com.eventlab.booking_service.service;

import com.eventlab.booking_service.dto.BookingRequestDTO;
import com.eventlab.booking_service.dto.BookingResponseDTO;
import com.eventlab.booking_service.dto.EventDTO;
import com.eventlab.booking_service.entity.Booking;
import com.eventlab.booking_service.exception.BookingConflictException;
import com.eventlab.booking_service.exception.BookingNotFoundException;
import com.eventlab.booking_service.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventClientService eventClientService;

    public BookingService(BookingRepository bookingRepository, EventClientService eventClientService) {
        this.bookingRepository = bookingRepository;
        this.eventClientService = eventClientService;
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        EventDTO event = eventClientService.getEventById(request.getEventId());

        String normalizedEmail = request.getParticipantEmail().trim().toLowerCase();
        if (bookingRepository.existsByEventIdAndParticipantEmail(request.getEventId(), normalizedEmail)) {
            throw new BookingConflictException("Questa email risulta già prenotata per questo evento");
        }

        long currentBookings = bookingRepository.countByEventId(request.getEventId());
        if (event.getMaxSeats() != null && currentBookings >= event.getMaxSeats()) {
            throw new BookingConflictException("Posti esauriti per questo evento");
        }

        Booking booking = new Booking(
                request.getEventId(),
                request.getParticipantName().trim(),
                normalizedEmail
        );

        Booking savedBooking = bookingRepository.save(booking);
        return toResponseDTO(savedBooking);
    }

    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<BookingResponseDTO> getBookingsByEventId(Long eventId) {
        eventClientService.getEventById(eventId);
        return bookingRepository.findByEventId(eventId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException("Prenotazione con ID " + id + " non trovata");
        }
        bookingRepository.deleteById(id);
    }

    private BookingResponseDTO toResponseDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getEventId(),
                booking.getParticipantName(),
                booking.getParticipantEmail(),
                booking.getCreatedAt()
        );
    }
}