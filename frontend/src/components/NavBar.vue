<template>
  <nav class="navbar navbar-expand-lg navbar-dark" style="background-color: #0d7377;">
    <div class="container-fluid px-4">

      <!-- Brand -->
      <RouterLink class="navbar-brand fw-bold" to="/">
        Manage your little fish
      </RouterLink>

      <!-- Burger button (mobile) -->
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#mainNav"
        aria-controls="mainNav"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <!-- Links -->
      <div class="collapse navbar-collapse" id="mainNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <RouterLink class="nav-link" to="/aquarium">Aquarium</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/my-fish">My Fish</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/friends">Friends</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/shop">Shop</RouterLink>
          </li>
        </ul>

        <!-- Coins + avatar -->
        <div class="d-flex align-items-center gap-3">
          <span class="coins-badge">
            <svg viewBox="0 0 24 24" width="14" height="14" fill="#fff"><circle cx="12" cy="12" r="10"/></svg>
            {{ formattedCoins }}
          </span>
          <RouterLink to="/profile" class="avatar-btn" aria-label="Profile">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="#fff">
              <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/>
            </svg>
          </RouterLink>
        </div>
      </div>

    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'

// Props
const props = defineProps({
  coins: {
    type: Number,
    default: 0
  }
})

// Format coins with a space as thousands separator (e.g. 1 240)
const formattedCoins = computed(() =>
  props.coins.toLocaleString('fr-FR')
)
</script>

<style scoped>
.coins-badge {
  background: #f5a623;
  color: #fff;
  font-weight: 700;
  border-radius: 999px;
  padding: 0.3rem 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.9rem;
}

.avatar-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #1a3a4a;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  transition: background 0.2s;
}
.avatar-btn:hover { background: #0d2233; }
</style>