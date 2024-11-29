<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean, statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'

// VARIABLES -----------------------------------------------------------------------------------------
const authStore = useAuthStore()
const { status, data: userData } = useAuth()
const isAdmin = (userData.value?.user as any)?.isAdmin === true

const objItemSelectedForRightClickApplyPayment = ref({} as GenericObject)
const objItemSelectedForRightClickChangeAgency = ref({} as GenericObject)
const objItemSelectedForRightClickApplyPaymentOtherDeduction = ref({} as GenericObject)
const objItemSelectedForRightClickPaymentWithOrNotAttachment = ref({} as GenericObject)
const objItemSelectedForRightClickNavigateToPayment = ref({} as GenericObject)

const objItemSelectedForRightClickInvoice = ref({} as GenericObject)

const onOffDialogPaymentDetailSummary = ref(false)

// Attachments for Invoice
const attachmentDialogOpenInvoice = ref<boolean>(false)
const attachmentListInvoice = ref<any[]>([])

// Attachments
const attachmentDialogOpen = ref<boolean>(false)
const attachmentList = ref<any[]>([])
const paymentSelectedForAttachment = ref<GenericObject>({})

// Share Files
const shareFilesDialogOpen = ref<boolean>(false)
const shareFilesList = ref<any[]>([])
const paymentSelectedForShareFiles = ref<GenericObject>({})

const contextMenu = ref()
const contextMenuInvoice = ref()

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
    id: 'statusHistory',
    label: 'Deposit',
    icon: 'pi pi-cog',
    iconSvg: 'M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z',
    viewBox: '0 -960 960 960',
    width: '19px',
    height: '19px',
    command: ($event: any) => dialogPaymentDetailSummary($event),
    disabled: false,
    visible: authStore.can(['PAYMENT-MANAGEMENT:SHOW-HISTORY']),
  },
  // {
  //   id: 'applyPaymentOtherDeduction',
  //   label: 'Apply Other Deductions',
  //   icon: 'pi pi-cog',
  //   iconSvg: '',
  //   viewBox: '',
  //   width: '24px',
  //   height: '24px',
  //   command: ($event: any) => openModalApplyPaymentOtherDeduction($event),
  //   disabled: false,
  //   visible: authStore.can(['PAYMENT-MANAGEMENT:APPLY-PAYMENT']),
  // },
  // {
  //   id: 'applyPayment',
  //   label: 'Apply Payment',
  //   icon: 'pi pi-cog',
  //   iconSvg: '',
  //   viewBox: '',
  //   width: '24px',
  //   height: '24px',
  //   command: ($event: any) => openModalApplyPayment($event),
  //   disabled: true,
  //   visible: authStore.can(['PAYMENT-MANAGEMENT:APPLY-PAYMENT']),
  // },
  {
    id: 'navigateToPaymentDetails',
    label: 'Show Details',
    icon: 'pi pi-cog',
    iconSvg: '',
    command: ($event: any) => navigateToPaymentDetails($event),
    disabled: false,
    visible: true,
  },
  {
    id: 'document',
    label: 'Document',
    icon: 'pi pi-file-word',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => handleAttachmentDialogOpen($event),
    disabled: false,
    visible: authStore.can(['PAYMENT-MANAGEMENT:DOCUMENT']),
  },
  {
    id: 'shareFiles',
    label: 'Share Files',
    icon: 'pi pi-share-alt',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => handleShareFilesDialogOpen($event),
    disabled: false,
    visible: authStore.can(['PAYMENT-MANAGEMENT:EDIT']),
  },
  {
    id: 'paymentWithoutAttachment',
    label: 'Payment Without Attachment',
    icon: '',
    iconSvg: 'M8.5 5.3L7.16 3.96C7.62 2.26 9.15 1 11 1c2.21 0 4 1.79 4 4v6.8l-1.5-1.5V5a2.5 2.5 0 0 0-5 0zM18 6h-1.5v7.3l1.5 1.5zm4.11 15.46l-1.27 1.27l-3.22-3.23c-.81 2.05-2.79 3.5-5.12 3.5C9.46 23 7 20.54 7 17.5V8.89L1.11 3l1.28-1.27zM11.5 15.5c0 .55.45 1 1 1s1-.45 1-1v-.11l-2-2zm4.92 2.81l-1.69-1.69A2.48 2.48 0 0 1 12.5 18a2.5 2.5 0 0 1-2.5-2.5v-3.61l-1.5-1.5v7.11c0 2.21 1.79 4 4 4a4.01 4.01 0 0 0 3.92-3.19M10 6.8l1.5 1.5V6H10z',
    viewBox: '0 0 24 24',
    width: '16px',
    height: '16px',
    command: ($event: any) => checkAttachment('ATTACHMENT_WITH_ERROR'),
    disabled: true,
    visible: true,
  },
  {
    id: 'paymentWithAttachment',
    label: 'Payment With Attachment',
    icon: '',
    iconSvg: 'M13.5 21.36c.2.48.47.93.79 1.34A5.497 5.497 0 0 1 7 17.5V5c0-2.21 1.79-4 4-4s4 1.79 4 4v9.54c-.97.87-1.65 2.04-1.9 3.38c-.19.05-.39.08-.6.08a2.5 2.5 0 0 1-2.5-2.5V6h1.5v9.5c0 .55.45 1 1 1s1-.45 1-1V5a2.5 2.5 0 0 0-5 0v12.5c0 2.21 1.79 4 4 4c.34 0 .67-.06 1-.14M18 6h-1.5v7.55c.47-.21.97-.37 1.5-.46zm3.34 9.84l-3.59 3.59l-1.59-1.59L15 19l2.75 3l4.75-4.75z',
    viewBox: '0 0 24 24',
    width: '16px',
    height: '16px',
    command: ($event: any) => checkAttachment('ATTACHMENT_WITHOUT_ERROR'),
    disabled: true,
    visible: true,
  },
])

const allMenuListItemsInvoice = ref([
  {
    id: 'navigateToPaymentDetails',
    label: 'Show Details',
    icon: 'pi pi-cog',
    iconSvg: '',
    command: ($event: any) => navigateToInvoiceDetails($event),
    disabled: false,
    visible: true,
  },
  {
    id: 'document',
    label: 'Document',
    icon: 'pi pi-file-word',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => handleAttachmentDialogOpenForInvoice($event),
    disabled: false,
    visible: true
  },
])
const allDefaultItem = { id: 'All', name: 'All', code: 'All' }
const activeTab = ref(0)
const allDefault = { id: 'All', name: 'All' }
const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [],
  hotel: [],
  client: null,
  clientName: '',
  clientStatus: '',
  creditDays: 0,
  language: '',
  primaryPhone: '',
  alternativePhone: '',
  email: '',
  contactName: '',
  country: '',
})
const filterToSearchTemp = {
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [],
  hotel: [],
  client: null,
  clientName: '',
  clientStatus: '',
  creditDays: 0,
  language: '',
  primaryPhone: '',
  alternativePhone: '',
  email: '',
  contactName: '',
  country: '',
}
// agency: [allDefaultItem],
//   hotel: [allDefaultItem],

const objLoading = ref({
  loadingAgency: false,
  loadingClient: false,
  loadingHotel: false,
  loadingStatus: false
})

const objApis = ref({
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  status: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  transactionType: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' },
})

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})
const confClientApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-client',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})
interface SubTotals {
  paymentAmount: number
  depositBalance: number
  applied: number
  noApplied: number
  noAppliedPorcentage: number
  appliedPorcentage: number
  depositBalancePorcentage: number
}
const subTotals = ref<SubTotals>({ paymentAmount: 0, depositBalance: 0, applied: 0, noApplied: 0, noAppliedPorcentage: 0, appliedPorcentage: 0, depositBalancePorcentage: 0 })
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const listItemsAgency = ref<any[]>([])
const listItemsInvoice = ref<any[]>([])
const loadingSearch = ref(false)
const formReload = ref(0)
const agencyList = ref<any[]>([])
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const hotelList = ref<any[]>([])
const clientList = ref<any[]>([])

const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency-type',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    disabled: false,
    dataType: 'code',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(5, 'Maximum 5 characters').regex(/^[a-z]+$/i, 'Only text characters allowed')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string()
      .trim()
      .min(1, 'The name field is required')
      .max(50, 'Maximum 50 characters')
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mb-3 mt-3',
  },
]

const item = ref<GenericObject>({
  name: '',
  code: '',
  status: true,
})

const itemTemp = ref<GenericObject>({
  name: '',
  code: '',
  status: true,
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
]

const columns: IColumn[] = [
  { field: 'icon', header: '', width: '25px', type: 'slot-icon', icon: 'pi pi-paperclip', sortable: false, showFilter: false, hidden: false },
  { field: 'paymentId', header: 'Id', type: 'text' },
  { field: 'transactionDate', header: 'Trans. Date', type: 'text' },
  { field: 'hotel', header: 'Hotel', width: '80px', widthTruncate: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' } },
  { field: 'agency', header: 'Agency', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'paymentAmount', header: 'P.Amount', type: 'number' },
  { field: 'depositBalance', header: 'D.Balance', type: 'number' },
  { field: 'applied', header: 'Applied', type: 'number' },
  { field: 'notApplied', header: 'Not Applied', type: 'number' },

]
const columnsInvoice: IColumn[] = [
  // { field: 'icon', header: '', type: 'text', showFilter: false, icon: 'pi pi-paperclip', sortable: false, width: '30px' },
  { field: 'hotel', header: 'Hotel', width: '80px', widthTruncate: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' } },
  { field: 'agency', header: 'Agency', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'invoiceNumber', header: 'Inv.No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen.Date', type: 'date' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  { field: 'paymentAmount', header: 'P.Amount', type: 'text' },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'text' },
  { field: 'aging', header: 'Aging', type: 'text' },
  { field: 'invoiceStatus', header: 'Status', frozen: true, type: 'slot-select', objApi: { moduleApi: 'invoicing', uriApi: 'manage-invoice-status' } },

]
const columnsAgency: IColumn[] = [
  { field: 'manageAgency', header: 'Agency', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'manageRegion', header: 'Region', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-region' } },
  { field: 'emailContact', header: 'Email Contact', type: 'text' },

]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Payment',
  moduleApi: 'payment',
  uriApi: 'payment',
  loading: false,
  // selectionMode: 'single' as 'multiple' | 'single',
  selectAllItemByDefault: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const optionsInv = ref({
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
const optionsAgency = ref({
  tableName: 'Agency',
  moduleApi: 'settings',
  uriApi: 'manage-agency-contact',
  loading: false,
  actionsAsMenu: false,
  showPagination: false,
  showCustomEmptyTable: true,
  scrollHeight: '150px',
  messageToDelete: 'Do you want to save the change?'
})
const optionsInvoice = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  selectAllItemByDefault: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const payloadOnChangePage = ref<PageState>()
const payloadOnChangePageAgency = ref<PageState>()
const payloadOnChangePageInv = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const payloadInv = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const payloadAgency = ref<IQueryRequest>({
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
const paginationInvoice = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const paginationAgency = ref<IPagination>({
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

async function getListContactByAgency(agencyIds: string[] = []) {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    optionsAgency.value.loading = true
    listItemsAgency.value = []
    const newListItems = []

    const filterAgency = payloadAgency.value.filter.find((item: IFilter) => item.key === 'manageAgency.id')
    if (filterAgency) {
      filterAgency.value = agencyIds
    }
    else {
      payloadAgency.value.filter.push({
        key: 'manageAgency.id',
        operator: 'IN',
        value: agencyIds,
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }

    const response = await GenericService.search(optionsAgency.value.moduleApi, optionsAgency.value.uriApi, payloadAgency.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationAgency.value.page = page
    paginationAgency.value.limit = size
    paginationAgency.value.totalElements = totalElements
    paginationAgency.value.totalPages = totalPages

    const existingIds = new Set(listItemsAgency.value.map(item => item.id))

    for (const iterator of dataList) {
      // if (Object.prototype.hasOwnProperty.call(iterator, 'agency')) {
      //   iterator.agencyType = iterator.agency.agencyTypeResponse
      //   iterator.agency = {
      //     ...iterator.agency,
      //     name: `${iterator.agency.code} - ${iterator.agency.name}`
      //   }
      // }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id)
      }
    }

    listItemsAgency.value = [...listItemsAgency.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    optionsAgency.value.loading = false
  }
}

async function getList() {
  const count: SubTotals = { paymentAmount: 0, depositBalance: 0, applied: 0, noApplied: 0, noAppliedPorcentage: 0, appliedPorcentage: 0, depositBalancePorcentage: 0 }
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const filterPaymentSource = payload.value.filter.find((item: IFilter) => item.key === 'paymentSource.code')
    if (filterPaymentSource) {
      filterPaymentSource.value = ['EXP', 'BK']
    }
    else {
      payload.value.filter.push({
        key: 'paymentSource.code',
        operator: 'IN',
        value: ['EXP', 'BK'],
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }

    const filterDepositAmount = payload.value.filter.find((item: IFilter) => item.key === 'depositAmount')
    if (filterDepositAmount) {
      filterDepositAmount.value = 0
    }
    else {
      payload.value.filter.push({
        key: 'depositAmount',
        operator: 'GREATER_THAN',
        value: 0,
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }

    const filterStatusCanceled = payload.value.filter.find((item: IFilter) => item.key === 'paymentStatus.cancelled')
    if (filterStatusCanceled) {
      filterStatusCanceled.value = false
    }
    else {
      payload.value.filter.push({
        key: 'paymentStatus.cancelled',
        operator: 'EQUALS',
        value: false,
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }

    const filterStatusApplied = payload.value.filter.find((item: IFilter) => item.key === 'paymentStatus.applied')
    if (filterStatusApplied) {
      filterStatusApplied.value = false
    }
    else {
      payload.value.filter.push({
        key: 'paymentStatus.applied',
        operator: 'EQUALS',
        value: false,
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    interface ListColor {
      NONE: string
      ATTACHMENT_WITH_ERROR: string
      ATTACHMENT_WITHOUT_ERROR: string
    }

    const listColor: ListColor = {
      NONE: '#616161',
      ATTACHMENT_WITH_ERROR: '#ff002b',
      ATTACHMENT_WITHOUT_ERROR: '#00b816',
    }
    let color = listColor.NONE

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel')) {
        iterator.hotel = {
          ...iterator.hotel,
          name: `${iterator.hotel.code} - ${iterator.hotel.name}`
        }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'agency')) {
        iterator.agencyType = iterator.agency.agencyTypeResponse
        iterator.agency = {
          ...iterator.agency,
          name: `${iterator.agency.code} - ${iterator.agency.name}`
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'paymentId')) {
        iterator.paymentId = String(iterator.paymentId)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'paymentAmount')) {
        count.paymentAmount = count.paymentAmount + iterator.paymentAmount
        iterator.paymentAmount = iterator.paymentAmount || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'depositBalance')) {
        count.depositBalance += iterator.depositBalance
        iterator.depositBalance = iterator.depositBalance || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'applied')) {
        count.applied += iterator.applied
        iterator.applied = iterator.applied || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'notApplied')) {
        count.noApplied += iterator.notApplied
        iterator.notApplied = iterator.notApplied || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'depositAmount')) {
        iterator.depositAmount = iterator.depositAmount || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'otherDeductions')) {
        iterator.otherDeductions = String(iterator.otherDeductions)
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'attachmentStatus')) {
        if (iterator.attachmentStatus?.patWithAttachment) {
          color = listColor.ATTACHMENT_WITHOUT_ERROR
        }
        else if (iterator.attachmentStatus?.pwaWithOutAttachment) {
          color = listColor.ATTACHMENT_WITH_ERROR
        }
        else if (iterator.attachmentStatus?.nonNone) {
          color = listColor.NONE
        }
        else {
          color = listColor.NONE
        }
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, color, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]

    // if (listItems.value.length > 0) {
    //   idItemToLoadFirstTime.value = listItems.value[0].id
    // }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    if (pagination.value.totalElements !== 0) {
      if (count.noApplied > 0 && count.paymentAmount > 0) {
        count.noAppliedPorcentage = (count.noApplied * 100) / count.paymentAmount
      }
      if (count.applied > 0 && count.paymentAmount > 0) {
        count.appliedPorcentage = (count.applied * 100) / count.paymentAmount
      }
      if (count.depositBalance > 0 && count.paymentAmount > 0) {
        count.depositBalancePorcentage = (count.depositBalance * 100) / count.paymentAmount
      }
    }
    subTotals.value = { ...count }
  }
}

async function getListInvoice() {
  if (optionsInv.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    optionsInv.value.loading = true
    listItemsInvoice.value = []
    const newListItems = []

    // totalInvoiceAmount.value = 0
    // totalDueAmount.value = 0

    // Filtros por reconciledStatus y sentStatus ----------------------------------------------------------------------------------------------
    const objFilterByReconciledStatus = payloadInv.value.filter.find((item: IFilter) => item.key === 'manageInvoiceStatus.reconciledStatus')
    if (objFilterByReconciledStatus) {
      objFilterByReconciledStatus.value = true
    }
    else {
      payloadInv.value.filter.push({
        key: 'manageInvoiceStatus.reconciledStatus',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'OR',
        type: 'filterSearch'
      })
    }
    const objFilterBySentStatus = payloadInv.value.filter.find((item: IFilter) => item.key === 'manageInvoiceStatus.sentStatus')
    if (objFilterBySentStatus) {
      objFilterBySentStatus.value = true
    }
    else {
      payloadInv.value.filter.push({
        key: 'manageInvoiceStatus.sentStatus',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'OR',
        type: 'filterSearch'
      })
    }
    // Aqui termina el filtro por reconciledStatus y sentStatus ----------------------------------------------------------------------------------------------

    // Filtro por el check Enable To Policy para el Status ----------------------------------------------------------------------------------------------
    const objFilterByEnableToPolicy = payloadInv.value.filter.find((item: IFilter) => item.key === 'manageInvoiceStatus.enabledToPolicy')
    if (objFilterByEnableToPolicy) {
      objFilterByEnableToPolicy.value = true
    }
    else {
      payloadInv.value.filter.push({
        key: 'manageInvoiceStatus.enabledToPolicy',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }
    // Aqui termina el filtro por el check Enable To Policy para el Status ----------------------------------------------------------------------------------------------

    // Filtro por el check Enable To Policy para el Invoice Type ----------------------------------------------------------------------------------------------

    // const objFilterByInvoiceType = payloadInv.value.filter.find((item: IFilter) => item.key === 'manageInvoiceType.enabledToPolicy')
    // if (objFilterByInvoiceType) {
    //   objFilterByInvoiceType.value = true
    // }
    // else {
    //   payloadInv.value.filter.push({
    //     key: 'manageInvoiceType.enabledToPolicy',
    //     operator: 'EQUALS',
    //     value: true,
    //     logicalOperation: 'AND',
    //     type: 'filterSearch'
    //   })
    // }
    // Aqui termina el filtro por el check Enable To Policy para el Invoice Type ----------------------------------------------------------------------------------------------

    // Filtro por el dueAmount ----------------------------------------------------------------------------------------------
    const objFilterByDueAmount = payloadInv.value.filter.find((item: IFilter) => item.key === 'dueAmount')
    if (objFilterByDueAmount) {
      objFilterByDueAmount.value = 0
    }
    else {
      payloadInv.value.filter.push({
        key: 'dueAmount',
        operator: 'GREATER_THAN',
        value: 0,
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }
    // Aqui termina el filtro por el dueAmount ----------------------------------------------------------------------------------------------

    const response = await GenericService.search(optionsInv.value.moduleApi, optionsInv.value.uriApi, payloadInv.value)
    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationInvoice.value.page = page
    paginationInvoice.value.limit = size
    paginationInvoice.value.totalElements = totalElements
    paginationInvoice.value.totalPages = totalPages

    const existingIds = new Set(listItemsInvoice.value.map(item => item.id))

    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        let invoiceNumber
        if (iterator?.invoiceNumber?.split('-')?.length === 3) {
          invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`
        }
        else {
          invoiceNumber = iterator?.invoiceNumber
        }
        newListItems.push({
          ...iterator,
          loadingEdit: false,
          loadingDelete: false,
          invoiceDate: new Date(iterator?.invoiceDate),
          agencyCd: iterator?.agency?.code,
          dueAmount: iterator?.dueAmount || 0,
          invoiceAmount: iterator?.invoiceAmount || 0,
          invoiceNumber: invoiceNumber ? invoiceNumber.replace('OLD', 'CRE') : '',
          hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}` }
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }

      // totalInvoiceAmount.value += iterator.invoiceAmount
      // totalDueAmount.value += iterator.dueAmount ? Number(iterator?.dueAmount) : 0
    }

    listItemsInvoice.value = [...listItemsInvoice.value, ...newListItems]
    return listItemsInvoice
  }

  catch (error) {
    console.error(error)
  }
  finally {
    optionsInv.value.loading = false
  }
}
interface InvoiceStatus {
  id: string
  code: string
  name: string
  showClone: boolean
  enabledToApply: boolean
  sentStatus: boolean
  reconciledStatus: boolean
  canceledStatus: boolean
  processStatus: boolean
}
function getStatusBadgeBackgroundColorByItem(item: InvoiceStatus) {
  if (!item) { return }
  if (item.processStatus) { return '#FF8D00' }
  if (item.sentStatus) { return '#006400' }
  if (item.reconciledStatus) { return '#005FB7' }
  if (item.canceledStatus) { return '#888888' }
}

function searchAndFilter() {
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
  options.value.selectAllItemByDefault = false
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value = JSON.parse(JSON.stringify(filterToSearchTemp))
  getList()
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}
function goToInvoice() {
  navigateTo('/invoice')
}
function goToPayment() {
  navigateTo('/payment')
}
async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.name = response.name
        item.value.status = statusToBoolean(response.status)
        item.value.description = response.description
        item.value.code = response.code
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
    payload.status = statusToString(payload.status)
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
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
      idItem.value = ''
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
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
      reject: () => {
        // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
      }
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
      reject: () => { }
    })
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

async function parseDataTableFilterInvoice(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsInvoice)

  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      if (parseFilter[i]?.key === 'agencyCd') {
        parseFilter[i].key = 'agency.code'
      }

      if (parseFilter[i]?.key === 'status') {
        parseFilter[i].key = 'invoiceStatus'
      }

      if (parseFilter[i]?.key === 'invoiceNumber') {
        parseFilter[i].key = 'invoiceNumberPrefix'
      }
    }
  }

  payloadFilter.value.filter = [...parseFilter || []]
  getListInvoice()
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

    clientList.value = []
    const response = await GenericService.search(confClientApi.moduleApi, confClientApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      clientList.value = [...clientList.value, {
        id: iterator.id,
        name: `${iterator.code} - ${iterator.name}`,
        onlyName: iterator.name,
        code: iterator.code,
        status: iterator.status
      }]
    }
  }
  catch (error) {
    console.error('Error loading client list:', error)
  }
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
  description?: string
  creditDay?: number
  country?: {
    id?: string
    name?: string
    code?: string
    status?: string
    managerLanguage?: {
      id?: string
      name?: string
      code?: string
      status?: string
    }
  }
  phone?: string
  alternativePhone?: string
  email?: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  code?: string
  description?: string
  creditDay?: number
  language?: string
  country?: string
  primaryPhone?: string
  alternativePhone?: string
  email?: string
}

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description,
    creditDay: data.creditDay,
    primaryPhone: data?.phone,
    alternativePhone: data?.alternativePhone,
    email: data?.email
  }
}

function mapFunctionForHotel(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description,
  }
}

async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingAgency = true
    let agencyTemp: any[] = []
    agencyList.value = []
    agencyTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    agencyList.value = [...agencyList.value, ...agencyTemp]
  }
  catch (error) {
    objLoading.value.loadingAgency = false
  }
  finally {
    objLoading.value.loadingAgency = false
  }
}

async function getAgencyListForLoadAll(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingAgency = true
    return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
  }
  catch (error) {
    objLoading.value.loadingAgency = false
  }
  finally {
    objLoading.value.loadingAgency = false
  }
}

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingHotel = true
    let hotelTemp: any[] = []
    hotelList.value = []
    hotelTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunctionForHotel, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    hotelList.value = [...hotelList.value, ...hotelTemp]
  }
  catch (error) {
    objLoading.value.loadingHotel = false
  }
  finally {
    console.log(hotelList.value)

    objLoading.value.loadingHotel = false
  }
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}
function onSortFieldInvoice(event: any) {
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
    if (event.sortField === 'status') {
      event.sortField = 'invoiceStatus'
    }
    if (event.sortField === 'invoiceNumber') {
      event.sortField = 'invoiceNumberPrefix'
    }
    payloadInv.value.sortBy = event.sortField
    payloadInv.value.sortType = event.sortOrder
    parseDataTableFilterInvoice(event.filter)
    // getListInvoice()
  }
}
// || filterToSearch.value.agency || filterToSearch.value.hotel
const disabledSearch = computed(() => {
  return filterToSearch.value.client?.id === '' || filterToSearch.value.agency.length === 0 || filterToSearch.value.hotel.length === 0
})

async function checkAttachment(code: string) {
  const payload = {
    payment: objItemSelectedForRightClickNavigateToPayment.value ? objItemSelectedForRightClickNavigateToPayment.value.id : '',
    status: code,
    employee: userData.value?.user?.userId || ''
  }
  try {
    await GenericService.create('payment', 'payment/change-attachment-status', payload)
    toast.add({ severity: 'success', summary: 'Success', detail: `The payment with id ${objItemSelectedForRightClickPaymentWithOrNotAttachment.value?.paymentId} was updated successfully`, life: 3000 })
    await getList()
  }
  catch (error) {
    console.log(error)
  }
}

function onRowContextMenu(event: any) {
  // idPaymentSelectedForPrint.value = event?.data?.id || ''
  // isPrintByRightClick.value = true
  // idPaymentSelectedForPrintChangeAgency.value = event?.data?.id || ''
  // objClientFormChangeAgency.value = event?.data?.client
  // currentAgencyForChangeAgency.value = event?.data?.agency
  // listClientFormChangeAgency.value = event?.data?.client ? [event?.data?.client] : []
  objItemSelectedForRightClickChangeAgency.value = event.data

  if (event && event.data) {
    paymentSelectedForAttachment.value = event.data
    objItemSelectedForRightClickApplyPaymentOtherDeduction.value = event.data
    objItemSelectedForRightClickNavigateToPayment.value = event.data

    // share files
    paymentSelectedForShareFiles.value = event.data
  }
  else {
    paymentSelectedForAttachment.value = {}
    objItemSelectedForRightClickApplyPaymentOtherDeduction.value = {}
    // share files
    paymentSelectedForShareFiles.value = {}
  }
  objItemSelectedForRightClickApplyPayment.value = event.data
  objItemSelectedForRightClickPaymentWithOrNotAttachment.value = event.data
  if (event && event.data && (event.data.paymentStatus && event.data.paymentStatus.cancelled === false && event.data.paymentStatus.applied === false)) {
    const menuItemApplayPayment = allMenuListItems.value.find(item => item.id === 'applyPayment')
    if (menuItemApplayPayment) {
      menuItemApplayPayment.disabled = false
      menuItemApplayPayment.visible = true
    }
  }
  else {
    const menuItemApplayPayment = allMenuListItems.value.find(item => item.id === 'applyPayment')
    if (menuItemApplayPayment) {
      menuItemApplayPayment.disabled = true
      menuItemApplayPayment.visible = true
    }
  }

  if (event && event.data && event.data?.hasAttachment && event.data?.attachmentStatus?.supported === false && event.data.attachmentStatus.nonNone) {
    const menuItemPaymentWithAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithAttachment')
    if (menuItemPaymentWithAttachment) {
      menuItemPaymentWithAttachment.disabled = false
      menuItemPaymentWithAttachment.visible = true
    }
    const menuItemPaymentWithOutAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithoutAttachment')
    if (menuItemPaymentWithOutAttachment) {
      menuItemPaymentWithOutAttachment.disabled = false
      menuItemPaymentWithOutAttachment.visible = true
    }
  }
  else {
    if (event && event.data && event.data?.hasAttachment && event.data?.attachmentStatus?.supported === false && event.data.attachmentStatus.pwaWithOutAttachment) {
      const menuItemPaymentWithAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithAttachment')
      if (menuItemPaymentWithAttachment) {
        menuItemPaymentWithAttachment.disabled = false
        menuItemPaymentWithAttachment.visible = true
      }
      const menuItemPaymentWithOutAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithoutAttachment')
      if (menuItemPaymentWithOutAttachment) {
        menuItemPaymentWithOutAttachment.disabled = true
        menuItemPaymentWithOutAttachment.visible = true
      }
    }
    else if (event && event.data && event.data?.hasAttachment && event.data?.attachmentStatus?.supported === false && event.data.attachmentStatus.patWithAttachment) {
      const menuItemPaymentWithOutAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithoutAttachment')
      if (menuItemPaymentWithOutAttachment) {
        menuItemPaymentWithOutAttachment.disabled = false
        menuItemPaymentWithOutAttachment.visible = true
      }
      const menuItemPaymentWithAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithAttachment')
      if (menuItemPaymentWithAttachment) {
        menuItemPaymentWithAttachment.disabled = true
        menuItemPaymentWithAttachment.visible = true
      }
    }
    else if (event && event.data && event.data?.hasAttachment && event.data?.attachmentStatus?.supported === true) {
      const menuItemPaymentWithAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithAttachment')
      if (menuItemPaymentWithAttachment) {
        menuItemPaymentWithAttachment.disabled = true
        menuItemPaymentWithAttachment.visible = true
      }
      const menuItemPaymentWithOutAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithoutAttachment')
      if (menuItemPaymentWithOutAttachment) {
        menuItemPaymentWithOutAttachment.disabled = true
        menuItemPaymentWithOutAttachment.visible = true
      }
    }
  }
  // event && event.data && event.data.applyPayment === false

  if (event && event.data && event.data.paymentStatus.applied === false && event.data.paymentStatus.cancelled === false) {
    const menuItemChangeAgency = allMenuListItems.value.find(item => item.id === 'changeAgency')
    if (menuItemChangeAgency) {
      menuItemChangeAgency.disabled = false
      menuItemChangeAgency.visible = true
    }
  }
  else {
    const menuItemChangeAgency = allMenuListItems.value.find(item => item.id === 'changeAgency')
    if (menuItemChangeAgency) {
      menuItemChangeAgency.disabled = true
      menuItemChangeAgency.visible = true
    }
  }

  if (event && event.data && (event.data.paymentStatus.code !== 'CAN' || event.data.paymentStatus.name !== 'Cancelled')) {
    const menuItemOtherDeduction = allMenuListItems.value.find(item => item.id === 'applyPaymentOtherDeduction')
    if (menuItemOtherDeduction) {
      menuItemOtherDeduction.disabled = false
      menuItemOtherDeduction.visible = true
    }
  }
  else {
    const menuItemOtherDeduction = allMenuListItems.value.find(item => item.id === 'applyPaymentOtherDeduction')
    if (menuItemOtherDeduction) {
      menuItemOtherDeduction.disabled = true
      menuItemOtherDeduction.visible = true
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

function onRowContextMenuInvoice(event: any) {
  if (event && event.data) {
    objItemSelectedForRightClickInvoice.value = event.data
  }

  const allHidden = allMenuListItemsInvoice.value.every(item => !item.visible)
  if (!allHidden) {
    contextMenuInvoice.value.show(event.originalEvent)
  }
  else {
    contextMenuInvoice.value.hide()
  }
}

function addAttachment(attachment: any) {
  attachmentList.value = [...attachmentList.value, attachment]
}

function updateAttachment(attachment: any) {
  const index = attachmentList.value.findIndex(item => item.id === attachment.id)
  attachmentList.value[index] = attachment
}

function handleShareFilesDialogOpen() {
  shareFilesDialogOpen.value = true
}
function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

function handleAttachmentDialogOpenForInvoice() {
  attachmentDialogOpenInvoice.value = true
}
function navigateToPaymentDetails($event: any) {
  if (objItemSelectedForRightClickNavigateToPayment.value && objItemSelectedForRightClickNavigateToPayment.value.id) {
    const url = `/payment/form/?id=${objItemSelectedForRightClickNavigateToPayment.value.id}`
    window.open(url, '_blank')
  }
}

function navigateToInvoiceDetails($event: any) {
  if (objItemSelectedForRightClickInvoice.value && objItemSelectedForRightClickInvoice.value.id) {
    let invoceType = ''
    switch (objItemSelectedForRightClickInvoice.value.invoiceType) {
      case 'INVOICE':
        invoceType = '/invoice'
        break
      case 'INCOME':
        invoceType = '/invoice/income'
        break
      case 'CREDIT':
        invoceType = '/invoice/credit'
        break
    }

    const url = `${invoceType}/edit/${objItemSelectedForRightClickInvoice.value.id}`
    window.open(url, '_blank')
  }
}
function dialogPaymentDetailSummary() {
  onOffDialogPaymentDetailSummary.value = true
}
function onCloseDialogSummary($event: any) {
  onOffDialogPaymentDetailSummary.value = $event
}
const op = ref()
const showClientView = ref(true)
const op2 = ref()
const showClientDetail = ref(true)
function onOffModalClientView() {
  if (op.value) {
    op.value.hide()
  }
  showClientView.value = !showClientView.value
}
function toggle(event: Event) {
  if (showClientView.value === false) {
    op.value.toggle(event)
  }
}

function onOffModalClientDetail() {
  if (op2.value) {
    op2.value.hide()
  }
  showClientDetail.value = !showClientDetail.value
}
function toggle2(event: Event) {
  if (showClientDetail.value === false) {
    op2.value.toggle(event)
  }
}
// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})
watch(payloadOnChangePageInv, (newValue) => {
  payloadInv.value.page = newValue?.page ? newValue?.page : 0
  payloadInv.value.pageSize = newValue?.rows ? newValue.rows : 10
  getListInvoice()
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
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
    getListInvoice()
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="grid p-0 m-0 my-0 py-0 px-0 mx-0">
    <div class="col-12 py-0 px-1">
      <div class="font-bold p-0 m-0">
        <h5 class="mb-0 p-0 ">
          Collection Management
        </h5>
      </div>
    </div>
    <div class="col-12 md:order-1 md:col-12 xl:col-6 lg:col-12 mt-0 px-1 py-0">
      <!-- Accordion y campos del payment -->

      <div v-if="showClientView" class="card p-0 m-0">
        <!-- Encabezado Completo -->
        <div class="font-bold text-lg bg-primary custom-card-header px-4">
          Search View
        </div>

        <!-- Contenedor de Contenido -->
        <div style="display: flex; height: 23%;" class="responsive-height">
          <!-- Sección Izquierda -->
          <div class="p-0 m-0 py-0 px-0 " style="flex: 1;">
            <div class="grid p-0 py-0 px-0 m-0">
              <!-- Selector de Cliente -->
              <div class="col-12 md:col-12 lg:col-12 xl:col-12 flex pb-0  w-full">
                <div class="flex flex-column gap-2 py-0 w-full">
                  <div class="flex align-items-center gap-2 px-0 py-0 ">
                    <label class="filter-label font-bold ml-3" for="client">Client<span
                      class="text-red"
                    >*</span></label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete"
                        :multiple="false"
                        field="name"
                        item-value="id"
                        class="w-full custom-input"
                        :model="filterToSearch.client"
                        :suggestions="clientList"
                        placeholder=""
                        @load="($event) => getClientList($event)"
                        @change="async ($event) => {
                          filterToSearch.client = $event
                          filterToSearch.agency = []
                          filterToSearch.clientName = $event?.onlyName ? $event?.onlyName : ''
                          filterToSearch.clientStatus = $event?.status ? $event?.status : ''
                          // filterToSearch.client = $event.filter(element => element?.id !== 'All');
                          // filterToSearch.agency = filterToSearch.client.length > 0 ? [{ id: 'All', name: 'All', code: 'All' }] : [];
                          const filter: FilterCriteria[] = [
                            {
                              key: 'client.id',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: filterToSearch.client?.id ? filterToSearch.client?.id : '',
                            },
                            {
                              key: 'status',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                              logicalOperation: 'AND',
                            },
                            {
                              key: 'autoReconcile',
                              operator: 'EQUALS',
                              value: true,
                              logicalOperation: 'AND',
                            },
                          ]
                          filterToSearch.agency = await getAgencyListForLoadAll(objApis.agency.moduleApi, objApis.agency.uriApi, {
                            query: '',
                            keys: ['name', 'code'],
                          }, filter)
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>{{ value?.code }}</div>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Selector de Agencia -->
              <div class="col-12 pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 px-0 py-0">
                    <label class="filter-label font-bold " for="agency">Agency<span
                      class="text-red"
                    >*</span></label>
                    <div class="w-full ">
                      <DebouncedMultiSelectComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete-agency"
                        field="code"
                        item-value="id"
                        :max-selected-labels="4"
                        class="w-full agency-input"
                        :model="filterToSearch.agency"
                        :loading="objLoading.loadingAgency"
                        :suggestions="agencyList"
                        placeholder=""
                        @change="($event) => {
                          filterToSearch.agency = $event;
                          const totalCreditDays = $event.reduce((sum, item) => sum + item.creditDay, 0);
                          filterToSearch.creditDays = totalCreditDays
                          filterToSearch.primaryPhone = $event[0]?.primaryPhone ? $event[0]?.primaryPhone : ''
                          filterToSearch.alternativePhone = $event[0]?.alternativePhone ? $event[0]?.alternativePhone : ''
                          filterToSearch.email = $event[0]?.email ? $event[0]?.email : ''
                          const listIds: string[] = $event.map((item: any) => item.id)
                          getListContactByAgency(listIds)
                        }"
                        @load="async ($event) => {
                          const filter: FilterCriteria[] = [
                            {
                              key: 'client.id',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: filterToSearch.client?.id ? filterToSearch.client?.id : '',
                            },
                            {
                              key: 'status',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                              logicalOperation: 'AND',
                            },
                            {
                              key: 'autoReconcile',
                              operator: 'EQUALS',
                              value: true,
                              logicalOperation: 'AND',
                            },
                          ]
                          await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, {
                            query: $event,
                            keys: ['name', 'code'],
                          }, filter)
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>{{ value?.code }}</div>
                        </template>
                      </DebouncedMultiSelectComponent>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Selector de Hotel -->
              <div class="col-12 pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 px-0 py-0">
                    <label class="filter-label font-bold ml-3" for="hotel">Hotel<span
                      class="text-red"
                    >*</span></label>
                    <div class="w-full">
                      <DebouncedMultiSelectComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete-hotel"
                        field="code"
                        item-value="id"
                        class="w-full hotel-input"
                        :max-selected-labels="4"
                        :model="filterToSearch.hotel"
                        :loading="objLoading.loadingHotel"
                        :suggestions="hotelList"
                        placeholder=""
                        @change="($event) => {
                          filterToSearch.hotel = $event;
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
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>{{ value?.code }}</div>
                        </template>
                      </DebouncedMultiSelectComponent>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex col-12 justify-content-end mt-2 py-2 xl:mt-0 py-2 pb-3">
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

          <!-- Divisor Vertical -->
          <div style="width: 4px; background-color: #d3d3d3; height: auto; margin: 0;" />

          <!-- Sección Derecha -->
          <div class="px-2 py-0 m-0 my-0 mt-0" style="flex: 1; padding: 16px;">
            <div class="grid py-0 my-0 px-0" style="max-width: 1200px; margin: auto;">
              <!-- Fila para Cliente y Agencia -->
              <div class="col-12 mb-0 ">
                <div class="flex items-center w-full" style="flex-wrap: nowrap;">
                  <!-- Usar flex para alinear en una fila -->
                  <label
                    for="client" class="font-bold mb-0 mt-2 required"
                    style="margin-right: 8px; flex: 0 0 auto;"
                  >Client Name</label>
                  <InputText
                    id="client" v-model="filterToSearch.clientName" class="w-full "
                    style="flex: 1;"
                  />
                </div>
              </div>

              <div class="col-12 mb-0 py-0">
                <div class="flex items-center w-full" style="flex-wrap: nowrap;">
                  <!-- Usar flex para alinear en una fila -->
                  <label
                    for="client" class="font-bold mb-0 mr-1 mt-2 required"
                    style="margin-right: 8px; flex: 0 0 auto;"
                  >Client Status</label>
                  <InputText
                    id="client" v-model="filterToSearch.clientStatus" class="w-full "
                    style="flex: 1;"
                  />
                </div>
              </div>
              <div class="col-12 mb-0 ">
                <div class="flex items-center w-full" style="flex-wrap: nowrap;">
                  <!-- Usar flex para alinear en una fila -->
                  <label
                    for="credit" class="font-bold mb-0 ml-0 mt-2 "
                    style="margin-right: 9px; flex: 0 0 auto;"
                  >Credit
                    Days</label>
                  <InputText
                    id="client" v-model="filterToSearch.creditDays" class="w-full  "
                    style="flex: 1;"
                  />
                </div>
              </div>
              <div v-if="false" class="col-12 my-0 py-0 pb-0 xl:col-12 mb-2">
                <div class="flex items-center w-full " style="flex-wrap: nowrap;">
                  <label for="language" class="font-bold mb-0 ml-3 mt-2">Language</label>
                  <div style="flex: 1; display: flex; gap: 8px; flex-wrap: nowrap;">
                    <InputText id="language1" v-model="filterToSearch.language" class="w-full" />
                    <InputText
                      id="timezone" v-model="currentTime" placeholder="Time Zone 12:10"
                      class="w-full pr-1 timezone-input"
                      style="background-color: var(--primary-color); color: white;" readonly
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex justify-content-between mt-0">
        <div class="flex align-items-center gap-2">
          <div class="p-2 font-bold">
            Payment View
          </div>
        </div>
        <div class="flex align-items-center gap-2">
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="ml-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Share File'" text label="Share File" icon="pi pi-share-alt"
              class="w-8rem" severity="primary" @click="clearForm"
            />
          </div>
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="ml-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Export'" text label="Export" icon="pi pi-download" class="w-6rem"
              severity="primary" @click="clearForm"
            />
          </div>
          <Button
            v-tooltip.left="'More'" label="+More"
            severity="primary" text class="" @click="goToPayment()"
          />
        </div>
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
        @on-row-right-click="onRowContextMenu($event)"
      >
        <template #column-icon="{ data: objData, column }">
          <div class="flex align-items-center justify-content-center p-0 m-0">
            <Button
              v-if="objData.hasAttachment"
              :icon="column.icon"
              class="p-button-rounded p-button-text w-2rem h-2rem"
              aria-label="Submit"
              :disabled="objData?.attachmentStatus?.nonNone"
              :style="{ color: objData.color }"
            />
          </div>
        </template>
        <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center ">
            <Row>
              <!-- <Column
                :colspan="6"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              /> -->
              <!-- <Column
                :footer="`Total Deposit #: ${''}`" :colspan="0"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              /> -->
              <Column
                :footer="`Total #: ${pagination.totalElements}`" :colspan="3"
                footer-style="text-align:left; font-weight: bold; color:#ffffff; background-color:#0F8BFD;"
              />
              <Column
                :footer="`Total $: ${formatNumber(subTotals.paymentAmount)}`" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
            </Row>
            <Row>
              <!-- <Column
                :colspan="6"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              /> -->
              <Column
                :footer="`Total Deposit $: ${formatNumber(subTotals.depositBalance)}`" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                :footer="`Total Applied $: ${formatNumber(subTotals.applied)}`" :colspan="3"
                footer-style="text-align:left; font-weight: bold; color:#000000; background-color:#ffffff;"
              />
              <Column
                :footer="`Total N.A $: ${formatNumber(subTotals.noApplied)}`" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
            </Row>
            <Row>
              <!-- <Column
                :colspan="6"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              /> -->
              <Column
                :footer="`Total Deposit %:  ${formatNumber(subTotals.depositBalancePorcentage, 2, 2)}`" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                :footer="`Total Applied %:  ${formatNumber(subTotals.appliedPorcentage, 2, 2)}`" :colspan="3"
                footer-style="text-align:left; font-weight: bold; color:#ffffff; background-color:#0F8BFD;"
              />
              <Column
                :footer="`Total N.A %:  ${formatNumber(subTotals.noAppliedPorcentage, 2, 2)}`" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <!-- <Column
                footer="Total Transit %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              /> -->
            </Row>
          </ColumnGroup>
        </template>
      </DynamicTable>
    </div>
    <!-- Section Invoice -->
    <div class="col-12 md:order-0 md:col-12 xl:col-6 lg:col-12 px-1 py-0">
      <div v-if="showClientDetail" class="card px-1 m-0 py-0">
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          Agency Contact
        </div>
        <div class="w-full" style="height: 13rem; min-height: 12.1rem;">
          <DynamicTable
            component-table-id="componentTableIdInCollection"
            :data="listItemsAgency" :columns="columnsAgency" :options="optionsAgency"
            :pagination="paginationAgency" @on-confirm-create="clearForm"
            @open-edit-dialog="getItemById($event)" @update:clicked-item="getItemById($event)"
            @on-change-pagination="payloadOnChangePage = $event"
            @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems"
            @on-sort-field="onSortField"
          >
            <template #emptyTable="{ data }">
              <div class="flex flex-column flex-wrap align-items-center justify-content-center py-3">
                <span v-if="!optionsAgency?.loading" class="flex flex-column align-items-center justify-content-center">
                  <div class="row">
                    <i class="pi pi-trash mb-3" style="font-size: 2rem;" />
                  </div>
                  <div class="row">
                    <p>{{ data.messageForEmptyTable }}</p>
                  </div>
                </span>
                <span v-else class="flex flex-column align-items-center justify-content-center">
                  <i class="pi pi-spin pi-spinner" style="font-size: 2.6rem" />
                </span>
              </div>
            </template>
          </DynamicTable>
        </div>
      </div>

      <div class="flex justify-content-between mt-0">
        <div class="flex align-items-center gap-2">
          <div class="p-2 font-bold">
            Invoice View
          </div>
        </div>
        <div class="flex align-items-center gap-2">
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Email'" text label="Email" icon="pi pi-envelope" class="w-6rem"
              severity="primary" @click="clearForm"
            />
          </div>
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="ml-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Print'" text label="Print" icon="pi pi-print" class="w-5rem"
              severity="primary" @click="clearForm"
            />
          </div>
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="ml-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Export'" text label="Export" icon="pi pi-download" class="w-6rem"
              severity="primary" @click="clearForm"
            />
          </div>
          <Button
            v-tooltip.left="'More'" label="+More"
            severity="primary" text class="" @click="goToInvoice()"
          />
        </div>
      </div>

      <DynamicTable
        :data="listItemsInvoice"
        :columns="columnsInvoice"
        :options="optionsInv"
        :pagination="paginationInvoice"
        @on-confirm-create="clearForm"
        @open-edit-dialog="getItemById($event)"
        @update:clicked-item="getItemById($event)"
        @on-change-pagination="payloadOnChangePageInv = $event"
        @on-change-filter="parseDataTableFilterInvoice"
        @on-list-item="resetListItems"
        @on-sort-field="onSortFieldInvoice"
        @on-row-right-click="onRowContextMenuInvoice($event)"
      >
        <template #column-invoiceStatus="{ data: objData }">
          <Badge
            v-if="objData.invoiceStatus" :value="objData.invoiceStatus.name"
            :style="`background-color: ${getStatusBadgeBackgroundColorByItem(objData.invoiceStatus)}`"
          />
        </template>
        <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center ">
            <Row>
              <Column
                footer="Total #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; color:#ffffff; background-color:#0F8BFD;"
              />
              <Column
                footer="Total $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total Pending #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total Invoice B #:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
            </Row>
            <Row>
              <Column
                footer="Total 30 Days #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; color:#000000; background-color:#ffffff;"
              />
              <Column
                footer="Total 60 Days #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total 90 Days #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total 120 Days #:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
            </Row>
            <Row>
              <Column
                footer="Total 30 Days $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; color:#ffffff; background-color:#0F8BFD;"
              />
              <Column
                footer="Total 60 Days $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total 90 Days $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total 120 Days $:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
            </Row>
            <Row>
              <Column
                footer="Total 30 Days %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; color:#000000; background-color:#ffffff;"
              />
              <Column
                footer="Total 60 Days %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total 90 Days %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total 120 Days %:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
            </Row>
          </ColumnGroup>
        </template>
      </DynamicTable>
    </div>
  </div>

  <div v-if="attachmentDialogOpenInvoice">
    <AttachmentDialogForManagerInvoice
      :close-dialog="() => { attachmentDialogOpenInvoice = false, getListInvoice() }"
      :is-creation-dialog="false"
      header="Manage Invoice Attachment"
      :open-dialog="attachmentDialogOpenInvoice"
      :selected-invoice="objItemSelectedForRightClickInvoice?.id"
      :selected-invoice-obj="objItemSelectedForRightClickInvoice"
      :disable-delete-btn="true"
      :document-option-has-been-used="true"
    />
  </div>

  <DialogPaymentDetailSummary
    title="Transactions ANTI Summary"
    :visible="onOffDialogPaymentDetailSummary"
    :selected-payment="objItemSelectedForRightClickNavigateToPayment"
    @update:visible="onCloseDialogSummary($event)"
  />

  <div v-if="attachmentDialogOpen">
    <PaymentAttachmentDialog
      is-create-or-edit-payment="edit"
      :add-item="addAttachment"
      :close-dialog="() => {
        attachmentDialogOpen = false
        getList()
      }"
      :is-creation-dialog="true"
      header="Manage Payment Attachment"
      :list-items="attachmentList"
      :open-dialog="attachmentDialogOpen"
      :update-item="updateAttachment"
      :selected-payment="paymentSelectedForAttachment"
      @update:list-items="attachmentList = $event"
    />
  </div>

  <div v-if="shareFilesDialogOpen">
    <PaymentShareFilesDialog
      is-create-or-edit-payment="edit"
      :add-item="addAttachment"
      :close-dialog="() => {
        shareFilesDialogOpen = false
        getList()
      }"
      :is-creation-dialog="true"
      header="Share Files"
      :list-items="shareFilesList"
      :open-dialog="shareFilesDialogOpen"
      :update-item="updateAttachment"
      :selected-payment="paymentSelectedForShareFiles"
      @update:list-items="shareFilesList = $event"
    />
  </div>

  <ContextMenu ref="contextMenu" :model="allMenuListItems">
    <template #itemicon="{ item }">
      <div v-if="item.iconSvg !== ''" class="w-2rem flex justify-content-center align-items-center">
        <svg xmlns="http://www.w3.org/2000/svg" :height="item.height" :viewBox="item.viewBox" :width="item.width" fill="#8d8faa">
          <path :d="item.iconSvg" />
        </svg>
      </div>
      <div v-else class="w-2rem flex justify-content-center align-items-center">
        <i v-if="item.icon" :class="item.icon" />
      </div>
    </template>
  </ContextMenu>

  <ContextMenu ref="contextMenuInvoice" :model="allMenuListItemsInvoice">
    <template #itemicon="{ item }">
      <div v-if="item.iconSvg !== ''" class="w-2rem flex justify-content-center align-items-center">
        <svg xmlns="http://www.w3.org/2000/svg" :height="item.height" :viewBox="item.viewBox" :width="item.width" fill="#8d8faa">
          <path :d="item.iconSvg" />
        </svg>
      </div>
      <div v-else class="w-2rem flex justify-content-center align-items-center">
        <i v-if="item.icon" :class="item.icon" />
      </div>
    </template>
  </ContextMenu>
</template>

<style lang="scss">
#componentTableIdInCollection {
  .p-datatable-thead>tr:first-child>th {
    background-color: #f1f1f4;
    color: var(--primary-color);
    font-weight: bold;

    svg {
      color: var(--primary-color);
    }
    .p-column-filter-menu-button > i {
        color: var(--primary-color) !important;
    }
  }
}
.timezone-input {
    background-color: var(--primary-color);
    color: white;
    font-weight: bold;
}

.timezone-input::placeholder {
    color: white;
    /* Cambia el color del placeholder a blanco */

}

.no-global-style .p-tabview-nav-container {
    padding-left: 0 !important;
    background-color: initial !important;
    border-top-left-radius: 0 !important;
    border-top-right-radius: 0 !important;
}

#tabView {
    .p-tabview-nav-container {
        .p-tabview-nav-link {
            color: var(--secondary-color) !important;
        }

        .p-tabview-nav-link:hover {
            border-bottom-color: transparent !important;
        }
    }
}

.tab-view {
    height: 10px;

}

.text-red {
    color: red;
    /* Define color rojo para el asterisco */
}

.tab-header {
    background-color: transparent !important;
    /* Fondo transparente para las pestañas */
    background-color: initial !important;
    /* Color azul para el texto */
    transition: color 0.3s;
    /* Transición suave para el color */
    padding: 6px;
    /* Menor padding para altura reducida */
}

.tab-header:hover {
    color: #0A6FB8 !important;
    /* Color más oscuro al pasar el mouse */
}

.header-card {
    border: 1px solid #ccc;
    /* Borde de la tarjeta */
    border-bottom-left-radius: 0;
    /* Borde inferior izquierdo recto */
    border-bottom-right-radius: 0;
    /* Borde inferior derecho recto */
    overflow: hidden;
    /* Asegura que los bordes redondeados se apliquen correctamente */
}

.custom-input {
    height: 30px;
    /* Altura por defecto */
}

.agency-input {
    height: 30px;
    /* Altura por defecto */
}

.hotel-input {
    height: 30px;
    /* Altura por defecto */
}

@media (max-width: 1199px) {

    /* Para pantallas grandes (lg) */
    .responsive-height {
        height: 23vh;
        /* Ajustar altura para pantallas grandes */
        margin: auto;
    }
}

/* Media query para pantallas grandes */
@media (min-width: 1200px) {
    .responsive-height {
        height: 25vh;
        /* Ajustar altura para pantallas extra largas */
    }

    .custom-input {
        height: 30px;
        /* Ajustar altura para pantallas grandes */
    }

    .agency-input {
        height: 30px;
        /* Ajustar altura para pantallas grandes */
    }

    .hotel-input {
        height: 30px;
        /* Altura por defecto */

    }

}
</style>
