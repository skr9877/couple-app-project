<template>
  <div class="page">
    <div class="header">
      <button class="back" @click="$router.back()">← 달력</button>
      <h1>📝 일정 등록</h1>
    </div>

    <div class="form-card">
      <div class="date-display">{{ formattedDate }}</div>

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
          >
            {{ w.label }}
          </button>
        </div>
      </div>

      <div class="form-group memo-group">
        <label>메모</label>
        <textarea v-model="form.memo" placeholder="메모 (선택사항)"></textarea>
      </div>

      <button class="save-btn" @click="save">💕 일정 등록</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const date = route.query.date || new Date().toISOString().slice(0, 10)

const formattedDate = computed(() => {
  const [y, m, d] = date.split('-')
  return `${y}년 ${Number(m)}월 ${Number(d)}일`
})

const whoOptions = [
  { value: '함께', label: '💕 함께' },
  { value: '나', label: '👧 나' },
  { value: '상대방', label: '👦 상대방' },
]

const form = ref({ title: '', time: '', who: '함께', memo: '' })

function save() {
  if (!form.value.title.trim()) {
    alert('제목을 입력해주세요')
    return
  }
  const schedules = JSON.parse(localStorage.getItem('schedules') || '[]')
  schedules.push({ id: Date.now(), date, ...form.value })
  localStorage.setItem('schedules', JSON.stringify(schedules))
  router.push({ name: 'calendar' })
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
  background: white;
  border: none;
  color: #e91e63;
  font-weight: bold;
  padding: 7px 15px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
}

h1 {
  color: white;
  font-size: 1.5rem;
  margin: 0;
}

.form-card {
  flex: 1;
  background: white;
  border-radius: 18px;
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
  overflow: hidden;
}

.date-display {
  text-align: center;
  font-size: 1.15rem;
  font-weight: bold;
  color: #e91e63;
  background: #fce4ec;
  border-radius: 12px;
  padding: 11px;
  flex-shrink: 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
  flex-shrink: 0;
}

.memo-group {
  flex: 1;
  min-height: 0;
}

.memo-group textarea {
  flex: 1;
  min-height: 0;
}

label {
  font-size: 0.8rem;
  font-weight: bold;
  color: #aaa;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

input:not([type='time']),
input[type='time'] {
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 11px 14px;
  font-size: 0.95rem;
  color: #333;
  outline: none;
  transition: border-color 0.2s;
}

input:focus {
  border-color: #f48fb1;
}

textarea {
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 11px 14px;
  font-size: 0.95rem;
  color: #333;
  outline: none;
  resize: none;
  font-family: inherit;
  transition: border-color 0.2s;
  width: 100%;
  box-sizing: border-box;
  height: 100%;
}

textarea:focus {
  border-color: #f48fb1;
}

.who-buttons {
  display: flex;
  gap: 10px;
}

.who-btn {
  flex: 1;
  padding: 11px 0;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  background: white;
  font-size: 0.92rem;
  cursor: pointer;
  color: #aaa;
  transition: all 0.2s;
}

.who-btn.active {
  border-color: #e91e63;
  background: #fce4ec;
  color: #e91e63;
  font-weight: bold;
}

.save-btn {
  background: linear-gradient(135deg, #e91e63, #f48fb1);
  color: white;
  border: none;
  border-radius: 14px;
  padding: 15px;
  font-size: 1.05rem;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.2s;
  flex-shrink: 0;
}

.save-btn:hover {
  opacity: 0.9;
}
</style>
