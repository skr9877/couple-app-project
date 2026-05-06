<template>
  <div class="page">
    <div class="header">
      <RouterLink to="/" class="back">← 홈</RouterLink>
      <h1>🍽️ 맛집 지도</h1>
    </div>
    <div id="map"></div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  const script = document.createElement('script')
  script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_MAP_KEY}&autoload=false`
  script.onload = () => {
    window.kakao.maps.load(() => {
      const container = document.getElementById('map')
      const options = {
        center: new window.kakao.maps.LatLng(37.5665, 126.9780), // 서울 기본값
        level: 5,
      }
      new window.kakao.maps.Map(container, options)
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

#map {
  width: 100%;
  height: calc(100vh - 140px);
  border-radius: 20px;
}
</style>
