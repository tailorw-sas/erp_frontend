import { defineNuxtPlugin } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.directive('user-selectable', {
    mounted(el: HTMLElement) {
      const enable = (el: HTMLElement) => {
        el.style.setProperty('user-select', 'text', 'important')
        el.style.setProperty('-webkit-user-select', 'text', 'important')
        el.style.setProperty('-moz-user-select', 'text', 'important')
        el.style.setProperty('-ms-user-select', 'text', 'important')
        // opcional: eliminar handlers que bloqueen selección
        el.onmousedown = null
        el.onselectstart = null
      }

      enable(el)
      el.querySelectorAll<HTMLElement>('*').forEach(enable)
    }
  })
})
