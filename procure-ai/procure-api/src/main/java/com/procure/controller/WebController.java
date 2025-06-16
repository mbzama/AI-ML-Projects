package com.procure.controller;

import com.procure.model.RfxEvent;
import com.procure.model.enums.EventStatus;
import com.procure.model.enums.EventType;
import com.procure.repository.RfxEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
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
    }

    @GetMapping("/auctions")
    public String auctions() {
        return "auctions";
    }

    // REST API endpoint for public auction listing
    @GetMapping("/api/auctions")
    @ResponseBody
    public ResponseEntity<List<RfxEvent>> getPublicAuctions() {
        // Return only published auctions for public view
        List<RfxEvent> publicAuctions = rfxEventRepository.findByEventTypeAndStatus(EventType.AUCTION, EventStatus.PUBLISHED);
        return ResponseEntity.ok(publicAuctions);
    }
}
