package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser() {
        User newUser = userService.createUser();
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/send-invitation/{senderId}")
    public ResponseEntity<Void> sendInvitation(@PathVariable Long senderId) {
        userService.sendInvitation(senderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/claim-reward/{userId}")
    public ResponseEntity<User> claimReward(@PathVariable Long userId) {
        User updatedUser = userService.claimReward(userId);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/get-invitations/{userId}")
    public ResponseEntity<Partnership>  getInvitations(@PathVariable Long userId) {
        Partnership partnership = userService.getInvitations(userId);
        return ResponseEntity.ok(partnership);
    }

    @GetMapping("/get-suggestions/{userId}")
    public ResponseEntity<List <User>>  getSuggestions(@PathVariable Long userId) {
         List<User> partnerSuggestions = userService.getSuggestions(userId);
        return ResponseEntity.ok(partnerSuggestions);
    }

    @GetMapping("/get/balloons-info/{userId}")
    public ResponseEntity<Map<String, Object>> getBalloonsInfo(@PathVariable Long userId) {
        Map<String, Object> balloonsInfo = userService.getBalloonsInfo(userId);
        return ResponseEntity.ok(balloonsInfo);
    }

    @GetMapping("/get-leaderboard")
    public ResponseEntity<List<User>> getLeaderboard() {
        List<User> leaderboard = userService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @PutMapping("/level-up/{userId}")
    public ResponseEntity<Map<String,Object>> updateUserLevel(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.updateUserLevel(userId));
    }

    @PutMapping("/set-coin/{userId}")
    public ResponseEntity<Map<String,Object>> updateUserCoin(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.updateUserLevel(userId));
    }

    @PutMapping("/update-coins/{userId}/{amount}")
    public ResponseEntity<User> updateUserCoin(@PathVariable Long userId, @PathVariable int amount) {
        User updatedUser = userService.updateUserCoin(userId, amount);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/update-helium-count/{userId}/{amount}")
    public ResponseEntity<User> updateUserHeliumCount(@PathVariable Long userId, @PathVariable int amount) {
        User updatedUser = userService.updateUserHeliumCount(userId, amount);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/update-balloon-progress/{userId}/{amount}")
    public ResponseEntity<Partnership> updateBalloonProgress(@PathVariable Long userId, @PathVariable int amount) {
        Partnership updatedPartnership = userService.updateBalloonProgress(userId, amount);
        return ResponseEntity.ok(updatedPartnership);
    }

    // used for testing purposes to update the user level over 50
    // won't be used in production
    @PutMapping("/set-level/{userId}/{newLevel}")
    public ResponseEntity<User> setUserLevel(@PathVariable Long userId, @PathVariable int newLevel) {
        User updatedUser = userService.setUserLevel(userId, newLevel);
        return ResponseEntity.ok(updatedUser);
    }
}