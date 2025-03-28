<script setup lang="ts">
import dayjs from 'dayjs'
import { FilterMatchMode, FilterOperator } from 'primevue/api'
import BlockUI from 'primevue/blockui'
import type { DataTableFilterMeta } from 'primevue/datatable'
import type { PageState } from 'primevue/paginator'
import { reactive, ref } from 'vue'
import type { PropType } from 'vue'
import type { IFilter, IStandardObject } from '../fields/interfaces/IFieldInterfaces'
import type { IData } from './interfaces/IModelData'
import type { IColumn, IObjApi } from './interfaces/ITableInterfaces'
import DialogDelete from './components/DialogDelete.vue'
import { ENUM_OPERATOR_DATE, ENUM_OPERATOR_NUMERIC, ENUM_OPERATOR_SELECT, ENUM_OPERATOR_STRING } from './enums'
import { GenericService } from '~/services/generic-services'

const props = defineProps({
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
      actionsAsMenu: boolean
      showAcctions?: boolean
      showCreate?: boolean
      showEdit?: boolean
      showAttend?: boolean
      showDelete?: boolean
      showLocalDelete?: boolean
      showFilters?: boolean
      showTitleBar?: boolean
      messageToDelete: string
      search?: boolean
    }>
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
})

const emits = defineEmits<{
  (e: 'onListItem'): void
  (e: 'onConfirmCreate'): void
  (e: 'onChangePagination', value: PageState): void
  (e: 'onConfirmEdit', value: string): void
  (e: 'onChangeFilter', value: DataTableFilterMeta | null): void
  (e: 'openEditDialog', value: any): void
  (e: 'openAttendDialog', value: any): void
  (e: 'openDeleteDialog', value: any): void
  (e: 'onLocalDelete', value: any): void
  (e: 'update:clickedItem', value: any): void
  (e: 'update:doubleClicked', value: any): void
  (e: 'onSortField', value: any): void
  (e: 'onExpandField', value: any): void
  (e: 'onSelectField', value: any): void
  (e: 'onRowRightClick', value: any): void
}>()

const expandedRows = ref<any>({})

const dataMap = reactive<Map<string, any>>(new Map())

const menu = ref()
// const menuFilter: { [key: string]: Ref<any> } = {
//   code: ref(null),
//   name: ref(null),
//   description: ref(null),
// }
type FilterDisplayMode = 'row' | 'menu' | undefined

const modeFilterDisplay: Ref<FilterDisplayMode> = ref('menu')
const menuFilter: { [key: string]: Ref<any> } = {}
const menuFilterForRowDisplay: Ref = ref()
const menuItemsNumeric = ref(ENUM_OPERATOR_NUMERIC)

// Iteramos sobre el array de campos y añadimos las propiedades a menuFilter
props.columns.forEach((field) => {
  menuFilter[field.field] = ref(null)
})

const openDialogDelete = ref(false)
const clickedItem = ref<IData | undefined>({})
const metaKey = ref(false)
const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')

const objeto: { [key: string]: any } = {}
const objetoFilter: { [key: string]: any } = {
  search: { value: null, matchMode: FilterMatchMode.CONTAINS },
}
// Asignar el objeto a objListData y hacerlo reactivo
const objListData = reactive(objeto)

const filters1 = ref(objetoFilter
  //   {
  //         search: { value: null, matchMode: FilterMatchMode.CONTAINS },
  //         moduleName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
  //         action: { value: null, matchMode: FilterMatchMode.IN },
  //         'country.name': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
  //         representative: { value: null, matchMode: FilterMatchMode.IN },
  //         date: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] },
  //         balance: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
  //         status: { operator: FilterOperator.OR, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
  //         activity: { value: [0, 100], matchMode: FilterMatchMode.BETWEEN },
  //         verified: { value: null, matchMode: FilterMatchMode.EQUALS }
  //    }
)
const filtersReset = reactive(objetoFilter
  // {
  // search: { value: null, matchMode: FilterMatchMode.CONTAINS },
  // moduleName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
  // action: { value: null, matchMode: FilterMatchMode.IN },
  // 'country.name': { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
  // representative: { value: null, matchMode: FilterMatchMode.IN },
  // date: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] },
  // balance: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
  // status: { operator: FilterOperator.OR, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
  // activity: { value: [0, 100], matchMode: FilterMatchMode.BETWEEN },
  // verified: { value: null, matchMode: FilterMatchMode.EQUALS }
  // }
)

const objFilterToClear = ref()

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
// const menuItemsBoolean = ref(ENUM_OPERATOR_BOOLEAN)

function onListItem() {
  emits('onListItem')
}

function onChangePageOrLimit(event: PageState) {
  emits('onChangePagination', event)
}

function onChangeFilters(value: DataTableFilterMeta) {
  emits('onChangeFilter', value)
}

function openNew() {
  emits('onConfirmCreate')
}

function clearFilter1() {
  filters1.value = { ...filtersReset }
  emits('onChangeFilter', null)
}

function toggleMenu(event: Event) {
  menu.value.toggle(event)
}

function onEdit(item: any) {
  emits('openEditDialog', item.id)
}

function onSelectItem(item: any) {
  if (item) {
    emits('update:clickedItem', item?.id)
  }
}

function onDoubleClickItem(item: any) {
  if (item?.data) {
    emits('update:doubleClicked', { id: item?.data?.id, type: item?.data?.invoiceType, status: item?.data?.status })
  }
}

function onAttend(item: any) {
  emits('openAttendDialog', item)
}

function showConfirmDelete(item: any) {
  clickedItem.value = item
  openDialogDelete.value = true
}

function closeDialogDelete() {
  openDialogDelete.value = false
}

function onRowRightClick(event: any) {
  emits('onRowRightClick', event)
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
      await GenericService.deleteItem(props.options?.moduleApi || '', props.options?.uriApi || '', id)
      closeDialogDelete()
      onListItem()
    }
    catch (error) {
      console.error(error)
    }
  }
}

async function getList(objApi: IObjApi | null = null, filter: IFilter[] = [], localItems: any[] = []) {
  try {
    let listItems: any[] = [] // Cambio el tipo de elementos a any
    if (localItems.length === 0 && objApi?.moduleApi && objApi.uriApi) {
      const payload = {
        filter,
        query: '',
        pageSize: 200,
        page: 0
      }
      if (objApi) {
        const response = await GenericService.search(objApi.moduleApi, objApi.uriApi, payload)
        for (const iterator of response.data) {
          listItems = [...listItems, { id: iterator.id, name: objApi.keyValue ? iterator[objApi.keyValue] : iterator.name }]
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
        case 'number':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.CONTAINS }
          break
        case 'select':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.IN }] }
          // filters1.value[iterator.field] = { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] }
          break
        case 'local-select':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.IN }] }
          // filters1.value[iterator.field] = { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] }
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
        default:
          break
      }

      if ((iterator.type === 'local-select' || iterator.type === 'select' || iterator.type === 'custom-badge') && (iterator.localItems && iterator.localItems?.length > 0)) {
        objListData[iterator.field] = [...iterator.localItems]
      }
    }
    objFilterToClear.value = JSON.parse(JSON.stringify(filters1.value))
  }
  catch (error) {
    console.error(error)
  }
}
function toggleMenuFilter(event: Event, i: number | string) {
  if (modeFilterDisplay.value === 'menu') {
    menuFilter[i].value[0].toggle(event)
  }
  else {
    menuFilterForRowDisplay.value[i].toggle(event)
  }
}

async function getOptionsListOldVersion() {
  try {
    for (const iterator of props.columns) {
      switch (iterator.type) {
        case 'text':

          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.CONTAINS }
          break
        case 'select':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.IN }
          // filters1.value[iterator.field] = { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] }
          break
        case 'local-select':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.IN }
          // filters1.value[iterator.field] = { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] }
          break
        case 'date':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.DATE_IS }
          break
        case 'bool':
          filters1.value[iterator.field] = { value: null, matchMode: FilterMatchMode.EQUALS }
          break
        default:
          break
      }

      if ((iterator.type === 'local-select' || iterator.type === 'select') && (iterator.localItems && iterator.localItems?.length > 0)) {
        objListData[iterator.field] = [...iterator.localItems]
      }
    }
  }
  catch (error) {
    console.error(error)
  }
}

async function getDataFromSelectors() {
  try {
    for (const iterator of props.columns) {
      if (iterator.type === 'select') {
        const response = await getList({ moduleApi: iterator.objApi?.moduleApi || '', uriApi: iterator.objApi?.uriApi || '', keyValue: iterator.objApi?.keyValue }, [], [])
        objListData[iterator.field] = response
      }
    }
  }
  catch (error) {

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

function getNameById(id: string, array: IStandardObject[]) {
  const foundItem = array.find(item => item.id === id)
  return foundItem ? foundItem.name : ''
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

function haveFilterApplay(filtersValue: any, column: any) {
  let result = false

  if (column.type === 'bool') {
    result = filtersValue[column.field].value !== null
  }
  if (column.type === 'select' || column.type === 'local-select' || column.type === 'custom-badge') {
    result = filtersValue[column.field].constraints[0].value !== null
  }
  if (column.type === 'date' || column.type === 'date-editable') {
    result = filtersValue[column.field].constraints[0].value !== null
  }
  if (column.type === 'text') {
    result = filtersValue[column.field]?.value
  }
  return result
}

function clearIndividualFilter(param1) {
  // const filterReset = { ...objFilterToClear[param1] }

  filters1.value[param1] = { ...objFilterToClear.value[param1] }

  // Llama a la función callback
  onChangeFilters(filters1.value)
}

function onCellEditComplete(event: any, data: any) {
  emits('onCellEditComplete', { newDate: event, data })
}

async function onRowExpand({ data }: any) {
  // check if already loaded
  if (dataMap.has(data.id)) {
    return
  }

  emits('onExpandField', data?.id)
  emits('onSelectField', data)

  const { $api } = useNuxtApp()
  const serverUrl = useRequestURL()

  const url = `${serverUrl.origin}/site/${props.options?.moduleApi}/${props.options?.uriApi}/${data.id}`
  const response = await $api<any>(url)
  dataMap.set(data.id, response)
}
function onRowCollapse(event: { data: { name: any } }) {
  // toast.add({ severity: 'success', summary: 'Product Collapsed', detail: event.data.name, life: 3000 })
  emits('onExpandField', '')
  emits('onSelectField', null)
}

onMounted(() => {
  getDataFromSelectors()
})

getOptionsList()
</script>

<template>
  <Toolbar v-if="props.options?.hasOwnProperty('showTitleBar') ? props.options?.showTitleBar : false" class="mb-1 text-white" style="background-color: #0F8BFD;">
    <template #start>
      <div class="my-1">
        <h5 class="m-0">
          {{ options?.tableName }}
        </h5>
      </div>
    </template>

    <template #end>
      <slot name="topbar-end">
        <FileUpload
          mode="basic" accept="image/*" :max-file-size="1000000" label="Importar" choose-label="Importar"
          class="mr-2 inline-block"
        />
      </slot>
    </template>
  </Toolbar>

  <BlockUI :blocked="options?.loading">
    <div class="card p-0">
      <DataTable
        v-model:filters="filters1"
        v-model:expandedRows="expandedRows"
        v-model:selection="clickedItem"
        :filter-display="modeFilterDisplay"
        :meta-key-selection="metaKey"
        selection-mode="single"
        sort-mode="single"
        :value="data"
        data-key="id"
        show-gridlines
        striped-rows
        removable-sort
        lazy
        scrollable
        scroll-height="75vh"
        @row-dblclick="onDoubleClickItem"
        @row-expand="onRowExpand"
        @row-collapse="onRowCollapse"
        @sort="onSortField"
        @update:selection="onSelectItem"
        @update:filters="onChangeFilters"
        @row-contextmenu="onRowRightClick"
      >
        <template #empty>
          <div class="flex flex-column flex-wrap align-items-center justify-content-center py-8">
            <span v-if="!options?.loading" class="flex flex-column align-items-center justify-content-center">
              <div class="row">
                <i class="pi pi-trash mb-3" style="font-size: 2rem;" />
              </div>
              <div class="row">
                <p>{{ messageForEmptyTable }}</p>
              </div>
            </span>
            <span v-else class="flex flex-column align-items-center justify-content-center">
              <i class="pi pi-spin pi-spinner" style="font-size: 2.6rem" />
            </span>
          </div>
        </template>
        <!-- :show-filter-match-modes="column.type !== 'bool' " -->
        <Column expander style="width: 1rem">
          <template #rowtogglericon="props">
            <i :class="`${props.rowExpanded ? 'pi pi-minus' : 'pi pi-plus'}`" style=" color: black; border: 1px solid black; border-radius: 4px; padding: 0px;" </i />
          </template>
        </Column>
        <Column selection-mode="single" header-style="width: 3rem">
          <template #body="templateProps">
            <slot name="row-selector-body" :item="templateProps.data" />
          </template>
        </Column>
        <Column
          v-for="(column, index) in columns" :key="column.field"
          :field="column.field"
          :show-filter-match-modes="false"
          :sortable="column.hasOwnProperty('sortable') ? column.sortable : true"
          :filter-field="column.field"
          class="custom-table-head" :class="column.columnClass"
          :style="{
            width: column.type === 'image' ? '80px' : column?.width ? column?.width : 'auto',
            minWidth: column?.width ? column?.width : 'auto',
          // maxWidth: column?.width ? column?.width : 'auto',
          }"
        >
          <template #header>
            <span v-tooltip="column.tooltip">{{ column.header }}</span>
          </template>
          <template #body="{ data }">
            <slot :name="`column-${column.field}`" :item="data[column.field]">
              <span v-if="column.type === 'icon' && column.icon">
                <Button :icon="column.icon" class="p-button-rounded p-button-text w-2rem h-2rem" aria-label="Submit" />
              </span>

              <span v-if="typeof data[column.field] === 'object' && data[column.field] !== null" v-tooltip.top="data[column.field].name" class="truncate">
                <span v-if="column.type === 'select' || column.type === 'local-select'">
                  {{ data[column.field].name }}
                </span>
              </span>
              <span v-else-if="column.type === 'date'" v-tooltip.top="data[column.field] ? dayjs(data[column.field]).format('YYYY-MM-DD') : 'No date'" :class="data[column.field] ? '' : 'font-bold p-error'" class="truncate">
                {{ data[column.field] ? dayjs(data[column.field]).format('YYYY-MM-DD') : 'No date' }}
              </span>
              <span v-else-if="column.type === 'date-editable'" v-tooltip.top="data[column.field] ? dayjs(data[column.field]).format('YYYY-MM-DD') : 'No date'" :class="data[column.field] ? '' : 'font-bold p-error'" class="truncate w-full">
                <span v-if="column.props?.isRange">{{ formatRangeDate(data[column.field]) }}</span>
                <span v-else>{{ data[column.field] ? dayjs(data[column.field]).format('YYYY-MM-DD') : 'No date' }}</span>
              </span>
              <span v-else-if="column.type === 'image'">
                <NuxtImg v-if="data[column.field]" :src="data[column.field]" alt="Avatar" class="avatar" />
                <div v-else>
                  <Avatar icon="pi pi-image" style="background-color: #dee9fc; color: #1a2551" shape="circle" size="large" />
                </div>
              </span>
              <span v-else-if="column.type === 'bool'">
                <Badge v-tooltip.top="data[column.field] ? 'Manual' : 'Imported'" :value="data[column.field]?.toString()?.charAt(0)?.toUpperCase() + data[column.field]?.toString()?.slice(1)" :severity="data[column.field] ? 'success' : 'danger'" class="success" />
              </span>
              <span v-else-if="column.type === 'number'">
                {{ formatNumber(data[column.field]) }}
              </span>
              <span v-else-if="column.type === 'custom-badge'">
                <Badge
                  v-tooltip.top="data[column.field]" :value="data[column.field].toString()"
                  :class="column.statusClassMap?.find(e => e.status === data[column.field])?.class"
                />
              </span>
              <span v-else>
                <span v-if="column.type === 'local-select'" v-tooltip.top="data[column.field].name" class="truncate">
                  {{ (column.hasOwnProperty('localItems') && column.localItems) ? getNameById(data[column.field], column.localItems) : '' }}
                </span>
                <span v-else-if="column.type === 'text'" v-tooltip.top="data[column.field] ? data[column.field].toString() : ''" class="truncate">
                  <span v-if="column.badge && data[column.field]">
                    <Badge v-tooltip.top="data[column.field]" :value="data[column.field] ? 'True' : 'False'" :severity="data[column.field] ? 'success' : 'danger'" class="}" />
                  </span>
                  <span v-else>
                    {{ data[column.field] }}
                  </span>
                </span>
              </span>
            </slot>
          </template>
          <template v-if="(column.type === 'image' || column.type === 'icon' || column.showFilter === false) ? false : options?.hasOwnProperty('showFilters') ? options?.showFilters : true" #filter="{ filterModel, filterCallback }">
            <div v-if="column.type === 'text'" class="flex flex-column">
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
              <!-- @change="filterCallback()" -->
              <Button
                v-if="false"
                type="button"
                icon="pi pi-filter"
                text
                aria-haspopup="true"
                :aria-controls="`overlayPanel_${index}`"
                @click="toggleMenuFilter($event, modeFilterDisplay === 'menu' ? column.field : index)"
              />

              <Menu :id="column.field" :ref="modeFilterDisplay === 'row' ? 'menuFilterForRowDisplay' : menuFilter[column.field]" :model="menuItemsString" :popup="true" class="w-full md:w-9rem">
                <template #item="{ item, props }">
                  <a v-ripple class="flex align-items-center" v-bind="props.action" @click="filterModel.matchMode = item.id; filterCallback()">
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
                <template #item="{ item, props }">
                  <a v-ripple class="flex align-items-center" v-bind="props.action" @click="filterModel.matchMode = item.id; filterCallback()">
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>
            </div>

            <div v-if="column.type === 'select' || column.type === 'local-select' || column.type === 'custom-badge'" class="flex flex-column">
              <Dropdown
                v-if="true"
                key="selectMultiple1"
                v-model="filterModel.matchMode"
                :options="menuItemsSelect"
                option-label="label"
                placeholder="Select a operator"
                class="w-full mb-2"
              />

              <MultiSelect
                v-model="filterModel.value"
                :options="objListData[column.field]"
                option-label="name"
                placeholder="Select one or more"
                class="p-column-filter w-full"
                :max-selected-labels="2"
              >
                <template #option="slotProps">
                  <div class="flex align-items-center gap-2">
                    <span>{{ slotProps.option.name }}</span>
                  </div>
                </template>
              </MultiSelect>
              <Button
                v-if="false"
                type="button"
                icon="pi pi-filter"
                text aria-haspopup="true"
                aria-controls="overlay_menu_filter"
                @click="toggleMenuFilter($event, modeFilterDisplay === 'menu' ? column.field : index)"
              />
              <Menu id="overlay_menu_filter" :ref="modeFilterDisplay === 'row' ? 'menuFilterForRowDisplay' : menuFilter[column.field]" :model="menuItemsSelect" :popup="true" class="w-full md:w-9rem">
                <template #item="{ item, props }">
                  <a v-ripple class="flex align-items-center" v-bind="props.action" @click="filterModel.matchMode = item.id; filterCallback()">
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
          <template v-if="column.type === 'date-editable'" #editor="{ data, field }">
            <Calendar
              v-model="data[field]" :manual-input="false"
              style="width: 100%" view="month"
              date-format="yy-mm-dd"
              @update:model-value="onCellEditComplete($event, data)"
            />
          </template>
        </Column>
        <template #expansion="slotProps">
          <div v-if="dataMap.get(slotProps.data.id) === undefined" class="p-4">
            <Skeleton class="mb-2" />
            <Skeleton width="10rem" class="mb-2" />
            <Skeleton width="5rem" class="mb-2" />
            <Skeleton height="2rem" class="mb-2" />
            <Skeleton width="10rem" height="4rem" />
          </div>
          <slot name="expanded-item" :item-id="slotProps.data.id" :loaded-item="dataMap.get(slotProps.data.id)">
            <div class="p-1">
              <pre>{{ dataMap.get(slotProps.data.id) }}</pre>
            </div>
          </slot>
        </template>
        <Column
          v-if="options?.hasOwnProperty('showAcctions') ? options?.showAcctions : false" field="action" header=""
          style="width: 90px; text-align: center;"
        >
          <template #body="{ data, index }">
            <span v-if="options?.actionsAsMenu ? options?.actionsAsMenu : false">
              <Button
                type="button" icon="pi pi-ellipsis-v" severity="secondary" text aria-haspopup="true"
                aria-controls="overlay_menu" @click="toggleMenu($event, index, data)"
              />
              <!-- <Menu v-if="true" ref="menu" id="overlay_menu" :model="menuItems" :popup="true" /> -->

              <Menu id="overlay_menu" ref="menu" :model="menuItems" :popup="true" class="w-full md:w-9rem">
                <template #item="{ item, props }">
                  <a
                    v-ripple class="flex align-items-center" v-bind="props.action"
                    @click="handleAction(item.action, data)"
                  >
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>

            </span>
            <span v-else>
              <Button
                v-if="options?.hasOwnProperty('showAttend') ? options?.showAttend : false" type="button"
                icon="pi pi-file-check" severity="primary" class="mx-1" text aria-haspopup="true"
                aria-controls="overlay_menu" :loading="data.loadingEdit" @click="onAttend(data)"
              />
              <Button
                v-if="options?.hasOwnProperty('showEdit') ? options?.showEdit : true" v-tooltip.left="'Edit'"
                type="button" icon="pi pi-pencil" severity="primary" class="mx-1" text aria-haspopup="true"
                aria-controls="overlay_menu" :loading="data.loadingEdit" @click="onEdit(data)"
              />
              <Button
                v-if="options?.hasOwnProperty('showDelete') ? options?.showDelete : true"
                v-tooltip.left="'Delete'" type="button" icon="pi pi-trash" class="mx-1" severity="danger" text
                aria-haspopup="true" aria-controls="overlay_menu" :loading="data.loadingDelete"
                @click="showConfirmDelete(data)"
              />

              <!-- Local -->
              <Button
                v-if="options?.hasOwnProperty('showLocalDelete') ? options?.showLocalDelete : false"
                v-tooltip.left="'Delete'" type="button" icon="pi pi-trash" class="mx-1" severity="danger" text
                aria-haspopup="true" aria-controls="overlay_menu" :loading="data.loadingDelete"
                @click="showConfirmDelete(data)"
              />
            </span>
          </template>
        </Column>
        <slot name="datatable-footer" />
      </DataTable>
    </div>

    <div class="flex justify-content-center align-items-center mt-3 card p-0">
      <Paginator
        v-if="props.pagination" :rows="Number(props.pagination.limit) || 50"
        :total-records="props.pagination.totalElements" :rows-per-page-options="[10, 20, 30, 50, 100, 200, 500]"
        @page="onChangePageOrLimit($event)"
      />
      <Divider layout="vertical" />
      <div class="flex align-items-center mx-3">
        <Badge class="px-2 py-3 flex align-items-center" severity="secondary">
          <span>
            Total:
          </span>
          <span class="font-bold">
            {{ props.pagination?.totalElements }}
          </span>
        </Badge>
      </div>

      <slot name="pagination-right" />
    </div>
  </BlockUI>

  <!-- Dialog Delete -->
  <DialogDelete
    v-if="clickedItem" :open-dialog="openDialogDelete" :data="clickedItem"
    :message="props.options?.messageToDelete ? props.options.messageToDelete : 'Are you sure you want to delete the selected item?'"
    @on-close-dialog="closeDialogDelete"
    @on-delete-confirmed="deleteItem($event, options?.hasOwnProperty('showLocalDelete') ? options?.showLocalDelete : false)"
  />
</template>

<style lang="scss" scoped>
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
  width: 40px; /* Ancho del avatar */
  height: 40px; /* Alto del avatar */
  border-radius: 50%; /* Para hacerlo redondeado */
  background-size: cover; /* Para ajustar el tamaño de la imagen de fondo */
  background-position: center; /* Para centrar la imagen de fondo */
}

:deep(.p-datatable-row-expansion > td) {
  padding: 0;
  background-color: #F5F5F5
}
</style>
