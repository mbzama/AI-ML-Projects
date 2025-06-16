package zama.learning.procureai.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import zama.learning.procureai.api.model.RfxEvent;
import zama.learning.procureai.api.model.enums.EventStatus;
import zama.learning.procureai.api.model.enums.EventType;
import zama.learning.procureai.api.repository.RfxEventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Tag(name = "Public API", description = "Public endpoints for accessing auctions and web pages")
public class WebController {

    @Autowired
    private RfxEventRepository rfxEventRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<RfxEvent> activeEvents = rfxEventRepository.findByStatus(EventStatus.PUBLISHED);
        model.addAttribute("activeEvents", activeEvents);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/vendor")
    public String vendor() {
        return "vendor";
    }

    @GetMapping("/approver")
    public String approver() {
        return "approver";
    }    @GetMapping("/create-auction")
    public String createAuction() {
        return "create-auction";
    }    @GetMapping("/auctions")
    public String auctions() {
        return "auctions";
    }

    @GetMapping("/api-docs")
    public String apiDocs() {
        return "redirect:/api/swagger-ui.html";
    }

    // REST API endpoint for public auction listing
    @Operation(summary = "Get public auctions", description = "Retrieve all published auctions available for public viewing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Auctions retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RfxEvent.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/auctions")
    @ResponseBody
    public ResponseEntity<List<RfxEvent>> getPublicAuctions() {
        // Return only published auctions for public view
        List<RfxEvent> publicAuctions = rfxEventRepository.findByEventTypeAndStatus(EventType.AUCTION, EventStatus.PUBLISHED);
        return ResponseEntity.ok(publicAuctions);
    }
}
