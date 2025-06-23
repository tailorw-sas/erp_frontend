<!-- LocalSelectField.vue - REFACTORED TO USE BaseField -->
<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import Dropdown from 'primevue/dropdown'
import type { FormFieldProps, ValidationError } from '../../types/form'
import BaseField from './BaseField.vue'

// Props que extienden FormFieldProps para ser compatible con el sistema
interface LocalSelectFieldProps extends Omit<FormFieldProps<any>, 'value'> {
  value?: any
  readonly?: boolean
  required?: boolean
  name?: string
  id?: string
  placeholder?: string
  helpText?: string
  label?: string
  description?: string
  size?: string
  variant?: string
  class?: string
  config?: any
  onUpdate?: (value: any) => void
  disabled?: boolean
  clearable?: boolean
}

const props = withDefaults(defineProps<LocalSelectFieldProps>(), {
  readonly: false,
  required: false,
  disabled: false,
  clearable: true
})

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'blur': []
  'focus': []
  'clear': []
  'change': [value: any]
}>()

const internalValue = ref(props.value)

// Watch del valor prop para sincronizar
watch(() => props.value, (newValue) => {
  internalValue.value = newValue
}, { immediate: true })

// Computed para el valor actual (compatible con BaseField)
const currentValue = computed({
  get: () => internalValue.value,
  set: (newValue: any) => {
    internalValue.value = newValue
    handleValueUpdate(newValue)
  }
})

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

const normalizedOptions = computed(() => {
  return normalizeOptions(props.field?.options || [])
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

const placeholder = computed(() => {
  if (props.field?.ui?.placeholder) {
    return props.field.ui.placeholder
  }

  if (props.field?.placeholder) {
    return props.field.placeholder
  }

  const label = props.field?.label || props.field?.field || 'Option'
  return `Select ${label}`
})

// Computed para clases del componente
const componentClasses = computed(() => {
  const classes = ['local-select-dropdown']

  if (props.error && props.error.length > 0) {
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

// Computed para el estilo del campo
const fieldStyle = computed(() => props.field?.ui?.style || {})

// Computed para el BaseField props
const baseFieldProps = computed(() => ({
  field: props.field,
  value: props.value ?? null,
  error: props.error,
  loading: props.loading,
  disabled: props.disabled,
  readonly: props.readonly,
  required: props.required,
  name: props.name,
  id: props.id,
  placeholder: props.placeholder,
  helpText: props.helpText,
  label: props.label,
  description: props.description,
  size: props.size,
  variant: props.variant,
  class: props.class,
  config: props.config,
  style: fieldStyle.value,
  onUpdate: props.onUpdate || handleValueUpdate
}))

// Event handlers compatibles con BaseField
function handleValueUpdate(value: any) {
  if (props.onUpdate) {
    props.onUpdate(value)
  }

  if (value !== props.value) {
    emit('update:modelValue', value)
  }

  emit('change', value)
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
    handleValueUpdate(null)
  }

  emit('clear')
}

function handleChange(event: any) {
  const newValue = event?.value !== undefined ? event.value : event
  currentValue.value = newValue
}

function handleFilter(event: any) {
  console.log('Filter:', event.value)
}

// Función para obtener el display label
function getDisplayLabel(value: any): string {
  if (value === null || value === undefined) {
    return ''
  }

  const option = normalizedOptions.value.find(opt => opt.value === value)
  return option?.name || String(value)
}

// Expose functions for BaseField compatibility
defineExpose({
  focus: () => {}, // TODO: Implementar focus en el dropdown
  blur: () => {}, // TODO: Implementar blur en el dropdown
  clear: handleClear,
  getDisplayLabel
})
</script>

<template>
  <!-- ✅ USANDO BaseField COMO DateField Y SelectField -->
  <BaseField
    v-if="field?.dataType === 'localselect'"
    v-bind="baseFieldProps"
  >
    <template #input="{ fieldId, onUpdate, onBlur, onFocus }">
      <Dropdown
        :id="fieldId"
        v-model="currentValue"
        :options="normalizedOptions"
        option-label="name"
        option-value="value"
        :placeholder="placeholder"
        :class="componentClasses"
        :style="fieldStyle"
        :filter="normalizedOptions.length > 10"
        :show-clear="showClearButton"
        :disabled="isDisabled"
        :loading="props.loading || false"
        filter-placeholder="Search..."
        @update:model-value="onUpdate"
        @blur="onBlur"
        @focus="onFocus"
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
            <span>{{ getDisplayLabel(value) }}</span>
          </div>
          <span v-else class="dropdown-placeholder">
            {{ placeholder }}
          </span>
        </template>

        <template #empty>
          <div class="p-dropdown-empty-message">
            <span>No options available.</span>
          </div>
        </template>

        <template #loader>
          <div class="p-dropdown-loader">
            <i class="pi pi-spin pi-spinner" />
          </div>
        </template>
      </Dropdown>
    </template>

    <!-- ✅ SLOTS PASADOS A BaseField -->
    <template #label="slotProps">
      <slot name="label" v-bind="slotProps" />
    </template>

    <template #prefix="slotProps">
      <slot name="prefix" v-bind="slotProps" />
    </template>

    <template #suffix="slotProps">
      <slot name="suffix" v-bind="slotProps" />
    </template>

    <template #icon="slotProps">
      <slot name="icon" v-bind="slotProps" />
    </template>

    <template #help="slotProps">
      <slot name="help" v-bind="slotProps" />
    </template>

    <template #error="slotProps">
      <slot name="error" v-bind="slotProps" />
    </template>

    <template #loading>
      <slot name="loading" />
    </template>

    <template #clear-icon>
      <slot name="clear-icon" />
    </template>
  </BaseField>
</template>
