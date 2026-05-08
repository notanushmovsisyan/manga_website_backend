package org.manga_backend.authms.domain.service;

import jakarta.transaction.Transactional;
import org.manga_backend.authms.api.JwtResponse;
import org.manga_backend.authms.api.TokenRefreshException;
import org.manga_backend.authms.api.UserNotFoundException;
import org.manga_backend.authms.domain.model.RefreshToken;
import org.manga_backend.authms.persistence.RefreshTokenMapper;
import org.manga_backend.authms.persistence.entity.RefreshTokenEntity;
import org.manga_backend.authms.persistence.entity.UserEntity;
import org.manga_backend.authms.persistence.repository.RefreshTokenRepository;
import org.manga_backend.authms.persistence.repository.UserRepository;
import org.manga_backend.authms.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public RefreshTokenEntity createRefreshToken(String username) {
        UserEntity existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Refresh token creation failed. User not found with username={}", username);
                    return new UserNotFoundException("User doesn't exist");
                });

        RefreshTokenEntity refreshToken = new RefreshTokenEntity(
                existingUser,
                UUID.randomUUID().toString(),
                LocalDateTime.now().plus(Duration.ofMillis(refreshTokenDurationMs))
        );

        log.info("Refresh token created successfully username={}", username);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired refresh token for username={}", token.getUser().getUsername());
            refreshTokenRepository.delete(RefreshTokenMapper.refreshTokenToRefreshTokenEntity(token));
            throw new TokenRefreshException("Refresh token was expired. Please make a new sign-in request");
        }
        return token;
    }

    @Transactional
    public JwtResponse refreshAccessToken(String requestToken) {

        return refreshTokenRepository.findByToken(requestToken)
                .map(RefreshTokenMapper::refreshTokenEntityToRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String role = "ROLE_" + user.getRole().name();
                    String accessToken = jwtUtil.generateToken(user.getUsername(), role);
                    log.info("Access token refreshed successfully username={}", user.getUsername());
                    return new JwtResponse(accessToken, requestToken, user.getUsername(), role);
                })
                .orElseThrow(() -> {
                    log.warn("Refresh token not found in database");
                    return new TokenRefreshException("Refresh token is not in database!");
                });
    }

    public RefreshToken findByToken(String token) {
        return RefreshTokenMapper.refreshTokenEntityToRefreshToken(refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Token doesn't exist")));
    }
}
