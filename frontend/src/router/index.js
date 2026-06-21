import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'
import DailyChallengesView from '../views/DailyChallengesView.vue'
import FishView from '../views/FishView.vue'
import FriendsView from '../views/FriendsView.vue'
import ShopView from '../views/ShopView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import SearchResultsView from '@/views/SearchResultsView.vue'
import UserProfileView   from '@/views/UserProfileView.vue'
import AquariumView from '@/views/AquariumView.vue'
import TradeView from '@/views/TradeView.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/',                  component: HomeView },
    { path: '/login',             component: LoginView },
    { path: '/register',          component: RegisterView },
    { path: '/profile',           component: ProfileView,          meta: { requiresAuth: true } },
    { path: '/aquarium',          component: AquariumView,         meta: { requiresAuth: true } },
    { path: '/my-fish',           component: FishView,             meta: { requiresAuth: true } },
    { path: '/friends',           component: FriendsView,          meta: { requiresAuth: true } },
    { path: '/shop',              component: ShopView,             meta: { requiresAuth: true } },
    { path: '/daily-challenges',  component: DailyChallengesView,  meta: { requiresAuth: true } },
    { path: '/trade',             component: TradeView,            meta: { requiresAuth: true } },
    { path: '/search',            component: SearchResultsView },
    { path: '/users/:pseudo',     component: UserProfileView },
  ],
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
