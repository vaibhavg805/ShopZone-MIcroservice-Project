package com.vaibhav.user_service.serviceImpl;

import com.vaibhav.user_service.client.ProductClient;
import com.vaibhav.user_service.client.dto.ProductResponseDto;
import com.vaibhav.user_service.dto.ChangePassword;
import com.vaibhav.user_service.entity.PasswordResetToken;
import com.vaibhav.user_service.entity.User;
import com.vaibhav.user_service.mail.EmailService;
import com.vaibhav.user_service.repository.PasswordResetTokenRepository;
import com.vaibhav.user_service.repository.UserRepository;
import com.vaibhav.user_service.service.UserService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * @param changePassword
     */
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ProductClient productClient;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final EmailService emailService;

    @Override
    @Transactional
    public void changePassword(ChangePassword changePassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken)
        {
            throw new AccessDeniedException("User is not authenticated");
        }
            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        // Verify old password
        if (!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword()))
        {
             throw new BadCredentialsException("Old password is incorrect");
        }
            user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            SecurityContextHolder.clearContext();
        // Done -- Now Frontend Need to clear JWT token from storage and redirect to login page
    }

    /**
     * @param email
     */
    @Override
    @Transactional
    public String requestPasswordReset(String email)  {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Email Not Exist in Records..."));

        logger.info("Resetting Token From Repository...");
        passwordResetTokenRepository.deleteByUser(user);
        logger.info("Token deleted From Repository");
        String[] tokenResult = createNewResetToken();
        logger.info("Password reset token generated for user={}", user.getUsername());
        PasswordResetToken setToken = PasswordResetToken.builder()
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .token(tokenResult[1])
                .build();
        //save
        passwordResetTokenRepository.save(setToken);
        logger.info("Raw Token{}",tokenResult[0]);
        logger.info("hashed token{}",tokenResult[1]);
        return emailService.sendEmail(user.getEmail(),tokenResult[0]);
    }

    /**
     * @param rawToken
     * @param newPassword
     */
    @Override
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String hashedToken = hashToken(rawToken);

        PasswordResetToken token = passwordResetTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (token.isUsed())
            throw new RuntimeException("Token already used");
        if (token.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Token expired");

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        token.setUsed(true);
    }

    /**
     * @return
     */
    @Override
    public String callProductService() {
        return productClient.testApi("Hello from user-service");
    }

    // 1. Generate & Hash for DB Storage
    public String[] createNewResetToken()  {
        byte[] rawBytes = new byte[32];
        secureRandom.nextBytes(rawBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(rawBytes);
        // Hash it for the DB
        String hashedToken = null;
        try{
            hashedToken = hashToken(rawToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Return both: rawToken to email the user, hashedToken to save in DB
        return new String[]{rawToken, hashedToken};
    }

    // 2. The Hashing Logic
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    // 3. Secure Verification
    public boolean verifyToken(String providedRawToken, String storedHashedToken){
        String hashedProvided = hashToken(providedRawToken);
        // CRITICAL: Use MessageDigest.isEqual for timing attack protection
        return MessageDigest.isEqual(
                hashedProvided.getBytes(StandardCharsets.UTF_8),
                storedHashedToken.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Inter-Service API Logic
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "productFallback")
    @Retry(name = "productServiceRetry")
    public List<ProductResponseDto> getAllProducts() {
        return productClient.getAllProducts();
    }

    // Fallback method
    // must have same return type as above method
    // must have Throwable as extra parameter
    public List<ProductResponseDto> productFallback(Throwable throwable) {
        log.error("Product service is down. Reason: {}", throwable.getMessage());
        log.error("Fallback triggered!");
        log.error("Exception class: {}", throwable.getClass().getName());
        log.error("Exception message: {}", throwable.getMessage());
        // Return a safe default response
        // Don't return null — return empty list or cached data
        return List.of(); // empty list — user sees "no products" not an error
    }


}
