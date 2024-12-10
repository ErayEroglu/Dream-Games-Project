package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    private int calculateReward(User.ABTestGroup group) {
        return group == User.ABTestGroup.GroupA ? 1000 : 1500;
    }
}