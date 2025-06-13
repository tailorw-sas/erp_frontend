<!-- CustomAutoCompleteComponent.vue -->
<script setup lang="ts">
import { useDebounceFn } from '@vueuse/core'
import type {
  AutoCompleteChangeEvent,
  AutoCompleteCompleteEvent,
  AutoCompleteItemSelectEvent
} from 'primevue/autocomplete'
import Logger from '../../utils/Logger'
import type { IFilter } from './interfaces/IFieldInterfaces'
import { useDynamicData } from '~/composables/useDynamicData'

// Definir interfaces para mayor claridad
interface ApiConfig {
  moduleApi: string
  uriApi: string
}

interface DependentField {
  name: string
  filterKeyValue: string
}

// Props simplificados sin herencia de altura
const props = withDefaults(defineProps<{
  model: any
  field: string
  itemValue: string
  placeholder?: string
  id?: string
  tabindex?: number
  debounceTimeMs?: number
  disabled?: boolean
  multiple?: boolean
  dependentField?: string
  apiConfig?: ApiConfig
  filtersBase?: IFilter[]
  minQueryLength?: number
  maxItems?: number
  clearable?: boolean
  loading?: boolean
  loadOnOpen?: boolean
  initialQuery?: string
  invalid?: boolean
}>(), {
  placeholder: 'Type to search',
  id: 'autocomplete',
  tabindex: 0,
  debounceTimeMs: 300,
  disabled: false,
  multiple: false,
  filtersBase: () => [],
  minQueryLength: 1,
  maxItems: 50,
  clearable: true,
  loading: false,
  loadOnOpen: true,
  initialQuery: '',
  invalid: false
})

// Emits con mejor tipado (BASE ORIGINAL)
const emit = defineEmits<{
  'update:modelValue': [value: any]
  'change': [value: any]
  'error': [error: Error]
  'itemUnselect': [item: any]
  'focus': []
  'blur': []
}>()

const { loadDynamicData } = useDynamicData()

// Estado reactivo mejorado (BASE ORIGINAL)
const localModelValue = ref(props.model)
const instance = ref()
const shouldReopen = ref(true)
const isLoading = ref(false)
const lastQuery = ref('')
const errorMessage = ref('')
const internalSuggestions = ref<unknown[]>([])

// Watcher optimizado con deep watching (BASE ORIGINAL)
watch(
  () => props.model,
  (newValue) => {
    localModelValue.value = newValue
    emit('update:modelValue', newValue)
  },
  { immediate: true, deep: true }
)

// Computed para validaciones
const hasApiConfig = computed(() => {
  return props.apiConfig?.moduleApi && props.apiConfig?.uriApi
})

// COMPUTED para clases dinámicas - SIMPLIFICADO
const autocompleteClasses = computed(() => {
  return [
    'dynamic-autocomplete',
    {
      'p-invalid': props.invalid
    }
  ]
})

// Computed para mostrar el botón clear
const showClearButton = computed(() => {
  return props.clearable && localModelValue.value
    && (Array.isArray(localModelValue.value) ? localModelValue.value.length > 0 : true)
})

// Función para extraer código del name (HOMOLOGADA)
function extractCodeFromName(name: string): string {
  if (name && name.includes(' - ')) {
    return name.split(' - ')[0]
  }
  return name || 'No code'
}

// Función para parsear dependentField de forma segura (BASE ORIGINAL)
function parseDependentField(): DependentField | null {
  if (!props.dependentField) { return null }

  try {
    return JSON.parse(props.dependentField) as DependentField
  }
  catch (error) {
    Logger.warn('Error parsing dependentField:', error)
    emit('error', error as Error)
    return null
  }
}

// Función mejorada para construir filtros (BASE ORIGINAL)
function buildFilters(query: string): IFilter[] {
  const filters: IFilter[] = [...props.filtersBase]

  // Agregar filtro dependiente si existe
  const dependentField = parseDependentField()
  if (dependentField && props.model && typeof props.model === 'object') {
    const dependentValue = props.model[dependentField.name]
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

  // Agregar filtros de búsqueda solo si hay query
  if (query) {
    filters.push(
      { key: 'code', operator: 'LIKE', value: query, logicalOperation: 'OR' },
      { key: 'name', operator: 'LIKE', value: query, logicalOperation: 'OR' }
    )
  }

  return filters
}

// Función de búsqueda con manejo de errores mejorado (BASE ORIGINAL)
async function performSearch(query: string): Promise<void> {
  if (!hasApiConfig.value) {
    Logger.warn('No apiConfig provided, cannot perform search')
    return
  }

  try {
    isLoading.value = true
    errorMessage.value = ''

    const filters = buildFilters(query)
    const results = await loadDynamicData(
      query,
      props.apiConfig!.moduleApi,
      props.apiConfig!.uriApi,
      filters
    )

    // Actualizar las sugerencias internas
    if (results && Array.isArray(results)) {
      internalSuggestions.value = results
    }
    else {
      internalSuggestions.value = []
    }

    lastQuery.value = query
  }
  catch (error) {
    Logger.error('Error loading dynamic data:', error)
    errorMessage.value = 'Error al cargar los datos'
    emit('error', error as Error)
    // Limpiar sugerencias en caso de error
    internalSuggestions.value = []
  }
  finally {
    isLoading.value = false
  }
}

// Función debounced mejorada (BASE ORIGINAL)
const debouncedComplete = useDebounceFn(
  async (event: AutoCompleteCompleteEvent) => {
    const query = event.query?.trim() ?? ''

    // Para búsquedas vacías o muy cortas, usar la consulta inicial
    if (!query || query.length < props.minQueryLength) {
      if (props.loadOnOpen) {
        await performSearch(props.initialQuery)
      }
      return
    }

    await performSearch(query)
  },
  props.debounceTimeMs,
  { maxWait: 2000 }
)

// Funciones de utilidad mejoradas (BASE ORIGINAL)
function openDropdown(): void {
  instance.value?.show()
}

// Función para cargar datos iniciales
async function loadInitialData(): Promise<void> {
  if (props.loadOnOpen) {
    await performSearch(props.initialQuery)
  }
}

function clearSelection(): void {
  if (props.multiple) {
    localModelValue.value = []
  }
  else {
    localModelValue.value = null
  }
  emit('update:modelValue', localModelValue.value)
  emit('change', localModelValue.value)
}

// Función mejorada para selección múltiple (BASE ORIGINAL)
function toggleSelection(item: any): void {
  if (!Array.isArray(localModelValue.value)) {
    localModelValue.value = []
  }

  const index = localModelValue.value.findIndex(
    (selectedItem: any) => selectedItem[props.itemValue] === item[props.itemValue]
  )

  if (index !== -1) {
    const removedItem = localModelValue.value.splice(index, 1)[0]
    emit('itemUnselect', removedItem)
  }
  else {
    // Verificar límite máximo si está definido
    if (props.maxItems && localModelValue.value.length >= props.maxItems) {
      Logger.warn(`Maximum items limit reached: ${props.maxItems}`)
      return
    }
    localModelValue.value.push(item)
  }

  emit('update:modelValue', localModelValue.value)
  emit('change', localModelValue.value)
}

// Event handlers mejorados (BASE ORIGINAL)
function onItemSelect(event: AutoCompleteItemSelectEvent): void {
  if (props.multiple) {
    toggleSelection(event.value)
    shouldReopen.value = true
  }
  else {
    emit('update:modelValue', event.value)
    emit('change', event.value)
  }
}

function onHide(): void {
  if (shouldReopen.value && props.multiple) {
    nextTick(() => {
      openDropdown()
      shouldReopen.value = false
    })
  }
}

function onFocus(): void {
  // Cargar datos iniciales al hacer focus si no hay sugerencias
  if (internalSuggestions.value.length === 0 && props.loadOnOpen) {
    loadInitialData()
  }
  emit('focus')
}

function onBlur(): void {
  emit('blur')
}

// Evento cuando se abre el dropdown
function onShow(): void {
  // Cargar datos iniciales al abrir el dropdown
  if (internalSuggestions.value.length === 0 && props.loadOnOpen) {
    loadInitialData()
  }
}

// Función de utilidad más robusta (BASE ORIGINAL)
function wait(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// Función mejorada para el evento change (BASE ORIGINAL)
async function handleChange(event: AutoCompleteChangeEvent): Promise<void> {
  try {
    const value = JSON.parse(JSON.stringify(event.value))
    await wait(50) // Pequeña espera para estabilizar el estado

    if (typeof value === 'object' && value !== null) {
      emit('change', value)
      emit('update:modelValue', value)
    }
  }
  catch (error) {
    Logger.error('Error handling change event:', error)
    emit('error', error as Error)
  }
}

// Función para obtener el label del chip
function getChipLabel(value: any): string {
  // Si tiene code, usarlo
  if (value.code) {
    return value.code
  }
  // Si no tiene code pero el name tiene formato "CODIGO - NOMBRE"
  return extractCodeFromName(value.name || value[props.field])
}

// Cargar datos iniciales al montar el componente (BASE ORIGINAL)
onMounted(() => {
  if (props.loadOnOpen && internalSuggestions.value.length === 0) {
    loadInitialData()
  }
})
</script>

<template>
  <div class="w-full" :class="autocompleteClasses">
    <AutoComplete
      :id="props.id"
      ref="instance"
      v-model="localModelValue"
      :suggestions="internalSuggestions"
      :field="props.field"
      :item-value="props.itemValue"
      :placeholder="props.placeholder"
      :disabled="props.disabled || isLoading"
      :tabindex="props.tabindex"
      :loading="isLoading || props.loading"
      class="w-full enhanced-form__field"
      force-selection
      dropdown
      :multiple="props.multiple"
      @complete="debouncedComplete"
      @item-select="onItemSelect"
      @change="handleChange"
      @hide="onHide"
      @focus="onFocus"
      @blur="onBlur"
      @show="onShow"
    >
      <template #option="slotProps">
        <slot name="option" :item="slotProps.option">
          <div
            class="dynamic-autocomplete__option"
            :class="{ 'dynamic-autocomplete__option--inactive': slotProps.option.status === 'INACTIVE' }"
          >
            {{ slotProps.option.name }}
          </div>
        </slot>
      </template>

      <template #chip="slotProps">
        <slot name="chip" :value="slotProps.value">
          <div
            class=""
            :class="{ 'dynamic-autocomplete__chip--inactive': slotProps.value?.status === 'INACTIVE' }"
          >
            {{ getChipLabel(slotProps.value) }}
          </div>
        </slot>
      </template>

      <template #empty>
        <slot name="empty">
          <div class="dynamic-autocomplete__empty">
            <span v-if="errorMessage" class="dynamic-autocomplete__error">
              {{ errorMessage }}
            </span>
            <span v-else-if="isLoading" class="dynamic-autocomplete__loading">
              Cargando...
            </span>
            <span v-else class="dynamic-autocomplete__no-results">
              No se encontraron resultados.
            </span>
          </div>
        </slot>
      </template>

      <template #loader>
        <div class="dynamic-autocomplete__loader">
          <i class="pi pi-spin pi-spinner" />
        </div>
      </template>
    </AutoComplete>

    <!-- Botón de limpiar opcional -->
    <Button
      v-if="showClearButton"
      icon="pi pi-times"
      text
      size="small"
      class="dynamic-autocomplete__clear-button"
      @click="clearSelection"
    />
  </div>
</template>
