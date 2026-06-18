<template>
  <main class="profile-layout">

    <!-- ── Toast ── -->
    <Transition name="toast">
      <div v-if="toast.visible" class="toast-notif" :class="`toast-notif--${toast.type}`">
        {{ toast.message }}
      </div>
    </Transition>

    <!-- ── Avatar + pseudo ── -->
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

    <!-- ── Stats ── -->
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

    <!-- ── Edit profile ── -->
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

    <!-- ── Theme ── -->
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

    <!-- ── Danger zone ── -->
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

  </main>

  <!-- ── Password modal ── -->
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

// ── User state ────────────────────────────────────────────────────────────────

const user = reactive({
  pseudo:      '',
  role:        '',
  coins:       0,
  fishCount:   0,
  friendCount: 0,
  email:       '',
  createdAt:   null,
  theme:       'LIGHT',
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

// ── Form state ────────────────────────────────────────────────────────────────

const form = reactive({ pseudo: '', email: '' })
const isDark = ref(false)

function resetForm() {
  form.pseudo = user.pseudo
  form.email  = user.email
}

// ── Toast ─────────────────────────────────────────────────────────────────────

const toast = reactive({ visible: false, message: '', type: 'success' })
let toastTimer = null

function showToast(message, type = 'success') {
  clearTimeout(toastTimer)
  toast.message = message
  toast.type    = type
  toast.visible = true
  toastTimer = setTimeout(() => { toast.visible = false }, 3500)
}

// ── Load profile ──────────────────────────────────────────────────────────────

async function loadProfile() {
  try {
    // Replace 1 with the actual logged-in user id
    const data = await get_api('/api/users/1')
    Object.assign(user, data)
    resetForm()
    isDark.value = data.theme === 'DARK'
  } catch {
    console.warn('Could not load profile.')
  }
}

// ── Save profile ──────────────────────────────────────────────────────────────

async function saveProfile() {
  try {
    await put_api('/api/users/1', { pseudo: form.pseudo, email: form.email })
    showToast('Profile updated successfully.', 'success')
    await loadProfile()
  } catch {
    showToast('Could not update profile. Please try again.', 'error')
  }
}

// ── Theme ─────────────────────────────────────────────────────────────────────

async function saveTheme() {
  try {
    await put_api('/api/users/1', { theme: isDark.value ? 'DARK' : 'LIGHT' })
  } catch {
    console.warn('Could not update theme.')
  }
}

// ── Password modal ────────────────────────────────────────────────────────────

const showPasswordModal = ref(false)
const passwordForm = reactive({ password: '', confirm: '' })

function openPasswordModal() {
  passwordForm.password = ''
  passwordForm.confirm  = ''
  showPasswordModal.value = true
}

function closePasswordModal() {
  showPasswordModal.value = false
}

async function savePassword() {
  if (!passwordForm.password) {
    showToast('Please enter a new password.', 'error')
    return
  }
  if (passwordForm.password !== passwordForm.confirm) {
    showToast('Passwords do not match.', 'error')
    return
  }
  try {
    await put_api('/api/users/1', { password: passwordForm.password })
    showToast('Password updated successfully.', 'success')
    closePasswordModal()
  } catch {
    showToast('Could not update password. Please try again.', 'error')
  }
}

// Close modal on Escape key
function onKeyDown(e) {
  if (e.key === 'Escape' && showPasswordModal.value) closePasswordModal()
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(() => {
  loadProfile()
  document.addEventListener('keydown', onKeyDown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', onKeyDown)
  clearTimeout(toastTimer)
})
</script>

<style scoped>
/* ── Layout ── */
.profile-layout {
  max-width: 760px;
  margin: 2rem auto;
  padding: 0 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

/* ── Card ── */
.pcard {
  background: #fff;
  border-radius: 14px;
  padding: 1.5rem;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
}
.pcard--danger { border: 1.5px solid #fde8e8; }

/* ── Avatar ── */
.profile-header {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}
.avatar-circle {
  width: 80px; height: 80px;
  border-radius: 50%;
  background: #0d7377;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  font-size: 2rem;
  color: #fff;
  font-weight: 700;
}
.profile-meta h2 {
  font-size: 1.25rem;
  color: #1a3a4a;
  margin-bottom: 0.2rem;
}
.role-badge {
  display: inline-block;
  background: #e8f7f7;
  color: #0d7377;
  border-radius: 999px;
  padding: 0.15rem 0.75rem;
  font-size: 0.78rem;
  font-weight: 600;
}
.joined {
  font-size: 0.8rem;
  color: #aaa;
  margin-top: 0.3rem;
}

/* ── Section title ── */
.section-title {
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #0d7377;
  margin-bottom: 1rem;
}
.section-title--danger { color: #e74c3c; }

/* ── Stats ── */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  text-align: center;
}
.stat-item strong {
  display: block;
  font-size: 1.5rem;
  color: #0d7377;
  font-weight: 700;
}
.stat-item span { font-size: 0.8rem; color: #888; }

/* ── Form ── */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}
label {
  font-size: 0.82rem;
  color: #666;
  font-weight: 500;
}
input[type="text"],
input[type="email"],
input[type="password"] {
  border: 1.5px solid #dde3ea;
  border-radius: 8px;
  padding: 0.55rem 0.8rem;
  font-size: 0.9rem;
  color: #1a3a4a;
  outline: none;
  transition: border-color 0.2s;
  background: #fafbfc;
  width: 100%;
}
input:focus { border-color: #0d7377; background: #fff; }

/* ── Buttons ── */
.btn-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.8rem;
  margin-top: 1rem;
}
.btn-row-right {
  display: flex;
  gap: 0.8rem;
}
button {
  border: none;
  border-radius: 8px;
  padding: 0.6rem 1.4rem;
  font-size: 0.9rem;
  cursor: pointer;
  font-weight: 600;
  transition: opacity 0.2s;
}
button:hover { opacity: 0.85; }
.btn-primary { background: #0d7377; color: #fff; }
.btn-ghost   { background: #f0f4f8; color: #555; }
.btn-danger  { background: #fff; color: #e74c3c; border: 1.5px solid #e74c3c; }

/* ── Theme toggle ── */
.theme-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.theme-label { font-size: 0.9rem; color: #1a3a4a; }
.theme-label small { display: block; font-size: 0.78rem; color: #aaa; }

.toggle { position: relative; width: 52px; height: 28px; }
.toggle input { display: none; }
.toggle-track {
  position: absolute;
  inset: 0;
  background: #dde3ea;
  border-radius: 999px;
  cursor: pointer;
  transition: background 0.3s;
}
.toggle input:checked + .toggle-track { background: #0d7377; }
.toggle-track::after {
  content: '';
  position: absolute;
  top: 3px; left: 3px;
  width: 22px; height: 22px;
  border-radius: 50%;
  background: #fff;
  transition: transform 0.3s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}
.toggle input:checked + .toggle-track::after { transform: translateX(24px); }

/* ── Danger zone ── */
.danger-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.danger-label { font-size: 0.9rem; color: #1a3a4a; font-weight: 500; }
.danger-hint  { font-size: 0.78rem; color: #aaa; }

/* ── Toast ── */
.toast-notif {
  position: fixed;
  bottom: 1.5rem;
  left: 50%;
  transform: translateX(-50%);
  padding: 0.7rem 1.4rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #fff;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
.toast-notif--success { background: #0d7377; }
.toast-notif--error   { background: #e74c3c; }

.toast-enter-active, .toast-leave-active { transition: opacity 0.3s, transform 0.3s; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateX(-50%) translateY(10px); }

/* ── Modal ── */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}
.modal-box {
  background: #fff;
  border-radius: 14px;
  padding: 2rem;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}
.modal-box h3 {
  font-size: 1.1rem;
  color: #1a3a4a;
  margin-bottom: 1.2rem;
}

.modal-enter-active, .modal-leave-active { transition: opacity 0.25s, transform 0.25s; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.96); }

/* ── Responsive ── */
@media (max-width: 560px) {
  .form-grid { grid-template-columns: 1fr; }
  .btn-row { flex-direction: column; align-items: stretch; }
  .btn-row-right { flex-direction: column; }
}
</style>