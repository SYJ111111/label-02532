<template>
  <div class="detail-page" v-loading="loading">
    <template v-if="article">
      <div class="detail-card">
        <h1 class="detail-title">{{ article.title }}</h1>
        <div class="detail-meta">
          <span><el-icon><User /></el-icon> {{ article.authorName }}</span>
          <span><el-icon><Calendar /></el-icon> {{ article.createdAt }}</span>
          <span><el-icon><View /></el-icon> {{ article.viewCount }} 次阅读</span>
          <el-tag v-if="article.categoryName" size="small">{{ article.categoryName }}</el-tag>
        </div>
        <div class="detail-cover" v-if="article.coverImage">
          <img :src="article.coverImage" alt="" />
        </div>
        <div class="detail-content">{{ article.content }}</div>
      </div>
      <div class="detail-back">
        <el-button plain @click="$router.push('/')">
          <el-icon><ArrowLeft /></el-icon> 返回首页
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '@/api/public'

const route = useRoute()
const loading = ref(false)
const article = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getArticleDetail(route.params.id)
    article.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.detail-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.05);
  padding: 40px 48px;
  max-width: 840px;
  margin: 0 auto;
}
.detail-title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1.4;
  margin-bottom: 16px;
}
.detail-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 14px;
  color: #909399;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f2f5;
  margin-bottom: 24px;
  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}
.detail-cover {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  img { width: 100%; display: block; }
}
.detail-content {
  font-size: 16px;
  line-height: 1.8;
  color: #34495e;
  white-space: pre-wrap;
  word-wrap: break-word;
}
.detail-back {
  max-width: 840px;
  margin: 24px auto 0;
}
</style>
