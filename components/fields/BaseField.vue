<!-- components/fields/BaseField.vue -->
<script setup lang="ts" generic="T = unknown">
import { type ComputedRef, computed, inject } from 'vue'
import type {
  FormConfig,
  FormContext,
  FormFieldProps,
  ValidationError
} from '../../types/form'

// Props
interface BaseFieldProps extends Omit<FormFieldProps<T>, 'onUpdate' | 'onBlur' | 'onFocus'> {
  'id'?: string
  'data-testid'?: string
}

const props = defineProps<BaseFieldProps>()

// Emits
const emit = defineEmits<{
  'update:value': [value: T]
  'blur': []
  'focus': []
  'clear': []
}>()

// Inject form context
const formContext = inject<FormContext>('formContext', undefined)

// Computed properties
const fieldId = computed(() =>
  props.id || `field-${props.field.name}`
)

const fieldConfig = computed(() => ({
  ...formContext?.config,
  ...props.config
}))

const fieldClasses = computed(() => {
  const classes = ['form-field']

  // Size classes
  classes.push(`form-field--${fieldConfig.value.size}`)

  // Variant classes
  classes.push(`form-field--${fieldConfig.value.variant}`)

  // Density classes
  classes.push(`form-field--${fieldConfig.value.density}`)

  // State classes
  if (hasErrors.value) { classes.push('form-field--error') }
  if (props.touched) { classes.push('form-field--touched') }
  if (props.dirty) { classes.push('form-field--dirty') }
  if (props.disabled) { classes.push('form-field--disabled') }
  if (props.loading) { classes.push('form-field--loading') }
  if (isRequired.value) { classes.push('form-field--required') }

  // Custom classes
  if (props.field.ui?.className) {
    if (Array.isArray(props.field.ui.className)) {
      classes.push(...props.field.ui.className)
    }
    else {
      classes.push(props.field.ui.className)
    }
  }

  return classes
})

const fieldStyles = computed(() => ({
  ...props.field.ui?.style,
  ...fieldConfig.value.style
}))

const hasErrors = computed(() =>
  props.error && props.error.length > 0
)

const isRequired = computed(() => {
  // Check if field has required validation
  // This is a simplified check - in real implementation,
  // you'd need to introspect the validation schema
  return props.field.validation?.toString().includes('required') || false
})

const isDisabled = computed(() =>
  props.disabled || props.field.ui?.readonly || false
)

const isLoading = computed(() =>
  props.loading || props.field.ui?.loading || false
)

const placeholder = computed(() =>
  props.field.ui?.placeholder || ''
)

const helpText = computed(() =>
  props.field.ui?.helpText || ''
)

// Event handlers
function handleUpdate(value: T) {
  if (!isDisabled.value) {
    emit('update:value', value)
  }
}

function handleBlur() {
  if (!isDisabled.value) {
    emit('blur')
  }
}

function handleFocus() {
  if (!isDisabled.value) {
    emit('focus')
  }
}

function handleClear() {
  if (!isDisabled.value && props.field.ui?.clearable) {
    emit('clear')
  }
}

// Expose computed properties and handlers for child components
defineExpose({
  fieldId,
  fieldConfig,
  fieldClasses,
  fieldStyles,
  hasErrors,
  isRequired,
  isDisabled,
  isLoading,
  placeholder,
  helpText,
  handleUpdate,
  handleBlur,
  handleFocus,
  handleClear
})
</script>

<template>
  <div
    :class="fieldClasses"
    :style="fieldStyles"
    :data-testid="$props['data-testid']"
  >
    <!-- Field Label -->
    <label
      v-if="field.label"
      :for="fieldId"
      class="form-field__label"
      :class="{
        'form-field__label--required': isRequired && fieldConfig.showRequiredIndicator,
      }"
    >
      <slot name="label" :field="field" :config="fieldConfig">
        {{ field.label }}
        <span
          v-if="isRequired && fieldConfig.showRequiredIndicator"
          class="form-field__required-indicator"
        >
          {{ fieldConfig.requiredIndicator }}
        </span>
      </slot>
    </label>

    <!-- Field Input Container -->
    <div class="form-field__input-container">
      <!-- Prefix -->
      <div
        v-if="field.ui?.prefix"
        class="form-field__prefix"
      >
        <slot name="prefix" :field="field">
          {{ field.ui.prefix }}
        </slot>
      </div>

      <!-- Input Field (slot for specific field implementations) -->
      <div class="form-field__input-wrapper">
        <slot
          name="input"
          :field="field"
          :value="value"
          :field-id="fieldId"
          :is-disabled="isDisabled"
          :is-loading="isLoading"
          :has-errors="hasErrors"
          :placeholder="placeholder"
          :on-update="handleUpdate"
          :on-blur="handleBlur"
          :on-focus="handleFocus"
        />

        <!-- Loading indicator -->
        <div
          v-if="isLoading"
          class="form-field__loading-indicator"
        >
          <slot name="loading">
            <i class="pi pi-spinner pi-spin" />
          </slot>
        </div>

        <!-- Clear button -->
        <button
          v-if="field.ui?.clearable && value && !isDisabled"
          type="button"
          class="form-field__clear-button"
          :aria-label="`Clear ${field.label || field.name}`"
          @click="handleClear"
        >
          <slot name="clear-icon">
            <i class="pi pi-times" />
          </slot>
        </button>
      </div>

      <!-- Suffix -->
      <div
        v-if="field.ui?.suffix"
        class="form-field__suffix"
      >
        <slot name="suffix" :field="field">
          {{ field.ui.suffix }}
        </slot>
      </div>

      <!-- Icon -->
      <div
        v-if="field.ui?.icon"
        class="form-field__icon"
      >
        <slot name="icon" :field="field">
          <i :class="field.ui.icon" />
        </slot>
      </div>
    </div>

    <!-- Help Text -->
    <div
      v-if="helpText && !hasErrors"
      class="form-field__help-text"
    >
      <slot name="help" :field="field" :help-text="helpText">
        {{ helpText }}
      </slot>
    </div>

    <!-- Error Messages -->
    <div
      v-if="hasErrors"
      class="form-field__error-container"
      role="alert"
      :aria-live="touched ? 'polite' : 'off'"
    >
      <div
        v-for="(errorItem, index) in error"
        :key="`${errorItem.code}-${index}`"
        class="form-field__error-message"
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
</template>

<style scoped>
/* ===============================
   BASE FIELD STYLES
   =============================== */

.form-field {
  --field-spacing-xs: 0.25rem;
  --field-spacing-sm: 0.5rem;
  --field-spacing-md: 0.75rem;
  --field-spacing-lg: 1rem;

  --field-border-width: 1px;
  --field-border-radius: 6px;
  --field-transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

  --field-font-size-sm: 0.75rem;
  --field-font-size-md: 0.875rem;
  --field-font-size-lg: 1rem;

  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  margin-bottom: var(--field-spacing-lg);
}

/* ===============================
   SIZE VARIANTS
   =============================== */

.form-field--small {
  --field-height: 2rem;
  --field-font-size: var(--field-font-size-sm);
  --field-padding: 0 var(--field-spacing-sm);
}

.form-field--medium {
  --field-height: 2.5rem;
  --field-font-size: var(--field-font-size-md);
  --field-padding: 0 var(--field-spacing-md);
}

.form-field--large {
  --field-height: 3rem;
  --field-font-size: var(--field-font-size-lg);
  --field-padding: 0 var(--field-spacing-lg);
}

/* ===============================
   DENSITY VARIANTS
   =============================== */

.form-field--compact {
  margin-bottom: var(--field-spacing-sm);
}

.form-field--comfortable {
  margin-bottom: var(--field-spacing-md);
}

.form-field--spacious {
  margin-bottom: calc(var(--field-spacing-lg) * 1.5);
}

/* ===============================
   VARIANT STYLES
   =============================== */

.form-field--outlined {
  --field-border-color: rgb(209 213 219);
  --field-border-color-hover: rgb(156 163 175);
  --field-border-color-focus: rgb(59 130 246);
  --field-background: rgb(255 255 255);
}

.form-field--filled {
  --field-border-color: transparent;
  --field-border-color-hover: transparent;
  --field-border-color-focus: rgb(59 130 246);
  --field-background: rgb(249 250 251);
}

.form-field--underlined {
  --field-border-color: transparent transparent rgb(209 213 219) transparent;
  --field-border-color-hover: transparent transparent rgb(156 163 175) transparent;
  --field-border-color-focus: transparent transparent rgb(59 130 246) transparent;
  --field-background: transparent;
  --field-border-radius: 0;
}

/* ===============================
   LABEL STYLES
   =============================== */

.form-field__label {
  display: block;
  font-size: var(--field-font-size-sm);
  font-weight: 500;
  color: rgb(55 65 81);
  margin-bottom: var(--field-spacing-xs);
  line-height: 1.5;
  cursor: pointer;
}

.form-field__required-indicator {
  color: rgb(239 68 68);
  margin-left: 0.125rem;
  font-weight: 600;
}

/* ===============================
   INPUT CONTAINER
   =============================== */

.form-field__input-container {
  position: relative;
  display: flex;
  align-items: stretch;
  width: 100%;
}

.form-field__input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0; /* Allow flex item to shrink */
}

.form-field__prefix,
.form-field__suffix {
  display: flex;
  align-items: center;
  padding: 0 var(--field-spacing-sm);
  background: rgb(249 250 251);
  border: var(--field-border-width) solid var(--field-border-color);
  color: rgb(107 114 128);
  font-size: var(--field-font-size);
  white-space: nowrap;
  user-select: none;
}

.form-field__prefix {
  border-right: none;
  border-radius: var(--field-border-radius) 0 0 var(--field-border-radius);
}

.form-field__suffix {
  border-left: none;
  border-radius: 0 var(--field-border-radius) var(--field-border-radius) 0;
}

.form-field__icon {
  position: absolute;
  right: var(--field-spacing-md);
  top: 50%;
  transform: translateY(-50%);
  color: rgb(107 114 128);
  pointer-events: none;
  z-index: 1;
}

/* ===============================
   INTERACTIVE ELEMENTS
   =============================== */

.form-field__clear-button {
  position: absolute;
  right: var(--field-spacing-sm);
  top: 50%;
  transform: translateY(-50%);
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
  z-index: 2;
  transition: var(--field-transition);
}

.form-field__clear-button:hover {
  background: rgb(243 244 246);
  color: rgb(55 65 81);
}

.form-field__clear-button:focus {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 1px;
}

.form-field__loading-indicator {
  position: absolute;
  right: var(--field-spacing-md);
  top: 50%;
  transform: translateY(-50%);
  color: rgb(59 130 246);
  z-index: 1;
}

/* ===============================
   HELP TEXT
   =============================== */

.form-field__help-text {
  margin-top: var(--field-spacing-xs);
  font-size: var(--field-font-size-sm);
  color: rgb(107 114 128);
  line-height: 1.4;
}

/* ===============================
   ERROR STYLES
   =============================== */

.form-field__error-container {
  margin-top: var(--field-spacing-xs);
}

.form-field__error-message {
  display: flex;
  align-items: flex-start;
  gap: var(--field-spacing-xs);
  font-size: var(--field-font-size-sm);
  color: rgb(239 68 68);
  line-height: 1.4;
  margin-bottom: var(--field-spacing-xs);
}

.form-field__error-message:last-child {
  margin-bottom: 0;
}

.form-field__error-message i {
  flex-shrink: 0;
  margin-top: 0.125rem;
}

/* ===============================
   STATE STYLES
   =============================== */

.form-field--error {
  --field-border-color: rgb(239 68 68);
  --field-border-color-hover: rgb(239 68 68);
  --field-border-color-focus: rgb(239 68 68);
}

.form-field--disabled {
  opacity: 0.6;
  pointer-events: none;
}

.form-field--loading {
  position: relative;
}

/* ===============================
   FOCUS MANAGEMENT
   =============================== */

.form-field:focus-within .form-field__input-container {
  z-index: 1;
}

/* ===============================
   RESPONSIVE DESIGN
   =============================== */

@media (max-width: 640px) {
  .form-field {
    margin-bottom: var(--field-spacing-md);
  }

  .form-field--small {
    --field-height: 2.25rem;
  }

  .form-field--medium {
    --field-height: 2.75rem;
  }

  .form-field--large {
    --field-height: 3.25rem;
  }
}

/* ===============================
   ANIMATION UTILITIES
   =============================== */

@keyframes field-shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}

.form-field--error.form-field--touched {
  animation: field-shake 0.3s ease-in-out;
}

/* ===============================
   ACCESSIBILITY IMPROVEMENTS
   =============================== */

.form-field__label:focus-visible,
.form-field__clear-button:focus-visible {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 2px;
}

@media (prefers-reduced-motion: reduce) {
  .form-field,
  .form-field__clear-button {
    transition: none;
  }

  .form-field--error.form-field--touched {
    animation: none;
  }
}

/* ===============================
   HIGH CONTRAST MODE
   =============================== */

@media (prefers-contrast: high) {
  .form-field {
    --field-border-color: rgb(0 0 0);
    --field-border-color-focus: rgb(0 0 255);
  }

  .form-field--error {
    --field-border-color: rgb(255 0 0);
  }
}
</style>
