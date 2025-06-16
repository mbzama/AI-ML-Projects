package com.procure.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.procure.model.enums.EventStatus;
import com.procure.model.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rfx_events")
public class RfxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status = EventStatus.DRAFT;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Size(max = 3)
    @Column(name = "currency")
    private String currency = "USD";

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "minimum_bid_increment", precision = 12, scale = 2)
    private BigDecimal minimumBidIncrement = BigDecimal.valueOf(0.01);

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    @OneToMany(mappedBy = "rfxEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<RfxLineItem> lineItems;

    // Constructors
    public RfxEvent() {}

    public RfxEvent(String title, EventType eventType, User createdBy) {
        this.title = title;
        this.eventType = eventType;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
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

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public BigDecimal getMinimumBidIncrement() {
        return minimumBidIncrement;
    }

    public void setMinimumBidIncrement(BigDecimal minimumBidIncrement) {
        this.minimumBidIncrement = minimumBidIncrement;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<RfxLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<RfxLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == EventStatus.PUBLISHED || status == EventStatus.IN_PROGRESS;
    }

    public boolean isExpired() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }
}
