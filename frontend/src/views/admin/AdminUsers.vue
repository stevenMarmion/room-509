<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />

    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search by pseudo or email…" />
    </div>

    <div v-if="loading" class="admin-state">Loading…</div>
    <div v-else-if="error" class="admin-state admin-state--error">{{ error }}</div>

    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>Pseudo</th><th>Email</th><th>Coins</th>
            <th>Role</th><th>Theme</th><th>Created</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in filtered" :key="u.id">
            <td class="td-muted">{{ u.id }}</td>
            <td class="td-bold">{{ u.pseudo }}</td>
            <td>{{ u.email }}</td>
            <td><span class="badge badge--gold">{{ u.coins }}</span></td>
            <td><span class="badge" :class="u.role === 'ADMIN' ? 'badge--red' : 'badge--teal'">{{ u.role }}</span></td>
            <td>{{ u.theme }}</td>
            <td class="td-muted">{{ formatDate(u.createdAt) }}</td>
            <td class="td-actions">
              <button class="btn-edit" @click="openEdit(u)">Edit</button>
              <button class="btn-del"  @click="remove(u.pseudo, 'pseudo')">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="modal.open" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal__title">Edit User</h2>
        <div class="admin-form">
          <label>Pseudo<input v-model="modal.data.pseudo" /></label>
          <label>Email<input v-model="modal.data.email" /></label>
          <label>Avatar<input v-model="modal.data.avatar" /></label>
          <label>Coins<input type="number" v-model.number="modal.data.coins" /></label>
          <label>Role
            <select v-model="modal.data.role">
              <option>USER</option>
              <option>ADMIN</option>
            </select>
          </label>
          <label>Theme
            <select v-model="modal.data.theme">
              <option>LIGHT</option>
              <option>DARK</option>
            </select>
          </label>
        </div>
        <div class="admin-modal__actions">
          <button class="btn-cancel" @click="closeModal">Cancel</button>
          <button class="btn-save" :disabled="saving" @click="saveUser">{{ saving ? 'Saving…' : 'Save' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { get_api, put_api, patch_api, delete_api } from '@/services/api.js'
import AdminToast from './AdminToast.vue'

const items   = ref([])
const loading = ref(false)
const error   = ref(null)
const search  = ref('')
const saving  = ref(false)
const modal   = ref({ open: false, data: {} })
const toast   = ref({ visible: false, message: '', type: 'success' })
let toastTimer = null

function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  toast.value = { visible: true, message: msg, type }
  toastTimer = setTimeout(() => { toast.value.visible = false }, 3000)
}

const filtered = computed(() => {
  const q = search.value.toLowerCase()
  return items.value.filter(u =>
    u.pseudo?.toLowerCase().includes(q) || u.email?.toLowerCase().includes(q)
  )
})

function formatDate(d) { return d ? new Date(d).toLocaleDateString() : '—' }

async function load() {
  loading.value = true
  error.value   = null
  try { items.value = await get_api('/api/users') }
  catch { error.value = 'Failed to load users' }
  finally { loading.value = false }
}

function openEdit(u) { modal.value = { open: true, data: { ...u } } }
function closeModal() { modal.value = { open: false, data: {} } }

async function saveUser() {
  saving.value = true
  try {
    const d = modal.value.data
    // Update base fields
    await put_api(`/api/users/${d.pseudo}`, { pseudo: d.pseudo, email: d.email, avatar: d.avatar, theme: d.theme })
    // Update role separately
    await patch_api(`/api/users/${d.pseudo}/role`, { role: d.role })
    // Update coins separately
    await patch_api(`/api/users/${d.pseudo}/coins`, { coins: d.coins })
    await load()
    closeModal()
    showToast('User updated')
  } catch { showToast('Update failed', 'error') }
  finally { saving.value = false }
}

async function remove(pseudo) {
  if (!confirm(`Delete user ${pseudo}?`)) return
  try {
    await delete_api(`/api/users/${pseudo}`)
    items.value = items.value.filter(u => u.pseudo !== pseudo)
    showToast('User deleted')
  } catch { showToast('Delete failed', 'error') }
}

onMounted(load)
</script>

<style scoped src="@/assets/admin.css" />