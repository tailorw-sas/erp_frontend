<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import dayjs from 'dayjs'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import { validateEntityStatus } from '#imports'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const formReload = ref(0)
const loadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const loadingDelete = ref(false)
const filterToSearch = ref<IData>({
  search: '',
  hotel: [],
})
const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'invoice-close-operation',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'beginDate',
    header: 'Begin Date',
    dataType: 'date',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The begin date field is required',
      invalid_type_error: 'The begin date field is required',
    })
  },
  {
    field: 'endDate',
    header: 'End Date',
    dataType: 'date',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The end date field is required',
      invalid_type_error: 'The end date field is required',
    })
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mt-3 mb-3',
    headerClass: 'mb-1'
  },
]

const item = ref<GenericObject>({
  hotel: null,
  beginDate: '',
  endDate: '',
  status: true
})

const itemTemp = ref<GenericObject>({
  hotel: null,
  beginDate: '',
  endDate: '',
  status: true
})

const hotelList = ref<any[]>([])
const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})
// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'hotel', header: 'Hotel', type: 'text', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel', keyValue: 'code' }, sortable: true },
  { field: 'beginDate', header: 'From', type: 'date' },
  { field: 'endDate', header: 'To', type: 'date' },
  { field: 'status', header: 'Active', type: 'bool' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice Close Operation',
  moduleApi: 'invoicing',
  uriApi: 'invoice-close-operation',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: ''
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'DES'
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
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fields[0].disabled = false
  updateFieldProperty(fields, 'status', 'disabled', true)
  formReload.value++
}

async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel')) {
        iterator.hotel = `${iterator.hotel.code} - ${iterator.hotel.name}`
      }
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]

    if (listItems.value.length > 0) {
      idItemToLoadFirstTime.value = listItems.value[0].id
    }
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
    const ids = filterToSearch.value.hotel.map((e: any) => e.id)
    payload.value.filter = [...payload.value.filter, {
      key: 'hotel.id',
      operator: 'IN',
      value: ids,
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.search = ''
  filterToSearch.value.hotel = []
  getList()
}

function resetListItems() {
  payload.value.page = 0
  getList()
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)

      if (response) {
        hotelList.value = [response.hotel]
        item.value.id = response.id
        item.value.hotel = response.hotel
        item.value.hotel.status = 'ACTIVE' // todo: temnporal hasta que se arregle en el back
        item.value.status = statusToBoolean(response.status)
        const newDate = new Date(response.beginDate)
        newDate.setDate(newDate.getDate() + 1)
        item.value.beginDate = newDate || null
        item.value.endDate = response.endDate ? dayjs(response.endDate).format('YYYY-MM-DD') : null
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Item could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    payload.beginDate = payload.beginDate ? dayjs(payload.beginDate).format('YYYY-MM-DD') : ''
    payload.endDate = payload.endDate ? dayjs(payload.endDate).format('YYYY-MM-DD') : ''
    payload.hotel = typeof payload.hotel === 'object' ? payload.hotel.id : payload.hotel
    payload.status = statusToString(payload.status)
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }

  payload.beginDate = payload.beginDate ? dayjs(payload.beginDate).format('YYYY-MM-DD') : ''
  payload.endDate = payload.endDate ? dayjs(payload.endDate).format('YYYY-MM-DD') : ''
  payload.hotel = typeof payload.hotel === 'object' ? payload.hotel.id : payload.hotel
  payload.status = statusToString(payload.status)
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
  toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    clearForm()
    getList()
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true
  if (idItem.value) {
    try {
      await updateItem(item)
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
      await createItem(item)
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
    getList()
  }
}

function requireConfirmationToSave(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    saveItem(item)
  }
  else {
    const { event } = item
    confirm.require({
      target: event.currentTarget,
      group: 'headless',
      header: 'Save the record',
      message: 'Do you want to save the change?',
      rejectLabel: 'Cancel',
      acceptLabel: 'Accept',
      accept: () => {
        saveItem(item)
      },
      reject: () => {}
    })
  }
}
function requireConfirmationToDelete(event: any) {
  if (!useRuntimeConfig().public.showDeleteConfirm) {
    deleteItem(idItem.value)
  }
  else {
    confirm.require({
      target: event.currentTarget,
      group: 'headless',
      header: 'Save the record',
      message: 'Do you want to save the change?',
      acceptClass: 'p-button-danger',
      rejectLabel: 'Cancel',
      acceptLabel: 'Accept',
      accept: () => {
        deleteItem(idItem.value)
      },
      reject: () => {}
    })
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

const disabledSearch = computed(() => {
  return false
})

const disabledClearSearch = computed(() => {
  return (filterToSearch.value.hotel.length === 0)
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
          sortBy: 'description',
          sortType: 'DES'
        }

    const response = await GenericService.search(confHotelApi.moduleApi, confHotelApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error on loading hotel list:', error)
  }
}

function getLastDayOfMonth(date: Date): Date {
  const year = date.getFullYear()
  const month = date.getMonth()

  // Crear una nueva fecha con el mes siguiente y el día 0 (el día anterior al primero del mes siguiente)
  const lastDay = new Date(year, month + 1, 0)
  return lastDay
}

function isInCurrentMonth(date: Date): boolean {
  const currentDate = new Date()
  const currentYear = currentDate.getFullYear()
  const currentMonth = currentDate.getMonth()

  const givenYear = date.getFullYear()
  const givenMonth = date.getMonth()

  return currentYear === givenYear && currentMonth === givenMonth
}

// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
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
      Invoice Close Operation
    </h3>
    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 order-0 md:order-1 md:col-6 xl:col-9">
      <div class="card p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header">
                Search Fields
              </div>
            </template>
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex align-items-center gap-2">
                <label for="email">Select</label>
                <DebouncedAutoCompleteComponent
                  id="autocomplete"
                  field="code"
                  item-value="id"
                  multiple
                  :model="filterToSearch.hotel"
                  :suggestions="hotelList"
                  @change="($event) => {
                    filterToSearch.hotel = $event
                  }"
                  @load="($event) => getHotelList($event)"
                >
                  <template #option="{ item }">
                    {{ item.name }}
                  </template>
                </DebouncedAutoCompleteComponent>
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
        @on-confirm-create="clearForm"
        @open-edit-dialog="getItemById($event)"
        @update:clicked-item="getItemById($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      />
    </div>
    <div class="col-12 order-1 md:order-0 md:col-6 xl:col-3">
      <div>
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          {{ formTitle }}
        </div>
        <div class="card">
          <EditFormV2
            :key="formReload"
            :fields="fields"
            :item="item"
            :show-actions="true"
            :loading-save="loadingSaveAll"
            @update:item="($event) => item = $event"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-hotel="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.hotel"
                :suggestions="hotelList"
                @change="($event) => {
                  onUpdate('hotel', $event)
                }"
                @load="($event) => getHotelList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-beginDate="{ item: data, onUpdate }">
              <Calendar
                v-if="!loadingSaveAll"
                v-model="data.beginDate"
                date-format="yy-mm-dd"
                :min-date="new Date('2020-01-01')"
                :max-date="new Date()"
                @update:model-value="($event) => {
                  onUpdate('beginDate', $event)
                }"
              />
              <Skeleton v-else height="2rem" />
            </template>
            <template #field-endDate="{ item: data, onUpdate }">
              <Calendar
                v-if="!loadingSaveAll"
                v-model="data.endDate"
                date-format="yy-mm-dd"
                :min-date="data.beginDate ? new Date(data.beginDate) : undefined"
                :max-date="data.beginDate && !isInCurrentMonth(data.beginDate) ? getLastDayOfMonth(data.beginDate) : new Date()"
                @update:model-value="($event) => {
                  onUpdate('endDate', $event)
                }"
              />
              <Skeleton v-else height="2rem" />
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
  </div>
</template>
