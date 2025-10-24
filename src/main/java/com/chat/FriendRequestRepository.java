package com.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 친구 요청 Repository
 */
@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {

    /**
     * 받은 친구 요청 조회
     */
    List<FriendRequestEntity> findByToUserIdAndStatus(Long toUserId, FriendStatus status);

    /**
     * 보낸 친구 요청 조회
     */
    List<FriendRequestEntity> findByFromUserIdAndStatus(Long fromUserId, FriendStatus status);

    /**
     * 특정 친구 요청 조회
     */
    Optional<FriendRequestEntity> findByFromUserIdAndToUserIdAndStatus(
        Long fromUserId, Long toUserId, FriendStatus status);

    /**
     * 친구 요청 존재 여부 확인
     */
    boolean existsByFromUserIdAndToUserIdAndStatus(
        Long fromUserId, Long toUserId, FriendStatus status);

    /**
     * 대기 중인 모든 친구 요청 조회
     */
    List<FriendRequestEntity> findByToUserIdAndStatusOrderByCreatedAtDesc(
        Long toUserId, FriendStatus status);
}
