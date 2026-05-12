<template>
  <div class="page">
    <div class="header">
      <button class="back" @click="$router.back()">← 뒤로</button>
      <h1>🎂 기념일</h1>
    </div>

    <div v-if="anniversary" class="card">
      <template v-if="!isEditing">
        <!-- 조회 모드 -->
        <div class="date-badge">매년 {{ anniversary.month }}월 {{ anniversary.day }}일</div>

        <div class="detail-title">{{ anniversary.title }}</div>

        <div class="detail-row">
          <span class="detail-label">누구랑</span>
          <span class="detail-value">{{ whoLabel(anniversary.who) }}</span>
        </div>

        <div v-if="anniversary.memo" class="detail-row">
          <span class="detail-label">내용</span>
          <span class="detail-value memo">{{ anniversary.memo }}</span>
        </div>

        <div class="btn-row">
          <button class="edit-btn" @click="startEdit">✏️ 수정</button>
          <button class="del-btn" @click="remove">🗑️ 삭제</button>
        </div>
      </template>

      <template v-else>
        <!-- 수정 모드 -->
        <div class="form-group">
          <label>제목</label>
          <input v-model="form.title" placeholder="기념일 이름을 입력하세요" />
        </div>

        <div class="form-group">
          <label>날짜</label>
          <div class="date-row">
            <select v-model="form.month">
              <option v-for="m in 12" :key="m" :value="m">{{ m }}월</option>
            </select>
            <select v-model="form.day">
              <option v-for="d in daysInMonth" :key="d" :value="d">{{ d }}일</option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label>누구랑</label>
          <div class="who-buttons">
            <button
              v-for="opt in whoOptions"
              :key="opt.value"
              class="who-btn"
              :class="{ active: form.who === opt.value }"
              @click="form.who = opt.value"
            >{{ opt.label }}</button>
          </div>
        </div>

        <div class="form-group memo-group">
          <label>내용 <span class="optional">(선택)</span></label>
          <textarea v-model="form.memo" placeholder="메모를 입력하세요"></textarea>
        </div>

        <div class="btn-row">
          <button class="cancel-btn" @click="cancelEdit">취소</button>
          <button class="save-btn" :disabled="!form.title.trim()" @click="save">저장</button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const anniversary = ref(null)
const isEditing = ref(false)
const form = ref({ title: '', month: 1, day: 1, memo: '', who: '함께' })

const whoOptions = [
  { value: '함께', label: '💕 함께' },
  { value: '나', label: '👧 나' },
  { value: '상대방', label: '👦 상대방' },
]

const maxDaysPerMonth = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
const daysInMonth = computed(() => maxDaysPerMonth[form.value.month - 1])

watch(() => form.value.month, () => {
  if (form.value.day > daysInMonth.value) form.value.day = daysInMonth.value
})

function whoLabel(who) {
  if (who === '나') return '👧 나'
  if (who === '상대방') return '👦 상대방'
  return '💕 함께'
}

onMounted(async () => {
  const res = await fetch(`/api/anniversaries/${route.params.id}`)
  anniversary.value = await res.json()
})

function startEdit() {
  form.value = {
    title: anniversary.value.title,
    month: anniversary.value.month,
    day: anniversary.value.day,
    memo: anniversary.value.memo || '',
    who: anniversary.value.who,
  }
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
}

async function save() {
  if (!form.value.title.trim()) return
  const res = await fetch(`/api/anniversaries/${route.params.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      title: form.value.title.trim(),
      month: form.value.month,
      day: form.value.day,
      memo: form.value.memo.trim(),
      who: form.value.who,
    }),
  })
  anniversary.value = await res.json()
  isEditing.value = false
}

async function remove() {
  if (!confirm(`"${anniversary.value.title}" 기념일을 삭제할까요?`)) return
  await fetch(`/api/anniversaries/${route.params.id}`, { method: 'DELETE' })
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

.card {
  flex: 1;
  background: white;
  border-radius: 18px;
  padding: 28px 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 0;
  overflow: hidden;
}

/* ── 조회 모드 ── */
.date-badge {
  text-align: center;
  font-size: 1.1rem;
  font-weight: bold;
  color: #ff9800;
  background: #fff3e0;
  border-radius: 12px;
  padding: 12px;
  flex-shrink: 0;
}
.detail-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: #222;
  flex-shrink: 0;
}
.detail-row {
  display: flex;
  flex-direction: column;
  gap: 5px;
  flex-shrink: 0;
}
.detail-label {
  font-size: 0.78rem;
  font-weight: bold;
  color: #aaa;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.detail-value {
  font-size: 1rem;
  color: #333;
}
.detail-value.memo {
  white-space: pre-wrap;
  line-height: 1.6;
}

/* ── 수정 모드 ── */
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
  height: 100%;
}

label {
  font-size: 0.8rem;
  font-weight: bold;
  color: #aaa;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.optional {
  font-weight: normal;
  text-transform: none;
  letter-spacing: 0;
  color: #ccc;
}

input,
select,
textarea {
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 11px 14px;
  font-size: 0.95rem;
  color: #333;
  outline: none;
  transition: border-color 0.2s;
  font-family: inherit;
}
input:focus,
select:focus,
textarea:focus {
  border-color: #f48fb1;
}
textarea {
  resize: none;
  width: 100%;
  box-sizing: border-box;
}

.date-row {
  display: flex;
  gap: 10px;
}
.date-row select { flex: 1; }

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

/* ── 버튼 ── */
.btn-row {
  display: flex;
  gap: 10px;
  margin-top: auto;
  flex-shrink: 0;
}
.edit-btn {
  flex: 1;
  padding: 15px;
  background: linear-gradient(135deg, #e91e63, #f48fb1);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 1.05rem;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.2s;
}
.edit-btn:hover { opacity: 0.9; }

.del-btn {
  flex: 1;
  padding: 15px;
  background: white;
  color: #e91e63;
  border: 2px solid #fce4ec;
  border-radius: 14px;
  font-size: 1.05rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}
.del-btn:hover {
  background: #fce4ec;
}

.save-btn {
  flex: 1;
  padding: 15px;
  background: linear-gradient(135deg, #e91e63, #f48fb1);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 1.05rem;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.2s;
}
.save-btn:hover:not(:disabled) { opacity: 0.9; }
.save-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.cancel-btn {
  padding: 15px 20px;
  border: 2px solid #f0f0f0;
  border-radius: 14px;
  background: white;
  color: #aaa;
  font-size: 1.05rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}
.cancel-btn:hover {
  border-color: #e91e63;
  color: #e91e63;
}
</style>
