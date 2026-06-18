<template>
  <main class="challenges-layout">

    <!-- ── Header ── -->
    <div class="challenges-header">
      <div>
        <h1>Daily challenges</h1>
        <p class="challenges-date">{{ todayLabel }}</p>
      </div>
      <div class="challenges-summary">
        <span class="summary-count">{{ completedCount }}/{{ challenges.length }}</span>
        <span class="summary-label">completed</span>
        <span class="summary-coins">+{{ earnedCoins }} coins</span>
      </div>
    </div>

    <!-- ── Progress bar ── -->
    <div class="progress-track">
      <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
    </div>

    <!-- ── Loading ── -->
    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Loading challenges...</p>
    </div>

    <!-- ── Error ── -->
    <div v-else-if="error" class="state-box state-box--error">
      <p>{{ error }}</p>
      <button class="btn-ghost" @click="loadChallenges">Try again</button>
    </div>

    <!-- ── Challenge cards ── -->
    <div v-else class="challenges-grid">
      <div
        v-for="challenge in challenges"
        :key="challenge.id"
        class="challenge-card"
        :class="{ 'challenge-card--done': challenge.completed }"
      >
        <!-- Checkmark -->
        <div class="challenge-check" :class="{ 'challenge-check--done': challenge.completed }">
          <svg v-if="challenge.completed" viewBox="0 0 24 24" fill="#fff" width="18" height="18">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
          </svg>
        </div>

        <!-- Content -->
        <div class="challenge-content">
          <strong class="challenge-name">{{ challenge.name }}</strong>
          <span class="challenge-date">{{ formatDate(challenge.date) }}</span>
        </div>

        <!-- Reward -->
        <div class="challenge-reward" :class="{ 'challenge-reward--done': challenge.completed }">
          <svg viewBox="0 0 24 24" width="13" height="13" fill="currentColor"><circle cx="12" cy="12" r="10"/></svg>
          +{{ challenge.reward }}
        </div>
      </div>
    </div>

  </main>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { get_api } from '@/services/api.js'

// ── State ─────────────────────────────────────────────────────────────────────

const challenges = ref([])
const loading    = ref(true)
const error      = ref(null)

// ── Computed ──────────────────────────────────────────────────────────────────

const completedCount = computed(() =>
  challenges.value.filter(c => c.completed).length
)

const earnedCoins = computed(() =>
  challenges.value
    .filter(c => c.completed)
    .reduce((sum, c) => sum + c.reward, 0)
)

const progressPercent = computed(() =>
  challenges.value.length
    ? (completedCount.value / challenges.value.length) * 100
    : 0
)

const todayLabel = computed(() =>
  new Date().toLocaleDateString('en-US', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
)

// ── Helpers ───────────────────────────────────────────────────────────────────

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('en-US', { day: 'numeric', month: 'short' })
}

// ── Load ──────────────────────────────────────────────────────────────────────

async function loadChallenges() {
  loading.value = true
  error.value   = null
  try {
    challenges.value = await get_api('/api/challenges/random')
  } catch {
    error.value = 'Could not load challenges. Please try again.'
  } finally {
    loading.value = false
  }
}

// ── Lifecycle ─────────────────────────────────────────────────────────────────

onMounted(loadChallenges)
</script>

<style scoped>
/* ── Layout ── */
.challenges-layout {
  max-width: 680px;
  margin: 2rem auto;
  padding: 0 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

/* ── Header ── */
.challenges-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
}
.challenges-header h1 {
  font-size: 1.4rem;
  color: #1a3a4a;
  font-weight: 700;
  margin-bottom: 0.2rem;
}
.challenges-date {
  font-size: 0.82rem;
  color: #aaa;
}

.challenges-summary {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
  background: #fff;
  border-radius: 12px;
  padding: 0.6rem 1.2rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
}
.summary-count {
  font-size: 1.3rem;
  font-weight: 700;
  color: #0d7377;
}
.summary-label {
  font-size: 0.82rem;
  color: #aaa;
}
.summary-coins {
  font-size: 0.85rem;
  font-weight: 600;
  color: #f5a623;
  margin-left: 0.4rem;
}

/* ── Progress bar ── */
.progress-track {
  height: 6px;
  background: #e8f0f0;
  border-radius: 999px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background: #0d7377;
  border-radius: 999px;
  transition: width 0.5s ease;
}

/* ── Challenge cards ── */
.challenges-grid {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.challenge-card {
  background: #fff;
  border-radius: 14px;
  padding: 1.1rem 1.3rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
  transition: box-shadow 0.2s, opacity 0.2s;
}
.challenge-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.challenge-card--done { opacity: 0.65; }

/* Checkmark circle */
.challenge-check {
  width: 36px; height: 36px;
  border-radius: 50%;
  border: 2px solid #dde3ea;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  transition: background 0.2s, border-color 0.2s;
}
.challenge-check--done {
  background: #0d7377;
  border-color: #0d7377;
}

/* Content */
.challenge-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}
.challenge-name {
  font-size: 0.95rem;
  color: #1a3a4a;
}
.challenge-date {
  font-size: 0.78rem;
  color: #aaa;
}

/* Reward badge */
.challenge-reward {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.85rem;
  font-weight: 700;
  color: #f5a623;
  background: #fff8ee;
  border-radius: 999px;
  padding: 0.25rem 0.75rem;
  flex-shrink: 0;
}
.challenge-reward--done {
  color: #aaa;
  background: #f5f5f5;
}

/* ── States ── */
.state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem 1rem;
  color: #aaa;
  font-size: 0.9rem;
}
.state-box--error { color: #e74c3c; }

.spinner {
  width: 32px; height: 32px;
  border: 3px solid #e8f0f0;
  border-top-color: #0d7377;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Buttons ── */
.btn-ghost {
  border: none;
  border-radius: 8px;
  padding: 0.6rem 1.4rem;
  font-size: 0.9rem;
  cursor: pointer;
  font-weight: 600;
  background: #f0f4f8;
  color: #555;
  transition: opacity 0.2s;
}
.btn-ghost:hover { opacity: 0.8; }

/* ── Responsive ── */
@media (max-width: 480px) {
  .challenges-header { flex-direction: column; align-items: flex-start; }
}
</style>