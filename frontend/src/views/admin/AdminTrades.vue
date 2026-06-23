<!-- AdminTrades.vue -->
<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />
    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search by ID or status…" />
    </div>
    <div v-if="loading" class="admin-state">Loading…</div>
    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>Price</th><th>Status</th><th>Fish</th><th>Created</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="t in filtered" :key="t.id">
            <td class="td-muted">{{ t.id }}</td>
            <td><span class="badge badge--gold">{{ t.price }}</span></td>
            <td><span class="badge" :class="statusClass(t.status)">{{ t.status }}</span></td>
            <td>{{ t.fish?.length ?? 0 }} fish</td>
            <td class="td-muted">{{ t.createdAt ? new Date(t.createdAt).toLocaleDateString() : '—' }}</td>
            <td class="td-actions">
              <button class="btn-del" @click="remove(t.id)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { get_api, delete_api } from '@/services/api.js'
import AdminToast from './AdminToast.vue'

const items = ref([]), loading = ref(false), search = ref('')
const toast = reactive({ visible: false, message: '', type: 'success' })
let toastTimer = null
function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  Object.assign(toast, { visible: true, message: msg, type })
  toastTimer = setTimeout(() => { toast.visible = false }, 3000)
}
const filtered = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return items.value
  return items.value.filter(t =>
    String(t.id).includes(q) ||
    t.status?.toLowerCase().includes(q)
  )
})
function statusClass(s) {
  return s === 'ACCEPTED' ? 'badge--green' : s === 'REJECTED' ? 'badge--red' : 'badge--grey'
}
async function load() {
  loading.value = true
  try { items.value = await get_api('/api/trades') }
  catch { showToast('Load failed', 'error') }
  finally { loading.value = false }
}
async function remove(id) {
  if (!confirm('Delete trade?')) return
  try { await delete_api(`/api/trades/${id}`); items.value = items.value.filter(t => t.id !== id); showToast('Deleted') }
  catch { showToast('Delete failed', 'error') }
}
onMounted(load)
</script>
<style scoped src="@/assets/admin.css" />