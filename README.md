# ⚡ BEAM - 글로벌 보안 메신저

**Messages at the speed of light.**

안전하고 빠른 차세대 글로벌 메신저 플랫폼입니다.

## 🎯 BEAM이란?

BEAM은 프라이버시와 속도를 최우선으로 하는 메신저입니다.
빛처럼 빠르게 전달되는 안전한 대화를 경험하세요.

### 핵심 가치
- 🔒 **보안 우선**: E2E 암호화로 완벽한 프라이버시 보호
- ⚡ **초고속**: 실시간 메시지 전송, 지연 제로
- 🌍 **글로벌**: 전 세계 누구와도 소통
- 🎨 **직관적**: 배우지 않아도 사용할 수 있는 UI

## ✨ 주요 기능

### 💬 메시징
- **1:1 채팅**: 개인 메시지 전송 (DM)
- **그룹 채팅**: 최대 100명까지 그룹 대화
- **읽음 표시**: 메시지 전달/읽음 확인
- **메시지 검색**: 전체 대화 내역 검색
- **타이핑 인디케이터**: 상대방 입력 중 표시

### 👥 소셜 기능
- **친구 시스템**: 친구 요청/수락/거절
- **사용자 검색**: 전화번호/아이디 검색
- **온라인 상태**: 실시간 접속 상태 표시
- **차단 기능**: 원치 않는 사용자 차단

### 📁 파일 & 미디어
- **파일 전송**: 최대 10MB 파일 공유
- **이미지 업로드**: 자동 썸네일 생성 (200px)
- **다운로드 추적**: 다운로드 횟수 카운트
- **파일 분류**: IMAGE, VIDEO, AUDIO, DOCUMENT

### 🔐 보안
- **JWT 인증**: 안전한 토큰 기반 인증
- **BCrypt**: 비밀번호 암호화
- **전화번호 인증**: 본인 인증 프레임워크

## 🛠 기술 스택

### Backend
- **Java 17** + **Spring Boot 3.2**
- **WebSocket** (STOMP 프로토콜, SockJS 폴백)
- **Spring Data JPA** (ORM, Hibernate)
- **Spring Security** (JWT 인증)
- **PostgreSQL** (운영 DB) / **H2** (개발 DB)
- **Lombok** (코드 간결화)
- **Maven** (빌드 도구)

### 메시징 아키텍처
- **STOMP over WebSocket**: 실시간 양방향 통신
- **/topic**: 그룹 채팅 브로드캐스트
- **/queue**: 개인 메시지 (point-to-point)
- **SockJS**: WebSocket 미지원 브라우저 폴백

### 데이터베이스 설계
- **14개 엔티티**: User, DirectMessage, Conversation, Friend, Room, RoomMember, GroupMessage, FileMetadata, ReadReceipt 등
- **인덱스 최적화**: 조회 성능 향상
- **트랜잭션 관리**: @Transactional

### 인프라
- **Docker** (컨테이너화)
- **Koyeb/AWS** (클라우드 배포)
- **HikariCP** (커넥션 풀)

## 🚀 빠른 시작

### 로컬 개발 환경

```bash
# 저장소 클론
git clone https://github.com/araeLaver/simple-chat-server.git
cd simple-chat-server

# 환경변수 설정
cp .env.example .env
# .env 파일을 열어서 DB 자격증명 입력
nano .env

# Maven 빌드
mvn clean install

# 개발 모드 실행
export $(cat .env | xargs)  # Linux/Mac
mvn spring-boot:run

# 브라우저에서 접속
open http://localhost:8080
```

**⚠️ 중요**: `.env` 파일은 Git에 커밋하지 마세요! (`.gitignore`에 이미 포함됨)

### Docker 실행

```bash
# 이미지 빌드
docker build -t beam-server .

# 컨테이너 실행
docker run -p 8080:8080 \
  -e DATABASE_URL="jdbc:postgresql://your-host/db" \
  -e DATABASE_USERNAME="username" \
  -e DATABASE_PASSWORD="password" \
  -e JWT_SECRET="your-secret-key" \
  -e CORS_ALLOWED_ORIGINS="http://localhost:8080" \
  -e SPRING_PROFILES_ACTIVE=dev \
  beam-server
```

**💡 Tip**: `.env` 파일 사용 시: `docker run --env-file .env -p 8080:8080 beam-server`

## 📦 배포

### 프로덕션 환경 변수

**필수 환경 변수**:

```bash
# 데이터베이스
DATABASE_URL=jdbc:postgresql://host/db?currentSchema=chatapp_prod&sslmode=require
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_secure_password

# 보안
JWT_SECRET=your-256-bit-secret-key  # Generate: openssl rand -base64 64
CORS_ALLOWED_ORIGINS=https://beam.chat,https://www.beam.chat

# 서버
SPRING_PROFILES_ACTIVE=prod
PORT=8080
```

**보안 가이드**: [SECURITY.md](SECURITY.md) 참고

### Koyeb 배포

```bash
# GitHub 저장소 연동 후 자동 배포
# 또는 Docker 이미지 배포
docker push your-registry/beam-server:latest
```

## 🏗 아키텍처

```
┌─────────────────┐
│  Frontend (SPA) │
│  Vanilla JS     │
└────────┬────────┘
         │ WebSocket
         ↓
┌─────────────────┐
│  Spring Boot    │
│  WebSocket      │
│  REST API       │
└────────┬────────┘
         │ JPA
         ↓
┌─────────────────┐
│  PostgreSQL     │
│  (Messages/User)│
└─────────────────┘
```

### 보안 계층
- **전송 계층**: WSS (WebSocket Secure) / HTTPS
- **애플리케이션 계층**: E2E 암호화 (비밀 채팅)
- **데이터베이스**: SSL 연결, 암호화된 비밀번호

## 🎯 로드맵

### Phase 1: MVP ✅ (현재)
- [x] 실시간 채팅 (WebSocket)
- [x] 채팅방 생성/관리
- [x] 보안 모드 (일반/비밀/임시)
- [x] PWA 지원

### Phase 2: 핵심 기능 ✅ (완료)
- [x] JWT 기반 인증 시스템
- [x] 1:1 채팅 (DM)
- [x] 친구 시스템 (요청/수락/거절/차단)
- [x] 그룹 채팅 (방 생성/관리/권한)
- [x] 파일 전송 (이미지 썸네일 자동 생성)
- [x] 읽음 표시 (Read Receipts)
- [x] 메시지 검색
- [x] WebSocket 실시간 통신
- [x] 타이핑 인디케이터
- [x] 온라인 상태 실시간 업데이트

### Phase 3: 확장 (계획)
- [ ] 음성/영상 통화 (WebRTC)
- [ ] 크로스 플랫폼 앱 (React Native)
- [ ] 다국어 지원 (i18n)
- [ ] 봇 API
- [ ] 채널/브로드캐스트

### Phase 4: 글로벌화 (미래)
- [ ] 오픈소스 클라이언트
- [ ] 블록체인 투명성 감사
- [ ] 익명 아이디 지원
- [ ] 탈중앙화 아키텍처

## 🔧 개발 가이드

### 프로파일 설정
- `local`: H2 인메모리 DB, 빠른 개발
- `dev`: PostgreSQL, 개발 서버
- `prod`: PostgreSQL, 운영 최적화

### 코드 스타일
- **언어**: Java 17+
- **포맷**: Google Java Style Guide
- **컨벤션**: RESTful API, Semantic Commit

### 기여하기
1. Fork this repository
2. Create your feature branch (`git checkout -b feature/amazing`)
3. Commit your changes (`git commit -m 'feat: Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing`)
5. Open a Pull Request

## 📊 성능 최적화

### 백엔드
- HikariCP Connection Pool
- JPA 배치 처리 최적화
- G1GC로 낮은 지연시간
- Redis 캐싱 (예정)

### 프론트엔드
- 번들 최소화 (Vanilla JS)
- Service Worker 캐싱
- Lazy Loading
- CSS Variables로 테마 전환 최적화

## 📄 라이선스

MIT License - 자유롭게 사용하고 수정하세요

## 💬 커뮤니티

- **Website**: https://beam.chat (예정)
- **Twitter**: @beamchat (예정)
- **Discord**: discord.gg/beam (예정)

---

**⚡ BEAM - Messages at the speed of light.**
