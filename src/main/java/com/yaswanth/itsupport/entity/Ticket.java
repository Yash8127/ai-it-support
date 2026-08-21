package com.yaswanth.itsupport.entity;

import java.time.LocalDateTime;

import com.yaswanth.itsupport.enums.TicketPriority;
import com.yaswanth.itsupport.enums.TicketStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	private String category;

	@Enumerated(EnumType.STRING)
	private TicketPriority priority;

	@Enumerated(EnumType.STRING)
	private TicketStatus status;
	
	@Column(columnDefinition = "TEXT")
	private String aiSuggestion;

	private LocalDateTime createdAt;

	// Getters and Setters
}