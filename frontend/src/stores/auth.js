import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { post_api, get_api } from '@/services/api.js'

export const useAuthStore = defineStore('auth', () => {

  // ── State ───────────────────────────────────────────────────────────────────
  // The JWT itself lives in an httpOnly cookie (set by the backend) — invisible
  // and inaccessible to JS. We only keep the user's pseudo here to know who is
  // logged in, persisted across refresh via sessionStorage.

  const pseudo = ref(sessionStorage.getItem('pseudo') || null)
  const user   = ref(null)
  const role = ref(sessionStorage.getItem('role') || null)
  const isAdmin = computed(() => role.value === 'ADMIN')

  const isAuthenticated = computed(() => !!pseudo.value)

  // ── Actions ─────────────────────────────────────────────────────────────────

  async function login(loginPseudo, password) {
    const data = await post_api('/api/auth/login', { pseudo: loginPseudo, password })
    setSession(data)
    return data
  }

  async function register(newPseudo, email, password, passwordConfirm) {
    const data = await post_api('/api/auth/register', {
      pseudo: newPseudo,
      email,
      password,
      passwordConfirm,
    })
    setSession(data)
    return data
  }

  async function logout() {
    try {
      await post_api('/api/auth/logout', {})
    } finally {
      clearSession()
    }
  }

  // Re-checks the session on app load (e.g. after a page refresh).
  // The cookie travels automatically with the request if still valid.
  async function fetchCurrentUser() {
    if (!pseudo.value) return
    try {
      const data = await get_api(`/api/users/${encodeURIComponent(pseudo.value)}`)
      user.value = data
    } catch {
      clearSession()
    }
  }

  function setSession(data) {
    user.value   = data
    pseudo.value = data.pseudo
    role.value = data.role
    sessionStorage.setItem('pseudo', data.pseudo)
    sessionStorage.setItem('role', data.role)
  }

  function clearSession() {
    user.value   = null
    pseudo.value = null
    role.value = null
    sessionStorage.removeItem('pseudo')
    sessionStorage.removeItem('role')
  }

  return {
    pseudo,
    user,
    isAuthenticated,
    login,
    register,
    logout,
    fetchCurrentUser,
    role,
    isAdmin
  }
})