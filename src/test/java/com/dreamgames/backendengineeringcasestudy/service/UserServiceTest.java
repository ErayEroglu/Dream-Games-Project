package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.repository.PartnershipRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PartnershipRepository partnershipRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser();

        assertNotNull(createdUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserLevel() {
        User user = new User();
        user.setId(1L);
        user.setLevel(1);
        user.setCoins(100);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Map<String, Object> response = userService.updateUserLevel(1L);

        assertEquals(2, response.get("level"));
        assertEquals(200, response.get("coins"));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void setUserLevel() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.setUserLevel(1L, 10);

        assertEquals(10, updatedUser.getLevel());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserCoin() {
        User user = new User();
        user.setId(1L);
        user.setCoins(100);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUserCoin(1L, 50);

        assertEquals(150, updatedUser.getCoins());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserHeliumCount() {
        User user = new User();
        user.setId(1L);
        user.setHeliumCount(100);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUserHeliumCount(1L, 50);

        assertEquals(150, updatedUser.getHeliumCount());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void claimReward() {
        User user = new User();
        user.setId(1L);
        user.setAbTestGroup(User.ABTestGroup.GroupA);
        user.setCoins(100);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.claimReward(1L);

        assertEquals(1100, updatedUser.getCoins());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getInvitations() {
        User sender = new User();
        User receiver = new User();
        sender.setId(1L);
        receiver.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        when(partnershipRepository.findByReceiver(receiver)).thenReturn(List.of(partnership));

        Partnership foundPartnership = userService.getInvitations(2L);

        assertNotNull(foundPartnership);
        assertEquals(Partnership.PartnershipStatus.PENDING, foundPartnership.getStatus());
        verify(userRepository, times(1)).findById(2L);
        verify(partnershipRepository, times(1)).findByReceiver(receiver);
    }

    @Test
    void sendInvitation() {
        User sender = new User();
        User receiver = new User();
        sender.setId(1L);
        sender.setAbTestGroup(User.ABTestGroup.GroupA);

        receiver.setId(2L);
        receiver.setAbTestGroup(User.ABTestGroup.GroupA);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user.getId() == 1L ? sender : receiver;
        });
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findSuggestions(anyLong(), any(), anyList(), anyList())).thenReturn(List.of(receiver));

        userRepository.save(sender);
        userRepository.save(receiver);

        userService.sendInvitation(1L);

        verify(userRepository, times(2)).save(any(User.class));
        verify(userRepository, times(1)).findSuggestions(anyLong(), any(), anyList(), anyList());
        verify(partnershipRepository, times(1)).save(any(Partnership.class));
    }

    @Test
    void getSuggestions() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(partnershipRepository.findAcceptedPartnership(1L)).thenReturn(Optional.empty());
        when(partnershipRepository.findRejectedPartnerships(1L)).thenReturn(List.of());
        when(partnershipRepository.findPendingPartnerships(1L)).thenReturn(List.of());
        when(userRepository.findSuggestions(anyLong(), any(), anyList(), anyList())).thenReturn(List.of(user));

        List<User> suggestions = userService.getSuggestions(1L);

        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        verify(userRepository, times(1)).findById(1L);
        verify(partnershipRepository, times(1)).findAcceptedPartnership(1L);
        verify(partnershipRepository, times(1)).findRejectedPartnerships(1L);
        verify(partnershipRepository, times(1)).findPendingPartnerships(1L);
        verify(userRepository, times(1)).findSuggestions(anyLong(), any(), anyList(), anyList());
    }

    @Test
    void updateBalloonProgress() {
        User user = new User();
        user.setId(1L);
        user.setHeliumCount(100);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Partnership partnership = new Partnership();
        partnership.setBalloonProgress(0);
        partnership.setInflationThreshold(100);
        when(partnershipRepository.findAcceptedPartnership(1L)).thenReturn(Optional.of(partnership));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(partnershipRepository.save(any(Partnership.class))).thenReturn(partnership);

        Partnership updatedPartnership = userService.updateBalloonProgress(1L, 50);

        assertEquals(50, updatedPartnership.getBalloonProgress());
        assertEquals(50, user.getHeliumCount());
        verify(userRepository, times(1)).findById(1L);
        verify(partnershipRepository, times(1)).findAcceptedPartnership(1L);
        verify(userRepository, times(1)).save(any(User.class));
        verify(partnershipRepository, times(1)).save(any(Partnership.class));
    }

    @Test
    void getBalloonsInfo() {
        User sender = new User();
        sender.setId(1L);
        sender.setHeliumCount(100);
        User receiver = new User();
        receiver.setId(2L);
        receiver.setHeliumCount(100);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.ACCEPTED);

        partnership.setBalloonProgress(50);
        partnership.setInflationThreshold(100);
        when(partnershipRepository.findAcceptedPartnership(1L)).thenReturn(Optional.of(partnership));

        Map<String, Object> balloonsInfo = userService.getBalloonsInfo(1L);

        assertEquals(50, balloonsInfo.get("inflatingScore"));
        assertEquals(100, balloonsInfo.get("inflationThreshold"));
        assertEquals(100, balloonsInfo.get("unusedHeliumCount"));
        verify(userRepository, times(1)).findById(1L);
        verify(partnershipRepository, times(1)).findAcceptedPartnership(1L);
    }

    @Test
    void getLeaderboard() {
        User user = new User();
        when(userRepository.findTop100Users()).thenReturn(List.of(user));

        List<User> leaderboard = userService.getLeaderboard();

        assertNotNull(leaderboard);
        assertFalse(leaderboard.isEmpty());
        verify(userRepository, times(1)).findTop100Users();
    }
}