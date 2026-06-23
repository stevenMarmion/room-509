<template>
  <Transition name="toast">
    <div v-if="toast.visible" class="admin-toast" :class="`admin-toast--${toast.type}`">
      {{ toast.message }}
    </div>
  </Transition>
</template>

<script setup>
defineProps({ toast: Object })
</script>

<style scoped>
.admin-toast {
  position: fixed; bottom: 1.5rem; left: 50%;
  transform: translateX(-50%);
  padding: 0.7rem 1.4rem; border-radius: 8px;
  font-size: 0.88rem; font-weight: 600; color: #fff;
  z-index: 9999; box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  white-space: nowrap;
}
.admin-toast--success { background: #0d7377; }
.admin-toast--error   { background: #c0392b; }
.toast-enter-active, .toast-leave-active { transition: opacity 0.25s, transform 0.25s; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateX(-50%) translateY(8px); }
</style>