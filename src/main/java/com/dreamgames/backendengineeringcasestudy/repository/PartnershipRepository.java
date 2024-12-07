package com.dreamgames.backendengineeringcasestudy.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
    public interface PartnershipRepository extends JpaRepository<Partnership, Long> {
        Optional<Partnership> findBySenderAndReceiver(User sender, User receiver);
        List<Partnership> findByReceiverAndStatus(User receiver, Partnership.PartnershipStatus status);
    }

