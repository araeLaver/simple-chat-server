package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 친구 요청 보내기
     */
    public FriendEntity sendFriendRequest(Long userId, Long friendId) {
        // 본인에게 친구 요청할 수 없음
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("본인에게는 친구 요청을 보낼 수 없습니다.");
        }

        // 이미 친구 관계가 있는지 확인
        Optional<FriendEntity> existing = friendRepository.findFriendshipBetween(userId, friendId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 친구 요청이 존재하거나 친구 관계입니다.");
        }

        FriendEntity friendRequest = new FriendEntity(userId, friendId);
        return friendRepository.save(friendRequest);
    }

    /**
     * 친구 요청 수락
     */
    public FriendEntity acceptFriendRequest(Long userId, Long requesterId) {
        // 요청자가 나에게 보낸 친구 요청 찾기
        Optional<FriendEntity> friendRequest = friendRepository.findFriendshipBetween(requesterId, userId);

        if (friendRequest.isEmpty()) {
            throw new IllegalArgumentException("친구 요청을 찾을 수 없습니다.");
        }

        FriendEntity request = friendRequest.get();
        if (!request.getFriendId().equals(userId)) {
            throw new IllegalArgumentException("잘못된 친구 요청입니다.");
        }

        if (request.getStatus() != FriendStatus.PENDING) {
            throw new IllegalArgumentException("대기 중인 요청이 아닙니다.");
        }

        request.accept();
        return friendRepository.save(request);
    }

    /**
     * 친구 요청 거절 또는 친구 삭제
     */
    public void removeFriend(Long userId, Long friendId) {
        Optional<FriendEntity> friendship = friendRepository.findFriendshipBetween(userId, friendId);
        friendship.ifPresent(friendRepository::delete);
    }

    /**
     * 친구 차단
     */
    public FriendEntity blockFriend(Long userId, Long friendId) {
        Optional<FriendEntity> friendship = friendRepository.findFriendshipBetween(userId, friendId);

        if (friendship.isPresent()) {
            FriendEntity friend = friendship.get();
            friend.block();
            return friendRepository.save(friend);
        } else {
            // 친구 관계가 없으면 새로 차단 관계 생성
            FriendEntity blocked = new FriendEntity(userId, friendId);
            blocked.block();
            return friendRepository.save(blocked);
        }
    }

    /**
     * 내 친구 목록 조회 (수락된 친구만)
     */
    public List<UserEntity> getFriendList(Long userId) {
        List<FriendEntity> friendships = friendRepository.findAllFriendsByUserId(userId, FriendStatus.ACCEPTED);
        List<UserEntity> friends = new ArrayList<>();

        for (FriendEntity friendship : friendships) {
            Long friendId = friendship.getUserId().equals(userId) ? friendship.getFriendId() : friendship.getUserId();
            userRepository.findById(friendId).ifPresent(friends::add);
        }

        return friends;
    }

    /**
     * 받은 친구 요청 목록 조회
     */
    public List<UserEntity> getPendingFriendRequests(Long userId) {
        List<FriendEntity> requests = friendRepository.findByFriendIdAndStatus(userId, FriendStatus.PENDING);
        List<UserEntity> requesters = new ArrayList<>();

        for (FriendEntity request : requests) {
            userRepository.findById(request.getUserId()).ifPresent(requesters::add);
        }

        return requesters;
    }

    /**
     * 보낸 친구 요청 목록 조회
     */
    public List<UserEntity> getSentFriendRequests(Long userId) {
        List<FriendEntity> requests = friendRepository.findByUserIdAndStatus(userId, FriendStatus.PENDING);
        List<UserEntity> receivers = new ArrayList<>();

        for (FriendEntity request : requests) {
            userRepository.findById(request.getFriendId()).ifPresent(receivers::add);
        }

        return receivers;
    }

    /**
     * 친구 여부 확인
     */
    public boolean areFriends(Long userId, Long friendId) {
        return friendRepository.existsFriendship(userId, friendId, FriendStatus.ACCEPTED);
    }
}
