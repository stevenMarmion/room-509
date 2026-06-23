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
import AdminLayout from '@/views/admin/AdminLayout.vue'
import AdminUsers from '@/views/admin/AdminUsers.vue'
import AdminFish from '@/views/admin/AdminFish.vue'
import AdminAquariums from '@/views/admin/AdminAquariums.vue'
import AdminShop from '@/views/admin/AdminShop.vue'
import AdminTrades from '@/views/admin/AdminTrades.vue'
import AdminChallenges from '@/views/admin/AdminChallenges.vue'
import AdminFriendships from '@/views/admin/AdminFriendships.vue'
import AdminNotifications from '@/views/admin/AdminNotifications.vue'

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
    { path: '/admin',             component: AdminLayout,          meta: { requiresAdmin: true },  children: [
      { path: '',             redirect: '/admin/users' },
      { path: 'users',        component: AdminUsers },
      { path: 'aquariums',    component: AdminAquariums },
      { path: 'fish',         component: AdminFish },
      { path: 'shop',         component: AdminShop },
      { path: 'trades',       component: AdminTrades },
      { path: 'challenges',   component: AdminChallenges },
      { path: 'friendships',  component: AdminFriendships },
      { path: 'notifications',component: AdminNotifications },
    ]},
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
