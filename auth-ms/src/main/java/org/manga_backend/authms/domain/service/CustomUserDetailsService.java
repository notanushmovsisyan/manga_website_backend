package org.manga_backend.authms.domain.service;

import org.manga_backend.authms.domain.model.User;
import org.manga_backend.authms.persistence.UserMapper;
import org.manga_backend.authms.persistence.entity.UserEntity;
import org.manga_backend.authms.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: user not found with username={}", username);
                    return new UsernameNotFoundException("User not found.");
                });

        log.debug("User found for authentication username={}", username);
        User user = UserMapper.userEntityToUser(userEntity);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(authority)
        );
    }
}
