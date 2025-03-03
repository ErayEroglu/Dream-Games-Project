package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.repository.PartnershipRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class PartnershipService {
    private final UserRepository userRepository;
    private final PartnershipRepository partnershipRepository;

    public PartnershipService(UserRepository userRepository, PartnershipRepository partnershipRepository) {
        this.userRepository = userRepository;
        this.partnershipRepository = partnershipRepository;
    }

    // accepts the partnership between two users
    public void acceptPartnership(Long senderId, Long receiverId) {
        if (isNotaValidTime()) {
            throw new IllegalArgumentException("Partnership can only be answered between 8:00 and 22:00");
        }

        Partnership partnership = findPartnership(senderId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Partnership not found"));

        if (partnership.getStatus() != Partnership.PartnershipStatus.PENDING) {
            throw new IllegalArgumentException("Partnership is not pending");
        }

        // if one of the users matched with another user after the invitation was sent
        // the partnership should be deleted from db
        if (partnershipRepository.findAcceptedPartnership(senderId).isPresent() ||
                partnershipRepository.findAcceptedPartnership(receiverId).isPresent()) {
            partnershipRepository.delete(partnership);
            throw new IllegalArgumentException("One of the users already has a partner");
        }

        User sender = partnership.getSender();
        User receiver = partnership.getReceiver();

        sender.setPartnerID(receiverId);
        receiver.setPartnerID(senderId);

        userRepository.save(sender);
        userRepository.save(receiver);

        partnership.updateStatus(Partnership.PartnershipStatus.ACCEPTED);
        partnershipRepository.save(partnership);

        List<Partnership> pendingPartnershipsForReceiver = getPendingPartnerships(receiverId);
        partnershipRepository.deleteAll(pendingPartnershipsForReceiver);

        List<Partnership> pendingPartnershipsForSender = getPendingPartnerships(senderId);
        partnershipRepository.deleteAll(pendingPartnershipsForSender);
    }

    // rejects the partnership between two users
    public void rejectPartnership(Long senderId, Long receiverId) {
        if (isNotaValidTime()) {
            throw new IllegalArgumentException("Partnership can only be answered between 8:00 and 22:00");
        }

        Partnership partnership = findPartnership(senderId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Partnership not found"));

        partnership.setStatus(Partnership.PartnershipStatus.REJECTED);
        partnershipRepository.save(partnership);
    }

    // returns the pending partnerships for the user
    public List<Partnership> getPendingPartnerships(Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        return partnershipRepository.findByReceiverAndStatus(receiver, Partnership.PartnershipStatus.PENDING);
    }

    // cancels the partnership request and deletes it from the db
    public void endPartnership(Long senderId, Long receiverId) {
        Partnership partnership = findPartnership(senderId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Partnership not found"));
        User sender = partnership.getSender();
        User receiver = partnership.getReceiver();

        sender.setPartnerID(null);
        receiver.setPartnerID(null);

        userRepository.save(sender);
        userRepository.save(receiver);

        partnershipRepository.delete(partnership);
    }

    // use this when the limited event is over
    public void resetAllPartnerships() {
        partnershipRepository.deleteAll();
    }

    // the partnership can only exist during the live event
    // which means the partnership can only exist between 8:00 and 22:00
    public boolean isNotaValidTime() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int hour = now.getHour();
        return hour < 8 || hour > 22;
    }

    // utility method to find the partnership between two users
    private Optional<Partnership> findPartnership(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        return partnershipRepository.findBySenderAndReceiver(sender, receiver);
    }
}