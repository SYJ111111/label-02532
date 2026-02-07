<template>
  <div class="home-page">
    <div class="category-bar">
      <div
        class="category-item"
        :class="{ active: !selectedCategory }"
        @click="selectCategory(null)"
      >
        <el-icon><Grid /></el-icon>
        全部
      </div>
      <div
        class="category-item"
        :class="{ active: selectedCategory === c.id }"
        v-for="c in categories"
        :key="c.id"
        @click="selectCategory(c.id)"
      >{{ c.name }}</div>
    </div>
    <div class="home-content">
      <div class="article-list" v-loading="loading">
        <div class="article-card" v-for="article in articles" :key="article.id" @click="$router.push(`/article/${article.id}`)">
          <div class="article-cover" v-if="article.coverImage">
            <img :src="article.coverImage" alt="" />
          </div>
          <div class="article-cover placeholder" v-else>
            <el-icon :size="48" color="#d0d5dd"><Document /></el-icon>
          </div>
          <div class="article-body">
            <h2 class="article-title">{{ article.title }}</h2>
            <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
            <div class="article-meta">
              <div class="meta-left">
                <span class="author">
                  <el-avatar :size="24" class="author-avatar">{{ (article.authorName || '匿')[0] }}</el-avatar>
                  {{ article.authorName }}
                </span>
                <span class="date"><el-icon><Calendar /></el-icon> {{ formatDate(article.createdAt) }}</span>
                <span class="views"><el-icon><View /></el-icon> {{ article.viewCount }}</span>
              </div>
              <el-tag v-if="article.categoryName" size="small" effect="plain" round>{{ article.categoryName }}</el-tag>
            </div>
          </div>
        </div>
        <el-empty v-if="!loading && articles.length === 0" description="暂无文章" :image-size="120" />
      </div>
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadArticles"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles, getCategories } from '@/api/public'

const articles = ref([])
const categories = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(8)
const loading = ref(false)
const selectedCategory = ref(null)

onMounted(() => {
  loadArticles()
  loadCategories()
})

async function loadArticles() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (selectedCategory.value) params.categoryId = selectedCategory.value
    const res = await getArticles(params)
    articles.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const res = await getCategories()
  categories.value = res.data
}

function selectCategory(id) {
  selectedCategory.value = id
  pageNum.value = 1
  loadArticles()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 10)
}
</script>

<style lang="scss" scoped>
.category-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 24px;
  margin-bottom: 28px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  flex-wrap: wrap;
}
.category-item {
  padding: 10px 22px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  background: #f1f5f9;
  transition: all 0.25s ease;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
  &:hover {
    background: #e0e7ff;
    color: #4f46e5;
    transform: translateY(-1px);
  }
  &.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
  }
}
.home-content {
  display: flex;
  flex-direction: column;
}
.article-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-height: 200px;
}
.article-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  display: flex;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
    border-color: #e0e7ff;
    .article-title { color: #667eea; }
  }
}
.article-cover {
  width: 260px;
  min-height: 180px;
  flex-shrink: 0;
  overflow: hidden;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  &:hover img { transform: scale(1.05); }
}
.article-body {
  flex: 1;
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}
.article-title {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}
.article-summary {
  font-size: 15px;
  color: #64748b;
  margin-bottom: 16px;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.article-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  .meta-left {
    display: flex;
    align-items: center;
    gap: 20px;
  }
  span {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #94a3b8;
  }
  .author {
    font-weight: 500;
    color: #64748b;
  }
  .author-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    font-size: 12px;
  }
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media screen and (max-width: 768px) {
  .category-bar {
    gap: 8px;
    padding: 16px;
  }
  .category-item {
    padding: 8px 16px;
    font-size: 13px;
  }
  .article-card {
    flex-direction: column;
  }
  .article-cover {
    width: 100%;
    min-height: 200px;
  }
  .article-body {
    padding: 20px;
  }
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>