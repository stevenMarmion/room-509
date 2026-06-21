<template>
  <main class="trades-layout">

    <!-- ── Toast ── -->
    <Transition name="toast">
      <div v-if="toast.visible" class="toast-notif" :class="`toast-notif--${toast.type}`">
        {{ toast.message }}
      </div>
    </Transition>

    <!-- ── Header ── -->
    <div class="page-header">
      <div>
        <h1>Trades</h1>
        <p class="page-subtitle">{{ filteredTrades.length }} trade{{ filteredTrades.length !== 1 ? 's' : '' }} found</p>
      </div>
    </div>

    <!-- ── Filters ── -->
    <div class="filters-card">

      <div class="filter-group">
        <label>Friend</label>
        <input type="text" v-model="filters.friend" placeholder="Search by pseudo..." />
      </div>

      <div class="filter-group">
        <label>Status</label>
        <select v-model="filters.status">
          <option value="">All</option>
          <option value="PENDING">Pending</option>
          <option value="ACCEPTED">Accepted</option>
          <option value="REJECTED">Rejected</option>
        </select>
      </div>

      <div class="filter-group">
        <label>Direction</label>
        <select v-model="filters.direction">
          <option value="">All</option>
          <option value="SENT">Sent by me</option>
          <option value="RECEIVED">Received by me</option>
        </select>
      </div>

      <div class="filter-group">
        <label>Max. price</label>
        <input type="number" v-model.number="filters.maxPrice" placeholder="Any" min="0" />
      </div>

      <div class="filter-group filter-group--reset">
        <button class="btn-ghost" @click="resetFilters">Reset filters</button>
      </div>

    </div>

    <!-- ── Loading ── -->
    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Loading trades...</p>
    </div>

    <!-- ── Error ── -->
    <div v-else-if="error" class="state-box state-box--error">
      <p>{{ error }}</p>
      <button class="btn-ghost" @click="loadTrades">Try again</button>
    </div>

    <!-- ── Empty ── -->
    <div v-else-if="filteredTrades.length === 0" class="state-box">
      <p>No trades match your filters.</p>
    </div>

    <!-- ── Trades list ── -->
    <div v-else class="trades-list">
      <button
        v-for="trade in filteredTrades"
        :key="trade.id"
        class="trade-card"
        @click="openTradePopup(trade)"
      >
        <div class="trade-avatar">{{ otherParty(trade).charAt(0).toUpperCase() }}</div>

        <div class="trade-info">
          <strong class="trade-pseudo">
            {{ direction(trade) === 'SENT' ? 'To' : 'From' }} {{ otherParty(trade) }}
          </strong>
          <span class="trade-fish-names">{{ fishNames(trade) }}</span>
        </div>

        <div class="trade-meta">
          <span class="trade-price">
            <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor"><circle cx="12" cy="12" r="10"/></svg>
            {{ trade.price }}
          </span>
          <span class="badge" :class="statusClass(trade.status)">{{ trade.status }}</span>
        </div>
      </button>
    </div>

  </main>

  <!-- ── Trade detail popup ── -->
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="selectedTrade" class="modal-backdrop" @click.self="closeTradePopup">
        <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="trade-modal-title">

          <button class="modal-close" @click="closeTradePopup" aria-label="Close">✕</button>

          <h3 id="trade-modal-title">Trade details</h3>

          <div class="trade-detail-header">
            <div class="trade-avatar trade-avatar--lg">{{ otherParty(selectedTrade).charAt(0).toUpperCase() }}</div>
            <div>
              <strong>{{ otherParty(selectedTrade) }}</strong>
              <span class="trade-direction">
                {{ direction(selectedTrade) === 'SENT' ? 'You proposed this trade' : 'They proposed this trade' }}
              </span>
            </div>
          </div>

          <div class="trade-detail-rows">
            <div class="detail-row">
              <span>Fish</span>
              <strong>{{ fishNames(selectedTrade) }}</strong>
            </div>
            <div class="detail-row">
              <span>Price</span>
              <strong class="price-highlight">{{ selectedTrade.price }} coins</strong>
            </div>
            <div class="detail-row">
              <span>Date</span>
              <strong>{{ formatDate(selectedTrade.createdAt) }}</strong>
            </div>
            <div class="detail-row">
              <span>Status</span>
              <span class="badge" :class="statusClass(selectedTrade.status)">{{ selectedTrade.status }}</span>
            </div>
          </div>

          <!-- Actions only for pending trades received by me -->
          <div v-if="selectedTrade.status === 'PENDING' && direction(selectedTrade) === 'RECEIVED'" class="btn-row">
            <button class="btn-ghost" :disabled="responding" @click="respond('reject')">
              {{ responding ? 'Please wait...' : 'Reject' }}
            </button>
            <button class="btn-primary" :disabled="responding" @click="respond('accept')">
              {{ responding ? 'Please wait...' : 'Accept' }}
            </button>
          </div>

          <p v-else-if="selectedTrade.status === 'PENDING'" class="trade-hint">
            Waiting for {{ otherParty(selectedTrade) }} to respond.
          </p>

        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { get_api, post_api } from '@/services/api.js'
import { useAuthStore } from '@/stores/auth.js'

const authStore = useAuthStore()

// ── State ─────────────────────────────────────────────────────────────────────

const trades  = ref([])
const loading = ref(true)
const error   = ref(null)

const filters = reactive({
  friend:    '',
  status:    '',
  direction: '',
  maxPrice:  null,
})

// ── Computed ──────────────────────────────────────────────────────────────────

const filteredTrades = computed(() =>
  trades.value.filter(t => {
    const party = otherParty(t).toLowerCase()
    if (filters.friend    && !party.includes(filters.friend.toLowerCase()))  return false
    if (filters.status    && t.status !== filters.status)                   return false
    if (filters.direction && direction(t) !== filters.direction)            return false
    if (filters.maxPrice != null && t.price > filters.maxPrice)             return false
    return true
  })
)

// ── Helpers ───────────────────────────────────────────────────────────────────

function direction(trade) {
  return trade.initiatorPseudo === authStore.pseudo ? 'SENT' : 'RECEIVED'
}

function otherParty(trade) {
  return direction(trade) === 'SENT' ? trade.receiverPseudo : trade.initiatorPseudo
}

function fishNames(trade) {
  if (!trade.fish || trade.fish.length === 0) return 'No fish'
  return trade.fish.map(f => f.name).join(', ')
}

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('en-US', { day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function statusClass(status) {
  return {
    PENDING:  'badge--pending',
    ACCEPTED: 'badge--accepted',
    REJECTED: 'badge--rejected',
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
  Object.assign(filters, { friend: '', status: '', direction: '', maxPrice: null })
}

// ── Load ──────────────────────────────────────────────────────────────────────

async function loadTrades() {
  loading.value = true
  error.value   = null
  try {
    trades.value = await get_api(`/api/trades/user/${authStore.pseudo}`)
  } catch {
    error.value = 'Could not load trades. Please try again.'
  } finally {
    loading.value = false
  }
}

// ── Popup ─────────────────────────────────────────────────────────────────────

const selectedTrade = ref(null)
const responding    = ref(false)

function openTradePopup(trade) {
  selectedTrade.value = trade
}

function closeTradePopup() {
  selectedTrade.value = null
}

async function respond(action) {
  if (!selectedTrade.value) return
  responding.value = true
  const tradeId = selectedTrade.value.id
  const endpoint = action === 'accept' ? 'accept' : 'reject'

  try {
    await post_api(`/api/trades/${tradeId}/${endpoint}`, {})
    showToast(
      action === 'accept' ? 'Trade accepted!' : 'Trade rejected.',
      'success'
    )
    closeTradePopup()
    await loadTrades()
  } catch {
    showToast('Could not process this trade. Please try again.', 'error')
  } finally {
    responding.value = false
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(loadTrades)
</script>

<style scoped>
/* ── Layout ── */
.trades-layout {
  max-width: 800px;
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

/* ── Trades list ── */
.trades-list {
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
}

.trade-card {
  background: #fff;
  border: none;
  border-radius: 14px;
  padding: 1rem 1.3rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
  cursor: pointer;
  text-align: left;
  width: 100%;
  transition: box-shadow 0.2s, transform 0.15s;
}
.trade-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-1px);
}

.trade-avatar {
  width: 44px; height: 44px;
  border-radius: 50%;
  background: #0d7377;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.trade-avatar--lg { width: 52px; height: 52px; font-size: 1.3rem; }

.trade-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  min-width: 0;
}
.trade-pseudo { font-size: 0.95rem; color: #1a3a4a; }
.trade-fish-names {
  font-size: 0.78rem;
  color: #aaa;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.trade-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.4rem;
  flex-shrink: 0;
}

.trade-price {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.85rem;
  font-weight: 700;
  color: #f5a623;
}

/* ── Badges ── */
.badge {
  display: inline-block;
  border-radius: 999px;
  padding: 0.2rem 0.7rem;
  font-size: 0.74rem;
  font-weight: 600;
}
.badge--pending  { background: #fff8ee; color: #f5a623; }
.badge--accepted { background: #e8f7f7; color: #0d7377; }
.badge--rejected { background: #fde8e8; color: #e74c3c; }

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
  background: #0d7377;
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
.toast-notif--success { background: #0d7377; }
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
  background: #fff;
  border-radius: 16px;
  padding: 1.8rem;
  width: 100%;
  max-width: 400px;
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
  color: #aaa;
  cursor: pointer;
  line-height: 1;
  padding: 0.2rem;
}
.modal-close:hover { color: #555; }

.modal-box h3 {
  font-size: 1.05rem;
  color: #1a3a4a;
  margin-bottom: 1.2rem;
}

.trade-detail-header {
  display: flex;
  align-items: center;
  gap: 0.9rem;
  margin-bottom: 1.2rem;
}
.trade-detail-header strong {
  display: block;
  font-size: 1rem;
  color: #1a3a4a;
}
.trade-direction {
  font-size: 0.78rem;
  color: #aaa;
}

.trade-detail-rows {
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  padding: 1rem 0;
  border-top: 1px solid #f0f4f8;
  border-bottom: 1px solid #f0f4f8;
}

.detail-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.88rem;
}
.detail-row span { color: #888; }
.detail-row strong { color: #1a3a4a; text-align: right; }
.price-highlight { color: #f5a623 !important; }

.trade-hint {
  font-size: 0.82rem;
  color: #aaa;
  text-align: center;
  margin-top: 1.2rem;
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
  .trade-card { flex-wrap: wrap; }
  .trade-meta { align-items: flex-start; }
}
</style>