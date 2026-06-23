<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />
    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search by user or status…" />
    </div>
    <div v-if="loading" class="admin-state">Loading…</div>
    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr><th>ID</th><th>Sender</th><th>Receiver</th><th>Status</th><th>Since</th><th>Actions</th></tr>
        </thead>
        <tbody>
          <tr v-for="f in filtered" :key="f.id">
            <td class="td-muted">{{ f.id }}</td>
            <td class="td-bold">{{ f.senderUsername ?? '—' }}</td>
            <td>{{ f.receiverUsername ?? '—' }}</td>
            <td>
              <span class="badge" :class="f.status === 'ACCEPTED' ? 'badge--green' : f.status === 'BLOCKED' ? 'badge--red' : 'badge--grey'">
                {{ f.status }}
              </span>
            </td>
            <td class="td-muted">{{ f.since ? new Date(f.since).toLocaleDateString() : '—' }}</td>
            <td class="td-actions">
              <button class="btn-del" @click="remove(f.id)">Delete</button>
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
  return items.value.filter(f =>
    String(f.id).includes(q) ||
    f.status?.toLowerCase().includes(q) ||
    f.senderUsername?.toLowerCase().includes(q) ||
    f.receiverUsername?.toLowerCase().includes(q)
  )
})

async function load() {
  loading.value = true
  try { items.value = await get_api('/api/friendships') }
  catch { showToast('Load failed', 'error') }
  finally { loading.value = false }
}

async function remove(id) {
  if (!confirm('Delete friendship?')) return
  try {
    await delete_api(`/api/friendships/${id}`)
    items.value = items.value.filter(f => f.id !== id)
    showToast('Deleted')
  } catch { showToast('Delete failed', 'error') }
}

onMounted(load)
</script>
<style scoped src="@/assets/admin.css" />