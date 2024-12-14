package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Use H2 by default
@ActiveProfiles("test")
@Transactional
class PartnershipRepositoryTest {

    @Autowired
    private PartnershipRepository partnershipRepository;

    @Autowired
    private UserRepository userRepository;

    private User createAndSaveUser() {
        User user = new User();
        user.setLevel(55);
        user.setCoins(3000);
        user.setAbTestGroup(User.ABTestGroup.GroupA);
        return userRepository.save(user);
    }

    @Test
    void findBySenderAndReceiver() {
        // Arrange
        User sender = createAndSaveUser();
        User receiver = createAndSaveUser();

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);

        // Act
        Optional<Partnership> foundPartnership = partnershipRepository.findBySenderAndReceiver(sender, receiver);

        // Assert
        assertTrue(foundPartnership.isPresent());
        assertEquals(partnership.getId(), foundPartnership.get().getId());
    }

    @Test
    void findByReceiverAndStatus() {
        // Arrange
        User sender = createAndSaveUser();
        User receiver = createAndSaveUser();

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);

        // Act
        List<Partnership> foundPartnerships = partnershipRepository.findByReceiverAndStatus(receiver, Partnership.PartnershipStatus.PENDING);

        // Assert
        assertFalse(foundPartnerships.isEmpty());
        assertEquals(partnership.getId(), foundPartnerships.get(0).getId());
    }

    @Test
    void findByReceiver() {
        // Arrange
        User sender = createAndSaveUser();
        User receiver = createAndSaveUser();

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);

        // Act
        List<Partnership> foundPartnerships = partnershipRepository.findByReceiver(receiver);

        // Assert
        assertFalse(foundPartnerships.isEmpty());
        assertEquals(partnership.getId(), foundPartnerships.get(0).getId());
    }

    @Test
    void findRejectedPartnerships() {
        // Arrange
        User sender = createAndSaveUser();
        User receiver = createAndSaveUser();

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.REJECTED);
        partnershipRepository.save(partnership);

        // Act
        List<Long> rejectedPartnerships = partnershipRepository.findRejectedPartnerships(sender.getId());

        // Assert
        assertFalse(rejectedPartnerships.isEmpty());
        assertEquals(receiver.getId(), rejectedPartnerships.get(0));
    }

    @Test
    void findPendingPartnerships() {
        // Arrange
        User sender = createAndSaveUser();
        User receiver = createAndSaveUser();

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);

        // Act
        List<Long> pendingPartnerships = partnershipRepository.findPendingPartnerships(sender.getId());

        // Assert
        assertFalse(pendingPartnerships.isEmpty());
        assertEquals(receiver.getId(), pendingPartnerships.get(0));
    }

    @Test
    void findAcceptedPartnership() {
        // Arrange
        User sender = createAndSaveUser();
        User receiver = createAndSaveUser();

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.ACCEPTED);
        partnershipRepository.save(partnership);

        // Act
        Optional<Partnership> acceptedPartnership = partnershipRepository.findAcceptedPartnership(sender.getId());

        // Assert
        assertTrue(acceptedPartnership.isPresent());
        assertEquals(partnership.getId(), acceptedPartnership.get().getId());
    }
}