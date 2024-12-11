<script setup lang="ts">
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
  },
  loading: {
    type: Boolean,
    default: false,
    required: false
  }
})

const emit = defineEmits(['load', 'update:modelValue', 'change'])

const localModelValue = ref(props.model)
const instance = ref()

// Watch for changes in props.model and update localModelValue accordingly
watch(() => props.model, (newValue) => {
  localModelValue.value = newValue
}, { immediate: true })

function loadItems() {
  emit('load')
}

function wait(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms))
}
</script>

<template>
  <Dropdown
    :id="props.id"
    ref="instance"
    v-model="localModelValue"
    :option-label="field"
    :options="props.suggestions"
    :placeholder="props.placeholder"
    :disabled="props.disabled || props.loading"
    :loading="props.loading"
    @before-show="loadItems"
    @change="async ($event) => {
      emit('change', $event.value)
    }"
  >
    <template #option="props">
      <slot name="option" :item="props.option" />
    </template>
  </Dropdown>
</template>

<style>
</style>
