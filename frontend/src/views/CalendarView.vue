<template>
  <div class="page">
    <div class="header">
      <RouterLink to="/" class="back">← 홈</RouterLink>
      <h1>📅 달력</h1>
    </div>

    <div class="main-content" ref="containerRef">
      <!-- 좌측: 이번 달 일정 리스트 -->
      <div class="schedule-panel" :style="{ width: panelWidth + 'px' }">
        <div class="panel-title">{{ month + 1 }}월 일정</div>
        <div v-if="monthSchedules.length === 0" class="empty-msg">
          이번 달 일정이 없어요 🌸
        </div>
        <div
          v-for="s in monthSchedules"
          :key="s.id"
          class="schedule-item"
          @click="router.push({ name: 'schedule-detail', params: { id: s.id } })"
        >
          <div class="schedule-badge">{{ formatDay(s.date) }}</div>
          <div class="schedule-info">
            <div class="s-title">{{ s.title }}</div>
            <div class="s-meta">
              <span v-if="s.time">{{ s.time }}</span>
              <span>{{ s.who }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 드래그 핸들 -->
      <div
        class="divider"
        :class="{ dragging: isDragging }"
        @mousedown.prevent="startDrag"
      >
        <div class="divider-handle"></div>
      </div>

      <!-- 우측: 달력 -->
      <div class="calendar-panel">
        <div class="month-nav">
          <button class="nav-btn" @click="prevMonth">‹</button>
          <span class="month-title">{{ year }}년 {{ month + 1 }}월</span>
          <button class="nav-btn" @click="nextMonth">›</button>
        </div>

        <div class="day-headers">
          <span
            v-for="(d, i) in dayNames"
            :key="d"
            class="day-header"
            :class="{ sun: i === 0, sat: i === 6 }"
          >{{ d }}</span>
        </div>

        <div class="day-grid">
          <div
            v-for="(cell, i) in calendarCells"
            :key="i"
            class="day-cell"
            :class="{
              empty: !cell.day,
              today: cell.isToday,
              sunday: cell.dayOfWeek === 0,
              saturday: cell.dayOfWeek === 6,
            }"
            @click="cell.day && goToRegister(cell)"
          >
            <template v-if="cell.day">
              <div class="day-num-wrap">
                <span class="day-number">{{ cell.day }}</span>
              </div>
              <div class="cell-events">
                <div
                  v-for="s in visibleSchedules(cell)"
                  :key="s.id"
                  class="event-bar"
                  :class="whoClass(s.who)"
                  :title="s.title"
                  @click.stop="router.push({ name: 'schedule-detail', params: { id: s.id } })"
                >
                  <span v-if="s.time" class="event-time">{{ s.time }}</span>
                  {{ s.title }}
                </div>
                <div v-if="hiddenCount(cell) > 0" class="more-count">
                  +{{ hiddenCount(cell) }}개 더
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const MAX_VISIBLE = 2

const today = new Date()
const year = ref(today.getFullYear())
const month = ref(today.getMonth())
const schedules = ref([])

const dayNames = ['일', '월', '화', '수', '목', '금', '토']

// ── 리사이즈 ──
const containerRef = ref(null)
const panelWidth = ref(440)
const isDragging = ref(false)

function startDrag() {
  isDragging.value = true
  document.body.style.cursor = 'col-resize'
  document.body.style.userSelect = 'none'
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}
function onDrag(e) {
  if (!isDragging.value || !containerRef.value) return
  const rect = containerRef.value.getBoundingClientRect()
  panelWidth.value = Math.min(rect.width * 0.4, Math.max(150, e.clientX - rect.left))
}
function stopDrag() {
  isDragging.value = false
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

onMounted(() => {
  schedules.value = JSON.parse(localStorage.getItem('schedules') || '[]')
})
onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
})

function prevMonth() {
  if (month.value === 0) { month.value = 11; year.value-- } else month.value--
}
function nextMonth() {
  if (month.value === 11) { month.value = 0; year.value++ } else month.value++
}

const calendarCells = computed(() => {
  const firstDay = new Date(year.value, month.value, 1).getDay()
  const daysInMonth = new Date(year.value, month.value + 1, 0).getDate()
  const cells = []
  for (let i = 0; i < firstDay; i++) cells.push({ day: null, dayOfWeek: i })
  for (let d = 1; d <= daysInMonth; d++) {
    const date = new Date(year.value, month.value, d)
    cells.push({
      day: d,
      dayOfWeek: date.getDay(),
      isToday: today.getFullYear() === year.value && today.getMonth() === month.value && today.getDate() === d,
      dateStr: `${year.value}-${String(month.value + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`,
    })
  }
  return cells
})

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

function whoClass(who) {
  if (who === '나') return 'who-me'
  if (who === '상대방') return 'who-other'
  return 'who-together'
}

const monthSchedules = computed(() => {
  const prefix = `${year.value}-${String(month.value + 1).padStart(2, '0')}`
  return schedules.value
    .filter((s) => s.date.startsWith(prefix))
    .sort((a, b) => a.date.localeCompare(b.date) || (a.time || '').localeCompare(b.time || ''))
})

function formatDay(dateStr) {
  const [, m, d] = dateStr.split('-')
  return `${Number(m)}/${Number(d)}`
}
function goToRegister(cell) {
  router.push({ name: 'schedule-register', query: { date: cell.dateStr } })
}
</script>

<style scoped>
.page {
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
  padding: 20px 24px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 14px;
  flex-shrink: 0;
}
.back {
  text-decoration: none;
  color: #e91e63;
  font-weight: bold;
  background: white;
  padding: 7px 15px;
  border-radius: 20px;
  font-size: 0.9rem;
}
h1 {
  color: white;
  font-size: 1.5rem;
  margin: 0;
}

.main-content {
  flex: 1;
  display: flex;
  min-height: 0;
}

/* ── 드래그 핸들 ── */
.divider {
  width: 14px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: col-resize;
  user-select: none;
}
.divider-handle {
  width: 4px;
  height: 48px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 2px;
  transition: background 0.2s, height 0.2s;
}
.divider:hover .divider-handle,
.divider.dragging .divider-handle {
  background: white;
  height: 64px;
}

/* ── 좌측 일정 리스트 ── */
.schedule-panel {
  flex-shrink: 0;
  background: white;
  border-radius: 18px;
  padding: 16px 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;
}
.schedule-panel::-webkit-scrollbar { width: 4px; }
.schedule-panel::-webkit-scrollbar-thumb { background: #f48fb1; border-radius: 2px; }
.schedule-panel::-webkit-scrollbar-track { background: transparent; }

.panel-title {
  font-size: 1.9rem;
  font-weight: bold;
  color: #e91e63;
  padding-bottom: 10px;
  border-bottom: 2px solid #fce4ec;
  flex-shrink: 0;
}
.empty-msg {
  text-align: center;
  color: #ccc;
  font-size: 0.85rem;
  margin-top: 16px;
}
.schedule-item {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  padding: 9px 10px;
  background: #fafafa;
  border-radius: 12px;
  flex-shrink: 0;
}
.schedule-badge {
  background: #fce4ec;
  color: #e91e63;
  font-size: 0.72rem;
  font-weight: bold;
  padding: 3px 7px;
  border-radius: 8px;
  flex-shrink: 0;
  white-space: nowrap;
  line-height: 1.6;
}
.schedule-info { flex: 1; min-width: 0; }
.s-title {
  font-size: 0.85rem;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.s-meta {
  display: flex;
  gap: 5px;
  margin-top: 2px;
  font-size: 0.72rem;
  color: #aaa;
}

/* ── 우측 달력 ── */
.calendar-panel {
  flex: 1;
  background: white;
  border-radius: 18px;
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.month-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  flex-shrink: 0;
}
.month-title {
  font-size: 2.07rem;
  font-weight: bold;
  color: #333;
}
.nav-btn {
  background: #fce4ec;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  font-size: 1.35rem;
  color: #e91e63;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}
.nav-btn:hover { background: #f48fb1; color: white; }

/* 요일 헤더 */
.day-headers {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}
.day-header {
  text-align: center;
  font-size: 0.75rem;
  font-weight: bold;
  color: #bbb;
  padding: 5px 0;
}
.day-header.sun { color: #e91e63; }
.day-header.sat { color: #1976d2; }

/* 날짜 그리드 */
.day-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-auto-rows: 1fr;
  border-left: 1px solid #eee;
  min-height: 0;
}

.day-cell {
  border-right: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 4px 5px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  overflow: hidden;
  transition: background 0.12s;
}
.day-cell:not(.empty):hover { background: #fff5f7; }
.day-cell.empty { cursor: default; background: #fafafa; }

/* 날짜 숫자 — 좌측 상단 */
.day-num-wrap {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 3px;
}
.day-number {
  font-size: 0.82rem;
  color: #333;
  line-height: 1;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-weight: 500;
}
.day-cell.sunday .day-number { color: #e91e63; }
.day-cell.saturday .day-number { color: #1976d2; }
.day-cell.today .day-number {
  background: #e91e63;
  color: white !important;
  font-weight: bold;
}

/* 이벤트 바 */
.cell-events {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-height: 0;
}
.event-bar {
  font-size: 0.7rem;
  padding: 1px 5px;
  border-radius: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: white;
  line-height: 1.6;
  cursor: pointer;
}
.event-time {
  margin-right: 3px;
  opacity: 0.85;
  font-size: 0.65rem;
}

/* 누구랑 색상 */
.who-together { background: #e91e63; }
.who-me       { background: #8e24aa; }
.who-other    { background: #1565c0; }

.more-count {
  font-size: 0.75rem;
  font-weight: bold;
  color: #e91e63;
  background: #fce4ec;
  border-radius: 3px;
  padding: 1px 5px;
  cursor: pointer;
  line-height: 1.5;
}
.more-count:hover { background: #f48fb1; color: white; }
</style>
