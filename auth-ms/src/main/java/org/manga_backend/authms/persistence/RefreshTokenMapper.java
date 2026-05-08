package org.manga_backend.authms.persistence;

import org.manga_backend.authms.domain.model.RefreshToken;
import org.manga_backend.authms.persistence.entity.RefreshTokenEntity;

public class RefreshTokenMapper {

    public static RefreshToken refreshTokenEntityToRefreshToken(RefreshTokenEntity entity) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(entity.getToken());
        refreshToken.setId(entity.getId());
        refreshToken.setRevoked(entity.getRevoked());
        refreshToken.setCreatedAt(entity.getCreatedAt());
        refreshToken.setExpiresAt(entity.getExpiresAt());
        refreshToken.setUser(UserMapper.userEntityToUser(entity.getUser()));

        return refreshToken;
    }

    public static RefreshTokenEntity refreshTokenToRefreshTokenEntity(RefreshToken token) {
        return new RefreshTokenEntity(
                UserMapper.userToUserEntity(token.getUser()),
                token.getToken(),
                token.getExpiresAt()
        );
    }
}
