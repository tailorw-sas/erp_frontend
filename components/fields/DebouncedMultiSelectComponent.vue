<script setup lang="ts">
import { useDebounceFn } from '@vueuse/core'
import MultiSelect from 'primevue/multiselect'

const props = defineProps({
  suggestions: {
    type: Array,
    required: true
  },
  model: {
    type: Array, // Ahora `model` será un Array directamente para reflejar las selecciones múltiples
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
    default: 'Select options'
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
      <slot name="custom-value" :value="props.model" class="custom-chip">
        <span v-for="(item, index) in props.model.slice(0, props.maxSelectedLabels)" :key="index" class="custom-chip">
          <span class="chip-label" :style="{ color: item.status === 'INACTIVE' ? 'red' : '' }">{{ item[props.field] }}</span>
          <button class="remove-button" aria-label="Remove" @click.stop="removeItem(item)"><i class="pi pi-times-circle" /></button>
        </span>
        <!-- Mostrar un chip adicional con la cantidad restante si se excede el límite -->
        <span v-if="props.model.length > props.maxSelectedLabels" class="custom-chip">
          <span>{{ `+${props.model.length - props.maxSelectedLabels}` }}</span>
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

.p-multiselect .p-multiselect-label {
  padding: 0.2145rem 0.571rem !important;
}

.remove-button:hover {
  color: #2d2d3a; /* Color más oscuro al hacer hover */
}
</style>
