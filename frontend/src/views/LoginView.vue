<template>
  <main class="auth-layout">
    <div class="auth-card">

      <h1>Welcome back</h1>
      <p class="auth-subtitle">Log in to manage your aquarium</p>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="pseudo">Pseudo</label>
          <input id="pseudo" type="text" v-model="form.pseudo" autocomplete="username" required />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input id="password" type="password" v-model="form.password" autocomplete="current-password" required />
        </div>

        <p v-if="error" class="auth-error">{{ error }}</p>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Log in' }}
        </button>
      </form>

      <p class="auth-switch">
        Don't have an account? <RouterLink to="/register">Sign up</RouterLink>
      </p>

    </div>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({ pseudo: '', password: '' })
const loading = ref(false)
const error   = ref(null)

async function handleLogin() {
  error.value   = null
  loading.value = true
  try {
    await authStore.login(form.pseudo, form.password)
    router.push('/')
  } catch {
    error.value = 'Invalid pseudo or password.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-layout {
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: var(--c-page-bg);
}

.auth-card {
  background: var(--c-card);
  border-radius: 16px;
  padding: 2.5rem;
  width: 100%;
  max-width: 380px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.auth-card h1 {
  font-size: 1.4rem;
  color: var(--c-heading);
  margin-bottom: 0.3rem;
}

.auth-subtitle {
  font-size: 0.88rem;
  color: var(--c-text-muted);
  margin-bottom: 1.8rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  margin-bottom: 1rem;
}

label {
  font-size: 0.82rem;
  color: var(--c-text-secondary);
  font-weight: 500;
}

input[type="text"],
input[type="password"],
input[type="email"] {
  border: 1.5px solid var(--c-border);
  border-radius: 8px;
  padding: 0.6rem 0.85rem;
  font-size: 0.92rem;
  color: var(--c-text);
  outline: none;
  background: var(--c-input-bg);
  transition: border-color 0.2s;
  width: 100%;
}
input:focus { border-color: var(--c-brand); background: var(--c-card); }

.auth-error {
  color: #e74c3c;
  font-size: 0.85rem;
  margin-bottom: 1rem;
}

.btn-primary {
  width: 100%;
  border: none;
  border-radius: 8px;
  padding: 0.7rem;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  background: var(--c-brand);
  color: #fff;
  transition: opacity 0.2s;
  margin-top: 0.5rem;
}
.btn-primary:hover:not(:disabled) { opacity: 0.88; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.auth-switch {
  text-align: center;
  font-size: 0.85rem;
  color: var(--c-text-muted);
  margin-top: 1.5rem;
}
.auth-switch a {
  color: var(--c-brand);
  font-weight: 600;
  text-decoration: none;
}
.auth-switch a:hover { text-decoration: underline; }
</style>