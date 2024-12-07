package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/level-up/{userId}")
    public ResponseEntity<User> updateUserLevel(@PathVariable Long userId) {
        User updatedUser = userService.updateUserLevel(userId);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/set-coin/{userId}")
    public ResponseEntity<User> updateUserCoin(@PathVariable Long userId) {
        User updatedUser = userService.updateUserLevel(userId);
        return ResponseEntity.ok(updatedUser);
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

    // used for testing purposes to update the user level over 50
    // won't be used in production
    @PutMapping("/set-level/{userId}/{newLevel}")
    public ResponseEntity<User> setUserLevel(@PathVariable Long userId, @PathVariable int newLevel) {
        User updatedUser = userService.setUserLevel(userId, newLevel);
        return ResponseEntity.ok(updatedUser);
    }
}