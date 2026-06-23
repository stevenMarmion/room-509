<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />
    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search challenges…" />
      <button class="btn-create" @click="openCreate">+ New Challenge</button>
    </div>
    <div v-if="loading" class="admin-state">Loading…</div>
    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Reward</th>
            <th>Description</th>
            <th>Assigned to</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in filtered" :key="c.id">
            <td class="td-muted">{{ c.id }}</td>
            <td class="td-bold">{{ c.name }}</td>
            <td><span class="badge badge--gold">{{ c.reward }}</span></td>
            <td class="td-muted" style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">
              {{ c.description }}
            </td>
            <td>
              <span class="badge badge--teal">
                {{ c.userEntries?.length ?? 0 }} users
              </span>
            </td>
            <td class="td-actions">
              <button class="btn-edit" @click="openEdit(c)">Edit</button>
              <button class="btn-del"  @click="remove(c.id)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="modal.open" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal__title">{{ modal.mode === 'create' ? 'New Challenge' : 'Edit Challenge' }}</h2>
        <div class="admin-form">
          <label>Name<input v-model="modal.data.name" /></label>
          <label>Reward<input type="number" v-model.number="modal.data.reward" /></label>
          <label>Description<textarea v-model="modal.data.description"></textarea></label>
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
const search  = ref('')
const saving  = ref(false)
const toast   = reactive({ visible: false, message: '', type: 'success' })
const modal   = reactive({ open: false, mode: 'create', data: {} })
let toastTimer = null

function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  Object.assign(toast, { visible: true, message: msg, type })
  toastTimer = setTimeout(() => { toast.visible = false }, 3000)
}

const filtered = computed(() =>
  items.value.filter(c => c.name?.toLowerCase().includes(search.value.toLowerCase()))
)

async function load() {
  loading.value = true
  try { items.value = await get_api('/api/challenges') }
  catch { showToast('Load failed', 'error') }
  finally { loading.value = false }
}

function openCreate() {
  Object.assign(modal, {
    open: true, mode: 'create',
    data: { name: '', reward: 10, description: '' }  // plus de date ni completed
  })
}

function openEdit(c) {
  Object.assign(modal, {
    open: true, mode: 'edit',
    data: { id: c.id, name: c.name, reward: c.reward, description: c.description }
  })
}

function closeModal() { modal.open = false }

async function save() {
  saving.value = true
  try {
    if (modal.mode === 'create') {
      const created = await post_api('/api/challenges', modal.data)
      items.value.push(created)
    } else {
      const updated = await put_api(`/api/challenges/${modal.data.id}`, modal.data)
      const idx = items.value.findIndex(c => c.id === modal.data.id)
      if (idx !== -1) items.value[idx] = updated
    }
    showToast('Saved')
    closeModal()
  } catch { showToast('Save failed', 'error') }
  finally { saving.value = false }
}

async function remove(id) {
  if (!confirm('Delete challenge? This will also remove all user assignments.')) return
  try {
    await delete_api(`/api/challenges/${id}`)
    items.value = items.value.filter(c => c.id !== id)
    showToast('Deleted')
  } catch { showToast('Delete failed', 'error') }
}

onMounted(load)
</script>
<style scoped src="@/assets/admin.css" />