package com.beam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);
            Long friendId = Long.valueOf(request.get("friendId").toString());

            FriendEntity friendRequest = friendService.sendFriendRequest(userId, friendId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Friend request sent");
            response.put("requestId", friendRequest.getId());
            response.put("status", friendRequest.getStatus().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);
            Long requesterId = Long.valueOf(request.get("requesterId").toString());

            FriendEntity friendship = friendService.acceptFriendRequest(userId, requesterId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Friend request accepted");
            response.put("friendshipId", friendship.getId());
            response.put("status", friendship.getStatus().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectFriendRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);
            Long requesterId = Long.valueOf(request.get("requesterId").toString());

            friendService.rejectFriendRequest(userId, requesterId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Friend request rejected");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/block")
    public ResponseEntity<?> blockUser(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);
            Long blockUserId = Long.valueOf(request.get("userId").toString());

            friendService.blockUser(userId, blockUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User blocked");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/unfriend/{friendId}")
    public ResponseEntity<?> unfriend(
            @RequestHeader("Authorization") String token,
            @PathVariable Long friendId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            friendService.unfriend(userId, friendId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Friend removed");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getFriendList(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            List<FriendEntity> friendships = friendService.getFriendList(userId);

            List<Map<String, Object>> result = friendships.stream().map(friendship -> {
                Long friendId = friendship.getUserId().equals(userId)
                    ? friendship.getFriendId()
                    : friendship.getUserId();

                Optional<UserEntity> friendOpt = userRepository.findById(friendId);

                Map<String, Object> friendMap = new HashMap<>();
                friendMap.put("friendId", friendId);
                friendMap.put("username", friendOpt.map(UserEntity::getUsername).orElse("Unknown"));
                friendMap.put("displayName", friendOpt.map(UserEntity::getDisplayName).orElse("Unknown"));
                friendMap.put("phoneNumber", friendOpt.map(UserEntity::getPhoneNumber).orElse(null));
                friendMap.put("isOnline", friendOpt.map(UserEntity::getIsOnline).orElse(false));
                friendMap.put("lastSeen", friendOpt.map(u -> u.getLastSeen() != null ? u.getLastSeen().toString() : null).orElse(null));
                friendMap.put("friendsSince", friendship.getAcceptedAt() != null ? friendship.getAcceptedAt().toString() : null);

                return friendMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/requests/received")
    public ResponseEntity<?> getPendingRequestsReceived(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            List<FriendEntity> requests = friendService.getPendingRequestsReceived(userId);

            List<Map<String, Object>> result = requests.stream().map(request -> {
                Long requesterId = request.getUserId();
                Optional<UserEntity> requesterOpt = userRepository.findById(requesterId);

                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("requestId", request.getId());
                requestMap.put("requesterId", requesterId);
                requestMap.put("username", requesterOpt.map(UserEntity::getUsername).orElse("Unknown"));
                requestMap.put("displayName", requesterOpt.map(UserEntity::getDisplayName).orElse("Unknown"));
                requestMap.put("requestedAt", request.getRequestedAt() != null ? request.getRequestedAt().toString() : null);

                return requestMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/requests/sent")
    public ResponseEntity<?> getPendingRequestsSent(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            List<FriendEntity> requests = friendService.getPendingRequestsSent(userId);

            List<Map<String, Object>> result = requests.stream().map(request -> {
                Long friendId = request.getFriendId();
                Optional<UserEntity> friendOpt = userRepository.findById(friendId);

                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("requestId", request.getId());
                requestMap.put("friendId", friendId);
                requestMap.put("username", friendOpt.map(UserEntity::getUsername).orElse("Unknown"));
                requestMap.put("displayName", friendOpt.map(UserEntity::getDisplayName).orElse("Unknown"));
                requestMap.put("requestedAt", request.getRequestedAt() != null ? request.getRequestedAt().toString() : null);

                return requestMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/requests/count")
    public ResponseEntity<?> getPendingRequestCount(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            Integer count = friendService.getPendingRequestCount(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam String query) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            List<UserEntity> users = friendService.searchUsers(query);

            List<Map<String, Object>> result = users.stream()
                .filter(user -> !user.getId().equals(userId))
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("displayName", user.getDisplayName());
                    userMap.put("phoneNumber", user.getPhoneNumber());
                    userMap.put("isOnline", user.getIsOnline());

                    return userMap;
                }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}