<script setup lang="ts">
import { useDebounceFn } from '@vueuse/core'

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

// Watch for changes in props.model and update localModelValue accordingly
watch(() => props.model, (newValue) => {
  localModelValue.value = newValue
})

const debouncedComplete = useDebounceFn((event: any) => {
  emit('load', event.query)
}, props.debounceTimeMs, { maxWait: 5000 })
</script>

<template>
  <AutoComplete
    :id="props.id"
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
    @change="($event) => {
      if (typeof $event.value === 'object') {
        emit('change', $event.value)
      }
    }"
  >
    <template #option="props">
      <slot name="option" :item="props.option" />
    </template>
    <template #chip="props">
      <div
        :style="{
          color: (Object.prototype.hasOwnProperty.call(props.value, 'status') && props.value.status === 'INACTIVE') ? 'red' : '',
        }"
      >
        {{ props.value[field] }}
      </div>
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
</style>
