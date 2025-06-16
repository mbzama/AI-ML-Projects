package com.procure.controller;

import com.procure.dto.request.AuctionCreateRequest;
import com.procure.model.RfxEvent;
import com.procure.model.RfxLineItem;
import com.procure.model.User;
import com.procure.model.Vendor;
import com.procure.model.enums.EventStatus;
import com.procure.model.enums.EventType;
import com.procure.repository.RfxEventRepository;
import com.procure.repository.RfxLineItemRepository;
import com.procure.repository.UserRepository;
import com.procure.repository.VendorRepository;
import com.procure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RfxEventRepository rfxEventRepository;

    @Autowired
    private RfxLineItemRepository rfxLineItemRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    @PreAuthorize("hasRole('CREATOR')")
    public String adminAccess() {
        return "Admin Board.";
    }

    // Vendor Management
    @GetMapping("/vendors")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendors = vendorRepository.findAll();
        return ResponseEntity.ok(vendors);
    }

    @PostMapping("/vendors/{vendorId}/approve")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> approveVendor(@PathVariable Long vendorId) {
        Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);
        if (vendorOpt.isPresent()) {
            Vendor vendor = vendorOpt.get();
            vendor.setIsApproved(true);
            vendorRepository.save(vendor);
            return ResponseEntity.ok("Vendor approved successfully");
        }
        return ResponseEntity.notFound().build();
    }

    // RFX Event Management
    @PostMapping("/events")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<RfxEvent> createEvent(@RequestBody RfxEvent event, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<User> userOpt = userRepository.findById(userPrincipal.getId());
        
        if (userOpt.isPresent()) {
            event.setCreatedBy(userOpt.get());
            event.setStatus(EventStatus.DRAFT);
            RfxEvent savedEvent = rfxEventRepository.save(event);
            return ResponseEntity.ok(savedEvent);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/events")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<List<RfxEvent>> getAllEvents() {
        List<RfxEvent> events = rfxEventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/events/{eventId}/publish")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> publishEvent(@PathVariable Long eventId) {
        Optional<RfxEvent> eventOpt = rfxEventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            RfxEvent event = eventOpt.get();
            event.setStatus(EventStatus.PUBLISHED);
            if (event.getStartDate() == null) {
                event.setStartDate(LocalDateTime.now());
            }
            rfxEventRepository.save(event);
            return ResponseEntity.ok("Event published successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/events/{eventId}/line-items")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<RfxLineItem> addLineItem(@PathVariable Long eventId, @RequestBody RfxLineItem lineItem) {
        Optional<RfxEvent> eventOpt = rfxEventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            lineItem.setRfxEvent(eventOpt.get());
            RfxLineItem savedLineItem = rfxLineItemRepository.save(lineItem);
            return ResponseEntity.ok(savedLineItem);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/events/{eventId}/line-items")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<List<RfxLineItem>> getEventLineItems(@PathVariable Long eventId) {
        List<RfxLineItem> lineItems = rfxLineItemRepository.findByRfxEventId(eventId);
        return ResponseEntity.ok(lineItems);
    }    // Auction-specific endpoints
    @PostMapping("/auctions")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> createAuction(@Valid @RequestBody AuctionCreateRequest auctionRequest, 
                                          BindingResult bindingResult, Authentication authentication) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                StringBuilder errorMsg = new StringBuilder("Validation errors: ");
                bindingResult.getFieldErrors().forEach(error -> 
                    errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
                );
                return ResponseEntity.badRequest().body(errorMsg.toString());
            }
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Optional<User> userOpt = userRepository.findById(userPrincipal.getId());
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            // Validate dates
            if (auctionRequest.getStartDate().isAfter(auctionRequest.getEndDate())) {
                return ResponseEntity.badRequest().body("Start date must be before end date");
            }
            
            if (auctionRequest.getEndDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("End date must be in the future");
            }

            // Create RfxEvent from DTO
            RfxEvent auction = new RfxEvent();
            auction.setTitle(auctionRequest.getTitle());
            auction.setDescription(auctionRequest.getDescription());
            auction.setEventType(EventType.AUCTION);
            auction.setCreatedBy(userOpt.get());
            auction.setStatus(EventStatus.DRAFT);
            auction.setStartDate(auctionRequest.getStartDate());
            auction.setEndDate(auctionRequest.getEndDate());
            auction.setCurrency(auctionRequest.getCurrency());
            auction.setTermsAndConditions(auctionRequest.getTermsAndConditions());
            
            // Set minimum bid increment with default if not provided
            if (auctionRequest.getMinimumBidIncrement() != null) {
                auction.setMinimumBidIncrement(auctionRequest.getMinimumBidIncrement());
            } else {
                auction.setMinimumBidIncrement(BigDecimal.valueOf(1.00));
            }
            
            RfxEvent savedAuction = rfxEventRepository.save(auction);
            return ResponseEntity.ok(savedAuction);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating auction: " + e.getMessage());
        }
    }

    @GetMapping("/auctions")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<List<RfxEvent>> getAllAuctions() {
        List<RfxEvent> auctions = rfxEventRepository.findByEventType(EventType.AUCTION);
        return ResponseEntity.ok(auctions);
    }

    @GetMapping("/auctions/{auctionId}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> getAuction(@PathVariable Long auctionId) {
        Optional<RfxEvent> auctionOpt = rfxEventRepository.findById(auctionId);
        if (auctionOpt.isPresent() && auctionOpt.get().getEventType() == EventType.AUCTION) {
            return ResponseEntity.ok(auctionOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/auctions/{auctionId}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> updateAuction(@PathVariable Long auctionId, @RequestBody RfxEvent auctionUpdate, Authentication authentication) {
        try {
            Optional<RfxEvent> auctionOpt = rfxEventRepository.findById(auctionId);
            if (auctionOpt.isEmpty() || auctionOpt.get().getEventType() != EventType.AUCTION) {
                return ResponseEntity.notFound().build();
            }

            RfxEvent auction = auctionOpt.get();
            
            // Update fields
            if (auctionUpdate.getTitle() != null) {
                auction.setTitle(auctionUpdate.getTitle());
            }
            if (auctionUpdate.getDescription() != null) {
                auction.setDescription(auctionUpdate.getDescription());
            }
            if (auctionUpdate.getStartDate() != null) {
                auction.setStartDate(auctionUpdate.getStartDate());
            }
            if (auctionUpdate.getEndDate() != null) {
                auction.setEndDate(auctionUpdate.getEndDate());
            }
            if (auctionUpdate.getMinimumBidIncrement() != null) {
                auction.setMinimumBidIncrement(auctionUpdate.getMinimumBidIncrement());
            }
            if (auctionUpdate.getCurrency() != null) {
                auction.setCurrency(auctionUpdate.getCurrency());
            }
            if (auctionUpdate.getTermsAndConditions() != null) {
                auction.setTermsAndConditions(auctionUpdate.getTermsAndConditions());
            }
            
            // Validate dates
            if (auction.getStartDate() != null && auction.getEndDate() != null) {
                if (auction.getStartDate().isAfter(auction.getEndDate())) {
                    return ResponseEntity.badRequest().body("Start date must be before end date");
                }
            }
            
            RfxEvent savedAuction = rfxEventRepository.save(auction);
            return ResponseEntity.ok(savedAuction);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating auction: " + e.getMessage());
        }
    }

    @GetMapping("/auctions/{auctionId}/line-items")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<List<RfxLineItem>> getAuctionLineItems(@PathVariable Long auctionId) {
        Optional<RfxEvent> auctionOpt = rfxEventRepository.findById(auctionId);
        if (auctionOpt.isPresent() && auctionOpt.get().getEventType() == EventType.AUCTION) {
            List<RfxLineItem> lineItems = rfxLineItemRepository.findByRfxEventId(auctionId);
            return ResponseEntity.ok(lineItems);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/auctions/{auctionId}/publish")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> publishAuction(@PathVariable Long auctionId) {
        Optional<RfxEvent> auctionOpt = rfxEventRepository.findById(auctionId);
        if (auctionOpt.isPresent() && auctionOpt.get().getEventType() == EventType.AUCTION) {
            RfxEvent auction = auctionOpt.get();
            
            // Validate auction before publishing
            if (auction.getTitle() == null || auction.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Auction title is required");
            }
            if (auction.getEndDate() == null) {
                return ResponseEntity.badRequest().body("Auction end date is required");
            }
            if (auction.getEndDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("Auction end date must be in the future");
            }
            
            auction.setStatus(EventStatus.PUBLISHED);
            if (auction.getStartDate() == null) {
                auction.setStartDate(LocalDateTime.now());
            }
            rfxEventRepository.save(auction);
            return ResponseEntity.ok("Auction published successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
