package com.beam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

<<<<<<< HEAD:src/main/java/com/beam/UserRepository.java
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);
=======
    boolean existsByUsername(String username);

    // 사용자 이름으로 검색 (친구 추가용)
    java.util.List<UserEntity> findByUsernameContaining(String keyword);
>>>>>>> 9106cb986b257fdd9ab7197fea5c599fbc536571:src/main/java/com/chat/UserRepository.java
}