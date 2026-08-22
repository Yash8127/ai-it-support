package com.yaswanth.itsupport.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yaswanth.itsupport.ai.AiTicketAnalyzer;
import com.yaswanth.itsupport.dto.AiTicketAnalysisResponse;
import com.yaswanth.itsupport.dto.TicketRequest;
import com.yaswanth.itsupport.dto.TicketResponse;
import com.yaswanth.itsupport.entity.Ticket;
import com.yaswanth.itsupport.enums.TicketPriority;
import com.yaswanth.itsupport.enums.TicketStatus;
import com.yaswanth.itsupport.expection.TicketNotFoundException;
import com.yaswanth.itsupport.repository.TicketRepository;

@Service
public class TicketService {

	private final TicketRepository ticketRepository;
	private final AiTicketAnalyzer aiTicketAnalyzer;

	public TicketService(TicketRepository ticketRepository, AiTicketAnalyzer aiTicketAnalyzer) {

		this.ticketRepository = ticketRepository;
		this.aiTicketAnalyzer = aiTicketAnalyzer;

	}

	// CREATE TICKET
	public TicketResponse createTicket(TicketRequest request) {

		Ticket ticket = new Ticket();

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

	// GET ALL TICKETS
	public List<TicketResponse> getAllTickets() {

		return ticketRepository.findAll().stream().map(this::convertToResponse).toList();
	}

	// GET TICKET BY ID
	public TicketResponse getTicketById(Long id) {

		Ticket ticket = ticketRepository.findById(id)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

		return convertToResponse(ticket);
	}

	// UPDATE THE TCKET
	public TicketResponse updateTicket(Long id, TicketRequest request) {

		Ticket existingTicket = ticketRepository.findById(id)
				.orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

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

		ticketRepository.delete(ticket);
	}

	// FILTER TICKETS BY STATUS

	public List<TicketResponse> getTicketsByStatus(TicketStatus status) {

		return ticketRepository.findByStatus(status).stream().map(this::convertToResponse).toList();
	}

	// FILTER TICKETS BY PRIORITY
	public List<TicketResponse> getTicketsByPriority(TicketPriority priority) {

		return ticketRepository.findByPriority(priority).stream().map(this::convertToResponse).toList();
	}

	// FILTER TICKETS BY STATUS AND PRIORITY
	public List<TicketResponse> getTicketsByStatusAndPriority(TicketStatus status, TicketPriority priority) {

		return ticketRepository.findByStatusAndPriority(status, priority).stream().map(this::convertToResponse)
				.toList();
	}

	// FILTER TICKETS BY CTAEGORY OR DESCRIPTION
	public List<TicketResponse> searchTickets(String keyword) {

		return ticketRepository.searchTickets(keyword).stream().map(this::convertToResponse).toList();
	}
}