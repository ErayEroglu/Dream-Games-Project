package com.dreamgames.backendengineeringcasestudy.controller;
import com.dreamgames.backendengineeringcasestudy.service.SessionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetAllSessions() {
        sessionService.endAllSessions();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/end/{sessionId}")
    public ResponseEntity<Void> endSession(@PathVariable Long sessionId) {
        sessionService.endSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/start/{partnershipId}")
    public ResponseEntity<Void> startSession(@PathVariable Long partnershipId) {
        sessionService.startSession(partnershipId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-balloon-progress/{sessionId}/{progress}")
    public ResponseEntity<Void> updateBalloonProgress(@PathVariable Long sessionId, @PathVariable int progress) {
        sessionService.updateBalloonProgress(sessionId, progress);
        return ResponseEntity.ok().build();
    }
}
