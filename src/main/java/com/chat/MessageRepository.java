package com.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    
    @Query("SELECT m FROM MessageEntity m WHERE m.roomId = :roomId ORDER BY m.timestamp DESC LIMIT 50")
    List<MessageEntity> findTop50ByRoomIdOrderByTimestampDesc(@Param("roomId") String roomId);
    
    List<MessageEntity> findByRoomIdOrderByTimestampAsc(String roomId);
    
    List<MessageEntity> findByExpiresAtBefore(LocalDateTime dateTime);
    
    List<MessageEntity> findBySecurityTypeAndTimestampBefore(MessageSecurityType securityType, LocalDateTime dateTime);
}