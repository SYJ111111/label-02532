<template>
  <div class="page-container">
    <div class="stat-grid">
      <div class="stat-card" v-for="item in statCards" :key="item.label">
        <div class="stat-icon" :style="{ background: item.bg }">
          <el-icon :size="28" color="#fff"><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>
    </div>
    <div class="content-card">
      <h3 class="card-title">最近文章</h3>
      <el-table :data="recentArticles" stripe v-loading="tableLoading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="authorName" label="作者" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="80" align="center" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getArticleList, getStats } from '@/api/article'

const articleTotal = ref(0)
const pendingTotal = ref(0)
const categoryTotal = ref(0)
const userTotal = ref(0)
const totalViews = ref(0)
const recentArticles = ref([])
const tableLoading = ref(false)

const statCards = computed(() => [
  { label: '文章总数', value: articleTotal.value, icon: 'Document', bg: 'linear-gradient(135deg, #667eea, #764ba2)' },
  { label: '待审核', value: pendingTotal.value, icon: 'Timer', bg: 'linear-gradient(135deg, #f093fb, #f5576c)' },
  { label: '用户总数', value: userTotal.value, icon: 'User', bg: 'linear-gradient(135deg, #4facfe, #00f2fe)' },
  { label: '总浏览量', value: totalViews.value, icon: 'View', bg: 'linear-gradient(135deg, #43e97b, #38f9d7)' }
])

onMounted(async () => {
  tableLoading.value = true
  try {
    const [statsRes, articleRes] = await Promise.all([
      getStats(),
      getArticleList({ pageNum: 1, pageSize: 10 })
    ])
    articleTotal.value = statsRes.data.articleCount
    pendingTotal.value = statsRes.data.pendingCount
    categoryTotal.value = statsRes.data.categoryCount
    userTotal.value = statsRes.data.userCount
    totalViews.value = statsRes.data.totalViews
    recentArticles.value = articleRes.data.records
  } catch {
    // error handled by interceptor
  } finally {
    tableLoading.value = false
  }
})

function statusType(status) {
  const map = { DRAFT: 'info', PENDING: 'warning', PUBLISHED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}

function statusText(status) {
  const map = { DRAFT: '草稿', PENDING: '待审核', PUBLISHED: '已发布', REJECTED: '已拒绝' }
  return map[status] || status
}
</script>

<style lang="scss" scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}
.stat-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.2s;
  &:hover { transform: translateY(-2px); }
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}
</style>
