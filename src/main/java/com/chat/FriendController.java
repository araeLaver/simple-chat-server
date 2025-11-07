package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * 친구 요청 보내기
     * POST /api/friends/request
     */
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            Long friendId = request.get("friendId");

            FriendEntity friendRequest = friendService.sendFriendRequest(userId, friendId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구 요청을 보냈습니다.");
            response.put("data", friendRequest);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 친구 요청 수락
     * POST /api/friends/accept
     */
    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            Long requesterId = request.get("requesterId");

            FriendEntity friendship = friendService.acceptFriendRequest(userId, requesterId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구 요청을 수락했습니다.");
            response.put("data", friendship);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 친구 삭제
     * DELETE /api/friends/{friendId}
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> removeFriend(@RequestParam Long userId, @PathVariable Long friendId) {
        try {
            friendService.removeFriend(userId, friendId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구를 삭제했습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 친구 차단
     * POST /api/friends/block
     */
    @PostMapping("/block")
    public ResponseEntity<?> blockFriend(@RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            Long friendId = request.get("friendId");

            FriendEntity blocked = friendService.blockFriend(userId, friendId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "친구를 차단했습니다.");
            response.put("data", blocked);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 내 친구 목록 조회
     * GET /api/friends?userId={userId}
     */
    @GetMapping
    public ResponseEntity<?> getFriendList(@RequestParam Long userId) {
        try {
            List<UserEntity> friends = friendService.getFriendList(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", friends);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 받은 친구 요청 목록
     * GET /api/friends/requests/received?userId={userId}
     */
    @GetMapping("/requests/received")
    public ResponseEntity<?> getReceivedRequests(@RequestParam Long userId) {
        try {
            List<UserEntity> requests = friendService.getPendingFriendRequests(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", requests);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 보낸 친구 요청 목록
     * GET /api/friends/requests/sent?userId={userId}
     */
    @GetMapping("/requests/sent")
    public ResponseEntity<?> getSentRequests(@RequestParam Long userId) {
        try {
            List<UserEntity> requests = friendService.getSentFriendRequests(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", requests);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }
}
