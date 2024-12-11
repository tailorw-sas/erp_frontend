<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { useToast } from 'primevue/usetoast'
import dayjs from 'dayjs'
import Menu from 'primevue/menu'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import { getLastDayOfMonth } from '~/utils/helpers'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'
// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const listItems = ref<any[]>([])
const selectedElements = ref<string[]>([])
const selectedDate = ref<Date>()
const loadingSearch = ref(false)
const menu = ref() // Reference for the menu component
const allDefaultItem = { id: 'All', name: 'All', code: 'All' }
const filterToSearch = ref<IData>({
  search: '',
  active: true,
  hotel: [allDefaultItem],
})
const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'invoice-close-operation',
})

const hotelList = ref<any[]>([])
const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel', keyValue: 'name' }, sortable: true },
  { field: 'date', header: 'Current Close Operation', type: 'date-editable', width: '220px', widthTruncate: '50px', props: { isRange: true } },
  // { field: 'status', header: 'Active', type: 'bool', width: '25px', widthTruncate: '25px', showFilter: false },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice Close Operation',
  moduleApi: 'invoicing',
  uriApi: 'invoice-close-operation',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: '',
  selectionMode: 'multiple'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [{
    key: 'hotel.status',
    operator: 'EQUALS',
    value: 'ACTIVE',
    logicalOperation: 'AND',
    type: 'filterSearch',
  }],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------

async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    listItems.value = []
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages
    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.hotel.status)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel')) {
        iterator.hotel = { id: iterator.hotel.id, name: `${iterator.hotel.code} - ${iterator.hotel.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'beginDate')) {
        iterator.date = dayjs(iterator.beginDate).toDate()
      }
    }
    listItems.value = [...dataList]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]

  if (filterToSearch.value.hotel.length > 0) {
    const filteredItems = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All')
    if (filteredItems.length > 0) {
      const ids = filterToSearch.value.hotel.map((e: any) => e.id)
      payload.value.filter = [...payload.value.filter, {
        key: 'hotel.id',
        operator: 'IN',
        value: ids,
        logicalOperation: 'AND',
        type: 'filterSearch',
      }]
    }
  }
  // filterToSearch.value.active ? 'ACTIVE' : 'INACTIVE',
  payload.value.filter = [...payload.value.filter, {
    key: 'hotel.status',
    operator: 'EQUALS',
    value: 'ACTIVE',
    logicalOperation: 'AND',
    type: 'filterSearch',
  }]
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  payload.value.filter = [...payload.value.filter, {
    key: 'hotel.status',
    operator: 'EQUALS',
    value: 'ACTIVE',
    logicalOperation: 'AND',
    type: 'filterSearch',
  }]
  filterToSearch.value.search = ''
  filterToSearch.value.hotel = [allDefaultItem]
  filterToSearch.value.active = true
  getList()
}

function resetListItems() {
  payload.value.page = 0
  getList()
}

async function onMultipleSelect(data: any) {
  selectedElements.value = data
}

async function updateItem(item: { [key: string]: any }) {
  const payload: { [key: string]: any } = {}
  payload.beginDate = item.newDate ? dayjs(item.newDate).format('YYYY-MM-DD') : ''
  const lastDay = getLastDayOfMonth(item.newDate)
  payload.endDate = item.newDate ? dayjs(lastDay).format('YYYY-MM-DD') : ''
  payload.hotel = typeof item.data.hotel === 'object' ? item.data.hotel.id : item.data.hotel
  payload.status = statusToString(item.data.status)
  await GenericService.update(confApi.moduleApi, confApi.uriApi, item.data.id || '', payload)
  toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
}

async function saveItem(item: { [key: string]: any }) {
  options.value.loading = true
  const currentList = [...listItems.value]
  listItems.value = []
  let successOperation = true
  try {
    await updateItem(item)
  }
  catch (error: any) {
    successOperation = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    options.value.loading = false
    if (successOperation) {
      getList()
    }
    else {
      listItems.value = [...currentList]
    }
  }
}

async function saveMultiple() {
  options.value.loading = true
  const currentList = [...listItems.value]
  listItems.value = []
  let successOperation = true
  try {
    const payload: { [key: string]: any } = {}
    payload.beginDate = selectedDate.value ? dayjs(selectedDate.value).format('YYYY-MM-DD') : ''
    const lastDay = selectedDate.value ? getLastDayOfMonth(selectedDate.value) : ''
    payload.endDate = selectedDate.value ? dayjs(lastDay).format('YYYY-MM-DD') : ''
    const selectedHotels = currentList.filter(item => selectedElements.value.includes(item.id))
    payload.hotels = selectedHotels.map((e: any) => e.hotel.id)
    await GenericService.update(confApi.moduleApi, confApi.uriApi, 'all', payload, 'PUT')
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
  }
  catch (error: any) {
    successOperation = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    options.value.loading = false
    if (successOperation) {
      getList()
    }
    else {
      listItems.value = [...currentList]
    }
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  if (parseFilter) {
    const index = parseFilter.findIndex((filter: IFilter) => filter.key === 'date')
    if (index !== -1) {
      parseFilter[index].key = 'beginDate'
    }
  }
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField === 'date' ? 'beginDate' : event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

const disabledSearch = computed(() => {
  return false
})

const disabledClearSearch = computed(() => {
  return filterToSearch.value.active && filterToSearch.value.hotel.length === 0
})

async function getHotelList(query: string) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'name',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'OR'
            },
            {
              key: 'code',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'OR'
            },
            {
              key: 'status',
              operator: 'EQUALS',
              value: 'ACTIVE',
              logicalOperation: 'AND'
            }
          ],
          query: '',
          pageSize: 20,
          page: 0,
          sortBy: 'name',
          sortType: ENUM_SHORT_TYPE.DESC
        }

    const response = await GenericService.search(confHotelApi.moduleApi, confHotelApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = [allDefaultItem]
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error on loading hotel list:', error)
  }
}

function handleCalendarSelect(date: Date) {
  selectedDate.value = date
  menu.value.hide()
  saveMultiple()
}

function toggle(event: any) {
  menu.value.toggle(event)
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
onMounted(async () => {
  if (useRuntimeConfig().public.loadTableData) {
    await getList()
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Invoice Closing Date
    </h3>
    <div class="my-2 flex justify-content-end px-0">
      <Button
        v-tooltip.left="'Multiple Close Operation'" label="Select Date" icon="pi pi-calendar" severity="primary"
        :disabled="selectedElements.length < 2" @click="toggle"
      />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 order-0 md:order-1">
      <div class="card p-0 m-0">
        <Accordion :active-index="0">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header">
                Search Fields
              </div>
            </template>
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex align-items-center gap-2">
                <label for="email">Hotel</label>
                <DebouncedAutoCompleteComponent
                  id="autocomplete"
                  field="code"
                  item-value="id"
                  multiple
                  :model="filterToSearch.hotel"
                  :suggestions="hotelList"
                  @change="($event) => {
                    if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                      filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                    }
                    else {
                      filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                    }
                  }"
                  @load="($event) => getHotelList($event)"
                >
                  <template #option="{ item }">
                    {{ item.name }}
                  </template>
                </DebouncedAutoCompleteComponent>
              </div>
              <div v-if="false" class="flex align-items-center">
                <Checkbox v-model="filterToSearch.active" :binary="true" />
                <label class="ml-2 font-bold"> Active </label>
              </div>
              <div class="flex align-items-center">
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :disabled="disabledClearSearch" :loading="loadingSearch" @click="clearFilterToSearch" />
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <!--      <div v-if="selectedElements.length > 1" class="flex justify-content-end custom-icon-button-container">
        <Button v-tooltip.left="'Select date'" icon="pi pi-calendar" text rounded class="mr-1" @click="clearForm" />
      </div> -->
      <DynamicTable
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @update:clicked-item="onMultipleSelect($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
        @on-cell-edit-complete="saveItem"
      />
      <div v-if="false" class="flex justify-content-end">
        <Button class="ml-2" icon="pi pi-times" severity="secondary" @click="() => { navigateTo('/') }" />
      </div>
    </div>
  </div>
  <Menu id="invoice_co" ref="menu" :popup="true">
    <template #start>
      <Calendar v-model="selectedDate" inline view="month" @update:model-value="handleCalendarSelect" />
    </template>
  </Menu>
</template>

<style lang="scss">
.custom-icon-button-container {
  background-color: #E7F5FF;
  .p-button-icon-only .pi {
    font-size: 1.3em;
  }
}
</style>
