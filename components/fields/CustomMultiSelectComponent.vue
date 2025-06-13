<!-- CustomMultiSelectComponent.vue -->
<script setup lang="ts">
import { useDebounceFn } from '@vueuse/core'
import MultiSelect from 'primevue/multiselect'
import Logger from '../../utils/Logger'
import type { IFilter } from './interfaces/IFieldInterfaces'
import { useDynamicData } from '~/composables/useDynamicData'

// Interfaces
interface SuggestionItem {
  [key: string]: any
  status?: string
}

interface ApiConfig {
  moduleApi: string
  uriApi: string
}

// Props
const props = withDefaults(defineProps<{
  modelValue?: SuggestionItem[]
  model?: SuggestionItem[]
  field: string
  itemValue: string
  placeholder?: string
  id?: string
  maxSelectedLabels?: number
  debounceTimeMs?: number
  disabled?: boolean
  loading?: boolean
  apiConfig?: ApiConfig
  dependentField?: string
  filtersBase?: IFilter[]
  minQueryLength?: number
  maxItems?: number
  loadOnOpen?: boolean
  initialQuery?: string
  suggestions?: SuggestionItem[]
  tabIndex?: number
  showClear?: boolean
  filterable?: boolean
}>(), {
  placeholder: 'Select options',
  id: 'multiselect',
  maxSelectedLabels: 3,
  debounceTimeMs: 300,
  disabled: false,
  loading: false,
  filtersBase: () => [],
  minQueryLength: 0,
  maxItems: 50,
  loadOnOpen: true,
  initialQuery: '',
  modelValue: () => [],
  model: () => [],
  suggestions: () => [],
  tabIndex: -1,
  showClear: true,
  filterable: true
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: SuggestionItem[]]
  'change': [value: SuggestionItem[]]
  'error': [error: Error]
}>()

const { loadDynamicData } = useDynamicData()

// Estado
const instance = ref()
const isLoading = ref(false)
const internalSuggestions = ref<SuggestionItem[]>([])

// Computed para modelo
const selectedItems = computed({
  get() {
    const value = props.modelValue?.length ? props.modelValue : (props.model || [])
    return Array.isArray(value) ? value : []
  },
  set(newValue: SuggestionItem[]) {
    emit('update:modelValue', newValue)
    emit('change', newValue)
  }
})

// Computed para opciones
const availableOptions = computed(() => {
  const options = [...internalSuggestions.value]

  if (props.suggestions?.length) {
    props.suggestions.forEach((item) => {
      if (!options.some(o => o[props.itemValue] === item[props.itemValue])) {
        options.push(item)
      }
    })
  }

  selectedItems.value.forEach((selected) => {
    if (!options.some(o => o[props.itemValue] === selected[props.itemValue])) {
      options.push(selected)
    }
  })

  return options
})

// Funciones
function buildFilters(query: string): IFilter[] {
  const filters: IFilter[] = [...props.filtersBase]

  if (query && query.length >= props.minQueryLength) {
    filters.push(
      { key: 'code', operator: 'LIKE', value: query, logicalOperation: 'OR' },
      { key: 'name', operator: 'LIKE', value: query, logicalOperation: 'OR' }
    )
  }

  return filters
}

async function performSearch(query: string = ''): Promise<void> {
  if (!props.apiConfig?.moduleApi || !props.apiConfig?.uriApi) { return }

  try {
    isLoading.value = true
    const filters = buildFilters(query)
    const results = await loadDynamicData(
      query,
      props.apiConfig.moduleApi,
      props.apiConfig.uriApi,
      filters
    )

    if (results && Array.isArray(results)) {
      internalSuggestions.value = results
    }
  }
  catch (error) {
    Logger.error('Error loading data:', error)
    emit('error', error as Error)
  }
  finally {
    isLoading.value = false
  }
}

const debouncedSearch = useDebounceFn(
  async (event: any) => {
    const query = event.value?.trim() ?? ''
    await performSearch(query)
  },
  props.debounceTimeMs
)

function removeChip(itemId: string): void {
  // selectedItems contiene strings (IDs), necesitamos filtrar por string
  const updated = selectedItems.value.filter((id: any) => id !== itemId)
  selectedItems.value = updated
}

function handleChange(event: any): void {
  if (Array.isArray(event.value)) {
    selectedItems.value = event.value
  }
}

function handleBeforeShow(): void {
  if (availableOptions.value.length === 0 && props.loadOnOpen) {
    performSearch(props.initialQuery)
  }
}

// Funciones para el template de chips
function getChipLabel(item: any): string {
  const itemId = item
  const fullObject = availableOptions.value.find((option: any) =>
    option[props.itemValue] === itemId
  )

  if (fullObject) {
    // Si tiene code, usarlo
    if (fullObject.code) {
      return fullObject.code
    }

    // Si no tiene code pero el name tiene formato "CODIGO - NOMBRE"
    if (fullObject.name && fullObject.name.includes(' - ')) {
      const code = fullObject.name.split(' - ')[0]
      return code
    }

    // Fallback al name completo
    return fullObject.name || 'No name'
  }

  return `Not found: ${itemId.substring(0, 8)}`
}

// Lifecycle
onMounted(() => {
  if (props.loadOnOpen && availableOptions.value.length === 0) {
    performSearch(props.initialQuery)
  }
})
</script>

<template>
  <div class="dynamic-multiselect w-full">
    <MultiSelect
      :id="props.id"
      ref="instance"
      v-model="selectedItems"
      class="w-full"
      :options="availableOptions"
      :option-label="props.field"
      :option-value="props.itemValue"
      :placeholder="props.placeholder"
      :disabled="props.disabled || isLoading"
      :tabindex="props.tabIndex"
      :filter="props.filterable"
      :loading="isLoading || props.loading"
      display="comma"
      @filter="debouncedSearch"
      @change="handleChange"
      @before-show="handleBeforeShow"
    >
      <template #value="{ value }">
        <div v-if="value && value.length > 0" class="dynamic-multiselect__value-container flex align-items-center flex-wrap gap-1">
          <!-- Chips visibles -->
          <div
            v-for="(item, index) in value.slice(0, props.maxSelectedLabels)"
            :key="`chip-${index}`"
            class="dynamic-multiselect__chip"
          >
            <span class="dynamic-multiselect__chip-label">
              {{ getChipLabel(item) }}
            </span>
            <i
              class="pi pi-times dynamic-multiselect__chip-remove"
              @click.stop="removeChip(item)"
            />
          </div>

          <!-- Contador de elementos adicionales -->
          <div
            v-if="value.length > props.maxSelectedLabels"
            class="dynamic-multiselect__chip dynamic-multiselect__chip--counter"
          >
            +{{ value.length - props.maxSelectedLabels }}
          </div>
        </div>
        <span v-else class="dynamic-multiselect__placeholder">
          {{ props.placeholder }}
        </span>
      </template>

      <template #option="{ option }">
        <div
          class="dynamic-multiselect__option"
          :class="{ 'dynamic-multiselect__option--inactive': option.status === 'INACTIVE' }"
        >
          {{ option.name }}
        </div>
      </template>
    </MultiSelect>
  </div>
</template>

<style scoped>
/* Solo importar los estilos custom, PrimeFlex ya está cargado globalmente */
@import '@/assets/styles/components/dynamic-multiselect.scss';
</style>
