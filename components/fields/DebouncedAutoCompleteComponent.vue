<script setup lang="ts">
import { useDebounceFn } from '@vueuse/core'
import type { AutoCompleteChangeEvent, AutoCompleteItemSelectEvent } from 'primevue/autocomplete'

const props = defineProps({
  suggestions: {
    type: Array,
    required: true
  },
  model: {
    type: Object,
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
    default: 'autocomplete'
  },
  debounceTimeMs: {
    type: Number,
    default: 300
  },
  disabled: {
    type: Boolean,
    default: false
  },
  multiple: {
    type: Boolean,
    default: false,
    required: false
  }
})

const emit = defineEmits(['load', 'update:modelValue', 'change'])

const localModelValue = ref(props.model)
const instance = ref()
const shouldReopen = ref(true)

// Watch for changes in props.model and update localModelValue accordingly
watch(() => props.model, (newValue) => {
  localModelValue.value = newValue
}, { immediate: true })

const debouncedComplete = useDebounceFn((event: any) => {
  emit('load', event.query)
}, props.debounceTimeMs, { maxWait: 5000 })

const openDropdown = () => instance.value?.show()

function toggleSelection(item: any) {
  const index = localModelValue.value.findIndex((selectedItem: any) => selectedItem[props.itemValue] === item[props.itemValue])
  if (index !== -1) {
    localModelValue.value.splice(index, 1) // Remover el elemento si ya está seleccionado
  }
}

function onItemSelect(event: AutoCompleteItemSelectEvent) {
  if (props.multiple) {
    toggleSelection(event.value)
    shouldReopen.value = true
  }
}

function onHide() {
  if (shouldReopen.value && props.multiple) {
    openDropdown()
    shouldReopen.value = false
  }
}

function wait(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms))
}
</script>

<template>
  <AutoComplete
    :id="props.id"
    ref="instance"
    v-model="localModelValue"
    :suggestions="props.suggestions"
    :field="props.field"
    :item-value="props.itemValue"
    :placeholder="props.placeholder"
    :disabled="props.disabled"
    force-selection
    dropdown
    :multiple="props.multiple"
    @complete="debouncedComplete"
    @item-select="onItemSelect"
    @change="async ($event: AutoCompleteChangeEvent) => {
      const ev = JSON.parse(JSON.stringify($event.value));
      await wait(50) // Espera 50ms
      if (typeof ev === 'object') {
        emit('change', ev)
      }
    }"
    @hide="onHide"
  >
    <template #option="props">
      <slot name="option" :item="props.option" />
    </template>
    <template #chip="props">
      <slot name="chip" :value="props.value">
        <div
          :style="{
            color: (Object.prototype.hasOwnProperty.call(props.value, 'status') && props.value.status === 'INACTIVE') ? 'red' : '',
          }"
        >
          {{ props.value[field] }}
        </div>
      </slot>
    </template>
  </AutoComplete>
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
