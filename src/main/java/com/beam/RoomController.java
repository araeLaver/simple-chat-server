package com.beam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomMemberRepository roomMemberRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createRoom(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            String roomName = request.get("roomName").toString();
            String description = request.getOrDefault("description", "").toString();
            String roomTypeStr = request.getOrDefault("roomType", "PUBLIC").toString();
            Integer maxMembers = request.containsKey("maxMembers")
                ? Integer.valueOf(request.get("maxMembers").toString())
                : 100;

            RoomEntity.RoomType roomType = RoomEntity.RoomType.valueOf(roomTypeStr);

            RoomEntity room = roomService.createRoom(userId, roomName, description, roomType, maxMembers);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("roomId", room.getId());
            response.put("roomName", room.getRoomName());
            response.put("roomType", room.getRoomType().toString());
            response.put("message", "방이 생성되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewRoom(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            String roomName = request.get("roomName").toString();
            String description = request.getOrDefault("description", "").toString();
            String roomTypeStr = request.getOrDefault("roomType", "PUBLIC").toString();
            Integer maxMembers = request.containsKey("maxMembers")
                ? Integer.valueOf(request.get("maxMembers").toString())
                : 100;

            RoomEntity.RoomType roomType = RoomEntity.RoomType.valueOf(roomTypeStr);

            RoomEntity room = roomService.createRoom(userId, roomName, description, roomType, maxMembers);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("roomId", room.getId());
            response.put("roomName", room.getRoomName());
            response.put("roomType", room.getRoomType().toString());
            response.put("message", "Room created successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            String roomName = request.containsKey("roomName") ? request.get("roomName").toString() : null;
            String description = request.containsKey("description") ? request.get("description").toString() : null;
            Integer maxMembers = request.containsKey("maxMembers")
                ? Integer.valueOf(request.get("maxMembers").toString())
                : null;

            RoomEntity room = roomService.updateRoom(roomId, userId, roomName, description, maxMembers);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("roomId", room.getId());
            response.put("roomName", room.getRoomName());
            response.put("message", "Room updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            roomService.deleteRoom(roomId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Room deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{roomId}/members")
    public ResponseEntity<?> addMember(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long inviterId = jwtUtil.getUserIdFromToken(jwtToken);
            Long userId = Long.valueOf(request.get("userId").toString());

            roomService.addMember(roomId, userId, inviterId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Member added successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{roomId}/members/{userId}")
    public ResponseEntity<?> removeMember(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId,
            @PathVariable Long userId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long removerId = jwtUtil.getUserIdFromToken(jwtToken);

            roomService.removeMember(roomId, userId, removerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Member removed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<?> leaveRoom(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            roomService.leaveRoom(roomId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Left room successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{roomId}/messages")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId,
            @RequestBody Map<String, Object> request) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            String content = request.get("content").toString();
            String messageTypeStr = request.getOrDefault("messageType", "TEXT").toString();
            GroupMessageEntity.MessageType messageType = GroupMessageEntity.MessageType.valueOf(messageTypeStr);

            GroupMessageEntity message = roomService.sendMessage(roomId, userId, content, messageType);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messageId", message.getId());
            response.put("content", message.getContent());
            response.put("timestamp", message.getTimestamp().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getRoomMessages(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            List<GroupMessageEntity> messages = roomService.getRoomMessages(roomId, userId);

            List<Map<String, Object>> result = messages.stream().map(msg -> {
                Optional<UserEntity> senderOpt = userRepository.findById(msg.getSenderId());

                Map<String, Object> msgMap = new HashMap<>();
                msgMap.put("id", msg.getId());
                msgMap.put("senderId", msg.getSenderId());
                msgMap.put("senderName", senderOpt.map(UserEntity::getDisplayName).orElse("Unknown"));
                msgMap.put("content", msg.getContent());
                msgMap.put("messageType", msg.getMessageType().toString());
                msgMap.put("timestamp", msg.getTimestamp().toString());
                msgMap.put("readCount", msg.getReadCount());
                msgMap.put("isMine", msg.getSenderId().equals(userId));

                return msgMap;
            }).collect(Collectors.toList());

            Collections.reverse(result);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{roomId}/read")
    public ResponseEntity<?> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            roomService.markAsRead(roomId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Messages marked as read");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/my-rooms")
    public ResponseEntity<?> getMyRooms(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            List<RoomEntity> rooms = roomService.getUserRooms(userId);

            List<Map<String, Object>> result = rooms.stream().map(room -> {
                Optional<RoomMemberEntity> membershipOpt = roomMemberRepository
                    .findByRoomIdAndUserIdAndIsActiveTrue(room.getId(), userId);

                Map<String, Object> roomMap = new HashMap<>();
                roomMap.put("roomId", room.getId());
                roomMap.put("roomName", room.getRoomName());
                roomMap.put("description", room.getDescription());
                roomMap.put("roomType", room.getRoomType().toString());
                roomMap.put("currentMembers", room.getCurrentMembers());
                roomMap.put("maxMembers", room.getMaxMembers());
                roomMap.put("lastMessage", room.getLastMessage());
                roomMap.put("lastMessageTime", room.getLastMessageTime() != null
                    ? room.getLastMessageTime().toString() : null);
                roomMap.put("unreadCount", membershipOpt.map(RoomMemberEntity::getUnreadCount).orElse(0));
                roomMap.put("myRole", membershipOpt.map(m -> m.getRole().toString()).orElse("MEMBER"));

                return roomMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{roomId}/members")
    public ResponseEntity<?> getRoomMembers(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roomId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwtToken);

            List<RoomMemberEntity> members = roomService.getRoomMembers(roomId, userId);

            List<Map<String, Object>> result = members.stream().map(member -> {
                Optional<UserEntity> userOpt = userRepository.findById(member.getUserId());

                Map<String, Object> memberMap = new HashMap<>();
                memberMap.put("userId", member.getUserId());
                memberMap.put("username", userOpt.map(UserEntity::getUsername).orElse("Unknown"));
                memberMap.put("displayName", userOpt.map(UserEntity::getDisplayName).orElse("Unknown"));
                memberMap.put("role", member.getRole().toString());
                memberMap.put("isOnline", userOpt.map(UserEntity::getIsOnline).orElse(false));
                memberMap.put("joinedAt", member.getJoinedAt().toString());

                return memberMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRooms(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword) {
        try {
            List<RoomEntity> rooms = roomService.searchRooms(keyword);

            List<Map<String, Object>> result = rooms.stream().map(room -> {
                Map<String, Object> roomMap = new HashMap<>();
                roomMap.put("roomId", room.getId());
                roomMap.put("roomName", room.getRoomName());
                roomMap.put("description", room.getDescription());
                roomMap.put("roomType", room.getRoomType().toString());
                roomMap.put("currentMembers", room.getCurrentMembers());
                roomMap.put("maxMembers", room.getMaxMembers());

                return roomMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}