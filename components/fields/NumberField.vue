<!-- components/fields/NumberField.vue -->
<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import InputNumber from 'primevue/inputnumber'
import type { FormFieldProps } from '../../types/form'
import BaseField from './BaseField.vue'

// Props
interface NumberFieldProps extends FormFieldProps<number | null> {
  min?: number
  max?: number
  step?: number
  minFractionDigits?: number
  maxFractionDigits?: number
  showButtons?: boolean
  buttonLayout?: 'stacked' | 'horizontal'
  incrementButtonClass?: string
  decrementButtonClass?: string
  incrementButtonIcon?: string
  decrementButtonIcon?: string
  currency?: string
  currencyDisplay?: 'symbol' | 'code' | 'name'
  locale?: string
  localeMatcher?: 'lookup' | 'best fit'
  mode?: 'decimal' | 'currency'
  prefix?: string
  suffix?: string
  useGrouping?: boolean
  format?: boolean
}

const props = withDefaults(defineProps<NumberFieldProps>(), {
  step: 1,
  minFractionDigits: 0,
  maxFractionDigits: 2,
  showButtons: true,
  buttonLayout: 'stacked',
  incrementButtonIcon: 'pi pi-angle-up',
  decrementButtonIcon: 'pi pi-angle-down',
  currencyDisplay: 'symbol',
  localeMatcher: 'best fit',
  mode: 'decimal',
  useGrouping: true,
  format: true
})

// Emits
const emit = defineEmits<{
  'update:value': [value: number | null]
  'blur': []
  'focus': []
  'clear': []
  'input': [value: number | null]
}>()

// Template ref
const inputRef = ref<HTMLElement | null>(null)

// Computed properties
const inputValue = computed({
  get: () => props.value,
  set: (newValue: number | null) => {
    handleUpdate(newValue)
  }
})

const inputClasses = computed(() => {
  const classes = ['number-field__input']

  if (props.error && props.error.length > 0) {
    classes.push('p-invalid')
  }

  return classes
})

const inputMode = computed<'decimal' | 'currency'>(() => {
  switch (props.field.type) {
    case 'currency':
      return 'currency'
    case 'percentage':
      return 'decimal' // PrimeVue InputNumber doesn't support 'percent', use 'decimal'
    default:
      return props.mode || 'decimal'
  }
})

const computedCurrency = computed(() => {
  if (props.field.type === 'currency') {
    return props.currency || 'USD'
  }
  return props.currency
})

const computedPrefix = computed(() => {
  if (props.field.type === 'percentage' && !props.prefix && !props.suffix) {
    return undefined
  }
  return props.prefix || props.field.ui?.prefix
})

const computedSuffix = computed(() => {
  if (props.field.type === 'percentage' && !props.suffix && !props.prefix) {
    return '%'
  }
  return props.suffix || props.field.ui?.suffix
})

const minValue = computed(() =>
  props.min ?? props.field.min
)

const maxValue = computed(() =>
  props.max ?? props.field.max
)

const stepValue = computed(() =>
  props.step ?? props.field.step ?? 1
)

// Remove unused precisionValue - we'll use maxFractionDigits directly
// const precisionValue = computed(() =>
//   props.field.precision ?? props.maxFractionDigits
// )

const isDisabled = computed(() =>
  props.disabled || props.field.ui?.readonly
)

const placeholder = computed(() => {
  if (props.field.type === 'currency') {
    return props.field.ui?.placeholder || 'Enter amount'
  }
  if (props.field.type === 'percentage') {
    return props.field.ui?.placeholder || 'Enter percentage'
  }
  return props.field.ui?.placeholder || 'Enter number'
})

// Methods
function handleUpdate(value: number | null) {
  let processedValue = value

  // Apply field-specific processing
  if (props.field.type === 'percentage' && value !== null) {
    // Ensure percentage is between 0-100 unless min/max override this
    const min = minValue.value ?? 0
    const max = maxValue.value ?? 100
    processedValue = Math.min(Math.max(value, min), max)
  }

  emit('update:value', processedValue)
  emit('input', processedValue)
}

function handleBlur() {
  emit('blur')
}

function handleFocus() {
  emit('focus')
}

function handleClear() {
  emit('update:value', null)
  emit('clear')
}

function increment() {
  const currentValue = inputValue.value || 0
  const newValue = currentValue + stepValue.value
  const maxVal = maxValue.value

  if (maxVal === undefined || newValue <= maxVal) {
    handleUpdate(newValue)
  }
}

function decrement() {
  const currentValue = inputValue.value || 0
  const newValue = currentValue - stepValue.value
  const minVal = minValue.value

  if (minVal === undefined || newValue >= minVal) {
    handleUpdate(newValue)
  }
}

// Format value for display
function formatValue(value: number | null): string {
  if (value === null || value === undefined) { return '' }

  try {
    if (props.field.type === 'currency' && computedCurrency.value) {
      return new Intl.NumberFormat(props.locale, {
        style: 'currency',
        currency: computedCurrency.value,
        currencyDisplay: props.currencyDisplay,
        minimumFractionDigits: props.minFractionDigits,
        maximumFractionDigits: props.maxFractionDigits
      }).format(value)
    }

    if (props.field.type === 'percentage') {
      // Format as percentage manually since PrimeVue doesn't support 'percent' mode
      return `${new Intl.NumberFormat(props.locale, {
        minimumFractionDigits: props.minFractionDigits,
        maximumFractionDigits: props.maxFractionDigits,
        useGrouping: props.useGrouping
      }).format(value)}%`
    }

    return new Intl.NumberFormat(props.locale, {
      minimumFractionDigits: props.minFractionDigits,
      maximumFractionDigits: props.maxFractionDigits,
      useGrouping: props.useGrouping
    }).format(value)
  }
  catch (error) {
    console.warn('Number formatting error:', error)
    return value.toString()
  }
}

// Expose methods for parent components
async function focus() {
  await nextTick()
  const input = inputRef.value?.querySelector('input')
  input?.focus()
}

function blur() {
  const input = inputRef.value?.querySelector('input')
  input?.blur()
}

function select() {
  const input = inputRef.value?.querySelector('input')
  if (input instanceof HTMLInputElement) {
    input.select()
  }
}

defineExpose({
  focus,
  blur,
  select,
  increment,
  decrement,
  formatValue,
  inputRef
})
</script>

<template>
  <BaseField
    v-bind="$props"
    @update:value="handleUpdate"
    @blur="handleBlur"
    @focus="handleFocus"
    @clear="handleClear"
  >
    <template #input="{ fieldId, onBlur, onFocus }">
      <div ref="inputRef">
        <InputNumber
          :id="fieldId"
          v-model="inputValue"
          :class="inputClasses"
          :mode="inputMode"
          :currency="computedCurrency"
          :currency-display="currencyDisplay"
          :locale="locale"
          :locale-matcher="localeMatcher"
          :min="minValue"
          :max="maxValue"
          :step="stepValue"
          :min-fraction-digits="minFractionDigits"
          :max-fraction-digits="maxFractionDigits"
          :prefix="computedPrefix"
          :suffix="computedSuffix"
          :placeholder="placeholder"
          :disabled="isDisabled"
          :show-buttons="showButtons"
          :button-layout="buttonLayout"
          :increment-button-class="incrementButtonClass"
          :decrement-button-class="decrementButtonClass"
          :increment-button-icon="incrementButtonIcon"
          :decrement-button-icon="decrementButtonIcon"
          :use-grouping="useGrouping"
          :format="format"
          @blur="onBlur"
          @focus="onFocus"
        />
      </div>
    </template>

    <!-- Custom slots passthrough -->
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

<style scoped>
/* ===============================
   NUMBER FIELD SPECIFIC STYLES
   =============================== */

.number-field__input {
  width: 100%;
}

/* ===============================
   INPUT NUMBER OVERRIDES
   =============================== */

:deep(.p-inputnumber) {
  width: 100%;
  display: flex;
  align-items: stretch;
}

:deep(.p-inputnumber-input) {
  height: var(--field-height);
  padding: var(--field-padding);
  font-size: var(--field-font-size);
  border: var(--field-border-width) solid var(--field-border-color);
  border-radius: var(--field-border-radius);
  background: var(--field-background);
  color: rgb(17 24 39);
  transition: var(--field-transition);
  outline: none;
  font-family: inherit;
  text-align: right;
  font-variant-numeric: tabular-nums;
}

:deep(.p-inputnumber-input::placeholder) {
  color: rgb(156 163 175);
  text-align: left;
}

:deep(.p-inputnumber-input:hover:not(:disabled)) {
  border-color: var(--field-border-color-hover);
}

:deep(.p-inputnumber-input:focus) {
  border-color: var(--field-border-color-focus);
  box-shadow: 0 0 0 2px rgb(59 130 246 / 0.1);
}

:deep(.p-inputnumber-input:disabled) {
  background: rgb(249 250 251);
  color: rgb(156 163 175);
  cursor: not-allowed;
}

:deep(.p-inputnumber.p-invalid .p-inputnumber-input) {
  border-color: rgb(239 68 68);
  box-shadow: 0 0 0 2px rgb(239 68 68 / 0.1);
}

:deep(.p-inputnumber.p-invalid .p-inputnumber-input:focus) {
  border-color: rgb(239 68 68);
  box-shadow: 0 0 0 2px rgb(239 68 68 / 0.2);
}

/* ===============================
   BUTTON STYLES
   =============================== */

:deep(.p-inputnumber-buttons-stacked) {
  flex-direction: column;
}

:deep(.p-inputnumber-buttons-horizontal) {
  flex-direction: row;
}

:deep(.p-inputnumber-button) {
  background: rgb(249 250 251);
  border: var(--field-border-width) solid var(--field-border-color);
  color: rgb(107 114 128);
  width: var(--field-height);
  height: calc(var(--field-height) / 2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--field-transition);
}

:deep(.p-inputnumber-buttons-horizontal .p-inputnumber-button) {
  height: var(--field-height);
}

:deep(.p-inputnumber-button:hover:not(:disabled)) {
  background: rgb(243 244 246);
  color: rgb(55 65 81);
}

:deep(.p-inputnumber-button:active) {
  background: rgb(229 231 235);
}

:deep(.p-inputnumber-button:disabled) {
  opacity: 0.6;
  cursor: not-allowed;
}

:deep(.p-inputnumber-button-up) {
  border-radius: 0 var(--field-border-radius) 0 0;
  border-bottom: none;
}

:deep(.p-inputnumber-button-down) {
  border-radius: 0 0 var(--field-border-radius) 0;
}

:deep(.p-inputnumber-buttons-horizontal .p-inputnumber-button-up) {
  border-radius: 0 var(--field-border-radius) var(--field-border-radius) 0;
  border-bottom: var(--field-border-width) solid var(--field-border-color);
  border-left: none;
}

:deep(.p-inputnumber-buttons-horizontal .p-inputnumber-button-down) {
  border-radius: var(--field-border-radius) 0 0 var(--field-border-radius);
  border-right: none;
}

:deep(.p-inputnumber-buttons-stacked .p-inputnumber-input) {
  border-radius: var(--field-border-radius) 0 0 var(--field-border-radius);
  border-right: none;
}

:deep(.p-inputnumber-buttons-horizontal .p-inputnumber-input) {
  border-radius: 0;
  border-left: none;
  border-right: none;
}

/* ===============================
   CURRENCY SPECIFIC STYLES
   =============================== */

.number-field__input[data-mode="currency"] :deep(.p-inputnumber-input) {
  font-weight: 500;
}

.number-field__input[data-mode="currency"] :deep(.p-inputnumber-input::placeholder) {
  font-weight: normal;
}

/* ===============================
   PERCENTAGE SPECIFIC STYLES
   =============================== */

.number-field__input[data-mode="percent"] :deep(.p-inputnumber-input) {
  text-align: right;
}

/* ===============================
   NO BUTTONS VARIANT
   =============================== */

:deep(.p-inputnumber:not(.p-inputnumber-buttons-stacked):not(.p-inputnumber-buttons-horizontal) .p-inputnumber-input) {
  border-radius: var(--field-border-radius);
  border: var(--field-border-width) solid var(--field-border-color);
}

/* ===============================
   FOCUS STATES
   =============================== */

:deep(.p-inputnumber:focus-within .p-inputnumber-button) {
  border-color: var(--field-border-color-focus);
}

:deep(.p-inputnumber.p-invalid:focus-within .p-inputnumber-button) {
  border-color: rgb(239 68 68);
}

/* ===============================
   RESPONSIVE DESIGN
   =============================== */

@media (max-width: 640px) {
  :deep(.p-inputnumber-input) {
    font-size: 16px; /* Prevent zoom on iOS */
  }

  :deep(.p-inputnumber-button) {
    width: 3rem;
  }
}

/* ===============================
   ACCESSIBILITY ENHANCEMENTS
   =============================== */

:deep(.p-inputnumber-input:focus-visible) {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 2px;
}

:deep(.p-inputnumber-button:focus-visible) {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 1px;
}

@media (prefers-reduced-motion: reduce) {
  :deep(.p-inputnumber-input),
  :deep(.p-inputnumber-button) {
    transition: none;
  }
}

/* ===============================
   DARK MODE SUPPORT
   =============================== */

@media (prefers-color-scheme: dark) {
  :deep(.p-inputnumber-input) {
    background: rgb(31 41 55);
    color: rgb(243 244 246);
    border-color: rgb(75 85 99);
  }

  :deep(.p-inputnumber-input::placeholder) {
    color: rgb(107 114 128);
  }

  :deep(.p-inputnumber-input:disabled) {
    background: rgb(17 24 39);
    color: rgb(75 85 99);
  }

  :deep(.p-inputnumber-button) {
    background: rgb(31 41 55);
    border-color: rgb(75 85 99);
    color: rgb(156 163 175);
  }

  :deep(.p-inputnumber-button:hover:not(:disabled)) {
    background: rgb(55 65 81);
    color: rgb(209 213 219);
  }
}

/* ===============================
   HIGH CONTRAST MODE
   =============================== */

@media (prefers-contrast: high) {
  :deep(.p-inputnumber-input) {
    border-width: 2px;
  }

  :deep(.p-inputnumber-button) {
    border-width: 2px;
  }
}

/* ===============================
   PRINT STYLES
   =============================== */

@media print {
  :deep(.p-inputnumber-button) {
    display: none;
  }

  :deep(.p-inputnumber-input) {
    border-radius: var(--field-border-radius) !important;
    border: 1px solid rgb(0 0 0) !important;
  }
}
</style>
