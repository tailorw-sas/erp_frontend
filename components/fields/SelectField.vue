<!-- components/fields/SelectField.vue -->
<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import Dropdown from 'primevue/dropdown'
import MultiSelect from 'primevue/multiselect'
import type { FormFieldProps, SelectOption, ValidationError } from '../../types/form'
import BaseField from './BaseField.vue'

// Tipos específicos para componentes PrimeVue
interface PrimeVueComponentRef {
  $el?: HTMLElement
  show?: (isFocus?: boolean) => void
  hide?: (isFocus?: boolean) => void
}

interface DropdownRef extends PrimeVueComponentRef {}
interface MultiSelectRef extends PrimeVueComponentRef {}

// Props específicas para SelectField que extienden las props base
interface SelectFieldProps extends Omit<FormFieldProps<unknown>, 'onUpdate'> {
  // Props específicas del componente PrimeVue
  filter?: boolean
  showClear?: boolean
  emptyFilterMessage?: string
  emptyMessage?: string
  maxSelectedLabels?: number
  selectionLimit?: number
  display?: 'comma' | 'chip'
  appendTo?: string | HTMLElement
  scrollHeight?: string
  virtualScrollerOptions?: Record<string, unknown>
  autoOptionFocus?: boolean
  autoFilterFocus?: boolean
  filterBy?: string
  filterFields?: string[]
  filterMatchMode?: 'startsWith' | 'contains' | 'endsWith'
  optionDisabled?: string | ((option: SelectOption<unknown>) => boolean)
  optionGroupLabel?: string
  optionGroupChildren?: string
  // Props personalizadas adicionales
  optionLabel?: string | ((option: SelectOption<unknown>) => string)
  optionValue?: string | ((option: SelectOption<unknown>) => unknown)
}

const props = withDefaults(defineProps<SelectFieldProps>(), {
  filter: true,
  showClear: true,
  emptyFilterMessage: 'No results found',
  emptyMessage: 'No options available',
  maxSelectedLabels: 3,
  display: 'chip',
  // scrollHeight: '200px',
  autoOptionFocus: true,
  autoFilterFocus: true,
  filterMatchMode: 'contains',
  optionLabel: 'label',
  optionValue: 'value'
})

// Emits
const emit = defineEmits<{
  'update:value': [value: unknown]
  'blur': []
  'focus': []
  'clear': []
  'change': [value: unknown]
  'filter': [event: { originalEvent: Event, value: string }]
  'show': []
  'hide': []
}>()

// Template refs con tipos correctos
const dropdownRef = ref<DropdownRef | null>(null)
const multiselectRef = ref<MultiSelectRef | null>(null)

// Computed properties
const isMultiple = computed(() =>
  props.field.multiple || props.field.type === 'multiselect'
)

const selectOptions = computed((): SelectOption<unknown>[] => {
  // Priorizar opciones del field sobre las del componente
  if (props.field.options && props.field.options.length > 0) {
    return props.field.options
  }

  return []
})

// Función helper para obtener el valor de optionLabel
function getOptionLabel(option: SelectOption<unknown>): string {
  const labelKey = props.optionLabel

  if (typeof labelKey === 'function') {
    return labelKey(option)
  }

  if (typeof labelKey === 'string' && labelKey in option) {
    const value = option[labelKey as keyof SelectOption<unknown>]
    return String(value ?? '')
  }

  return String(option.label ?? option.value ?? '')
}

// Función helper para obtener el valor de optionValue
function getOptionValue(option: SelectOption<unknown>): unknown {
  const valueKey = props.optionValue

  if (typeof valueKey === 'function') {
    return valueKey(option)
  }

  if (typeof valueKey === 'string' && valueKey in option) {
    return option[valueKey as keyof SelectOption<unknown>]
  }

  return option.value
}

const selectValue = computed({
  get: () => props.value,
  set: (newValue: unknown) => {
    handleUpdate(newValue)
  }
})

const selectClasses = computed(() => {
  const classes = ['select-field__control']

  // Usar la nueva estructura de errores
  if (props.error && props.error.length > 0) {
    classes.push('p-invalid')
  }

  if (props.loading) {
    classes.push('select-field__control--loading')
  }

  // Aplicar clases del tema/configuración
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

const isDisabled = computed(() =>
  props.disabled || props.field.ui?.readonly || props.loading
)

const placeholder = computed(() => {
  if (props.loading) {
    return 'Loading...'
  }

  // Usar el placeholder de la configuración UI
  if (props.field.ui?.placeholder) {
    return props.field.ui.placeholder
  }

  if (isMultiple.value) {
    return 'Select items'
  }

  return 'Select an option'
})

const hasValue = computed(() => {
  if (isMultiple.value) {
    return Array.isArray(props.value) && props.value.length > 0
  }
  return props.value != null && props.value !== ''
})

const showClearButton = computed(() =>
  props.showClear && hasValue.value && (props.field.ui?.clearable ?? true)
)

// Configuración de estilo desde el field
const fieldStyle = computed(() => {
  return props.field.ui?.style || {}
})

// Methods
function handleUpdate(value: unknown) {
  emit('update:value', value)
  emit('change', value)
}

function handleBlur() {
  emit('blur')
}

function handleFocus() {
  emit('focus')
}

function handleClear() {
  const clearedValue = isMultiple.value ? [] : null
  emit('update:value', clearedValue)
  emit('clear')
}

function handleFilter(event: { originalEvent: Event, value: string }) {
  emit('filter', event)
}

function handleShow() {
  emit('show')
}

function handleHide() {
  emit('hide')
}

// Funciones helper para encontrar opciones de forma segura
function findOptionByValue(value: unknown): SelectOption<unknown> | undefined {
  return selectOptions.value.find(opt => getOptionValue(opt) === value)
}

// Función para formatear errores de validación (prefijo _ para indicar que puede no usarse)
function _getErrorMessages(): string[] {
  if (!props.error || props.error.length === 0) { return [] }
  return props.error.map((err: ValidationError) => err.message)
}

// Expose methods for parent components
async function focus() {
  await nextTick()
  if (isMultiple.value && multiselectRef.value?.$el) {
    const input = multiselectRef.value.$el.querySelector('input')
    input?.focus()
  }
  else if (dropdownRef.value?.$el) {
    const input = dropdownRef.value.$el.querySelector('input')
    input?.focus()
  }
}

function blur() {
  if (isMultiple.value && multiselectRef.value?.$el) {
    const input = multiselectRef.value.$el.querySelector('input')
    input?.blur()
  }
  else if (dropdownRef.value?.$el) {
    const input = dropdownRef.value.$el.querySelector('input')
    input?.blur()
  }
}

function show() {
  if (isMultiple.value) {
    multiselectRef.value?.show?.()
  }
  else {
    dropdownRef.value?.show?.()
  }
}

function hide() {
  if (isMultiple.value) {
    multiselectRef.value?.hide?.()
  }
  else {
    dropdownRef.value?.hide?.()
  }
}

defineExpose({
  focus,
  blur,
  show,
  hide,
  dropdownRef,
  multiselectRef
})

// Watch para cargar datos asincrónicos
watch(() => props.field.asyncDataSource, async (newDataSource) => {
  if (newDataSource?.endpoint) {
    // TODO: Implementar carga de datos asíncrona
    // Solo mostrar en desarrollo sin usar console.log directo
    if (process.env.NODE_ENV === 'development') {
      // eslint-disable-next-line no-console
      console.log('Async data source configured:', newDataSource)
    }
  }
}, { immediate: true })
</script>

<template>
  <BaseField
    v-bind="$props"
    :style="fieldStyle"
    @update:value="handleUpdate"
    @blur="handleBlur"
    @focus="handleFocus"
    @clear="handleClear"
  >
    <template #input="{ fieldId, onBlur, onFocus }">
      <!-- Single Select Dropdown -->
      <Dropdown
        v-if="!isMultiple"
        :id="fieldId"
        ref="dropdownRef"
        v-model="selectValue"
        :class="selectClasses"
        :options="selectOptions"
        :option-label="optionLabel"
        :option-value="optionValue"
        :option-disabled="optionDisabled"
        :option-group-label="optionGroupLabel"
        :option-group-children="optionGroupChildren"
        :placeholder="placeholder"
        :disabled="isDisabled"
        :loading="loading"
        :filter="filter && field.searchable !== false"
        :filter-by="filterBy"
        :filter-fields="filterFields"
        :filter-match-mode="filterMatchMode"
        :show-clear="showClearButton"
        :empty-filter-message="emptyFilterMessage"
        :empty-message="emptyMessage"
        :append-to="appendTo"
        :scroll-height="scrollHeight"
        :virtual-scroller-options="virtualScrollerOptions"
        :auto-option-focus="autoOptionFocus"
        :auto-filter-focus="autoFilterFocus"
        @blur="onBlur"
        @focus="onFocus"
        @change="handleUpdate"
        @filter="handleFilter"
        @show="handleShow"
        @hide="handleHide"
      >
        <!-- Option template -->
        <template #option="{ option, index }">
          <slot
            name="option"
            :option="option"
            :index="index"
            :is-selected="selectValue === getOptionValue(option)"
          >
            <div class="select-field__option">
              <i
                v-if="option.icon"
                :class="option.icon"
                class="select-field__option-icon"
              />
              <div class="select-field__option-content">
                <div class="select-field__option-label">
                  {{ getOptionLabel(option) }}
                </div>
                <div
                  v-if="option.description"
                  class="select-field__option-description"
                >
                  {{ option.description }}
                </div>
              </div>
              <div
                v-if="option.disabled"
                class="select-field__option-disabled-indicator"
              >
                <i class="pi pi-lock" />
              </div>
            </div>
          </slot>
        </template>

        <!-- Selected value template -->
        <template #value="{ value, placeholder: ph }">
          <slot
            name="value"
            :value="value"
            :option="findOptionByValue(value)"
            :placeholder="ph"
          >
            <div v-if="value" class="select-field__selected-value">
              <i
                v-if="findOptionByValue(value)?.icon"
                :class="findOptionByValue(value)?.icon"
                class="select-field__selected-icon"
              />
              {{ getOptionLabel(findOptionByValue(value) || { label: value, value }) }}
            </div>
            <span v-else class="select-field__placeholder">
              {{ ph }}
            </span>
          </slot>
        </template>

        <!-- Empty template -->
        <template #empty>
          <slot name="empty">
            <div class="select-field__empty">
              <i class="pi pi-info-circle" />
              {{ emptyMessage }}
            </div>
          </slot>
        </template>

        <!-- Loading template -->
        <template #loader>
          <slot name="loader">
            <div class="select-field__loader">
              <i class="pi pi-spinner pi-spin" />
              Loading options...
            </div>
          </slot>
        </template>
      </Dropdown>

      <!-- Multiple Select -->
      <MultiSelect
        v-else
        :id="fieldId"
        ref="multiselectRef"
        v-model="selectValue"
        :class="selectClasses"
        :options="selectOptions"
        :option-label="optionLabel"
        :option-value="optionValue"
        :option-disabled="optionDisabled"
        :option-group-label="optionGroupLabel"
        :option-group-children="optionGroupChildren"
        :placeholder="placeholder"
        :disabled="isDisabled"
        :loading="loading"
        :filter="filter && field.searchable !== false"
        :filter-by="filterBy"
        :filter-fields="filterFields"
        :filter-match-mode="filterMatchMode"
        :show-clear="showClearButton"
        :max-selected-labels="maxSelectedLabels"
        :selection-limit="selectionLimit"
        :display="display"
        :empty-filter-message="emptyFilterMessage"
        :empty-message="emptyMessage"
        :append-to="appendTo"
        :scroll-height="scrollHeight"
        :virtual-scroller-options="virtualScrollerOptions"
        :auto-option-focus="autoOptionFocus"
        :auto-filter-focus="autoFilterFocus"
        @blur="onBlur"
        @focus="onFocus"
        @change="handleUpdate"
        @filter="handleFilter"
        @show="handleShow"
        @hide="handleHide"
      >
        <!-- Option template -->
        <template #option="{ option, index }">
          <slot
            name="option"
            :option="option"
            :index="index"
            :is-selected="Array.isArray(selectValue) && selectValue.includes(getOptionValue(option))"
          >
            <div class="select-field__option">
              <i
                v-if="option.icon"
                :class="option.icon"
                class="select-field__option-icon"
              />
              <div class="select-field__option-content">
                <div class="select-field__option-label">
                  {{ getOptionLabel(option) }}
                </div>
                <div
                  v-if="option.description"
                  class="select-field__option-description"
                >
                  {{ option.description }}
                </div>
              </div>
              <div
                v-if="option.disabled"
                class="select-field__option-disabled-indicator"
              >
                <i class="pi pi-lock" />
              </div>
            </div>
          </slot>
        </template>

        <!-- Chip template for selected items -->
        <template #chip="{ value: chipValue }">
          <slot
            name="chip"
            :value="chipValue"
            :option="findOptionByValue(chipValue)"
          >
            <div class="select-field__chip">
              <i
                v-if="findOptionByValue(chipValue)?.icon"
                :class="findOptionByValue(chipValue)?.icon"
                class="select-field__chip-icon"
              />
              {{ getOptionLabel(findOptionByValue(chipValue) || { label: chipValue, value: chipValue }) }}
            </div>
          </slot>
        </template>

        <!-- Empty template -->
        <template #empty>
          <slot name="empty">
            <div class="select-field__empty">
              <i class="pi pi-info-circle" />
              {{ emptyMessage }}
            </div>
          </slot>
        </template>

        <!-- Loading template -->
        <template #loader>
          <slot name="loader">
            <div class="select-field__loader">
              <i class="pi pi-spinner pi-spin" />
              Loading options...
            </div>
          </slot>
        </template>
      </MultiSelect>
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
/* Estilos usando tokens del tema */
.select-field__control {
  width: 100%;
  min-height: var(--field-height, 2.5rem);
  border: var(--field-border-width, 1px) solid var(--field-border-color, rgb(209 213 219));
  border-radius: var(--field-border-radius, 0.375rem);
  background: var(--field-background, white);
  transition: var(--field-transition, all 0.2s ease-in-out);
}

.select-field__control:hover:not(.p-disabled) {
  border-color: var(--field-border-color-hover, rgb(156 163 175));
}

.select-field__control:not(.p-disabled).p-focus {
  border-color: var(--field-border-color-focus, rgb(59 130 246));
  box-shadow: var(--field-focus-shadow, 0 0 0 2px rgb(59 130 246 / 0.1));
}

.select-field__control.p-invalid {
  border-color: var(--field-error-color, rgb(239 68 68));
  box-shadow: var(--field-error-shadow, 0 0 0 2px rgb(239 68 68 / 0.1));
}

.select-field__control.p-invalid:not(.p-disabled).p-focus {
  border-color: var(--field-error-color, rgb(239 68 68));
  box-shadow: var(--field-error-focus-shadow, 0 0 0 2px rgb(239 68 68 / 0.2));
}

.select-field__control--loading {
  position: relative;
}

.select-field__control--loading::after {
  content: '';
  position: absolute;
  right: 2.5rem;
  top: 50%;
  transform: translateY(-50%);
  width: 1rem;
  height: 1rem;
  border: 2px solid var(--field-loading-color, rgb(229 231 235));
  border-top-color: var(--field-primary-color, rgb(59 130 246));
  border-radius: 50%;
  animation: select-field-spin 1s linear infinite;
}

@keyframes select-field-spin {
  to {
    transform: translateY(-50%) rotate(360deg);
  }
}

.select-field__option {
  display: flex;
  align-items: center;
  gap: var(--field-spacing-sm, 0.5rem);
  padding: var(--field-spacing-sm, 0.5rem) var(--field-spacing-md, 0.75rem);
  width: 100%;
}

.select-field__option-icon {
  flex-shrink: 0;
  color: var(--field-text-secondary, rgb(107 114 128));
}

.select-field__option-content {
  flex: 1;
  min-width: 0;
}

.select-field__option-label {
  font-size: var(--field-font-size, 0.875rem);
  color: var(--field-text-color, rgb(17 24 39));
  line-height: var(--field-line-height, 1.5);
  word-break: break-word;
}

.select-field__option-description {
  font-size: var(--field-font-size-sm, 0.75rem);
  color: var(--field-text-secondary, rgb(107 114 128));
  line-height: var(--field-line-height-tight, 1.4);
  margin-top: 0.125rem;
}

.select-field__option-disabled-indicator {
  flex-shrink: 0;
  color: var(--field-disabled-color, rgb(156 163 175));
}

.select-field__option:hover .select-field__option-label {
  color: var(--field-primary-color, rgb(59 130 246));
}

.select-field__option[aria-selected="true"] .select-field__option-label {
  color: var(--field-primary-color, rgb(59 130 246));
  font-weight: var(--field-font-weight-medium, 500);
}

.select-field__option[aria-disabled="true"] {
  opacity: 0.6;
  cursor: not-allowed;
}

.select-field__option[aria-disabled="true"]:hover .select-field__option-label {
  color: var(--field-text-color, rgb(17 24 39));
}

.select-field__selected-value {
  display: flex;
  align-items: center;
  gap: var(--field-spacing-xs, 0.25rem);
  font-size: var(--field-font-size, 0.875rem);
  color: var(--field-text-color, rgb(17 24 39));
  line-height: var(--field-line-height, 1.5);
}

.select-field__selected-icon {
  flex-shrink: 0;
  color: var(--field-text-secondary, rgb(107 114 128));
}

.select-field__placeholder {
  color: var(--field-placeholder-color, rgb(156 163 175));
  font-size: var(--field-font-size, 0.875rem);
}

.select-field__chip {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.125rem 0.5rem;
  background: var(--field-primary-color, rgb(59 130 246));
  color: white;
  font-size: var(--field-font-size-xs, 0.75rem);
  font-weight: var(--field-font-weight-medium, 500);
  border-radius: var(--field-border-radius-full, 9999px);
  line-height: var(--field-line-height-tight, 1.4);
  max-width: 100%;
}

.select-field__chip-icon {
  flex-shrink: 0;
  font-size: 0.625rem;
}

.select-field__empty,
.select-field__loader {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--field-spacing-xs, 0.25rem);
  padding: var(--field-spacing-lg, 1rem);
  color: var(--field-text-secondary, rgb(107 114 128));
  font-size: var(--field-font-size-sm, 0.75rem);
  text-align: center;
}

.select-field__loader {
  color: var(--field-primary-color, rgb(59 130 246));
}

/* Panel styles usando deep selectors */
:deep(.p-dropdown-panel),
:deep(.p-multiselect-panel) {
  border: 1px solid var(--field-border-color, rgb(229 231 235));
  border-radius: var(--field-border-radius, 0.375rem);
  box-shadow: var(--field-shadow-lg, 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1));
  background: var(--field-background, white);
  z-index: 1000;
}

:deep(.p-dropdown-items),
:deep(.p-multiselect-items) {
  padding: var(--field-spacing-xs, 0.25rem) 0;
}

:deep(.p-dropdown-item),
:deep(.p-multiselect-item) {
  margin: 0 var(--field-spacing-xs, 0.25rem);
  border-radius: calc(var(--field-border-radius, 0.375rem) - 2px);
  transition: var(--field-transition, all 0.2s ease-in-out);
}

:deep(.p-dropdown-item:hover),
:deep(.p-multiselect-item:hover) {
  background: var(--field-hover-color, rgb(249 250 251));
}

:deep(.p-dropdown-item.p-highlight),
:deep(.p-multiselect-item.p-highlight) {
  background: var(--field-selected-color, rgb(239 246 255));
  color: var(--field-primary-color, rgb(59 130 246));
}

:deep(.p-dropdown-filter-container),
:deep(.p-multiselect-filter-container) {
  padding: var(--field-spacing-sm, 0.5rem);
  border-bottom: 1px solid var(--field-border-color, rgb(229 231 235));
}

:deep(.p-dropdown-filter),
:deep(.p-multiselect-filter) {
  width: 100%;
  padding: var(--field-spacing-sm, 0.5rem);
  border: 1px solid var(--field-border-color, rgb(209 213 219));
  border-radius: calc(var(--field-border-radius, 0.375rem) - 2px);
  font-size: var(--field-font-size-sm, 0.75rem);
}

:deep(.p-dropdown-filter:focus),
:deep(.p-multiselect-filter:focus) {
  outline: none;
  border-color: var(--field-primary-color, rgb(59 130 246));
  box-shadow: var(--field-focus-shadow, 0 0 0 1px rgb(59 130 246));
}

:deep(.p-multiselect-label-container) {
  padding: 0.25rem 0.5rem;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.25rem;
  min-height: calc(var(--field-height, 2.5rem) - 2px);
}

:deep(.p-multiselect-token) {
  background: var(--field-primary-color, rgb(59 130 246));
  color: white;
  border-radius: var(--field-border-radius-full, 9999px);
  padding: 0.125rem 0.5rem;
  font-size: var(--field-font-size-xs, 0.75rem);
  font-weight: var(--field-font-weight-medium, 500);
  display: flex;
  align-items: center;
  gap: 0.25rem;
  max-width: 100%;
}

:deep(.p-multiselect-token-label) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.p-multiselect-token-icon) {
  cursor: pointer;
  transition: var(--field-transition, all 0.2s ease-in-out);
}

:deep(.p-multiselect-token-icon:hover) {
  background: var(--field-primary-hover, rgb(37 99 235));
  border-radius: 50%;
}

/* Responsive design */
@media (max-width: 640px) {
  .select-field__option {
    padding: var(--field-spacing-md, 0.75rem);
  }

  .select-field__option-description {
    display: none;
  }

  :deep(.p-dropdown-panel),
  :deep(.p-multiselect-panel) {
    max-height: 60vh;
  }
}

/* Accessibility */
:deep(.p-dropdown:focus-visible),
:deep(.p-multiselect:focus-visible) {
  outline: 2px solid var(--field-primary-color, rgb(59 130 246));
  outline-offset: 2px;
}

@media (prefers-reduced-motion: reduce) {
  .select-field__control,
  :deep(.p-dropdown-item),
  :deep(.p-multiselect-item),
  :deep(.p-multiselect-token-icon) {
    transition: none;
  }

  .select-field__control--loading::after {
    animation: none;
  }
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .select-field__option-label {
    color: var(--field-text-color-dark, rgb(243 244 246));
  }

  .select-field__selected-value {
    color: var(--field-text-color-dark, rgb(243 244 246));
  }

  :deep(.p-dropdown-panel),
  :deep(.p-multiselect-panel) {
    background: var(--field-background-dark, rgb(31 41 55));
    border-color: var(--field-border-color-dark, rgb(75 85 99));
  }

  :deep(.p-dropdown-item:hover),
  :deep(.p-multiselect-item:hover) {
    background: var(--field-hover-color-dark, rgb(55 65 81));
  }

  :deep(.p-dropdown-filter),
  :deep(.p-multiselect-filter) {
    background: var(--field-background-dark, rgb(17 24 39));
    border-color: var(--field-border-color-dark, rgb(75 85 99));
    color: var(--field-text-color-dark, rgb(243 244 246));
  }
}
</style>
