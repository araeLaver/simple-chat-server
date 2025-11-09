package com.beam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MessageCleanupScheduler {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredMessages() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<MessageEntity> expiredMessages = messageRepository.findByExpiresAtBefore(now);
            
            if (!expiredMessages.isEmpty()) {
                messageRepository.deleteAll(expiredMessages);
                System.out.println("만료된 메시지 " + expiredMessages.size() + "개 삭제됨");
            }
            
        } catch (Exception e) {
            System.err.println("메시지 정리 작업 실패: " + e.getMessage());
        }
    }
    
    @Scheduled(fixedRate = 300000)
    public void cleanupOldVolatileMessages() {
        try {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            List<MessageEntity> oldVolatileMessages = messageRepository
                .findBySecurityTypeAndTimestampBefore(MessageSecurityType.VOLATILE, oneHourAgo);
            
            if (!oldVolatileMessages.isEmpty()) {
                messageRepository.deleteAll(oldVolatileMessages);
                System.out.println("오래된 휘발성 메시지 " + oldVolatileMessages.size() + "개 삭제됨");
            }
            
        } catch (Exception e) {
            System.err.println("휘발성 메시지 정리 작업 실패: " + e.getMessage());
        }
    }
}