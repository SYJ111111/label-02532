import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'article/:id', name: 'ArticleDetail', component: () => import('@/views/ArticleDetail.vue') },
      { path: 'login', name: 'Login', component: () => import('@/views/Login.vue') },
      { path: 'register', name: 'Register', component: () => import('@/views/Register.vue') },
      { path: 'my-articles', name: 'MyArticles', component: () => import('@/views/MyArticles.vue'), meta: { requiresAuth: true } },
      { path: 'write', name: 'ArticleWrite', component: () => import('@/views/ArticleWrite.vue'), meta: { requiresAuth: true } },
      { path: 'write/:id', name: 'ArticleEdit', component: () => import('@/views/ArticleWrite.vue'), meta: { requiresAuth: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
