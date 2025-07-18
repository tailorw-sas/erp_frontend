<script setup lang="ts">
import { useDebounceFn } from '@vueuse/core'
import MultiSelect from 'primevue/multiselect'

interface OptionItem {
  id: string | number;
  name: string;
  code?: string;
  [key: string]: any;
}
interface SelectionModal {
  open: (options: {
    items: OptionItem[],
    selectedIds?: Array<string | number>,
    title?: string,
    multiple?: boolean
  }) => Promise<Array<string | number>>
}
const selectionModal = inject('selectionModal') as SelectionModal
const focusManager = inject('focusManager') as { setFocusedComponent: (c: string | null) => void }

if (!selectionModal) {
  console.warn('[DebouncedAutoCompleteComponent] No se encontró el modal inyectado.')
}

const props = defineProps({
  suggestions: {
    type: Array,
    required: true
  },
  model: {
    type: Array, // Ahora model será un Array directamente para reflejar las selecciones múltiples
    required: true
  },
  field: {
    type: String,
    required: true
  },
  itemValue: {
    type: String,
    required: true
  },
  placeholder: {
    type: String,
    default: 'Type to search'
  },
  id: {
    type: String,
    default: 'multiselect'
  },
  maxSelectedLabels: {
    type: Number,
    default: 2
  },
  debounceTimeMs: {
    type: Number,
    default: 300
  },
  disabled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['load', 'update:modelValue', 'change'])
const allSuggestions = ref<any[]>(props.suggestions)

const debouncedComplete = useDebounceFn((event: any) => {
  emit('load', event.value)
}, props.debounceTimeMs, { maxWait: 5000 })

const wait = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

function removeItem(item: any) {
  const updatedModel = props.model.filter(
    (selectedItem: any) => selectedItem[props.itemValue] !== item[props.itemValue]
  )
  emit('change', updatedModel)
}


const wrapper = ref<HTMLElement | null>(null)
const hasFocus = ref(false)

const handleKeyDown = (e: KeyboardEvent) => {
  if (e.ctrlKey && e.key === 'F2' && hasFocus.value) {
    e.preventDefault()
    openSelectionModal()
  }
}

const setupFocusHandlers = () => {
  if (wrapper.value) {
    wrapper.value.addEventListener('focusin', () => {
      hasFocus.value = true
      window.addEventListener('keydown', handleKeyDown)
      focusManager?.setFocusedComponent('debounced-autocomplete')
    })
    wrapper.value.addEventListener('focusout', () => {
      hasFocus.value = false
      window.removeEventListener('keydown', handleKeyDown)
      focusManager?.setFocusedComponent(null)
    })
  }
}

onMounted(setupFocusHandlers)
onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
  focusManager?.setFocusedComponent(null)
})


function castToTypedItems(rawItems: any[]): OptionItem[] {
  return rawItems.map((item) => ({
    ...item,
    name: item[props.field] ?? item.name ?? 'Sin nombre',
  }))
}

const openSelectionModal = async () => {
  if (!selectionModal) return

  const typedSuggestions = castToTypedItems(props.suggestions)
const selectedIds = Array.isArray(props.model)
  ? props.model.map((item: any) => item[props.itemValue])
  : props.model
    ? [props.model[props.itemValue]]
    : []

  const newSelectedIds = await selectionModal.open({
    items: typedSuggestions,
    selectedIds,
    title: 'Seleccionar',
    multiple: true
  })

  const selectedItems = typedSuggestions.filter(item =>
    (newSelectedIds as (string | number)[]).includes(item[props.itemValue])
  )

  emit('change', selectedItems)
}


watch(() => props.suggestions, (newSuggestions) => {
  const selectedItems = [...props.model]

  // Filtra los elementos de selectedItems que no están en newSuggestions
  const filteredSelectedItems = selectedItems.filter(
    (item: any) => !newSuggestions.some((suggestion: any) => suggestion[props.itemValue] === item[props.itemValue])
  )

  // Agrega los elementos seleccionados al final de la lista de sugerencias
  allSuggestions.value = [...newSuggestions, ...filteredSelectedItems]
})
</script>

<template>
    <div ref="wrapper" class="multiselect-wrapper">

  <MultiSelect
    :id="props.id"
    class="w-full"
    style="overflow: hidden"
    :model-value="props.model"
    :options="allSuggestions"
    :option-label="props.field"
    :placeholder="props.placeholder"
    :disabled="props.disabled"
    :filter="true"
    :loading="props.loading"
    :max-selected-labels="props.maxSelectedLabels"
    @filter="debouncedComplete"
    @change="async ($event) => {
      const ev = JSON.parse(JSON.stringify($event.value))
      await wait(50) // Espera 50ms
      emit('change', ev)
    }"
    @before-show="($event) => {
      emit('load', '')
    }"
  >
    <template #value>
      <slot name="custom-value" :value="props.model" :remove-item="removeItem" class="custom-chip">
        <span v-for="(item, index) in (Array.isArray(props.model) ? props.model : []).slice(0, props.maxSelectedLabels)" :key="index" class="custom-chip">
          <span class="chip-label" :style="{ color: item.status === 'INACTIVE' ? 'red' : '' }">{{ item[props.field] }}</span>
          <button class="remove-button" aria-label="Remove" @click.stop="removeItem(item)"><i class="pi pi-times-circle" /></button>
        </span>
        <!-- Mostrar un chip adicional con la cantidad restante si se excede el límite -->
        <span v-if="Array.isArray(props.model) && props.model.length > props.maxSelectedLabels"
 class="custom-chip">
         <span>{{ props.model.length - props.maxSelectedLabels }}</span>
         </span>
      </slot>
    </template>
    <template #option="slotProps">
      <slot name="option" :item="slotProps.option">
        <div
          :style="{ color: slotProps.option.status === 'INACTIVE' ? 'red' : '' }"
        >
          {{ slotProps.option[props.field] }}
        </div>
      </slot>
    </template>
  </MultiSelect>
  </div>
</template>

<style>
.p-autocomplete .p-button {
  color: #676B89 !important;
  background: rgba(68, 72, 109, 0.07) !important;
  border: transparent !important;
  padding-right: 4px !important;
}
.p-autocomplete-multiple-container {
  width: 100%;
}
</style>
