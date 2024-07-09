<script setup lang="ts">
import dayjs from 'dayjs'
import { FilterMatchMode, FilterOperator } from 'primevue/api'
import BlockUI from 'primevue/blockui'
import type { DataTableFilterMeta } from 'primevue/datatable'
import type { PageState } from 'primevue/paginator'
import type { IFilter, IStandardObject } from '../fields/interfaces/IFieldInterfaces'
import type { IData } from './interfaces/IModelData'
import type { IColumn, IObjApi } from './interfaces/ITableInterfaces'
import DialogDelete from './components/DialogDelete.vue'
import { ENUM_OPERATOR_DATE, ENUM_OPERATOR_SELECT, ENUM_OPERATOR_STRING } from './enums'
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
      showToolBar?: boolean
      showTitleBar?: boolean
      messageToDelete: string
      search?: boolean
      showContextMenu?: boolean
      menuModel?: Array<any>
      isTabView?: boolean
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
  (e: 'onSortField', value: any): void
  (e: 'update:doubleClicked', value: any): void
}>()

const menu = ref()
const menuFilter: Ref = ref()
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

const menuItems = ref([
  {
    items: [
      {
        label: 'Editar',
        icon: 'pi pi-pencil',
        action: 'edit'
      },
      {
        label: 'Eliminar',
        icon: 'pi pi-trash',
        action: 'delete'
      }
    ]
  }
])
const cm = ref()
const selectedItem = ref()

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

function onDoubleClickItem(item: any) {
  if (item?.data) {
    emits('update:doubleClicked', item?.data?.id)
  }
}

function toggleMenu(event: Event) {
  menu.value.toggle(event)
}
function toggleMenuFilter(event: Event, i: number) {
  menuFilter.value[i].toggle(event)
}

function onEdit(item: any) {
  emits('openEditDialog', item.id)
}

function onSelectItem(item: any) {
  if (item) {
    emits('update:clickedItem', item?.id)
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
        case 'select':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.IN }] }
          // filters1.value[iterator.field] = { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] }
          break
        case 'local-select':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.IN }] }
          // filters1.value[iterator.field] = { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] }
          break
        case 'date':
          filters1.value[iterator.field] = { constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
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
      sortOrder: event.sortOrder === 1 ? 'ASC' : 'DES'
    }
    emits('onSortField', { ...shortAndFilter })
  }
}

function getNameById(id: string, array: IStandardObject[]) {
  const foundItem = array.find(item => item.id === id)
  return foundItem ? foundItem.name : ''
}

watch(() => props.data, async (newValue) => {
  if (newValue.length > 0) {
    clickedItem.value = props.data[0]
  }
})

onMounted(() => {
  getDataFromSelectors()
  if (props.data.length > 0) {
    clickedItem.value = props.data[0]
  }
})
function onRowContextMenu(event) {
  cm.value.show(event.originalEvent)
}

getOptionsList()
</script>

<template>
  <Toolbar v-if="props.options?.hasOwnProperty('showTitleBar') ? props.options?.showTitleBar : false" class="mb-4">
    <template #start>
      <div class="my-2">
        <h5 class="m-0">
          {{ options?.tableName }}
        </h5>
      </div>
    </template>

    <template #end>
      <FileUpload
        mode="basic" accept="image/*" :max-file-size="1000000" label="Importar" choose-label="Importar"
        class="mr-2 inline-block"
      />
    </template>
  </Toolbar>

  <BlockUI :blocked="options?.loading">
    <div class="card p-0">
      <div v-if="options?.showContextMenu">
        <ContextMenu ref="cm" :model="options.menuModel" @hide="selectedItem = null">
          <template #item="props">
            <Button
              :class="`w-full my-2 mx-1 ${props.item.class}`" :icon="props.item.icon"
              :label="props.item.label as string"
              @click="() => { if (props.item.command) props.item.command(selectedItem) }"
            />
          </template>
        </ContextMenu>
      </div>
      <DataTable
        v-model:contextMenuSelection="selectedItem"
        v-model:filters="filters1" v-model:selection="clickedItem"
        :context-menu="options?.showContextMenu" :meta-key-selection="metaKey" selection-mode="single"
        filter-display="row" sort-mode="single" :value="data" data-key="id" show-gridlines
        striped-rows removable-sort lazy scrollable scroll-height="60vh" :filters="filters1" row-group-mode="subheader"
        @row-dblclick="onDoubleClickItem" @row-contextmenu="onRowContextMenu" @sort="onSortField" @update:selection="onSelectItem"
        @update:filters="onChangeFilters"
      >
        <template v-if="props.options?.hasOwnProperty('showToolBar') ? props.options?.showToolBar : false" #header>
          <div class="flex flex-column md:flex-row md:justify-content-between md:align-items-center">
            <span class="flex mt-2 md:mt-0">
              <div class="my-2">
                <h5 class="m-0">{{ options?.tableName }}</h5>
              </div>
              <Divider layout="vertical" />
              <Button
                v-tooltip.right="'Clear'" type="button" icon="pi pi-filter-slash" severity="primary" label="Clear"
                outlined @click="clearFilter1(filters1)"
              />
              <Divider layout="vertical" />
              <span v-if="props.options?.search || false">
                <InputText
                  v-model="filters1.search.value" class="w-full sm:w-auto"
                  placeholder="Press enter to search..."
                />
                <Button
                  label="Buscar" icon="pi pi-plus" class="mx-2" severity="primary"
                  @click="onChangeFilters(filters1)"
                >
                  <i class="pi pi-search" />
                </Button>
              </span>
            </span>
            <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2">
              <Button
                v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" class="mr-2" severity="primary"
                @click="openNew"
              />
            </div>
          </div>
        </template>

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
        <Column v-if="options?.showAcctions" selection-mode="single" header-style="width: 3rem">
          <template #body="templateProps">
            <slot name="row-selector-body" :item="templateProps.data" />
          </template>
        </Column>
        <!-- :show-filter-match-modes="column.type !== 'bool' " -->
        <Column
          v-for="(column, index) in columns" :key="column.field" :field="column.field" :header="column.header"
          :show-filter-match-modes="false" :sortable="options?.showAcctions" :filter-field="column.field" class="custom-table-head"
          :style="{ width: column.type === 'image' ? '80px' : 'auto' }"
        >
          <template #body="{ data }">
            <span v-if="typeof data[column.field] === 'object' && data[column.field] !== null">
              <span v-if="column.type === 'select' || column.type === 'local-select'">
                {{ data[column.field].name }}
              </span>
              <span v-if="column.type === 'date'">
                {{ data[column.field] ? dayjs(data[column.field]).format('YYYY-MM-DD') : '' }}
              </span>
            </span>
            <span v-else-if="column.type === 'image'">
              <NuxtImg v-if="data[column.field]" :src="data[column.field]" alt="Avatar" class="avatar" />
              <div v-else>
                <Avatar
                  icon="pi pi-image" style="background-color: #dee9fc; color: #1a2551" shape="circle"
                  size="large"
                />
              </div>
            </span>
            <span v-else-if="column.type === 'bool'">
              <Badge
                v-tooltip.top="data[column.field] ? 'Active' : 'Inactive'"
                :value="data[column.field].toString().charAt(0).toUpperCase() + data[column.field].toString().slice(1)"
                :severity="data[column.field] ? 'success' : 'danger'" class="success"
              />
            </span>
            <span v-else>
              <span v-if="column.type === 'local-select'">
                {{ (column.hasOwnProperty('localItems') && column.localItems) ? getNameById(data[column.field],
                                                                                            column.localItems) : '' }}
              </span>
              <span v-else>
                {{ data[column.field] }}
              </span>
            </span>
          </template>
          <template
            v-if="options?.hasOwnProperty('showFilters') ? options?.showFilters : true"
            #filter="{ filterModel, filterCallback }"
          >
            <div v-if="column.type === 'text'" class="flex">
              <InputText
                v-model="filterModel.value" type="text" class="p-column-filter w-full"
                placeholder="Write a text" @change="filterCallback()"
              />
              <Button
                type="button" icon="pi pi-filter" text aria-haspopup="true"
                :aria-controls="`overlayPanel_${index}`" @click="toggleMenuFilter($event, index)"
              />

              <Menu :id="column.field" ref="menuFilter" :model="menuItemsString" :popup="true" class="w-full md:w-9rem">
                <template #item="{ item, props }">
                  <a
                    v-ripple class="flex align-items-center" v-bind="props.action"
                    @click="filterModel.matchMode = item.id; filterCallback()"
                  >
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>
            </div>

            <div v-if="column.type === 'select' || column.type === 'local-select'" class="flex">
              <MultiSelect
                v-model="filterModel.value" :options="objListData[column.field]" option-label="name"
                placeholder="Select one or more" class="p-column-filter w-full" :max-selected-labels="2"
                @change="filterCallback()"
              >
                <template #option="slotProps">
                  <div class="flex align-items-center gap-2">
                    <span>{{ slotProps.option.name }}</span>
                  </div>
                </template>
              </MultiSelect>
              <Button
                type="button" icon="pi pi-filter" text aria-haspopup="true" aria-controls="overlay_menu_filter"
                @click="toggleMenuFilter($event, index, data)"
              />
              <Menu
                id="overlay_menu_filter" ref="menuFilter" :model="menuItemsSelect" :popup="true"
                class="w-full md:w-9rem"
              >
                <template #item="{ item, props }">
                  <a
                    v-ripple class="flex align-items-center" v-bind="props.action"
                    @click="filterModel.matchMode = item.id; filterCallback()"
                  >
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>
            </div>

            <div v-if="column.type === 'bool'" class="flex align-items-center">
              <TriStateCheckbox
                :id="column.field" ref="menuFilter" v-model="filterModel.value"
                @update:model-value="filterCallback()"
              />
              <label :for="column.field" class="font-bold ml-3"> {{ column.header }} </label>
            </div>

            <div v-if="column.type === 'date'" class="flex">
              <Calendar
                v-model="filterModel.value" type="text" date-format="yy-mm-dd" placeholder="yyyy-mm-dd"
                mask="99/99/9999" class="p-column-filter w-full" @date-select="filterCallback()"
              />
              <Button
                type="button" icon="pi pi-filter" text aria-haspopup="true" aria-controls="overlay_menu_filter"
                @click="toggleMenuFilter($event, index, data)"
              />
              <Menu
                id="overlay_menu_filter" ref="menuFilter" :model="menuItemsDate" :popup="true"
                class="w-full md:w-9rem"
              >
                <template #item="{ item, props }">
                  <a
                    v-ripple class="flex align-items-center" v-bind="props.action"
                    @click="filterModel.matchMode = item.id; filterCallback()"
                  >
                    <span :class="item.icon" />
                    <span class="ml-2">{{ item.label }}</span>
                  </a>
                </template>
              </Menu>
            </div>
          </template>
        </Column>
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
                v-if="options?.hasOwnProperty('showEdit') ? options?.showEdit : true" v-tooltip.left="'Editar'"
                type="button" icon="pi pi-pencil" severity="primary" class="mx-1" text aria-haspopup="true"
                aria-controls="overlay_menu" :loading="data.loadingEdit" @click="onEdit(data)"
              />
              <Button
                v-if="options?.hasOwnProperty('showDelete') ? options?.showDelete : true"
                v-tooltip.left="'Eliminar'" type="button" icon="pi pi-trash" class="mx-1" severity="danger" text
                aria-haspopup="true" aria-controls="overlay_menu" :loading="data.loadingDelete"
                @click="showConfirmDelete(data)"
              />

              <!-- Local -->
              <Button
                v-if="options?.hasOwnProperty('showLocalDelete') ? options?.showLocalDelete : false"
                v-tooltip.left="'Eliminar'" type="button" icon="pi pi-trash" class="mx-1" severity="danger" text
                aria-haspopup="true" aria-controls="overlay_menu" :loading="data.loadingDelete"
                @click="showConfirmDelete(data)"
              />
            </span>
          </template>
        </Column>
      </DataTable>
    </div>

    <div class="flex flex-row justify-content-end  w-full align-items-center mt-3 card p-0">
      <slot name="pagination-left" />

      <div class="align-items-center justify-content-between" style="width: 65%; display: flex;">
        <div class="flex align-items-center">
          <div class="w-fit">
            <Paginator
              v-if="props.pagination" :rows="Number(props.pagination.limit) || 50"
              :total-records="props.pagination.totalElements" :rows-per-page-options="[10, 20, 30, 50]"
              @page="onChangePageOrLimit($event)"
            />
          </div>
          <Divider layout="vertical" />
          <div class="flex  w-fit align-items-center mx-3 gap-4 justify-content-between ">
            <slot name="pagination-total-section" :total="props.pagination?.totalElements">
              <Badge class="px-2 py-3 flex align-items-center" severity="secondary">
                <span>
                  Total:
                </span>
                <span class="font-bold font">
                  {{ props.pagination?.totalElements }}
                </span>
              </Badge>
            </slot>
          </div>
        </div>

        <slot name="pagination-right" />
      </div>
    </div>
  </BlockUI>

  <!-- Dialog Delete -->
  <DialogDelete
    v-if="clickedItem" :open-dialog="openDialogDelete" :data="clickedItem"
    :message="props.options?.messageToDelete ? props.options.messageToDelete : '¿Estás seguro que desea eliminar el elemento seleccionado?'"
    @on-close-dialog="closeDialogDelete"
    @on-delete-confirmed="deleteItem($event, options?.hasOwnProperty('showLocalDelete') ? options?.showLocalDelete : false)"
  />
</template>

<style lang="scss" scoped>
.rounded-border {
  border-radius: 10px;
}

.avatar {
  width: 40px;
  /* Ancho del avatar */
  height: 40px;
  /* Alto del avatar */
  border-radius: 50%;
  /* Para hacerlo redondeado */
  background-size: cover;
  /* Para ajustar el tamaño de la imagen de fondo */
  background-position: center;
  /* Para centrar la imagen de fondo */
}
</style>
