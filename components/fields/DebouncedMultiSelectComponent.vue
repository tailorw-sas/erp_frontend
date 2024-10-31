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
/* const computedSuggestions = computed(() => {
  // Si `suggestions` no contiene todos los elementos de `model`, añádelos temporalmente
  console.log(props.suggestions)
  console.log(localModelValue.value)
  return [...new Set([...props.suggestions, ...props.model])]
}) */

const computedSuggestions = computed(() => {
  // Copia las sugerencias para no modificar el arreglo original
  const updatedSuggestions = [...props.suggestions]

  // Elimina cualquier elemento que ya esté seleccionado
  props.model.forEach((selectedItem: any) => {
    const index = updatedSuggestions.findIndex(
      (item: any) => item[props.itemValue] === selectedItem[props.itemValue]
    )
    if (index !== -1) {
      updatedSuggestions.splice(index, 1) // Elimina el duplicado
    }
  })

  // Agrega los elementos seleccionados al final de la lista de sugerencias
  return [...updatedSuggestions, ...props.model]
})

const debouncedComplete = useDebounceFn((event: any) => {
  emit('load', event.value)
}, props.debounceTimeMs, { maxWait: 5000 })

const wait = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

function removeItem(item: any) {
  const updatedModel = props.model.filter(
    (selectedItem: any) => selectedItem[props.itemValue] !== item[props.itemValue]
  )
  console.log(updatedModel)
  emit('change', updatedModel)
}
</script>

<template>
  <MultiSelect
    :id="props.id"
    :model-value="props.model"
    :options="computedSuggestions"
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
  >
    <template #value>

      <slot name="custom-value" :value="props.model" class="custom-chip">
        <span v-for="(item, index) in props.model.slice(0, props.maxSelectedLabels)" :key="index" class="custom-chip">
          <span class="chip-label">{{ item[props.field] }}</span>
          <button @click.stop="removeItem(item)" class="remove-button" aria-label="Remove"><i class="pi pi-times-circle" /></button>
        </span>
        <!-- Mostrar un chip adicional con la cantidad restante si se excede el límite -->
        <span v-if="props.model.length > props.maxSelectedLabels" class="custom-chip">
          <span >{{ `+${props.model.length - props.maxSelectedLabels}` }}</span>
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
  font-size: 14px;
  margin: 0px 2px;
  padding: 0.2145rem 0.571rem;
  background: rgba(68, 72, 109, 0.17);
  color: #44486D;
}

.chip-label {
  margin-right: 8px;
}

.remove-button {
  background-color: transparent;
  border: none;
  color: #5a5a7c; /* Color del botón similar al de la imagen */
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-button:hover {
  color: #2d2d3a; /* Color más oscuro al hacer hover */
}
</style>
