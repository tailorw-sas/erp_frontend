<script setup lang="ts">
import dayjs from 'dayjs'
import { FilterMatchMode } from 'primevue/api'
import BlockUI from 'primevue/blockui'
import type { DataTableFilterMeta } from 'primevue/datatable'
import type { PageState } from 'primevue/paginator'
import type { PropType } from 'vue'
import type { IFilter, IQueryRequest, IStandardObject } from '../fields/interfaces/IFieldInterfaces'
import type { IColumn, IColumnSlotProps, IEditableSlotProps, IQueryToSearch, ISortOptions } from './interfaces/ITableInterfaces'
import { ENUM_OPERATOR_DATE, ENUM_OPERATOR_NUMERIC, ENUM_OPERATOR_SELECT, ENUM_OPERATOR_STRING } from './enums'
import DialogDelete from './components/DialogDelete.vue'
import { formatNumber, getLastDayOfMonth, removeDuplicatesMap } from '~/utils/helpers'
import { GenericService } from '~/services/generic-services'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'

export interface IObjApi {
  moduleApi: string
  uriApi: string
  filter?: IFilter[]
  keyValue?: string
  mapFunction?: (data: any) => any
  sortOption?: ISortOptions
}

const props = defineProps({
  componentTableId: {
    type: String,
    required: false,
  },
  data: {
    type: Array as () => Array<any>,
    required: true,
  },
  columns: {
    type: Array as () => Array<IColumn>,
    required: true,
  },
  options: {
    type: Object as PropType<{
      tableName: string
      moduleApi: string
      uriApi: string
      loading: boolean
      actionsAsMenu?: boolean
      showAcctions?: boolean
      showCreate?: boolean
      showEdit?: boolean
      showDelete?: boolean
      showLocalDelete?: boolean
      showFilters?: boolean
      showTitleBar?: boolean
      messageToDelete: string
      search?: boolean
      selectionMode?: 'single' | 'multiple' | undefined
      expandableRows?: boolean
      selectAllItemByDefault?: boolean
      selectFirstItemByDefault?: boolean
      showPagination?: boolean
      showCustomEmptyTable?: boolean
      scrollHeight?: string | undefined
      showSelectedItems?: boolean
    }>,
    required: true,
  },
  pagination: {
    type: Object as PropType<{
      limit: number
      page: number
      totalElements: number
      totalPages: number
      search: string
    }>,
    required: false,
  },
  actionsWidth: {
    type: Number,
    default: 90,
  },
  parentComponentLoading: {
    type: Boolean,
    required: false,
  },
  // Esta propiedad de va a usar para el ordenamiento local, para que ordene local debe venir en false
  // En el manejo del onSortField se debe validar que no se llame al api en el padre
  isCustomSorting: {
    type: Boolean,
    required: false,
    default: true,
  },
  selectedItems: {
    type: Array as PropType<any[]>,
    required: false,
  },
  // Mostrar el componente de paginación
  showLocalPagination: {
    type: Boolean,
    required: false,
    default: false,
  },
})

const emits = defineEmits<{
  (e: 'onListItem'): void
  (e: 'onConfirmCreate'): void
  (e: 'onChangePagination', value: PageState): void
  (e: 'onConfirmEdit', value: string): void
  (e: 'onChangeFilter', value: DataTableFilterMeta | null): void
  (e: 'openEditDialog', value: any): void
  (e: 'openDeleteDialog', value: any): void
  (e: 'onLocalDelete', value: any): void
  (e: 'update:clickedItem', value: any): void
  (e: 'update:selectedItems', value: any): void
  (e: 'onSortField', value: any): void
  (e: 'onRowDoubleClick', value: any): void
  (e: 'onRowRightClick', value: any): void
  (e: 'onCellEditComplete', value: any): void
  (e: 'onTableCellEditComplete', value: any): void
  (e: 'onExpandRow', value: any): void
}>()

// Composables y refs
const menu = ref()

type FilterDisplayMode = 'row' | 'menu' | undefined

const modeFilterDisplay: Ref<FilterDisplayMode> = ref('menu')
const menuFilter: { [key: string]: Ref<any> } = {}
const objeto: { [key: string]: any } = {}
// Asignar el objeto a objListData y hacerlo reactivo
const objListData = reactive(objeto)

// Iteramos sobre el array de campos y añadimos las propiedades a menuFilter
props.columns.forEach((field) => {
  menuFilter[field.field] = ref(null)
})

const openDialogDelete = ref(false)
const clickedItem = ref<any>([])
const expandedRows = ref({})
const metaKey = ref(false)
const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')

const objetoFilter: { [key: string]: any } = {
  search: { value: null, matchMode: FilterMatchMode.CONTAINS },
}

const objForLoadings = ref<{ [key: string]: any }>({})
const objForValues = ref<Record<string, Array<{ [key: string]: any }>>>({})

const objFilterToClear = ref()

const filters1 = ref(objetoFilter)

const menuItems = ref([
  {
    items: [
      {
        label: 'Edit',
        icon: 'pi pi-pencil',
        action: 'edit'
      },
      {
        label: 'Delete',
        icon: 'pi pi-trash',
        action: 'delete'
      }
    ]
  }
])
const menuItemsDate = ref(ENUM_OPERATOR_DATE)
const menuItemsString = ref(ENUM_OPERATOR_STRING)
const menuItemsSelect = ref(ENUM_OPERATOR_SELECT)
const menuItemsNumeric = ref(ENUM_OPERATOR_NUMERIC)

// Funciones del componente
async function onRowExpand({ data }: any) {
  emits('onExpandRow', data?.id)
}

function onRowCollapse(_event: { data: { name: any } }) {
  emits('onExpandRow', '')
}

function onListItem() {
  emits('onListItem')
}

function onChangePageOrLimit(event: PageState) {
  emits('onChangePagination', event)
}

function onChangeFilters(value: DataTableFilterMeta) {
  emits('onChangeFilter', value)
}

function clearIndividualFilter(param1: string) {
  filters1.value[param1] = JSON.parse(JSON.stringify(objFilterToClear.value[param1]))
  objForValues.value[param1] = []

  // Llama a la función callback
  onChangeFilters(filters1.value)
}

function toggleMenu(event: Event) {
  menu.value.toggle(event)
}

function toggleMenuFilter(event: Event, i: number | string) {
  if (modeFilterDisplay.value === 'menu') {
    menuFilter[i].value[0].toggle(event)
  }
  else {
    menuFilterForRowDisplay.value[i].toggle(event)
  }
}

function onEdit(item: any) {
  emits('openEditDialog', item.id)
}

function onSelectItem(item: any) {
  if (item) {
    if (Array.isArray(item)) {
      if (item.length > 0) {
        const ids = item.map((i: any) => i.id)
        emits('update:clickedItem', ids)
        emits('update:selectedItems', item)
      }
      else if (item.length === 0) {
        emits('update:clickedItem', [])
        emits('update:selectedItems', [])
      }
    }
    else {
      if (typeof item === 'object') {
        // ✅ Emitir el objeto completo, no solo el ID
        emits('update:clickedItem', item)
        emits('update:selectedItems', [item])
      }
    }
  }
  else {
    emits('update:clickedItem', null)
    emits('update:selectedItems', [])
  }
}

function onRowDoubleClick(event: { originalEvent: { type: string }, data: any }) {
  if (event.originalEvent.type === 'dblclick' && event.data) {
    emits('onRowDoubleClick', event.data)
  }
}

function onRowRightClick(event: any) {
  emits('onRowRightClick', event)
}

function onCellEditComplete(event: any, data: any) {
  emits('onCellEditComplete', { newDate: event, data, event })
}

function onTableCellEditComplete(event: any) {
  emits('onTableCellEditComplete', { data: event?.data, event, field: event?.field, newValue: event?.newValue, newData: event?.newData })
}

function showConfirmDelete(item: any) {
  clickedItem.value = item
  openDialogDelete.value = true
}

function closeDialogDelete() {
  openDialogDelete.value = false
}

function handleAction(action: 'edit' | 'delete', data: any) {
  if (action === 'edit') {
    onEdit(data)
  }
  if (action === 'delete') {
    showConfirmDelete(data)
  }
}

async function deleteItem(id: string, isLocal = false) {
  if (isLocal) {
    emits('onLocalDelete', clickedItem.value)
    closeDialogDelete()
  }
  else {
    try {
      await GenericService.delete(props.options?.moduleApi || '', props.options?.uriApi || '', id)
      closeDialogDelete()
      onListItem()
    }
    catch (error) {
      console.error(error)
    }
  }
}

async function getList(
  objApi: IObjApi | null = null,
  filter: IFilter[] = [],
  localItems: any[] = [],
  mapFunction?: (data: any) => any | undefined,
  sortOptions?: ISortOptions | undefined,
  objToSearch: IQueryToSearch = {
    query: '',
    keys: ['name', 'code'],
  }
) {
  try {
    let listItems: any[] = []
    if (localItems.length === 0 && objApi?.moduleApi && objApi.uriApi) {
      // Nueva lógica para evitar duplicados en el filtro
      const mappedFilters = objToSearch && objToSearch.query !== ''
        ? objToSearch.keys.map<IFilter>(key => ({
            key,
            operator: 'LIKE',
            value: objToSearch.query,
            logicalOperation: 'OR',
            type: 'filterSearch'
          }))
        : []
      // Eliminar filtros duplicados por clave y tipo
      const uniqueFilters = [...new Map([...mappedFilters, ...filter].map(item => [`${item.key}_${item.type}`, item])).values()]

      const payload: IQueryRequest = {
        filter: uniqueFilters,
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: sortOptions?.sortBy || 'createdAt',
        sortType: sortOptions?.sortType || 'DESC'
      }
      if (objApi) {
        const response = await GenericService.search(objApi.moduleApi, objApi.uriApi, payload)
        if (mapFunction) {
          listItems = response.data.map(mapFunction)
        }
        else {
          for (const iterator of response.data) {
            listItems = [...listItems, { id: iterator.id, name: objApi.keyValue ? iterator[objApi.keyValue] : iterator.name, code: iterator.code ? iterator.code : '' }]
          }
        }
      }
    }
    else {
      listItems = [...localItems]
    }
    return [...listItems]
  }
  catch (error) {
    console.error(error)
    return []
  }
}

async function getOptionsList() {
  try {
    for (const iterator of props.columns) {
      switch (iterator.type) {
        case 'text':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.CONTAINS }
          break
        case 'slot-text':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.CONTAINS }
          break
        case 'number':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.CONTAINS }
          break
        case 'select':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.IN }] }
          break
        case 'local-select':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.IN }] }
          break
        case 'date':
        case 'date-editable':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
          break
        case 'bool':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.EQUALS }
          break
        case 'custom-badge':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.IN }] }
          break
        case 'slot-select':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.IN }] }
          break
        default:
          break
      }

      if ((iterator.type === 'local-select' || iterator.type === 'select' || iterator.type === 'custom-badge' || iterator.type === 'slot-select') && (iterator.localItems && iterator.localItems?.length > 0)) {
        objListData[iterator.field] = [...iterator.localItems]
      }
    }
    objFilterToClear.value = JSON.parse(JSON.stringify(filters1.value))
  }
  catch (error) {
    console.error(error)
  }
}

async function getDataFromSelectors() {
  try {
    for (const iterator of props.columns) {
      if (iterator.type === 'select' || iterator.type === 'custom-badge' || iterator.type === 'slot-select') {
        objForLoadings.value[iterator.field] = false
        objForValues.value[iterator.field] = []
        const response = await getList(
          {
            moduleApi: iterator.objApi?.moduleApi || '',
            uriApi: iterator.objApi?.uriApi || '',
            keyValue: iterator.objApi?.keyValue
          },
          iterator.objApi?.filter as IFilter[] || [],
          iterator.localItems || [],
          iterator.objApi?.mapFunction,
          iterator.objApi?.sortOption
        )
        objListData[iterator.field] = []
        for (const item of response) {
          objListData[iterator.field] = [
            ...objListData[iterator.field],
            {
              ...item,
              name: 'code' in item ? `${item.code} - ${item.name}` : item.name
            }
          ]
        }
      }
    }
  }
  catch (error) {
    console.error(error)
  }
}

async function getDataFromFiltersSelectors(column: IColumn, objToSearch: IQueryToSearch) {
  try {
    objForLoadings.value[column.field] = true
    const response = await getList(
      {
        moduleApi: column.objApi?.moduleApi || '',
        uriApi: column.objApi?.uriApi || '',
        keyValue: column.objApi?.keyValue
      },
      column.objApi?.filter as IFilter[] || [],
      column.localItems || [],
      column.objApi?.mapFunction,
      column.objApi?.sortOption,
      objToSearch
    )
    objListData[column.field] = []
    for (const item of response) {
      objListData[column.field] = [
        ...objListData[column.field],
        {
          ...item,
          name: 'code' in item ? `${item.code} - ${item.name}` : item.name
        }
      ]
    }

    objForLoadings.value[column.field] = false
  }
  catch (error) {
    objForLoadings.value[column.field] = false
    console.error(error)
  }
}

function onSortField(event: any) {
  if (event && event.filters) {
    const shortAndFilter = {
      filter: event.filters,
      sortField: event.sortField,
      sortOrder: event.sortOrder === 1 ? ENUM_SHORT_TYPE.ASC : ENUM_SHORT_TYPE.DESC
    }
    emits('onSortField', { ...shortAndFilter })
  }
}

function haveFilterApplay(filtersValue: any, column: any) {
  let result = false
  if (column.type === 'bool') {
    result = filtersValue[column.field].value !== null
  }
  if (column.type === 'select' || column.type === 'local-select' || column.type === 'custom-badge' || column.type === 'slot-select') {
    result = filtersValue[column.field].constraints[0].value !== null
  }
  if (column.type === 'date' || column.type === 'date-editable') {
    result = filtersValue[column.field].constraints[0].value !== null
  }
  if (column.type === 'text' || column.type === 'slot-text' || column.type === 'number') {
    result = filtersValue[column.field].value
  }
  return result
}

function getNameById(id: string, array: IStandardObject[]) {
  const foundItem = array.find(item => item.id === id)
  return foundItem ? foundItem.name : ''
}

function rowClass(data: any) {
  return data.rowClass
}

function formatRangeDate(date: string): string {
  if (!date) {
    return 'No date'
  }

  const parsedDate = dayjs(date).toDate()
  const startDate = dayjs(date).format('YYYY-MM-DD')
  const endDate = dayjs(getLastDayOfMonth(parsedDate)).format('YYYY-MM-DD')

  return `${startDate} - ${endDate}`
}

function clearSelectedItems() {
  clickedItem.value = []
}

// ✅ No usar defineSlots() para evitar conflictos de tipos con slots dinámicos
// Los slots funcionarán perfectamente sin esta definición explícita

// Watchers
watch(() => props.data, async (newValue) => {
  // ✅ Evitar acumulación de elementos, limpiar primero
  if (props.options?.selectAllItemByDefault) {
    clickedItem.value = [...newValue] // Reemplazar, no acumular
  }
  else {
    if ('selectFirstItemByDefault' in props.options) {
      if (props.options?.selectFirstItemByDefault) {
        if (newValue.length > 0 && props.options?.selectionMode !== 'multiple') {
          clickedItem.value = newValue[0]
        }
      }
    }
  }
})

// ✅ Evitar loop infinito usando nextTick y una bandera
const isUpdatingSelection = ref(false)

watch(clickedItem, async (newValue) => {
  if (isUpdatingSelection.value) { return }

  isUpdatingSelection.value = true
  await nextTick()
  onSelectItem(newValue)
  isUpdatingSelection.value = false
})

// Lifecycle hooks
onMounted(() => {
  getDataFromSelectors()
  if (props.options?.selectAllItemByDefault) {
    clickedItem.value = props.data
  }
  else if ('selectFirstItemByDefault' in props.options) {
    if (props.options?.selectFirstItemByDefault) {
      if (props.data.length > 0 && props.options?.selectionMode !== 'multiple') {
        clickedItem.value = props.data[0]
      }
    }
  }
  if (props.selectedItems) {
    clickedItem.value = props.selectedItems
  }
})

// Initialize options
getOptionsList()

// Expose functions
defineExpose({ clearSelectedItems })
</script>

<template>
  <div v-if="props.options?.hasOwnProperty('showTitleBar') ? props.options?.showTitleBar : false">
    <slot name="datatable-toolbar">
      <Toolbar class="mb-4">
        <template #start>
          <div class="my-2">
            <h5 class="m-0">
              {{ options?.tableName }}
            </h5>
          </div>
        </template>
        <template #end>
          <FileUpload mode="basic" accept="image/*" :max-file-size="1000000" label="Importar" choose-label="Importar" class="mr-2 inline-block" />
        </template>
      </Toolbar>
    </slot>
  </div>

  <BlockUI :blocked="options?.loading || parentComponentLoading" class="block-ui-container">
    <div
      v-if="options?.loading"
      class="flex flex-column align-items-center justify-content-center full-size"
    >
      <i class="pi pi-spin pi-spinner" style="font-size: 2.6rem" />
    </div>

    <div class="card p-0 mb-0">
      <DataTable
        :id="'componentTableId' in props ? props.componentTableId : ''"
        v-model:filters="filters1"
        v-model:selection="clickedItem"
        v-model:expanded-rows="expandedRows"
        context-menu
        resizable-columns
        column-resize-mode="fit"
        :meta-key-selection="metaKey"
        :selection-mode="options?.selectionMode ?? 'single'"
        :filter-display="modeFilterDisplay"
        :row-class="rowClass"
        sort-mode="single"
        :value="data"
        data-key="id"
        show-gridlines
        striped-rows
        removable-sort
        :lazy="props.isCustomSorting"
        scrollable
        :scroll-height="'scrollHeight' in props?.options ? props?.options?.scrollHeight : '70vh'"
        edit-mode="cell"
        style="border: 0"
        @sort="onSortField"
        @update:selection="onSelectItem"
        @update:filters="onChangeFilters"
        @row-dblclick="onRowDoubleClick"
        @row-contextmenu="onRowRightClick"
        @cell-edit-complete="onTableCellEditComplete"
        @row-expand="onRowExpand"
        @row-collapse="onRowCollapse"
        @column-resize-end="($event) => {
          console.log('column-resize-end', $event);
        }"
      >
        <template #loading>
          <div class="flex flex-column flex-wrap align-items-center justify-content-center py-8">
            <span class="flex flex-column align-items-center justify-content-center">
              <i class="pi pi-spin pi-spinner" style="font-size: 2.6rem" />
            </span>
          </div>
        </template>

        <template #empty>
          <div v-if="'showCustomEmptyTable' in props?.options ? props.options.showCustomEmptyTable : false">
            <slot name="emptyTable" :data="{ messageForEmptyTable }" />
          </div>
          <div v-else class="flex flex-column flex-wrap align-items-center justify-content-center py-8">
            <span v-if="!options?.loading" class="flex flex-column align-items-center justify-content-center">
              <div class="row">
                <i class="pi pi-trash mb-3" style="font-size: 2rem;" />
              </div>
              <div class="row">
                <p>{{ messageForEmptyTable }}</p>
              </div>
            </span>
          </div>
        </template>

        <Column
          v-if="options?.selectionMode" :selection-mode="options?.selectionMode ?? undefined"
          header-style="width: 3rem"
        />

        <Column v-if="options?.hasOwnProperty('expandableRows') ? options?.expandableRows : false" expander style="width: 2rem">
          <template #rowtogglericon="{ rowExpanded }">
            <i :class="rowExpanded ? 'pi pi-minus' : 'pi pi-plus'" style="border: 1px solid #dee2e6; padding: 2px; border-radius: 10%;" />
          </template>
        </Column>

        <Column
          v-for="(column) in columns"
          :key="column.field"
          :field="column.field"
          :show-filter-match-modes="false"
          :sortable="column.hasOwnProperty('sortable') ? column.sortable : true"
          :filter-field="column.field"
          :frozen="column.frozen"
          align-frozen="right"
          class="custom-table-head" :class="column.columnClass"
          :style="{
            maxWidth: column?.maxWidth ? column?.maxWidth : 'auto',
            height: '20px',
          }"
        >
          <template #header>
            <span v-tooltip="column.tooltip">{{ column.header }}</span>
          </template>

          <template #body="{ data: rowData }">
            <slot v-if="column.type === 'slot-text'" :name="`column-${column.field}`" :data="rowData" :column="column" />
            <slot v-if="column.type === 'slot-select'" :name="`column-${column.field}`" :data="rowData" :column="column" />
            <slot v-if="column.type === 'slot-icon'" :name="`column-${column.field}`" :data="rowData" :column="column" />
            <slot v-if="column.type === 'slot-date-editable'" :name="`column-${column.field}`" :data="rowData" :column="column" />
            <slot v-if="column.type === 'slot-bagde'" :name="`column-${column.field}`" :data="rowData" :column="column" />

            <span v-if="column.type === 'icon' && column.icon">
              <Button :icon="column.icon" class="p-button-rounded p-button-text w-2rem h-2rem" aria-label="Submit" />
            </span>

            <span v-else-if="column.type === 'date-editable'" v-tooltip.top="rowData[column.field] ? dayjs(rowData[column.field]).format('YYYY-MM-DD') : 'No date'" :class="rowData[column.field] ? '' : 'font-bold p-error'" class="truncate w-full">
              <span v-if="column.props?.isRange">{{ formatRangeDate(rowData[column.field]) }}</span>
              <span v-else>{{ rowData[column.field] ? dayjs(rowData[column.field]).format('YYYY-MM-DD') : 'No date' }}</span>
            </span>

            <span v-else-if="column.type === 'date'" v-tooltip.top="rowData[column.field] ? dayjs(rowData[column.field]).format('YYYY-MM-DD') : 'No date'" :class="rowData[column.field] ? '' : 'font-bold p-error'" class="truncate">
              {{ rowData[column.field] ? dayjs(rowData[column.field]).format('YYYY-MM-DD') : '' }}
            </span>

            <span v-else-if="column.type === 'datetime'" v-tooltip.top="rowData[column.field] ? dayjs(rowData[column.field]).format('YYYY-MM-DD') : 'No date'" :class="rowData[column.field] ? '' : 'font-bold p-error'" class="truncate">
              {{ rowData[column.field] ? dayjs(rowData[column.field]).format('YYYY-MM-DD hh:mm a') : 'No date' }}
            </span>

            <span v-if="typeof rowData[column.field] === 'object' && rowData[column.field] !== null" v-tooltip.top="rowData[column.field].name" class="truncate">
              <span v-if="column.type === 'select' || column.type === 'local-select'">
                {{ rowData[column.field].name }}
              </span>
            </span>

            <span v-else-if="column.type === 'image'">
              <NuxtImg v-if="rowData[column.field]" :src="rowData[column.field]" alt="Avatar" class="avatar" />
              <div v-else>
                <Avatar icon="pi pi-image" style="background-color: #dee9fc; color: #1a2551" shape="circle" size="large" />
              </div>
            </span>

            <span v-else-if="column.type === 'bool'">
              <Badge v-tooltip.top="rowData[column.field] ? 'Active' : 'Inactive'" :value="rowData[column.field].toString().charAt(0).toUpperCase() + rowData[column.field].toString().slice(1)" :severity="rowData[column.field] ? 'success' : 'danger'" class="success" />
            </span>

            <span v-else-if="column.type === 'number'">
              {{ formatNumber(rowData[column.field]) }}
            </span>

            <span v-else-if="column.type === 'custom-badge'">
              <Badge
                v-tooltip.top="rowData[column.field].toString()"
                :value="rowData[column.field]"
                :class="column.statusClassMap?.find(e => e.status === rowData[column.field])?.class"
              />
            </span>

            <span v-else>
              <span v-if="column.type === 'local-select'" v-tooltip.top="rowData[column.field].name" class="truncate">
                {{ (column.hasOwnProperty('localItems') && column.localItems) ? getNameById(rowData[column.field], column.localItems) : '' }}
              </span>
              <span v-else-if="column.type === 'text'" v-tooltip.top="rowData[column.field] ? rowData[column.field].toString() : ''" class="truncate">
                <span v-if="column.badge && rowData[column.field]">
                  <Badge v-tooltip.top="rowData[column.field]" :value="rowData[column.field] ? 'True' : 'False'" :severity="rowData[column.field] ? 'success' : 'danger'" class="}" />
                </span>
                <span v-else>
                  {{ rowData[column.field] }}
                </span>
              </span>
            </span>
          </template>

          <template v-if="(column.type === 'image' || column.type === 'icon' || column.showFilter === false) ? false : options?.hasOwnProperty('showFilters') ? options?.showFilters : true" #filter="{ filterModel, filterCallback }">
            <div v-if="column.type === 'text' || column.type === 'slot-text'" class="flex flex-column">
              <Dropdown
                v-if="true"
                v-model="filterModel.matchMode"
                :options="menuItemsString"
                option-label="label"
                placeholder="Select a operator"
                class="w-full mb-2"
              />
              <InputText
                v-model="filterModel.value"
                type="text"
                class="p-column-filter w-full"
                placeholder="Write a text"
              />

              <Menu :id="column.field" :ref="modeFilterDisplay === 'row' ? 'menuFilterForRowDisplay' : menuFilter[column.field]" :model="menuItemsString" :popup="true" class="w-full md:w-9rem">
                <template #item="{ item, props: menuProps }">
                  <a v-ripple class="flex align-items-center" v-bind="menuProps.action" @click="filterModel.matchMode = item.id; filterCallback()">
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>
            </div>

            <div v-if="column.type === 'number'" class="flex flex-column">
              <Dropdown
                v-if="true"
                v-model="filterModel.matchMode"
                :options="menuItemsNumeric"
                option-label="label"
                placeholder="Select a operator"
                class="w-full mb-2"
              />
              <InputNumber
                v-model="filterModel.value"
                class="p-column-filter w-full"
                placeholder="Write a number"
                :min-fraction-digits="2"
                :max-fraction-digits="4"
              />

              <Menu :id="column.field" :ref="modeFilterDisplay === 'row' ? 'menuFilterForRowDisplay' : menuFilter[column.field]" :model="menuItemsNumeric" :popup="true" class="w-full md:w-9rem">
                <template #item="{ item, props: menuProps }">
                  <a v-ripple class="flex align-items-center" v-bind="menuProps.action" @click="filterModel.matchMode = item.id; filterCallback()">
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>
            </div>

            <div v-if="column.type === 'select' || column.type === 'local-select' || column.type === 'custom-badge' || column.type === 'slot-select'" class="flex flex-column">
              <Dropdown
                v-if="true"
                key="selectMultiple1"
                v-model="filterModel.matchMode"
                :options="menuItemsSelect"
                option-label="label"
                placeholder="Select a operator"
                class="w-full mb-2"
              />

              <DebouncedMultiSelectComponent
                id="autocomplete"
                class="w-full h-2rem align-items-center"
                field="name"
                item-value="id"
                :max-selected-labels="2"
                :model="objForValues[column.field]"
                :suggestions="[...objListData[column.field]]"
                :loading="objForLoadings[column.field]"
                @change="($event) => {
                  objForValues[column.field] = $event
                  filterModel.value = $event
                }"
                @load="async($event) => {
                  const objQueryToSearch = {
                    query: $event,
                    keys: ['name', 'code'],
                  }
                  await getDataFromFiltersSelectors(column, objQueryToSearch)
                }"
              />

              <Menu id="overlay_menu_filter" :ref="modeFilterDisplay === 'row' ? 'menuFilterForRowDisplay' : menuFilter[column.field]" :model="menuItemsSelect" :popup="true" class="w-full md:w-9rem">
                <template #item="{ item, props: menuProps }">
                  <a v-ripple class="flex align-items-center" v-bind="menuProps.action" @click="filterModel.matchMode = item.id; filterCallback()">
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>
            </div>

            <div v-if="column.type === 'bool'" class="flex align-items-center">
              <TriStateCheckbox :id="column.field" ref="menuFilter" v-model="filterModel.value" />
              <label :for="column.field" class="font-bold ml-3"> {{ column.header }} </label>
            </div>

            <div v-if="column.type === 'date' || column.type === 'date-editable'" class="flex flex-column">
              <Dropdown
                v-if="true"
                v-model="filterModel.matchMode"
                :options="menuItemsDate"
                option-label="label"
                placeholder="Select a operator"
                class="w-full mb-2"
              />
              <Calendar
                v-model="filterModel.value"
                type="text"
                date-format="yy-mm-dd"
                placeholder="yyyy-mm-dd"
                mask="99/99/9999"
                class="p-column-filter w-full"
              />
            </div>

            <div v-if="column.type === 'datetime' || column.type === 'datetime-editable'" class="flex flex-column">
              <Dropdown
                v-if="true"
                v-model="filterModel.matchMode"
                :options="menuItemsDate"
                option-label="label"
                placeholder="Select a operator"
                class="w-full mb-2"
              />
              <Calendar
                v-model="filterModel.value"
                type="text"
                show-time
                hour-format="12"
                date-format="yy-mm-dd"
                placeholder="yyyy-mm-dd"
                mask="99/99/9999"
                class="p-column-filter w-full"
              />
            </div>
          </template>

          <template #filtericon>
            <Button v-if="haveFilterApplay(filters1, column)" severity="secondary" type="button" class="bg-primary" style="position: relative;">
              <i class="pi pi-filter-fill text-white" />
              <strong class="p-error" style="position: absolute; top: 0; right: 0;">*</strong>
            </Button>
            <i v-else class="pi pi-filter text-white" />
          </template>

          <template #filterclear="{ field }">
            <Button type="button" label="Clear" severity="secondary" @click="clearIndividualFilter(field)" />
          </template>

          <template #editor="{ data: editData, field }">
            <template v-if="column.type === 'date-editable'">
              <slot :name="`column-${column.field}`" :item="{ data: editData, field, column, onCellEditComplete }">
                <Calendar
                  v-model="editData[field]"
                  :manual-input="false"
                  style="width: 100%"
                  :view="(column.props?.calendarMode as 'month' | 'date' | 'year') || 'month'"
                  date-format="yy-mm-dd"
                  :max-date="column.props?.maxDate"
                  @update:model-value="onCellEditComplete($event, editData)"
                />
              </slot>
            </template>
            <template v-else-if="column.editable && (column.type === 'text' || column.type === 'number')">
              <slot :name="`column-editable-${column.field}`" :item="{ data: editData, field, column, onCellEditComplete }">
                <InputText v-if="column.type === 'text'" v-model="editData[field]" style="width: 100%" autofocus fluid />
                <InputNumber v-if="column.type === 'number'" v-model="editData[field]" style="width: 100%" autofocus fluid />
              </slot>
            </template>
          </template>
        </Column>

        <Column v-if="options?.hasOwnProperty('showAcctions') ? options?.showAcctions : false" field="action" header="" :style="{ 'width': `${props.actionsWidth}px`, 'text-align': 'center' }">
          <template #body="{ data: actionData }">
            <span v-if="options?.actionsAsMenu ? options?.actionsAsMenu : false">
              <Button type="button" icon="pi pi-ellipsis-v" severity="secondary" text aria-haspopup="true" aria-controls="overlay_menu" @click="toggleMenu($event)" />

              <Menu id="overlay_menu" ref="menu" :model="menuItems" :popup="true" class="w-full md:w-9rem">
                <template #item="{ item, props: menuProps }">
                  <a v-ripple class="flex align-items-center" v-bind="menuProps.action" @click="handleAction(item.action, actionData)">
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>
            </span>

            <span v-else>
              <Button
                v-if="options?.hasOwnProperty('showEdit') ? options?.showEdit : true"
                v-tooltip.left="'Edit'"
                type="button"
                icon="pi pi-pencil"
                severity="primary"
                class="mx-1"
                text
                aria-haspopup="true"
                aria-controls="overlay_menu"
                :loading="actionData.loadingEdit"
                @click="onEdit(actionData)"
              />
              <Button
                v-if="options?.hasOwnProperty('showDelete') ? options?.showDelete : true"
                v-tooltip.left="'Delete'"
                type="button"
                icon="pi pi-trash"
                class="mx-1"
                severity="danger"
                text
                aria-haspopup="true"
                aria-controls="overlay_menu"
                :loading="actionData.loadingDelete"
                @click="showConfirmDelete(actionData)"
              />

              <Button
                v-if="options?.hasOwnProperty('showLocalDelete') ? options?.showLocalDelete : false"
                v-tooltip.left="'Delete'"
                type="button"
                icon="pi pi-trash"
                class="mx-1"
                severity="danger"
                text
                aria-haspopup="true"
                aria-controls="overlay_menu"
                :loading="actionData.loadingDelete"
                @click="showConfirmDelete(actionData)"
              />
            </span>
          </template>
        </Column>

        <template #expansion="slotProps">
          <slot name="expansion" :data="slotProps.data" />
        </template>

        <slot name="datatable-footer" />
      </DataTable>
    </div>
  </BlockUI>

  <div v-if="'showPagination' in props?.options ? props.options.showPagination : true" class="flex justify-content-center align-items-center mt-2 card py-0">
    <div v-if="props.showLocalPagination">
      <slot name="pagination" />
    </div>
    <div v-else class="flex justify-content-between align-items-center w-full">
      <div class="flex align-items-center w-15rem">
        <strong v-if="props.options.showSelectedItems">Selected Item: {{ clickedItem?.length || 0 }}</strong>
      </div>

      <div class="flex align-items-center">
        <Paginator
          :rows="Number(props.pagination?.limit ?? 50)"
          :total-records="props.pagination?.totalElements ?? 0"
          :rows-per-page-options="[10, 20, 30, 50, 100, 200, 500]"
          @page="onChangePageOrLimit($event)"
        />
        <Badge class="px-2 py-3 flex align-items-center" severity="secondary">
          <span>Total:</span>
          <slot name="pagination-total" :total="props.pagination?.totalElements">
            <span class="font-bold">
              {{ props.pagination?.totalElements }}
            </span>
          </slot>
        </Badge>
      </div>

      <div class="flex align-items-center w-15rem">
        <slot name="pagination-right" />
      </div>
    </div>
  </div>

  <DialogDelete
    v-if="clickedItem"
    :open-dialog="openDialogDelete"
    :data="clickedItem"
    :message="props.options?.messageToDelete ? props.options.messageToDelete : 'Are you sure you want to delete the selected item?'"
    @on-close-dialog="closeDialogDelete"
    @on-delete-confirmed="deleteItem($event, options?.hasOwnProperty('showLocalDelete') ? options?.showLocalDelete : false)"
  />
</template>

<style lang="scss" scoped>
.block-ui-container {
  position: relative;
}

.full-size {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
}

.rounded-border {
  border-radius: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-size: cover;
  background-position: center;
}

:deep(.p-datatable-row-expansion > td) {
  padding: 0;
  background-color: #F5F5F5;
}
</style>
