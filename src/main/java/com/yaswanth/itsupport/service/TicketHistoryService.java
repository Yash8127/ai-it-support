package com.yaswanth.itsupport.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yaswanth.itsupport.dto.TicketHistoryResponse;
import com.yaswanth.itsupport.entity.Ticket;
import com.yaswanth.itsupport.entity.TicketHistory;
import com.yaswanth.itsupport.entity.User;
import com.yaswanth.itsupport.repository.TicketHistoryRepository;
import com.yaswanth.itsupport.repository.TicketRepository;
import com.yaswanth.itsupport.repository.UserRepository;

@Service
public class TicketHistoryService {

	private final TicketHistoryRepository ticketHistoryRepository;
	private final UserRepository userRepository;
	private final TicketRepository ticketRepository;

	public TicketHistoryService(TicketHistoryRepository ticketHistoryRepository, UserRepository userRepository,
			TicketRepository ticketRepository) {

		this.ticketHistoryRepository = ticketHistoryRepository;
		this.userRepository = userRepository;
		this.ticketRepository = ticketRepository;
	}

	// =========================================================
	// RECORD TICKET ACTIVITY
	// =========================================================

	public void recordActivity(Ticket ticket, String action, String oldValue, String newValue) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		TicketHistory history = new TicketHistory();

		history.setTicketId(ticket.getId());
		history.setUser(user);
		history.setAction(action);
		history.setOldValue(oldValue);
		history.setNewValue(newValue);
		history.setCreatedAt(LocalDateTime.now());

		ticketHistoryRepository.save(history);

		System.out.println(">>> TICKET HISTORY CREATED" + " | TICKET: " + ticket.getId() + " | USER: " + username
				+ " | ACTION: " + action);
	}

	public List<TicketHistoryResponse> getHistoryByTicketId(Long ticketId) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {

			Ticket ticket = ticketRepository.findById(ticketId)
					.orElseThrow(() -> new RuntimeException("Ticket not found"));

			if (!ticket.getUser().getUsername().equals(username)) {
				throw new RuntimeException("Ticket not found");
			}
		}

		return ticketHistoryRepository.findByTicketIdOrderByCreatedAtDesc(ticketId).stream()
				.map(history -> new TicketHistoryResponse(history.getId(), history.getTicketId(),
						history.getUser().getUsername(), history.getAction(), history.getOldValue(),
						history.getNewValue(), history.getCreatedAt()))
				.collect(Collectors.toList());
	}

	public List<TicketHistoryResponse> getDeletedTicketHistory() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			throw new RuntimeException("Access denied");
		}

		List<TicketHistory> deletedHistory = ticketHistoryRepository.findByActionOrderByCreatedAtDesc("DELETED");

		return deletedHistory.stream()
				.map(history -> new TicketHistoryResponse(history.getId(), history.getTicketId(),
						history.getUser().getUsername(), history.getAction(), history.getOldValue(),
						history.getNewValue(), history.getCreatedAt()))
				.toList();
	}
}