# 🎯 카카오톡 대체 메신저 개발 로드맵

> **목표**: 대한민국 대표 메신저로 기초적이고 기본적인 메신저 기능 완벽 구현

---

## 📊 현재 상태 평가

### ✅ 구현 완료 (30%)
- [x] 그룹 채팅방
- [x] 실시간 메시지 전송
- [x] 메시지 암호화 (AES-GCM)
- [x] 비밀방 / 임시방
- [x] PWA 지원
- [x] 다크모드
- [x] 방 생성/삭제

### ❌ 치명적으로 부족 (70%)
- [ ] **1:1 개인 채팅**
- [ ] **친구/연락처 시스템**
- [ ] **읽음 표시**
- [ ] **실제 파일 전송**
- [ ] **이미지 프리뷰**
- [ ] **푸시 알림**
- [ ] **메시지 검색**
- [ ] **프로필 관리**
- [ ] **안읽은 메시지 카운트**

---

## 🚀 Phase 1: 긴급 보안 패치 (완료 ✅)

**기간**: 1일
**상태**: ✅ 완료

### 완료 항목
- [x] DB 자격증명 환경변수화
- [x] BCrypt 비밀번호 해싱
- [x] AES-GCM 암호화 적용
- [x] CORS Origin 제한
- [x] Spring Security 의존성 추가

### 🔴 즉시 조치 필요
```bash
# 1. 노출된 DB 비밀번호 변경 (즉시!)
# Koyeb 대시보드에서 PostgreSQL 비밀번호 재설정

# 2. 환경변수 설정
cp .env.example .env
# .env 파일 편집하여 실제 값 입력

# 3. Git 히스토리에서 민감정보 제거 (선택)
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch src/main/resources/application-prod.properties' \
  --prune-empty --tag-name-filter cat -- --all
```

---

## 🎯 Phase 2: 기본 메신저 핵심 기능 (4주)

### Week 1: 1:1 채팅 시스템

**우선순위**: 🔴 최고
**없으면 메신저가 아님**

#### 구현 항목
1. **친구 시스템**
   ```java
   // 신규 엔티티
   @Entity FriendEntity {
       Long id;
       Long userId;
       Long friendId;
       FriendStatus status; // PENDING, ACCEPTED, BLOCKED
       LocalDateTime createdAt;
   }

   @Entity FriendRequestEntity {
       Long id;
       Long fromUserId;
       Long toUserId;
       String message;
       RequestStatus status;
   }
   ```

2. **1:1 채팅방 자동 생성**
   ```java
   // 친구 클릭 시 자동으로 1:1 방 생성
   public String getOrCreateDirectRoom(Long user1Id, Long user2Id) {
       String roomId = "direct_" + Math.min(user1Id, user2Id)
                      + "_" + Math.max(user1Id, user2Id);
       // 기존 방 찾기 or 신규 생성
   }
   ```

3. **친구 목록 UI**
   - 온라인/오프라인 상태 표시
   - 마지막 메시지 미리보기
   - 안읽은 메시지 개수

#### 예상 산출물
- `FriendService.java`
- `FriendRepository.java`
- `DirectChatController.java`
- 프론트엔드 친구 목록 UI

---

### Week 2: 읽음 표시 & 타이핑 인디케이터

**우선순위**: 🔴 최고
**카카오톡 기본 중의 기본**

#### 구현 항목
1. **읽음 표시 (Read Receipt)**
   ```java
   @Entity MessageReadStatus {
       Long messageId;
       Long userId;
       LocalDateTime readAt;
   }

   // WebSocket 메시지 타입 추가
   {
       type: "messageRead",
       messageId: 12345,
       userId: 67890,
       readAt: "2025-10-24T15:30:00"
   }
   ```

2. **타이핑 인디케이터**
   ```javascript
   // 입력 중 상태 전송 (300ms 디바운스)
   socket.send({
       type: "typing",
       roomId: "room123",
       userId: "user456",
       isTyping: true
   });
   ```

3. **안읽은 메시지 카운트**
   ```sql
   -- 효율적인 쿼리 필요
   SELECT room_id, COUNT(*) as unread_count
   FROM messages
   WHERE room_id IN (user_rooms)
     AND created_at > last_read_at
   GROUP BY room_id;
   ```

#### UI 구현
- 메시지 옆에 "읽음" / 숫자 표시
- 채팅방 목록에 빨간 배지
- "OOO님이 입력 중..." 표시

---

### Week 3: 파일 전송 시스템

**우선순위**: 🟠 높음
**이미지는 메신저 필수**

#### 구현 항목
1. **파일 저장소 구축**
   ```yaml
   # 선택지
   옵션 1: AWS S3 (추천)
   옵션 2: MinIO (오픈소스 S3 호환)
   옵션 3: Cloudflare R2 (저렴)
   ```

2. **파일 업로드 API**
   ```java
   @PostMapping("/api/files/upload")
   public ResponseEntity<FileResponse> uploadFile(
       @RequestParam("file") MultipartFile file,
       @RequestParam("roomId") String roomId
   ) {
       // 1. 파일 검증 (크기, 타입, 악성코드)
       // 2. S3 업로드
       // 3. 썸네일 생성 (이미지인 경우)
       // 4. DB에 파일 메타데이터 저장
       // 5. WebSocket으로 전송
   }
   ```

3. **지원 파일 타입**
   - 이미지: JPG, PNG, GIF, WebP (최대 20MB)
   - 문서: PDF, DOC, XLS (최대 50MB)
   - 압축: ZIP, RAR (최대 100MB)
   - 영상: MP4 (최대 300MB)

4. **보안 강화**
   ```java
   // 안티바이러스 스캔
   // 파일명 sanitization
   // Content-Type 검증
   // 악성 확장자 차단
   ```

#### UI 구현
- 드래그 앤 드롭 업로드
- 이미지 인라인 프리뷰
- 진행률 표시
- 다운로드 버튼

---

### Week 4: 메시지 검색 & 프로필

**우선순위**: 🟠 높음

#### 1. 메시지 검색
```sql
-- Full-Text Search (PostgreSQL)
CREATE INDEX idx_messages_content_fts
ON messages USING gin(to_tsvector('korean', content));

-- 검색 쿼리
SELECT * FROM messages
WHERE to_tsvector('korean', content) @@ to_tsquery('korean', :query)
  AND room_id = :roomId
ORDER BY timestamp DESC
LIMIT 50;
```

#### 2. 프로필 시스템
```java
@Entity UserProfile {
    Long userId;
    String displayName;
    String statusMessage;
    String profileImageUrl;
    String backgroundImageUrl;
    LocalDateTime birthday;
    Boolean isPublic;
}
```

#### 3. 프로필 기능
- 프로필 사진 업로드
- 상태 메시지 설정
- 배경 이미지
- 생일 표시 (선택)

---

## 🏗 Phase 3: 확장성 & 안정성 (3주)

### Week 5: Redis 세션 관리

**필수**: 서버 2대 이상 운영 불가능

#### 구현
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

```properties
# application.properties
spring.session.store-type=redis
spring.redis.host=${REDIS_HOST}
spring.redis.port=${REDIS_PORT}
spring.redis.password=${REDIS_PASSWORD}
```

#### 혜택
- ✅ 서버 재시작해도 세션 유지
- ✅ 로드 밸런서 사용 가능
- ✅ 100만 동시접속 대응

---

### Week 6: 메시지 브로커 (RabbitMQ)

**목적**: 서버 간 메시지 동기화

#### 구조
```
User A (Server 1) → RabbitMQ → Server 2 → User B
                              → Server 3 → User C
```

#### 구현
```java
@Configuration
public class RabbitMQConfig {
    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange("chat.exchange");
    }

    @Bean
    public Queue messageQueue() {
        return new Queue("chat.messages");
    }
}

@Service
public class MessageBroadcaster {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void broadcast(ChatMessage message) {
        rabbitTemplate.convertAndSend(
            "chat.exchange",
            "message." + message.getRoomId(),
            message
        );
    }
}
```

---

### Week 7: 모니터링 & 로깅

#### 1. Prometheus + Grafana
```yaml
# docker-compose.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
```

#### 2. 주요 메트릭
- 동시 접속자 수
- 메시지 처리 속도 (msg/sec)
- DB 커넥션 풀 사용률
- WebSocket 연결 수
- API 응답 시간 (P50, P95, P99)

#### 3. 알림 설정
```yaml
# alertmanager.yml
groups:
  - name: chat_alerts
    rules:
      - alert: HighCPUUsage
        expr: cpu_usage > 80
        for: 5m
      - alert: MessageQueueBacklog
        expr: message_queue_size > 10000
```

---

## 🎨 Phase 4: UX 개선 (2주)

### Week 8: 사용자 경험 향상

#### 1. 무한 스크롤
```javascript
// 메시지 히스토리 lazy loading
const messageObserver = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) {
        loadMoreMessages();
    }
});
```

#### 2. 이모지 & 반응
```javascript
// 메시지에 이모지 반응
{
    type: "reaction",
    messageId: 12345,
    emoji: "👍",
    userId: 67890
}
```

#### 3. 메시지 답장
```html
<div class="reply-preview">
    <span class="reply-to">@홍길동</span>
    <span class="reply-content">안녕하세요</span>
</div>
```

#### 4. 멘션 (@)
```javascript
// @ 입력 시 친구 목록 자동완성
@홍길동 <- 클릭하면 알림 전송
```

---

### Week 9: 성능 최적화

#### 1. 프론트엔드 최적화
```bash
# Vite 빌드 도구 도입
npm install vite
npm install @vitejs/plugin-react

# 코드 스플리팅
import { lazy, Suspense } from 'react';
const ChatRoom = lazy(() => import('./ChatRoom'));
```

#### 2. 이미지 최적화
- WebP 변환 (75% 용량 감소)
- 썸네일 자동 생성
- CDN 배포

#### 3. DB 쿼리 최적화
```sql
-- 복합 인덱스 추가
CREATE INDEX idx_messages_room_timestamp
ON messages(room_id, timestamp DESC);

CREATE INDEX idx_messages_user_unread
ON messages(user_id, is_read)
WHERE is_read = false;
```

---

## ⚖️ Phase 5: 법적 준수 (1주)

### Week 10: 개인정보보호법 대응

#### 1. 필수 문서
- [ ] 개인정보 처리방침
- [ ] 이용약관
- [ ] 위치기반서비스 이용약관
- [ ] 청소년 보호정책

#### 2. 개인정보 보호 기능
```java
// 회원 탈퇴
@DeleteMapping("/api/users/me")
public ResponseEntity<?> deleteAccount() {
    // 1. 개인정보 완전 삭제
    // 2. 메시지 익명화 (법적 보존기간 고려)
    // 3. 파일 삭제
    // 4. 로그 삭제
}

// 개인정보 다운로드 (데이터 이동권)
@GetMapping("/api/users/me/export")
public ResponseEntity<byte[]> exportUserData() {
    // JSON 형식으로 모든 데이터 제공
}
```

#### 3. 보안 인증
- [ ] ISMS-P 인증 준비
- [ ] 개인정보 영향평가 (PIA)
- [ ] 모의해킹 테스트

---

## 🧪 Phase 6: 품질 보증 (계속)

### 테스트 커버리지 목표: 80%

#### 1. 단위 테스트
```java
@SpringBootTest
class MessageServiceTest {
    @Test
    void 메시지_저장_테스트() {
        ChatMessage message = new ChatMessage("user1", "Hello");
        MessageEntity saved = messageService.save(message);
        assertNotNull(saved.getId());
    }

    @Test
    void 암호화_메시지_저장_테스트() {
        // AES-GCM 암호화 검증
    }
}
```

#### 2. 통합 테스트
```java
@WebMvcTest
class ChatWebSocketHandlerTest {
    @Test
    void WebSocket_연결_테스트() {
        // WebSocket 연결 및 메시지 전송 검증
    }
}
```

#### 3. 부하 테스트
```bash
# Apache JMeter or Gatling
# 시나리오: 동시 10,000명 접속
# 목표: 평균 응답시간 < 500ms
```

---

## 📱 Phase 7: 모바일 앱 (선택)

### 네이티브 앱 vs PWA

#### 현재: PWA (완료)
- ✅ 설치 가능
- ✅ 오프라인 지원
- ⚠️ 푸시 알림 제한 (iOS)

#### 옵션 1: Flutter (추천)
- 장점: 단일 코드베이스
- 단점: 학습 곡선

#### 옵션 2: React Native
- 장점: JavaScript 재사용
- 단점: 브릿지 오버헤드

---

## 💰 Phase 8: 비즈니스 모델 (선택)

### 수익화 전략

#### 1. 프리미엄 기능
- 클라우드 저장 공간 확장 (10GB → 100GB)
- 프로필 꾸미기 (테마, 배경)
- 그룹 채팅방 인원 확대 (100명 → 1000명)

#### 2. 광고
- 채팅방 리스트 하단 배너 (비침입적)

#### 3. 비즈니스 계정
- 기업용 메신저
- 관리자 대시보드
- 분석 리포트

---

## 📊 성능 목표

### 카카오톡 수준 벤치마크

| 항목 | 목표 | 현재 상태 |
|------|------|----------|
| 동시 접속자 | 10만 명 | 1,000명 (추정) |
| 메시지 지연 | < 100ms | ~50ms ✅ |
| 이미지 전송 | < 3초 | ❌ 미구현 |
| 서버 가용성 | 99.9% | ❌ 모니터링 없음 |
| DB 응답 | < 50ms | ~20ms ✅ |
| 메시지 처리량 | 10,000 msg/sec | ❌ 미측정 |

---

## 🗓 전체 일정표

```gantt
Phase 1: 보안 패치         ██ (1일) ✅
Phase 2: 핵심 기능         ████████████ (4주)
Phase 3: 확장성            ██████ (3주)
Phase 4: UX 개선           ████ (2주)
Phase 5: 법적 준수         ██ (1주)
Phase 6: 품질 보증         ████ (계속)
Phase 7: 모바일 앱         ████████ (선택)
Phase 8: 비즈니스          ████ (선택)
```

**총 소요 기간**: 10주 (핵심 기능)
**완전체**: 16주 (모바일 포함)

---

## 🎯 단계별 체크리스트

### MVP (Minimum Viable Product) - 6주
- [ ] 1:1 채팅
- [ ] 친구 시스템
- [ ] 읽음 표시
- [ ] 파일 전송
- [ ] 메시지 검색
- [ ] 프로필

### Production Ready - 10주
- [ ] Redis 세션
- [ ] 메시지 브로커
- [ ] 모니터링
- [ ] 법적 문서
- [ ] 테스트 80%+

### 카카오톡 수준 - 16주
- [ ] 모바일 앱
- [ ] 음성 통화 (WebRTC)
- [ ] 영상 통화
- [ ] 이모티콘 스토어
- [ ] 채팅봇 API

---

## 🚨 리스크 관리

### 높은 리스크

#### 1. 확장성 한계
**문제**: 인메모리 구조로 서버 1대만 가능
**해결**: Phase 3에서 Redis + RabbitMQ 필수

#### 2. DB 병목
**문제**: 메시지 증가 시 조회 속도 저하
**해결**: 샤딩, 읽기 전용 Replica

#### 3. 파일 저장 비용
**문제**: S3 비용 급증
**해결**: CDN, 이미지 압축, 오래된 파일 자동 삭제

---

## 💡 추천 개발 순서

### 1단계 (가장 중요)
1. ✅ 보안 패치 (완료)
2. **1:1 채팅** ← 지금 이거!
3. **친구 시스템** ← 이거도 필수!
4. **읽음 표시** ← 없으면 불편

### 2단계 (사용자 경험)
5. 파일 전송
6. 이미지 프리뷰
7. 프로필
8. 메시지 검색

### 3단계 (기술 부채 해결)
9. Redis 세션
10. 메시지 브로커
11. 모니터링
12. 테스트 코드

---

## 🎓 학습 자료

### Spring WebSocket
- [Spring WebSocket 공식 문서](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [STOMP Protocol](https://stomp.github.io/)

### 실시간 메시징 아키텍처
- [WhatsApp 아키텍처](https://www.highscalability.com/whatsapp-architecture/)
- [Discord 확장성](https://discord.com/blog/how-discord-stores-billions-of-messages)

### 보안
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [개인정보보호법 가이드라인](https://www.pipc.go.kr/)

---

## 📞 다음 액션

### 즉시 (오늘)
```bash
# 1. DB 비밀번호 변경
# Koyeb 콘솔에서 PostgreSQL 비밀번호 재설정

# 2. 환경변수 설정
export DATABASE_PASSWORD="새로운_안전한_비밀번호_여기"
export CORS_ALLOWED_ORIGINS="https://yourdomain.com"

# 3. 빌드 & 배포
mvn clean package
docker build -t securechat:v2 .
```

### 이번 주
- [ ] 1:1 채팅 설계 (ERD 작성)
- [ ] 친구 시스템 API 스펙 정의
- [ ] 프론트엔드 UI 목업

### 다음 주
- [ ] 1:1 채팅 백엔드 구현
- [ ] 친구 시스템 프론트엔드
- [ ] 통합 테스트

---

## 🎉 결론

현재 SecureChat은 **30%** 완성도입니다.
카카오톡을 대체하려면 **10주의 집중 개발**이 필요합니다.

### 핵심 메시지
1. ✅ 보안 문제는 해결됨
2. ❌ 1:1 채팅이 없어서 메신저로서 불완전
3. 🎯 10주 후 MVP 출시 가능
4. 📱 16주 후 모바일까지 완성

**지금 가장 중요한 것**: 1:1 채팅 + 친구 시스템

---

**작성일**: 2025-10-24
**다음 업데이트**: Phase 2 Week 1 완료 후
