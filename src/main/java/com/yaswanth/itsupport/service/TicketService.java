package com.yaswanth.itsupport.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yaswanth.itsupport.ai.AiTicketAnalyzer;
import com.yaswanth.itsupport.dto.AiTicketAnalysisResponse;
import com.yaswanth.itsupport.dto.TicketRequest;
import com.yaswanth.itsupport.dto.TicketResponse;
import com.yaswanth.itsupport.entity.Ticket;
import com.yaswanth.itsupport.entity.User;
import com.yaswanth.itsupport.enums.TicketPriority;
import com.yaswanth.itsupport.enums.TicketStatus;
import com.yaswanth.itsupport.expection.TicketNotFoundException;
import com.yaswanth.itsupport.repository.TicketRepository;
import com.yaswanth.itsupport.repository.UserRepository;

@Service
public class TicketService {

	private final TicketRepository ticketRepository;
	private final AiTicketAnalyzer aiTicketAnalyzer;
	private final UserRepository userRepository;

	public TicketService(TicketRepository ticketRepository, AiTicketAnalyzer aiTicketAnalyzer,
			UserRepository userRepository) {

		this.ticketRepository = ticketRepository;
		this.aiTicketAnalyzer = aiTicketAnalyzer;
		this.userRepository = userRepository;

	}

	// CREATE TICKET
	public TicketResponse createTicket(TicketRequest request) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		Ticket ticket = new Ticket();

		ticket.setUser(user);

		ticket.setTitle(request.getTitle());
		ticket.setDescription(request.getDescription());

		// Send title and description to AI
		String message = """
				Title: %s
				Description: %s
				""".formatted(request.getTitle(), request.getDescription());

		System.out.println(">>> AI TICKET ANALYSIS STARTED");

		AiTicketAnalysisResponse analysis = aiTicketAnalyzer.analyzeTicket(message);

		System.out.println(">>> AI CATEGORY: " + analysis.getCategory());
		System.out.println(">>> AI PRIORITY: " + analysis.getPriority());
		System.out.println(">>> AI SUGGESTION: " + analysis.getSuggestion());

		// AI-generated category
		ticket.setCategory(analysis.getCategory());

		// AI-generated priority
		TicketPriority priority = TicketPriority.valueOf(analysis.getPriority().trim().toUpperCase());

		ticket.setPriority(priority);

		// AI-generated suggestion
		ticket.setAiSuggestion(analysis.getSuggestion());

		// Server-controlled fields
		ticket.setStatus(TicketStatus.OPEN);
		ticket.setCreatedAt(LocalDateTime.now());

		// Save ticket
		Ticket savedTicket = ticketRepository.save(ticket);

		System.out.println(">>> TICKET CREATED WITH ID: " + savedTicket.getId());

		return convertToResponse(savedTicket);
	}

	// Convert Entity → Response DTO
	private TicketResponse convertToResponse(Ticket ticket) {

		return new TicketResponse(ticket.getId(), ticket.getTitle(), ticket.getDescription(), ticket.getCategory(),
				ticket.getPriority(), ticket.getStatus(), ticket.getAiSuggestion(), ticket.getCreatedAt());
	}

	// GET TICKETS BASED ON USER ROLE
	public List<TicketResponse> getAllTickets() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		List<Ticket> tickets;

		if (isAdmin) {
			tickets = ticketRepository.findAll();
		} else {
			tickets = ticketRepository.findByUserUsername(username);
		}

		return tickets.stream().map(this::convertToResponse).toList();
	}

	// GET TICKET BY ID
	public TicketResponse getTicketById(Long id) {

		Ticket ticket = ticketRepository.findById(id)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin && !ticket.getUser().getUsername().equals(username)) {

			throw new TicketNotFoundException("Ticket not found with id: " + id);
		}

		return convertToResponse(ticket);
	}

	// UPDATE THE TCKET
	public TicketResponse updateTicket(Long id, TicketRequest request) {

		Ticket existingTicket = ticketRepository.findById(id)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin && !existingTicket.getUser().getUsername().equals(username)) {

			throw new TicketNotFoundException("Ticket not found with id: " + id);
		}

		existingTicket.setTitle(request.getTitle());
		existingTicket.setDescription(request.getDescription());
		existingTicket.setCategory(request.getCategory());
		existingTicket.setPriority(request.getPriority());

		Ticket updatedTicket = ticketRepository.save(existingTicket);

		return convertToResponse(updatedTicket);
	}

	// UPDATE ONLY TICKET STATUS
	public TicketResponse updateTicketStatus(Long id, TicketStatus status) {

		Ticket existingTicket = ticketRepository.findById(id)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

		existingTicket.setStatus(status);

		Ticket updatedTicket = ticketRepository.save(existingTicket);

		return convertToResponse(updatedTicket);
	}

	// DELETE TICKET
	public void deleteTicket(Long id) {

		Ticket ticket = ticketRepository.findById(id)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			throw new TicketNotFoundException("Ticket not found with id: " + id);
		}

		ticketRepository.delete(ticket);
	}

	// FILTER TICKETS BY STATUS

	public List<TicketResponse> getTicketsByStatus(TicketStatus status) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		List<Ticket> tickets;

		if (isAdmin) {
			tickets = ticketRepository.findByStatus(status);
		} else {
			tickets = ticketRepository.findByStatusAndUserUsername(status, username);
		}

		return tickets.stream().map(this::convertToResponse).toList();
	}

	// FILTER TICKETS BY PRIORITY

	public List<TicketResponse> getTicketsByPriority(TicketPriority priority) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		List<Ticket> tickets;

		if (isAdmin) {
			tickets = ticketRepository.findByPriority(priority);
		} else {
			tickets = ticketRepository.findByPriorityAndUserUsername(priority, username);
		}

		return tickets.stream().map(this::convertToResponse).toList();
	}

	// FILTER TICKETS BY STATUS AND PRIORITY

	public List<TicketResponse> getTicketsByStatusAndPriority(TicketStatus status, TicketPriority priority) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		List<Ticket> tickets;

		if (isAdmin) {
			tickets = ticketRepository.findByStatusAndPriority(status, priority);
		} else {
			tickets = ticketRepository.findByStatusAndPriorityAndUserUsername(status, priority, username);
		}

		return tickets.stream().map(this::convertToResponse).toList();
	}

	// FILTER TICKETS BY CATEGORY OR DESCRIPTION

	public List<TicketResponse> searchTickets(String keyword) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		List<Ticket> tickets;

		if (isAdmin) {
			tickets = ticketRepository.searchTickets(keyword);
		} else {
			tickets = ticketRepository.searchTicketsByUser(keyword, username);
		}

		return tickets.stream().map(this::convertToResponse).toList();
	}
}