<template>
  <div class="error-page">
    <div class="app-card error-page__card">
      <h1>404</h1>
      <p>页面不存在，可能是路由地址错误或该页面已被移除。</p>
      <el-button type="primary" @click="$router.push('/')">回到首页</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.error-page {
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 24px;
}

.error-page__card {
  max-width: 520px;
  padding: 40px;
  text-align: center;
}

.error-page__card h1 {
  margin: 0 0 12px;
  font-size: 72px;
}

.error-page__card p {
  margin: 0 0 24px;
  color: $app-sub-text-color;
}
</style>
