<template>
  <div class="blog-layout">
    <header class="blog-header">
      <div class="header-inner">
        <router-link to="/" class="blog-logo">
          <el-icon :size="24"><EditPen /></el-icon>
          <span>个人博客</span>
        </router-link>
        <nav class="header-nav">
          <router-link to="/" class="nav-link">首页</router-link>
          <template v-if="userStore.token">
            <el-dropdown @command="handleCommand">
              <span class="nav-link user-link">
                <el-icon><User /></el-icon>
                {{ userStore.userInfo?.nickname || '用户' }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-link">登录</router-link>
            <router-link to="/register" class="nav-btn">注册</router-link>
          </template>
        </nav>
      </div>
    </header>
    <main class="blog-main">
      <router-view />
    </main>
    <footer class="blog-footer">
      <p>&copy; 2024 个人博客 · 基于 Spring Boot + Vue 3 构建</p>
    </footer>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

onMounted(async () => {
  if (userStore.token) {
    try {
      await userStore.getInfo()
    } catch {
      userStore.logout()
    }
  }
})

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  }
}
</script>

<style lang="scss" scoped>
.blog-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.blog-header {
  background: #fff;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.blog-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
  color: #2d8cf0;
  text-decoration: none;
}
.header-nav {
  display: flex;
  align-items: center;
  gap: 20px;
}
.nav-link {
  font-size: 15px;
  color: #606266;
  text-decoration: none;
  transition: color 0.2s;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover, &.router-link-exact-active { color: #2d8cf0; }
}
.user-link {
  outline: none;
}
.nav-btn {
  font-size: 14px;
  color: #fff;
  background: #2d8cf0;
  padding: 6px 20px;
  border-radius: 20px;
  text-decoration: none;
  transition: background 0.2s;
  &:hover { background: #57a3f3; }
}
.blog-main {
  flex: 1;
  max-width: 1100px;
  width: 100%;
  margin: 0 auto;
  padding: 32px 24px;
}
.blog-footer {
  background: #2c3e50;
  color: #a0aec0;
  text-align: center;
  padding: 24px;
  font-size: 14px;
  margin-top: auto;
}

@media screen and (max-width: 640px) {
  .header-inner {
    padding: 0 16px;
  }
  .blog-logo span {
    display: none;
  }
  .header-nav {
    gap: 12px;
  }
  .blog-main {
    padding: 16px;
  }
}
</style>
