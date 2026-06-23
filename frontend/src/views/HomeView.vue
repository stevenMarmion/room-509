<template>
  <main class="home-layout" v-if="authStore.isAuthenticated">

    <!-- ── Aquarium ── -->
      <div class="aquarium-container" ref="aquariumRef" @click="onAquariumClick">
        <img src="@/assets/far.png" class="aquarium-bg" />

        <FishSprite
          v-for="fish in fishList"
          :key="'sprite-' + fish.id"
          :color="fish.color"
          :lifePoints="fish.lifePoints"
          :king="kingIds.has(fish.id)"
          :bounds="bounds"
        />

        <Transition name="coin-popup">
          <div v-if="coinPopup.visible" class="coin-popup" :style="{ left: coinPopup.x + 'px', top: coinPopup.y + 'px' }">
            +1
          </div>
        </Transition>
      </div>

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

  <!-- ══ LANDING PAGE — not authenticated ══ -->
  <main class="landing" v-else>

    <!-- ── Hero ── -->
    <section class="hero">
      <h1>Grow your own little aquarium</h1>
      <p class="hero-subtitle">
        Feed your fish, complete daily challenges, trade with friends,
        and watch your aquarium come to life.
      </p>
      <div class="hero-actions">
        <RouterLink to="/register" class="btn-hero btn-hero--solid">Get started — it's free</RouterLink>
        <RouterLink to="/login" class="btn-hero btn-hero--ghost">I already have an account</RouterLink>
      </div>
    </section>

    <!-- ── Preview image ── -->
    <section class="hero-preview">
      <img src="@/assets/far.png" alt="Aquarium preview" />
    </section>

    <!-- ── Features ── -->
    <section class="features">
      <div class="feature-card">
        <span class="feature-icon" style="background:#0d7377;">
          <svg viewBox="0 0 24 24" fill="#fff" width="22" height="22">
            <path d="M21 6H3a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h18a1 1 0 0 0 1-1V7a1 1 0 0 0-1-1zm-1 10H4V8h16v8zm-8-6H8v2h4v-2zm4 0h-2v2h2v-2z"/>
          </svg>
        </span>
        <strong>Daily challenges</strong>
        <p>Complete short tasks every day to earn coins and grow your collection.</p>
      </div>

      <div class="feature-card">
        <span class="feature-icon" style="background:#14a085;">
          <svg viewBox="0 0 24 24" fill="#fff" width="22" height="22">
            <path d="M7 18c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm10 0c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM5.2 4H2V2H0v2h2l3.6 7.6L4.2 14C4.1 14.3 4 14.6 4 15c0 1.1.9 2 2 2h14v-2H6.4c-.1 0-.2-.1-.2-.2l.03-.1.9-1.7H19c.75 0 1.41-.41 1.75-1.03L23 6H5.2z"/>
          </svg>
        </span>
        <strong>Shop & upgrades</strong>
        <p>Buy food and upgrade your aquarium's capacity as your fish family grows.</p>
      </div>

      <div class="feature-card">
        <span class="feature-icon" style="background:#f5a623;">
          <svg viewBox="0 0 24 24" fill="#fff" width="22" height="22">
            <path d="M16 11c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 3-1.34 3-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05C16.19 13.89 17 14.6 17 15.5V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
          </svg>
        </span>
        <strong>Friends & trades</strong>
        <p>Add friends, trade fish, and build your aquarium community together.</p>
      </div>
    </section>

  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import { get_api, post_api } from '@/services/api.js'
import FishSprite from '@/components/FishSprite.vue'

const authStore = useAuthStore()

// ── Aquarium ──────────────────────────────────────────────────
const aquariumRef = ref(null)
const bounds      = reactive({ width: 800, height: 500 })
const fishList    = ref([])

const kingIds = computed(() => {
  const oldest = {}
  for (const fish of fishList.value) {
    const c = fish.color?.toLowerCase()
    if (!c) continue
    if (!oldest[c] || fish.age > oldest[c].age) oldest[c] = fish
  }
  return new Set(Object.values(oldest).map(f => f.id))
})

async function loadAquarium() {
  try {
    const user = await get_api(`/api/users/${authStore.pseudo}`)
    authStore.user = user
    fishList.value = user.aquarium?.fish ?? []
  } catch (e) {
    console.warn('Could not load aquarium fish', e)
  }
}

// ── Click to earn ────────────────────────────────────────────
const coinPopup = reactive({ visible: false, x: 0, y: 0 })
let coinPopupTimer = null

async function onAquariumClick(e) {
  const rect = aquariumRef.value.getBoundingClientRect()
  coinPopup.x = e.clientX - rect.left
  coinPopup.y = e.clientY - rect.top

  clearTimeout(coinPopupTimer)
  coinPopup.visible = true
  coinPopupTimer = setTimeout(() => { coinPopup.visible = false }, 800)

  try {
    const updated = await post_api(`/api/users/${authStore.pseudo}/earn`, {})
    if (authStore.user && updated?.coins != null) {
      authStore.user.coins = updated.coins
    } else {
      await authStore.fetchCurrentUser()
    }
  } catch {
    console.warn('Could not earn coins')
  }
}

async function measureBounds() {
  await nextTick()
  if (aquariumRef.value) {
    const rect    = aquariumRef.value.getBoundingClientRect()
    bounds.width  = rect.width
    bounds.height = rect.height
  }
}

onMounted(async () => {
  if (authStore.isAuthenticated) {
    await loadAquarium()
    await measureBounds()
  }
})

// Si l'auth arrive après le montage (token chargé de manière asynchrone)
watch(() => authStore.isAuthenticated, async (val) => {
  if (val) {
    await loadAquarium()
    await measureBounds()
  }
})

const challengesSummary = ref('Loading...')
const tradeRequests     = ref('Loading...')
</script>

<style scoped>
/* ══ Authenticated layout ══ */
.home-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 1.2rem;
  padding: 1.5rem;
  align-items: start;
}

/* ── Aquarium ── */
.aquarium-container {
  position: relative;
  width: 100%;
  height: 500px;
  overflow: hidden;
  border-radius: 12px;
  cursor: pointer;
}
.aquarium-bg {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
/* ── Coin popup ── */
.coin-popup {
  position: absolute;
  pointer-events: none;
  font-size: 1.2rem;
  font-weight: 800;
  color: #f5c842;
  text-shadow: 0 1px 3px rgba(0,0,0,0.5);
  transform: translateX(-50%);
}
.coin-popup-enter-active { transition: opacity 0.15s, transform 0.6s ease-out; }
.coin-popup-leave-active { transition: opacity 0.4s, transform 0.6s ease-out; }
.coin-popup-enter-from { opacity: 0; transform: translateX(-50%) translateY(0); }
.coin-popup-leave-to   { opacity: 0; transform: translateX(-50%) translateY(-30px); }

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

/* ══ Landing page (unauthenticated) ══ */
.landing {
  display: flex;
  flex-direction: column;
  gap: 3rem;
  padding: 1.5rem;
  max-width: 1000px;
  margin: 0 auto;
}

/* ── Hero ── */
.hero {
  text-align: center;
  padding: 3rem 1rem 1rem;
}

.hero h1 {
  font-size: 2.1rem;
  color: #1a3a4a;
  font-weight: 800;
  margin-bottom: 0.8rem;
  line-height: 1.25;
}

.hero-subtitle {
  font-size: 1.02rem;
  color: #667;
  max-width: 560px;
  margin: 0 auto 1.8rem;
  line-height: 1.55;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.btn-hero {
  border-radius: 999px;
  padding: 0.75rem 1.7rem;
  font-size: 0.95rem;
  font-weight: 700;
  text-decoration: none;
  transition: opacity 0.2s, transform 0.15s;
}
.btn-hero:hover { transform: translateY(-1px); }

.btn-hero--solid {
  background: #0d7377;
  color: #fff;
}
.btn-hero--solid:hover { opacity: 0.9; }

.btn-hero--ghost {
  background: #fff;
  color: #0d7377;
  border: 1.5px solid #dde3ea;
}
.btn-hero--ghost:hover { border-color: #0d7377; }

/* ── Preview image ── */
.hero-preview {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(13, 115, 119, 0.15);
}
.hero-preview img {
  width: 100%;
  height: 360px;
  object-fit: cover;
  display: block;
}

/* ── Features ── */
.features {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.2rem;
}

.feature-card {
  background: #fff;
  border-radius: 14px;
  padding: 1.5rem;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.feature-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-card strong {
  font-size: 1rem;
  color: #1a3a4a;
}

.feature-card p {
  font-size: 0.85rem;
  color: #888;
  line-height: 1.5;
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
  .features {
    grid-template-columns: 1fr;
  }
  .hero h1 {
    font-size: 1.6rem;
  }
}
</style>