<template>
  <div class="article-write">
    <div class="write-header">
      <h2>{{ isEdit ? '编辑文章' : '写文章' }}</h2>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
      <el-form-item label="文章标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="200" show-word-limit />
      </el-form-item>

      <el-form-item label="文章分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="请选择分类" clearable style="width: 100%">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="文章摘要">
        <el-input v-model="form.summary" type="textarea" :rows="3" 
                  placeholder="请输入文章摘要（选填，不填则自动截取正文前200字）" 
                  maxlength="500" show-word-limit />
      </el-form-item>

      <el-form-item label="封面图片">
        <el-input v-model="form.coverImage" placeholder="请输入封面图片URL（选填）" />
      </el-form-item>

      <el-form-item label="文章内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="18" placeholder="请输入文章内容" />
      </el-form-item>

      <el-form-item>
        <div class="form-actions">
          <el-button type="primary" :loading="loading" round @click="handleSave">
            <el-icon><Document /></el-icon> 保存草稿
          </el-button>
          <el-button type="success" :loading="loading" round @click="handleSaveAndSubmit">
            <el-icon><Upload /></el-icon> 保存并提交审核
          </el-button>
          <el-button round @click="$router.back()">取消</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleDetail, saveArticle, submitArticle } from '@/api/article'
import { getCategories } from '@/api/public'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const categories = ref([])
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: null
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
}

onMounted(async () => {
  const catRes = await getCategories()
  categories.value = catRes.data

  if (route.params.id) {
    try {
      const res = await getArticleDetail(route.params.id)
      Object.assign(form, {
        id: res.data.id,
        title: res.data.title,
        summary: res.data.summary,
        content: res.data.content,
        coverImage: res.data.coverImage,
        categoryId: res.data.categoryId
      })
    } catch (e) {
      ElMessage.error('文章不存在或无权访问')
      router.push('/my-articles')
    }
  }
})

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await saveArticle(form)
    ElMessage.success('保存成功')
    router.push('/my-articles')
  } finally {
    loading.value = false
  }
}

async function handleSaveAndSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await saveArticle(form)
    const articleId = form.id || res.data
    await submitArticle(articleId)
    ElMessage.success('已提交审核')
    router.push('/my-articles')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.article-write {
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
  padding: 40px 48px;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}
.write-header {
  margin-bottom: 32px;
  h2 {
    font-size: 28px;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
  }
}
.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
}
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #334155;
}
:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 12px;
}
:deep(.el-select) {
  .el-input__wrapper {
    border-radius: 12px;
  }
}
</style>