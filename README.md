# 💕 Couple App Project

커플을 위한 공유 앱 — 일정 관리, 앨범, 지도, D-Day 등을 함께 사용할 수 있는 웹 애플리케이션입니다.

---

## 포트 정보

| 서버 | 포트 |
|------|------|
| 프론트엔드 (Vue.js + Vite) | http://localhost:5173 |
| 백엔드 (Spring Boot) | http://localhost:8080 |

---

## 실행 방법

### 프론트엔드
```bash
cd frontend
npm install   # 최초 1회만
npm run dev
```

### 백엔드
```bash
./mvnw spring-boot:run
```

---

## 주요 기능

### 🏠 홈 화면
- D-Day 위젯 (함께한 날 카운트)
- 오늘의 한줄 (본인 + 상대방)
- **오늘의 일정** 위젯 — 오늘 등록된 일정 카드 표시
- **미니 달력** — 월 이동 가능, 일정 있는 날 이벤트 바 표시
- 슬라이드 사이드 메뉴 (일정 등록 / 앨범 / 맛집 찾기 / 편지 쓰기 / 기념일 등록)

### 📅 달력
- 월간 캘린더 (네이버 달력 스타일)
  - 날짜 좌상단 배치, 일정 이벤트 바, 초과 일정 `+N개 더` 뱃지
  - 일/토 컬러 구분, 오늘 날짜 강조
- 좌측 이번 달 일정 리스트 패널 (드래그로 너비 조절, 최대 40%)
- 날짜 클릭 → 일정 등록 화면으로 이동

### 📋 일정 등록 / 상세
- 일정 등록: 날짜·제목·시간·누구랑(함께/나/상대방)·메모
- 일정 상세: 조회 → 수정 / 삭제 (홈·달력 양쪽에서 접근 가능)

### 📷 앨범
- 사진 그리드 (6열 3행 기준)
- **연/월 피커** — 연도 네비게이션 + 월 선택, 사진 있는 달 강조
- **전체보기** 모드 — 월 헤더와 함께 전체 사진 표시
- 필터 바는 스크롤과 독립된 고정 영역
- 사진 클릭 시 뷰어 (확대 보기 + 삭제)
- 사진 업로드 (다중 선택 지원)

### 🗺️ 맛집 찾기
- (개발 예정)

---

## 프로젝트 구조

```
couple-app-project/
├── src/                        # Spring Boot 백엔드
│   └── main/
│       ├── java/me/survivalking/coupleappproject/
│       │   ├── CoupleAppProjectApplication.java
│       │   ├── HomeController.java
│       │   ├── config/
│       │   │   └── CorsConfig.java
│       │   └── controller/
│       │       └── TestController.java
│       └── resources/
│           └── application.properties
│
├── frontend/                   # Vue.js 프론트엔드
│   └── src/
│       ├── App.vue
│       ├── main.js
│       ├── router/
│       │   └── index.js
│       └── views/
│           ├── HomeView.vue            # 홈 화면 (D-Day, 오늘의 일정, 미니 달력)
│           ├── CalendarView.vue        # 월간 달력 + 일정 리스트 패널
│           ├── ScheduleRegisterView.vue# 일정 등록
│           ├── ScheduleDetailView.vue  # 일정 상세/수정/삭제
│           ├── PhotosView.vue          # 앨범 (월 필터 + 그리드)
│           └── MapView.vue             # 맛집 찾기 (예정)
│
├── pom.xml
└── COMMANDS.md
```

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| 프론트엔드 | Vue.js 3 (Composition API), Vue Router 4, Vite |
| 백엔드 | Spring Boot, Spring MVC |
| 데이터 저장 | localStorage (클라이언트 임시 저장) |
| 스타일 | CSS (Scoped), Flexbox, CSS Grid |
