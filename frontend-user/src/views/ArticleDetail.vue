<template>
  <div class="detail-page" v-loading="loading">
    <template v-if="article">
      <div class="detail-card">
        <div class="detail-header">
          <el-tag v-if="article.categoryName" effect="plain" round>{{ article.categoryName }}</el-tag>
          <h1 class="detail-title">{{ article.title }}</h1>
          <div class="detail-meta">
            <div class="author-info">
              <el-avatar :size="40" class="author-avatar">{{ (article.authorName || '匿')[0] }}</el-avatar>
              <div class="author-text">
                <span class="author-name">{{ article.authorName }}</span>
                <span class="publish-time">发布于 {{ formatDate(article.createdAt) }}</span>
              </div>
            </div>
            <div class="article-stats">
              <span><el-icon><View /></el-icon> {{ article.viewCount }} 阅读</span>
            </div>
          </div>
        </div>
        <el-divider />
        <div class="detail-cover" v-if="article.coverImage">
          <img :src="article.coverImage" alt="" />
        </div>
        <div class="detail-content">{{ article.content }}</div>
      </div>
      <div class="detail-actions">
        <el-button size="large" round @click="$router.push('/')">
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

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 16)
}
</script>

<style lang="scss" scoped>
.detail-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  padding: 48px 56px;
  max-width: 900px;
  margin: 0 auto;
}
.detail-header {
  text-align: center;
  margin-bottom: 8px;
}
.detail-title {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.4;
  margin: 20px 0 24px;
}
.detail-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
}
.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.author-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}
.author-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  .author-name {
    font-size: 15px;
    font-weight: 600;
    color: #334155;
  }
  .publish-time {
    font-size: 13px;
    color: #94a3b8;
  }
}
.article-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  span {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    color: #64748b;
  }
}
.detail-cover {
  margin: 32px 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  img {
    width: 100%;
    display: block;
  }
}
.detail-content {
  font-size: 17px;
  line-height: 2;
  color: #334155;
  white-space: pre-wrap;
  word-wrap: break-word;
}
.detail-actions {
  max-width: 900px;
  margin: 32px auto 0;
  display: flex;
  justify-content: center;
}

@media screen and (max-width: 768px) {
  .detail-card {
    padding: 32px 24px;
    border-radius: 16px;
  }
  .detail-title {
    font-size: 24px;
  }
  .detail-meta {
    flex-direction: column;
    gap: 16px;
  }
}
</style>