<!-- components/CopyableArea.vue -->
<script setup lang="ts">
import { onMounted, ref } from 'vue'

const wrapper = ref<HTMLElement | null>(null)

onMounted(() => {
  const el = wrapper.value!
  // 1) Forzar estilos de selección
  Object.assign(el.style, {
    userSelect: 'text',
    WebkitUserSelect: 'text',
    MozUserSelect: 'text',
    msUserSelect: 'text'
  })
  // 2) Eliminar atributo unselectable (IE/old)
  el.removeAttribute('unselectable')

  // 3) En modo captura, detener cualquier preventDefault/stopPropagation
  const blockEvents = ['selectstart', 'copy', 'cut', 'contextmenu', 'mousedown', 'mouseup']
  blockEvents.forEach((evt) => {
    el.addEventListener(evt, (e) => {
      // que no suba a otros handlers que bloqueen
      e.stopImmediatePropagation()
      // y no prevenir el default: dejamos que copie
    }, true)
  })
})
</script>

<template>
  <div ref="wrapper" class="copyable-area">
    <slot />
  </div>
</template>

<style scoped>
.copyable-area {
  /* por si acaso, fuerza en CSS también */
  -webkit-user-select: text !important;
     -moz-user-select: text !important;
      -ms-user-select: text !important;
          user-select: text !important;
}
</style>
