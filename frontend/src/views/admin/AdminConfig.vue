<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />
    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search config keys…" />
      <button class="btn-create" @click="openCreate">+ New Config</button>
    </div>
    <div v-if="loading" class="admin-state">Loading…</div>
    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead><tr><th>ID</th><th>Key</th><th>Value</th><th>Description</th><th>Actions</th></tr></thead>
        <tbody>
          <tr v-for="c in filtered" :key="c.id">
            <td class="td-muted">{{ c.id }}</td>
            <td class="td-bold"><code>{{ c.key }}</code></td>
            <td>{{ c.value }}</td>
            <td class="td-muted">{{ c.description }}</td>
            <td class="td-actions">
              <button class="btn-edit" @click="openEdit(c)">Edit</button>
              <button class="btn-del"  @click="remove(c.key)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="modal.open" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal__title">{{ modal.mode === 'create' ? 'New Config' : 'Edit Config' }}</h2>
        <div class="admin-form">
          <label>Key<input v-model="modal.data.key" :disabled="modal.mode === 'edit'" /></label>
          <label>Value<input v-model="modal.data.value" /></label>
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

const items = ref([]), loading = ref(false), search = ref(''), saving = ref(false)
const toast = reactive({ visible: false, message: '', type: 'success' })
const modal = reactive({ open: false, mode: 'create', data: {} })
let toastTimer = null
function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  Object.assign(toast, { visible: true, message: msg, type })
  toastTimer = setTimeout(() => { toast.visible = false }, 3000)
}
const filtered = computed(() => items.value.filter(c =>
  c.key?.toLowerCase().includes(search.value.toLowerCase()) ||
  c.description?.toLowerCase().includes(search.value.toLowerCase())
))
async function load() {
  loading.value = true
  try { items.value = await get_api('/api/config') }
  catch { showToast('Load failed', 'error') }
  finally { loading.value = false }
}
function openCreate() { Object.assign(modal, { open: true, mode: 'create', data: { key: '', value: '', description: '' } }) }
function openEdit(c)  { Object.assign(modal, { open: true, mode: 'edit', data: { ...c } }) }
function closeModal() { modal.open = false }
async function save() {
  saving.value = true
  try {
    if (modal.mode === 'create') {
      const created = await post_api('/api/config', modal.data)
      items.value.push(created)
    } else {
      const updated = await put_api(`/api/config/${modal.data.key}`, modal.data)
      const idx = items.value.findIndex(c => c.key === modal.data.key)
      if (idx !== -1) items.value[idx] = updated
    }
    showToast('Saved'); closeModal()
  } catch { showToast('Save failed', 'error') }
  finally { saving.value = false }
}
async function remove(key) {
  if (!confirm(`Delete config "${key}"?`)) return
  try { await delete_api(`/api/config/${key}`); items.value = items.value.filter(c => c.key !== key); showToast('Deleted') }
  catch { showToast('Delete failed', 'error') }
}
onMounted(load)
</script>
<style scoped src="@/assets/admin.css" />