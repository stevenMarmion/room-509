<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />

    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search fish…" />
      <button class="btn-create" @click="openCreate">+ New Fish</button>
    </div>

    <div v-if="loading" class="admin-state">Loading…</div>
    <div v-else-if="error" class="admin-state admin-state--error">{{ error }}</div>
    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr><th>ID</th><th>Name</th><th>Species</th><th>Color</th><th>Price</th><th>Size</th><th>Age</th><th>LP</th><th>Last fed</th><th>Actions</th></tr>
        </thead>
        <tbody>
          <tr v-for="f in filtered" :key="f.id">
            <td class="td-muted">{{ f.id }}</td>
            <td class="td-bold">{{ f.name }}</td>
            <td>{{ f.species ?? '—' }}</td>
            <td>
              <span v-if="f.color" class="color-chip" :style="{ background: f.color.toLowerCase() }"></span>
              {{ f.color ?? '—' }}
            </td>
            <td><span class="badge badge--gold">{{ f.price }}</span></td>
            <td>{{ f.size }}</td>
            <td>{{ f.age }}</td>
            <td>
              <span class="badge" :class="f.lifePoints > 50 ? 'badge--green' : 'badge--red'">{{ f.lifePoints }}</span>
            </td>
            <td class="td-muted">{{ f.lastFedAt ? new Date(f.lastFedAt).toLocaleDateString() : '—' }}</td>
            <td class="td-actions">
              <button class="btn-edit" @click="openEdit(f)">Edit</button>
              <button class="btn-del"  @click="remove(f.id)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="modal.open" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal__title">{{ modal.mode === 'create' ? 'New Fish' : 'Edit Fish' }}</h2>
        <div class="admin-form">
          <label>Name<input v-model="modal.data.name" /></label>
          <label>Species<input v-model="modal.data.species" /></label>
          <label>Color<input v-model="modal.data.color" /></label>
          <label>Price<input type="number" v-model.number="modal.data.price" /></label>
          <label>Size<input type="number" v-model.number="modal.data.size" /></label>
          <label>Age<input type="number" v-model.number="modal.data.age" /></label>
          <label>Life Points<input type="number" v-model.number="modal.data.lifePoints" /></label>
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
import { ref, computed, onMounted, reactive } from 'vue'
import { get_api, post_api, put_api, delete_api } from '@/services/api.js'
import AdminToast from './AdminToast.vue'

const items   = ref([])
const loading = ref(false)
const error   = ref(null)
const search  = ref('')
const saving  = ref(false)
const modal   = reactive({ open: false, mode: 'create', data: {} })
const toast   = reactive({ visible: false, message: '', type: 'success' })
let toastTimer = null

function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  Object.assign(toast, { visible: true, message: msg, type })
  toastTimer = setTimeout(() => { toast.visible = false }, 3000)
}

const filtered = computed(() => {
  const q = search.value.toLowerCase()
  return items.value.filter(f => f.name?.toLowerCase().includes(q) || f.species?.toLowerCase().includes(q))
})

async function load() {
  loading.value = true; error.value = null
  try { items.value = await get_api('/api/fish') }
  catch { error.value = 'Failed to load fish' }
  finally { loading.value = false }
}

function openCreate() { Object.assign(modal, { open: true, mode: 'create', data: { name: '', species: '', color: '', price: 10, size: 1, age: 0, lifePoints: 100 } }) }
function openEdit(f)  { Object.assign(modal, { open: true, mode: 'edit', data: { ...f } }) }
function closeModal() { modal.open = false }

async function save() {
  saving.value = true
  try {
    if (modal.mode === 'create') {
      const created = await post_api('/api/fish', modal.data)
      items.value.push(created)
    } else {
      const updated = await put_api(`/api/fish/${modal.data.id}`, modal.data)
      const idx = items.value.findIndex(f => f.id === modal.data.id)
      if (idx !== -1) items.value[idx] = updated
    }
    showToast(modal.mode === 'create' ? 'Fish created' : 'Fish updated')
    closeModal()
  } catch { showToast('Save failed', 'error') }
  finally { saving.value = false }
}

async function remove(id) {
  if (!confirm('Delete this fish?')) return
  try {
    await delete_api(`/api/fish/${id}`)
    items.value = items.value.filter(f => f.id !== id)
    showToast('Fish deleted')
  } catch { showToast('Delete failed', 'error') }
}

onMounted(load)
</script>

<style scoped src="@/assets/admin.css" />
<style scoped>
.color-chip {
  display: inline-block; width: 12px; height: 12px;
  border-radius: 50%; border: 1px solid rgba(0,0,0,0.15);
  vertical-align: middle; margin-right: 4px;
}
</style>