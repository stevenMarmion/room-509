<template>
  <NavBar :coins="userCoins" />
  <RouterView />
  <AppFooter />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import AppFooter from '@/components/AppFooter.vue'
import { useAuthStore } from '@/stores/auth.js'

const userCoins = ref(0)
const authStore = useAuthStore()

onMounted(() => {
  authStore.fetchCurrentUser()
})
</script>