package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.Session;
import com.dreamgames.backendengineeringcasestudy.repository.PartnershipRepository;
import com.dreamgames.backendengineeringcasestudy.repository.SessionRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final PartnershipRepository partnershipRepository;
    private final UserRepository userRepository;

    public SessionService(SessionRepository sessionRepository, PartnershipRepository partnershipRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.partnershipRepository = partnershipRepository;
        this.userRepository = userRepository;
    }

    public void startSession(Long partnershipId) {
        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new RuntimeException("Partnership not found"));
        Session session = new Session(partnership, true);
        sessionRepository.save(session);
    }

    public void endSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setActive(false);
        sessionRepository.save(session);
    }

    public void deleteSession(Long sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    public void endAllSessions() {
        sessionRepository.deleteAll();
    }

    public void updateBalloonProgress(Long sessionId, int progress) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setBalloonProgress(progress);
        sessionRepository.save(session);
    }
}