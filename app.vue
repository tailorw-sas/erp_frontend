<script setup lang="ts">
import '~/assets/styles.scss'

useHead({
  link: [
    {
      id: 'theme-link',
      rel: 'stylesheet',
      type: 'text/css',
      href: '/layout/styles/theme/theme-light/blue/theme.css',
    },
  ],

  // este style se inyecta al final del <head>, tras tu tema
  style: [
    {
      children: `
        *, *::before, *::after {
          -webkit-user-select: text !important;
             -moz-user-select: text !important;
              -ms-user-select: text !important;
                  user-select: text !important;
        }
      `
    }
  ]
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
