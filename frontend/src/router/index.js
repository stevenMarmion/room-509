import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'

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
    { path: '/my-fish',           component: HomeView },
    { path: '/friends',           component: HomeView },
    { path: '/shop',              component: HomeView },
    { path: '/daily-challenges',  component: HomeView },
  ],
})

export default router
