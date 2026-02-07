<template>
  <div class="page-container">
    <div class="content-card" style="max-width: 600px;">
      <h3 class="card-title">个人信息</h3>
      <el-form :model="form" label-width="80px" size="large" v-loading="loading">
        <el-form-item label="用户名">
          <el-input :value="form.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="form.role === 'ADMIN' ? 'danger' : 'primary'">{{ form.role === 'ADMIN' ? '管理员' : '普通用户' }}</el-tag>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProfile, updateProfile } from '@/api/user'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const form = reactive({ username: '', nickname: '', email: '', role: '' })

onMounted(async () => {
  loading.value = true
  try {
    const res = await getProfile()
    Object.assign(form, res.data)
  } finally {
    loading.value = false
  }
})

async function handleSave() {
  saving.value = true
  try {
    await updateProfile({ nickname: form.nickname, email: form.email })
    ElMessage.success('保存成功')
    await userStore.getInfo()
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
}
</style>
