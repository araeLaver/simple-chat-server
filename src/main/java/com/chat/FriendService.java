package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 친구 관리 서비스 (개선됨)
 * 카카오톡 수준의 친구 시스템
 *
 * 개선 사항:
 * - 트랜잭션 격리 레벨 추가
 * - 양방향 친구 요청 체크
 * - 사용자 존재 확인
 * - 동시성 문제 해결
 * - 페이징 지원
 */
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 친구 요청 보내기 (개선됨)
     */
    public FriendRequestEntity sendFriendRequest(Long fromUserId, Long toUserId, String message) {
        // 1. 자기 자신에게 요청 불가
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        // 2. 사용자 존재 여부 확인 (중요!)
        if (!userRepository.existsById(fromUserId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (!userRepository.existsById(toUserId)) {
            throw new IllegalArgumentException("친구 추가할 사용자를 찾을 수 없습니다.");
        }

        // 3. 이미 친구인지 확인
        if (friendRepository.existsByUserIdAndFriendUserIdAndStatus(fromUserId, toUserId, FriendStatus.ACCEPTED)) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }

        // 4. 양방향 친구 요청 체크 (개선됨!)
        // A→B 또는 B→A 중 하나라도 PENDING이면 중복
        if (friendRequestRepository.existsByFromUserIdAndToUserIdAndStatus(fromUserId, toUserId, FriendStatus.PENDING)) {
            throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
        }
        if (friendRequestRepository.existsByFromUserIdAndToUserIdAndStatus(toUserId, fromUserId, FriendStatus.PENDING)) {
            throw new IllegalArgumentException("상대방이 이미 친구 요청을 보냈습니다. 받은 요청을 확인하세요.");
        }

        // 5. 차단 여부 확인
        if (friendRepository.existsByUserIdAndFriendUserIdAndStatus(toUserId, fromUserId, FriendStatus.BLOCKED)) {
            throw new IllegalArgumentException("상대방이 회원님을 차단했습니다.");
        }

        // 6. 친구 요청 생성
        FriendRequestEntity request = new FriendRequestEntity(fromUserId, toUserId, message);
        return friendRequestRepository.save(request);
    }

    /**
     * 친구 요청 수락 (개선됨)
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

        // 4. 이미 친구인지 재확인 (동시성 문제 방지)
        if (friendRepository.existsByUserIdAndFriendUserIdAndStatus(userId, request.getFromUserId(), FriendStatus.ACCEPTED)) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }

        // 5. 양방향 친구 관계 생성
        FriendEntity friend1 = new FriendEntity(userId, request.getFromUserId(), FriendStatus.ACCEPTED);
        FriendEntity friend2 = new FriendEntity(request.getFromUserId(), userId, FriendStatus.ACCEPTED);

        try {
            friendRepository.save(friend1);
            friendRepository.save(friend2);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 유니크 제약 조건 위반 시 (중복 친구)
            throw new IllegalArgumentException("친구 관계가 이미 존재합니다.");
        }

        // 6. 요청 상태 업데이트
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
    @Transactional(readOnly = true)
    public List<FriendEntity> getFriendList(Long userId) {
        return friendRepository.findByUserIdAndStatus(userId, FriendStatus.ACCEPTED);
    }

    /**
     * 친구 목록 조회 with 페이징 (개선됨)
     */
    @Transactional(readOnly = true)
    public Page<FriendEntity> getFriendListPaged(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
            Sort.by("isFavorite").descending()
                .and(Sort.by("createdAt").descending()));

        return friendRepository.findByUserIdAndStatus(userId, FriendStatus.ACCEPTED, pageable);
    }

    /**
     * 받은 친구 요청 목록
     */
    @Transactional(readOnly = true)
    public List<FriendRequestEntity> getReceivedFriendRequests(Long userId) {
        return friendRequestRepository.findByToUserIdAndStatusOrderByCreatedAtDesc(userId, FriendStatus.PENDING);
    }

    /**
     * 보낸 친구 요청 목록
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public boolean isFriend(Long userId, Long friendUserId) {
        return friendRepository.existsByUserIdAndFriendUserIdAndStatus(userId, friendUserId, FriendStatus.ACCEPTED);
    }

    /**
     * 차단 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isBlocked(Long userId, Long friendUserId) {
        return friendRepository.existsByUserIdAndFriendUserIdAndStatus(userId, friendUserId, FriendStatus.BLOCKED);
    }
}
