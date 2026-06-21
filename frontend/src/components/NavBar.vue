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
        <ul class="navbar-nav me-auto mb-2 mb-lg-0" v-if="authStore.isAuthenticated">
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

        <!-- Search bar -->
        <form class="search-form ms-auto mb-2 mb-lg-0 me-lg-3" @submit.prevent="handleSearch">
          <svg class="search-icon" viewBox="0 0 24 24" width="15" height="15" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="7" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <input
            type="search"
            v-model="searchQuery"
            placeholder="Search users..."
            aria-label="Search users"
          />
        </form>

        <!-- Right side: auth-dependent -->
        <div class="d-flex align-items-center gap-3">

          <!-- Logged in: avatar -->
          <template v-if="authStore.isAuthenticated">
            <RouterLink to="/profile" class="avatar-btn" aria-label="Profile">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="#fff">
                <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/>
              </svg>
            </RouterLink>
          </template>

          <!-- Logged out: login + register -->
          <template v-else>
            <RouterLink to="/login" class="btn-auth btn-auth--ghost">Log in</RouterLink>
            <RouterLink to="/register" class="btn-auth btn-auth--solid">Sign up</RouterLink>
          </template>

        </div>
      </div>

    </div>
  </nav>
</template>

<script setup>
import { ref, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const router = useRouter()
const authStore = useAuthStore()

// ── Search ────────────────────────────────────────────────────────────────────

const searchQuery = ref('')

function handleSearch() {
  const query = searchQuery.value.trim()
  if (!query) return
  router.push({ path: '/search', query: { q: query } })
}
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

/* ── Search bar ── */
.search-form {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 0.7rem;
  color: rgba(255, 255, 255, 0.6);
  pointer-events: none;
}

.search-form input {
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 999px;
  padding: 0.4rem 0.9rem 0.4rem 2.1rem;
  font-size: 0.85rem;
  color: #fff;
  width: 200px;
  outline: none;
  transition: background 0.2s, border-color 0.2s, width 0.2s;
}
.search-form input::placeholder { color: rgba(255, 255, 255, 0.6); }
.search-form input:focus {
  background: rgba(255, 255, 255, 0.18);
  border-color: rgba(255, 255, 255, 0.4);
  width: 240px;
}

/* ── Auth buttons ── */
.btn-auth {
  border-radius: 999px;
  padding: 0.4rem 1.1rem;
  font-size: 0.88rem;
  font-weight: 600;
  text-decoration: none;
  transition: opacity 0.2s, background 0.2s;
  white-space: nowrap;
}

.btn-auth--ghost {
  color: #fff;
  border: 1.5px solid rgba(255, 255, 255, 0.5);
}
.btn-auth--ghost:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.btn-auth--solid {
  background: #f5a623;
  color: #fff;
}
.btn-auth--solid:hover { opacity: 0.88; }

/* ── Responsive ── */
@media (max-width: 991px) {
  .search-form input { width: 100%; }
  .d-flex.align-items-center.gap-3 {
    margin-top: 0.8rem;
    flex-wrap: wrap;
  }
}
</style>