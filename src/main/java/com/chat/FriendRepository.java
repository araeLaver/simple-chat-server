package com.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 친구 관계 Repository
 */
@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

    /**
     * 특정 사용자의 모든 친구 조회
     */
    List<FriendEntity> findByUserIdAndStatus(Long userId, FriendStatus status);

    /**
     * 특정 사용자의 친구 관계 조회
     */
    Optional<FriendEntity> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

    /**
     * 친구 관계 존재 여부 확인
     */
    boolean existsByUserIdAndFriendUserIdAndStatus(Long userId, Long friendUserId, FriendStatus status);

    /**
     * 즐겨찾기 친구 목록 조회
     */
    List<FriendEntity> findByUserIdAndIsFavoriteTrueAndStatus(Long userId, FriendStatus status);

    /**
     * 양방향 친구 관계 조회 (서로 친구인지 확인)
     */
    @Query("SELECT f1 FROM FriendEntity f1 WHERE " +
           "f1.userId = :userId AND f1.friendUserId = :friendId AND f1.status = 'ACCEPTED' AND " +
           "EXISTS (SELECT f2 FROM FriendEntity f2 WHERE " +
           "f2.userId = :friendId AND f2.friendUserId = :userId AND f2.status = 'ACCEPTED')")
    Optional<FriendEntity> findMutualFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 차단된 사용자 목록
     */
    List<FriendEntity> findByUserIdAndStatus(Long userId, FriendStatus status, org.springframework.data.domain.Sort sort);

    /**
     * 친구 목록 조회 with 페이징
     */
    org.springframework.data.domain.Page<FriendEntity> findByUserIdAndStatus(
        Long userId,
        FriendStatus status,
        org.springframework.data.domain.Pageable pageable
    );

    /**
     * 친구 요청 가능 여부 확인 (양방향 체크)
     */
    @Query("SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END FROM FriendRequestEntity fr " +
           "WHERE ((fr.fromUserId = :userId1 AND fr.toUserId = :userId2) " +
           "OR (fr.fromUserId = :userId2 AND fr.toUserId = :userId1)) " +
           "AND fr.status = 'PENDING'")
    boolean hasAnyPendingRequest(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
