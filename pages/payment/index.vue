<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import dayjs from 'dayjs'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean, statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'
import { itemMenuList } from '~/components/payment/indexBtns'

// VARIABLES -----------------------------------------------------------------------------------------
const allDefaultItem = { id: 'All', name: 'All', status: 'ACTIVE' }
const listItems = ref<any[]>([])
const clientItemsList = ref<any[]>([])
const agencyItemsList = ref<any[]>([])
const hotelItemsList = ref<any[]>([])
const statusItemsList = ref<any[]>([])

const startOfMonth = ref<any>(null)
const endOfMonth = ref<any>(null)

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

// -------------------------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'paymentId', name: 'Payment Id', disabled: false },
  { id: 'transfNo', name: 'Transf. No', disabled: true },
  { id: 'totalAmount', name: 'T. Amount', disabled: true },
  { id: 'remark', name: 'Remark' },
  { id: 'detailId', name: 'Detail Id', disabled: true },
  { id: 'detailAmount', name: 'Detail Amount', disabled: true },
  { id: 'detailRemark', name: 'Detail Remark', disabled: true },
  { id: 'invoiceNo', name: 'Invoice No', disabled: true },
  { id: 'bookingId', name: 'Booking Id', disabled: true },
  { id: 'guest', name: 'Guest', disabled: true },
  { id: 'firstName', name: 'Guest First Name', disabled: true },
  { id: 'lastName', name: 'Guest Last Name', disabled: true },
  { id: 'reservation', name: 'Reservation', disabled: true },
  { id: 'cupon', name: 'Cupon', disabled: true },
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
  { field: 'noApplied', header: 'Not Applied', tooltip: 'Not Applied', width: '60px', type: 'text' },
  { field: 'remark', header: 'Remark', width: '100px', type: 'text' },
  { field: 'paymentStatus', header: 'Status', frozen: true, type: 'slot-select', statusClassMap: sClassMap, objApi: { moduleApi: 'settings', uriApi: 'manage-payment-status' }, sortable: true },
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

// FUNCTIONS ---------------------------------------------------------------------------------------------

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
        count.paymentAmount += iterator.paymentAmount
        iterator.paymentAmount = String(iterator.paymentAmount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'depositBalance')) {
        count.depositBalance += iterator.depositBalance
        iterator.depositBalance = String(iterator.depositBalance)
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
          id: iterator.bankAccount.id,
          name: iterator.bankAccount.accountNumber
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
      operator: 'EQUALS',
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

async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  let clientTemp: any[] = []
  clientItemsList.value = [allDefaultItem]
  clientTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [], queryObj, mapFunction)
  clientItemsList.value = [...clientItemsList.value, ...clientTemp]
}
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let agencyTemp: any[] = []
  agencyItemsList.value = [allDefaultItem]
  agencyTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
  agencyItemsList.value = [...agencyItemsList.value, ...agencyTemp]
}
async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let hotelTemp: any[] = []
  hotelItemsList.value = [allDefaultItem]
  hotelTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
  hotelItemsList.value = [...hotelItemsList.value, ...hotelTemp]
}

async function getStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let statusTemp: any[] = []
  statusItemsList.value = [allDefaultItem]
  statusTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
  statusItemsList.value = [...statusItemsList.value, ...statusTemp]
}

function getMonthStartAndEnd(date) {
  const startOfMonth = dayjs(date).startOf('month').format('YYYY-MM-DD')
  const endOfMonth = dayjs(date).endOf('month').format('YYYY-MM-DD')
  return { startOfMonth, endOfMonth }
}

const disabledDates = computed(() => {
  return filterAllDateRange.value
})

// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
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

// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(async () => {
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

function toggle(event: Event, index: number) {
  const menu: any = itemMenuList.value[index].menuRef
  if (menu) {
    menu.toggle(event)
  }
  else {
    console.error('Menu reference is not defined or initialized.')
  }
}
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Payment Management
    </h3>
    <div class="flex justify-content-end px-0">
      <span v-for="(objBtnAndMenu, index) in itemMenuList" :key="index">
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
              <span class="mr-2 flex align-items-center justify-content-center p-0">
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
                          await getClientList(objApis.client.moduleApi, objApis.client.uriApi, objQueryToSearch)
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
                        console.log(filterAllDateRange);

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
            <Column :footer="Math.round((subTotals.paymentAmount + Number.EPSILON) * 100) / 100" footer-style="font-weight: bold;" />
            <Column :footer="Math.round((subTotals.depositBalance + Number.EPSILON) * 100) / 100" footer-style="font-weight: bold;" />
            <Column :footer="Math.round((subTotals.applied + Number.EPSILON) * 100) / 100" footer-style="font-weight: bold;" />
            <Column :footer="Math.round((subTotals.noApplied + Number.EPSILON) * 100) / 100" footer-style="font-weight: bold;" />
            <Column :colspan="0" />
            <Column :colspan="0" />
          </Row>
        </ColumnGroup>
      </template>
    </DynamicTable>
  </div>
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

.p-datatable-tfoot {
  background-color: var(--primary-color);
  tr td {
    color: #fff;
  }
}

:deep(.p-datatable-tbody) {
  background-color: #fff !important;
}
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
