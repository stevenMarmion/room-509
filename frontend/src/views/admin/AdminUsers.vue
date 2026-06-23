<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />

    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search by pseudo or email…" />
      <button class="btn-create" @click="openCreate">+ New User</button>
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
              <button class="btn-del"  @click="remove(u.pseudo)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="modal.open" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal__title">{{ modal.mode === 'create' ? 'New User' : 'Edit User' }}</h2>
        <div class="admin-form">
          <label>Pseudo<input v-model="modal.data.pseudo" /></label>
          <label>Email<input v-model="modal.data.email" /></label>
          <label v-if="modal.mode === 'create'">
            Password<input type="password" v-model="modal.data.password" />
          </label>
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
          <button class="btn-save" :disabled="saving" @click="save">{{ saving ? 'Saving…' : 'Save' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { get_api, post_api, put_api, patch_api, delete_api } from '@/services/api.js'
import AdminToast from './AdminToast.vue'
import { useAuthStore } from '@/stores/auth.js'

const authStore = useAuthStore()
const items   = ref([])
const loading = ref(false)
const error   = ref(null)
const search  = ref('')
const saving  = ref(false)
const modal   = ref({ open: false, mode: 'create', data: {} })
const toast   = ref({ visible: false, message: '', type: 'success' })
const initialPseudo = ref('')
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

function openCreate() {
  modal.value = {
    open: true,
    mode: 'create',
    data: { pseudo: '', email: '', password: '', avatar: '', coins: 0, role: 'USER', theme: 'LIGHT' }
  }
}

function openEdit(u) {
  initialPseudo.value = u.pseudo
  modal.value = { open: true, mode: 'edit', data: { ...u } }
}

function closeModal() { modal.value = { open: false, mode: 'create', data: {} } }

async function save() {
  saving.value = true
  try {
    if (modal.value.mode === 'create') {
      // Utilise l'endpoint register existant
      await post_api('/api/auth/register', {
        pseudo:   modal.value.data.pseudo,
        email:    modal.value.data.email,
        password: modal.value.data.password,
        passwordConfirm: modal.value.data.password,
      })
      // Applique coins/role/theme si différents des défauts
      const pseudo = modal.value.data.pseudo
      if (modal.value.data.role !== 'USER')
        await patch_api(`/api/users/${pseudo}/role`, { role: modal.value.data.role })
      if (modal.value.data.coins !== 0)
        await patch_api(`/api/users/${pseudo}/coins`, { coins: modal.value.data.coins })
      // avatar et theme via PUT si renseignés
      if (modal.value.data.avatar || modal.value.data.theme !== 'LIGHT')
        await put_api(`/api/users/${pseudo}`, {
          pseudo,
          email:  modal.value.data.email,
          avatar: modal.value.data.avatar,
          theme:  modal.value.data.theme,
        })
      showToast('User created')
    } else {
      const d = modal.value.data
      const wasCurrentUser = initialPseudo.value === authStore.pseudo

      await put_api(`/api/users/${initialPseudo.value}`, { pseudo: d.pseudo, email: d.email, avatar: d.avatar, theme: d.theme })
      await patch_api(`/api/users/${d.pseudo}/role`, { role: d.role })
      await patch_api(`/api/users/${d.pseudo}/coins`, { coins: d.coins })

      if (wasCurrentUser) {
        authStore.pseudo = d.pseudo
        authStore.role   = d.role
        sessionStorage.setItem('pseudo', d.pseudo)
        sessionStorage.setItem('role',   d.role)
      }
      showToast('User updated')
    }

    await load()
    closeModal()
  } catch { showToast('Save failed', 'error') }
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