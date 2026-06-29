<template>
  <footer class="footer">
    <div class="footer-inner">

      <!-- Brand -->
      <div class="footer-brand">
        <span class="footer-name">Manage your little fish</span>
      </div>

      <!-- Links -->
      <nav class="footer-links">
        <RouterLink to="/">Aquarium</RouterLink>
        <RouterLink to="/my-fish">My Fish</RouterLink>
        <RouterLink to="/friends">Friends</RouterLink>
        <RouterLink to="/shop">Shop</RouterLink>
        <RouterLink to="/daily-challenges">Challenges</RouterLink>
      </nav>

      <!-- Legal -->
      <div class="footer-legal">
        © {{ currentYear }} Manage your little fish · All rights reserved
      </div>

    </div>
  </footer>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const currentYear = new Date().getFullYear()
</script>

<style scoped>
.footer {
  /* margin-top: 300px; */
  background: var(--c-brand);
  color: rgba(255, 255, 255, 0.75);
}

.footer-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
}

/* ── Brand ── */
.footer-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
  color: #fff;
}
.footer-logo { font-size: 1.2rem; }

/* ── Links ── */
.footer-links {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.75);
  text-decoration: none;
  transition: color 0.2s;
}
.footer-links a:hover,
.footer-links a.router-link-active {
  color: #fff;
}

/* ── Legal ── */
.footer-legal {
  color: rgba(255, 255, 255, 0.5);
}

/* ── Responsive ── */
@media (max-width: 600px) {
  .footer-inner {
    flex-direction: column;
    align-items: flex-start;
    gap: 1.2rem;
  }
  .footer-links { gap: 1rem; }
}
</style>