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
import { get_api } from '@/services/api.js'

const userCoins = ref(0)

onMounted(async () => {
  try {
    // Replace 1 with the actual logged-in user id
    const user = await get_api('/api/users/1')
    userCoins.value = user.coins
  } catch (e) {
    console.warn('Could not load user:', e)
  }
})
</script>