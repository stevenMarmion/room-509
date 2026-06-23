<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />
    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search aquariums…" />
    </div>
    <div v-if="loading" class="admin-state">Loading…</div>
    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead><tr><th>ID</th><th>Name</th><th>Owner</th><th>Level</th><th>Capacity</th><th>Fish</th><th>Public</th><th>Actions</th></tr></thead>
        <tbody>
          <tr v-for="a in filtered" :key="a.id">
            <td class="td-muted">{{ a.id }}</td>
            <td class="td-bold">{{ a.name }}</td>
            <td>{{ a.user?.pseudo ?? '—' }}</td>
            <td><span class="badge badge--teal">Lv {{ a.level }}</span></td>
            <td>{{ a.fish?.length ?? 0 }} / {{ a.capacity }}</td>
            <td>{{ a.fish?.length ?? 0 }}</td>
            <td><span class="badge" :class="a.public ? 'badge--green' : 'badge--grey'">{{ a.public ? 'Public' : 'Private' }}</span></td>
            <td class="td-actions">
              <button class="btn-edit" @click="openEdit(a)">Edit</button>
              <button class="btn-del"  @click="remove(a.id)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="modal.open" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal__title">Edit Aquarium</h2>
        <div class="admin-form">
          <label>Name<input v-model="modal.data.name" /></label>
          <label>Level<input type="number" v-model.number="modal.data.level" min="1" max="10" /></label>
          <label>Capacity<input type="number" v-model.number="modal.data.capacity" min="1" /></label>
          <label style="flex-direction:row;align-items:center;gap:0.5rem;">
            <input type="checkbox" v-model="modal.data.public" style="width:auto;" /> Public
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
import { ref, computed, onMounted, reactive } from 'vue'
import { get_api, put_api, delete_api } from '@/services/api.js'
import AdminToast from './AdminToast.vue'

const items = ref([]), loading = ref(false), search = ref(''), saving = ref(false)
const toast = reactive({ visible: false, message: '', type: 'success' })
const modal = reactive({ open: false, data: {} })
let toastTimer = null
function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  Object.assign(toast, { visible: true, message: msg, type })
  toastTimer = setTimeout(() => { toast.visible = false }, 3000)
}
const filtered = computed(() => items.value.filter(a =>
  a.name?.toLowerCase().includes(search.value.toLowerCase()) ||
  a.user?.pseudo?.toLowerCase().includes(search.value.toLowerCase())
))
async function load() {
  loading.value = true
  try { items.value = await get_api('/api/aquariums') }
  catch { showToast('Load failed', 'error') }
  finally { loading.value = false }
}
function openEdit(a)  { Object.assign(modal, { open: true, data: { ...a } }) }
function closeModal() { modal.open = false }
async function save() {
  saving.value = true
  try {
    const updated = await put_api(`/api/aquariums/${modal.data.id}`, modal.data)
    const idx = items.value.findIndex(a => a.id === modal.data.id)
    if (idx !== -1) items.value[idx] = updated
    showToast('Updated'); closeModal()
  } catch { showToast('Save failed', 'error') }
  finally { saving.value = false }
}
async function remove(id) {
  if (!confirm('Delete aquarium?')) return
  try { await delete_api(`/api/aquariums/${id}`); items.value = items.value.filter(a => a.id !== id); showToast('Deleted') }
  catch { showToast('Delete failed', 'error') }
}
onMounted(load)
</script>
<style scoped src="@/assets/admin.css" />