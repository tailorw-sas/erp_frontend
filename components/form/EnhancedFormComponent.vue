<!-- components/form/EnhancedFormComponent.vue -->
<script setup lang="ts" generic="T extends Record<string, unknown> = Record<string, unknown>">
import {
  type Component,
  computed,
  nextTick,
  onBeforeUnmount,
  onMounted,
  provide,
  ref,
  watch
} from 'vue'
import Button from 'primevue/button'
import { useForm } from '../../composables/useForm'
import type {
  FormActions,
  FormConfig,
  FormContext,
  FormField,
  FormFieldEvent,
  FormSubmitEvent,
  FormValidationEvent,
  ValidationError
} from '../../types/form'
import { globalFieldFactory } from '../fields/registry'

// Props
interface EnhancedFormProps {
  fields: FormField[]
  initialValues?: Partial<T>
  config?: FormConfig
  validateOnChange?: boolean
  validateOnBlur?: boolean
  showActions?: boolean
  showActionsInline?: boolean
  hideSubmitButton?: boolean
  hideResetButton?: boolean
  hideCancelButton?: boolean
  submitLabel?: string
  resetLabel?: string
  cancelLabel?: string
  loading?: boolean
  disabled?: boolean
  readonly?: boolean
  dataTestId?: string // Changed from 'data-testid' to camelCase
}

const props = withDefaults(defineProps<EnhancedFormProps>(), {
  validateOnChange: true,
  validateOnBlur: true,
  showActions: true,
  showActionsInline: false,
  hideSubmitButton: false,
  hideResetButton: false,
  hideCancelButton: true,
  submitLabel: 'Submit',
  resetLabel: 'Reset',
  cancelLabel: 'Cancel',
  loading: false,
  disabled: false,
  readonly: false
})

// Emits - changed to camelCase
const emit = defineEmits<{
  'submit': [event: FormSubmitEvent<T>]
  'cancel': []
  'reset': []
  'fieldChange': [event: FormFieldEvent] // Changed from 'field-change'
  'fieldBlur': [fieldName: string] // Changed from 'field-blur'
  'fieldFocus': [fieldName: string] // Changed from 'field-focus'
  'validationChange': [event: FormValidationEvent] // Changed from 'validation-change'
  'update:values': [values: T]
  'update:errors': [errors: Record<keyof T, ValidationError[]>]
  'update:valid': [isValid: boolean]
  'update:dirty': [isDirty: boolean]
}>()

// Initialize form first
const {
  state: formState,
  actions: formActions,
  config: formConfig,
  fields: fieldsMap,
  isValid,
  isDirty,
  isTouched,
  isSubmitting,
  submitCount
} = useForm<T>({
  initialValues: props.initialValues,
  fields: props.fields,
  config: {
    ...props.config,
    validateOnChange: props.validateOnChange,
    validateOnBlur: props.validateOnBlur
  },
  onSubmit: async (values: T, _actions: FormActions<T>) => {
    const submitEvent: FormSubmitEvent<T> = {
      values,
      isValid: formState.value.meta.valid,
      errors: formState.value.errors,
      timestamp: Date.now()
    }
    emit('submit', submitEvent)
  },
  onValidate: async (_values: T) => {
    // Custom form-level validation can be added here
    return { success: true, errors: [] }
  }
})

// Template ref
const formRef = ref<HTMLFormElement | null>(null)

// Computed properties
const visibleFields = computed(() =>
  props.fields.filter(field => !field.conditional || evaluateConditional(field))
)

const formClasses = computed(() => {
  const classes = ['enhanced-form']

  // Size classes
  classes.push(`enhanced-form--${formConfig.value.size || 'medium'}`)

  // Variant classes
  classes.push(`enhanced-form--${formConfig.value.variant || 'default'}`)

  // Density classes
  classes.push(`enhanced-form--${formConfig.value.density || 'comfortable'}`)

  // State classes
  if (isValid.value) { classes.push('enhanced-form--valid') }
  if (!isValid.value && isTouched.value) { classes.push('enhanced-form--invalid') }
  if (isDirty.value) { classes.push('enhanced-form--dirty') }
  if (props.loading || isSubmitting.value) { classes.push('enhanced-form--loading') }
  if (props.disabled) { classes.push('enhanced-form--disabled') }
  if (props.readonly) { classes.push('enhanced-form--readonly') }

  // Custom classes
  if (formConfig.value.className) {
    if (Array.isArray(formConfig.value.className)) {
      classes.push(...formConfig.value.className)
    }
    else {
      classes.push(formConfig.value.className)
    }
  }

  return classes
})

const formStyles = computed(() => ({
  ...formConfig.value.style
}))

const canSubmit = computed(() =>
  isValid.value && !props.loading && !isSubmitting.value && !props.disabled
)

const canReset = computed(() =>
  isDirty.value && !props.loading && !isSubmitting.value && !props.disabled
)

// Provide form context to child components
const formContext: FormContext<T> = {
  state: formState.value,
  actions: formActions,
  config: formConfig.value,
  fields: fieldsMap.value
}
provide('formContext', formContext)

// Methods
function evaluateConditional(field: FormField): boolean {
  if (!field.conditional) { return true }

  const { field: conditionField, value: conditionValue, operator = 'eq' } = field.conditional
  const fieldValue = formState.value.values[conditionField as keyof T]

  switch (operator) {
    case 'eq':
      return fieldValue === conditionValue
    case 'neq':
      return fieldValue !== conditionValue
    case 'in':
      return Array.isArray(conditionValue) && conditionValue.includes(fieldValue)
    case 'nin':
      return Array.isArray(conditionValue) && !conditionValue.includes(fieldValue)
    case 'gt':
      return typeof fieldValue === 'number' && typeof conditionValue === 'number' && fieldValue > conditionValue
    case 'lt':
      return typeof fieldValue === 'number' && typeof conditionValue === 'number' && fieldValue < conditionValue
    case 'gte':
      return typeof fieldValue === 'number' && typeof conditionValue === 'number' && fieldValue >= conditionValue
    case 'lte':
      return typeof fieldValue === 'number' && typeof conditionValue === 'number' && fieldValue <= conditionValue
    default:
      return true
  }
}

function getFieldComponent(field: FormField): Component | null {
  return globalFieldFactory.create(field.type)
}

function handleFieldUpdate(fieldName: string, value: unknown) {
  const previousValue = formState.value.values[fieldName as keyof T]
  formActions.setFieldValue(fieldName as keyof T, value as T[keyof T])

  const changeEvent: FormFieldEvent = {
    field: fieldName,
    value,
    previousValue,
    timestamp: Date.now()
  }
  emit('fieldChange', changeEvent)
}

function handleFieldBlur(fieldName: string) {
  formActions.setFieldTouched(fieldName as keyof T, true)
  emit('fieldBlur', fieldName)
}

function handleFieldFocus(fieldName: string) {
  emit('fieldFocus', fieldName)
}

async function handleSubmit(event?: Event) {
  if (event) {
    event.preventDefault()
  }

  if (!canSubmit.value) { return }

  try {
    await formActions.submit()
  }
  catch (error) {
    if (process.env.NODE_ENV === 'development') {
      console.error('Form submission error:', error)
    }
    // Error handling can be customized here
  }
}

function handleReset() {
  formActions.reset()
  emit('reset')
}

function handleCancel() {
  emit('cancel')
}

async function focusFirstField() {
  await nextTick()
  const firstField = formRef.value?.querySelector('input, select, textarea') as HTMLElement
  firstField?.focus()
}

async function focusFirstErrorField() {
  await nextTick()
  const firstErrorField = formRef.value?.querySelector('.form-field--error input, .form-field--error select, .form-field--error textarea') as HTMLElement
  firstErrorField?.focus()
}

// Watchers
watch(formState, (newState) => {
  emit('update:values', newState.values)
  emit('update:errors', newState.errors)
  emit('update:valid', newState.meta.valid)
  emit('update:dirty', newState.meta.dirty)
}, { deep: true })

watch(isValid, (newIsValid, oldIsValid) => {
  if (newIsValid !== oldIsValid) {
    const validationEvent: FormValidationEvent = {
      isValid: newIsValid,
      errors: Object.values(formState.value.errors).flat(),
      timestamp: Date.now()
    }
    emit('validationChange', validationEvent)
  }
})

// Lifecycle
onMounted(() => {
  // Auto-focus first field if configured
  if ((formConfig.value as any).autoFocus) {
    focusFirstField()
  }
})

onBeforeUnmount(() => {
  // Cleanup if needed
})

// Expose methods for parent components
defineExpose({
  // Form actions
  submit: handleSubmit,
  reset: handleReset,
  validate: formActions.validateForm,
  // Field actions
  setFieldValue: formActions.setFieldValue,
  setFieldError: formActions.setFieldError,
  setFieldTouched: formActions.setFieldTouched,
  clearField: formActions.clearField,
  resetField: formActions.resetField,
  validateField: formActions.validateField,
  // Focus management
  focusFirstField,
  focusFirstErrorField,
  // State getters
  getValues: () => formState.value.values,
  getErrors: () => formState.value.errors,
  isValid: () => isValid.value,
  isDirty: () => isDirty.value,
  isTouched: () => isTouched.value,
  isSubmitting: () => isSubmitting.value,
  // References
  formRef,
  formState,
  formActions
})

const isDevelopment = computed(() => {
  return process.env.NODE_ENV === 'development'
})
</script>

<template>
  <form
    ref="formRef"
    :class="formClasses"
    :style="formStyles"
    :data-testid="dataTestId"
    novalidate
    @submit="handleSubmit"
  >
    <!-- Form Fields -->
    <div class="enhanced-form__fields">
      <template
        v-for="field in visibleFields"
        :key="field.name"
      >
        <!-- Dynamic Field Component -->
        <component
          :is="getFieldComponent(field)"
          v-if="getFieldComponent(field)"
          :field="field"
          :value="formState.values[field.name as keyof T]"
          :error="formState.errors[field.name as keyof T]"
          :touched="formState.touched[field.name as keyof T]"
          :dirty="formState.dirty[field.name as keyof T]"
          :disabled="disabled || field.ui?.readonly"
          :loading="loading"
          :config="formConfig"
          :obj-api="field.objApi"
          :kw-args="field.kwArgs"
          :api-config="field.apiConfig"
          :multiple="field.multiple"
          :max-selected-labels="field.maxSelectedLabels"
          :debounce-time-ms="field.debounceTimeMs"
          :filters-base="field.filtersBase"
          :dependent-field="field.dependentField"
          :load-on-open="field.loadOnOpen"
          :min-query-length="field.minQueryLength"
          :max-items="field.maxItems"
          :show-clear="field.showClear"
          :filterable="field.filterable"
          @update:value="handleFieldUpdate(field.name, $event)"
          @blur="handleFieldBlur(field.name)"
          @focus="handleFieldFocus(field.name)"
          @clear="formActions.clearField(field.name as keyof T)"
        >
          <!-- Pass through all field-specific slots -->
          <template
            v-for="(_, slotName) in $slots"
            :key="slotName"
            #[slotName]="slotProps"
          >
            <slot
              :name="slotName"
              v-bind="slotProps"
            />
          </template>
        </component>

        <!-- Fallback for missing field component -->
        <div
          v-else
          :key="`fallback-${field.name}`"
          class="enhanced-form__field-error"
        >
          <p class="enhanced-form__field-error-message">
            Field component for type "{{ field.type }}" not found.
          </p>
        </div>
      </template>
    </div>

    <!-- Form Actions -->
    <div
      v-if="showActions"
      class="enhanced-form__actions"
      :class="{
        'enhanced-form__actions--inline': showActionsInline,
      }"
    >
      <slot
        name="actions"
        :form-state="formState"
        :form-actions="formActions"
        :can-submit="canSubmit"
        :can-reset="canReset"
        :is-loading="loading || isSubmitting"
        :submit="handleSubmit"
        :reset="handleReset"
        :cancel="handleCancel"
      >
        <!-- Cancel Button -->
        <Button
          v-if="!hideCancelButton"
          type="button"
          :label="cancelLabel"
          severity="secondary"
          outlined
          :disabled="disabled"
          :loading="loading || isSubmitting"
          class="enhanced-form__button enhanced-form__button--cancel"
          @click="handleCancel"
        />

        <!-- Reset Button -->
        <Button
          v-if="!hideResetButton"
          type="button"
          :label="resetLabel"
          severity="secondary"
          :disabled="!canReset"
          :loading="loading || isSubmitting"
          class="enhanced-form__button enhanced-form__button--reset"
          @click="handleReset"
        />

        <!-- Submit Button -->
        <Button
          v-if="!hideSubmitButton"
          type="submit"
          :label="submitLabel"
          :disabled="!canSubmit"
          :loading="loading || isSubmitting"
          class="enhanced-form__button enhanced-form__button--submit"
        />
      </slot>
    </div>

    <!-- Debug Information (development only) -->
    <div
      v-if="isDevelopment && (formConfig as any).showDebugInfo"
      class="enhanced-form__debug"
    >
      <details>
        <summary>Form Debug Info</summary>
        <pre>{{ JSON.stringify({
          isValid,
          isDirty,
          isTouched,
          isSubmitting,
          submitCount,
          values: formState.values,
          errors: formState.errors,
          touched: formState.touched,
          dirty: formState.dirty,
        }, null, 2) }}</pre>
      </details>
    </div>
  </form>
</template>

<style scoped>
/* ===============================
   ENHANCED FORM STYLES
   =============================== */

.enhanced-form {
  --form-spacing-xs: 0.25rem;
  --form-spacing-sm: 0.5rem;
  --form-spacing-md: 0.75rem;
  --form-spacing-lg: 1rem;
  --form-spacing-xl: 1.5rem;
  --form-border-radius: 8px;
  --form-transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

  position: relative;
  width: 100%;
  font-family: inherit;
}

/* ===============================
   SIZE VARIANTS
   =============================== */

.enhanced-form--small {
  font-size: 0.875rem;
}

.enhanced-form--medium {
  font-size: 1rem;
}

.enhanced-form--large {
  font-size: 1.125rem;
}

/* ===============================
   DENSITY VARIANTS
   =============================== */

.enhanced-form--compact .enhanced-form__fields {
  gap: var(--form-spacing-sm);
}

.enhanced-form--comfortable .enhanced-form__fields {
  gap: var(--form-spacing-md);
}

.enhanced-form--spacious .enhanced-form__fields {
  gap: var(--form-spacing-lg);
}

/* ===============================
   VARIANT STYLES
   =============================== */

.enhanced-form--outlined {
  border: 1px solid rgb(229 231 235);
  border-radius: var(--form-border-radius);
  padding: var(--form-spacing-xl);
  background: white;
}

.enhanced-form--filled {
  background: rgb(249 250 251);
  border-radius: var(--form-border-radius);
  padding: var(--form-spacing-xl);
}

.enhanced-form--underlined {
  border-bottom: 2px solid rgb(229 231 235);
  padding-bottom: var(--form-spacing-lg);
}

/* ===============================
   FIELDS CONTAINER
   =============================== */

.enhanced-form__fields {
  display: flex;
  flex-direction: column;
  gap: var(--form-spacing-lg);
  width: 100%;
}

/* ===============================
   FIELD ERROR FALLBACK
   =============================== */

.enhanced-form__field-error {
  padding: var(--form-spacing-md);
  background: rgb(254 242 242);
  border: 1px solid rgb(252 165 165);
  border-radius: var(--form-border-radius);
  color: rgb(153 27 27);
}

.enhanced-form__field-error-message {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 500;
}

/* ===============================
   ACTIONS CONTAINER
   =============================== */

.enhanced-form__actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: var(--form-spacing-sm);
  margin-top: var(--form-spacing-xl);
  padding-top: var(--form-spacing-lg);
  border-top: 1px solid rgb(229 231 235);
}

.enhanced-form__actions--inline {
  margin-top: var(--form-spacing-lg);
  padding-top: 0;
  border-top: none;
}

/* ===============================
   FORM BUTTONS
   =============================== */

.enhanced-form__button {
  min-width: 6rem;
  height: 2.5rem;
  padding: 0 var(--form-spacing-lg);
  border-radius: var(--form-border-radius);
  font-weight: 500;
  transition: var(--form-transition);
  cursor: pointer;
  border: 1px solid transparent;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  white-space: nowrap;
}

.enhanced-form__button--submit {
  background: rgb(59 130 246);
  color: white;
  border-color: rgb(59 130 246);
}

.enhanced-form__button--submit:hover:not(:disabled) {
  background: rgb(37 99 235);
  border-color: rgb(37 99 235);
}

.enhanced-form__button--submit:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgb(59 130 246 / 0.5);
}

.enhanced-form__button--reset {
  background: rgb(107 114 128);
  color: white;
  border-color: rgb(107 114 128);
}

.enhanced-form__button--reset:hover:not(:disabled) {
  background: rgb(75 85 99);
  border-color: rgb(75 85 99);
}

.enhanced-form__button--reset:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgb(107 114 128 / 0.5);
}

.enhanced-form__button--cancel {
  background: transparent;
  color: rgb(107 114 128);
  border-color: rgb(209 213 219);
}

.enhanced-form__button--cancel:hover:not(:disabled) {
  background: rgb(249 250 251);
  color: rgb(55 65 81);
  border-color: rgb(156 163 175);
}

.enhanced-form__button--cancel:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgb(107 114 128 / 0.3);
}

.enhanced-form__button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===============================
   STATE STYLES
   =============================== */

.enhanced-form--loading {
  position: relative;
}

.enhanced-form--loading::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  border-radius: inherit;
  z-index: 1000;
  pointer-events: none;
}

.enhanced-form--disabled {
  opacity: 0.6;
  pointer-events: none;
}

.enhanced-form--readonly {
  position: relative;
}

/* ===============================
   DEBUG STYLES
   =============================== */

.enhanced-form__debug {
  margin-top: var(--form-spacing-xl);
  padding: var(--form-spacing-lg);
  background: rgb(249 250 251);
  border: 1px solid rgb(229 231 235);
  border-radius: var(--form-border-radius);
  font-family: ui-monospace, SFMono-Regular, "SF Mono", Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}

.enhanced-form__debug details {
  cursor: pointer;
}

.enhanced-form__debug summary {
  font-weight: 600;
  color: rgb(55 65 81);
  margin-bottom: var(--form-spacing-sm);
}

.enhanced-form__debug pre {
  font-size: 0.75rem;
  color: rgb(75 85 99);
  overflow: auto;
  max-height: 20rem;
  margin: 0;
  padding: var(--form-spacing-sm);
  background: white;
  border-radius: calc(var(--form-border-radius) - 2px);
  border: 1px solid rgb(229 231 235);
}

/* ===============================
   RESPONSIVE DESIGN
   =============================== */

@media (max-width: 768px) {
  .enhanced-form__actions {
    flex-direction: column;
    align-items: stretch;
  }

  .enhanced-form__button {
    width: 100%;
    min-width: auto;
  }

  .enhanced-form--outlined,
  .enhanced-form--filled {
    padding: var(--form-spacing-lg);
  }
}

@media (max-width: 480px) {
  .enhanced-form {
    --form-spacing-xs: 0.125rem;
    --form-spacing-sm: 0.25rem;
    --form-spacing-md: 0.5rem;
    --form-spacing-lg: 0.75rem;
    --form-spacing-xl: 1rem;
  }

  .enhanced-form--outlined,
  .enhanced-form--filled {
    padding: var(--form-spacing-md);
  }
}

/* ===============================
   ACCESSIBILITY ENHANCEMENTS
   =============================== */

.enhanced-form__button:focus-visible {
  outline: 2px solid currentColor;
  outline-offset: 2px;
}

@media (prefers-reduced-motion: reduce) {
  .enhanced-form,
  .enhanced-form__button {
    transition: none;
  }

  .enhanced-form--loading::after {
    animation: none;
  }
}

/* ===============================
   HIGH CONTRAST MODE
   =============================== */

@media (prefers-contrast: high) {
  .enhanced-form--outlined {
    border-color: rgb(0 0 0);
  }

  .enhanced-form__actions {
    border-color: rgb(0 0 0);
  }

  .enhanced-form__button {
    border-width: 2px;
  }
}

/* ===============================
   DARK MODE SUPPORT
   =============================== */

@media (prefers-color-scheme: dark) {
  .enhanced-form--outlined {
    background: rgb(17 24 39);
    border-color: rgb(75 85 99);
  }

  .enhanced-form--filled {
    background: rgb(31 41 55);
  }

  .enhanced-form--underlined {
    border-color: rgb(75 85 99);
  }

  .enhanced-form__actions {
    border-color: rgb(75 85 99);
  }

  .enhanced-form__debug {
    background: rgb(31 41 55);
    border-color: rgb(75 85 99);
  }

  .enhanced-form__debug summary {
    color: rgb(209 213 219);
  }

  .enhanced-form__debug pre {
    background: rgb(17 24 39);
    border-color: rgb(75 85 99);
    color: rgb(156 163 175);
  }

  .enhanced-form__field-error {
    background: rgb(69 10 10);
    border-color: rgb(153 27 27);
    color: rgb(252 165 165);
  }
}

/* ===============================
   PRINT STYLES
   =============================== */

@media print {
  .enhanced-form__actions {
    display: none;
  }

  .enhanced-form__debug {
    display: none;
  }

  .enhanced-form {
    box-shadow: none;
    border: 1px solid rgb(0 0 0);
  }
}

/* ===============================
   ANIMATION KEYFRAMES
   =============================== */

@keyframes form-shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}

.enhanced-form--invalid.enhanced-form--touched {
  animation: form-shake 0.3s ease-in-out;
}

@keyframes form-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}

.enhanced-form--loading .enhanced-form__fields {
  animation: form-pulse 1.5s ease-in-out infinite;
}

/* ===============================
   UTILITY CLASSES
   =============================== */

.enhanced-form--no-spacing .enhanced-form__fields {
  gap: 0;
}

.enhanced-form--wide-spacing .enhanced-form__fields {
  gap: var(--form-spacing-xl);
}

.enhanced-form--center {
  max-width: 32rem;
  margin: 0 auto;
}

.enhanced-form--full-width {
  width: 100%;
}

.enhanced-form--fixed-height {
  height: 100vh;
  overflow: auto;
}

.enhanced-form__fields--grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--form-spacing-lg);
}

.enhanced-form__fields--two-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--form-spacing-lg);
}

@media (max-width: 768px) {
  .enhanced-form__fields--grid,
  .enhanced-form__fields--two-columns {
    grid-template-columns: 1fr;
  }
}
</style>
