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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnershipServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PartnershipRepository partnershipRepository;

    @InjectMocks
    private PartnershipService partnershipService;

    @Test
    void acceptPartnership() {
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);

        if (LocalDateTime.now(ZoneOffset.UTC).getHour() < 8 || LocalDateTime.now(ZoneOffset.UTC).getHour() > 22) {
            assertThrows(IllegalArgumentException.class, () -> partnershipService.acceptPartnership(1L, 2L));
        } else {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
            when(partnershipRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(partnership));
            partnershipService.acceptPartnership(1L, 2L);
            assertEquals(Partnership.PartnershipStatus.ACCEPTED, partnership.getStatus());
            assertEquals(2L, sender.getPartnerID());
            assertEquals(1L, receiver.getPartnerID());
            verify(userRepository, times(2)).save(any(User.class));
            verify(partnershipRepository, times(1)).save(partnership);
            verify(partnershipRepository, times(2)).deleteAll(anyList());
        }
    }

    @Test
    void rejectPartnership() {
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.PENDING);

        if (LocalDateTime.now(ZoneOffset.UTC).getHour() < 8 || LocalDateTime.now(ZoneOffset.UTC).getHour() > 22) {
            assertThrows(IllegalArgumentException.class, () -> partnershipService.rejectPartnership(1L, 2L));
        } else {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
            when(partnershipRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(partnership));
            partnershipService.rejectPartnership(1L, 2L);

            assertEquals(Partnership.PartnershipStatus.REJECTED, partnership.getStatus());
            verify(partnershipRepository, times(1)).save(partnership);
        }
    }

    @Test
    void getPendingPartnerships() {
        User receiver = new User();
        receiver.setId(2L);
        Partnership partnership = new Partnership();
        partnership.setReceiver(receiver);
        partnership.setStatus(Partnership.PartnershipStatus.PENDING);

        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(partnershipRepository.findByReceiverAndStatus(receiver, Partnership.PartnershipStatus.PENDING)).thenReturn(List.of(partnership));

        List<Partnership> pendingPartnerships = partnershipService.getPendingPartnerships(2L);

        assertFalse(pendingPartnerships.isEmpty());
        assertEquals(Partnership.PartnershipStatus.PENDING, pendingPartnerships.get(0).getStatus());
        verify(userRepository, times(1)).findById(2L);
        verify(partnershipRepository, times(1)).findByReceiverAndStatus(receiver, Partnership.PartnershipStatus.PENDING);
    }

    @Test
    void endPartnership() {
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        Partnership partnership = new Partnership(sender, receiver, Partnership.PartnershipStatus.ACCEPTED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(partnershipRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(partnership));

        partnershipService.endPartnership(1L, 2L);

        assertNull(sender.getPartnerID());
        assertNull(receiver.getPartnerID());
        verify(userRepository, times(2)).save(any(User.class));
        verify(partnershipRepository, times(1)).delete(partnership);
    }

    @Test
    void resetAllPartnerships() {
        partnershipService.resetAllPartnerships();
        verify(partnershipRepository, times(1)).deleteAll();
    }
}