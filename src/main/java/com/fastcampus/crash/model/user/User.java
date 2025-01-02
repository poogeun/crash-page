package com.fastcampus.crash.model.user;

import com.fastcampus.crash.model.entity.UserEntity;

public record User(Long userId, String username, String name, String email) {

    // UserEntity -> User 변환
    public static User from(UserEntity userEntity) {
        return new User(
                userEntity.getUserId(),
                userEntity.getUsername(),
                userEntity.getName(),
                userEntity.getEmail());
    }
}
