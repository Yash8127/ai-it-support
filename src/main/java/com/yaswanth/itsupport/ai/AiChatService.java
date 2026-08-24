package com.yaswanth.itsupport.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.yaswanth.itsupport.dto.AiTicketAnalysisResponse;
import com.yaswanth.itsupport.dto.TicketResponse;
import com.yaswanth.itsupport.service.TicketService;

import tools.jackson.databind.ObjectMapper;

@Service
public class AiChatService {

	private final ChatClient chatClient;
	private final TicketTools ticketTools;
	private final TicketService ticketService;

	/*
	 * Stores the ticket ID waiting for deletion confirmation.
	 *
	 * Example:
	 *
	 * User: Delete ticket 17
	 *
	 * pendingDeleteTicketId = 17
	 *
	 * Then:
	 *
	 * User: Yes
	 *
	 * -> delete ticket 17
	 */
	private Long pendingDeleteTicketId;

	public AiChatService(ChatClient.Builder chatClientBuilder, TicketTools ticketTools, TicketService ticketService) {

		this.chatClient = chatClientBuilder.build();
		this.ticketTools = ticketTools;
		this.ticketService = ticketService;
	}

	// =========================================================
	// AI CHAT
	// =========================================================

	public String chat(String message) {

		System.out.println(">>> CHAT METHOD CALLED: " + message);

		String lowerMessage = message.trim().toLowerCase();

		// =====================================================
		// 1. HANDLE PENDING DELETE CONFIRMATION
		// =====================================================

		if (pendingDeleteTicketId != null) {

			// USER CONFIRMED
			if (lowerMessage.equals("yes") || lowerMessage.equals("confirm") || lowerMessage.equals("delete it")
					|| lowerMessage.equals("proceed") || lowerMessage.equals("yes, delete it")) {

				Long id = pendingDeleteTicketId;

				// Clear state BEFORE tool call
				pendingDeleteTicketId = null;

				System.out.println(">>> DELETE CONFIRMED FOR TICKET: " + id);

				return chatClient.prompt().system("""
						You are an IT support assistant.

						Delete ONLY the ticket whose ID is provided
						by the user.

						Use the deleteTicket tool.

						Do not use any other ticket tool.

						If the tool returns TICKET_DELETED:
						respond exactly:

						Ticket deleted successfully.

						If the tool returns TOOL_ERROR:
						respond exactly:

						The ticket could not be deleted.

						Do not invent information.
						Do not output JSON.
						Do not mention internal tools.
						""").user("Delete ticket " + id).tools(ticketTools).call().content();
			}

			// USER CANCELLED
			if (lowerMessage.equals("no") || lowerMessage.equals("cancel") || lowerMessage.equals("don't delete")
					|| lowerMessage.equals("do not delete")) {

				System.out.println(">>> DELETE CANCELLED FOR TICKET: " + pendingDeleteTicketId);

				pendingDeleteTicketId = null;

				return "Ticket deletion cancelled.";
			}

			// INVALID CONFIRMATION RESPONSE
			return """
					Please confirm the deletion.

					Reply "Yes" to delete the ticket or
					"No" to cancel.
					""";
		}

		// =====================================================
		// 2. DETECT NEW DELETE REQUEST
		// =====================================================

		if (lowerMessage.startsWith("delete ticket ")) {

			String idText = lowerMessage.substring("delete ticket ".length()).trim();

			try {

				Long id = Long.parseLong(idText);

				System.out.println(">>> DELETE REQUEST FOR TICKET: " + id);

				// Check ticket exists
				TicketResponse ticket = ticketService.getTicketById(id);

				// Store pending delete
				pendingDeleteTicketId = id;

				System.out.println(">>> DELETE CONFIRMATION PENDING FOR: " + id);

				return """
						Ticket %d - %s is about to be deleted.

						Are you sure you want to delete this ticket?
						Reply "Yes" to delete or "No" to cancel.
						""".formatted(ticket.getId(), ticket.getTitle());

			} catch (NumberFormatException e) {

				return "Invalid ticket ID.";

			} catch (Exception e) {

				System.out.println(">>> DELETE REQUEST ERROR: " + e.getMessage());

				return "Ticket could not be found.";
			}
		}

		// =====================================================
		// 3. NORMAL AI CHAT
		// =====================================================

		return chatClient.prompt().system("""
				You are an AI IT Support Assistant.

				Your job is to answer IT support questions and
				retrieve existing ticket information using tools.

				==================================================
				ABSOLUTE RULE
				==================================================

				NEVER invent ticket information.

				The result returned by a ticket tool is the ONLY
				source of ticket information.

				If a tool returns ticket records, display those
				records.

				DO NOT replace ticket records with general IT
				advice.

				DO NOT ignore a successful tool result.

				==================================================
				TOOL SELECTION
				==================================================

				RULE 1 - SPECIFIC TICKET ID

				If the user asks about one specific ticket ID,
				use getTicketById.

				Example:

				User:
				Show ticket 17

				Tool:
				getTicketById(id=17)


				==================================================
				RULE 2 - STATUS AND PRIORITY
				==================================================

				If BOTH status and priority are present,
				use ONLY:

				getTicketsByStatusAndPriority

				Example:

				Show open high priority tickets

				status = OPEN
				priority = HIGH


				Example:

				Show critical open tickets

				status = OPEN
				priority = CRITICAL


				==================================================
				RULE 3 - PRIORITY ONLY
				==================================================

				If ONLY priority is present,
				use ONLY:

				getTicketsByPriority

				Valid priorities:

				LOW
				MEDIUM
				HIGH
				CRITICAL

				Examples:

				Show critical tickets
				-> priority = CRITICAL

				Show high priority tickets
				-> priority = HIGH

				Show medium priority tickets
				-> priority = MEDIUM

				Show low priority tickets
				-> priority = LOW


				==================================================
				RULE 4 - STATUS ONLY
				==================================================

				If ONLY status is present,
				use ONLY:

				getTicketsByStatus

				Valid statuses:

				OPEN
				IN_PROGRESS
				RESOLVED
				CLOSED

				Examples:

				Show open tickets
				-> status = OPEN

				Show resolved tickets
				-> status = RESOLVED

				Show closed tickets
				-> status = CLOSED


				==================================================
				RULE 5 - KEYWORD SEARCH
				==================================================

				If the user wants to find tickets using a
				keyword, device, application, issue, or problem
				description, use:

				searchTickets

				Examples:

				Find laptop tickets
				-> searchTickets(keyword="laptop")

				Find WiFi tickets
				-> searchTickets(keyword="WiFi")

				Find login problems
				-> searchTickets(keyword="login")


				IMPORTANT:

				"Find laptop tickets" means SEARCH TICKETS.

				It does NOT mean:

				"Give laptop troubleshooting advice."


				==================================================
				RULE 6 - UPDATE TICKET STATUS
				==================================================

				If the user asks to change a ticket's status,
				use:

				updateTicketStatus

				Examples:

				Resolve ticket 17
				-> id=17
				-> status=RESOLVED

				Close ticket 17
				-> id=17
				-> status=CLOSED

				Reopen ticket 17
				-> id=17
				-> status=OPEN

				Move ticket 17 to in progress
				-> id=17
				-> status=IN_PROGRESS

				Do NOT use deleteTicket for status changes.


				==================================================
				RULE 7 - RESOLUTION / TROUBLESHOOTING
				==================================================

				If the user explicitly asks for:

				resolution
				troubleshooting
				fix
				solution
				recommendation
				steps to solve
				how to resolve

				for a specific ticket:

				FIRST use getTicketById.

				Then provide practical troubleshooting advice
				based ONLY on the information returned by the
				ticket.

				IMPORTANT:

				Providing resolution advice does NOT change the
				ticket status.

				Do NOT call updateTicketStatus unless the user
				explicitly asks to change the status.

				Example:

				Give me a resolution for ticket 17

				-> getTicketById(id=17)

				Then provide resolution steps.


				==================================================
				PRIORITY VS STATUS
				==================================================

				PRIORITY:

				LOW
				MEDIUM
				HIGH
				CRITICAL

				STATUS:

				OPEN
				IN_PROGRESS
				RESOLVED
				CLOSED

				CRITICAL = PRIORITY
				HIGH = PRIORITY
				MEDIUM = PRIORITY
				LOW = PRIORITY

				OPEN = STATUS
				IN_PROGRESS = STATUS
				RESOLVED = STATUS
				CLOSED = STATUS

				NEVER treat a priority as a status.

				NEVER treat a status as a priority.


				==================================================
				TOOL RESULT - SUCCESS
				==================================================

				A successful list tool returns:

				TOOL_SUCCESS
				TICKET_COUNT=N

				followed by ticket records.

				Example:

				TOOL_SUCCESS
				TICKET_COUNT=2

				TICKET
				ID=3
				TITLE=Printer not working
				CATEGORY=HARDWARE
				PRIORITY=HIGH
				STATUS=OPEN

				TICKET
				ID=5
				TITLE=Wi-Fi issue
				CATEGORY=NETWORK
				PRIORITY=HIGH
				STATUS=OPEN


				==================================================
				CRITICAL DISPLAY RULE
				==================================================

				If TOOL_SUCCESS is present:

				TICKETS WERE FOUND.

				You MUST display ALL returned tickets.

				If TICKET_COUNT=2,
				display exactly 2 tickets.

				If TICKET_COUNT=7,
				display exactly 7 tickets.

				NEVER say:

				No matching tickets were found.

				when TOOL_SUCCESS is present.

				NEVER display only the count.

				NEVER omit tickets.

				NEVER merge tickets.

				NEVER invent tickets.


				==================================================
				SEARCH RESULT RULE
				==================================================

				searchTickets follows the SAME rules.

				If searchTickets returns:

				TOOL_SUCCESS
				TICKET_COUNT=7

				then 7 tickets were found.

				Display all 7 tickets.

				DO NOT provide generic troubleshooting advice.

				DO NOT provide a solution to the issue unless
				the user explicitly asks for a solution.

				For a request such as:

				Find laptop tickets

				the response MUST contain the returned tickets.


				==================================================
				LIST TICKET FORMAT
				==================================================

				For every returned ticket display:

				Ticket ID: <ID>
				Title: <TITLE>
				Category: <CATEGORY>
				Priority: <PRIORITY>
				Status: <STATUS>


				==================================================
				NO RESULTS
				==================================================

				ONLY when the tool returns exactly:

				NO_TICKETS_FOUND

				respond:

				No matching tickets were found.


				==================================================
				TOOL ERROR
				==================================================

				If a tool returns:

				TOOL_ERROR
				INVALID_PRIORITY
				INVALID_STATUS
				INVALID_FILTER

				do not invent ticket information.

				Respond:

				The ticket request could not be completed.


				==================================================
				SPECIFIC TICKET RESULT
				==================================================

				When getTicketById returns a ticket, display:

				Ticket ID
				Title
				Description
				Category
				Priority
				Status
				AI Suggestion
				Created At


				==================================================
				STATUS UPDATE RESULT
				==================================================

				When updateTicketStatus successfully updates a
				ticket, display the values returned by the tool.

				Do not invent values.

				Do not change the returned status.

				Example:

				Ticket updated successfully.

				Ticket ID: 17
				Title: Laptop WiFi problem
				Category: NETWORK
				Priority: LOW
				Status: RESOLVED


				==================================================
				GENERAL IT QUESTIONS
				==================================================

				If the user asks a general IT question and no
				ticket information is required, answer normally.

				Example:

				What is DHCP?

				Explain DHCP using normal IT knowledge.


				==================================================
				RESOLUTION REQUEST
				==================================================

				Only provide troubleshooting or resolution steps
				when the user explicitly asks for them.

				Examples:

				"How can I fix ticket 17?"
				"Give me a resolution for ticket 17."
				"How do I solve this issue?"

				For ticket-specific resolution requests,
				retrieve the ticket first.


				==================================================
				FINAL RESPONSE RULES
				==================================================

				Do not mention internal tool calls.

				Do not output JSON for normal ticket requests.

				Do not explain your reasoning.

				Do not invent information.

				Do not create fake ticket records.

				Do not replace database results with general
				knowledge.

				Keep responses concise and professional.
				""").user(message).tools(ticketTools).call().content();
	}

	// =========================================================
	// AI TICKET ANALYSIS
	// =========================================================

	public AiTicketAnalysisResponse analyzeTicket(String message) {

		String response = chatClient.prompt().system("""
				You are an IT Support Ticket Analyzer.

				Analyze the user's IT problem and return
				ONLY valid JSON.

				The JSON must contain exactly these fields:

				category
				priority
				suggestion

				Allowed categories:

				NETWORK
				HARDWARE
				SOFTWARE
				LOGIN
				SECURITY
				OTHER

				Priority rules:

				CRITICAL:

				Use only when there is a major
				business-wide outage, serious security
				incident, data loss, or a critical system
				affecting many users.

				HIGH:

				Use when an important user is completely
				blocked from performing their work, or an
				important business service is unavailable.

				MEDIUM:

				Use for normal work-impacting problems such
				as laptop connectivity issues, application
				problems, or login issues affecting one user.

				LOW:

				Use for minor issues, general questions,
				cosmetic problems, or issues that do not
				significantly affect work.

				Examples:

				WiFi not working for one employee
				-> MEDIUM

				Company-wide network outage
				-> CRITICAL

				Important business application unavailable
				-> HIGH

				Minor display issue
				-> LOW

				Keep the suggestion short and practical.

				Return ONLY JSON.

				Do not include markdown.

				Do not include explanations.

				Example:

				{
				  "category": "NETWORK",
				  "priority": "MEDIUM",
				  "suggestion": "Restart the laptop and reconnect to the office WiFi."
				}
				""").user(message).call().content();

		return parseResponse(response);
	}

	// =========================================================
	// PARSE AI RESPONSE
	// =========================================================

	private AiTicketAnalysisResponse parseResponse(String response) {

		response = response.replace("```json", "").replace("```", "").trim();

		ObjectMapper objectMapper = new ObjectMapper();

		try {

			return objectMapper.readValue(response, AiTicketAnalysisResponse.class);

		} catch (Exception e) {

			throw new RuntimeException("Unable to parse AI response: " + response);
		}
	}
}