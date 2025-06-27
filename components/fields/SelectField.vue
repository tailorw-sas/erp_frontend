<script setup lang="ts">
import { computed, inject, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
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
  'chipRemove': [item: any]
  'selectAll': [selectedIds: any[]]
  'deselectAll': [emptyValue: any[]]
  'toggleAll': [newValue: any[]]
}>()

const { loadDynamicData } = useDynamicData()

// 🔧 FUNCIÓN MEMOIZE SIMPLE
function createMemoize<T extends (...args: any[]) => any>(fn: T): T {
  const cache = new Map()
  return ((...args: Parameters<T>) => {
    const key = JSON.stringify(args)
    if (cache.has(key)) {
      return cache.get(key)
    }
    const result = fn(...args)
    cache.set(key, result)
    return result
  }) as T
}

// ============================================================================
// REACTIVE STATE
// ============================================================================
const formContext = inject<FormContext>('formContext', {} as FormContext)
const instance = ref()
const internalSuggestions = ref<SuggestionItem[]>([])
const internalValue = ref<unknown>(null)
const hasInitialValueLoaded = ref(false)

const componentState = reactive({
  isLoading: false,
  isSearching: false,
  isFirstLoad: true,
  isInitialized: false,
  isDropdownOpen: false,
  shouldReopen: true,
  errorState: null as string | null,
  searchQuery: '',
  lastParentValue: null as any,
  componentKey: 0,
  // 🎯 NUEVAS PROPIEDADES PARA MULTISELECT MEJORADO
  multiselectElement: null as HTMLElement | null,
  panelElement: null as HTMLElement | null,
  panelObserver: null as MutationObserver | null,
  showSelectAllInHeader: true
})

const searchController = ref<AbortController | null>(null)

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================
function forceComponentUpdate() {
  componentState.componentKey++
}

// 🔧 MEMOIZACIÓN DE FUNCIONES COSTOSAS
const getDisplayLabelMemo = createMemoize((item: SuggestionItem) => {
  if (!item) { return '' }
  if (item.name === 'Loading...') { return 'Loading...' }
  if (item.code && item.name && item.name !== 'Loading...') { return item.code }
  if (item.code) { return item.code }
  if (item.name?.includes(' - ')) { return item.name.split(' - ')[0] }
  return item.name || String(item.id || '')
})

const getDropdownLabelMemo = createMemoize((item: SuggestionItem) => {
  if (!item) { return '' }
  if (item.code && item.name && item.name !== 'Loading...') {
    return `${item.code} - ${item.name}`
  }
  if (item.code) { return item.code }
  if (item.name) { return item.name }
  return String(item.id || '')
})

// 🎯 NUEVAS FUNCIONES PARA MULTISELECT MEJORADO
/**
 * Aplica estilos de apertura consistentes con dropdown
 */
function applyMultiselectOpenStyles(): void {
  const element = componentState.multiselectElement
  if (!element) { return }

  element.style.setProperty('border-bottom-left-radius', '0', 'important')
  element.style.setProperty('border-bottom-right-radius', '0', 'important')
  element.style.setProperty('border-bottom-color', 'transparent', 'important')
  element.style.setProperty('border-color', 'var(--enhanced-form-focus-border)', 'important')
  element.style.setProperty('box-shadow', 'var(--enhanced-form-focus-shadow)', 'important')
  element.style.setProperty('transform', 'translateY(-1px)', 'important')
  element.style.setProperty('z-index', 'calc(var(--enhanced-form-dropdown-z) + 1)', 'important')
}

/**
 * Remueve estilos de apertura
 */
function removeMultiselectOpenStyles(): void {
  const element = componentState.multiselectElement
  if (!element) { return }

  const propertiesToReset = [
    'border-bottom-left-radius',
    'border-bottom-right-radius',
    'border-bottom-color',
    'border-color',
    'box-shadow',
    'transform',
    'z-index'
  ]
  propertiesToReset.forEach(prop => element.style.removeProperty(prop))
}

/**
 * Inicializa el comportamiento mejorado del multiselect
 */
function initializeMultiselectBehavior(): void {
  nextTick(() => {
    const multiselectEl = instance.value?.$el
    if (multiselectEl && isMultiple.value) {
      componentState.multiselectElement = multiselectEl

      // Observer para detectar cambios en las clases
      const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
          if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
            const target = mutation.target as HTMLElement
            const isOpen = target.classList.contains('p-multiselect-opened')

            if (isOpen && !componentState.isDropdownOpen) {
              componentState.isDropdownOpen = true
              applyMultiselectOpenStyles()

              // Aplicar estilos al panel
              setTimeout(() => {
                const panel = document.querySelector('.p-multiselect-panel') as HTMLElement
                if (panel) {
                  componentState.panelElement = panel
                  panel.style.setProperty('border-top', 'none', 'important')
                  panel.style.setProperty('border-radius', '0 0 0.5rem 0.5rem', 'important')
                  panel.style.setProperty('box-shadow', 'var(--enhanced-form-shadow-lg)', 'important')
                }
              }, 0)
            }
            else if (!isOpen && componentState.isDropdownOpen) {
              componentState.isDropdownOpen = false
              removeMultiselectOpenStyles()
            }
          }
        })
      })

      observer.observe(multiselectEl, {
        attributes: true,
        attributeFilter: ['class']
      })

      componentState.panelObserver = observer
    }
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

// FIXED: Single isMultiple computed property
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
  const optionsMap = new Map()
  const seenIds = new Set()

  const addUniqueOption = (item: SuggestionItem) => {
    const id = String(item.id)
    if (!seenIds.has(id)) {
      seenIds.add(id)
      optionsMap.set(id, item)
    }
  }

  // Priorizar valor actual para evitar "Loading..."
  if (internalValue.value) {
    if (isMultiple.value && Array.isArray(internalValue.value)) {
      internalValue.value.forEach((item) => {
        if (item && typeof item === 'object' && 'id' in item) {
          addUniqueOption(item as SuggestionItem)
        }
      })
    }
    else if (!isMultiple.value && typeof internalValue.value === 'object') {
      addUniqueOption(internalValue.value as SuggestionItem)
    }
  }

  // Agregar suggestions internas y props
  [...internalSuggestions.value, ...props.suggestions].forEach(addUniqueOption)

  return Array.from(optionsMap.values())
})

const currentValue = computed({
  get() {
    return internalValue.value
  },
  set(newValue: unknown) {
    Logger.log('🔄 [CURRENT VALUE SET]', {
      fieldName: props.field?.name || props.name,
      oldValue: internalValue.value,
      newValue,
      isMultiple: isMultiple.value
    })
    internalValue.value = newValue
  }
})

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

const placeholder = computed(() => {
  if (props.loading || componentState.isLoading) {
    return 'Loading...'
  }
  if (componentState.isSearching) {
    return 'Searching...'
  }
  if (effectiveParams.value.dependentField && !dependentFieldValue.value) {
    return `Please select ${effectiveParams.value.dependentField} first`
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
  if (componentState.errorState) {
    classes.push('select-field__component--error')
  }
  if (componentState.isLoading || componentState.isSearching) {
    classes.push('select-field__component--loading')
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

const accessibilityProps = computed(() => ({
  'aria-label': `${isMultiple.value ? 'MultiSelect' : 'Select'} for ${props.field?.label || props.label}`,
  'aria-describedby': props.field?.helpText ? `${props.id}-help` : undefined,
  'aria-invalid': !!(props.error && Array.isArray(props.error) && props.error.length > 0),
  'aria-required': props.required,
  'aria-expanded': componentState.isDropdownOpen,
  'aria-multiselectable': isMultiple.value
}))

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

const dropdownKey = computed(() => `dropdown-${componentState.componentKey}`)
const multiselectKey = computed(() => `multiselect-${componentState.componentKey}`)

// ============================================================================
// 🎯 MULTISELECT DISPLAY LOGIC - ENHANCED
// ============================================================================

// Computed para el valor seleccionado tipado correctamente
const selectedItems = computed(() => {
  if (isMultiple.value && Array.isArray(currentValue.value)) {
    return currentValue.value
  }
  return []
})

// Computed para los elementos visibles con límite
const visibleSelectedItems = computed(() => {
  const maxLabels = effectiveParams.value.maxSelectedLabels
  if (selectedItems.value.length <= maxLabels) {
    return selectedItems.value
  }
  return selectedItems.value.slice(0, maxLabels)
})

// Computed para el contador de elementos adicionales
const additionalItemsCount = computed(() => {
  const maxLabels = effectiveParams.value.maxSelectedLabels
  const totalSelected = selectedItems.value.length
  return Math.max(0, totalSelected - maxLabels)
})

// Computed para verificar si hay elementos adicionales
const hasAdditionalItems = computed(() => {
  return additionalItemsCount.value > 0
})

// 🎯 COMPUTED MEJORADO PARA MULTISELECT
const isMultiselectOpen = computed(() => {
  return componentState.isDropdownOpen && isMultiple.value
})

const enhancedMultiSelectClasses = computed(() => {
  const classes = [...componentClasses.value]

  if (isMultiselectOpen.value) {
    classes.push('multiselect-opened')
  }

  return classes
})

// ============================================================================
// 🎯 ENHANCED COMPUTED PROPERTIES FOR SELECT ALL FUNCTIONALITY
// ============================================================================

// Computed para contar items seleccionados
const selectedItemsCount = computed(() => {
  if (isMultiple.value && Array.isArray(currentValue.value)) {
    return currentValue.value.length
  }
  return 0
})

// Computed para contar opciones disponibles
const availableOptionsCount = computed(() => {
  return availableOptions.value.filter(option => option.name !== 'Loading...').length
})

// Computed para verificar si todos están seleccionados
const isAllSelected = computed(() => {
  if (!isMultiple.value || availableOptionsCount.value === 0) {
    return false
  }

  const validOptions = availableOptions.value.filter(option => option.name !== 'Loading...')
  const selectedIds = Array.isArray(currentValue.value) ? currentValue.value : []

  return validOptions.length > 0 && validOptions.every(option =>
    selectedIds.includes(option.id)
  )
})

// ============================================================================
// CORE FUNCTIONS
// ============================================================================
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

  if (searchController.value) {
    searchController.value.abort()
  }

  const controller = new AbortController()
  searchController.value = controller

  try {
    componentState.isLoading = true
    componentState.isSearching = true
    componentState.errorState = null
    componentState.searchQuery = query

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

    if (!controller.signal.aborted) {
      if (results && Array.isArray(results)) {
        internalSuggestions.value = results
        componentState.isFirstLoad = false
        Logger.info('🔍 [PERFORM SEARCH] ✅ Loaded:', {
          count: results.length,
          fieldName: props.field?.name || props.name
        })
      }
      else {
        internalSuggestions.value = []
      }
    }
  }
  catch (error) {
    if (!controller.signal.aborted) {
      Logger.error('🔍 [PERFORM SEARCH] Error:', error)
      handleError(error, 'PERFORM_SEARCH')
      internalSuggestions.value = []
    }
  }
  finally {
    if (!controller.signal.aborted) {
      componentState.isLoading = false
      componentState.isSearching = false
    }
  }
}

function handleError(error: unknown, context: string) {
  const errorMessage = error instanceof Error ? error.message : 'Unknown error'

  Logger.error(`[${context}] Error:`, error)

  if (errorMessage.includes('network') || errorMessage.includes('fetch')) {
    componentState.errorState = 'Network error. Please check your connection.'
  }
  else if (errorMessage.includes('timeout')) {
    componentState.errorState = 'Request timeout. Please try again.'
  }
  else if (errorMessage.includes('unauthorized')) {
    componentState.errorState = 'Authorization error. Please refresh the page.'
  }
  else {
    componentState.errorState = 'An error occurred. Please try again.'
  }

  emit('error', error as Error)

  setTimeout(() => {
    if (componentState.errorState) {
      componentState.errorState = null
    }
  }, 5000)
}

const debouncedSearch = useDebounceFn(
  async (event: any) => {
    const query = event.query?.trim() ?? ''
    Logger.info('🔍 [SEARCH] Search triggered:', {
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
  computed(() => effectiveParams.value.debounceTimeMs),
  { maxWait: 2000 }
)

// ============================================================================
// EVENT HANDLERS
// ============================================================================
function notifyValueChange(value: unknown) {
  try {
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
      type: typeof value,
      isArray: Array.isArray(value),
      length: Array.isArray(value) ? value.length : 'not array'
    })
  }
  catch (error) {
    console.error('🔥 [NOTIFY VALUE CHANGE] Error:', error)
    nextTick(() => {
      forceComponentUpdate()
    })
  }
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
  if (JSON.stringify(oldValue) !== JSON.stringify(value)) {
    forceComponentUpdate()
    nextTick(() => {
      setTimeout(() => {
        forceComponentUpdate()
      }, 50)
    })
  }
}

// 🎯 MULTISELECT CHANGE HANDLER - SIN REAPERTURA
function handleMultiSelectChange(event: any): void {
  if (isMultiple.value && Array.isArray(event.value)) {
    Logger.log('🔄 [MULTISELECT CHANGE] Enhanced change handler:', {
      fieldName: props.field?.name || props.name,
      oldValue: internalValue.value,
      newValue: event.value,
      count: event.value.length
    })

    try {
      // Actualización inmediata del valor
      internalValue.value = event.value

      // Forzar actualización visual inmediata
      forceComponentUpdate()

      // Notificar cambios para componentes dependientes
      nextTick(() => {
        notifyValueChange(event.value)
      })
    }
    catch (error) {
      Logger.error('🔥 [MULTISELECT CHANGE] Enhanced error handler:', error)
      internalValue.value = event.value
      forceComponentUpdate()
    }
  }
}

function handleFocus() {
  emit('focus')
}

function handleBlur() {
  emit('blur')
}

function handleShow(): void {
  componentState.isDropdownOpen = true
  Logger.log('🔄 [SHOW] Dropdown opened:', {
    fieldName: props.field?.name || props.name,
    isMultiple: isMultiple.value,
    optionsCount: availableOptions.value.length
  })

  if (availableOptions.value.length === 0 && props.loadOnOpen && !componentState.isLoading) {
    performSearch('')
  }

  // Para multiselect, aplicar estilos después de que se abra
  if (isMultiple.value) {
    nextTick(() => {
      applyMultiselectOpenStyles()
    })
  }
}

function handleHide(): void {
  // 🎯 INTERCEPTAR EL CIERRE - SOLO PERMITIR SI ES INTENCIONAL
  if (componentState.isDropdownOpen && isMultiple.value) {
    // Verificar si el cierre es por click fuera o ESC (legítimo)
    const activeElement = document.activeElement
    const isClickingOutside = !activeElement || !activeElement.closest('.p-multiselect-panel, .enhanced-multiselect')

    if (!isClickingOutside) {
      Logger.log('🛡️ [PREVENT HIDE] Preventing close - user is still interacting with multiselect')
      return // NO permitir el cierre
    }
  }

  componentState.isDropdownOpen = false
  Logger.log('🔄 [HIDE] Dropdown closed:', {
    fieldName: props.field?.name || props.name,
    isMultiple: isMultiple.value
  })

  if (isMultiple.value) {
    removeMultiselectOpenStyles()
  }
}

function handleHideMultiselect(): void {
  componentState.isDropdownOpen = false

  if (isMultiple.value) {
    removeMultiselectOpenStyles()
  }
}

function handleItemSelect(event: { originalEvent: Event, value: any }): void {
  if (event.originalEvent) {
    event.originalEvent.stopPropagation()
  }

  Logger.log('🔄 [ITEM SELECT] Item selected:', {
    value: event.value,
    fieldName: props.field?.name || props.name
  })
}

function handleItemUnselect(event: { originalEvent: Event, value: any }): void {
  if (event.originalEvent) {
    event.originalEvent.stopPropagation()
  }

  Logger.log('🔄 [ITEM UNSELECT] Item unselected:', {
    value: event.value,
    fieldName: props.field?.name || props.name
  })
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
// 🎯 ENHANCED EVENT HANDLERS FOR SELECT ALL FUNCTIONALITY
// ============================================================================

/**
 * Selecciona todas las opciones disponibles
 */
function handleSelectAll(): void {
  if (!isMultiple.value) {
    Logger.warn('🔄 [SELECT ALL] Called on non-multiple select')
    return
  }

  const validOptions = availableOptions.value.filter(option =>
    option.name !== 'Loading...'
    && option.id !== null
    && option.id !== undefined
  )

  if (validOptions.length === 0) {
    Logger.warn('🔄 [SELECT ALL] No valid options available')
    return
  }

  const allIds = validOptions.map(option => option.id)

  Logger.info('🔄 [SELECT ALL] Selecting all options:', {
    fieldName: props.field?.name || props.name,
    count: allIds.length,
    previousCount: selectedItemsCount.value
  })

  try {
    internalValue.value = allIds
    notifyValueChange(allIds)
    forceComponentUpdate()

    // Emit custom event for select all
    emit('selectAll', allIds)

    nextTick(() => {
      Logger.info('🔄 [SELECT ALL] ✅ Completed:', {
        newCount: selectedItemsCount.value,
        isAllSelected: isAllSelected.value
      })
    })
  }
  catch (error) {
    Logger.error('🔥 [SELECT ALL] Error:', error)
    handleError(error, 'SELECT_ALL')
  }
}

/**
 * Deselecciona todas las opciones
 */
function handleDeselectAll(): void {
  if (!isMultiple.value) {
    Logger.warn('🔄 [DESELECT ALL] Called on non-multiple select')
    return
  }

  Logger.info('🔄 [DESELECT ALL] Clearing all selections:', {
    fieldName: props.field?.name || props.name,
    previousCount: selectedItemsCount.value
  })

  try {
    const emptyValue: any[] = []
    internalValue.value = emptyValue
    notifyValueChange(emptyValue)
    forceComponentUpdate()

    // Emit custom events
    emit('deselectAll', emptyValue)
    emit('clear')

    nextTick(() => {
      Logger.info('🔄 [DESELECT ALL] ✅ Completed:', {
        newCount: selectedItemsCount.value,
        isAllSelected: isAllSelected.value
      })
    })
  }
  catch (error) {
    Logger.error('🔥 [DESELECT ALL] Error:', error)
    handleError(error, 'DESELECT_ALL')
  }
}

/**
 * Maneja el evento nativo de select-all-change de PrimeVue
 */
function handleNativeSelectAllChange(event: { checked: boolean }): void {
  Logger.log('🔄 [SELECT ALL CHANGE] Native PrimeVue event:', {
    checked: event.checked,
    fieldName: props.field?.name || props.name
  })

  if (event.checked) {
    handleSelectAll()
  }
  else {
    handleDeselectAll()
  }
}

// ============================================================================
// DISPLAY FUNCTIONS
// ============================================================================
function getDisplayLabel(item: SuggestionItem | any): string {
  return getDisplayLabelMemo(item)
}

function getDropdownLabel(item: SuggestionItem): string {
  return getDropdownLabelMemo(item)
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
  const option = getOptionById(selectedValue)
  if (option) {
    return getDisplayLabel(option)
  }
  return 'Loading...'
}

function removeChip(itemToRemove: any): void {
  if (!Array.isArray(internalValue.value)) {
    Logger.warn('removeChip: internalValue is not an array')
    return
  }

  const originalValue = [...internalValue.value]
  const indexToRemove = originalValue.findIndex((item: any) => {
    if (typeof item === 'object' && typeof itemToRemove === 'object') {
      return String(item.id) === String(itemToRemove.id)
    }
    if (typeof item === 'object') {
      return String(item.id) === String(itemToRemove)
    }
    return String(item) === String(itemToRemove)
  })

  if (indexToRemove === -1) {
    Logger.warn('removeChip: Item not found for removal', itemToRemove)
    return
  }

  const newValue = originalValue.filter((_, index) => index !== indexToRemove)

  try {
    internalValue.value = newValue
    nextTick(() => {
      notifyValueChange(newValue)
      emit('chipRemove', itemToRemove)
      emit('change', newValue)
    })
    Logger.info('removeChip: Successfully removed item', {
      removed: itemToRemove,
      newLength: newValue.length
    })
  }
  catch (error) {
    Logger.error('removeChip: Error updating value', error)
    internalValue.value = originalValue
  }
}

// ============================================================================
// WATCHERS
// ============================================================================
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
  if (JSON.stringify(oldValue) !== JSON.stringify(internalValue.value)) {
    forceComponentUpdate()
    nextTick(() => {
      setTimeout(() => {
        forceComponentUpdate()
      }, 100)
    })
  }
}, { immediate: true })

watch(dependentFieldValue, async (newValue, oldValue) => {
  const dependentField = effectiveParams.value.dependentField
  if (!dependentField || !componentState.isInitialized) {
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
    componentState.errorState = null
    const hasValidParentValue = newValue !== null
      && newValue !== undefined
      && newValue !== ''
      && (!Array.isArray(newValue) || newValue.length > 0)
    if (hasValidParentValue) {
      await nextTick()
      await performSearch('')
    }
    componentState.lastParentValue = newValue
  }
}, {
  immediate: false,
  deep: true,
  flush: 'post'
})

// 🎯 WATCHER MEJORADO PARA MULTISELECT
watch(() => internalValue.value, (newValue, oldValue) => {
  if (isMultiple.value && Array.isArray(newValue)) {
    // Verificar si realmente cambió para evitar loops infinitos
    const hasChanged = JSON.stringify(newValue) !== JSON.stringify(oldValue)

    if (hasChanged) {
      Logger.log('🔄 [INTERNAL VALUE WATCH] MultiSelect value changed:', {
        fieldName: props.field?.name || props.name,
        newCount: newValue.length,
        oldCount: Array.isArray(oldValue) ? oldValue.length : 0
      })

      // Forzar actualización visual después de un cambio
      nextTick(() => {
        forceComponentUpdate()
      })
    }
  }
}, {
  deep: true,
  flush: 'post'
})

// ============================================================================
// LIFECYCLE
// ============================================================================
onMounted(async () => {
  Logger.log('🚀 [MOUNT] Enhanced initialization', {
    fieldName: props.field?.name || props.name,
    dependentField: effectiveParams.value.dependentField,
    hasApiConfig: !!effectiveApiConfig.value,
    currentValue: internalValue.value,
    isMultiple: isMultiple.value
  })
  await nextTick()
  componentState.isInitialized = true

  // 🎯 INICIALIZAR COMPORTAMIENTO MEJORADO DEL MULTISELECT
  if (isMultiple.value) {
    initializeMultiselectBehavior()
  }

  // Aplicar estilos mejorados a los items después del mount
  setTimeout(() => {
    const items = document.querySelectorAll('.p-multiselect-item, .p-dropdown-item')
    items.forEach((item) => {
      (item as HTMLElement).style.setProperty('padding', '0.75rem 1rem', 'important')
      ;(item as HTMLElement).style.setProperty('margin', '0.125rem 0.5rem', 'important')
      ;(item as HTMLElement).style.setProperty('border-radius', '0.375rem', 'important')
      ;(item as HTMLElement).style.setProperty('transition', 'all 0.15s ease', 'important')
    })
  }, 150)

  const dependentField = effectiveParams.value.dependentField
  if (!dependentField) {
    if (props.loadOnOpen && effectiveApiConfig.value) {
      await performSearch(props.initialQuery)
    }
  }
  else {
    const parentValue = getCurrentFormValue(dependentField)
    componentState.lastParentValue = parentValue
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
  componentState.isInitialized = false

  if (searchController.value) {
    searchController.value.abort()
  }

  // 🎯 CLEANUP MEJORADO PARA MULTISELECT
  if (componentState.panelObserver) {
    componentState.panelObserver.disconnect()
    componentState.panelObserver = null
  }
})

// ============================================================================
// 🎯 ENHANCED EXPOSE WITH SELECT ALL METHODS
// ============================================================================
defineExpose({
  handleSelectAll,
  handleDeselectAll, // 🎯 New expose
  handleNativeSelectAllChange, // 🎯 New expose - PrimeVue native event handler
  selectedItemsCount: () => selectedItemsCount.value, // 🎯 New expose
  availableOptionsCount: () => availableOptionsCount.value, // 🎯 New expose
  isAllSelected: () => isAllSelected.value, // 🎯 New expose

  // Existing methods
  focus: () => instance.value?.focus?.(),
  blur: () => instance.value?.blur?.(),
  clear: handleClear,
  performSearch,
  getDisplayLabel,
  instance,
  getCurrentFormValue,
  dependentFieldValue: () => dependentFieldValue.value,
  effectiveParams: () => effectiveParams.value,
  componentKey: () => componentState.componentKey,
  dropdownKey,
  multiselectKey,
  isSearching: () => componentState.isSearching,
  errorState: () => componentState.errorState,
  availableOptions: () => availableOptions.value,
  removeChip,
  componentState: () => componentState,
  applyMultiselectOpenStyles,
  removeMultiselectOpenStyles,
  initializeMultiselectBehavior,
  isMultiselectOpen: () => isMultiselectOpen.value,
  enhancedMultiSelectClasses: () => enhancedMultiSelectClasses.value
})
</script>

<template>
  <BaseField v-bind="baseFieldProps">
    <template #input="{ fieldId }">
      <!-- ======================================================================
           SINGLE SELECT DROPDOWN
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
        :disabled="isDisabled || componentState.isLoading"
        :loading="componentState.isLoading || props.loading"
        :class="componentClasses"
        :style="fieldStyle"
        :filter="true"
        filter-placeholder="Type to search..."
        :show-clear="false"
        :reset-filter-on-hide="false"
        :empty-filter-message="componentState.isLoading ? 'Loading...' : 'No results found'"
        :empty-message="componentState.isLoading ? 'Loading...' : 'No options available'"
        :data-keep-open="true"
        :meta-key-selection="false"
        v-bind="accessibilityProps"
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

        <!-- Empty State -->
        <template #empty>
          <div class="dropdown-empty">
            <div class="dropdown-empty__content">
              <i v-if="componentState.isLoading || componentState.isSearching" class="pi pi-spin pi-spinner text-lg text-gray-400" />
              <i v-else-if="componentState.errorState" class="pi pi-exclamation-triangle text-lg text-red-400" />
              <i v-else class="pi pi-search text-lg text-gray-400" />
              <span v-if="componentState.isLoading || componentState.isSearching" class="dropdown-empty__text">
                Searching...
              </span>
              <span v-else-if="componentState.errorState" class="dropdown-empty__text">
                {{ componentState.errorState }}
              </span>
              <span v-else-if="effectiveParams.dependentField && !dependentFieldValue" class="dropdown-empty__text">
                Please select {{ effectiveParams.dependentField }} first
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
           🎯 ENHANCED MULTI SELECT WITH SELECT ALL FUNCTIONALITY
           ====================================================================== -->
      <MultiSelect
        v-else
        :id="fieldId"
        :key="multiselectKey"
        ref="instance"
        v-model="currentValue"
        class="enhanced-multiselect"
        :class="[...enhancedMultiSelectClasses, { 'multiselect-opened': componentState.isDropdownOpen && isMultiple }]"
        :style="fieldStyle"
        :options="availableOptions"
        option-label="name"
        option-value="id"
        :placeholder="placeholder"
        :disabled="isDisabled || componentState.isLoading"
        :filter="true"
        :loading="componentState.isLoading || props.loading"
        :max-selected-labels="0"
        display="chip"
        :show-toggle-all="true"
        v-bind="accessibilityProps"
        @filter="debouncedSearch"
        @show="handleShow"
        @hide="handleHide"
        @focus="handleFocus"
        @blur="handleBlur"
        @clear="handleClear"
        @change="handleMultiSelectChange"
        @item-select="handleItemSelect"
        @item-unselect="handleItemUnselect"
        @selectall-change="handleNativeSelectAllChange"
      >
        <!-- 🎯 VALUE TEMPLATE CON CHIPS HERMOSOS -->
        <template #value>
          <div class="enhanced-multiselect__value-container">
            <!-- Chips Container -->
            <div v-if="selectedItems.length > 0" class="enhanced-multiselect__chips">
              <!-- Chips Visibles -->
              <div
                v-for="itemId in visibleSelectedItems"
                :key="`chip-${itemId}-${componentState.componentKey}`"
                class="enhanced-multiselect__chip"
                :title="getDisplayLabel(getOptionById(itemId)) || `ID: ${itemId}`"
              >
                <span class="enhanced-multiselect__chip-label">
                  {{ getDisplayLabel(getOptionById(itemId)) || `ID: ${itemId}` }}
                </span>
                <button
                  type="button"
                  class="enhanced-multiselect__chip-remove"
                  :aria-label="`Remove ${getDisplayLabel(getOptionById(itemId)) || itemId}`"
                  @click.stop.prevent="removeChip(itemId)"
                >
                  <i class="pi pi-times" aria-hidden="true" />
                </button>
              </div>

              <!-- Contador de items adicionales -->
              <div
                v-if="hasAdditionalItems"
                class="enhanced-multiselect__additional"
                :title="`${additionalItemsCount} more items selected`"
              >
                <span class="enhanced-multiselect__additional-text">
                  +{{ additionalItemsCount }}
                </span>
              </div>
            </div>

            <!-- Placeholder cuando está vacío -->
            <div v-else class="enhanced-multiselect__placeholder">
              <span class="enhanced-multiselect__placeholder-text">{{ placeholder }}</span>
            </div>
          </div>
        </template>

        <!-- 🎯 OPTION TEMPLATE CON MEJOR HOVER -->
        <template #option="{ option }">
          <div
            class="enhanced-multiselect__option"
            :class="{
              'enhanced-multiselect__option--inactive': option.status === 'INACTIVE',
              'enhanced-multiselect__option--loading': option.name === 'Loading...',
              'enhanced-multiselect__option--selected': isMultiple && Array.isArray(currentValue) && currentValue.includes(option.id),
            }"
          >
            <div v-if="option.name === 'Loading...'" class="enhanced-multiselect__option-loading">
              <i class="pi pi-spin pi-spinner" />
              <span>Loading...</span>
            </div>
            <div v-else class="enhanced-multiselect__option-content">
              <!-- Contenido de la opción sin checkbox ni status -->
              <span class="enhanced-multiselect__option-label">{{ getDropdownLabel(option) }}</span>
            </div>
          </div>
        </template>

        <!-- Enhanced Empty State -->
        <template #empty>
          <div class="enhanced-multiselect__empty-state">
            <div v-if="componentState.isLoading || componentState.isSearching" class="enhanced-multiselect__empty-loading">
              <i class="pi pi-spin pi-spinner enhanced-multiselect__empty-spinner" />
              <div class="enhanced-multiselect__empty-content">
                <span class="enhanced-multiselect__empty-text">Searching...</span>
                <span class="enhanced-multiselect__empty-subtitle">Please wait</span>
              </div>
            </div>

            <div v-else-if="componentState.errorState" class="enhanced-multiselect__empty-error">
              <i class="pi pi-exclamation-triangle enhanced-multiselect__empty-icon enhanced-multiselect__empty-icon--error" />
              <div class="enhanced-multiselect__empty-content">
                <span class="enhanced-multiselect__empty-text">{{ componentState.errorState }}</span>
                <button class="enhanced-multiselect__empty-retry" @click="performSearch('')">
                  <i class="pi pi-refresh" />
                  Retry
                </button>
              </div>
            </div>

            <div v-else-if="effectiveParams.dependentField && !dependentFieldValue" class="enhanced-multiselect__empty-dependent">
              <i class="pi pi-info-circle enhanced-multiselect__empty-icon enhanced-multiselect__empty-icon--info" />
              <div class="enhanced-multiselect__empty-content">
                <span class="enhanced-multiselect__empty-text">Selection Required</span>
                <span class="enhanced-multiselect__empty-subtitle">Please select {{ effectiveParams.dependentField }} first</span>
              </div>
            </div>

            <div v-else class="enhanced-multiselect__empty-no-results">
              <i class="pi pi-search enhanced-multiselect__empty-icon enhanced-multiselect__empty-icon--search" />
              <div class="enhanced-multiselect__empty-content">
                <span class="enhanced-multiselect__empty-text">No results found</span>
                <span v-if="componentState.searchQuery" class="enhanced-multiselect__empty-query">
                  for "{{ componentState.searchQuery }}"
                </span>
                <span v-else class="enhanced-multiselect__empty-subtitle">Start typing to search</span>
              </div>
            </div>
          </div>
        </template>

        <!-- Enhanced Loader -->
        <template #loader>
          <div class="enhanced-multiselect__loader">
            <i class="pi pi-spin pi-spinner" />
            <span>Loading options...</span>
          </div>
        </template>

        <!-- 🎯 FOOTER CON CONTADOR EN TIEMPO REAL -->
        <template #footer>
          <div class="enhanced-multiselect__footer">
            <div class="enhanced-multiselect__footer-stats">
              <span class="enhanced-multiselect__footer-selected">
                <i class="pi pi-check-circle" />
                {{ selectedItemsCount }} selected
              </span>
              <span class="enhanced-multiselect__footer-divider">•</span>
              <span class="enhanced-multiselect__footer-available">
                <i class="pi pi-list" />
                {{ availableOptionsCount }} available
              </span>
            </div>
            <div v-if="selectedItemsCount > 0" class="enhanced-multiselect__footer-actions">
              <button
                class="enhanced-multiselect__footer-clear"
                :title="`Clear all ${selectedItemsCount} selections`"
                @click.stop.prevent="handleDeselectAll"
              >
                <i class="pi pi-times" />
                Clear All
              </button>
            </div>
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

<style scoped>
/* ===============================================================================
   🎯 ENHANCED SELECT FIELD STYLES - PROFESSIONAL UI/UX
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
  /* No rotation transition */
}

:deep(.p-dropdown.p-dropdown-opened .p-dropdown-trigger .p-icon) {
  /* No rotation - keep icon pointing down */
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

/* 🎯 MULTISELECT COMPONENT STYLES - EXACT MATCH WITH DROPDOWN */
.enhanced-multiselect {
  width: 100%;
}

:deep(.enhanced-multiselect) {
  width: 100% !important;
  height: var(--enhanced-form-field-height) !important;
  min-height: var(--enhanced-form-field-height) !important;
  max-height: var(--enhanced-form-field-height) !important;
  background: white !important;
  border: var(--enhanced-form-border-width) solid #d1d5db !important;
  border-radius: var(--enhanced-form-border-radius) !important;
  transition: var(--enhanced-form-transition) !important;
  box-shadow: var(--enhanced-form-shadow) !important;
  overflow: hidden !important;
  position: relative !important;
  max-width: none !important;
  min-width: 0 !important;
  box-sizing: border-box !important;
  display: flex !important;
  align-items: center !important;
}

/* 🎯 MULTISELECT TRIGGER - EXACT MATCH WITH DROPDOWN */
:deep(.enhanced-multiselect .p-multiselect-trigger) {
  position: absolute !important;
  right: 0 !important;
  top: 0 !important;
  height: 100% !important;
  width: var(--enhanced-form-field-height) !important;
  background: transparent !important;
  border: none !important;
  border-left: 1px solid #d1d5db !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  transition: var(--enhanced-form-transition) !important;
  box-shadow: none !important;
  outline: none !important;
  z-index: 10 !important;
}

:deep(.enhanced-multiselect .p-multiselect-trigger:hover) {
  background: #f9fafb !important;
}

:deep(.enhanced-multiselect .p-multiselect-trigger .p-icon) {
  color: #3b82f6 !important;
  /* Removed rotation transition */
}

/* Remove rotation when opened */
:deep(.enhanced-multiselect.p-multiselect-opened .p-multiselect-trigger .p-icon) {
  /* No rotation */
}

:deep(.enhanced-multiselect:hover:not(.p-disabled):not(.p-multiselect-opened):not(.p-focus)) {
  border-color: #9ca3af !important;
  transform: translateY(-1px) !important;
  box-shadow: var(--enhanced-form-shadow-lg) !important;
}

:deep(.enhanced-multiselect.p-focus:not(.p-multiselect-opened)) {
  border-color: var(--enhanced-form-focus-border) !important;
  box-shadow: var(--enhanced-form-focus-shadow) !important;
  transform: translateY(-1px) !important;
}

/* 🎯 MULTISELECT OPENED STATE - Comportamiento igual al dropdown */
.enhanced-multiselect.multiselect-opened :deep(.p-multiselect),
:deep(.enhanced-multiselect.p-multiselect-opened) {
  border-bottom-left-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
  border-bottom-color: transparent !important;
  border-color: var(--enhanced-form-focus-border) !important;
  box-shadow: var(--enhanced-form-focus-shadow) !important;
  z-index: calc(var(--enhanced-form-dropdown-z) + 1) !important;
  transform: translateY(-1px) !important;
}

/* Rotación del ícono cuando está abierto - DESHABILITADO */
.enhanced-multiselect.multiselect-opened :deep(.p-multiselect-trigger .p-icon),
:deep(.enhanced-multiselect.p-multiselect-opened .p-multiselect-trigger .p-icon) {
  /* No rotation - keep icon pointing down */
}

:deep(.enhanced-multiselect.p-invalid) {
  border-color: var(--enhanced-form-error-border) !important;
  box-shadow: var(--enhanced-form-error-shadow) !important;
  animation: fieldErrorShake 0.5s ease-in-out !important;
}

:deep(.enhanced-multiselect.p-invalid:focus),
:deep(.enhanced-multiselect.p-invalid.p-focus) {
  border-color: var(--enhanced-form-error-border) !important;
  box-shadow: var(--enhanced-form-error-shadow-hover) !important;
}

/* 🎯 ENHANCED PANEL STYLES - IMPROVED VISUAL HIERARCHY */
:deep(.p-multiselect-panel) {
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

/* 🎯 MINIMAL STYLING - LET PRIMEVUE DO ITS THING */
:deep(.p-multiselect-panel .p-multiselect-header) {
  /* Only adjust layout to ensure checkbox and filter are inline */
  display: flex !important;
  align-items: center !important;
  gap: 0.75rem !important;
}

/* Ensure checkbox is visible if PrimeVue generates it */
:deep(.p-multiselect-panel .p-multiselect-header .p-checkbox) {
  display: flex !important;
}

/* Ensure filter container takes remaining space */
:deep(.p-multiselect-panel .p-multiselect-header .p-multiselect-filter-container) {
  flex: 1 !important;
}

/* Hide close button - optional */
:deep(.p-multiselect-panel .p-multiselect-header .p-multiselect-close) {
  display: none !important;
}

/* 🎯 COMPACT MULTISELECT ITEMS - MATCH DROPDOWN STYLE */
:deep(.p-multiselect-panel .p-multiselect-items-wrapper) {
  padding: 0 !important;
}

:deep(.p-multiselect-panel .p-multiselect-items) {
  padding: 0.25rem 0 !important;
}

:deep(.p-multiselect-panel .p-multiselect-item) {
  padding: 0.5rem 1rem !important;
  margin: 0 !important;
  min-height: auto !important;
  font-size: 0.875rem !important;
  border: none !important;
  border-radius: 0 !important;
  transition: background-color 0.15s ease !important;
}

:deep(.p-multiselect-panel .p-multiselect-item:hover) {
  background: #f3f4f6 !important;
}

:deep(.p-multiselect-panel .p-multiselect-item.p-highlight) {
  background: #eff6ff !important;
  color: #1e40af !important;
}

/* 🎯 SIMPLIFICADO - SOLO ESTILOS NECESARIOS */

/* Hide the checkbox input but keep the visual box */
:deep(.p-checkbox-input) {
  position: absolute !important;
  opacity: 0 !important;
  width: 0 !important;
  height: 0 !important;
  pointer-events: none !important;
}

/* 📦 VALUE CONTAINER - EXACT MATCH WITH DROPDOWN LAYOUT */
.enhanced-multiselect__value-container {
  width: 100%;
  height: calc(var(--enhanced-form-field-height) - 4px);
  min-height: calc(var(--enhanced-form-field-height) - 4px);
  max-height: calc(var(--enhanced-form-field-height) - 4px);
  display: flex;
  align-items: center;
  padding: 0 calc(var(--enhanced-form-field-height) + 0.25rem) 0 1rem;
  gap: 0.375rem;
  flex-wrap: nowrap;
  overflow: hidden;
  box-sizing: border-box;
  position: relative;
  z-index: 1;
  font-size: var(--enhanced-form-font-size);
}

/* 🏷️ CHIPS CONTAINER - OPTIMIZED LAYOUT WITH TRIGGER SPACE */
.enhanced-multiselect__chips {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  flex-wrap: nowrap;
  overflow: hidden;
  width: 100%;
  height: 100%;

  /* Smooth scrolling for overflow */
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.enhanced-multiselect__chips::-webkit-scrollbar {
  display: none;
}

/* 🎨 INDIVIDUAL CHIP - COMPACT SINGLE LINE */
.enhanced-multiselect__chip {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  border: 1px solid rgba(59, 130, 246, 0.2);
  color: #1e40af;
  border-radius: 0.75rem;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  font-weight: 500;
  height: 1.5rem;
  max-width: 120px;
  white-space: nowrap;
  flex-shrink: 0;
  box-sizing: border-box;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  animation: chip-appear 0.2s ease-out;
  cursor: default;
}

.enhanced-multiselect__chip:hover {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
  transform: scale(1.02);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

/* 📝 CHIP LABEL - ELLIPSIS HANDLING */
.enhanced-multiselect__chip-label {
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: inherit;
  color: inherit;
  margin: 0;
  padding: 0;
  line-height: 1.2;
}

/* ❌ CHIP REMOVE BUTTON - ACCESSIBLE */
.enhanced-multiselect__chip-remove {
  flex-shrink: 0;
  width: 1.25rem;
  height: 1.25rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  color: currentColor;
  padding: 0;
  margin: 0;
}

.enhanced-multiselect__chip-remove:hover {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
  transform: scale(1.1);
}

.enhanced-multiselect__chip-remove:focus {
  outline: 2px solid rgba(239, 68, 68, 0.3);
  outline-offset: 1px;
}

.enhanced-multiselect__chip-remove .pi {
  font-size: 0.875rem;
  color: inherit;
  line-height: 1;
}

/* 🔢 ADDITIONAL ITEMS COUNTER - PREMIUM DESIGN */
.enhanced-multiselect__additional {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  border: 1px solid #d1d5db;
  color: #374151;
  border-radius: 1rem;
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
  font-weight: 600;
  height: 2rem;
  min-width: 2.5rem;
  flex-shrink: 0;
  cursor: default;
  transition: all 0.2s ease;
}

.enhanced-multiselect__additional:hover {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
  transform: scale(1.05);
}

.enhanced-multiselect__additional-text {
  font-weight: 600;
  letter-spacing: 0.025em;
}

/* 📝 PLACEHOLDER - ULTRA SPECIFIC MATCH WITH DROPDOWN LABEL */
.enhanced-multiselect__placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  color: #9ca3af;
  font-style: italic;
  padding: 0;
  margin: 0;
  font-size: var(--enhanced-form-font-size);
  line-height: 1.5;
}

.enhanced-multiselect__placeholder-text {
  color: inherit;
  font-size: inherit;
  font-style: inherit;
  padding: 0;
  margin: 0;
  line-height: inherit;
  display: inline-block;
}

/* 🎯 ENHANCED OPTION TEMPLATE - CLEAN DESIGN WITHOUT CHECKBOX */
.enhanced-multiselect__option {
  display: flex;
  align-items: center;
  padding: 0 !important;
  cursor: pointer;
  transition: none !important;
  border: none;
  background: transparent;
  margin: 0 !important;
  border-radius: 0 !important;
  font-size: 0.875rem !important;
  box-sizing: border-box !important;
  width: 100% !important;
}

.enhanced-multiselect__option--loading {
  color: #6b7280;
  cursor: wait;
  justify-content: center;
}

.enhanced-multiselect__option--inactive {
  opacity: 0.6;
  color: #9ca3af;
}

.enhanced-multiselect__option-loading {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6b7280;
  justify-content: center;
  width: 100%;
}

.enhanced-multiselect__option-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.enhanced-multiselect__option-label {
  flex: 1;
  font-weight: 400;
  color: #374151;
  font-size: 0.875rem;
  line-height: 1.25rem;
}

/* 🔍 ENHANCED EMPTY STATES - COMPACT PROFESSIONAL FEEDBACK */
.enhanced-multiselect__empty-state {
  padding: 1.5rem 1rem;
  text-align: center;
  color: #6b7280;
  min-height: 4rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.enhanced-multiselect__empty-loading,
.enhanced-multiselect__empty-error,
.enhanced-multiselect__empty-dependent,
.enhanced-multiselect__empty-no-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  max-width: 300px;
}

.enhanced-multiselect__empty-spinner {
  font-size: 1.5rem;
  color: #3b82f6;
}

.enhanced-multiselect__empty-icon {
  font-size: 1.5rem;
  color: #9ca3af;
}

.enhanced-multiselect__empty-icon--error {
  color: #ef4444;
}

.enhanced-multiselect__empty-icon--info {
  color: #3b82f6;
}

.enhanced-multiselect__empty-icon--search {
  color: #9ca3af;
}

.enhanced-multiselect__empty-content {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  text-align: center;
}

.enhanced-multiselect__empty-text {
  font-size: 0.875rem;
  line-height: 1.25;
  color: #6b7280;
  margin: 0;
  font-weight: 500;
}

.enhanced-multiselect__empty-subtitle {
  font-size: 0.75rem;
  color: #9ca3af;
  margin: 0;
}

.enhanced-multiselect__empty-query {
  font-size: 0.75rem;
  color: #9ca3af;
  font-style: italic;
  margin: 0;
}

.enhanced-multiselect__empty-retry {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  font-size: 0.8125rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.enhanced-multiselect__empty-retry:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* 🎯 FOOTER MEJORADO CON STATS Y ACCIONES */
.enhanced-multiselect__footer {
  padding: 1rem;
  border-top: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.enhanced-multiselect__footer-stats {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.8125rem;
  color: #64748b;
  font-weight: 500;
}

.enhanced-multiselect__footer-selected,
.enhanced-multiselect__footer-available {
  display: flex;
  align-items: center;
  gap: 0.375rem;
}

.enhanced-multiselect__footer-selected i {
  color: #16a34a;
  font-size: 0.875rem;
}

.enhanced-multiselect__footer-available i {
  color: #64748b;
  font-size: 0.875rem;
}

.enhanced-multiselect__footer-divider {
  color: #cbd5e1;
  font-weight: 300;
}

.enhanced-multiselect__footer-actions {
  display: flex;
  gap: 0.5rem;
}

.enhanced-multiselect__footer-clear {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  font-size: 0.75rem;
  font-weight: 500;
  transition: all 0.2s ease;
  box-shadow: 0 1px 2px rgba(239, 68, 68, 0.2);
}

.enhanced-multiselect__footer-clear:hover {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.3);
}

.enhanced-multiselect__footer-clear i {
  font-size: 0.75rem;
}

/* 🔄 ENHANCED LOADER - COMPACT */
.enhanced-multiselect__loader {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  color: #6b7280;
  font-size: 0.8125rem;
}

/* 🎬 ANIMATIONS */
@keyframes chip-appear {
  0% {
    opacity: 0;
    transform: scale(0.8) translateY(10px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes fieldErrorShake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-2px); }
  20%, 40%, 60%, 80% { transform: translateX(2px); }
}

/* 📱 ENHANCED RESPONSIVE DESIGN */
@media (max-width: 768px) {
  .enhanced-multiselect__value-container {
    min-height: calc(var(--enhanced-form-field-height-mobile) - 4px);
    padding: 0.5rem 0.75rem;
  }

  .enhanced-multiselect__chip {
    max-width: 150px;
    font-size: 0.8rem;
    padding: 0.25rem 0.625rem;
  }

  .enhanced-multiselect__additional {
    font-size: 0.8rem;
    padding: 0.25rem 0.625rem;
  }

  .enhanced-multiselect__header-controls {
    gap: 0.5rem;
  }
}

@media (max-width: 480px) {
  .enhanced-multiselect__chip {
    max-width: 120px;
  }
}

/* 🎯 ULTRA HIGH SPECIFICITY OVERRIDES FOR PRIMEVUE */
.enhanced-multiselect {
  :deep(.p-multiselect.p-component.p-inputwrapper) {
    width: 100% !important;
    height: var(--enhanced-form-field-height) !important;
  }

  :deep(.p-multiselect .p-multiselect-label-container) {
    overflow: hidden !important;
    flex: 1 !important;
    height: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
  }

  :deep(.p-multiselect .p-multiselect-token) {
    display: none !important; /* Hide default chips, we use custom */
  }
}

/* 🎯 KEEP HEADER CHECKBOX VISIBLE, HIDE ONLY ITEM CHECKBOXES - REMOVED EMPTY RULE */

/* 🎯 DROPDOWN ITEMS - ENSURE CONSISTENT STYLE */
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

/* 🎯 MULTISELECT ITEMS COMPACTOS - REMOVED EMPTY DUPLICATE RULES */
:deep(.p-multiselect-panel .p-multiselect-item) {
  padding: 0.25rem 0.75rem !important;
  margin: 0.125rem 0.5rem !important;
  min-height: 1.75rem !important;
  max-height: 1.75rem !important;
  height: 1.75rem !important;
  line-height: 1.25rem !important;
  border: none !important;
  border-radius: 0.375rem !important;
  transition: all 0.15s ease !important;
  font-size: 0.875rem !important;
  display: flex !important;
  align-items: center !important;
  gap: 0.75rem !important;
}

/* 🎯 ESTADOS HOVER Y SELECTED CON MEJOR CONTRASTE */
:deep(.p-multiselect-panel .p-multiselect-item:hover) {
  background: #f3f4f6 !important;
  color: #111827 !important;
}

:deep(.p-multiselect-panel .p-multiselect-item.p-highlight) {
  background: #dbeafe !important;
  color: #1e40af !important;
  font-weight: 600 !important;
}

/* CONTAINERS - SIN PADDING EXTRA */
.p-multiselect-items,
.p-dropdown-items {
  padding: 0.25rem 0 !important;
  margin: 0 !important;
}

/* Ensure proper header styling for dropdown */
.dropdown-option,
.dropdown-selected,
.dropdown-placeholder,
.dropdown-empty,
.dropdown-loader {
  font-size: var(--enhanced-form-font-size);
  color: #374151;
}

.dropdown-option--loading,
.dropdown-selected--loading {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6b7280;
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
  justify-content: center;
  padding: 1rem;
  color: #6b7280;
}
</style>
