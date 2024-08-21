<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import dayjs from 'dayjs'
import Checkbox from 'primevue/checkbox'
import ContextMenu from 'primevue/contextmenu'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'
import getUrlByImage from '~/composables/files'
import AttachmentDialog from '~/components/invoice/attachment/AttachmentDialog.vue'
import AttachmentHistoryDialog from '~/components/invoice/attachment/AttachmentHistoryDialog.vue'

// VARIABLES -----------------------------------------------------------------------------------------
const authStore = useAuthStore()
const { status, data } = useAuth()
const isAdmin = (data.value?.user as any)?.isAdmin === true
const menu = ref()
const menu_import = ref()
const menu_reconcile=ref()
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const formReload = ref(0)
const invoiceTypeList = ref<any[]>()

const totalInvoiceAmount = ref(0)
const totalDueAmount = ref(0)

const bookingDialogOpen = ref<boolean>(false)

const loadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const hotelError = ref(false)
const attachmentHistoryDialogOpen = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)
const attachmentInvoice = <any>ref(null)

const active = ref(0)

const loadingDelete = ref(false)
const filterToSearch = ref<IData>({
  criteria: ENUM_INVOICE_CRITERIA[3],
  search: '',
  client: [],
  agency: [],
  hotel: [{ id: 'All', name: 'All', code: 'All' }],
  status: [{ id: 'PROCECSED', name: 'Processed' }, { id: 'RECONCILED', name: 'Reconciled' }, { id: 'SENT', name: 'Sent' },],
  invoiceType: [{ id: 'All', name: 'All', code: 'All' }],
  from: dayjs(new Date()).startOf('month').toDate(),
  to: dayjs(new Date()).endOf('month').toDate(),

  includeInvoicePaid: true
})

const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
})

const disableClient = ref<boolean>(false)
const disableDates = ref<boolean>(true)

const expandedInvoice = ref('')

const hotelList = ref<any[]>([])
const statusList = ref<any[]>([])
const clientList = ref<any[]>([])
const agencyList = ref<any[]>([])

const confclientListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-client',
})

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confinvoiceTypeListtApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-type',
})

const computedShowMenuItemNewCedit = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:CREDIT-CREATE'])))
})
const computedShowMenuItemShowHistory = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:ATTACHMENT-SHOW-HISTORY'])))
})

const computedShowMenuItemAttachment = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:ATTACHMENT-CREATE']) || authStore.can(['INVOICE-MANAGEMENT:ATTACHMENT-EDIT'])))
})

const computedShowMenuItemPrint = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:PRINT'])))
})


const invoiceContextMenu = ref()
const invoiceAllContextMenuItems = ref([
  {
    label: 'Change Agency',
    icon: 'pi pi-pencil',
    command: () => { },
    default: false
  },
  {
    label: 'New Credit',
    icon: 'pi pi-credit-card',
    command: () => {
      navigateTo(`invoice/create?type=${InvoiceType.CREDIT}&selected=${attachmentInvoice.value.id}`, { open: { target: '_blank' } })
    },
    default: true,
    disabled: computedShowMenuItemNewCedit
  },
  {
    label: 'Status History',
    icon: 'pi pi-file',
    command: handleAttachmentHistoryDialogOpen,
    default: true,
    disabled: computedShowMenuItemShowHistory
  },
  {
    label: 'Document',
    icon: 'pi pi-paperclip',
    command: () => {
      attachmentDialogOpen.value = true
    },
    default: true,
    disabled: computedShowMenuItemAttachment
  },
  {
    label: 'Print',
    icon: 'pi pi-print',
    command: () => {
      if (attachmentInvoice.value?.hasAttachments) {
        exportAttachments()
      }
    },
    default: true,
    disabled: computedShowMenuItemPrint
  },
])

const invoiceContextMenuItems = ref<any[]>([])

const exportDialogOpen = ref(false)
const exportPdfDialogOpen = ref(false)
const exportAttachmentsDialogOpen = ref(false)
const exportBlob = ref<any>(null)

////
const computedexpandedInvoice = computed(() => {
  return expandedInvoice.value === ''
})


const computedShowMenuItemInvoice = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:CREATE'])))
})

const computedShowMenuItemReconcile = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:CREATE'])))
})

const computedShowMenuItemCredit = computed(() => {
  if (!authStore.can(['INVOICE-MANAGEMENT:CREATE'])) {
    return true
  } else {
    return expandedInvoice.value === ''
  }
})

const computedShowMenuItemOldCredit = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:OLD-CREDIT-CREATE'])))
})

const createItems = ref([
  {
    label: 'Invoice',
    command: () => navigateTo(`invoice/create?type=${InvoiceType.INVOICE}`, { open: { target: '_blank' } }),
    disabled: computedShowMenuItemInvoice
  },
  // {
  //   label: 'Credit',
  //   command: () => navigateTo(`invoice/create?type=${InvoiceType.CREDIT}&selected=${expandedInvoice.value}`),
  //   disabled: computedShowMenuItemCredit
  // },
  {
    label: 'Old Credit',
    command: () => navigateTo(`invoice/create?type=${InvoiceType.OLD_CREDIT}`, { open: { target: '_blank' } }),
    disabled: computedShowMenuItemOldCredit
  },
])

const createReconcile = ref([
  {
    label: 'Reconcile Automatic',
    command: () => navigateTo('invoice/reconcile-automatic', { open: { target: '_blank' } }),
    disabled: computedShowMenuItemReconcile
  },
  // {
  //   label: 'Credit',
  //   command: () => navigateTo(`invoice/create?type=${InvoiceType.CREDIT}&selected=${expandedInvoice.value}`),
  //   disabled: computedShowMenuItemCredit
  // },
  {
    label: 'Reconcile from Files',
    command: () => navigateTo('reconcile-automatic', { open: { target: '_blank' } }),
    disabled: computedShowMenuItemOldCredit
  },
])


const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    disabled: false,
    dataType: 'code',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(5, 'Maximum 5 characters').regex(/^[a-z]+$/i, 'Only text characters allowed')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1'
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    class: 'field col-12  mb-3 mt-3',
  },
]

const item = ref<GenericObject>({
  image: '',
  name: '',
  code: '',
  description: '',
  status: true
})

const itemTemp = ref<GenericObject>({
  image: '',
  name: '',
  code: '',
  description: '',
  status: true
})

// IMPORTS ---------------------------------------------------------------------------------------------
const computedShowMenuItemImportBookingFromFile = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:IMPORT-BOOKING-FROM-FILE'])))
})

const computedShowMenuItemImportBookingFromVirtual = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:IMPORT-BOOKING-FROM-VIRTUAL'])))
})

const itemsMenuImport = ref([
  {
    label: 'Booking From File',
    command: () => navigateTo('invoice/import', { open: { target: '_blank' } }),
    disabled: computedShowMenuItemImportBookingFromFile
  },
  {
    label: 'Booking From File (Virtual Hotels)',
    command: () => navigateTo('invoice/import-virtual'),
    disabled: computedShowMenuItemImportBookingFromVirtual
  }
])
// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Id', type: 'text' },
  // { field: 'invoiceType', header: 'Type', type: 'select' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: confhotelListApi },
  { field: 'agencyCd', header: 'Agency CD', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confagencyListApi },
  { field: 'invoiceNumber', header: 'Inv. No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen. Date', type: 'date' },
  { field: 'isManual', header: 'Manual', type: 'bool', tooltip: 'Manual' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'text' },
  // { field: 'autoRec', header: 'Auto Rec', type: 'bool' },
  { field: 'status', header: 'Status', type: 'local-select', localItems: ENUM_INVOICE_STATUS },
]
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'invoiceNumber', name: 'Invoice Number' },
  { id: 'invoiceDate', name: 'Invoice Date' },
  { id: 'invoiceAmount', name: 'Invoice Amount' },
]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  showEdit: false,
  showAcctions: false,
  messageToDelete: 'Do you want to save the change?',
  showTitleBar: false
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'invoiceId',
  sortType: ENUM_SHORT_TYPE.ASC
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
  formReload.value++
}

function handleAttachmentHistoryDialogOpen() {
  attachmentHistoryDialogOpen.value = true
}


async function exportToPdf() {
  exportPdfDialogOpen.value = true
}

async function exportAttachments() {
  exportAttachmentsDialogOpen.value = true
}

async function exportList() {
  try {
    //   const response = await GenericService.export(options.value.moduleApi, options.value.uriApi, payload.value)
    // exportBlob.value = response

    exportDialogOpen.value = true




    // const url = window.URL.createObjectURL(response);
    //       const link = document.createElement('a');
    //       link.href = url;
    //       link.setAttribute('download', 'invoice-list.xlsx'); 
    //       document.body.appendChild(link);
    //       link.click();
    //       document.body.removeChild(link);
    //       window.URL.revokeObjectURL(url);

  } catch (error) {
    console.log(error);
  }

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

    totalInvoiceAmount.value = 0
    totalDueAmount.value = 0

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        let invoiceNumber
        if (iterator?.invoiceNumber?.split('-')?.length === 3) {
          invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`
        } else {
          invoiceNumber = iterator?.invoiceNumber
        }
        newListItems.push({
          ...iterator, loadingEdit: false, loadingDelete: false, invoiceDate: new Date(iterator?.invoiceDate), agencyCd: iterator?.agency?.code, dueAmount: iterator?.dueAmount || '0', invoiceNumber: invoiceNumber.replace("OLD", "CRE"),


          hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ""}-${iterator?.hotel?.name || ""}` }
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }

      totalInvoiceAmount.value += iterator.invoiceAmount
      totalDueAmount.value += iterator.dueAmount ? Number(iterator?.dueAmount) : 0
    }

    listItems.value = [...listItems.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

const openEditDialog = async (item: any, type: string) => await navigateTo({ path: `invoice/edit/${item}`, query: { type: type } }, { open: { target: '_blank' } })

async function resetListItems() {
  payload.value.page = 0
  getList()
}

function searchAndFilter() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'invoiceId',
    sortType: ENUM_SHORT_TYPE.ASC
  }




  if (!filterToSearch.value.search) {
    if (filterToSearch.value.includeInvoicePaid) {
      payload.value.filter = [...payload.value.filter, {
        key: 'dueAmount',
        operator: 'EQUALS',
        value: 0,
        logicalOperation: 'AND'
      }]
    } else {
      payload.value.filter = [...payload.value.filter, {
        key: 'dueAmount',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: 1,
        logicalOperation: 'AND'
      }]
    }



    if (filterToSearch.value.client?.length > 0 && !filterToSearch.value.client.find(item => item.id === 'All')) {
      const filteredItems = filterToSearch.value.client.filter((item: any) => item?.id !== 'All')
      const itemIds = filteredItems?.map((item: any) => item?.id)

      payload.value.filter = [...payload.value.filter, {
        key: 'agency.client.id',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }
    if (filterToSearch.value.agency?.length > 0 && !filterToSearch.value.agency.find(item => item.id === 'All')) {
      const filteredItems = filterToSearch.value.agency.filter((item: any) => item?.id !== 'All')
      const itemIds = filteredItems?.map((item: any) => item?.id)
      payload.value.filter = [...payload.value.filter, {
        key: 'agency.id',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }



    if (filterToSearch.value.status?.length > 0) {
      const filteredItems = filterToSearch.value.status.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'invoiceStatus',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND'
        }]
      }
    }
    if (filterToSearch.value.invoiceType?.length > 0) {
      const filteredItems = filterToSearch.value.invoiceType.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'invoiceType',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND'
        }]
      }
    }
    if (filterToSearch.value.from && !disableDates.value) {
      payload.value.filter = [...payload.value.filter, {
        key: 'invoiceDate',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).startOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND'
      }]
    }
    if (filterToSearch.value.to && !disableDates.value) {
      payload.value.filter = [...payload.value.filter, {
        key: 'invoiceDate',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).endOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND'
      }]
    }
  }

  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }


  if (filterToSearch.value.hotel?.length > 0) {
    const filteredItems = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All')
    if (filteredItems.length > 0) {
      const itemIds = filteredItems?.map((item: any) => item?.id)
      payload.value.filter = [...payload.value.filter, {
        key: 'hotel.id',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }
  }
  else {
    if (filterToSearch.value.criteria?.id !== ENUM_INVOICE_CRITERIA[3]?.id && filterToSearch.value.criteria?.id !== ENUM_INVOICE_CRITERIA[4]?.id) {
      return hotelError.value = true
    }
  }



  getList()
}

function clearFilterToSearch() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'invoiceId',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  filterToSearch.value = {
    criteria: ENUM_INVOICE_CRITERIA[3],
    search: '',
    client: [],
    agency: [],
    hotel: [{ id: 'All', name: 'All', code: 'All' }],
    status: [{ id: 'PROCECSED', name: 'Processed' }, { id: 'RECONCILED', name: 'Reconciled' }, { id: 'SENT', name: 'Sent' },],
    invoiceType: [{ id: 'All', name: 'All', code: 'All' }],
    from: dayjs(new Date()).startOf('month').toDate(),
    to: dayjs(new Date()).endOf('month').toDate(),

    includeInvoicePaid: true
  }
  getList()
}
async function getItemById(data: { id: string, type: string, status: any }) {

  openEditDialog(data?.id, data?.type)

}

function handleDialogOpen() {
  bookingDialogOpen.value = true
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.status = statusToString(payload.status)
    payload.image = typeof payload.image === 'object' ? await getUrlByImage(payload.image) : payload.image
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.image = typeof payload.image === 'object' ? await getUrlByImage(payload.image) : payload.image
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
    getList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
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
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      await createItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
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

function requireConfirmationToDelete(event: any) {
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
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function getHotelList(query = '') {
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
      pageSize: 200,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    hotelList.value = [{ id: 'All', name: 'All', code: 'All' }]
    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getClientList(query = '') {
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
      pageSize: 200,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    clientList.value = [{ id: 'All', name: 'All', code: 'All' }]
    const response = await GenericService.search(confclientListApi.moduleApi, confclientListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      clientList.value = [...clientList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getStatusList(query = '') {
  try {
    statusList.value = [{ id: 'All', name: 'All', code: 'All' }, ...ENUM_INVOICE_STATUS]
    
    if (query) {
      statusList.value = statusList.value.filter(inv => String(inv?.name).toLowerCase().includes(query.toLowerCase()))
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getInvoiceTypeList(query = '') {
  try {
    invoiceTypeList.value = [{ id: 'All', name: 'All', code: 'All' }, ...ENUM_INVOICE_TYPE]

    if (query) {
      invoiceTypeList.value = invoiceTypeList.value.filter(inv => String(inv?.name).toLowerCase().includes(query.toLowerCase()))
    }


  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getAgencyList(query = '') {
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
      ] as any,
      query: '',
      pageSize: 200,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    agencyList.value = [{ id: 'All', name: 'All', code: 'All' }]

    if (filterToSearch.value.client?.length === 0) {
      return agencyList.value = []
    }
    const clientIds: any[] = []

    filterToSearch.value?.client?.forEach((client: any) => clientIds.push(client?.id))

    payload.filter.push({
      key: 'client.id',
      operator: 'IN',
      value: clientIds,
      logicalOperation: 'AND'
    })

    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  console.log(payloadFilter);

  // if(payloadFilter?.agencyCd){
  //   payloadFilter['agency.code'] = payloadFilter.agencyCd
  //   delete payloadFilter.agencyCd
  // }

  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)

  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {

      if (parseFilter[i]?.key === 'agencyCd') {
        parseFilter[i].key = 'agency.code'
      }

      if (parseFilter[i]?.key === 'status') {
        parseFilter[i].key = 'invoiceStatus'
      }

    }
  }

  payload.value.filter = [...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'hotel') {
      event.sortField = 'hotel.name'
    }
    if (event.sortField === 'agencyCd') {
      event.sortField = 'agency.code'
    }
    if (event.sortField === 'agency') {
      event.sortField = 'agency.name'
    }
    // if (event.sortField === 'invoiceNumber') {
    //   event.sortField = 'invoiceNumber'
    // }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getList()
  }
}

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

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})

function toggle(event) {
  menu.value.toggle(event)
}

function toggleReconcile(event) {
  menu_reconcile.value.toggle(event)
}
function toggleImport(event) {
  menu_import.value.toggle(event)
}

function setMenuOptions(statusId: string) {
  invoiceContextMenuItems.value = [...invoiceAllContextMenuItems.value.filter((item: any) => item?.default)]
}

function onRowRightClick(event: any) {

  setMenuOptions(event.data.status)
  console.log(event?.data);

  if (event.data?.invoiceType !== InvoiceType.INVOICE) {
    console.log('event');
    invoiceContextMenuItems.value = [...invoiceContextMenuItems.value.filter((item: any) => item?.label !== 'New Credit')]
  }

  // Mostrar solo si es para estos estados
  attachmentInvoice.value = event.data
  invoiceContextMenu.value.show(event.originalEvent)
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

watch(disableClient, () => {
  if (disableClient.value) {
    filterToSearch.value.client = []
    filterToSearch.value.agency = []
  }
})
watch(filterToSearch, () => {
  if (filterToSearch.value.criteria?.id === ENUM_INVOICE_CRITERIA[3]?.id || filterToSearch.value.criteria?.id === ENUM_INVOICE_CRITERIA[1]?.id) {
    hotelError.value = false
  }
}, { deep: true })

// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  getList()
})

const legend = ref(
  [
    {
      name: 'Processed',
      color: '#FF8D00',
      colClass: 'pr-4',
    },
    {
      name: 'Cancelled',
      color: '#686868',
      colClass: 'pr-4',
    },
    {
      name: 'Waiting',
      color: '#F90303',
      colClass: 'pr-4',
    },
    {
      name: 'Reconciled',
      color: '#005FB7',
      colClass: 'pr-4',
    },
    {
      name: 'Sent',
      color: '#006400',
      colClass: '',
    },
  ]
)

// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class=" col-12 align-items-center grid w-full">
    <div class="flex align-items-center justify-content-between w-full">
      <h3 class="mb-0 w-6">
        Invoice Management
      </h3>
      <div class="flex flex-row w-full place-content-center justify-center justify-content-end">
        <Button v-if="status === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:SHOW-BTN-NEW']))"
          v-tooltip.left="'New'" label="New" icon="pi pi-plus" severity="primary" aria-haspopup="true"
          aria-controls="overlay_menu" @click="toggle" />
        <Menu id="overlay_menu" ref="menu" :model="createItems" :popup="true" />

        <PopupNavigationMenu v-if="false" :items="createItems" icon="pi pi-plus" label="New">
          <template #item="props">
            <button :disabled="props.props.label === 'Credit' && expandedInvoice === ''"
              style="border: none; width: 100%;">
              {{ props.props.label }}
            </button>
          </template>
        </PopupNavigationMenu>

        <Button v-if="status === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:SHOW-BTN-IMPORT']))"
          v-tooltip.left="'Import'" class="ml-2" label="Import" icon="pi pi-file-import" severity="primary"
          aria-haspopup="true" aria-controls="overlay_menu_import" @click="toggleImport">

        </Button>
        <Menu id="overlay_menu_import" ref="menu_import" class="ml-2" :model="itemsMenuImport" :popup="true" />
        <PopupNavigationMenu v-if="false" :items="itemsMenuImport" icon="pi pi-plus" label="Import">
          <template #item="props">
            <button style="border: none; width: 100%;">
              {{ props.props.label }}
            </button>
          </template>
        </PopupNavigationMenu>
        <!-- <SplitButton class="ml-2" icon="pi pi-download" label="Import" :model="itemsMenuImport" /> -->
        <!---<Button class="ml-2" icon="pi pi-copy" label="Rec Inv" />.-->
        <Button v-if="status === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:SHOW-BTN-RECONCILE']))" v-tooltip.left="'Reconcile Invoice'" class="ml-2" label="Rec Inv" icon="pi pi-copy" severity="primary"
          aria-haspopup="true" aria-controls="overlay_menu_reconcile" @click="toggleReconcile"/>

        <Menu id="overlay_menu_reconcile" ref="menu_reconcile" :model="createReconcile" :popup="true" />

<PopupNavigationMenu v-if="false" :items="createReconcile" icon="pi pi-copy" label="Rec Inv">
  <template #item="props">
    <button 
      style="border: none; width: 100%;">
      {{ props.props.label }}
    </button>
  </template>
</PopupNavigationMenu>


        <Button class="ml-2" icon="pi pi-envelope" label="Send" />
        <!-- <Button
          class="ml-2" icon="pi pi-paperclip" :disabled="!attachmentInvoice" label="Document" @click="() => {
            attachmentDialogOpen = true
          }"
        /> -->
        <!-- <Button class="ml-2" icon="pi pi-file-plus" label="Process" /> -->
        <!--  <Button class="ml-2" icon="pi pi-cog" label="Adjustment" disabled /> -->
        <Button class="ml-2" icon="pi pi-print" label="Print" :disabled="listItems.length === 0" @click="exportToPdf" />
        <Button class="ml-2" icon="pi pi-download" label="Export" :disabled="listItems.length === 0"
          @click="() => exportList()" />
        <!-- <Button class="ml-2" icon="pi pi-times" label="Exit" @click="() => { navigateTo('/') }" /> -->
      </div>
    </div>
  </div>
  <div class="grid w-full">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class=" p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div
                class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
                <div>
                  Search Fields
                </div>
                <div>
                  <PaymentLegend :legend="legend" />
                </div>
              </div>
            </template>
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="" class="font-bold">Client</label>
                  <label for="" class="font-bold">Agency</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete" multiple field="name"
                        item-value="id" :model="filterToSearch.client" :suggestions="clientList" placeholder=""
                        :disabled="disableClient" style="max-width: 400px;" @load="($event) => getClientList($event)"
                        @change="($event) => {

                          if (!filterToSearch.client.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                            filterToSearch.client = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {

                            filterToSearch.client = $event.filter((element: any) => element?.id !== 'All')
                          }

                          filterToSearch.agency = filterToSearch.client.length > 0 ? [{ id: 'All', name: 'All', code: 'All' }] : []
                        }">
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>
                            {{ value?.code }}
                          </div>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete" placeholder="" multiple
                        field="name" item-value="id" :model="filterToSearch.agency" :suggestions="agencyList"
                        :disabled="disableClient" style="max-width: 400px;" @change="($event) => {

                          if (!filterToSearch.agency.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                            filterToSearch.agency = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {

                            filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                          }

                        }" @load="($event) => getAgencyList($event)">
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>
                            {{ value?.code }}-{{ value?.name }}
                          </div>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>

              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="" class="font-bold">Hotel</label>
                  <label for="" class="font-bold">Status</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full" style=" z-index:5">
                      <div class="flex gap-2 w-full">
                        <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete" field="name" multiple
                          item-value="id" :model="filterToSearch.hotel" :suggestions="hotelList" placeholder=""
                          class="w-full" @load="($event) => getHotelList($event)" @change="($event) => {

                            if (!filterToSearch.hotel.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                              filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                            }
                            else {

                              filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                            }
                            hotelError = false

                          }">
                          <template #option="props">
                            <span>{{ props.item.code }} - {{ props.item.name }}</span>
                          </template>
                          <template #chip="{ value }">
                            <div>
                              {{ value?.code }}-{{ value?.name }}
                            </div>
                          </template>
                        </DebouncedAutoCompleteComponent>
                        <div v-if="hotelError" class="flex align-items-center text-sm">
                          <span style="color: red; margin-right: 3px; margin-left: 3px;">You must select the "Hotel"
                            field
                            as required</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete" field="name" multiple
                        item-value="id" :model="filterToSearch.status" :suggestions="statusList" placeholder=""
                        @load="($event) => getStatusList($event)" @change="($event) => {
                          if (!filterToSearch.status.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                            filterToSearch.status = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {

                            filterToSearch.status = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }">
                        <template #option="props">
                          <span>{{ props.item.name }}</span>
                        </template>

                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="" class="font-bold">From</label>
                  <label for="" class="font-bold">To</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <Calendar v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                        show-icon icon-display="input" style="max-width: 130px; max-height: 32px"
                        :disabled="disableDates" :max-date="dayjs(new Date()).endOf('month').toDate()" />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <Calendar v-model="filterToSearch.to" date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                        icon-display="input" style="max-width: 130px; max-height: 32px; overflow: auto;"
                        :invalid="filterToSearch.to < filterToSearch.from" :disabled="disableDates"
                        :max-date="dayjs(new Date()).endOf('month').toDate()" :min-date="filterToSearch.from" />
                      <span v-if="filterToSearch.to < filterToSearch.from" style="color: red; margin-left: 2px;">Check
                        date range</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex align-items-center gap-2 ">
                <Checkbox id="all-check-1" v-model="disableDates" :binary="true" style="z-index: 999;" />
                <label for="all-check-1" class="font-bold">All</label>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="" class="font-bold">Criteria:</label>
                  <label for="" class="font-bold">Search:</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <Dropdown v-model="filterToSearch.criteria" show-clear option-label="name"
                        style="min-width: 140px;" :options="[...ENUM_INVOICE_CRITERIA]">
                        <template #option="slotProps">
                          <div class="flex align-items-center gap-2">
                            <span>{{ slotProps.option.name }}</span>
                          </div>
                        </template>
                      </Dropdown>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto mr-3">

                      <InputText v-model="filterToSearch.search" type="text" style="width: 140px;" />


                    </div>
                  </div>
                </div>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-start align-items-end mr-1">
                  <label for="" class="font-bold">Type: </label>
                  <div class="w-full lg:w-auto" />
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete" field="name" multiple
                        placeholder="" item-value="id" :model="filterToSearch.invoiceType"
                        :suggestions="invoiceTypeList" style="max-width: 400px;"
                        @load="($event) => getInvoiceTypeList($event)" @change="($event) => {
                          if (!filterToSearch.invoiceType.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                            filterToSearch.invoiceType = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {

                            filterToSearch.invoiceType = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }">
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>
                            {{ value?.code }}-{{ value?.name }}
                          </div>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2 w-full">
                    <Checkbox id="all-check-2" v-model="filterToSearch.includeInvoicePaid" :binary="true" />
                    <label for="all-check-2" class="font-bold">Include Invoice Paid</label>
                  </div>
                  <div class="flex align-items-center gap-2" />
                </div>
              </div>
              <div class="flex align-items-center gap-2">
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch"
                  :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                  :loading="loadingSearch" @click="clearFilterToSearch" />
              </div>
              <!-- <div class="col-12 md:col-3 sm:mb-2 flex align-items-center">
            </div> -->
              <!-- <div class="col-12 md:col-5 flex justify-content-end">
            </div> -->
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <ExpandableTable :data="listItems" :columns="columns" :options="options" :pagination="pagination"
        @on-confirm-create="clearForm" @open-edit-dialog="openEditDialog($event)"
        @on-change-pagination="payloadOnChangePage = $event" @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems" @on-sort-field="onSortField" @update:double-clicked="getItemById"
        @on-expand-field="($event) => { expandedInvoice = $event }"
        @on-select-field="($event) => { attachmentInvoice = $event }" @on-row-right-click="onRowRightClick">
        <template #column-invoiceDate="{ item: data }">
          {{ dayjs(data).format('DD-MM-YYYY') }}
        </template>
        <template #row-selector-body="props">
          <button v-if="props.item?.hasAttachments" class="pi pi-paperclip"
            style="background-color: white; border: 0; padding: 5px; border-radius: 100%;" @click="() => {
              attachmentInvoice = props.item
              attachmentDialogOpen = true

            }" />
        </template>

        <template #expanded-item="props">
          <InvoiceTabView :invoice-obj-amount="0" :selected-invoice="props.itemId" :is-dialog-open="bookingDialogOpen"
            :close-dialog="() => { bookingDialogOpen = false }" :open-dialog="handleDialogOpen" :active="active"
            :set-active="($event) => { active = $event }" :is-detail-view="true" />
        </template>

        <template #column-status="props">
          <Badge :value="getStatusName(props.item)"
            :style="`background-color: ${getStatusBadgeBackgroundColor(props?.item)}`" />
        </template>

        <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center font-bold font-500" style="font-weight: 700">
            <Row>
              <Column footer="Totals:" :colspan="9" footer-style="text-align:right; font-weight: 700" />
              <Column :footer="totalInvoiceAmount" footer-style="font-weight: 700" />
              <Column :footer="totalDueAmount" footer-style="font-weight: 700" />
              <Column :colspan="9" />


            </Row>
          </ColumnGroup>
        </template>


      </ExpandableTable>
    </div>
    <ContextMenu ref="invoiceContextMenu" :model="invoiceContextMenuItems" />
  </div>
  <div v-if="attachmentDialogOpen">
    <AttachmentDialog :close-dialog="() => { attachmentDialogOpen = false, getList() }" :is-creation-dialog="false"
      header="Manage Invoice Attachment" :open-dialog="attachmentDialogOpen" :selected-invoice="attachmentInvoice?.id"
      :selected-invoice-obj="attachmentInvoice" />
  </div>
  <div v-if="attachmentHistoryDialogOpen">
    <InvoiceHistoryDialog :selected-attachment="''" :close-dialog="() => { attachmentHistoryDialogOpen = false }"
      header="Invoice Status History" :open-dialog="attachmentHistoryDialogOpen"
      :selected-invoice="attachmentInvoice?.id" :selected-invoice-obj="attachmentInvoice" :is-creation-dialog="false" />
  </div>

  <div v-if="exportDialogOpen">
    <ExportDialog :total="pagination.totalElements" :close-dialog="() => { exportDialogOpen = false }"
      :open-dialog="exportDialogOpen" :payload="payload" />
  </div>
  <div v-if="exportPdfDialogOpen">
    <ExportToPdfDialog :close-dialog="() => { exportPdfDialogOpen = false }" :open-dialog="exportPdfDialogOpen"
      :payload="payload" :invoices="listItems" :total-amount="totalInvoiceAmount"
      :total-due-amount="totalInvoiceAmount" />
  </div>
  <div v-if="exportAttachmentsDialogOpen">
    <ExportAttachmentsDialog :close-dialog="() => { exportAttachmentsDialogOpen = false }"
      :open-dialog="exportAttachmentsDialogOpen" :payload="payload" :invoice="attachmentInvoice" />
  </div>
</template>
