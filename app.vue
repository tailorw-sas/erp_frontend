<script setup lang="ts">
import '~/assets/styles.scss'
import { ref } from 'vue'
import { onMounted, onUnmounted } from 'vue'

// Variable para rastrear el componente enfocado
const focusedComponent = ref<string | null>(null)
// Manejar eventos de teclado globalmente
const handleKeyDown = (e: KeyboardEvent) => {
  if (e.ctrlKey && e.key === 'F2' && focusedComponent.value === 'debounced-multiselect') {
    e.preventDefault()
    // Aquí puedes abrir el modal si es necesario
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
})

provide('focusManager', {
  setFocusedComponent: (component: string | null) => {
    focusedComponent.value = component
  }
})

// AQUI ESTA EL ETADO GLOBAL DEL MODAL....(SOLO aquí)
const modalState = ref({
  visible: false,
  title: 'Seleccionar',
  items: [] as Array<{ id: string | number; name: string; code?: string }>,
  selectedIds: [] as Array<string | number>,
  multiple: true,
  resolve: null as Function | null
})

// AQUI ESTA LA FUNCION PARA ABRIR EL MODALL (exportada globalmente)

function openSelectionModal(options: {
  items: Array<{ id: string | number; name: string; code?: string }>,
  selectedIds?: Array<string | number>,
  title?: string,
  multiple?: boolean
}): Promise<Array<string | number>> {
  return new Promise((resolve) => {
    modalState.value = {
      visible: true,
      title: options.title || 'Seleccionar',
      items: options.items,
      selectedIds: options.selectedIds || [],
      multiple: options.multiple !== false,
      resolve
    }
  })
}

provide('selectionModal', { open: openSelectionModal })

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

</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>

    <CtrlModal
    :visible="modalState.visible"
    :items="modalState.items"
    :selectedIds="modalState.selectedIds"
    :title="modalState.title"
    :multiple="modalState.multiple"
    @update:visible="(val: boolean) => { 
  modalState.visible = val
  if (!val && modalState.resolve) {
    modalState.resolve([...modalState.selectedIds])
    modalState.resolve = null
  }
}"
   @update:selectedIds="(val: Array<string | number>) => modalState.selectedIds = val"
  />
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
