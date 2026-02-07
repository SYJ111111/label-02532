<template>
  <div class="blog-layout">
    <header class="blog-header">
      <div class="header-inner">
        <router-link to="/" class="blog-logo">
          <div class="logo-icon">
            <el-icon :size="20"><EditPen /></el-icon>
          </div>
          <span>个人博客</span>
        </router-link>
        <nav class="header-nav">
          <router-link to="/" class="nav-link">
            <el-icon><HomeFilled /></el-icon>
            首页
          </router-link>
          <template v-if="userStore.token">
            <router-link to="/write" class="nav-link">
              <el-icon><Edit /></el-icon>
              写文章
            </router-link>
            <el-dropdown @command="handleCommand">
              <span class="nav-link user-link">
                <el-avatar :size="28" class="user-avatar">
                  {{ (userStore.userInfo?.nickname || '用')[0] }}
                </el-avatar>
                {{ userStore.userInfo?.nickname || '用户' }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="myArticles">
                    <el-icon><Document /></el-icon>
                    我的文章
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-link">登录</router-link>
            <router-link to="/register" class="nav-btn">
              <el-icon><UserFilled /></el-icon>
              注册
            </router-link>
          </template>
        </nav>
      </div>
    </header>
    <main class="blog-main">
      <router-view />
    </main>
    <footer class="blog-footer">
      <div class="footer-content">
        <p>© 2024 个人博客 · 基于 Spring Boot + Vue 3 构建</p>
      </div>
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
  } else if (cmd === 'myArticles') {
    router.push('/my-articles')
  }
}
</script>

<style lang="scss" scoped>
.blog-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.blog-header {
  flex-shrink: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.25);
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.blog-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  text-decoration: none;
  .logo-icon {
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
  }
}
.header-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nav-link {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  transition: all 0.2s;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border-radius: 10px;
  &:hover {
    background: rgba(255, 255, 255, 0.15);
    color: #fff;
  }
  &.router-link-exact-active {
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
  }
}
.user-link {
  outline: none;
}
.user-avatar {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}
.nav-btn {
  font-size: 14px;
  color: #667eea;
  background: #fff;
  padding: 10px 24px;
  border-radius: 25px;
  text-decoration: none;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  }
}
.blog-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 32px;
}
.blog-footer {
  flex-shrink: 0;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  padding: 32px;
  font-size: 14px;
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

@media screen and (max-width: 640px) {
  .header-inner {
    padding: 0 16px;
    height: 60px;
  }
  .blog-logo {
    font-size: 18px;
    .logo-icon { width: 36px; height: 36px; }
    span { display: none; }
  }
  .header-nav {
    gap: 4px;
  }
  .nav-link {
    padding: 8px 12px;
    font-size: 14px;
  }
  .blog-main {
    padding: 24px 16px;
  }
}
</style>