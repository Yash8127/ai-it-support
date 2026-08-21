package com.yaswanth.itsupport.dto;

public class AiTicketAnalysisResponse {

    private String category;
    private String priority;
    private String suggestion;

    public AiTicketAnalysisResponse() {
    }

    public AiTicketAnalysisResponse(String category, String priority, String suggestion) {
        this.category = category;
        this.priority = priority;
        this.suggestion = suggestion;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}