<template>
  <div class="page">
    <div class="header">
      <RouterLink to="/" class="back">← 홈</RouterLink>
      <h1>📷 사진</h1>
    </div>
    <div class="content">
      <p>사진 기능 준비 중</p>
    </div>
  </div>
</template>

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
  margin-bottom: 40px;
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
  background: white;
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  color: #888;
}
</style>
