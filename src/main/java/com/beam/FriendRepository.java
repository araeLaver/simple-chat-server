<<<<<<< HEAD:src/main/java/com/beam/FriendRepository.java
package com.beam;
=======
package com.chat;
>>>>>>> 9106cb986b257fdd9ab7197fea5c599fbc536571:src/main/java/com/chat/FriendRepository.java

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

<<<<<<< HEAD:src/main/java/com/beam/FriendRepository.java
    @Query("SELECT f FROM FriendEntity f WHERE " +
           "(f.userId = :userId AND f.friendId = :friendId) OR " +
           "(f.userId = :friendId AND f.friendId = :userId)")
    Optional<FriendEntity> findFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("SELECT f FROM FriendEntity f WHERE " +
           "f.userId = :userId AND f.status = :status")
    List<FriendEntity> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FriendEntity.FriendStatus status);

    @Query("SELECT f FROM FriendEntity f WHERE " +
           "f.friendId = :userId AND f.status = 'PENDING'")
    List<FriendEntity> findPendingRequestsReceived(@Param("userId") Long userId);

    @Query("SELECT f FROM FriendEntity f WHERE " +
           "((f.userId = :userId OR f.friendId = :userId) AND f.status = 'ACCEPTED')")
    List<FriendEntity> findAcceptedFriends(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM FriendEntity f WHERE " +
           "f.friendId = :userId AND f.status = 'PENDING'")
    Integer countPendingRequests(@Param("userId") Long userId);

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
}
=======
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
>>>>>>> 9106cb986b257fdd9ab7197fea5c599fbc536571:src/main/java/com/chat/FriendRepository.java
