package com.yaswanth.itsupport.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yaswanth.itsupport.dto.DashboardAnalyticsResponse;
import com.yaswanth.itsupport.entity.Ticket;
import com.yaswanth.itsupport.enums.TicketPriority;
import com.yaswanth.itsupport.enums.TicketStatus;
import com.yaswanth.itsupport.repository.TicketRepository;

@Service
public class DashboardAnalyticsService {

    private final TicketRepository ticketRepository;

    public DashboardAnalyticsService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public DashboardAnalyticsResponse getAnalytics() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        List<Ticket> tickets;

        if (isAdmin) {
            tickets = ticketRepository.findAll();
        } else {
            tickets = ticketRepository.findByUserUsername(username);
        }

        long totalTickets = tickets.size();

        long openTickets = tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.OPEN)
                .count();

        long inProgressTickets = tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS)
                .count();

        long resolvedTickets = tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.RESOLVED)
                .count();

        long closedTickets = tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.CLOSED)
                .count();

        long criticalTickets = tickets.stream()
                .filter(t -> t.getPriority() == TicketPriority.CRITICAL)
                .count();

        long highPriorityTickets = tickets.stream()
                .filter(t -> t.getPriority() == TicketPriority.HIGH)
                .count();

        long mediumPriorityTickets = tickets.stream()
                .filter(t -> t.getPriority() == TicketPriority.MEDIUM)
                .count();

        long lowPriorityTickets = tickets.stream()
                .filter(t -> t.getPriority() == TicketPriority.LOW)
                .count();

        return new DashboardAnalyticsResponse(
                totalTickets,
                openTickets,
                inProgressTickets,
                resolvedTickets,
                closedTickets,
                criticalTickets,
                highPriorityTickets,
                mediumPriorityTickets,
                lowPriorityTickets
        );
    }
}