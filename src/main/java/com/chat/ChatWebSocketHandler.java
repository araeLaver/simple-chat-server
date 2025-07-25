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
        chatRooms.put("general", new ChatRoom("general", "일반 채팅방"));
        chatRooms.put("tech", new ChatRoom("tech", "개발 이야기"));
        chatRooms.put("casual", new ChatRoom("casual", "자유 토론"));
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
                    
                    messageService.saveMessage(chatMessage);
                    broadcastToRoom(roomId, chatMessage);
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
        ChatMessage roomListMessage = new ChatMessage("시스템", 
            objectMapper.writeValueAsString(chatRooms.values()), 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
            "roomlist");
            
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(roomListMessage)));
        }
    }

    private void sendMessageHistory(WebSocketSession session, String roomId) throws Exception {
        List<MessageEntity> messages = messageService.getRecentMessages(roomId);
        
        for (int i = messages.size() - 1; i >= 0; i--) {
            MessageEntity msg = messages.get(i);
            ChatMessage historyMessage = new ChatMessage(
                msg.getSender(), 
                msg.getContent(), 
                msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                msg.getMessageType()
            );
            historyMessage.setRoomId(roomId);
            
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
}