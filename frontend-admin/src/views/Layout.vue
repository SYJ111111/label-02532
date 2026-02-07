<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-area">
        <el-icon :size="28" color="#fff"><EditPen /></el-icon>
        <span v-show="!isCollapse" class="logo-text">博客后台</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
        background-color="#1e1e2d"
        text-color="#a2a3b7"
        active-text-color="#667eea"
        class="aside-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/article">
          <el-icon><Document /></el-icon>
          <template #title>文章审核</template>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><FolderOpened /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/log">
          <el-icon><Tickets /></el-icon>
          <template #title>操作日志</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse" :size="20">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="page-title">{{ route.meta.title || '首页' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :style="{ backgroundColor: '#667eea' }">
                {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || 'U')[0] }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const checkWidth = () => {
  isCollapse.value = window.innerWidth < 768
}

onMounted(async () => {
  checkWidth()
  window.addEventListener('resize', checkWidth)
  try {
    await userStore.getInfo()
  } catch {
    router.push('/login')
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', checkWidth)
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  min-height: 100vh;
}
.layout-aside {
  background-color: #1e1e2d;
  transition: width 0.3s;
  overflow-x: hidden;
  overflow-y: auto;
}
.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}
.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  white-space: nowrap;
}
.aside-menu {
  border-right: none;
  overflow: hidden !important;
}
.layout-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  padding: 0 24px;
  height: 60px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  cursor: pointer;
  color: #606266;
  &:hover { color: #667eea; }
}
.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}
.username {
  font-size: 14px;
}
.layout-main {
  background-color: #f0f2f5;
  padding: 24px;
}

@media screen and (max-width: 768px) {
  .layout-header {
    padding: 0 12px;
  }
  .layout-main {
    padding: 16px;
  }
  .username {
    display: none;
  }
}
</style>
