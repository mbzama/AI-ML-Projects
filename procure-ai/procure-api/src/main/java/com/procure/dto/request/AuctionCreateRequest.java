package com.procure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionCreateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    private String description;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    @Size(max = 3, message = "Currency code must not exceed 3 characters")
    private String currency = "USD";
    
    @Positive(message = "Minimum bid increment must be positive")
    private BigDecimal minimumBidIncrement;
    
    private String termsAndConditions;
    
    // Constructors
    public AuctionCreateRequest() {}
    
    public AuctionCreateRequest(String title, String description, LocalDateTime startDate, 
                               LocalDateTime endDate, String currency, BigDecimal minimumBidIncrement) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currency = currency;
        this.minimumBidIncrement = minimumBidIncrement;
    }
    
    // Getters and Setters
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
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getMinimumBidIncrement() {
        return minimumBidIncrement;
    }
    
    public void setMinimumBidIncrement(BigDecimal minimumBidIncrement) {
        this.minimumBidIncrement = minimumBidIncrement;
    }
    
    public String getTermsAndConditions() {
        return termsAndConditions;
    }
    
    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
