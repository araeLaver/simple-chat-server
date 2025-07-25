# 🚀 Simple Chat Server

현대적인 실시간 채팅 플랫폼 - 카카오톡을 능가하는 기능들을 제공합니다.

## ✨ 주요 기능

### 🎯 핵심 기능
- **실시간 채팅** - WebSocket 기반 즉시 메시징
- **다중 채팅방** - 일반, 개발, 자유토론 방
- **사용자 관리** - 회원가입, 로그인, 사용자 목록
- **파일 공유** - 드래그&드롭 파일 업로드 (10MB)
- **메시지 기록** - 영구 저장 및 검색 가능
- **크로스 플랫폼** - 웹, 모바일 모두 지원

### 🛡️ 보안 기능
- 사용자 인증 시스템
- 파일 업로드 보안 검증
- SQL Injection 방지
- CORS 설정

## 🏗️ 기술 스택

### Backend
- **Java 17** + **Spring Boot 3.2**
- **WebSocket** - 실시간 통신
- **JPA/Hibernate** - ORM
- **PostgreSQL** (Production) / **H2** (Development)

### Frontend
- **HTML5** + **CSS3** + **Vanilla JavaScript**
- **Responsive Design** - 모바일 최적화
- **Progressive Web App** 지원

### 배포 환경
- **Koyeb** - 클라우드 배포
- **PostgreSQL** - 프로덕션 데이터베이스
- **GitHub Actions** - CI/CD

## 🚀 빠른 시작

### 개발 환경 실행
```bash
# 저장소 클론
git clone <repository-url>
cd simple-chat-server

# 개발 환경으로 실행 (H2 Database)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 프로덕션 배포
```bash
# 프로덕션 환경으로 실행 (PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 🌐 API 엔드포인트

### 인증 API
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인

### 파일 API
- `POST /api/files/upload` - 파일 업로드
- `GET /api/files/download/{filename}` - 파일 다운로드

### WebSocket
- `ws://localhost:8080/chat` - 실시간 채팅

## 📱 사용 방법

1. **http://localhost:8080** 접속
2. **회원가입** 또는 **로그인**
3. **채팅 시작** 버튼 클릭
4. **채팅방 선택** 후 대화 시작
5. **파일 드래그&드롭**으로 파일 공유

## 🛠️ 개발 가이드

### 프로필 설정
- `dev` - 개발 환경 (H2 Database)
- `prod` - 프로덕션 환경 (PostgreSQL)

### 데이터베이스 관리
- **H2 Console**: http://localhost:8080/h2-console
- **PostgreSQL**: Koyeb 대시보드에서 관리

### 환경 변수
```bash
# 개발
export SPRING_PROFILES_ACTIVE=dev

# 프로덕션
export SPRING_PROFILES_ACTIVE=prod
export PORT=8080
```

## 🔧 설정

### application.properties
```properties
# 기본 프로필
spring.profiles.active=dev

# 서버 설정
server.port=${PORT:8080}

# 파일 업로드
spring.servlet.multipart.max-file-size=10MB
```

## 📈 확장 계획

- [ ] 개인 메시지 (DM)
- [ ] 이모티콘 시스템
- [ ] 알림 시스템
- [ ] 모바일 앱 (React Native)
- [ ] 관리자 대시보드
- [ ] 메시지 검색
- [ ] 파일 미리보기
- [ ] 음성/영상 통화

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 라이선스

MIT License - 자유롭게 사용 가능합니다.

---

Made with ❤️ by [Your Name]