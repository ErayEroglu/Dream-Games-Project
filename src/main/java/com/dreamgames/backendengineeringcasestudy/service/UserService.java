package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.repository.PartnershipRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PartnershipRepository partnershipRepository;

    public UserService(UserRepository userRepository, PartnershipRepository partnershipRepository) {
        this.userRepository = userRepository;
        this.partnershipRepository = partnershipRepository;
    }

    public User createUser() {
        User user = new User();
        return userRepository.save(user);
    }

    public User updateUserLevel(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLevel(user.getLevel() + 1);
        user.setCoins(user.getCoins() + 100);

        return userRepository.save(user);
    }

    // used for testing purposes
    // updates user level to a specific value, instead of incrementing it
    // won't be used in production
    public User setUserLevel(Long userId, int newLevel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLevel(newLevel);
        return userRepository.save(user);
    }

    public User updateUserCoin(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setCoins(user.getCoins() + amount);
        return userRepository.save(user);
    }

    public User updateUserHeliumCount(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setHeliumCount(user.getHeliumCount() + amount);
        return userRepository.save(user);
    }

    public User claimReward(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int reward = calculateReward(user.getAbTestGroup());
        user.setCoins(user.getCoins() + reward);

        return userRepository.save(user);
    }

    public Partnership getInvitation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Partnership> allPartnerships = partnershipRepository.findByReceiver(user);
        allPartnerships.removeIf(partnership -> partnership.getStatus()
                != Partnership.PartnershipStatus.PENDING);
        if (allPartnerships.isEmpty() ){
            throw new IllegalArgumentException("No pending partnerships found");
        }
        return allPartnerships.get(0);
    }

    public void sendInvitation(Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        List<User> suggestions = getSuggestions(senderId);
        if (suggestions.isEmpty()) {
            throw new IllegalArgumentException("No available players are found");
        }
        User receiver =  suggestions.get(0);

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);
    }

    public List<User> getSuggestions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Partnership> acceptedPartnership = partnershipRepository.findAcceptedPartnership(userId);
        if (acceptedPartnership.isPresent())
            throw new IllegalArgumentException("User already has a partner");

        List<Long> rejectedUserIds = partnershipRepository.findRejectedPartnerships(userId);
        List<Long> pendingUserIds = partnershipRepository.findPendingPartnerships(userId);
        return userRepository.findSuggestions(userId, user.getAbTestGroup(), rejectedUserIds, pendingUserIds);
    }

    public void updateBalloonProgress(Long userId, int progress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Optional<Partnership> acceptedPartnership = partnershipRepository.findAcceptedPartnership(userId);
        if (acceptedPartnership.isEmpty())
            throw new IllegalArgumentException("No accepted partnership found, to update the balloon progress there " +
                    "should be an accepted partnership");

        Partnership partnership = acceptedPartnership.get();
        partnership.setBalloonProgress(partnership.getBalloonProgress() + progress);
        user.setHeliumCount(user.getHeliumCount() - progress);
        userRepository.save(user);

        if (partnership.getBalloonProgress() >= partnership.getInflationThreshold()) {
             partnership.setStatus(Partnership.PartnershipStatus.ACCEPTED);
             User sender = partnership.getSender();
             User receiver = partnership.getReceiver();

             claimReward(sender.getId());
             claimReward(receiver.getId());
        }
        partnershipRepository.save(partnership);
    }

    private int calculateReward(User.ABTestGroup group) {
        return group == User.ABTestGroup.GroupA ? 1000 : 1500;
    }
}