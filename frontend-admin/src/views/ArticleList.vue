<template>
  <div class="page-container">
    <div class="content-card">
      <div class="search-bar">
        <el-input v-model="query.title" placeholder="搜索文章标题" clearable style="width: 220px" prefix-icon="Search" />
        <el-select v-model="query.categoryId" placeholder="选择分类" clearable style="width: 200px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="选择状态" clearable style="width: 140px">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="authorName" label="作者" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="handlePreview(row)">查看</el-button>
            <el-button v-if="row.status === 'PENDING'" size="small" type="success" plain @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.status === 'PENDING'" size="small" type="warning" plain @click="handleReject(row)">拒绝</el-button>
            <el-button v-if="row.status === 'PUBLISHED'" size="small" type="warning" plain @click="handleOffline(row)">下架</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="loadData" />
      </div>
    </div>
    <el-dialog v-model="previewVisible" title="文章预览" width="800px" top="5vh">
      <div class="preview-content" v-if="previewArticle">
        <h2>{{ previewArticle.title }}</h2>
        <div class="preview-meta">
          <span>作者：{{ previewArticle.authorName }}</span>
          <span>分类：{{ previewArticle.categoryName || '无' }}</span>
          <span>状态：{{ statusText(previewArticle.status) }}</span>
        </div>
        <el-divider />
        <div class="preview-body">{{ previewArticle.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getArticleList, getArticleDetail, approveArticle, rejectArticle, offlineArticle, deleteArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categories = ref([])
const query = reactive({ title: '', categoryId: null, status: '', pageNum: 1, pageSize: 10 })
const previewVisible = ref(false)
const previewArticle = ref(null)

onMounted(() => { loadData(); loadCategories() })

async function loadData() {
  loading.value = true
  try {
    const res = await getArticleList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function loadCategories() {
  const res = await getCategoryList()
  categories.value = res.data
}

async function handlePreview(row) {
  const res = await getArticleDetail(row.id)
  previewArticle.value = res.data
  previewVisible.value = true
}

async function handleApprove(row) {
  await ElMessageBox.confirm('确定要审核通过这篇文章吗？', '提示')
  await approveArticle(row.id)
  ElMessage.success('审核通过')
  loadData()
}

async function handleReject(row) {
  await ElMessageBox.confirm('确定要拒绝这篇文章吗？', '提示', { type: 'warning' })
  await rejectArticle(row.id)
  ElMessage.success('已拒绝')
  loadData()
}

async function handleOffline(row) {
  await ElMessageBox.confirm('确定要下架这篇文章吗？', '提示', { type: 'warning' })
  await offlineArticle(row.id)
  ElMessage.success('已下架')
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', { type: 'warning' })
  await deleteArticle(row.id)
  ElMessage.success('删除成功')
  loadData()
}

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
.preview-content { h2 { font-size: 22px; margin: 0 0 16px; color: #303133; } }
.preview-meta { display: flex; gap: 24px; font-size: 14px; color: #909399; }
.preview-body { line-height: 1.8; font-size: 15px; color: #606266; white-space: pre-wrap; }
</style>
