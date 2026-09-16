package com.yaswanth.itsupport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsResponse {

    private long totalTickets;

    private long openTickets;

    private long inProgressTickets;

    private long resolvedTickets;

    private long closedTickets;

    private long criticalTickets;

    private long highPriorityTickets;

    private long mediumPriorityTickets;

    private long lowPriorityTickets;
}