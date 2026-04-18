<template>
  <component :is="iconComponent" />
</template>

<script setup lang="ts">
import {
  AlarmClock,
  Avatar,
  Bell,
  Calendar,
  Checked,
  CollectionTag,
  DataBoard,
  Document,
  Edit,
  EditPen,
  Finished,
  Fold,
  Histogram,
  House,
  Management,
  Medal,
  Memo,
  Menu,
  Notebook,
  Notification,
  Reading,
  Setting,
  Tickets,
  Timer,
  TrendCharts,
  User,
  UserFilled,
  Warning
} from '@element-plus/icons-vue'
import { computed } from 'vue'

const props = defineProps<{
  name?: string
}>()

const iconMap = {
  AlarmClock,
  Avatar,
  Bell,
  Calendar,
  Checked,
  CollectionTag,
  DataBoard,
  Document,
  Edit,
  EditPen,
  Finished,
  Fold,
  Histogram,
  House,
  Management,
  Medal,
  Memo,
  Menu,
  Notebook,
  Notification,
  Reading,
  Setting,
  Tickets,
  Timer,
  TrendCharts,
  User,
  UserFilled,
  Warning
}

const iconComponent = computed(() => iconMap[props.name as keyof typeof iconMap] || Menu)
</script>
