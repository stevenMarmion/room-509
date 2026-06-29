<template>
  <main class="profile-layout">

    <Transition name="toast">
      <div v-if="toast.visible" class="toast-notif" :class="`toast-notif--${toast.type}`">
        {{ toast.message }}
      </div>
    </Transition>

    <!-- Avatar + pseudo -->
    <div class="pcard">
      <div class="profile-header">
        <div class="avatar-circle">{{ avatarInitial }}</div>
        <div class="profile-meta">
          <h2>{{ user.pseudo }}</h2>
          <span class="role-badge">{{ user.role }}</span>
          <p class="joined">{{ joinedLabel }}</p>
        </div>
      </div>
    </div>

    <!-- Stats -->
    <div class="pcard">
      <div class="section-title">Stats</div>
      <div class="stats-grid">
        <div class="stat-item">
          <strong>{{ user.coins.toLocaleString('fr-FR') }}</strong>
          <span>Coins</span>
        </div>
        <div class="stat-item">
          <strong>{{ user.fishCount }}</strong>
          <span>Fish</span>
        </div>
        <div class="stat-item">
          <strong>{{ user.friendCount }}</strong>
          <span>Friends</span>
        </div>
      </div>
    </div>

    <!-- Edit profile -->
    <div class="pcard">
      <div class="section-title">Edit profile</div>
      <form @submit.prevent="saveProfile">
        <div class="form-grid">
          <div class="form-group">
            <label for="pseudo">Pseudo</label>
            <input id="pseudo" type="text" v-model="form.pseudo" />
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input id="email" type="email" v-model="form.email" />
          </div>
        </div>
        <div class="btn-row">
          <button type="button" class="btn-ghost" @click="openPasswordModal">Change password</button>
          <div class="btn-row-right">
            <button type="button" class="btn-ghost" @click="resetForm">Cancel</button>
            <button type="submit" class="btn-primary">Save changes</button>
          </div>
        </div>
      </form>
    </div>

    <!-- Notifications -->
    <div class="pcard">
      <div class="section-title">Notifications</div>
      <div class="notif-list">
        <div class="notif-row">
          <div>
            <div class="notif-label">Fish death alert</div>
            <div class="notif-hint">Get notified when a fish dies in your aquarium.</div>
          </div>
          <label class="toggle">
            <input type="checkbox" v-model="notifForm.notifyOnDeath" @change="saveNotifications" />
            <div class="toggle-track"></div>
          </label>
        </div>
        <div class="notif-row">
          <div>
            <div class="notif-label">Fish in danger</div>
            <div class="notif-hint">Get notified before a fish is about to die.</div>
          </div>
          <label class="toggle">
            <input type="checkbox" v-model="notifForm.notifyBeforeDeath" @change="saveNotifications" />
            <div class="toggle-track"></div>
          </label>
        </div>
        <div class="notif-row">
          <div>
            <div class="notif-label">Daily reminder</div>
            <div class="notif-hint">Receive a daily reminder to feed your fish.</div>
          </div>
          <label class="toggle">
            <input type="checkbox" v-model="notifForm.dailyReminder" @change="saveNotifications" />
            <div class="toggle-track"></div>
          </label>
        </div>
      </div>
    </div>

    <!-- Theme -->
    <div class="pcard">
      <div class="section-title">Theme</div>
      <div class="theme-row">
        <div class="theme-label">
          Dark mode
          <small>Switch between light and dark interface</small>
        </div>
        <label class="toggle">
          <input type="checkbox" v-model="isDark" @change="saveTheme" />
          <div class="toggle-track"></div>
        </label>
      </div>
    </div>

    <!-- Danger zone -->
    <div class="pcard pcard--danger">
      <div class="section-title section-title--danger">Danger zone</div>
      <div class="danger-row">
        <div>
          <div class="danger-label">Delete account</div>
          <div class="danger-hint">This action is irreversible.</div>
        </div>
        <button class="btn-danger">Delete my account</button>
      </div>
    </div>

    <!-- Logout -->
    <div class="pcard">
      <div class="section-title">Session</div>
      <div class="danger-row">
        <div>
          <div class="danger-label">Log out</div>
          <div class="danger-hint">You'll need to log in again to access your account.</div>
        </div>
        <button class="btn-ghost" @click="handleLogout">Log out</button>
      </div>
    </div>

    <!-- Admin panel -->
    <div v-if="authStore.role === 'ADMIN'" class="pcard pcard--admin">
      <div class="section-title section-title--admin">Administration</div>
      <div class="danger-row">
        <div>
          <div class="danger-label">Admin panel</div>
          <div class="danger-hint">Manage users, fish, shop, trades and app config.</div>
        </div>
        <RouterLink to="/admin" class="btn-admin">Open panel</RouterLink>
      </div>
    </div>

  </main>

  <!-- Password modal -->
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="showPasswordModal" class="modal-backdrop" @click.self="closePasswordModal">
        <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="modal-title">
          <h3 id="modal-title">Change password</h3>
          <form @submit.prevent="savePassword">
            <div class="form-group">
              <label for="password">New password</label>
              <input id="password" type="password" v-model="passwordForm.password" placeholder="Enter new password" />
            </div>
            <div class="form-group" style="margin-top: 0.8rem;">
              <label for="password-confirm">Confirm password</label>
              <input id="password-confirm" type="password" v-model="passwordForm.confirm" placeholder="Confirm new password" />
            </div>
            <div class="btn-row" style="margin-top: 1.2rem;">
              <button type="button" class="btn-ghost" @click="closePasswordModal">Cancel</button>
              <button type="submit" class="btn-primary">Update password</button>
            </div>
          </form>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { get_api, put_api } from '@/services/api.js'
import { useAuthStore } from '@/stores/auth.js'
import { useRouter } from 'vue-router'
import { RouterLink } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const user = reactive({
  pseudo: '', role: '', coins: 0, fishCount: 0,
  friendCount: 0, email: '', createdAt: null, theme: 'LIGHT',
})

const avatarInitial = computed(() =>
  user.pseudo ? user.pseudo.charAt(0).toUpperCase() : '?'
)

const joinedLabel = computed(() => {
  if (!user.createdAt) return ''
  return 'Member since ' + new Date(user.createdAt).toLocaleDateString('en-US', {
    month: 'long', year: 'numeric'
  })
})

const form = reactive({ pseudo: '', email: '' })
const isDark = ref(false)

const notifForm = reactive({
  notifyOnDeath: false,
  notifyBeforeDeath: false,
  dailyReminder: false,
})
let notifId = null

function resetForm() {
  form.pseudo = user.pseudo
  form.email  = user.email
}

const toast = reactive({ visible: false, message: '', type: 'success' })
let toastTimer = null

function showToast(message, type = 'success') {
  clearTimeout(toastTimer)
  toast.message = message
  toast.type    = type
  toast.visible = true
  toastTimer = setTimeout(() => { toast.visible = false }, 3500)
}

async function loadProfile() {
  try {
    const data = await get_api(`/api/users/${authStore.pseudo}`)
    authStore.user = data
    Object.assign(user, data)
    resetForm()
    isDark.value = data.theme === 'DARK'
    authStore.role = data.role
  } catch {
    console.warn('Could not load profile.')
  }
}

async function loadNotifications() {
  try {
    // l'endpoint retourne la préférence de l'utilisateur courant
    const data = await get_api(`/api/notifications/me`)
    notifId = data.id
    notifForm.notifyOnDeath   = data.notifyOnDeath
    notifForm.notifyBeforeDeath = data.notifyBeforeDeath
    notifForm.dailyReminder   = data.dailyReminder
  } catch {
    console.warn('Could not load notification preferences.')
  }
}

async function saveNotifications() {
  if (!notifId) return
  try {
    await put_api(`/api/notifications/${notifId}`, { ...notifForm })
    showToast('Notification preferences saved.', 'success')
  } catch {
    showToast('Could not save notification preferences.', 'error')
  }
}

async function saveProfile() {
  try {
    await put_api(`/api/users/${authStore.pseudo}`, { pseudo: form.pseudo, email: form.email })
    showToast('Profile updated successfully.', 'success')
    authStore.pseudo = form.pseudo
    await loadProfile()
  } catch {
    showToast('Could not update profile. Please try again.', 'error')
  }
}

async function saveTheme() {
  const t = isDark.value ? 'DARK' : 'LIGHT'
  authStore.setTheme(t)
  try {
    await put_api(`/api/users/${authStore.pseudo}`, { theme: t })
  } catch {
    console.warn('Could not update theme.')
  }
}

const showPasswordModal = ref(false)
const passwordForm = reactive({ password: '', confirm: '' })

function openPasswordModal() {
  passwordForm.password = ''
  passwordForm.confirm  = ''
  showPasswordModal.value = true
}
function closePasswordModal() { showPasswordModal.value = false }

async function savePassword() {
  if (!passwordForm.password) {
    showToast('Please enter a new password.', 'error'); return
  }
  if (passwordForm.password !== passwordForm.confirm) {
    showToast('Passwords do not match.', 'error'); return
  }
  try {
    await put_api(`/api/users/${authStore.pseudo}`, { password: passwordForm.password })
    showToast('Password updated successfully.', 'success')
    closePasswordModal()
  } catch {
    showToast('Could not update password. Please try again.', 'error')
  }
}

async function handleLogout() {
  try {
    await authStore.logout()
    router.push('/login')
  } catch {
    showToast('Could not log out. Please try again.', 'error')
  }
}

function onKeyDown(e) {
  if (e.key === 'Escape' && showPasswordModal.value) closePasswordModal()
}

onMounted(() => {
  loadProfile()
  loadNotifications()
  document.addEventListener('keydown', onKeyDown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', onKeyDown)
  clearTimeout(toastTimer)
})
</script>

<style scoped>
/* ── Notifications ── */
.notif-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.notif-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.85rem 0;
  border-bottom: 1px solid var(--c-divider);
}
.notif-row:last-child { border-bottom: none; }
.notif-label { font-size: 0.9rem; color: var(--c-text); font-weight: 500; }
.notif-hint  { font-size: 0.78rem; color: var(--c-text-muted); margin-top: 0.1rem; }

.pcard--admin  { border: 1.5px solid var(--c-brand-soft); }
.section-title--admin { color: var(--c-brand); }
.btn-admin {
  background: #0d2137; color: #fff; border: none; border-radius: 8px;
  padding: 0.6rem 1.4rem; font-size: 0.9rem; font-weight: 600;
  cursor: pointer; text-decoration: none; white-space: nowrap; transition: opacity 0.2s;
}
.btn-admin:hover { opacity: 0.85; }
.profile-layout { max-width: 760px; margin: 2rem auto; padding: 0 1.5rem; display: flex; flex-direction: column; gap: 1.2rem; }
.pcard { background: var(--c-card); border-radius: 14px; padding: 1.5rem; box-shadow: 0 1px 4px rgba(0,0,0,0.07); }
.pcard--danger { border: 1.5px solid #fde8e8; }
.profile-header { display: flex; align-items: center; gap: 1.5rem; }
.avatar-circle { width: 80px; height: 80px; border-radius: 50%; background: var(--c-brand); display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 2rem; color: #fff; font-weight: 700; }
.profile-meta h2 { font-size: 1.25rem; color: var(--c-heading); margin-bottom: 0.2rem; }
.role-badge { display: inline-block; background: var(--c-brand-soft); color: var(--c-brand); border-radius: 999px; padding: 0.15rem 0.75rem; font-size: 0.78rem; font-weight: 600; }
.joined { font-size: 0.8rem; color: var(--c-text-muted); margin-top: 0.3rem; }
.section-title { font-size: 0.85rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; color: var(--c-brand); margin-bottom: 1rem; }
.section-title--danger { color: #e74c3c; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; text-align: center; }
.stat-item strong { display: block; font-size: 1.5rem; color: var(--c-brand); font-weight: 700; }
.stat-item span { font-size: 0.8rem; color: var(--c-text-muted); }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.form-group { display: flex; flex-direction: column; gap: 0.3rem; }
label { font-size: 0.82rem; color: var(--c-text-secondary); font-weight: 500; }
input[type="text"], input[type="email"], input[type="password"] { border: 1.5px solid var(--c-border); border-radius: 8px; padding: 0.55rem 0.8rem; font-size: 0.9rem; color: var(--c-text); outline: none; transition: border-color 0.2s; background: var(--c-input-bg); width: 100%; }
input:focus { border-color: var(--c-brand); background: var(--c-card); }
.btn-row { display: flex; align-items: center; justify-content: space-between; gap: 0.8rem; margin-top: 1rem; }
.btn-row-right { display: flex; gap: 0.8rem; }
button { border: none; border-radius: 8px; padding: 0.6rem 1.4rem; font-size: 0.9rem; cursor: pointer; font-weight: 600; transition: opacity 0.2s; }
button:hover { opacity: 0.85; }
.btn-primary { background: var(--c-brand); color: #fff; }
.btn-ghost   { background: var(--c-ghost-bg); color: var(--c-ghost-text); }
.btn-danger  { background: var(--c-card); color: #e74c3c; border: 1.5px solid #e74c3c; }
.theme-row { display: flex; align-items: center; justify-content: space-between; }
.theme-label { font-size: 0.9rem; color: var(--c-text); }
.theme-label small { display: block; font-size: 0.78rem; color: var(--c-text-muted); }
.toggle { position: relative; width: 52px; height: 28px; }
.toggle input { display: none; }
.toggle-track { position: absolute; inset: 0; background: var(--c-border); border-radius: 999px; cursor: pointer; transition: background 0.3s; }
.toggle input:checked + .toggle-track { background: var(--c-brand); }
.toggle-track::after { content: ''; position: absolute; top: 3px; left: 3px; width: 22px; height: 22px; border-radius: 50%; background: #fff; transition: transform 0.3s; box-shadow: 0 1px 3px rgba(0,0,0,0.2); }
.toggle input:checked + .toggle-track::after { transform: translateX(24px); }
.danger-row { display: flex; align-items: center; justify-content: space-between; }
.danger-label { font-size: 0.9rem; color: var(--c-text); font-weight: 500; }
.danger-hint  { font-size: 0.78rem; color: var(--c-text-muted); }
.toast-notif { position: fixed; bottom: 1.5rem; left: 50%; transform: translateX(-50%); padding: 0.7rem 1.4rem; border-radius: 8px; font-size: 0.9rem; font-weight: 600; color: #fff; z-index: 1000; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
.toast-notif--success { background: var(--c-brand); }
.toast-notif--error   { background: #e74c3c; }
.toast-enter-active, .toast-leave-active { transition: opacity 0.3s, transform 0.3s; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateX(-50%) translateY(10px); }
.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 999; }
.modal-box { background: var(--c-card); border-radius: 14px; padding: 2rem; width: 100%; max-width: 420px; box-shadow: 0 8px 32px rgba(0,0,0,0.15); }
.modal-box h3 { font-size: 1.1rem; color: var(--c-heading); margin-bottom: 1.2rem; }
.modal-enter-active, .modal-leave-active { transition: opacity 0.25s, transform 0.25s; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.96); }
@media (max-width: 560px) {
  .form-grid { grid-template-columns: 1fr; }
  .btn-row { flex-direction: column; align-items: stretch; }
  .btn-row-right { flex-direction: column; }
}
</style>