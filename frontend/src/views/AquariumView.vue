<template>
  <main class="aq-layout">

    <!-- ── Toast ── -->
    <Transition name="toast">
      <div v-if="toast.visible" class="toast-notif" :class="`toast-notif--${toast.type}`">
        {{ toast.message }}
      </div>
    </Transition>

    <!-- ── Loading ── -->
    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Loading your aquarium...</p>
    </div>

    <!-- ── Error ── -->
    <div v-else-if="error" class="state-box state-box--error">
      <p>{{ error }}</p>
      <button class="btn-ghost" @click="loadAll">Try again</button>
    </div>

    <!-- ── Content ── -->
    <template v-else>

      <!-- Header -->
      <div class="acard">
        <div class="aq-header">
          <div>
            <h1>{{ aquarium.name }}</h1>
            <p class="aq-subtitle">Your aquarium</p>
          </div>

          <!-- Visibility toggle -->
          <div class="visibility-toggle">
            <span class="visibility-label">{{ aquarium.public ? 'Public' : 'Private' }}</span>
            <label class="toggle">
              <input type="checkbox" v-model="aquarium.public" :disabled="saving" @change="toggleVisibility" />
              <div class="toggle-track"></div>
            </label>
          </div>
        </div>

        <p class="visibility-hint">
          <svg v-if="aquarium.public" viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z" /><circle cx="12" cy="12" r="3" />
          </svg>
          <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="5" y="11" width="14" height="9" rx="2" /><path d="M8 11V7a4 4 0 0 1 8 0v4" />
          </svg>
          {{ aquarium.public
              ? 'Other users can view your aquarium and fish.'
              : 'Only you can see your aquarium and fish.' }}
        </p>
      </div>

      <!-- Stats -->
      <div class="acard">
        <div class="section-title">Stats</div>
        <div class="stats-grid">
          <div class="stat-item">
            <strong>{{ aquarium.level }}</strong>
            <span>Level</span>
          </div>
          <div class="stat-item">
            <strong>{{ fishes.length }} / {{ aquarium.capacity }}</strong>
            <span>Fish capacity</span>
          </div>
          <div class="stat-item">
            <strong>{{ capacityPercent }}%</strong>
            <span>Filled</span>
          </div>
        </div>
        <div class="capacity-track">
          <div class="capacity-fill" :style="{ width: capacityPercent + '%' }"></div>
        </div>
      </div>

      <!-- Fish preview -->
      <div class="acard">
        <div class="fish-header">
          <div class="section-title" style="margin-bottom: 0;">Your fish</div>
          <RouterLink to="/my-fish" class="link-more">View all in My Fish →</RouterLink>
        </div>

        <div v-if="fishes.length === 0" class="state-box state-box--inline">
          <p>Your aquarium has no fish yet.</p>
          <RouterLink to="/shop" class="btn-ghost">Visit the shop</RouterLink>
        </div>

        <div v-else class="fish-grid">
          <RouterLink
            v-for="fish in fishes.slice(0, 6)"
            :key="fish.id"
            to="/my-fish"
            class="fish-chip"
          >
            <span class="fish-dot" :style="{ background: lifeColor(fish.lifePoints) }"></span>
            <span class="fish-name">{{ fish.name }}</span>
            <span class="fish-species">{{ fish.species }}</span>
          </RouterLink>
        </div>

        <RouterLink v-if="fishes.length > 6" to="/my-fish" class="link-more link-more--block">
          + {{ fishes.length - 6 }} more fish — see details in My Fish
        </RouterLink>
      </div>

    </template>

  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { get_api, put_api } from '@/services/api.js'
import { useAuthStore } from '@/stores/auth.js'

const authStore = useAuthStore()

// ── State ─────────────────────────────────────────────────────────────────────

const aquarium = reactive({
  id:        null,
  name:      '',
  public:    false,
  level:     0,
  capacity:  0,
})

const fishes  = ref([])
const loading = ref(true)
const error   = ref(null)
const saving  = ref(false)

// ── Computed ──────────────────────────────────────────────────────────────────

const capacityPercent = computed(() => {
  if (!aquarium.capacity) return 0
  return Math.min(100, Math.round((fishes.value.length / aquarium.capacity) * 100))
})

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

// ── Load ──────────────────────────────────────────────────────────────────────

async function loadAll() {
  loading.value = true
  error.value   = null
  try {
    const user = await get_api(`/api/users/${authStore.pseudo}`)
    Object.assign(aquarium, user.aquarium)
    fishes.value = await get_api(`/api/users/${authStore.pseudo}/fishes`)
  } catch {
    error.value = 'Could not load your aquarium. Please try again.'
  } finally {
    loading.value = false
  }
}

// ── Toggle visibility ─────────────────────────────────────────────────────────

async function toggleVisibility() {
  const previousValue = !aquarium.public
  saving.value = true
  try {
    await put_api(`/api/aquariums/${aquarium.id}/visibility`, aquarium.public)
    showToast(`Your aquarium is now ${aquarium.public ? 'public' : 'private'}.`, 'success')
  } catch {
    aquarium.public = previousValue
    showToast('Could not update visibility. Please try again.', 'error')
  } finally {
    saving.value = false
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(loadAll)
</script>

<style scoped>
/* ── Layout ── */
.aq-layout {
  max-width: 760px;
  margin: 2rem auto;
  padding: 0 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

/* ── Card ── */
.acard {
  background: var(--c-card);
  border-radius: 14px;
  padding: 1.5rem;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
}

/* ── Header ── */
.aq-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}
.aq-header h1 {
  font-size: 1.3rem;
  color: var(--c-heading);
  font-weight: 700;
}
.aq-subtitle {
  font-size: 0.82rem;
  color: var(--c-text-muted);
  margin-top: 0.2rem;
}

/* ── Visibility toggle ── */
.visibility-toggle {
  display: flex;
  align-items: center;
  gap: 0.7rem;
}
.visibility-label {
  font-size: 0.88rem;
  font-weight: 600;
  color: var(--c-text);
}

.toggle { position: relative; width: 52px; height: 28px; flex-shrink: 0; }
.toggle input { display: none; }
.toggle-track {
  position: absolute;
  inset: 0;
  background: var(--c-border);
  border-radius: 999px;
  cursor: pointer;
  transition: background 0.3s;
}
.toggle input:checked + .toggle-track { background: var(--c-brand); }
.toggle input:disabled + .toggle-track { opacity: 0.6; cursor: not-allowed; }
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

.visibility-hint {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.82rem;
  color: var(--c-text-muted);
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--c-divider);
}

/* ── Section title ── */
.section-title {
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--c-brand);
  margin-bottom: 1rem;
}

/* ── Stats ── */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  text-align: center;
  margin-bottom: 1.2rem;
}
.stat-item strong {
  display: block;
  font-size: 1.4rem;
  color: var(--c-brand);
  font-weight: 700;
}
.stat-item span { font-size: 0.8rem; color: var(--c-text-muted); }

.capacity-track {
  height: 8px;
  background: var(--c-track);
  border-radius: 999px;
  overflow: hidden;
}
.capacity-fill {
  height: 100%;
  background: var(--c-brand);
  border-radius: 999px;
  transition: width 0.5s ease;
}

/* ── Fish section ── */
.fish-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.link-more {
  font-size: 0.84rem;
  color: var(--c-brand);
  text-decoration: none;
  font-weight: 600;
}
.link-more:hover { text-decoration: underline; }
.link-more--block {
  display: block;
  text-align: center;
  margin-top: 1rem;
}

.fish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 0.7rem;
}

.fish-chip {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  background: var(--c-row-hover);
  border-radius: 10px;
  padding: 0.7rem 0.9rem;
  text-decoration: none;
  color: inherit;
  transition: background 0.2s;
}
.fish-chip:hover { background: var(--c-row-alt); }

.fish-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  margin-bottom: 0.2rem;
}
.fish-name { font-size: 0.88rem; font-weight: 600; color: var(--c-text); }
.fish-species { font-size: 0.76rem; color: var(--c-text-muted); }

/* ── States ── */
.state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem 1rem;
  color: var(--c-text-muted);
  font-size: 0.9rem;
  background: var(--c-card);
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
  width: 32px; height: 32px;
  border: 3px solid var(--c-track);
  border-top-color: var(--c-brand);
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
  background: var(--c-ghost-bg);
  color: var(--c-ghost-text);
  text-decoration: none;
  transition: opacity 0.2s;
  display: inline-block;
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
.toast-notif--success { background: var(--c-brand); }
.toast-notif--error   { background: #e74c3c; }

.toast-enter-active, .toast-leave-active { transition: opacity 0.3s, transform 0.3s; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateX(-50%) translateY(10px); }

/* ── Responsive ── */
@media (max-width: 560px) {
  .aq-header { flex-direction: column; align-items: flex-start; }
  .stats-grid { grid-template-columns: 1fr; }
}
</style>