package org.manga_backend.authms.domain.service;

import jakarta.transaction.Transactional;
import org.manga_backend.authms.api.JwtResponse;
import org.manga_backend.authms.api.LoginRequest;
import org.manga_backend.authms.api.UserAlreadyExistsException;
import org.manga_backend.authms.api.RegisterRequest;
import org.manga_backend.authms.domain.model.Role;
import org.manga_backend.authms.persistence.entity.RefreshTokenEntity;
import org.manga_backend.authms.persistence.entity.UserEntity;
import org.manga_backend.authms.persistence.repository.UserRepository;
import org.manga_backend.authms.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtils;
    private final AuthenticationManager authManager;
    private final RefreshTokenService refreshTokenService;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtils,
                       AuthenticationManager authManager,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.authManager = authManager;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public JwtResponse createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("User registration failed. Username already exists username={}", request.getUsername());
            throw new UserAlreadyExistsException("Username is already taken");
        }

        Role assignedRole = Role.READER;
        if (request.getRole() != null) {
            assignedRole = Role.valueOf(request.getRole().toUpperCase());
        }

        String encodedPassword = encoder.encode(request.getPassword());

        UserEntity savedUser = userRepository.save(new UserEntity(
                request.getUsername(),
                encodedPassword,
                assignedRole
                )
        );

        log.info("User created successfully with username={} role={}", savedUser.getUsername(), assignedRole);
        String roleString = "ROLE_" + assignedRole.name();
        String jwt = jwtUtils.generateToken(savedUser.getUsername(), roleString);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(savedUser.getUsername());
        return new JwtResponse(jwt, refreshToken.getToken(), savedUser.getUsername(), roleString);
    }

    public JwtResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails details = (UserDetails) authentication.getPrincipal();
        String role = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_READER");

        log.info("Login successful username={} role={}", details.getUsername(), role);

        String jwt = jwtUtils.generateToken(details.getUsername(), role);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(details.getUsername());
        return new JwtResponse(jwt, refreshToken.getToken(), details.getUsername(), role);
    }

    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
