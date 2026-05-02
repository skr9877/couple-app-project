<template>
  <div class="page">
    <div class="header">
      <RouterLink to="/" class="back">← 홈</RouterLink>
      <h1>📷 앨범</h1>
    </div>

    <!-- 필터 바 (독립 고정 영역) -->
    <div class="filter-bar">
      <button class="month-selector" @click="showPicker = !showPicker">
        📅 {{ filterLabel }}
        <span class="picker-arrow" :class="{ open: showPicker }">▾</span>
      </button>
      <button
        class="all-btn"
        :class="{ active: filterKey === null }"
        @click="filterKey = null; showPicker = false"
      >전체보기</button>
    </div>

    <!-- 월 선택 피커 -->
    <div v-if="showPicker" class="picker-overlay" @click.self="showPicker = false">
      <div class="picker-box">
        <div class="picker-year-nav">
          <button class="year-nav-btn" @click="pickerYear--">‹</button>
          <span class="picker-year-label">{{ pickerYear }}년</span>
          <button class="year-nav-btn" @click="pickerYear++">›</button>
        </div>
        <div class="picker-months">
          <button
            v-for="m in 12"
            :key="m"
            class="picker-month-btn"
            :class="{
              active: filterKey === monthKey(pickerYear, m),
              'has-photos': hasPhotosIn(pickerYear, m),
            }"
            @click="selectMonth(m)"
          >{{ m }}월</button>
        </div>
      </div>
    </div>

    <!-- 앨범 그리드 (스크롤 영역) -->
    <div class="album-section">
      <div v-if="filteredGroups.length === 0" class="empty-msg">
        <div class="empty-icon">🖼️</div>
        <p>사진이 없어요</p>
        <p class="empty-sub">아래 버튼으로 추가해보세요</p>
      </div>

      <template v-else>
        <div v-for="group in filteredGroups" :key="group.key" class="month-group">
          <div v-if="filterKey === null" class="month-header">{{ group.label }}</div>
          <div class="photo-grid">
            <div
              v-for="(photo, i) in group.photos"
              :key="i"
              class="photo-card"
              @click="openPhoto(photo)"
            >
              <img :src="photoUrl(photo)" :alt="photoTitle(photo)" />
              <div class="photo-overlay">
                <span class="photo-title">{{ photoTitle(photo) }}</span>
                <span class="photo-date">{{ photoDate(photo) }}</span>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 사진 등록 (고정 하단) -->
    <div class="upload-section">
      <div class="upload-info">
        <span class="upload-count">{{ filteredCount }}장</span>
        <span class="upload-label">의 추억이 있어요 💕</span>
      </div>
      <label class="upload-btn">
        <input
          type="file"
          accept="image/*"
          multiple
          hidden
          @change="handleUpload"
        />
        <span>📸 사진 추가</span>
      </label>
    </div>

    <!-- 사진 뷰어 -->
    <div v-if="viewerPhoto" class="viewer" @click.self="viewerPhoto = null">
      <div class="viewer-box">
        <button class="viewer-close" @click="viewerPhoto = null">✕</button>
        <img :src="photoUrl(viewerPhoto)" :alt="photoTitle(viewerPhoto)" />
        <div class="viewer-info">
          <span class="viewer-title">{{ photoTitle(viewerPhoto) }}</span>
          <span class="viewer-date">{{ photoDate(viewerPhoto) }}</span>
          <button class="viewer-delete" @click="deletePhoto(viewerPhoto)">🗑 삭제</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'

const photos = ref([])
const viewerPhoto = ref(null)
const filterKey = ref(null)
const showPicker = ref(false)
const pickerYear = ref(new Date().getFullYear())

async function fetchPhotos() {
  const url = filterKey.value ? `/api/photos?month=${filterKey.value}` : '/api/photos'
  const res = await fetch(url)
  photos.value = await res.json()
}

onMounted(fetchPhotos)
watch(filterKey, fetchPhotos)

function photoUrl(photo) {
  return `/files/image/${photo.yearMonth}/${photo.storedFilename}`
}

function photoTitle(photo) {
  return photo.originalFilename.replace(/\.[^/.]+$/, '')
}

function photoDate(photo) {
  return new Date(photo.uploadDate).toLocaleDateString('ko-KR')
}

function monthKey(year, month) {
  return `${year}-${String(month).padStart(2, '0')}`
}

function hasPhotosIn(year, month) {
  return photos.value.some((p) => p.monthKey === monthKey(year, month))
}

function selectMonth(m) {
  filterKey.value = monthKey(pickerYear.value, m)
  showPicker.value = false
}

const filterLabel = computed(() => {
  if (!filterKey.value) return '전체 보기'
  const [y, m] = filterKey.value.split('-')
  return `${y}년 ${Number(m)}월`
})

const filteredGroups = computed(() => {
  const map = {}
  photos.value.forEach((photo) => {
    const key = photo.monthKey || 'unknown'
    if (!map[key]) map[key] = { key, label: photo.monthLabel || key, photos: [] }
    map[key].photos.push(photo)
  })
  return Object.values(map).sort((a, b) => b.key.localeCompare(a.key))
})

const filteredCount = computed(() =>
  filteredGroups.value.reduce((sum, g) => sum + g.photos.length, 0)
)

async function handleUpload(e) {
  const files = Array.from(e.target.files)
  for (const file of files) {
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetch('/api/photos', { method: 'POST', body: formData })
    const saved = await res.json()
    photos.value.unshift(saved)
  }
  e.target.value = ''
}

function openPhoto(photo) {
  viewerPhoto.value = photo
}

async function deletePhoto(photo) {
  if (!confirm('사진을 삭제할까요?')) return
  await fetch(`/api/photos/${photo.id}`, { method: 'DELETE' })
  photos.value = photos.value.filter((p) => p.id !== photo.id)
  viewerPhoto.value = null
}
</script>

<style scoped>
.page {
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
  padding: 20px 24px 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
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
h1 { color: white; font-size: 1.5rem; margin: 0; }

/* ── 필터 바 ── */
.filter-bar {
  flex-shrink: 0;
  background: white;
  border-radius: 14px 14px 0 0;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid #f0f0f0;
  z-index: 10;
}

.month-selector {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #fce4ec;
  border: none;
  border-radius: 10px;
  padding: 8px 14px;
  font-size: 0.95rem;
  font-weight: bold;
  color: #e91e63;
  cursor: pointer;
  transition: background 0.2s;
}
.month-selector:hover { background: #f8bbd0; }
.picker-arrow {
  font-size: 0.8rem;
  transition: transform 0.2s;
  display: inline-block;
}
.picker-arrow.open { transform: rotate(180deg); }

.all-btn {
  background: none;
  border: 1.5px solid #f0f0f0;
  border-radius: 10px;
  padding: 7px 14px;
  font-size: 0.88rem;
  color: #aaa;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}
.all-btn.active {
  border-color: #e91e63;
  background: #fce4ec;
  color: #e91e63;
  font-weight: bold;
}
.all-btn:hover { border-color: #e91e63; color: #e91e63; }

/* ── 피커 오버레이 ── */
.picker-overlay {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  padding: 155px 24px 0;
}

.picker-box {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(233, 30, 99, 0.2);
  padding: 16px;
  width: 260px;
}

.picker-year-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.picker-year-label { font-size: 1.1rem; font-weight: bold; color: #333; }
.year-nav-btn {
  background: #fce4ec; border: none; border-radius: 50%;
  width: 32px; height: 32px; font-size: 1.2rem; color: #e91e63;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: background 0.2s;
}
.year-nav-btn:hover { background: #f48fb1; color: white; }

.picker-months {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 6px;
}
.picker-month-btn {
  padding: 10px 0;
  border: 1.5px solid #f0f0f0;
  border-radius: 10px;
  background: white;
  font-size: 0.88rem;
  color: #555;
  cursor: pointer;
  transition: all 0.15s;
  font-weight: 500;
}
.picker-month-btn:hover { border-color: #f48fb1; color: #e91e63; }
.picker-month-btn.has-photos { background: #fff5f8; border-color: #f48fb1; color: #e91e63; }
.picker-month-btn.active {
  background: #e91e63;
  border-color: #e91e63;
  color: white;
  font-weight: bold;
}

/* ── 앨범 그리드 (스크롤) ── */
.album-section {
  flex: 1;
  min-height: 0;
  background: white;
  overflow-y: auto;
  padding: 16px 20px;
}
.album-section::-webkit-scrollbar { width: 5px; }
.album-section::-webkit-scrollbar-thumb { background: #f48fb1; border-radius: 3px; }
.album-section::-webkit-scrollbar-track { background: transparent; }

.empty-msg {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #ccc;
  gap: 8px;
}
.empty-icon { font-size: 3.5rem; }
.empty-msg p { margin: 0; font-size: 1rem; }
.empty-sub { font-size: 0.82rem !important; color: #ddd; }

.month-group { margin-bottom: 20px; }

.month-header {
  font-size: 1rem;
  font-weight: bold;
  color: #e91e63;
  padding: 0 0 10px;
  border-bottom: 2px solid #fce4ec;
  margin-bottom: 12px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  grid-auto-rows: calc((100vh - 320px) / 3);
  gap: 6px;
}

.photo-card {
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  background: #f5f5f5;
}
.photo-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.2s;
}
.photo-card:hover img { transform: scale(1.05); }

.photo-overlay {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.5));
  padding: 20px 10px 8px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.2s;
}
.photo-card:hover .photo-overlay { opacity: 1; }
.photo-title {
  color: white; font-size: 0.78rem; font-weight: bold;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.photo-date { color: rgba(255,255,255,0.7); font-size: 0.68rem; }

/* ── 사진 등록 (고정 하단) ── */
.upload-section {
  flex-shrink: 0;
  background: white;
  border-top: 1px solid #f0f0f0;
  padding: 16px 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.upload-info { display: flex; align-items: baseline; gap: 4px; }
.upload-count { font-size: 1.4rem; font-weight: bold; color: #e91e63; }
.upload-label { font-size: 0.85rem; color: #aaa; }
.upload-btn {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  background: linear-gradient(135deg, #e91e63, #f48fb1);
  color: white; border-radius: 14px; padding: 16px;
  font-size: 1.05rem; font-weight: bold; cursor: pointer;
  transition: opacity 0.2s; width: 100%; box-sizing: border-box;
}
.upload-btn:hover { opacity: 0.9; }

/* ── 뷰어 ── */
.viewer {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.75);
  display: flex; align-items: center; justify-content: center;
  z-index: 200;
}
.viewer-box {
  background: white; border-radius: 20px; overflow: hidden;
  max-width: 80vw; max-height: 85vh;
  display: flex; flex-direction: column; position: relative;
}
.viewer-close {
  position: absolute; top: 12px; right: 12px;
  background: rgba(0,0,0,0.4); border: none; color: white;
  width: 32px; height: 32px; border-radius: 50%; font-size: 1rem;
  cursor: pointer; z-index: 1; display: flex; align-items: center; justify-content: center;
}
.viewer-box img { max-height: 70vh; width: 100%; object-fit: contain; background: #111; }
.viewer-info { padding: 14px 18px; display: flex; align-items: center; gap: 10px; }
.viewer-title { font-size: 0.95rem; font-weight: bold; color: #222; flex: 1; }
.viewer-date { font-size: 0.78rem; color: #aaa; }
.viewer-delete {
  background: none; border: 1.5px solid #ffcdd2;
  color: #ef5350; border-radius: 10px;
  padding: 6px 14px; font-size: 0.82rem;
  cursor: pointer; transition: background 0.2s;
}
.viewer-delete:hover { background: #ffebee; }
</style>
