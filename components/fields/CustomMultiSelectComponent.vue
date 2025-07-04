<!-- CustomMultiSelectComponent.vue -->
<script setup lang="ts">
import { computed, inject, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import Dropdown from 'primevue/dropdown'
import MultiSelect from 'primevue/multiselect'
import type { FormContext, FormFieldProps } from '../../types/form'
import Logger from '../../utils/Logger'
import BaseField from './BaseField.vue'
import type { IFilter } from './interfaces/IFieldInterfaces'
import { useDynamicData } from '~/composables/useDynamicData'

// ============================================================================
// INTERFACES & TYPES
// ============================================================================

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
  onUpdate?: (value: unknown) => void

  // Component-specific props
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

  // ReportViewer compatibility
  objApi?: {
    moduleApi: string
    uriApi: string
  }

  kwArgs?: {
    filtersBase?: any[]
    dependentField?: string
    filterKeyValue?: string
    debounceTimeMs?: number
    maxSelectedLabels?: number
    getParentValues?: () => Record<string, any>
    [key: string]: any
  }

  parentValues?: Record<string, any>
}

// ============================================================================
// COMPONENT SETUP
// ============================================================================

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

const emit = defineEmits<{
  'update:modelValue': [value: unknown]
  'update:value': [value: unknown]
  'blur': []
  'focus': []
  'clear': []
  'change': [value: unknown]
  'error': [error: Error]
}>()

const { loadDynamicData } = useDynamicData()

// ============================================================================
// REACTIVE STATE
// ============================================================================

const formContext = inject<FormContext>('formContext', {} as FormContext)
const instance = ref()
const isLoading = ref(false)
const internalSuggestions = ref<SuggestionItem[]>([])
const shouldReopen = ref(true)
const isInitialized = ref(false)
const lastParentValue = ref<any>(null)
const internalValue = ref<unknown>(null)
const hasInitialValueLoaded = ref(false)
const componentKey = ref(0)
const isDropdownOpen = ref(false)

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

function forceComponentUpdate() {
  componentKey.value++
  Logger.log('🔄 [FORCE UPDATE] Component key incremented:', {
    fieldName: props.field?.name || props.name,
    componentKey: componentKey.value,
    currentValue: internalValue.value
  })
}

// ============================================================================
// COMPUTED PROPERTIES
// ============================================================================

const effectiveApiConfig = computed((): ApiConfig | null => {
  if (props.apiConfig?.moduleApi && props.apiConfig?.uriApi) {
    return props.apiConfig
  }

  if (props.objApi?.moduleApi && props.objApi?.uriApi) {
    return {
      moduleApi: props.objApi.moduleApi,
      uriApi: props.objApi.uriApi
    }
  }

  if (props.field?.objApi?.moduleApi && props.field?.objApi?.uriApi) {
    return {
      moduleApi: props.field.objApi.moduleApi,
      uriApi: props.field.objApi.uriApi
    }
  }

  return null
})

const effectiveParams = computed(() => {
  const baseParams = {
    multiple: props.multiple,
    maxSelectedLabels: props.maxSelectedLabels,
    debounceTimeMs: props.debounceTimeMs,
    filtersBase: props.filtersBase,
    dependentField: props.dependentField,
    filterKeyValue: '',
    minQueryLength: props.minQueryLength,
    maxItems: props.maxItems
  }

  if (props.kwArgs) {
    return {
      ...baseParams,
      multiple: props.kwArgs.multiple ?? baseParams.multiple,
      maxSelectedLabels: props.kwArgs.maxSelectedLabels ?? baseParams.maxSelectedLabels,
      debounceTimeMs: props.kwArgs.debounceTimeMs ?? baseParams.debounceTimeMs,
      filtersBase: props.kwArgs.filtersBase ?? baseParams.filtersBase,
      dependentField: props.kwArgs.dependentField ?? baseParams.dependentField,
      filterKeyValue: props.kwArgs.filterKeyValue ?? baseParams.filterKeyValue,
      minQueryLength: props.kwArgs.minQueryLength ?? baseParams.minQueryLength,
      maxItems: props.kwArgs.maxItems ?? baseParams.maxItems
    }
  }

  return baseParams
})

const isMultiple = computed(() => {
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

const availableOptions = computed(() => {
  const options: SuggestionItem[] = []

  if (internalSuggestions.value.length > 0) {
    internalSuggestions.value.forEach((item) => {
      if (!options.some(o => String(o.id) === String(item.id))) {
        options.push(item)
      }
    })
  }

  if (props.suggestions?.length) {
    props.suggestions.forEach((item) => {
      if (!options.some(o => String(o.id) === String(item.id))) {
        options.push(item)
      }
    })
  }

  const currentVal = internalValue.value
  if (currentVal !== null && currentVal !== undefined) {
    if (isMultiple.value && Array.isArray(currentVal)) {
      currentVal.forEach((selected) => {
        if (selected && typeof selected === 'object' && 'id' in selected) {
          if (!options.some(o => String(o.id) === String(selected.id))) {
            options.push(selected as SuggestionItem)
          }
        }
      })
    }
    else if (!isMultiple.value) {
      let targetId: string | number | null = null
      let targetOption: SuggestionItem | null = null

      if (typeof currentVal === 'object' && 'id' in currentVal) {
        targetOption = currentVal as SuggestionItem
        targetId = targetOption.id || null
      }
      else {
        targetId = currentVal as string | number
      }

      if (targetId !== null && targetId !== undefined) {
        const existingOption = options.find(o => String(o.id) === String(targetId))

        if (!existingOption) {
          if (targetOption) {
            options.push(targetOption)
          }
          else {
            const displayOption: SuggestionItem = {
              id: targetId,
              name: 'Loading...',
              code: String(targetId)
            }
            options.push(displayOption)
          }
        }
      }
    }
  }

  return options
})

// ✅ SOLUCION: Computed simplificado sin interferencias
const currentValue = computed({
  get() {
    return internalValue.value
  },
  set(newValue: unknown) {
    Logger.log('🔄 [CURRENT VALUE SET] Selection Update:', {
      fieldName: props.field?.name || props.name,
      oldValue: internalValue.value,
      newValue,
      isMultiple: isMultiple.value
    })

    // ✅ CRITICO: Actualización directa sin delays
    internalValue.value = newValue
    notifyValueChange(newValue)
  }
})

const dropdownKey = computed(() => `dropdown-${componentKey.value}`)
const multiselectKey = computed(() => `multiselect-${componentKey.value}`)

const placeholder = computed(() => {
  if (props.loading || isLoading.value) {
    return 'Loading...'
  }

  if (props.field?.ui?.placeholder || props.field?.placeholder) {
    return props.field.ui?.placeholder || props.field.placeholder
  }

  return isMultiple.value ? 'Select options' : 'Select an option'
})

const componentClasses = computed(() => {
  const classes = ['select-field__component']

  if (props.error && Array.isArray(props.error) && props.error.length > 0) {
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

const fieldStyle = computed(() => props.field?.ui?.style || {})

const isDisabled = computed(() =>
  props.disabled || props.field?.ui?.readonly || props.readonly
)

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
  onUpdate: (value: unknown) => {
    handleValueUpdate(value)
  }
}))

const dependentFieldValue = computed(() => {
  const dependentField = effectiveParams.value.dependentField
  if (!dependentField) { return null }

  try {
    const value = getCurrentFormValue(dependentField)
    return value
  }
  catch (error) {
    Logger.error('🔄 [DEPENDENCY COMPUTED] Error getting parent value:', error)
    return null
  }
})

function getCurrentFormValue(fieldName: string): any {
  if (props.kwArgs?.getParentValues && typeof props.kwArgs.getParentValues === 'function') {
    try {
      const parentValues = props.kwArgs.getParentValues()
      if (parentValues && typeof parentValues === 'object' && fieldName in parentValues) {
        return parentValues[fieldName]
      }
    }
    catch (error) {
      Logger.error('🔍 [GET FORM VALUE] Getter error:', error)
    }
  }

  if (props.parentValues && typeof props.parentValues === 'object' && fieldName in props.parentValues) {
    return props.parentValues[fieldName]
  }

  if (formContext?.state?.values && typeof formContext.state.values === 'object' && fieldName in formContext.state.values) {
    return formContext.state.values[fieldName]
  }

  return null
}

function buildFilters(query: string): IFilter[] {
  const filters: IFilter[] = []

  const baseFilters = effectiveParams.value.filtersBase.filter(filter =>
    filter
    && filter.key
    && filter.value !== null
    && filter.value !== undefined
    && filter.value !== ''
  )
  filters.push(...baseFilters)

  const dependentField = effectiveParams.value.dependentField
  const filterKeyValue = effectiveParams.value.filterKeyValue

  if (dependentField && filterKeyValue) {
    const parentValue = getCurrentFormValue(dependentField)

    if (parentValue !== null && parentValue !== undefined && parentValue !== '') {
      let filterValues: any[] = []

      if (Array.isArray(parentValue)) {
        filterValues = parentValue
          .map(v => (typeof v === 'object' && v !== null && 'id' in v) ? v.id : v)
          .filter(v => v !== null && v !== undefined && v !== '')
      }
      else {
        const singleValue = (typeof parentValue === 'object' && parentValue !== null && 'id' in parentValue)
          ? parentValue.id
          : parentValue

        if (singleValue !== null && singleValue !== undefined && singleValue !== '') {
          filterValues = [singleValue]
        }
      }

      if (filterValues.length > 0) {
        const existingIndex = filters.findIndex(f => f.key === filterKeyValue)
        if (existingIndex !== -1) {
          filters.splice(existingIndex, 1)
        }

        filters.push({
          key: filterKeyValue,
          operator: 'IN',
          value: filterValues,
          logicalOperation: 'AND'
        })
      }
    }
  }

  if (query && query.length >= effectiveParams.value.minQueryLength) {
    const searchFilters = [
      { key: 'code', operator: 'LIKE', value: query, logicalOperation: 'OR' },
      { key: 'name', operator: 'LIKE', value: query, logicalOperation: 'OR' }
    ]

    searchFilters.forEach((searchFilter) => {
      const exists = filters.find(f => f.key === searchFilter.key && f.operator === 'LIKE')
      if (!exists) {
        filters.push(searchFilter as IFilter)
      }
    })
  }

  return filters
}

async function performSearch(query: string = ''): Promise<void> {
  const apiConfig = effectiveApiConfig.value

  if (!apiConfig?.moduleApi || !apiConfig?.uriApi) {
    Logger.warn('🔍 [PERFORM SEARCH] No API configuration available')
    return
  }

  const dependentField = effectiveParams.value.dependentField
  const filterKeyValue = effectiveParams.value.filterKeyValue

  if (dependentField && filterKeyValue) {
    const parentValue = getCurrentFormValue(dependentField)
    const hasValidParentValue = parentValue !== null
      && parentValue !== undefined
      && parentValue !== ''
      && (!Array.isArray(parentValue) || parentValue.length > 0)

    if (!hasValidParentValue) {
      Logger.log('🔍 [PERFORM SEARCH] No valid parent value, clearing options')
      internalSuggestions.value = []
      return
    }
  }

  try {
    isLoading.value = true
    const filters = buildFilters(query)

    Logger.info('🔍 [PERFORM SEARCH] Executing search:', {
      query,
      filters,
      fieldName: props.field?.name || props.name
    })

    const results = await loadDynamicData(
      query,
      apiConfig.moduleApi,
      apiConfig.uriApi,
      filters
    )

    if (results && Array.isArray(results)) {
      internalSuggestions.value = results

      Logger.info('🔍 [PERFORM SEARCH] ✅ Loaded:', {
        count: results.length,
        fieldName: props.field?.name || props.name
      })
    }
    else {
      internalSuggestions.value = []
    }
  }
  catch (error) {
    Logger.error('🔍 [PERFORM SEARCH] Error:', error)
    emit('error', error as Error)
    internalSuggestions.value = []
  }
  finally {
    isLoading.value = false
  }
}

const debouncedSearch = useDebounceFn(
  async (event: any) => {
    const query = event.query?.trim() ?? ''

    Logger.info('🔍 [MODERN SEARCH] Search triggered:', {
      query,
      fieldName: props.field?.name || props.name
    })

    if (!query || query.length < effectiveParams.value.minQueryLength) {
      if (props.loadOnOpen) {
        await performSearch('')
      }
    }
    else {
      await performSearch(query)
    }
  },
  effectiveParams.value.debounceTimeMs,
  { maxWait: 2000 }
)

// ============================================================================
// EVENT HANDLERS
// ============================================================================

function notifyValueChange(value: unknown) {
  emit('update:modelValue', value)
  emit('update:value', value)
  emit('change', value)

  if (props.onUpdate && typeof props.onUpdate === 'function') {
    try {
      props.onUpdate(value)
    }
    catch (error) {
      Logger.error('🔥 [ERROR] props.onUpdate failed:', error)
    }
  }

  Logger.log('🔄 [VALUE CHANGE] Notified:', {
    fieldName: props.field?.name || props.name,
    value,
    type: typeof value
  })
}

function handleValueUpdate(value: unknown) {
  Logger.log('🔄 [HANDLE VALUE UPDATE]', {
    fieldName: props.field?.name || props.name,
    value,
    oldValue: internalValue.value
  })

  const oldValue = internalValue.value
  internalValue.value = value
  notifyValueChange(value)

  // CRITICAL: Force update with improved timing
  if (JSON.stringify(oldValue) !== JSON.stringify(value)) {
    // Immediate update
    forceComponentUpdate()

    // Additional update after options have time to stabilize
    nextTick(() => {
      setTimeout(() => {
        forceComponentUpdate()
      }, 50)
    })
  }
}

function handleFocus() {
  emit('focus')
}

function handleBlur() {
  emit('blur')
}

function handleShow(): void {
  isDropdownOpen.value = true

  if (availableOptions.value.length === 0 && props.loadOnOpen && !isLoading.value) {
    performSearch('')
  }
}

function handleHide(): void {
  isDropdownOpen.value = false

  if (shouldReopen.value && isMultiple.value) {
    nextTick(() => {
      instance.value?.show?.()
      shouldReopen.value = false
    })
  }
}

function handleClear() {
  const clearedValue = isMultiple.value ? [] : null

  Logger.log('🧹 [CLEAR]', {
    fieldName: props.field?.name || props.name,
    clearedValue
  })

  internalValue.value = clearedValue
  notifyValueChange(clearedValue)
  emit('clear')
}

// ============================================================================
// DISPLAY FUNCTIONS
// ============================================================================

function getDisplayLabel(item: SuggestionItem | any): string {
  if (!item) { return '' }

  if (item.name === 'Loading...') {
    return 'Loading...'
  }

  if (item.code && item.name && item.name !== 'Loading...') {
    return item.code
  }

  if (item.code) {
    return item.code
  }

  if (item.name && item.name.includes(' - ')) {
    return item.name.split(' - ')[0]
  }

  return item.name || String(item.id || '')
}

function getDropdownLabel(item: SuggestionItem): string {
  if (!item) { return '' }

  if (item.code && item.name && item.name !== 'Loading...') {
    return `${item.code} - ${item.name}`
  }

  if (item.code) {
    return item.code
  }

  if (item.name) {
    return item.name
  }

  return String(item.id || '')
}

// ✅ NUEVA: Función mejorada para chips basada en CustomMultiSelect
function getChipLabel(item: any): string {
  // Si item es un ID primitivo, buscar en opciones
  if (typeof item === 'string' || typeof item === 'number') {
    const fullObject = availableOptions.value.find(option =>
      String(option.id) === String(item)
    )

    if (fullObject) {
      // Si tiene code, usarlo
      if (fullObject.code) {
        return fullObject.code
      }

      // Si no tiene code pero el name tiene formato "CODIGO - NOMBRE"
      if (fullObject.name && fullObject.name.includes(' - ')) {
        return fullObject.name.split(' - ')[0]
      }

      // Fallback al name completo
      return fullObject.name || 'No name'
    }

    return `ID: ${String(item).substring(0, 8)}`
  }

  // Si item es un objeto, usar getDisplayLabel
  return getDisplayLabel(item)
}

function getOptionById(id: any): SuggestionItem | null {
  if (!id && id !== 0) { return null }

  return availableOptions.value.find((option) => {
    const exactMatch = option.id === id
    const stringMatch = String(option.id) === String(id)
    const numberMatch = Number(option.id) === Number(id)
      && !Number.isNaN(Number(option.id))
      && !Number.isNaN(Number(id))

    return exactMatch || stringMatch || numberMatch
  }) || null
}

function getSelectedDisplayValue(selectedValue: any): string {
  if (!selectedValue && selectedValue !== 0) {
    return ''
  }

  // Try to find the option in available options first
  const option = getOptionById(selectedValue)

  if (option) {
    return getDisplayLabel(option)
  }

  // If not found, just return a fallback without triggering searches
  // This prevents interference with user search interactions
  Logger.log('🔍 [GET SELECTED DISPLAY] Option not found, showing fallback:', {
    selectedValue,
    availableCount: availableOptions.value.length
  })

  return 'Loading...'
}

// ✅ NUEVA: Función removeChip mejorada
function removeChip(itemToRemove: any): void {
  if (!isMultiple.value || !Array.isArray(internalValue.value)) {
    return
  }

  const currentArray = internalValue.value as any[]
  const updated = currentArray.filter((item: any) => {
    // Comparar por ID si es objeto, por valor si es primitivo
    const itemId = typeof item === 'object' ? item.id : item
    const removeId = typeof itemToRemove === 'object' ? itemToRemove.id : itemToRemove
    return String(itemId) !== String(removeId)
  })

  Logger.log('🗑️ [REMOVE CHIP]', {
    fieldName: props.field?.name || props.name,
    itemToRemove,
    before: currentArray.length,
    after: updated.length
  })

  // ✅ CRITICO: Actualización limpia sin force updates
  internalValue.value = updated
  notifyValueChange(updated)
}

watch(() => props.value, (newValue) => {
  Logger.log('🔄 [PROP VALUE WATCH]', {
    fieldName: props.field?.name || props.name,
    newValue,
    currentInternal: internalValue.value
  })

  const oldValue = internalValue.value

  if (isMultiple.value) {
    internalValue.value = Array.isArray(newValue) ? newValue : (newValue ? [newValue] : [])
  }
  else {
    internalValue.value = Array.isArray(newValue) ? (newValue[0] || null) : newValue
  }

  hasInitialValueLoaded.value = true

  // CRITICAL: Enhanced force update for external value changes
  if (JSON.stringify(oldValue) !== JSON.stringify(internalValue.value)) {
    // Immediate update
    forceComponentUpdate()

    // Delayed update to ensure all options are available
    nextTick(() => {
      setTimeout(() => {
        forceComponentUpdate()
      }, 100)
    })
  }
}, { immediate: true })

watch(dependentFieldValue, async (newValue, oldValue) => {
  const dependentField = effectiveParams.value.dependentField

  if (!dependentField || !isInitialized.value) {
    return
  }

  const hasChanged = JSON.stringify(newValue) !== JSON.stringify(oldValue)

  if (hasChanged) {
    Logger.log('🔄 [DEPENDENCY WATCH] Parent changed:', {
      dependentField,
      newValue,
      oldValue
    })

    const clearedValue = isMultiple.value ? [] : null
    internalValue.value = clearedValue
    notifyValueChange(clearedValue)

    internalSuggestions.value = []

    const hasValidParentValue = newValue !== null
      && newValue !== undefined
      && newValue !== ''
      && (!Array.isArray(newValue) || newValue.length > 0)

    if (hasValidParentValue) {
      await nextTick()
      await performSearch('')
    }

    lastParentValue.value = newValue
  }
}, {
  immediate: false,
  deep: true,
  flush: 'post'
})

// ============================================================================
// WATCHERS
// ============================================================================

onMounted(async () => {
  Logger.log('🚀 [MOUNT]', {
    fieldName: props.field?.name || props.name,
    dependentField: effectiveParams.value.dependentField,
    hasApiConfig: !!effectiveApiConfig.value,
    currentValue: internalValue.value
  })

  await nextTick()
  isInitialized.value = true

  const dependentField = effectiveParams.value.dependentField

  if (!dependentField) {
    if (props.loadOnOpen && effectiveApiConfig.value) {
      await performSearch(props.initialQuery)
    }
  }
  else {
    const parentValue = getCurrentFormValue(dependentField)
    lastParentValue.value = parentValue

    const hasValidParentValue = parentValue !== null
      && parentValue !== undefined
      && parentValue !== ''
      && (!Array.isArray(parentValue) || parentValue.length > 0)

    if (hasValidParentValue && props.loadOnOpen && effectiveApiConfig.value) {
      await performSearch(props.initialQuery)
    }
  }
})

onUnmounted(() => {
  isInitialized.value = false
})

// ============================================================================
// EXPOSE
// ============================================================================

defineExpose({
  focus: () => instance.value?.focus?.(),
  blur: () => instance.value?.blur?.(),
  clear: handleClear,
  performSearch,
  getDisplayLabel,
  instance,
  getCurrentFormValue,
  dependentFieldValue: () => dependentFieldValue.value,
  effectiveParams: () => effectiveParams.value,
  componentKey,
  dropdownKey,
  multiselectKey
})
</script>

<template>
  <BaseField v-bind="baseFieldProps">
    <template #input="{ fieldId }">
      <!-- ======================================================================
           SINGLE SELECT DROPDOWN - SIN CAMBIOS
           ====================================================================== -->
      <Dropdown
        v-if="!isMultiple"
        :id="fieldId"
        :key="dropdownKey"
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
        filter-placeholder="Type to search..."
        :show-clear="false"
        :reset-filter-on-hide="false"
        :empty-filter-message="isLoading ? 'Loading...' : 'No results found'"
        :empty-message="isLoading ? 'Loading...' : 'No options available'"
        @focus="handleFocus"
        @blur="handleBlur"
        @show="handleShow"
        @hide="handleHide"
        @clear="handleClear"
        @filter="debouncedSearch"
      >
        <!-- Option Template -->
        <template #option="{ option }">
          <div class="dropdown-option">
            <span v-if="option.name === 'Loading...'" class="dropdown-option--loading">
              <i class="pi pi-spin pi-spinner text-xs mr-2" />
              Loading...
            </span>
            <span v-else>
              {{ getDropdownLabel(option) }}
            </span>
          </div>
        </template>

        <!-- Selected Value Template -->
        <template #value="{ value: selectedValue, placeholder: dropdownPlaceholder }">
          <div v-if="selectedValue !== null && selectedValue !== undefined" class="dropdown-selected">
            <span v-if="getSelectedDisplayValue(selectedValue) === 'Loading...'" class="dropdown-selected--loading">
              <i class="pi pi-spin pi-spinner text-xs mr-2" />
              Loading...
            </span>
            <span v-else class="dropdown-selected--value">
              {{ getSelectedDisplayValue(selectedValue) }}
            </span>
          </div>
          <span v-else class="dropdown-placeholder">
            {{ dropdownPlaceholder }}
          </span>
        </template>

        <!-- Empty State with Search Context -->
        <template #empty>
          <div class="dropdown-empty">
            <div class="dropdown-empty__content">
              <i v-if="isLoading" class="pi pi-spin pi-spinner text-lg text-gray-400" />
              <i v-else class="pi pi-search text-lg text-gray-400" />

              <span v-if="isLoading" class="dropdown-empty__text">
                Searching...
              </span>
              <span v-else class="dropdown-empty__text">
                Start typing to search...
              </span>
            </div>
          </div>
        </template>
        <!-- Loading State -->
        <template #loader>
          <div class="dropdown-loader">
            <i class="pi pi-spin pi-spinner" />
          </div>
        </template>
      </Dropdown>

      <!-- ======================================================================
           MULTI SELECT - SOLUCION: USAR display="chip" SIN template #value
           ====================================================================== -->
      <MultiSelect
        v-else
        :id="fieldId"
        :key="multiselectKey"
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
        display="chip"
        :show-toggle-all="false"
        :empty-filter-message="isLoading ? 'Loading...' : 'No results found'"
        :empty-message="isLoading ? 'Loading...' : 'No options available'"
        @filter="debouncedSearch"
        @before-show="handleShow"
        @focus="handleFocus"
        @blur="handleBlur"
        @clear="handleClear"
      >
        <!-- ✅ SOLUCION: Solo template #chip para eliminación, SIN template #value -->
        <template #chip="{ value: chipValue }">
          <div class="p-multiselect-token">
            <span class="p-multiselect-token-label">{{ getChipLabel(chipValue) }}</span>
            <span
              class="p-multiselect-token-icon pi pi-times"
              @click.stop="removeChip(chipValue)"
            />
          </div>
        </template>

        <!-- Templates que NO interfieren -->
        <template #option="{ option }">
          <div class="multiselect-option">
            <span v-if="option.name === 'Loading...'" class="multiselect-option--loading">
              <i class="pi pi-spin pi-spinner text-xs mr-2" />
              Loading...
            </span>
            <span v-else>
              {{ getDropdownLabel(option) }}
            </span>
          </div>
        </template>

        <template #empty>
          <div class="multiselect-empty">
            <div class="multiselect-empty__content">
              <i v-if="isLoading" class="pi pi-spin pi-spinner text-lg text-gray-400" />
              <i v-else class="pi pi-search text-lg text-gray-400" />

              <span v-if="isLoading" class="multiselect-empty__text">
                Searching...
              </span>
              <span v-else class="multiselect-empty__text">
                No results found
              </span>
            </div>
          </div>
        </template>

        <template #loader>
          <div class="multiselect-loader">
            <i class="pi pi-spin pi-spinner" />
          </div>
        </template>
      </MultiSelect>
    </template>

    <!-- Pass through all field-specific slots -->
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
