package zama.learning.procureai.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import zama.learning.procureai.api.model.*;
import zama.learning.procureai.api.model.enums.BidStatus;
import zama.learning.procureai.api.model.enums.EventStatus;
import zama.learning.procureai.api.repository.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/approver")
public class ApproverController {

    @Autowired
    private RfxEventRepository rfxEventRepository;

    @Autowired
    private RfqResponseRepository rfqResponseRepository;

    @Autowired
    private AuctionBidRepository auctionBidRepository;

    @GetMapping("/test")
    @PreAuthorize("hasRole('APPROVER')")
    public String approverAccess() {
        return "Approver Board.";
    }

    @GetMapping("/events/completed")
    @PreAuthorize("hasRole('APPROVER')")
    public ResponseEntity<List<RfxEvent>> getCompletedEvents() {
        List<RfxEvent> events = rfxEventRepository.findByStatus(EventStatus.COMPLETED);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/events/{eventId}/complete")
    @PreAuthorize("hasRole('APPROVER')")
    public ResponseEntity<?> completeEvent(@PathVariable Long eventId) {
        Optional<RfxEvent> eventOpt = rfxEventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            RfxEvent event = eventOpt.get();
            event.setStatus(EventStatus.COMPLETED);
            rfxEventRepository.save(event);
            return ResponseEntity.ok("Event completed successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/rfq/{eventId}/responses")
    @PreAuthorize("hasRole('APPROVER')")
    public ResponseEntity<List<RfqResponse>> getRfqResponses(@PathVariable Long eventId) {
        List<RfqResponse> responses = rfqResponseRepository.findByRfxEventId(eventId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/rfq/response/{responseId}/award")
    @PreAuthorize("hasRole('APPROVER')")
    public ResponseEntity<?> awardRfqResponse(@PathVariable Long responseId) {
        Optional<RfqResponse> responseOpt = rfqResponseRepository.findById(responseId);
        if (responseOpt.isPresent()) {
            RfqResponse response = responseOpt.get();
            response.setStatus(BidStatus.AWARDED);
            rfqResponseRepository.save(response);

            // Mark event as awarded
            RfxEvent event = response.getRfxEvent();
            event.setStatus(EventStatus.AWARDED);
            rfxEventRepository.save(event);

            return ResponseEntity.ok("Response awarded successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/auction/{eventId}/bids")
    @PreAuthorize("hasRole('APPROVER')")
    public ResponseEntity<List<AuctionBid>> getAuctionBids(@PathVariable Long eventId) {
        List<AuctionBid> bids = auctionBidRepository.findByRfxEventId(eventId);
        return ResponseEntity.ok(bids);
    }

    @PostMapping("/auction/{eventId}/award/{vendorId}")
    @PreAuthorize("hasRole('APPROVER')")
    public ResponseEntity<?> awardAuction(@PathVariable Long eventId, @PathVariable Long vendorId) {
        Optional<RfxEvent> eventOpt = rfxEventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            RfxEvent event = eventOpt.get();
            event.setStatus(EventStatus.AWARDED);
            rfxEventRepository.save(event);

            // Mark winning bids as awarded and others as rejected
            List<AuctionBid> bids = auctionBidRepository.findByRfxEventId(eventId);
            for (AuctionBid bid : bids) {
                if (bid.getVendor().getId().equals(vendorId)) {
                    // This is the winning bid - mark as awarded
                    bid.setIsActive(false);
                } else {
                    // Other bids - mark as inactive
                    bid.setIsActive(false);
                }
                auctionBidRepository.save(bid);
            }

            return ResponseEntity.ok("Auction awarded successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/events/pending-approval")
    @PreAuthorize("hasRole('APPROVER')")
    public ResponseEntity<List<RfxEvent>> getPendingApprovalEvents() {
        List<RfxEvent> events = rfxEventRepository.findByStatus(EventStatus.PUBLISHED);
        return ResponseEntity.ok(events);
    }
}
