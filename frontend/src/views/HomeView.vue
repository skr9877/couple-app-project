<template>
  <div class="home">
    <div class="card">
      <div class="heart">💕</div>
      <h1>Couple App</h1>
      <p>함께하는 모든 순간</p>
      <button @click="testApi">백엔드 연동 테스트</button>
      <p v-if="result" class="result">{{ result }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const result = ref('')

async function testApi() {
  try {
    const res = await fetch('http://localhost:8080/api/test')
    const data = await res.json()
    result.value = data.message
  } catch (e) {
    result.value = '연동 실패 - 백엔드 서버를 확인하세요'
  }
}
</script>

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

p {
  color: #888;
  font-size: 1.1rem;
}

button {
  margin-top: 20px;
  padding: 10px 24px;
  background: #e91e63;
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 1rem;
  cursor: pointer;
}

button:hover {
  background: #c2185b;
}

.result {
  margin-top: 16px;
  color: #e91e63;
  font-weight: bold;
}
</style>
