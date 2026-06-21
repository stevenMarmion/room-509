<template>
  <main class="profile-layout">

    <!-- ── Toast ── -->
    <Transition name="toast">
      <div v-if="toast.visible" class="toast-notif" :class="`toast-notif--${toast.type}`">
        {{ toast.message }}
      </div>
    </Transition>

    <!-- ── Loading ── -->
    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Loading profile...</p>
    </div>

    <!-- ── Error ── -->
    <div v-else-if="error" class="state-box state-box--error">
      <p>{{ error }}</p>
      <button class="btn-ghost" @click="loadAll">Try again</button>
    </div>

    <!-- ── Profile ── -->
    <template v-else>

      <!-- Header card -->
      <div class="pcard">
        <div class="profile-header">
          <div class="avatar-circle">{{ avatarInitial }}</div>
          <div class="profile-meta">
            <h2>{{ user.pseudo }}</h2>
            <span class="role-badge">{{ user.role }}</span>
            <p class="joined">{{ joinedLabel }}</p>
          </div>

          <!-- Friend action -->
          <div v-if="!isOwnProfile" class="friend-action">
            <button
              v-if="friendStatus === 'NONE' && authStore.isAuthenticated"
              class="btn-primary"
              :disabled="sendingRequest"
              @click="sendFriendRequest"
            >
              {{ sendingRequest ? 'Sending...' : 'Add friend' }}
            </button>
            <span v-else-if="friendStatus === 'PENDING'" class="status-pill status-pill--pending">
              Request sent
            </span>
            <span v-else-if="friendStatus === 'ACCEPTED'" class="status-pill status-pill--accepted">
              Already friends
            </span>
            <span v-else-if="friendStatus === 'BLOCKED'" class="status-pill status-pill--blocked">
              Blocked
            </span>
          </div>
        </div>
      </div>

      <!-- Aquarium -->
      <div class="pcard">
        <div class="section-title">Aquarium</div>

        <!-- Public aquarium -->
        <template v-if="user.aquarium?.public">
          <div class="aquarium-info">
            <div class="aquarium-stat">
              <strong>{{ user.aquarium.name }}</strong>
              <span>Name</span>
            </div>
            <div class="aquarium-stat">
              <strong>{{ user.aquarium.level }}</strong>
              <span>Level</span>
            </div>
            <div class="aquarium-stat">
              <strong>{{ user.aquarium.capacity }}</strong>
              <span>Capacity</span>
            </div>
            <div class="aquarium-stat">
              <strong>{{ fishes.length }}</strong>
              <span>Fish</span>
            </div>
          </div>

          <!-- Fish table -->
          <div v-if="loadingFish" class="state-box state-box--inline">
            <div class="spinner"></div>
            <p>Loading fish...</p>
          </div>
          <div v-else-if="fishes.length === 0" class="state-box state-box--inline">
            <p>This aquarium has no fish yet.</p>
          </div>
          <div v-else class="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Species</th>
                  <th>Color</th>
                  <th>Size</th>
                  <th>Age</th>
                  <th>Life pts</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="fish in fishes" :key="fish.id">
                  <td class="td-name">{{ fish.name }}</td>
                  <td>{{ fish.species }}</td>
                  <td>{{ fish.color }}</td>
                  <td>{{ fish.size }} cm</td>
                  <td>{{ fish.age }} yr{{ fish.age > 1 ? 's' : '' }}</td>
                  <td>
                    <div class="lp-bar-wrap">
                      <div class="lp-bar" :style="{ width: Math.min(fish.lifePoints, 100) + '%', background: lifeColor(fish.lifePoints) }"></div>
                      <span class="lp-label">{{ fish.lifePoints }}</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>

        <!-- Private aquarium -->
        <div v-else class="private-notice">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="1.6">
            <rect x="5" y="11" width="14" height="9" rx="2" />
            <path d="M8 11V7a4 4 0 0 1 8 0v4" />
          </svg>
          <p>This user's aquarium is private.</p>
        </div>
      </div>

    </template>

  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { get_api, post_api } from '@/services/api.js'
import { useAuthStore } from '@/stores/auth.js'

const route = useRoute()
const authStore = useAuthStore()

// ── State ─────────────────────────────────────────────────────────────────────

const user = reactive({
  pseudo:    '',
  role:      '',
  createdAt: null,
  aquarium:  null,
})

const fishes       = ref([])
const loading      = ref(true)
const loadingFish  = ref(false)
const error        = ref(null)

const friendStatus    = ref('NONE') // NONE | PENDING | ACCEPTED | BLOCKED
const sendingRequest  = ref(false)

// ── Computed ──────────────────────────────────────────────────────────────────

const avatarInitial = computed(() =>
  user.pseudo ? user.pseudo.charAt(0).toUpperCase() : '?'
)

const joinedLabel = computed(() => {
  if (!user.createdAt) return ''
  return 'Member since ' + new Date(user.createdAt).toLocaleDateString('en-US', {
    month: 'long', year: 'numeric'
  })
})

const isOwnProfile = computed(() =>
  authStore.pseudo && authStore.pseudo === user.pseudo
)

// ── Helpers ───────────────────────────────────────────────────────────────────

function lifeColor(lp) {
  if (lp >= 70) return '#0d7377'
  if (lp >= 35) return '#f5a623'
  return '#e74c3c'
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
  const pseudo = route.params.pseudo
  const data = await get_api(`/api/users/${pseudo}`)
  Object.assign(user, data)
}

async function loadFish() {
  if (!user.aquarium?.public) return
  loadingFish.value = true
  try {
    fishes.value = await get_api(`/api/users/${user.pseudo}/fishes`)
  } catch {
    fishes.value = []
  } finally {
    loadingFish.value = false
  }
}

async function loadFriendStatus() {
  if (!authStore.pseudo || isOwnProfile.value) return
  try {
    const friends = await get_api(`/api/users/${user.pseudo}/friends`)
    const match = friends.find(f => f.pseudo === authStore.pseudo)
    friendStatus.value = match ? match.status : 'NONE'
  } catch {
    friendStatus.value = 'NONE'
  }
}

async function loadAll() {
  loading.value = true
  error.value   = null
  try {
    await loadProfile()
    await Promise.all([loadFish(), loadFriendStatus()])
  } catch {
    error.value = 'Could not load this profile. Please try again.'
  } finally {
    loading.value = false
  }
}

// ── Friend request ────────────────────────────────────────────────────────────

async function sendFriendRequest() {
  sendingRequest.value = true
  try {
    await post_api(
      `/api/users/${authStore.pseudo}/add-friend?friendPseudo=${encodeURIComponent(user.pseudo)}`,
      {}
    )
    friendStatus.value = 'PENDING'
    showToast(`Friend request sent to ${user.pseudo}.`, 'success')
  } catch {
    showToast('Could not send friend request. Please try again.', 'error')
  } finally {
    sendingRequest.value = false
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(loadAll)
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

/* ── Avatar header ── */
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
.profile-meta { flex: 1; }
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

/* ── Friend action ── */
.friend-action { flex-shrink: 0; }

.btn-primary {
  border: none;
  border-radius: 8px;
  padding: 0.6rem 1.3rem;
  font-size: 0.88rem;
  font-weight: 600;
  cursor: pointer;
  background: #0d7377;
  color: #fff;
  transition: opacity 0.2s;
}
.btn-primary:hover:not(:disabled) { opacity: 0.85; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.status-pill {
  display: inline-block;
  border-radius: 999px;
  padding: 0.4rem 1rem;
  font-size: 0.82rem;
  font-weight: 600;
}
.status-pill--pending  { background: #fff8ee; color: #f5a623; }
.status-pill--accepted { background: #e8f7f7; color: #0d7377; }
.status-pill--blocked  { background: #fde8e8; color: #e74c3c; }

/* ── Section title ── */
.section-title {
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #0d7377;
  margin-bottom: 1rem;
}

/* ── Aquarium info ── */
.aquarium-info {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  text-align: center;
  margin-bottom: 1.2rem;
}
.aquarium-stat strong {
  display: block;
  font-size: 1.2rem;
  color: #0d7377;
  font-weight: 700;
}
.aquarium-stat span { font-size: 0.78rem; color: #888; }

/* ── Private notice ── */
.private-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
  padding: 2.5rem 1rem;
  color: #aaa;
  font-size: 0.9rem;
}

/* ── Table ── */
.table-wrapper {
  border-radius: 10px;
  overflow-x: auto;
  border: 1px solid #f0f4f8;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.88rem;
}

thead tr { border-bottom: 2px solid #f0f4f8; }

th {
  padding: 0.7rem 1rem;
  text-align: left;
  font-size: 0.74rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #0d7377;
  white-space: nowrap;
}

tbody tr { border-bottom: 1px solid #f5f7fa; }
tbody tr:last-child { border-bottom: none; }

td {
  padding: 0.7rem 1rem;
  color: #1a3a4a;
  vertical-align: middle;
}
.td-name { font-weight: 600; }

.lp-bar-wrap {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.lp-bar {
  height: 5px;
  border-radius: 999px;
  min-width: 4px;
  max-width: 60px;
  width: 60px;
}
.lp-label { font-size: 0.8rem; color: #555; }

/* ── States ── */
.state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem 1rem;
  color: #aaa;
  font-size: 0.9rem;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
}
.state-box--inline {
  box-shadow: none;
  background: none;
  padding: 1.5rem 1rem;
}
.state-box--error { color: #e74c3c; }

.spinner {
  width: 28px; height: 28px;
  border: 3px solid #e8f0f0;
  border-top-color: #0d7377;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.btn-ghost {
  border: none;
  border-radius: 8px;
  padding: 0.55rem 1.2rem;
  font-size: 0.88rem;
  cursor: pointer;
  font-weight: 600;
  background: #f0f4f8;
  color: #555;
  transition: opacity 0.2s;
}
.btn-ghost:hover { opacity: 0.8; }

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
  white-space: nowrap;
}
.toast-notif--success { background: #0d7377; }
.toast-notif--error   { background: #e74c3c; }

.toast-enter-active, .toast-leave-active { transition: opacity 0.3s, transform 0.3s; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateX(-50%) translateY(10px); }

/* ── Responsive ── */
@media (max-width: 560px) {
  .profile-header { flex-wrap: wrap; }
  .friend-action { width: 100%; }
  .friend-action .btn-primary { width: 100%; }
  .aquarium-info { grid-template-columns: repeat(2, 1fr); }
}
</style>