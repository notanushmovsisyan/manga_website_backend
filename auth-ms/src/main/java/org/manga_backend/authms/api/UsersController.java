package org.manga_backend.authms.api;

import jakarta.validation.Valid;
import org.manga_backend.authms.domain.service.RefreshTokenService;
import org.manga_backend.authms.domain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@Validated
@Tag(name = "Users", description = "Endpoints for managing users")
public class UsersController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    public UsersController(UserService userService,
                           RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest request) {
        JwtResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = userService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Token validation failed: unauthenticated request");
            throw new BadCredentialsException("Invalid or missing token");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_READER");

        log.info("Token validated successfully username={} role={}", userDetails.getUsername(), role);

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "role", role,
                "status", "Valid"
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            log.warn("Refresh token request failed: token missing");
            throw new IllegalArgumentException("Missing refresh token");
        }

        JwtResponse response = refreshTokenService.refreshAccessToken(requestRefreshToken);
        return ResponseEntity.ok(response);
    }
}
