package com.yaswanth.itsupport.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yaswanth.itsupport.dto.TicketRequest;
import com.yaswanth.itsupport.dto.TicketResponse;
import com.yaswanth.itsupport.enums.TicketPriority;
import com.yaswanth.itsupport.enums.TicketStatus;
import com.yaswanth.itsupport.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

	private final TicketService ticketService;

	public TicketController(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	@PostMapping
	public TicketResponse createTicket(@Valid @RequestBody TicketRequest request) {

		return ticketService.createTicket(request);
	}

	// http://localhost:8080/api/tickets
	@GetMapping
	public List<TicketResponse> getAllTickets() {

		return ticketService.getAllTickets();
	}

	@GetMapping("/{id}")
	public TicketResponse getTicketById(@PathVariable Long id) {

		return ticketService.getTicketById(id);
	}

	@PutMapping("/{id}")
	public TicketResponse updateTicket(@PathVariable Long id, @Valid @RequestBody TicketRequest request) {

		return ticketService.updateTicket(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> deleteTicket(@PathVariable Long id) {

		ticketService.deleteTicket(id);

		Map<String, Object> response = new HashMap<>();

		response.put("status", 200);
		response.put("message", "Ticket deleted successfully");
		response.put("ticketId", id);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/filter")
	public List<TicketResponse> getTicketsByStatus(@RequestParam TicketStatus status) {

		return ticketService.getTicketsByStatus(status);
	}

	@GetMapping("/filter/priority")
	public List<TicketResponse> getTicketsByPriority(@RequestParam TicketPriority priority) {

		return ticketService.getTicketsByPriority(priority);
	}

	@GetMapping("/filter/status-priority")
	public List<TicketResponse> getTicketsByStatusAndPriority(@RequestParam TicketStatus status,
			@RequestParam TicketPriority priority) {

		return ticketService.getTicketsByStatusAndPriority(status, priority);
	}

	@GetMapping("/search")
	public List<TicketResponse> searchTickets(@RequestParam String keyword) {

		return ticketService.searchTickets(keyword);
	}
}