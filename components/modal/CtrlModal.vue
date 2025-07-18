<script setup lang="ts">
const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  items: {
    type: Array as PropType<Array<{ id: string | number; name: string; code?: string }>>,
    required: true,
    default: () => []
  },
  selectedIds: {
    type: Array as PropType<Array<string | number>>,
    required: true,
    default: () => []
  },
  title: {
    type: String,
    default: 'Seleccionar'
  },
  multiple: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:visible', 'update:selectedIds'])

const searchTerm = ref('')
const localSelectedIds = ref([...props.selectedIds])

// Sincronizar cuando cambian las props...
watch(() => props.selectedIds, (newVal) => {
  localSelectedIds.value = [...newVal]
}, { deep: true })

const filteredItems = computed(() => {
  if (!searchTerm.value) return props.items
  
  const term = searchTerm.value.toLowerCase()
  return props.items.filter(item => {
    const nameMatch = item.name?.toLowerCase().includes(term) || false
    const codeMatch = item.code?.toLowerCase().includes(term) || false
    return nameMatch || codeMatch
  })
})

const isSelected = (id: string | number) => {
  return localSelectedIds.value.includes(id)
}

const toggleSelection = (id: string | number) => {
  if (props.multiple) {
    localSelectedIds.value = isSelected(id)
      ? localSelectedIds.value.filter(itemId => itemId !== id)
      : [...localSelectedIds.value, id]
  } else {
    localSelectedIds.value = isSelected(id) ? [] : [id]
  }
}

const applySelection = () => {
  emit('update:selectedIds', [...localSelectedIds.value])
  emit('update:visible', false)
}

const cancelSelection = () => {
  localSelectedIds.value = [...props.selectedIds]
  emit('update:visible', false)
}
</script>

<template>
  <Dialog
    :visible="visible"
    @update:visible="(val: boolean) => $emit('update:visible', val)"
    :modal="true"
    :style="{ width: '70%' }"
  >
    <template #header>
      <div class="flex align-items-center justify-content-between w-full">
        <h5 class="m-0">{{ title }}</h5>
      </div>
    </template>
    
    <div class="p-fluid pt-2">
      <InputText
        v-model="searchTerm"
        placeholder="Search by name or code..."
        class="w-full mb-3"
      />

      <div style="max-height: 400px; overflow-y: auto;">
        <div
          v-for="item in filteredItems"
          :key="item.id"
          class="flex align-items-center py-2 border-bottom-1 surface-border gap-4 px-2 cursor-pointer hover:surface-100"
          @click="toggleSelection(item.id)"
        >
          <Checkbox 
            :modelValue="isSelected(item.id)" 
            :binary="true" 
            @click.stop="toggleSelection(item.id)"
          />
          <div class="flex-grow-1">
            <div class="font-semibold">{{ item.name || 'Sin nombre' }}</div>
            <div class="text-sm text-500" v-if="item.code">{{ item.code }}</div>
          </div>
        </div>

        <div v-if="filteredItems.length === 0" class="text-center text-gray-500 py-3">
          No hay datos disponibles
        </div>
      </div>
    </div>

    <div class="flex justify-content-between w-full">
      <span class="text-sm text-500 align-self-center">
        Selected: {{ localSelectedIds.length }}
      </span>
      <div class="flex gap-2">
        <Button 
          label="Aplicar" 
          icon="pi pi-check" 
          class="p-button-primary"
          @click="applySelection"
        />
        <Button 
          label="Cancelar" 
          icon="pi pi-times" 
          class="p-button-text"
          @click="cancelSelection"
        />
      </div>
    </div>
  </Dialog>
</template>
<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0; /* top:0; left:0; right:0; bottom:0 */
  background: rgba(0, 0, 0, 0.6); /* fondo gris semitransparente */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* bien arriba */
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  min-width: 300px;
  outline: none; /* para que no tenga borde de foco azul */
}
</style>