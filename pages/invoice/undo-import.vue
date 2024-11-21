<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'

import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

import type { IData } from '~/components/table/interfaces/IModelData'

const multiSelectLoading = ref({

  hotel: false,
})
const entryCode = ref('')
const randomCode = ref(generateRandomCode())
const idItemToLoadFirstTime = ref('')
const totalAmount = ref(0)
const totalInvoiceAmount = ref(0)
const openDialog = ref(false)
const toast = useToast()
const listItems = ref<any[]>([])
const bookinglistItems = ref<any[]>([])
const selectedElements = ref<string[]>([])
const startOfMonth = ref<any>(null)
const endOfMonth = ref<any>(null)
const filterAllDateRange = ref(false)
const loadingSearch = ref(false)

const loadingSaveAll = ref(false)

// const allDefaultItem = { id: 'All', name: 'All', code: 'All' }

const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  onlyManual: true,
  onlyInssist: false,
  hotel: [],
  date: dayjs(new Date()).startOf('month').toDate(),

})

const hotelList = ref<any[]>([])

const { data: userData } = useAuth()
const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-booking/import',
})

const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})
const confAgencyApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

// VARIABLES -----------------------------------------------------------------------------------------
const selectedInvoiceIds = ref<string[]>([])
//
const idItem = ref('')
const ENUM_FILTER = [
  { id: 'invoiceId', name: 'Invoice Id' },
]

const confApiApplyUndo = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/undo',
})

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})
const confRoomRateApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-room-rate',
})

// -------------------------------------------------------------------------------------------------------
interface DataListItem {
  id: string
  name: string
  code: string
  status: string
}

interface ListItem {
  id: string
  name: string
  code: string
  status: string
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} ${data.name}`,
    code: `${data.code}`,
    status: data.status
  }
}
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Id', type: 'text' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: { ...confhotelListApi, keyValue: 'name', mapFunction } },
  { field: 'agency', header: 'Agency', type: 'select', objApi: { ...confagencyListApi, keyValue: 'name', mapFunction } },
  { field: 'invoiceNumber', header: 'Inv. No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen. Date', type: 'date' },
  { field: 'isManual', header: 'Manual', type: 'bool', tooltip: 'Manual' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'number' },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'number' },
  { field: 'status', header: 'Reverse Status', width: '12%', frozen: true, showFilter: false, type: 'slot-select', localItems: ENUM_INVOICE_STATUS, sortable: true },
]

const columnsExpandable: IColumn[] = [
  { field: 'firstName', header: 'First Name', type: 'text' },
  { field: 'lastName', header: 'Last Name', type: 'text' },
  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'nights', header: 'Nights', type: 'text' },
  { field: 'adults', header: 'Adults', type: 'text' },
  { field: 'children', header: 'Children', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'text' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'text' },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'number' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'number' },
]
const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')
// -------------------------------------------------------------------------------------------------------
function generateRandomCode() {
  return Math.floor(100000 + Math.random() * 900000).toString()
}
function handleDialogClose() {
  openDialog.value = false
  entryCode.value = ''
  randomCode.value = generateRandomCode()
}
function handleClose() {
  openDialog.value = false
  entryCode.value = ''
  randomCode.value = generateRandomCode()
}
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Undo Import',
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
  expandableRows: true,
  loading: false,
  showDelete: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  selectAllItemByDefault: true,
  showFilters: true,
  messageToDelete: 'Do you want to save the change?'
})

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'invoiceId',
  sortType: ENUM_SHORT_TYPE.ASC
})
const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'bookingId',
  sortType: ENUM_SHORT_TYPE.ASC
})

const payloadOnChangePage = ref<PageState>()
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------
function openUndo() {
  openDialog.value = true
}
async function onMultipleSelect(data: any) {
  selectedElements.value = [...data]
}
// FUNCTIONS ---------------------------------------------------------------------------------------------
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

    const response = await GenericService.search('invoicing', 'manage-invoice', payload.value)
    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    totalInvoiceAmount.value = 0
    totalAmount.value = 0

    const existingIds = new Set(listItems.value.map(item => item.id))
    selectedInvoiceIds.value = []
    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      totalAmount.value += iterator.dueAmount || 0 // Asegúrate de manejar posibles valores nulos
      totalInvoiceAmount.value += iterator.invoiceAmount || 0
      if (!existingIds.has(iterator.id)) {
        newListItems.push({
          ...iterator,
          hotel: {
            ...iterator?.hotel,
            name: `${iterator?.hotel?.code} - ${iterator?.hotel?.name}`
          },
          agency: {
            ...iterator?.agency,
            name: `${iterator?.agency?.code} - ${iterator?.agency?.name}`
          },
          roomRates: [],
          loadingEdit: false,
          loadingDelete: false,
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
        selectedInvoiceIds.value.push(iterator.id)
      }
    }

    listItems.value = [...listItems.value, ...newListItems]
  }

  catch (error) {
    options.value.loading = false
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}
async function getBookingList(clearFilter: boolean = false) {
  try {
    const Payload: any = ({
      filter: [
        // {
        //   key: 'invoice.id',
        //   operator: 'IN',
        //   value: selectedInvoiceIds.value,
        //   logicalOperation: 'AND'
        // }
      ],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    })

    Payload.filter = []
    Payload.filter = [
      {
        key: 'invoice.invoiceDate',
        operator: 'EQUALS',
        value: dayjs(filterToSearch.value.date).startOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND'
      },
      {
        key: 'invoice.deleteInvoice',
        operator: 'EQUALS',
        value: false,
        logicalOperation: 'AND'
      },
      // {
      //   key: 'invoice.hotel.id',
      //   operator: 'IN',
      //   value: filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All').map((item: any) => item?.id) || '',
      //   logicalOperation: 'AND'
      // }
    ]
    const existItemAll = filterToSearch.value.hotel.some((item: any) => item?.id === 'All')
    if (filterToSearch.value.hotel && filterToSearch.value.hotel.length > 0 && !existItemAll) {
      const objFilterToSearch = Payload.filter.find((item: any) => item?.key === 'invoice.hotel.id')
      if (objFilterToSearch) {
        objFilterToSearch.value = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All').map((item: any) => item?.id)
      }
      else {
        Payload.filter = [...Payload.filter, {
          key: 'invoice.hotel.id',
          operator: 'IN',
          value: filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All').map((item: any) => item?.id),
          logicalOperation: 'AND'
        }]
      }
    }

    bookinglistItems.value = []
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, Payload)
    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      //    const id = v4()
      totalAmount.value += iterator.invoiceAmount || 0 // Asegúrate de manejar posibles valores nulos
      totalInvoiceAmount.value += iterator.dueAmount || 0

      bookinglistItems.value = [...bookinglistItems.value, {
        ...iterator,

        loadingEdit: false,
        loadingDelete: false,
        bookingId: '',
        hotelAmountTemp: iterator.hotelAmount ? formatNumber(iterator.hotelAmount) : 0,
        ratePlan: iterator.ratePlan?.name || '',
        roomType: iterator.roomType?.name || '',
        agency: iterator?.invoice?.agency,
        invoiceAmount: iterator.invoiceAmount,
        invoiceAmountTemp: iterator.invoiceAmount ? formatNumber(iterator.invoiceAmount) : 0,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        fullName: `${iterator.firstName ? iterator.firstName : ''} ${iterator.lastName ? iterator.lastName : ''}`
      }]
    }

    return bookinglistItems.value
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

function handleCheckboxChange(type) {
  if (type === 'manual') {
    filterToSearch.value.onlyManual = true
    filterToSearch.value.onlyInssist = false
  }
  else if (type === 'inssist') {
    filterToSearch.value.onlyManual = false
    filterToSearch.value.onlyInssist = true
  }
}

async function getHotelList(query: string = '') {
  try {
    multiSelectLoading.value.hotel = true
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
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
  finally {
    multiSelectLoading.value.hotel = false
  }
}

async function clearForm() {
  navigateTo('/invoice')
}

async function resetListItems() {
  Payload.value.page = 0
  getBookingList()
}
function getStatusName(code: string) {
  switch (code) {
    case 'PROCECSED': return 'Processed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Cancelled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}
function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCECSED': return '#FF8D00'
    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELED': return '#888888'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      /*   if (parseFilter[i]?.key === 'status') {
              parseFilter[i].key = 'invoiceStatus'
            }
      */

    }
  }

  Payload.value.filter = [...parseFilter || []]
  getBookingList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'ratePlan') {
      event.sortField = 'ratePlan.name'
    }
    if (event.sortField === 'roomType') {
      event.sortField = 'roomType.name'
    }
    if (event.sortField === 'fullName') {
      event.sortField = 'fistName'
    }
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    getBookingList()
  }
}

async function searchAndFilter() {
  // Filtros adicionales
  const filterOfInvoiceDate = payload.value.filter.find((item: any) => item?.key === 'invoiceDate')
  if (filterOfInvoiceDate) {
    filterOfInvoiceDate.value = dayjs(filterToSearch.value.date).startOf('day').format('YYYY-MM-DD')
  }
  else {
    payload.value.filter.push({
      key: 'invoiceDate',
      operator: 'EQUALS',
      value: dayjs(filterToSearch.value.date).startOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    })
  }

  // Filter by hotel.id
  const filterOfHotelId = payload.value.filter.find((item: any) => item?.key === 'hotel.id')
  if (filterOfHotelId) {
    filterOfHotelId.value = filterToSearch.value.hotel
      .filter((item: any) => item?.id !== 'All')
      .map((item: any) => item?.id)
  }
  else {
    payload.value.filter.push({
      key: 'hotel.id',
      operator: 'IN',
      value: filterToSearch.value.hotel
        .filter((item: any) => item?.id !== 'All')
        .map((item: any) => item?.id),
      logicalOperation: 'AND',
      type: 'filterSearch'
    })
  }

  // Filter by importType
  // const filterOfImportType = payload.value.filter.find((item: any) => item?.key === 'importType')
  // if (filterOfImportType) {
  //   filterOfImportType.value = ['BOOKING_FROM_FILE_VIRTUAL_HOTEL', 'INVOICE_BOOKING_FROM_FILE']
  // }
  // else {
  //   payload.value.filter.push({
  //     key: 'importType',
  //     operator: 'IN',
  //     value: ['BOOKING_FROM_FILE_VIRTUAL_HOTEL', 'INVOICE_BOOKING_FROM_FILE'],
  //     logicalOperation: 'AND',
  //     type: 'filterSearch'
  //   })
  // }

  // Obtener la lista de facturas
  options.value.selectAllItemByDefault = false
  await getList()
  // await getBookingList()
}

function clearFilterToSearch() {
  // Limpiar los filtros existentes
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]

  // Reiniciar los valores de búsqueda a sus estados iniciales
  filterToSearch.value = {
    // Mantener el primer elemento del enum como valor predeterminado
    search: '', // Dejar el campo de búsqueda en blanco
    onlyManual: true,
    onlyInssist: false,
    hotel: [], // Restablecer a valor predeterminado
    date: dayjs(new Date()).startOf('month').toDate(),

  }
  selectedElements.value = []
  bookinglistItems.value = []
  totalAmount.value = 0
  totalInvoiceAmount.value = 0
  Pagination.value.totalElements = 0
}

async function applyUndo() {
  try {
    loadingSaveAll.value = true
    if (selectedElements.value.length > 0) {
      const payload = {
        ids: selectedElements.value,
      }
      const response = await GenericService.create(confApiApplyUndo.moduleApi, confApiApplyUndo.uriApi, payload)
      loadingSaveAll.value = false
      openDialog.value = false
      if (response) {
        toast.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Undo Successfully',
          life: 3000
        })
        await searchAndFilter()
      }
    }
  }
  catch (error) {
    loadingSaveAll.value = false
    console.error('Error applying undo:', error)
  }
}

const disabledSearch = computed(() => {
  return filterToSearch.value.hotel.length === 0 || filterToSearch.value.date === null
})

async function getRoomRateList(invoiceId: string = '') {
  if (invoiceId === '') { return }
  try {
    let listRoomRateTemp: any[] = []

    const payload: IQueryRequest = {
      filter: [
        {
          key: 'booking.invoice.id',
          operator: 'EQUALS',
          value: invoiceId,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 50,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    const response = await GenericService.search(confRoomRateApi.moduleApi, confRoomRateApi.uriApi, payload)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      listRoomRateTemp = [...listRoomRateTemp, {
        ...iterator,
        firstName: iterator?.booking?.firstName,
        lastName: iterator?.booking?.lastName,
        checkIn: iterator?.checkIn ? dayjs(iterator?.checkIn).format('YYYY-MM-DD') : '',
        checkOut: iterator?.checkOut ? dayjs(iterator?.checkOut).format('YYYY-MM-DD') : '',
        children: iterator?.children || 0,
        adults: iterator?.adults || 0,
        roomType: iterator?.booking?.roomType ? { ...iterator.booking.roomType, name: `${iterator?.booking?.roomType?.code || ''}-${iterator?.booking?.roomType?.name || ''}` } : null,
        ratePlan: iterator?.booking?.ratePlan ? { ...iterator.booking.ratePlan, name: `${iterator?.booking?.ratePlan?.code || ''}-${iterator?.booking?.ratePlan?.name || ''}` } : null,
        invoiceAmount: formatNumber(iterator?.invoiceAmount) || 0,
        hotelAmount: formatNumber(iterator?.hotelAmount) || 0,
      }]
    }
    return listRoomRateTemp
  }
  catch (error) {
    console.error(error)
  }
}

async function getRoomRateByInvoice(invoiceId: string) {
  const objInvoice = listItems.value.find((item: any) => item.id === invoiceId)

  if (objInvoice) {
    objInvoice.roomRates = await getRoomRateList(invoiceId)
  }
}

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getBookingList()
})

onMounted(async () => {
  // filterToSearch.value.criterial = ENUM_FILTER[0]

  // await getList()
  // selectedElements.value = listItems.value.map((item: any) => item.id)
})
</script>

<template>
  <div class="bg-white" style="max-height: 100vh; height: 90vh">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      Bookings To Remove
    </div>
    <div class="grid">
      <div class="col-12 order-0 w-full md:order-1 md:col-6  lg:col-6 xl:col-6">
        <div class="mb-0">
          <div class="grid mt-1 m-2">
            <div class="col-12 md:col-6 lg:col-3 xl:col-3 flex pb-0">
              <div class="flex flex-column gap-2 w-full">
                <div class="flex align-items-center gap-2">
                  <label class="filter-label font-bold " for="">Hotel:<span
                    class="text-red"
                  >*</span></label>
                  <div class="w-full">
                    <DebouncedMultiSelectComponent
                      v-if="!loadingSaveAll"
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :model="filterToSearch.hotel"
                      :suggestions="hotelList"
                      :loading="multiSelectLoading.hotel"
                      @change="($event) => {

                        filterToSearch.hotel = $event
                      }"
                      @load="($event) => getHotelList($event)"
                    />
                    <!--
                    <DebouncedAutoCompleteComponent
                      v-if="!loadingSaveAll" id="autocomplete"
                      :multiple="true" class="w-full" field="name" item-value="id"
                      :model="filterToSearch.hotel" :suggestions="hotelList"
                      @load="($event) => getHotelList($event)" @change="($event) => {
                        if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All');
                        }
                        else {
                          filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All');
                        }
                      }"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedAutoCompleteComponent>
                    -->
                  </div>
                </div>
              </div>
            </div>

            <div class="col-12 order-0  md:order-1 md:col-6 xl:col-3 lg:col-3 flex pb-0">
              <div class="flex flex-column gap-2 w-full">
                <div class="flex align-items-center gap-2">
                  <label class="filter-label font-bold" for="">Date</label>
                  <div class="w-full">
                    <Calendar
                      v-model="filterToSearch.date" date-format="yy-mm-dd"
                      icon="pi pi-calendar-plus" show-icon icon-display="input" class="w-full"
                      :min-date="new Date(startOfMonth)"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div class="flex align-items-center gap-2 m-3 ">
              <Checkbox
                id="only-manual" v-model="filterToSearch.onlyManual" :binary="true"
                style="z-index: 999;" @change="handleCheckboxChange('manual')"
              />
              <label for="all-check-1" class="font-bold">Only Manual</label>
            </div>

            <div class="flex align-items-center gap-2 m-3">
              <Checkbox
                id="only-inssist" v-model="filterToSearch.onlyInssist" :binary="true"
                style="z-index: 999;" @change="handleCheckboxChange('inssist')"
              />
              <label for="all-check-1" class="font-bold">Only Inssist</label>
            </div>
            <div class="flex align-items-center mt-3 mb-3">
              <Button
                v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search"
                :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter"
              />
              <Button
                v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                :loading="loadingSearch" @click="clearFilterToSearch"
              />
            </div>
          </div>
        </div>
        <div class="m-3">
          <DynamicTable
            :data="listItems"
            :columns="columns"
            :options="options"
            :pagination="Pagination"
            @on-confirm-create="clearForm"
            @on-change-pagination="payloadOnChangePage = $event"
            @on-change-filter="parseDataTableFilter"
            @on-list-item="resetListItems"
            @on-sort-field="onSortField"
            @update:clicked-item="onMultipleSelect($event)"
            @on-expand-row="getRoomRateByInvoice($event)"
          >
            <template #column-message="{ data }">
              <div id="fieldError">
                <span v-tooltip.bottom="data.message" style="color: red;">{{ data.message }}</span>
              </div>
            </template>

            <template #column-status="{ data }">
              <div id="fieldError">
                <span v-tooltip.bottom="data.sendStatusError" style="color: red;">{{ data.sendStatusError }}</span>
              </div>
            </template>

            <template #datatable-footer>
              <ColumnGroup
                type="footer" class="flex align-items-center font-bold font-500"
                style="font-weight: 700"
              >
                <Row>
                  <Column
                    footer="Totals:" :colspan="8"
                    footer-style="text-align:right; font-weight: 700"
                  />
                  <Column :footer="totalAmount ? formatNumber(Math.round((totalAmount + Number.EPSILON) * 100) / 100) : ''" :colspan="1" />
                  <Column :footer="totalInvoiceAmount ? formatNumber(Math.round((totalInvoiceAmount + Number.EPSILON) * 100) / 100) : ''" :colspan="1" />
                  <Column :colspan="1" />
                </Row>
              </ColumnGroup>
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
          <Button v-tooltip.top="'Apply'" class="w-3rem mx-2" icon="pi pi-check" :disabled="selectedElements.length === 0" @click="openUndo()" />

          <Button
            v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times"
            @click="clearForm"
          />
        </div>
        <div v-if="openDialog">
          <Dialog
            v-model:visible="openDialog" modal class="mx-3 sm:mx-0"
            content-class="border-round-bottom border-top-1 surface-border" :style="{ width: '26%' }" :pt="{
              root: {
                class: 'custom-dialog',
              },
              header: {
                style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
              },

            }" @hide="openDialog = false"
          >
            <template #header>
              <div class="flex justify-content-between">
                <h5 class="m-0">
                  Do you want to undo import this booking ?
                </h5>
              </div>
            </template>
            <template #default>
              <div class="p-2 pb-0">
                <div class="flex align-items-center mt-3 mb-2">
                  <div class="mr-2 px-3">
                    <span class="font-bold text-2xl">{{ randomCode }}</span>
                  </div>
                  <div>
                    <InputText
                      id="entry-code" v-model="entryCode" type="text"
                      placeholder="Enter code" class="w-15rem h-3rem"
                    />
                  </div>
                </div>
                <div class="flex justify-content-end mb-0">
                  <Button
                    :disabled="entryCode !== randomCode"
                    class="mr-2 p-button-primary h-2rem  w-3rem mt-3 "
                    icon="pi pi-save"
                    :loading="loadingSaveAll"
                    @click="applyUndo"
                  />

                  <Button
                    v-tooltip.top="'Cancel'" severity="secondary" class="h-2rem w-3rem p-button mt-3 px-2" icon="pi pi-times"
                    @click="handleClose"
                  />
                </div>
              </div>
            </template>
          </Dialog>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.p-button-gray{
    background-color: #f5f5f5 !important;
    color: #666 !important;
  border-color: #ddd !important;

}

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

.text-red {
    color: red;
    /* Define color rojo para el asterisco */
}
</style>
