<template>
  <section class="page-container">
    <header v-if="title || $slots.extra" class="page-container__header">
      <div>
        <h2 class="page-container__title">{{ title }}</h2>
        <p v-if="description" class="page-container__description">{{ description }}</p>
      </div>
      <div class="page-container__extra">
        <slot name="extra" />
      </div>
    </header>
    <slot />
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title?: string
  description?: string
}>()
</script>

<style scoped lang="scss">
.page-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-container__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 4px 2px;
}

.page-container__title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.page-container__description {
  margin: 8px 0 0;
  color: $app-text-secondary;
  font-size: 14px;
}

.page-container__extra {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
