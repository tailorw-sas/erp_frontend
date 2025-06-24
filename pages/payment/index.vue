<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { useToast } from 'primevue/usetoast'
import dayjs from 'dayjs'
import { z } from 'zod'
import { pad } from 'lodash'
import { formatNumber } from './utils/helperFilters'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean } from '~/utils/helpers'
import { isPrintModalOpen, itemMenuList } from '~/components/payment/indexBtns'
import IfCan from '~/components/auth/IfCan.vue'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import PaymentShareFilesDialog from '~/components/payment/PaymentShareFilesDialog.vue'
import PaymentBankDialog from '~/pages/payment/import-of-bank.vue'
import PaymentExpenseDialog from '~/pages/payment/import-of-expense.vue'
import PaymentAntiToIncomeDialog from '~/pages/payment/import-anti-income.vue'
import PaymentExpenseToBookingDialog from '~/pages/payment/import-expense-booking.vue'
import PaymentDetailDialog from '~/pages/payment/import-detail.vue'
import PayPrint from '~/pages/payment/print/index.vue'
import { copyTableToClipboard } from '~/pages/payment/utils/clipboardUtils'
import { copyPaymentsToClipboardPayMang } from '~/pages/payment/utils/clipboardUtilsListPayMang'
import { exportDataToExcel } from '~/utils/export-to-excel'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const authStore = useAuthStore()
const { status, data: userData } = useAuth()
const isAdmin = (userData.value?.user as any)?.isAdmin === true

// const  = { id: 'All', name: 'All', status: 'ACTIVE' }
const listItems = ref<any[]>([])
const clientItemsList = ref<any[]>([])
const agencyItemsList = ref<any[]>([])
const hotelItemsList = ref<any[]>([])
const statusItemsList = ref<any[]>([])
const openDialogApplyPayment = ref(false)
const openDialogImportTransactionsFromVCC = ref(false)
const openDialogApplyPaymentOtherDeduction = ref(false)
const openDialogCopyBatch = ref(false)
const disabledBtnApplyPayment = ref(true)
const disabledBtnApplyPaymentOtherDeduction = ref(true)
const objItemSelectedForRightClickApplyPayment = ref({} as GenericObject)
const objItemSelectedForRightClickChangeAgency = ref({} as GenericObject)
const objItemSelectedForRightClickApplyPaymentOtherDeduction = ref({} as GenericObject)
const objItemSelectedForRightClickPaymentWithOrNotAttachment = ref({} as GenericObject)
const paymentDetailsTypeDepositList = ref<any[]>([])
const paymentDetailsTypeDepositLoading = ref(false)
const paymentDetailsTypeDepositSelected = ref<any[]>([])

const startOfMonth = ref<any>(null)
const endOfMonth = ref<any>(null)

const checkApplyPayment = ref(true)
const loadAllInvoices = ref(false)
const idInvoicesSelectedToApplyPayment = ref<string[]>([])
const idInvoicesSelectedToApplyPaymentForOtherDeduction = ref<string[]>([])
const invoiceAmmountSelected = ref(0)
const paymentAmmountSelected = ref(0)
const paymentBalance = ref(0)
const PaymentBankDialogVisible = ref(false)
const PaymentExpenseDialogVisible = ref(false)
const PaymentAntiToIncomeDialogVisible = ref(false)
const PaymentExpenseToBookingDialogVisible = ref(false)
const PaymentDetailDialogVisible = ref(false)

// Attachments
const attachmentDialogOpen = ref<boolean>(false)
const attachmentList = ref<any[]>([])
const paymentSelectedForAttachment = ref<GenericObject>({})

// Share Files
const shareFilesDialogOpen = ref<boolean>(false)
const shareFilesList = ref<any[]>([])
const paymentSelectedForShareFiles = ref<GenericObject>({})

// CHange Agency
const idPaymentSelectedForPrintChangeAgency = ref('')
const objClientFormChangeAgency = ref<GenericObject>({})
const currentAgencyForChangeAgency = ref<GenericObject>({})
const listClientFormChangeAgency = ref<any[]>([])
const openDialogChangeAgency = ref(false)
const listAgencyByClient = ref<any[]>([])

// Print Modal

function closePrintModal() {
  isPrintModalOpen.value = false
}

const columnsChangeAgency = ref<IColumn[]>([
  { field: 'code', header: 'Code', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'name', header: 'Name', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'description', header: 'Description', type: 'text', width: '90px', sortable: true, showFilter: true },
])
const optionsOfTableChangeAgency = ref({
  tableName: 'Change Agency',
  moduleApi: 'settings',
  uriApi: 'manage-agency',
  expandableRows: false,
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const payloadChangeAgency = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'name',
  sortType: ENUM_SHORT_TYPE.ASC
})
const paginationChangeAgency = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const payloadOnChangePageChangeAgency = ref<PageState>()

// PRINT
const isPrintByRightClick = ref(false)
const idPaymentSelectedForPrint = ref('')
const paymentSelectedForPrintList = ref<any[]>([])
const openPrint = ref(false)
const openModalExportToExcel = ref(false)
const loadingPrintDetail = ref(false)
const loadingSaveAll = ref(false)
const formReload = ref(0)
const confApiPaymentDetailPrint = reactive({
  moduleApi: 'payment',
  uriApi: 'payment/report',
})

const fieldPrint = ref<FieldDefinitionType[]>([
  {
    field: 'paymentAndDetails',
    header: 'Payment and Details',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 md:col-6 required mb-2',
  },
  {
    field: 'invoiceRelated',
    header: 'Invoice Related',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 md:col-6 required mb-2',
  },
  {
    field: 'paymentSupport',
    header: 'Payment Support',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 md:col-6 required mb-2',
  },
  {
    field: 'invoiceRelatedWithSupport',
    header: 'Invoice Related With Support',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 md:col-6 required mb-2',
  },
  {
    field: 'allPaymentsSupport',
    header: 'All Payment Support',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 md:col-6 required mb-2',
  },
])

const itemPrint = ref<GenericObject>({
  paymentAndDetails: true,
  paymentSupport: false,
  allPaymentsSupport: false,
  invoiceRelated: false,
  invoiceRelatedWithSupport: false
})

const itemTempPrint = ref<GenericObject>({
  paymentAndDetails: true,
  paymentSupport: false,
  allPaymentsSupport: false,
  invoiceRelated: false,
  invoiceRelatedWithSupport: false
})

const idItemToLoadFirstTime = ref('')
const objApis = ref({
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  status: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  transactionType: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' },
})

const legend = ref(
  [
    // {
    //   name: 'Transit',
    //   color: '#ff002b',
    //   colClass: 'pr-4',
    // },
    {
      name: 'Confirmed',
      color: '#0c2bff',
      colClass: 'pr-3',
    },
    {
      name: 'Applied',
      color: '#00b816',
      colClass: 'pr-3',
    },
    {
      name: 'Cancelled',
      color: '#888888',
      colClass: 'pr-3',
    },

  ]
)
const filterAllDateRange = ref(false)
const filterToSearch = ref<GenericObject>({
  client: [],
  agency: [],
  allClientAndAgency: false,
  hotel: [],
  status: [],
  from: '',
  to: '',
  allFromAndTo: false,
  criteria: null,
  value: '',
  type: '',
  payApplied: null,
  detail: true,
})

const filterToSearchTemp = ref<GenericObject>({
  client: [],
  agency: [],
  allClientAndAgency: false,
  hotel: [],
  status: [],
  from: '',
  to: '',
  allFromAndTo: false,
  criteria: null,
  value: '',
  type: '',
  payApplied: null,
  detail: true,
})

const contextMenu = ref()
const allMenuListItems = ref([
  {
    id: 'paymentSelected',
    label: 'Select Payment:',
    icon: 'pi pi-folder',
    command: () => {},
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    disabled: true,
    visible: true,
  },
  {
    id: 'statusHistory',
    label: 'Status History',
    icon: 'pi pi-cog',
    iconSvg: 'M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z',
    viewBox: '0 -960 960 960',
    width: '19px',
    height: '19px',
    command: ($event: any) => openDialogStatusHistory($event),
    disabled: false,
    visible: authStore.can(['PAYMENT-MANAGEMENT:SHOW-HISTORY']),
  },
  {
    id: 'applyPaymentOtherDeduction',
    label: 'Apply Other Deductions',
    icon: 'pi pi-cog',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => openModalApplyPaymentOtherDeduction($event),
    disabled: false,
    visible: authStore.can(['PAYMENT-MANAGEMENT:APPLY-PAYMENT']),
  },
  {
    id: 'applyPayment',
    label: 'Apply Payment',
    icon: 'pi pi-cog',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => openModalApplyPayment($event),
    disabled: true,
    visible: authStore.can(['PAYMENT-MANAGEMENT:APPLY-PAYMENT']),
  },
  {
    id: 'CopyBatch',
    label: 'Copy Batch',
    icon: 'pi pi-file',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => openModalCopyBatch($event),
    disabled: false,
    visible: authStore.can(['PAYMENT-MANAGEMENT:APPLY-PAYMENT']),
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
    id: 'print',
    label: 'Print',
    icon: 'pi pi-print',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => {
      openDialogPrint()
    },
    disabled: false,
    visible: authStore.can(['PAYMENT-MANAGEMENT:PRINT']),
  },
  {
    id: 'changeAgency',
    label: 'Change Agency',
    icon: 'pi pi-arrow-right-arrow-left',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => openModalApplyChangeAgency($event),
    disabled: true,
    visible: authStore.can(['PAYMENT-MANAGEMENT:EDIT']),
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
    id: 'importTransaction',
    label: 'Import Transaction',
    icon: 'pi pi-file-import',
    iconSvg: '',
    viewBox: '',
    width: '24px',
    height: '24px',
    command: ($event: any) => { openDialogImportTransactionsFromVCC.value = true },
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
// -------------------------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'paymentId', name: 'Payment Id', disabled: false },
  { id: 'reference', name: 'Reference No.', disabled: false },
  { id: 'paymentAmount', name: 'P. Amount', disabled: false },
  { id: 'remark', name: 'Remark' },
  { id: 'paymentDetails.paymentDetailId', name: 'Detail Id', disabled: false },
  { id: 'paymentDetails.amount', name: 'Detail Amount', disabled: false },
  { id: 'paymentDetails.remark', name: 'Detail Remark', disabled: false },
  { id: 'paymentDetails.manageBooking.invoice.invoiceNumber', name: 'Invoice No.', disabled: false },
  { id: 'paymentDetails.manageBooking.bookingId', name: 'Booking Id', disabled: false },
  { id: 'paymentDetails.manageBooking.fullName', name: 'Full Name', disabled: false },
  { id: 'paymentDetails.manageBooking.reservationNumber', name: 'Reservation No.', disabled: false },
  { id: 'paymentDetails.manageBooking.couponNumber', name: 'Coupon No.', disabled: false },
]

const ENUM_FILTER_TYPE = [
  { id: 'All', name: 'All', status: 'ACTIVE' },
  { id: 'applied', name: 'Applied' },
  { id: 'noApplied', name: 'UnApplied' },
]

const sClassMap: IStatusClass[] = [
  { status: 'Transit', class: 'text-transit' },
  { status: 'Cancelled', class: 'text-cancelled' },
  { status: 'Confirmed', class: 'text-confirmed' },
  { status: 'Applied', class: 'text-applied' },
]

// Encuentra el menú 'import'
const importMenu = computed(() => itemMenuList.value.find(item => item.id === 'import'))

// Encuentra el ítem 'Payment of bank'
const paymentOfBankItem = computed(() =>
  importMenu.value?.menuItems.find(item => item.label === 'Payment Of Bank')
)

// Reemplaza el command con la nueva función
if (paymentOfBankItem.value) {
  paymentOfBankItem.value.command = () => {
    PaymentBankDialogVisible.value = true
  }
}
function closeImportBank() {
  PaymentBankDialogVisible.value = false
}

// Encuentra el ítem 'Payment of Expense'
const paymentOfExpenseItem = computed(() =>
  importMenu.value?.menuItems.find(item => item.label === 'Payment Of Expense')
)

// Reemplaza el command con la nueva función
if (paymentOfBankItem.value) {
  paymentOfExpenseItem.value.command = () => {
    PaymentExpenseDialogVisible.value = true
  }
}
function closeImportExpense() {
  PaymentExpenseDialogVisible.value = false
}

// Encuentra el ítem 'Anti To Income'
const paymentAntiToIncomeItem = computed(() =>
  importMenu.value?.menuItems.find(item => item.label === 'Anti To Income')
)

// Reemplaza el command con la nueva función
if (paymentAntiToIncomeItem.value) {
  paymentAntiToIncomeItem.value.command = () => {
    PaymentAntiToIncomeDialogVisible.value = true
  }
}
function closeImportAntiToIncome() {
  PaymentAntiToIncomeDialogVisible.value = false
}

// Encuentra el ítem 'Expense To Booking'
const paymentExpenseToBookingItem = computed(() =>
  importMenu.value?.menuItems.find(item => item.label === 'Expense To Booking')
)

// Reemplaza el command con la nueva función
if (paymentExpenseToBookingItem.value) {
  paymentExpenseToBookingItem.value.command = () => {
    PaymentExpenseToBookingDialogVisible.value = true
  }
}
function closeImportExpenseToBooking() {
  PaymentExpenseToBookingDialogVisible.value = false
}

// Encuentra el ítem 'Payment Detail'
const paymentPaymentDetailItem = computed(() =>
  importMenu.value?.menuItems.find(item => item.label === 'Payment Detail')
)

// Reemplaza el command con la nueva función
if (paymentPaymentDetailItem.value) {
  paymentPaymentDetailItem.value.command = () => {
    PaymentDetailDialogVisible.value = true
  }
}
function closePaymentDetail() {
  PaymentDetailDialogVisible.value = false
}

// TABLE COLUMNS -----------------------------------------------------------------------------------------
interface SubTotals {
  paymentAmount: number
  depositBalance: number
  applied: number
  noApplied: number
}
const subTotals = ref<SubTotals>({ paymentAmount: 0, depositBalance: 0, applied: 0, noApplied: 0 })

interface DataListItemForBankAccount {
  id: string
  accountNumber: string
  manageBank: {
    id: string
    name: string
  }
  status: string
}

interface ListItemForBankAccount {
  id: string
  name: string
  status: string
}
function mapFunctionForBankAccount(data: DataListItemForBankAccount): ListItemForBankAccount {
  return {
    id: data.id,
    name: `${data.manageBank.name} (${data.accountNumber}) `,
    status: data.status
  }
}
const columns: IColumn[] = [
  {
    field: 'icon',
    header: 'Att',
    width: '20px', // Establece un tamaño fijo para la columna del icono
    minWidth: '20px', // Evita que se haga más pequeña
    maxWidth: '30px', // Evita que se haga más grande
    type: 'slot-icon',
    icon: 'pi pi-paperclip',
    sortable: true,
    showFilter: false,
    hidden: false
  },
  {
    field: 'paymentId',
    header: 'Id',
    tooltip: 'Payment ID',
    width: '20px', // Ancho fijo
    minWidth: '50px', // Ancho mínimo
    maxWidth: '50px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text',
    type: 'text',
    showFilter: true
  },
  {
    field: 'paymentSource',
    header: 'P.S',
    tooltip: 'Payment Source',
    width: '20px', // Define un ancho fijo
    minWidth: '50px', // Define un ancho mínimo
    maxWidth: '40px', // Evita que crezca demasiado
    widthTruncate: '50px', // Asegura que el truncamiento respete este tamaño
    columnClass: 'truncate-text',
    type: 'select',
    objApi: { moduleApi: 'settings', uriApi: 'manage-payment-source' }
  },
  {
    field: 'transactionDate',
    header: 'Trans. D',
    tooltip: 'Transaction Date',
    width: '20px', // Define un ancho fijo
    minWidth: '50px', // Define un ancho mínimo
    maxWidth: '60px', // Evita que crezca demasiado
    widthTruncate: '50px', // Personalización para asegurar truncamiento
    columnClass: 'truncate-text',
    type: 'date'
  },
  {
    field: 'hotel',
    header: 'Hotel',
    width: '120px', // Aumenta el ancho principal
    minWidth: '100px', // Define un ancho mínimo mayor
    maxWidth: '120px', // Permite un máximo de 120px antes de truncar
    widthTruncate: '120px', // Asegura que el truncamiento respete este tamaño
    columnClass: 'truncate-text',
    type: 'select',
    objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' }
  },
  {
    field: 'client',
    header: 'Client',
    width: '90px',
    minWidth: '60px', // Ajusta el ancho mínimo antes de truncar
    maxWidth: '90px', // Límite máximo antes de truncar
    widthTruncate: '90px', // Asegura que el truncamiento respete este tamaño
    columnClass: 'truncate-text', // Aplica la clase CSS para truncar
    type: 'select',
    objApi: {
      moduleApi: 'settings',
      uriApi: 'manage-client'
    }
  },
  {
    field: 'agency',
    header: 'Agency',
    width: '120px',
    minWidth: '100px', // Ajusta el ancho mínimo antes de truncar
    maxWidth: '120px', // Límite máximo antes de truncar
    widthTruncate: '120px', // Asegura que el truncamiento respete este tamaño
    columnClass: 'truncate-text', // Aplica la clase CSS para truncar
    type: 'select',
    objApi: {
      moduleApi: 'settings',
      uriApi: 'manage-agency'
    }
  },
  {
    field: 'agencyType',
    header: 'Agency T.',
    tooltip: 'Agency Type',
    width: '70px', // Ancho fijo
    minWidth: '60px', // Define un ancho mínimo
    maxWidth: '70px', // Evita que se expanda demasiado
    widthTruncate: '70px', // Control de truncamiento
    columnClass: 'truncate-text',
    type: 'select',
    objApi: { moduleApi: 'settings', uriApi: 'manage-agency-type' }
  },
  // { field: 'agencyTypeResponse', header: 'Agency Type', tooltip: 'Agency Type', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency-type' } },
  {
    field: 'bankAccount',
    header: 'Bank Acc',
    tooltip: 'Bank Account',
    width: '100px',
    minWidth: '80px', // Ajusta el ancho mínimo antes de truncar
    maxWidth: '100px', // Límite máximo antes de truncar
    widthTruncate: '100px', // Asegura que el truncamiento respete este tamaño
    columnClass: 'truncate-text', // Aplica la clase CSS para truncar
    type: 'select',
    objApi: {
      moduleApi: 'settings',
      uriApi: 'manage-bank-account',
      keyValue: 'name',
      mapFunction: mapFunctionForBankAccount,
      sortOption: { sortBy: 'manageBank.name', sortType: ENUM_SHORT_TYPE.ASC }
    }
  },
  { field: 'paymentAmount', header: 'P. Amount', tooltip: 'Payment Amount', width: '70px', type: 'number' },
  { field: 'depositBalance', header: 'D.Balance', tooltip: 'Deposit Balance', width: '60px', type: 'number' },
  { field: 'applied', header: 'Applied', tooltip: 'Applied', width: '60px', type: 'number' },
  { field: 'notApplied', header: 'Not Applied', tooltip: 'Not Applied', width: '60px', type: 'number' },
  { field: 'remark', header: 'Remark', width: '100px', maxWidth: '200px', type: 'text' },
  {
    field: 'paymentStatus',
    header: 'Status',
    width: '60px', // Define un ancho fijo
    minWidth: '50px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '60px', // Control de truncamiento
    columnClass: 'truncate-text',
    frozen: true, // Mantiene la columna fija
    type: 'slot-select',
    statusClassMap: sClassMap,
    objApi: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
    sortable: true
  }

]

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Payment',
  moduleApi: 'payment',
  uriApi: 'payment',
  loading: false,
  actionsAsMenu: true,
  // selectionMode: 'multiple',
  selectAllItemByDefault: false,
  showSelectedItems: false,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?'
})
// selectionMode: 'multiple',
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

const messageForEmptyTable = ref('There are no items to show.')
const loadingSaveApplyPayment = ref(false)
const invoiceSelectedListForApplyPayment = ref<any[]>([])
const applyPaymentListOfInvoice = ref<any[]>([])
const applyPaymentColumns = ref<IColumn[]>([
  {
    field: 'invoiceId',
    header: 'Id',
    type: 'text',
    width: '70px', // Define un ancho fijo
    minWidth: '60px', // Establece un ancho mínimo
    maxWidth: '70px', // Evita que se expanda demasiado
    widthTruncate: '70px', // Control de truncamiento
    columnClass: 'truncate-text',
    sortable: true,
    showFilter: false
  },
  {
    field: 'invoiceNumber',
    header: 'Invoice No.',
    type: 'text',
    width: '70px', // Define un ancho fijo
    minWidth: '60px', // Establece un ancho mínimo
    maxWidth: '70px', // Evita que se expanda demasiado
    widthTruncate: '70px', // Control de truncamiento
    columnClass: 'truncate-text',
    sortable: true,
    showFilter: false
  },
  {
    field: 'agency',
    header: 'Agency',
    type: 'select',
    width: '90px', // Define un ancho fijo
    minWidth: '80px', // Establece un ancho mínimo
    maxWidth: '90px', // Evita que se expanda demasiado
    widthTruncate: '90px', // Control de truncamiento
    columnClass: 'truncate-text',
    sortable: true,
    showFilter: false
  },
  {
    field: 'hotel',
    header: 'Hotel',
    type: 'select',
    width: '90px', // Define un ancho fijo
    minWidth: '80px', // Establece un ancho mínimo
    maxWidth: '90px', // Evita que se expanda demasiado
    widthTruncate: '90px', // Control de truncamiento
    columnClass: 'truncate-text',
    sortable: false,
    showFilter: false
  },
  { field: 'couponNumbers', header: 'Coupon No.', type: 'text', width: '90px', maxWidth: '100px', sortable: true, showFilter: false },
  { field: 'invoiceAmountTemp', header: 'Invoice Amount', type: 'text', width: '80px', sortable: true, showFilter: false },
  { field: 'dueAmountTemp', header: 'Invoice Balance', type: 'text', width: '80px', sortable: true, showFilter: false },

])

// Table
const columnsExpandTable: IColumn[] = [
  { field: 'bookingId', header: 'Id', width: '120px', type: 'text', sortable: false },
  { field: 'fullName', header: 'Full Name', width: '200px', type: 'text', sortable: false },
  { field: 'hotelBookingNumber', header: 'Reservation No.', width: '120px', maxWidth: '150px', type: 'text', sortable: false },
  // { field: 'invoiceNumber', header: 'Invoice No', width: '150px', type: 'text', sortable: false },
  { field: 'couponNumber', header: 'Coupon No.', width: '120px', type: 'text', sortable: false },
  // { field: 'adult', header: 'Adult', width: '120px', type: 'text', sortable: false },
  { field: 'checkIn', header: 'Check In', width: '120px', type: 'text', sortable: false },
  { field: 'checkOut', header: 'Check Out', width: '120px', type: 'text', sortable: false },
  { field: 'nights', header: 'Nights', width: '100px', type: 'text', sortable: false },
  { field: 'invoiceAmount', header: 'Booking Amount', width: '100px', type: 'text', sortable: false },
  { field: 'dueAmount', header: 'Booking Balance', width: '100px', type: 'text', sortable: false },
]

const columnsPaymentDetailTypeDeposit: IColumn[] = [
  { field: 'paymentDetailId', header: 'Id', tooltip: 'Detail Id', width: 'auto', type: 'text' },
  { field: 'transactionType', header: 'P. Trans Type', tooltip: 'Payment Transaction Type', width: '150px', type: 'text' },
  { field: 'amount', header: 'Deposit Amount', tooltip: 'Deposit Amount', width: 'auto', type: 'text' },
  { field: 'applyDepositValue', header: 'Deposit Balance', tooltip: 'Deposit Amount', width: 'auto', type: 'text' },
  { field: 'remark', header: 'Remark', width: 'auto', maxWidth: '200px', type: 'text' },
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
const payloadpaymentDetailForTypeDepositPagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 10,
  search: ''
})

const payloadpaymentDetailForTypeDepositPaginationTemp = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
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
  showSelectedItems: true,
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
  sortBy: 'invoiceNumber',
  sortType: ENUM_SHORT_TYPE.ASC
})
const applyPaymentPagination = ref<IPagination>({
  page: 0,
  limit: 10,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const applyPaymentPaginationTemp = ref<IPagination>({
  page: 0,
  limit: 100,
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
  sortBy: 'invoice.invoiceNumber',
  sortType: ENUM_SHORT_TYPE.ASC
})
const applyPaymentBookingPagination = ref<IPagination>({
  page: 0,
  limit: 10,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const applyPaymentBookingOnChangePage = ref<PageState>()

const openDialogHistory = ref(false)
const historyList = ref<any[]>([])
const employeeList = ref<any[]>([])
const historyColumns = ref<IColumn[]>([
  { field: 'paymentHistoryId', header: 'Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'paymentId', header: 'Payment Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'createdAt', header: 'Date', type: 'date', width: '120px', sortable: false, showFilter: false },
  { field: 'employee', header: 'Employee', type: 'select', width: '150px', localItems: [], sortable: false, showFilter: false },
  { field: 'description', header: 'Remark', type: 'text', width: '200px', sortable: false, showFilter: false },
  { field: 'paymentStatus', header: 'Status', type: 'slot-text', width: '60px', statusClassMap: sClassMap, objApi: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' }, sortable: false, showFilter: false },
  // { field: 'paymentStatus', header: 'Status', width: '100px', type: 'slot-select', statusClassMap: sClassMap, objApi: { moduleApi: 'settings', uriApi: 'manage-payment-status' }, sortable: true, showFilter: false },
])

const historyOptions = ref({
  tableName: 'Payment Attachment',
  moduleApi: 'payment',
  uriApi: 'payment-attachment-status-history',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  showPagination: false,
  messageToDelete: 'Do you want to save the change?'
})
const payloadHistory = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 1000,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'ASC'
})
const historyPagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const histotyPayloadOnChangePage = ref<PageState>()

// -------------------------------------------------------------------------------------------------------
const applyPaymentListOfInvoiceOtherDeduction = ref<any[]>([])
const transactionTypeList = ref<any[]>([])
const transactionType = ref<GenericObject>({})
const fieldRemark = ref('')
const allInvoiceCheckIsChecked = ref(false)

const applyPaymentColumnsOtherDeduction = ref<IColumn[]>([
  { field: 'invoiceId', header: 'Invoice Id', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'bookingId', header: 'Booking Id', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'invoiceNumber', header: 'Invoice No.', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'fullName', header: 'Full Name', type: 'text', width: '80px', // Define un ancho fijo
    minWidth: '70px', // Establece un ancho mínimo
    maxWidth: '90px', // Evita que se expanda demasiado
    widthTruncate: '80px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'couponNumber', header: 'Coupon No.', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'hotelBookingNumber', header: 'Reservation No.', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'checkIn', header: 'Check-In', type: 'date', width: '30px', // Define un ancho fijo
    minWidth: '20px', // Establece un ancho mínimo
    maxWidth: '40px', // Evita que se expanda demasiado
    widthTruncate: '30px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: false, showFilter: false },
  { field: 'checkOut', header: 'Check-Out', type: 'date', width: '30px', // Define un ancho fijo
    minWidth: '20px', // Establece un ancho mínimo
    maxWidth: '40px', // Evita que se expanda demasiado
    widthTruncate: '30px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: false, showFilter: false },
  { field: 'bookingAmountTemp', header: 'Booking Amount', type: 'number', width: '90px', sortable: true, showFilter: true },
  { field: 'dueAmountTemp', header: 'Booking Balance', type: 'number', width: '90px', sortable: true, showFilter: true, editable: true },
])

const allInvoiceCheckIsChecked1 = ref(false)

const applyPaymentColumnsOtherDeduction1 = ref<IColumn[]>([
  { field: 'paymentId', header: 'Payment Id', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'couponNumber', header: 'Coupon', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'invoiceNumber', header: 'Invoice No', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'dueAmountTemp', header: 'Balance', type: 'number', width: '70px', sortable: true, showFilter: true, editable: true },
  { field: 'trans', header: 'Trans. Category', type: 'date', width: '30px', // Define un ancho fijo
    minWidth: '20px', // Establece un ancho mínimo
    maxWidth: '40px', // Evita que se expanda demasiado
    widthTruncate: '30px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'ANTI', header: 'ANTI', type: 'date', width: '30px', // Define un ancho fijo
    minWidth: '20px', // Establece un ancho mínimo
    maxWidth: '40px', // Evita que se expanda demasiado
    widthTruncate: '30px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'rema', header: 'Remark', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'firstName', header: 'First Name', type: 'text', width: '80px', // Define un ancho fijo
    minWidth: '70px', // Establece un ancho mínimo
    maxWidth: '90px', // Evita que se expanda demasiado
    widthTruncate: '80px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'lastName', header: 'Last Name', type: 'text', width: '80px', // Define un ancho fijo
    minWidth: '70px', // Establece un ancho mínimo
    maxWidth: '90px', // Evita que se expanda demasiado
    widthTruncate: '80px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'hotelBookingNumber', header: 'Booking No', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'bookingId', header: 'Book Id', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
  { field: 'Imp', header: 'Imp Status ', type: 'text', width: '50px', // Define un ancho fijo
    minWidth: '40px', // Establece un ancho mínimo
    maxWidth: '60px', // Evita que se expanda demasiado
    widthTruncate: '50px', // Control de truncamiento
    columnClass: 'truncate-text', sortable: true, showFilter: true },
])

const applyPaymentOptionsOtherDeduction = ref({
  tableName: 'Apply Payment',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/search-payment',
  expandableRows: false,
  selectionMode: 'multiple',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const applyPaymentOptionsOtherDeduction1 = ref({
  tableName: 'Apply Payment',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/search-payment',
  expandableRows: false,
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const applyPaymentPayloadOtherDeduction = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 500,
  page: 0,
  sortBy: 'invoice.invoiceNumber',
  sortType: ENUM_SHORT_TYPE.ASC
})
const applyPaymentPaginationOtherDeduction = ref<IPagination>({
  page: 0,
  limit: 500,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const applyPaymentPayloadOtherDeductionPayment = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 100,
  page: 0,
  sortBy: 'manageBooking.bookingId',
  sortType: ENUM_SHORT_TYPE.ASC
})
const applyPaymentPayloadOtherDeduction1 = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 500,
  page: 0,
  sortBy: 'invoice.invoiceNumber',
  sortType: ENUM_SHORT_TYPE.ASC
})
const applyPaymentPaginationOtherDeduction1 = ref<IPagination>({
  page: 0,
  limit: 500,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const confApiPaymentExportToExcel = reactive({
  moduleApi: 'payment',
  uriApi: 'payment/excel-exporter',
})

const applyPaymentOnChangePageOtherDeduction = ref<PageState>()
const applyPaymentOnChangePageOtherDeduction1 = ref<PageState>()

const loadingExportToExcel = ref(false)
const fieldExportToExcel = ref<FieldDefinitionType[]>([
  {
    field: 'exportSumary',
    header: 'Export Sumary',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 md:col-6 required mb-3',
  },
  {
    field: 'fileName',
    header: 'File Name',
    dataType: 'text',
    class: 'field col-12 mb-2 flex align-items-center',
    headerClass: 'mr-6',
  },
])

const objExportToExcel = ref<GenericObject>({
  search: payload.value,
  fileName: '',
  exportSumary: true
})

const objExportToExcelTemp = ref<GenericObject>({
  search: payload.value,
  fileName: '',
  exportSumary: false
})

interface DataListItemEmployee {
  id: string
  firstName: string
  lastName: string
  email: string
}

interface ListItemEmployee {
  id: string
  name: string
  status: boolean | string
}

function mapFunctionEmployee(data: DataListItemEmployee): ListItemEmployee {
  return {
    id: data.id,
    name: `${data.firstName} ${data.lastName}`,
    status: 'Active'
  }
}

async function getEmployeeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  employeeList.value = await getDataList<DataListItemEmployee, ListItemEmployee>(moduleApi, uriApi, filter, queryObj, mapFunctionEmployee)
  const columnEmployee = historyColumns.value.find(item => item.field === 'employee')
  if (columnEmployee) {
    columnEmployee.localItems = [...JSON.parse(JSON.stringify(employeeList.value))]
  }
}

// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

function addAttachment(attachment: any) {
  attachmentList.value = [...attachmentList.value, attachment]
}

function updateAttachment(attachment: any) {
  const index = attachmentList.value.findIndex(item => item.id === attachment.id)
  attachmentList.value[index] = attachment
}

async function checkAttachment(code: string) {
  const payload = {
    payment: idPaymentSelectedForPrint.value || '',
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

function handleShareFilesDialogOpen() {
  shareFilesDialogOpen.value = true
}

function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCESSED': return '#FF8D00'
    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELED': return '#888888'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

function getStatusName(code: string) {
  switch (code) {
    case 'PROCESSED': return 'Processed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Cancelled'
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

    const payloadOfPaymentList = {
      ...payload.value,
    }
    localStorage.setItem('payloadOfPaymentList', JSON.stringify(payloadOfPaymentList))
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)
    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    interface ListColor {
      NONE: string
      OTHER: string
      SUPPORTED: string
      ATTACHMENT_WITH_ERROR: string
      ATTACHMENT_WITHOUT_ERROR: string
    }

    const listColor: ListColor = {
      NONE: '',
      OTHER: '#9d9d9d',
      SUPPORTED: '#0a0a0a',
      ATTACHMENT_WITH_ERROR: '#fd1600',
      ATTACHMENT_WITHOUT_ERROR: '#24d600',
    }
    let color = listColor.NONE

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'agency')) {
        iterator.agencyType = iterator.agency.agencyTypeResponse
        iterator.agency = {
          ...iterator.agency,
          name: `${iterator.agency.code} - ${iterator.agency.name}`
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'client')) {
        iterator.client = {
          ...iterator.client,
          name: `${iterator.client.code} - ${iterator.client.name}`
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel')) {
        iterator.hotel = {
          ...iterator.hotel,
          name: `${iterator.hotel.code} - ${iterator.hotel.name}`
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'bankAccount')) {
        if (iterator.bankAccount && iterator.bankAccount.id) {
          iterator.bankAccount = {
            id: iterator.bankAccount.id,
            name: `${iterator.bankAccount.accountNumber} - ${iterator.bankAccount.nameOfBank}`,
            accountNumber: `${iterator.bankAccount.accountNumber} - ${iterator.bankAccount.nameOfBank}`,
          }
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
      // if (Object.prototype.hasOwnProperty.call(iterator, 'identified')) {
      //   iterator.identified = formatNumber(iterator.identified)
      // }
      // if (Object.prototype.hasOwnProperty.call(iterator, 'notIdentified')) {
      //   iterator.notIdentified = formatNumber(iterator.notIdentified)
      // }
      if (Object.prototype.hasOwnProperty.call(iterator, 'bankAccount')) {
        iterator.bankAccount = {
          id: iterator?.bankAccount?.id,
          name: iterator?.bankAccount?.accountNumber
        }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = String(iterator.status)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'attachmentStatus')) {
        if (iterator.attachmentStatus?.code === 'PAT') {
          color = listColor.ATTACHMENT_WITHOUT_ERROR
        }
        else if (iterator.attachmentStatus?.code === 'PWA') {
          color = listColor.ATTACHMENT_WITH_ERROR
        }
        else if (iterator.attachmentStatus?.code === 'OTHER') {
          color = listColor.OTHER
        }
        else if (iterator.attachmentStatus?.code === 'SUP') {
          color = listColor.SUPPORTED
        }
        else {
          color = listColor.NONE
        }
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
        newListItems.push(
          {
            ...iterator,
            loadingEdit: false,
            loadingDelete: false,
            color,
          }
          // color: listColor[iterator.eattachment as keyof ListColor]
        ) // color: valores[Math.floor(Math.random() * valores.length)]
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

async function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criteria && filterToSearch.value.value) {
    let keyValue = ''
    const keyTemp = filterToSearch.value.criteria ? filterToSearch.value.criteria.id : ''
    if (keyTemp !== '' && keyTemp === 'id') {
      keyValue = 'paymentId'
    }
    payload.value.filter = [...payload.value.filter, {
      key: keyValue || (filterToSearch.value.criteria ? filterToSearch.value.criteria.id : ''),
      operator: (
        keyTemp === 'paymentDetails.remark'
        || keyTemp === 'paymentDetails.manageBooking.fullName'
        || keyTemp === 'paymentDetails.manageBooking.reservationNumber'
        || keyTemp === 'paymentDetails.manageBooking.couponNumber'
        || keyTemp === 'reference'
      )
        ? 'LIKE'
        : 'LIKE',
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
    if (filterToSearch.value.from && filterAllDateRange.value === false) {
      payload.value.filter = [...payload.value.filter, {
        key: filterToSearch.value.payApplied ? 'paymentDetails.transactionDate' : 'transactionDate',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }

    if (filterToSearch.value.to && filterAllDateRange.value === false) {
      payload.value.filter = [...payload.value.filter, {
        key: filterToSearch.value.payApplied ? 'paymentDetails.transactionDate' : 'transactionDate',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }

    // Pay Apply
    // if (filterToSearch.value.payApplied !== null) {
    //   payload.value.filter = [...payload.value.filter, {
    //     key: 'payApply',
    //     operator: 'EQUAL',
    //     value: filterToSearch.value.payApplied,
    //     logicalOperation: 'AND',
    //     type: 'filterSearch'
    //   }]
    // }

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
  options.value.selectAllItemByDefault = false
  await getList()
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  const objFilter = parseFilter?.find((item: IFilter) => item?.key === 'agencyType.id')
  if (objFilter) {
    objFilter.key = 'agency.agencyType.id'
  }

  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  await getList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'paymentStatus') {
      event.sortField = 'paymentStatus.name'
    }
    if (event.sortField === 'icon') {
      event.sortField = 'attachmentStatus.code'
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

async function parseDataTableFilterForChangeAgency(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsChangeAgency.value)
  payloadChangeAgency.value.filter = [...payloadChangeAgency.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payloadChangeAgency.value.filter = [...payloadChangeAgency.value.filter, ...parseFilter || []]
  await getAgencyByClient() // objClientFormChangeAgency.value.id
}

function onSortFieldForChangeAgency(event: any) {
  if (event) {
    payloadChangeAgency.value.sortBy = event.sortField
    payloadChangeAgency.value.sortType = event.sortOrder
    parseDataTableFilterForChangeAgency(event.filter)
  }
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
  description?: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  code?: string
  description?: string
}

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description
  }
}

interface DataListItemForStatus {
  id: string
  name: string
  code: string
  status: string
  description?: string
  applied: boolean
  confirmed: boolean
  cancelled: boolean
  transit: boolean
}

interface ListItemForStatus {
  id: string
  name: string
  status: boolean | string
  code?: string
  description?: string
  applied: boolean
  confirmed: boolean
  cancelled: boolean
  transit: boolean
}

function mapFunctionForStatus(data: DataListItemForStatus): DataListItemForStatus {
  return {
    id: data.id,
    name: `${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description,
    applied: data.applied,
    confirmed: data.confirmed,
    cancelled: data.cancelled,
    transit: data.transit
  }
}
const objLoading = ref({
  loadingAgency: false,
  loadingClient: false,
  loadingHotel: false,
  loadingStatus: false
})
async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[],) {
  try {
    objLoading.value.loadingClient = true
    let clientTemp: any[] = []
    clientItemsList.value = []
    listClientFormChangeAgency.value = []
    clientTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    clientItemsList.value = [...clientItemsList.value, ...clientTemp]
    listClientFormChangeAgency.value = [...listClientFormChangeAgency.value, ...clientTemp]
  }
  catch (error) {
    objLoading.value.loadingClient = false
  }
  finally {
    objLoading.value.loadingClient = false
  }
}
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingAgency = true
    let agencyTemp: any[] = []
    agencyItemsList.value = []
    agencyTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    agencyItemsList.value = [...agencyItemsList.value, ...agencyTemp]
  }
  catch (error) {
    objLoading.value.loadingAgency = false
  }
  finally {
    objLoading.value.loadingAgency = false
  }
}
// 1) Extiende el tipo de queryObj para que acepte page/pageSize
interface QueryParams {
  query: string
  keys: string[]
  page?: number
  pageSize?: number
}

async function getAgencyListTemp(
  moduleApi: string,
  uriApi: string,
  queryObj: QueryParams,
  filter?: FilterCriteria[]
) {
  const {
    query,
    keys,
    page = 0, // valor por defecto
    pageSize = 1000 // hasta 1000 filas
  } = queryObj

  return await getDataList<DataListItem, ListItem>(
    moduleApi,
    uriApi,
    filter,
    { query, keys },
    mapFunction,
    {
      page,
      pageSize,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }
  )
}

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingHotel = true
    let hotelTemp: any[] = []
    hotelItemsList.value = []
    hotelTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    hotelItemsList.value = [...hotelItemsList.value, ...hotelTemp]
  }
  catch (error) {
    objLoading.value.loadingHotel = false
  }
  finally {
    objLoading.value.loadingHotel = false
  }
}
async function getHotelListTemp(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}
async function getStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingStatus = true
    let statusTemp: any[] = []
    statusItemsList.value = []
    statusTemp = await getDataList<DataListItemForStatus, ListItemForStatus>(moduleApi, uriApi, filter, queryObj, mapFunctionForStatus, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    statusItemsList.value = [...statusItemsList.value, ...statusTemp]
  }
  catch (error) {
    objLoading.value.loadingStatus = false
  }
  finally {
    objLoading.value.loadingStatus = false
  }
}

function copiarDatosOtherDeductions() {
  copyTableToClipboard(applyPaymentColumnsOtherDeduction.value, applyPaymentListOfInvoiceOtherDeduction.value, toast)
}

function copiarDatosBatch() {
  copyTableToClipboard(applyPaymentColumnsOtherDeduction1.value, applyPaymentListOfInvoiceOtherDeduction.value, toast)
}
function exportarOtherDeductions() {
  exportDataToExcel(applyPaymentColumnsOtherDeduction1.value, applyPaymentListOfInvoiceOtherDeduction.value, 'other-deductions.xls')
}

function copiarDatos() {
  copyPaymentsToClipboardPayMang(columns, listItems.value, toast)
}

async function getAgencyByClient(clientId: string = '') {
  if (optionsOfTableChangeAgency.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    optionsOfTableChangeAgency.value.loading = true
    listAgencyByClient.value = []
    const newListItems = []

    const objFilterById = payloadChangeAgency.value.filter.find((item: FilterCriteria) => item.key === 'id')
    if (objFilterById) {
      objFilterById.value = currentAgencyForChangeAgency.value?.id
    }
    else {
      payloadChangeAgency.value.filter.push({
        key: 'id',
        operator: 'NOT_EQUALS',
        value: currentAgencyForChangeAgency.value?.id,
        logicalOperation: 'AND',
      })
    }

    if (clientId !== '') {
      const objFilterByClient = payloadChangeAgency.value.filter.find((item: FilterCriteria) => item.key === 'client.id')
      if (objFilterByClient) {
        objFilterByClient.value = clientId
      }
      else {
        payloadChangeAgency.value.filter.push({
          key: 'client.id',
          operator: 'EQUALS',
          value: clientId,
          logicalOperation: 'AND',
        })
      }
    }

    const objFilterByStatus = payloadChangeAgency.value.filter.find((item: FilterCriteria) => item.key === 'status')
    if (objFilterByStatus) {
      objFilterByStatus.value = 'ACTIVE'
    }
    else {
      payloadChangeAgency.value.filter.push({
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND',
      })
    }

    const response = await GenericService.search(optionsOfTableChangeAgency.value.moduleApi, optionsOfTableChangeAgency.value.uriApi, payloadChangeAgency.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationChangeAgency.value.page = page
    paginationChangeAgency.value.limit = size
    paginationChangeAgency.value.totalElements = totalElements
    paginationChangeAgency.value.totalPages = totalPages

    const existingIds = new Set(listAgencyByClient.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({
          id: iterator.id,
          name: `${iterator.name}`,
          code: `${iterator.code}`,
          description: `${iterator.description}`,
          status: statusToBoolean(iterator.status),
          client: iterator.client?.id
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listAgencyByClient.value = [...listAgencyByClient.value, ...newListItems]
  }
  catch (error) {
    optionsOfTableChangeAgency.value.loading = false
    console.error(error)
  }
  finally {
    optionsOfTableChangeAgency.value.loading = false
  }
}

async function applyPaymentGetList(): Promise<void> {
  if (applyPaymentOptions.value.loading) { return }

  try {
    applyPaymentOptions.value.loading = true
    applyPaymentPagination.value = { ...applyPaymentPaginationTemp.value }
    applyPaymentListOfInvoice.value = []
    const newListItems = []

    if (objItemSelectedForRightClickApplyPayment.value?.client?.status !== 'ACTIVE') { return }

    // Buscar agencias activas del cliente
    const agencies = await getAgencyListTemp(
      objApis.value.agency.moduleApi,
      objApis.value.agency.uriApi,
      {
        query: '',
        keys: ['name', 'code'],
        page: 0, // página inicial
        pageSize: 1000 // hasta 1 000 registros
      },
      [
        { key: 'client.id', logicalOperation: 'AND', operator: 'EQUALS', value: objItemSelectedForRightClickApplyPayment.value.client.id },
        { key: 'client.status', logicalOperation: 'AND', operator: 'EQUALS', value: 'ACTIVE' }
      ]
    )
    if (!agencies.length) { return }

    const hotel = objItemSelectedForRightClickApplyPayment.value.hotel
    const filters: FilterCriteria[] = [
      { key: 'agency.id', operator: 'IN', value: agencies.map(a => a.id), logicalOperation: 'AND' },
      { key: 'dueAmount', operator: 'GREATER_THAN', value: '0.00', logicalOperation: 'AND' },
      { key: 'manageInvoiceStatus.enabledToApply', operator: 'EQUALS', value: true, logicalOperation: 'AND' },
    ]
    if (hotel && hotel.id) {
      if (hotel.status === 'ACTIVE' && hotel.applyByTradingCompany) {
        // Buscar hoteles de la misma trading company
        const hotels = await getHotelListTemp(
          objApis.value.hotel.moduleApi,
          objApis.value.hotel.uriApi,
          { query: '', keys: ['name', 'code'] },
          [
            { key: 'manageTradingCompanies.id', logicalOperation: 'AND', operator: 'EQUALS', value: hotel.manageTradingCompany },
            { key: 'applyByTradingCompany', logicalOperation: 'AND', operator: 'EQUALS', value: true },
            { key: 'status', logicalOperation: 'AND', operator: 'EQUALS', value: 'ACTIVE' }
          ]
        )
        if (hotels.length) {
          filters.push({ key: 'hotel.id', operator: 'IN', value: hotels.map(h => h.id), logicalOperation: 'AND' })
        }
      }
      else {
        filters.push({ key: 'hotel.id', operator: 'EQUALS', value: hotel.id, logicalOperation: 'AND' })
      }
    }

    // Aplicar los filtros
    applyPaymentPayload.value.filter = filters
    const response = await GenericService.search(
      applyPaymentOptions.value.moduleApi,
      applyPaymentOptions.value.uriApi,
      applyPaymentPayload.value
    )

    const { data: dataList, page, size, totalElements, totalPages } = response

    applyPaymentPagination.value.page = page
    applyPaymentPagination.value.limit = size
    applyPaymentPagination.value.totalElements = totalElements
    applyPaymentPagination.value.totalPages = totalPages

    const existingIds = new Set(applyPaymentListOfInvoice.value.map(item => item.id))
    for (const iterator of dataList) {
      if (!existingIds.has(iterator.id)) {
        newListItems.push({
          ...iterator,
          invoiceNo: `${iterator.invoice?.manageInvoiceType?.code}-${iterator.invoice?.invoiceNo}`,
          invoiceId: iterator.invoiceId || '',
          invoiceAmountTemp: iterator.invoiceAmount || 0,
          dueAmountTemp: iterator.dueAmount || 0,
          bookingAmount: iterator.invoiceAmount ? formatNumber(iterator.invoiceAmount.toString()) : '0.00', // Monto de la factura
          bookingBalance: iterator.dueAmount ? formatNumber(iterator.dueAmount.toString()) : '0.00', // Balance de la factura
          loadingEdit: false,
          loadingDelete: false,
          loadingBookings: false,
          bookingsList: [],
          bookings: [],
        })
        existingIds.add(iterator.id)
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
        },
        {
          key: 'dueAmount',
          operator: 'GREATER_THAN',
          value: '0.00',
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
        // iterator.bookingAmount = iterator.invoiceAmount?.toString()
        // iterator.bookingBalance = iterator.dueAmount?.toString()
        iterator.bookingAmountTemp = iterator.invoiceAmount ? formatNumber(iterator.invoiceAmount.toString()) : 0
        iterator.bookingBalance = iterator.dueAmount ? formatNumber(iterator.dueAmount.toString()) : '0'

        // Verificar si el ID ya existe en la lista
        if (!existingIds.has(iterator.id)) {
          newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
          existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
        }
      }

      listBookingsForApplyPayment = [...listBookingsForApplyPayment, ...newListItems]
      objInvoice.bookings = [...listBookingsForApplyPayment]
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
  if (paymentDetailsTypeDepositLoading.value) { return }

  try {
    // Copiar paginación temporal (si la usas como en applyPaymentGetList)
    payloadpaymentDetailForTypeDepositPagination.value = {
      ...payloadpaymentDetailForTypeDepositPaginationTemp.value
    }

    paymentDetailsTypeDepositLoading.value = true
    paymentDetailsTypeDepositList.value = []
    const newListItems = []

    const filters = payloadpaymentDetailForTypeDeposit.value.filter

    const paymentId = objItemSelectedForRightClickApplyPayment.value?.id
    const filterByPaymentId = filters.find(f => f.key === 'payment.id')
    if (filterByPaymentId) {
      filterByPaymentId.value = paymentId
    }
    else {
      filters.push({
        key: 'payment.id',
        operator: 'EQUALS',
        value: paymentId,
        logicalOperation: 'AND'
      })
    }

    const depositFilter = filters.find(f => f.key === 'transactionType.deposit')
    if (depositFilter) {
      depositFilter.value = true
    }
    else {
      filters.push({
        key: 'transactionType.deposit',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'AND'
      })
    }

    const canceledFilter = filters.find(f => f.key === 'canceledTransaction')
    if (canceledFilter) {
      canceledFilter.value = false
    }
    else {
      filters.push({
        key: 'canceledTransaction',
        operator: 'EQUALS',
        value: false,
        logicalOperation: 'AND'
      })
    }

    // Agregar paginación al payload
    const payloadWithPagination = {
      ...payloadpaymentDetailForTypeDeposit.value,
      page: payloadpaymentDetailForTypeDepositPagination.value.page,
      size: payloadpaymentDetailForTypeDepositPagination.value.limit
    }

    const response = await GenericService.search('payment', 'payment-detail', payloadWithPagination)

    const {
      data: dataList,
      page,
      size,
      totalElements,
      totalPages
    } = response

    payloadpaymentDetailForTypeDepositPagination.value.page = page
    payloadpaymentDetailForTypeDepositPagination.value.limit = size
    payloadpaymentDetailForTypeDepositPagination.value.totalElements = totalElements
    payloadpaymentDetailForTypeDepositPagination.value.totalPages = totalPages

    const existingIds = new Set(paymentDetailsTypeDepositList.value.map(item => item.id))

    for (const item of dataList) {
      if ('amount' in item) {
        item.amount = (!Number.isNaN(item.amount) && item.amount !== null && item.amount !== '')
          ? Number.parseFloat(item.amount).toString()
          : '0'
        item.amount = formatNumber(item.amount)
      }

      if ('status' in item) {
        item.status = statusToBoolean(item.status)
      }

      if ('transactionDate' in item) {
        item.transactionDate = item.transactionDate ? dayjs(item.transactionDate).format('YYYY-MM-DD') : null
      }

      if ('parentId' in item) {
        item.parentId = item.parentId?.toString() || ''
      }

      if ('transactionType' in item) {
        item.transactionType = `${item.transactionType.code} - ${item.transactionType.name}`
        if (item.deposit) {
          item.rowClass = 'row-deposit'
        }
      }

      if ('manageBooking' in item) {
        item.adults = item.manageBooking?.adults?.toString()
        item.children = item.manageBooking?.children?.toString()
        item.couponNumber = item.manageBooking?.couponNumber?.toString()
        item.fullName = item.manageBooking?.fullName
        item.reservationNumber = item.manageBooking?.reservationNumber?.toString()
        item.bookingId = item.manageBooking?.bookingId?.toString()
        item.invoiceNumber = item.manageBooking?.invoice?.invoiceNumber?.toString()
      }

      if (!existingIds.has(item.id)) {
        newListItems.push({ ...item })
        existingIds.add(item.id)
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

function removeDuplicateFilters(request: IQueryRequest): IQueryRequest {
  const uniqueFilters = request.filter.reduce((acc: IFilter[], current: IFilter) => {
    const isDuplicate = acc.some(item =>
      item.key === current.key
      && item.operator === current.operator
      && JSON.stringify(item.value) === JSON.stringify(current.value)
      && item.logicalOperation === current.logicalOperation
    )

    if (!isDuplicate) {
      acc.push(current)
    }
    return acc
  }, [])

  return {
    ...request,
    filter: uniqueFilters
  }
}

async function applyPaymentGetListForOtherDeductions() {
  if (applyPaymentOptionsOtherDeduction.value.loading) { return }

  applyPaymentOptionsOtherDeduction.value.loading = true
  applyPaymentOptionsOtherDeduction1.value.loading = true
  applyPaymentListOfInvoiceOtherDeduction.value = []
  const newListItems: typeof applyPaymentListOfInvoiceOtherDeduction.value = []

  try {
    const paymentCtx = objItemSelectedForRightClickApplyPaymentOtherDeduction.value
    if (paymentCtx?.client.status !== 'ACTIVE') { return }

    const filters: FilterCriteria[] = []

    // solo se agrega si no se marcó loadAllInvoices
    if (!loadAllInvoices.value && paymentCtx.id) {
      await applyPaymentGetListForOtherDeductionsPayment(paymentCtx.id)
    }
    else {
      filters.push({
        key: 'dueAmount',
        operator: 'GREATER_THAN',
        value: '0.00',
        logicalOperation: 'AND'
      })

      filters.push({
        key: 'invoice.manageInvoiceStatus.enabledToApply',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'AND'
      })

      const agencyQuery = { query: '', keys: ['name', 'code'] }
      const agencyFilter: FilterCriteria[] = [
        { key: 'client.id', operator: 'EQUALS', value: paymentCtx.client.id, logicalOperation: 'AND' },
        { key: 'client.status', operator: 'EQUALS', value: 'ACTIVE', logicalOperation: 'AND' },
        { key: 'status', operator: 'EQUALS', value: 'ACTIVE', logicalOperation: 'AND' }
      ]

      const agencies = await getAgencyListTemp(
        objApis.value.agency.moduleApi,
        objApis.value.agency.uriApi,
        agencyQuery,
        agencyFilter
      )

      if (!agencies.length) { return }

      filters.push({
        key: 'invoice.agency.id',
        operator: 'IN',
        value: agencies.map(a => a.id),
        logicalOperation: 'AND'
      })

      const hotel = paymentCtx.hotel
      if (hotel?.id) {
        const hotelQuery = { query: '', keys: ['name', 'code'] }
        let hotelIds: string[] = []

        if (hotel.status === 'ACTIVE' && hotel.applyByTradingCompany) {
          const tradingFilter: FilterCriteria[] = [
            { key: 'manageTradingCompanies.id', operator: 'EQUALS', value: hotel.manageTradingCompany, logicalOperation: 'AND' },
            { key: 'applyByTradingCompany', operator: 'EQUALS', value: true, logicalOperation: 'AND' },
            { key: 'status', operator: 'EQUALS', value: 'ACTIVE', logicalOperation: 'AND' }
          ]

          const hotels = await getHotelListTemp(
            objApis.value.hotel.moduleApi,
            objApis.value.hotel.uriApi,
            hotelQuery,
            tradingFilter
          )

          hotelIds = hotels.length ? hotels.map(h => h.id) : [hotel.id]
        }
        else {
          hotelIds = [hotel.id]
        }

        filters.push({
          key: 'invoice.hotel.id',
          operator: hotelIds.length > 1 ? 'IN' : 'EQUALS',
          value: hotelIds.length > 1 ? hotelIds : hotelIds[0],
          logicalOperation: 'AND'
        })
      }

      applyPaymentPayloadOtherDeduction.value.filter = removeDuplicateFilters({ filter: filters } as IQueryRequest).filter

      const response = await GenericService.search(
        'invoicing',
        'manage-booking',
        applyPaymentPayloadOtherDeduction.value
      )

      const { data: dataList, page, size, totalElements, totalPages } = response

      Object.assign(applyPaymentPaginationOtherDeduction.value, { page, limit: size, totalElements, totalPages })

      const existing = new Set(applyPaymentListOfInvoiceOtherDeduction.value.map(i => i.id))
      dataList.forEach((item) => {
        if (!existing.has(item.id)) {
          const record = {
            ...item,
            invoiceId: item.invoice?.invoiceId.toString(),
            invoiceNumber: item.invoice?.invoiceNumberPrefix.toString(),
            checkIn: item.checkIn ? dayjs(item.checkIn).format('YYYY-MM-DD') : null,
            checkOut: item.checkOut ? dayjs(item.checkOut).format('YYYY-MM-DD') : null,
            dueAmountTemp: item.dueAmount || 0,
            bookingAmountTemp: item.invoiceAmount || 0,
            paymentId: paymentCtx.paymentId,
            loadingEdit: false,
            loadingDelete: false,
            loadingBookings: false
          }
          newListItems.push(record)
          existing.add(item.id)
        }
      })

      applyPaymentListOfInvoiceOtherDeduction.value = newListItems
    }
  }
  catch (error) {
    console.error('applyPaymentOtherDeduction error:', error)
  }
  finally {
    applyPaymentOptionsOtherDeduction.value.loading = false
    applyPaymentOptionsOtherDeduction1.value.loading = false
  }
}

async function applyPaymentGetListForOtherDeductionsPayment(id: any) {
  try {
    const newListItems: typeof applyPaymentListOfInvoiceOtherDeduction.value = []
    const filters: FilterCriteria[] = []

    filters.push({
      key: 'payment.id',
      operator: 'EQUALS',
      value: id,
      logicalOperation: 'AND'
    })

    filters.push({
      key: 'manageBooking',
      operator: 'IS_NOT_NULL',
      value: '',
      logicalOperation: 'AND'
    })

    applyPaymentPayloadOtherDeductionPayment.value.filter = removeDuplicateFilters({ filter: filters } as IQueryRequest).filter

    const response = await GenericService.search(
      'payment',
      'payment-detail',
      applyPaymentPayloadOtherDeductionPayment.value
    )
    const { data: dataList, page, size, totalElements, totalPages } = response
    Object.assign(applyPaymentPaginationOtherDeduction.value, { page, limit: size, totalElements, totalPages })

    const existing = new Set(applyPaymentListOfInvoiceOtherDeduction.value.map(i => i.id))
    dataList.forEach((item) => {
      if (!existing.has(item.manageBooking.id)) {
        if (item.manageBooking) {
          const record = {
            ...item.manageBooking,
            invoiceId: item.manageBooking.invoice?.invoiceId.toString(),
            invoiceNumber: item.manageBooking.invoice?.invoiceNumber.toString(),
            checkIn: item.manageBooking.checkIn ? dayjs(item.manageBooking.checkIn).format('YYYY-MM-DD') : null,
            checkOut: item.manageBooking.checkOut ? dayjs(item.manageBooking.checkOut).format('YYYY-MM-DD') : null,
            dueAmountTemp: item.manageBooking.amountBalance || 0,
            bookingAmountTemp: item.manageBooking.amountBalance || 0,
            hotelBookingNumber: item.manageBooking.reservationNumber,
            paymentId: id,
            loadingEdit: false,
            loadingDelete: false,
            loadingBookings: false
          }
          newListItems.push(record)
          existing.add(item.manageBooking.id)
        }
      }
    })

    applyPaymentListOfInvoiceOtherDeduction.value = newListItems
  }
  catch (error) {
    console.error('applyPaymentOtherDeduction error:', error)
  }
  finally {
    applyPaymentOptionsOtherDeduction.value.loading = false
    applyPaymentOptionsOtherDeduction1.value.loading = false
  }
}

function closeModalApplyPayment() {
  objItemSelectedForRightClickApplyPayment.value = {}
  openDialogApplyPayment.value = false
  disabledBtnApplyPayment.value = true
  idInvoicesSelectedToApplyPayment.value = []
  paymentDetailsTypeDepositSelected.value = []
  invoiceAmmountSelected.value = 0
  applyPaymentPayload.value.filter = []
}

function closeModalApplyPaymentOtherDeductions() {
  objItemSelectedForRightClickApplyPaymentOtherDeduction.value = {}
  openDialogApplyPaymentOtherDeduction.value = false
  disabledBtnApplyPaymentOtherDeduction.value = true
  allInvoiceCheckIsChecked.value = false
  loadAllInvoices.value = false
  idInvoicesSelectedToApplyPaymentForOtherDeduction.value = []
  applyPaymentOnChangePageOtherDeduction.value = undefined
  applyPaymentPayloadOtherDeduction.value.filter = []
}

function closeModalCopyBatch() {
  objItemSelectedForRightClickApplyPaymentOtherDeduction.value = {}
  openDialogCopyBatch.value = false
  disabledBtnApplyPaymentOtherDeduction.value = true
  allInvoiceCheckIsChecked.value = false
  loadAllInvoices.value = false
  idInvoicesSelectedToApplyPaymentForOtherDeduction.value = []
  applyPaymentOnChangePageOtherDeduction1.value = undefined
  applyPaymentPayloadOtherDeduction.value.filter = []
}
const manualFilter = ref('')

async function openModalApplyPayment() {
  manualFilter.value = ''
  paymentDetailsTypeDepositSelected.value = []
  openDialogApplyPayment.value = true
  invoiceAmmountSelected.value = 0
  paymentAmmountSelected.value = objItemSelectedForRightClickApplyPayment.value.paymentBalance
  paymentBalance.value = objItemSelectedForRightClickApplyPayment.value.paymentBalance
  applyPaymentOnChangePage.value = undefined
  checkApplyPayment.value = false
  applyPaymentGetList()
  getListPaymentDetailTypeDeposit()
}

async function openModalApplyPaymentOtherDeduction() {
  openDialogApplyPaymentOtherDeduction.value = true
  paymentAmmountSelected.value = objItemSelectedForRightClickApplyPaymentOtherDeduction.value.paymentBalance
  paymentBalance.value = objItemSelectedForRightClickApplyPaymentOtherDeduction.value.paymentBalance
  transactionType.value = {}
  fieldRemark.value = ''
  loadAllInvoices.value = false
  applyPaymentGetListForOtherDeductions()
}

async function openModalCopyBatch() {
  openDialogCopyBatch.value = true
  paymentAmmountSelected.value = objItemSelectedForRightClickApplyPaymentOtherDeduction.value.paymentBalance
  paymentBalance.value = objItemSelectedForRightClickApplyPaymentOtherDeduction.value.paymentBalance
  transactionType.value = {}
  fieldRemark.value = ''
  loadAllInvoices.value = false
  applyPaymentGetListForOtherDeductions()
}

async function openModalApplyChangeAgency() {
  openDialogChangeAgency.value = true
  getAgencyByClient() // objClientFormChangeAgency.value.id
}

function closeModalChangeAgency() {
  openDialogChangeAgency.value = false
  listAgencyByClient.value = []
  objClientFormChangeAgency.value = {}
}

async function onExpandRowApplyPayment(event: any) {
  await applyPaymentBookingGetList(event)
}

function onRowContextMenu(event: any) {
  idPaymentSelectedForPrint.value = event?.data?.id || ''
  isPrintByRightClick.value = true
  idPaymentSelectedForPrintChangeAgency.value = event?.data?.id || ''
  objClientFormChangeAgency.value = event?.data?.client
  currentAgencyForChangeAgency.value = event?.data?.agency
  listClientFormChangeAgency.value = event?.data?.client ? [event?.data?.client] : []
  objItemSelectedForRightClickChangeAgency.value = event.data

  if (event && event.data) {
    paymentSelectedForAttachment.value = event.data
    objItemSelectedForRightClickApplyPaymentOtherDeduction.value = event.data
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

  const selectedPaymentItem = allMenuListItems.value.find(item => item.id === 'paymentSelected')
  if (selectedPaymentItem) {
    selectedPaymentItem.label = `Selected Payment: ${event.data.paymentId}`
  }

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

  if (event && event.data && event.data?.attachmentStatus && (event.data?.attachmentStatus?.supported === false || event.data.attachmentStatus.nonNone) && (event.data.attachmentStatus.pwaWithOutAttachment === false && event.data.attachmentStatus.patWithAttachment === false)) {
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
    if (event && event.data && event.data?.attachmentStatus?.supported === false && event.data.attachmentStatus.pwaWithOutAttachment) {
      const menuItemPaymentWithAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithAttachment')
      if (menuItemPaymentWithAttachment) {
        menuItemPaymentWithAttachment.disabled = false
        menuItemPaymentWithAttachment.visible = true
      }
      const menuItemPaymentWithOutAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithoutAttachment')
      if (menuItemPaymentWithOutAttachment) {
        menuItemPaymentWithOutAttachment.disabled = true
        menuItemPaymentWithOutAttachment.visible = false
      }
    }
    else if (event && event.data && event.data?.attachmentStatus?.supported === false && event.data.attachmentStatus.patWithAttachment) {
      const menuItemPaymentWithOutAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithoutAttachment')
      if (menuItemPaymentWithOutAttachment) {
        menuItemPaymentWithOutAttachment.disabled = false
        menuItemPaymentWithOutAttachment.visible = true
      }
      const menuItemPaymentWithAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithAttachment')
      if (menuItemPaymentWithAttachment) {
        menuItemPaymentWithAttachment.disabled = true
        menuItemPaymentWithAttachment.visible = false
      }
    }
    else if (event && event.data && event.data?.attachmentStatus?.supported === true) {
      const menuItemPaymentWithAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithAttachment')
      if (menuItemPaymentWithAttachment) {
        menuItemPaymentWithAttachment.disabled = true
        menuItemPaymentWithAttachment.visible = false
      }
      const menuItemPaymentWithOutAttachment = allMenuListItems.value.find(item => item.id === 'paymentWithoutAttachment')
      if (menuItemPaymentWithOutAttachment) {
        menuItemPaymentWithOutAttachment.disabled = true
        menuItemPaymentWithOutAttachment.visible = false
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

async function onRowDoubleClickInDataTableForChangeAgency(event: any) {
  if (optionsOfTableChangeAgency.value.loading) { return }
  try {
    optionsOfTableChangeAgency.value.loading = true
    const payloadToApplyPayment: GenericObject = {
      payment: objItemSelectedForRightClickChangeAgency.value.id || '',
      transactionDate: objItemSelectedForRightClickChangeAgency.value.transactionDate,
      reference: objItemSelectedForRightClickChangeAgency.value.reference,
      remark: objItemSelectedForRightClickChangeAgency.value.remark,
      paymentSource: objItemSelectedForRightClickChangeAgency.value.paymentSource?.id || '',
      paymentStatus: objItemSelectedForRightClickChangeAgency.value.paymentStatus?.id || '',
      client: event.client || '',
      agency: event?.id,
      hotel: objItemSelectedForRightClickChangeAgency.value.hotel?.id || '',
      bankAccount: objItemSelectedForRightClickChangeAgency.value.bankAccount?.id || '',
      attachmentStatus: objItemSelectedForRightClickChangeAgency.value.attachmentStatus?.id || '',
    }

    await GenericService.update(options.value.moduleApi, options.value.uriApi, objItemSelectedForRightClickChangeAgency.value.id || '', payloadToApplyPayment)
    openDialogApplyPayment.value = false
    toast.add({ severity: 'success', summary: 'Successful', detail: 'The agency has been changed successfully', life: 3000 })
    openDialogChangeAgency.value = false
  }
  catch (error: any) {
    openDialogApplyPayment.value = false
    toast.add({ severity: 'error', summary: 'Error', detail: error?.data?.data?.error?.errors[0]?.message ? error?.data?.data?.error?.errors[0]?.message : 'The agency could not be changed', life: 3000 })
  }
  finally {
    optionsOfTableChangeAgency.value.loading = false
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
        return total
      }
    }, 0) // 0 es el valor inicial
  disabledBtnApplyPayment.value = false
}

async function selectRowsOfInvoiceOfOtherDeduction(event: any) {
  idInvoicesSelectedToApplyPaymentForOtherDeduction.value = event
  const selectedIds = event
  if (selectedIds.length === 0) {
    disabledBtnApplyPaymentOtherDeduction.value = true
    return
  }
  disabledBtnApplyPaymentOtherDeduction.value = false
}

async function onCellEditCompleteApplyPaymentOtherDeduction(event: any) {
  const { data, newValue, field } = event
  switch (field) {
    case 'dueAmountTemp': {
      let valueTemp = 0

      if (typeof newValue === 'string') {
        valueTemp = Number.parseFloat(newValue.replace(/,/g, ''))

        if (valueTemp > 0 && valueTemp <= data.dueAmount) {
          data[field] = valueTemp
        }
        else {
          toast.add({ severity: 'error', summary: 'Error', detail: `The amount must be greater than 0 and less than or equal to ${formatNumber(data.dueAmount)}`, life: 4000 })
        }
      }
      else if (typeof newValue === 'number') {
        if (newValue > 0 && newValue <= data.dueAmount) {
          data[field] = newValue
        }
        else {
          toast.add({ severity: 'error', summary: 'Error', detail: `The amount must be greater than 0 and less than or equal to ${formatNumber(data.dueAmount)}`, life: 4000 })
        }
      }
    }
      break
  }
  // data[field] = newValue
}

function sumAmountOfPaymentDetailTypeDeposit(transactions: any[]) {
  if (!transactions || transactions.length === 0) {
    return 0 // Si el array no tiene valores, devolver 0
  }

  return transactions.reduce((total, item) => {
    let applyDepositValue = 0
    if (typeof item.applyDepositValue === 'number') {
      applyDepositValue = Number.parseFloat(item.applyDepositValue)
    }
    else if (typeof item.applyDepositValue === 'string') {
      applyDepositValue = Number.parseFloat(item.applyDepositValue.replace(/,/g, ''))
    }

    // Verificar si el amount es un número válido y sumarlo
    if (!Number.isNaN(applyDepositValue)) {
      return total + Math.abs(applyDepositValue) // Convertir a positivo y sumar
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
      invoices: [...idInvoicesSelectedToApplyPayment.value], // este ya es un array de ids
      deposits: [...paymentDetailsTypeDepositSelected.value.map(item => item.id)], // Convertir a ids[]
      applyDeposit: paymentDetailsTypeDepositSelected.value.length > 0,
      applyPaymentBalance: checkApplyPayment.value,
      employee: userData?.value?.user?.userId || ''
    }

    const response = await GenericService.create('payment', 'payment/apply-payment', payload)

    if (response) {
      loadingSaveApplyPayment.value = false
      openDialogApplyPayment.value = false
      disabledBtnApplyPayment.value = true
      objItemSelectedForRightClickApplyPayment.value = {}
      idInvoicesSelectedToApplyPayment.value = []
      paymentDetailsTypeDepositSelected.value = []
      toast.add({ severity: 'success', summary: 'Successful', detail: 'Payment has been applied successfully', life: 3000 })
      getList()
    }
  }
  catch (error: any) {
    // objItemSelectedForRightClickApplyPayment.value = {}
    // idInvoicesSelectedToApplyPayment.value = []
    // paymentDetailsTypeDepositSelected.value = []
    loadingSaveApplyPayment.value = false
    if (error.data.data.error.status === 1179) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'The invoice not found, please try again in a few minutes o refresh the page', life: 6000 })
    }
  }
}

async function saveApplyPaymentOtherDeduction() {
  if (loadingSaveApplyPayment.value === true) { return }
  try {
    if (validateForm()) {
      let listTemp: any[] = []
      listTemp = applyPaymentListOfInvoiceOtherDeduction.value
        .filter(item => idInvoicesSelectedToApplyPaymentForOtherDeduction.value.includes(item.id)).map((item) => {
          return {
            bookingId: item.id,
            bookingBalance: typeof item.dueAmountTemp !== 'number' ? Number.parseFloat(item.dueAmountTemp.replace(/,/g, '')) : item.dueAmountTemp
          }
        })

      loadingSaveApplyPayment.value = true
      const payload = {
        payment: objItemSelectedForRightClickApplyPaymentOtherDeduction.value.id || '',
        booking: [...listTemp], // este ya es un array de ids
        transactionType: transactionType.value?.id || '',
        remark: fieldRemark.value?.trim() || transactionType.value?.defaultRemark?.trim() || '',
        employee: userData?.value?.user?.userId || ''
      }

      await GenericService.create('payment', 'payment-detail/apply-other-deductions', payload)
      toast.add({ severity: 'success', summary: 'Successful', detail: 'Payment Other Deduction has been applied successfully', life: 3000 })
      closeModalApplyPaymentOtherDeductions()
      getList()
    }
  }
  catch (error) {
    loadingSaveApplyPayment.value = false
    console.log(error)
  }
  finally {
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
      for (const childMenu of rootMenu.menuItems) {
        if (childMenu?.permission && childMenu?.permission?.length > 0) {
          (status.value === 'authenticated' && (isAdmin || authStore.can(childMenu.permission))) ? childMenu.disabled = false : childMenu.disabled = true
        }
      }
    }
  }
}

async function exportToExcel() {
  try {
    if (paymentSelectedForPrintList.value.length > 0) {
      const response = await GenericService.create('payment', `payment/excel-exporter/export-summary`, { paymentIds: [...paymentSelectedForPrintList.value] })
    }
  }
  catch (error) {
    console.log(error)
  }
}

function assingFunctionsToExportMenuInItemMenuList() {
  const itemsExportToExcel: any = itemMenuList.value.find(item => item.id === 'export')
  if (itemsExportToExcel) {
    itemsExportToExcel.btnOnClick = function () {
      openDialogExportToExcel()
    }
    // itemsExportToExcel.menuItems = itemsExportToExcel.menuItems.map((item: any) => {
    //   const objExportHierarchy = item.items.find((obj: any) => obj.id === 'export-hierarchy')
    //   if (objExportHierarchy) {
    //     objExportHierarchy.command = function () {
    //     }
    //   }

    //   const objExportSummary = item.items.find((obj: any) => obj.id === 'export-summary')
    //   if (objExportSummary) {
    //     objExportSummary.command = function () {
    //       openDialogExportToExcel()
    //     }
    //   }

    //   const objExportInvoice = item.items.find((obj: any) => obj.id === 'export-invoice')
    //   if (objExportInvoice) {
    //     objExportInvoice.command = function () {
    //       console.log('Esto es una prueba')
    //     }
    //   }
    //   return item
    // })
  }
}
// paymentSelectedForPrintList
async function paymentPrint(event: any) {
  try {
    loadingPrintDetail.value = true
    let nameOfPdf = ''
    let paymentTypeArray: string[] = []
    const payloadTemp: { paymentId: string[], paymentType: string[] } = {
      paymentId: isPrintByRightClick.value ? [idPaymentSelectedForPrint.value] : paymentSelectedForPrintList.value,
      paymentType: [],
    }
    // En caso de que solo este marcado el paymentAndDetails
    if (event && event.paymentAndDetails) {
      paymentTypeArray = [...paymentTypeArray, 'PAYMENT_DETAILS']
    }
    if (event && event.paymentSupport) {
      paymentTypeArray = [...paymentTypeArray, 'PAYMENT_SUPPORT']
    }
    if (event && event.allPaymentsSupport) {
      paymentTypeArray = [...paymentTypeArray, 'ALL_SUPPORT']
    }
    if (event && event.invoiceRelated) {
      paymentTypeArray = [...paymentTypeArray, 'INVOICE_RELATED']
    }
    if (event && event.invoiceRelatedWithSupport) {
      paymentTypeArray = [...paymentTypeArray, 'INVOICE_RELATED_SUPPORT']
    }

    nameOfPdf = `payment-list-${dayjs().format('YYYY-MM-DD')}.pdf`
    payloadTemp.paymentType = paymentTypeArray
    const response: any = await GenericService.create(confApiPaymentDetailPrint.moduleApi, confApiPaymentDetailPrint.uriApi, payloadTemp)

    const url = window.URL.createObjectURL(new Blob([response]))
    const a = document.createElement('a')
    a.href = url
    a.download = nameOfPdf
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
    loadingPrintDetail.value = false
  }
  catch (error) {
    loadingPrintDetail.value = false
    toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was failed', life: 3000 })
  }
  finally {
    loadingPrintDetail.value = false
    openPrint.value = false
  }
}

async function openDialogPrint() {
  itemPrint.value = JSON.parse(JSON.stringify(itemTempPrint.value))
  openPrint.value = true
}

async function closeDialogPrint() {
  idPaymentSelectedForPrint.value = ''
  // paymentSelectedForPrintList.value = []
  itemPrint.value = JSON.parse(JSON.stringify(itemTempPrint.value))
  openPrint.value = false
  isPrintByRightClick.value = false
}

async function paymentExportToExcel(event: any) {
  try {
    loadingExportToExcel.value = true
    let nameOfPdf = ''
    const payloadTemp: { fileName: string, search: IQueryRequest } = {
      fileName: event.fileName,
      search: payload.value
    }

    const response: any = await GenericService.create(confApiPaymentExportToExcel.moduleApi, confApiPaymentExportToExcel.uriApi, payloadTemp)

    // Asignar el nombre al archivo Excel
    nameOfPdf = event.fileName ? event.fileName : `export-summary-${dayjs().format('YYYY-MM-DD')}-excel.xlsx`

    // Convertir Base64 a Blob
    const byteCharacters = atob(response.excel) // Decodifica la cadena Base64
    const byteNumbers = Array.from({ length: byteCharacters.length }).fill(null).map((_, i) => byteCharacters.charCodeAt(i))
    const byteArray = new Uint8Array(byteNumbers)
    const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })

    // Crear URL y descargar el archivo
    const url = window.URL.createObjectURL(blob)

    if (url) {
      const a = document.createElement('a')
      a.href = url
      a.download = nameOfPdf
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
    }

    loadingExportToExcel.value = false
  }
  catch (error) {
    loadingExportToExcel.value = false
    toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was failed', life: 3000 })
  }
  finally {
    loadingExportToExcel.value = false
    openModalExportToExcel.value = false
  }
}

async function openDialogExportToExcel() {
  openModalExportToExcel.value = true
}

async function closeDialogExportToExcel() {
  openModalExportToExcel.value = false
}

function assingFuntionsForPrint(itemId: any) {
  if (itemId && itemId.length > 0) {
    idPaymentSelectedForPrint.value = itemId[0]
    paymentSelectedForPrintList.value = itemId
    const itemMenuObj = itemMenuList.value.find(item => item.id === 'print')
    if (itemMenuObj) {
      itemMenuObj.btnDisabled = false
      itemMenuObj.btnOnClick = () => {
        isPrintByRightClick.value = false
        openDialogPrint()
      }
    }
  }
  else {
    idPaymentSelectedForPrint.value = ''
    paymentSelectedForPrintList.value = []
    const itemMenuObj = itemMenuList.value.find(item => item.id === 'print')
    if (itemMenuObj) {
      itemMenuObj.btnDisabled = true
      itemMenuObj.btnOnClick = () => {}
    }
  }
}

async function historyGetList() {
  if (historyOptions.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    historyOptions.value.loading = true
    historyList.value = []
    const newListItems = []
    const objFilter = payloadHistory.value.filter.find(item => item.key === 'payment.id')

    if (objFilter) {
      objFilter.value = idPaymentSelectedForPrint.value.toString() || ''
    }
    else {
      payloadHistory.value.filter.push({
        key: 'payment.id',
        operator: 'EQUALS',
        value: idPaymentSelectedForPrint.value || '',
        logicalOperation: 'AND'
      })
    }

    const response = await GenericService.search(historyOptions.value.moduleApi, historyOptions.value.uriApi, payloadHistory.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    historyPagination.value.page = page
    historyPagination.value.limit = size
    historyPagination.value.totalElements = totalElements
    historyPagination.value.totalPages = totalPages

    const existingIds = new Set(historyList.value.map(item => item.id))

    for (const iterator of dataList) {
      iterator.paymentId = iterator.payment?.paymentId.toString()
      // iterator.paymentStatus = iterator.status

      if (Object.prototype.hasOwnProperty.call(iterator, 'employee')) {
        if (iterator?.employee?.firstName && iterator?.employee?.lastName) {
          iterator.employee = { id: iterator.employee?.id, name: `${iterator.employee?.firstName} ${iterator.employee?.lastName}` }
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        if (iterator?.status && iterator?.status?.includes('-')) {
          iterator.paymentStatus = iterator?.status?.split('-')[1]
        }
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    historyList.value = [...historyList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    historyOptions.value.loading = false
  }
}

async function openDialogStatusHistory(event = null) {
  openDialogHistory.value = true
  await historyGetList()
}

function historyOnSortField(event: any) {
  if (event) {
    if (event.sortField === 'attachmentType') {
      event.sortField = 'attachmentType.name'
    }
    if (event.sortField === 'resourceType') {
      event.sortField = 'resourceType.name'
    }
    payloadHistory.value.sortBy = event.sortField
    payloadHistory.value.sortType = event.sortOrder
    historyParseDataTableFilter(event.filter)
  }
}

async function historyParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, historyColumns.value)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      if (parseFilter[i]?.key === 'paymentStatus') {
        parseFilter[i].key = 'status'
      }
      if (parseFilter[i]?.key === 'employee') {
        parseFilter[i].key = 'employee.id'
      }
    }
  }
  payloadHistory.value.filter = [...parseFilter || []]
  historyGetList()
}

interface DataListItemForOtherDeduction {
  id: string
  code: string
  status: string | boolean
  description: string
  name: string
  cash: boolean
  agencyRateAmount: boolean
  negative: boolean
  policyCredit: boolean
  remarkRequired: boolean
  minNumberOfCharacter: number
  defaultRemark: string
  deposit: boolean
  applyDeposit: boolean
  defaults: boolean
}

interface ListItemForOtherDeduction {
  id: string
  code: string
  status: string | boolean
  description: string
  name: string
  cash: boolean
  agencyRateAmount: boolean
  negative: boolean
  policyCredit: boolean
  remarkRequired: boolean
  minNumberOfCharacter: number
  defaultRemark: string
  deposit: boolean
  applyDeposit: boolean
  default: boolean
}
function mapFunctionForTransactionType(data: DataListItemForOtherDeduction): ListItemForOtherDeduction {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    code: data.code,
    description: data.description,
    minNumberOfCharacter: data.minNumberOfCharacter,
    defaultRemark: data.defaultRemark,
    deposit: data.deposit,
    applyDeposit: data.applyDeposit,
    agencyRateAmount: data.agencyRateAmount,
    negative: data.negative,
    policyCredit: data.policyCredit,
    status: data.status,
    default: data.defaults,
    cash: data.cash,
    remarkRequired: data.remarkRequired

  }
}

async function getTransactionTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[], short?: IQueryToSort) {
  transactionTypeList.value = await getDataList<DataListItemForOtherDeduction, ListItemForOtherDeduction>(moduleApi, uriApi, filter, queryObj, mapFunctionForTransactionType, short)
}

function isRowSelectable(rowData: any) {
  return rowData.applyDepositValue > 0
}

function onRowSelectAll(event: any) {
  paymentDetailsTypeDepositSelected.value = event.filter(item => item.applyDepositValue > 0)
}

const fields: Array<FieldDefinitionType> = [
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    validation: z.string().trim().max(255, 'Maximum 255 characters')
  },
]

const errors = ref<{ [key: string]: string[] }>({})

async function validateField(fieldKey: string, value: any) {
  const field = fields.find(f => f.field === fieldKey)
  if (field && field.validation) {
    const result = field.validation.safeParse(value)
    if (!result.success) {
      errors.value[fieldKey] = result && result.error ? result.error.issues.map(e => e.message) : []
    }
    else {
      delete errors.value[fieldKey]
    }
  }
}

function validateForm() {
  let isValid = true
  fields.forEach((field) => {
    if (field.validation) {
      const result = field.validation.safeParse(fieldRemark.value)
      if (!result.success) {
        isValid = false
        errors.value[field.field] = result && result.error ? result.error.issues.map(e => e.message) : []
      }
      else {
        delete errors.value[field.field]
      }
    }
  })
  return isValid
}

async function processValidation($event: any) {
  errors.value = {}
  if ($event.remarkRequired === false) {
    const decimalSchema = z.object(
      {
        remark: z
          .string().trim()
          .max(255, 'Maximum 255 characters')
      }
    )
    updateFieldProperty(fields, 'remark', 'validation', decimalSchema.shape.remark)
  }
  if ($event.remarkRequired === true) {
    const decimalSchema = z.object(
      {
        remark: z
          .string().trim()
          .min($event.minNumberOfCharacter, { message: `The field "Remark" should have a minimum of ${$event.minNumberOfCharacter} characters.` })
          .max(255, 'Maximum 255 characters')
      }
    )
    updateFieldProperty(fields, 'remark', 'validation', decimalSchema.shape.remark)
  }
}

async function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value = JSON.parse(JSON.stringify(filterToSearchTemp.value))
  filterToSearch.value.criteria = ENUM_FILTER[0]
}

async function loadDefaultsConfig() {
  await clearFilterToSearch()

  startOfMonth.value = getMonthStartAndEnd(new Date()).startOfMonth
  endOfMonth.value = getMonthStartAndEnd(new Date()).endOfMonth
  filterToSearch.value.from = dayjs(startOfMonth.value).format('YYYY-MM-DD')
  filterToSearch.value.to = dayjs(endOfMonth.value).format('YYYY-MM-DD')
  filterToSearch.value.criteria = ENUM_FILTER[0]
  const objQueryToSearch = {
    query: '',
    keys: ['name', 'code'],
  }
  await getStatusList(objApis.value.status.moduleApi, objApis.value.status.uriApi, objQueryToSearch)
  filterToSearch.value.status = statusItemsList.value.filter((item: ListItemForStatus) => item.applied === true || item.confirmed === true)

  const filterForEmployee: FilterCriteria[] = [
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
    },
  ]
  await getEmployeeList('settings', 'manage-employee', {
    query: '',
    keys: ['name', 'code'],
  }, filterForEmployee)
  await searchAndFilter()
}

function showIconAttachment(objData: any) {
  if (objData.hasAttachment) {
    return true
  }
  else if (objData.attachmentStatus && objData.attachmentStatus.pwaWithOutAttachment) {
    return true
  }
  else if (objData.attachmentStatus && objData.attachmentStatus.patWithAttachment) {
    return true
  }
  return false
}

async function parseDataTableFilterForApplyPayment(event: any) {
  // 1) Averigua dónde viene la metadata de filtro
  const meta = event.filters ?? event.filter ?? {}

  // 2) Construye tu array de IFilter[] manualmente

  // 3) Asigna al payload y resetea página
  applyPaymentPayload.value.filter = parseFilter
  applyPaymentPayload.value.page = 0

  // 4) Recarga la lista
  await applyPaymentGetList()
}

const filteredInvoices = computed(() => {
  const terms = manualFilter.value.trim().toLowerCase().split(/\s+/)

  if (!terms.length || !terms[0]) {
    return applyPaymentListOfInvoice.value
  }

  return applyPaymentListOfInvoice.value.filter(inv =>
    terms.some((term) => {
      const fields = [
        inv.invoiceNumberPrefix,
        inv.invoiceId,
        inv.invoiceNumber,
        inv.couponNumbers,
        inv.invoiceAmount,
        inv.dueAmount,
        inv.agency,
        inv.hotel,
      ]

      return fields.some(k =>
        String(k ?? '') // convierte null/undefined o números a cadena
          .toLowerCase()
          .includes(term)
      )
    })
  )
})

async function onManualSearch() {
  // 1. Construye tu filtro LIKE único (o varios términos con OR/AND)
  const searchTerm = manualFilter.value.trim()
  applyPaymentPayload.value.page = 0 // arrancas siempre en la primera página
  applyPaymentPayload.value.filter = [
    {
      key: 'invoiceNumberPrefix', // o el campo que quieras buscar
      operator: 'LIKE',
      value: `%${searchTerm}%`,
      logicalOperation: 'AND',
      type: 'filterSearch'
    },
    {
      key: 'invoiceId',
      operator: 'LIKE',
      value: `%${searchTerm}%`,
      logicalOperation: 'AND',
      type: 'filterSearch'
    },
    {
      key: 'invoiceNumber',
      operator: 'LIKE',
      value: `%${searchTerm}%`,
      logicalOperation: 'AND',
      type: 'filterSearch'
    },
    {
      key: 'couponNumbers',
      operator: 'LIKE',
      value: `%${searchTerm}%`,
      logicalOperation: 'AND',
      type: 'filterSearch'
    },
    {
      key: 'invoiceAmountTemp',
      operator: 'LIKE',
      value: `%${searchTerm}%`,
      logicalOperation: 'AND',
      type: 'filterSearch'
    },
    {
      key: 'dueAmountTemp',
      operator: 'LIKE',
      value: `%${searchTerm}%`,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }
  ]

  // 2. Pides al servidor sólo las filas que coinciden
  const response = await GenericService.search(
    applyPaymentOptions.value.moduleApi,
    applyPaymentOptions.value.uriApi,
    applyPaymentPayload.value
  )

  // 3. Actualizas tu lista (y la paginación vendrá ya acorde al total de coincidencias)
  applyPaymentListOfInvoice.value = response.data
  applyPaymentPagination.value.totalElements = response.totalElements
  applyPaymentPagination.value.totalPages = response.totalPages
}

async function parseDataTableFilterForApplyPayment1(payloadFilter: any) {
  const parseFilter: IFilter[] = await getEventFromTable(payloadFilter, applyPaymentColumns.value) || []

  parseFilter.forEach((f) => {
    switch (f.key) {
      case 'invoiceNumber':
        f.key = 'invoiceNumber'
        break
      case 'invoiceId':
        f.key = 'invoiceId'
        break
      case 'couponNumbers':
        // dejamos couponNumbers tal cual
        f.key = 'couponNumbers'
        break
      case 'dueAmountTemp':
        f.key = 'dueAmount'
        break
      case 'invoiceAmountTemp':
        f.key = 'invoiceAmount'
        break
      case 'status.id':
        f.key = 'manageInvoiceStatus.id'
        break
    }
    f.type = 'filterSearch'
  })

  // 3) Reconstruye el array de filtros en el payload
  applyPaymentPayload.value.filter
    = applyPaymentPayload.value.filter.filter(f => f.type !== 'filterSearch')
  applyPaymentPayload.value.filter.push(...parseFilter)

  // 4) Resetea página y recarga
  applyPaymentPayload.value.page = 0
  await applyPaymentGetList()
}

async function applyPaymentOnSortField(event: any) {
  if (event) {
    if (event.sortField === 'invoiceNumber') {
      event.sortField = 'invoiceNumberPrefix'
    }
    if (event.sortField === 'couponNumbers') {
      event.sortField = 'bookings.couponNumber'
    }
    if (event.sortField === 'dueAmountTemp') {
      event.sortField = 'dueAmount'
    }
    if (event.sortField === 'invoiceAmountTemp') {
      event.sortField = 'invoiceAmount'
    }
    if (event.sortField === 'status') {
      event.sortField = 'manageInvoiceStatus.name'
    }
    applyPaymentPayload.value.sortBy = event.sortField
    applyPaymentPayload.value.sortType = event.sortOrder
    parseDataTableFilterForApplyPayment(event.filter)
  }
  applyPaymentGetList()
}

async function parseDataTableFilterForApplyPaymentOtherDeduction(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, applyPaymentColumnsOtherDeduction.value)
  const objFilter = parseFilter?.find((item: IFilter) => item?.key === 'invoiceId')
  if (objFilter) {
    objFilter.key = 'invoice.invoiceId'
  }
  const objFilterBookingId = parseFilter?.find((item: IFilter) => item?.key === 'bookingId')
  if (objFilterBookingId) {
    objFilterBookingId.key = 'bookingId'
  }
  const objFilterInvoiceNumber = parseFilter?.find((item: IFilter) => item?.key === 'invoiceNumber')
  if (objFilterInvoiceNumber) {
    objFilterInvoiceNumber.key = 'invoice.invoiceNumberPrefix'
  }
  const objFilterFullName = parseFilter?.find((item: IFilter) => item?.key === 'fullName')
  if (objFilterFullName) {
    objFilterFullName.key = 'fullName'
  }
  const objFilterCoupon = parseFilter?.find((item: IFilter) => item?.key === 'couponNumber')
  if (objFilterCoupon) {
    objFilterCoupon.key = 'couponNumber'
  }
  const objFilterReservationNumber = parseFilter?.find((item: IFilter) => item?.key === 'reservationNumber')
  if (objFilterReservationNumber) {
    objFilterReservationNumber.key = 'reservationNumber'
  }
  const objFilterInvoiceAmount = parseFilter?.find((item: IFilter) => item?.key === 'bookingAmountTemp')
  if (objFilterInvoiceAmount) {
    objFilterInvoiceAmount.key = 'invoiceAmount'
  }
  const objFilterDueAmount = parseFilter?.find((item: IFilter) => item?.key === 'dueAmountTemp')
  if (objFilterDueAmount) {
    objFilterDueAmount.key = 'dueAmount'
  }

  if (parseFilter && parseFilter?.length > 0) {
    parseFilter.forEach((filter) => {
      filter.type = 'filterSearch'
    })
  }

  applyPaymentPayloadOtherDeduction.value.filter = [...applyPaymentPayloadOtherDeduction.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  applyPaymentPayloadOtherDeduction.value.filter = [...applyPaymentPayloadOtherDeduction.value.filter, ...parseFilter || []]
  await applyPaymentGetListForOtherDeductions()
}

function applyPaymentOtherDeductionOnSortField(event: any) {
  if (event) {
    if (event.sortField === 'invoiceId') {
      event.sortField = 'invoice.invoiceId'
    }
    if (event.sortField === 'bookingId') {
      event.sortField = 'bookingId'
    }
    if (event.sortField === 'invoiceNumber') {
      event.sortField = 'invoice.invoiceNumberPrefix'
    }
    if (event.sortField === 'couponNumber') {
      event.sortField = 'invoice.invoiceNumberPrefix'
    }
    if (event.sortField === 'reservationNumber') {
      event.sortField = 'reservationNumber'
    }
    if (event.sortField === 'dueAmountTemp') {
      event.sortField = 'dueAmount'
    }
    if (event.sortField === 'bookingAmountTemp') {
      event.sortField = 'invoiceAmount'
    }
    applyPaymentPayloadOtherDeduction.value.sortBy = event.sortField
    applyPaymentPayloadOtherDeduction.value.sortType = event.sortOrder
    applyPaymentGetListForOtherDeductions(event.filter)
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

watch(payloadOnChangePageChangeAgency, async (newValue) => {
  const newPageSize = newValue?.rows ?? 10
  const newPage = newValue?.page ?? 0

  payloadChangeAgency.value.pageSize = newPageSize
  payloadChangeAgency.value.page = newPage

  await getAgencyByClient()

  const totalPages = paginationChangeAgency.value?.totalPages ?? 1
  if (payloadChangeAgency.value.page >= totalPages) {
    payloadChangeAgency.value.page = Math.max(totalPages - 1, 0)
    await getAgencyByClient()
  }
})

watch(applyPaymentOnChangePage, (newValue) => {
  const newPageSize = newValue?.rows ?? 10
  const newPage = newValue?.page ?? 0

  // Si cambia el tamaño de página, reiniciar al inicio
  if (newPageSize !== applyPaymentPayload.value.pageSize) {
    applyPaymentPayload.value.page = 0
  }
  else {
    applyPaymentPayload.value.page = newPage
  }

  applyPaymentPayload.value.pageSize = newPageSize
  applyPaymentGetList()
})

watch(applyPaymentOnChangePageOtherDeduction, async (newValue) => {
  const newPageSize = newValue?.rows ?? 500
  const newPage = newValue?.page ?? 0

  applyPaymentPayloadOtherDeduction.value.pageSize = newPageSize
  applyPaymentPayloadOtherDeduction.value.page = newPage

  await applyPaymentGetListForOtherDeductions()

  const totalPages = applyPaymentPaginationOtherDeduction.value.totalPages ?? 1
  if (applyPaymentPayloadOtherDeduction.value.page >= totalPages) {
    applyPaymentPayloadOtherDeduction.value.page = Math.max(totalPages - 1, 0)
    await applyPaymentGetListForOtherDeductions()
  }
})

watch(applyPaymentOnChangePageOtherDeduction1, async (newValue) => {
  const newPageSize = newValue?.rows ?? 500
  const newPage = newValue?.page ?? 0

  applyPaymentPayloadOtherDeduction1.value.pageSize = newPageSize
  applyPaymentPayloadOtherDeduction1.value.page = newPage

  await applyPaymentGetListForOtherDeductions()

  const totalPages = applyPaymentPaginationOtherDeduction1.value.totalPages ?? 1
  if (applyPaymentPayloadOtherDeduction1.value.page >= totalPages) {
    applyPaymentPayloadOtherDeduction1.value.page = Math.max(totalPages - 1, 0)
    await applyPaymentGetListForOtherDeductions()
  }
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
  assingFunctionsToExportMenuInItemMenuList()
  // assingFuntionsForPrint()
  document.title = 'Payment Management'
  await loadDefaultsConfig()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h5 class="-mb-1 w-6 mt-0" style="line-height: 1; position: relative; top: 4px;">
      Payment Management
    </h5>
    <div class="flex flex-row w-full place-content-center justify-center justify-content-end -mt-2 -mb-1">
      <span v-for="(objBtnAndMenu, index) in itemMenuList" :key="index">
        <IfCan :perms="objBtnAndMenu.permission && objBtnAndMenu?.permission?.length > 0 ? objBtnAndMenu.permission : []">
          <div v-if="objBtnAndMenu.showBtn()" class="my-2 flex justify-content-end pl-2 pr-0">
            <Button
              v-tooltip.left="objBtnAndMenu.btnToolTip"
              :label="objBtnAndMenu.btnLabel"
              severity="primary"
              class="h-2rem"
              :disabled="objBtnAndMenu.btnDisabled"
              aria-haspopup="true"
              :aria-controls="objBtnAndMenu.menuId"
              @click="objBtnAndMenu.menuItems.length > 0 ? toggle($event, index) : (objBtnAndMenu.btnOnClick ? objBtnAndMenu.btnOnClick() : null)"
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
      <Accordion :active-index="0" class="mb-0">
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
          <div class="flex gap-4 flex-column lg:flex-row -mt-2">
            <!-- first filter -->
            <div class="col-12 md:col-2 align-items-center my-0 py-0 w-auto">
              <div class="grid align-items-center justify-content-center">
                <div class="col-12">
                  <div class="flex align-items-center mb-2">
                    <label for="" class="mr-0 font-bold w-6rem"> Client:</label>
                    <div class="w-full">
                      <DebouncedMultiSelectComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete"
                        field="name"
                        class="w-full h-2rem align-items-center"
                        item-value="id"
                        :max-selected-labels="1"
                        :model="filterToSearch.client"
                        :suggestions="[...clientItemsList]"
                        :loading="objLoading.loadingClient"
                        @change="($event) => {
                          if (!filterToSearch.client.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.client = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.client = $event.filter((element: any) => element?.id !== 'All')
                          }
                          if (filterToSearch.client.length === 0) {
                            filterToSearch.agency = []
                          }
                          filterToSearch.agency = []
                          agencyItemsList = []
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
                      <!-- <DebouncedAutoCompleteComponent
                        v-if="false"
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
                      /> -->
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <label for="" class="mr-2 font-bold"> Agency:</label>
                    <div class="w-full">
                      <DebouncedMultiSelectComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete"
                        class="w-full h-2rem align-items-center"
                        field="name"
                        item-value="id"
                        :max-selected-labels="1"
                        :model="filterToSearch.agency"
                        :suggestions="[...agencyItemsList]"
                        :loading="objLoading.loadingAgency"
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
                      <!-- <DebouncedAutoCompleteComponent
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
                      /> -->
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
                    <label for="" class="mr-2 font-bold"> Hotels:</label>
                    <div class="w-full">
                      <DebouncedMultiSelectComponent
                        id="autocomplete"
                        class="w-full h-2rem align-items-center"
                        field="name"
                        item-value="id"
                        :max-selected-labels="1"
                        :model="filterToSearch.hotel"
                        :suggestions="[...hotelItemsList]"
                        :loading="objLoading.loadingHotel"
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
                      <DebouncedAutoCompleteComponent
                        v-if="false"
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
                    <label for="" class="mr-2 font-bold">Status:</label>
                    <div class="w-full">
                      <DebouncedMultiSelectComponent
                        id="autocomplete"
                        class="w-full h-2rem align-items-center"
                        field="name"
                        item-value="id"
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
                            // {
                            //   key: 'transit',
                            //   logicalOperation: 'AND',
                            //   operator: 'EQUALS',
                            //   value: false,
                            // },
                          ]
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          await getStatusList(objApis.status.moduleApi, objApis.status.uriApi, objQueryToSearch, filter)
                        }"
                      />
                      <DebouncedAutoCompleteComponent
                        v-if="false"
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
                            // {
                            //   key: 'code',
                            //   logicalOperation: 'AND',
                            //   operator: 'NOT_EQUALS',
                            //   value: 'TRA',
                            // },
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
                    <label for="" class="mr-2 font-bold"> From:</label>
                    <div class="w-9rem">
                      <!-- :min-date="new Date(startOfMonth)"
                        :max-date="filterToSearch.to ? new Date(filterToSearch.to) : new Date(endOfMonth)" -->
                      <Calendar
                        id="from"
                        v-model="filterToSearch.from"
                        class="w-full"
                        :max-date="filterToSearch.to ? new Date(filterToSearch.to) : new Date(endOfMonth)"
                        date-format="yy-mm-dd"
                        :disabled="disabledDates"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <label for="" class="mr-2 font-bold" style="padding-right: 17px;"> To:</label>
                    <div class="w-9rem">
                      <!-- :max-date="new Date(endOfMonth)" -->
                      <Calendar
                        id="to"
                        v-model="filterToSearch.to"
                        class="w-auto"
                        :min-date="filterToSearch.from ? new Date(filterToSearch.from) : new Date(startOfMonth)"
                        date-format="yy-mm-dd"
                        :disabled="disabledDates"
                      />
                    </div>
                  </div>
                </div>
                <div class="col-12 md:col-1 w-auto pr-0">
                  <div class="flex justify-content-end w-auto">
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
                    <label for="" class="font-bold">All</label>
                  </div>
                </div>
              </div>
            </div>

            <!-- fourth filter -->
            <div class="col-12 md:col-2 align-items-center my-0 py-0 w-auto">
              <div class="grid align-items-center justify-content-center">
                <div class="col-12">
                  <div class="flex align-items-center mb-2">
                    <label for="" class="mr-2 font-bold"> Criteria:</label>
                    <div class="w-12rem">
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
                    <label for="" class="w-4rem font-bold mr-1">Value:</label>
                    <InputText v-model="filterToSearch.value" type="text" placeholder="" class="w-12rem" style="max-width: 12rem;" />
                  </div>
                </div>
              </div>
            </div>

            <!-- fifth filter -->
            <div class="col-12 md:col-2 align-items-center my-0 py-0 w-auto">
              <div class="flex align-items-center h-full -ml-4">
                <Checkbox
                  id="payApplied"
                  v-model="filterToSearch.payApplied"
                  :binary="true"
                />
                <label for="payApplied" class="ml-2 font-bold"> Pay Applied</label>
              </div>
              <div v-if="false" class="grid align-items-center justify-content-center">
                <div class="col-12">
                  <div v-if="false" class="flex align-items-center mb-2">
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
                      <TriStateCheckbox
                        id="payApplied"
                        v-model="filterToSearch.payApplied"
                        :binary="true"
                      />
                      <label for="payApplied" class="ml-2 font-bold"> Pay Applied</label>
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
                @click="loadDefaultsConfig"
              />

              <Button
                v-tooltip.top="'Copy Table'"
                class="p-button-lg w-1rem h-2rem ml-2"
                style="margin-left: 10px; margin-top: 10px"
                icon="pi pi-copy"
                @click="copiarDatos"
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
    <!-- @update:clicked-item="assingFuntionsForPrint($event)" -->
    <div class="-mt-4">
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
        @on-row-double-click="goToFormInNewTab($event)"
        @on-row-right-click="onRowContextMenu($event)"
      >
        <template #column-icon="{ data: objData, column }">
          <div class="clip-wrapper">
            <!-- <pre>{{ objData.attachmentStatus }}</pre> -->
            <Button
              v-if="showIconAttachment(objData)"
              :icon="column.icon"
              class="p-button-text p-button-rounded clip-icon-bg"
              aria-label="Submit"
              :disabled="objData?.attachmentStatus?.nonNone"
              :style="{ color: objData.color }"
            />
          </div>
        <!-- style="color: #616161;" -->
        <!-- :style="{ 'background-color': '#00b816' }" -->
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
              <Column footer="Totals:" :colspan="options.selectionMode === 'multiple' ? 10 : 9" footer-style="text-align:right; font-weight: bold;" />
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
    </div>
    <!-- Dialog change agency -->
    <Dialog
      v-model:visible="openDialogChangeAgency"
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
      }"
      @hide="closeModalChangeAgency()"
    >
      <template #header>
        <div class="flex align-items-center justify-content-between w-full">
          <div class="flex align-items-center">
            <h5 class="m-0">
              Change Agency
            </h5>
          </div>
          <div class="flex align-items-center">
            <h5 class="m-0 mr-4">
              Payment Id: {{ objItemSelectedForRightClickChangeAgency.paymentId }}
            </h5>
          </div>
        </div>
      </template>
      <template #default>
        <div class="p-fluid pt-3">
          <!-- // Label -->
          <div class="flex justify-content-between mb-2">
            <div v-if="false" class="flex align-items-center mb-3">
              <div class="mr-2">
                <label for="autocomplete" class="font-semibold"> Client: </label>
              </div>
              <div class="mr-4">
                <!-- <pre>{{ objClientFormChangeAgency }}</pre> -->
                <DebouncedAutoCompleteComponent
                  id="autocomplete"
                  class="w-29rem"
                  field="name"
                  item-value="id"
                  :model="objClientFormChangeAgency"
                  :suggestions="[...listClientFormChangeAgency]"
                  @change="async ($event) => {
                    objClientFormChangeAgency = $event
                    // if ($event && $event.id) {
                    //   await getAgencyByClient($event.id)
                    // }
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
            <div v-if="true" class="bg-primary w-auto h-2rem flex align-items-center px-2" style="border-radius: 5px">
              <strong class="mr-2 w-auto">Client:</strong>
              <!-- <span class="w-auto text-white font-semibold">{{ objClientFormChangeAgency.code }} - {{ objClientFormChangeAgency.name }}</span> -->
              <span class="w-auto text-white font-semibold">{{ objClientFormChangeAgency.name }}</span>
            </div>
            <div class="bg-primary w-auto h-2rem flex align-items-center px-2" style="border-radius: 5px">
              <strong class="mr-2 w-auto">Current Agency:</strong>
              <!-- <span class="w-auto text-white font-semibold">{{ currentAgencyForChangeAgency.code }} - {{ currentAgencyForChangeAgency.name }}</span> -->
              <span class="w-auto text-white font-semibold">{{ currentAgencyForChangeAgency.name }}</span>
            </div>
          </div>

          <DynamicTable
            class="card p-0"
            :data="listAgencyByClient"
            :columns="columnsChangeAgency"
            :options="optionsOfTableChangeAgency"
            :pagination="paginationChangeAgency"
            @on-sort-field="onSortFieldForChangeAgency"
            @on-change-filter="parseDataTableFilterForChangeAgency"
            @on-change-pagination="payloadOnChangePageChangeAgency = $event"
            @on-row-double-click="onRowDoubleClickInDataTableForChangeAgency"
          />
        </div>
        <div class="flex justify-content-end">
          <div>
            <!-- idInvoicesSelectedToApplyPaymentForOtherDeduction.length === 0 -->
            <Button
              v-if="false"
              v-tooltip.top="'Apply Payment'"
              class="w-3rem mx-1"
              icon="pi pi-check"
              :disabled="Object.keys(transactionType).length === 0 || idInvoicesSelectedToApplyPaymentForOtherDeduction.length === 0"
              :loading="loadingSaveApplyPayment"
              @click="saveApplyPayment"
            />
          </div>
        </div>
      </template>
    </Dialog>

    <!-- Dialog Apply Payment Clic Derecho Payment Management -->
    <div v-if="openDialogApplyPayment">
      <Dialog
        v-model:visible="openDialogApplyPayment"
        modal
        class="mx-3 sm:mx-0"
        content-class="border-round-bottom border-top-1 surface-border"
        :style="{ width: '90vw', maxHeight: '100vh' }"
        :pt="{
          root: {
            class: 'custom-dialog-history',
          },
          header: {
            style: 'padding-top: 0.1rem; padding-bottom: 0.1rem',
          },
        }"
        @hide="closeModalApplyPayment()"
      >
        <template #header>
          <div class="flex justify-content-between align-items-center w-full">
            <div>
              <h5 class="m-0">
                Select Invoice
              </h5>
            </div>
            <div class="font-bold mr-5">
              <h5 class="m-0">
                Payment Id: {{ objItemSelectedForRightClickApplyPayment.paymentId }}
              </h5>
            </div>
          </div>
        </template>

        <template #default>
          <div class="-px-4 -my-4 py-1 mb-3">
            <div class="p-fluid pt-3">
              <BlockUI v-if="applyPaymentListOfInvoice.length" :blocked="paymentDetailsTypeDepositLoading" class="mb-3 -mt-2">
                <DataTable
                  v-model:selection="paymentDetailsTypeDepositSelected"
                  :value="paymentDetailsTypeDepositList"
                  striped-rows
                  show-gridlines
                  scrollable
                  scroll-height="150px"
                  :row-class="(row) => isRowSelectable(row) ? 'p-selectable-row' : 'p-disabled p-text-disabled'"
                  data-key="id"
                  selection-mode="multiple"
                  @update:selection="onRowSelectAll"
                >
                  <Column selection-mode="multiple" header-style="width: 3rem" />
                  <Column
                    v-for="column of columnsPaymentDetailTypeDeposit.filter(c =>
                      ['paymentDetailId', 'transactionType', 'amount', 'applyDepositValue', 'remark'].includes(c.field),
                    )"
                    :key="column.field"
                    :field="column.field"
                    :header="column.header"
                    :sortable="column?.sortable"
                  >
                    <template v-if="column.field === 'applyDepositValue'" #body="slotProps">
                      {{ formatNumber(slotProps.data.applyDepositValue) }}
                    </template>
                  </Column>

                  <template #empty>
                    <div class="flex flex-column flex-wrap align-items-center justify-content-center py-8">
                      <span v-if="!options?.loading" class="flex flex-column align-items-center justify-content-center">
                        <div class="row">
                          <i class="pi pi-trash -mt-6" style="font-size: 2rem;" />
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
              <div class="flex align-items-center mb-3">
                <InputText
                  v-model="manualFilter"
                  placeholder="Search Data…"
                  class="-mb-3 -mt-3"
                  style="padding-top: 0.1rem"
                  @keyup.enter="onManualSearch"
                />
              </div>
              <DynamicTable
                class="card p-0"
                :data="filteredInvoices"
                :columns="applyPaymentColumns"
                :options="applyPaymentOptions"
                :pagination="applyPaymentPagination"
                @on-change-pagination="applyPaymentOnChangePage = $event"
                @on-change-filter="parseDataTableFilterForApplyPayment"
                @update:clicked-item="addAmmountsToApplyPayment($event)"
                @on-sort-field="applyPaymentOnSortField"
                @on-expand-row="onExpandRowApplyPayment($event)"
              >
                <template #column-status="{ data: item }">
                  <Badge :value="getStatusName(item?.status)" :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`" />
                </template>

                <template #expansion="{ data: item }">
                  <div class="p-0 m-0">
                    <DataTable :value="item.bookings" striped-rows>
                      <Column v-for="column of columnsExpandTable" :key="column.field" :field="column.field" :header="column.header" :sortable="column?.sortable">
                        <template v-if="column.field === 'dueAmount'" #body="slotProps">
                          {{ formatNumber(slotProps.data.dueAmount) }}
                        </template>
                        <template v-else-if="column.field === 'invoiceAmount'" #body="slotProps">
                          {{ formatNumber(slotProps.data.invoiceAmount) }}
                        </template>
                      </Column>
                      <template #empty>
                        <div class="flex flex-column flex-wrap align-items-center justify-content-center py-8">
                          <span v-if="!item?.loadingBookings" class="flex flex-column align-items-center justify-content-center">
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
          <div class="flex justify-content-between -mb-4 -mt-3">
            <div class="flex align-items-center">
              <Chip class="bg-primary py-1 font-bold" label="Applied Payment Amount:">
                Available Payment Amount: ${{ formatNumber(paymentAmmountSelected) }}
              </Chip>
              <Chip class="bg-primary py-1 mx-2 font-bold" label="Invoice Amount Selected: $0.00">
                Invoice Amount Selected: ${{ formatNumber(invoiceAmmountSelected) }}
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
              <Button
                v-tooltip.top="'Apply Payment'"
                class="w-3rem mx-1"
                icon="pi pi-check"
                :disabled="disabledBtnApplyPayment || (paymentAmmountSelected <= 0 || paymentAmmountSelected === null || paymentAmmountSelected === undefined)"
                :loading="loadingSaveApplyPayment"
                @click="saveApplyPayment"
              />
            </div>
          </div>
        </template>
      </Dialog>
    </div>

    <!-- Dialog Apply Payment Other Deduction -->
    <Dialog
      v-model:visible="openDialogApplyPaymentOtherDeduction"
      modal
      class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
      :style="{ width: '90vw', maxHeight: '100vh' }"
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
      @hide="closeModalApplyPaymentOtherDeductions()"
    >
      <template #header>
        <div class="flex justify-content-between align-items-center w-full">
          <h5 class="m-0">
            Select Booking
          </h5>
          <h5 class="m-0 font-bold mr-5">
            Payment Id: {{ objItemSelectedForRightClickApplyPaymentOtherDeduction.paymentId }}
          </h5>
        </div>
      </template>
      <template #default>
        <div class="p-fluid pt-3">
          <!-- // Label -->
          <div class="grid">
            <div class="col-12 md:col-4 w-auto">
              <div class="flex align-items-center">
                <div class="mr-2">
                  <label for="autocomplete" class="font-semibold"> Transaction </label>
                </div>
                <div class="mr-4">
                  <DebouncedAutoCompleteComponent
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="transactionType"
                    :suggestions="[...transactionTypeList]"
                    @change="($event) => {
                      // transactionType = $event
                      if ($event) {
                        transactionType = $event
                        processValidation($event)
                      }
                      else {
                        transactionType = {}
                      }
                    }"
                    @load="async($event) => {
                      const objQueryToSearch = {
                        query: $event,
                        keys: ['name', 'code'],
                      }
                      const filter: FilterCriteria[] = []
                      const sortObj = {
                        sortBy: '',
                        // sortType: ENUM_SHORT_TYPE.DESC,
                      }
                      filter.push(
                        {
                          key: 'cash',
                          logicalOperation: 'AND',
                          operator: 'EQUALS',
                          value: false,
                        },
                        {
                          key: 'deposit',
                          logicalOperation: 'AND',
                          operator: 'EQUALS',
                          value: false,
                        },
                        {
                          key: 'debit',
                          logicalOperation: 'AND',
                          operator: 'EQUALS',
                          value: false,
                        },
                        {
                          key: 'applyDeposit',
                          logicalOperation: 'AND',
                          operator: 'EQUALS',
                          value: false,
                        },
                        {
                          key: 'status',
                          logicalOperation: 'AND',
                          operator: 'EQUALS',
                          value: 'ACTIVE',
                        },
                      )

                      await getTransactionTypeList(objApis.transactionType.moduleApi, objApis.transactionType.uriApi, objQueryToSearch, filter, sortObj)
                    }"
                  />
                </div>
              </div>
            </div>
            <div class="col-12 md:col-8 w-auto">
              <div class="flex align-items-center">
                <div class="mr-2">
                  <label for="autocomplete" class="font-semibold"> Remark </label>
                </div>
                <div class="w-30rem">
                  <InputText
                    v-model="fieldRemark"
                    show-clear
                    @update:model-value="($event) => {
                      validateField('remark', $event)
                    }"
                  />
                  <div v-if="true" class="flex">
                    <div class="w-full">
                      <div v-if="errors.remark" class="p-error text-xs w-full">
                        <div v-for="error in errors.remark" :key="error" class="error-message">
                          {{ error }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- @on-row-double-click="onRowDoubleClickInDataTableApplyPayment" -->
          <DynamicTable
            class="card p-0"
            :data="applyPaymentListOfInvoiceOtherDeduction"
            :columns="applyPaymentColumnsOtherDeduction"
            :options="applyPaymentOptionsOtherDeduction"
            :pagination="applyPaymentPaginationOtherDeduction"
            @on-change-pagination="applyPaymentOnChangePageOtherDeduction = $event"
            @on-change-filter="parseDataTableFilterForApplyPaymentOtherDeduction($event)"
            @update:clicked-item="selectRowsOfInvoiceOfOtherDeduction($event)"
            @on-table-cell-edit-complete="onCellEditCompleteApplyPaymentOtherDeduction($event)"
            @on-sort-field="applyPaymentOtherDeductionOnSortField"
          >
            <!-- @update:clicked-item="invoiceSelectedListForApplyPayment = $event" -->
            <template #column-status="{ data: item }">
              <Badge :value="getStatusName(item?.status)" :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`" />
            </template>

            <!-- <template #expansion="{ data: item }">
              <div class="p-0 m-0">
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
            </template> -->
          </DynamicTable>
        </div>
        <div class="flex justify-content-between">
          <div class="flex align-items-center">
            <!-- :disabled="applyPaymentListOfInvoiceOtherDeduction.length > 0 && allInvoiceCheckIsChecked === false" -->
            <Checkbox
              id="checkApplyPayment"
              v-model="loadAllInvoices"
              :binary="true"
              :disabled="false"
              @update:model-value="($event) => {

                // Este check solo se activa si la lista esta vacia, pero si hace click en el check y se llene la lista (se supone que el check se deshabilite)
                // la variable allInvoiceCheckIsChecked se usa para controlar eso
                allInvoiceCheckIsChecked = true
                applyPaymentGetListForOtherDeductions();
              }"
            />
            <label for="checkApplyPayment" class="ml-2 font-bold">
              Load All Bookings
            </label>
          </div>
          <div>
            <Button
              v-tooltip.top="'Copy Table'"
              class="w-3rem mx-1"
              icon="pi pi-copy"
              @click="copiarDatosOtherDeductions"
            />
            <!-- idInvoicesSelectedToApplyPaymentForOtherDeduction.length === 0 -->
            <Button
              v-tooltip.top="'Apply Payment'"
              class="w-3rem mx-1"
              icon="pi pi-check"
              :disabled="('id' in transactionType) === false || idInvoicesSelectedToApplyPaymentForOtherDeduction.length === 0"
              :loading="loadingSaveApplyPayment"
              @click="saveApplyPaymentOtherDeduction"
            />
          </div>
        </div>
      </template>
    </Dialog>

    <Dialog
      v-model:visible="openDialogCopyBatch"
      modal
      class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
      :style="{ width: '90vw', maxHeight: '100vh' }"
      :pt="{
        root: {
          class: 'custom-dialog-history',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
      }"
      @hide="closeModalApplyPaymentOtherDeductions()"
    >
      <template #header>
        <div class="flex justify-content-between align-items-center w-full">
          <h5 class="m-0">
            Colums of Batch
          </h5>
          <h5 class="m-0 font-bold mr-5">
            Payment Id: {{ objItemSelectedForRightClickApplyPaymentOtherDeduction.paymentId }}
          </h5>
        </div>
      </template>
      <template #default>
        <div class="p-fluid pt-0">
          <div class="grid">
            <div class="col-12 md:col-8 w-auto">
              <div class="flex align-items-center">
                <div class="w-30rem">
                  <div v-if="true" class="flex">
                    <div class="w-full">
                      <div v-if="errors.remark" class="p-error text-xs w-full">
                        <div v-for="error in errors.remark" :key="error" class="error-message">
                          {{ error }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <DynamicTable
          class="card p-0"
          :data="applyPaymentListOfInvoiceOtherDeduction"
          :columns="applyPaymentColumnsOtherDeduction1"
          :options="applyPaymentOptionsOtherDeduction1"
          :pagination="applyPaymentPaginationOtherDeduction"
          @on-change-pagination="applyPaymentOnChangePageOtherDeduction = $event"
          @on-change-filter="parseDataTableFilterForApplyPaymentOtherDeduction($event)"
          @update:clicked-item="selectRowsOfInvoiceOfOtherDeduction($event)"
          @on-table-cell-edit-complete="onCellEditCompleteApplyPaymentOtherDeduction($event)"
          @on-sort-field="applyPaymentOtherDeductionOnSortField"
        >
          <!-- Slot para la columna "status" -->
          <template #column-status="{ data: item }">
            <Badge
              :value="getStatusName(item?.status)"
              :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`"
            />
          </template>

          <!-- Footer con total de registros -->
          <template #datatable-footer>
            <ColumnGroup type="footer">
              <Row>
                <Column
                  :colspan="applyPaymentColumnsOtherDeduction1.length"
                  footer-style="text-align:right; font-weight: bold;"
                >
                  <template #footer>
                    <div class="flex justify-content-end pr-4 text-sm">
                      Total: {{ applyPaymentListOfInvoiceOtherDeduction.length }}
                    </div>
                  </template>
                </Column>
              </Row>
            </ColumnGroup>
          </template>
        </DynamicTable>

        <div class="flex justify-content-between">
          <div class="flex align-items-center">
            <Checkbox
              id="checkApplyPayment"
              v-model="loadAllInvoices"
              :binary="true"
              :disabled="false"
              @update:model-value="($event) => {

                // Este check solo se activa si la lista esta vacia, pero si hace click en el check y se llene la lista (se supone que el check se deshabilite)
                // la variable allInvoiceCheckIsChecked se usa para controlar eso
                allInvoiceCheckIsChecked1 = true
                applyPaymentGetListForOtherDeductions();
              }"
            />
            <label for="checkApplyPayment" class="ml-2 font-bold">
              Load All Bookings
            </label>
          </div>
          <div>
            <Button
              v-tooltip.top="'Copy Batch'"
              class="w-3rem mx-1 mt-1"
              icon="pi pi-copy"
              @click="copiarDatosBatch"
            />
            <Button
              v-tooltip.top="'Export Excel'"
              class="w-3rem mx-1 mt-1"
              icon="pi pi-file-excel"
              @click="exportarOtherDeductions"
            />
          </div>
        </div>
      </template>
    </Dialog>

    <Dialog
      v-model:visible="openDialogHistory"
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
      @hide="openDialogHistory = false"
    >
      <template #header>
        <div class="flex justify-content-between w-full">
          <div class="flex align-items-center">
            <h5 class="m-0">
              Payment Status History
            </h5>
          </div>
          <div class="flex align-items-center">
            <h5 class="m-0 mr-4">
              Payment Id: {{ objItemSelectedForRightClickChangeAgency.paymentId }}
            </h5>
          </div>
        </div>
      </template>
      <template #default>
        <div class="p-fluid pt-3">
          <DynamicTable
            class="card p-0"
            :data="historyList"
            :columns="historyColumns"
            :options="historyOptions"
            :pagination="historyPagination"
            @on-change-pagination="histotyPayloadOnChangePage = $event"
            @on-change-filter="historyParseDataTableFilter"
            @on-sort-field="historyOnSortField"
          >
            <template #column-paymentStatus="{ data, column }">
              <Badge
                v-tooltip.top="data.paymentStatus.toString()"
                :value="data.paymentStatus"
                :class="column.statusClassMap?.find(e => e.status === data.paymentStatus)?.class"
              />
            </template>
          </DynamicTable>
        </div>
      </template>
    </Dialog>

    <!-- PRINT -->
    <Dialog
      v-model:visible="openPrint"
      modal
      class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
      :style="{ width: '37%' }"
      :pt="{
        root: {
          class: 'custom-dialog',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
        // mask: {
        //   style: 'backdrop-filter: blur(5px)',
        // },
      }"
      @hide="closeDialogPrint()"
    >
      <template #header>
        <div class="flex justify-content-between w-full">
          <h5 class="m-0">
            Payment To Print
          </h5>
          <div class="flex align-items-center">
            <h5 class="m-0 mr-4">
              Payment Id: {{ objItemSelectedForRightClickChangeAgency.paymentId }}
            </h5>
          </div>
        </div>
      </template>
      <template #default>
        <div class="p-fluid pt-3">
          <EditFormV2
            :key="formReload"
            class="mt-3"
            :fields="fieldPrint"
            :item="itemPrint"
            container-class="grid pt-3"
            :show-actions="true"
            :loading-save="loadingSaveAll"
            @cancel="closeDialogExportToExcel"
            @submit="paymentPrint($event)"
          >
            <template #field-paymentSupport="{ item: data, onUpdate }">
              <Checkbox
                id="paymentSupport"
                v-model="data.paymentSupport"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('paymentSupport', $event)
                  if ($event) {
                    onUpdate('allPaymentsSupport', false)
                  }
                }"
              />
              <label for="paymentSupport" class="ml-2 font-bold">
                Payment Support
              </label>
            </template>

            <template #field-allPaymentsSupport="{ item: data, onUpdate }">
              <Checkbox
                id="allPaymentsSupport"
                v-model="data.allPaymentsSupport"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('allPaymentsSupport', $event)
                  if ($event) {
                    onUpdate('paymentSupport', false)
                  }
                }"
              />
              <label for="allPaymentsSupport" class="ml-2 font-bold">
                All Payment Supports
              </label>
            </template>

            <template #field-invoiceRelated="{ item: data, onUpdate }">
              <Checkbox
                id="invoiceRelated"
                v-model="data.invoiceRelated"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('invoiceRelated', $event)
                  if ($event) {
                    onUpdate('invoiceRelatedWithSupport', false)
                  }
                }"
              />
              <label for="invoiceRelated" class="ml-2 font-bold">
                Invoice Related
              </label>
            </template>

            <template #field-invoiceRelatedWithSupport="{ item: data, onUpdate }">
              <Checkbox
                id="invoiceRelatedWithSupport"
                v-model="data.invoiceRelatedWithSupport"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('invoiceRelatedWithSupport', $event)
                  if ($event) {
                    onUpdate('invoiceRelated', false)
                  }
                }"
              />
              <label for="invoiceRelatedWithSupport" class="ml-2 font-bold">
                Invoice Related With Supports
              </label>
            </template>

            <template #form-footer="props">
              <Button v-tooltip.top="'Print'" :loading="loadingPrintDetail" class="w-3rem ml-1 sticky" icon="pi pi-print" @click="props.item.submitForm($event)" />
              <!-- <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times" @click="closeDialogPrint" /> -->
            </template>
          </EditFormV2>
        </div>
      </template>
    </Dialog>

    <!-- Export To Excel -->
    <Dialog
      v-model:visible="openModalExportToExcel"
      modal
      class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
      :style="{ width: '340px' }"
      :pt="{
        root: {
          class: 'custom-dialog',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
        // mask: {
        //   style: 'backdrop-filter: blur(5px)',
        // },
      }"
      @hide="closeDialogExportToExcel()"
    >
      <template #header>
        <div class="flex justify-content-between">
          <h5 class="m-0">
            Export Setting
          </h5>
        </div>
      </template>
      <template #default>
        <div class="p-fluid pt-3">
          <EditFormV2
            :key="formReload"
            class="mt-3"
            :fields="fieldExportToExcel"
            :item="objExportToExcel"
            :show-actions="true"
            :loading-save="loadingSaveAll"
            @cancel="closeDialogPrint"
            @submit="paymentExportToExcel($event)"
          >
            <template #field-exportSumary="{ item: data, onUpdate }">
              <label for="exportSumary" class="mr-2 font-bold">
                Export Sumary
              </label>
              <Checkbox
                id="exportSumary"
                v-model="data.exportSumary"
                :binary="true"
                disabled
                @update:model-value="($event) => {
                  onUpdate('exportSumary', $event)
                }"
              />
            </template>

            <!-- <template #field-fileName="{ item: data, onUpdate }">
              <div style="background-color: aqua; width: 100%;">
                <InputText
                  v-if="!loadingExportToExcel"
                  id="fileName"
                  v-model="data.fileName"
                  @input="onUpdate('fileName', $event)"
                />

                <Skeleton v-else height="2rem" class="mb-2" />
              </div>
            </template> -->

            <template #form-footer="props">
              <Button v-tooltip.top="'Save'" :loading="loadingExportToExcel" class="w-3rem ml-1 sticky" icon="pi pi-save" @click="props.item.submitForm($event)" />
              <!-- <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times" @click="closeDialogExportToExcel" /> -->
            </template>
          </EditFormV2>
        </div>
      </template>
    </Dialog>
  </div>
  <!-- Modal para Print -->
  <template>
    <div>
      <Dialog
        v-model:visible="isPrintModalOpen" modal header="Payment To Print" :style="{ width: '95vw', height: '100vh' }"
        :closable="true"
      >
        <PayPrint />
        <template #footer />
      </Dialog>
    </div>
  </template>

  <div v-if="attachmentDialogOpen">
    <PaymentAttachmentDialog
      is-create-or-edit-payment="edit"
      :add-item="addAttachment"
      :close-dialog="() => {
        attachmentDialogOpen = false
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

  <!--  Import Transactions From VCC -->
  <div v-if="openDialogImportTransactionsFromVCC">
    <PaymentImportTransactionsFromVCCDialog
      :open-dialog="openDialogImportTransactionsFromVCC" :close-dialog="() => {
        openDialogImportTransactionsFromVCC = false
      }" :selected-payment="paymentSelectedForAttachment"
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
  <DynamicContentModalImport
    :visible="PaymentExpenseToBookingDialogVisible" :component="PaymentExpenseToBookingDialog"
    header="Expense To Booking Import" :style="{ width, height, 'min-height': '98vh', 'min-width': '90vw' }"
    @close="closeImportExpenseToBooking"
  />
  <DynamicContentModalImport
    :visible="PaymentDetailDialogVisible" :component="PaymentDetailDialog"
    header="Payment Detail Import" :style="{ width, height, 'min-height': '98vh', 'min-width': '90vw' }"
    @close="closePaymentDetail"
  />
  <DynamicContentModalImport
    :visible="PaymentAntiToIncomeDialogVisible" :component="PaymentAntiToIncomeDialog"
    header="Anti To Income Import" :style="{ width, height, 'min-height': '98vh', 'min-width': '90vw' }"
    @close="closeImportAntiToIncome"
  />
  <DynamicContentModalImport
    :visible="PaymentExpenseDialogVisible" :component="PaymentExpenseDialog"
    header="Payment of Expense Import" :style="{ width, height, 'min-height': '98vh', 'min-width': '90vw' }"
    @close="closeImportExpense"
  />
  <DynamicContentModalImport
    :visible="PaymentBankDialogVisible" :component="PaymentBankDialog"
    header="Payment of Bank Import" :style="{ width, height, 'min-height': '98vh', 'min-width': '90vw' }"
    @close="closeImportBank"
  />
</template>

// <style lang="scss">
// .text-transit {
//   background-color: #ff002b;
//   color: #fff;
// }
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
.p-text-disabled {
  color: #888888;
}
.clip-wrapper {
  /* asegurarnos de tener espacio para el icono agrandado */
  width: 3rem !important;
  height: 1.8rem !important;
  display: flex;
  align-items: auto;
  justify-content: auto;
}
.clip-icon-bg {
   background-color: #ffffff;
   font-size: 4rem !important;       /* agranda */
  font-weight: 500 !important;        /* un poco más grueso */
  text-shadow: 0 0 1px currentColor;  /* pequeño glow */
  line-height: 1 !important;         /* ajustar línea */
  transform: scale(1.8) !important;
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
