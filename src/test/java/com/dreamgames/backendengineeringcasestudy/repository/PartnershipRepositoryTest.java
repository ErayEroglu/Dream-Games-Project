package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PartnershipRepositoryTest {

    @Autowired
    private PartnershipRepository partnershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findBySenderAndReceiver() {
        User sender = new User();
        sender.setId(1L);
        userRepository.save(sender);

        User receiver = new User();
        receiver.setId(2L);
        userRepository.save(receiver);

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);

        Optional<Partnership> foundPartnership = partnershipRepository.findBySenderAndReceiver(sender, receiver);
        assertTrue(foundPartnership.isPresent());
        assertEquals(partnership.getId(), foundPartnership.get().getId());
    }

    @Test
    void findByReceiverAndStatus() {
        User sender = new User();
        User receiver = new User();

        sender.setId(1L);
        receiver.setId(2L);

        userRepository.save(receiver);
        userRepository.save(sender);

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);

        List<Partnership> foundPartnerships = partnershipRepository.findByReceiverAndStatus(receiver, Partnership.PartnershipStatus.PENDING);
        assertFalse(foundPartnerships.isEmpty());
        assertEquals(partnership.getId(), foundPartnerships.get(0).getId());
    }

    @Test
    void findByReceiver() {
        User sender = new User();
        User receiver = new User();

        sender.setId(1L);
        receiver.setId(2L);

        userRepository.save(receiver);
        userRepository.save(sender);

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);

        List<Partnership> foundPartnerships = partnershipRepository.findByReceiver(receiver);
        assertFalse(foundPartnerships.isEmpty());
        assertEquals(partnership.getId(), foundPartnerships.get(0).getId());
    }

    @Test
    void findRejectedPartnerships() {
        User sender = new User();
        User receiver = new User();

        sender.setId(1L);
        receiver.setId(2L);

        userRepository.save(receiver);
        userRepository.save(sender);

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.REJECTED);
        partnershipRepository.save(partnership);

        List<Long> rejectedPartnerships = partnershipRepository.findRejectedPartnerships(sender.getId());
        assertFalse(rejectedPartnerships.isEmpty());
        assertEquals(receiver.getId(), rejectedPartnerships.get(0));
    }

    @Test
    void findPendingPartnerships() {
        User sender = new User();
        User receiver = new User();

        sender.setId(1L);
        receiver.setId(2L);

        userRepository.save(receiver);
        userRepository.save(sender);

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);
        partnershipRepository.save(partnership);

        List<Long> pendingPartnerships = partnershipRepository.findPendingPartnerships(sender.getId());
        assertFalse(pendingPartnerships.isEmpty());
        assertEquals(receiver.getId(), pendingPartnerships.get(0));
    }

    @Test
    void findAcceptedPartnership() {
        User sender = new User();
        User receiver = new User();

        sender.setId(1L);
        receiver.setId(2L);

        userRepository.save(receiver);
        userRepository.save(sender);

        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.ACCEPTED);
        partnershipRepository.save(partnership);

        Optional<Partnership> acceptedPartnership = partnershipRepository.findAcceptedPartnership(sender.getId());
        assertTrue(acceptedPartnership.isPresent());
        assertEquals(partnership.getId(), acceptedPartnership.get().getId());
    }
}