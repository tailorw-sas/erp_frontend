<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { v4 as uuidv4 } from 'uuid'
import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

import type { IData } from '~/components/table/interfaces/IModelData'

const { data: userData } = useAuth()
const idItemToLoadFirstTime = ref('')
const toast = useToast()
const listItems = ref<any[]>([])
const selectedElements = ref<string[]>([])
const loadingSearch = ref(false)
const loadingSaveAll = ref(false)
const importProcess = ref(false)
const marcadosTodos = ref(true)
const resultTable = ref(null)

const allDefaultItem = { id: 'All', name: 'All', code: 'All' }

const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [],
  hotel: null,
  from: dayjs(new Date()).startOf('month').toDate(),
  to: dayjs(new Date()).endOf('month').toDate(),
})

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])

// VARIABLES -----------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'hotelInvoiceNumber', name: 'Hotel Invoice Number' },
]

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confRoomRateApi = reactive({
  moduleApi: 'innsist',
  uriApi: 'room-rate',
})

const confImportBookingApi = reactive({
  moduleApi: 'innsist',
  uriApi: 'import-booking',
})

const confImportProcessApi = reactive({
  moduleApi: 'innsist',
  uriApi: 'import-process',
})

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'hotel', header: 'Hotel', type: 'text', maxWidth: '50px' },
  { field: 'agencyCode', header: 'Agency', type: 'text', maxWidth: '10px' },
  { field: 'agencyAlias', header: 'Agency Alias', type: 'text', maxWidth: '15px' },
  { field: 'firstName', header: 'First Name', type: 'text', maxWidth: '12px' },
  { field: 'lastName', header: 'Last Name', type: 'text', maxWidth: '12px' },
  { field: 'reservationCode', header: 'Reserv No', type: 'text', maxWidth: '10px' },
  { field: 'roomType', header: 'Room Type', type: 'text', maxWidth: '10px' },
  { field: 'couponNumber', header: 'Coupon No', type: 'text', maxWidth: '20px' },
  { field: 'checkInDate', header: 'Check In', type: 'date', maxWidth: '10px' },
  { field: 'checkOutDate', header: 'Check Out', type: 'date', maxWidth: '10px' },
  { field: 'hotelInvoiceAmount', header: 'Hotel Amount', type: 'text', maxWidth: '10px' },
  { field: 'amount', header: 'Invoice Amount', type: 'text', maxWidth: '10px' },
  { field: 'message', header: 'Error', type: 'slot-text', maxWidth: '10px' }
]

const columnsExpandable: IColumn[] = [
  { field: 'firstName', header: 'First Name', type: 'text' },
  { field: 'lastName', header: 'Last Name', type: 'text' },
  { field: 'checkInDate', header: 'Check In', type: 'date' },
  { field: 'checkOutDate', header: 'Check Out', type: 'date' },
  { field: 'childrens', header: 'Children', type: 'text' },
  { field: 'adults', header: 'Adults', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'select' },
  { field: 'stayDays', header: 'Nights', type: 'text' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'select' },
  { field: 'hotelInvoiceAmount', header: 'Hotel Amount', type: 'text' },
  { field: 'amount', header: 'Invoice Amount', type: 'text' },
]

const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Bookings To Import From Innsist',
  moduleApi: 'innsist',
  uriApi: 'booking',
  loading: false,
  showDelete: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  showFilters: true,
  selectAllItemByDefault: false,
  expandableRows: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
  selectFirstItemByDefault: false,
  scrollHeight: '500px'
})

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 500,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.ASC
})

const errorListPayload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 500,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.ASC
})

const payloadOnChangePage = ref<PageState>()
const pagination = ref<IPagination>({
  page: 0,
  limit: 500,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

// -------------------------------------------------------------------------------------------------------
async function onMultipleSelect(data: any) {
  if (data.length > 0) {
    if (data.length === pagination.value.totalElements && marcadosTodos.value === true) {
      resultTable?.value.clearSelectedItems()
      selectedElements.value = []
      marcadosTodos.value = !marcadosTodos.value
      importProcess.value = false
    }
    else {
      if (data.length === pagination.value.totalElements) {
        marcadosTodos.value = true
      }
      selectedElements.value = data.filter((item: { message: string | null | undefined }) => item.message === undefined || item.message === null || item.message.trim() === '')
      importProcess.value = true
    }
  }
  else {
    importProcess.value = false
    selectedElements.value = []
  }
}

// FUNCTIONS ---------------------------------------------------------------------------------------------
async function searchErrorList(processId: any, elementsToImportNumber: number) {
  const newPayload: IQueryRequest = {
    filter: [],
    query: processId,
    pageSize: 100,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  errorListPayload.value = newPayload

  const dataList = await getErrorList()

  // Verificar si no hay resultados
  if (!dataList || dataList.value.length === 0) {
    await getList()
    toast.add({
      severity: 'info',
      summary: 'Confirmed',
      detail: `Import process successful. ${elementsToImportNumber} bookings imported.`,
      life: 5000
    })
  }
  else {
    toast.add({
      severity: 'warn',
      summary: 'Confirmed',
      detail: `Import process unsuccessful. ${dataList.value.length} bookings have errors`,
      life: 5000
    })
  }
}

async function getErrorList() {
  try {
    idItemToLoadFirstTime.value = ''
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(confImportBookingApi.moduleApi, confImportBookingApi.uriApi, errorListPayload.value)
    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    // const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      // if (!existingIds.has(iterator.id)) {
      newListItems.push({
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        agencyAlias: `${iterator?.agency?.name || ''}-${iterator?.agency?.agencyAlias || ''}`,
        agencyCd: iterator?.agency?.code,
        hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}` },
        rowClass: 'p-disabled p-text-disabled'
      })
      // existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      // }
    }

    listItems.value = [...listItems.value, ...newListItems]
    return listItems
  }

  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function getList() {
  listItems.value = []
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    const newListItems = []
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      newListItems.push({
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        agencyAlias: `${iterator?.agency?.name || ''}-${iterator?.agency?.agencyAlias || ''}`,
        hotel: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}`,
        roomType: `${iterator?.roomType?.code || ''}`,
        rowClass: isRowSelectable(iterator) ? 'p-selectable-row' : 'p-disabled p-text-disabled',
        selected: isRowSelectable(iterator)
      })
    }

    listItems.value = [...listItems.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }

  return listItems
}

function isRowSelectable(rowData: any) {
  return (rowData.message === undefined || rowData.message === null || rowData.message.trim() === '')
}

async function getHotelList(query: string = '') {
  try {
    const payload = {
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
        },

      ],
      query: '',
      pageSize: 100,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = [allDefaultItem]
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getAgencyList(query: string = '') {
  try {
    const payload = {
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
      pageSize: 100,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    agencyList.value = [allDefaultItem]
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function clearForm() {
  navigateTo('/invoice')
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      if (parseFilter[i]?.key === 'invoiceNumber') {
        parseFilter[i].key = 'invoiceNumberPrefix'
      }
    }
  }

  payload.value.filter = [...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getList()
  }
}

async function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 500,
    page: 0,
    sortBy: '',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  // Mantener los filtros existentes
  newPayload.filter = [
    ...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')
  ]

  const statusList = ['PENDING', 'FAILED']
  statusList.forEach((element: any) => {
    newPayload.filter.push({
      key: 'status',
      operator: 'EQUALS',
      value: element,
      logicalOperation: 'OR',
      type: 'filterSearch'
    })
  })

  // Filtros de hoteles
  if (filterToSearch.value.hotel) {
    if (filterToSearch.value.hotel?.id !== 'All') {
        newPayload.filter.push({
          key: 'hotel.id',
          operator: 'EQUALS',
        value: filterToSearch.value.hotel?.id,
        logicalOperation: 'AND',
          type: 'filterSearch'
        })
    }
  }

  // Filtros de agencias
  if (filterToSearch.value.agency?.length > 0) {
    const selectedAgencyIds = filterToSearch.value.agency
      .filter((item: any) => item?.id !== 'All')
      .map((item: any) => item?.id)

    if (selectedAgencyIds.length > 0) {
        newPayload.filter.push({
          key: 'agency.id',
        operator: 'IN',
        value: selectedAgencyIds,
        logicalOperation: 'AND',
          type: 'filterSearch'
        })
    }
  }

  // Filtros de rango de fechas usando 'from' y 'to'
  if (filterToSearch.value.from) {
    newPayload.filter.push({
      key: 'invoicingDate',
      operator: 'GREATER_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.from).startOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    })
  }
  if (filterToSearch.value.to) {
    newPayload.filter.push({
      key: 'invoicingDate',
      operator: 'LESS_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.to).endOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    })
  }

  payload.value = newPayload

  const dataList = await getList()

  // Seleccionar automáticamente todos los elementos retornados
  if (dataList && dataList.value.length > 0) {
    selectedElements.value = dataList.value.filter(item => item.message === undefined || item.message === null || item.message.trim() === '')// .map(item => item.id)
  }
  else {
    selectedElements.value = [] // Asegurarse de que esté vacío si no hay resultados
  }

  // Verificar si no hay resultados
  if (!dataList || dataList.value.length === 0) {
    toast.add({
      severity: 'info',
      summary: 'Confirmed',
      detail: `No bookings available. `,
      life: 5000 // Duración del toast en milisegundos
    })
  }

  importProcess.value = true
}

function clearFilterToSearch() {
  // Limpiar los filtros existentes
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]

  // Reiniciar los valores de búsqueda a sus estados iniciales
  filterToSearch.value = {
    criterial: ENUM_FILTER[0],
    search: '',
    agency: [],
    hotel: [],
    from: dayjs(new Date()).startOf('month').toDate(),
    to: dayjs(new Date()).endOf('month').toDate(),
  }
  listItems.value = []
  pagination.value.totalElements = 0
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criteria && filterToSearch.value.search)
  return false
})

const disabledImport = computed(() => {
  return selectedElements.value.length === 0 && importProcess.value === false
})

async function getRoomRateByBooking(bookingId: string) {
  const objInvoice = listItems.value.find((item: any) => item.id === bookingId)

  if (objInvoice) {
    objInvoice.roomRates = await getRoomRateList(bookingId)
  }
}

async function getRoomRateList(bookingId: string = '') {
  if (bookingId === '') { return }
  try {
    let listRoomRateTemp: any[] = []

    const payload: IQueryRequest = {
      filter: [
        {
          key: 'booking.id',
          operator: 'EQUALS',
          value: bookingId,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 100,
      page: 0,
      sortBy: 'renewalNumber',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    const response = await GenericService.search(confRoomRateApi.moduleApi, confRoomRateApi.uriApi, payload)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      listRoomRateTemp = [...listRoomRateTemp, {
        ...iterator,
        firstName: iterator?.firstName,
        lastName: iterator?.lastName,
        checkIn: iterator?.checkIn ? dayjs(iterator?.checkIn).format('YYYY-MM-DD') : '',
        checkOut: iterator?.checkOut ? dayjs(iterator?.checkOut).format('YYYY-MM-DD') : '',
        childrens: iterator?.childrens || 0,
        adults: iterator?.adults || 0,
        roomType: iterator?.roomType,
        ratePlan: iterator?.ratePlan,
        hotelInvoiceAmount: formatNumber(iterator?.hotelInvoiceAmount) || 0,
        amountPaymentApplied: formatNumber(iterator?.amountPaymentApplied) || 0,
      }]
    }

    return listRoomRateTemp
  }
  catch (error) {
    console.error(error)
  }
}

async function importBookings() {
  if (selectedElements.value.length === 0) {
    toast.add({
      severity: 'info',
      summary: 'Confirmed',
      detail: `No bookings selected. `,
      life: 3000
    })
    return
  }
  options.value.loading = true
  importProcess.value = false

  try {
    const uuid = uuidv4()
    const elementsToImportNumber = selectedElements.value.length

    const payload = {
      id: uuid,
      userId: userData?.value?.user?.userId,
      bookings: selectedElements.value.map(item => item.id)
    }

    await GenericService.import(confImportBookingApi.moduleApi, confImportBookingApi.uriApi, payload)

    await checkProcessStatus(uuid)

    await searchErrorList(uuid, elementsToImportNumber)

    selectedElements.value = []
  }
  catch (error: any) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: error.message || 'There was a problem with the import process. Please try again later.',
      life: 3000
    })
    options.value.loading = false
  }
}

async function checkProcessStatus(id: any) {
  return new Promise((resolve, reject) => {
    let attempts = 0
    const maxAttempts = 30
    const interval = setInterval(async () => {
      try {
        const response = await GenericService.getById(confImportProcessApi.moduleApi, confImportProcessApi.uriApi, id)
        if (response.status === 'COMPLETED') {
          clearInterval(interval)
          resolve(response.status)
        }
        else if (attempts >= maxAttempts) {
            clearInterval(interval)
          reject(new Error('Tiempo de espera agotado'))
        }
      }
      catch (error) {
        clearInterval(interval)
        reject(error)
      }
      attempts++
    }, 2000)
  })
}

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getList()
})

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
})
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h5 class="mb-0">
      {{ options.tableName }}
    </h5>
  </div>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class=" p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div
                class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center"
              >
                <div>
                  Filters
                </div>
              </div>
            </template>

            <div class="grid">
              <div class="col-12 md:col-6 lg:col-6 flex pb-0">
                <div class="flex flex-row gap-2 w-full">
                  <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">Agency:</label>
                    <div class="w-full" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete" :multiple="true"
                        class="w-full" field="name" item-value="id" :model="filterToSearch.agency"
                        :suggestions="agencyList" @load="($event) => getAgencyList($event)" @change="($event) => {
                          if (!filterToSearch.agency.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.agency = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2 w-full">
                    <label class="filter-label font-bold ml-3" for="">Hotel:</label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete" :multiple="false"
                        class="w-full" field="name" item-value="id" :model="filterToSearch.hotel"
                        :suggestions="hotelList" @load="($event) => getHotelList($event)" @change="($event) => { filterToSearch.hotel = $event }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>

              <div class="col-12 md:col-6 lg:col-5 flex pb-0">
                <div class="flex flex-row gap-5 w-full">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">From:</label>
                    <div class="w-full" style=" z-index:5 ">
                      <Calendar
                        v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                        show-icon icon-display="input" class="w-full" :max-date="new Date()"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2 ml-4">
                    <label class="filter-label font-bold" for="">To:</label>
                    <div class="w-full">
                      <Calendar
                        v-model="filterToSearch.to" date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                        icon-display="input" class="w-full" :max-date="new Date()" :min-date="filterToSearch.from"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div class="col-12 md:col-6 lg:col-1 flex pb-0 pr-2">
                <div class="flex w-full">
                  <div class="flex flex-row w-full">
                    <div class="flex align-items-center mx-3">
                      <Button
                        v-tooltip.top="'Search'" class="w-3rem mx-2 " icon="pi pi-search"
                        :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter"
                      />
                      <Button
                        v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                        :loading="loadingSearch" @click="clearFilterToSearch"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <div class="p-0">
        <DynamicTable
          ref="resultTable"
          :data="listItems"
          :columns="columns"
          :options="options"
          :pagination="pagination"
          :selected-items="selectedElements"
          @on-confirm-create="clearForm"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="parseDataTableFilter"
          @on-list-item="resetListItems"
          @on-sort-field="onSortField"
          @update:selected-items="onMultipleSelect($event)"
          @on-expand-row="getRoomRateByBooking($event)"
        >
          <template #column-message="{ data }">
            <div id="fieldError">
              <span v-tooltip.bottom="data.message" style="color: red;">{{ data.message }}</span>
            </div>
          </template>
          <template #expansion="{ data: item }">
            <div class="p-0 m-0">
              <DataTable :value="item.roomRates" striped-rows>
                <Column v-for="column of columnsExpandable" :key="column.field" :field="column.field" :header="column.header" :sortable="column?.sortable" />
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
              </DataTable>
            </div>
          </template>
        </DynamicTable>
      </div>
      <div class="flex align-items-end justify-content-end">
        <Button v-tooltip.top="'Import'" class="w-3rem mx-2" icon="pi pi-save" :disabled="disabledImport" @click="importBookings" />
        <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times" @click="clearForm" />
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.custom-file-upload {
  background-color: transparent !important;
  border: none !important;
  text-align: left !important;
}

.custom-width {
  width: 300px;
  /* Ajusta el ancho como desees, por ejemplo 300px, 50%, etc. */
}

.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  max-width: 150px;
  /* Ajusta el ancho máximo según tus necesidades */
}
</style>
