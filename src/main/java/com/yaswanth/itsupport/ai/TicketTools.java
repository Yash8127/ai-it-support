package com.yaswanth.itsupport.ai;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.yaswanth.itsupport.dto.TicketResponse;
import com.yaswanth.itsupport.enums.TicketPriority;
import com.yaswanth.itsupport.enums.TicketStatus;
import com.yaswanth.itsupport.service.TicketService;

@Component
public class TicketTools {

	private final TicketService ticketService;

	public TicketTools(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	// =========================================================
	// 1. SEARCH TICKETS BY KEYWORD
	// =========================================================

	@Tool(description = """
			Search IT support tickets by keyword.

			Use this ONLY when the user searches for a keyword,
			issue, device, application, or problem description.

			Examples:
			Find laptop tickets -> keyword=laptop
			Find WiFi tickets -> keyword=WiFi
			Find login tickets -> keyword=login

			Do NOT use this tool for status filtering.
			Do NOT use this tool for priority filtering.
			""")
	public String searchTickets(
			@ToolParam(description = "Keyword to search in ticket title and description", required = true) String keyword) {

		System.out.println(">>> AI TOOL CALLED: searchTickets [" + keyword + "]");

		if (keyword == null || keyword.isBlank()) {
			System.out.println(">>> ERROR: Search keyword is null or empty");

			return "TOOL_ERROR: Search keyword is required.";
		}

		List<TicketResponse> tickets = ticketService.searchTickets(keyword.trim());

		System.out.println(">>> TOOL RESULT COUNT: " + tickets.size());

		return buildTicketListResult(tickets);
	}

	// =========================================================
	// 2. GET TICKET BY ID
	// =========================================================

	@Tool(description = """
			Get ONE specific IT support ticket using its numeric ID.

			Use this ONLY when the user gives a specific ticket ID.

			Example:
			Show ticket 15 -> id=15
			Give details about ticket 9 -> id=9

			Do NOT use this tool to search multiple tickets.
			""")
	public String getTicketById(@ToolParam(description = "Numeric ID of the ticket", required = true) Long id) {

		System.out.println(">>> AI TOOL CALLED: getTicketById [" + id + "]");

		if (id == null) {
			System.out.println(">>> ERROR: Ticket ID is null");

			return "TOOL_ERROR: Ticket ID is required.";
		}

		TicketResponse ticket;

		try {
			ticket = ticketService.getTicketById(id);
		} catch (Exception e) {

			System.out.println(">>> ERROR: Ticket not found: " + id);

			return "NO_TICKETS_FOUND";
		}

		if (ticket == null) {
			return "NO_TICKETS_FOUND";
		}

		return """
				TOOL_SUCCESS
				TICKET
				ID=%s
				TITLE=%s
				DESCRIPTION=%s
				CATEGORY=%s
				PRIORITY=%s
				STATUS=%s
				AI_SUGGESTION=%s
				CREATED_AT=%s
				""".formatted(ticket.getId(), ticket.getTitle(), ticket.getDescription(), ticket.getCategory(),
				ticket.getPriority(), ticket.getStatus(), ticket.getAiSuggestion(), ticket.getCreatedAt());
	}

	// =========================================================
	// 3. GET TICKETS BY PRIORITY
	// =========================================================

	@Tool(description = """
			Get ALL IT support tickets with ONE specific priority.

			Use this ONLY when the user specifies a priority
			and does NOT specify a status.

			Valid priorities:
			LOW
			MEDIUM
			HIGH
			CRITICAL

			Examples:

			Show critical tickets
			-> priority=CRITICAL

			Show high priority tickets
			-> priority=HIGH

			Show medium priority tickets
			-> priority=MEDIUM

			Show low priority tickets
			-> priority=LOW

			CRITICAL, HIGH, MEDIUM and LOW are PRIORITIES.
			They are NOT statuses.
			""")
	public String getTicketsByPriority(@ToolParam(description = """
			Required ticket priority.
			Must be exactly one of:
			LOW
			MEDIUM
			HIGH
			CRITICAL
			""", required = true) String priority) {

		System.out.println(">>> AI TOOL CALLED: getTicketsByPriority [" + priority + "]");

		if (priority == null || priority.isBlank()) {

			System.out.println(">>> ERROR: Priority is null");

			return "TOOL_ERROR: Priority is required.";
		}

		TicketPriority ticketPriority;

		try {

			ticketPriority = TicketPriority.valueOf(priority.trim().toUpperCase());

		} catch (IllegalArgumentException e) {

			System.out.println(">>> ERROR: Invalid priority: " + priority);

			return """
					TOOL_ERROR:
					Invalid priority.
					Allowed values: LOW, MEDIUM, HIGH, CRITICAL.
					""";
		}

		List<TicketResponse> tickets = ticketService.getTicketsByPriority(ticketPriority);

		System.out.println(">>> TOOL RESULT COUNT: " + tickets.size());

		return buildTicketListResult(tickets);
	}

	// =========================================================
	// 4. GET TICKETS BY STATUS
	// =========================================================

	@Tool(description = """
			Get ALL IT support tickets with ONE specific status.

			Use this ONLY when the user specifies a status
			and does NOT specify a priority.

			Valid statuses:
			OPEN
			IN_PROGRESS
			RESOLVED
			CLOSED

			Examples:

			Show open tickets
			-> status=OPEN

			Show resolved tickets
			-> status=RESOLVED

			OPEN, IN_PROGRESS, RESOLVED and CLOSED are STATUSES.

			LOW, MEDIUM, HIGH and CRITICAL are NOT statuses.
			""")
	public String getTicketsByStatus(@ToolParam(description = """
			Required ticket status.
			Must be exactly one of:
			OPEN
			IN_PROGRESS
			RESOLVED
			CLOSED
			""", required = true) String status) {

		System.out.println(">>> AI TOOL CALLED: getTicketsByStatus [" + status + "]");

		if (status == null || status.isBlank()) {

			System.out.println(">>> ERROR: Status is null");

			return "TOOL_ERROR: Status is required.";
		}

		TicketStatus ticketStatus;

		try {

			ticketStatus = TicketStatus.valueOf(status.trim().toUpperCase());

		} catch (IllegalArgumentException e) {

			System.out.println(">>> ERROR: Invalid status: " + status);

			return """
					TOOL_ERROR:
					Invalid status.
					Allowed values:
					OPEN, IN_PROGRESS, RESOLVED, CLOSED.
					""";
		}

		List<TicketResponse> tickets = ticketService.getTicketsByStatus(ticketStatus);

		System.out.println(">>> TOOL RESULT COUNT: " + tickets.size());

		return buildTicketListResult(tickets);
	}

	// =========================================================
	// 5. GET TICKETS BY STATUS + PRIORITY
	// =========================================================

	@Tool(description = """
			Get ALL IT support tickets matching BOTH a status
			AND a priority.

			Use this ONLY when the user specifies BOTH.

			Valid statuses:
			OPEN
			IN_PROGRESS
			RESOLVED
			CLOSED

			Valid priorities:
			LOW
			MEDIUM
			HIGH
			CRITICAL

			Examples:

			Show open critical tickets
			-> status=OPEN
			-> priority=CRITICAL

			Show open high priority tickets
			-> status=OPEN
			-> priority=HIGH

			Show resolved medium priority tickets
			-> status=RESOLVED
			-> priority=MEDIUM
			""")
	public String getTicketsByStatusAndPriority(

			@ToolParam(description = """
					Required ticket status.
					Must be:
					OPEN, IN_PROGRESS, RESOLVED, or CLOSED.
					""", required = true) String status,

			@ToolParam(description = """
					Required ticket priority.
					Must be:
					LOW, MEDIUM, HIGH, or CRITICAL.
					""", required = true) String priority) {

		System.out.println(">>> AI TOOL CALLED: getTicketsByStatusAndPriority(" + status + ", " + priority + ")");

		if (status == null || status.isBlank()) {

			System.out.println(">>> ERROR: Status is null");

			return "TOOL_ERROR: Status is required.";
		}

		if (priority == null || priority.isBlank()) {

			System.out.println(">>> ERROR: Priority is null");

			return "TOOL_ERROR: Priority is required.";
		}

		TicketStatus ticketStatus;
		TicketPriority ticketPriority;

		try {

			ticketStatus = TicketStatus.valueOf(status.trim().toUpperCase());

			ticketPriority = TicketPriority.valueOf(priority.trim().toUpperCase());

		} catch (IllegalArgumentException e) {

			System.out.println(">>> ERROR: Invalid status or priority");

			return """
					TOOL_ERROR:
					Invalid status or priority.

					Status:
					OPEN, IN_PROGRESS, RESOLVED, CLOSED

					Priority:
					LOW, MEDIUM, HIGH, CRITICAL
					""";
		}

		List<TicketResponse> tickets = ticketService.getTicketsByStatusAndPriority(ticketStatus, ticketPriority);

		System.out.println(">>> TOOL RESULT COUNT: " + tickets.size());

		return buildTicketListResult(tickets);
	}

	// =========================================================
	// COMMON RESULT FORMAT
	// =========================================================

	private String buildTicketListResult(List<TicketResponse> tickets) {

		if (tickets == null || tickets.isEmpty()) {

			return "NO_TICKETS_FOUND";
		}

		StringBuilder result = new StringBuilder();

		result.append("TOOL_SUCCESS\n");
		result.append("TICKET_COUNT=").append(tickets.size()).append("\n");

		for (TicketResponse ticket : tickets) {

			result.append("TICKET\n");
			result.append("ID=").append(ticket.getId()).append("\n");
			result.append("TITLE=").append(ticket.getTitle()).append("\n");
			result.append("CATEGORY=").append(ticket.getCategory()).append("\n");
			result.append("PRIORITY=").append(ticket.getPriority()).append("\n");
			result.append("STATUS=").append(ticket.getStatus()).append("\n");
		}

		return result.toString();
	}
}