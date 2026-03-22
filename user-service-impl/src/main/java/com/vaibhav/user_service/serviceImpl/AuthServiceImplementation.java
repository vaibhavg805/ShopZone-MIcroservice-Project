package com.vaibhav.user_service.serviceImpl;

import com.vaibhav.user_service.dto.*;
import com.vaibhav.user_service.entity.RefreshToken;
import com.vaibhav.user_service.entity.Role;
import com.vaibhav.user_service.entity.User;
import com.vaibhav.user_service.exception.RefreshTokenExpiredException;
import com.vaibhav.user_service.exception.RefreshTokenNotFoundException;
import com.vaibhav.user_service.exception.RoleNotFoundException;
import com.vaibhav.user_service.exception.UserAlreadyExistException;
import com.vaibhav.user_service.mapper.UserMapper;
import com.vaibhav.user_service.repository.RefreshTokenRepository;
import com.vaibhav.user_service.repository.RoleRepository;
import com.vaibhav.user_service.repository.UserRepository;
import com.vaibhav.user_service.security.JwtFilter;
import com.vaibhav.user_service.security.JwtUtil;
import com.vaibhav.user_service.security.customservice.CustomUserDetailService;
import com.vaibhav.user_service.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImplementation implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;
    private CustomUserDetailService customUserDetailService;

    public AuthServiceImplementation(UserRepository userRepository, RoleRepository roleRepository,
                                     PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager
                                     ,JwtUtil jwtUtil, UserMapper userMapper,
                                     RefreshTokenRepository refreshTokenRepository,
                                     Clock clock, CustomUserDetailService customUserDetailService) {
            this.userRepository=userRepository;
            this.roleRepository=roleRepository;
            this.passwordEncoder=passwordEncoder;
            this.authenticationManager=authenticationManager;
            this.jwtUtil=jwtUtil;
            this.userMapper=userMapper;
            this.refreshTokenRepository=refreshTokenRepository;
            this.clock=clock;
            this.customUserDetailService=customUserDetailService;
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImplementation.class);

    @Override
    public UserResponseDto saveDataForUser(UserRequestDto userRequestDto) {
        User user = userMapper.convertRequestDtoToEntity(userRequestDto);
        logger.info("User Is converted to Entity{}", user);
        logger.info("Dto Username IS{}", userRequestDto.getUsername());
        logger.info("Entity User Name IS{}", user.getUsername());
        boolean alreadyExist = userRepository.findByUsername(user.getUsername()).isPresent();
        if(alreadyExist) {
            throw new UserAlreadyExistException("Username Already Exist");
        }
        if(user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password Is Empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Password Set");
        if(user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() ->
                                        new RoleNotFoundException("Role Not Found"));

         user.getRoles().add(role);
        logger.info("Roles Added...");
        try{
            User savedUser = userRepository.save(user);
            logger.info("User Saved in Db...");
            return userMapper.convertEntityToResponseDto(savedUser);
        } catch (Exception e) {
            logger.error("SAVE FAILED", e);
            throw e;
        }

    }

    @Override
    public UserResponseDto saveDataForAdmin(UserRequestDto adminRequestDto) {

        User adminUser =
                userMapper.convertRequestDtoToEntity(adminRequestDto);
        boolean alreadyExist = userRepository.findByUsername(adminUser.getUsername()).isPresent();
        if(alreadyExist) {
            throw new UserAlreadyExistException("Username Already Exist");
        }
        if(adminUser.getPassword() == null || adminUser.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password Is Empty");
        }
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        if(adminUser.getRoles() == null) {
            adminUser.setRoles(new HashSet<>());
        }
        Role role = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() ->
                new RoleNotFoundException("Role Not Found"));

        adminUser.getRoles().add(role);
        User savedUser = userRepository.save(adminUser);

        return userMapper.convertEntityToResponseDto(savedUser);
    }

    @Override
    public UserResponseDto saveDataForSeller(UserRequestDto userRequestDto) {
        User user = userMapper.convertRequestDtoToEntity(userRequestDto);
        boolean alreadyExist = userRepository.findByUsername(user.getUsername()).isPresent();
        if(alreadyExist) {
            throw new UserAlreadyExistException("Username Already Exist");
        }
        if(user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password Is Empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        Role role = roleRepository.findByName("ROLE_SELLER").orElseThrow(() ->
                new RoleNotFoundException("Role Not Found"));

        user.getRoles().add(role);
        User savedUser = userRepository.save(user);

        return userMapper.convertEntityToResponseDto(savedUser);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsernameWithRoles(loginRequest.getLogin()).orElseThrow(() ->
                new UsernameNotFoundException("User Not Exist in Records..."));
        logger.info("Data Received Is{}",loginRequest.getLogin());
        logger.info("User Exist in records{}",user.getUsername());

        try{
            logger.info("Calling Authentication Manager...");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLogin(),loginRequest.getPassword())
            );
        } catch (Exception e) {
            logger.error("AUTH FAILED", e);
            throw e;
        }
        logger.info("Authentication Completed...");
        UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getUsername());
        String loginJwtToken = jwtUtil.generateToken(userDetails);
        logger.info("Jwt Token Created");
        String loginRefreshToken = generateRefreshToken(user.getUsername());
        logger.info("Refresh Token Created");
        return LoginResponse.builder()
                .refreshToken(loginRefreshToken)
                .accessToken(loginJwtToken)
                .build();
    }

    @Override
    public String generateRefreshToken(String username) {
        User user = userRepository.findByUsernameWithRoles(username).orElseThrow(() ->
                new UsernameNotFoundException("Given Username Not Exist..."));
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now(clock).plusDays(2));
        refreshToken.setUser(user);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Override
    @Transactional
    public LoginResponse validateAndGenerateAccessToken(AccessToken accessToken) {
        String refreshToken = accessToken.getRefreshToken();
       RefreshToken validateRefreshToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() ->
                new RefreshTokenNotFoundException("Token Not Found..."));

       logger.info("Token Found");

        if(validateRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(validateRefreshToken);
            throw  new RefreshTokenExpiredException("Token Expired...");
        }

        logger.info("Token IS Valid");

        String newRefreshToken = generateRefreshToken(validateRefreshToken.getUser().getUsername());
        logger.info("Refresh Token{}",newRefreshToken);
        User user = validateRefreshToken.getUser();
        logger.info("Username For Refresh Token{}",user.getUsername());
        UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getUsername());
        String newJwtToken = jwtUtil.generateToken(userDetails);
        logger.info("Jwt Generated{}",newJwtToken);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setRefreshToken(newRefreshToken);
        loginResponse.setAccessToken(newJwtToken);
        return  loginResponse;
    }

    @Override
    @Transactional
    public String logOut() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            String username = auth.getName();
            logger.info("Name we get from Authentication{}",username);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            refreshTokenRepository.deleteByUser(user);
        }
        return "Logged Out...";
    }

}
