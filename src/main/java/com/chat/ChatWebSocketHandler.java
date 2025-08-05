package com.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private MessageService messageService;

    public ChatWebSocketHandler() {
        chatRooms.put("general", new ChatRoom("general", "일반 채팅방", RoomType.NORMAL, null));
        chatRooms.put("tech", new ChatRoom("tech", "개발 이야기", RoomType.NORMAL, null));
        chatRooms.put("casual", new ChatRoom("casual", "자유 토론", RoomType.NORMAL, null));
        chatRooms.put("secret", new ChatRoom("secret", "🔐 비밀 대화", RoomType.SECRET, "secret123"));
        chatRooms.put("volatile", new ChatRoom("volatile", "⏰ 임시 채팅", RoomType.VOLATILE, null));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        sendRoomList(session);
        System.out.println("새로운 연결: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            
            if ("joinRoom".equals(chatMessage.getType())) {
                String roomId = chatMessage.getRoomId();
                if (roomId == null) roomId = "general";
                
                leaveCurrentRoom(session);
                joinRoom(session, roomId, chatMessage.getSender());
                
            } else if ("message".equals(chatMessage.getType()) || chatMessage.getType() == null) {
                String roomId = sessionToRoom.get(session.getId());
                if (roomId != null) {
                    chatMessage.setType("message");
                    chatMessage.setRoomId(roomId);
                    
                    ChatRoom room = chatRooms.get(roomId);
                    if (room != null) {
                        processSecureMessage(chatMessage, room);
                        messageService.saveMessage(chatMessage);
                        broadcastToRoom(roomId, chatMessage);
                    }
                }
            } else if ("file".equals(chatMessage.getType())) {
                String roomId = sessionToRoom.get(session.getId());
                if (roomId != null) {
                    chatMessage.setRoomId(roomId);
                    
                    messageService.saveMessage(chatMessage);
                    broadcastToRoom(roomId, chatMessage);
                }
            } else if ("getHistory".equals(chatMessage.getType())) {
                String roomId = chatMessage.getRoomId();
                if (roomId != null) {
                    sendMessageHistory(session, roomId);
                }
            } else if ("createRoom".equals(chatMessage.getType())) {
                handleCreateRoom(session, chatMessage);
            } else if ("joinSecretRoom".equals(chatMessage.getType())) {
                handleJoinSecretRoom(session, chatMessage);
            } else if ("deleteRoom".equals(chatMessage.getType())) {
                handleDeleteRoom(session, chatMessage);
            }
            
            System.out.println("메시지 처리: " + chatMessage.getSender() + " - " + chatMessage.getContent());
            
        } catch (Exception e) {
            System.err.println("메시지 처리 오류: " + e.getMessage());
        }
    }

    private void joinRoom(WebSocketSession session, String roomId, String username) throws Exception {
        ChatRoom room = chatRooms.get(roomId);
        if (room == null) return;
        
        User user = new User(session.getId(), username, session.getId());
        users.put(session.getId(), user);
        room.addUser(user);
        sessionToRoom.put(session.getId(), roomId);
        
        ChatMessage joinMessage = new ChatMessage("시스템", 
            username + "님이 " + room.getRoomName() + "에 입장하셨습니다.", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
            "system");
        joinMessage.setRoomId(roomId);
        
        broadcastToRoom(roomId, joinMessage);
        sendRoomUserList(roomId);
        sendMessageHistory(session, roomId);
    }

    private void leaveCurrentRoom(WebSocketSession session) throws Exception {
        String currentRoomId = sessionToRoom.get(session.getId());
        if (currentRoomId != null) {
            ChatRoom room = chatRooms.get(currentRoomId);
            User user = room.removeUser(session.getId());
            sessionToRoom.remove(session.getId());
            
            if (user != null) {
                ChatMessage leaveMessage = new ChatMessage("시스템", 
                    user.getUsername() + "님이 " + room.getRoomName() + "에서 퇴장하셨습니다.", 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
                    "system");
                leaveMessage.setRoomId(currentRoomId);
                
                broadcastToRoom(currentRoomId, leaveMessage);
                sendRoomUserList(currentRoomId);
            }
        }
    }

    private void broadcastToRoom(String roomId, ChatMessage message) throws Exception {
        ChatRoom room = chatRooms.get(roomId);
        if (room == null) return;
        
        String messageJson = objectMapper.writeValueAsString(message);
        for (User user : room.getUsers().values()) {
            WebSocketSession userSession = findSessionById(user.getSessionId());
            if (userSession != null && userSession.isOpen()) {
                userSession.sendMessage(new TextMessage(messageJson));
            }
        }
    }

    private void sendRoomUserList(String roomId) throws Exception {
        ChatRoom room = chatRooms.get(roomId);
        if (room == null) return;
        
        ChatMessage userListMessage = new ChatMessage("시스템", 
            objectMapper.writeValueAsString(room.getUsers().values()), 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
            "userlist");
        userListMessage.setRoomId(roomId);
        
        broadcastToRoom(roomId, userListMessage);
    }

    private void sendRoomList(WebSocketSession session) throws Exception {
        // 방 정보를 더 자세히 포함하는 객체 생성
        List<Map<String, Object>> roomDetails = new ArrayList<>();
        
        for (ChatRoom room : chatRooms.values()) {
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put("roomId", room.getRoomId());
            roomInfo.put("roomName", room.getRoomName());
            roomInfo.put("roomType", room.getRoomType().toString());
            roomInfo.put("userCount", room.getUserCount());
            roomInfo.put("creator", room.getCreator());
            roomInfo.put("description", room.getDescription());
            roomInfo.put("isSecureRoom", room.isSecureRoom());
            // 비밀번호는 보안상 전송하지 않음
            
            roomDetails.add(roomInfo);
        }
        
        ChatMessage roomListMessage = new ChatMessage("시스템", 
            objectMapper.writeValueAsString(roomDetails), 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
            "roomlist");
            
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(roomListMessage)));
        }
    }

    private void sendMessageHistory(WebSocketSession session, String roomId) throws Exception {
        List<MessageEntity> messages = messageService.getRecentMessages(roomId);
        ChatRoom room = chatRooms.get(roomId);
        
        for (MessageEntity msg : messages) {
            if (msg.isExpired()) {
                continue;
            }
            
            ChatMessage historyMessage = new ChatMessage(
                msg.getSender(), 
                msg.getContent(), 
                msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                msg.getMessageType()
            );
            historyMessage.setRoomId(roomId);
            historyMessage.setSecurityType(msg.getSecurityType());
            historyMessage.setIsEncrypted(msg.getIsEncrypted());
            
            if (room != null) {
                historyMessage = decryptMessageForDisplay(historyMessage, room);
            }
            
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(historyMessage)));
            }
        }
    }

    private WebSocketSession findSessionById(String sessionId) {
        return sessions.stream()
            .filter(session -> session.getId().equals(sessionId))
            .findFirst()
            .orElse(null);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        leaveCurrentRoom(session);
        users.remove(session.getId());
        
        System.out.println("연결 종료: " + session.getId());
    }
    
    private void processSecureMessage(ChatMessage chatMessage, ChatRoom room) {
        switch (room.getRoomType()) {
            case SECRET:
                chatMessage.setSecurityType(MessageSecurityType.SECRET);
                if (room.getEncryptionKey() != null) {
                    try {
                        String encryptedContent = EncryptionUtil.encrypt(chatMessage.getContent(), room.getEncryptionKey());
                        chatMessage.setContent(encryptedContent);
                        chatMessage.setIsEncrypted(true);
                        chatMessage.setEncryptionKey(room.getEncryptionKey());
                    } catch (Exception e) {
                        System.err.println("암호화 실패: " + e.getMessage());
                    }
                }
                break;
                
            case VOLATILE:
                chatMessage.setSecurityType(MessageSecurityType.VOLATILE);
                if (chatMessage.getVolatileDuration() != null && chatMessage.getVolatileDuration() > 0) {
                    LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(chatMessage.getVolatileDuration());
                    chatMessage.setType("volatile");
                }
                break;
                
            case NORMAL:
            default:
                chatMessage.setSecurityType(MessageSecurityType.NORMAL);
                break;
        }
    }
    
    private ChatMessage decryptMessageForDisplay(ChatMessage message, ChatRoom room) {
        if (message.getIsEncrypted() != null && message.getIsEncrypted() && 
            room.getRoomType() == RoomType.SECRET && room.getEncryptionKey() != null) {
            try {
                String decryptedContent = EncryptionUtil.decrypt(message.getContent(), room.getEncryptionKey());
                ChatMessage decryptedMessage = new ChatMessage(
                    message.getSender(), 
                    decryptedContent, 
                    message.getTimestamp(), 
                    message.getType()
                );
                decryptedMessage.setRoomId(message.getRoomId());
                decryptedMessage.setSecurityType(message.getSecurityType());
                return decryptedMessage;
            } catch (Exception e) {
                System.err.println("복호화 실패: " + e.getMessage());
                message.setContent("🔐 암호화된 메시지");
            }
        }
        return message;
    }

    private void handleCreateRoom(WebSocketSession session, ChatMessage message) throws Exception {
        try {
            String roomName = message.getRoomName();
            String roomTypeStr = message.getRoomType();
            String creator = message.getCreator();
            String description = message.getDescription();
            String password = message.getPassword();
            
            if (roomName == null || roomName.trim().isEmpty()) {
                sendErrorMessage(session, "방 이름을 입력하세요.");
                return;
            }
            
            // 방 이름 중복 체크
            if (isRoomNameDuplicate(roomName.trim())) {
                sendErrorMessage(session, "이미 존재하는 방 이름입니다. 다른 이름을 사용해주세요.");
                return;
            }
            
            // 고유한 방 ID 생성
            String roomId = "custom_" + System.currentTimeMillis();
            
            // 방 유형에 따른 RoomType 설정
            RoomType type = RoomType.NORMAL;
            if ("SECRET".equals(roomTypeStr)) {
                type = RoomType.SECRET;
                if (password == null || password.trim().isEmpty()) {
                    sendErrorMessage(session, "비밀방은 비밀번호가 필요합니다.");
                    return;
                }
            } else if ("VOLATILE".equals(roomTypeStr)) {
                type = RoomType.VOLATILE;
            }
            
            // 방 생성
            ChatRoom newRoom = new ChatRoom(roomId, roomName.trim(), type, password, creator, description);
            chatRooms.put(roomId, newRoom);
            
            System.out.println("새 방 생성: " + roomName + " (" + type + ") by " + creator);
            
            // 전체 사용자에게 방 목록 업데이트 전송
            broadcastRoomListUpdate();
            
            // 생성자를 자동으로 방에 입장시킴
            joinRoom(session, roomId, creator);
            
            // 성공 메시지 전송
            ChatMessage successMessage = new ChatMessage("시스템", 
                "방 '" + roomName + "'이 성공적으로 생성되었습니다!", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
                "success");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(successMessage)));
            
        } catch (Exception e) {
            System.err.println("방 생성 오류: " + e.getMessage());
            sendErrorMessage(session, "방 생성 중 오류가 발생했습니다.");
        }
    }

    private void handleJoinSecretRoom(WebSocketSession session, ChatMessage message) throws Exception {
        String roomId = message.getRoomId();
        String password = message.getContent();
        String username = message.getSender();
        
        ChatRoom room = chatRooms.get(roomId);
        if (room == null) {
            sendErrorMessage(session, "존재하지 않는 방입니다.");
            return;
        }
        
        if (room.getRoomType() != RoomType.SECRET) {
            sendErrorMessage(session, "비밀방이 아닙니다.");
            return;
        }
        
        if (!room.verifyPassword(password)) {
            sendErrorMessage(session, "비밀번호가 올바르지 않습니다.");
            return;
        }
        
        // 비밀번호가 맞으면 방에 입장
        leaveCurrentRoom(session);
        joinRoom(session, roomId, username);
    }

    private void broadcastRoomListUpdate() throws Exception {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                sendRoomList(session);
            }
        }
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage) throws Exception {
        ChatMessage error = new ChatMessage("시스템", errorMessage, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), "error");
        
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
        }
    }
    
    private boolean isRoomNameDuplicate(String roomName) {
        return chatRooms.values().stream()
            .anyMatch(room -> room.getRoomName().equals(roomName));
    }
    
    private void handleDeleteRoom(WebSocketSession session, ChatMessage message) throws Exception {
        try {
            String roomId = message.getRoomId();
            String requestUser = message.getSender();
            
            if (roomId == null || roomId.trim().isEmpty()) {
                sendErrorMessage(session, "삭제할 방을 지정하세요.");
                return;
            }
            
            ChatRoom room = chatRooms.get(roomId);
            if (room == null) {
                sendErrorMessage(session, "존재하지 않는 방입니다.");
                return;
            }
            
            // 기본 방은 삭제 불가
            if (roomId.equals("general") || roomId.equals("tech") || roomId.equals("casual") || 
                roomId.equals("secret") || roomId.equals("volatile")) {
                sendErrorMessage(session, "기본 방은 삭제할 수 없습니다.");
                return;
            }
            
            // 방장만 삭제 가능
            if (!requestUser.equals(room.getCreator())) {
                sendErrorMessage(session, "방장만 방을 삭제할 수 있습니다.");
                return;
            }
            
            // 방에 있는 모든 사용자를 내보냄
            for (User user : new ArrayList<>(room.getUsers().values())) {
                WebSocketSession userSession = findSessionById(user.getSessionId());
                if (userSession != null) {
                    sessionToRoom.remove(userSession.getId());
                    
                    // 삭제 알림 메시지 전송
                    ChatMessage deleteMessage = new ChatMessage("시스템", 
                        "방이 삭제되었습니다. 로비로 이동합니다.", 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
                        "roomDeleted");
                    deleteMessage.setRoomId(roomId);
                    
                    if (userSession.isOpen()) {
                        userSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(deleteMessage)));
                    }
                }
            }
            
            // 방 삭제
            chatRooms.remove(roomId);
            
            System.out.println("방 삭제: " + room.getRoomName() + " by " + requestUser);
            
            // 모든 사용자에게 방 목록 업데이트
            broadcastRoomListUpdate();
            
            // 성공 메시지 전송
            ChatMessage successMessage = new ChatMessage("시스템", 
                "방이 성공적으로 삭제되었습니다.", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
                "success");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(successMessage)));
            
        } catch (Exception e) {
            System.err.println("방 삭제 오류: " + e.getMessage());
            sendErrorMessage(session, "방 삭제 중 오류가 발생했습니다.");
        }
    }
}