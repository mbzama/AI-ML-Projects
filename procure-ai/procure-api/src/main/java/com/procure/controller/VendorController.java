package com.procure.controller;

import com.procure.model.*;
import com.procure.model.enums.EventStatus;
import com.procure.repository.*;
import com.procure.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    private RfxEventRepository rfxEventRepository;

    @Autowired
    private RfxLineItemRepository rfxLineItemRepository;

    @Autowired
    private RfqResponseRepository rfqResponseRepository;

    @Autowired
    private AuctionBidRepository auctionBidRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    @PreAuthorize("hasRole('VENDOR')")
    public String vendorAccess() {
        return "Vendor Board.";
    }

    @GetMapping("/events/active")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<RfxEvent>> getActiveEvents() {
        List<RfxEvent> events = rfxEventRepository.findByStatus(EventStatus.PUBLISHED);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<RfxEvent> getEvent(@PathVariable Long eventId) {
        Optional<RfxEvent> eventOpt = rfxEventRepository.findById(eventId);
        return eventOpt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/events/{eventId}/line-items")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<RfxLineItem>> getEventLineItems(@PathVariable Long eventId) {
        List<RfxLineItem> lineItems = rfxLineItemRepository.findByRfxEventId(eventId);
        return ResponseEntity.ok(lineItems);
    }

    // RFQ Response Management
    @PostMapping("/rfq/{eventId}/response")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> submitRfqResponse(@PathVariable Long eventId, 
                                               @RequestBody RfqResponse response, 
                                               Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<Vendor> vendorOpt = vendorRepository.findByUserId(userPrincipal.getId());
        Optional<RfxEvent> eventOpt = rfxEventRepository.findById(eventId);

        if (vendorOpt.isPresent() && eventOpt.isPresent()) {
            response.setVendor(vendorOpt.get());
            response.setRfxEvent(eventOpt.get());
            RfqResponse savedResponse = rfqResponseRepository.save(response);
            return ResponseEntity.ok(savedResponse);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/rfq/responses")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<RfqResponse>> getMyRfqResponses(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<Vendor> vendorOpt = vendorRepository.findByUserId(userPrincipal.getId());
        
        if (vendorOpt.isPresent()) {
            List<RfqResponse> responses = rfqResponseRepository.findByVendorId(vendorOpt.get().getId());
            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.badRequest().build();
    }

    // Auction Bid Management
    @PostMapping("/auction/{eventId}/bid")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> submitAuctionBid(@PathVariable Long eventId, 
                                              @RequestBody AuctionBid bid, 
                                              Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<Vendor> vendorOpt = vendorRepository.findByUserId(userPrincipal.getId());
        Optional<RfxEvent> eventOpt = rfxEventRepository.findById(eventId);

        if (vendorOpt.isPresent() && eventOpt.isPresent()) {
            RfxEvent event = eventOpt.get();
            if (!event.isActive() || event.isExpired()) {
                return ResponseEntity.badRequest().body("Event is not active or has expired");
            }

            bid.setVendor(vendorOpt.get());
            bid.setRfxEvent(event);
            AuctionBid savedBid = auctionBidRepository.save(bid);
            return ResponseEntity.ok(savedBid);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/auction/bids")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<AuctionBid>> getMyAuctionBids(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<Vendor> vendorOpt = vendorRepository.findByUserId(userPrincipal.getId());
        
        if (vendorOpt.isPresent()) {
            List<AuctionBid> bids = auctionBidRepository.findByVendorId(vendorOpt.get().getId());
            return ResponseEntity.ok(bids);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/auction/{eventId}/current-bids")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<AuctionBid>> getCurrentBids(@PathVariable Long eventId) {
        List<AuctionBid> bids = auctionBidRepository.findByRfxEventId(eventId);
        return ResponseEntity.ok(bids);
    }
}
