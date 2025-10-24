package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 친구 관리 REST API
 * 카카오톡 수준의 친구 시스템 API
 */
@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private DirectChatService directChatService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 친구 요청 보내기
     * POST /api/friends/requests
     */
    @PostMapping("/requests")
    public ResponseEntity<?> sendFriendRequest(@RequestBody Map<String, Object> request) {
        try {
            Long fromUserId = Long.parseLong(request.get("fromUserId").toString());
            Long toUserId = Long.parseLong(request.get("toUserId").toString());
            String message = request.getOrDefault("message", "친구가 되어주세요!").toString();

            FriendRequestEntity friendRequest = friendService.sendFriendRequest(fromUserId, toUserId, message);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구 요청을 보냈습니다.");
            response.put("requestId", friendRequest.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 친구 요청 수락
     * POST /api/friends/requests/{requestId}/accept
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<?> acceptFriendRequest(
        @PathVariable Long requestId,
        @RequestBody Map<String, Object> request
    ) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            friendService.acceptFriendRequest(requestId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구 요청을 수락했습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 친구 요청 거절
     * POST /api/friends/requests/{requestId}/reject
     */
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<?> rejectFriendRequest(
        @PathVariable Long requestId,
        @RequestBody Map<String, Object> request
    ) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            friendService.rejectFriendRequest(requestId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구 요청을 거절했습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 친구 목록 조회
     * GET /api/friends?userId={userId}
     */
    @GetMapping
    public ResponseEntity<?> getFriendList(@RequestParam Long userId) {
        try {
            List<FriendEntity> friends = friendService.getFriendList(userId);

            // 친구 정보 매핑
            List<Map<String, Object>> friendList = friends.stream().map(friend -> {
                Map<String, Object> friendInfo = new HashMap<>();
                friendInfo.put("id", friend.getId());
                friendInfo.put("friendUserId", friend.getFriendUserId());
                friendInfo.put("nickname", friend.getNickname());
                friendInfo.put("isFavorite", friend.getIsFavorite());
                friendInfo.put("createdAt", friend.getCreatedAt());

                // 실제로는 UserEntity에서 사용자 정보 조회
                userRepository.findById(friend.getFriendUserId()).ifPresent(user -> {
                    friendInfo.put("username", user.getUsername());
                    // friendInfo.put("profileImageUrl", user.getProfileImageUrl());
                    // friendInfo.put("statusMessage", user.getStatusMessage());
                });

                return friendInfo;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("friends", friendList);
            response.put("count", friendList.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 받은 친구 요청 목록
     * GET /api/friends/requests/received?userId={userId}
     */
    @GetMapping("/requests/received")
    public ResponseEntity<?> getReceivedRequests(@RequestParam Long userId) {
        try {
            List<FriendRequestEntity> requests = friendService.getReceivedFriendRequests(userId);

            List<Map<String, Object>> requestList = requests.stream().map(req -> {
                Map<String, Object> reqInfo = new HashMap<>();
                reqInfo.put("id", req.getId());
                reqInfo.put("fromUserId", req.getFromUserId());
                reqInfo.put("message", req.getMessage());
                reqInfo.put("createdAt", req.getCreatedAt());

                userRepository.findById(req.getFromUserId()).ifPresent(user -> {
                    reqInfo.put("fromUsername", user.getUsername());
                });

                return reqInfo;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("requests", requestList);
            response.put("count", requestList.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 친구 삭제
     * DELETE /api/friends/{friendUserId}
     */
    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<?> removeFriend(
        @PathVariable Long friendUserId,
        @RequestParam Long userId
    ) {
        try {
            friendService.removeFriend(userId, friendUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구를 삭제했습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 친구 차단
     * POST /api/friends/{friendUserId}/block
     */
    @PostMapping("/{friendUserId}/block")
    public ResponseEntity<?> blockFriend(
        @PathVariable Long friendUserId,
        @RequestBody Map<String, Object> request
    ) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            friendService.blockFriend(userId, friendUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구를 차단했습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 1:1 채팅방 생성/조회
     * POST /api/friends/direct-chat
     */
    @PostMapping("/direct-chat")
    public ResponseEntity<?> createDirectChat(@RequestBody Map<String, Object> request) {
        try {
            Long user1Id = Long.parseLong(request.get("user1Id").toString());
            Long user2Id = Long.parseLong(request.get("user2Id").toString());

            String roomId = directChatService.getOrCreateDirectRoomId(user1Id, user2Id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("roomId", roomId);
            response.put("message", "1:1 채팅방이 생성되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 즐겨찾기 토글
     * POST /api/friends/{friendUserId}/favorite
     */
    @PostMapping("/{friendUserId}/favorite")
    public ResponseEntity<?> toggleFavorite(
        @PathVariable Long friendUserId,
        @RequestBody Map<String, Object> request
    ) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            friendService.toggleFavorite(userId, friendUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "즐겨찾기가 변경되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
