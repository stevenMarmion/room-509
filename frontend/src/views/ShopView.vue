<template>
  <main class="shop-layout">

    <!-- ── Toast ── -->
    <Transition name="toast">
      <div v-if="toast.visible" class="toast-notif" :class="`toast-notif--${toast.type}`">
        {{ toast.message }}
      </div>
    </Transition>

    <!-- ── Header ── -->
    <div class="page-header">
      <div>
        <h1>Shop</h1>
        <p class="page-subtitle">Spend your coins wisely</p>
      </div>
      <div class="coins-display">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><circle cx="12" cy="12" r="10"/></svg>
        {{ userCoins.toLocaleString('fr-FR') }} coins
      </div>
    </div>

    <!-- ── Tabs ── -->
    <div class="tabs">
      <button class="tab" :class="{ 'tab--active': activeTab === 'food' }"     @click="activeTab = 'food'">
        Food
      </button>
      <button class="tab" :class="{ 'tab--active': activeTab === 'upgrades' }" @click="activeTab = 'upgrades'">
        Aquarium upgrades
      </button>
    </div>

    <!-- ══ FOOD TAB ══ -->
    <template v-if="activeTab === 'food'">

      <!-- Filters -->
      <div class="filters-card">
        <div class="filter-group">
          <label>Name</label>
          <input type="text" v-model="foodFilters.name" placeholder="Search food..." />
        </div>
        <div class="filter-group">
          <label>Max. price</label>
          <input type="number" v-model.number="foodFilters.maxPrice" placeholder="Any" min="0" />
        </div>
        <div class="filter-group">
          <label>Min. nutrition</label>
          <input type="number" v-model.number="foodFilters.minNutrition" placeholder="0" min="0" />
        </div>
        <div class="filter-group filter-group--reset">
          <button class="btn-ghost" @click="resetFoodFilters">Reset</button>
        </div>
      </div>

      <!-- State: loading -->
      <div v-if="loadingFood" class="state-box">
        <div class="spinner"></div>
        <p>Loading food...</p>
      </div>

      <!-- State: error -->
      <div v-else-if="errorFood" class="state-box state-box--error">
        <p>{{ errorFood }}</p>
        <button class="btn-ghost" @click="loadFood">Try again</button>
      </div>

      <!-- State: empty -->
      <div v-else-if="filteredFood.length === 0" class="state-box">
        <p>No food matches your filters.</p>
      </div>

      <!-- Table -->
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th @click="sortFood('name')"          class="sortable">Name          <SortIcon field="name"          :current="foodSort" /></th>
              <th @click="sortFood('price')"         class="sortable">Price         <SortIcon field="price"         :current="foodSort" /></th>
              <th @click="sortFood('nutritionValue')" class="sortable">Nutrition     <SortIcon field="nutritionValue" :current="foodSort" /></th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredFood" :key="item.id">
              <td class="td-name">{{ item.name }}</td>
              <td>
                <span class="price-badge">
                  <svg viewBox="0 0 24 24" width="11" height="11" fill="currentColor"><circle cx="12" cy="12" r="10"/></svg>
                  {{ item.price }}
                </span>
              </td>
              <td>
                <div class="nutrition-bar-wrap">
                  <div class="nutrition-bar" :style="{ width: Math.min(item.nutritionValue, 100) + '%' }"></div>
                  <span class="nutrition-label">{{ item.nutritionValue }}</span>
                </div>
              </td>
              <td>
                <button
                  class="btn-buy"
                  :disabled="userCoins < item.price"
                  :title="userCoins < item.price ? 'Not enough coins' : ''"
                  @click="buy('food', item)"
                >
                  Buy
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </template>

    <!-- ══ UPGRADES TAB ══ -->
    <template v-if="activeTab === 'upgrades'">

      <!-- Filters -->
      <div class="filters-card">
        <div class="filter-group">
          <label>Name</label>
          <input type="text" v-model="upgradeFilters.name" placeholder="Search upgrade..." />
        </div>
        <div class="filter-group">
          <label>Max. price</label>
          <input type="number" v-model.number="upgradeFilters.maxPrice" placeholder="Any" min="0" />
        </div>
        <div class="filter-group">
          <label>Min. capacity bonus</label>
          <input type="number" v-model.number="upgradeFilters.minCapacity" placeholder="0" min="0" />
        </div>
        <div class="filter-group">
          <label>Min. level bonus</label>
          <input type="number" v-model.number="upgradeFilters.minLevel" placeholder="0" min="0" />
        </div>
        <div class="filter-group filter-group--reset">
          <button class="btn-ghost" @click="resetUpgradeFilters">Reset</button>
        </div>
      </div>

      <!-- State: loading -->
      <div v-if="loadingUpgrades" class="state-box">
        <div class="spinner"></div>
        <p>Loading upgrades...</p>
      </div>

      <!-- State: error -->
      <div v-else-if="errorUpgrades" class="state-box state-box--error">
        <p>{{ errorUpgrades }}</p>
        <button class="btn-ghost" @click="loadUpgrades">Try again</button>
      </div>

      <!-- State: empty -->
      <div v-else-if="filteredUpgrades.length === 0" class="state-box">
        <p>No upgrades match your filters.</p>
      </div>

      <!-- Table -->
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th @click="sortUpgrade('name')"          class="sortable">Name          <SortIcon field="name"          :current="upgradeSort" /></th>
              <th @click="sortUpgrade('price')"         class="sortable">Price         <SortIcon field="price"         :current="upgradeSort" /></th>
              <th @click="sortUpgrade('capacityBonus')" class="sortable">Capacity +    <SortIcon field="capacityBonus" :current="upgradeSort" /></th>
              <th @click="sortUpgrade('levelBonus')"    class="sortable">Level +       <SortIcon field="levelBonus"    :current="upgradeSort" /></th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredUpgrades" :key="item.id">
              <td class="td-name">{{ item.name }}</td>
              <td>
                <span class="price-badge">
                  <svg viewBox="0 0 24 24" width="11" height="11" fill="currentColor"><circle cx="12" cy="12" r="10"/></svg>
                  {{ item.price }}
                </span>
              </td>
              <td><span class="bonus-badge bonus-badge--teal">+{{ item.capacityBonus }}</span></td>
              <td><span class="bonus-badge bonus-badge--green">+{{ item.levelBonus }}</span></td>
              <td>
                <button
                  class="btn-buy"
                  :disabled="userCoins < item.price"
                  :title="userCoins < item.price ? 'Not enough coins' : ''"
                  @click="buy('upgrade', item)"
                >
                  Buy
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </template>

  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { h } from 'vue'
import { get_api, post_api } from '@/services/api.js'

// ── Sort icon ─────────────────────────────────────────────────────────────────

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

const activeTab  = ref('food')
const userCoins  = ref(0)

const food          = ref([])
const loadingFood   = ref(true)
const errorFood     = ref(null)

const upgrades        = ref([])
const loadingUpgrades = ref(true)
const errorUpgrades   = ref(null)

// ── Filters ───────────────────────────────────────────────────────────────────

const foodFilters = reactive({ name: '', maxPrice: null, minNutrition: null })
const upgradeFilters = reactive({ name: '', maxPrice: null, minCapacity: null, minLevel: null })

function resetFoodFilters()    { Object.assign(foodFilters,    { name: '', maxPrice: null, minNutrition: null }) }
function resetUpgradeFilters() { Object.assign(upgradeFilters, { name: '', maxPrice: null, minCapacity: null, minLevel: null }) }

// ── Sort ──────────────────────────────────────────────────────────────────────

const foodSort    = reactive({ field: 'name', dir: 'asc' })
const upgradeSort = reactive({ field: 'name', dir: 'asc' })

function applySort(list, sort) {
  return [...list].sort((a, b) => {
    const valA = a[sort.field]
    const valB = b[sort.field]
    const cmp  = typeof valA === 'string' ? valA.localeCompare(valB) : valA - valB
    return sort.dir === 'asc' ? cmp : -cmp
  })
}

function toggleSort(sort, field) {
  if (sort.field === field) sort.dir = sort.dir === 'asc' ? 'desc' : 'asc'
  else { sort.field = field; sort.dir = 'asc' }
}

function sortFood(field)    { toggleSort(foodSort, field) }
function sortUpgrade(field) { toggleSort(upgradeSort, field) }

// ── Computed ──────────────────────────────────────────────────────────────────

const filteredFood = computed(() => {
  const filtered = food.value.filter(f => {
    if (foodFilters.name         && !f.name.toLowerCase().includes(foodFilters.name.toLowerCase())) return false
    if (foodFilters.maxPrice     != null && f.price          > foodFilters.maxPrice)                return false
    if (foodFilters.minNutrition != null && f.nutritionValue < foodFilters.minNutrition)            return false
    return true
  })
  return applySort(filtered, foodSort)
})

const filteredUpgrades = computed(() => {
  const filtered = upgrades.value.filter(u => {
    if (upgradeFilters.name        && !u.name.toLowerCase().includes(upgradeFilters.name.toLowerCase())) return false
    if (upgradeFilters.maxPrice    != null && u.price         > upgradeFilters.maxPrice)                 return false
    if (upgradeFilters.minCapacity != null && u.capacityBonus < upgradeFilters.minCapacity)              return false
    if (upgradeFilters.minLevel    != null && u.levelBonus    < upgradeFilters.minLevel)                 return false
    return true
  })
  return applySort(filtered, upgradeSort)
})

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

// ── Buy ───────────────────────────────────────────────────────────────────────

async function buy(type, item) {
  try {
    // Replace 1 with the actual logged-in user id from your Pinia store
    await post_api(`/api/shop/${type === 'food' ? 'food' : 'upgrades'}/${item.id}/buy`, { userId: 1 })
    userCoins.value -= item.price
    showToast(`${item.name} purchased successfully!`, 'success')
  } catch {
    showToast(`Could not purchase ${item.name}. Please try again.`, 'error')
  }
}

// ── Load ──────────────────────────────────────────────────────────────────────

async function loadFood() {
  loadingFood.value = true
  errorFood.value   = null
  try {
    food.value = await get_api('/api/shop/food')
  } catch {
    errorFood.value = 'Could not load food items. Please try again.'
  } finally {
    loadingFood.value = false
  }
}

async function loadUpgrades() {
  loadingUpgrades.value = true
  errorUpgrades.value   = null
  try {
    upgrades.value = await get_api('/api/shop/upgrades')
  } catch {
    errorUpgrades.value = 'Could not load upgrades. Please try again.'
  } finally {
    loadingUpgrades.value = false
  }
}

async function loadUserCoins() {
  try {
    // Replace 1 with the actual logged-in user id from your Pinia store
    const user = await get_api('/api/users/1')
    userCoins.value = user.coins
  } catch {
    console.warn('Could not load user coins.')
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(() => {
  loadFood()
  loadUpgrades()
  loadUserCoins()
})
</script>

<style scoped>
/* ── Layout ── */
.shop-layout {
  max-width: 1000px;
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
  flex-wrap: wrap;
  gap: 1rem;
}
.page-header h1 {
  font-size: 1.4rem;
  color: #1a3a4a;
  font-weight: 700;
  margin-bottom: 0.2rem;
}
.page-subtitle { font-size: 0.82rem; color: #aaa; }

.coins-display {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  background: #fff8ee;
  color: #f5a623;
  font-weight: 700;
  font-size: 0.95rem;
  border-radius: 999px;
  padding: 0.4rem 1rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
}

/* ── Tabs ── */
.tabs {
  display: flex;
  gap: 0.5rem;
  border-bottom: 2px solid #f0f4f8;
  padding-bottom: 0;
}

.tab {
  border: none;
  background: none;
  padding: 0.6rem 1.4rem;
  font-size: 0.92rem;
  font-weight: 600;
  color: #aaa;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
  border-radius: 6px 6px 0 0;
  transition: color 0.2s, border-color 0.2s;
}
.tab:hover { color: #0d7377; }
.tab--active {
  color: #0d7377;
  border-bottom-color: #0d7377;
  background: #f8fbfb;
}

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

thead tr { border-bottom: 2px solid #f0f4f8; }

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

/* ── Price badge ── */
.price-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  background: #fff8ee;
  color: #f5a623;
  font-weight: 700;
  font-size: 0.85rem;
  border-radius: 999px;
  padding: 0.2rem 0.7rem;
}

/* ── Nutrition bar ── */
.nutrition-bar-wrap {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
.nutrition-bar {
  height: 6px;
  border-radius: 999px;
  background: #0d7377;
  max-width: 80px;
  min-width: 4px;
  transition: width 0.3s;
}
.nutrition-label {
  font-size: 0.82rem;
  color: #555;
  min-width: 24px;
}

/* ── Bonus badges ── */
.bonus-badge {
  display: inline-block;
  border-radius: 999px;
  padding: 0.2rem 0.7rem;
  font-size: 0.82rem;
  font-weight: 700;
}
.bonus-badge--teal  { background: #e8f7f7; color: #0d7377; }
.bonus-badge--green { background: #e8f5e9; color: #2e7d32; }

/* ── Buy button ── */
.btn-buy {
  border: none;
  border-radius: 8px;
  padding: 0.45rem 1.1rem;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  background: #0d7377;
  color: #fff;
  transition: opacity 0.2s;
}
.btn-buy:hover:not(:disabled) { opacity: 0.85; }
.btn-buy:disabled {
  background: #dde3ea;
  color: #aaa;
  cursor: not-allowed;
}

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
</style>