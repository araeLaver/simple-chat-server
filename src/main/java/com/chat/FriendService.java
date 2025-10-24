package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 친구 관리 서비스
 * 카카오톡 수준의 친구 시스템
 */
@Service
@Transactional
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 친구 요청 보내기
     */
    public FriendRequestEntity sendFriendRequest(Long fromUserId, Long toUserId, String message) {
        // 1. 자기 자신에게 요청 불가
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        // 2. 이미 친구인지 확인
        if (friendRepository.existsByUserIdAndFriendUserIdAndStatus(fromUserId, toUserId, FriendStatus.ACCEPTED)) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }

        // 3. 이미 요청이 있는지 확인
        if (friendRequestRepository.existsByFromUserIdAndToUserIdAndStatus(fromUserId, toUserId, FriendStatus.PENDING)) {
            throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
        }

        // 4. 차단 여부 확인
        if (friendRepository.existsByUserIdAndFriendUserIdAndStatus(toUserId, fromUserId, FriendStatus.BLOCKED)) {
            throw new IllegalArgumentException("상대방이 회원님을 차단했습니다.");
        }

        // 5. 친구 요청 생성
        FriendRequestEntity request = new FriendRequestEntity(fromUserId, toUserId, message);
        return friendRequestRepository.save(request);
    }

    /**
     * 친구 요청 수락
     */
    public void acceptFriendRequest(Long requestId, Long userId) {
        // 1. 친구 요청 조회
        FriendRequestEntity request = friendRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 2. 권한 확인 (요청 받은 사람만 수락 가능)
        if (!request.getToUserId().equals(userId)) {
            throw new IllegalArgumentException("친구 요청을 수락할 권한이 없습니다.");
        }

        // 3. 이미 처리된 요청인지 확인
        if (request.getStatus() != FriendStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        // 4. 양방향 친구 관계 생성
        FriendEntity friend1 = new FriendEntity(userId, request.getFromUserId(), FriendStatus.ACCEPTED);
        FriendEntity friend2 = new FriendEntity(request.getFromUserId(), userId, FriendStatus.ACCEPTED);

        friendRepository.save(friend1);
        friendRepository.save(friend2);

        // 5. 요청 상태 업데이트
        request.accept();
        friendRequestRepository.save(request);
    }

    /**
     * 친구 요청 거절
     */
    public void rejectFriendRequest(Long requestId, Long userId) {
        FriendRequestEntity request = friendRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        if (!request.getToUserId().equals(userId)) {
            throw new IllegalArgumentException("친구 요청을 거절할 권한이 없습니다.");
        }

        if (request.getStatus() != FriendStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        request.reject();
        friendRequestRepository.save(request);
    }

    /**
     * 친구 삭제
     */
    public void removeFriend(Long userId, Long friendUserId) {
        // 양방향 친구 관계 삭제
        Optional<FriendEntity> friend1 = friendRepository.findByUserIdAndFriendUserId(userId, friendUserId);
        Optional<FriendEntity> friend2 = friendRepository.findByUserIdAndFriendUserId(friendUserId, userId);

        friend1.ifPresent(f -> {
            f.setStatus(FriendStatus.DELETED);
            friendRepository.save(f);
        });

        friend2.ifPresent(f -> {
            f.setStatus(FriendStatus.DELETED);
            friendRepository.save(f);
        });
    }

    /**
     * 친구 차단
     */
    public void blockFriend(Long userId, Long friendUserId) {
        Optional<FriendEntity> friendOptional = friendRepository.findByUserIdAndFriendUserId(userId, friendUserId);

        if (friendOptional.isPresent()) {
            FriendEntity friend = friendOptional.get();
            friend.setStatus(FriendStatus.BLOCKED);
            friendRepository.save(friend);
        } else {
            // 친구가 아니어도 차단 가능
            FriendEntity blockedFriend = new FriendEntity(userId, friendUserId, FriendStatus.BLOCKED);
            friendRepository.save(blockedFriend);
        }
    }

    /**
     * 친구 차단 해제
     */
    public void unblockFriend(Long userId, Long friendUserId) {
        Optional<FriendEntity> friendOptional = friendRepository.findByUserIdAndFriendUserId(userId, friendUserId);

        friendOptional.ifPresent(friend -> {
            if (friend.getStatus() == FriendStatus.BLOCKED) {
                friend.setStatus(FriendStatus.DELETED);
                friendRepository.save(friend);
            }
        });
    }

    /**
     * 친구 목록 조회
     */
    public List<FriendEntity> getFriendList(Long userId) {
        return friendRepository.findByUserIdAndStatus(userId, FriendStatus.ACCEPTED);
    }

    /**
     * 받은 친구 요청 목록
     */
    public List<FriendRequestEntity> getReceivedFriendRequests(Long userId) {
        return friendRequestRepository.findByToUserIdAndStatusOrderByCreatedAtDesc(userId, FriendStatus.PENDING);
    }

    /**
     * 보낸 친구 요청 목록
     */
    public List<FriendRequestEntity> getSentFriendRequests(Long userId) {
        return friendRequestRepository.findByFromUserIdAndStatus(userId, FriendStatus.PENDING);
    }

    /**
     * 즐겨찾기 설정/해제
     */
    public void toggleFavorite(Long userId, Long friendUserId) {
        Optional<FriendEntity> friendOptional = friendRepository.findByUserIdAndFriendUserId(userId, friendUserId);

        friendOptional.ifPresent(friend -> {
            friend.setIsFavorite(!friend.getIsFavorite());
            friendRepository.save(friend);
        });
    }

    /**
     * 친구 별명 설정
     */
    public void setNickname(Long userId, Long friendUserId, String nickname) {
        Optional<FriendEntity> friendOptional = friendRepository.findByUserIdAndFriendUserId(userId, friendUserId);

        friendOptional.ifPresent(friend -> {
            friend.setNickname(nickname);
            friendRepository.save(friend);
        });
    }

    /**
     * 친구 여부 확인
     */
    public boolean isFriend(Long userId, Long friendUserId) {
        return friendRepository.existsByUserIdAndFriendUserIdAndStatus(userId, friendUserId, FriendStatus.ACCEPTED);
    }

    /**
     * 차단 여부 확인
     */
    public boolean isBlocked(Long userId, Long friendUserId) {
        return friendRepository.existsByUserIdAndFriendUserIdAndStatus(userId, friendUserId, FriendStatus.BLOCKED);
    }
}
