package com.dreamgames.backendengineeringcasestudy.repository;

import com.dreamgames.backendengineeringcasestudy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // returns a list of users that do not have a partner and meting the requirements to participate a live event
    @Query("SELECT u FROM User u " +
            "WHERE u.abTestGroup = :abTestGroup " +
            "AND u.level >= 50 AND u.coins >= 2500 " +
            "AND u.partnerID IS NULL AND u.id NOT IN :rejectedUserIds " +
            "AND u.id NOT IN :pendingUserIds AND u.id != :userId " +
            "ORDER BY FUNCTION('RAND') " +
            "LIMIT 10")
    List<User> findSuggestions(@Param("userId") Long userId,
                               @Param("abTestGroup") User.ABTestGroup abTestGroup,
                               @Param("rejectedUserIds") List<Long> rejectedUserIds,
                               @Param("pendingUserIds") List<Long> pendingUserIds);

    // return the top 100 users based on their level
    @Query("SELECT u.id AS id, u.level AS level FROM User u " +
            "ORDER BY u.level DESC " +
            "LIMIT 100")
    List<Map<String, Object>> findTop100Users();
}
