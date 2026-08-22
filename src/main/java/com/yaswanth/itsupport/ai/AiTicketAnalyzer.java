package com.yaswanth.itsupport.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.yaswanth.itsupport.dto.AiTicketAnalysisResponse;

import tools.jackson.databind.ObjectMapper;

@Service
public class AiTicketAnalyzer {

	private final ChatClient chatClient;

	public AiTicketAnalyzer(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	public AiTicketAnalysisResponse analyzeTicket(String message) {

		String response = chatClient.prompt().system("""
				You are an IT Support Ticket Classification System.

				Your job is to analyze the user's IT support problem and
				return the correct CATEGORY, PRIORITY, and a short SUGGESTION.

				IMPORTANT:
				Classification must be based ONLY on the user's problem.
				Do not invent information.

				==============================
				ALLOWED CATEGORY VALUES
				==============================

				NETWORK
				HARDWARE
				SOFTWARE
				LOGIN
				SECURITY
				OTHER

				==============================
				ALLOWED PRIORITY VALUES
				==============================

				LOW
				MEDIUM
				HIGH
				CRITICAL

				==============================
				PRIORITY RULES
				==============================

				CRITICAL:
				Use CRITICAL ONLY when the problem causes:
				- Company-wide outage
				- Major business-wide service disruption
				- Serious security incident
				- Major data loss
				- Critical system unavailable for many users

				Examples:
				"The entire company network is down"
				-> CRITICAL

				"All employees cannot access the company network"
				-> CRITICAL

				"Production system is completely unavailable for the company"
				-> CRITICAL


				HIGH:
				Use HIGH when:
				- An important business service is unavailable
				- A user is completely blocked from an important business task
				- A major application is unavailable
				- The problem has significant business impact

				Examples:
				"The payroll application is completely down"
				-> HIGH

				"I cannot access the production application"
				-> HIGH


				MEDIUM:
				Use MEDIUM for normal work-impacting problems
				affecting one user or a small number of users.

				Examples:
				"My laptop cannot connect to WiFi"
				-> MEDIUM

				"WiFi is not working on my laptop"
				-> MEDIUM

				"I cannot login to my laptop"
				-> MEDIUM

				"My application is not working"
				-> MEDIUM

				"My printer is not working"
				-> MEDIUM


				LOW:
				Use LOW ONLY for minor issues that do not significantly
				affect the user's ability to work.

				Examples:
				"The desktop wallpaper looks blurry"
				-> LOW

				"How do I change my desktop wallpaper?"
				-> LOW

				"Minor display customization issue"
				-> LOW


				==============================
				IMPORTANT PRIORITY RULE
				==============================

				A normal laptop, WiFi, printer, login, or application
				problem affecting ONE USER should normally be MEDIUM.

				Do NOT classify a normal single-user IT problem as LOW
				unless it is clearly minor or cosmetic.

				CRITICAL requires evidence of a major or company-wide
				impact.

				HIGH requires significant business impact.

				==============================
				CATEGORY RULES
				==============================

				NETWORK:
				WiFi, internet, LAN, VPN, network connectivity,
				DNS, connection problems.

				HARDWARE:
				Laptop hardware, keyboard, mouse, monitor, printer,
				hard disk, RAM, physical device problems.

				SOFTWARE:
				Application errors, software crashes, installation
				problems, application configuration issues.

				LOGIN:
				Password problems, account login problems,
				authentication problems.

				SECURITY:
				Malware, phishing, suspicious activity,
				unauthorized access, security incidents.

				OTHER:
				Problems that do not clearly belong to the above
				categories.


				==============================
				SUGGESTION RULES
				==============================

				Provide ONE short and practical troubleshooting suggestion.

				Do not provide multiple paragraphs.

				==============================
				OUTPUT FORMAT
				==============================

				Return ONLY valid JSON.

				Do NOT use markdown.

				Do NOT use ```json.

				Do NOT add explanations before or after JSON.

				JSON must contain exactly these fields:

				{
				  "category": "NETWORK",
				  "priority": "MEDIUM",
				  "suggestion": "Restart the laptop and reconnect to the office WiFi."
				}

				The category MUST be one of:
				NETWORK, HARDWARE, SOFTWARE, LOGIN, SECURITY, OTHER

				The priority MUST be one of:
				LOW, MEDIUM, HIGH, CRITICAL
				""").user(message).call().content();

		System.out.println(">>> AI RAW RESPONSE: " + response);

		return parseResponse(response);
	}

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