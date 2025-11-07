package com.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    // 사용자 이름으로 검색 (친구 추가용)
    java.util.List<UserEntity> findByUsernameContaining(String keyword);
}