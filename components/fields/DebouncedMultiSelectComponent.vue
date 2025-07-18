<script setup lang="ts">
import { useDebounceFn } from '@vueuse/core'
import MultiSelect from 'primevue/multiselect'
import { ref, inject, onMounted, onUnmounted, watch} from 'vue'

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
  },
  contextLabel: {
  type: String,
  default: ''
}
})

const wrapper = ref<HTMLElement | null>(null)

const emit = defineEmits(['load', 'update:modelValue', 'change'])
const allSuggestions = ref<any[]>(props.suggestions)
const hasFocus = ref(false)

const selectionModal = inject('selectionModal') as SelectionModal
const focusManager = inject('focusManager') as { setFocusedComponent: (c: string | null) => void }


if (!selectionModal) {
  console.warn('[DebouncedMultiSelectComponent] No se encontró el modal inyectado (selectionModal).')
}

const typedSuggestions = computed(() => props.suggestions as OptionItem[])
const typedModel = computed(() => props.model as OptionItem[])

function castToTypedItems(rawItems: any[]): OptionItem[] {
  return rawItems.map((item) => ({
    ...item,
    name: item[props.field] ?? item.name ?? 'Sin nombre',
  }))
}

const openSelectionModal = async () => {
  if (!selectionModal) return

  const typedSuggestions = castToTypedItems(props.suggestions)

  const selectedIds = (props.model as OptionItem[]).map(item => item[props.itemValue])

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



const handleKeyDown = (e: KeyboardEvent) => {
  if (e.ctrlKey && e.key === 'F2' && hasFocus.value) {
    e.preventDefault()
    openSelectionModal()
  }
}




const setupFocusHandlers = () => {
  if (wrapper.value) {
    wrapper.value.addEventListener('focusin', () => {
        console.log('[AutoComplete] Focus IN')

      hasFocus.value = true
      window.addEventListener('keydown', handleKeyDown)
      focusManager?.setFocusedComponent('debounced-multiselect')
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


const debouncedComplete = useDebounceFn((event: any) => {
  emit('load', event.value)
}, props.debounceTimeMs, { maxWait: 5000 })

function removeItem(item: OptionItem) {
 const updatedModel = (props.model as OptionItem[]).filter(
  (selectedItem) => selectedItem[props.itemValue] !== item[props.itemValue]
)

  emit('change', updatedModel)
}


watch(() => props.suggestions, (newSuggestions) => {
  const suggestions = newSuggestions as OptionItem[]
  const selectedItems = props.model as OptionItem[]

  const filteredSelectedItems = selectedItems.filter(
    item => !suggestions.some(suggestion =>
      suggestion[props.itemValue] === item[props.itemValue]
    )
  )

  allSuggestions.value = [...suggestions, ...filteredSelectedItems]
}, { deep: true })

</script>

<template>
 <div 
   ref="wrapper" class="multiselect-wrapper" tabindex="0">
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
        <span v-for="(item, index) in (props.model || []).slice(0, props.maxSelectedLabels)" :key="index" class="custom-chip">
          <span class="chip-label" :style="{ color: item.status === 'INACTIVE' ? 'red' : '' }">{{ item[props.field] }}</span>
          <button class="remove-button" aria-label="Remove" @click.stop="removeItem(item)"><i class="pi pi-times-circle" /></button>
        </span>
        <!-- Mostrar un chip adicional con la cantidad restante si se excede el límite -->
        <span v-if="props.model && props.model.length > props.maxSelectedLabels" class="custom-chip">
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
.custom-chip {
  display: inline-flex;
  align-items: center;
  border-radius: 16px; /* Borde redondeado */
  font-size: 1rem;
  margin-right: 2px;
  padding: 0.2145rem 0.571rem;
  background: rgba(68, 72, 109, 0.17);
  color: #44486D;
}

.chip-label {}

.remove-button {
  background-color: transparent;
  border: none;
  color: #5a5a7c; /* Color del botón similar al de la imagen */
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 0 0 0 6px;
  justify-content: center;
}

/* .p-multiselect .p-multiselect-label {
  padding: 0.2145rem 0.571rem !important;
} */

.remove-button:hover {
  color: #2d2d3a; /* Color más oscuro al hacer hover */
}
</style>
