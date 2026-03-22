package com.vaibhav.user_service.repository;

import com.vaibhav.user_service.entity.Address;
import com.vaibhav.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {

    long countByUser(User user);
    List<Address> findByUser(User user);
    @Modifying(clearAutomatically = true)
    @Query(""" 
        update Address a
        set a.isDefault = false
       where a.user.id = :userId
       """)
    void clearDefaultForUser(Long userId);

    Optional<Address> findByIdAndIsDeletedFalse(Long id);

    // Find all active addresses for a user
    List<Address> findByUserIdAndIsDeletedFalse(Long userId);

    List<Address> findByUserAndIsDeletedFalse(User user);

    // Find active address belonging to a specific user (for authorization check)
    Optional<Address> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);

    // Hard delete — use only if needed
    @Query("DELETE FROM Address a WHERE a.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
