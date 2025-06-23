<!-- SelectField.vue - FIXED VERSION -->
<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import Dropdown from 'primevue/dropdown'
import MultiSelect from 'primevue/multiselect'
import type { FormFieldProps } from '../../types/form'
import Logger from '../../utils/Logger'
import BaseField from './BaseField.vue'
import type { IFilter } from './interfaces/IFieldInterfaces'
import { useDynamicData } from '~/composables/useDynamicData'

// Interfaces
interface SuggestionItem {
  [key: string]: any
  id?: string | number
  name?: string
  code?: string
  status?: string
}

interface ApiConfig {
  moduleApi: string
  uriApi: string
}

// ✅ FIXED: Props interface with correct onUpdate type
interface SelectFieldProps extends Omit<FormFieldProps<unknown>, 'value' | 'onUpdate'> {
  value?: unknown
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
  onUpdate: (value: unknown) => void // ✅ FIXED: Required function, not optional

  // Props adicionales específicas del componente
  multiple?: boolean
  maxSelectedLabels?: number
  debounceTimeMs?: number
  apiConfig?: ApiConfig
  filtersBase?: any[]
  suggestions?: any[]
  loadOnOpen?: boolean
  initialQuery?: string
  dependentField?: string
  minQueryLength?: number
  maxItems?: number
  showClear?: boolean
  filterable?: boolean

  // Soporte para objApi de ReportViewer
  objApi?: {
    moduleApi: string
    uriApi: string
  }

  // Soporte para kwArgs de ReportViewer
  kwArgs?: {
    filtersBase?: any[]
    dependentField?: string
    debounceTimeMs?: number
    maxSelectedLabels?: number
    [key: string]: any
  }
}

const props = withDefaults(defineProps<SelectFieldProps>(), {
  multiple: false,
  maxSelectedLabels: 3,
  debounceTimeMs: 300,
  filtersBase: () => [],
  suggestions: () => [],
  loadOnOpen: true,
  initialQuery: '',
  minQueryLength: 0,
  maxItems: 50,
  showClear: true,
  filterable: true,
  readonly: false,
  required: false
})

// Emits compatibles con el sistema de formularios
const emit = defineEmits<{
  'update:modelValue': [value: unknown]
  'blur': []
  'focus': []
  'clear': []
  'change': [value: unknown]
  'error': [error: Error]
}>()

const { loadDynamicData } = useDynamicData()

// Estado interno
const instance = ref()
const isLoading = ref(false)
const internalSuggestions = ref<SuggestionItem[]>([])
const shouldReopen = ref(true)
const internalValue = ref<unknown>(null)

// Computed para obtener configuración API consolidada
const effectiveApiConfig = computed((): ApiConfig | null => {
  Logger.log('🔧 Computing effectiveApiConfig:')
  Logger.log('- props.apiConfig:', props.apiConfig)
  Logger.log('- props.objApi:', props.objApi)
  Logger.log('- props.field?.objApi:', props.field?.objApi)

  // Prioridad: apiConfig > objApi > field.objApi
  if (props.apiConfig?.moduleApi && props.apiConfig?.uriApi) {
    Logger.log('✅ Using props.apiConfig')
    return props.apiConfig
  }

  if (props.objApi?.moduleApi && props.objApi?.uriApi) {
    Logger.log('✅ Using props.objApi')
    return {
      moduleApi: props.objApi.moduleApi,
      uriApi: props.objApi.uriApi
    }
  }

  // ✅ AGREGAR: Verificar si viene en field.objApi
  if (props.field?.objApi?.moduleApi && props.field?.objApi?.uriApi) {
    Logger.log('✅ Using props.field.objApi')
    return {
      moduleApi: props.field.objApi.moduleApi,
      uriApi: props.field.objApi.uriApi
    }
  }

  Logger.log('❌ No valid API config found')
  return null
})

// Computed para obtener parámetros efectivos
const effectiveParams = computed(() => {
  const baseParams = {
    multiple: props.multiple,
    maxSelectedLabels: props.maxSelectedLabels,
    debounceTimeMs: props.debounceTimeMs,
    filtersBase: props.filtersBase,
    dependentField: props.dependentField,
    minQueryLength: props.minQueryLength,
    maxItems: props.maxItems
  }

  // Merge con kwArgs si existen
  if (props.kwArgs) {
    return {
      ...baseParams,
      multiple: props.kwArgs.multiple ?? baseParams.multiple,
      maxSelectedLabels: props.kwArgs.maxSelectedLabels ?? baseParams.maxSelectedLabels,
      debounceTimeMs: props.kwArgs.debounceTimeMs ?? baseParams.debounceTimeMs,
      filtersBase: props.kwArgs.filtersBase ?? baseParams.filtersBase,
      dependentField: props.kwArgs.dependentField ?? baseParams.dependentField,
      minQueryLength: props.kwArgs.minQueryLength ?? baseParams.minQueryLength,
      maxItems: props.kwArgs.maxItems ?? baseParams.maxItems
    }
  }

  return baseParams
})

// Computed para determinar si es múltiple
const isMultiple = computed(() => {
  // Prioridad: effectiveParams > field.type > field.multiple > props.multiple
  if (effectiveParams.value.multiple !== undefined) {
    return effectiveParams.value.multiple
  }

  if (props.field?.type === 'multiselect') {
    return true
  }

  if (props.field?.type === 'select') {
    return false
  }

  return props.field?.multiple || props.multiple || false
})

// Watch del valor prop para sincronizar
watch(() => props.value, (newValue) => {
  if (isMultiple.value) {
    internalValue.value = Array.isArray(newValue) ? newValue : (newValue ? [newValue] : [])
  }
  else {
    internalValue.value = Array.isArray(newValue) ? newValue[0] || null : newValue
  }
}, { immediate: true })

// Computed para el valor actual (compatible con BaseField)
const currentValue = computed({
  get() {
    return internalValue.value
  },
  set(newValue: unknown) {
    internalValue.value = newValue
    handleValueUpdate(newValue)
  }
})

// Computed para opciones disponibles
const availableOptions = computed(() => {
  const options: SuggestionItem[] = [...internalSuggestions.value]

  // Agregar sugerencias externas
  if (props.suggestions?.length) {
    props.suggestions.forEach((item) => {
      if (!options.some(o => o.id === item.id)) {
        options.push(item)
      }
    })
  }

  // Agregar elementos seleccionados para asegurar que estén disponibles
  if (isMultiple.value && Array.isArray(currentValue.value)) {
    currentValue.value.forEach((selected) => {
      if (selected && typeof selected === 'object' && 'id' in selected) {
        if (!options.some(o => o.id === selected.id)) {
          options.push(selected as SuggestionItem)
        }
      }
    })
  }
  else if (!isMultiple.value && currentValue.value && typeof currentValue.value === 'object') {
    const selected = currentValue.value as SuggestionItem
    if (selected.id && !options.some(o => o.id === selected.id)) {
      options.push(selected)
    }
  }

  return options
})

// Computed para el placeholder
const placeholder = computed(() => {
  if (props.loading || isLoading.value) {
    return 'Loading...'
  }

  if (props.field?.ui?.placeholder || props.field?.placeholder) {
    return props.field.ui?.placeholder || props.field.placeholder
  }

  return isMultiple.value ? 'Select options' : 'Select an option'
})

// Computed para clases del componente
const componentClasses = computed(() => {
  const classes = ['select-field__component']

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

// Computed para determinar si está deshabilitado
const isDisabled = computed(() =>
  props.disabled || props.field?.ui?.readonly || props.readonly
)

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
  onUpdate: props.onUpdate
}))

// Funciones de filtrado
function parseDependentField(): { name: string, filterKeyValue: string } | null {
  const dependentField = effectiveParams.value.dependentField
  if (!dependentField) { return null }

  try {
    // Si ya es un objeto, devolverlo directamente
    if (typeof dependentField === 'object') {
      return dependentField as { name: string, filterKeyValue: string }
    }

    // Si es string, intentar parsearlo como JSON
    if (typeof dependentField === 'string') {
      return JSON.parse(dependentField)
    }

    return null
  }
  catch (error) {
    Logger.warn('Error parsing dependentField:', error)
    return null
  }
}

function buildFilters(query: string): IFilter[] {
  const filters: IFilter[] = [...effectiveParams.value.filtersBase]

  // Agregar filtro dependiente si existe
  const dependentField = parseDependentField()
  if (dependentField && currentValue.value && typeof currentValue.value === 'object') {
    const dependentValue = (currentValue.value as any)[dependentField.name]
    const filterValue = typeof dependentValue === 'object' ? dependentValue?.id : dependentValue

    if (filterValue) {
      filters.push({
        key: dependentField.filterKeyValue,
        operator: 'EQUALS',
        value: filterValue,
        logicalOperation: 'AND'
      })
    }
  }

  // Agregar filtros de búsqueda
  if (query && query.length >= effectiveParams.value.minQueryLength) {
    filters.push(
      { key: 'code', operator: 'LIKE', value: query, logicalOperation: 'OR' },
      { key: 'name', operator: 'LIKE', value: query, logicalOperation: 'OR' }
    )
  }

  return filters
}

// Función de búsqueda
async function performSearch(query: string = ''): Promise<void> {
  const apiConfig = effectiveApiConfig.value

  if (!apiConfig?.moduleApi || !apiConfig?.uriApi) {
    Logger.warn('No API configuration available, skipping search')
    return
  }

  try {
    isLoading.value = true
    const filters = buildFilters(query)

    Logger.info('Performing search:', {
      query,
      filters,
      apiConfig
    })

    const results = await loadDynamicData(
      query,
      apiConfig.moduleApi,
      apiConfig.uriApi,
      filters
    )

    if (results && Array.isArray(results)) {
      internalSuggestions.value = results
      Logger.info('Search results:', results.length)
    }
    else {
      internalSuggestions.value = []
    }
  }
  catch (error) {
    Logger.error('Error loading data:', error)
    emit('error', error as Error)
    internalSuggestions.value = []
  }
  finally {
    isLoading.value = false
  }
}

// Búsqueda con debounce - Unified for both single and multi
const debouncedSearch = useDebounceFn(
  async (event: any) => {
    const query = event.query?.trim() ?? event.value?.trim() ?? ''

    Logger.info('🔍 Search triggered:', {
      query,
      minLength: effectiveParams.value.minQueryLength,
      isMultiple: isMultiple.value
    })

    // Unified search behavior - respects minQueryLength for both
    if (!query || query.length < effectiveParams.value.minQueryLength) {
      if (props.loadOnOpen) {
        await performSearch(props.initialQuery)
      }
      return
    }

    await performSearch(query)
  },
  effectiveParams.value.debounceTimeMs,
  { maxWait: 2000 }
)

// Event handlers compatibles con BaseField
function handleValueUpdate(value: unknown) {
  props.onUpdate(value)

  if (value !== props.value) {
    emit('update:modelValue', value)
  }

  emit('change', value)
}

function handleItemSelect(event: any): void {
  if (isMultiple.value) {
    // Para multiselect, manejar la lógica de toggle
    const currentValues = Array.isArray(currentValue.value) ? [...currentValue.value] : []
    const newItem = event.value

    const existingIndex = currentValues.findIndex(
      item => (typeof item === 'object' ? item.id : item) === (typeof newItem === 'object' ? newItem.id : newItem)
    )

    if (existingIndex !== -1) {
      currentValues.splice(existingIndex, 1)
    }
    else {
      if (effectiveParams.value.maxItems && currentValues.length >= effectiveParams.value.maxItems) {
        Logger.warn(`Maximum items limit reached: ${effectiveParams.value.maxItems}`)
        return
      }
      currentValues.push(newItem)
    }

    currentValue.value = currentValues
    shouldReopen.value = true
  }
  else {
    currentValue.value = event.value
  }
}

// ✅ SIMPLIFIED: Standard focus and blur handlers
function _handleFocus() {
  if (availableOptions.value.length === 0 && props.loadOnOpen) {
    performSearch(props.initialQuery)
  }
  emit('focus')
}

function _handleBlur() {
  emit('blur')
}

function handleHide(): void {
  if (shouldReopen.value && isMultiple.value) {
    nextTick(() => {
      instance.value?.show?.()
      shouldReopen.value = false
    })
  }
}

function handleShow(): void {
  if (availableOptions.value.length === 0 && props.loadOnOpen) {
    performSearch(props.initialQuery)
  }
}

function handleClear() {
  if (isMultiple.value) {
    currentValue.value = []
  }
  else {
    currentValue.value = null
  }
  emit('clear')
}

// Standard display functions
function getDisplayLabel(item: SuggestionItem): string {
  if (!item) { return 'No item' }

  if (item.code) {
    return item.code
  }

  if (item.name && item.name.includes(' - ')) {
    return item.name.split(' - ')[0]
  }

  return item.name || item.id?.toString() || 'No name'
}

function getChipLabel(item: any): string {
  // Para multiselect, item puede ser el ID si se está usando itemValue
  if (typeof item === 'string' || typeof item === 'number') {
    const fullObject = availableOptions.value.find((option: any) =>
      option.id === item
    )
    return fullObject ? getDisplayLabel(fullObject) : `ID: ${item}`
  }

  return getDisplayLabel(item)
}

// ✅ NEW: Helper to get option by ID for single select display
function getOptionById(id: any): SuggestionItem | null {
  if (!id) { return null }
  return availableOptions.value.find(option => option.id === id) || null
}

function removeChip(itemToRemove: any): void {
  if (!isMultiple.value || !Array.isArray(currentValue.value)) { return }

  const updated = currentValue.value.filter((item: any) => {
    const itemId = typeof item === 'object' ? item.id : item
    const removeId = typeof itemToRemove === 'object' ? itemToRemove.id : itemToRemove
    return itemId !== removeId
  })

  currentValue.value = updated
}

// Lifecycle
onMounted(() => {
  if (props.loadOnOpen && availableOptions.value.length === 0 && effectiveApiConfig.value) {
    performSearch(props.initialQuery)
  }
})

// Watchers
watch(() => effectiveApiConfig.value, (newConfig) => {
  if (newConfig && props.loadOnOpen) {
    performSearch(props.initialQuery)
  }
}, { immediate: true, deep: true })

// Expose functions for BaseField compatibility
defineExpose({
  focus: () => instance.value?.focus?.(),
  blur: () => instance.value?.blur?.(),
  clear: handleClear,
  performSearch,
  getDisplayLabel,
  instance
})
</script>

<template>
  <!-- ✅ USANDO BaseField COMO DateField -->
  <BaseField v-bind="baseFieldProps">
    <template #input="{ fieldId, onUpdate: onFieldUpdate, onBlur: onFieldBlur, onFocus: onFieldFocus }">
      <!-- Single Select - Dropdown with Search (like MultiSelect but single) -->
      <Dropdown
        v-if="!isMultiple"
        :id="fieldId"
        ref="instance"
        v-model="currentValue"
        :options="availableOptions"
        option-label="name"
        option-value="id"
        :placeholder="placeholder"
        :disabled="isDisabled || isLoading"
        :loading="isLoading || props.loading"
        :class="componentClasses"
        :style="fieldStyle"
        :filter="true"
        filter-placeholder="Search..."
        :show-clear="props.showClear"
        :reset-filter-on-hide="false"
        @change="onFieldUpdate"
        @focus="onFieldFocus"
        @blur="onFieldBlur"
        @show="handleShow"
        @hide="handleHide"
        @filter="debouncedSearch"
      >
        <template #option="{ option }">
          <div class="dropdown-option">
            <div class="flex align-items-center gap-2">
              <!-- Main content -->
              <div class="flex-1">
                <div class="font-medium">
                  {{ getDisplayLabel(option) }}
                </div>
                <div v-if="option.description" class="text-xs text-gray-500 mt-1">
                  {{ option.description }}
                </div>
              </div>
            </div>
          </div>
        </template>

        <template #value="{ value }">
          <div v-if="value !== null && value !== undefined" class="dropdown-selected">
            <span>{{ getDisplayLabel(getOptionById(value)) }}</span>
          </div>
          <span v-else class="dropdown-placeholder">
            {{ placeholder }}
          </span>
        </template>

        <template #empty>
          <div class="p-dropdown-empty-message">
            <div class="flex flex-column align-items-center gap-2 py-3">
              <i v-if="isLoading" class="pi pi-spin pi-spinner text-xl" />
              <i v-else class="pi pi-search text-xl text-gray-400" />

              <span v-if="isLoading" class="text-sm text-gray-600">
                Searching...
              </span>
              <span v-else class="text-sm text-gray-600">
                No results found
              </span>
            </div>
          </div>
        </template>

        <template #loader>
          <div class="p-dropdown-loader">
            <i class="pi pi-spin pi-spinner" />
          </div>
        </template>
      </Dropdown>

      <!-- Multiple Select - MultiSelect -->
      <MultiSelect
        v-else
        :id="fieldId"
        ref="instance"
        v-model="currentValue"
        :class="componentClasses"
        :style="fieldStyle"
        :options="availableOptions"
        option-label="name"
        option-value="id"
        :placeholder="placeholder"
        :disabled="isDisabled || isLoading"
        :filter="props.filterable"
        :loading="isLoading || props.loading"
        :max-selected-labels="effectiveParams.maxSelectedLabels"
        display="comma"
        @filter="debouncedSearch"
        @change="onFieldUpdate"
        @before-show="handleShow"
        @focus="onFieldFocus"
        @blur="onFieldBlur"
      >
        <template #value="{ value: selectedValue }">
          <div v-if="selectedValue && selectedValue.length > 0" class="flex align-items-center flex-wrap gap-1">
            <!-- Chips visibles -->
            <div
              v-for="(item, index) in selectedValue.slice(0, effectiveParams.maxSelectedLabels)"
              :key="`chip-${index}`"
              class="p-multiselect-token"
            >
              <span class="p-multiselect-token-label">
                {{ getChipLabel(item) }}
              </span>
              <i
                class="p-multiselect-token-icon pi pi-times"
                @click.stop="removeChip(item)"
              />
            </div>

            <!-- Contador de elementos adicionales -->
            <div
              v-if="selectedValue.length > effectiveParams.maxSelectedLabels"
              class="p-multiselect-token"
            >
              +{{ selectedValue.length - effectiveParams.maxSelectedLabels }}
            </div>
          </div>
          <span v-else class="dropdown-placeholder">
            {{ placeholder }}
          </span>
        </template>

        <template #option="{ option }">
          <div class="dropdown-option">
            {{ getDisplayLabel(option) }}
          </div>
        </template>

        <template #empty>
          <div class="p-multiselect-empty-message">
            <span v-if="isLoading">
              Loading...
            </span>
            <span v-else>
              No results found.
            </span>
          </div>
        </template>

        <template #loader>
          <div class="p-multiselect-loader">
            <i class="pi pi-spin pi-spinner" />
          </div>
        </template>
      </MultiSelect>
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
