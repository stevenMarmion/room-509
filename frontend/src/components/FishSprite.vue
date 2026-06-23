<template>
  <div class="fish-sprite" :style="spriteStyle" />
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import spriteUrl from '@/assets/fish_sprite_sheet_64.png'

const props = defineProps({
  color:  { type: String, default: 'blue' },
  bounds: { type: Object, required: true },
})

// ── Sprite sheet config ───────────────────────────────────────
// Chaque poisson fait 64×64px dans le sheet
// Colonnes  → couleurs (dans l'ordre du sheet)
// Lignes    → frames d'animation (4 frames de nage par direction)

const SPRITE_W = 64
const SPRITE_H = 64
const FRAME_COUNT = 4

const COLOR_COL = {
  blue:   0,
  brown:  1,
  purple: 2,
  pink:   3,
  teal:   4,
  orange: 5,
  gold:   6,
  green:  7,
  hotpink:8,
  black:  9,
  red:    10,
}

// Lignes du sprite sheet selon direction
// À ajuster si ton sheet est organisé différemment
const ROW_SWIM_RIGHT = 5
const ROW_SWIM_LEFT  = 5

// ── Mouvement ─────────────────────────────────────────────────
const SPEED     = 1.0
const TICK      = 16    // ms (~60fps)
const ANIM_RATE = 140   // ms par frame

const col = computed(() => COLOR_COL[props.color?.toLowerCase()] ?? 0)

const x  = ref(0)
const y  = ref(0)
const dx = ref((Math.random() < 0.5 ? -1 : 1) * SPEED)
const dy = ref((Math.random() - 0.5) * SPEED * 0.6)
const frame = ref(0)

watch(() => props.bounds.width, (w) => {
  if (w > 0 && x.value === 0) {
    x.value = Math.random() * Math.max(0, w - SPRITE_W)
    y.value = Math.random() * Math.max(0, props.bounds.height - SPRITE_H)
  }
}, { immediate: true })

const direction = computed(() => dx.value >= 0 ? 'right' : 'left')

const spriteStyle = computed(() => {
  if (props.bounds.width === 0) return { display: 'none' }

  const row = direction.value === 'right' ? ROW_SWIM_RIGHT : ROW_SWIM_LEFT
  const bx  = -(col.value * FRAME_COUNT * SPRITE_W + frame.value * SPRITE_W)
  const by  = -(row * SPRITE_H)

  return {
    position:           'absolute',
    top:                '0',
    left:               '0',
    width:              SPRITE_W + 'px',
    height:             SPRITE_H + 'px',
    backgroundImage:    `url(${spriteUrl})`,
    backgroundPosition: `${bx}px ${by}px`,
    backgroundRepeat:   'no-repeat',
    imageRendering:     'pixelated',
    transform:          `translate(${x.value}px, ${y.value}px)`,
    willChange:         'transform',
    cursor:             'pointer',
  }
})

let moveTimer, animTimer

onMounted(() => {
  console.log('FishSprite mounted, color:', props.color, 'bounds:', props.bounds, 'spriteUrl:', spriteUrl)

  moveTimer = setInterval(() => {
    x.value += dx.value
    y.value += dy.value

    // Rebonds sur les bords
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

    // Légère errance aléatoire
    if (Math.random() < 0.008) dy.value = (Math.random() - 0.5) * SPEED * 0.8
    if (Math.random() < 0.003) dx.value *= -1
  }, TICK)

  animTimer = setInterval(() => {
    frame.value = (frame.value + 1) % FRAME_COUNT
  }, ANIM_RATE)
})

onUnmounted(() => {
  clearInterval(moveTimer)
  clearInterval(animTimer)
})
</script>