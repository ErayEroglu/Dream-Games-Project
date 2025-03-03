package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.repository.PartnershipRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.util.*;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PartnershipRepository partnershipRepository;

    public UserService(UserRepository userRepository, PartnershipRepository partnershipRepository) {
        this.userRepository = userRepository;
        this.partnershipRepository = partnershipRepository;
    }

    // creates a new user and saves it to the database
    public User createUser() {
        User user = new User();
        return userRepository.save(user);
    }

    // updates the user level by 1 and adds 100 coins to the user
    public Map<String, Object> updateUserLevel(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLevel(user.getLevel() + 1);
        user.setCoins(user.getCoins() + 100);
        userRepository.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("level", user.getLevel());
        response.put("coins", user.getCoins());
        return response;
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

    // updates the user coins by the given amount
    public User updateUserCoin(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setCoins(user.getCoins() + amount);
        return userRepository.save(user);
    }

    // updates the user helium count by the given amount
    public User updateUserHeliumCount(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setHeliumCount(user.getHeliumCount() + amount);
        return userRepository.save(user);
    }

    // updates the user AB test group
    public User claimReward(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int reward = calculateReward(user.getAbTestGroup());
        user.setCoins(user.getCoins() + reward);

        return userRepository.save(user);
    }

    // return pending partnership invitations for the user
    public Partnership getInvitations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Partnership> allPartnerships = new ArrayList<>(partnershipRepository.findByReceiver(user));
        allPartnerships.removeIf(partnership -> partnership.getStatus()
                != Partnership.PartnershipStatus.PENDING);
        if (allPartnerships.isEmpty() ){
            throw new IllegalArgumentException("No pending partnerships found");
        }
        return allPartnerships.get(0);
    }

    // sends an invitation to a suggested user
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

    // returns a list of suggested users for the user
    public List<User> getSuggestions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getLevel() < 50 || user.getCoins() < 2500)
            throw new IllegalArgumentException("User does not meet the requirements to get suggestions");

        Optional<Partnership> acceptedPartnership = partnershipRepository.findAcceptedPartnership(userId);
        if (acceptedPartnership.isPresent())
            throw new IllegalArgumentException("User already has a partner");

        List<Long> rejectedUserIds = partnershipRepository.findRejectedPartnerships(userId);
        List<Long> pendingUserIds = partnershipRepository.findPendingPartnerships(userId);
        return userRepository.findSuggestions(userId, user.getAbTestGroup(), rejectedUserIds, pendingUserIds);
    }

    // updates the balloon progress by consuming helium and checks if the balloon is inflated
    public Partnership updateBalloonProgress(Long userId, int progress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Optional<Partnership> acceptedPartnership = partnershipRepository.findAcceptedPartnership(userId);
        if (acceptedPartnership.isEmpty())
            throw new IllegalArgumentException("No accepted partnership found, to update the balloon progress there " +
                    "should be an accepted partnership");

        if (progress > user.getHeliumCount())
            throw new IllegalArgumentException("User does not have enough helium to update the balloon progress");

        Partnership partnership = acceptedPartnership.get();
        partnership.setBalloonProgress(partnership.getBalloonProgress() + progress);
        user.setHeliumCount(user.getHeliumCount() - progress);
        userRepository.save(user);

        // if the balloon is popped, users should claim their rewards
        if (partnership.getBalloonProgress() >= partnership.getInflationThreshold()) {
             partnership.setStatus(Partnership.PartnershipStatus.ACCEPTED);
             User sender = partnership.getSender();
             User receiver = partnership.getReceiver();

             claimReward(sender.getId());
             claimReward(receiver.getId());
        }
        partnershipRepository.save(partnership);
        return partnership;
    }

    // returns the current balloon progress and partnership details
    public Map<String, Object> getBalloonsInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Optional<Partnership> acceptedPartnership = partnershipRepository.findAcceptedPartnership(userId);
        if (acceptedPartnership.isEmpty())
            throw new IllegalArgumentException("No accepted partnership found, to get the balloons info there " +
                    "should be an accepted partnership");

        Partnership partnership = acceptedPartnership.get();
        Map<String, Object> response = new HashMap<>();
        response.put("inflatingScore", partnership.getBalloonProgress());
        response.put("inflationThreshold", partnership.getInflationThreshold());
        response.put("unusedHeliumCount", user.getHeliumCount());
        response.put("partnerDetails", Map.of(
                "senderId", partnership.getSender().getId(),
                "receiverId", partnership.getReceiver().getId()
        ));
        return response;
    }

    // returns the leaderboard of the top 100 users
    public List<Map<String, Object>> getLeaderboard() {
        return userRepository.findTop100Users();
    }

    private int calculateReward(User.ABTestGroup group) {
        return group == User.ABTestGroup.GroupA ? 1000 : 1500;
    }
}