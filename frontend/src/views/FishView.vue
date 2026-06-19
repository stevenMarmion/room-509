<template>
  <main class="fishes-layout">

    <!-- ── Header ── -->
    <div class="page-header">
      <div>
        <h1>My Fish</h1>
        <p class="page-subtitle">{{ filteredFishes.length }} fish found</p>
      </div>
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
          </tr>
        </thead>
        <tbody>
          <tr v-for="fish in filteredFishes" :key="fish.id">
            <td class="td-name">{{ fish.name }}</td>
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
          </tr>
        </tbody>
      </table>
    </div>

  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { get_api } from '@/services/api.js'
import { useAuthStore } from '@/stores/auth.js'

const authStore = useAuthStore()

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

// Map a life points value (0–100) to a color
function lifePercent(lp) { return Math.min(100, Math.max(0, lp)) }
function lifeColor(lp) {
  if (lp >= 70) return '#0d7377'
  if (lp >= 35) return '#f5a623'
  return '#e74c3c'
}

// Give a subtle tinted background based on the color name
const COLOR_MAP = {
  red: '#ffe5e5', orange: '#fff3e0', yellow: '#fffde7',
  green: '#e8f5e9', blue: '#e3f2fd', purple: '#f3e5f5',
  pink: '#fce4ec', white: '#f5f5f5', black: '#e0e0e0',
  grey: '#eeeeee', gray: '#eeeeee', brown: '#efebe9',
}
function colorHint(color) {
  return COLOR_MAP[color?.toLowerCase()] ?? '#e8f7f7'
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

// ── Load ──────────────────────────────────────────────────────────────────────

async function loadFishes() {
  loading.value = true
  error.value   = null
  try {
    // Replace 'alice' with the actual logged-in user pseudo
    fishes.value = await get_api(`/api/users/${authStore.pseudo}/fishes`)
  } catch {
    error.value = 'Could not load your fish. Please try again.'
  } finally {
    loading.value = false
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(loadFishes)
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
input[type="number"] {
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
input:focus { border-color: #0d7377; background: #fff; }

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

.td-name { font-weight: 600; }
.td-date { font-size: 0.82rem; color: #aaa; white-space: nowrap; }

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
  width: 100%;
}
.btn-ghost:hover { opacity: 0.8; }

/* ── Responsive ── */
@media (max-width: 600px) {
  th, td { padding: 0.7rem 0.8rem; }
}
</style>