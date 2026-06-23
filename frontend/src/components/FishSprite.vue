<template>
  <div class="fish-sprite" :style="spriteStyle" />
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import spriteUrl from '@/assets/fish_sprite_sheet_64.png'

const props = defineProps({
  color:      { type: String, default: 'blue' },
  bounds:     { type: Object, default: () => ({ width: 64, height: 64 }) },
  lifePoints: { type: Number, default: 100 },
  animation:  { type: String, default: null },
  king:       { type: Boolean, default: false },
  static:     { type: Boolean, default: false },
})

const SPRITE_W = 64
const SPRITE_H = 64
const FRAME_COUNT = 4

const COLOR_INDEX = {
  blue: 0, brown: 1, purple: 2, pink: 3,
  teal: 4, orange: 5, gold: 6, green: 7,
  hotpink: 8, black: 9, red: 10,
  grey: 9, gray: 9,
}

const ANIM_ROW = {
  bubbles: 0,
  swim:    1,
  crown:   3,
  heal:    4,
  shimmer: 5,
  dead:    6,
  dash:    7,
}

const SPEED     = 1.0
const DASH_SPEED = 4.0
const TICK      = 16
const ANIM_RATE = 140

const colorIdx = computed(() => COLOR_INDEX[props.color?.toLowerCase()] ?? 0)

const x  = ref(0)
const y  = ref(0)
const dx = ref((Math.random() < 0.5 ? -1 : 1) * SPEED)
const dy = ref((Math.random() - 0.5) * SPEED * 0.6)
const frame = ref(0)

// ── Animation state ──────────────────────────────────────────
const activeEffect = ref(null) // 'bubbles' | 'shimmer' | 'heal' | 'dash' | null
const isDead = computed(() => props.lifePoints <= 0)
const isDashing = computed(() => activeEffect.value === 'dash')
const isFrozen = computed(() => isDead.value || activeEffect.value === 'bubbles' || activeEffect.value === 'shimmer')

// Detect HP gain to trigger heal animation
let prevLifePoints = props.lifePoints
watch(() => props.lifePoints, (newVal, oldVal) => {
  if (newVal > oldVal && newVal > 0) {
    triggerEffect('heal', 1500)
  }
  prevLifePoints = newVal
})

function triggerEffect(name, durationMs) {
  activeEffect.value = name
  setTimeout(() => {
    if (activeEffect.value === name) activeEffect.value = null
  }, durationMs)
}

// ── Animation row resolution ─────────────────────────────────
const resolvedAnimRow = computed(() => {
  if (props.animation && ANIM_ROW[props.animation] !== undefined) {
    return ANIM_ROW[props.animation]
  }

  if (isDead.value) return ANIM_ROW.dead

  if (activeEffect.value === 'dash')    return ANIM_ROW.dash
  if (activeEffect.value === 'heal')    return ANIM_ROW.heal
  if (activeEffect.value === 'bubbles') return ANIM_ROW.bubbles
  if (activeEffect.value === 'shimmer') return ANIM_ROW.shimmer

  if (props.king) return ANIM_ROW.crown

  return ANIM_ROW.swim
})

// ── Sprite flip (all anims flip based on direction) ──────────
const flipX = computed(() => dx.value >= 0)

// ── Position init ────────────────────────────────────────────
watch(() => props.bounds.width, (w) => {
  if (w > 0 && x.value === 0 && !props.static) {
    x.value = Math.random() * Math.max(0, w - SPRITE_W)
    y.value = Math.random() * Math.max(0, props.bounds.height - SPRITE_H)
  }
}, { immediate: true })

// ── Styles ───────────────────────────────────────────────────
const spriteStyle = computed(() => {
  const idx = colorIdx.value
  const band = Math.floor(idx / 4)
  const posInBand = idx % 4
  const animRow = resolvedAnimRow.value

  const bx = -(posInBand * FRAME_COUNT * SPRITE_W + frame.value * SPRITE_W)
  const by = -(band * 8 * SPRITE_H + animRow * SPRITE_H)

  const base = {
    width:              SPRITE_W + 'px',
    height:             SPRITE_H + 'px',
    backgroundImage:    `url(${spriteUrl})`,
    backgroundPosition: `${bx}px ${by}px`,
    backgroundRepeat:   'no-repeat',
    imageRendering:     'pixelated',
  }

  if (props.static) {
    return { ...base, display: 'inline-block', verticalAlign: 'middle' }
  }

  if (props.bounds.width === 0) return { display: 'none' }

  const scaleStr = flipX.value ? ' scaleX(-1)' : ''
  return {
    ...base,
    position:  'absolute',
    top:       '0',
    left:      '0',
    transform: `translate(${x.value}px, ${y.value}px)${scaleStr}`,
    willChange: 'transform',
    cursor:    'pointer',
  }
})

// ── Timers ───────────────────────────────────────────────────
let moveTimer, animTimer, effectTimer

onMounted(() => {
  animTimer = setInterval(() => {
    frame.value = (frame.value + 1) % FRAME_COUNT
  }, ANIM_RATE)

  if (props.static) return

  // Movement
  moveTimer = setInterval(() => {
    if (isFrozen.value) return

    const speed = isDashing.value ? DASH_SPEED : SPEED
    const currentDx = dx.value >= 0 ? speed : -speed
    const currentDy = isDashing.value ? 0 : dy.value

    x.value += currentDx
    y.value += currentDy

    if (x.value <= 0) {
      x.value = 0
      dx.value = Math.abs(dx.value)
    }
    if (x.value >= props.bounds.width - SPRITE_W) {
      x.value = props.bounds.width - SPRITE_W
      dx.value = -Math.abs(dx.value)
    }
    if (y.value <= 0) {
      y.value = 0
      dy.value = Math.abs(dy.value)
    }
    if (y.value >= props.bounds.height - SPRITE_H) {
      y.value = props.bounds.height - SPRITE_H
      dy.value = -Math.abs(dy.value)
    }

    if (!isDashing.value) {
      if (Math.random() < 0.008) dy.value = (Math.random() - 0.5) * SPEED * 0.8
      if (Math.random() < 0.003) dx.value *= -1
    }
  }, TICK)

  // Random effects: bubbles, shimmer, dash
  effectTimer = setInterval(() => {
    if (isDead.value || activeEffect.value || props.king) return

    const roll = Math.random()
    if (roll < 0.03) {
      triggerEffect('bubbles', FRAME_COUNT * ANIM_RATE * 2)
    } else if (roll < 0.06) {
      triggerEffect('shimmer', FRAME_COUNT * ANIM_RATE * 2)
    } else if (roll < 0.08) {
      triggerEffect('dash', 600)
    }
  }, 2000)
})

onUnmounted(() => {
  clearInterval(moveTimer)
  clearInterval(animTimer)
  clearInterval(effectTimer)
})
</script>
