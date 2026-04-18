<template>
  <div class="error-page">
    <div class="app-card error-page__card">
      <h1>403</h1>
      <p>当前账号没有访问该页面的权限，请联系管理员或返回工作台。</p>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        <el-button @click="$router.back()">返回上一页</el-button>
      </div>
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
