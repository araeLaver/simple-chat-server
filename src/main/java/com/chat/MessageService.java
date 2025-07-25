package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return messageRepository.save(entity);
    }
    
    public List<MessageEntity> getRecentMessages(String roomId) {
        return messageRepository.findTop50ByRoomIdOrderByTimestampDesc(roomId);
    }
    
    public List<MessageEntity> getAllRoomMessages(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}