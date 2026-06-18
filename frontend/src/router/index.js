import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'
import DailyChallengesView from '../views/DailyChallengesView.vue'
import FishView from '../views/FishView.vue'
import FriendsView from '../views/FriendsView.vue'
import ShopView from '../views/ShopView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    { 
      path: '/profile', 
      name: 'profile',
      component: ProfileView,
    },
    { path: '/aquarium',          component: HomeView },
    { path: '/my-fish',           component: FishView },
    { path: '/friends',           component: FriendsView },
    { path: '/shop',              component: ShopView },
    { path: '/daily-challenges',  component: DailyChallengesView },
  ],
})

export default router
