<template>
  <div class="page">
    <div class="header">
      <RouterLink to="/" class="back">← 홈</RouterLink>
      <h1>🎂 기념일 등록</h1>
    </div>

    <div class="content">
      <!-- 좌측: 등록 폼 -->
      <div class="form-card">
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
          <button v-if="editingId" class="cancel-btn" @click="cancelEdit">취소</button>
          <button class="save-btn" :disabled="!form.title.trim()" @click="submit">
            {{ editingId ? '✏️ 수정 완료' : '🎂 기념일 등록' }}
          </button>
        </div>
      </div>

      <!-- 우측: 등록된 기념일 목록 -->
      <div class="list-card">
        <div class="list-title">등록된 기념일</div>
        <div v-if="sortedAnniversaries.length === 0" class="empty">아직 기념일이 없어요 🎁</div>
        <div class="list-scroll">
          <div
            v-for="ann in sortedAnniversaries"
            :key="ann.id"
            class="ann-item"
            :class="{ editing: editingId === ann.id, selected: selectedId === ann.id }"
            @click="toggleSelect(ann.id)"
          >
            <div class="ann-badge">{{ ann.month }}/{{ ann.day }}</div>
            <div class="ann-info">
              <div class="ann-title">{{ ann.title }}</div>
              <div class="ann-meta">
                <span>{{ whoLabel(ann.who) }}</span>
                <span v-if="ann.memo" class="ann-memo">{{ ann.memo }}</span>
              </div>
            </div>
            <div v-if="selectedId === ann.id" class="action-btns" @click.stop>
              <button class="action-edit" @click="startEdit(ann); selectedId = null">수정</button>
              <button class="action-del" @click="remove(ann.id)">삭제</button>
              <button class="action-cancel" @click="selectedId = null">취소</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const anniversaries = ref([])
const form = ref({ title: '', month: 1, day: 1, memo: '', who: '함께' })
const editingId = ref(null)
const selectedId = ref(null)

function toggleSelect(id) {
  selectedId.value = selectedId.value === id ? null : id
}

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

const sortedAnniversaries = computed(() =>
  [...anniversaries.value].sort((a, b) => a.month - b.month || a.day - b.day)
)

function whoLabel(who) {
  if (who === '나') return '👧 나'
  if (who === '상대방') return '👦 상대방'
  return '💕 함께'
}

onMounted(async () => {
  const res = await fetch('/api/anniversaries')
  anniversaries.value = await res.json()
})

function startEdit(ann) {
  editingId.value = ann.id
  form.value = { title: ann.title, month: ann.month, day: ann.day, memo: ann.memo || '', who: ann.who }
}

function cancelEdit() {
  editingId.value = null
  selectedId.value = null
  form.value = { title: '', month: 1, day: 1, memo: '', who: '함께' }
}

async function submit() {
  if (!form.value.title.trim()) return
  const payload = {
    title: form.value.title.trim(),
    month: form.value.month,
    day: form.value.day,
    memo: form.value.memo.trim(),
    who: form.value.who,
  }
  if (editingId.value) {
    const res = await fetch(`/api/anniversaries/${editingId.value}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    const updated = await res.json()
    const idx = anniversaries.value.findIndex((a) => a.id === editingId.value)
    if (idx !== -1) anniversaries.value[idx] = updated
    editingId.value = null
  } else {
    const res = await fetch('/api/anniversaries', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    anniversaries.value.push(await res.json())
  }
  form.value = { title: '', month: 1, day: 1, memo: '', who: '함께' }
}

async function remove(id) {
  await fetch(`/api/anniversaries/${id}`, { method: 'DELETE' })
  anniversaries.value = anniversaries.value.filter((a) => a.id !== id)
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

.content {
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 16px;
}

/* ── 좌측 폼 ── */
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
.date-row select {
  flex: 1;
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

.btn-row {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}
.save-btn {
  flex: 1;
  background: linear-gradient(135deg, #e91e63, #f48fb1);
  color: white;
  border: none;
  border-radius: 14px;
  padding: 15px;
  font-size: 1.05rem;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.2s;
}
.save-btn:hover:not(:disabled) {
  opacity: 0.9;
}
.save-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
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

/* ── 우측 목록 ── */
.list-card {
  flex: 1;
  background: white;
  border-radius: 18px;
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.list-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 2px solid #fce4ec;
  flex-shrink: 0;
}
.empty {
  text-align: center;
  color: #ccc;
  font-size: 0.88rem;
  margin-top: 24px;
}
.list-scroll {
  flex: 1;
  overflow-y: auto;
}
.list-scroll::-webkit-scrollbar { width: 4px; }
.list-scroll::-webkit-scrollbar-thumb { background: #f48fb1; border-radius: 2px; }

.ann-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 8px;
  border-radius: 12px;
  transition: background 0.15s;
}
.ann-item { cursor: pointer; }
.ann-item:hover { background: #fafafa; }
.ann-item.editing { background: #fff3e0; }
.ann-item.selected { background: #fce4ec; }

.action-btns {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}
.action-edit,
.action-del,
.action-cancel {
  padding: 5px 10px;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: bold;
  cursor: pointer;
  border: none;
  transition: opacity 0.15s;
}
.action-edit {
  background: #e91e63;
  color: white;
}
.action-del {
  background: white;
  color: #e91e63;
  border: 2px solid #f48fb1;
}
.action-cancel {
  background: white;
  color: #aaa;
  border: 2px solid #f0f0f0;
}
.action-edit:hover, .action-del:hover, .action-cancel:hover { opacity: 0.8; }

.ann-badge {
  background: #fff3e0;
  color: #ff9800;
  font-size: 0.85rem;
  font-weight: bold;
  padding: 5px 12px;
  border-radius: 10px;
  white-space: nowrap;
  flex-shrink: 0;
}
.ann-info {
  flex: 1;
  min-width: 0;
}
.ann-title {
  font-size: 0.92rem;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.ann-meta {
  display: flex;
  gap: 6px;
  margin-top: 3px;
  font-size: 0.75rem;
  color: #aaa;
}
.ann-memo {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.del-btn {
  background: none;
  border: none;
  color: #ccc;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 5px 8px;
  border-radius: 8px;
  transition: all 0.2s;
  flex-shrink: 0;
}
.del-btn:hover {
  background: #fce4ec;
  color: #e91e63;
}
</style>
