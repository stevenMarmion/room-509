<template>
  <main class="fishes-layout">

    <!-- ── Toast ── -->
    <Transition name="toast">
      <div v-if="toast.visible" class="toast-notif" :class="`toast-notif--${toast.type}`">
        {{ toast.message }}
      </div>
    </Transition>

    <!-- ── Header ── -->
    <div class="page-header">
      <div>
        <h1>My Fish</h1>
        <p class="page-subtitle">{{ filteredFishes.length }} fish found</p>
      </div>
    </div>

    <!-- ── Mini Aquarium ── -->
    <div v-if="fishes.length > 0 && !loading" class="mini-aquarium" ref="miniAquariumRef">
      <img :src="aquariumBg" class="mini-aquarium-bg" />
      <FishSprite
        v-for="fish in fishes.slice(0, 12)"
        :key="'aq-' + fish.id"
        :color="fish.color"
        :lifePoints="fish.lifePoints"
        :king="kingIds.has(fish.id)"
        :bounds="aquariumBounds"
      />
    </div>

    <!-- ── Filters ── -->
    <div class="filters-card">

      <!-- Search by name -->
      <div class="filter-group">
        <label>Name</label>
        <input type="text" v-model="filters.name" placeholder="Search by name..." />
      </div>

      <!-- Species -->
      <div class="filter-group">
        <label>Species</label>
        <input type="text" v-model="filters.species" placeholder="e.g. Clownfish" />
      </div>

      <!-- Color -->
      <div class="filter-group">
        <label>Color</label>
        <input type="text" v-model="filters.color" placeholder="e.g. Orange" />
      </div>

      <!-- Min life points -->
      <div class="filter-group">
        <label>Min. life points</label>
        <input type="number" v-model.number="filters.minLifePoints" placeholder="0" min="0" />
      </div>

      <!-- Max age -->
      <div class="filter-group">
        <label>Max. age</label>
        <input type="number" v-model.number="filters.maxAge" placeholder="Any" min="0" />
      </div>

      <!-- Reset -->
      <div class="filter-group filter-group--reset">
        <button class="btn-ghost" @click="resetFilters">Reset filters</button>
      </div>

    </div>

    <!-- ── Loading ── -->
    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Loading your fish...</p>
    </div>

    <!-- ── Error ── -->
    <div v-else-if="error" class="state-box state-box--error">
      <p>{{ error }}</p>
      <button class="btn-ghost" @click="loadFishes">Try again</button>
    </div>

    <!-- ── Empty ── -->
    <div v-else-if="filteredFishes.length === 0" class="state-box">
      <p>No fish match your filters.</p>
    </div>

    <!-- ── Table ── -->
    <div v-else class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th @click="sortBy('name')"      class="sortable">Name       <SortIcon field="name"        :current="sort" /></th>
            <th @click="sortBy('species')"   class="sortable">Species    <SortIcon field="species"     :current="sort" /></th>
            <th @click="sortBy('color')"     class="sortable">Color      <SortIcon field="color"       :current="sort" /></th>
            <th @click="sortBy('size')"      class="sortable">Size       <SortIcon field="size"        :current="sort" /></th>
            <th @click="sortBy('age')"       class="sortable">Age        <SortIcon field="age"         :current="sort" /></th>
            <th @click="sortBy('lifePoints')" class="sortable">Life pts   <SortIcon field="lifePoints" :current="sort" /></th>
            <th>Last fed</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="fish in filteredFishes" :key="fish.id">
            <td class="td-name">
              <div class="fish-name-cell">
                <div class="sprite-thumb">
                  <FishSprite
                    :color="fish.color"
                    :lifePoints="fish.lifePoints"
                    :king="kingIds.has(fish.id)"
                    :static="true"
                  />
                </div>
                <span>{{ fish.name }}</span>
              </div>
            </td>
            <td>{{ fish.species }}</td>
            <td>
              <span class="color-badge" :style="{ background: colorHint(fish.color) }">
                {{ fish.color }}
              </span>
            </td>
            <td>{{ fish.size }} cm</td>
            <td>{{ fish.age }} yr{{ fish.age > 1 ? 's' : '' }}</td>
            <td>
              <div class="lp-bar-wrap">
                <div class="lp-bar" :style="{ width: lifePercent(fish.lifePoints) + '%', background: lifeColor(fish.lifePoints) }"></div>
                <span class="lp-label">{{ fish.lifePoints }}</span>
              </div>
            </td>
            <td class="td-date">{{ formatDate(fish.lastFedAt) }}</td>
            <td>
              <div class="td-actions">
                <button class="icon-btn" title="Rename" @click="openEditPopup(fish)">
                  <svg viewBox="0 0 24 24" width="15" height="15" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z" />
                  </svg>
                </button>
                <button class="icon-btn" title="Trade" @click="openTradePopup(fish)">
                  <svg viewBox="0 0 24 24" width="15" height="15" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="17 1 21 5 17 9" /><path d="M3 11V9a4 4 0 0 1 4-4h14" />
                    <polyline points="7 23 3 19 7 15" /><path d="M21 13v2a4 4 0 0 1-4 4H3" />
                  </svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

  </main>

  <!-- ── Edit name popup ── -->
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="editingFish" class="modal-backdrop" @click.self="closeEditPopup">
        <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="edit-modal-title">
          <h3 id="edit-modal-title">Rename fish</h3>
          <form @submit.prevent="saveFishName">
            <div class="form-group">
              <label for="fish-name">Name</label>
              <input id="fish-name" type="text" v-model="editName" autofocus />
            </div>
            <div class="btn-row">
              <button type="button" class="btn-ghost" @click="closeEditPopup">Cancel</button>
              <button type="submit" class="btn-primary" :disabled="savingName || !editName.trim()">
                {{ savingName ? 'Saving...' : 'Save' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </Transition>
  </Teleport>

  <!-- ── Trade popup ── -->
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="tradingFish" class="modal-backdrop" @click.self="closeTradePopup">
        <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="trade-modal-title">
          <h3 id="trade-modal-title">Trade "{{ tradingFish.name }}"</h3>

          <form @submit.prevent="submitTrade">

            <div class="form-group">
              <label for="trade-friend">Friend</label>
              <select id="trade-friend" v-model="tradeForm.friendPseudo" required :disabled="loadingFriends">
                <option value="" disabled>{{ loadingFriends ? 'Loading friends...' : 'Select a friend' }}</option>
                <option v-for="f in friends" :key="f.pseudo" :value="f.pseudo">{{ f.pseudo }}</option>
              </select>
              <p v-if="!loadingFriends && friends.length === 0" class="form-hint">
                You have no friends yet to trade with.
              </p>
            </div>

            <div class="form-group" style="margin-top: 0.9rem;">
              <label for="trade-price">Price (coins)</label>
              <input id="trade-price" type="number" v-model.number="tradeForm.price" min="1" required />
            </div>

            <div class="btn-row">
              <button type="button" class="btn-ghost" @click="closeTradePopup">Cancel</button>
              <button type="submit" class="btn-primary" :disabled="submittingTrade || friends.length === 0">
                {{ submittingTrade ? 'Sending...' : 'Propose trade' }}
              </button>
            </div>

          </form>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { get_api, put_api, post_api } from '@/services/api.js'
import { useAuthStore } from '@/stores/auth.js'
import FishSprite from '@/components/FishSprite.vue'
import aquariumBg from '@/assets/far.png'

const authStore = useAuthStore()

// ── Mini aquarium ────────────────────────────────────────────────────────────
const miniAquariumRef = ref(null)
const aquariumBounds  = reactive({ width: 0, height: 0 })

async function measureAquarium() {
  await nextTick()
  if (miniAquariumRef.value) {
    const rect = miniAquariumRef.value.getBoundingClientRect()
    aquariumBounds.width  = rect.width
    aquariumBounds.height = rect.height
  }
}

// ── Sort icon sub-component ───────────────────────────────────────────────────

import { h } from 'vue'

const SortIcon = {
  props: ['field', 'current'],
  setup(props) {
    return () => h('span', { class: 'sort-icon' }, [
      h('span', { style: { opacity: props.current.field === props.field && props.current.dir === 'asc'  ? 1 : 0.25 } }, '▲'),
      h('span', { style: { opacity: props.current.field === props.field && props.current.dir === 'desc' ? 1 : 0.25 } }, '▼'),
    ])
  }
}

// ── King fish (oldest per color) ─────────────────────────────────────────────

const kingIds = computed(() => {
  const oldest = {}
  for (const fish of fishes.value) {
    const c = fish.color?.toLowerCase()
    if (!c) continue
    if (!oldest[c] || fish.age > oldest[c].age) oldest[c] = fish
  }
  return new Set(Object.values(oldest).map(f => f.id))
})

// ── State ─────────────────────────────────────────────────────────────────────

const fishes  = ref([])
const loading = ref(true)
const error   = ref(null)

const filters = reactive({
  name:          '',
  species:       '',
  color:         '',
  minLifePoints: null,
  maxAge:        null,
})

const sort = reactive({ field: 'name', dir: 'asc' })

// ── Computed ──────────────────────────────────────────────────────────────────

const filteredFishes = computed(() => {
  let result = fishes.value.filter(f => {
    if (filters.name    && !f.name.toLowerCase().includes(filters.name.toLowerCase()))       return false
    if (filters.species && !f.species.toLowerCase().includes(filters.species.toLowerCase())) return false
    if (filters.color   && !f.color.toLowerCase().includes(filters.color.toLowerCase()))     return false
    if (filters.minLifePoints != null && f.lifePoints < filters.minLifePoints)               return false
    if (filters.maxAge        != null && f.age > filters.maxAge)                             return false
    return true
  })

  result = [...result].sort((a, b) => {
    const valA = a[sort.field]
    const valB = b[sort.field]
    const cmp  = typeof valA === 'string'
      ? valA.localeCompare(valB)
      : valA - valB
    return sort.dir === 'asc' ? cmp : -cmp
  })

  return result
})

// ── Helpers ───────────────────────────────────────────────────────────────────

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleString('en-US', {
    day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'
  })
}

function lifePercent(lp) { return Math.min(100, Math.max(0, lp)) }
function lifeColor(lp) {
  if (lp >= 70) return '#0d7377'
  if (lp >= 35) return '#f5a623'
  return '#e74c3c'
}

const COLOR_MAP = {
  red: '#ffe5e5', orange: '#fff3e0', yellow: '#fffde7',
  green: '#e8f5e9', blue: '#e3f2fd', purple: '#f3e5f5',
  pink: '#fce4ec', white: '#f5f5f5', black: '#e0e0e0',
  grey: '#eeeeee', gray: '#eeeeee', brown: '#efebe9',
}
function colorHint(color) {
  return COLOR_MAP[color?.toLowerCase()] ?? '#e8f7f7'
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

// ── Sort ──────────────────────────────────────────────────────────────────────

function sortBy(field) {
  if (sort.field === field) {
    sort.dir = sort.dir === 'asc' ? 'desc' : 'asc'
  } else {
    sort.field = field
    sort.dir   = 'asc'
  }
}

// ── Filters ───────────────────────────────────────────────────────────────────

function resetFilters() {
  Object.assign(filters, { name: '', species: '', color: '', minLifePoints: null, maxAge: null })
}

// ── Load fish ─────────────────────────────────────────────────────────────────

async function loadFishes() {
  loading.value = true
  error.value   = null
  try {
    fishes.value = await get_api(`/api/users/${authStore.pseudo}/fishes`)
  } catch {
    error.value = 'Could not load your fish. Please try again.'
  } finally {
    loading.value = false
  }
}

// ── Edit name popup ───────────────────────────────────────────────────────────

const editingFish = ref(null)
const editName    = ref('')
const savingName  = ref(false)

function openEditPopup(fish) {
  editingFish.value = fish
  editName.value    = fish.name
}

function closeEditPopup() {
  editingFish.value = null
}

async function saveFishName() {
  const newName = editName.value.trim()
  if (!newName || !editingFish.value) return

  savingName.value = true
  try {
    await put_api(`/api/fish/${editingFish.value.id}`, newName)
    editingFish.value.name = newName
    showToast('Fish renamed successfully.', 'success')
    closeEditPopup()
  } catch {
    showToast('Could not rename fish. Please try again.', 'error')
  } finally {
    savingName.value = false
  }
}

// ── Trade popup ───────────────────────────────────────────────────────────────

const tradingFish     = ref(null)
const friends         = ref([])
const loadingFriends  = ref(false)
const submittingTrade = ref(false)

const tradeForm = reactive({ friendPseudo: '', price: 50 })

async function openTradePopup(fish) {
  tradingFish.value = fish
  tradeForm.friendPseudo = ''
  tradeForm.price = 50
  await loadFriends()
}

function closeTradePopup() {
  tradingFish.value = null
}

async function loadFriends() {
  loadingFriends.value = true
  try {
    const allFriendships = await get_api(`/api/friendships/${authStore.pseudo}/friends`)
    friends.value = allFriendships.filter(f => f.status === 'ACCEPTED')
  } catch {
    friends.value = []
  } finally {
    loadingFriends.value = false
  }
}

async function submitTrade() {
  if (!tradingFish.value || !tradeForm.friendPseudo) return

  submittingTrade.value = true
  try {
    // Fetch receiver's id from their pseudo
    const receiverUser = await get_api(`/api/users/${tradeForm.friendPseudo}`)

    await post_api('/api/trades', {
      initiatorPseudo: authStore.pseudo,
      receiverId:      receiverUser.id,
      price:           tradeForm.price,
      fishIds:         [tradingFish.value.id],
    })

    showToast(`Trade proposed to ${tradeForm.friendPseudo}.`, 'success')
    closeTradePopup()
  } catch {
    showToast('Could not propose this trade. Please try again.', 'error')
  } finally {
    submittingTrade.value = false
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(async () => {
  await loadFishes()
  await measureAquarium()
  window.addEventListener('resize', measureAquarium)
})

onUnmounted(() => {
  window.removeEventListener('resize', measureAquarium)
})
</script>

<style scoped>
/* ── Layout ── */
.fishes-layout {
  max-width: 1100px;
  margin: 2rem auto;
  padding: 0 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

/* ── Header ── */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
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

/* ── Table ── */
.table-wrapper {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

thead tr {
  border-bottom: 2px solid #f0f4f8;
}

th {
  padding: 0.9rem 1.2rem;
  text-align: left;
  font-size: 0.78rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #0d7377;
  white-space: nowrap;
  user-select: none;
}
th.sortable { cursor: pointer; }
th.sortable:hover { color: #0a5c60; }

.sort-icon {
  display: inline-flex;
  flex-direction: column;
  font-size: 0.55rem;
  line-height: 1;
  vertical-align: middle;
  margin-left: 0.3rem;
  gap: 1px;
}

tbody tr {
  border-bottom: 1px solid #f5f7fa;
  transition: background 0.15s;
}
tbody tr:last-child { border-bottom: none; }
tbody tr:hover { background: #f8fbfb; }

td {
  padding: 0.85rem 1.2rem;
  color: #1a3a4a;
  vertical-align: middle;
}

/* ── Mini aquarium ── */
.mini-aquarium {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
}
.mini-aquarium-bg {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ── Sprite in table ── */
.fish-name-cell {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.sprite-thumb {
  width: 32px;
  height: 32px;
  overflow: hidden;
  flex-shrink: 0;
}
.sprite-thumb > * {
  transform: scale(0.5);
  transform-origin: top left;
}

.td-name { font-weight: 600; }
.td-date { font-size: 0.82rem; color: #aaa; white-space: nowrap; }

/* ── Actions ── */
.td-actions {
  display: flex;
  gap: 0.4rem;
}

.icon-btn {
  width: 30px; height: 30px;
  border: 1.5px solid #dde3ea;
  border-radius: 8px;
  background: #fff;
  color: #555;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s, background 0.2s;
}
.icon-btn:hover {
  border-color: #0d7377;
  color: #0d7377;
  background: #f8fbfb;
}

/* ── Color badge ── */
.color-badge {
  display: inline-block;
  border-radius: 999px;
  padding: 0.2rem 0.75rem;
  font-size: 0.8rem;
  color: #1a3a4a;
  font-weight: 500;
}

/* ── Life points bar ── */
.lp-bar-wrap {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
.lp-bar {
  height: 6px;
  border-radius: 999px;
  min-width: 4px;
  max-width: 80px;
  width: 80px;
  transition: width 0.3s;
}
.lp-label {
  font-size: 0.82rem;
  color: #555;
  min-width: 24px;
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
}

.modal-box h3 {
  font-size: 1.05rem;
  color: #1a3a4a;
  margin-bottom: 1.2rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}
.form-group label {
  font-size: 0.82rem;
  color: #666;
  font-weight: 500;
}
.form-hint {
  font-size: 0.78rem;
  color: #e74c3c;
  margin-top: 0.3rem;
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
@media (max-width: 600px) {
  th, td { padding: 0.7rem 0.8rem; }
}
</style>