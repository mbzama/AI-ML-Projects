package zama.learning.procureai.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_bids")
public class AuctionBid {
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
    @Column(name = "bid_amount", precision = 12, scale = 2)
    private BigDecimal bidAmount;

    @CreationTimestamp
    @Column(name = "bid_time")
    private LocalDateTime bidTime;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    // Constructors
    public AuctionBid() {}

    public AuctionBid(RfxEvent rfxEvent, Vendor vendor, RfxLineItem lineItem, BigDecimal bidAmount) {
        this.rfxEvent = rfxEvent;
        this.vendor = vendor;
        this.lineItem = lineItem;
        this.bidAmount = bidAmount;
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

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
