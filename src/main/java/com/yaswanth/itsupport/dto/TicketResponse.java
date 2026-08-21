package com.yaswanth.itsupport.dto;

import com.yaswanth.itsupport.enums.TicketPriority;
import com.yaswanth.itsupport.enums.TicketStatus;

import java.time.LocalDateTime;

public class TicketResponse {

	private Long id;
	private String title;
	private String description;
	private String category;
	private TicketPriority priority;
	private TicketStatus status;
	private LocalDateTime createdAt;
	private String aiSuggestion;

	public TicketResponse(Long id, String title, String description, String category, TicketPriority priority,
			TicketStatus status, String aiSuggestion, LocalDateTime createdAt) {

		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.priority = priority;
		this.status = status;
		this.aiSuggestion = aiSuggestion;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getCategory() {
		return category;
	}

	public TicketPriority getPriority() {
		return priority;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getAiSuggestion() {
		return aiSuggestion;
	}

	public void setAiSuggestion(String aiSuggestion) {
		this.aiSuggestion = aiSuggestion;
	}
}