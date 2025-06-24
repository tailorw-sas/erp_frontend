<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { v4 as uuidv4 } from 'uuid'
import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { copyPaymentsToClipboardPayMang } from '~/pages/payment/utils/clipboardUtilsListPayMang'

import type { IData } from '~/components/table/interfaces/IModelData'

const emit = defineEmits(['close'])
const { data: userData } = useAuth()
const idItemToLoadFirstTime = ref('')
const toast = useToast()
const listItems = ref<any[]>([])
const listItemsErrors = ref<any[]>([])
const listItemsSearchErrors = ref<any[]>([])
const selectedElements = ref<string[]>([])
const loadingSearch = ref(false)
const loadingSaveAll = ref(false)
const importProcess = ref(false)
const resultTable = ref<InstanceType<typeof DynamicTable> | null>(null)
const showDataTable = ref(true)
const showErrorsDataTable = ref(false)
const showErrorsSearchDataTable = ref(false)
const processId = ref('')
const totalImportedRows = ref(0)
const totalHotelInvoiceAmount = ref(0)
const totalInvoiceAmount = ref(0)

const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [],
  hotel: null,
  from: new Date(),
  to: new Date(),
})

const hotelList = ref<any[]>([])
const hotelListTotal = ref<any[]>([])
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
  uriApi: 'room-rate',
})

const confImportRoomRateApi = reactive({
  moduleApi: 'innsist',
  uriApi: 'import-room-rate',
})

const confInvoiceApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
})

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'hotel', header: 'Hotel', type: 'text', minWidth: '25px', maxWidth: '150px' },
  { field: 'agency', header: 'Agency', type: 'text', minWidth: '10px', maxWidth: '75px' },
  { field: 'agencyAlias', header: 'Agency Alias', type: 'text', minWidth: '25px', maxWidth: '150px' },
  { field: 'firstName', header: 'First Name', type: 'text', minWidth: '12px', maxWidth: '75px' },
  { field: 'lastName', header: 'Last Name', type: 'text', minWidth: '12px', maxWidth: '75px' },
  { field: 'reservationCode', header: 'Reserv No', type: 'text', minWidth: '10px', maxWidth: '75px' },
  { field: 'roomType', header: 'Room Type', type: 'text', minWidth: '10px', maxWidth: '50px' },
  { field: 'couponNumber', header: 'Coupon No', type: 'text', minWidth: '20px', maxWidth: '75px' },
  { field: 'checkInDate', header: 'Check In', type: 'date', minWidth: '10px', maxWidth: '75px' },
  { field: 'checkOutDate', header: 'Check Out', type: 'date', minWidth: '10px', maxWidth: '75px' },
  { field: 'hotelInvoiceAmount', header: 'Hot. Amount', tooltip: 'Hotel Amount', type: 'number', minWidth: '10px', maxWidth: '75px' },
  { field: 'amount', header: 'Rate Amount', tooltip: 'Rate Amount', type: 'number', minWidth: '10px', maxWidth: '75px' },
  { field: 'status', header: 'Status', type: 'slot-text', minWidth: '50px', maxWidth: '150px' }
]

const columnsExpandable: IColumn[] = [
  { field: 'firstName', header: 'First Name', type: 'text' },
  { field: 'lastName', header: 'Last Name', type: 'text' },
  { field: 'checkInDate', header: 'Check In', type: 'date' },
  { field: 'checkOutDate', header: 'Check Out', type: 'date' },
  { field: 'adults', header: 'Adults', type: 'text' },
  { field: 'childrens', header: 'Children', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'select' },
  { field: 'stayDays', header: 'Nights', type: 'text' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'select' },
  { field: 'hotelInvoiceAmount', header: 'Hotel Amount', type: 'text' },
  { field: 'amount', header: 'Rate Amount', type: 'text' },
]

const columnsErrors: IColumn[] = [
  { field: 'hotel', header: 'Hotel', type: 'text', maxWidth: '30px' },
  { field: 'agency', header: 'Agency', type: 'text', maxWidth: '10px' },
  { field: 'invoicingDate', header: 'Booking Date', type: 'date', maxWidth: '10px' },
  { field: 'guestName', header: 'Full Name', type: 'text', maxWidth: '20px' },
  { field: 'reservationCode', header: 'Reserv No', type: 'text', maxWidth: '10px' },
  { field: 'couponNumber', header: 'Coupon No', type: 'text', maxWidth: '10px' },
  { field: 'roomType', header: 'Room Type', type: 'text', maxWidth: '10px' },
  { field: 'checkInDate', header: 'Check In', type: 'date', maxWidth: '10px' },
  { field: 'checkOutDate', header: 'Check Out', type: 'date', maxWidth: '10px' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'text', maxWidth: '10px' },
  { field: 'hotelInvoiceAmount', header: 'Hotel Amount', type: 'number', maxWidth: '10px' },
  { field: 'amount', header: 'Invoice Amount', type: 'number', maxWidth: '10px' },
  { field: 'message', header: 'Error', type: 'slot-text', maxWidth: '70px' }
]

const columnsSearchErrors: IColumn[] = [
  { field: 'hotel', header: 'Hotel', type: 'text', minWidth: '30px', maxWidth: '120px' },
  { field: 'agency', header: 'Agency', type: 'text', minWidth: '10px', maxWidth: '50px' },
  { field: 'invoicingDate', header: 'Booking Date', type: 'date', minWidth: '10px', maxWidth: '50px' },
  { field: 'guestName', header: 'Full Name', type: 'text', minWidth: '20px', maxWidth: '100px' },
  { field: 'reservationCode', header: 'Reserv No', type: 'text', minWidth: '10px', maxWidth: '50px' },
  { field: 'couponNumber', header: 'Coupon No', type: 'text', minWidth: '10px', maxWidth: '70px' },
  { field: 'roomType', header: 'Room Type', type: 'text', minWidth: '10px', maxWidth: '50px' },
  { field: 'checkInDate', header: 'Check In', type: 'date', minWidth: '10px', maxWidth: '50px' },
  { field: 'checkOutDate', header: 'Check Out', type: 'date', minWidth: '10px', maxWidth: '50px' },
  { field: 'hotelInvoiceAmount', header: 'Hotel Amount', type: 'text', minWidth: '10px', maxWidth: '40px' },
  { field: 'amount', header: 'Invoice Amount', type: 'text', minWidth: '10px', maxWidth: '40px' },
  { field: 'message', header: 'Error', type: 'slot-text', minWidth: '50px', maxWidth: '100px' }
]

const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  // tableName: 'Bookings To Import From Innsist',
  moduleApi: 'innsist',
  uriApi: 'room-rate',
  loading: false,
  showDelete: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  showFilters: true,
  selectAllItemByDefault: true,
  expandableRows: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
  selectFirstItemByDefault: false,
  showLocalPagination: false,
  showSelectedItems: true
})

const optionsListErrors = ref({
  tableName: 'List Errors Import From Innsist',
  moduleApi: 'innsist',
  uriApi: 'booking',
  loading: false,
  showDelete: false,
  showFilters: true,
  selectAllItemByDefault: false,
  expandableRows: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
  selectFirstItemByDefault: false,
  scrollHeight: '500px',
  showPagination: true
})

const optionsSearchErrors = ref({
  tableName: 'List Errors Import From Innsist',
  moduleApi: 'innsist',
  uriApi: 'booking',
  loading: false,
  showDelete: false,
  showFilters: true,
  selectAllItemByDefault: false,
  expandableRows: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
  selectFirstItemByDefault: false,
  scrollHeight: '500px',
  showPagination: true
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

const payloadSearchErrors = ref<IQueryRequest>({
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

const paginationErroList = ref<IPagination>({
  page: 0,
  limit: 500,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const paginationSearchErrors = ref<IPagination>({
  page: 0,
  limit: 500,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const payloadOnChangePageErrorList = ref<PageState>()

// -------------------------------------------------------------------------------------------------------
async function onMultipleSelect(data: any) {
  selectedElements.value = data
}

// FUNCTIONS ---------------------------------------------------------------------------------------------
async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    selectedElements.value = []
    resultTable.value?.clearSelectedItems()
    listItems.value = []
    options.value.loading = true
    idItemToLoadFirstTime.value = ''

    const newListItems = []
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)
    const { data: dataList, totalPages, totalElements, size, page } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      newListItems.push({
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        agency: `${iterator?.agency?.code || ''}-${iterator?.agency?.name || ''}`,
        agencyAlias: `${iterator?.agency?.name || ''}-${iterator?.agency?.agencyAlias || ''}`,
        hotel: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}`,
        roomType: `${iterator?.roomType?.code || ''}`,
      })

      totalHotelInvoiceAmount.value += iterator.hotelInvoiceAmount ? Number(iterator.hotelInvoiceAmount) : 0
      totalInvoiceAmount.value += iterator.amount
    }

    listItems.value = [...listItems.value, ...newListItems]

    if (listItems.value && listItems.value.length > 0) {
      selectedElements.value = listItems.value.filter(item => item.message === undefined || item.message === null || item.message.trim() === '').map(item => item.id)
    }
    else {
      selectedElements.value = []
      toast.add({
        severity: 'info',
        summary: 'Confirmed',
        detail: `No bookings available. `,
        life: 5000
      })
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

function isRowSelectable(rowData: any) {
  return (rowData.message === undefined || rowData.message === null || rowData.message.trim() === '')
}

async function getListSearchErrors() {
  if (optionsSearchErrors.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    listItemsSearchErrors.value = []
    optionsSearchErrors.value.loading = true
    const newListItems = []
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payloadSearchErrors.value)
    const { data: dataList, totalPages, totalElements, size, page } = response

    paginationSearchErrors.value.page = page
    paginationSearchErrors.value.limit = size
    paginationSearchErrors.value.totalElements = totalElements
    paginationSearchErrors.value.totalPages = totalPages

    for (const iterator of dataList) {
      newListItems.push({
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        agencyAlias: `${iterator?.agency?.name || ''}-${iterator?.agency?.agencyAlias || ''}`,
        hotel: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}`,
        roomType: `${iterator?.roomType?.code || ''}`,
        selected: isRowSelectable(iterator)
      })
    }

    listItemsSearchErrors.value = [...listItemsSearchErrors.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    optionsSearchErrors.value.loading = false
  }
}
async function getHotelListFilter(query: string = '') {
  hotelList.value = []
  hotelList.value = hotelListTotal.value.filter(item =>
    item.code.toUpperCase().includes(query.toUpperCase())
    || item.name.toUpperCase().includes(query.toUpperCase())
  )
}
async function getHotelList() {
  try {
    options.value.loading = true
    hotelListTotal.value = []
    const payload = {
      filter: [
        {
          key: 'isVirtual',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: false,
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
    for (const iterator of dataList) {
      hotelListTotal.value = [...hotelListTotal.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
    hotelList.value = [...hotelListTotal.value]
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
  options.value.loading = false
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
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function openErrorsSearch(event: any) {
  showErrorsSearchDataTable.value = event
  await getListSearchErrors()
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
  showErrorsDataTable.value = false
  showDataTable.value = true
  totalHotelInvoiceAmount.value = 0
  totalInvoiceAmount.value = 0
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 500,
    page: 0,
    sortBy: 'createdAt',
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
  //
  newPayload.filter.push({
    key: 'agency.id',
    operator: 'IS_NOT_NULL',
    value: '',
    logicalOperation: 'AND',
    type: 'filterSearch'
  })

  payload.value = newPayload

  await getList()
  await searchAndFilterSearchErrors()

  importProcess.value = true
}

async function searchAndFilterSearchErrors() {
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 500,
    page: 0,
    sortBy: '',
    sortType: ENUM_SHORT_TYPE.ASC
  }
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
  //
  newPayload.filter.push({
    key: 'agency.id',
    operator: 'IS_NULL',
    value: '',
    logicalOperation: 'AND',
    type: 'filterSearch'
  })

  payloadSearchErrors.value = newPayload

  await getListSearchErrors()
}

function copiarDatos() {
  copyPaymentsToClipboardPayMang(columns, listItems.value, toast)
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
  pagination.value = {
    page: 0,
    limit: 500,
    totalElements: 0,
    totalPages: 0,
    search: ''
  }

  listItems.value = []
  listItemsSearchErrors.value = []
  listItemsErrors.value = []
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criteria && filterToSearch.value.search)
  return false
})

const disabledImport = computed(() => {
  return selectedElements.value.length === 0 && importProcess.value === false
})

const disabledSearchErrors = computed(() => {
  return listItemsSearchErrors.value.length === 0
})

async function getRoomRateByBooking(bookingId: string) {
  const objInvoice = listItems.value.find((item: any) => item.id === bookingId)

  if (objInvoice) {
    objInvoice.roomRates = await getRoomRateList(bookingId)
  }
}

async function getRoomRateByBookingErrors(bookingId: string) {
  const objInvoice = listItemsErrors.value.find((item: any) => item.id === bookingId)

  if (objInvoice) {
    objInvoice.roomRates = await getRoomRateList(bookingId)
  }
}

async function getRoomRateByBookingSearchErrors(bookingId: string) {
  const objInvoice = listItemsSearchErrors.value.find((item: any) => item.id === bookingId)

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
          key: 'id',
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

    const { data: dataList } = response

    for (const iterator of dataList) {
      listRoomRateTemp = [...listRoomRateTemp, {
        ...iterator,
        firstName: iterator?.firstName,
        lastName: iterator?.lastName,
        checkIn: iterator?.checkIn ? dayjs(iterator?.checkIn).format('YYYY-MM-DD') : '',
        checkOut: iterator?.checkOut ? dayjs(iterator?.checkOut).format('YYYY-MM-DD') : '',
        childrens: iterator?.childrens || 0,
        adults: iterator?.adults || 0,
        roomType: `${iterator?.roomType?.code || ''}`,
        ratePlan: `${iterator?.ratePlan.code || ''}`,
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

function generateProcessId() {
  processId.value = uuidv4()
}

async function importBookings() {
  listItemsSearchErrors.value = []
  const idItemsToImport = selectedElements.value
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

  generateProcessId()
  const elementsToImportNumber = selectedElements.value.length
  let processContinue = false

  try {
    const payload = {
      id: processId.value,
      userId: userData?.value?.user?.userId,
      roomRates: selectedElements.value
    }

    selectedElements.value = []
    await GenericService.import(confImportBookingApi.moduleApi, confImportBookingApi.uriApi, payload)
    processContinue = true
  }
  catch (error: any) {
    const messageError = error?.data?.data?.error?.errorMessage || 'Unexpected error. Please, try again'

    toast.add({
      severity: 'error',
      summary: 'Error importing bookings',
      detail: messageError,
      life: 3000
    })

    options.value.loading = false
    await getList()
    await searchAndFilterSearchErrors()
    showErrorsDataTable.value = false
    showDataTable.value = true
  }

  if (processContinue) {
    try {
      await checkProcessStatus(processId.value)

      await getErrorList(processId.value)

      if (listItemsErrors.value.length !== 0) {
        showErrorsDataTable.value = true
        showDataTable.value = false
        toast.add({
          severity: 'error',
          summary: 'Error',
          detail: `Failed to import data. Ensure all fields are correct.`,
          life: 5000
        })
        importProcess.value = false
      }
      else {
        toast.add({
          severity: 'info',
          summary: 'Confirmed',
          detail: `Import process successful. ${elementsToImportNumber} bookings imported.`,
          life: 5000
        })
        options.value.loading = false
        showErrorsDataTable.value = false
        showDataTable.value = true
        onClose()
      }

      // await updateBookingsStatus(processId.value)
    }
    catch (error: any) {
      const messageError = error?.data?.data?.error?.errorMessage || 'Unexpected error. Please, try again'

      // invocar al servicio de reverso de bookings
      listItemsErrors.value = idItemsToImport.map(item => ({
        id: item,
        message: messageError,
      }))
      // await updateBookingsStatus(processId.value)

      toast.add({
        severity: 'error',
        summary: 'Error importing bookings',
        detail: messageError,
        life: 3000
      })

      options.value.loading = false
      await getList()
      await searchAndFilterSearchErrors()
      showErrorsDataTable.value = false
      showDataTable.value = true
    }
    finally {
      options.value.loading = false
    }
  }
}

function onClose() {
  emit('close')
}

async function checkProcessStatus(id: any) {
  return new Promise<void>((resolve, reject) => {
    let status = 'RUNNING'
    let attempts = 0
    const maxAttempts = 150
    const interval = setInterval(async () => {
      try {
        attempts++
        if (attempts >= maxAttempts) {
          clearInterval(interval)
          reject(new Error('There was a problem with the import process. Please try again later.'))
          return
        }

        const response = await GenericService.getById(confInvoiceApi.moduleApi, confInvoiceApi.uriApi, id, 'import-status')
        status = response.status
        totalImportedRows.value = response.totalRows ?? 0

        if (status === 'FINISHED') {
          clearInterval(interval)
          resolve()
        }
      }
      catch (error: any) {
        clearInterval(interval)
        const errorMessage
          = error?.data?.data?.error?.errorMessage
          || 'An unexpected error occurred while checking the process status.'
        reject(new Error(errorMessage))
      }
    }, 2000)
  })
}

async function getErrorList(processId: any) {
  try {
    optionsListErrors.value.loading = true
    payload.value = { ...payload.value, query: processId }
    listItemsErrors.value = []
    const newListItems = []
    const response = await GenericService.search(confImportRoomRateApi.moduleApi, confImportRoomRateApi.uriApi, payload.value)
    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      newListItems.push(
        {
          ...iterator,
          id: iterator.id,
          hotel: `${iterator.hotel?.code} ${iterator.hotel?.name}`,
          agency: `${iterator.agency?.code} ${iterator.agency?.name}`,
          guestName: `${iterator.firstName} ${iterator.lastName}`,
          roomType: `${iterator.roomType?.code} ${iterator.roomType?.name}`,
          ratePlan: `${iterator.ratePlan?.code} ${iterator.ratePlan?.name}`,
          loadingEdit: false,
          loadingDelete: false
        }
      )
    }

    listItemsErrors.value = [...listItemsErrors.value, ...newListItems]
  }
  catch (error) {
    console.error('Error loading file:', error)
  }
  finally {
    optionsListErrors.value.loading = false
  }
}

async function updateBookingsStatus(processId: any) {
  try {
    const newListErrorResponse = []

    for (const item of listItemsErrors.value) {
      newListErrorResponse.push({
        bookingId: item.id,
        errorMessage: item.message
      })
    }

    const payload = {
      importProcessId: processId,
      errorResponses: newListErrorResponse
    }

    await GenericService.updateBookings(confImportBookingApi.moduleApi, confImportBookingApi.uriApi, payload)
  }
  catch (error: any) {
    toast.add({ severity: 'warn', summary: 'Completed with errors', detail: 'The import process finished with errors, please validate the bookings in Manage Invoice', life: 5000 })
  }
}

async function parseDataTableFilterErrorList(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsErrors)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      if (parseFilter[i]?.key === 'invoiceNumber') {
        parseFilter[i].key = 'invoiceNumberPrefix'
      }
    }
  }

  payload.value.filter = [...parseFilter || []]
  getErrorList(processId.value)
}

async function parseDataTableFilterSearchErrors(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsSearchErrors)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      if (parseFilter[i]?.key === 'invoiceNumber') {
        parseFilter[i].key = 'invoiceNumberPrefix'
      }
    }
  }

  payload.value.filter = [...parseFilter || []]
  getListSearchErrors()
}

async function resetErrorListItems() {
  errorListPayload.value.page = 0
  getErrorList(processId.value)
}

function onSortFieldErrorList(event: any) {
  if (event) {
    errorListPayload.value.sortBy = event.sortField
    errorListPayload.value.sortType = event.sortOrder
    getErrorList(processId.value)
  }
}

function onSortFieldSearchErrors(event: any) {
  if (event) {
    errorListPayload.value.sortBy = event.sortField
    errorListPayload.value.sortType = event.sortOrder
    getListSearchErrors()
  }
}

function getStatusBadgeBackgroundColorByItem(status: string) {
  if (!status) { return }
  if (status === 'PENDING') { return '#005FB7' }
  if (status === 'FAILED') { return '#F74646' }
}

function getStatusName(code: string) {
  switch (code) {
    case 'PENDING': return 'Pending'
    case 'FAILED': return 'Failed'
    default:
      return ''
  }
}

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 500

  getList()
})

watch(payloadOnChangePageErrorList, (newValue) => {
  errorListPayload.value.page = newValue?.page ? newValue?.page : 0
  errorListPayload.value.pageSize = newValue?.rows ? newValue.rows : 500

  getErrorList(processId.value)
})

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  await getHotelList()
})
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h5 class="mb-0">
      {{ options.tableName }}
    </h5>
  </div>
  <div class="grid -mr-4">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class="mt-3">
        <!-- <Accordion :active-index="0" class="mb-2"> -->
        <AccordionTab>
          <!-- <template #header>
            <div
              class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center"
            >
              <div>
                Filters
              </div>
            </div>
          </template> -->

          <div class="grid mr-1">
            <div class="col-12 md:col-6 lg:col-6 flex pb-0">
              <div class="flex flex-row gap-2 w-full">
                <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                  <label class="filter-label font-bold" for="">Agency:</label>
                  <div class="w-full" style=" z-index:5 ">
                    <DebouncedMultiSelectComponent
                      v-if="!loadingSaveAll"
                      id="autocomplete"
                      class="w-full"
                      field="name"
                      item-value="id"
                      :multiple="true"
                      :max-selected-labels="1"
                      :model="filterToSearch.agency"
                      :suggestions="agencyList"
                      @load="($event) => getAgencyList($event)"
                      @change="($event) => {
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
                    </DebouncedMultiSelectComponent>
                  </div>
                </div>
                <div class="flex align-items-center gap-2 w-full">
                  <label class="filter-label font-bold ml-3" for="">Hotel:</label>
                  <div class="w-full">
                    <DebouncedAutoCompleteComponent
                      v-if="!loadingSaveAll" id="autocomplete" :multiple="false"
                      class="w-full" field="name" item-value="id" :model="filterToSearch.hotel"
                      :suggestions="hotelList" @load="($event) => getHotelListFilter($event)" @change="($event) => { filterToSearch.hotel = $event }"
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
                  <Button
                    v-tooltip.top="'Copiar tabla'"
                    class="p-button-lg w-1rem h-2rem pt-2 -mr-2 -ml-2"
                    icon="pi pi-copy"
                    @click="copiarDatos"
                  />
                </div>
              </div>
            </div>
          </div>
        </AccordionTab>
        <!-- </Accordion> -->
      </div>
      <div class="mt-2" />

      <div v-if="showDataTable" class="p-0">
        <DynamicTable
          ref="resultTable"
          :data="listItems"
          :columns="columns"
          :options="options"
          :pagination="pagination"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="parseDataTableFilter"
          @on-list-item="resetListItems"
          @on-sort-field="onSortField"
          @update:clicked-item="onMultipleSelect($event)"
          @on-expand-row="getRoomRateByBooking($event)"
        >
          <template #column-status="{ data }">
            <Badge
              v-tooltip.top="data.message"
              :value="getStatusName(data.status)"
              :style="`background-color: ${getStatusBadgeBackgroundColorByItem(data.status)}`"
            />
          </template>
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

          <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center font-bold font-100" style="font-weight: 700">
              <Row>
                <Column footer="Totals:" :colspan="12" footer-style="text-align:right; font-weight: 700" />
                <Column :footer="formatNumber(Math.round((totalHotelInvoiceAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: 700; width:'10px' " />
                <Column :footer="formatNumber(Math.round((totalInvoiceAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: 700" />
                <Column :colspan="12" />
              </Row>
            </ColumnGroup>
          </template>
        </DynamicTable>
        <div class="flex align-items-center justify-content-end gap-2 mt-3">
          <ColumnGroup type="footer" class="flex align-items-center font-bold font-100" style="font-weight: 700">
            <Row>
              <Column footer="Booking to import:" :colspan="12" footer-style="text-align:right; font-weight: 700" />
              <Column :footer="formatNumber(Math.round((pagination.totalElements + Number.EPSILON) * 100) / 100)" footer-style="font-weight: 700; width:'10px' " />
              <Column :colspan="12" />
            </Row>
            <Row>
              <Column footer="Booking with errors:" :colspan="12" footer-style="text-align:right; font-weight: 700" />
              <Column :footer="formatNumber(Math.round((paginationSearchErrors.totalElements + Number.EPSILON) * 100) / 100)" footer-style="font-weight: 700; width:'10px' " />
              <Column :colspan="12" />
            </Row>
          </ColumnGroup>
        </div>
      </div>
      <div v-if="showErrorsDataTable" class="p-0">
        <DynamicTable
          :data="listItemsErrors"
          :columns="columnsErrors"
          :options="optionsListErrors"
          :pagination="paginationErroList"
          @on-change-pagination="payloadOnChangePageErrorList = $event"
          @on-change-filter="parseDataTableFilterErrorList"
          @on-list-item="resetErrorListItems"
          @on-sort-field="onSortFieldErrorList"
          @on-expand-row="getRoomRateByBookingErrors($event)"
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
                    <span v-if="!optionsListErrors?.loading" class="flex flex-column align-items-center justify-content-center">
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
        <Button v-tooltip.top="'View Errors Search'" :outlined="disabledSearchErrors" severity="danger" class="w-3rem mx-2" icon="pi pi-times-circle" :disabled="disabledSearchErrors" @click="openErrorsSearch($event)" />
        <!-- <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times" @click="clearForm" /> -->
      </div>
    </div>
  </div>
  <Dialog v-model:visible="showErrorsSearchDataTable" modal header="Bookings with errors" :style="{ width: '150rem' }">
    <div class="grid">
      <div class="col-12 md:order-1 md:col-12 xl:col-12">
        <DynamicTable
          :data="listItemsSearchErrors"
          :columns="columnsSearchErrors"
          :options="optionsSearchErrors"
          :pagination="paginationSearchErrors"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="parseDataTableFilterSearchErrors"
          @on-sort-field="onSortFieldSearchErrors"
          @on-expand-row="getRoomRateByBookingSearchErrors($event)"
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
                    <span v-if="!optionsListErrors?.loading" class="flex flex-column align-items-center justify-content-center">
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
    </div>
  </Dialog>
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
