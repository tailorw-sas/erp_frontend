<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import type { IData, IPatient } from '~/components/table/interfaces/IModelData'
import { GenericService } from '~/services/generic-services'
import type { IFilter, IFormField, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'

// VARIABLES -----------------------------------------------------------------------------------------
const listItems = ref<IPatient[]>([])
const loadingSearch = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

// FORM CONFIG -------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'name', name: 'Name' },
  { id: 'email', name: 'Email' },
  { id: 'userName', name: 'User Name' },
]
// -------------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns = ref<IColumn[]>([
  // { field: 'image', header: 'Image', type: 'image' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'lastName', header: 'Last Name', type: 'text' },
  { field: 'email', header: 'Email', type: 'text' },
  { field: 'userName', header: 'User Name', type: 'text' },
  { field: 'userType', header: 'User Type', type: 'local-select', localItems: ENUM_USER_TYPE },
  { field: 'status', header: 'Status', type: 'local-select', localItems: ENUM_STATUS },
  { field: 'createdAt', header: 'Created At', type: 'date' },
])
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Users',
  moduleApi: 'identity',
  uriApi: 'users',
  loading: false,
  showToolBar: false,
  showAcctions: true,
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

function searchAndFilter() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'name',
    sortType: 'DES'
  }
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }
  getList()
}

function clearFilterToSearch() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'name',
    sortType: 'DES'
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getList()
}
async function getList() {
  options.value.loading = true
  listItems.value = []

  const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

  const { data: dataList, page, size, totalElements, totalPages } = response

  pagination.value.page = page
  pagination.value.limit = size
  pagination.value.totalElements = totalElements
  pagination.value.totalPages = totalPages

  for (const iterator of dataList) {
    listItems.value = [...listItems.value, { ...iterator, loadingEdit: false, loadingDelete: false }]
  }
  options.value.loading = false
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

function isSelectField(field: any) {
  const column = columns.value.find(column => column.field === field)
  return column && (column.type === 'select' || column.type === 'local-select')
}
function isLocalItemsField(field: any) {
  const column = columns.value.find(column => column.field === field)
  return column && (column.localItems && column.localItems?.length > 0)
}

function getTransformedKey(keyName: string) {
  let keyResult = ''
  if (keyName === 'moduleName') {
    keyResult = 'module'
  }
  if (!isLocalItemsField(keyName) && keyResult !== '') {
    keyResult = `${keyResult}.id`
  }
  else if (!isLocalItemsField(keyName) && keyResult === '') {
    keyResult = `${keyName}.id`
  }
  else if (isLocalItemsField(keyName) && keyResult === '') {
    keyResult = `${keyName}`
  }
  return keyResult
}

function existsInPayloadFilter(key: string, array: IFilter[]): boolean {
  return array.some(filter => filter.key === key)
}

async function getEventFromTable(payloadFilter: any) {
  if (typeof payloadFilter === 'object') {
    let arrayFilter: IFilter[] = []
    for (const key in payloadFilter) {
      if (Object.prototype.hasOwnProperty.call(payloadFilter, key) && key !== 'search') {
        // Para los tipos Select
        if (isSelectField(key)) {
          const element = payloadFilter[key]

          if (element && Array.isArray(element.value) && element.value.length > 0) {
            for (const iterator of element.value) {
              const ketTemp = getTransformedKey(key)
              if (!existsInPayloadFilter(ketTemp, arrayFilter)) {
                const objFilter: IFilter = {
                  key: ketTemp,
                  operator: element.matchMode.toUpperCase(),
                  value: [iterator.id],
                  logicalOperation: 'AND'
                }
                arrayFilter = [...arrayFilter, objFilter]
              }
              else {
                const filterKey = ketTemp
                const index = arrayFilter.findIndex(item => item.key === filterKey)
                if (index !== -1) {
                  arrayFilter[index].value = [...arrayFilter[index].value, iterator.id]
                }
              }
            }
          }
        }
        else { // En caso de que no sea un filtro de tipo SELECT
          const element = payloadFilter[key]
          if (element.value || element.value === false) {
            const objFilter: IFilter = {
              key,
              operator: element.matchMode.toUpperCase() === 'CONTAINS' ? 'LIKE' : element.matchMode.toUpperCase(),
              value: element.value,
              logicalOperation: 'AND'
            }
            arrayFilter = [...arrayFilter, objFilter]
          }
        }
      }
    }
    // Eliminando elmentos duplicados
    arrayFilter = arrayFilter.filter((value, index, self) => {
      return self.indexOf(value) === index
    })
    payload.value.filter = [...arrayFilter]
    getList()
  }
}

async function openCreateDialog() {
  await navigateTo({ path: 'user/create' })
}

async function openEditDialog(item: any) {
  await navigateTo({ path: `user/edit/${item}` })
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getEventFromTable(event.filter)
  }
}
// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getList()
})
// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
  }
})

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Manage Users
    </h3>
    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="openCreateDialog" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12">
      <div class="card p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header">
                Filters
              </div>
            </template>
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex align-items-center gap-2">
                <label for="email3">Criteria</label>
                <div class="w-full lg:w-auto">
                  <Dropdown
                    v-model="filterToSearch.criterial"
                    :options="[...ENUM_FILTER]"
                    option-label="name"
                    placeholder="Criteria"
                    return-object="false"
                    class="align-items-center w-full"
                    show-clear
                    @update:model-value="filterToSearch.search = ''"
                  />
                </div>
              </div>
              <div class="flex align-items-center gap-2">
                <label for="email">Search</label>
                <div class="w-full lg:w-auto">
                  <IconField icon-position="left" class="w-full">
                    <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                    <InputIcon class="pi pi-search" />
                  </IconField>
                </div>
              </div>
              <div class="flex align-items-center">
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :disabled="disabledClearSearch" :loading="loadingSearch" @click="clearFilterToSearch" />
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <DynamicTable
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="openCreateDialog"
        @open-edit-dialog="openEditDialog($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="getEventFromTable"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      />
    </div>
  </div>
</template>
