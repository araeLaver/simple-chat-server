# 🔐 SecureChat - 실시간 보안 채팅 서버

안전하고 직관적인 실시간 채팅 애플리케이션입니다.

## ✨ 주요 기능

### 🏠 사용자 맞춤 채팅방
- **방 생성**: 일반방, 비밀방, 임시방 생성 가능
- **방장 권한**: 👑 방장 표시 및 비밀방 비밀번호 상시 확인
- **보안 모드별 분리**: 탭별로 다른 보안 레벨의 방들만 표시

### 🔒 강력한 보안 시스템
- **비밀방**: 비밀번호로 보호되는 암호화된 채팅
- **임시방**: 메시지가 자동으로 삭제되는 휘발성 채팅
- **일반방**: 표준 채팅 환경

### 🎨 사용자 경험
- **반응형 디자인**: 모바일/데스크톱 최적화
- **다크모드**: 테마 전환 지원
- **PWA 지원**: 앱처럼 설치 가능
- **실시간 알림**: 새 메시지 데스크톱 알림

### 👥 사용자 시스템
- **게스트 모드**: 회원가입 없이 즉시 채팅 시작
- **회원 시스템**: 계정 생성으로 추가 기능 이용

## 🛠 기술 스택

### Backend
- **Java 17** + **Spring Boot 3.2**
- **WebSocket** (실시간 통신)
- **Spring Data JPA** (데이터 액세스)
- **PostgreSQL** (운영 DB) / **H2** (개발/테스트)
- **암호화**: AES-256 (비밀방 메시지)

### Frontend
- **Vanilla JavaScript** (경량화)
- **Modern CSS** (Grid, Flexbox, CSS Variables)
- **Service Worker** (PWA)
- **WebSocket API** (실시간 통신)

### 배포 & 인프라
- **Docker** (컨테이너화)
- **Koyeb** (클라우드 플랫폼)
- **GitHub Actions** (CI/CD)

## 🚀 배포 가이드

### Koyeb 배포 설정

1. **환경 변수 설정**:
```
DATABASE_URL=jdbc:postgresql://your-db-host/koyebdb?currentSchema=chatapp_prod&sslmode=require
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
CORS_ALLOWED_ORIGINS=https://your-domain.com
SPRING_PROFILES_ACTIVE=prod
```

2. **배포 명령어**:
```bash
# Docker 이미지 빌드 및 푸시
docker build -t secure-chat .
docker push your-registry/secure-chat

# 또는 Koyeb GitHub 연동으로 자동 배포
```

### 로컬 개발 환경

```bash
# 프로젝트 클론
git clone https://github.com/your-username/simple-chat-server.git
cd simple-chat-server

# Maven 빌드
mvn clean install

# 로컬 실행 (H2 DB 사용)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 개발 환경 실행 (PostgreSQL 사용)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📦 Docker 빌드

```bash
# 멀티스테이지 빌드로 최적화된 이미지 생성
docker build -t secure-chat .

# 컨테이너 실행
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=your-db-url \
  -e DATABASE_USERNAME=your-username \
  -e DATABASE_PASSWORD=your-password \
  secure-chat
```

## 🏗 아키텍처

```
Frontend (Vanilla JS + CSS)
    ↕ WebSocket
Backend (Spring Boot)
    ↕ JPA
Database (PostgreSQL / H2)
```

### 보안 계층
- **전송 계층**: WSS (WebSocket Secure)
- **애플리케이션 계층**: 메시지별 암호화 (비밀방)
- **데이터베이스**: SSL 연결, 암호화된 비밀번호

## 📱 PWA 기능

- **오프라인 지원**: Service Worker 캐싱
- **설치 가능**: 브라우저에서 앱으로 설치
- **푸시 알림**: 새 메시지 알림 (예정)
- **반응형**: 모든 디바이스에서 최적화

## 🔧 개발 도구

### 프로파일 설정
- `local`: H2 메모리 DB, 개발용 설정
- `dev`: PostgreSQL, 개발 서버용
- `prod`: PostgreSQL, 운영 서버용 최적화

### 데이터베이스 마이그레이션
```sql
-- 운영 DB 스키마 생성
CREATE SCHEMA IF NOT EXISTS chatapp_prod;
```

## 🌟 주요 특징

### 실시간 통신
- WebSocket 기반 즉시 메시지 전송
- 연결 끊김 시 자동 재연결
- 사용자 온라인 상태 실시간 표시

### 메시지 보안
- **일반**: 표준 채팅
- **비밀**: AES-256 암호화 + 비밀번호 보호
- **임시**: 시간 기반 자동 삭제 (10초~5분)

### 사용자 인터페이스
- 직관적인 탭 기반 네비게이션
- 방 생성 모달로 쉬운 설정
- 방장 전용 기능 (비밀번호 표시 등)

## 📈 성능 최적화

### 백엔드
- Connection Pool 최적화
- JPA 배치 처리
- G1GC 사용으로 낮은 지연시간

### 프론트엔드
- 바닐라 JS로 번들 크기 최소화
- CSS Variables로 테마 전환 최적화
- Service Worker 캐싱

## 🤝 기여 가이드

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 라이선스

MIT License - 자유롭게 사용하세요!

## 🙋‍♂️ 지원

문제가 있거나 질문이 있으시면 [Issues](https://github.com/your-username/simple-chat-server/issues)에 등록해 주세요.

---

**🔐 SecureChat** - 안전하고 사용하기 쉬운 실시간 채팅의 새로운 기준