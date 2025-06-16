package com.procure.model;

import com.procure.model.enums.BidStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rfq_responses", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"rfx_event_id", "vendor_id", "line_item_id"}))
public class RfqResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rfx_event_id", nullable = false)
    private RfxEvent rfxEvent;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "line_item_id", nullable = false)
    private RfxLineItem lineItem;

    @NotNull
    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @NotNull
    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "delivery_time_days")
    private Integer deliveryTimeDays;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BidStatus status = BidStatus.SUBMITTED;

    @CreationTimestamp
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // Constructors
    public RfqResponse() {}

    public RfqResponse(RfxEvent rfxEvent, Vendor vendor, RfxLineItem lineItem, BigDecimal unitPrice) {
        this.rfxEvent = rfxEvent;
        this.vendor = vendor;
        this.lineItem = lineItem;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(lineItem.getQuantity()));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RfxEvent getRfxEvent() {
        return rfxEvent;
    }

    public void setRfxEvent(RfxEvent rfxEvent) {
        this.rfxEvent = rfxEvent;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public RfxLineItem getLineItem() {
        return lineItem;
    }

    public void setLineItem(RfxLineItem lineItem) {
        this.lineItem = lineItem;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (lineItem != null && lineItem.getQuantity() != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(lineItem.getQuantity()));
        }
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getDeliveryTimeDays() {
        return deliveryTimeDays;
    }

    public void setDeliveryTimeDays(Integer deliveryTimeDays) {
        this.deliveryTimeDays = deliveryTimeDays;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public BidStatus getStatus() {
        return status;
    }

    public void setStatus(BidStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
