<script setup lang="ts">
import '~/assets/styles.scss'
import { onMounted } from 'vue'

useHead({
  link: [
    {
      id: 'theme-link',
      rel: 'stylesheet',
      type: 'text/css',
      href: '/layout/styles/theme/theme-light/blue/theme.css',
    },
  ],
})
onMounted(() => {
  onMounted(() => {
  // 1) Forzar CSS al final del <head>
    const prev = document.getElementById('force-user-select')
    if (prev) { prev.remove() }

    const css = `
    *, *::before, *::after {
      -webkit-user-select: text !important;
         -moz-user-select: text !important;
          -ms-user-select: text !important;
              user-select: text !important;
    }
  `
    const styleTag = document.createElement('style')
    styleTag.id = 'force-user-select'
    styleTag.appendChild(document.createTextNode(css))
    document.head.appendChild(styleTag)

    // 2) Eliminar atributos "unselectable" si los hubiera
    document.querySelectorAll('[unselectable]').forEach(el => el.removeAttribute('unselectable'))

    // 3) Quitar handlers inline que bloqueen la selección
    window.onselectstart = null
    window.ondragstart = null

    // 4) Añadir listeners en fase de captura para evitar que otros handlers
    //    cancelen el select, copy, etc.
    const events = [
      'selectstart',
      'copy',
      'cut',
      'mousedown',
      'mouseup',
      'keydown',
      'contextmenu'
    ]
    events.forEach((evt) => {
      document.addEventListener(evt, (e) => {
      // detenemos la propagación hacia cualquier handler que haga e.preventDefault()
        e.stopImmediatePropagation()
      // no llamamos a preventDefault, de modo que el navegador por defecto copia el texto
      }, true /* captura */)
    })
  })
})
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<style lang="scss">
.p-toast.p-toast-top-right {
  top: 10px !important;
}
.custom-card-header {
    margin-bottom: 0px;
    border-top-left-radius: 5px;
    border-top-right-radius: 5px;
    padding-top: 10px;
    padding-bottom: 10px;
  }

  td.p-frozen-column {
  background-color: #fff !important;
}
th .p-column-header-content .p-checkbox.p-component.p-highlight .p-checkbox-box {
    /* Estilos que quieres aplicar */
    background-color: --var(--secondary-color);
    border-color: rgb(255, 255, 255); /* Ejemplo de estilo */
}
th .p-column-header-content .p-checkbox.p-component .p-checkbox-box {
    /* Estilos que quieres aplicar */
    background-color: --var(--secondary-color);
    border-color: rgb(255, 255, 255); /* Ejemplo de estilo */
}
</style>
