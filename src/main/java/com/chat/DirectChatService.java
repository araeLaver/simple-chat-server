package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 1:1 채팅 서비스
 * 카카오톡 수준의 개인 메시지 기능
 */
@Service
@Transactional
public class DirectChatService {

    @Autowired
    private FriendService friendService;

    /**
     * 1:1 채팅방 ID 생성 (또는 기존 방 조회)
     *
     * 규칙: direct_{작은ID}_{큰ID}
     * 예: direct_123_456
     *
     * 이렇게 하면 항상 같은 방 ID가 생성됨
     */
    public String getOrCreateDirectRoomId(Long user1Id, Long user2Id) {
        // 1. 친구 관계 확인 (선택 사항 - 친구가 아니어도 메시지 가능하게 하려면 주석 처리)
        // if (!friendService.isFriend(user1Id, user2Id)) {
        //     throw new IllegalArgumentException("친구만 1:1 채팅을 할 수 있습니다.");
        // }

        // 2. 차단 확인
        if (friendService.isBlocked(user1Id, user2Id) || friendService.isBlocked(user2Id, user1Id)) {
            throw new IllegalArgumentException("차단된 사용자와는 채팅할 수 없습니다.");
        }

        // 3. 작은 ID를 앞에, 큰 ID를 뒤에 배치
        Long smallerId = Math.min(user1Id, user2Id);
        Long largerId = Math.max(user1Id, user2Id);

        // 4. 1:1 채팅방 ID 반환
        return String.format("direct_%d_%d", smallerId, largerId);
    }

    /**
     * 1:1 채팅방 ID인지 확인
     */
    public boolean isDirectChat(String roomId) {
        return roomId != null && roomId.startsWith("direct_");
    }

    /**
     * 1:1 채팅방 ID에서 상대방 ID 추출
     *
     * @param roomId 채팅방 ID (예: direct_123_456)
     * @param myUserId 내 사용자 ID
     * @return 상대방 사용자 ID
     */
    public Long getOtherUserId(String roomId, Long myUserId) {
        if (!isDirectChat(roomId)) {
            throw new IllegalArgumentException("1:1 채팅방이 아닙니다.");
        }

        // direct_123_456 -> [direct, 123, 456]
        String[] parts = roomId.split("_");
        if (parts.length != 3) {
            throw new IllegalArgumentException("잘못된 채팅방 ID입니다.");
        }

        Long user1Id = Long.parseLong(parts[1]);
        Long user2Id = Long.parseLong(parts[2]);

        if (user1Id.equals(myUserId)) {
            return user2Id;
        } else if (user2Id.equals(myUserId)) {
            return user1Id;
        } else {
            throw new IllegalArgumentException("이 채팅방의 참가자가 아닙니다.");
        }
    }

    /**
     * 1:1 채팅방 참가자 확인
     */
    public boolean isParticipant(String roomId, Long userId) {
        if (!isDirectChat(roomId)) {
            return false;
        }

        String[] parts = roomId.split("_");
        if (parts.length != 3) {
            return false;
        }

        Long user1Id = Long.parseLong(parts[1]);
        Long user2Id = Long.parseLong(parts[2]);

        return user1Id.equals(userId) || user2Id.equals(userId);
    }

    /**
     * 1:1 채팅방 이름 생성
     * (실제로는 상대방 이름을 표시)
     */
    public String getDirectChatRoomName(Long myUserId, Long otherUserId, String otherUsername) {
        // 카카오톡처럼 상대방 이름만 표시
        return otherUsername;
    }
}
