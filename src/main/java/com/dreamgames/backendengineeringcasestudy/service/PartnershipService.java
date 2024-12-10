package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.repository.PartnershipRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public void sendPartnershipRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and receiver are the same user");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (sender.getPartnerID() != null || receiver.getPartnerID() != null) {
            throw new IllegalArgumentException("One or both users already have partners");
        }

        if (sender.getAbTestGroup() != receiver.getAbTestGroup()) {
            throw new IllegalArgumentException("Users are not in the same AB test group");
        }

        Optional<Partnership> existingPartnership = findPartnership(sender, receiver);

        if (existingPartnership.isPresent()) {
            throw new IllegalArgumentException("A partnership request already exists between these users");
        }

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);
    }

    public void acceptPartnership(Long senderId, Long receiverId) {
        Partnership partnership = findPartnership(senderId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Partnership not found"));

        if (partnership.getStatus() != Partnership.PartnershipStatus.PENDING) {
            throw new IllegalArgumentException("Partnership is not pending");
        }

        User sender = partnership.getSender();
        User receiver = partnership.getReceiver();

        sender.setPartnerID(receiverId);
        receiver.setPartnerID(senderId);

        userRepository.save(sender);
        userRepository.save(receiver);

        partnership.setStatus(Partnership.PartnershipStatus.ACCEPTED);
        partnershipRepository.save(partnership);
    }

    public void rejectPartnership(Long senderId, Long receiverId) {
        Partnership partnership = findPartnership(senderId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Partnership not found"));

        partnership.setStatus(Partnership.PartnershipStatus.REJECTED);
        partnershipRepository.save(partnership);
    }

    public List<Partnership> getPendingPartnerships(Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        return partnershipRepository.findByReceiverAndStatus(receiver, Partnership.PartnershipStatus.PENDING);
    }

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

    public void resetAllPartnerships() {
        partnershipRepository.deleteAll();
    }



    private Optional<Partnership> findPartnership(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        return partnershipRepository.findBySenderAndReceiver(sender, receiver);
    }

    private Optional<Partnership> findPartnership(User sender, User receiver) {
        return partnershipRepository.findBySenderAndReceiver(sender, receiver);
    }

    //TODO: Implement the following methods:
    // show 10 suggestions for a user
    // add session logic for a partnership
    // balloon progress for a user ??? might be an another class and table
}