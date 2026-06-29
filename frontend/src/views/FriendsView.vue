<template>
  <main class="friends-layout">

    <!-- ── Toast ── -->
    <Transition name="toast">
      <div v-if="toast.visible" class="toast-notif" :class="`toast-notif--${toast.type}`">
        {{ toast.message }}
      </div>
    </Transition>

    <!-- ── Header ── -->
    <div class="page-header">
      <div>
        <h1>Friends</h1>
        <p class="page-subtitle">{{ filteredFriends.length }} friend{{ filteredFriends.length !== 1 ? 's' : '' }} found</p>
      </div>
    </div>

    <!-- ── Pending requests ── -->
    <div v-if="pendingRequests.length > 0" class="pending-card">
      <div class="pending-header">
        <span class="section-title">Friend requests</span>
        <span class="pending-count">{{ pendingRequests.length }}</span>
      </div>
      <div class="pending-list">
        <button
          v-for="req in pendingRequests"
          :key="req.id"
          class="pending-chip"
          @click="openRequestPopup(req)"
        >
          <span class="pending-avatar">{{ req.pseudo.charAt(0).toUpperCase() }}</span>
          <span class="pending-pseudo">{{ req.pseudo }}</span>
        </button>
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

  <!-- ── Friend request popup ── -->
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="selectedRequest" class="modal-backdrop" @click.self="closeRequestPopup">
        <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="request-modal-title">

          <button class="modal-close" @click="closeRequestPopup" aria-label="Close">✕</button>

          <h3 id="request-modal-title">Friend request</h3>

          <button class="request-profile" @click="goToProfile(selectedRequest.pseudo)">
            <span class="request-avatar">{{ selectedRequest.pseudo.charAt(0).toUpperCase() }}</span>
            <div class="request-info">
              <strong>{{ selectedRequest.pseudo }}</strong>
              <span class="badge badge--role">{{ selectedRequest.role }}</span>
            </div>
            <svg class="request-chevron" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6" />
            </svg>
          </button>

          <p class="request-hint">Sent {{ formatDate(selectedRequest.since) }} · Click their name to view their profile</p>

          <div class="btn-row">
            <button class="btn-ghost" :disabled="responding" @click="respondToRequest('reject')">
              Decline
            </button>
            <button class="btn-primary" :disabled="responding" @click="respondToRequest('accept')">
              {{ responding ? 'Please wait...' : 'Accept' }}
            </button>
          </div>

        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get_api, post_api } from '@/services/api.js'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// ── State ─────────────────────────────────────────────────────────────────────

const friends = ref([])
const loading = ref(true)
const error   = ref(null)

const pendingRequests = ref([])

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

// ── Toast ─────────────────────────────────────────────────────────────────────

const toast = reactive({ visible: false, message: '', type: 'success' })
let toastTimer = null

function showToast(message, type = 'success') {
  clearTimeout(toastTimer)
  toast.message = message
  toast.type    = type
  toast.visible = true
  toastTimer = setTimeout(() => { toast.visible = false }, 3500)
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
    friends.value = await get_api(`/api/friendships/${authStore.pseudo}/friends`)
  } catch {
    error.value = 'Could not load your friends. Please try again.'
  } finally {
    loading.value = false
  }
}

async function loadPendingRequests() {
  try {
    pendingRequests.value = await get_api(`/api/friendships/${authStore.pseudo}/pending`)
  } catch {
    pendingRequests.value = []
  }
}

// ── Pending request popup ────────────────────────────────────────────────────

const selectedRequest = ref(null)
const responding      = ref(false)

function openRequestPopup(req) {
  selectedRequest.value = req
}

function closeRequestPopup() {
  selectedRequest.value = null
}

function goToProfile(pseudo) {
  closeRequestPopup()
  router.push(`/users/${pseudo}`)
}

async function respondToRequest(action) {
  if (!selectedRequest.value) return
  responding.value = true
  const requesterPseudo = selectedRequest.value.pseudo
  const endpoint = action === 'accept' ? 'accept-friend' : 'reject-friend'

  try {
    await post_api(
      `/api/friendships/${authStore.pseudo}/${endpoint}?friendPseudo=${encodeURIComponent(requesterPseudo)}`,
      {}
    )
    showToast(
      action === 'accept' ? `You're now friends with ${requesterPseudo}.` : `Request from ${requesterPseudo} declined.`,
      'success'
    )
    closeRequestPopup()
    await Promise.all([loadFriends(), loadPendingRequests()])
  } catch {
    showToast('Could not process this request. Please try again.', 'error')
  } finally {
    responding.value = false
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(() => {
  loadFriends()
  loadPendingRequests()
})
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
  color: var(--c-heading);
  font-weight: 700;
  margin-bottom: 0.2rem;
}
.page-subtitle { font-size: 0.82rem; color: var(--c-text-muted); }

/* ── Pending requests ── */
.pending-card {
  background: #fff8ee;
  border: 1.5px solid #ffe7bf;
  border-radius: 14px;
  padding: 1.2rem 1.5rem;
}

.pending-header {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  margin-bottom: 0.9rem;
}

.section-title {
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #f5a623;
}

.pending-count {
  background: #f5a623;
  color: #fff;
  font-size: 0.72rem;
  font-weight: 700;
  border-radius: 999px;
  padding: 0.1rem 0.55rem;
}

.pending-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
}

.pending-chip {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #fff;
  border: 1px solid #ffe7bf;
  border-radius: 999px;
  padding: 0.35rem 0.9rem 0.35rem 0.35rem;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.15s;
}
.pending-chip:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transform: translateY(-1px);
}

.pending-avatar {
  width: 28px; height: 28px;
  border-radius: 50%;
  background: #f5a623;
  color: #fff;
  font-size: 0.78rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.pending-pseudo {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--c-text);
}

/* ── Filters ── */
.filters-card {
  background: var(--c-card);
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
  color: var(--c-text-secondary);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

input[type="text"],
input[type="number"],
select {
  border: 1.5px solid var(--c-border);
  border-radius: 8px;
  padding: 0.5rem 0.75rem;
  font-size: 0.88rem;
  color: var(--c-text);
  outline: none;
  background: var(--c-input-bg);
  transition: border-color 0.2s;
  width: 100%;
}
input:focus, select:focus { border-color: var(--c-brand); background: var(--c-card); }

/* ── Friends grid ── */
.friends-grid {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.friend-card {
  background: var(--c-card);
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
  background: var(--c-brand);
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
  color: var(--c-text);
}
.friend-since {
  font-size: 0.78rem;
  color: var(--c-text-muted);
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
.badge--role     { background: var(--c-ghost-bg); color: var(--c-ghost-text); }
.badge--accepted { background: var(--c-brand-soft); color: var(--c-brand); }
.badge--pending  { background: #fff8ee; color: #f5a623; }
.badge--blocked  { background: #fde8e8; color: #e74c3c; }

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
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
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

/* ── Buttons ── */
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
.btn-ghost:hover:not(:disabled) { opacity: 0.8; }
.btn-ghost:disabled { opacity: 0.5; cursor: not-allowed; }

.btn-primary {
  border: none;
  border-radius: 8px;
  padding: 0.55rem 1.2rem;
  font-size: 0.88rem;
  cursor: pointer;
  font-weight: 600;
  background: var(--c-brand);
  color: #fff;
  transition: opacity 0.2s;
}
.btn-primary:hover:not(:disabled) { opacity: 0.85; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

/* ── Toast ── */
.toast-notif {
  position: fixed;
  bottom: 1.5rem;
  left: 50%;
  transform: translateX(-50%);
  padding: 0.7rem 1.4rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #fff;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  white-space: nowrap;
}
.toast-notif--success { background: var(--c-brand); }
.toast-notif--error   { background: #e74c3c; }

.toast-enter-active, .toast-leave-active { transition: opacity 0.3s, transform 0.3s; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateX(-50%) translateY(10px); }

/* ── Modal ── */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  padding: 1rem;
}

.modal-box {
  background: var(--c-card);
  border-radius: 16px;
  padding: 1.8rem;
  width: 100%;
  max-width: 380px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
  position: relative;
}

.modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  border: none;
  background: none;
  font-size: 1rem;
  color: var(--c-text-muted);
  cursor: pointer;
  line-height: 1;
  padding: 0.2rem;
}
.modal-close:hover { color: var(--c-ghost-text); }

.modal-box h3 {
  font-size: 1.05rem;
  color: var(--c-heading);
  margin-bottom: 1.2rem;
}

.request-profile {
  display: flex;
  align-items: center;
  gap: 0.9rem;
  width: 100%;
  border: 1.5px solid var(--c-divider);
  border-radius: 12px;
  padding: 0.8rem 1rem;
  background: var(--c-input-bg);
  cursor: pointer;
  text-align: left;
  transition: border-color 0.2s, background 0.2s;
}
.request-profile:hover {
  border-color: var(--c-brand);
  background: var(--c-row-hover);
}

.request-avatar {
  width: 48px; height: 48px;
  border-radius: 50%;
  background: var(--c-brand);
  color: #fff;
  font-size: 1.2rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.request-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}
.request-info strong { font-size: 0.98rem; color: var(--c-text); }

.request-chevron { color: var(--c-border); flex-shrink: 0; }

.request-hint {
  font-size: 0.78rem;
  color: var(--c-text-muted);
  margin-top: 0.7rem;
}

.btn-row {
  display: flex;
  gap: 0.8rem;
  margin-top: 1.4rem;
}
.btn-row .btn-ghost,
.btn-row .btn-primary {
  flex: 1;
}

.modal-enter-active, .modal-leave-active { transition: opacity 0.25s, transform 0.25s; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.96); }

/* ── Responsive ── */
@media (max-width: 560px) {
  .friend-card { flex-wrap: wrap; }
  .friend-badges { order: 3; }
}
</style>