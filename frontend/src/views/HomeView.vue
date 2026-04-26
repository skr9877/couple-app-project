<template>
  <div class="app-wrapper" :class="{ 'panel-open': isPanelOpen }">

    <!-- 좌측 슬라이드 패널 -->
    <div class="side-panel">
      <div class="panel-header">
        <button class="close-btn" @click="closePanel">✕</button>
      </div>
      <nav class="panel-menu">
        <RouterLink to="/calendar" class="panel-item" @click="closePanel">
          <span class="panel-icon">📅</span><span>일정 등록</span>
        </RouterLink>
        <RouterLink to="/photos" class="panel-item" @click="closePanel">
          <span class="panel-icon">📷</span><span>앨범</span>
        </RouterLink>
        <RouterLink to="/map" class="panel-item" @click="closePanel">
          <span class="panel-icon">🍽️</span><span>맛집 찾기</span>
        </RouterLink>
        <a href="#" class="panel-item">
          <span class="panel-icon">💌</span><span>편지 쓰기</span>
        </a>
        <a href="#" class="panel-item">
          <span class="panel-icon">🎁</span><span>기념일 등록</span>
        </a>
      </nav>
    </div>

    <!-- 메인 콘텐츠 -->
    <div class="main-content">
      <header class="top-bar">
        <button class="menu-btn" @click="openPanel">≡</button>
        <span class="app-title">💕 우리의 공간</span>
        <div class="spacer"></div>
      </header>

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

        <!-- 일정 보기 + 달력 -->
        <div class="cal-schedule-row">

          <!-- 오늘의 일정 (좌측) -->
          <div class="widget schedule-widget">
            <div class="widget-header">
              <div>
                <div class="widget-title">오늘의 일정</div>
                <div class="today-label">{{ todayLabel }}</div>
              </div>
              <RouterLink to="/calendar" class="add-btn">+ 추가</RouterLink>
            </div>

            <div v-if="todaySchedules.length === 0" class="schedule-empty">
              <div class="empty-icon">📭</div>
              <div>오늘 일정 없음</div>
            </div>

            <div
              v-for="s in todaySchedules"
              :key="s.id"
              class="schedule-card"
              :class="whoClass(s.who)"
              @click="router.push({ name: 'schedule-detail', params: { id: s.id } })"
            >
              <div class="card-accent"></div>
              <div class="card-body">
                <div class="card-top">
                  <span class="card-time" v-if="s.time">{{ s.time }}</span>
                  <span class="card-who" :class="whoClass(s.who)">{{ whoLabel(s.who) }}</span>
                </div>
                <div class="card-title">{{ s.title }}</div>
                <div v-if="s.memo" class="card-memo">{{ s.memo }}</div>
              </div>
            </div>
          </div>

          <!-- 미니 달력 (우측, 크게) -->
          <div class="widget calendar-widget">
            <div class="cal-header">
              <button class="cal-nav" @click="prevMonth">‹</button>
              <span class="cal-title">{{ calYear }}.{{ String(calMonth + 1).padStart(2, '0') }}</span>
              <button class="cal-nav" @click="nextMonth">›</button>
            </div>

            <div class="cal-day-headers">
              <span
                v-for="(d, i) in dayNames"
                :key="d"
                class="cal-day-name"
                :class="{ sun: i === 0, sat: i === 6 }"
              >{{ d }}</span>
            </div>

            <div class="cal-grid">
              <div
                v-for="(cell, idx) in calendarCells"
                :key="idx"
                class="cal-cell"
                :class="{
                  empty: !cell.day,
                  today: cell.isToday,
                  sunday: cell.dayOfWeek === 0,
                  saturday: cell.dayOfWeek === 6,
                }"
                @click="cell.day && goToDate(cell.dateStr)"
              >
                <template v-if="cell.day">
                  <div class="cal-num-wrap">
                    <span class="cal-num">{{ cell.day }}</span>
                  </div>
                  <div class="cal-events">
                    <div
                      v-for="s in visibleSchedules(cell)"
                      :key="s.id"
                      class="cal-event-bar"
                      :class="whoClass(s.who)"
                      :title="s.title"
                      @click.stop="router.push({ name: 'schedule-detail', params: { id: s.id } })"
                    >{{ s.title }}</div>
                    <div v-if="hiddenCount(cell) > 0" class="cal-more">+{{ hiddenCount(cell) }}</div>
                  </div>
                </template>
              </div>
            </div>

            <RouterLink to="/calendar" class="cal-full-link">전체 달력 →</RouterLink>
          </div>

        </div>

      </div>
    </div>

    <div class="overlay" v-if="isPanelOpen" @click="closePanel"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isPanelOpen = ref(false)
const openPanel = () => { isPanelOpen.value = true }
const closePanel = () => { isPanelOpen.value = false }

// ── D-Day ──
const startDate = '2024-01-01'
const dday = computed(() => {
  return Math.floor((new Date() - new Date(startDate)) / (1000 * 60 * 60 * 24))
})

// ── 공통 ──
const today = new Date()
const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
const dayNames = ['일', '월', '화', '수', '목', '금', '토']
const schedules = ref([])

onMounted(() => {
  schedules.value = JSON.parse(localStorage.getItem('schedules') || '[]')
})

// ── 미니 달력 ──
const calYear = ref(today.getFullYear())
const calMonth = ref(today.getMonth())

function prevMonth() {
  if (calMonth.value === 0) { calMonth.value = 11; calYear.value-- } else calMonth.value--
}
function nextMonth() {
  if (calMonth.value === 11) { calMonth.value = 0; calYear.value++ } else calMonth.value++
}

const calendarCells = computed(() => {
  const firstDay = new Date(calYear.value, calMonth.value, 1).getDay()
  const daysInMonth = new Date(calYear.value, calMonth.value + 1, 0).getDate()
  const cells = []
  for (let i = 0; i < firstDay; i++) cells.push({ day: null, dayOfWeek: i })
  for (let d = 1; d <= daysInMonth; d++) {
    const date = new Date(calYear.value, calMonth.value, d)
    const dateStr = `${calYear.value}-${String(calMonth.value + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    cells.push({
      day: d,
      dayOfWeek: date.getDay(),
      isToday: dateStr === todayStr,
      dateStr,
    })
  }
  return cells
})

const MAX_VISIBLE = 1

function getSchedules(cell) {
  return schedules.value
    .filter((s) => s.date === cell.dateStr)
    .sort((a, b) => (a.time || '').localeCompare(b.time || ''))
}
function visibleSchedules(cell) {
  return getSchedules(cell).slice(0, MAX_VISIBLE)
}
function hiddenCount(cell) {
  return Math.max(0, getSchedules(cell).length - MAX_VISIBLE)
}
function goToDate(dateStr) {
  router.push({ name: 'schedule-register', query: { date: dateStr } })
}

// ── 일정 보기 (오늘) ──
const todayLabel = computed(() => {
  const d = ['일', '월', '화', '수', '목', '금', '토'][today.getDay()]
  return `${today.getMonth() + 1}월 ${today.getDate()}일 (${d})`
})

const todaySchedules = computed(() =>
  schedules.value
    .filter((s) => s.date === todayStr)
    .sort((a, b) => (a.time || '').localeCompare(b.time || ''))
)

function whoClass(who) {
  if (who === '나') return 'who-me'
  if (who === '상대방') return 'who-other'
  return 'who-together'
}
function whoLabel(who) {
  if (who === '나') return '👧 나'
  if (who === '상대방') return '👦 상대방'
  return '💕 함께'
}

// ── 오늘의 한줄 ──
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
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
  position: relative;
}

/* ── 사이드 패널 ── */
.side-panel {
  position: fixed;
  top: 0; left: 0;
  width: 20%; min-width: 140px; height: 100vh;
  background: white;
  box-shadow: 4px 0 20px rgba(233, 30, 99, 0.15);
  transform: translateX(-100%);
  transition: transform 0.3s ease;
  z-index: 100;
  display: flex; flex-direction: column;
}
.app-wrapper.panel-open .side-panel { transform: translateX(0); }

.panel-header {
  display: flex; justify-content: flex-end;
  padding: 14px; border-bottom: 1px solid #f8bbd0;
}
.close-btn {
  background: none; border: none; font-size: 1.1rem;
  color: #e91e63; cursor: pointer; padding: 4px 8px;
  border-radius: 8px; transition: background 0.2s;
}
.close-btn:hover { background: #fff0f5; }

.panel-menu { display: flex; flex-direction: column; padding: 8px 0; gap: 2px; }
.panel-item {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 18px; text-decoration: none;
  color: #444; font-size: 0.88rem; font-weight: 500;
  transition: background 0.2s, color 0.2s;
}
.panel-item:hover { background: #fff0f5; color: #e91e63; }
.panel-icon { font-size: 1.2rem; }

/* ── 메인 콘텐츠 ── */
.main-content {
  flex: 1;
  height: 100vh;
  overflow: hidden;
  transition: margin-left 0.3s ease;
  display: flex; flex-direction: column;
}
.app-wrapper.panel-open .main-content { margin-left: max(20%, 140px); }

.top-bar {
  flex-shrink: 0;
  display: flex; align-items: center;
  padding: 12px 18px; gap: 14px;
}
.menu-btn {
  background: white; border: none; font-size: 2rem;
  color: #e91e63; cursor: pointer; width: 50px; height: 50px;
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(233, 30, 99, 0.2); transition: transform 0.2s;
}
.menu-btn:hover { transform: scale(1.05); }
.app-title { color: white; font-size: 1.7rem; font-weight: bold; }
.spacer { flex: 1; }

/* content = 남은 높이 모두 사용 */
.content {
  flex: 1;
  min-height: 0;
  padding: 0 16px 16px;
  display: flex; flex-direction: column;
  gap: 10px;
}

/* ── 공통 위젯 ── */
.widget {
  background: white; border-radius: 16px; padding: 14px 16px;
  box-shadow: 0 3px 14px rgba(255, 100, 130, 0.13);
}
.widget-title { font-size: 0.88rem; font-weight: bold; color: #333; }

/* ── D-Day (고정 높이) ── */
.dday-widget { display: flex; align-items: center; gap: 14px; flex-shrink: 0; }
.dday-heart { font-size: 2.2rem; }
.dday-count { font-size: 1.6rem; font-weight: bold; color: #e91e63; }
.dday-label { font-size: 0.78rem; color: #888; }
.dday-date { font-size: 0.7rem; color: #bbb; margin-top: 2px; }

/* ── 오늘의 한줄 (고정 높이) ── */
.oneliner-widget { flex-shrink: 0; }
.oneliner-content { display: flex; flex-direction: column; gap: 6px; margin: 8px 0; }
.oneliner-item { display: flex; align-items: flex-start; gap: 7px; }
.ol-avatar { font-size: 1.1rem; }
.ol-text {
  font-size: 0.82rem; color: #555;
  background: #fff5f8; padding: 6px 10px;
  border-radius: 10px; flex: 1; line-height: 1.4;
}
.oneliner-input {
  width: 100%; border: 1.5px solid #f8bbd0; border-radius: 10px;
  padding: 8px 12px; font-size: 0.82rem; color: #333;
  outline: none; box-sizing: border-box; transition: border-color 0.2s;
}
.oneliner-input:focus { border-color: #e91e63; }

/* ── 달력+일정 행 (남은 공간 채움) ── */
.cal-schedule-row {
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 10px;
}

/* 일정 보기 (30%) */
.schedule-widget {
  flex: 2;
  min-height: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
.schedule-widget::-webkit-scrollbar { width: 3px; }
.schedule-widget::-webkit-scrollbar-thumb { background: #f48fb1; border-radius: 2px; }

/* 달력 (70%) */
.calendar-widget {
  flex: 7;
  min-height: 0;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
}

/* ── 미니 달력 ── */
.cal-header {
  flex-shrink: 0;
  display: flex; align-items: center; gap: 8px; margin-bottom: 12px;
}
.cal-title { font-size: 2.07rem; font-weight: bold; color: #333; flex: 1; text-align: center; }
.cal-nav {
  background: #fce4ec; border: none; border-radius: 50%;
  width: 36px; height: 36px; font-size: 1.35rem; color: #e91e63;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: background 0.2s; padding: 0;
}
.cal-nav:hover { background: #f48fb1; color: white; }

.cal-day-headers {
  flex-shrink: 0;
  display: grid; grid-template-columns: repeat(7, 1fr); margin-bottom: 6px;
}
.cal-day-name { text-align: center; font-size: 0.82rem; font-weight: bold; color: #ccc; padding: 4px 0; }
.cal-day-name.sun { color: #e91e63; }
.cal-day-name.sat { color: #1976d2; }

.cal-grid {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-auto-rows: 1fr;
  border-left: 1px solid #eee;
  border-top: 1px solid #eee;
}
.cal-cell {
  border-right: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 3px 4px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  overflow: hidden;
  transition: background 0.12s;
}
.cal-cell:not(.empty):hover { background: #fff5f7; }
.cal-cell.empty { cursor: default; background: #fafafa; }

.cal-num-wrap { display: flex; justify-content: flex-start; margin-bottom: 2px; }
.cal-num {
  font-size: 0.8rem; color: #333; line-height: 1;
  width: 22px; height: 22px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 50%;
}
.cal-cell.sunday .cal-num { color: #e91e63; }
.cal-cell.saturday .cal-num { color: #1976d2; }
.cal-cell.today .cal-num {
  background: #e91e63; color: white !important; font-weight: bold;
}

.cal-events { display: flex; flex-direction: column; gap: 1px; min-height: 0; }
.cal-event-bar {
  font-size: 0.62rem;
  padding: 1px 4px;
  border-radius: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: white;
  line-height: 1.5;
  cursor: pointer;
}
.cal-event-bar.who-together { background: #e91e63; }
.cal-event-bar.who-me       { background: #8e24aa; }
.cal-event-bar.who-other    { background: #1565c0; }
.cal-more {
  font-size: 0.68rem;
  font-weight: bold;
  color: #e91e63;
  background: #fce4ec;
  border-radius: 3px;
  padding: 1px 4px;
  line-height: 1.5;
}

.cal-full-link {
  flex-shrink: 0;
  display: block; text-align: center; margin-top: 8px;
  font-size: 0.7rem; color: #e91e63; text-decoration: none;
  background: #fce4ec; border-radius: 8px; padding: 4px;
  transition: background 0.2s;
}
.cal-full-link:hover { background: #f48fb1; color: white; }

/* ── 일정 보기 ── */
.widget-header {
  flex-shrink: 0;
  display: flex; align-items: flex-start;
  justify-content: space-between; margin-bottom: 12px;
}
.today-label { font-size: 0.92rem; color: #e91e63; font-weight: 600; margin-top: 3px; }
.add-btn {
  font-size: 0.75rem; color: #e91e63; text-decoration: none;
  background: #fce4ec; padding: 5px 11px; border-radius: 10px;
  transition: background 0.2s; white-space: nowrap; margin-top: 2px;
}
.add-btn:hover { background: #f48fb1; color: white; }

.schedule-empty {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; gap: 6px; flex: 1;
  color: #ccc; font-size: 0.8rem;
}
.empty-icon { font-size: 2rem; }

.schedule-card {
  display: flex; border-radius: 12px; overflow: hidden;
  margin-bottom: 8px; background: #fafafa;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06); flex-shrink: 0;
}
.schedule-card:last-child { margin-bottom: 0; }

.card-accent { width: 5px; flex-shrink: 0; }
.who-together .card-accent { background: #e91e63; }
.who-me       .card-accent { background: #8e24aa; }
.who-other    .card-accent { background: #1565c0; }

.card-body { flex: 1; padding: 10px 12px; }
.card-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 4px; }
.card-time { font-size: 0.75rem; color: #aaa; font-weight: 500; }
.card-who {
  font-size: 0.68rem; font-weight: bold; padding: 2px 7px; border-radius: 8px;
}
.card-who.who-together { background: #fce4ec; color: #e91e63; }
.card-who.who-me       { background: #f3e5f5; color: #8e24aa; }
.card-who.who-other    { background: #e3f2fd; color: #1565c0; }
.card-title { font-size: 0.88rem; font-weight: bold; color: #222; }
.card-memo { font-size: 0.72rem; color: #aaa; margin-top: 4px; line-height: 1.4; }

/* ── 오버레이 ── */
.overlay { position: fixed; inset: 0; background: transparent; z-index: 50; }
</style>
