package com.vaibhav.user_service.repository;

import com.vaibhav.user_service.entity.PasswordResetToken;
import com.vaibhav.user_service.entity.RefreshToken;
import com.vaibhav.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
