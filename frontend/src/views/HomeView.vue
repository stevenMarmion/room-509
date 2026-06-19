<template>
  <main class="home-layout">

    <!-- ── Aquarium ── -->
    <section class="aquarium-container">
      <img class="aquarium-bg" src="@/assets/far.png" alt="Aquarium" />
      <div class="aquarium-footer">Click a fish to earn !</div>
    </section>

    <!-- ── Sidebar ── -->
    <aside class="sidebar">

      <RouterLink to="/daily-challenges" class="side-card">
        <span class="side-icon" style="background:#0d7377;">
          <svg viewBox="0 0 24 24" fill="#fff" width="20" height="20">
            <path d="M21 6H3a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h18a1 1 0 0 0 1-1V7a1 1 0 0 0-1-1zm-1 10H4V8h16v8zm-8-6H8v2h4v-2zm4 0h-2v2h2v-2z"/>
          </svg>
        </span>
        <div class="side-text">
          <strong>Daily challenges</strong>
          <span>{{ challengesSummary }}</span>
        </div>
      </RouterLink>

      <RouterLink to="/shop" class="side-card">
        <span class="side-icon" style="background:#14a085;">
          <svg viewBox="0 0 24 24" fill="#fff" width="20" height="20">
            <path d="M7 18c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm10 0c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM5.2 4H2V2H0v2h2l3.6 7.6L4.2 14C4.1 14.3 4 14.6 4 15c0 1.1.9 2 2 2h14v-2H6.4c-.1 0-.2-.1-.2-.2l.03-.1.9-1.7H19c.75 0 1.41-.41 1.75-1.03L23 6H5.2z"/>
          </svg>
        </span>
        <div class="side-text">
          <strong>Shop</strong>
          <span>Food · upgrades</span>
        </div>
      </RouterLink>

      <RouterLink to="/friends" class="side-card">
        <span class="side-icon" style="background:#f5a623;">
          <svg viewBox="0 0 24 24" fill="#fff" width="20" height="20">
            <path d="M16 11c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 3-1.34 3-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05C16.19 13.89 17 14.6 17 15.5V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
          </svg>
        </span>
        <div class="side-text">
          <strong>Friends</strong>
          <span>{{ tradeRequests }}</span>
        </div>
      </RouterLink>

    </aside>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
// import { get_api } from '@/services/api.js'

const challengesSummary = ref('Loading...')
const tradeRequests     = ref('Loading...')

onMounted(async () => {
  // try {
  //   const config = await get_api('/api/config')
  //   console.log('Config loaded:', config)
  // } catch (e) {
  //   console.warn('Could not load config:', e)
  // }

  // Replace with real API calls when endpoints are ready
  challengesSummary.value = 'To complete with real datas'
  tradeRequests.value     = 'To complete with real datas'
})
</script>

<style scoped>
.home-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 1.2rem;
  padding: 1.5rem;
  align-items: start;
}

/* ── Aquarium ── */
.aquarium-container {
  overflow: hidden;
  border-radius: 8px;
  position: relative;
  background: #b2e4e8;
}

.aquarium-bg {
  width: 100%;
  height: 80vh;
  object-fit: cover;
  display: block;
}

.aquarium-footer {
  background: #c9a227;
  color: #fff;
  text-align: center;
  padding: 0.6rem;
  font-style: italic;
  font-size: 0.9rem;
}

/* ── Sidebar ── */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  position: sticky;
  top: 1.5rem;
}

.side-card {
  background: #fff;
  border-radius: 12px;
  padding: 1rem 1.2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  text-decoration: none;
  color: inherit;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  transition: box-shadow 0.2s, transform 0.2s;
}
.side-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-1px);
}

.side-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.side-text strong {
  display: block;
  font-size: 0.95rem;
  color: #1a1a2e;
}
.side-text span {
  font-size: 0.8rem;
  color: #888;
}

/* ── Responsive ── */
@media (max-width: 768px) {
  .home-layout {
    grid-template-columns: 1fr;
  }
  .aquarium-bg {
    height: 40vh;
  }
  .sidebar {
    position: static;
  }
}
</style>