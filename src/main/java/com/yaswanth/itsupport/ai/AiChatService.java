package com.yaswanth.itsupport.ai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * Stores pending delete confirmation separately for each user.
     *
     * Example:
     *
     * User A:
     * Delete ticket 17
     * -> pendingDeleteTickets["yaswanth"] = 17
     *
     * User B:
     * Delete ticket 25
     * -> pendingDeleteTickets["pooji"] = 25
     *
     * This prevents one user's confirmation from affecting another user.
     */
    private final Map<String, Long> pendingDeleteTickets =
            new ConcurrentHashMap<>();

    public AiChatService(
            ChatClient.Builder chatClientBuilder,
            TicketTools ticketTools,
            TicketService ticketService) {

        this.chatClient = chatClientBuilder.build();
        this.ticketTools = ticketTools;
        this.ticketService = ticketService;
    }

    // =========================================================
    // GET CURRENT USERNAME
    // =========================================================

    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }

    // =========================================================
    // AI CHAT
    // =========================================================

    public String chat(String message) {

        System.out.println(
                ">>> CHAT METHOD CALLED: " + message
        );

        String currentUsername = getCurrentUsername();

        String lowerMessage =
                message.trim().toLowerCase();

        // Get pending delete request for THIS user only
        Long pendingDeleteTicketId =
                pendingDeleteTickets.get(currentUsername);

        // =====================================================
        // 1. HANDLE PENDING DELETE CONFIRMATION
        // =====================================================

        if (pendingDeleteTicketId != null) {

            // -------------------------------------------------
            // USER CONFIRMED
            // -------------------------------------------------

            if (lowerMessage.equals("yes")
                    || lowerMessage.equals("confirm")
                    || lowerMessage.equals("delete it")
                    || lowerMessage.equals("proceed")
                    || lowerMessage.equals("yes, delete it")) {

                Long id = pendingDeleteTicketId;

                /*
                 * Clear state BEFORE tool call.
                 *
                 * This prevents the same confirmation
                 * from being reused accidentally.
                 */
                pendingDeleteTickets.remove(currentUsername);

                System.out.println(
                        ">>> DELETE CONFIRMED"
                        + " - USER: "
                        + currentUsername
                        + " - TICKET: "
                        + id
                );

                return chatClient.prompt()
                        .system("""
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
                                """)
                        .user("Delete ticket " + id)
                        .tools(ticketTools)
                        .call()
                        .content();
            }

            // -------------------------------------------------
            // USER CANCELLED
            // -------------------------------------------------

            if (lowerMessage.equals("no")
                    || lowerMessage.equals("cancel")
                    || lowerMessage.equals("don't delete")
                    || lowerMessage.equals("do not delete")) {

                System.out.println(
                        ">>> DELETE CANCELLED"
                        + " - USER: "
                        + currentUsername
                        + " - TICKET: "
                        + pendingDeleteTicketId
                );

                pendingDeleteTickets.remove(currentUsername);

                return "Ticket deletion cancelled.";
            }

            // -------------------------------------------------
            // INVALID CONFIRMATION RESPONSE
            // -------------------------------------------------

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

            String idText =
                    lowerMessage
                            .substring("delete ticket ".length())
                            .trim();

            try {

                Long id = Long.parseLong(idText);

                System.out.println(
                        ">>> DELETE REQUEST"
                        + " - USER: "
                        + currentUsername
                        + " - TICKET: "
                        + id
                );

                /*
                 * Check whether the ticket exists and whether
                 * the current user is allowed to access it.
                 *
                 * TicketService performs the ownership check.
                 */
                TicketResponse ticket =
                        ticketService.getTicketById(id);

                /*
                 * Store pending delete request ONLY for
                 * the current authenticated user.
                 */
                pendingDeleteTickets.put(
                        currentUsername,
                        id
                );

                System.out.println(
                        ">>> DELETE CONFIRMATION PENDING"
                        + " - USER: "
                        + currentUsername
                        + " - TICKET: "
                        + id
                );

                return """
                        Ticket %d - %s is about to be deleted.

                        Are you sure you want to delete this ticket?

                        Reply "Yes" to delete or "No" to cancel.
                        """.formatted(
                        ticket.getId(),
                        ticket.getTitle()
                );

            } catch (NumberFormatException e) {

                return "Invalid ticket ID.";

            } catch (Exception e) {

                System.out.println(
                        ">>> DELETE REQUEST ERROR: "
                                + e.getMessage()
                );

                return "Ticket could not be found.";
            }
        }

        // =====================================================
        // 3. NORMALIZE MESSAGE
        // =====================================================

        String normalizedMessage =
                lowerMessage
                        .replace("-", " ")
                        .replace("_", " ")
                        .replaceAll("\\s+", " ")
                        .trim();

        // =====================================================
        // 4. CHECK WHETHER THIS IS A TICKET LIST REQUEST
        // =====================================================

        boolean ticketListRequest =
                normalizedMessage.contains("tickets")
                || normalizedMessage.contains("ticket list")
                || normalizedMessage.contains("show me")
                || normalizedMessage.startsWith("show ")
                || normalizedMessage.startsWith("list ")
                || normalizedMessage.startsWith("find ")
                || normalizedMessage.startsWith("display ")
                || normalizedMessage.startsWith("get ");

        // =====================================================
        // 5. DETECT SPECIFIC TICKET ID
        // =====================================================

        boolean specificTicketRequest =
                normalizedMessage.matches(
                        ".*\\bticket\\s+#?\\d+\\b.*"
                );

        // =====================================================
        // 6. DETECT STATUS
        // =====================================================

        String detectedStatus = null;

        if (ticketListRequest && !specificTicketRequest) {

            if (normalizedMessage.matches(
                    ".*\\bopen\\b.*")) {

                detectedStatus = "OPEN";

            } else if (
                    normalizedMessage.contains("in progress")
                    || normalizedMessage.contains("in-progress")
            ) {

                detectedStatus = "IN_PROGRESS";

            } else if (
                    normalizedMessage.matches(
                            ".*\\bresolved\\b.*"
                    )
            ) {

                detectedStatus = "RESOLVED";

            } else if (
                    normalizedMessage.matches(
                            ".*\\bclosed\\b.*"
                    )
            ) {

                detectedStatus = "CLOSED";
            }
        }

        // =====================================================
        // 7. DETECT PRIORITY
        // =====================================================

        String detectedPriority = null;

        if (ticketListRequest && !specificTicketRequest) {

            // -------------------------------------------------
            // CRITICAL
            // -------------------------------------------------

            if (
                    normalizedMessage.contains(
                            "critical priority"
                    )
                    || normalizedMessage.matches(
                            ".*\\bcritical\\b.*tickets?.*"
                    )
                    || normalizedMessage.matches(
                            ".*\\bcritical\\b.*priority.*"
                    )
            ) {

                detectedPriority = "CRITICAL";

            }

            // -------------------------------------------------
            // HIGH
            // -------------------------------------------------

            else if (
                    normalizedMessage.contains(
                            "high priority"
                    )
                    || normalizedMessage.matches(
                            ".*\\bhigh\\b.*tickets?.*"
                    )
                    || normalizedMessage.matches(
                            ".*\\bhigh\\b.*priority.*"
                    )
            ) {

                detectedPriority = "HIGH";

            }

            // -------------------------------------------------
            // MEDIUM
            // -------------------------------------------------

            else if (
                    normalizedMessage.contains(
                            "medium priority"
                    )
                    || normalizedMessage.matches(
                            ".*\\bmedium\\b.*tickets?.*"
                    )
                    || normalizedMessage.matches(
                            ".*\\bmedium\\b.*priority.*"
                    )
            ) {

                detectedPriority = "MEDIUM";

            }

            // -------------------------------------------------
            // LOW
            // -------------------------------------------------

            else if (
                    normalizedMessage.contains(
                            "low priority"
                    )
                    || normalizedMessage.matches(
                            ".*\\blow\\b.*tickets?.*"
                    )
                    || normalizedMessage.matches(
                            ".*\\blow\\b.*priority.*"
                    )
            ) {

                detectedPriority = "LOW";
            }
        }

        // =====================================================
        // 8. STATUS + PRIORITY
        // =====================================================

        if (
                ticketListRequest
                && !specificTicketRequest
                && detectedStatus != null
                && detectedPriority != null
        ) {

            System.out.println(
                    ">>> DETECTED STATUS + PRIORITY: "
                            + detectedStatus
                            + ", "
                            + detectedPriority
            );

            String finalStatus = detectedStatus;
            String finalPriority = detectedPriority;

            return chatClient.prompt()
                    .system("""
                            You are an AI IT Support Assistant.

                            The user's request contains BOTH a ticket
                            status and a ticket priority.

                            You MUST use ONLY:

                            getTicketsByStatusAndPriority

                            Exact status:

                            %s

                            Exact priority:

                            %s

                            IMPORTANT:

                            CRITICAL is a PRIORITY.
                            CRITICAL is NEVER a STATUS.

                            HIGH is a PRIORITY.
                            HIGH is NEVER a STATUS.

                            MEDIUM is a PRIORITY.
                            MEDIUM is NEVER a STATUS.

                            LOW is a PRIORITY.
                            LOW is NEVER a STATUS.

                            OPEN is a STATUS.

                            IN_PROGRESS is a STATUS.

                            RESOLVED is a STATUS.

                            CLOSED is a STATUS.

                            Use the exact values provided above.

                            Do not select another ticket-list tool.

                            The tool result is authoritative.

                            If TOOL_SUCCESS is returned,
                            display ALL returned tickets.

                            For every ticket display:

                            Ticket ID
                            Title
                            Category
                            Priority
                            Status

                            If NO_TICKETS_FOUND is returned:

                            No matching tickets were found.

                            If TOOL_ERROR is returned:

                            The ticket request could not be completed.

                            Do not mention internal tool calls.
                            Do not output JSON.
                            Do not explain reasoning.
                            Do not invent ticket information.
                            """.formatted(
                            finalStatus,
                            finalPriority
                    ))
                    .user(message)
                    .tools(ticketTools)
                    .call()
                    .content();
        }

        // =====================================================
        // 9. PRIORITY ONLY
        // =====================================================

        if (
                ticketListRequest
                && !specificTicketRequest
                && detectedPriority != null
                && detectedStatus == null
        ) {

            System.out.println(
                    ">>> DETECTED PRIORITY ONLY: "
                            + detectedPriority
            );

            String finalPriority = detectedPriority;

            return chatClient.prompt()
                    .system("""
                            You are an AI IT Support Assistant.

                            The user's request contains ONLY a
                            ticket priority.

                            You MUST use ONLY:

                            getTicketsByPriority

                            Exact priority:

                            %s

                            IMPORTANT:

                            CRITICAL = PRIORITY

                            CRITICAL is NEVER a STATUS.

                            HIGH = PRIORITY

                            MEDIUM = PRIORITY

                            LOW = PRIORITY

                            NEVER use:

                            getTicketsByStatus

                            NEVER use:

                            getTicketsByStatusAndPriority

                            Use:

                            getTicketsByPriority

                            with:

                            priority = %s

                            The tool result is authoritative.

                            If TOOL_SUCCESS is returned,
                            display ALL returned tickets.

                            For every ticket display:

                            Ticket ID
                            Title
                            Category
                            Priority
                            Status

                            If NO_TICKETS_FOUND is returned:

                            No matching tickets were found.

                            If TOOL_ERROR is returned:

                            The ticket request could not be completed.

                            Do not mention internal tool calls.
                            Do not output JSON.
                            Do not explain reasoning.
                            Do not invent ticket information.
                            """.formatted(
                            finalPriority,
                            finalPriority
                    ))
                    .user(message)
                    .tools(ticketTools)
                    .call()
                    .content();
        }

        // =====================================================
        // 10. STATUS ONLY
        // =====================================================

        if (
                ticketListRequest
                && !specificTicketRequest
                && detectedStatus != null
                && detectedPriority == null
        ) {

            System.out.println(
                    ">>> DETECTED STATUS ONLY: "
                            + detectedStatus
            );

            String finalStatus = detectedStatus;

            return chatClient.prompt()
                    .system("""
                            You are an AI IT Support Assistant.

                            The user's request contains ONLY a
                            ticket status.

                            You MUST use ONLY:

                            getTicketsByStatus

                            Exact status:

                            %s

                            IMPORTANT:

                            OPEN = STATUS

                            IN_PROGRESS = STATUS

                            RESOLVED = STATUS

                            CLOSED = STATUS

                            CRITICAL = PRIORITY

                            HIGH = PRIORITY

                            MEDIUM = PRIORITY

                            LOW = PRIORITY

                            CRITICAL is NEVER a STATUS.

                            Do not use:

                            getTicketsByPriority

                            Do not use:

                            getTicketsByStatusAndPriority

                            Use:

                            getTicketsByStatus

                            with:

                            status = %s

                            The tool result is authoritative.

                            If TOOL_SUCCESS is returned,
                            display ALL returned tickets.

                            For every ticket display:

                            Ticket ID
                            Title
                            Category
                            Priority
                            Status

                            If NO_TICKETS_FOUND is returned:

                            No matching tickets were found.

                            If TOOL_ERROR is returned:

                            The ticket request could not be completed.

                            Do not mention internal tool calls.
                            Do not output JSON.
                            Do not explain reasoning.
                            Do not invent ticket information.
                            """.formatted(
                            finalStatus,
                            finalStatus
                    ))
                    .user(message)
                    .tools(ticketTools)
                    .call()
                    .content();
        }

        // =====================================================
        // 11. NORMAL AI CHAT
        // =====================================================

        return chatClient.prompt()
                .system("""
                        You are an AI IT Support Assistant.

                        Your job is to answer IT support questions
                        and retrieve existing ticket information
                        using tools.

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

                        Show ticket 17

                        -> getTicketById(id=17)

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

                        IMPORTANT:

                        CRITICAL IS A PRIORITY.

                        CRITICAL IS NEVER A STATUS.

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

                        Then provide practical troubleshooting advice.

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

                        respond:

                        The ticket request could not be completed.

                        Do not invent ticket information.

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

                        When updateTicketStatus successfully updates
                        a ticket, display the values returned by the tool.

                        Do not invent values.

                        Do not change the returned status.

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
                        """)
                .user(message)
                .tools(ticketTools)
                .call()
                .content();
    }

    // =========================================================
    // AI TICKET ANALYSIS
    // =========================================================

    public AiTicketAnalysisResponse analyzeTicket(String message) {

        String response =
                chatClient.prompt()
                        .system("""
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
                                """)
                        .user(message)
                        .call()
                        .content();

        return parseResponse(response);
    }

    // =========================================================
    // PARSE AI RESPONSE
    // =========================================================

    private AiTicketAnalysisResponse parseResponse(
            String response) {

        response = response
                .replace("```json", "")
                .replace("```", "")
                .trim();

        ObjectMapper objectMapper =
                new ObjectMapper();

        try {

            return objectMapper.readValue(
                    response,
                    AiTicketAnalysisResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to parse AI response: "
                            + response
            );
        }
    }
}