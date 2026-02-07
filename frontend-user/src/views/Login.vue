<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-icon">
          <el-icon :size="32"><User /></el-icon>
        </div>
        <h2>欢迎回来</h2>
        <p>登录您的账号继续</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" native-type="submit" class="submit-btn">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.auth-page {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}
.auth-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
  padding: 48px 40px;
}
.auth-header {
  text-align: center;
  margin-bottom: 36px;
  .auth-icon {
    width: 72px;
    height: 72px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    color: #fff;
  }
  h2 {
    font-size: 26px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 8px;
  }
  p {
    font-size: 15px;
    color: #64748b;
  }
}
.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  &:hover {
    opacity: 0.9;
  }
}
.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #64748b;
  a {
    color: #667eea;
    font-weight: 600;
    margin-left: 4px;
    &:hover { text-decoration: underline; }
  }
}
:deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 4px 16px;
}
</style>