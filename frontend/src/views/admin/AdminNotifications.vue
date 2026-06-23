<template>
  <div class="admin-section">
    <AdminToast :toast="toast" />
    <div class="admin-toolbar">
      <input v-model="search" class="admin-search" placeholder="Search by pseudo…" />
    </div>
    <div v-if="loading" class="admin-state">Loading…</div>
    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr><th>ID</th><th>User</th><th>On Death</th><th>Before Death</th><th>Daily Reminder</th><th>Actions</th></tr>
        </thead>
        <tbody>
          <tr v-for="n in filtered" :key="n.id">
            <td class="td-muted">{{ n.id }}</td>
            <td class="td-bold">{{ n.pseudo ?? '—' }}</td>
            <td><span class="badge" :class="n.notifyOnDeath ? 'badge--green' : 'badge--grey'">{{ n.notifyOnDeath ? 'Yes' : 'No' }}</span></td>
            <td><span class="badge" :class="n.notifyBeforeDeath ? 'badge--green' : 'badge--grey'">{{ n.notifyBeforeDeath ? 'Yes' : 'No' }}</span></td>
            <td><span class="badge" :class="n.dailyReminder ? 'badge--green' : 'badge--grey'">{{ n.dailyReminder ? 'Yes' : 'No' }}</span></td>
            <td class="td-actions">
              <button class="btn-edit" @click="openEdit(n)">Edit</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="modal.open" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal">
        <h2 class="admin-modal__title">Edit — {{ modal.data.pseudo }}</h2>
        <div class="admin-form">
          <label style="flex-direction:row;align-items:center;gap:0.5rem;">
            <input type="checkbox" v-model="modal.data.notifyOnDeath" style="width:auto;" /> Notify on Death
          </label>
          <label style="flex-direction:row;align-items:center;gap:0.5rem;">
            <input type="checkbox" v-model="modal.data.notifyBeforeDeath" style="width:auto;" /> Notify Before Death
          </label>
          <label style="flex-direction:row;align-items:center;gap:0.5rem;">
            <input type="checkbox" v-model="modal.data.dailyReminder" style="width:auto;" /> Daily Reminder
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
import { get_api, put_api } from '@/services/api.js'
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

const filtered = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return items.value
  return items.value.filter(n => n.pseudo?.toLowerCase().includes(q))
})

async function load() {
  loading.value = true
  try { items.value = await get_api('/api/notifications') }
  catch { showToast('Load failed', 'error') }
  finally { loading.value = false }
}

function openEdit(n)  { Object.assign(modal, { open: true, data: { ...n } }) }
function closeModal() { modal.open = false }

async function save() {
  saving.value = true
  try {
    const updated = await put_api(`/api/notifications/${modal.data.id}`, modal.data)
    const idx = items.value.findIndex(n => n.id === modal.data.id)
    if (idx !== -1) items.value[idx] = updated
    showToast('Updated')
    closeModal()
  } catch { showToast('Save failed', 'error') }
  finally { saving.value = false }
}

onMounted(load)
</script>
<style scoped src="@/assets/admin.css" />