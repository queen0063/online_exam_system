<template>
  <div class="app-card query-bar">
    <div class="query-bar__content">
      <slot />
    </div>
    <div v-if="$slots.actions" class="query-bar__actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.query-bar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
}

.query-bar__content {
  flex: 1;
}

.query-bar__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

@media (max-width: 960px) {
  .query-bar {
    flex-direction: column;
  }

  .query-bar__actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
