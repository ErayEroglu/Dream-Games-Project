package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findSuggestions() {
        Long userId = 1L;
        User.ABTestGroup abTestGroup = User.ABTestGroup.GroupA;
        List<Long> rejectedUserIds = List.of(2L, 3L);
        List<Long> pendingUserIds = List.of(4L, 5L);

        List<User> suggestions = userRepository.findSuggestions(userId, abTestGroup, rejectedUserIds, pendingUserIds);

        assertNotNull(suggestions);
        assertTrue(suggestions.size() <= 10);
        suggestions.forEach(user -> {
            assertNotEquals(userId, user.getId());
            assertNull(user.getPartnerID());
            assertFalse(rejectedUserIds.contains(user.getId()));
            assertFalse(pendingUserIds.contains(user.getId()));
        });
    }

    @Test
    void findTop100Users() {
        List<User> topUsers = userRepository.findTop100Users();

        assertNotNull(topUsers);
        assertTrue(topUsers.size() <= 100);
        for (int i = 1; i < topUsers.size(); i++) {
            assertTrue(topUsers.get(i - 1).getLevel() >= topUsers.get(i).getLevel());
        }
    }
}