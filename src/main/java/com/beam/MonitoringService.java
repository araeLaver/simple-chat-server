package com.beam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MonitoringService {

    @Autowired(required = false)
    private DirectMessageRepository directMessageRepository;

    @Autowired(required = false)
    private GroupMessageRepository groupMessageRepository;

    @Autowired(required = false)
    private UserRepository userRepository;

    @Autowired(required = false)
    private RoomRepository roomRepository;

    private final AtomicLong totalMessages = new AtomicLong(0);
    private final AtomicLong activeUsers = new AtomicLong(0);
    private final AtomicLong activeRooms = new AtomicLong(0);

    @Scheduled(fixedRate = 300000)
    public void updateMetrics() {
        try {
            if (directMessageRepository != null && groupMessageRepository != null) {
                long dmCount = directMessageRepository.count();
                long groupCount = groupMessageRepository.count();
                totalMessages.set(dmCount + groupCount);
            }

            if (userRepository != null) {
                long onlineUsers = userRepository.findAll().stream()
                    .filter(user -> user.getIsOnline() != null && user.getIsOnline())
                    .count();
                activeUsers.set(onlineUsers);
            }

            if (roomRepository != null) {
                long rooms = roomRepository.count();
                activeRooms.set(rooms);
            }

            System.out.println("=== BEAM 메신저 모니터링 ===");
            System.out.println("시간: " + LocalDateTime.now());
            System.out.println("총 메시지: " + totalMessages.get());
            System.out.println("온라인 사용자: " + activeUsers.get());
            System.out.println("활성 채팅방: " + activeRooms.get());
            System.out.println("==========================");

        } catch (Exception e) {
            System.err.println("모니터링 업데이트 실패: " + e.getMessage());
        }
    }

    public long getTotalMessages() {
        return totalMessages.get();
    }

    public long getActiveUsers() {
        return activeUsers.get();
    }

    public long getActiveRooms() {
        return activeRooms.get();
    }
}