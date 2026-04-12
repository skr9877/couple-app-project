<template>
  <div class="home">
    <div class="card">
      <div class="heart">💕</div>
      <h1>Couple App</h1>
      <p class="subtitle">함께하는 모든 순간</p>

      <div class="menu">
        <RouterLink to="/photos" class="menu-item">
          <span class="icon">📷</span>
          <span>사진</span>
        </RouterLink>
        <RouterLink to="/calendar" class="menu-item">
          <span class="icon">📅</span>
          <span>달력</span>
        </RouterLink>
        <RouterLink to="/map" class="menu-item">
          <span class="icon">🍽️</span>
          <span>맛집 지도</span>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
}

.card {
  text-align: center;
  background: white;
  border-radius: 20px;
  padding: 60px 80px;
  box-shadow: 0 20px 60px rgba(255, 100, 130, 0.2);
}

.heart {
  font-size: 64px;
  margin-bottom: 20px;
}

h1 {
  color: #e91e63;
  font-size: 2rem;
  margin-bottom: 10px;
}

.subtitle {
  color: #888;
  font-size: 1.1rem;
  margin-bottom: 40px;
}

.menu {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  background: #fff0f5;
  border: 2px solid #f8bbd0;
  border-radius: 16px;
  padding: 24px 32px;
  color: #e91e63;
  font-size: 1rem;
  font-weight: bold;
  transition: all 0.2s;
}

.menu-item:hover {
  background: #e91e63;
  color: white;
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(233, 30, 99, 0.3);
}

.icon {
  font-size: 2rem;
}
</style>
