<template>
  <main class="search-layout">

    <!-- ── Header ── -->
    <div class="page-header">
      <h1>Search results</h1>
      <p class="page-subtitle">
        {{ loading ? 'Searching...' : `${results.length} user${results.length !== 1 ? 's' : ''} found for "${query}"` }}
      </p>
    </div>

    <!-- ── Loading ── -->
    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Searching users...</p>
    </div>

    <!-- ── Error ── -->
    <div v-else-if="error" class="state-box state-box--error">
      <p>{{ error }}</p>
      <button class="btn-ghost" @click="search">Try again</button>
    </div>

    <!-- ── Empty ── -->
    <div v-else-if="results.length === 0" class="state-box">
      <p>No users found matching "{{ query }}".</p>
    </div>

    <!-- ── Results list ── -->
    <div v-else class="results-list">
      <button
        v-for="u in results"
        :key="u.pseudo"
        class="result-card"
        @click="goToProfile(u.pseudo)"
      >
        <div class="result-avatar">{{ u.pseudo.charAt(0).toUpperCase() }}</div>
        <div class="result-info">
          <strong class="result-pseudo">{{ u.pseudo }}</strong>
          <span class="result-role">{{ u.role }}</span>
        </div>
        <svg class="result-chevron" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6" />
        </svg>
      </button>
    </div>

  </main>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get_api } from '@/services/api.js'

const route  = useRoute()
const router = useRouter()

const query   = ref(route.query.q || '')
const results = ref([])
const loading = ref(true)
const error   = ref(null)

// ── Search ────────────────────────────────────────────────────────────────────

async function search() {
  loading.value = true
  error.value   = null
  try {
    // No dedicated search endpoint yet — fetch all and filter client-side
    const allUsers = await get_api('/api/users')
    const q = query.value.toLowerCase()
    results.value = allUsers.filter(u => u.pseudo.toLowerCase().includes(q))
  } catch {
    error.value = 'Could not load search results. Please try again.'
  } finally {
    loading.value = false
  }
}

function goToProfile(pseudo) {
  router.push(`/users/${pseudo}`)
}

// ── React to query changes (e.g. new search from navbar while staying on this page) ──

watch(() => route.query.q, (newQ) => {
  query.value = newQ || ''
  search()
})

onMounted(search)
</script>

<style scoped>
/* ── Layout ── */
.search-layout {
  max-width: 700px;
  margin: 2rem auto;
  padding: 0 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

/* ── Header ── */
.page-header h1 {
  font-size: 1.4rem;
  color: var(--c-heading);
  font-weight: 700;
  margin-bottom: 0.2rem;
}
.page-subtitle { font-size: 0.85rem; color: var(--c-text-muted); }

/* ── Results ── */
.results-list {
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
}

.result-card {
  background: var(--c-card);
  border: none;
  border-radius: 14px;
  padding: 1rem 1.3rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  cursor: pointer;
  text-align: left;
  width: 100%;
  transition: box-shadow 0.2s, transform 0.15s;
}
.result-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.result-avatar {
  width: 44px; height: 44px;
  border-radius: 50%;
  background: var(--c-brand);
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.result-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}
.result-pseudo { font-size: 0.95rem; color: var(--c-text); }
.result-role {
  font-size: 0.76rem;
  color: var(--c-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.result-chevron {
  color: var(--c-border);
  flex-shrink: 0;
}

/* ── States ── */
.state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem 1rem;
  color: var(--c-text-muted);
  font-size: 0.9rem;
  background: var(--c-card);
  border-radius: 14px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
}
.state-box--error { color: #e74c3c; }

.spinner {
  width: 32px; height: 32px;
  border: 3px solid var(--c-track);
  border-top-color: var(--c-brand);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.btn-ghost {
  border: none;
  border-radius: 8px;
  padding: 0.55rem 1.2rem;
  font-size: 0.88rem;
  cursor: pointer;
  font-weight: 600;
  background: var(--c-ghost-bg);
  color: var(--c-ghost-text);
  transition: opacity 0.2s;
}
.btn-ghost:hover { opacity: 0.8; }
</style>