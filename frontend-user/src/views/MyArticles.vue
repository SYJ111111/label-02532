<template>
  <div class="my-articles">
    <div class="page-header">
      <h2>我的文章</h2>
      <el-button type="primary" round @click="$router.push('/write')">
        <el-icon><EditPen /></el-icon> 写文章
      </el-button>
    </div>

    <div class="filter-bar">
      <el-radio-group v-model="query.status" @change="loadData">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="DRAFT">草稿</el-radio-button>
        <el-radio-button label="PENDING">审核中</el-radio-button>
        <el-radio-button label="PUBLISHED">已发布</el-radio-button>
        <el-radio-button label="REJECTED">未通过</el-radio-button>
      </el-radio-group>
    </div>

    <div class="article-list" v-loading="loading">
      <div v-if="articles.length === 0" class="empty-tip">
        <el-empty description="暂无文章" :image-size="120" />
      </div>
      <div v-else class="article-item" v-for="item in articles" :key="item.id">
        <div class="article-info">
          <h3 class="article-title" @click="handleView(item)">{{ item.title }}</h3>
          <p class="article-summary">{{ item.summary || '暂无摘要' }}</p>
          <div class="article-meta">
            <span class="category" v-if="item.categoryName">{{ item.categoryName }}</span>
            <span class="time"><el-icon><Calendar /></el-icon> {{ formatTime(item.createdAt) }}</span>
            <span class="views"><el-icon><View /></el-icon> {{ item.viewCount || 0 }}</span>
            <el-tag :type="statusType(item.status)" size="small" effect="plain">{{ statusText(item.status) }}</el-tag>
          </div>
        </div>
        <div class="article-actions">
          <el-button size="small" type="primary" plain round @click="$router.push(`/write/${item.id}`)">
            编辑
          </el-button>
          <el-button v-if="item.status === 'DRAFT' || item.status === 'REJECTED'" 
                     size="small" type="success" plain round @click="handleSubmit(item)">
            提交审核
          </el-button>
          <el-button size="small" type="danger" plain round @click="handleDelete(item)">删除</el-button>
        </div>
      </div>
    </div>

    <div class="pagination-wrapper" v-if="total > query.pageSize">
      <el-pagination
        v-model:current-page="query.pageNum"
        :page-size="query.pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadData"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyArticles, submitArticle, deleteArticle } from '@/api/article'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const articles = ref([])
const total = ref(0)
const query = reactive({ status: '', pageNum: 1, pageSize: 10 })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getMyArticles(query)
    articles.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleView(item) {
  if (item.status === 'PUBLISHED') {
    router.push(`/article/${item.id}`)
  } else {
    router.push(`/write/${item.id}`)
  }
}

async function handleSubmit(item) {
  await ElMessageBox.confirm('确定要提交审核吗？', '提示')
  await submitArticle(item.id)
  ElMessage.success('已提交审核')
  loadData()
}

async function handleDelete(item) {
  await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', { type: 'warning' })
  await deleteArticle(item.id)
  ElMessage.success('删除成功')
  loadData()
}

function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

function statusType(status) {
  const map = { DRAFT: 'info', PENDING: 'warning', PUBLISHED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}

function statusText(status) {
  const map = { DRAFT: '草稿', PENDING: '审核中', PUBLISHED: '已发布', REJECTED: '未通过' }
  return map[status] || status
}
</script>

<style lang="scss" scoped>
.my-articles {
  max-width: 960px;
  margin: 0 auto;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  h2 {
    font-size: 28px;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
  }
}
.filter-bar {
  margin-bottom: 24px;
  :deep(.el-radio-group) {
    display: flex;
    gap: 12px;
  }
  :deep(.el-radio-button__inner) {
    border-radius: 20px !important;
    border: none !important;
    padding: 10px 20px;
  }
  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
  }
}
.article-list {
  min-height: 200px;
}
.article-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  border: 1px solid transparent;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    border-color: #e0e7ff;
  }
}
.article-info {
  flex: 1;
  min-width: 0;
}
.article-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 10px;
  cursor: pointer;
  transition: color 0.2s;
  &:hover { color: #667eea; }
}
.article-summary {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #94a3b8;
  .category {
    background: #f1f5f9;
    padding: 4px 12px;
    border-radius: 6px;
    color: #64748b;
  }
  .time, .views {
    display: flex;
    align-items: center;
    gap: 4px;
  }
  :deep(.el-tag) {
    margin-left: 0;
  }
}
.article-actions {
  display: flex;
  gap: 8px;
  margin-left: 24px;
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
.empty-tip {
  padding: 60px 0;
  background: #fff;
  border-radius: 16px;
}
</style>
