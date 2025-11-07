package com.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

    // 내가 보낸 친구 요청 또는 친구 관계 조회
    List<FriendEntity> findByUserIdAndStatus(Long userId, FriendStatus status);

    // 나에게 온 친구 요청 조회
    List<FriendEntity> findByFriendIdAndStatus(Long friendId, FriendStatus status);

    // 양방향 친구 관계 조회
    @Query("SELECT f FROM FriendEntity f WHERE " +
           "(f.userId = :userId OR f.friendId = :userId) AND f.status = :status")
    List<FriendEntity> findAllFriendsByUserId(@Param("userId") Long userId, @Param("status") FriendStatus status);

    // 특정 사용자 간의 친구 관계 조회
    @Query("SELECT f FROM FriendEntity f WHERE " +
           "((f.userId = :userId AND f.friendId = :friendId) OR " +
           "(f.userId = :friendId AND f.friendId = :userId))")
    Optional<FriendEntity> findFriendshipBetween(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // 친구 여부 확인
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FriendEntity f WHERE " +
           "((f.userId = :userId AND f.friendId = :friendId) OR " +
           "(f.userId = :friendId AND f.friendId = :userId)) AND f.status = :status")
    boolean existsFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") FriendStatus status);
}
