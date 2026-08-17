package com.yaswanth.itsupport.dto;

import com.yaswanth.itsupport.enums.TicketPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TicketRequest {

	@NotBlank(message = "Title is required")
	@Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
	private String title;

	@NotBlank(message = "Description is required")
	@Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
	private String description;

	@NotBlank(message = "Category is required")
	private String category;

	@NotNull(message = "Priority is required")
	private TicketPriority priority;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public TicketPriority getPriority() {
		return priority;
	}

	public void setPriority(TicketPriority priority) {
		this.priority = priority;
	}
}