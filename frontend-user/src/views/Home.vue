<template>
  <div class="home-page">
    <div class="home-hero">
      <h1>欢迎来到我的博客</h1>
      <p>记录技术成长，分享生活感悟</p>
    </div>
    <div class="home-content">
      <div class="main-area">
        <div class="article-list" v-loading="loading">
          <div class="article-card" v-for="article in articles" :key="article.id" @click="$router.push(`/article/${article.id}`)">
            <div class="article-cover" v-if="article.coverImage">
              <img :src="article.coverImage" alt="" />
            </div>
            <div class="article-cover placeholder" v-else>
              <el-icon :size="40" color="#c0c4cc"><Document /></el-icon>
            </div>
            <div class="article-body">
              <h2 class="article-title">{{ article.title }}</h2>
              <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
              <div class="article-meta">
                <span><el-icon><User /></el-icon> {{ article.authorName }}</span>
                <span><el-icon><Calendar /></el-icon> {{ article.createdAt }}</span>
                <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
                <el-tag v-if="article.categoryName" size="small" type="info">{{ article.categoryName }}</el-tag>
              </div>
            </div>
          </div>
          <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />
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
      <aside class="side-area">
        <div class="side-card">
          <h3 class="side-title">文章分类</h3>
          <div class="category-list">
            <div
              class="category-item"
              :class="{ active: !selectedCategory }"
              @click="selectCategory(null)"
            >全部</div>
            <div
              class="category-item"
              :class="{ active: selectedCategory === c.id }"
              v-for="c in categories"
              :key="c.id"
              @click="selectCategory(c.id)"
            >{{ c.name }}</div>
          </div>
        </div>
      </aside>
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
</script>

<style lang="scss" scoped>
.home-hero {
  text-align: center;
  padding: 48px 24px 32px;
  h1 { font-size: 32px; font-weight: 700; color: #2c3e50; margin-bottom: 12px; }
  p { font-size: 16px; color: #7f8c8d; }
}
.home-content {
  display: flex;
  gap: 32px;
}
.main-area {
  flex: 1;
  min-width: 0;
}
.side-area {
  width: 280px;
  flex-shrink: 0;
}
.article-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 200px;
}
.article-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  display: flex;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
  }
}
.article-cover {
  width: 220px;
  min-height: 160px;
  flex-shrink: 0;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}
.article-body {
  flex: 1;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}
.article-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.article-summary {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #909399;
  flex-wrap: wrap;
  row-gap: 8px;
  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}
.side-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 24px;
}
.side-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f2f5;
}
.category-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.category-item {
  padding: 10px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
  &:hover { background: #f0f7ff; color: #2d8cf0; }
  &.active { background: #2d8cf0; color: #fff; }
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media screen and (max-width: 900px) {
  .home-content {
    flex-direction: column;
  }
  .side-area {
    width: 100%;
  }
  .side-card {
    .category-list {
      flex-direction: row;
      flex-wrap: wrap;
      gap: 8px;
    }
    .category-item {
      padding: 8px 16px;
    }
  }
}

@media screen and (max-width: 640px) {
  .article-card {
    flex-direction: column;
  }
  .article-cover {
    width: 100%;
    min-height: 180px;
  }
  .home-hero {
    padding: 32px 16px 24px;
    h1 { font-size: 24px; }
    p { font-size: 14px; }
  }
}
</style>
