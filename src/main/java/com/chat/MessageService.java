package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    public MessageEntity saveMessage(ChatMessage chatMessage) {
        MessageEntity entity = new MessageEntity(
            chatMessage.getSender(),
            chatMessage.getContent(),
            chatMessage.getRoomId(),
            chatMessage.getType() != null ? chatMessage.getType() : "message"
        );
        
        entity.setSecurityType(chatMessage.getSecurityType());
        entity.setIsEncrypted(chatMessage.getIsEncrypted());
        entity.setEncryptionKey(chatMessage.getEncryptionKey());
        
        if (chatMessage.getVolatileDuration() != null && chatMessage.getVolatileDuration() > 0) {
            entity.setExpiresAt(LocalDateTime.now().plusSeconds(chatMessage.getVolatileDuration()));
        }
        
        return messageRepository.save(entity);
    }
    
    public List<MessageEntity> getRecentMessages(String roomId) {
        return messageRepository.findTop50ByRoomIdOrderByTimestampDesc(roomId);
    }
    
    public List<MessageEntity> getAllRoomMessages(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}