<template>
  <div class="app-wrapper" :class="{ 'panel-open': isPanelOpen }">

    <!-- 좌측 슬라이드 패널 -->
    <div class="side-panel">
      <div class="panel-header">
        <button class="close-btn" @click="closePanel">✕</button>
      </div>
      <nav class="panel-menu">
        <RouterLink to="/photos" class="panel-item" @click="closePanel">
          <span class="panel-icon">📷</span>
          <span>사진 등록</span>
        </RouterLink>
        <RouterLink to="/calendar" class="panel-item" @click="closePanel">
          <span class="panel-icon">📅</span>
          <span>일정 등록</span>
        </RouterLink>
        <RouterLink to="/map" class="panel-item" @click="closePanel">
          <span class="panel-icon">🍽️</span>
          <span>맛집 찾기</span>
        </RouterLink>
        <a href="#" class="panel-item">
          <span class="panel-icon">💌</span>
          <span>편지 쓰기</span>
        </a>
        <a href="#" class="panel-item">
          <span class="panel-icon">🎁</span>
          <span>기념일 등록</span>
        </a>
      </nav>
    </div>

    <!-- 메인 콘텐츠 -->
    <div class="main-content">

      <!-- 상단 헤더 -->
      <header class="top-bar">
        <button class="menu-btn" @click="openPanel">≡</button>
        <span class="app-title">💕 우리의 공간</span>
        <div class="spacer"></div>
      </header>

      <!-- 콘텐츠 영역 -->
      <div class="content">

        <!-- D-Day 위젯 -->
        <div class="widget dday-widget">
          <div class="dday-heart">💕</div>
          <div class="dday-info">
            <div class="dday-count">D+{{ dday }}</div>
            <div class="dday-label">우리가 함께한 날</div>
            <div class="dday-date">{{ startDate }} 부터</div>
          </div>
        </div>

        <!-- 오늘의 일정 -->
        <div class="widget schedule-widget">
          <div class="widget-title">📅 오늘의 일정</div>
          <ul class="schedule-list">
            <li v-for="item in schedules" :key="item.id" class="schedule-item">
              <span class="schedule-time">{{ item.time }}</span>
              <span class="schedule-text">{{ item.text }}</span>
              <span class="schedule-who">{{ item.who }}</span>
            </li>
            <li v-if="schedules.length === 0" class="schedule-empty">오늘 일정이 없어요 😊</li>
          </ul>
        </div>

        <!-- 오늘의 한줄 -->
        <div class="widget oneliner-widget">
          <div class="widget-title">✏️ 오늘의 한줄</div>
          <div class="oneliner-content">
            <div class="oneliner-item">
              <span class="ol-avatar">👦</span>
              <span class="ol-text">{{ myOneLiner || '오늘의 한줄을 남겨봐요' }}</span>
            </div>
            <div class="oneliner-item">
              <span class="ol-avatar">👧</span>
              <span class="ol-text">{{ partnerOneLiner || '아직 한줄이 없어요' }}</span>
            </div>
          </div>
          <input
            v-model="myOneLinerInput"
            class="oneliner-input"
            placeholder="오늘 하루 어땠어? ✨"
            @keyup.enter="saveOneLiner"
          />
        </div>

      </div>
    </div>

    <!-- 패널 열렸을 때 배경 오버레이 (선택 클릭으로도 닫기) -->
    <div class="overlay" v-if="isPanelOpen" @click="closePanel"></div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const isPanelOpen = ref(false)

const openPanel = () => { isPanelOpen.value = true }
const closePanel = () => { isPanelOpen.value = false }

// D-Day 계산
const startDate = '2024-01-01'
const dday = computed(() => {
  const start = new Date(startDate)
  const today = new Date()
  const diff = Math.floor((today - start) / (1000 * 60 * 60 * 24))
  return diff
})

// 일정 (추후 API 연동)
const schedules = ref([
  { id: 1, time: '18:00', text: '저녁 같이 먹기', who: '💕' },
  { id: 2, time: '21:00', text: '영화 보기', who: '👦' },
])

// 오늘의 한줄
const myOneLiner = ref('')
const partnerOneLiner = ref('오늘도 사랑해 💕')
const myOneLinerInput = ref('')

const saveOneLiner = () => {
  if (myOneLinerInput.value.trim()) {
    myOneLiner.value = myOneLinerInput.value.trim()
    myOneLinerInput.value = ''
  }
}
</script>

<style scoped>
/* ── 전체 래퍼 ── */
.app-wrapper {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
  position: relative;
  overflow-x: hidden;
}

/* ── 사이드 패널 ── */
.side-panel {
  position: fixed;
  top: 0;
  left: 0;
  width: 20%;
  min-width: 140px;
  height: 100vh;
  background: white;
  box-shadow: 4px 0 20px rgba(233, 30, 99, 0.15);
  transform: translateX(-100%);
  transition: transform 0.3s ease;
  z-index: 100;
  display: flex;
  flex-direction: column;
}

.app-wrapper.panel-open .side-panel {
  transform: translateX(0);
}

.panel-header {
  display: flex;
  justify-content: flex-end;
  padding: 16px;
  border-bottom: 1px solid #f8bbd0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #e91e63;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}
.close-btn:hover {
  background: #fff0f5;
}

.panel-menu {
  display: flex;
  flex-direction: column;
  padding: 12px 0;
  gap: 4px;
}

.panel-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  text-decoration: none;
  color: #444;
  font-size: 0.9rem;
  font-weight: 500;
  transition: background 0.2s, color 0.2s;
  border-radius: 0;
}
.panel-item:hover {
  background: #fff0f5;
  color: #e91e63;
}

.panel-icon {
  font-size: 1.3rem;
}

/* ── 메인 콘텐츠 ── */
.main-content {
  flex: 1;
  transition: margin-left 0.3s ease;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-wrapper.panel-open .main-content {
  margin-left: max(20%, 140px);
}

/* ── 상단 바 ── */
.top-bar {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  gap: 16px;
}

.menu-btn {
  background: white;
  border: none;
  font-size: 1.5rem;
  color: #e91e63;
  cursor: pointer;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(233, 30, 99, 0.2);
  transition: transform 0.2s;
}
.menu-btn:hover {
  transform: scale(1.05);
}

.app-title {
  color: white;
  font-size: 1.2rem;
  font-weight: bold;
  text-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.spacer { flex: 1; }

/* ── 콘텐츠 영역 ── */
.content {
  padding: 0 20px 40px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ── 공통 위젯 ── */
.widget {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(255, 100, 130, 0.15);
}

/* ── D-Day ── */
.dday-widget {
  display: flex;
  align-items: center;
  gap: 20px;
}

.dday-heart {
  font-size: 3rem;
}

.dday-count {
  font-size: 2rem;
  font-weight: bold;
  color: #e91e63;
}

.dday-label {
  font-size: 0.9rem;
  color: #888;
}

.dday-date {
  font-size: 0.8rem;
  color: #bbb;
  margin-top: 4px;
}

/* ── 일정 위젯 ── */
.widget-title {
  font-size: 1rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.schedule-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.schedule-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: #fff5f8;
  border-radius: 12px;
  border-left: 3px solid #e91e63;
}

.schedule-time {
  font-size: 0.85rem;
  color: #e91e63;
  font-weight: bold;
  min-width: 45px;
}

.schedule-text {
  flex: 1;
  font-size: 0.9rem;
  color: #333;
}

.schedule-who {
  font-size: 1rem;
}

.schedule-empty {
  text-align: center;
  color: #bbb;
  font-size: 0.9rem;
  padding: 12px;
}

/* ── 오늘의 한줄 ── */
.oneliner-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 14px;
}

.oneliner-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.ol-avatar {
  font-size: 1.3rem;
}

.ol-text {
  font-size: 0.9rem;
  color: #555;
  background: #fff5f8;
  padding: 8px 12px;
  border-radius: 12px;
  flex: 1;
  line-height: 1.5;
}

.oneliner-input {
  width: 100%;
  border: 1.5px solid #f8bbd0;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 0.9rem;
  color: #333;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.2s;
}
.oneliner-input:focus {
  border-color: #e91e63;
}

/* ── 오버레이 ── */
.overlay {
  position: fixed;
  inset: 0;
  background: transparent;
  z-index: 50;
}
</style>
