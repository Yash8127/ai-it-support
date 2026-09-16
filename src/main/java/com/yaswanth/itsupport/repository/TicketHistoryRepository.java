package com.yaswanth.itsupport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yaswanth.itsupport.entity.TicketHistory;

public interface TicketHistoryRepository
        extends JpaRepository<TicketHistory, Long> {

    List<TicketHistory> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
    List<TicketHistory> findByActionOrderByCreatedAtDesc(
            String action
    );
}