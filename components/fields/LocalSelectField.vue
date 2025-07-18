<!-- LocalSelectField.vue - REFACTORED TO USE BaseField -->
<script setup lang="ts">
import { computed, ref, watch,  inject, onMounted, onUnmounted  } from 'vue'
import Dropdown from 'primevue/dropdown'
import type { FormFieldProps, ValidationError } from '../../types/form'
import BaseField from './BaseField.vue'

// Props que extienden FormFieldProps para ser compatible con el sistema
interface LocalSelectFieldProps extends Omit<FormFieldProps<any>, 'value' | 'onUpdate'> {
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
  'update:value': [value: any] // 🎯 AGREGAR ESTE EMIT IMPORTANTE
  'blur': []
  'focus': []
  'clear': []
  'change': [value: any]
}>()

interface OptionItem {
  id: string | number;
  name: string;
  [key: string]: any;
}

interface SelectionModal {
  open: (params: {
    items: OptionItem[]
    selectedIds?: Array<string | number>
    title?: string
    multiple?: boolean
  }) => Promise<Array<string | number>>
}

const selectionModal = inject('selectionModal') as SelectionModal
const focusManager = inject('focusManager') as { setFocusedComponent: (c: string | null) => void }

if (!selectionModal) {
  console.warn('[LocalSelectField] ❌ selectionModal no disponible.')
}

const internalValue = ref(props.value)

const wrapper = ref<HTMLElement | null>(null)
const hasFocus = ref(false)

const handleKeyDown = (e: KeyboardEvent) => {
  if (e.ctrlKey && e.key === 'F2' && hasFocus.value) {
    e.preventDefault()
    openSelectionModal()
  }
}

const setupFocusListeners = () => {
  if (!wrapper.value) return

  wrapper.value.addEventListener('focusin', () => {
    hasFocus.value = true
    focusManager?.setFocusedComponent('local-select-field')
    window.addEventListener('keydown', handleKeyDown)
  })

  wrapper.value.addEventListener('focusout', () => {
    hasFocus.value = false
    focusManager?.setFocusedComponent(null)
    window.removeEventListener('keydown', handleKeyDown)
  })
}

onMounted(setupFocusListeners)
onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
  focusManager?.setFocusedComponent(null)
})


const openSelectionModal = async () => {
  if (!selectionModal) return

 const options = normalizedOptions.value.map(opt => ({
  ...opt,
  id: opt.value,
  name: opt.name
}))
  const selectedValue = internalValue.value
  const selectedIds = selectedValue !== null && selectedValue !== undefined ? [selectedValue] : []

  const newSelectedIds = await selectionModal.open({
    items: options,
    selectedIds,
    title: 'Select',
    multiple: false
  })

  if (newSelectedIds.length > 0) {
    internalValue.value = newSelectedIds[0]
  }
}



// 🔄 WATCH BIDIRECCIONAL - CRÍTICO PARA SINCRONIZACIÓN
watch(() => props.value, (newValue) => {
  if (newValue !== internalValue.value) {
    Logger.log('🔄 Props value changed:', { from: internalValue.value, to: newValue })
    internalValue.value = newValue
  }
}, { immediate: true })

// 🔄 WATCH DEL VALOR INTERNO - SINCRONIZAR HACIA ARRIBA
watch(internalValue, (newValue) => {
  if (newValue !== props.value) {
    Logger.log('🔄 Internal value changed:', { from: props.value, to: newValue })
    // Emitir TODOS los eventos necesarios
    emit('update:modelValue', newValue)
    emit('update:value', newValue)
    emit('change', newValue)

    // Llamar onUpdate si existe
    if (props.onUpdate) {
      props.onUpdate(newValue)
    }
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
  const options = normalizeOptions(props.field?.options || [])
  Logger.log('🎯 Normalized options:', options)
  return options
})

const isDisabled = computed(() =>
  props.disabled || props.field?.ui?.readonly || props.readonly || false
)

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

// Computed para clases del componente - Mejorado con manejo estricto de errores
const componentClasses = computed(() => {
  const classes = ['select-field__component', 'local-select-dropdown']

  try {
    let hasRealError = false

    if (props.error) {
      if (Array.isArray(props.error)) {
        hasRealError = props.error.length > 0 && props.error.some(error =>
          error && (typeof error === 'string' ? (error as string).trim() : true)
        )
      }
      else if (typeof props.error === 'string') {
        hasRealError = (props.error as string).trim().length > 0
      }
      else if (props.error && typeof props.error === 'object') {
        hasRealError = true
      }
    }

    if (hasRealError) {
      classes.push('p-invalid')
    }
  }
  catch (error) {
    Logger.warn('🔍 [COMPONENT CLASSES] Error handling props.error:', error)
  }

  if (props.field?.ui?.className) {
    try {
      const uiClasses = Array.isArray(props.field.ui.className)
        ? props.field.ui.className
        : [props.field.ui.className]
      classes.push(...uiClasses)
    }
    catch (error) {
      Logger.warn('🔍 [COMPONENT CLASSES] Error handling ui.className:', error)
    }
  }

  return classes
})

// Computed para el estilo del campo
const fieldStyle = computed(() => props.field?.ui?.style || {})

// Computed para propiedades de accesibilidad
const accessibilityProps = computed(() => {
  let hasRealError = false
  try {
    if (props.error) {
      if (Array.isArray(props.error)) {
        hasRealError = props.error.length > 0 && props.error.some(error =>
          error && (typeof error === 'string' ? (error as string).trim() : true)
        )
      }
      else if (typeof props.error === 'string') {
        hasRealError = (props.error as string).trim().length > 0
      }
      else if (props.error && typeof props.error === 'object') {
        hasRealError = true
      }
    }
  }
  catch (error) {
    Logger.warn('🔍 [ACCESSIBILITY PROPS] Error handling props.error:', error)
    hasRealError = false
  }

  return {
    'aria-label': `Select for ${props.field?.label || props.label || 'option'}`,
    'aria-describedby': props.field?.helpText ? `${props.id}-help` : undefined,
    'aria-invalid': hasRealError,
    'aria-required': props.required
  }
})

// 🎯 FUNCIÓN PARA MANEJAR CAMBIOS DEL DROPDOWN
function handleDropdownChange(newValue: any) {
  Logger.log('🎯 Dropdown changed to:', newValue)
  // Actualizar directamente el valor interno
  // El watch se encargará de emitir los eventos
  internalValue.value = newValue
}

// Computed para el BaseField props - SIN onUpdate para evitar conflictos
const baseFieldProps = computed(() => ({
  field: props.field,
  value: internalValue.value, // 🎯 USAR EL VALOR INTERNO
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
  style: fieldStyle.value
  // 🎯 NO INCLUIR onUpdate aquí para evitar conflictos
}))

function _handleBlur() {
  emit('blur')
}

function _handleFocus() {
  emit('focus')
}

function handleClear() {
  Logger.log('🧹 Clearing value')
  internalValue.value = null
  emit('clear')
}

// Función para obtener el display label
function getDisplayLabel(value: any): string {
  if (value === null || value === undefined) {
    return ''
  }

  const option = normalizedOptions.value.find(opt => opt.value === value)
  return option?.name || String(value)
}

// 🔍 FUNCIÓN DE DEBUG PARA VERIFICAR EL VALOR ACTUAL
function getCurrentValue() {
  Logger.log('🔍 Current value check:', {
    internalValue: internalValue.value,
    propsValue: props.value,
    displayLabel: getDisplayLabel(internalValue.value)
  })
  return internalValue.value
}

// Expose functions for BaseField compatibility
defineExpose({
  focus: () => {},
  blur: () => {},
  clear: handleClear,
  getDisplayLabel,
  getCurrentValue, // 🔍 EXPONER PARA DEBUG
  // 🎯 EXPONER EL VALOR INTERNO PARA VERIFICACIÓN
  get value() { return internalValue.value },
  get internalValue() { return internalValue.value }
})
</script>

<template>
  <!-- ✅ USANDO BaseField COMO DateField Y SelectField -->
  <div ref="wrapper">
  <BaseField
    v-if="field?.dataType === 'localselect'"
    v-bind="baseFieldProps"
  >
    <template #input="{ fieldId }">
      <Dropdown
        :id="fieldId"
        v-model="internalValue"
        :options="normalizedOptions"
        option-label="name"
        option-value="value"
        :placeholder="placeholder"
        :class="componentClasses"
        :style="fieldStyle"
        :filter="normalizedOptions.length > 10"
        :show-clear="clearable && !isDisabled && internalValue !== null && internalValue !== undefined"
        :disabled="isDisabled"
        :loading="props.loading || false"
        filter-placeholder="Search..."
        v-bind="accessibilityProps"
        @update:model-value="handleDropdownChange"
        @blur="_handleBlur"
        @focus="_handleFocus"
        @clear="handleClear"
      >
        <!-- Option Template -->
        <template #option="{ option }">
          <div class="dropdown-option">
            <span>{{ option.name }}</span>
          </div>
        </template>

        <!-- Selected Value Template -->
        <template #value="{ value }">
          <div v-if="value !== null && value !== undefined" class="dropdown-selected">
            <span class="dropdown-selected--value">{{ getDisplayLabel(value) }}</span>
          </div>
          <span v-else class="dropdown-placeholder">
            {{ placeholder }}
          </span>
        </template>

        <!-- Empty State -->
        <template #empty>
          <div class="dropdown-empty">
            <div class="dropdown-empty__content">
              <i class="pi pi-search text-lg text-gray-400" />
              <span class="dropdown-empty__text">No options available.</span>
            </div>
          </div>
        </template>

        <!-- Loading State -->
        <template #loader>
          <div class="dropdown-loader">
            <i class="pi pi-spin pi-spinner" />
            <span>Loading options...</span>
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

  <!-- 🔍 DEBUG: MOSTRAR VALOR ACTUAL (REMOVER EN PRODUCCIÓN) -->
  <div v-if="false" style="margin-top: 8px; padding: 8px; background: #f0f0f0; border-radius: 4px; font-size: 12px;">
    <strong>Debug Info:</strong><br>
    Internal Value: {{ internalValue }}<br>
    Props Value: {{ props.value }}<br>
    Display Label: {{ getDisplayLabel(internalValue) }}
  </div>
  </div>
</template>

<style scoped>
/* ===============================================================================
   🎯 ENHANCED LOCAL SELECT FIELD STYLES - PROFESSIONAL UI/UX
   =============================================================================== */

/* 🎨 CSS VARIABLES - CONSISTENT WITH GLOBAL DESIGN SYSTEM */
:root {
  --enhanced-form-field-height: 2.75rem;
  --enhanced-form-field-height-mobile: 3rem;
  --enhanced-form-border-width: 2px;
  --enhanced-form-border-radius: 0.5rem;
  --enhanced-form-transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  --enhanced-form-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --enhanced-form-shadow-lg: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --enhanced-form-focus-border: #3b82f6;
  --enhanced-form-focus-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  --enhanced-form-error-border: #ef4444;
  --enhanced-form-error-shadow: 0 0 0 1px #ef4444;
  --enhanced-form-error-shadow-hover: 0 0 0 3px rgba(239, 68, 68, 0.1);
  --enhanced-form-dropdown-z: 1000;
  --enhanced-form-font-size: 1rem;
}

/* 🎯 DROPDOWN COMPONENT STYLES - PROFESSIONAL DESIGN */
:deep(.p-dropdown) {
  width: 100% !important;
  height: var(--enhanced-form-field-height) !important;
  min-height: var(--enhanced-form-field-height) !important;
  background: white !important;
  border: var(--enhanced-form-border-width) solid #d1d5db !important;
  border-radius: var(--enhanced-form-border-radius) !important;
  transition: var(--enhanced-form-transition) !important;
  box-shadow: var(--enhanced-form-shadow) !important;
  overflow: hidden !important;
  max-width: none !important;
  min-width: 0 !important;
  box-sizing: border-box !important;
}

:deep(.p-dropdown:hover:not(.p-disabled):not(.p-dropdown-opened):not(.p-focus)) {
  border-color: #9ca3af !important;
  transform: translateY(-1px) !important;
  box-shadow: var(--enhanced-form-shadow-lg) !important;
}

:deep(.p-dropdown.p-focus:not(.p-dropdown-opened)) {
  border-color: var(--enhanced-form-focus-border) !important;
  box-shadow: var(--enhanced-form-focus-shadow) !important;
  transform: translateY(-1px) !important;
}

:deep(.p-dropdown.p-dropdown-opened) {
  border-bottom-left-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
  border-bottom-color: transparent !important;
  border-color: var(--enhanced-form-focus-border) !important;
  box-shadow: var(--enhanced-form-focus-shadow) !important;
  z-index: calc(var(--enhanced-form-dropdown-z) + 1) !important;
  transform: translateY(-1px) !important;
}

:deep(.p-dropdown .p-dropdown-label),
:deep(.p-dropdown .p-dropdown-trigger),
:deep(.p-dropdown .p-inputtext) {
  border: none !important;
  box-shadow: none !important;
  outline: none !important;
}

:deep(.p-dropdown .p-dropdown-label) {
  height: calc(var(--enhanced-form-field-height) - 4px) !important;
  padding: 0 1rem !important;
  line-height: normal !important;
  display: flex !important;
  align-items: center !important;
  font-size: var(--enhanced-form-font-size) !important;
  color: #374151 !important;
  background: transparent !important;
}

:deep(.p-dropdown .p-dropdown-label.p-placeholder) {
  color: #9ca3af !important;
  font-style: italic !important;
}

:deep(.p-dropdown .p-dropdown-trigger) {
  height: calc(var(--enhanced-form-field-height) - 4px) !important;
  width: var(--enhanced-form-field-height) !important;
  background: transparent !important;
  border-left: 1px solid #d1d5db !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  transition: var(--enhanced-form-transition) !important;
}

:deep(.p-dropdown .p-dropdown-trigger:hover) {
  background: #f9fafb !important;
}

:deep(.p-dropdown .p-dropdown-trigger .p-icon) {
  color: #3b82f6 !important;
}

:deep(.p-dropdown.p-invalid) {
  border-color: var(--enhanced-form-error-border) !important;
  box-shadow: var(--enhanced-form-error-shadow) !important;
  animation: fieldErrorShake 0.5s ease-in-out !important;
}

:deep(.p-dropdown.p-invalid:focus),
:deep(.p-dropdown.p-invalid.p-focus) {
  border-color: var(--enhanced-form-error-border) !important;
  box-shadow: var(--enhanced-form-error-shadow-hover) !important;
}

/* 🎯 DROPDOWN PANELS */
:deep(.p-dropdown-panel) {
  border: 2px solid #e2e8f0 !important;
  border-top: none !important;
  border-radius: 0 0 var(--enhanced-form-border-radius) var(--enhanced-form-border-radius) !important;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1), 0 4px 10px rgba(0, 0, 0, 0.05) !important;
  z-index: calc(var(--enhanced-form-dropdown-z) + 1) !important;
  overflow: hidden !important;
  background: white !important;
  min-width: 100% !important;
  backdrop-filter: blur(12px) !important;
}

/* 🎯 DROPDOWN ITEMS */
:deep(.p-dropdown-panel .p-dropdown-items) {
  padding: 0.25rem 0 !important;
}

:deep(.p-dropdown-panel .p-dropdown-item) {
  padding: 0.5rem 1rem !important;
  margin: 0 !important;
  min-height: auto !important;
  font-size: 0.875rem !important;
  border: none !important;
  border-radius: 0 !important;
  transition: background-color 0.15s ease !important;
}

:deep(.p-dropdown-panel .p-dropdown-item:hover) {
  background: #f3f4f6 !important;
}

:deep(.p-dropdown-panel .p-dropdown-item.p-highlight) {
  background: #eff6ff !important;
  color: #1e40af !important;
}

/* 🎯 FILTER INPUT STYLING */
:deep(.p-dropdown-filter) {
  width: 100% !important;
  padding: 0.75rem 1rem !important;
  border: 1px solid #e2e8f0 !important;
  border-radius: 0.375rem !important;
  font-size: 0.875rem !important;
  transition: all 0.15s ease !important;
  background: white !important;
}

:deep(.p-dropdown-filter:focus) {
  outline: none !important;
  border-color: var(--enhanced-form-focus-border) !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
}

/* Dropdown styling */
.dropdown-option,
.dropdown-selected,
.dropdown-placeholder,
.dropdown-empty,
.dropdown-loader {
  font-size: var(--enhanced-form-font-size);
  color: #374151;
}

.dropdown-selected--value {
  color: #374151;
  font-weight: 500;
}

.dropdown-placeholder {
  color: #9ca3af;
  font-style: italic;
}

.dropdown-empty {
  padding: 2rem 1rem;
  text-align: center;
}

.dropdown-empty__content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.dropdown-empty__text {
  font-size: 0.875rem;
  color: #6b7280;
  margin-top: 0.5rem;
}

.dropdown-loader {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  color: #6b7280;
  font-size: 0.875rem;
}

/* 🎯 ACCESSIBILITY IMPROVEMENTS */
:deep(.p-dropdown[aria-expanded="true"]) {
  border-color: var(--enhanced-form-focus-border) !important;
  box-shadow: var(--enhanced-form-focus-shadow) !important;
}

/* 🎯 FOCUS VISIBLE STATES */
:deep(.p-dropdown:focus-visible) {
  outline: 2px solid var(--enhanced-form-focus-border) !important;
  outline-offset: 2px !important;
}

/* 🎯 DISABLED STATE */
:deep(.p-dropdown.p-disabled) {
  opacity: 0.6 !important;
  cursor: not-allowed !important;
  background: #f9fafb !important;
}

/* 🎯 LOADING STATE ENHANCEMENTS */
:deep(.p-dropdown.p-loading) {
  pointer-events: none !important;
}

:deep(.p-dropdown.p-loading .p-dropdown-trigger) {
  opacity: 0.6 !important;
}

/* 🎬 ANIMATIONS */
@keyframes fieldErrorShake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-2px); }
  20%, 40%, 60%, 80% { transform: translateX(2px); }
}

/* 📱 RESPONSIVE DESIGN */
@media (max-width: 768px) {
  :deep(.p-dropdown) {
    height: var(--enhanced-form-field-height-mobile) !important;
    min-height: var(--enhanced-form-field-height-mobile) !important;
  }

  :deep(.p-dropdown .p-dropdown-label) {
    height: calc(var(--enhanced-form-field-height-mobile) - 4px) !important;
    padding: 0 0.75rem !important;
  }

  :deep(.p-dropdown .p-dropdown-trigger) {
    height: calc(var(--enhanced-form-field-height-mobile) - 4px) !important;
    width: var(--enhanced-form-field-height-mobile) !important;
  }
}

/* 🎯 HIGH CONTRAST MODE SUPPORT */
@media (prefers-contrast: high) {
  :deep(.p-dropdown) {
    border-width: 3px !important;
    border-color: #000 !important;
  }
}

/* 🎯 REDUCED MOTION SUPPORT */
@media (prefers-reduced-motion: reduce) {
  :deep(.p-dropdown) {
    transition: none !important;
    animation: none !important;
  }
}

/* 🎯 PRINT STYLES */
@media print {
  :deep(.p-dropdown-panel) {
    display: none !important;
  }
}

/* 🎯 DARK MODE SUPPORT (if needed) */
@media (prefers-color-scheme: dark) {
  :root {
    --enhanced-form-focus-border: #60a5fa;
    --enhanced-form-focus-shadow: 0 0 0 3px rgba(96, 165, 250, 0.1);
  }
}
</style>
