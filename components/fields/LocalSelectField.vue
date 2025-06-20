<!-- LocalSelectField.vue - CLEAN VERSION SIN ESTILOS -->
<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import Dropdown from 'primevue/dropdown'

const props = defineProps<{
  field: {
    field: string
    dataType: string
    label?: string
    placeholder?: string
    required?: boolean
    options?: any[]
    ui?: {
      readonly?: boolean
      placeholder?: string
      className?: string | string[]
      style?: Record<string, any>
    }
    [key: string]: any
  }
  value?: any
  error?: any[]
  disabled?: boolean
  readonly?: boolean
  clearable?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'update:value': [value: any]
  'blur': []
  'focus': []
  'clear': []
  'change': [value: any]
}>()

const internalValue = ref(props.value)

watch(() => props.value, (newValue) => {
  internalValue.value = newValue
}, { immediate: true })

function normalizeOptions(options: any[]): Array<{ name: string, value: any }> {
  if (!options || !Array.isArray(options) || options.length === 0) {
    return []
  }

  if (options.every(opt => opt && typeof opt === 'object' && 'name' in opt && 'id' in opt)) {
    return options.map(opt => ({ name: opt.name, value: opt.id }))
  }

  if (options.every(opt => opt && typeof opt === 'object' && 'name' in opt && 'value' in opt)) {
    return options
  }

  if (options.every(opt => opt && typeof opt === 'object' && 'label' in opt && 'value' in opt)) {
    return options.map(opt => ({ name: opt.label, value: opt.value }))
  }

  if (options.every(opt => typeof opt === 'string')) {
    return options.map(opt => ({ name: opt, value: opt }))
  }

  return options.map((opt, _index) => ({ name: String(opt), value: opt }))
}

const fieldLabel = computed(() => {
  return props.field.label || props.field.field || 'Select Option'
})

const fieldPlaceholder = computed(() => {
  return props.field.ui?.placeholder || props.field.placeholder || `Select ${fieldLabel.value}`
})

const isRequired = computed(() => {
  return props.field.required || false
})

const hasError = computed(() => {
  return props.error && props.error.length > 0
})

const normalizedOptions = computed(() => {
  return normalizeOptions(props.field.options || [])
})

const isDisabled = computed(() =>
  props.disabled || props.field?.ui?.readonly || props.readonly || false
)

const isClearable = computed(() => {
  if (props.clearable !== undefined) {
    return props.clearable
  }
  return !isDisabled.value
})

const showClearButton = computed(() => {
  return isClearable.value
    && internalValue.value !== null
    && internalValue.value !== undefined
    && internalValue.value !== ''
    && !isDisabled.value
})

const dropdownValue = computed({
  get: () => internalValue.value,
  set: newValue => handleValueUpdate(newValue)
})

const dropdownClasses = computed(() => {
  const classes = ['w-full', 'local-select-dropdown']

  if (hasError.value) {
    classes.push('p-invalid')
  }

  if (props.field?.ui?.className) {
    const uiClasses = Array.isArray(props.field.ui.className)
      ? props.field.ui.className
      : [props.field.ui.className]
    classes.push(...uiClasses)
  }

  return classes
})

const fieldStyle = computed(() => props.field?.ui?.style || {})

function handleValueUpdate(value: any) {
  internalValue.value = value
  emit('update:modelValue', value)
  emit('update:value', value)
}

function handleBlur() {
  emit('blur')
}

function handleFocus() {
  emit('focus')
}

function handleClear() {
  const oldValue = internalValue.value
  internalValue.value = null

  if (oldValue !== null) {
    emit('update:modelValue', null)
    emit('update:value', null)
    emit('change', null)
  }

  emit('clear')
}

function handleChange(event: any) {
  const newValue = event?.value !== undefined ? event.value : event
  handleValueUpdate(newValue)
  emit('change', newValue)
}

function handleFilter(event: any) {
  console.log('Filter:', event.value)
}
</script>

<template>
  <div v-if="field.dataType === 'localselect'" class="local-select-field w-full">
    <!-- Label -->
    <label
      v-if="fieldLabel"
      :for="`local-select-${field.field}`"
      class="form-field__label"
      :class="{ 'form-field--required': isRequired }"
    >
      {{ fieldLabel }}
    </label>

    <!-- Input wrapper -->
    <div class="form-field__input-wrapper">
      <Dropdown
        :id="`local-select-${field.field}`"
        v-model="dropdownValue"
        :options="normalizedOptions"
        option-label="name"
        option-value="value"
        :placeholder="fieldPlaceholder"
        :class="dropdownClasses"
        :style="fieldStyle"
        :filter="normalizedOptions.length > 10"
        :show-clear="showClearButton"
        :disabled="isDisabled"
        :loading="false"
        filter-placeholder="Search..."
        @update:model-value="handleValueUpdate"
        @blur="handleBlur"
        @focus="handleFocus"
        @change="handleChange"
        @clear="handleClear"
        @filter="handleFilter"
      >
        <template #option="{ option }">
          <div class="dropdown-option">
            <span>{{ option.name }}</span>
          </div>
        </template>

        <template #value="{ value }">
          <div v-if="value !== null && value !== undefined" class="dropdown-selected">
            <span>{{ normalizedOptions.find(opt => opt.value === value)?.name || value }}</span>
          </div>
          <span v-else class="dropdown-placeholder">
            {{ fieldPlaceholder }}
          </span>
        </template>
      </Dropdown>
    </div>

    <!-- Error messages -->
    <div v-if="hasError" class="form-field__error-messages">
      <small
        v-for="(errorItem, index) in error"
        :key="index"
        class="form-field__error-message"
      >
        <i class="pi pi-exclamation-triangle" />
        {{ errorItem.message || errorItem }}
      </small>
    </div>
  </div>
</template>
