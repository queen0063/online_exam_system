<template>
  <div class="landing">
    <nav class="landing__nav">
      <div class="landing__nav-inner">
        <div class="landing__nav-brand">
          <img class="landing__logo" src="/logo.png" alt="Logo" />
          <span>在线考试系统</span>
        </div>
        <div class="landing__nav-actions">
          <el-button text @click="go('/login')">登录</el-button>
          <el-button type="primary" @click="go('/register')">注册</el-button>
        </div>
      </div>
    </nav>

    <section class="landing__hero">
      <div class="landing__hero-badge">Online Exam System</div>
      <h1 class="landing__hero-title">在线考试系统</h1>
      <p class="landing__hero-desc">
        覆盖题库、试卷、考试、答题、阅卷与成绩全过程的一站式解决方案。<br>
        支持管理员、教师、学生三类角色，让考试管理更高效。
      </p>
      <div class="landing__hero-actions">
        <el-button type="primary" size="large" @click="go('/login')">
          立即登录
        </el-button>
        <el-button size="large" @click="go('/register')">
          注册账号
        </el-button>
      </div>
    </section>

    <section class="landing__roles">
      <h2 class="landing__section-title">三种角色，各尽其能</h2>
      <div class="landing__role-grid">
        <div
          v-for="role in ROLES"
          :key="role.name"
          class="landing__role-card app-card card-hoverable"
        >
          <div class="landing__role-icon" :style="{ background: role.bg }">
            <el-icon :size="28"><app-icon :name="role.icon" /></el-icon>
          </div>
          <h3>{{ role.name }}</h3>
          <ul>
            <li v-for="feat in role.features" :key="feat">{{ feat }}</li>
          </ul>
        </div>
      </div>
    </section>

    <section class="landing__stats">
      <div v-for="stat in STATS" :key="stat.label" class="landing__stat">
        <div class="landing__stat-value">{{ stat.value }}</div>
        <div class="landing__stat-label">{{ stat.label }}</div>
      </div>
    </section>

    <footer class="landing__footer">
      <span>&copy; 在线考试系统</span>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import AppIcon from '@/components/common/AppIcon.vue'

const router = useRouter()

function go(path: string) {
  router.push(path)
}

const ROLES = [
  {
    name: '管理员',
    icon: 'Setting',
    bg: 'rgba(15, 108, 189, 0.1)',
    features: ['用户与角色管理', '科目与班级维护', '试卷与考试统筹', '全局成绩统计', '系统公告管理']
  },
  {
    name: '教师',
    icon: 'EditPen',
    bg: 'rgba(16, 124, 16, 0.1)',
    features: ['题库题目管理', '灵活组卷出卷', '安排考试计划', '在线阅卷打分', '成绩数据分析']
  },
  {
    name: '学生',
    icon: 'Reading',
    bg: 'rgba(156, 93, 11, 0.1)',
    features: ['在线参加考试', '实时查看成绩', '错题自动收录', '公告及时查看', '个人中心管理']
  }
]

const STATS = [
  { value: '5', label: '类题型' },
  { value: '3', label: '种角色' },
  { value: '全流程', label: '业务覆盖' },
  { value: '一站式', label: '解决方案' }
]
</script>

<style scoped lang="scss">
.landing {
  min-height: 100vh;
  background:
    radial-gradient(circle at 30% 20%, rgba(15, 108, 189, 0.08), transparent 40%),
    radial-gradient(circle at 80% 80%, rgba(139, 92, 246, 0.04), transparent 35%),
    linear-gradient(180deg, #f7f9fc 0%, #f3f6f9 100%);
}

// --- Navigation ---

.landing__nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 50;
  background: $app-surface-overlay;
  border-bottom: 1px solid $app-border-subtle;
  backdrop-filter: blur(20px) saturate(140%);
  -webkit-backdrop-filter: blur(20px) saturate(140%);
}

.landing__nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1120px;
  margin: 0 auto;
  padding: 0 24px;
  height: 56px;
}

.landing__nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: $app-text-color;
}

.landing__logo {
  width: 34px;
  height: 34px;
  border-radius: $radius-lg;
  object-fit: contain;
}

.landing__nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

// --- Hero ---

.landing__hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 140px 24px 80px;
}

.landing__hero-badge {
  display: inline-flex;
  padding: 6px 14px;
  border-radius: $radius-pill;
  background: $app-primary-light;
  color: $app-primary;
  font-size: 13px;
  letter-spacing: 0.04em;
  margin-bottom: 20px;
}

.landing__hero-title {
  margin: 0;
  font-size: 40px;
  font-weight: 700;
  line-height: 1.2;
  color: $app-text-color;
  background: linear-gradient(135deg, $app-text-color, $app-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.landing__hero-desc {
  margin: 16px 0 0;
  max-width: 560px;
  font-size: 16px;
  line-height: 1.8;
  color: $app-text-secondary;
}

.landing__hero-actions {
  display: flex;
  gap: 12px;
  margin-top: 36px;
}

// --- Roles ---

.landing__roles {
  max-width: 1120px;
  margin: 0 auto;
  padding: 40px 24px 60px;
}

.landing__section-title {
  text-align: center;
  margin: 0 0 32px;
  font-size: 26px;
  font-weight: 600;
  color: $app-text-color;
}

.landing__role-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.landing__role-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 32px 24px;
}

.landing__role-card h3 {
  margin: 16px 0 14px;
  font-size: 18px;
  font-weight: 600;
  color: $app-text-color;
}

.landing__role-card ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.landing__role-card li {
  font-size: 14px;
  color: $app-text-secondary;
}

.landing__role-card li::before {
  content: '· ';
  color: $app-primary;
  font-weight: 700;
}

.landing__role-icon {
  display: grid;
  width: 56px;
  height: 56px;
  place-items: center;
  border-radius: $radius-xl;
  color: $app-primary;
}

// --- Stats ---

.landing__stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  max-width: 1120px;
  margin: 0 auto;
  padding: 40px 24px;
}

.landing__stat-value {
  font-size: 36px;
  font-weight: 800;
  color: $app-primary;
  line-height: 1;
}

.landing__stat-label {
  margin-top: 8px;
  font-size: 14px;
  color: $app-text-secondary;
}

// --- Footer ---

.landing__footer {
  text-align: center;
  padding: 40px 24px 32px;
  font-size: 13px;
  color: $app-text-tertiary;
}

// --- Responsive ---

@media (max-width: 768px) {
  .landing__hero-title {
    font-size: 28px;
  }

  .landing__hero-desc {
    font-size: 14px;
  }

  .landing__role-grid {
    grid-template-columns: 1fr;
  }

  .landing__stats {
    gap: 32px;
    flex-wrap: wrap;
  }

  .landing__stat-value {
    font-size: 28px;
  }
}
</style>
