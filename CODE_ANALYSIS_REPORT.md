# 🔍 코드 정밀 분석 보고서 - 개선 필요 사항

## 🚨 치명적 문제 (Critical)

### 1. **인증/인가 부재** - 🔴 심각
**파일**: `FriendController.java`

```java
// ❌ 현재: 클라이언트가 userId를 직접 전송
@GetMapping
public ResponseEntity<?> getFriendList(@RequestParam Long userId) {
    // 누구든 아무 userId로 요청 가능!
}
```

**문제**:
- 사용자 A가 userId=123을 보내면 사용자 B의 친구 목록 조회 가능
- 인증 토큰 없음
- 권한 검증 없음

**영향**: 전체 사용자 데이터 유출 가능

---

### 2. **CORS 전체 허용** - 🔴 심각
**파일**: `FriendController.java:9`

```java
@CrossOrigin(origins = "*")  // ❌ 모든 도메인 허용
```

**문제**: CSRF 공격 취약

---

### 3. **중복 요청 체크 불완전** - 🟠 중간
**파일**: `FriendService.java:42-44`

```java
// ❌ A→B 요청만 확인, B→A 요청은 확인 안함
if (friendRequestRepository.existsByFromUserIdAndToUserIdAndStatus(
    fromUserId, toUserId, FriendStatus.PENDING)) {
    throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
}
```

**시나리오**:
1. User A → User B 친구 요청 (성공)
2. User B → User A 친구 요청 (성공!) ← 버그
3. 중복 친구 요청 2개 존재

---

### 4. **사용자 존재 여부 미확인** - 🟠 중간
**파일**: `FriendService.java:30-50`

```java
public FriendRequestEntity sendFriendRequest(Long fromUserId, Long toUserId, String message) {
    // ❌ fromUserId, toUserId가 실제 존재하는지 확인 안함
    // 존재하지 않는 사용자에게 요청 가능
}
```

---

### 5. **데이터 무결성 부족** - 🟠 중간
**파일**: `FriendEntity.java`, `FriendRequestEntity.java`

```java
@Entity
@Table(name = "friends")  // ❌ UNIQUE 제약 조건 없음
public class FriendEntity {
    private Long userId;
    private Long friendUserId;
    // 같은 친구 관계 여러 번 저장 가능!
}
```

**문제**:
- (user_id=1, friend_user_id=2) 중복 저장 가능
- 외래키 제약 조건 없음

---

### 6. **N+1 쿼리 문제** - 🟡 성능
**파일**: `FriendController.java:108-123`

```java
List<FriendEntity> friends = friendService.getFriendList(userId);

// ❌ 각 친구마다 userRepository.findById() 호출
friends.stream().map(friend -> {
    userRepository.findById(friend.getFriendUserId()).ifPresent(user -> {
        // N+1 문제 발생!
    });
});
```

**영향**: 친구 100명 → 101개 쿼리

---

### 7. **트랜잭션 격리 레벨 미지정** - 🟡 동시성
**파일**: `FriendService.java:15`

```java
@Transactional  // ❌ 격리 레벨 없음
public class FriendService {
    // 동시에 두 명이 친구 요청 → 중복 가능
}
```

---

### 8. **입력 검증 부재** - 🟡 보안
**파일**: `FriendController.java`

```java
@PostMapping("/requests")
public ResponseEntity<?> sendFriendRequest(@RequestBody Map<String, Object> request) {
    // ❌ 입력 검증 없음
    Long fromUserId = Long.parseLong(request.get("fromUserId").toString());
    // NullPointerException 가능!
}
```

---

### 9. **WebSocket 사용자 ID 생성 취약** - 🔴 심각
**파일**: `ChatWebSocketHandler.java:500`

```java
// ❌ 매우 위험!
Long myUserId = (long) myUsername.hashCode();
Long friendUserId = (long) friendUsername.hashCode();

// 문제:
// - "admin".hashCode() = 92668751 (예측 가능)
// - 충돌 가능: "Aa".hashCode() == "BB".hashCode()
```

---

### 10. **페이징 없음** - 🟡 성능
**파일**: `FriendController.java:99`

```java
List<FriendEntity> friends = friendService.getFriendList(userId);
// ❌ 친구 10,000명이면 10,000개 다 조회
```

---

## 📊 문제 우선순위

| 순위 | 문제 | 위험도 | 영향 |
|------|------|--------|------|
| 1 | 인증/인가 부재 | 🔴 Critical | 데이터 유출 |
| 2 | WebSocket ID 생성 | 🔴 Critical | 위조 가능 |
| 3 | CORS 전체 허용 | 🔴 Critical | CSRF 공격 |
| 4 | 데이터 무결성 | 🟠 High | 중복 데이터 |
| 5 | 사용자 검증 미흡 | 🟠 High | 잘못된 데이터 |
| 6 | 중복 요청 체크 | 🟠 High | 버그 |
| 7 | N+1 쿼리 | 🟡 Medium | 성능 저하 |
| 8 | 트랜잭션 격리 | 🟡 Medium | 동시성 문제 |
| 9 | 입력 검증 | 🟡 Medium | 예외 발생 |
| 10 | 페이징 부재 | 🟡 Medium | 대용량 느림 |

---

## 🔧 개선 방안 요약

### 즉시 조치 (Critical)
1. ✅ JWT 인증 추가
2. ✅ CORS 제한
3. ✅ WebSocket 인증 개선
4. ✅ 데이터 무결성 제약 조건

### 단기 조치 (1-2일)
5. ✅ N+1 쿼리 해결 (JOIN FETCH)
6. ✅ 입력 검증 (Bean Validation)
7. ✅ 중복 요청 체크 개선
8. ✅ 사용자 존재 검증

### 중기 조치 (1주)
9. ✅ 페이징 구현
10. ✅ 트랜잭션 격리 레벨
11. ✅ 에러 응답 표준화
12. ✅ 로깅 강화

---

## 📝 다음 단계

1. **JWT 인증 시스템 구현**
2. **데이터베이스 제약 조건 추가**
3. **입력 검증 DTO 생성**
4. **N+1 쿼리 최적화**
5. **통합 테스트 작성**

---

작성일: 2025-10-24
분석자: Claude Code
