<template>
  <main class="friends-layout">

    <!-- ── Header ── -->
    <div class="page-header">
      <div>
        <h1>Friends</h1>
        <p class="page-subtitle">{{ filteredFriends.length }} friend{{ filteredFriends.length !== 1 ? 's' : '' }} found</p>
      </div>
    </div>

    <!-- ── Filters ── -->
    <div class="filters-card">

      <div class="filter-group">
        <label>Pseudo</label>
        <input type="text" v-model="filters.pseudo" placeholder="Search by pseudo..." />
      </div>

      <div class="filter-group">
        <label>Status</label>
        <select v-model="filters.status">
          <option value="">All</option>
          <option value="ACCEPTED">Accepted</option>
          <option value="PENDING">Pending</option>
          <option value="BLOCKED">Blocked</option>
        </select>
      </div>

      <div class="filter-group">
        <label>Role</label>
        <select v-model="filters.role">
          <option value="">All</option>
          <option value="USER">User</option>
          <option value="ADMIN">Admin</option>
        </select>
      </div>

      <div class="filter-group filter-group--reset">
        <button class="btn-ghost" @click="resetFilters">Reset filters</button>
      </div>

    </div>

    <!-- ── Loading ── -->
    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Loading your friends...</p>
    </div>

    <!-- ── Error ── -->
    <div v-else-if="error" class="state-box state-box--error">
      <p>{{ error }}</p>
      <button class="btn-ghost" @click="loadFriends">Try again</button>
    </div>

    <!-- ── Empty ── -->
    <div v-else-if="filteredFriends.length === 0" class="state-box">
      <p>No friends match your filters.</p>
    </div>

    <!-- ── Grid ── -->
    <div v-else class="friends-grid">
      <div v-for="f in filteredFriends" :key="f.id" class="friend-card">

        <!-- Avatar -->
        <div class="friend-avatar">{{ f.pseudo.charAt(0).toUpperCase() }}</div>

        <!-- Info -->
        <div class="friend-info">
          <strong class="friend-pseudo">{{ f.pseudo }}</strong>
          <span class="friend-since">Friend since {{ formatDate(f.since) }}</span>
        </div>

        <!-- Badges -->
        <div class="friend-badges">
          <span class="badge badge--role">{{ f.role }}</span>
          <span class="badge" :class="statusClass(f.status)">{{ f.status }}</span>
        </div>
      </div>
    </div>

  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { get_api } from '@/services/api.js'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// ── State ─────────────────────────────────────────────────────────────────────

const friends = ref([])
const loading = ref(true)
const error   = ref(null)

const filters = reactive({
  pseudo:   '',
  status:   '',
  role:     '',
})

// ── Computed ──────────────────────────────────────────────────────────────────

const filteredFriends = computed(() =>
  friends.value.filter(f => {
    if (filters.pseudo   && !f.pseudo.toLowerCase().includes(filters.pseudo.toLowerCase())) return false
    if (filters.status   && f.status !== filters.status)                                    return false
    if (filters.role     && f.role   !== filters.role.toUpperCase())                        return false
    return true 
  })
)

// ── Helpers ───────────────────────────────────────────────────────────────────

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('en-US', { day: 'numeric', month: 'long', year: 'numeric' })
}

function statusClass(status) {
  return {
    ACCEPTED: 'badge--accepted',
    PENDING:  'badge--pending',
    BLOCKED:  'badge--blocked',
  }[status] ?? ''
}

// ── Filters ───────────────────────────────────────────────────────────────────

function resetFilters() {
  Object.assign(filters, { pseudo: '', status: '', role: ''})
}

// ── Load ──────────────────────────────────────────────────────────────────────

async function loadFriends() {
  loading.value = true
  error.value   = null
  try {
    // Replace 'alice' with the actual logged-in user pseudo from your Pinia store
    friends.value = await get_api(`/api/users/${authStore.pseudo}/friends`)
  } catch {
    error.value = 'Could not load your friends. Please try again.'
  } finally {
    loading.value = false
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(loadFriends)
</script>

<style scoped>
/* ── Layout ── */
.friends-layout {
  max-width: 900px;
  margin: 2rem auto;
  padding: 0 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

/* ── Header ── */
.page-header h1 {
  font-size: 1.4rem;
  color: #1a3a4a;
  font-weight: 700;
  margin-bottom: 0.2rem;
}
.page-subtitle { font-size: 0.82rem; color: #aaa; }

/* ── Filters ── */
.filters-card {
  background: #fff;
  border-radius: 14px;
  padding: 1.2rem 1.5rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 1rem;
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}
.filter-group--reset { justify-content: flex-end; }

.filter-group label {
  font-size: 0.78rem;
  color: #666;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

input[type="text"],
input[type="number"],
select {
  border: 1.5px solid #dde3ea;
  border-radius: 8px;
  padding: 0.5rem 0.75rem;
  font-size: 0.88rem;
  color: #1a3a4a;
  outline: none;
  background: #fafbfc;
  transition: border-color 0.2s;
  width: 100%;
}
input:focus, select:focus { border-color: #0d7377; background: #fff; }

/* ── Friends grid ── */
.friends-grid {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.friend-card {
  background: #fff;
  border-radius: 14px;
  padding: 1rem 1.4rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
  display: flex;
  align-items: center;
  gap: 1.2rem;
  transition: box-shadow 0.2s, transform 0.15s;
}
.friend-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-1px);
}

/* Avatar */
.friend-avatar {
  width: 46px; height: 46px;
  border-radius: 50%;
  background: #0d7377;
  color: #fff;
  font-size: 1.2rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

/* Info */
.friend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}
.friend-pseudo {
  font-size: 0.95rem;
  color: #1a3a4a;
}
.friend-since {
  font-size: 0.78rem;
  color: #aaa;
}

/* Badges */
.friend-badges {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.badge {
  display: inline-block;
  border-radius: 999px;
  padding: 0.2rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 600;
}
.badge--role     { background: #f0f4f8; color: #555; }
.badge--accepted { background: #e8f7f7; color: #0d7377; }
.badge--pending  { background: #fff8ee; color: #f5a623; }
.badge--blocked  { background: #fde8e8; color: #e74c3c; }

/* Coins */
.friend-coins {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.88rem;
  font-weight: 700;
  color: #f5a623;
  white-space: nowrap;
}

/* ── States ── */
.state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem 1rem;
  color: #aaa;
  font-size: 0.9rem;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
}
.state-box--error { color: #e74c3c; }

.spinner {
  width: 32px; height: 32px;
  border: 3px solid #e8f0f0;
  border-top-color: #0d7377;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Buttons ── */
.btn-ghost {
  border: none;
  border-radius: 8px;
  padding: 0.55rem 1.2rem;
  font-size: 0.88rem;
  cursor: pointer;
  font-weight: 600;
  background: #f0f4f8;
  color: #555;
  transition: opacity 0.2s;
  width: 100%;
}
.btn-ghost:hover { opacity: 0.8; }

/* ── Responsive ── */
@media (max-width: 560px) {
  .friend-card { flex-wrap: wrap; }
  .friend-badges { order: 3; }
  .friend-coins { order: 4; }
}
</style>