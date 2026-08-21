package com.yaswanth.itsupport.ai;

import org.springframework.stereotype.Service;

@Service
public class AiPriorityService {

    public String determinePriority(String title, String description) {

        String text = (title + " " + description).toLowerCase();

        // CRITICAL - major outage or many users affected
        if (text.contains("all employees")
                || text.contains("company-wide")
                || text.contains("entire company")
                || text.contains("complete outage")
                || text.contains("system down")
                || text.contains("security breach")
                || text.contains("data loss")) {

            return "CRITICAL";
        }

        // HIGH - important service or user completely blocked
        if (text.contains("production")
                || text.contains("business critical")
                || text.contains("completely blocked")
                || text.contains("cannot work")
                || text.contains("service unavailable")) {

            return "HIGH";
        }

        // LOW - minor or cosmetic problems
        if (text.contains("minor")
                || text.contains("cosmetic")
                || text.contains("icon")
                || text.contains("display issue")
                || text.contains("not important")) {

            return "LOW";
        }

        // Default for normal work-impacting problems
        return "MEDIUM";
    }
}