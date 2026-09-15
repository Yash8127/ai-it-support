package com.yaswanth.itsupport.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistoryResponse {

    private Long id;

    private Long ticketId;

    private String username;

    private String action;

    private String oldValue;

    private String newValue;

    private LocalDateTime createdAt;
}