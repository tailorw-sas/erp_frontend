<!-- components/fields/LocalSelectField.vue -->
<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import Dropdown from 'primevue/dropdown'

// Simple, flexible interfaces to avoid conflicts
interface SelectOption {
  name: string
  value: any
  id?: string | number
  description?: string
}

interface FieldConfig {
  name: string
  label?: string
  type: string
  placeholder?: string
  showClear?: boolean
  filterable?: boolean
  required?: boolean
  helpText?: string
  options?: any[] // Flexible to handle any data structure
  [key: string]: any // Allow any additional properties
}

interface ValidationError {
  message: string
  path?: string
}

// Props interface
interface LocalSelectFieldProps {
  field: FieldConfig
  value?: any
  error?: ValidationError[]
  touched?: boolean
  dirty?: boolean
  disabled?: boolean
  loading?: boolean
  config?: any
}

const props = withDefaults(defineProps<LocalSelectFieldProps>(), {
  disabled: false,
  loading: false,
  touched: false,
  dirty: false
})

// Emits
const emit = defineEmits<{
  'update:value': [value: any]
  'blur': []
  'focus': []
  'clear': []
}>()

// Internal state
const focused = ref(false)

// Computed properties
const fieldId = computed(() => `local-select-${props.field.name}`)

const fieldClasses = computed(() => {
  const classes = ['local-select-field']

  if (props.error && props.error.length > 0) {
    classes.push('local-select-field--error')
  }

  if (props.touched) {
    classes.push('local-select-field--touched')
  }

  if (props.dirty) {
    classes.push('local-select-field--dirty')
  }

  if (props.disabled) {
    classes.push('local-select-field--disabled')
  }

  if (focused.value) {
    classes.push('local-select-field--focused')
  }

  return classes
})

const dropdownClasses = computed(() => {
  const classes = ['w-full']

  if (props.error && props.error.length > 0) {
    classes.push('p-invalid')
  }

  return classes
})

const options = computed((): SelectOption[] => {
  // Get options from field configuration - flexible approach
  const fieldOptions = props.field.options || []

  if (fieldOptions.length === 0) {
    return []
  }

  // If first option is a string, treat all as strings
  if (typeof fieldOptions[0] === 'string') {
    return fieldOptions.map((option: string): SelectOption => ({
      name: option,
      value: option,
      id: option
    }))
  }

  // If first option is an object, normalize the structure
  if (typeof fieldOptions[0] === 'object' && fieldOptions[0] !== null) {
    return fieldOptions.map((option: any, index: number): SelectOption => {
      // Handle different object structures flexibly
      const name = option.name || option.label || option.text || option.value || `Option ${index + 1}`
      const value = option.value !== undefined ? option.value : (option.id !== undefined ? option.id : option.name || option)
      const id = option.id !== undefined ? option.id : (option.value !== undefined ? option.value : index.toString())

      return {
        name: String(name),
        value,
        id: String(id),
        description: option.description
      }
    })
  }

  // Fallback for any other data type
  return fieldOptions.map((option: any, index: number): SelectOption => ({
    name: String(option),
    value: option,
    id: String(index)
  }))
})

const placeholder = computed(() => {
  return props.field.placeholder || `Select ${props.field.label || props.field.name}`
})

const showClear = computed(() => {
  return props.field.showClear !== false && !props.disabled && !props.loading
})

const filterable = computed(() => {
  return props.field.filterable !== false && options.value.length > 5
})

// Methods
function handleChange(selectedValue: any) {
  emit('update:value', selectedValue)
}

function handleFocus() {
  focused.value = true
  emit('focus')
}

function handleBlur() {
  focused.value = false
  emit('blur')
}

function handleClear() {
  emit('update:value', null)
  emit('clear')
}

// Error message computation
const errorMessage = computed(() => {
  if (!props.error || props.error.length === 0) { return '' }
  return props.error[0]?.message || 'Invalid value'
})

// Helper method to get selected option name
function getSelectedOptionName(selectedValue: any): string {
  const option = options.value.find((opt: SelectOption) =>
    opt.value === selectedValue || opt.id === selectedValue
  )
  return option?.name || String(selectedValue || '')
}

// Computed property for development check
const isDevelopment = computed(() => {
  return typeof process !== 'undefined' && process.env?.NODE_ENV === 'development'
})

// Watch for field changes to handle dynamic options updates
watch(() => props.field.options, () => {
  // If current value is not in new options, clear it
  if (props.value && options.value.length > 0) {
    const hasValue = options.value.some((option: SelectOption) =>
      option.value === props.value || option.id === props.value
    )
    if (!hasValue) {
      handleClear()
    }
  }
}, { deep: true })
</script>

<template>
  <div :class="fieldClasses">
    <!-- Field Label -->
    <label
      v-if="field.label"
      :for="fieldId"
      class="local-select-field__label"
      :class="{
        'local-select-field__label--required': field.required,
        'local-select-field__label--error': error && error.length > 0,
      }"
    >
      {{ field.label }}
      <span v-if="field.required" class="local-select-field__required">*</span>
    </label>

    <!-- Dropdown Component -->
    <div class="local-select-field__input-container">
      <Dropdown
        :id="fieldId"
        :model-value="value"
        :options="options"
        option-label="name"
        option-value="value"
        :placeholder="placeholder"
        :class="dropdownClasses"
        :disabled="disabled || loading"
        :filter="filterable"
        :filter-placeholder="filterable ? 'Search options...' : undefined"
        :show-clear="showClear"
        :loading="loading"
        :aria-describedby="error && error.length > 0 ? `${fieldId}-error` : undefined"
        @update:model-value="handleChange"
        @focus="handleFocus"
        @blur="handleBlur"
        @clear="handleClear"
      >
        <!-- Custom option template if needed -->
        <template #option="{ option }">
          <div class="local-select-field__option">
            <span class="local-select-field__option-name">{{ option.name }}</span>
            <small
              v-if="option.description"
              class="local-select-field__option-description"
            >
              {{ option.description }}
            </small>
          </div>
        </template>

        <!-- Custom value template -->
        <template #value="{ value: selectedValue, placeholder: dropdownPlaceholder }">
          <div v-if="selectedValue" class="local-select-field__selected-value">
            {{ getSelectedOptionName(selectedValue) }}
          </div>
          <span v-else class="local-select-field__placeholder">
            {{ dropdownPlaceholder }}
          </span>
        </template>
      </Dropdown>

      <!-- Loading indicator overlay -->
      <div
        v-if="loading"
        class="local-select-field__loading-overlay"
      >
        <i class="pi pi-spin pi-spinner text-primary" />
      </div>
    </div>

    <!-- Help Text -->
    <small
      v-if="field.helpText && (!error || error.length === 0)"
      class="local-select-field__help-text"
    >
      {{ field.helpText }}
    </small>

    <!-- Error Message -->
    <small
      v-if="error && error.length > 0"
      :id="`${fieldId}-error`"
      class="local-select-field__error-message"
      role="alert"
    >
      {{ errorMessage }}
    </small>

    <!-- Debug info (development only) -->
    <details
      v-if="config?.showDebugInfo && isDevelopment"
      class="local-select-field__debug mt-2"
    >
      <summary class="text-xs text-gray-500 cursor-pointer">
        Debug Info
      </summary>
      <pre class="text-xs mt-1 p-2 bg-gray-100 rounded">{{ {
        fieldName: field.name,
        value,
        optionsCount: options.length,
        hasError: !!(error && error.length > 0),
        touched,
        dirty,
        disabled,
        loading,
      } }}</pre>
    </details>
  </div>
</template>

<style scoped>
.local-select-field {
  --field-spacing: 0.25rem;
  --field-border-radius: 6px;
  --field-transition: all 0.2s ease-in-out;

  position: relative;
  width: 100%;
}

/* Label Styles */
.local-select-field__label {
  display: block;
  font-size: 0.75rem;
  font-weight: 600;
  color: #374151;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: var(--field-spacing);
  line-height: 1.2;
  transition: var(--field-transition);
}

.local-select-field__label--required {
  color: #374151;
}

.local-select-field__label--error {
  color: #dc2626;
}

.local-select-field__required {
  color: #dc2626;
  margin-left: 0.125rem;
}

/* Input Container */
.local-select-field__input-container {
  position: relative;
  width: 100%;
}

/* Dropdown Override Styles */
.local-select-field :deep(.p-dropdown) {
  height: 2.5rem;
  min-height: 2.5rem;
  border-radius: var(--field-border-radius);
  transition: var(--field-transition);
}

.local-select-field :deep(.p-dropdown .p-dropdown-label) {
  height: 2.5rem;
  min-height: 2.5rem;
  padding: 0 0.75rem;
  line-height: 2.5rem;
  display: flex;
  align-items: center;
  font-size: 0.875rem;
}

.local-select-field :deep(.p-dropdown .p-dropdown-trigger) {
  height: 2.5rem;
  width: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Focus States */
.local-select-field--focused :deep(.p-dropdown) {
  border-color: #6366f1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.1);
}

/* Error States */
.local-select-field--error :deep(.p-dropdown) {
  border-color: #dc2626;
}

.local-select-field--error :deep(.p-dropdown:focus) {
  border-color: #dc2626;
  box-shadow: 0 0 0 2px rgba(220, 38, 38, 0.1);
}

/* Disabled States */
.local-select-field--disabled :deep(.p-dropdown) {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Option Styles */
.local-select-field__option {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
  padding: 0.25rem 0;
}

.local-select-field__option-name {
  font-weight: 500;
  color: #374151;
}

.local-select-field__option-description {
  color: #6b7280;
  font-size: 0.75rem;
  line-height: 1.2;
}

/* Selected Value */
.local-select-field__selected-value {
  color: #374151;
  font-weight: 500;
}

.local-select-field__placeholder {
  color: #9ca3af;
}

/* Loading Overlay */
.local-select-field__loading-overlay {
  position: absolute;
  top: 0;
  right: 0;
  height: 2.5rem;
  width: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 0 var(--field-border-radius) var(--field-border-radius) 0;
  pointer-events: none;
}

/* Help Text */
.local-select-field__help-text {
  display: block;
  font-size: 0.6875rem;
  color: #6b7280;
  line-height: 1.3;
  margin-top: var(--field-spacing);
}

/* Error Message */
.local-select-field__error-message {
  display: block;
  font-size: 0.6875rem;
  color: #dc2626;
  font-weight: 500;
  line-height: 1.3;
  margin-top: var(--field-spacing);
}

/* Debug Styles */
.local-select-field__debug {
  margin-top: 0.5rem;
  font-size: 0.625rem;
}

.local-select-field__debug summary {
  color: #6b7280;
  cursor: pointer;
  user-select: none;
}

.local-select-field__debug pre {
  background: #f3f4f6;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #e5e7eb;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
}

/* Responsive Design */
@media (max-width: 768px) {
  .local-select-field :deep(.p-dropdown),
  .local-select-field :deep(.p-dropdown .p-dropdown-label) {
    height: 2.75rem;
    min-height: 2.75rem;
  }

  .local-select-field :deep(.p-dropdown .p-dropdown-label) {
    line-height: 2.75rem;
  }

  .local-select-field :deep(.p-dropdown .p-dropdown-trigger) {
    height: 2.75rem;
  }

  .local-select-field__loading-overlay {
    height: 2.75rem;
  }
}

/* High Contrast Mode */
@media (prefers-contrast: high) {
  .local-select-field__label {
    font-weight: 700;
  }

  .local-select-field :deep(.p-dropdown) {
    border-width: 2px;
  }
}

/* Reduced Motion */
@media (prefers-reduced-motion: reduce) {
  .local-select-field,
  .local-select-field :deep(.p-dropdown) {
    transition: none;
  }
}

/* Dark Mode Support */
@media (prefers-color-scheme: dark) {
  .local-select-field__label {
    color: #d1d5db;
  }

  .local-select-field__label--error {
    color: #f87171;
  }

  .local-select-field__help-text {
    color: #9ca3af;
  }

  .local-select-field__error-message {
    color: #f87171;
  }

  .local-select-field__option-name {
    color: #f3f4f6;
  }

  .local-select-field__option-description {
    color: #9ca3af;
  }

  .local-select-field__debug pre {
    background: #374151;
    border-color: #4b5563;
    color: #d1d5db;
  }
}
</style>
