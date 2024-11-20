<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import ContextMenu from 'primevue/contextmenu'
import dayjs from 'dayjs'
import { useToast } from 'primevue/usetoast'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import { formatNumber } from '~/pages/payment/utils/helperFilters'
import { formatCardNumber } from '~/components/vcc/vcc_utils'
import AttachmentTransactionDialog from '~/components/vcc/attachment/AttachmentTransactionDialog.vue'
// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const authStore = useAuthStore()
const { status, data } = useAuth()
const isAdmin = (data.value?.user as any)?.isAdmin === true

const listItems = ref<any[]>([])
const newManualTransactionDialogVisible = ref(false)
const editManualTransactionDialogVisible = ref(false)
const transactionHistoryDialogVisible = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)
const newRefundDialogVisible = ref(false)
const loadingSaveAll = ref(false)
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const accordionLoading = ref({
  merchant: false,
  hotel: false,
  ccType: false,
  status: false,
})
const contextMenuTransaction = ref()
const allDefaultItem = { id: 'All', name: 'All', code: 'All' }
const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  merchant: [allDefaultItem],
  ccType: [allDefaultItem],
  hotel: [allDefaultItem],
  status: [allDefaultItem],
  from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
  to: new Date(),
})
const selectedTransactionId = ref('')
const contextMenu = ref()

enum MenuType {
  refund, resendLink, resendPost, cancelled, document
}

const allMenuListItems = [
  {
    index: 0,
    label: 'Status History',
    icon: 'pi pi-history',
    command: () => { transactionHistoryDialogVisible.value = true },
    default: true,
    disabled: false,
  },
  {
    index: 1,
    type: MenuType.document,
    label: 'Document',
    icon: 'pi pi-paperclip',
    command: () => handleAttachmentDialogOpen(),
    disabled: false,
    isCollection: false,
    default: true
  },
  {
    index: 2,
    type: MenuType.refund,
    label: 'Refund',
    icon: 'pi pi-dollar',
    command: () => openNewRefundDialog(),
    disabled: false,
    isCollection: true // que pertenecen a un status en el collection status
  },
  {
    // Mostrar siempre
    index: 3,
    type: MenuType.cancelled,
    label: 'Cancelled',
    icon: 'pi pi-times-circle',
    command: () => cancelTransaction(),
    disabled: false,
    isCollection: true
  },
  {
    index: 4,
    type: MenuType.resendLink,
    label: 'ReSend Link',
    icon: 'pi pi-send',
    command: () => resendLink(),
    disabled: false,
    isCollection: false
  },
  {
    index: 5,
    type: MenuType.resendPost,
    label: 'ReSend Post',
    icon: 'pi pi-send',
    command: () => resendPost(),
    disabled: false,
    isCollection: false
  },
]

const menuListItems = ref<any[]>([])

const collectionStatusList = ref<any[]>([])
const hotelList = ref<any[]>([])
const statusList = ref<any[]>([])
const merchantList = ref<any[]>([])
const ccTypeList = ref<any[]>([])

const confStatusListApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'manage-transaction-status',
})
const confMerchantListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-merchant',
})

const confHotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confCCTypeListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-credit-card-type',
})

const legend = ref(
  [
    {
      name: 'Created',
      color: '#FF8D00',
      colClass: 'pr-3',
    },
    {
      name: 'Sent',
      color: '#006400',
      colClass: 'pr-3',
    },
    {
      name: 'Received',
      color: '#3403F9',
      colClass: 'pr-3',
    },
    {
      name: 'Declined',
      color: '#661E22',
      colClass: 'pr-3',
    },
    {
      name: 'Paid',
      color: '#2E892E',
      colClass: 'pr-3',
    },
    {
      name: 'Cancelled',
      color: '#888888',
      colClass: 'pr-3',
    },
    {
      name: 'Reconciled',
      color: '#05D2FF',
      colClass: 'pr-3',
    },
    {
      name: 'Refund',
      color: '#666666',
      colClass: '',
    },
  ]
)

const sClassMap: IStatusClass[] = [
  { status: 'Sent', class: 'vcc-text-sent' },
  { status: 'Created', class: 'vcc-text-created' },
  { status: 'Received', class: 'vcc-text-received' },
  { status: 'Declined', class: 'vcc-text-declined' },
  { status: 'Paid', class: 'vcc-text-paid' },
  { status: 'Cancelled', class: 'vcc-text-cancelled' },
  { status: 'Reconciled', class: 'vcc-text-reconciled' },
  { status: 'Refund', class: 'vcc-text-refund' },
]
////

const computedShowMenuItemManualTransaction = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['VCC-MANAGEMENT:MANUAL-TRANSACTION'])))
})

const computedShowMenuItemAdjustmentTransaction = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['VCC-MANAGEMENT:ADJUSTMENT-TRANSACTION'])))
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'icon', header: '', width: '25px', type: 'slot-icon', icon: 'pi pi-paperclip', sortable: false, showFilter: false },
  { field: 'id', header: 'Id', type: 'text' },
  { field: 'parent', header: 'Parent Id', type: 'text' },
  { field: 'enrolleCode', header: 'Enrollee Code', type: 'text' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' }, sortable: true },
  { field: 'cardNumber', header: 'Card Number', type: 'text' },
  { field: 'creditCardType', header: 'CC Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-credit-card-type' }, sortable: true },
  { field: 'methodType', header: 'Method Type', type: 'text' },
  { field: 'referenceNumber', header: 'Reference', type: 'text', width: '220px', maxWidth: '220px' },
  { field: 'amount', header: 'Amount', type: 'text' },
  { field: 'commission', header: 'Commission', type: 'text' },
  { field: 'netAmount', header: 'T.Amount', type: 'text' },
  { field: 'checkIn', header: 'Trans Date', type: 'date' },
  { field: 'statusName', header: 'Status', type: 'custom-badge', frozen: true, statusClassMap: sClassMap, objApi: { moduleApi: 'creditcard', uriApi: 'manage-transaction-status' }, sortable: true },
]

const subTotals: any = ref({ amount: 0, commission: 0, net: 0 })
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'id', name: 'Id' },
]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Transactions',
  moduleApi: 'creditcard',
  uriApi: 'transactions',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
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
async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  const count = { amount: 0, commission: 0, net: 0 }
  subTotals.value = { ...count }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { transactionSearchResponse, transactionTotalResume } = response
    const { data: dataList, page, size, totalElements, totalPages } = transactionSearchResponse

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.statusName = iterator.status.name
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel') && iterator.hotel) {
        iterator.hotel = { id: iterator.hotel.id, name: `${iterator.hotel.code} - ${iterator.hotel.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'creditCardType') && iterator.creditCardType) {
        iterator.creditCardType = { id: iterator.creditCardType.id, name: `${iterator.creditCardType.code} - ${iterator.creditCardType.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'parent')) {
        iterator.parent = (iterator.parent) ? String(iterator.parent?.id) : null
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'id')) {
        iterator.id = String(iterator.id)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'cardNumber') && iterator.cardNumber) {
        iterator.cardNumber = formatCardNumber(String(iterator.cardNumber))
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        count.amount += iterator.amount
        iterator.amount = formatNumber(iterator.amount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'commission')) {
        count.commission += iterator.commission
        iterator.commission = formatNumber(iterator.commission)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'netAmount')) {
        count.net += iterator.netAmount
        iterator.netAmount = iterator.netAmount ? formatNumber(iterator.netAmount) : '0.00'
      }
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false, invoiceDate: new Date(iterator?.invoiceDate) })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]
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
  const newPayload: IQueryRequest = {
    filter: [{
      key: 'adjustment',
      operator: 'EQUALS',
      value: false,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    newPayload.filter = [...newPayload.filter, {
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }]
  }
  else {
    newPayload.filter = [...newPayload.filter, ...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
    // Filtro para no mostrar transacciones de ajuste
    // newPayload.filter = [...newPayload.filter, {
    //   key: 'adjustment',
    //   operator: 'EQUALS',
    //   value: false,
    //   logicalOperation: 'AND',
    // }]
    // Date
    if (filterToSearch.value.from) {
      newPayload.filter = [...newPayload.filter, {
        key: 'checkIn',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.to) {
      newPayload.filter = [...newPayload.filter, {
        key: 'checkIn',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.merchant?.length > 0) {
      const filteredItems = filterToSearch.value.merchant.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'merchant.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    if (filterToSearch.value.hotel?.length > 0) {
      const filteredItems = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'hotel.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    if (filterToSearch.value.ccType?.length > 0) {
      const filteredItems = filterToSearch.value.ccType.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'creditCardType.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    if (filterToSearch.value.status?.length > 0) {
      const filteredItems = filterToSearch.value.status.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'status.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
  }
  payload.value = newPayload
  getList()
}

function clearFilterToSearch() {
  filterToSearch.value = {
    criteria: null,
    search: '',
    merchant: [allDefaultItem],
    ccType: [allDefaultItem],
    hotel: [allDefaultItem],
    status: [allDefaultItem],
    from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
    to: new Date(),
  }
  filterToSearch.value.criteria = ENUM_FILTER[0]
  searchAndFilter()
}

async function getCollectionStatusList() {
  if (collectionStatusList.value.length > 0) {
    return collectionStatusList.value
  }
  try {
    const payload = {
      filter: [
        {
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 50,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confStatusListApi.moduleApi, confStatusListApi.uriApi, payload)
    const { data: dataList } = response
    collectionStatusList.value = dataList
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
  return collectionStatusList.value
}

async function getHotelList(query: string = '') {
  try {
    accordionLoading.value.hotel = true
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

    const response = await GenericService.search(confHotelListApi.moduleApi, confHotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = [allDefaultItem]
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
  finally {
    accordionLoading.value.hotel = false
  }
}

async function getMerchantList(query: string = '') {
  try {
    accordionLoading.value.merchant = true
    const payload = {
      filter: [
        {
          key: 'description',
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

    const response = await GenericService.search(confMerchantListApi.moduleApi, confMerchantListApi.uriApi, payload)
    const { data: dataList } = response
    merchantList.value = [allDefaultItem]
    for (const iterator of dataList) {
      merchantList.value = [...merchantList.value, { id: iterator.id, name: iterator.description, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading merchant list:', error)
  }
  finally {
    accordionLoading.value.merchant = false
  }
}

async function getStatusList(query: string = '') {
  try {
    accordionLoading.value.status = true
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

    const response = await GenericService.search(confStatusListApi.moduleApi, confStatusListApi.uriApi, payload)
    const { data: dataList } = response
    statusList.value = [allDefaultItem]
    for (const iterator of dataList) {
      statusList.value = [...statusList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading status list:', error)
  }
  finally {
    accordionLoading.value.status = false
  }
}

async function getCCTypeList(query: string = '') {
  try {
    accordionLoading.value.ccType = true
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

    const response = await GenericService.search(confCCTypeListApi.moduleApi, confCCTypeListApi.uriApi, payload)
    const { data: dataList } = response
    ccTypeList.value = [allDefaultItem]
    for (const iterator of dataList) {
      ccTypeList.value = [...ccTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading credit card type list:', error)
  }
  finally {
    accordionLoading.value.ccType = false
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]

  const statusFilter: any = getStatusFilter(payloadFilter.status)
  if (statusFilter) {
    const index = payload.value.filter.findIndex((filter: IFilter) => filter.key === statusFilter.key)
    if (index !== -1) {
      payload.value.filter[index] = statusFilter
    }
    else {
      payload.value.filter.push(statusFilter)
    }
  }

  getList()
}

function getStatusFilter(element: any) {
  if (element && Array.isArray(element.constraints) && element.constraints.length > 0) {
    for (const iterator of element.constraints) {
      if (iterator.value) {
        const ketTemp = 'status.name'
        let operator: string = ''
        if ('matchMode' in iterator) {
          if (typeof iterator.matchMode === 'object') {
            operator = iterator.matchMode.id.toUpperCase()
          }
          else {
            operator = iterator.matchMode.toUpperCase()
          }
        }
        if (Array.isArray(iterator.value) && iterator.value.length > 0) {
          const objFilter: IFilter = {
            key: ketTemp,
            operator,
            value: iterator.value.length > 0 ? [...iterator.value.map((item: any) => item.name)] : [],
            logicalOperation: 'AND',
          }
          return objFilter
        }
      }
    }
  }
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function resendLink() {
  try {
    if (contextMenuTransaction.value.id) {
      options.value.loading = true
      await GenericService.create('creditcard', 'transactions/resend-payment-link', { id: contextMenuTransaction.value.id })
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    options.value.loading = false
  }
}

async function resendPost() {
  try {
    if (contextMenuTransaction.value.id) {
      options.value.loading = true
      const response: any = await GenericService.create('creditcard', 'transactions/resend-post', { id: contextMenuTransaction.value.id })
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
      const htmlBody = response.result
      const newTab = window.open('', '_blank')
      newTab?.document.write(await htmlBody)
      newTab?.document.close()
    }
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    options.value.loading = false
  }
}

async function cancelTransaction() {
  options.value.loading = true
  const payload: { [key: string]: any } = {}
  try {
    if (contextMenuTransaction.value.id) {
      const cancelledStatus = await findCancelledStatus()
      if (cancelledStatus && cancelledStatus.length > 0) {
        payload.transactionStatus = cancelledStatus[0].id
        delete payload.event
        const response: any = await GenericService.update('creditcard', 'transactions', contextMenuTransaction.value.id, payload)
        toast.add({ severity: 'info', summary: 'Confirmed', detail: `The transaction details id ${response.id} was updated`, life: 10000 })
        options.value.loading = false
        await getList()
      }
      else {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Cancelled Config not found in Manage Transaction Status', life: 10000 })
      }
    }
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    options.value.loading = false
  }
}

async function findCancelledStatus() {
  try {
    const payload = {
      filter: [
        {
          key: 'cancelledStatus',
          operator: 'EQUALS',
          value: true,
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

    const response = await GenericService.search(confStatusListApi.moduleApi, confStatusListApi.uriApi, payload)
    const { data: dataList } = response
    return dataList
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
}

async function openNewManualTransactionDialog() {
  newManualTransactionDialogVisible.value = true
}

async function openNewRefundDialog() {
  newRefundDialogVisible.value = true
}

function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

async function onCloseEditManualTransactionDialog(isCancel: boolean) {
  editManualTransactionDialogVisible.value = false
  if (!isCancel) {
    getList()
  }
}

async function onCloseNewManualTransactionDialog(isCancel: boolean) {
  newManualTransactionDialogVisible.value = false
  if (!isCancel) {
    getList()
  }
}

async function onCloseNewRefundDialog(isCancel: boolean = true) {
  newRefundDialogVisible.value = false
  if (!isCancel) {
    getList()
  }
}

function onDoubleClick(item: any) {
  console.log(item)
  if (item.manual || item.parent) {
    const id = Object.prototype.hasOwnProperty.call(item, 'id') ? item.id : item
    selectedTransactionId.value = id
    editManualTransactionDialogVisible.value = true
  }
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criteria && filterToSearch.value.search)
  return false
})

async function findCollectionStatusMenuOptions(status: string) {
  const collection: any = collectionStatusList.value.find(item => item?.name.toLowerCase() === status.toLowerCase())
  if (collection) {
    const navigateOptions = collection.navigate.map((n: any) => n.name.toLowerCase())
    // se agregan los elementos que pertenecen al navigate de dicho status
    const navigates = allMenuListItems.filter((element: any) => element.isCollection && navigateOptions.includes(element.label.toLowerCase()))
    menuListItems.value = [...menuListItems.value, ...navigates]
  }
}

function findNoCollectionStatusMenuOptions() {
  const data = contextMenuTransaction.value
  const noCollectionItems: any[] = allMenuListItems.filter((element: any) => !element.isCollection)
  for (let i = 0; i < noCollectionItems.length; i++) {
    const element = noCollectionItems[i]
    if ([MenuType.resendPost, MenuType.resendLink].includes(element.type) && (data.status.isSent || data.status.isDeclined)) {
      menuListItems.value.push(element)
    }
  }
}

async function onRowRightClick(event: any) {
  // console.log(event.data)
  contextMenu.value.hide()
  contextMenuTransaction.value = event.data
  menuListItems.value = [] // Elementos que se van a mostrar en el menu
  menuListItems.value = allMenuListItems.filter((e: any) => e.default) // Agregar elementos por defecto
  // Agrega a la lista las opciones que estan presentes en el navigate para el collection status del estado del elemento seleccionado
  await findCollectionStatusMenuOptions(contextMenuTransaction.value.statusName)
  if (menuListItems.value.length > 0) {
    const enableManualTransaction = (status.value === 'authenticated' && (isAdmin || authStore.can(['VCC-MANAGEMENT:MANUAL-TRANSACTION'])))
    // aqui se valida que hayan fondos disponibles para la devolucion
    setRefundAvailable(enableManualTransaction ? contextMenuTransaction.value.permitRefund : false)
  }
  // Agregar opciones que no son tipo coleccion:
  findNoCollectionStatusMenuOptions()
  if (menuListItems.value.length > 0) {
    menuListItems.value = menuListItems.value.sort((a, b) => a.index - b.index)
    contextMenu.value.show(event.originalEvent)
  }
}

function setRefundAvailable(isAvailable: boolean) {
  const menuItem = menuListItems.value.find((item: any) => item.label === 'Refund')
  if (menuItem) {
    menuItem.disabled = !isAvailable
  }
}

function openBankReconciliation() {
  window.open('/vcc-management/bank-reconciliation', '_blank')
}

function openHotelPayment() {
  window.open('/vcc-management/hotel-payment', '_blank')
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
  filterToSearch.value.criteria = ENUM_FILTER[0]
  // getList()
  searchAndFilter()
  getCollectionStatusList()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Virtual Credit Card Management
    </h3>
    <div class="my-2 flex justify-content-end px-0">
      <Button class="ml-2" icon="pi pi-plus" label="New" @click="openNewManualTransactionDialog()" />
      <Button class="ml-2" icon="pi pi-building-columns" label="Bank Reconciliation" @click="openBankReconciliation()" />
      <Button class="ml-2" icon="pi pi-dollar" label="Hotel Payment" @click="openHotelPayment()" />
      <Button class="ml-2" icon="pi pi-dollar" label="Payment" disabled />
      <Button class="ml-2" icon="pi pi-download" label="Export" disabled />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 order-0">
      <div class="card p-0 mb-0">
        <Accordion :active-index="0">
          <AccordionTab>
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
            <div class="grid">
              <div class="col-12 md:col-6 lg:col-3 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">Merchant:</label>
                    <DebouncedMultiSelectComponent
                      v-if="!loadingSaveAll"
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :model="filterToSearch.merchant"
                      :suggestions="merchantList"
                      :loading="accordionLoading.merchant"
                      @change="($event) => {
                        if (!filterToSearch.merchant.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.merchant = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.merchant = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                      @load="($event) => getMerchantList($event)"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedMultiSelectComponent>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <label class="filter-label font-bold" for="">Hotel:</label>
                    <DebouncedMultiSelectComponent
                      v-if="!loadingSaveAll"
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :model="filterToSearch.hotel"
                      :suggestions="hotelList"
                      :loading="accordionLoading.hotel"
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
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedMultiSelectComponent>
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-3 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">CC Type:</label>
                    <DebouncedMultiSelectComponent
                      v-if="!loadingSaveAll"
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :model="filterToSearch.ccType"
                      :suggestions="ccTypeList"
                      :loading="accordionLoading.ccType"
                      @change="($event) => {
                        if (!filterToSearch.ccType.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.ccType = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.ccType = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                      @load="($event) => getCCTypeList($event)"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedMultiSelectComponent>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <label class="filter-label font-bold" for="">Status:</label>
                    <DebouncedMultiSelectComponent
                      v-if="!loadingSaveAll"
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :model="filterToSearch.status"
                      :suggestions="statusList"
                      :loading="accordionLoading.status"
                      @change="($event) => {
                        if (!filterToSearch.status.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.status = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.status = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                      @load="($event) => getStatusList($event)"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedMultiSelectComponent>
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">From:</label>
                    <div class="w-full" style=" z-index:5 ">
                      <Calendar
                        v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                        show-icon icon-display="input" class="w-full" :max-date="new Date()"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
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
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex w-full">
                  <div class="flex flex-row w-full">
                    <div class="flex flex-column gap-2 w-full">
                      <div class="flex align-items-center gap-2" style=" z-index:5 ">
                        <label class="filter-label font-bold" for="">Criteria:</label>
                        <div class="w-full">
                          <Dropdown
                            v-model="filterToSearch.criteria" :options="[...ENUM_FILTER]" option-label="name"
                            placeholder="Criteria" return-object="false" class="align-items-center w-full" show-clear
                          />
                        </div>
                      </div>
                      <div class="flex align-items-center gap-2">
                        <label class="filter-label font-bold" for="">Search:</label>
                        <div class="w-full">
                          <IconField icon-position="left">
                            <InputText v-model="filterToSearch.search" type="text" style="width: 100% !important;" />
                            <InputIcon class="pi pi-search" />
                          </IconField>
                        </div>
                      </div>
                    </div>
                    <div class="flex align-items-center">
                      <Button
                        v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch"
                        :loading="loadingSearch" @click="searchAndFilter"
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
      <!--      <div class="legend-container mb-1 grid grid-nogutter">
        <div class="text-white font-bold col-12 lg:col-3">
          Transactions
        </div>
      </div> -->
      <DynamicTable
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
        @on-row-right-click="onRowRightClick"
        @on-row-double-click="onDoubleClick($event)"
      >
        <template #column-icon="{ data: objData, column }">
          <div class="flex align-items-center justify-content-center p-0 m-0">
            <!-- <pre>{{ objData }}</pre> -->
            <Button
              v-if="objData.hasAttachment"
              :icon="column.icon"
              class="p-button-rounded p-button-text w-2rem h-2rem"
              aria-label="Submit"
            />
          </div>
          <!-- style="color: #616161;" -->
          <!-- :style="{ 'background-color': '#00b816' }" -->
        </template>
        <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center">
            <Row>
              <Column footer="Totals:" :colspan="9" footer-style="text-align:right" />
              <Column :footer="formatNumber(subTotals.amount)" />
              <Column :footer="formatNumber(subTotals.commission)" />
              <Column :footer="formatNumber(subTotals.net)" />
              <Column :colspan="2" />
            </Row>
          </ColumnGroup>
        </template>
      </DynamicTable>
    </div>
    <ContextMenu ref="contextMenu" :model="menuListItems" />
    <VCCNewManualTransaction :open-dialog="newManualTransactionDialogVisible" @on-close-dialog="onCloseNewManualTransactionDialog($event)" />
    <VCCNewRefund :open-dialog="newRefundDialogVisible" :parent-transaction="contextMenuTransaction" @on-close-dialog="onCloseNewRefundDialog($event)" />
    <VCCEditManualTransaction :open-dialog="editManualTransactionDialogVisible" :transaction-id="selectedTransactionId" @on-close-dialog="onCloseEditManualTransactionDialog($event)" />
    <div v-if="transactionHistoryDialogVisible">
      <TransactionStatusHistoryDialog :close-dialog="() => { transactionHistoryDialogVisible = false }" :open-dialog="transactionHistoryDialogVisible" :selected-transaction="contextMenuTransaction" :s-class-map="sClassMap" />
    </div>
    <div v-if="attachmentDialogOpen">
      <AttachmentTransactionDialog
        :close-dialog="(total: number) => {
          attachmentDialogOpen = false
          // item.hasAttachments = total > 0
        }" header="Manage Transaction Attachment" :open-dialog="attachmentDialogOpen" :selected-transaction="contextMenuTransaction"
      />
    </div>
  </div>
</template>

<style scoped>
.filter-label {
  min-width: 55px;
  text-align: end;
}
</style>

<style lang="scss">
#vcc-menu_list {
  .p-menuitem .p-menuitem-content {
    padding: 6px 2px;
  }
  .p-menuitem .p-menuitem-content:hover {
    cursor: pointer;
  }
}
</style>
