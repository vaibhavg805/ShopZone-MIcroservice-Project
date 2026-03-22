package com.vaibhav.user_service.security.customservice;

import com.vaibhav.user_service.entity.User;
import com.vaibhav.user_service.repository.UserRepository;
import com.vaibhav.user_service.serviceImpl.AuthServiceImplementation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private UserRepository userRepository;
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRoles(username).orElseThrow(() ->
                new UsernameNotFoundException("User Not Found: " + username));
        logger.info("User Loaded From Db...{}",user.getUsername());

        return new CustomUserDetails(user);
    }
}
