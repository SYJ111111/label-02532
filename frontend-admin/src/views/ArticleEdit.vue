<template>
  <div class="page-container">
    <div class="content-card">
      <div class="edit-header">
        <h3>{{ isEdit ? '编辑文章' : '写文章' }}</h3>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" size="large">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" clearable style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入文章摘要（选填）" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="封面">
          <el-input v-model="form.coverImage" placeholder="请输入封面图片URL（选填）" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="16" placeholder="请输入文章内容" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSave('PUBLISHED')">
            <el-icon><Upload /></el-icon> 发布
          </el-button>
          <el-button :loading="loading" @click="handleSave('DRAFT')">
            <el-icon><Document /></el-icon> 保存草稿
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleDetail, saveArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const categories = ref([])
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  id: null, title: '', summary: '', content: '', coverImage: '', categoryId: null, status: 'DRAFT'
})
const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
}

onMounted(async () => {
  const catRes = await getCategoryList()
  categories.value = catRes.data
  if (route.params.id) {
    const res = await getArticleDetail(route.params.id)
    Object.assign(form, {
      id: res.data.id, title: res.data.title, summary: res.data.summary,
      content: res.data.content, coverImage: res.data.coverImage,
      categoryId: res.data.categoryId, status: res.data.status
    })
  }
  nextTick(() => {
    formRef.value?.clearValidate()
  })
})

async function handleSave(status) {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await saveArticle({ ...form, status })
    ElMessage.success(status === 'PUBLISHED' ? '发布成功' : '保存成功')
    router.push('/article')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.edit-header {
  margin-bottom: 24px;
  h3 { font-size: 20px; font-weight: 600; color: #303133; }
}
</style>
