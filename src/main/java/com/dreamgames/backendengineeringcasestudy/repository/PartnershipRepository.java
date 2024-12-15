package com.dreamgames.backendengineeringcasestudy.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
    public interface PartnershipRepository extends JpaRepository<Partnership, Long> {
        Optional<Partnership> findBySenderAndReceiver(User sender, User receiver);
        List<Partnership> findByReceiverAndStatus(User receiver, Partnership.PartnershipStatus status);
        List<Partnership> findByReceiver(User user);

    // returns the list of id of the users that have rejected partnership with the given user
    @Query("SELECT CASE " +
            "WHEN p.sender.id = :userId THEN p.receiver.id " +
            "WHEN p.receiver.id = :userId THEN p.sender.id END " +
            "FROM Partnership p WHERE " +
            "(p.sender.id = :userId OR p.receiver.id = :userId) " +
            "AND p.status = 'REJECTED'")
    List<Long> findRejectedPartnerships(@Param("userId") Long userId);

    // returns the list of id of the users that have pending partnership with the given user
    @Query("SELECT CASE " +
            "WHEN p.sender.id = :userId THEN p.receiver.id " +
            "WHEN p.receiver.id = :userId THEN p.sender.id END " +
            "FROM Partnership p WHERE " +
            "(p.sender.id = :userId OR p.receiver.id = :userId) " +
            "AND p.status = 'PENDING'")
    List<Long> findPendingPartnerships(@Param("userId") Long userId);

    // returns the accepted partnership with the given user
    @Query( "SELECT p " +
            "FROM Partnership p WHERE " +
            "(p.sender.id = :userId OR p.receiver.id = :userId) " +
            "AND p.status = 'ACCEPTED' ORDER BY p.id DESC LIMIT 1")
    Optional<Partnership> findAcceptedPartnership(@Param("userId") Long userId);
}
