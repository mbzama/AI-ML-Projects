package zama.learning.procureai.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rfx_line_items")
public class RfxLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    @ManyToOne
    @JoinColumn(name = "rfx_event_id", nullable = false)
    @JsonBackReference
    private RfxEvent rfxEvent;

    @NotBlank
    @Size(max = 50)
    @Column(name = "item_number")
    private String itemNumber;

    @NotBlank
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "quantity")
    private Integer quantity = 1;

    @Size(max = 20)
    @Column(name = "unit")
    private String unit;

    @Column(name = "estimated_price", precision = 12, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public RfxLineItem() {}

    public RfxLineItem(RfxEvent rfxEvent, String itemNumber, String description, Integer quantity) {
        this.rfxEvent = rfxEvent;
        this.itemNumber = itemNumber;
        this.description = description;
        this.quantity = quantity;
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

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
