package zama.learning.procureai.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import zama.learning.procureai.api.dto.response.ApiResponseDto;
import zama.learning.procureai.api.dto.response.VendorResponseDto;
import zama.learning.procureai.api.model.Vendor;
import zama.learning.procureai.api.repository.VendorRepository;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/public")
@Tag(name = "Public API", description = "Public endpoints that don't require authentication")
public class PublicController {

    @Autowired
    private VendorRepository vendorRepository;

    @Operation(summary = "Get approved vendors", description = "Retrieve a list of all approved vendors for public viewing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Approved vendors retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/vendors")
    public ResponseEntity<ApiResponseDto<List<VendorResponseDto>>> getApprovedVendors(
            @Parameter(description = "Search by company name", required = false)
            @RequestParam(value = "search", required = false) String search) {
        try {
            List<Vendor> vendors;
            
            if (search != null && !search.trim().isEmpty()) {
                vendors = vendorRepository.findByIsApprovedAndCompanyNameContainingIgnoreCase(true, search);
            } else {
                vendors = vendorRepository.findByIsApproved(true);
            }
            
            List<VendorResponseDto> vendorDtos = vendors.stream()
                    .map(VendorResponseDto::new)
                    .toList();
            
            return ResponseEntity.ok(ApiResponseDto.success("Approved vendors retrieved successfully", vendorDtos));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Failed to retrieve approved vendors: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get vendor count", description = "Get the total number of approved vendors")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vendor count retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/vendors/count")
    public ResponseEntity<ApiResponseDto<Long>> getApprovedVendorCount() {
        try {
            long count = vendorRepository.countByIsApproved(true);
            return ResponseEntity.ok(ApiResponseDto.success("Approved vendor count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error("Failed to retrieve vendor count: " + e.getMessage()));
        }
    }
}