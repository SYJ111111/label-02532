import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '仪表盘' } },
      { path: 'article', name: 'ArticleList', component: () => import('@/views/ArticleList.vue'), meta: { title: '文章管理' } },
      { path: 'article/edit/:id?', name: 'ArticleEdit', component: () => import('@/views/ArticleEdit.vue'), meta: { title: '编辑文章' } },
      { path: 'category', name: 'CategoryList', component: () => import('@/views/CategoryList.vue'), meta: { title: '分类管理' } },
      { path: 'user', name: 'UserManage', component: () => import('@/views/UserManage.vue'), meta: { title: '用户管理' } },
      { path: 'log', name: 'OperationLog', component: () => import('@/views/OperationLog.vue'), meta: { title: '操作日志' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人信息' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login' && to.path !== '/register') {
    next('/login')
  } else {
    next()
  }
})

export default router
