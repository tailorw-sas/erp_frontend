<!-- components/fields/CheckboxField.vue -->
<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import { Checkbox } from 'primevue/checkbox'
import type { FormFieldProps } from '../../types/form'

// Props
interface CheckboxFieldProps extends FormFieldProps<boolean> {
  'binary'?: boolean
  'trueValue'?: any
  'falseValue'?: any
  'indeterminate'?: boolean
  'variant'?: 'default' | 'filled' | 'outlined'
  'size'?: 'small' | 'medium' | 'large'
  'data-testid'?: string
}

const props = withDefaults(defineProps<CheckboxFieldProps>(), {
  binary: true,
  trueValue: true,
  falseValue: false,
  indeterminate: false,
  variant: 'default',
  size: 'medium'
})

// Emits
const emit = defineEmits<{
  'update:value': [value: boolean | any]
  'blur': []
  'focus': []
  'clear': []
  'change': [value: boolean | any]
}>()

// Template ref
const checkboxRef = ref<InstanceType<typeof Checkbox> | null>(null)

// Computed properties
const fieldId = computed(() => `field-${props.field.name}`)

const checkboxValue = computed({
  get: () => {
    if (props.binary) {
      return Boolean(props.value)
    }
    return props.value === props.trueValue
  },
  set: (newValue: boolean) => {
    handleUpdate(newValue)
  }
})

const checkboxClasses = computed(() => {
  const classes = ['checkbox-field__input']

  // Size classes
  classes.push(`checkbox-field__input--${props.size}`)

  // Variant classes
  classes.push(`checkbox-field__input--${props.variant}`)

  // State classes
  if (props.error && props.error.length > 0) {
    classes.push('p-invalid')
  }

  if (props.indeterminate) {
    classes.push('checkbox-field__input--indeterminate')
  }

  return classes
})

const labelClasses = computed(() => {
  const classes = ['checkbox-field__label']

  // Size classes
  classes.push(`checkbox-field__label--${props.size}`)

  // State classes
  if (props.disabled || props.field.ui?.readonly) {
    classes.push('checkbox-field__label--disabled')
  }

  if (checkboxValue.value) {
    classes.push('checkbox-field__label--checked')
  }

  return classes
})

const containerClasses = computed(() => {
  const classes = ['checkbox-field']

  // Size classes
  classes.push(`checkbox-field--${props.size}`)

  // State classes
  if (props.error && props.error.length > 0) {
    classes.push('checkbox-field--error')
  }

  if (props.touched) {
    classes.push('checkbox-field--touched')
  }

  if (props.dirty) {
    classes.push('checkbox-field--dirty')
  }

  if (props.disabled || props.field.ui?.readonly) {
    classes.push('checkbox-field--disabled')
  }

  if (props.loading) {
    classes.push('checkbox-field--loading')
  }

  return classes
})

const isDisabled = computed(() =>
  props.disabled || props.field.ui?.readonly
)

const labelText = computed(() =>
  props.field.label || props.field.name
)

const isRequired = computed(() => {
  // Check if field has required validation
  return props.field.validation?.toString().includes('required') || false
})

const hasError = computed(() =>
  props.error && props.error.length > 0
)

// Methods
function handleUpdate(checked: boolean) {
  let newValue: boolean | any

  if (props.binary) {
    newValue = checked
  }
  else {
    newValue = checked ? props.trueValue : props.falseValue
  }

  emit('update:value', newValue)
  emit('change', newValue)
}

function handleBlur() {
  emit('blur')
}

function handleFocus() {
  emit('focus')
}

function handleClear() {
  const clearedValue = props.binary ? false : props.falseValue
  emit('update:value', clearedValue)
  emit('clear')
}

function handleLabelClick() {
  if (!isDisabled.value) {
    checkboxValue.value = !checkboxValue.value
  }
}

// Expose methods for parent components
async function focus() {
  await nextTick()
  checkboxRef.value?.$el?.focus()
}

function blur() {
  checkboxRef.value?.$el?.blur()
}

function toggle() {
  if (!isDisabled.value) {
    handleUpdate(!checkboxValue.value)
  }
}

defineExpose({
  focus,
  blur,
  toggle,
  checkboxRef
})
</script>

<template>
  <div
    :class="containerClasses"
    :data-testid="$props['data-testid']"
  >
    <!-- Checkbox Container -->
    <div class="checkbox-field__container">
      <!-- Checkbox Input -->
      <Checkbox
        :id="fieldId"
        ref="checkboxRef"
        v-model="checkboxValue"
        :class="checkboxClasses"
        :binary="binary"
        :true-value="trueValue"
        :false-value="falseValue"
        :disabled="isDisabled"
        :indeterminate="indeterminate"
        :aria-invalid="hasError ? 'true' : 'false'"
        :aria-describedby="hasError
          ? `${field.name}-error`
          : field.ui?.helpText
            ? `${field.name}-help`
            : undefined"
        :aria-required="isRequired ? 'true' : 'false'"
        @change="handleUpdate"
        @blur="handleBlur"
        @focus="handleFocus"
      />

      <!-- Label and Content -->
      <div class="checkbox-field__content">
        <!-- Label -->
        <label
          :for="fieldId"
          :class="labelClasses"
          @click="handleLabelClick"
        >
          <slot name="label" :field="field" :checked="checkboxValue">
            {{ labelText }}
            <span
              v-if="isRequired"
              class="checkbox-field__required-indicator"
            >
              *
            </span>
          </slot>
        </label>

        <!-- Help Text -->
        <div
          v-if="field.ui?.helpText && !hasError"
          :id="`${field.name}-help`"
          class="checkbox-field__help-text"
        >
          <slot name="help" :field="field" :help-text="field.ui.helpText">
            {{ field.ui.helpText }}
          </slot>
        </div>

        <!-- Error Messages -->
        <div
          v-if="hasError"
          :id="`${field.name}-error`"
          class="checkbox-field__error-container"
          role="alert"
          :aria-live="touched ? 'polite' : 'off'"
        >
          <div
            v-for="(errorItem, index) in error"
            :key="`${errorItem.code}-${index}`"
            class="checkbox-field__error-message"
          >
            <slot
              name="error"
              :error="errorItem"
              :field="field"
            >
              <i class="pi pi-exclamation-triangle" />
              {{ errorItem.message }}
            </slot>
          </div>
        </div>
      </div>

      <!-- Clear button (optional) -->
      <button
        v-if="field.ui?.clearable && checkboxValue && !isDisabled"
        type="button"
        class="checkbox-field__clear-button"
        :aria-label="`Clear ${labelText}`"
        @click="handleClear"
      >
        <slot name="clear-icon">
          <i class="pi pi-times" />
        </slot>
      </button>

      <!-- Loading indicator -->
      <div
        v-if="loading"
        class="checkbox-field__loading-indicator"
      >
        <slot name="loading">
          <i class="pi pi-spinner pi-spin" />
        </slot>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ===============================
   CHECKBOX FIELD STYLES
   =============================== */

.checkbox-field {
  --checkbox-size-small: 1rem;
  --checkbox-size-medium: 1.25rem;
  --checkbox-size-large: 1.5rem;

  --checkbox-spacing-xs: 0.25rem;
  --checkbox-spacing-sm: 0.5rem;
  --checkbox-spacing-md: 0.75rem;

  position: relative;
  width: 100%;
  margin-bottom: var(--field-spacing-lg, 1rem);
}

.checkbox-field__container {
  display: flex;
  align-items: flex-start;
  gap: var(--checkbox-spacing-sm);
  position: relative;
}

.checkbox-field__content {
  flex: 1;
  min-width: 0;
}

/* ===============================
   SIZE VARIANTS
   =============================== */

.checkbox-field--small {
  --checkbox-size: var(--checkbox-size-small);
  --checkbox-spacing: var(--checkbox-spacing-xs);
}

.checkbox-field--medium {
  --checkbox-size: var(--checkbox-size-medium);
  --checkbox-spacing: var(--checkbox-spacing-sm);
}

.checkbox-field--large {
  --checkbox-size: var(--checkbox-size-large);
  --checkbox-spacing: var(--checkbox-spacing-md);
}

/* ===============================
   CHECKBOX INPUT STYLES
   =============================== */

.checkbox-field__input {
  flex-shrink: 0;
  margin-top: 0.125rem; /* Align with text baseline */
  width: var(--checkbox-size);
  height: var(--checkbox-size);
}

.checkbox-field__input--small {
  width: var(--checkbox-size-small);
  height: var(--checkbox-size-small);
}

.checkbox-field__input--medium {
  width: var(--checkbox-size-medium);
  height: var(--checkbox-size-medium);
}

.checkbox-field__input--large {
  width: var(--checkbox-size-large);
  height: var(--checkbox-size-large);
}

/* ===============================
   CHECKBOX VARIANTS
   =============================== */

.checkbox-field__input--default :deep(.p-checkbox-box) {
  border: 2px solid rgb(209 213 219);
  background: white;
  border-radius: 4px;
}

.checkbox-field__input--filled :deep(.p-checkbox-box) {
  border: 2px solid rgb(209 213 219);
  background: rgb(249 250 251);
  border-radius: 4px;
}

.checkbox-field__input--outlined :deep(.p-checkbox-box) {
  border: 2px solid rgb(59 130 246);
  background: white;
  border-radius: 4px;
}

/* ===============================
   LABEL STYLES
   =============================== */

.checkbox-field__label {
  display: block;
  font-size: var(--field-font-size, 0.875rem);
  font-weight: 500;
  color: rgb(55 65 81);
  line-height: 1.5;
  cursor: pointer;
  user-select: none;
  transition: color 0.2s ease;
}

.checkbox-field__label--small {
  font-size: 0.75rem;
}

.checkbox-field__label--medium {
  font-size: 0.875rem;
}

.checkbox-field__label--large {
  font-size: 1rem;
}

.checkbox-field__label--disabled {
  color: rgb(156 163 175);
  cursor: not-allowed;
}

.checkbox-field__label--checked {
  color: rgb(59 130 246);
}

.checkbox-field__label:hover:not(.checkbox-field__label--disabled) {
  color: rgb(37 99 235);
}

.checkbox-field__required-indicator {
  color: rgb(239 68 68);
  margin-left: 0.25rem;
  font-weight: 600;
}

/* ===============================
   HELP TEXT STYLES
   =============================== */

.checkbox-field__help-text {
  margin-top: var(--checkbox-spacing-xs);
  font-size: 0.75rem;
  color: rgb(107 114 128);
  line-height: 1.4;
}

/* ===============================
   ERROR STYLES
   =============================== */

.checkbox-field__error-container {
  margin-top: var(--checkbox-spacing-xs);
}

.checkbox-field__error-message {
  display: flex;
  align-items: flex-start;
  gap: var(--checkbox-spacing-xs);
  font-size: 0.75rem;
  color: rgb(239 68 68);
  line-height: 1.4;
  margin-bottom: var(--checkbox-spacing-xs);
}

.checkbox-field__error-message:last-child {
  margin-bottom: 0;
}

.checkbox-field__error-message i {
  flex-shrink: 0;
  margin-top: 0.125rem;
}

/* ===============================
   INTERACTIVE ELEMENTS
   =============================== */

.checkbox-field__clear-button {
  position: absolute;
  top: 0;
  right: 0;
  width: 1.25rem;
  height: 1.25rem;
  border: none;
  background: none;
  color: rgb(107 114 128);
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  opacity: 0.7;
}

.checkbox-field__clear-button:hover {
  background: rgb(243 244 246);
  color: rgb(55 65 81);
  opacity: 1;
}

.checkbox-field__clear-button:focus {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 1px;
}

.checkbox-field__loading-indicator {
  position: absolute;
  top: 0;
  right: 0;
  color: rgb(59 130 246);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.25rem;
  height: 1.25rem;
}

/* ===============================
   PRIMEVUE CHECKBOX OVERRIDES
   =============================== */

:deep(.p-checkbox) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

:deep(.p-checkbox-box) {
  width: 100%;
  height: 100%;
  border: 2px solid rgb(209 213 219);
  background: white;
  border-radius: 4px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

:deep(.p-checkbox-box:hover) {
  border-color: rgb(59 130 246);
  background: rgb(239 246 255);
}

:deep(.p-checkbox-box.p-highlight) {
  border-color: rgb(59 130 246);
  background: rgb(59 130 246);
}

:deep(.p-checkbox-box.p-highlight:hover) {
  border-color: rgb(37 99 235);
  background: rgb(37 99 235);
}

:deep(.p-checkbox-icon) {
  font-size: 0.75rem;
  color: white;
  transition: all 0.2s ease;
}

:deep(.p-checkbox-box.p-focus) {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 2px;
}

:deep(.p-checkbox-box.p-disabled) {
  opacity: 0.6;
  cursor: not-allowed;
  background: rgb(249 250 251);
}

:deep(.p-checkbox-box.p-disabled:hover) {
  border-color: rgb(209 213 219);
  background: rgb(249 250 251);
}

/* ===============================
   INDETERMINATE STATE
   =============================== */

.checkbox-field__input--indeterminate :deep(.p-checkbox-box) {
  background: rgb(59 130 246);
  border-color: rgb(59 130 246);
}

.checkbox-field__input--indeterminate :deep(.p-checkbox-icon) {
  color: white;
}

.checkbox-field__input--indeterminate :deep(.p-checkbox-icon::before) {
  content: '\e91a'; /* PrimeIcons minus */
}

/* ===============================
   ERROR STATE
   =============================== */

.checkbox-field--error .checkbox-field__input :deep(.p-checkbox-box) {
  border-color: rgb(239 68 68);
}

.checkbox-field--error .checkbox-field__input :deep(.p-checkbox-box:hover) {
  border-color: rgb(220 38 38);
  background: rgb(254 242 242);
}

.checkbox-field--error .checkbox-field__input :deep(.p-checkbox-box.p-highlight) {
  border-color: rgb(239 68 68);
  background: rgb(239 68 68);
}

.checkbox-field--error .checkbox-field__input :deep(.p-checkbox-box.p-focus) {
  outline-color: rgb(239 68 68);
}

/* ===============================
   LOADING STATE
   =============================== */

.checkbox-field--loading {
  position: relative;
}

.checkbox-field--loading .checkbox-field__input {
  opacity: 0.7;
  pointer-events: none;
}

/* ===============================
   DISABLED STATE
   =============================== */

.checkbox-field--disabled {
  opacity: 0.6;
  pointer-events: none;
}

/* ===============================
   TOUCHED/DIRTY STATES
   =============================== */

.checkbox-field--touched {
  /* Optional: Add visual indication for touched state */
}

.checkbox-field--dirty {
  /* Optional: Add visual indication for dirty state */
}

/* ===============================
   SIZE SPECIFIC OVERRIDES
   =============================== */

.checkbox-field--small :deep(.p-checkbox-icon) {
  font-size: 0.625rem;
}

.checkbox-field--large :deep(.p-checkbox-icon) {
  font-size: 0.875rem;
}

/* ===============================
   RESPONSIVE DESIGN
   =============================== */

@media (max-width: 640px) {
  .checkbox-field__container {
    gap: var(--checkbox-spacing-md);
  }

  .checkbox-field__input {
    margin-top: 0.25rem;
  }

  .checkbox-field--small {
    --checkbox-size: var(--checkbox-size-medium);
  }

  .checkbox-field--medium {
    --checkbox-size: var(--checkbox-size-large);
  }
}

/* ===============================
   ACCESSIBILITY ENHANCEMENTS
   =============================== */

:deep(.p-checkbox-box:focus-visible) {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 2px;
}

.checkbox-field__label:focus-visible {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 2px;
  border-radius: 2px;
}

@media (prefers-reduced-motion: reduce) {
  :deep(.p-checkbox-box),
  :deep(.p-checkbox-icon),
  .checkbox-field__label,
  .checkbox-field__clear-button {
    transition: none;
  }
}

/* ===============================
   HIGH CONTRAST MODE
   =============================== */

@media (prefers-contrast: high) {
  :deep(.p-checkbox-box) {
    border-width: 3px;
    border-color: rgb(0 0 0);
  }

  :deep(.p-checkbox-box.p-highlight) {
    background: rgb(0 0 0);
    border-color: rgb(0 0 0);
  }

  .checkbox-field__label {
    color: rgb(0 0 0);
  }

  .checkbox-field__error-message {
    color: rgb(139 0 0);
  }
}

/* ===============================
   DARK MODE SUPPORT
   =============================== */

@media (prefers-color-scheme: dark) {
  .checkbox-field__label {
    color: rgb(209 213 219);
  }

  .checkbox-field__label--disabled {
    color: rgb(107 114 128);
  }

  .checkbox-field__label--checked {
    color: rgb(147 197 253);
  }

  .checkbox-field__help-text {
    color: rgb(156 163 175);
  }

  :deep(.p-checkbox-box) {
    background: rgb(31 41 55);
    border-color: rgb(75 85 99);
  }

  :deep(.p-checkbox-box:hover) {
    border-color: rgb(147 197 253);
    background: rgb(30 58 138);
  }

  :deep(.p-checkbox-box.p-highlight) {
    background: rgb(59 130 246);
    border-color: rgb(59 130 246);
  }

  :deep(.p-checkbox-box.p-disabled) {
    background: rgb(17 24 39);
    border-color: rgb(55 65 81);
  }

  .checkbox-field__clear-button {
    color: rgb(156 163 175);
  }

  .checkbox-field__clear-button:hover {
    background: rgb(55 65 81);
    color: rgb(209 213 219);
  }
}

/* ===============================
   ANIMATION UTILITIES
   =============================== */

@keyframes checkbox-check {
  0% {
    transform: scale(0) rotate(45deg);
    opacity: 0;
  }
  50% {
    transform: scale(1.2) rotate(45deg);
    opacity: 0.8;
  }
  100% {
    transform: scale(1) rotate(45deg);
    opacity: 1;
  }
}

:deep(.p-checkbox-box.p-highlight .p-checkbox-icon) {
  animation: checkbox-check 0.2s ease-out;
}

@keyframes checkbox-shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-2px); }
  75% { transform: translateX(2px); }
}

.checkbox-field--error.checkbox-field--touched {
  animation: checkbox-shake 0.3s ease-in-out;
}

@media (prefers-reduced-motion: reduce) {
  :deep(.p-checkbox-box.p-highlight .p-checkbox-icon) {
    animation: none;
  }

  .checkbox-field--error.checkbox-field--touched {
    animation: none;
  }
}

/* ===============================
   PRINT STYLES
   =============================== */

@media print {
  .checkbox-field__clear-button,
  .checkbox-field__loading-indicator {
    display: none;
  }

  :deep(.p-checkbox-box) {
    border: 2px solid rgb(0 0 0) !important;
    background: white !important;
    print-color-adjust: exact;
  }

  :deep(.p-checkbox-box.p-highlight) {
    background: rgb(0 0 0) !important;
    print-color-adjust: exact;
  }

  :deep(.p-checkbox-box.p-highlight::after) {
    content: '✓';
    color: white;
    font-weight: bold;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .checkbox-field__label {
    color: rgb(0 0 0) !important;
  }

  .checkbox-field__error-message {
    color: rgb(139 0 0) !important;
  }
}

/* ===============================
   FOCUS MANAGEMENT
   =============================== */

.checkbox-field:focus-within {
  /* Optional: Add focus-within styling */
}

/* ===============================
   UTILITY CLASSES
   =============================== */

.checkbox-field--inline {
  margin-bottom: 0;
  display: inline-block;
  margin-right: var(--checkbox-spacing-md);
}

.checkbox-field--block {
  display: block;
  width: 100%;
}

.checkbox-field--centered {
  justify-content: center;
}

.checkbox-field--right-aligned .checkbox-field__container {
  flex-direction: row-reverse;
  text-align: right;
}
</style>
