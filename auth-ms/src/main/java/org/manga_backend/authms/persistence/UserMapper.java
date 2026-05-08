package org.manga_backend.authms.persistence;

import org.manga_backend.authms.domain.model.User;
import org.manga_backend.authms.persistence.entity.UserEntity;

public class UserMapper {
    public static User userEntityToUser(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setPasswordHash(entity.getPasswordHash());
        user.setRole(entity.getRole());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        return user;
    }

    public static UserEntity userToUserEntity(User user) {
        UserEntity entity = new UserEntity(
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole()
        );

        if (user.getId() != null) {
            entity.setId(user.getId());
        }

        if (user.getCreatedAt() != null) {
            entity.setCreatedAt(user.getCreatedAt());
        }

        if (user.getUpdatedAt() != null) {
            entity.setUpdatedAt(user.getUpdatedAt());
        }

        return entity;
    }
}
