# Couple App Project

## 포트 정보

| 서버 | 포트 |
|------|------|
| 프론트엔드 (Vue.js + Vite) | http://localhost:5173 |
| 백엔드 (Spring Boot) | http://localhost:8080 |

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

## 프로젝트 구조

```
couple-app-project/
├── src/                        # Spring Boot 백엔드
│   └── main/
│       ├── java/me/survivalking/coupleappproject/
│       │   ├── CoupleAppProjectApplication.java   # 메인 진입점
│       │   ├── HomeController.java
│       │   ├── config/
│       │   │   └── CorsConfig.java                # CORS 설정
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
│       ├── views/
│       │   └── HomeView.vue
│       └── components/
│
├── pom.xml                     # Maven 의존성
└── COMMANDS.md                 # 실행 명령어 모음
```

## 기술 스택

- **프론트엔드**: Vue.js 3, Vue Router, Vite
- **백엔드**: Spring Boot 4, Spring MVC, Thymeleaf
