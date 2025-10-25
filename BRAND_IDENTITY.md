# 🎨 HANA 브랜드 아이덴티티

## 브랜드 개요

**HANA (하나)**는 카카오톡을 대체할 가장 기본적이고 간단한 메신저를 목표로 하는 브랜드입니다.

### 브랜드 철학
- **하나로 연결하다** - 사람과 사람을 하나로 연결
- **기본에 충실** - 불필요한 기능 제거, 핵심 기능에 집중
- **간단함** - 누구나 쉽게 사용할 수 있는 직관적 디자인
- **안전함** - 보안과 프라이버시 우선

---

## 브랜드 네이밍

### HANA (하나)
- **의미**: "하나" - 하나됨, 연결, 통일
- **발음**: 한국어로 쉽게 발음 가능 (2음절)
- **국제성**: 영어권에서도 발음 용이 (Hana)
- **차별성**: 경쟁사와 명확히 구분되는 고유한 이름

### 태그라인
**"하나로 연결하다"** - Connect as One

---

## 브랜드 컬러

### Primary Colors (주 색상)

#### Purple Gradient (보라 그라데이션)
```css
--primary: #7C3AED;        /* 진한 보라 */
--primary-light: #C084FC;  /* 연한 보라 */
--primary-dark: #6D28D9;   /* 더 진한 보라 */
```

**선택 이유:**
- ✅ **차별화**: KakaoTalk(노랑), Line(초록), Telegram(파랑)과 명확히 구분
- ✅ **프리미엄**: 고급스럽고 신뢰감 있는 이미지
- ✅ **보안 연상**: 보라색은 보안, 프라이버시와 연관
- ✅ **현대적**: 트렌디하고 젊은 감각
- ✅ **시인성**: 화면에서 돋보이는 색상

### Secondary Colors (보조 색상)

```css
--secondary: #4F46E5;      /* 인디고 */
--accent: #EC4899;         /* 핑크 (강조) */
```

### System Colors (시스템 색상)

```css
--success: #52c41a;   /* 성공 - 초록 */
--warning: #faad14;   /* 경고 - 주황 */
--error: #ff4d4f;     /* 오류 - 빨강 */
--info: #1890ff;      /* 정보 - 파랑 */
```

---

## 로고 디자인

### 컨셉
HANA 로고는 **두 사람이 연결되는 모습**을 형상화합니다.

### 구성 요소

```
┌────┐  ◯  ┌────┐
│    │  │  │    │
│    │──┼──│    │
│    │  │  │    │
└────┘  ◯  └────┘
```

1. **왼쪽 바**: 첫 번째 사람
2. **오른쪽 바**: 두 번째 사람
3. **중앙 원**: 연결점 (메시지)

### 로고 특징
- **심플함**: 최소한의 요소로 구성
- **의미**: H 형태 + 사람 연결 + 메시지 전달
- **확장성**: 다양한 크기에서 선명하게 표현
- **기억성**: 한 번 보면 기억에 남는 디자인

### 색상 적용
```svg
<defs>
  <linearGradient id="hanaBrand" x1="0%" y1="0%" x2="100%" y2="100%">
    <stop offset="0%" style="stop-color:#7C3AED"/>
    <stop offset="100%" style="stop-color:#C084FC"/>
  </linearGradient>
</defs>
```

---

## 타이포그래피

### 브랜드명 표기

```css
.brand-name {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #7C3AED, #C084FC);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
```

### 글꼴 체계
- **헤딩**: 800 (Extra Bold) - 강력한 브랜드 인상
- **본문**: 400 (Regular) - 읽기 편한 본문
- **강조**: 600 (Semi Bold) - 중요 정보 강조

### 폰트 스택
```css
font-family: -apple-system, BlinkMacSystemFont,
             'Segoe UI', 'Roboto',
             'Helvetica Neue', Arial, sans-serif;
```

---

## 디자인 시스템

### Glass Morphism (글래스모피즘)
HANA의 시그니처 스타일

```css
background: rgba(255, 255, 255, 0.8);
backdrop-filter: blur(20px);
-webkit-backdrop-filter: blur(20px);
border: 1px solid rgba(124, 58, 237, 0.2);
```

### 둥근 모서리 (Border Radius)
```css
--radius-sm: 6px;
--radius-base: 8px;
--radius-lg: 12px;
--radius-xl: 16px;
--radius-2xl: 24px;
```

### 그림자 (Shadows)
```css
--shadow-1: 0 2px 8px rgba(0, 0, 0, 0.06);
--shadow-2: 0 4px 12px rgba(0, 0, 0, 0.08);
--shadow-3: 0 8px 24px rgba(0, 0, 0, 0.12);
```

### 애니메이션
```css
/* 로고 플로팅 */
@keyframes brandLogoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

/* 브랜드 펄스 */
@keyframes brandPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}
```

---

## 경쟁사 비교

| 메신저 | 브랜드 컬러 | 특징 | HANA 차별점 |
|--------|------------|------|------------|
| **KakaoTalk** | 노란색 (#FAE100) | 한국 1위 | ✅ 보라색으로 차별화 |
| **Line** | 초록색 (#00B900) | 일본/태국 강세 | ✅ 더 모던한 디자인 |
| **Telegram** | 파란색 (#0088CC) | 보안 강조 | ✅ 더 간단한 UX |
| **WhatsApp** | 초록색 (#25D366) | 글로벌 1위 | ✅ 기본에 충실 |
| **Signal** | 파란색 (#3A76F0) | 프라이버시 | ✅ 더 친근한 브랜드 |

**HANA의 포지셔닝**: 보안 + 간단함 + 한국적 감성

---

## 브랜드 적용 가이드

### 1. 웹/앱
- ✅ 모든 페이지 상단에 HANA 로고 표시
- ✅ 주요 버튼에 브랜드 그라데이션 적용
- ✅ 로딩 화면에 브랜드 로고 애니메이션

### 2. 메타 태그
```html
<title>HANA - 하나로 연결하다</title>
<meta name="theme-color" content="#7C3AED">
<meta name="apple-mobile-web-app-title" content="HANA">
```

### 3. PWA 매니페스트
```json
{
  "name": "HANA - 하나로 연결하다",
  "short_name": "HANA",
  "theme_color": "#7C3AED"
}
```

### 4. 금지 사항
- ❌ 브랜드 컬러 임의 변경 금지
- ❌ 로고 비율 왜곡 금지
- ❌ 태그라인 수정 금지
- ❌ 타 브랜드와 혼동될 수 있는 디자인 금지

---

## 브랜드 진화 로드맵

### Phase 1: 기본 브랜딩 (완료) ✅
- [x] 브랜드명 확정: HANA
- [x] 컬러 시스템 구축
- [x] 로고 디자인
- [x] 웹 UI 적용

### Phase 2: 확장 (예정)
- [ ] 브랜드 사운드 (알림음)
- [ ] 브랜드 모션 (스플래시 애니메이션)
- [ ] 브랜드 일러스트레이션
- [ ] 이모티콘 세트

### Phase 3: 마케팅 (예정)
- [ ] 홍보 영상
- [ ] 소셜 미디어 템플릿
- [ ] 브랜드 스토리텔링
- [ ] 커뮤니티 빌딩

---

## 브랜드 가치 (Brand Values)

### 🎯 간단함 (Simplicity)
"복잡하지 않게, 필요한 것만"

### 🔒 안전함 (Security)
"당신의 대화는 당신만의 것"

### 💜 연결 (Connection)
"하나로 연결하다"

### ⚡ 빠름 (Speed)
"생각보다 빠른 메신저"

---

## 연락처

브랜드 관련 문의: [프로젝트 GitHub Issues](https://github.com/araeLaver/simple-chat-server/issues)

---

**© 2025 HANA - 하나로 연결하다**

*Generated with Claude Code - 2025-10-25*
