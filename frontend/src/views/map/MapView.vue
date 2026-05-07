<template>
  <div class="page">
    <div class="header">
      <RouterLink to="/" class="back">← 홈</RouterLink>
      <h1>🍽️ 맛집 지도</h1>
    </div>

    <div class="content">
      <div class="sidebar">
        <div class="search-box">
          <input
            v-model="keyword"
            @keyup.enter="search"
            placeholder="장소 검색..."
            class="search-input"
          />
          <button @click="search" class="search-btn">검색</button>
        </div>

        <div class="tab-bar">
          <button :class="['tab', { active: tab === 'results' }]" @click="tab = 'results'">
            검색결과 {{ searchResults.length > 0 ? `(${searchResults.length})` : '' }}
          </button>
          <button :class="['tab', { active: tab === 'favorites' }]" @click="tab = 'favorites'">
            즐겨찾기 {{ favorites.length > 0 ? `(${favorites.length})` : '' }}
          </button>
        </div>

        <div class="list">
          <template v-if="tab === 'results'">
            <div v-if="searchResults.length === 0" class="empty">검색 결과가 없습니다.</div>
            <div
              v-for="place in searchResults"
              :key="place.id"
              class="item"
              @click="moveToPlace(place.y, place.x)"
            >
              <div class="item-name">{{ place.place_name }}</div>
              <div class="item-addr">{{ place.road_address_name || place.address_name }}</div>
              <button
                :class="['fav-btn', { saved: isFavorite(place.id) }]"
                @click.stop="toggleFavorite(place)"
              >
                {{ isFavorite(place.id) ? '★ 저장됨' : '☆ 즐겨찾기' }}
              </button>
            </div>
          </template>

          <template v-else>
            <div v-if="favorites.length === 0" class="empty">저장된 즐겨찾기가 없습니다.</div>
            <template v-for="region in regionOrder" :key="region">
              <template v-if="groupedFavorites[region]">
                <div class="region-header">{{ region }}</div>
                <div
                  v-for="fav in groupedFavorites[region]"
                  :key="fav.id"
                  class="item"
                  @click="moveToPlace(fav.lat, fav.lng)"
                >
                  <div class="item-name">{{ fav.placeName }}</div>
                  <div class="item-addr">{{ fav.address }}</div>
                  <button class="del-btn" @click.stop="deleteFavorite(fav.id)">✕ 삭제</button>
                </div>
              </template>
            </template>
          </template>
        </div>
      </div>

      <div class="map-wrap">
        <div id="map"></div>
        <button class="my-location-btn" @click="goToMyLocation" title="내 위치로 이동">📍</button>
      </div>
    </div>

    <!-- 지도 클릭 팝업 -->
    <div v-if="clickPopup" class="popup-overlay" @click.self="clickPopup = null">
      <div class="popup">
        <div class="popup-addr">📌 {{ clickPopup.address }}</div>
        <input
          v-model="clickPopup.name"
          placeholder="장소 이름 입력"
          class="popup-input"
          @keyup.enter="saveClickPopup"
        />
        <div class="popup-actions">
          <button class="popup-cancel" @click="clickPopup = null">취소</button>
          <button class="popup-save" @click="saveClickPopup">저장</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const keyword = ref('')
const tab = ref('results')
const searchResults = ref([])
const favorites = ref([])
const clickPopup = ref(null)
let mapInstance = null
const favMarkers = []
let myLocationMarker = null
let tempMarker = null

const API_BASE = 'http://localhost:8080'

const regionOrder = ['서울', '인천', '경기', '강원', '충북', '충남', '대전', '세종', '경북', '경남', '대구', '부산', '울산', '전북', '전남', '광주', '제주', '기타']

const groupedFavorites = computed(() => {
  const groups = {}
  favorites.value.forEach(fav => {
    const r = fav.region || '기타'
    if (!groups[r]) groups[r] = []
    groups[r].push(fav)
  })
  return groups
})

function parseRegion(address) {
  if (!address) return '기타'
  if (address.startsWith('서울')) return '서울'
  if (address.startsWith('인천')) return '인천'
  if (address.startsWith('경기')) return '경기'
  if (address.startsWith('강원')) return '강원'
  if (address.startsWith('충청북') || address.startsWith('충북')) return '충북'
  if (address.startsWith('충청남') || address.startsWith('충남')) return '충남'
  if (address.startsWith('대전')) return '대전'
  if (address.startsWith('세종')) return '세종'
  if (address.startsWith('경상북') || address.startsWith('경북')) return '경북'
  if (address.startsWith('경상남') || address.startsWith('경남')) return '경남'
  if (address.startsWith('대구')) return '대구'
  if (address.startsWith('부산')) return '부산'
  if (address.startsWith('울산')) return '울산'
  if (address.startsWith('전라북') || address.startsWith('전북')) return '전북'
  if (address.startsWith('전라남') || address.startsWith('전남')) return '전남'
  if (address.startsWith('광주')) return '광주'
  if (address.startsWith('제주')) return '제주'
  return '기타'
}

async function loadFavorites() {
  const res = await fetch(`${API_BASE}/api/favorites`)
  favorites.value = await res.json()
  renderFavMarkers()
}

function renderFavMarkers() {
  favMarkers.forEach(m => m.setMap(null))
  favMarkers.length = 0
  if (!mapInstance) return
  favorites.value.forEach(fav => {
    const marker = new window.kakao.maps.Marker({
      map: mapInstance,
      position: new window.kakao.maps.LatLng(fav.lat, fav.lng),
      title: fav.placeName,
    })
    favMarkers.push(marker)
  })
}

function isFavorite(placeId) {
  return favorites.value.some(f => f.placeId === placeId)
}

async function saveFavorite(placeId, placeName, address, lat, lng) {
  await fetch(`${API_BASE}/api/favorites`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      placeId,
      placeName,
      address,
      lat,
      lng,
      region: parseRegion(address),
    }),
  })
  await loadFavorites()
}

async function toggleFavorite(place) {
  if (isFavorite(place.id)) return
  const address = place.road_address_name || place.address_name
  await saveFavorite(place.id, place.place_name, address, parseFloat(place.y), parseFloat(place.x))
}

async function deleteFavorite(id) {
  await fetch(`${API_BASE}/api/favorites/${id}`, { method: 'DELETE' })
  await loadFavorites()
}

async function saveClickPopup() {
  if (!clickPopup.value.name.trim()) return
  const { lat, lng, address, name } = clickPopup.value
  const placeId = `map_${lat}_${lng}`
  await saveFavorite(placeId, name, address, lat, lng)
  if (tempMarker) { tempMarker.setMap(null); tempMarker = null }
  clickPopup.value = null
  tab.value = 'favorites'
}

function search() {
  if (!keyword.value.trim() || !mapInstance) return
  const ps = new window.kakao.maps.services.Places()
  ps.keywordSearch(keyword.value, (data, status) => {
    if (status === window.kakao.maps.services.Status.OK) {
      searchResults.value = data
      tab.value = 'results'
      moveToPlace(data[0].y, data[0].x)
    } else {
      searchResults.value = []
    }
  })
}

function moveToPlace(lat, lng) {
  if (!mapInstance) return
  mapInstance.setCenter(new window.kakao.maps.LatLng(lat, lng))
  mapInstance.setLevel(3)
}

function setMyLocationMarker(lat, lng) {
  if (myLocationMarker) myLocationMarker.setMap(null)
  const image = new window.kakao.maps.MarkerImage(
    'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
    new window.kakao.maps.Size(24, 35)
  )
  myLocationMarker = new window.kakao.maps.Marker({
    map: mapInstance,
    position: new window.kakao.maps.LatLng(lat, lng),
    title: '내 위치',
    image,
  })
}

function goToMyLocation() {
  navigator.geolocation.getCurrentPosition(({ coords }) => {
    moveToPlace(coords.latitude, coords.longitude)
    setMyLocationMarker(coords.latitude, coords.longitude)
  })
}

onMounted(() => {
  const script = document.createElement('script')
  script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_MAP_KEY}&autoload=false&libraries=services`
  script.onload = () => {
    window.kakao.maps.load(() => {
      const container = document.getElementById('map')
      mapInstance = new window.kakao.maps.Map(container, {
        center: new window.kakao.maps.LatLng(37.5665, 126.9780),
        level: 5,
      })

      const geocoder = new window.kakao.maps.services.Geocoder()

      window.kakao.maps.event.addListener(mapInstance, 'click', (mouseEvent) => {
        const latlng = mouseEvent.latLng
        const lat = latlng.getLat()
        const lng = latlng.getLng()

        if (tempMarker) tempMarker.setMap(null)
        tempMarker = new window.kakao.maps.Marker({
          map: mapInstance,
          position: latlng,
        })

        geocoder.coord2Address(lng, lat, (result, status) => {
          if (status === window.kakao.maps.services.Status.OK) {
            const addr = result[0].road_address?.address_name || result[0].address.address_name
            clickPopup.value = { lat, lng, address: addr, name: '' }
          }
        })
      })

      loadFavorites()
      navigator.geolocation.getCurrentPosition(({ coords }) => {
        moveToPlace(coords.latitude, coords.longitude)
        setMyLocationMarker(coords.latitude, coords.longitude)
      })
    })
  }
  document.head.appendChild(script)
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.back {
  text-decoration: none;
  color: #e91e63;
  font-weight: bold;
  background: white;
  padding: 8px 16px;
  border-radius: 20px;
}

h1 {
  color: white;
  font-size: 1.8rem;
}

.content {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.sidebar {
  width: 300px;
  background: white;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.search-box {
  display: flex;
  gap: 8px;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.search-input {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 8px 12px;
  font-size: 0.9rem;
  outline: none;
}

.search-btn {
  background: #e91e63;
  color: white;
  border: none;
  border-radius: 10px;
  padding: 8px 14px;
  cursor: pointer;
  font-size: 0.9rem;
}

.tab-bar {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
}

.tab {
  flex: 1;
  padding: 10px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 0.85rem;
  color: #999;
}

.tab.active {
  color: #e91e63;
  border-bottom: 2px solid #e91e63;
  font-weight: bold;
}

.list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.empty {
  text-align: center;
  color: #bbb;
  padding: 40px 0;
  font-size: 0.9rem;
}

.region-header {
  font-size: 0.75rem;
  font-weight: bold;
  color: #e91e63;
  padding: 8px 12px 4px;
  letter-spacing: 0.5px;
}

.item {
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: background 0.15s;
}

.item:hover {
  background: #fff0f5;
}

.item-name {
  font-weight: bold;
  font-size: 0.95rem;
  margin-bottom: 4px;
}

.item-addr {
  font-size: 0.8rem;
  color: #888;
  margin-bottom: 8px;
}

.fav-btn {
  font-size: 0.8rem;
  padding: 4px 10px;
  border-radius: 20px;
  border: 1px solid #e91e63;
  color: #e91e63;
  background: white;
  cursor: pointer;
}

.fav-btn.saved {
  background: #e91e63;
  color: white;
}

.del-btn {
  font-size: 0.8rem;
  padding: 4px 10px;
  border-radius: 20px;
  border: 1px solid #ccc;
  color: #888;
  background: white;
  cursor: pointer;
}

.map-wrap {
  flex: 1;
  position: relative;
}

#map {
  width: 100%;
  height: 100%;
  border-radius: 20px;
  min-height: calc(100vh - 180px);
}

.my-location-btn {
  position: absolute;
  bottom: 20px;
  right: 20px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: none;
  background: white;
  font-size: 1.4rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
  cursor: pointer;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}

.my-location-btn:hover {
  background: #fff0f5;
}

.popup-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.popup {
  background: white;
  border-radius: 20px;
  padding: 24px;
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.popup-addr {
  font-size: 0.85rem;
  color: #555;
}

.popup-input {
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 0.95rem;
  outline: none;
}

.popup-input:focus {
  border-color: #e91e63;
}

.popup-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.popup-cancel {
  padding: 8px 18px;
  border-radius: 10px;
  border: 1px solid #ddd;
  background: white;
  color: #888;
  cursor: pointer;
}

.popup-save {
  padding: 8px 18px;
  border-radius: 10px;
  border: none;
  background: #e91e63;
  color: white;
  cursor: pointer;
  font-weight: bold;
}
</style>
