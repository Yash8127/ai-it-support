package com.yaswanth.itsupport.controller;

import com.yaswanth.itsupport.dto.DashboardAnalyticsResponse;
import com.yaswanth.itsupport.service.DashboardAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardAnalyticsController {

    private final DashboardAnalyticsService dashboardAnalyticsService;

    public DashboardAnalyticsController(
            DashboardAnalyticsService dashboardAnalyticsService) {
        this.dashboardAnalyticsService = dashboardAnalyticsService;
    }

    @GetMapping("/analytics")
    public DashboardAnalyticsResponse getAnalytics() {
        return dashboardAnalyticsService.getAnalytics();
    }
}