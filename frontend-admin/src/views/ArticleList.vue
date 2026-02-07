<template>
  <div class="page-container">
    <div class="content-card">
      <div class="search-bar">
        <el-input v-model="query.title" placeholder="搜索文章标题" clearable style="width: 220px" prefix-icon="Search" />
        <el-select v-model="query.categoryId" placeholder="选择分类" clearable style="width: 200px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="选择状态" clearable style="width: 140px">
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="草稿" value="DRAFT" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="primary" plain @click="$router.push('/article/edit')">
          <el-icon><Plus /></el-icon> 写文章
        </el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="authorName" label="作者" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="$router.push(`/article/edit/${row.id}`)">编辑</el-button>
            <el-button size="small" :type="row.status === 'PUBLISHED' ? 'warning' : 'success'" plain @click="toggleStatus(row)">
              {{ row.status === 'PUBLISHED' ? '下架' : '发布' }}
            </el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getArticleList, deleteArticle, updateArticleStatus } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categories = ref([])
const query = reactive({ title: '', categoryId: null, status: '', pageNum: 1, pageSize: 10 })

onMounted(() => {
  loadData()
  loadCategories()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getArticleList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const res = await getCategoryList()
  categories.value = res.data
}

async function toggleStatus(row) {
  const newStatus = row.status === 'PUBLISHED' ? 'DRAFT' : 'PUBLISHED'
  await updateArticleStatus(row.id, newStatus)
  ElMessage.success('状态更新成功')
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', { type: 'warning' })
  await deleteArticle(row.id)
  ElMessage.success('删除成功')
  loadData()
}
</script>
