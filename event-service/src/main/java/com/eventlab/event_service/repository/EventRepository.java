package com.eventlab.event_service.repository;
import java.util.List;

import com.eventlab.event_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAll();



}
