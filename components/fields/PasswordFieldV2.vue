<!-- components/fields/PasswordField.vue -->
<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import Password from 'primevue/password'
import type { FormFieldProps } from '../../types/form'
import BaseField from './BaseField.vue'

// Props
interface PasswordFieldProps extends FormFieldProps<string> {
  feedback?: boolean
  toggleMask?: boolean
  promptLabel?: string
  weakLabel?: string
  mediumLabel?: string
  strongLabel?: string
  mediumRegex?: string | RegExp
  strongRegex?: string | RegExp
  showStrengthMeter?: boolean
  inputClass?: string
  panelClass?: string
  meterClass?: string
  promptClass?: string
  weakClass?: string
  mediumClass?: string
  strongClass?: string
}

const props = withDefaults(defineProps<PasswordFieldProps>(), {
  feedback: true,
  toggleMask: true,
  promptLabel: 'Enter a password',
  weakLabel: 'Weak',
  mediumLabel: 'Medium',
  strongLabel: 'Strong',
  mediumRegex: '^(((?=.*[a-z])(?=.*[A-Z]))|((?=.*[a-z])(?=.*[0-9]))|((?=.*[A-Z])(?=.*[0-9])))(?=.{6,})',
  strongRegex: '^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})',
  showStrengthMeter: true
})

// Emits
const emit = defineEmits<{
  'update:value': [value: string]
  'blur': []
  'focus': []
  'clear': []
  'strengthChange': [strength: 'weak' | 'medium' | 'strong' | null]
}>()

// Template ref
const passwordRef = ref<HTMLElement | null>(null)

// Computed properties
const passwordValue = computed({
  get: () => props.value || '',
  set: (newValue: string) => {
    handleUpdate(newValue)
  }
})

const passwordClasses = computed(() => {
  const classes = ['password-field__input']

  if (props.error && props.error.length > 0) {
    classes.push('p-invalid')
  }

  if (props.inputClass) {
    classes.push(props.inputClass)
  }

  return classes
})

const panelClasses = computed(() => {
  const classes = ['password-field__panel']

  if (props.panelClass) {
    classes.push(props.panelClass)
  }

  return classes
})

const isDisabled = computed(() =>
  props.disabled || props.field.ui?.readonly
)

const placeholder = computed(() =>
  props.field.ui?.placeholder || 'Enter password'
)

const passwordStrength = computed(() => {
  const value = passwordValue.value
  if (!value) { return null }

  const strongPattern = new RegExp(props.strongRegex)
  const mediumPattern = new RegExp(props.mediumRegex)

  if (strongPattern.test(value)) { return 'strong' }
  if (mediumPattern.test(value)) { return 'medium' }
  return 'weak'
})

const strengthColor = computed(() => {
  switch (passwordStrength.value) {
    case 'strong':
      return '#10b981' // green
    case 'medium':
      return '#f59e0b' // yellow
    case 'weak':
      return '#ef4444' // red
    default:
      return '#e5e7eb' // gray
  }
})

const strengthPercentage = computed(() => {
  switch (passwordStrength.value) {
    case 'strong':
      return 100
    case 'medium':
      return 66
    case 'weak':
      return 33
    default:
      return 0
  }
})

// Methods
function handleUpdate(value: string) {
  emit('update:value', value)

  // Emit strength change
  const currentStrength = passwordStrength.value
  emit('strengthChange', currentStrength)
}

function handleBlur() {
  emit('blur')
}

function handleFocus() {
  emit('focus')
}

function handleClear() {
  emit('update:value', '')
  emit('clear')
}

// Password requirements checker
function getPasswordRequirements() {
  const value = passwordValue.value
  return {
    minLength: value.length >= 8,
    hasLowercase: /[a-z]/.test(value),
    hasUppercase: /[A-Z]/.test(value),
    hasNumber: /\d/.test(value),
    hasSpecialChar: /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(value)
  }
}

const passwordRequirements = computed(() => getPasswordRequirements())

// Expose methods for parent components
async function focus() {
  await nextTick()
  const input = passwordRef.value?.querySelector('input')
  input?.focus()
}

function blur() {
  const input = passwordRef.value?.querySelector('input')
  input?.blur()
}

function toggleVisibility() {
  const button = passwordRef.value?.querySelector('.p-password-toggle')
  if (button instanceof HTMLElement) {
    button.click()
  }
}

defineExpose({
  focus,
  blur,
  toggleVisibility,
  passwordStrength,
  passwordRequirements,
  passwordRef
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
      <div class="password-field__container">
        <Password
          :id="fieldId"
          ref="passwordRef"
          v-model="passwordValue"
          :class="passwordClasses"
          :panel-class="panelClasses"
          :disabled="isDisabled"
          :placeholder="placeholder"
          :feedback="feedback && !isDisabled"
          :toggle-mask="toggleMask"
          :prompt-label="promptLabel"
          :weak-label="weakLabel"
          :medium-label="mediumLabel"
          :strong-label="strongLabel"
          :medium-regex="mediumRegex"
          :strong-regex="strongRegex"
          @blur="onBlur"
          @focus="onFocus"
        />

        <!-- Custom Strength Meter -->
        <div
          v-if="showStrengthMeter && passwordValue"
          class="password-field__strength-meter"
        >
          <div class="password-field__strength-bar">
            <div
              class="password-field__strength-fill"
              :style="{
                width: `${strengthPercentage}%`,
                backgroundColor: strengthColor,
              }"
            />
          </div>
          <div class="password-field__strength-label">
            <span
              class="password-field__strength-text"
              :class="`password-field__strength-text--${passwordStrength}`"
            >
              {{ passwordStrength === 'strong' ? strongLabel
                : passwordStrength === 'medium' ? mediumLabel
                  : passwordStrength === 'weak' ? weakLabel : '' }}
            </span>
          </div>
        </div>

        <!-- Password Requirements -->
        <div
          v-if="feedback && passwordValue"
          class="password-field__requirements"
        >
          <div class="password-field__requirement-list">
            <div
              class="password-field__requirement"
              :class="{ 'password-field__requirement--met': passwordRequirements.minLength }"
            >
              <i :class="passwordRequirements.minLength ? 'pi pi-check' : 'pi pi-times'" />
              At least 8 characters
            </div>
            <div
              class="password-field__requirement"
              :class="{ 'password-field__requirement--met': passwordRequirements.hasLowercase }"
            >
              <i :class="passwordRequirements.hasLowercase ? 'pi pi-check' : 'pi pi-times'" />
              One lowercase letter
            </div>
            <div
              class="password-field__requirement"
              :class="{ 'password-field__requirement--met': passwordRequirements.hasUppercase }"
            >
              <i :class="passwordRequirements.hasUppercase ? 'pi pi-check' : 'pi pi-times'" />
              One uppercase letter
            </div>
            <div
              class="password-field__requirement"
              :class="{ 'password-field__requirement--met': passwordRequirements.hasNumber }"
            >
              <i :class="passwordRequirements.hasNumber ? 'pi pi-check' : 'pi pi-times'" />
              One number
            </div>
            <div
              class="password-field__requirement"
              :class="{ 'password-field__requirement--met': passwordRequirements.hasSpecialChar }"
            >
              <i :class="passwordRequirements.hasSpecialChar ? 'pi pi-check' : 'pi pi-times'" />
              One special character
            </div>
          </div>
        </div>
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
   PASSWORD FIELD STYLES
   =============================== */

.password-field__container {
  width: 100%;
}

.password-field__input {
  width: 100%;
}

/* ===============================
   STRENGTH METER
   =============================== */

.password-field__strength-meter {
  margin-top: 0.5rem;
}

.password-field__strength-bar {
  width: 100%;
  height: 4px;
  background: rgb(229 231 235);
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 0.25rem;
}

.password-field__strength-fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.password-field__strength-label {
  display: flex;
  justify-content: flex-end;
}

.password-field__strength-text {
  font-size: 0.75rem;
  font-weight: 500;
  text-transform: capitalize;
}

.password-field__strength-text--weak {
  color: rgb(239 68 68);
}

.password-field__strength-text--medium {
  color: rgb(245 158 11);
}

.password-field__strength-text--strong {
  color: rgb(16 185 129);
}

/* ===============================
   PASSWORD REQUIREMENTS
   =============================== */

.password-field__requirements {
  margin-top: 0.5rem;
  padding: 0.75rem;
  background: rgb(249 250 251);
  border: 1px solid rgb(229 231 235);
  border-radius: var(--field-border-radius, 6px);
}

.password-field__requirement-list {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.password-field__requirement {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.75rem;
  color: rgb(107 114 128);
  transition: color 0.2s ease;
}

.password-field__requirement--met {
  color: rgb(16 185 129);
}

.password-field__requirement i {
  font-size: 0.625rem;
  flex-shrink: 0;
}

.password-field__requirement--met i {
  color: rgb(16 185 129);
}

.password-field__requirement:not(.password-field__requirement--met) i {
  color: rgb(239 68 68);
}

/* ===============================
   PRIMEVUE PASSWORD OVERRIDES
   =============================== */

:deep(.p-password) {
  width: 100%;
  display: block;
}

:deep(.p-password input) {
  width: 100%;
  height: var(--field-height);
  padding: var(--field-padding);
  font-size: var(--field-font-size);
  border: var(--field-border-width) solid var(--field-border-color);
  border-radius: var(--field-border-radius);
  background: var(--field-background);
  color: rgb(17 24 39);
  transition: var(--field-transition);
}

:deep(.p-password input::placeholder) {
  color: rgb(156 163 175);
}

:deep(.p-password input:hover:not(:disabled)) {
  border-color: var(--field-border-color-hover);
}

:deep(.p-password input:focus) {
  border-color: var(--field-border-color-focus);
  box-shadow: 0 0 0 2px rgb(59 130 246 / 0.1);
  outline: none;
}

:deep(.p-password input:disabled) {
  background: rgb(249 250 251);
  color: rgb(156 163 175);
  cursor: not-allowed;
}

:deep(.p-password.p-invalid input) {
  border-color: rgb(239 68 68);
  box-shadow: 0 0 0 2px rgb(239 68 68 / 0.1);
}

:deep(.p-password.p-invalid input:focus) {
  border-color: rgb(239 68 68);
  box-shadow: 0 0 0 2px rgb(239 68 68 / 0.2);
}

/* ===============================
   PASSWORD TOGGLE BUTTON
   =============================== */

:deep(.p-password .p-password-toggle) {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: rgb(107 114 128);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  transition: color 0.2s ease;
}

:deep(.p-password .p-password-toggle:hover) {
  color: rgb(59 130 246);
}

:deep(.p-password .p-password-toggle:focus) {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 1px;
}

/* ===============================
   PASSWORD PANEL (FEEDBACK)
   =============================== */

:deep(.p-password-panel) {
  padding: 0.75rem;
  background: white;
  border: 1px solid rgb(229 231 235);
  border-radius: var(--field-border-radius, 6px);
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  margin-top: 0.25rem;
}

:deep(.p-password-meter) {
  margin-bottom: 0.5rem;
}

:deep(.p-password-meter .p-password-strength) {
  height: 4px;
  border-radius: 2px;
  transition: all 0.3s ease;
}

:deep(.p-password-meter .p-password-strength.weak) {
  background: rgb(239 68 68);
}

:deep(.p-password-meter .p-password-strength.medium) {
  background: rgb(245 158 11);
}

:deep(.p-password-meter .p-password-strength.strong) {
  background: rgb(16 185 129);
}

:deep(.p-password-info) {
  color: rgb(75 85 99);
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

/* ===============================
   RESPONSIVE DESIGN
   =============================== */

@media (max-width: 640px) {
  :deep(.p-password input) {
    font-size: 16px; /* Prevent zoom on iOS */
  }

  .password-field__requirements {
    padding: 0.5rem;
  }

  .password-field__requirement-list {
    gap: 0.25rem;
  }
}

/* ===============================
   ACCESSIBILITY ENHANCEMENTS
   =============================== */

:deep(.p-password input:focus-visible) {
  outline: 2px solid rgb(59 130 246);
  outline-offset: 2px;
}

@media (prefers-reduced-motion: reduce) {
  :deep(.p-password input),
  .password-field__strength-fill,
  .password-field__requirement {
    transition: none;
  }
}

/* ===============================
   DARK MODE SUPPORT
   =============================== */

@media (prefers-color-scheme: dark) {
  :deep(.p-password input) {
    background: rgb(31 41 55);
    color: rgb(243 244 246);
    border-color: rgb(75 85 99);
  }

  :deep(.p-password input::placeholder) {
    color: rgb(107 114 128);
  }

  :deep(.p-password input:disabled) {
    background: rgb(17 24 39);
    color: rgb(75 85 99);
  }

  :deep(.p-password .p-password-toggle) {
    color: rgb(156 163 175);
  }

  :deep(.p-password .p-password-toggle:hover) {
    color: rgb(147 197 253);
  }

  .password-field__requirements {
    background: rgb(31 41 55);
    border-color: rgb(75 85 99);
  }

  .password-field__requirement {
    color: rgb(156 163 175);
  }

  .password-field__strength-bar {
    background: rgb(75 85 99);
  }

  :deep(.p-password-panel) {
    background: rgb(31 41 55);
    border-color: rgb(75 85 99);
  }

  :deep(.p-password-info) {
    color: rgb(156 163 175);
  }
}

/* ===============================
   HIGH CONTRAST MODE
   =============================== */

@media (prefers-contrast: high) {
  :deep(.p-password input) {
    border-width: 2px;
  }

  .password-field__strength-fill {
    border: 1px solid rgb(0 0 0);
  }

  .password-field__requirement {
    font-weight: 600;
  }
}

/* ===============================
   PRINT STYLES
   =============================== */

@media print {
  .password-field__strength-meter,
  .password-field__requirements,
  :deep(.p-password .p-password-toggle) {
    display: none;
  }

  :deep(.p-password input) {
    border: 1px solid rgb(0 0 0) !important;
  }
}
</style>
