import { createRouter, createWebHistory } from 'vue-router'
import { getCurrentUser } from '../composables/useCommon'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Home',
    component: () => import(/* webpackChunkName: "home" */ '../views/Home.vue'),
    meta: { requireAuth: true, prefetch: true }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import(/* webpackChunkName: "love-stats" */ '../views/LoveStats.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/statistics/product',
    name: 'ProductStatistics',
    component: () => import(/* webpackChunkName: "statistics" */ '../views/Statistics.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/anniversary',
    name: 'Anniversary',
    component: () => import(/* webpackChunkName: "anniversary" */ '../views/Anniversary.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/album',
    name: 'Album',
    component: () => import(/* webpackChunkName: "album" */ '../views/Album.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/diary',
    name: 'Diary',
    component: () => import(/* webpackChunkName: "diary" */ '../views/Diary.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/personal',
    name: 'Personal',
    component: () => import(/* webpackChunkName: "personal" */ '../views/Personal.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import(/* webpackChunkName: "community" */ '../views/Community.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/community/post/:id',
    name: 'PostDetail',
    component: () => import(/* webpackChunkName: "post-detail" */ '../views/PostDetail.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/community/publish',
    name: 'PublishPost',
    component: () => import(/* webpackChunkName: "publish-post" */ '../views/PublishPost.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/community/edit/:id',
    name: 'EditPost',
    component: () => import(/* webpackChunkName: "publish-post" */ '../views/PublishPost.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/community/topic/:topic?',
    name: 'TopicRanking',
    component: () => import(/* webpackChunkName: "topic-ranking" */ '../views/TopicRanking.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/community/user/:id',
    name: 'UserHome',
    component: () => import(/* webpackChunkName: "user-home" */ '../views/UserHome.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/checkin',
    name: 'CheckIn',
    component: () => import(/* webpackChunkName: "checkin" */ '../views/CheckIn.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/wishlist',
    name: 'WishList',
    component: () => import(/* webpackChunkName: "wishlist" */ '../views/WishList.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/whisper',
    name: 'Whisper',
    component: () => import(/* webpackChunkName: "whisper" */ '../views/Whisper.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/stats',
    name: 'Stats',
    component: () => import(/* webpackChunkName: "stats" */ '../views/Stats.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import(/* webpackChunkName: "settings" */ '../views/Settings.vue'),
    meta: { requireAuth: true }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue'),
    meta: { requireAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0, behavior: 'smooth' }
    }
  }
})

router.beforeEach((to, from, next) => {
  const hasUser = getCurrentUser().id
  
  if (to.meta.requireAuth) {
    if (!hasUser) {
      localStorage.removeItem('user')
      next('/login')
    } else {
      next()
    }
  } else {
    if (hasUser && to.path === '/login') {
      next('/')
    } else {
      next()
    }
  }
})

router.afterEach((to) => {
  if (to.meta.prefetch) {
    const component = to.matched[0]?.components?.default
    if (component && typeof component === 'function') {
      component().catch(() => {})
    }
  }
})

export default router