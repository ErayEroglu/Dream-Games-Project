package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.service.PartnershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partnerships")
public class PartnershipController {
    private final PartnershipService partnershipService;

    public PartnershipController(PartnershipService partnershipService) {
        this.partnershipService = partnershipService;
    }

    @PostMapping("/accept/{senderId}/{receiverId}")
    public ResponseEntity<Void> acceptPartnership(@PathVariable Long senderId, @PathVariable Long receiverId) {
        partnershipService.acceptPartnership(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{senderId}/{receiverId}")
    public ResponseEntity<Void> rejectPartnership(@PathVariable Long senderId, @PathVariable Long receiverId) {
        partnershipService.rejectPartnership(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping ("/cancel/{senderId}/{receiverId}")
    public ResponseEntity<Void> endPartnership(@PathVariable Long senderId, @PathVariable Long receiverId) {
        partnershipService.endPartnership(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetAllPartnerships() {
        partnershipService.resetAllPartnerships();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending/{receiverId}")
    public ResponseEntity<List<Partnership>> getPendingPartnerships(@PathVariable Long receiverId) {
        List<Partnership> partnerships = partnershipService.getPendingPartnerships(receiverId);
        return ResponseEntity.ok(partnerships);
    }
}