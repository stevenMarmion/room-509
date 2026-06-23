<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />

    <div class="admin-tabs">
      <button class="admin-tab" :class="{ 'admin-tab--active': tab === 'food' }"     @click="tab = 'food'">Food</button>
      <button class="admin-tab" :class="{ 'admin-tab--active': tab === 'upgrades' }" @click="tab = 'upgrades'">Upgrades</button>
      <button class="admin-tab" :class="{ 'admin-tab--active': tab === 'fish' }"     @click="tab = 'fish'">Fish (shop)</button>
    </div>

    <!-- ── FOOD ── -->
    <template v-if="tab === 'food'">
      <div class="admin-toolbar">
        <input v-model="search" class="admin-search" placeholder="Search food…" />
        <button class="btn-create" @click="openCreate('food')">+ New Food</button>
      </div>
      <div v-if="loading" class="admin-state">Loading…</div>
      <div v-else class="admin-table-wrap">
        <table class="admin-table">
          <thead><tr><th>ID</th><th>Name</th><th>Price</th><th>Nutrition</th><th>Actions</th></tr></thead>
          <tbody>
            <tr v-for="item in filteredFood" :key="item.id">
              <td class="td-muted">{{ item.id }}</td>
              <td class="td-bold">{{ item.name }}</td>
              <td><span class="badge badge--gold">{{ item.price }}</span></td>
              <td><span class="badge badge--teal">{{ item.nutritionValue }}</span></td>
              <td class="td-actions">
                <button class="btn-edit" @click="openEdit('food', item)">Edit</button>
                <button class="btn-del"  @click="remove('food', item.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <!-- ── UPGRADES ── -->
    <template v-if="tab === 'upgrades'">
      <div class="admin-toolbar">
        <input v-model="search" class="admin-search" placeholder="Search upgrade…" />
        <button class="btn-create" @click="openCreate('upgrades')">+ New Upgrade</button>
      </div>
      <div v-if="loading" class="admin-state">Loading…</div>
      <div v-else class="admin-table-wrap">
        <table class="admin-table">
          <thead><tr><th>ID</th><th>Name</th><th>Price</th><th>Capacity +</th><th>Level +</th><th>Actions</th></tr></thead>
          <tbody>
            <tr v-for="item in filteredUpgrades" :key="item.id">
              <td class="td-muted">{{ item.id }}</td>
              <td class="td-bold">{{ item.name }}</td>
              <td><span class="badge badge--gold">{{ item.price }}</span></td>
              <td><span class="badge badge--teal">+{{ item.capacityBonus }}</span></td>
              <td><span class="badge badge--green">+{{ item.levelBonus }}</span></td>
              <td class="td-actions">
                <button class="btn-edit" @click="openEdit('upgrades', item)">Edit</button>
                <button class="btn-del"  @click="remove('upgrades', item.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <!-- ── FISH SHOP ── -->
    <template v-if="tab === 'fish'">
      <div class="admin-toolbar">
        <input v-model="search" class="admin-search" placeholder="Search fish…" />
        <button class="btn-create" @click="openCreate('fish')">+ New Shop Fish</button>
      </div>
      <div v-if="loading" class="admin-state">Loading…</div>
      <div v-else class="admin-table-wrap">
        <table class="admin-table">
          <thead><tr><th>ID</th><th>Name</th><th>Species</th><th>Color</th><th>Price</th><th>Size</th><th>Actions</th></tr></thead>
          <tbody>
            <tr v-for="item in filteredFish" :key="item.id">
              <td class="td-muted">{{ item.id }}</td>
              <td class="td-bold">{{ item.name }}</td>
              <td>{{ item.species ?? '—' }}</td>
              <td>{{ item.color ?? '—' }}</td>
              <td><span class="badge badge--gold">{{ item.price }}</span></td>
              <td>{{ item.size }}</td>
              <td class="td-actions">
                <button class="btn-edit" @click="openEdit('fish', item)">Edit</button>
                <button class="btn-del"  @click="remove('fish', item.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <!-- Modal -->
    <div v-if="modal.open" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal__title">{{ modal.mode === 'create' ? 'New' : 'Edit' }} {{ modal.type }}</h2>
        <div class="admin-form">
          <label>Name<input v-model="modal.data.name" /></label>
          <label>Price<input type="number" v-model.number="modal.data.price" /></label>
          <template v-if="modal.type === 'food'">
            <label>Nutrition Value<input type="number" v-model.number="modal.data.nutritionValue" /></label>
          </template>
          <template v-if="modal.type === 'upgrades'">
            <label>Capacity Bonus<input type="number" v-model.number="modal.data.capacityBonus" /></label>
            <label>Level Bonus<input type="number" v-model.number="modal.data.levelBonus" /></label>
          </template>
          <template v-if="modal.type === 'fish'">
            <label>Species<input v-model="modal.data.species" /></label>
            <label>Color<input v-model="modal.data.color" /></label>
            <label>Size<input type="number" v-model.number="modal.data.size" /></label>
          </template>
        </div>
        <div class="admin-modal__actions">
          <button class="btn-cancel" @click="closeModal">Cancel</button>
          <button class="btn-save" :disabled="saving" @click="save">{{ saving ? 'Saving…' : 'Save' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive, watch } from 'vue'
import { get_api, post_api, put_api, delete_api } from '@/services/api.js'
import AdminToast from './AdminToast.vue'

const tab     = ref('food')
const search  = ref('')
const saving  = ref(false)
const loading = ref(false)
const toast   = reactive({ visible: false, message: '', type: 'success' })
const modal   = reactive({ open: false, mode: 'create', type: 'food', data: {} })

const food     = ref([])
const upgrades = ref([])
const fish     = ref([])

let toastTimer = null
function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  Object.assign(toast, { visible: true, message: msg, type })
  toastTimer = setTimeout(() => { toast.visible = false }, 3000)
}

const filteredFood     = computed(() => food.value.filter(f => f.name?.toLowerCase().includes(search.value.toLowerCase())))
const filteredUpgrades = computed(() => upgrades.value.filter(u => u.name?.toLowerCase().includes(search.value.toLowerCase())))
const filteredFish     = computed(() => fish.value.filter(f => f.name?.toLowerCase().includes(search.value.toLowerCase())))

async function load() {
  loading.value = true
  try {
    ;[food.value, upgrades.value, fish.value] = await Promise.all([
      get_api('/api/shop/food'),
      get_api('/api/shop/upgrades'),
      get_api('/api/shop/fish'),
    ])
  } catch { showToast('Load failed', 'error') }
  finally { loading.value = false }
}

watch(tab, () => { search.value = '' })

const defaults = {
  food:     { name: '', price: 0, nutritionValue: 10 },
  upgrades: { name: '', price: 0, capacityBonus: 0, levelBonus: 0 },
  fish:     { name: '', price: 10, species: '', color: '', size: 1 },
}

function openCreate(type) { Object.assign(modal, { open: true, mode: 'create', type, data: { ...defaults[type] } }) }
function openEdit(type, item) { Object.assign(modal, { open: true, mode: 'edit', type, data: { ...item } }) }
function closeModal() { modal.open = false }

const listFor = { food, upgrades, fish }
const urlFor  = { food: '/api/shop/food', upgrades: '/api/shop/upgrades', fish: '/api/shop/fish' }

async function save() {
  saving.value = true
  const url  = urlFor[modal.type]
  const list = listFor[modal.type]
  try {
    if (modal.mode === 'create') {
      const created = await post_api(url, modal.data)
      list.value.push(created)
    } else {
      const updated = await put_api(`${url}/${modal.data.id}`, modal.data)
      const idx = list.value.findIndex(i => i.id === modal.data.id)
      if (idx !== -1) list.value[idx] = updated
    }
    showToast('Saved')
    closeModal()
  } catch { showToast('Save failed', 'error') }
  finally { saving.value = false }
}

async function remove(type, id) {
  if (!confirm('Delete?')) return
  try {
    await delete_api(`${urlFor[type]}/${id}`)
    listFor[type].value = listFor[type].value.filter(i => i.id !== id)
    showToast('Deleted')
  } catch { showToast('Delete failed', 'error') }
}

onMounted(load)
</script>

<style scoped src="@/assets/admin.css" />