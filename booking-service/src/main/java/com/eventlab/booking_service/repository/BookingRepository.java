package com.eventlab.booking_service.repository;

import com.eventlab.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByEventId(Long eventId);

    long countByEventId(Long eventId);

    boolean existsByEventIdAndParticipantEmail(Long eventId, String participantEmail);
}