<template>
  <div class="page">
    <div class="header">
      <button class="back" @click="$router.back()">← 뒤로</button>
      <h1>{{ isEditing ? '✏️ 일정 수정' : '📋 일정 상세' }}</h1>
      <button v-if="isEditing" class="cancel-btn" @click="cancelEdit">취소</button>
    </div>

    <div v-if="!schedule" class="not-found">
      일정을 찾을 수 없어요.
    </div>

    <div v-else class="form-card">
      <div class="date-display">{{ formattedDate }}</div>

      <!-- 조회 모드 -->
      <template v-if="!isEditing">
        <div class="detail-row">
          <span class="detail-label">제목</span>
          <span class="detail-value">{{ schedule.title }}</span>
        </div>
        <div class="detail-row" v-if="schedule.time">
          <span class="detail-label">시간</span>
          <span class="detail-value">{{ schedule.time }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">누구랑</span>
          <span class="detail-badge" :class="whoClass(schedule.who)">{{ whoLabel(schedule.who) }}</span>
        </div>
        <div class="detail-row" v-if="schedule.memo">
          <span class="detail-label">메모</span>
          <span class="detail-value memo">{{ schedule.memo }}</span>
        </div>
      </template>

      <!-- 수정 모드 -->
      <template v-else>
        <div class="form-group">
          <label>제목</label>
          <input v-model="form.title" placeholder="일정 제목을 입력하세요" />
        </div>

        <div class="form-group">
          <label>시간</label>
          <input type="time" v-model="form.time" />
        </div>

        <div class="form-group">
          <label>누구랑</label>
          <div class="who-buttons">
            <button
              v-for="w in whoOptions"
              :key="w.value"
              class="who-btn"
              :class="{ active: form.who === w.value }"
              @click="form.who = w.value"
            >{{ w.label }}</button>
          </div>
        </div>

        <div class="form-group memo-group">
          <label>메모</label>
          <textarea v-model="form.memo" placeholder="메모 (선택사항)"></textarea>
        </div>
      </template>

      <!-- 하단 버튼 2개 (항상 표시) -->
      <div class="bottom-actions">
        <button class="save-btn" @click="isEditing ? save() : (isEditing = true)">
          {{ isEditing ? '💕 저장' : '✏️ 수정' }}
        </button>
        <button class="delete-btn" @click="confirmDelete">🗑 삭제</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const id = Number(route.params.id)
const schedule = ref(null)
const isEditing = ref(false)

const whoOptions = [
  { value: '함께', label: '💕 함께' },
  { value: '나', label: '👧 나' },
  { value: '상대방', label: '👦 상대방' },
]

const form = ref({ title: '', time: '', who: '함께', memo: '' })

onMounted(() => {
  const all = JSON.parse(localStorage.getItem('schedules') || '[]')
  const found = all.find((s) => s.id === id)
  if (found) {
    schedule.value = found
    form.value = { title: found.title, time: found.time || '', who: found.who, memo: found.memo || '' }
  }
})

const formattedDate = computed(() => {
  if (!schedule.value) return ''
  const [y, m, d] = schedule.value.date.split('-')
  const dow = ['일', '월', '화', '수', '목', '금', '토'][new Date(schedule.value.date).getDay()]
  return `${y}년 ${Number(m)}월 ${Number(d)}일 (${dow})`
})

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

function cancelEdit() {
  form.value = { title: schedule.value.title, time: schedule.value.time || '', who: schedule.value.who, memo: schedule.value.memo || '' }
  isEditing.value = false
}

function save() {
  if (!form.value.title.trim()) { alert('제목을 입력해주세요'); return }
  const all = JSON.parse(localStorage.getItem('schedules') || '[]')
  const idx = all.findIndex((s) => s.id === id)
  if (idx !== -1) {
    all[idx] = { ...all[idx], ...form.value }
    localStorage.setItem('schedules', JSON.stringify(all))
    schedule.value = all[idx]
  }
  isEditing.value = false
}

function confirmDelete() {
  if (!confirm('일정을 삭제할까요?')) return
  const all = JSON.parse(localStorage.getItem('schedules') || '[]')
  localStorage.setItem('schedules', JSON.stringify(all.filter((s) => s.id !== id)))
  router.back()
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
  gap: 14px;
  margin-bottom: 16px;
  flex-shrink: 0;
}
.back {
  background: white; border: none; color: #e91e63;
  font-weight: bold; padding: 7px 15px; border-radius: 20px;
  cursor: pointer; font-size: 0.9rem;
}
h1 { color: white; font-size: 1.5rem; margin: 0; flex: 1; }
.cancel-btn {
  background: rgba(255,255,255,0.3); border: none; color: white;
  padding: 7px 14px; border-radius: 20px; cursor: pointer; font-size: 0.9rem;
}

.not-found {
  background: white; border-radius: 18px; padding: 40px;
  text-align: center; color: #bbb;
}

.form-card {
  flex: 1;
  background: white;
  border-radius: 18px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
  overflow-y: auto;
}

.date-display {
  text-align: center;
  font-size: 1.15rem;
  font-weight: bold;
  color: #e91e63;
  background: #fce4ec;
  border-radius: 12px;
  padding: 12px;
  flex-shrink: 0;
}

/* ── 조회 모드 ── */
.detail-row {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}
.detail-row:last-of-type { border-bottom: none; }
.detail-label {
  font-size: 0.85rem;
  font-weight: bold;
  color: #e91e63;
  min-width: 56px;
  padding-top: 2px;
}
.detail-value {
  font-size: 1rem;
  color: #222;
  flex: 1;
}
.detail-value.memo { color: #666; font-size: 0.9rem; line-height: 1.5; }
.detail-badge {
  font-size: 0.82rem;
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 10px;
}
.who-together { background: #fce4ec; color: #e91e63; }
.who-me       { background: #f3e5f5; color: #8e24aa; }
.who-other    { background: #e3f2fd; color: #1565c0; }

/* ── 수정 모드 ── */
.form-group { display: flex; flex-direction: column; gap: 7px; flex-shrink: 0; }
.memo-group { flex: 1; min-height: 0; }
.memo-group textarea { flex: 1; min-height: 0; }

label {
  font-size: 0.8rem; font-weight: bold; color: #aaa;
  text-transform: uppercase; letter-spacing: 0.05em;
}
input:not([type='time']),
input[type='time'] {
  border: 2px solid #f0f0f0; border-radius: 12px;
  padding: 12px 14px; font-size: 0.95rem; color: #333;
  outline: none; transition: border-color 0.2s;
}
input:focus { border-color: #f48fb1; }

textarea {
  border: 2px solid #f0f0f0; border-radius: 12px;
  padding: 12px 14px; font-size: 0.95rem; color: #333;
  outline: none; resize: none; font-family: inherit;
  transition: border-color 0.2s; width: 100%;
  box-sizing: border-box; height: 100%; min-height: 80px;
}
textarea:focus { border-color: #f48fb1; }

.who-buttons { display: flex; gap: 10px; }
.who-btn {
  flex: 1; padding: 11px 0; border: 2px solid #f0f0f0;
  border-radius: 12px; background: white; font-size: 0.92rem;
  cursor: pointer; color: #aaa; transition: all 0.2s;
}
.who-btn.active { border-color: #e91e63; background: #fce4ec; color: #e91e63; font-weight: bold; }

.bottom-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
  margin-top: auto;
  padding-top: 8px;
}

.save-btn {
  flex: 1;
  background: linear-gradient(135deg, #e91e63, #f48fb1);
  color: white; border: none; border-radius: 14px;
  padding: 15px; font-size: 1.05rem; font-weight: bold;
  cursor: pointer; transition: opacity 0.2s;
}
.save-btn:hover { opacity: 0.9; }

.delete-btn {
  flex: 1;
  background: none; border: 1.5px solid #ffcdd2;
  color: #ef5350; border-radius: 14px;
  padding: 14px; font-size: 1.05rem; font-weight: bold;
  cursor: pointer; transition: all 0.2s;
}
.delete-btn:hover { background: #ffebee; }
</style>
