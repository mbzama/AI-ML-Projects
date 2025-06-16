package zama.learning.procureai.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import zama.learning.procureai.api.dto.request.AuctionCreateRequest;
import zama.learning.procureai.api.dto.response.ApiResponseDto;
import zama.learning.procureai.api.dto.response.VendorResponseDto;
import zama.learning.procureai.api.model.RfxEvent;
import zama.learning.procureai.api.model.RfxLineItem;
import zama.learning.procureai.api.model.User;
import zama.learning.procureai.api.model.Vendor;
import zama.learning.procureai.api.model.enums.EventStatus;
import zama.learning.procureai.api.model.enums.EventType;
import zama.learning.procureai.api.repository.RfxEventRepository;
import zama.learning.procureai.api.repository.RfxLineItemRepository;
import zama.learning.procureai.api.repository.UserRepository;
import zama.learning.procureai.api.repository.VendorRepository;
import zama.learning.procureai.api.security.UserPrincipal;

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
@Tag(name = "Admin Management", description = "Administrative operations for managing auctions, vendors, and RFX events")
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
    }    // Vendor Management
    @Operation(summary = "Get all vendors", description = "Retrieve a list of all registered vendors in the system with pagination and filtering options")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vendors retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/vendors")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApiResponseDto<List<VendorResponseDto>>> getAllVendors(
            @Parameter(description = "Filter by approval status", required = false)
            @RequestParam(value = "approved", required = false) Boolean approved,
            @Parameter(description = "Search by company name", required = false)
            @RequestParam(value = "search", required = false) String search) {
        try {
            List<Vendor> vendors;
            
            if (approved != null && search != null) {
                vendors = vendorRepository.findByIsApprovedAndCompanyNameContainingIgnoreCase(approved, search);
            } else if (approved != null) {
                vendors = vendorRepository.findByIsApproved(approved);
            } else if (search != null && !search.trim().isEmpty()) {
                vendors = vendorRepository.findByCompanyNameContainingIgnoreCase(search);
            } else {
                vendors = vendorRepository.findAll();
            }
            
            List<VendorResponseDto> vendorDtos = vendors.stream()
                    .map(VendorResponseDto::new)
                    .toList();
            
            return ResponseEntity.ok(ApiResponseDto.success("Vendors retrieved successfully", vendorDtos));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Failed to retrieve vendors: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get vendor by ID", description = "Retrieve a specific vendor by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vendor retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Vendor not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/vendors/{vendorId}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> getVendorById(
            @Parameter(description = "ID of the vendor to retrieve", required = true)
            @PathVariable Long vendorId) {
        try {
            Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);
            if (vendorOpt.isPresent()) {
                VendorResponseDto vendorDto = new VendorResponseDto(vendorOpt.get());
                return ResponseEntity.ok(ApiResponseDto.success("Vendor retrieved successfully", vendorDto));
            }
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error("Vendor not found with ID: " + vendorId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Failed to retrieve vendor: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get vendor statistics", description = "Get summary statistics about vendors")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/vendors/stats")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApiResponseDto<Object>> getVendorStatistics() {
        try {
            long totalVendors = vendorRepository.count();
            long approvedVendors = vendorRepository.countByIsApproved(true);
            long pendingVendors = vendorRepository.countByIsApproved(false);
            
            var stats = new java.util.HashMap<String, Object>();
            stats.put("totalVendors", totalVendors);
            stats.put("approvedVendors", approvedVendors);
            stats.put("pendingVendors", pendingVendors);
            stats.put("approvalRate", totalVendors > 0 ? (double) approvedVendors / totalVendors * 100 : 0);
            
            return ResponseEntity.ok(ApiResponseDto.success("Vendor statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Failed to retrieve vendor statistics: " + e.getMessage()));
        }
    }    @Operation(summary = "Approve vendor", description = "Approve a vendor registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vendor approved successfully"),
        @ApiResponse(responseCode = "404", description = "Vendor not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/vendors/{vendorId}/approve")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> approveVendor(
            @Parameter(description = "ID of the vendor to approve", required = true)
            @PathVariable Long vendorId) {
        try {
            Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);
            if (vendorOpt.isPresent()) {
                Vendor vendor = vendorOpt.get();
                vendor.setIsApproved(true);
                Vendor savedVendor = vendorRepository.save(vendor);
                VendorResponseDto vendorDto = new VendorResponseDto(savedVendor);
                return ResponseEntity.ok(ApiResponseDto.success("Vendor approved successfully", vendorDto));
            }
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error("Vendor not found with ID: " + vendorId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Failed to approve vendor: " + e.getMessage()));
        }
    }

    @Operation(summary = "Reject vendor", description = "Reject a vendor registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vendor rejected successfully"),
        @ApiResponse(responseCode = "404", description = "Vendor not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/vendors/{vendorId}/reject")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> rejectVendor(
            @Parameter(description = "ID of the vendor to reject", required = true)
            @PathVariable Long vendorId) {
        try {
            Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);
            if (vendorOpt.isPresent()) {
                Vendor vendor = vendorOpt.get();
                vendor.setIsApproved(false);
                Vendor savedVendor = vendorRepository.save(vendor);
                VendorResponseDto vendorDto = new VendorResponseDto(savedVendor);
                return ResponseEntity.ok(ApiResponseDto.success("Vendor rejected successfully", vendorDto));
            }
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error("Vendor not found with ID: " + vendorId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Failed to reject vendor: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create new vendor", description = "Create a new vendor registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vendor created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid vendor data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required"),
        @ApiResponse(responseCode = "409", description = "Vendor already exists")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/vendors")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> createVendor(
            @Parameter(description = "Vendor data to create", required = true)
            @RequestBody Vendor vendor) {
        try {
            // Check if vendor with same registration number or tax ID already exists
            if (vendor.getRegistrationNumber() != null && 
                vendorRepository.existsByRegistrationNumber(vendor.getRegistrationNumber())) {
                return ResponseEntity.status(409)
                        .body(ApiResponseDto.error("Vendor with registration number " + vendor.getRegistrationNumber() + " already exists"));
            }
            
            if (vendor.getTaxId() != null && 
                vendorRepository.existsByTaxId(vendor.getTaxId())) {
                return ResponseEntity.status(409)
                        .body(ApiResponseDto.error("Vendor with tax ID " + vendor.getTaxId() + " already exists"));
            }
            
            // Set creation timestamp and default approval status
            vendor.setCreatedAt(LocalDateTime.now());
            vendor.setUpdatedAt(LocalDateTime.now());
            if (vendor.getIsApproved() == null) {
                vendor.setIsApproved(false); // Default to pending approval
            }
            
            Vendor savedVendor = vendorRepository.save(vendor);
            VendorResponseDto vendorDto = new VendorResponseDto(savedVendor);
            
            return ResponseEntity.status(201)
                    .body(ApiResponseDto.success("Vendor created successfully", vendorDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to create vendor: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update vendor", description = "Update an existing vendor's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vendor updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vendor not found"),
        @ApiResponse(responseCode = "400", description = "Invalid vendor data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required"),
        @ApiResponse(responseCode = "409", description = "Duplicate vendor data")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/vendors/{vendorId}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApiResponseDto<VendorResponseDto>> updateVendor(
            @Parameter(description = "ID of the vendor to update", required = true)
            @PathVariable Long vendorId,
            @Parameter(description = "Updated vendor data", required = true)
            @RequestBody Vendor vendorUpdate) {
        try {
            Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);
            if (!vendorOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(ApiResponseDto.error("Vendor not found with ID: " + vendorId));
            }
            
            Vendor existingVendor = vendorOpt.get();
            
            // Check for duplicate registration number or tax ID (excluding current vendor)
            if (vendorUpdate.getRegistrationNumber() != null && 
                !vendorUpdate.getRegistrationNumber().equals(existingVendor.getRegistrationNumber()) &&
                vendorRepository.existsByRegistrationNumber(vendorUpdate.getRegistrationNumber())) {
                return ResponseEntity.status(409)
                        .body(ApiResponseDto.error("Another vendor with registration number " + vendorUpdate.getRegistrationNumber() + " already exists"));
            }
            
            if (vendorUpdate.getTaxId() != null && 
                !vendorUpdate.getTaxId().equals(existingVendor.getTaxId()) &&
                vendorRepository.existsByTaxId(vendorUpdate.getTaxId())) {
                return ResponseEntity.status(409)
                        .body(ApiResponseDto.error("Another vendor with tax ID " + vendorUpdate.getTaxId() + " already exists"));
            }
            
            // Update vendor fields
            if (vendorUpdate.getCompanyName() != null) {
                existingVendor.setCompanyName(vendorUpdate.getCompanyName());
            }
            if (vendorUpdate.getRegistrationNumber() != null) {
                existingVendor.setRegistrationNumber(vendorUpdate.getRegistrationNumber());
            }
            if (vendorUpdate.getTaxId() != null) {
                existingVendor.setTaxId(vendorUpdate.getTaxId());
            }
            if (vendorUpdate.getAddress() != null) {
                existingVendor.setAddress(vendorUpdate.getAddress());
            }
            if (vendorUpdate.getCity() != null) {
                existingVendor.setCity(vendorUpdate.getCity());
            }
            if (vendorUpdate.getState() != null) {
                existingVendor.setState(vendorUpdate.getState());
            }
            if (vendorUpdate.getPostalCode() != null) {
                existingVendor.setPostalCode(vendorUpdate.getPostalCode());
            }
            if (vendorUpdate.getCountry() != null) {
                existingVendor.setCountry(vendorUpdate.getCountry());
            }
            if (vendorUpdate.getContactPerson() != null) {
                existingVendor.setContactPerson(vendorUpdate.getContactPerson());
            }
            if (vendorUpdate.getIsApproved() != null) {
                existingVendor.setIsApproved(vendorUpdate.getIsApproved());
            }
            
            existingVendor.setUpdatedAt(LocalDateTime.now());
            
            Vendor savedVendor = vendorRepository.save(existingVendor);
            VendorResponseDto vendorDto = new VendorResponseDto(savedVendor);
            
            return ResponseEntity.ok(ApiResponseDto.success("Vendor updated successfully", vendorDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to update vendor: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete vendor", description = "Delete a vendor from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vendor deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Vendor not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required"),
        @ApiResponse(responseCode = "409", description = "Cannot delete vendor with active bids/responses")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/vendors/{vendorId}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApiResponseDto<Object>> deleteVendor(
            @Parameter(description = "ID of the vendor to delete", required = true)
            @PathVariable Long vendorId) {
        try {
            Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);
            if (!vendorOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(ApiResponseDto.error("Vendor not found with ID: " + vendorId));
            }
            
            Vendor vendor = vendorOpt.get();
            
            // Check if vendor has any active bids or RFQ responses
            // Note: You might want to implement these checks based on your business logic
            // For now, we'll allow deletion but you can add constraints later
            
            vendorRepository.delete(vendor);
            
            return ResponseEntity.ok(ApiResponseDto.success("Vendor deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Failed to delete vendor: " + e.getMessage()));
        }
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
    }    @Operation(summary = "Get all auctions", description = "Retrieve a list of all auctions in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Auctions retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RfxEvent.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/auctions")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<List<RfxEvent>> getAllAuctions() {
        List<RfxEvent> auctions = rfxEventRepository.findByEventType(EventType.AUCTION);
        return ResponseEntity.ok(auctions);
    }

    @Operation(summary = "Get auction by ID", description = "Retrieve a specific auction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Auction found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RfxEvent.class))),
        @ApiResponse(responseCode = "404", description = "Auction not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/auctions/{auctionId}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> getAuction(
            @Parameter(description = "ID of the auction to retrieve", required = true)
            @PathVariable Long auctionId) {
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
