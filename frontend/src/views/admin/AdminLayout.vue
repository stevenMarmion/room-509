<template>
  <div class="admin-shell">

    <!-- ── Sidebar ── -->
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z"/></svg>
        Admin Panel
      </div>

      <nav class="admin-nav">
        <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" class="admin-nav__item" :class="{ 'admin-nav__item--active': $route.path.startsWith(item.to) }">
          <span class="admin-nav__icon" v-html="item.icon"></span>
          {{ item.label }}
        </RouterLink>
      </nav>

      <RouterLink to="/" class="admin-back">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/></svg>
        Back to app
      </RouterLink>
    </aside>

    <!-- ── Main ── -->
    <div class="admin-main">
      <header class="admin-topbar">
        <h1 class="admin-topbar__title">{{ currentTitle }}</h1>
        <span class="admin-topbar__badge">ADMIN</span>
      </header>
      <div class="admin-content">
        <RouterView />
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, RouterLink, RouterView } from 'vue-router'

const route = useRoute()

const navItems = [
  { to: '/admin/users',         label: 'Users',           icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/></svg>' },
  { to: '/admin/aquariums',     label: 'Aquariums',       icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M20 2H4a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2zm0 18H4V4h16v16z"/></svg>' },
  { to: '/admin/fish',          label: 'Fish',            icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z"/></svg>' },
  { to: '/admin/shop',          label: 'Shop',            icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M7 18c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm10 0c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM5.2 4H2V2H0v2h2l3.6 7.6L4.2 14C4.1 14.3 4 15 4 15c0 1.1.9 2 2 2h14v-2H6.4l.9-1.7H19c.75 0 1.41-.41 1.75-1.03L23 6H5.2z"/></svg>' },
  { to: '/admin/trades',        label: 'Trades',          icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M7 16V4m0 0L3 8m4-4 4 4M17 8v12m0 0 4-4m-4 4-4-4"/></svg>' },
  { to: '/admin/challenges',    label: 'Challenges',      icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M19 3H5c-1.1 0-2 .9-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z"/></svg>' },
  { to: '/admin/friendships',   label: 'Friendships',     icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M16 11c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 3-1.34 3-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05C16.19 13.89 17 14.6 17 15.5V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/></svg>' },
  { to: '/admin/notifications',  label: 'Notifications',  icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"/></svg>' },
  { to: '/admin/config',        label: 'Config',          icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58a.49.49 0 0 0 .12-.61l-1.92-3.32a.49.49 0 0 0-.59-.22l-2.39.96a7.01 7.01 0 0 0-1.62-.94l-.36-2.54a.484.484 0 0 0-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58a.49.49 0 0 0-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"/></svg>' },
]

const currentTitle = computed(() => {
  const item = navItems.find(i => route.path.startsWith(i.to))
  return item ? item.label : 'Admin'
})
</script>

<style scoped>
.admin-shell {
  display: flex;
  min-height: 100vh;
  background: #f0f4f8;
  font-family: 'Inter', system-ui, sans-serif;
}

/* ── Sidebar ── */
.admin-sidebar {
  width: 220px;
  flex-shrink: 0;
  background: #0d2137;
  display: flex;
  flex-direction: column;
  padding: 0;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  color: #fff;
  font-weight: 700;
  font-size: 0.95rem;
  padding: 1.4rem 1.2rem;
  border-bottom: 1px solid rgba(255,255,255,0.08);
  letter-spacing: 0.01em;
}

.admin-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0.8rem 0;
  gap: 2px;
}

.admin-nav__item {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  padding: 0.65rem 1.2rem;
  color: rgba(255,255,255,0.6);
  text-decoration: none;
  font-size: 0.88rem;
  font-weight: 500;
  border-left: 3px solid transparent;
  transition: all 0.15s;
}
.admin-nav__item:hover { color: #fff; background: rgba(255,255,255,0.05); }
.admin-nav__item--active {
  color: #fff;
  background: rgba(13,115,119,0.3);
  border-left-color: #0d7377;
}

.admin-nav__icon { display: flex; align-items: center; opacity: 0.8; }

.admin-back {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 1.2rem;
  color: rgba(255,255,255,0.4);
  text-decoration: none;
  font-size: 0.82rem;
  border-top: 1px solid rgba(255,255,255,0.08);
  transition: color 0.15s;
}
.admin-back:hover { color: rgba(255,255,255,0.8); }

/* ── Main ── */
.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.admin-topbar {
  background: #fff;
  border-bottom: 1px solid #e8edf2;
  padding: 1rem 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.admin-topbar__title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a3a4a;
  margin: 0;
}

.admin-topbar__badge {
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  background: #0d7377;
  color: #fff;
  border-radius: 4px;
  padding: 0.2rem 0.5rem;
}

.admin-content {
  padding: 1.5rem;
  flex: 1;
}
</style>