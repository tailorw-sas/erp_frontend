<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import dayjs from 'dayjs'
import { formatNumber } from './utils/helperFilters'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean, statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'
import { itemMenuList } from '~/components/payment/indexBtns'
import IfCan from '~/components/auth/IfCan.vue'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const authStore = useAuthStore()
const { status, data } = useAuth()
const isAdmin = (data.value?.user as any)?.isAdmin === true

const allDefaultItem = { id: 'All', name: 'All', status: 'ACTIVE' }
const listItems = ref<any[]>([])
const clientItemsList = ref<any[]>([])
const agencyItemsList = ref<any[]>([])
const hotelItemsList = ref<any[]>([])
const statusItemsList = ref<any[]>([])
const openDialogApplyPayment = ref(false)
const disabledBtnApplyPayment = ref(true)
const objItemSelectedForRightClickApplyPayment = ref({} as GenericObject)
const paymentDetailsTypeDepositList = ref<any[]>([])
const paymentDetailsTypeDepositLoading = ref(false)
const paymentDetailsTypeDepositSelected = ref<any[]>([])

const startOfMonth = ref<any>(null)
const endOfMonth = ref<any>(null)

const checkApplyPayment = ref(true)
const idInvoicesSelectedToApplyPayment = ref<string[]>([])
const invoiceAmmountSelected = ref(0)
const paymentAmmountSelected = ref(0)
const paymentBalance = ref(0)

const idItemToLoadFirstTime = ref('')
const objApis = ref({
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  status: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
})

const legend = ref(
  [
    {
      name: 'Transit',
      color: '#ff002b',
      colClass: 'pr-4',
    },
    {
      name: 'Cancelled',
      color: '#888888',
      colClass: 'pr-4',
    },
    {
      name: 'Confirmed',
      color: '#0c2bff',
      colClass: 'pr-4',
    },
    {
      name: 'Applied',
      color: '#00b816',
      colClass: '',
    },
  ]
)
const filterAllDateRange = ref(false)
const filterToSearch = ref<GenericObject>({
  client: [allDefaultItem],
  agency: [allDefaultItem],
  allClientAndAgency: false,
  hotel: [allDefaultItem],
  status: [allDefaultItem],
  from: '',
  to: '',
  allFromAndTo: false,
  criteria: null,
  value: '',
  type: allDefaultItem,
  payApplied: null,
  detail: true,
})

const filterToSearchTemp = ref<GenericObject>({
  client: [allDefaultItem],
  agency: [allDefaultItem],
  allClientAndAgency: false,
  hotel: [allDefaultItem],
  status: [allDefaultItem],
  from: '',
  to: '',
  allFromAndTo: false,
  criteria: null,
  value: '',
  type: allDefaultItem,
  payApplied: null,
  detail: true,
})

const contextMenu = ref()
const allMenuListItems = ref([
  // {
  //   id: 'applayDeposit',
  //   label: 'Apply Deposit',
  //   icon: 'pi pi-dollar',
  //   command: ($event: any) => openModalWithContentMenu($event),
  //   disabled: true,
  //   visible: true,
  // },
  {
    id: 'applyPayment',
    label: 'Apply Payment',
    icon: 'pi pi-cog',
    command: ($event: any) => openModalApplyPayment($event),
    disabled: true,
    visible: true,
  },
  // {
  //   id: 'navigateToInvoice',
  //   label: 'Navigate to Invoice',
  //   icon: 'pi pi-cog',
  //   command: ($event: any) => navigateToInvoice($event),
  //   disabled: true,
  //   visible: true,
  // },
])

// -------------------------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'paymentId', name: 'Payment Id', disabled: false },
  { id: 'referenceNumber', name: 'Reference No', disabled: true },
  { id: 'paymentAmount', name: 'P. Amount', disabled: false },
  { id: 'remark', name: 'Remark' },
  { id: 'paymentDetails.paymentDetailId', name: 'Detail Id', disabled: false },
  { id: 'paymentDetails.amount', name: 'Detail Amount', disabled: false },
  { id: 'paymentDetails.remark', name: 'Detail Remark', disabled: false },
  { id: 'invoiceNo', name: 'Invoice No', disabled: true },
  { id: 'bookingId', name: 'Booking Id', disabled: true },
  { id: 'firstName', name: 'Guest First Name', disabled: true },
  { id: 'lastName', name: 'Guest Last Name', disabled: true },
  { id: 'reservation', name: 'Reservation', disabled: true },
  { id: 'coupon', name: 'Coupon No', disabled: true },
]

const ENUM_FILTER_TYPE = [
  { id: 'All', name: 'All', status: 'ACTIVE' },
  { id: 'applied', name: 'Applied' },
  { id: 'noApplied', name: 'UnApplied' },
]
// const ENUM_FILTER_STATUS = [
//   { id: 'All', name: 'All', status: 'ACTIVE' },
//   { id: 'TRANSIT', name: 'TRANSIT' },
//   { id: 'CANCELLED', name: 'CANCELLED' },
//   { id: 'CONFIRMED', name: 'CONFIRMED' },
//   { id: 'APPLIED', name: 'APPLIED' },
// ]

const sClassMap: IStatusClass[] = [
  { status: 'Transit', class: 'text-transit' },
  { status: 'Cancelled', class: 'text-cancelled' },
  { status: 'Confirmed', class: 'text-confirmed' },
  { status: 'Applied', class: 'text-applied' },
]

// TABLE COLUMNS -----------------------------------------------------------------------------------------
interface SubTotals {
  paymentAmount: number
  depositBalance: number
  applied: number
  noApplied: number
}
const subTotals = ref<SubTotals>({ paymentAmount: 0, depositBalance: 0, applied: 0, noApplied: 0 })

const columns: IColumn[] = [
  { field: 'icon', header: '', width: '25px', type: 'slot-icon', icon: 'pi pi-paperclip', sortable: false, showFilter: false, hidden: false },
  { field: 'paymentId', header: 'ID', tooltip: 'Payment ID', width: '40px', type: 'text', showFilter: true },
  { field: 'paymentSource', header: 'P.Source', tooltip: 'Payment Source', width: '60px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-source' } },
  { field: 'transactionDate', header: 'Trans. Date', tooltip: 'Transaction Date', width: '60px', type: 'date' },
  { field: 'hotel', header: 'Hotel', width: '80px', widthTruncate: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' } },
  { field: 'client', header: 'Client', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-client' } },
  { field: 'agency', header: 'Agency', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'agencyType', header: 'Agency Type', tooltip: 'Agency Type', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency-type' } },
  // { field: 'agencyTypeResponse', header: 'Agency Type', tooltip: 'Agency Type', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency-type' } },
  { field: 'bankAccount', header: 'Bank Acc', tooltip: 'Bank Account', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-bank-account', keyValue: 'accountNumber' } },
  { field: 'paymentAmount', header: 'P. Amount', tooltip: 'Payment Amount', width: '70px', type: 'text' },
  { field: 'depositBalance', header: 'D.Balance', tooltip: 'Deposit Balance', width: '60px', type: 'text' },
  { field: 'applied', header: 'Applied', tooltip: 'Applied', width: '60px', type: 'text' },
  { field: 'notApplied', header: 'Not Applied', tooltip: 'Not Applied', width: '60px', type: 'text' },
  { field: 'remark', header: 'Remark', width: '100px', type: 'text' },
  { field: 'paymentStatus', header: 'Status', width: '100px', frozen: true, type: 'slot-select', statusClassMap: sClassMap, objApi: { moduleApi: 'settings', uriApi: 'manage-payment-status' }, sortable: true },
  // { field: 'totalAmount', header: 'T.Amount', tooltip: 'Total Amount', width: '60px', type: 'text' },
  // { field: 'attachmentStatus', header: 'Attachment Status', width: '100px', type: 'select' },
  // { field: 'paymentBalance', header: 'Payment Balance', width: '200px', type: 'text' },
  // { field: 'depositAmount', header: 'Deposit Amount', width: '200px', type: 'text' },
  // { field: 'otherDeductions', header: 'Other Deductions', width: '200px', type: 'text' },
  // { field: 'identified', header: 'Identified', width: '200px', type: 'text' },
  // { field: 'notIdentified', header: 'Not Identified', width: '200px', type: 'text' },
]

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Payment',
  moduleApi: 'payment',
  uriApi: 'payment',
  loading: false,
  actionsAsMenu: true,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'paymentId',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')
const loadingSaveApplyPayment = ref(false)
const invoiceSelectedListForApplyPayment = ref<any[]>([])
const applyPaymentListOfInvoice = ref<any[]>([])
const applyPaymentColumns = ref<IColumn[]>([
  { field: 'invoiceNo', header: 'Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'invoiceNumber', header: 'Invoice Number', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'agency', header: 'Agency', type: 'select', width: '90px', sortable: false, showFilter: false },
  { field: 'hotel', header: 'Hotel', type: 'select', width: '90px', sortable: false, showFilter: false },
  { field: 'couponNumber', header: 'Coupon No', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'status', header: 'Status', type: 'slot-text', width: '90px', sortable: false, showFilter: false },
])

// Table
const columnsExpandTable: IColumn[] = [
  { field: 'bookingId', header: 'Id', width: '120px', type: 'text', sortable: false },
  { field: 'fullName', header: 'Full Name', width: '200px', type: 'text', sortable: false },
  { field: 'reservationNumber', header: 'Reservation No', width: '120px', type: 'text', sortable: false },
  // { field: 'invoiceNumber', header: 'Invoice No', width: '150px', type: 'text', sortable: false },
  { field: 'couponCode', header: 'Coupon No', width: '120px', type: 'text', sortable: false },
  // { field: 'adult', header: 'Adult', width: '120px', type: 'text', sortable: false },
  { field: 'checkIn', header: 'Check-In', width: '120px', type: 'text', sortable: false },
  { field: 'checkOut', header: 'Check-Out', width: '120px', type: 'text', sortable: false },
  { field: 'nights', header: 'Nights', width: '100px', type: 'text', sortable: false },
  { field: 'invoiceAmount', header: 'Booking Amount', width: '100px', type: 'text', sortable: false },
  { field: 'dueAmount', header: 'Booking Balance', width: '100px', type: 'text', sortable: false },
]

const columnsPaymentDetailTypeDeposit: IColumn[] = [
  { field: 'paymentDetailId', header: 'Id', tooltip: 'Detail Id', width: 'auto', type: 'text' },
  { field: 'transactionType', header: 'P. Trans Type', tooltip: 'Payment Transaction Type', width: '150px', type: 'text' },
  { field: 'amount', header: 'Deposit Amount', tooltip: 'Deposit Amount', width: 'auto', type: 'text' },
  { field: 'applyDepositValue', header: 'Deposit. Balance', tooltip: 'Deposit Amount', width: 'auto', type: 'text' },
  { field: 'remark', header: 'Remark', width: 'auto', type: 'text' },
  // { field: 'bookingId', header: 'Booking Id', tooltip: 'Booking Id', width: '100px', type: 'text' },
  // { field: 'invoiceNumber', header: 'Invoice No', tooltip: 'Invoice No', width: '100px', type: 'text' },
  // { field: 'transactionDate', header: 'Transaction Date', tooltip: 'Transaction Date', width: 'auto', type: 'text' },
  // { field: 'fullName', header: 'Full Name', tooltip: 'Full Name', width: '150px', type: 'text' },
  // // { field: 'firstName', header: 'First Name', tooltip: 'First Name', width: '150px', type: 'text' },
  // // { field: 'lastName', header: 'Last Name', tooltip: 'Last Name', width: '150px', type: 'text' },
  // { field: 'reservationNumber', header: 'Reservation No', tooltip: 'Reservation', width: 'auto', type: 'text' },
  // { field: 'couponNumber', header: 'Coupon No', tooltip: 'Coupon No', width: 'auto', type: 'text' },
  // // { field: 'checkIn', header: 'Check In', tooltip: 'Check In', width: 'auto', type: 'text' },
  // // { field: 'checkOut', header: 'Check Out', tooltip: 'Check Out', width: 'auto', type: 'text' },
  // { field: 'adults', header: 'Adults', tooltip: 'Adults', width: 'auto', type: 'text' },
  // { field: 'children', header: 'Children', tooltip: 'Children', width: 'auto', type: 'text' },
  // // { field: 'deposit', header: 'Deposit', tooltip: 'Deposit', width: 'auto', type: 'bool' },
  // { field: 'parentId', header: 'Parent Id', width: 'auto', type: 'text' },
  // // { field: 'groupId', header: 'Group Id', width: 'auto', type: 'text' },

]
const payloadpaymentDetailForTypeDeposit = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 300,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const applyPaymentOptions = ref({
  tableName: 'Apply Payment',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/search-payment',
  expandableRows: true,
  selectionMode: 'multiple',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const applyPaymentBookingOptions = ref({
  tableName: 'Booking',
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
  showDelete: false,
  showFilters: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const payloadToApplyPayment = ref<GenericObject> ({
  applyPayment: false,
  booking: ''
})

const applyPaymentPayload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.ASC
})
const applyPaymentPagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const applyPaymentOnChangePage = ref<PageState>()

const applyPaymentBookingPayload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.ASC
})
const applyPaymentBookingPagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const applyPaymentBookingOnChangePage = ref<PageState>()

// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCECSED': return '#FF8D00'
    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELED': return '#F90303'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

function getStatusName(code: string) {
  switch (code) {
    case 'PROCECSED': return 'Processed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Canceled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}

// Go To Create Form
function goToCreateForm() {
  navigateTo('/payment/create')
}

function goToCreateFormInNewTab() {
  const url = '/payment/create'
  window.open(url, '_blank')
}

function goToForm(item: any) {
  navigateTo({ path: '/payment/form', query: { id: item.hasOwnProperty('id') ? item.id : item } })
}

function goToFormInNewTab(item: any) {
  const id = item.hasOwnProperty('id') ? item.id : item
  const url = `/payment/form?id=${encodeURIComponent(id)}`
  window.open(url, '_blank')
}

async function getAgencyTypeByAgency(params: string) {
  if (params === '') {
    return []
  }
  else {
    const payload = {
      filter: [
        {
          key: 'id',
          operator: 'LIKE',
          value: params,
          logicalOperation: 'AND',
        }
      ],
      query: '',
      pageSize: 50,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }
    const response = await GenericService.search('settings', 'manage-agency', payload)
    return response.data
  }
}

async function getList() {
  const count: SubTotals = { paymentAmount: 0, depositBalance: 0, applied: 0, noApplied: 0 }
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
    const arrayNumero = []
    for (const iterator of dataList) {
      // if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
      //   iterator.status = statusToBoolean(iterator.status)
      // }

      // const agency = await getAgencyTypeByAgency(iterator.agency.id)

      // if (agency.length > 0) {
      //   iterator.agencyType = agency[0].agencyType
      // }
      if (Object.prototype.hasOwnProperty.call(iterator, 'agency')) {
        iterator.agencyType = iterator.agency.agencyTypeResponse
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'paymentId')) {
        iterator.paymentId = String(iterator.paymentId)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'paymentAmount')) {
        count.paymentAmount = count.paymentAmount + iterator.paymentAmount
        iterator.paymentAmount = formatNumber(iterator.paymentAmount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'depositBalance')) {
        count.depositBalance += iterator.depositBalance
        iterator.depositBalance = formatNumber(iterator.depositBalance)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'applied')) {
        count.applied += iterator.applied
        iterator.applied = String(iterator.applied)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'noApplied')) {
        count.noApplied += iterator.noApplied
        iterator.noApplied = String(iterator.noApplied)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'depositAmount')) {
        iterator.depositAmount = String(iterator.depositAmount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'otherDeductions')) {
        iterator.otherDeductions = String(iterator.otherDeductions)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'identified')) {
        iterator.identified = String(iterator.identified)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'notIdentified')) {
        iterator.notIdentified = String(iterator.notIdentified)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'bankAccount')) {
        iterator.bankAccount = {
          id: iterator?.bankAccount?.id,
          name: iterator?.bankAccount?.accountNumber
        }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = String(iterator.status)
      }

      // "paymentAmount": 1000,
      // "paymentBalance": 1000,
      // "depositAmount": 100,
      // "depositBalance": 100,
      // "otherDeductions": 0,
      // "identified": 100,
      // "notIdentified": 900,

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
    subTotals.value = { ...count }
  }
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criteria && filterToSearch.value.value) {
    let keyValue = ''
    const keyTemp = filterToSearch.value.criteria ? filterToSearch.value.criteria.id : ''
    if (keyTemp !== '' && keyTemp === 'id') {
      keyValue = 'paymentId'
    }
    payload.value.filter = [...payload.value.filter, {
      key: keyValue || (filterToSearch.value.criteria ? filterToSearch.value.criteria.id : ''),
      operator: keyTemp === 'paymentDetails.remark' ? 'LIKE' : 'EQUALS',
      value: filterToSearch.value.value,
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  else {
    payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
    // Client
    if (filterToSearch.value.client?.length > 0) {
      const filteredItems = filterToSearch.value.client.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'client.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    // Agency
    if (filterToSearch.value.agency?.length > 0) {
      const filteredItems = filterToSearch.value.agency.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'agency.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    // Hotel
    if (filterToSearch.value.hotel?.length > 0) {
      const filteredItems = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'hotel.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    // Status
    if (filterToSearch.value.status?.length > 0) {
      const filteredItems = filterToSearch.value.status.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'paymentStatus.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }

    // Date
    if (filterToSearch.value.from) {
      payload.value.filter = [...payload.value.filter, {
        key: 'transactionDate',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.to) {
      payload.value.filter = [...payload.value.filter, {
        key: 'transactionDate',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }

    // Pay Apply
    if (filterToSearch.value.payApplied !== null) {
      payload.value.filter = [...payload.value.filter, {
        key: 'payApply',
        operator: 'EQUAL',
        value: filterToSearch.value.payApplied,
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }

    // // Detail
    // if (filterToSearch.value.detail !== null) {
    //   payload.value.filter = [...payload.value.filter, {
    //     key: 'paymentDetails',
    //     operator: 'EXISTS',
    //     value: '',
    //     logicalOperation: 'AND',
    //     type: 'filterSearch'
    //   }]
    // }
  }
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value = JSON.parse(JSON.stringify(filterToSearchTemp.value))
  filterToSearch.value.criteria = ENUM_FILTER[0]
  getList()
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  const objFilter = parseFilter?.find((item: IFilter) => item?.key === 'agencyType.id')
  if (objFilter) {
    objFilter.key = 'agency.agencyType.id'
  }

  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'paymentStatus') {
      event.sortField = 'paymentStatus.name'
    }
    if (event.sortField === 'paymentSource') {
      event.sortField = 'paymentSource.name'
    }
    if (event.sortField === 'bankAccount') {
      event.sortField = 'bankAccount.accountNumber'
    }
    if (event.sortField === 'client') {
      event.sortField = 'client.name'
    }
    if (event.sortField === 'agencyType') {
      event.sortField = 'agency.agencyType.name'
    }
    if (event.sortField === 'agency') {
      event.sortField = 'agency.name'
    }
    if (event.sortField === 'hotel') {
      event.sortField = 'hotel.name'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
}

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.name}`,
    status: data.status
  }
}

async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let clientTemp: any[] = []
  clientItemsList.value = [allDefaultItem]
  clientTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
  clientItemsList.value = [...clientItemsList.value, ...clientTemp]
}
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let agencyTemp: any[] = []
  agencyItemsList.value = [allDefaultItem]
  agencyTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
  agencyItemsList.value = [...agencyItemsList.value, ...agencyTemp]
}
async function getAgencyListTemp(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}
async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let hotelTemp: any[] = []
  hotelItemsList.value = [allDefaultItem]
  hotelTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
  hotelItemsList.value = [...hotelItemsList.value, ...hotelTemp]
}

async function getHotelListTemp(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

async function getStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let statusTemp: any[] = []
  statusItemsList.value = [allDefaultItem]
  statusTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
  statusItemsList.value = [...statusItemsList.value, ...statusTemp]
}

async function applyPaymentGetList() {
  if (applyPaymentOptions.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    applyPaymentOptions.value.loading = true
    applyPaymentListOfInvoice.value = []
    const newListItems = []

    // Validacion para busar por por las agencias
    const filter: FilterCriteria[] = [
      {
        key: 'client.id',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: objItemSelectedForRightClickApplyPayment.value?.client.id,
      },
      {
        key: 'status',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: 'ACTIVE',
      },
    ]
    const objQueryToSearch = {
      query: '',
      keys: ['name', 'code'],
    }

    let listAgenciesForApplyPayment: any[] = []
    listAgenciesForApplyPayment = await getAgencyListTemp(objApis.value.agency.moduleApi, objApis.value.agency.uriApi, objQueryToSearch, filter)

    if (listAgenciesForApplyPayment.length > 0) {
      const objFilter = applyPaymentPayload.value.filter.find(item => item.key === 'agency.id')

      if (objFilter) {
        objFilter.value = listAgenciesForApplyPayment.map(item => item.id)
      }
      else {
        // applyPaymentPayload.value.filter.push({
        //   key: 'agency.id',
        //   operator: 'IN',
        //   value: listAgenciesForApplyPayment.map(item => item.id),
        //   logicalOperation: 'AND'
        // })
      }
    }

    // Validacion para bucsar por los hoteles
    if (objItemSelectedForRightClickApplyPayment.value?.hotel && objItemSelectedForRightClickApplyPayment.value?.hotel.id) {
      if (objItemSelectedForRightClickApplyPayment.value?.hotel.applyByTradingCompany) {
        // Obtener los hoteles dado el id de la agencia del payment y ademas de eso que pertenezcan a la misma trading company del hotel seleccionado
        const filter: FilterCriteria[] = [
          // {
          //   key: 'agency.id',
          //   logicalOperation: 'AND',
          //   operator: 'EQUALS',
          //   value: item.value.agency.id,
          // },
          {
            key: 'manageTradingCompanies.id',
            logicalOperation: 'AND',
            operator: 'EQUALS',
            value: objItemSelectedForRightClickApplyPayment.value?.hotel?.manageTradingCompany,
          },
        ]
        const objQueryToSearch = {
          query: '',
          keys: ['name', 'code'],
        }

        let listHotelsForApplyPayment: any[] = []
        listHotelsForApplyPayment = await getHotelListTemp(objApis.value.hotel.moduleApi, objApis.value.hotel.uriApi, objQueryToSearch, filter)

        if (listHotelsForApplyPayment.length > 0) {
          const objFilter = applyPaymentPayload.value.filter.find(item => item.key === 'hotel.id')

          if (objFilter) {
            objFilter.value = listHotelsForApplyPayment.map(item => item.id)
          }
          else {
            applyPaymentPayload.value.filter.push({
              key: 'hotel.id',
              operator: 'IN',
              value: listHotelsForApplyPayment.map(item => item.id),
              logicalOperation: 'AND'
            })
          }
        }
      }
      else {
        const objFilter = applyPaymentPayload.value.filter.find(item => item.key === 'hotel.id')

        if (objFilter) {
          objFilter.value = objItemSelectedForRightClickApplyPayment.value?.hotel.id
        }
        else {
          // applyPaymentPayload.value.filter.push({
          //   key: 'hotel.id',
          //   operator: 'EQUALS',
          //   value: objItemSelectedForRightClickApplyPayment.value?.hotel.id,
          //   logicalOperation: 'AND'
          // })
        }
      }
    }

    const objFilter = applyPaymentPayload.value.filter.find(item => item.key === 'dueAmount')

    if (objFilter) {
      objFilter.value = 0
    }
    else {
      applyPaymentPayload.value.filter.push({
        key: 'dueAmount',
        operator: 'GREATER_THAN',
        value: 0,
        logicalOperation: 'AND'
      })
    }

    const response = await GenericService.search(applyPaymentOptions.value.moduleApi, applyPaymentOptions.value.uriApi, applyPaymentPayload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    applyPaymentPagination.value.page = page
    applyPaymentPagination.value.limit = size
    applyPaymentPagination.value.totalElements = totalElements
    applyPaymentPagination.value.totalPages = totalPages

    const existingIds = new Set(applyPaymentListOfInvoice.value.map(item => item.id))

    for (const iterator of dataList) {
      for (const booking of iterator.bookings) {
        booking.checkIn = booking.checkIn ? dayjs(booking.checkIn).format('YYYY-MM-DD') : null
        booking.checkOut = booking.checkOut ? dayjs(booking.checkOut).format('YYYY-MM-DD') : null
      }
      // iterator.invoiceId = iterator.invoice?.invoiceId.toString()
      // iterator.bookingAmount = iterator.invoiceAmount?.toString()
      // iterator.bookingBalance = iterator.dueAmount?.toString()
      // iterator.paymentStatus = iterator.status

      iterator.bookingsList = []

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false, loadingBookings: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    applyPaymentListOfInvoice.value = [...applyPaymentListOfInvoice.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    applyPaymentOptions.value.loading = false
  }
}

async function applyPaymentBookingGetList(idInvoice: string = '') {
  if (idInvoice && idInvoice !== '') {
    const objInvoice = applyPaymentListOfInvoice.value.find(item => item.id === idInvoice)
    if (objInvoice.loadingBookings) {
    // Si ya hay una solicitud en proceso, no hacer nada.
      return
    }
    try {
      objInvoice.loadingBookings = true
      let listBookingsForApplyPayment: any[] = []
      const newListItems = []

      applyPaymentBookingPayload.value.filter = [
        {
          key: 'invoice.id',
          operator: 'EQUALS',
          value: idInvoice,
          logicalOperation: 'AND'
        }
      ]

      const response = await GenericService.search(applyPaymentBookingOptions.value.moduleApi, applyPaymentBookingOptions.value.uriApi, applyPaymentBookingPayload.value)

      const { data: dataList, page, size, totalElements, totalPages } = response

      applyPaymentBookingPagination.value.page = page
      applyPaymentBookingPagination.value.limit = size
      applyPaymentBookingPagination.value.totalElements = totalElements
      applyPaymentBookingPagination.value.totalPages = totalPages

      const existingIds = new Set(listBookingsForApplyPayment.map(item => item.id))

      for (const iterator of dataList) {
        iterator.invoiceId = iterator.invoice?.invoiceId.toString()
        iterator.checkIn = iterator.checkIn ? dayjs(iterator.checkIn).format('YYYY-MM-DD') : null
        iterator.checkOut = iterator.checkOut ? dayjs(iterator.checkOut).format('YYYY-MM-DD') : null
        iterator.bookingAmount = iterator.invoiceAmount?.toString()
        iterator.bookingBalance = iterator.dueAmount?.toString()

        // Verificar si el ID ya existe en la lista
        if (!existingIds.has(iterator.id)) {
          newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
          existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
        }
      }

      listBookingsForApplyPayment = [...listBookingsForApplyPayment, ...newListItems]
      objInvoice.bookingsList = [...listBookingsForApplyPayment]
    }
    catch (error) {
      objInvoice.loadingBookings = false
      console.error(error)
    }
    finally {
      objInvoice.loadingBookings = false
    }
  }
  else {
    applyPaymentBookingPayload.value.filter = []
  }
}

async function getListPaymentDetailTypeDeposit() {
  if (paymentDetailsTypeDepositLoading.value) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    paymentDetailsTypeDepositLoading.value = true
    paymentDetailsTypeDepositList.value = []
    const newListItems = []

    const objFilter = payloadpaymentDetailForTypeDeposit.value.filter.find(item => item.key === 'payment.id')

    if (objFilter) {
      objFilter.value = objItemSelectedForRightClickApplyPayment.value.id
    }
    else {
      payloadpaymentDetailForTypeDeposit.value.filter.push({
        key: 'payment.id',
        operator: 'EQUALS',
        value: objItemSelectedForRightClickApplyPayment.value.id,
        logicalOperation: 'AND'
      })
    }

    const objFilterForTransactionType = payloadpaymentDetailForTypeDeposit.value.filter.find(item => item.key === 'transactionType.deposit')

    if (objFilterForTransactionType) {
      objFilterForTransactionType.value = true
    }
    else {
      payloadpaymentDetailForTypeDeposit.value.filter.push({
        key: 'transactionType.deposit',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'AND'
      })
    }

    const response = await GenericService.search('payment', 'payment-detail', payloadpaymentDetailForTypeDeposit.value)

    const { data: dataList } = response
    // page, size, totalElements, totalPages
    // pagination.value.page = page
    // pagination.value.limit = size
    // pagination.value.totalElements = totalElements
    // pagination.value.totalPages = totalPages

    const existingIds = new Set(paymentDetailsTypeDepositList.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        iterator.amount = (!Number.isNaN(iterator.amount) && iterator.amount !== null && iterator.amount !== '')
          ? Number.parseFloat(iterator.amount).toString()
          : '0'

        iterator.amount = formatNumber(iterator.amount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'transactionDate')) {
        iterator.transactionDate = iterator.transactionDate ? dayjs(iterator.transactionDate).format('YYYY-MM-DD') : null
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'parentId')) {
        iterator.parentId = iterator.parentId ? iterator.parentId.toString() : ''
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'transactionType')) {
        iterator.transactionType = `${iterator.transactionType.code} - ${iterator.transactionType.name}`

        if (iterator.deposit) {
          iterator.rowClass = 'row-deposit' // Clase CSS para las transacciones de tipo deposit
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'manageBooking')) {
        iterator.adults = iterator.manageBooking?.adults?.toString()
        iterator.children = iterator.manageBooking?.children?.toString()
        iterator.couponNumber = iterator.manageBooking?.couponNumber?.toString()
        iterator.fullName = iterator.manageBooking?.fullName
        iterator.reservationNumber = iterator.manageBooking?.reservationNumber?.toString()
        iterator.bookingId = iterator.manageBooking?.bookingId?.toString()
        iterator.invoiceNumber = iterator.manageBooking?.invoice?.invoiceNumber?.toString()
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    paymentDetailsTypeDepositList.value = [...paymentDetailsTypeDepositList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    paymentDetailsTypeDepositLoading.value = false
  }
}

function closeModalApplyPayment() {
  objItemSelectedForRightClickApplyPayment.value = {}
  openDialogApplyPayment.value = false
}

async function openModalApplyPayment() {
  openDialogApplyPayment.value = true
  paymentAmmountSelected.value = objItemSelectedForRightClickApplyPayment.value.paymentBalance
  paymentBalance.value = objItemSelectedForRightClickApplyPayment.value.paymentBalance
  applyPaymentGetList()
  getListPaymentDetailTypeDeposit()
}

async function onExpandRowApplyPayment(event: any) {
  await applyPaymentBookingGetList(event)
}

function onRowContextMenu(event: any) {
  if (event && event.data && (event.data.notIdentified !== '' || event.data.notIdentified !== null) && event.data.notIdentified > 0) {
    objItemSelectedForRightClickApplyPayment.value = event.data
    const menuItemApplayPayment = allMenuListItems.value.find(item => item.id === 'applyPayment')
    if (menuItemApplayPayment) {
      menuItemApplayPayment.disabled = false
      menuItemApplayPayment.visible = true
    }
  }
  else {
    objItemSelectedForRightClickApplyPayment.value = {}
    const menuItemApplayPayment = allMenuListItems.value.find(item => item.id === 'applyPayment')
    if (menuItemApplayPayment) {
      menuItemApplayPayment.disabled = true
      menuItemApplayPayment.visible = false
    }
  }

  const allHidden = allMenuListItems.value.every(item => !item.visible)
  if (!allHidden) {
    contextMenu.value.show(event.originalEvent)
  }
  else {
    contextMenu.value.hide()
  }
}

async function onRowDoubleClickInDataTableApplyPayment(event: any) {
  try {
    const payloadToApplyPayment: GenericObject = {
      payment: objItemSelectedForRightClickApplyPayment.value.id || '',
      invoices: []
    }

    const response: any = await GenericService.create('payment', 'payment-detail/apply-payment', payloadToApplyPayment)

    if (response) {
      openDialogApplyPayment.value = false
      toast.add({ severity: 'success', summary: 'Successful', detail: 'Payment has been applied successfully', life: 3000 })
    }
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Payment could not be applied', life: 3000 })
  }
}

async function addAmmountsToApplyPayment(event: any) {
  idInvoicesSelectedToApplyPayment.value = event
  const selectedIds = event
  if (selectedIds.length === 0) {
    disabledBtnApplyPayment.value = true
    return invoiceAmmountSelected.value = 0
  }
  // Filtramos los objetos cuyos IDs coincidan con los del array 'selectedIds'
  invoiceAmmountSelected.value = applyPaymentListOfInvoice.value
    .filter(item => selectedIds.includes(item.id)) // Solo los que tengan un id que coincida
    .reduce((total, item) => {
      // Verificamos si invoiceAmount es un número válido
      if (typeof item.dueAmount === 'number' && !Number.isNaN(item.dueAmount)) {
        return total + item.dueAmount
      }
      else {
        return total // Si no es válido, simplemente lo ignoramos
      }
    }, 0) // 0 es el valor inicial
  disabledBtnApplyPayment.value = false
}

function sumAmountOfPaymentDetailTypeDeposit(transactions: any[]) {
  if (!transactions || transactions.length === 0) {
    return 0 // Si el array no tiene valores, devolver 0
  }

  return transactions.reduce((total, item) => {
    const amount = Number.parseFloat(item.amount.replace(/,/g, ''))

    // Verificar si el amount es un número válido y sumarlo
    if (!Number.isNaN(amount)) {
      return total + Math.abs(amount) // Convertir a positivo y sumar
    }

    return total
  }, 0) // Valor inicial es 0
}

function sumApplyPamentAmount(transactions: any[]) {
  if (checkApplyPayment.value) {
    paymentAmmountSelected.value = paymentBalance.value + sumAmountOfPaymentDetailTypeDeposit(transactions)
  }
  else {
    paymentAmmountSelected.value = sumAmountOfPaymentDetailTypeDeposit(transactions)
  }
}

async function changeValueByCheckApplyPaymentBalance(valueOfCheckbox: boolean) {
  // paymentDetailsTypeDepositSelected
  if (valueOfCheckbox) {
    sumApplyPamentAmount(paymentDetailsTypeDepositSelected.value)
  }
  else {
    paymentAmmountSelected.value = sumAmountOfPaymentDetailTypeDeposit(paymentDetailsTypeDepositSelected.value)
  }
}

async function saveApplyPayment() {
  try {
    loadingSaveApplyPayment.value = true
    const payload = {
      payment: objItemSelectedForRightClickApplyPayment.value.id || '',
      invoices: [...idInvoicesSelectedToApplyPayment.value]
    }
    const response = await GenericService.create('payment', 'payment/apply-payment', payload)

    if (response) {
      loadingSaveApplyPayment.value = false
      openDialogApplyPayment.value = false
    }
  }
  catch (error) {
    objItemSelectedForRightClickApplyPayment.value = {}
    idInvoicesSelectedToApplyPayment.value = []
    loadingSaveApplyPayment.value = false
  }
}

function getMonthStartAndEnd(date) {
  const startOfMonth = dayjs(date).startOf('month').format('YYYY-MM-DD')
  const endOfMonth = dayjs(date).endOf('month').format('YYYY-MM-DD')
  return { startOfMonth, endOfMonth }
}

const disabledDates = computed(() => {
  return filterAllDateRange.value
})

function toggle(event: Event, index: number) {
  const menu: any = itemMenuList.value[index].menuRef
  if (menu) {
    menu.toggle(event)
  }
  else {
    console.error('Menu reference is not defined or initialized.')
  }
}

function havePermissionMenu() {
  for (const rootMenu of itemMenuList.value) {
    if (rootMenu.menuItems.length > 0) {
      for (const childMenu of rootMenu.menuItems[0].items) {
        if (childMenu?.permission && childMenu?.permission?.length > 0) {
          (status.value === 'authenticated' && (isAdmin || authStore.can(childMenu.permission))) ? childMenu.disabled = false : childMenu.disabled = true
        }
      }
    }
  }
}
// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(paymentDetailsTypeDepositSelected, (newValue) => {
  sumApplyPamentAmount(newValue)
}, { deep: true })

watch(filterAllDateRange, (newValue) => {
  if (newValue) {
    filterToSearch.value.from = dayjs(startOfMonth.value).format('YYYY-MM-DD')
    filterToSearch.value.to = dayjs(endOfMonth.value).format('YYYY-MM-DD')
  }
})

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})

watch(filterToSearch, (newValue) => {
  if (newValue.status.length > 1 && newValue.status.find((item: { id: string, name: string, status?: string }) => item.id === 'All')) {
    filterToSearch.value.status = newValue.status.filter((item: { id: string, name: string, status?: string }) => item.id !== 'All')
  }
}, { deep: true })

// watch(invoiceSelectedListForApplyPayment, (newValue) => {
//   console.log(newValue)
// }, { deep: true })

// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(async () => {
  havePermissionMenu()

  startOfMonth.value = getMonthStartAndEnd(new Date()).startOfMonth
  endOfMonth.value = getMonthStartAndEnd(new Date()).endOfMonth
  filterToSearch.value.from = dayjs(startOfMonth.value).format('YYYY-MM-DD')
  filterToSearch.value.to = dayjs(endOfMonth.value).format('YYYY-MM-DD')
  filterToSearch.value.criteria = ENUM_FILTER[0]
  // if (useRuntimeConfig().public.loadTableData) {
  // }
  await getList()
  const objQueryToSearch = {
    query: '',
    keys: ['name', 'code'],
  }
  await getStatusList(objApis.value.status.moduleApi, objApis.value.status.uriApi, objQueryToSearch)
  filterToSearch.value.status = statusItemsList.value.filter((item: { id: string, name: string, status?: string }) => item.name === 'Applied' || item.name === 'Confirmed')
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Payment Management
    </h3>
    <div class="flex justify-content-end px-0">
      <span v-for="(objBtnAndMenu, index) in itemMenuList" :key="index">
        <IfCan :perms="objBtnAndMenu.permission && objBtnAndMenu?.permission?.length > 0 ? objBtnAndMenu.permission : []">
          <div v-if="objBtnAndMenu.showBtn()" class="my-2 flex justify-content-end pl-2 pr-0">
            <Button
              v-tooltip.left="objBtnAndMenu.btnToolTip"
              :label="objBtnAndMenu.btnLabel"
              severity="primary"
              :disabled="objBtnAndMenu.btnDisabled"
              aria-haspopup="true"
              :aria-controls="objBtnAndMenu.menuId"
              @click="toggle($event, index)"
            >
              <template #icon>
                <i
                  v-if="objBtnAndMenu?.pBtnIcon !== undefined && objBtnAndMenu.pBtnIcon !== null && objBtnAndMenu.pBtnIcon !== ''"
                  :class="objBtnAndMenu.pBtnIcon"
                  class="mr-2"
                  style="width: 20px; height: 20px; margin: 0 auto; display: flex; justify-content: center; align-items: center;"
                />
                <span v-else class="mr-2 flex align-items-center justify-content-center p-0">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    :height="objBtnAndMenu.btnIcon.svgHeight"
                    viewBox="0 -960 960 960"
                    :width="objBtnAndMenu.btnIcon.svgWidth"
                    :fill="objBtnAndMenu.btnIcon.svgFill"
                  >
                    <path :d="objBtnAndMenu.btnIcon.svgPath" />
                  </svg>
                </span>
              </template>
            </Button>
            <Menu
              v-if="objBtnAndMenu.menuItems.length > 0"
              :id="objBtnAndMenu.menuId"
              :ref="el => objBtnAndMenu.menuRef = el"
              :model="objBtnAndMenu.menuItems"
              :popup="true"
            />
          </div>
        </IfCan>
      </span>
    </div>
  </div>
  <div>
    <div class="card p-0 m-0">
      <Accordion id="accordion" :active-index="0">
        <AccordionTab content-class="p-0 m-0">
          <template #header>
            <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
              <div>
                Search Fields
              </div>
              <div>
                <PaymentLegend :legend="legend" />
              </div>
            </div>
          </template>
          <div v-if="true" class="grid p-0 m-0" style="margin: 0 auto;">
            <!-- first filter -->
            <div class="col-12 md:col-2 align-items-center my-0 py-0 w-auto">
              <div class="grid align-items-center justify-content-center">
                <div class="col-12">
                  <div class="flex align-items-center mb-2">
                    <!-- <pre>{{ filterToSearch }}</pre> -->
                    <label for="" class="mr-2 font-bold"> Client</label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        field="name"
                        item-value="id"
                        class="w-full"
                        :multiple="true"
                        :model="filterToSearch.client"
                        :suggestions="[...clientItemsList]"
                        @change="async ($event) => {
                          if (!filterToSearch.client.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.client = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.client = $event.filter((element: any) => element?.id !== 'All')
                          }
                          if (filterToSearch.client.length === 0) {
                            filterToSearch.agency = []
                          }
                        }"
                        @load="async($event) => {
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          const filter: FilterCriteria[] = [{
                            key: 'status',
                            logicalOperation: 'AND',
                            operator: 'EQUALS',
                            value: 'ACTIVE',
                          }]
                          await getClientList(objApis.client.moduleApi, objApis.client.uriApi, objQueryToSearch, filter)
                        }"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <label for="" class="mr-2 font-bold"> Agency</label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        field="name"
                        item-value="id"
                        class="w-full"
                        :multiple="true"
                        :model="filterToSearch.agency"
                        :suggestions="[...agencyItemsList]"
                        @change="($event) => {
                          if (!filterToSearch.agency.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.agency = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                        @load="async($event) => {
                          let ids = []
                          if (filterToSearch.client.length > 0) {
                            ids = filterToSearch.client.map((element: any) => element?.id)
                          }

                          const filter: FilterCriteria[] = [
                            {
                              key: 'client.id',
                              logicalOperation: 'AND',
                              operator: 'IN',
                              value: ids,
                            },
                            {
                              key: 'status',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                            },
                          ]
                          await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, {
                            query: $event,
                            keys: ['name', 'code'],
                          }, filter)
                        }"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- second filter -->
            <div class="col-12 md:col-2 align-items-center my-0 py-0 w-auto">
              <div class="grid align-items-center justify-content-center">
                <div class="col-12">
                  <div class="flex align-items-center mb-2">
                    <label for="" class="mr-2 font-bold"> Hotels</label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        class="w-full"
                        field="name"
                        item-value="id"
                        :multiple="true"
                        :model="filterToSearch.hotel"
                        :suggestions="[...hotelItemsList]"
                        @change="($event) => {
                          if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                        @load="async($event) => {
                          const filter: FilterCriteria[] = [
                            {
                              key: 'status',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                            },
                          ]
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          await getHotelList(objApis.hotel.moduleApi, objApis.hotel.uriApi, objQueryToSearch, filter)
                        }"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <label for="" class="mr-2 font-bold">Status</label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        class="w-full"
                        field="name"
                        item-value="id"
                        :multiple="true"
                        :model="filterToSearch.status"
                        :suggestions="[...statusItemsList]"
                        @change="($event) => {
                          if (!filterToSearch.status.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.status = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.status = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                        @load="async($event) => {
                          const filter: FilterCriteria[] = [
                            {
                              key: 'status',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                            },
                          ]
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          await getStatusList(objApis.status.moduleApi, objApis.status.uriApi, objQueryToSearch, filter)
                        }"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- third filter From - To -->
            <div class="col-12 md:col-2 align-items-center my-0 py-0 w-auto">
              <div class="grid align-items-center justify-content-center p-0 m-0">
                <div class="col-12 md:col-10 p-0 m-0 w-auto">
                  <div class="flex align-items-center mb-2">
                    <label for="" class="mr-2 font-bold"> From</label>
                    <div class="w-9rem">
                      <Calendar
                        id="from"
                        v-model="filterToSearch.from"
                        :min-date="new Date(startOfMonth)"
                        :max-date="filterToSearch.to ? new Date(filterToSearch.to) : new Date(endOfMonth)"
                        class="w-full"
                        date-format="dd/mm/yy"
                        :disabled="disabledDates"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <label for="" class="mr-2 font-bold" style="padding-right: 17px;"> To</label>
                    <div class="w-9rem">
                      <Calendar
                        id="to"
                        v-model="filterToSearch.to"
                        class="w-auto"
                        :min-date="filterToSearch.from ? new Date(filterToSearch.from) : new Date(startOfMonth)"
                        :max-date="new Date(endOfMonth)"
                        date-format="dd/mm/yy"
                        :disabled="disabledDates"
                      />
                    </div>
                  </div>
                </div>
                <div class="col-12 md:col-1 w-auto">
                  <div class="flex justify-content-end">
                    <Checkbox
                      v-model="filterAllDateRange"
                      binary
                      class="mr-2"
                      @change="() => {
                        if (!filterAllDateRange) {
                          filterToSearch.from = startOfMonth
                          filterToSearch.to = endOfMonth
                        }
                      }"
                    />
                    <label for="" class="mr-2 font-bold">All</label>
                  </div>
                </div>
              </div>
            </div>

            <!-- fourth filter -->
            <div class="col-12 md:col-2 align-items-center my-0 py-0 w-auto">
              <div class="grid align-items-center justify-content-center">
                <div class="col-12">
                  <div class="flex align-items-center mb-2">
                    <label for="" class="mr-2 font-bold"> Criteria</label>
                    <div class="w-10rem">
                      <Dropdown
                        v-model="filterToSearch.criteria"
                        :options="[...ENUM_FILTER]"
                        option-label="name"
                        placeholder="Criteria"
                        return-object="false"
                        option-disabled="disabled"
                        class="align-items-center w-full"
                        show-clear
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <label for="" class="w-4rem font-bold">Value</label>
                    <InputText v-model="filterToSearch.value" type="text" placeholder="" class="w-10rem" style="max-width: 10rem;" />
                  </div>
                </div>
              </div>
            </div>

            <!-- fifth filter -->
            <div class="col-12 md:col-2 align-items-center my-0 py-0 w-auto">
              <div class="grid align-items-center justify-content-center">
                <div class="col-12">
                  <div class="flex align-items-center mb-2">
                    <label for="" class="mr-2 font-bold"> Type</label>
                    <div class="w-full">
                      <Dropdown
                        v-model="filterToSearch.type"
                        :options="[...ENUM_FILTER_TYPE]"
                        option-label="name"
                        placeholder="Type"
                        return-object="false"
                        class="align-items-center w-full"
                        show-clear
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center justify-content-between pl-6">
                    <div class="flex align-items-center">
                      <label for="payApplied" class="mr-2 font-bold"> Pay Applied</label>
                      <TriStateCheckbox
                        id="payApplied"
                        v-model="filterToSearch.payApplied"
                        :binary="true"
                      />
                    </div>

                    <!-- <div class="flex align-items-center ml-2">
                      <label for="detail" class="mr-2 font-bold"> Details</label>
                      <TriStateCheckbox
                        id="detail"
                        v-model="filterToSearch.detail"
                        :binary="true"
                      />
                    </div> -->
                  </div>
                </div>
              </div>
            </div>

            <!-- Button filter -->
            <div class="col-12 md:col-1 flex align-items-center my-0 py-0 w-auto justify-content-center">
              <Button
                v-tooltip.top="'Filter'"
                label=""
                class="p-button-lg w-3rem h-3rem mr-2"
                icon="pi pi-search"
                @click="searchAndFilter"
              />
              <Button
                v-tooltip.top="'Clear'"
                label=""
                outlined
                class="p-button-lg w-3rem h-3rem"
                icon="pi pi-filter-slash"
                @click="clearFilterToSearch"
              />
            </div>
          </div>
        </AccordionTab>
      </Accordion>
    </div>
    <div v-if="false" class="card py-2 my-2 flex justify-content-between flex-column md:flex-row" style="border: 0.5px solid #e6e6e6;">
      <div class="text-xl font-bold flex align-items-center text-primary">
        Payment
      </div>
    </div>
    <DynamicTable
      :data="listItems"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      @on-confirm-create="goToCreateForm"
      @on-change-pagination="payloadOnChangePage = $event"
      @on-change-filter="parseDataTableFilter"
      @on-list-item="resetListItems"
      @on-sort-field="onSortField"
      @open-edit-dialog="goToFormInNewTab($event)"
      @on-row-double-click="goToFormInNewTab($event)"
      @on-row-right-click="onRowContextMenu($event)"
    >
      <template #column-icon="{ data, column }">
        <Button
          v-if="data.hasAttachment"
          :icon="column.icon"
          class="p-button-rounded p-button-text w-2rem h-2rem"
          aria-label="Submit"
          style="color: #616161;"
        />
      </template>

      <template #column-paymentStatus="{ data, column }">
        <Badge
          v-tooltip.top="data.paymentStatus.name.toString()"
          :value="data.paymentStatus.name"
          :class="column.statusClassMap?.find(e => e.status === data.paymentStatus.name)?.class"
        />
      </template>
      <template #datatable-footer>
        <ColumnGroup type="footer" class="flex align-items-center">
          <Row>
            <Column footer="Totals:" :colspan="9" footer-style="text-align:right; font-weight: bold;" />
            <Column :footer="formatNumber(Math.round((subTotals.paymentAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
            <Column :footer="formatNumber(Math.round((subTotals.depositBalance + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
            <Column :footer="formatNumber(Math.round((subTotals.applied + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
            <Column :footer="formatNumber(Math.round((subTotals.noApplied + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
            <Column :colspan="0" />
            <Column :colspan="0" />
          </Row>
        </ColumnGroup>
      </template>
    </DynamicTable>
    <!-- Dialog Apply Payment -->
    <Dialog
      v-model:visible="openDialogApplyPayment"
      modal
      class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
      :style="{ width: '60%' }"
      :pt="{
        root: {
          class: 'custom-dialog-history',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
        // mask: {
        //   style: 'backdrop-filter: blur(5px)',
        // },
      }"
      @hide="closeModalApplyPayment()"
    >
      <template #header>
        <div class="flex justify-content-between">
          <h5 class="m-0">
            Apply Payment Details
          </h5>
        </div>
      </template>
      <template #default>
        <div class="p-fluid pt-3">
          <!-- <pre>{{ paymentDetailsTypeDepositSelected }}</pre> -->
          <BlockUI v-if="objItemSelectedForRightClickApplyPayment?.hasDetailTypeDeposit" :blocked="paymentDetailsTypeDepositLoading" class="mb-3">
            <DataTable
              v-model:selection="paymentDetailsTypeDepositSelected"
              :value="paymentDetailsTypeDepositList"
              striped-rows
              show-gridlines
              data-key="id"
              selection-mode="multiple"
              style="background-color: #F5F5F5;"
            >
              <!-- @update:selection="onSelectionChange($event)" -->
              <!-- @selection-change="onSelectionChange" -->
              <Column selection-mode="multiple" header-style="width: 3rem" />
              <Column v-for="column of columnsPaymentDetailTypeDeposit" :key="column.field" :field="column.field" :header="column.header" :sortable="column?.sortable" />
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
          </BlockUI>

          <DynamicTable
            class="card p-0"
            :data="applyPaymentListOfInvoice"
            :columns="applyPaymentColumns"
            :options="applyPaymentOptions"
            :pagination="applyPaymentPagination"
            @on-change-pagination="applyPaymentOnChangePage = $event"
            @on-row-double-click="onRowDoubleClickInDataTableApplyPayment"
            @on-expand-row="onExpandRowApplyPayment($event)"
            @update:clicked-item="addAmmountsToApplyPayment($event)"
          >
            <!-- @update:clicked-item="invoiceSelectedListForApplyPayment = $event" -->
            <template #column-status="{ data: item }">
              <Badge
                :value="getStatusName(item?.status)"
                :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`"
              />
            </template>

            <template #expansion="{ data: item }">
              <!-- <pre>{{ item.bookingsList }}</pre> -->
              <div class="p-0 m-0">
                <!-- <DynamicTable
                  class="card p-0"
                  :parent-component-loading="item.loadingBookings"
                  :data="item.bookingsList"
                  :columns="columnsExpandTable"
                  :options="applyPaymentBookingOptions"
                  :pagination="applyPaymentBookingPagination"
                  @on-change-pagination="applyPaymentBookingOnChangePage = $event"
                /> -->

                <DataTable :value="item.bookings" striped-rows>
                  <Column v-for="column of columnsExpandTable" :key="column.field" :field="column.field" :header="column.header" :sortable="column?.sortable" />
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
        <div class="flex justify-content-between">
          <div class="flex align-items-center">
            <Chip class="bg-primary py-1 font-bold" label="Applied Payment Amount:">
              Available Payment Amount: ${{ paymentAmmountSelected }}
            </Chip>
            <Chip class="bg-primary py-1 mx-2 font-bold" label="Invoice Amount Selected: $0.00">
              Invoice Amount Selected: ${{ invoiceAmmountSelected }}
            </Chip>
            <Checkbox
              id="checkApplyPayment"
              v-model="checkApplyPayment"
              :binary="true"
              @update:model-value="($event) => {
                changeValueByCheckApplyPaymentBalance($event);
              }"
            />
            <label for="checkApplyPayment" class="ml-2 font-bold">
              Apply Payment Balance
            </label>
          </div>
          <div>
            <Button v-tooltip.top="'Apply Payment'" class="w-3rem mx-1" icon="pi pi-check" :disabled="disabledBtnApplyPayment" :loading="loadingSaveApplyPayment" @click="saveApplyPayment" />
            <Button v-tooltip.top="'Cancel'" class="w-3rem" icon="pi pi-times" severity="secondary" @click="closeModalApplyPayment()" />
          </div>
        </div>
      </template>
    </Dialog>
  </div>
  <ContextMenu ref="contextMenu" :model="allMenuListItems" />
</template>

<style lang="scss">
.text-transit {
  background-color: #ff002b;
  color: #fff;
}
.text-cancelled {
  background-color: #888888;
  color: #fff;
}
.text-confirmed {
  background-color: #0c2bff;
  color: #fff;
}
.text-applied {
  background-color: #00b816;
  color: #fff;
}

// :deep(.p-datatable-tbody) {
//   background-color: #fff !important;
// }
  // #accordion {
  //   .p-accordion-tab {
  //     .p-accordion-header-link {
  //       .p-accordion-header-text {
  //         font-size: 1rem;
  //       }
  //     }
  //   }
  // }
</style>
