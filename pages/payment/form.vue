<script setup lang="ts">
import dayjs from 'dayjs'
import { z } from 'zod'
import { useRoute } from 'vue-router'
import type { PageState } from 'primevue/paginator'
import { formatNumber, formatToTwoDecimalPlaces } from './utils/helperFilters'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import DialogPaymentDetailForm from '~/components/payment/DialogPaymentDetailForm.vue'
import PaymentAttachmentDialog, { type FileObject } from '~/components/payment/PaymentAttachmentDialog.vue'
import type { TransactionItem } from '~/components/payment/interfaces'
import { generateStyledPDF } from '~/components/payment/utils'

const route = useRoute()
const toast = useToast()
const confirm = useConfirm()
const authStore = useAuthStore()
const { status, data: userData } = useAuth()
const isAdmin = (userData.value?.user as any)?.isAdmin === true

const refForm: Ref = ref(null)
const idItem = ref('')
const idItemDetail = ref('')
const idPaymentDetail = ref('')
const detailItemForApplyPayment = ref<any>(null)
const isSplitAction = ref(false)
const enableSplitAction = ref(false)
const enableDepositSummaryAction = ref(false)
const formReload = ref(0)
const formReloadAgency = ref(0)
const refPaymentDetailForm = ref(0)
const dialogPaymentDetailFormReload = ref(0)

const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const loadingPrintDetail = ref(false)

const forceSave = ref(false)
const submitEvent = new Event('')
const paymentSourceList = ref<any[]>([])
const clientList = ref<any[]>([])
const agencyList = ref<any[]>([])
const hotelList = ref<any[]>([])
const bankAccountList = ref<any[]>([])
const paymentStatusList = ref<any[]>([])
const attachmentStatusList = ref<any[]>([])
const onOffDialogPaymentDetail = ref(false)
const onOffDialogPaymentDetailSummary = ref(false)
const hasBeenEdited = ref(0)
const hasBeenCreated = ref(0)
const actionOfModal = ref<'new-detail' | 'deposit-transfer' | 'split-deposit' | 'apply-deposit' | 'apply-payment' | undefined>(undefined)

const isApplyPaymentFromTheForm = ref(false)
const payloadToApplyPayment = ref<GenericObject> ({
  applyPayment: false,
  booking: ''
})

interface SubTotals {
  depositAmount: number
}
const subTotals = ref<SubTotals>({ depositAmount: 0 })

const attachmentDialogOpen = ref<boolean>(false)
const attachmentList = ref<any[]>([])

const paymentDetailsList = ref<any[]>([])

const contextMenu = ref()
const objItemSelectedForRightClick = ref({} as GenericObject)
const objItemSelectedForRightClickApplyPayment = ref({} as GenericObject)
const objItemSelectedForRightClickNavigateToInvoice = ref({} as GenericObject)
const allMenuListItems = ref([
  {
    id: 'applayDeposit',
    label: 'Apply Deposit',
    icon: 'pi pi-dollar',
    command: ($event: any) => openModalWithContentMenu($event),
    disabled: true,
    visible: true,
  },
  {
    id: 'applyPayment',
    label: 'Apply Payment',
    icon: 'pi pi-cog',
    command: ($event: any) => openModalApplyPayment($event),
    disabled: true,
    visible: true,
  },
  {
    id: 'navigateToInvoice',
    label: 'Navigate to Invoice',
    icon: 'pi pi-cog',
    command: ($event: any) => navigateToInvoice($event),
    disabled: true,
    visible: true,
  },
])
// Apply Payment
const openDialogApplyPayment = ref(false)
const applyPaymentList = ref<any[]>([])
const applyPaymentColumns = ref<IColumn[]>([
  { field: 'invoiceId', header: 'Invoice Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'bookingId', header: 'Booking Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'fullName', header: 'Full Name', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'couponNumber', header: 'Coupon No', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'reservationNumber', header: 'Reservation No', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'checkIn', header: 'Check-In', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'checkOut', header: 'Check-Out', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'bookingAmount', header: 'Booking Amount', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'bookingBalance', header: 'Booking Balance', type: 'text', width: '90px', sortable: false, showFilter: false },
])
const applyPaymentOptions = ref({
  tableName: 'Apply Payment',
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const applyPaymentPayload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'bookingId',
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
// ------------------------------------------------------------------------------------------------

// History
const openDialogHistory = ref(false)
const historyList = ref<any[]>([])
const employeeList = ref<any[]>([])
const historyColumns = ref<IColumn[]>([
  { field: 'paymentHistoryId', header: 'Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'paymentId', header: 'Payment Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'createdAt', header: 'Date', type: 'date', width: '120px' },
  { field: 'employee', header: 'Employee', type: 'select', width: '150px', localItems: [] },
  { field: 'description', header: 'Description', type: 'text', width: '200px' },
  { field: 'paymentStatus', header: 'Status', type: 'text', width: '60px', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' } },
])

const historyOptions = ref({
  tableName: 'Payment Attachment',
  moduleApi: 'payment',
  uriApi: 'payment-attachment-status-history',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const payloadHistory = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
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
// ----------------------------------

// PRINT
const openPrint = ref(false)

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
// ----------------------------------

const confApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment',
})

const confApiPaymentDetail = reactive({
  moduleApi: 'payment',
  uriApi: 'payment-detail',
})

const confApiPaymentDetailPrint = reactive({
  moduleApi: 'payment',
  uriApi: 'payment/report',
})

const objApis = ref({
  paymentSource: { moduleApi: 'settings', uriApi: 'manage-payment-source' },
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  bankAccount: { moduleApi: 'settings', uriApi: 'manage-bank-account' },
  paymentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  attachmentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' },
  employee: { moduleApi: 'settings', uriApi: 'manage-employee' },
})

const columns: IColumn[] = [
  { field: 'paymentDetailId', header: 'Id', tooltip: 'Detail Id', width: 'auto', type: 'text' },
  { field: 'bookingId', header: 'Booking Id', tooltip: 'Booking Id', width: '100px', type: 'text' },
  { field: 'invoiceNumber', header: 'Invoice No', tooltip: 'Invoice No', width: '100px', type: 'text' },
  { field: 'transactionDate', header: 'Transaction Date', tooltip: 'Transaction Date', width: 'auto', type: 'text' },
  { field: 'fullName', header: 'Full Name', tooltip: 'Full Name', width: '150px', type: 'text' },
  // { field: 'firstName', header: 'First Name', tooltip: 'First Name', width: '150px', type: 'text' },
  // { field: 'lastName', header: 'Last Name', tooltip: 'Last Name', width: '150px', type: 'text' },
  { field: 'reservationNumber', header: 'Reservation No', tooltip: 'Reservation', width: 'auto', type: 'text' },
  { field: 'couponNumber', header: 'Coupon No', tooltip: 'Coupon No', width: 'auto', type: 'text' },
  // { field: 'checkIn', header: 'Check In', tooltip: 'Check In', width: 'auto', type: 'text' },
  // { field: 'checkOut', header: 'Check Out', tooltip: 'Check Out', width: 'auto', type: 'text' },
  { field: 'adults', header: 'Adults', tooltip: 'Adults', width: 'auto', type: 'text' },
  { field: 'children', header: 'Children', tooltip: 'Children', width: 'auto', type: 'text' },
  // { field: 'deposit', header: 'Deposit', tooltip: 'Deposit', width: 'auto', type: 'bool' },
  { field: 'amount', header: 'D. Amount', tooltip: 'Deposit Amount', width: 'auto', type: 'text' },
  { field: 'transactionType', header: 'P. Trans Type', tooltip: 'Payment Transaction Type', width: '150px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' } },
  { field: 'parentId', header: 'Parent Id', width: 'auto', type: 'text' },
  // { field: 'groupId', header: 'Group Id', width: 'auto', type: 'text' },
  { field: 'remark', header: 'Remark', width: 'auto', type: 'text' },

]

const item = ref({
  paymentId: '0',
  paymentSource: null,
  reference: '',
  transactionDate: '',
  paymentStatus: null,
  client: null,
  agency: null,
  hotel: null,
  bankAccount: null,
  attachmentStatus: null,
  paymentAmount: 0,
  paymentBalance: '0',
  depositAmount: '0',
  depositBalance: '0',
  otherDeductions: '0',
  identified: '0',
  notIdentified: '',
  remark: '',
  status: '',
} as GenericObject)

const itemTemp = ref({
  paymentId: '0',
  paymentSource: null,
  reference: '',
  transactionDate: '',
  paymentStatus: null,
  client: null,
  agency: null,
  hotel: null,
  bankAccount: null,
  attachmentStatus: null,
  paymentAmount: 0,
  paymentBalance: '0',
  depositAmount: '0',
  depositBalance: '0',
  otherDeductions: '0',
  identified: '0',
  notIdentified: '',
  remark: '',
  status: '',
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit Payment' : 'New Payment'
})

const decimalRegex = /^\d+(\.\d{1,2})?$/

// const initialSchema = z.object(
//   {
//     amount: z
//       .string()
//       .min(1, { message: 'The amount field is required' })
//       .regex(decimalRegex, { message: 'The amount does not meet the correct format of n integer digits and 2 decimal digits' })
//       .refine(value => Number.parseFloat(value) <= item.value.paymentBalance, { message: 'The amount must be greater than zero and less or equal than Payment Balance' }),
//     paymentAmmount: z
//       .string()
//       .min(1, { message: 'The payment amount field is required' })
//       .regex(decimalRegex, { message: 'The payment amount does not meet the correct format of n integer digits and 2 decimal digits' })
//       .refine(value => Number.parseFloat(value) >= 1, { message: 'The payment amount field must be at least 1' })
//   },
// )

const decimalSchema = z.object(
  {
    amount: z
      .string()
      .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) <= item.value.paymentBalance), { message: 'The amount must be greater than zero and less or equal than Payment Balance' }),
    paymentAmmount: z
      .number({
        invalid_type_error: 'The payment amount field must be a number',
        required_error: 'The payment amount field is required',
      })
      .refine(value => value >= 1, { message: 'The payment amount field must be at least 1' })
  },
)

const fields: Array<FieldDefinitionType> = [
  {
    field: 'paymentId',
    header: 'Id',
    disabled: true,
    dataType: 'text',
    class: 'field col-12 md:col-3',
  },
  {
    field: 'client',
    header: 'Client',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('client'),
  },
  {
    field: 'paymentAmount',
    header: 'Payment Amount',
    dataType: 'number',
    disabled: false,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-1 required',
    validation: decimalSchema.shape.paymentAmmount
  },
  {
    field: 'depositAmount',
    header: 'Deposit Amount',
    dataType: 'number',
    disabled: true,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'identified',
    header: 'Identified',
    dataType: 'number',
    disabled: true,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'otherDeductions',
    header: 'Other Deductions',
    dataType: 'number',
    disabled: true,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-3',
  },
  {
    field: 'paymentSource',
    header: 'Payment Source',
    dataType: 'select',
    class: 'field col-12 md:col-1 required',
    validation: validateEntityStatus('payment source'),
  },
  {
    field: 'paymentStatus',
    header: 'Status',
    dataType: 'select',
    disabled: true,
    class: 'field col-12 md:col-1 required',
    validation: validateEntityStatus('payment status'),
  },
  {
    field: 'transactionDate',
    header: 'Bank Dep. Date',
    dataType: 'date',
    class: 'field col-12 md:col-1 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Bank Deposit Date field is required',
      invalid_type_error: 'The Bank Deposit Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Bank Deposit Date field cannot be greater than current date')

  },
  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    disabled: true,
    validation: validateEntityStatus('agency'),
  },
  {
    field: 'paymentBalance',
    header: 'Payment Balance',
    dataType: 'number',
    disabled: true,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'depositBalance',
    header: 'Deposit Balance',
    dataType: 'number',
    disabled: true,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'notIdentified',
    header: 'Not Identified',
    dataType: 'number',
    disabled: true,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'attachmentStatus',
    header: 'Attachment Status',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('attachment status'),
  },
  {
    field: 'reference',
    header: 'Reference No.',
    dataType: 'text',
    class: 'field col-12 md:col-3',
  },

  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'bankAccount',
    header: 'Bank Account',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('bank account'),
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'text',
    class: 'field col-12 md:col-3',
  },

  // {
  //   field: 'status',
  //   header: 'Active',
  //   dataType: 'check',
  //   class: 'field col-12 md:col-1 mt-3 mb-3',
  //   headerClass: 'mb-1',
  // },

]

const fieldPaymentDetails = ref<FieldDefinitionType[]>([
  {
    field: 'transactionType',
    header: 'Payment Transaction Type',
    dataType: 'select',
    class: 'field col-12',
    validation: validateEntityStatus('transaction category'),
  },
  {
    field: 'amount',
    header: 'Amount',
    dataType: 'text',
    disabled: false,
    helpText: `Max amount: ${item.value.paymentBalance.toString()}`,
    class: 'field col-12 required',
    validation: decimalSchema.shape.amount
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'textarea',
    class: 'field col-12',
  },
])

const itemDetails = ref({
  id: '',
  payment: '',
  transactionType: null,
  amount: '0',
  remark: '',
  status: '',
  oldAmount: '',
  paymentDetail: '',
  applyDepositValue: '0',
  children: [],
  childrenTotalValue: 0,
})

const itemDetailsTemp = ref({
  id: '',
  payment: '',
  transactionType: null,
  amount: '0',
  remark: '',
  status: '',
  oldAmount: '',
  paymentDetail: '',
  applyDepositValue: '0',
  children: [],
  childrenTotalValue: 0,
})

const options = ref({
  tableName: 'Payment Details',
  moduleApi: 'payment',
  uriApi: 'payment-detail',
  loading: false,
  actionsAsMenu: false,
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

function isObjectEmpty(obj) {
  return Object.keys(obj).length === 0
}

function getNameByActions(action: 'new-detail' | 'deposit-transfer' | 'split-deposit' | 'apply-deposit' | 'apply-payment' | undefined) {
  switch (action) {
    case 'new-detail':
      return itemDetails.value.id ? 'Edit Payment Details' : 'New Payment Details'
    case 'deposit-transfer':
      return 'New Deposit Detail'
    case 'split-deposit':
      return 'Split | New Payment Details'
    default:
      return 'New Payment Details'
  }
}

function clearForm() {
  item.value = JSON.parse(JSON.stringify(itemTemp.value))
  idItem.value = ''
  formReload.value++
}

function clearFormDetails() {
  itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
  actionOfModal.value = 'new-detail'
}

function openModalWithContentMenu($event) {
  if (!isObjectEmpty(objItemSelectedForRightClick.value)) {
    openDialogPaymentDetailsByAction(objItemSelectedForRightClick.value.id, 'apply-deposit')
  }
  if (!isObjectEmpty(objItemSelectedForRightClickApplyPayment.value)) {
    openDialogPaymentDetailsByAction(objItemSelectedForRightClickApplyPayment.value.id, 'apply-payment')
  }
}

function openDialogPaymentDetails(event: any) {
  if (event) {
    itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
    const objToEdit = paymentDetailsList.value.find(x => x.id === event.id)

    if (objToEdit) {
      itemDetails.value = { ...objToEdit }
    }

    onOffDialogPaymentDetail.value = true
  }
  actionOfModal.value = 'new-detail'

  const decimalSchema = z.object(
    {
      amount: z
        .string()
        .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) >= 0.01) && (Number.parseFloat((item.value.paymentBalance - Number.parseFloat(value)).toFixed(2).toString()) >= 0.01), { message: 'The amount must be greater than zero and less or equal than Payment Balance' })
    },
  )

  updateFieldProperty(fieldPaymentDetails.value, 'amount', 'validation', decimalSchema.shape.amount)
  updateFieldProperty(fieldPaymentDetails.value, 'amount', 'helpText', `Max amount: ${Math.abs(item.value.paymentBalance)}`)
  onOffDialogPaymentDetail.value = true
}

function openDialogPaymentDetailsByAction(idDetail: any = null, action: 'new-detail' | 'deposit-transfer' | 'split-deposit' | 'apply-deposit' | 'apply-payment' | undefined = undefined) {
  const idDetailTemp = JSON.parse(JSON.stringify(idDetail))
  if (action !== undefined) {
    actionOfModal.value = action
  }
  if (idDetailTemp && actionOfModal.value !== 'deposit-transfer') {
    itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
    const objToEditTemp = paymentDetailsList.value.find(x => x.id === (typeof idDetailTemp === 'object' ? idDetailTemp.id : idDetailTemp))

    const objToEdit = JSON.parse(JSON.stringify(objToEditTemp))

    if (objToEdit) {
      objToEdit.amount = Math.abs(objToEdit.amount).toString()
      objToEdit.oldAmount = objToEdit.amount.toString()
      objToEdit.amount = formatToTwoDecimalPlaces(objToEdit.amount)
      itemDetails.value = { ...objToEdit }
      itemDetails.value.paymentDetail = idDetailTemp

      if (actionOfModal.value === 'new-detail') {
        if (itemDetails.value?.transactionType?.cash === false && itemDetails.value?.transactionType?.deposit === false) {
          const decimalSchema = z.object(
            {
              amount: z
                .string()
                .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) > 0), { message: 'The amount must be greater than zero' })
            },
          )
          updateFieldProperty(fieldPaymentDetails.value, 'amount', 'validation', decimalSchema.shape.amount)
          updateFieldProperty(fieldPaymentDetails.value, 'amount', 'helpText', 'Max amount: ∞')
          // updateFieldProperty(fieldPaymentDetails.value, 'remark', 'disabled', false)
        }
        else {
          const decimalSchema = z.object(
            {
              amount: z
                .string()
                .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) >= 0.01) && (Number.parseFloat((item.value.paymentBalance - Number.parseFloat(value)).toFixed(2).toString()) >= 0.01), { message: 'The amount must be greater than zero and less or equal than Payment Balance' })
            },
          )

          updateFieldProperty(fieldPaymentDetails.value, 'amount', 'validation', decimalSchema.shape.amount)
          updateFieldProperty(fieldPaymentDetails.value, 'amount', 'helpText', `Max amount: ${Math.abs(item.value.paymentBalance)}`)
          // updateFieldProperty(fieldPaymentDetails.value, 'remark', 'disabled', false)
          // const decimalSchema = z.object(
          //   {
          //     amount: z
          //       .string()
          //       .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) > 0) && (Number.parseFloat(value) <= item.value.paymentBalance), { message: 'The amount must be greater than zero and lessor equal than Payment Balance' })
          //   },
          // )
          // updateFieldProperty(fieldPaymentDetails.value, 'amount', 'validation', decimalSchema.shape.amount)
          // updateFieldProperty(fieldPaymentDetails.value, 'amount', 'helpText', `Max amount: ${Math.abs(item.value.paymentBalance)}`)
        }
      }
      if (actionOfModal.value === 'split-deposit') {
        const amountString = objToEditTemp.amount
        const sanitizedAmount = amountString.replace(/,/g, '') // Elimina las comas
        const amountTemp = sanitizedAmount ? Math.abs(Number(sanitizedAmount)) : 0

        const minValueToApply = (amountTemp - 0.01).toFixed(2)
        const decimalSchema = z.object(
          {
            remark: z.string(),
            amount: z
              .string()
              .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) >= 0.01) && (Number.parseFloat((amountTemp - Number.parseFloat(value)).toFixed(2).toString()) >= 0.01), { message: 'Deposit Amount must be greather than zero and less or equal than the selected transaction amount' })
          }
        )
        updateFieldProperty(fieldPaymentDetails.value, 'remark', 'validation', decimalSchema.shape.remark)
        // updateFieldProperty(fieldPaymentDetails.value, 'remark', 'disabled', false)
        updateFieldProperty(fieldPaymentDetails.value, 'amount', 'validation', decimalSchema.shape.amount)
        updateFieldProperty(fieldPaymentDetails.value, 'amount', 'helpText', `Max amount: ${minValueToApply}`)
      }
      if (actionOfModal.value === 'apply-deposit') {
        const oldAmount = objToEditTemp.amount ? Math.abs(Number.parseFloat(objToEditTemp.amount.replace(/,/g, ''))) : 0

        const childrenTotalValue = itemDetails.value.childrenTotalValue

        const minValueToApply = (oldAmount - childrenTotalValue - 0.01).toFixed(2)

        const decimalSchema = z.object(
          {
            remark: z.string(),
            amount: z
              .string()
              .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) >= 0.01) && (Number.parseFloat(value) <= Number.parseFloat(minValueToApply)), { message: 'Deposit Amount must be greather than zero and less or equal than the selected transaction amount' })
          }
        )
        updateFieldProperty(fieldPaymentDetails.value, 'remark', 'validation', decimalSchema.shape.remark)
        // updateFieldProperty(fieldPaymentDetails.value, 'remark', 'disabled', false)
        updateFieldProperty(fieldPaymentDetails.value, 'amount', 'validation', decimalSchema.shape.amount)
        updateFieldProperty(fieldPaymentDetails.value, 'amount', 'helpText', `Max amount to apply: ${Number.parseFloat(minValueToApply)} | Initial amount: ${oldAmount}`)
      }
    }

    onOffDialogPaymentDetail.value = true
  }
  if (actionOfModal.value === 'deposit-transfer') {
    itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
    const decimalSchema = z.object(
      {
        remark: z.string(),
        amount: z
          .string()
          .refine(value => !Number.isNaN(Number.parseFloat(value)) && Number.parseFloat(value) >= 0.01 && (Number.parseFloat(value) <= item.value.paymentBalance), { message: 'The amount must be greater than zero and less or equal than Payment Balance' })
      }
    )
    // updateFieldProperty(fieldPaymentDetails.value, 'remark', 'validation', decimalSchema.shape.remark)
    // updateFieldProperty(fieldPaymentDetails.value, 'remark', 'disabled', true)
    updateFieldProperty(fieldPaymentDetails.value, 'amount', 'validation', decimalSchema.shape.amount)
    updateFieldProperty(fieldPaymentDetails.value, 'amount', 'helpText', `Max amount: ${item.value.paymentBalance}`)
  }

  onOffDialogPaymentDetail.value = true
}

function dialogPaymentDetailSummary() {
  onOffDialogPaymentDetailSummary.value = true
}
function goToList() {
  navigateTo('/payment')
}

function goToForm(item: string) {
  navigateTo({ path: '/payment/form', query: { id: item } })
}

function navigateToInvoice($event: any) {
  if (objItemSelectedForRightClickNavigateToInvoice.value?.manageBooking.invoice?.id) {
    const url = `/invoice/edit/${objItemSelectedForRightClickNavigateToInvoice.value.manageBooking.invoice.id}`
    window.open(url, '_blank')
  }
}

function transformObjects(inputArray: FileObject[]) {
  return inputArray.map(item => ({
    status: item.resourceType.status,
    resource: item.resource || 'default-resource-id', // Puedes cambiar "default-resource-id" por un valor por defecto si es necesario
    employee: item.employee,
    resourceType: item.resourceType.id,
    attachmentType: item.attachmentType.id,
    fileName: item.fileName,
    fileWeight: item.fileSize,
    path: item.path,
    remark: item.remark
  }))
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.paymentSource = Object.prototype.hasOwnProperty.call(payload.paymentSource, 'id') ? payload.paymentSource.id : payload.paymentSource
    payload.transactionDate = payload.transactionDate !== null ? dayjs(payload.transactionDate).format('YYYY-MM-DD') : ''
    payload.paymentStatus = Object.prototype.hasOwnProperty.call(payload.paymentStatus, 'id') ? payload.paymentStatus.id : payload.paymentStatus
    payload.attachmentStatus = Object.prototype.hasOwnProperty.call(payload.attachmentStatus, 'id') ? payload.attachmentStatus.id : payload.attachmentStatus
    payload.client = Object.prototype.hasOwnProperty.call(payload.client, 'id') ? payload.client.id : payload.client
    payload.agency = Object.prototype.hasOwnProperty.call(payload.agency, 'id') ? payload.agency.id : payload.agency
    payload.hotel = Object.prototype.hasOwnProperty.call(payload.hotel, 'id') ? payload.hotel.id : payload.hotel
    payload.bankAccount = Object.prototype.hasOwnProperty.call(payload.bankAccount, 'id') ? payload.bankAccount.id : payload.bankAccount
    payload.paymentAmount = Number(payload.paymentAmount)
    payload.employee = userData?.value?.user?.userId || ''
    payload.status = statusToString(payload.status)
    if (attachmentList.value.length > 0) {
      payload.attachments = transformObjects(attachmentList.value)
    }
    else {
      delete payload.attachments
    }

    // delete payload.paymentSource
    // delete payload.reference
    // delete payload.transactionDate
    // delete payload.paymentStatus
    // delete payload.client
    // delete payload.agency
    // delete payload.hotel
    // delete payload.bankAccount
    // delete payload.attachmentStatus
    // delete payload.paymentAmount
    delete payload.paymentBalance
    delete payload.depositAmount
    delete payload.depositBalance
    delete payload.otherDeductions
    delete payload.identified
    delete payload.notIdentified
    delete payload.paymentId
    // delete payload.remark

    delete payload.id
    delete payload.event
    // delete payload.bankAccount

    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)

    if (response && response.payment) {
      // paymentId
      idItem.value = response.payment.id
      toast.add({ severity: 'info', summary: 'Confirmed', detail: `The payment id ${response.payment.paymentId} was created successfully`, life: 10000 })
      goToForm(idItem.value)
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was not successful', life: 10000 })
    }
    loadingSaveAll.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  // const successOperation = true
  if (idItem.value) {
    try {
      await updateItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      // successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
      await createItem(item)
    }
    catch (error: any) {
      // successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  // if (successOperation) {
  //   clearForm()
  // }
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)

      if (response) {
        item.value.id = id
        item.value.paymentId = response.paymentId
        item.value.reference = response.reference
        item.value.transactionDate = response.transactionDate
        const newDate = new Date(response.transactionDate)
        newDate.setDate(newDate.getDate() + 1)
        item.value.transactionDate = newDate || null

        // item.value.paymentAmount = formatToTwoDecimalPlaces(response.paymentAmount)
        item.value.paymentAmount = response.paymentAmount
        item.value.paymentBalance = formatToTwoDecimalPlaces(response.paymentBalance)
        item.value.depositAmount = formatToTwoDecimalPlaces(response.depositAmount)
        item.value.depositBalance = formatToTwoDecimalPlaces(response.depositBalance)
        item.value.otherDeductions = formatToTwoDecimalPlaces(response.otherDeductions)
        item.value.identified = formatToTwoDecimalPlaces(response.identified)
        item.value.notIdentified = formatToTwoDecimalPlaces(response.notIdentified)
        item.value.remark = response.remark

        const clientTemp = response.client
          ? {
              id: response.client.id,
              name: `${response.client.code} - ${response.client.name}`,
              status: response.client.status
            }
          : null
        clientList.value = [clientTemp]
        item.value.client = clientTemp

        const agencyTemp = response.agency
          ? {
              id: response.agency.id,
              name: `${response.agency.code} - ${response.agency.name}`,
              status: response.agency.status
            }
          : null
        agencyList.value = [agencyTemp]
        item.value.agency = agencyTemp

        const hotelTemp = response.hotel
          ? {
              id: response.hotel.id,
              name: `${response.hotel?.code} - ${response.hotel?.name}`,
              status: response.hotel?.status,
              applyByTradingCompany: response.hotel?.applyByTradingCompany,
              manageTradingCompany: response.hotel?.manageTradingCompany

            }
          : null
        hotelList.value = [hotelTemp]
        item.value.hotel = hotelTemp

        const bankAccountTemp = response.bankAccount
          ? {
              id: response.bankAccount.id,
              name: `${response.bankAccount.nameOfBank} - ${response.bankAccount.accountNumber}`,
              status: response.bankAccount.status
            }
          : null
        bankAccountList.value = [bankAccountTemp]
        item.value.bankAccount = bankAccountTemp
        // bankAccountList.value = typeof response.bankAccount === 'object' ? [{ id: response.bankAccount.id, name: response.bankAccount.accountNumber, status: response.bankAccount.status }] : []
        // item.value.bankAccount = typeof response.bankAccount === 'object' ? { id: response.bankAccount.id, name: response.bankAccount.accountNumber, status: response.bankAccount.status } : response.bankAccount

        const attachmentStatusTemp = response.attachmentStatus
          ? {
              id: response.attachmentStatus.id,
              name: `${response.attachmentStatus.code} - ${response.attachmentStatus.name}`,
              status: response.attachmentStatus.status
            }
          : null
        attachmentStatusList.value = [attachmentStatusTemp]
        item.value.attachmentStatus = attachmentStatusTemp

        const paymentStatusTemp = response.paymentStatus
          ? {
              id: response.paymentStatus.id,
              name: `${response.paymentStatus.code} - ${response.paymentStatus.name}`,
              status: response.paymentStatus.status
            }
          : null
        paymentStatusList.value = [paymentStatusTemp]
        item.value.paymentStatus = paymentStatusTemp

        const paymentSourceTemp = response.paymentSource
          ? {
              id: response.paymentSource.id,
              name: `${response.paymentSource.code} - ${response.paymentSource.name}`,
              status: response.paymentSource.status
            }
          : null
        paymentSourceList.value = [paymentSourceTemp]
        item.value.paymentSource = paymentSourceTemp
      }
      // debugger
      // item.value = { ...formatNumbersInObject(item.value, ['paymentAmount', 'depositAmount', 'otherDeductions', 'identified', 'notIdentified']) }

      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      if (actionOfModal.value !== 'split-deposit') {
        updateFieldProperty(fieldPaymentDetails.value, 'amount', 'helpText', `Max amount: ${item.value.paymentBalance}`)
      }
      formReload.value += 1

      await getListPaymentDetail()
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Account type methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function getListPaymentDetail() {
  const count: SubTotals = { depositAmount: 0 }
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    paymentDetailsList.value = []
    const newListItems = []

    const objFilter = payload.value.filter.find(item => item.key === 'payment.id')

    if (objFilter) {
      objFilter.value = idItem.value
    }
    else {
      payload.value.filter.push({
        key: 'payment.id',
        operator: 'EQUALS',
        value: idItem.value,
        logicalOperation: 'AND'
      })
    }

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(paymentDetailsList.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        count.depositAmount += iterator.amount
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
        iterator.deposit = iterator.transactionType.deposit
        iterator.transactionType.name = `${iterator.transactionType.code} - ${iterator.transactionType.name}`

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

        if (iterator?.manageBooking?.invoice?.invoiceType === 'CREDIT' && iterator?.transactionType?.cash) {
          iterator.bookingId = iterator.manageBooking?.bookingId?.toString()
          iterator.invoiceNumber = iterator.manageBooking?.invoice?.invoiceNumber?.toString()
        }
        else {
          iterator.bookingId = iterator.manageBooking?.bookingId
          iterator.invoiceNumber = iterator.manageBooking?.invoice?.invoiceNumber
        }

        // if (iterator?.manageBooking?.invoice?.invoiceType === 'CREDIT' && !iterator?.transactionType?.cash) {
        //   iterator.bookingId = iterator?.manageBooking?.parentResponse?.bookingId?.toString()
        //   iterator.invoiceNumber = iterator?.manageBooking?.invoice?.parent?.invoiceNumber?.toString()
        //   iterator.bookingId = iterator?.manageBooking.parentResponse?.bookingId
        //   iterator.invoiceNumber = iterator?.manageBooking.invoice?.parent?.invoiceNumber
        // }
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    paymentDetailsList.value = [...paymentDetailsList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    subTotals.value = { ...count }
  }
}

function disabledDeleteForPaymentWithChildren(id: string): boolean {
  const item = paymentDetailsList.value.find(item => item.id === id)
  return item && item.children && item.children.length > 0
}

function hasDepositTransaction(mainId: string, items: TransactionItem[]): boolean {
  // Buscar el objeto principal por su id

  const mainItem = items.find(item => item.id === mainId)
  if (!mainItem) {
    return false // Si no se encuentra el objeto principal, devolver false
  }

  const hasDeposit = (item: TransactionItem): boolean => {
    // Verificar si es de tipo deposit
    // Verificar que no se le haya aplicado deposito anteriormente

    // if (item.transactionType.deposit) {
    //   return true
    // }

    // // Verificar en los hijos del objeto
    // if (item.children && item.children.length > 0) {
    //   return item.children.some(child => hasDeposit(child))
    // }

    return item.transactionType.deposit && item.hasApplyDeposit === false
  }
  let amount = Number.parseFloat(mainItem.amount)
  amount = Number.isNaN(amount) ? 0 : amount
  amount = Math.abs(amount)
  return hasDeposit(mainItem) && Math.abs(amount) > 0.01 && (mainItem?.children?.length === 0 || mainItem?.children === undefined || mainItem?.children === null)
}

function hasDepositSummaryTransaction(items: TransactionItem[]): boolean {
  let count = 0
  for (const item of items) {
    if (item.transactionType.deposit) {
      count++
    }
  }

  return count > 0
}

async function createPaymentDetails(item: { [key: string]: any }) {
  if (item) {
    // loadingSaveAll.value = true
    const payload: { [key: string]: any } = JSON.parse(JSON.stringify(item))

    const payloadTemp: { [key: string]: any } = JSON.parse(JSON.stringify(item))

    payload.payment = idItem.value || ''
    payload.amount = Number.parseFloat(payload.amount)
    payload.employee = userData?.value?.user?.userId || ''
    payload.transactionType = Object.prototype.hasOwnProperty.call(payload.transactionType, 'id') ? payload.transactionType.id : payload.transactionType
    if (payload.remark === '') {
      payload.remark = payloadTemp?.transactionType?.defaultRemark
    }

    switch (actionOfModal.value) {
      case 'apply-deposit':{
        const payload: { [key: string]: any } = JSON.parse(JSON.stringify(item))

        payload.paymentDetail = JSON.parse(JSON.stringify(idPaymentDetail.value))
        payload.status = 'ACTIVE'
        payload.payment = idItem.value || ''
        payload.amount = Number.parseFloat(payload.amount)
        payload.employee = userData?.value?.user?.userId || ''
        payload.transactionType = Object.prototype.hasOwnProperty.call(payload.transactionType, 'id') ? payload.transactionType.id : payload.transactionType
        if (payload.remark === '') {
          payload.remark = payloadTemp?.transactionType?.defaultRemark
        }
        confApiPaymentDetail.uriApi = 'payment-detail/apply-deposit'
        await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payload)
        actionOfModal.value = 'new-detail'
      }
        break

      case 'new-detail': {
        const payloadNewDetail: { [key: string]: any } = JSON.parse(JSON.stringify(item))
        payloadNewDetail.paymentDetail = JSON.parse(JSON.stringify(idPaymentDetail.value))
        payloadNewDetail.status = 'ACTIVE'
        payloadNewDetail.payment = idItem.value || ''
        payloadNewDetail.amount = Number.parseFloat(payloadNewDetail.amount)
        payloadNewDetail.employee = userData?.value?.user?.userId || ''
        payloadNewDetail.transactionType = Object.prototype.hasOwnProperty.call(payloadNewDetail.transactionType, 'id') ? payloadNewDetail.transactionType.id : payloadNewDetail.transactionType
        if (payloadNewDetail.remark === '') {
          payloadNewDetail.remark = payloadTemp?.transactionType?.defaultRemark
        }
        confApiPaymentDetail.uriApi = 'payment-detail'
        await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payload)
        actionOfModal.value = 'new-detail'
      }
        break
      case 'deposit-transfer':
        confApiPaymentDetail.uriApi = 'payment-detail'
        await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payload)
        actionOfModal.value = 'new-detail'
        break

      case 'split-deposit':{
        const payloadSplit = {
          amount: item.amount.trim() !== '' && !Number.isNaN(item.amount) ? Number(item.amount) : 0,
          paymentDetail: JSON.parse(JSON.stringify(idPaymentDetail.value)),
          remark: item.remark === '' ? item.transactionType.defaultRemark : item.remark,
          employee: userData?.value?.user?.userId || '',
          transactionType: Object.prototype.hasOwnProperty.call(item.transactionType, 'id') ? item.transactionType.id : item.transactionType,
          status: 'ACTIVE'
        }
        confApiPaymentDetail.uriApi = 'payment-detail/split'
        await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payloadSplit)
        isSplitAction.value = false
        actionOfModal.value = 'new-detail'
        break
      }
    }

    // if (actionOfModal.value === 'apply-deposit') {
    //   confApiPaymentDetail.uriApi = 'payment-detail/apply-deposit'
    //   delete payload.payment
    //   await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payload)
    //   actionOfModal.value = 'new-detail'
    // }
    // else if (actionOfModal.value === 'new-detail' || actionOfModal.value === 'deposit-transfer') {
    //   confApiPaymentDetail.uriApi = 'payment-detail'
    //   await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payload)
    //   actionOfModal.value = 'new-detail'
    // }
    // else if (actionOfModal.value === 'split-deposit') {
    //   const payloadSplit = {
    //     amount: item.amount.trim() !== '' && !Number.isNaN(item.amount) ? Number(item.amount) : 0,
    //     paymentDetail: item.id,
    //     remark: item.remark,
    //     transactionType: Object.prototype.hasOwnProperty.call(item.transactionType, 'id') ? item.transactionType.id : item.transactionType,
    //     status: 'ACTIVE'
    //   }
    //   confApiPaymentDetail.uriApi = 'payment-detail/split'
    //   await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payloadSplit)
    //   isSplitAction.value = false
    //   actionOfModal.value = 'new-detail'
    // }

    onOffDialogPaymentDetail.value = false
    clearFormDetails()
  }
}

async function updatePaymentDetails(item: { [key: string]: any }) {
  if (item) {
    // loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.payment = idItem.value || ''
    payload.amount = Number.parseFloat(payload.amount)
    payload.employee = userData?.value?.user?.userId || ''
    payload.transactionType = Object.prototype.hasOwnProperty.call(payload.transactionType, 'id') ? payload.transactionType.id : payload.transactionType

    try {
      switch (actionOfModal.value) {
        case 'new-detail':
          confApiPaymentDetail.uriApi = 'payment-detail'
          await GenericService.update(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, item.id, payload)
          break
        case 'deposit-transfer':
          confApiPaymentDetail.uriApi = 'payment-detail'
          await GenericService.update(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, item.id, payload)
          break
        case 'split-deposit': {
          const payloadSplit = {
            amount: item.amount.trim() !== '' && !Number.isNaN(item.amount) ? Number(item.amount) : 0,
            paymentDetail: item.id,
            remark: item.remark,
            transactionType: Object.prototype.hasOwnProperty.call(item.transactionType, 'id') ? item.transactionType.id : item.transactionType,
            status: 'ACTIVE'
          }
          confApiPaymentDetail.uriApi = 'payment-detail/split'
          await GenericService.update(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, item.id, payloadSplit)
          break
        }
        case 'apply-deposit':{
          const payloadApplyDeposit = {
            amount: item.amount.trim() !== '' && !Number.isNaN(item.amount) ? Number(item.amount) : 0,
            paymentDetail: item.id,
            remark: item.remark,
            employee: userData?.value?.user?.userId || '',
            transactionType: Object.prototype.hasOwnProperty.call(item.transactionType, 'id') ? item.transactionType.id : item.transactionType,
            status: 'ACTIVE'
          }
          confApiPaymentDetail.uriApi = 'payment-detail/apply-deposit'
          await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payloadApplyDeposit)
          actionOfModal.value = 'new-detail'
          break
        }
        default:
          throw new Error('Invalid action')
      }

      onOffDialogPaymentDetail.value = false
      clearFormDetails()
    }
    catch (error) {
      console.error('Error updating payment details:', error)
      // Handle error (e.g., show error message to the user)
    }
    finally {
      // loadingSaveAll.value = false
      actionOfModal.value = 'new-detail'
    }
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.paymentSource = Object.prototype.hasOwnProperty.call(payload.paymentSource, 'id') ? payload.paymentSource.id : payload.paymentSource
  payload.transactionDate = payload.transactionDate !== null ? dayjs(payload.transactionDate).format('YYYY-MM-DD') : ''
  payload.paymentStatus = Object.prototype.hasOwnProperty.call(payload.paymentStatus, 'id') ? payload.paymentStatus.id : payload.paymentStatus
  payload.attachmentStatus = Object.prototype.hasOwnProperty.call(payload.attachmentStatus, 'id') ? payload.attachmentStatus.id : payload.attachmentStatus
  payload.client = Object.prototype.hasOwnProperty.call(payload.client, 'id') ? payload.client.id : payload.client
  payload.agency = Object.prototype.hasOwnProperty.call(payload.agency, 'id') ? payload.agency.id : payload.agency
  payload.hotel = Object.prototype.hasOwnProperty.call(payload.hotel, 'id') ? payload.hotel.id : payload.hotel
  payload.bankAccount = Object.prototype.hasOwnProperty.call(payload.bankAccount, 'id') ? payload.bankAccount.id : payload.bankAccount
  payload.paymentAmount = Number(payload.paymentAmount)
  payload.status = statusToString(payload.status)
  payload.employee = userData?.value?.user?.userId || ''

  // delete payload.paymentSource
  // delete payload.reference
  // delete payload.transactionDate
  // delete payload.paymentStatus
  // delete payload.client
  // delete payload.agency
  // delete payload.hotel
  // delete payload.bankAccount
  // delete payload.attachmentStatus
  // delete payload.paymentAmount
  delete payload.paymentBalance
  delete payload.depositAmount
  delete payload.depositBalance
  delete payload.otherDeductions
  delete payload.identified
  delete payload.notIdentified
  delete payload.paymentId
  // delete payload.remark

  delete payload.id
  delete payload.event
  // delete payload.bankAccount
  const id = route?.query?.id.toString()
  await GenericService.update(confApi.moduleApi, confApi.uriApi, id || '', payload)
  hasBeenEdited.value += 1
}

async function saveAndReload(item: { [key: string]: any }) {
  item.applyPayment = payloadToApplyPayment.value.applyPayment || false
  item.booking = payloadToApplyPayment.value.booking || ''
  try {
    // if (payloadToApplyPayment.value.applyPayment) {
    //   item.applyPayment = true
    //   item.booking = payloadToApplyPayment.value.booking
    // }
    if (actionOfModal.value === 'apply-deposit') {
      // item.paymentDetail = item.id

      await createPaymentDetails(item)
    }
    else if (item?.id && actionOfModal.value === 'split-deposit') {
      await createPaymentDetails(item)
    }
    else if (item?.id) {
      await updatePaymentDetails(item)
    }
    else {
      // if (payloadToApplyPayment.value.applyPayment) {
      //   item.applyPayment = true
      //   item.booking = payloadToApplyPayment.value.booking
      // }
      await createPaymentDetails(item)
    }
    itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
    await getItemById(idItem.value)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
}

function requireConfirmationToSave(item: any) {
  confirm.require({
    target: submitEvent.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      await saveItem(item)
    },
    reject: () => {}
  })
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
    accept: async () => {
      // await deleteItem(idItem.value)
    },
    reject: () => {}
  })
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
  defaults?: boolean
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  defaults?: boolean
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    defaults: data?.defaults || false
  }
}

async function getPaymentSourceList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  paymentSourceList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

async function getDefaultPaymentSourceList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  const defaultPaymentSourceList = ref<ListItem[]>([])
  const filter: FilterCriteria[] = [
    {
      key: 'code',
      logicalOperation: 'AND',
      operator: 'LIKE',
      value: 'bk',
    },
    {
      key: 'name',
      logicalOperation: 'AND',
      operator: 'LIKE',
      value: 'bank',
    }
  ]
  defaultPaymentSourceList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...filter], queryObj, mapFunction)
  if (defaultPaymentSourceList.value.length > 0) {
    item.value.paymentSource = defaultPaymentSourceList.value[0]
  }
}

function mapFunctionForClient(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status
  }
}

async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  clientList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunctionForClient, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  agencyList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

async function getAgencyListTemp(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

interface DataListItemHotel {
  id: string
  name: string
  code: string
  status: string
  defaults?: boolean
  applyByTradingCompany?: boolean
}

interface ListItemHotel {
  id: string
  name: string
  status: boolean | string
  defaults?: boolean
  applyByTradingCompany?: boolean
}
function mapFunctionHotel(data: DataListItemHotel): ListItemHotel {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    defaults: data?.defaults || false,
    applyByTradingCompany: data.applyByTradingCompany
  }
}

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  hotelList.value = await getDataList<DataListItemHotel, ListItemHotel>(moduleApi, uriApi, filter, queryObj, mapFunctionHotel, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

async function getHotelListTemp(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

async function getPaymentStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  paymentStatusList.value = await getDataList(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

async function getDefaultPaymentStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  const defaultPaymentStatusList = ref<ListItem[]>([])
  const filter: FilterCriteria[] = [
    {
      key: 'defaults',
      logicalOperation: 'AND',
      operator: 'LIKE',
      value: true,
    }
  ]
  defaultPaymentStatusList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...filter], queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
  if (defaultPaymentStatusList.value.length > 0) {
    item.value.paymentStatus = defaultPaymentStatusList.value[0]
  }
}

interface DataListItemForBankAccount {
  id: string
  accountNumber: string
  manageBank: {
    id: string
    name: string
    code: string
    status: string
  }
  status: string
}

interface ListItemForBankAccount {
  id: string
  name: string
  status: boolean | string
}

function mapFunctionForBankAccountList(data: DataListItemForBankAccount): ListItemForBankAccount {
  return {
    id: data.id,
    name: `${data.manageBank.name} - ${data.accountNumber}`,
    status: data.status
  }
}
async function getBankAccountList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  bankAccountList.value = await getDataList<DataListItemForBankAccount, ListItemForBankAccount>(moduleApi, uriApi, filter, queryObj, mapFunctionForBankAccountList)
}

interface DataListItemForAttachmentStatus {
  id: string
  name: string
  code: string
  status: string
}

interface ListItemForAttachmentStatus {
  id: string
  name: string
  status: boolean | string
}

function mapFunctionForAttachmentStatusList(data: DataListItemForAttachmentStatus): ListItemForAttachmentStatus {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status
  }
}
async function getAttachmentStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  attachmentStatusList.value = await getDataList<DataListItemForAttachmentStatus, ListItemForAttachmentStatus>(moduleApi, uriApi, filter, queryObj, mapFunctionForAttachmentStatusList, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

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

// Attachments
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

async function rowSelected(rowData: any) {
  if (rowData !== null && rowData !== undefined && rowData !== '') {
    idItemDetail.value = rowData
    idPaymentDetail.value = rowData

    enableSplitAction.value = hasDepositTransaction(rowData, paymentDetailsList.value)
  }
  else {
    idItemDetail.value = ''
    isSplitAction.value = false
    actionOfModal.value = 'new-detail'
    enableSplitAction.value = false
  }
}

function onCloseDialog($event: any) {
  if ($event === false) {
    isSplitAction.value = false
    actionOfModal.value = 'new-detail'
    payloadToApplyPayment.value.applyPayment = false
    payloadToApplyPayment.value.booking = ''
  }
  itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
  dialogPaymentDetailFormReload.value += 1
  onOffDialogPaymentDetail.value = $event
}

function onCloseDialogSummary($event: any) {
  onOffDialogPaymentDetailSummary.value = $event
}

async function loadDefaultsValues() {
  updateFieldProperty(fields, 'paymentStatus', 'disabled', true)
  await getDefaultPaymentSourceList(objApis.value.paymentSource.moduleApi, objApis.value.paymentSource.uriApi, {
    query: '',
    keys: ['name', 'code'],
  })
  await getDefaultPaymentStatusList(objApis.value.paymentStatus.moduleApi, objApis.value.paymentStatus.uriApi, {
    query: '',
    keys: ['name', 'code'],
  })
  const newDate = new Date()
  item.value.transactionDate = newDate

  const filter: FilterCriteria[] = [
    {
      key: 'defaults',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: true,
    },
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
    },
  ]
  await getAttachmentStatusList(objApis.value.attachmentStatus.moduleApi, objApis.value.attachmentStatus.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filter)
  item.value.attachmentStatus = attachmentStatusList.value.length > 0 ? attachmentStatusList.value[0] : null
  formReload.value++
}

async function handleSave(event: any) {
  if (event) {
    await saveItem(event)
    forceSave.value = false
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
      objFilter.value = route.query.id.toString() || ''
    }
    else {
      payloadHistory.value.filter.push({
        key: 'payment.id',
        operator: 'EQUALS',
        value: route.query.id.toString() || '',
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
      iterator.paymentStatus = iterator.status

      if (Object.prototype.hasOwnProperty.call(iterator, 'employee')) {
        if (iterator.employee.firstName && iterator.employee.lastName) {
          iterator.employee = { id: iterator.employee?.id, name: `${iterator.employee?.firstName} ${iterator.employee?.lastName}` }
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

async function applyPaymentGetList(amountComingOfForm: any = null) {
  if (applyPaymentOptions.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    applyPaymentOptions.value.loading = true
    applyPaymentList.value = []
    const newListItems = []

    const validNumber = detailItemForApplyPayment.value?.amount
      ? detailItemForApplyPayment.value.amount.replace(/,/g, '')
      : amountComingOfForm ? amountComingOfForm.replace(/,/g, '') : 0

    if (amountComingOfForm) {
      isApplyPaymentFromTheForm.value = true
    }
    else {
      isApplyPaymentFromTheForm.value = false
    }

    // Si existe un filtro con dueAmount, solo lo modificamos y si no existe se crea
    const objFilter = applyPaymentPayload.value.filter.find(item => item.key === 'dueAmount')

    if (objFilter) {
      objFilter.value = validNumber.toString()
    }
    else {
      applyPaymentPayload.value.filter.push({
        key: 'dueAmount',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: validNumber.toString(),
        logicalOperation: 'AND'
      })
    }
    // Validacion para busar por por las agencias
    const filter: FilterCriteria[] = [
      {
        key: 'client.id',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: item.value.client.id,
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
      const objFilter = applyPaymentPayload.value.filter.find(item => item.key === 'invoice.agency.id')

      if (objFilter) {
        objFilter.value = listAgenciesForApplyPayment.map(item => item.id)
      }
      else {
        applyPaymentPayload.value.filter.push({
          key: 'invoice.agency.id',
          operator: 'IN',
          value: listAgenciesForApplyPayment.map(item => item.id),
          logicalOperation: 'AND'
        })
      }
    }

    // Validacion para bucsar por los hoteles
    if (item.value.hotel && item.value.hotel.id) {
      if (item.value.hotel.applyByTradingCompany) {
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
            value: item.value.hotel?.manageTradingCompany,
          },
        ]
        const objQueryToSearch = {
          query: '',
          keys: ['name', 'code'],
        }

        let listHotelsForApplyPayment: any[] = []
        listHotelsForApplyPayment = await getHotelListTemp(objApis.value.hotel.moduleApi, objApis.value.hotel.uriApi, objQueryToSearch, filter)

        if (listHotelsForApplyPayment.length > 0) {
          const objFilter = applyPaymentPayload.value.filter.find(item => item.key === 'invoice.hotel.id')

          if (objFilter) {
            objFilter.value = listHotelsForApplyPayment.map(item => item.id)
          }
          else {
            applyPaymentPayload.value.filter.push({
              key: 'invoice.hotel.id',
              operator: 'IN',
              value: listHotelsForApplyPayment.map(item => item.id),
              logicalOperation: 'AND'
            })
          }
        }
      }
      else {
        const objFilter = applyPaymentPayload.value.filter.find(item => item.key === 'invoice.hotel.id')

        if (objFilter) {
          objFilter.value = item.value.hotel.id
        }
        else {
          applyPaymentPayload.value.filter.push({
            key: 'invoice.hotel.id',
            operator: 'EQUALS',
            value: item.value.hotel.id,
            logicalOperation: 'AND'
          })
        }
      }
    }
    const response = await GenericService.search(applyPaymentOptions.value.moduleApi, applyPaymentOptions.value.uriApi, applyPaymentPayload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    applyPaymentPagination.value.page = page
    applyPaymentPagination.value.limit = size
    applyPaymentPagination.value.totalElements = totalElements
    applyPaymentPagination.value.totalPages = totalPages

    const existingIds = new Set(applyPaymentList.value.map(item => item.id))

    for (const iterator of dataList) {
      iterator.invoiceId = iterator.invoice?.invoiceId.toString()
      iterator.checkIn = iterator.checkIn ? dayjs(iterator.checkIn).format('YYYY-MM-DD') : null
      iterator.checkOut = iterator.checkOut ? dayjs(iterator.checkOut).format('YYYY-MM-DD') : null
      iterator.bookingAmount = iterator.invoiceAmount?.toString()
      iterator.bookingBalance = iterator.dueAmount?.toString()
      // iterator.paymentStatus = iterator.status

      // if (Object.prototype.hasOwnProperty.call(iterator, 'employee')) {
      //   if (iterator.employee.firstName && iterator.employee.lastName) {
      //     iterator.employee = { id: iterator.employee?.id, name: `${iterator.employee?.firstName} ${iterator.employee?.lastName}` }
      //   }
      // }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    applyPaymentList.value = [...applyPaymentList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    applyPaymentOptions.value.loading = false
  }
}

async function openDialogStatusHistory() {
  openDialogHistory.value = true
  await historyGetList()
}

async function openModalApplyPayment($event: any) {
  openDialogApplyPayment.value = true
  await applyPaymentGetList($event.amount)
}

async function openDialogImportExcel(idItem: string) {
  if (idItem) {
    navigateTo({ path: '/payment/import-detail', query: { paymentId: idItem } })
  }
}

async function applyPaymentParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, applyPaymentColumns.value)
  // if (parseFilter && parseFilter?.length > 0) {
  //   for (let i = 0; i < parseFilter?.length; i++) {
  //     if (parseFilter[i]?.key === 'paymentStatus') {
  //       parseFilter[i].key = 'status'
  //     }
  //     if (parseFilter[i]?.key === 'employee') {
  //       parseFilter[i].key = 'employee.id'
  //     }
  //   }
  // }
  applyPaymentPayload.value.filter = [...parseFilter || []]
  applyPaymentGetList()
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

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...parseFilter || []]
  getListPaymentDetail()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'transactionType') {
      event.sortField = 'transactionType.name'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
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

async function deleteItem(id: string) {
  if (!id) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was failed', life: 3000 })
    return
  }
  try {
    loadingDelete.value = true
    let customUrl = ''
    if (userData?.value?.user?.userId) {
      customUrl = userData?.value?.user?.userId || ''
    }

    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id, 'employee', customUrl)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    if (route?.query?.id) {
      const id = route.query.id.toString()
      await getItemById(id)
    }
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
    idItemDetail.value = ''
  }
}

async function paymentPrint(event: any) {
  try {
    loadingPrintDetail.value = true
    let nameOfPdf = ''
    const payloadTemp = {
      paymentId: route?.query?.id ? route?.query?.id.toString() : '',
      paymentType: '',
    }
    // En caso de que solo este marcado el paymentAndDetails
    if (
      event && event.paymentAndDetails
      && (event.allPaymentsSupport === false && event.invoiceRelated === false && event.invoiceRelatedWithSupport === false && event.paymentSupport === false)
    ) {
      payloadTemp.paymentType = 'PAYMENT_DETAILS'
      nameOfPdf = `payment-details-${dayjs().format('YYYY-MM-DD')}.pdf`
    }

    if (
      event && event.paymentAndDetails && event.paymentSupport === true
      && (event.invoiceRelated === false && event.invoiceRelatedWithSupport === false && event.allPaymentsSupport === false)
    ) {
      payloadTemp.paymentType = 'PAYMENT_SUPPORT'
      nameOfPdf = `payment-support-${dayjs().format('YYYY-MM-DD')}.pdf`
    }

    if (
      event && event.paymentAndDetails && event.allPaymentsSupport === true
      && (event.invoiceRelated === false && event.invoiceRelatedWithSupport === false && event.paymentSupport === false)
    ) {
      payloadTemp.paymentType = 'ALL_SUPPORT'
      nameOfPdf = `payment-all-support-${dayjs().format('YYYY-MM-DD')}.pdf`
    }

    if (
      event && event.paymentAndDetails && event.invoiceRelated === true
      && (event.allPaymentsSupport === false && event.invoiceRelatedWithSupport === false && event.paymentSupport === false)
    ) {
      payloadTemp.paymentType = 'INVOICE_RELATED'
      nameOfPdf = `payment-invoice-related-${dayjs().format('YYYY-MM-DD')}.pdf`
    }

    if (
      event && event.paymentAndDetails && event.invoiceRelatedWithSupport === true
      && (event.allPaymentsSupport === false && event.invoiceRelated === false && event.paymentSupport === false)
    ) {
      payloadTemp.paymentType = 'INVOICE_RELATED_SUPPORT'
      nameOfPdf = `payment-invoice-related-support-${dayjs().format('YYYY-MM-DD')}.pdf`
    }

    const response: any = await GenericService.create(confApiPaymentDetailPrint.moduleApi, confApiPaymentDetailPrint.uriApi, payloadTemp)

    const url = window.URL.createObjectURL(new Blob([response]))
    const a = document.createElement('a')
    a.href = url
    a.download = nameOfPdf // Nombre del archivo que se descargará
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

  // generateStyledPDF()
}

async function closeDialogPrint() {
  itemPrint.value = JSON.parse(JSON.stringify(itemTempPrint.value))
  openPrint.value = false
}

function onRowContextMenu(event: any) {
  detailItemForApplyPayment.value = event?.data

  idPaymentDetail.value = event?.data?.id
  let minValueToApplyDeposit = 0
  const amount = Number.parseFloat(event.data.amount) || 0
  const childrenTotalValue = Number.parseFloat(event.data.childrenTotalValue) || 0

  minValueToApplyDeposit = Math.abs(Math.abs(amount) - Math.abs(childrenTotalValue))
  minValueToApplyDeposit = Number.parseFloat(minValueToApplyDeposit.toFixed(2))
  const applayDepositValue = Number.parseFloat(event.data.applayDepositValue) || minValueToApplyDeposit
  // Validaciones para el applay deposit
  if (status.value === 'authenticated' && (isAdmin || authStore.can(['PAYMENT-MANAGEMENT:APPLY-DEPOSIT']))) {
    if (event && event.data && event.data.deposit === false) {
      objItemSelectedForRightClick.value = {}
      const menuItemApplayDeposit = allMenuListItems.value.find(item => item.id === 'applayDeposit')
      if (menuItemApplayDeposit) {
        menuItemApplayDeposit.disabled = true
        menuItemApplayDeposit.visible = false
      }
    }
    else if (event && event.data && event.data.deposit === true && minValueToApplyDeposit <= 0.01) {
      objItemSelectedForRightClick.value = event.data
      const menuItemApplayDeposit = allMenuListItems.value.find(item => item.id === 'applayDeposit')
      if (menuItemApplayDeposit) {
        menuItemApplayDeposit.disabled = true
        menuItemApplayDeposit.visible = false
      }
    }
    else if (event && event.data && event.data.deposit === true && applayDepositValue > 0.01) {
      objItemSelectedForRightClick.value = event.data
      const menuItemApplayDeposit = allMenuListItems.value.find(item => item.id === 'applayDeposit')
      if (menuItemApplayDeposit) {
        menuItemApplayDeposit.disabled = false
        menuItemApplayDeposit.visible = true
      }
    }
    else {
      objItemSelectedForRightClick.value = {}
      const menuItemApplayDeposit = allMenuListItems.value.find(item => item.id === 'applayDeposit')
      if (menuItemApplayDeposit) {
        menuItemApplayDeposit.disabled = true
        menuItemApplayDeposit.visible = false
      }
    }
  }

  // Validaciones para el applay payment
  if (status.value === 'authenticated' && (isAdmin || authStore.can(['PAYMENT-MANAGEMENT:APPLY-PAYMENT']))) {
    if (event && event.data && event.data.transactionType && (event.data.transactionType.cash === true || event.data.transactionType.applyDeposit === true) && event.data.applyPayment === false && event.data.transactionType.deposit === false) {
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

    // Validaciones para el navigateToInvoice
    if (event && event.data && event.data.applyPayment === false) {
      objItemSelectedForRightClickNavigateToInvoice.value = {}
      const menuItemNavigateToInvoice = allMenuListItems.value.find(item => item.id === 'navigateToInvoice')
      if (menuItemNavigateToInvoice) {
        menuItemNavigateToInvoice.disabled = true
        menuItemNavigateToInvoice.visible = false
      }
    }
    else {
      objItemSelectedForRightClickNavigateToInvoice.value = event.data
      if (event && event.data && event.data.applyPayment === true) {
        // objItemSelectedForRightClickApplyPayment.value = event.data
        const menuItemNavigateToInvoice = allMenuListItems.value.find(item => item.id === 'navigateToInvoice')
        if (menuItemNavigateToInvoice) {
          menuItemNavigateToInvoice.disabled = false
          menuItemNavigateToInvoice.visible = true
        }
      }
    }
  }

  // Mostrar menu contextual si hay items visibles
  const allHidden = allMenuListItems.value.every(item => !item.visible)
  if (!allHidden) {
    contextMenu.value.show(event.originalEvent)
  }
  else {
    contextMenu.value.hide()
  }
}

async function onRowDoubleClickInDataTableApplyPayment(event: any) {
  if (isApplyPaymentFromTheForm.value) {
    payloadToApplyPayment.value.booking = event?.id
    payloadToApplyPayment.value.applyPayment = true
    openDialogApplyPayment.value = false
    toast.add({ severity: 'success', summary: 'Successful', detail: 'The selected payment will be applied once the corresponding detail is created.', life: 3000 })
  }
  else {
    try {
      const payloadToApplyPayment: GenericObject = {
        paymentDetail: idPaymentDetail.value || '',
        booking: event.id
      }

      const response: any = await GenericService.create('payment', 'payment-detail/apply-payment', payloadToApplyPayment)

      if (response) {
        openDialogApplyPayment.value = false
        toast.add({ severity: 'success', summary: 'Successful', detail: 'Payment has been applied successfully', life: 3000 })
        if (route?.query?.id) {
          const id = route.query.id.toString()
          await getItemById(id)
        }
      }
    }
    catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Payment could not be applied', life: 3000 })
    }
  }
}

function closeModalApplyPayment() {
  detailItemForApplyPayment.value = null
  openDialogApplyPayment.value = false
}

watch(() => hasBeenEdited.value, async (newValue) => {
  if (newValue) {
    if (route?.query?.id) {
      const id = route.query.id.toString()
      await getItemById(id)
    }
  }
})

watch(() => hasBeenCreated.value, async (newValue) => {
  if (newValue) {
    if (route?.query?.id) {
      const id = route.query.id.toString()
      await getItemById(id)
    }
  }
})

// const updateDepositSummaryAction = debounce((newValue) => {
//   enableDepositSummaryAction.value = hasDepositSummaryTransaction(newValue)
// }, 300) // Ajusta el tiempo de debounce según sea necesario

// Watcher
watch(() => paymentDetailsList.value, (newValue) => {
  enableDepositSummaryAction.value = hasDepositSummaryTransaction(newValue)
})

watch(() => route?.query?.id, async (newValue) => {
  if (newValue) {
    const id = newValue.toString()
    await getItemById(id)
  }
})

watch(applyPaymentOnChangePage, (newValue) => {
  applyPaymentPayload.value.page = newValue?.page ? newValue?.page : 0
  applyPaymentPayload.value.pageSize = newValue?.rows ? newValue.rows : 10
  applyPaymentGetList()
})

onMounted(async () => {
  const filterForEmployee: FilterCriteria[] = [
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
    },
  ]
  await getEmployeeList(objApis.value.employee.moduleApi, objApis.value.employee.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filterForEmployee)

  if (route?.query?.id) {
    const id = route.query.id.toString()
    await getItemById(id)
  }
  else {
    clearForm()
    loadDefaultsValues()
  }
})
</script>

<template>
  <div style="max-height: 100vh; height: 90vh">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      {{ formTitle }}
    </div>
    <div class="card p-4">
      <EditFormV2
        :key="formReload"
        ref="refForm"
        :fields="fields"
        :item="item"
        :show-actions="false"
        container-class="grid pt-3"
        :loading-save="loadingSaveAll"
        :loading-delete="loadingDelete"
        :force-save="forceSave"
        @cancel="clearForm"
        @force-save="forceSave = $event"
        @submit="handleSave($event)"
        @delete="requireConfirmationToDelete($event)"
      >
        <template #field-paymentAmount="{ item: data, onUpdate }">
          <InputNumber
            v-if="!loadingSaveAll"
            v-model="data.paymentAmount"
            show-clear
            mode="decimal"
            :readonly="idItem !== ''"
            :min-fraction-digits="2"
            :max-fraction-digits="4"
            @update:model-value="($event) => {
              onUpdate('paymentAmount', $event)
              if (idItem === '' && paymentDetailsList.length === 0) {
                onUpdate('notIdentified', $event)
              }
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>

        <template #field-client="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.client"
            :suggestions="[...clientList]"
            @change="async ($event) => {
              onUpdate('client', $event)
              const filter: FilterCriteria[] = [
                {
                  key: 'client.id',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: data.client?.id,
                },
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, {
                query: '',
                keys: ['name', 'code'],
              }, filter)
              onUpdate('agency', agencyList.length > 0 ? agencyList[0] : null)
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
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-paymentSource="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.paymentSource"
            :suggestions="[...paymentSourceList]"
            @change="($event) => {
              onUpdate('paymentSource', $event)
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
              await getPaymentSourceList(objApis.paymentSource.moduleApi, objApis.paymentSource.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-transactionDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.transactionDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('transactionDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" />
        </template>
        <!-- Agency -->
        <template #field-agency="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            :key="formReloadAgency"
            field="name"
            item-value="id"
            :model="data.agency"
            :suggestions="[...agencyList]"
            :disabled="!data.client"
            @change="($event) => {
              onUpdate('agency', $event)
            }"
            @load="async($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              const filter: FilterCriteria[] = [
                {
                  key: 'client.id',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: data.client?.id,
                },
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-hotel="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.hotel"
            :suggestions="[...hotelList]"
            @change="async ($event) => {
              onUpdate('hotel', $event)

              const filter: FilterCriteria[] = [
                {
                  key: 'manageHotel.id',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: $event?.id,
                },
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getBankAccountList(objApis.bankAccount.moduleApi, objApis.bankAccount.uriApi, {
                query: '',
                keys: ['accountNumber'],
              }, filter)

              onUpdate('bankAccount', bankAccountList.length > 0 ? bankAccountList[0] : null)
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
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-bankAccount="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.bankAccount"
            :disabled="!data.hotel"
            :suggestions="[...bankAccountList]"
            @change="($event) => {
              onUpdate('bankAccount', $event)
            }"
            @load="async($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              const filter: FilterCriteria[] = [
                {
                  key: 'manageHotel.id',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: data.hotel?.id,
                },
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getBankAccountList(objApis.bankAccount.moduleApi, objApis.bankAccount.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-paymentStatus="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.paymentStatus"
            :disabled="true"
            :suggestions="[...paymentStatusList]"
            @change="($event) => {
              onUpdate('paymentStatus', $event)
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
              await getPaymentStatusList(objApis.paymentStatus.moduleApi, objApis.paymentStatus.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-attachmentStatus="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :disabled="true"
            :model="data.attachmentStatus"
            :suggestions="[...attachmentStatusList]"
            @change="($event) => {
              onUpdate('attachmentStatus', $event)
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
              await getAttachmentStatusList(objApis.attachmentStatus.moduleApi, objApis.attachmentStatus.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
      </EditFormV2>
    </div>
    <DynamicTable
      :data="paymentDetailsList"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      @on-change-pagination="payloadOnChangePage = $event"
      @update:clicked-item="rowSelected($event)"
      @on-row-double-click="openDialogPaymentDetailsByAction($event, 'new-detail')"
      @on-change-filter="parseDataTableFilter"
      @on-sort-field="onSortField"
      @on-row-right-click="onRowContextMenu($event)"
    >
      <template #datatable-footer>
        <ColumnGroup type="footer" class="flex align-items-center">
          <Row>
            <Column footer="Totals:" :colspan="9" footer-style="text-align:right; font-weight: bold;" />
            <Column :footer="formatNumber(Math.round((subTotals.depositAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
            <Column :colspan="4" />
          </Row>
        </ColumnGroup>
      </template>
    </DynamicTable>

    <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
      <IfCan :perms="idItem ? ['PAYMENT-MANAGEMENT:EDIT'] : ['PAYMENT-MANAGEMENT:CREATE']">
        <Button v-tooltip.top="'Save'" class="w-3rem" icon="pi pi-save" :loading="loadingSaveAll" @click="forceSave = true" />
      </IfCan>
      <IfCan :perms="['PAYMENT-MANAGEMENT:SUMMARY']">
        <Button v-tooltip.top="'Deposit Summary'" :disabled="!enableDepositSummaryAction" class="w-3rem ml-1" @click="dialogPaymentDetailSummary">
          <template #icon>
            <span class="flex align-items-center justify-content-center p-0">
              <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24">
                <path fill="currentColor" d="M10 4v4h4V4zm6 0v4h4V4zm0 6v4h4v-4zm0 6v4h4v-4zm-2 4v-4h-4v4zm-6 0v-4H4v4zm0-6v-4H4v4zm0-6V4H4v4zm2 6h4v-4h-4zM4 2h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H4c-1.08 0-2-.9-2-2V4a2 2 0 0 1 2-2" />
              </svg>
            </span>
          </template>
        </Button>
      </IfCan>
      <IfCan :perms="['PAYMENT-MANAGEMENT:DEPOSIT-TRANSFER']">
        <Button v-tooltip.top="'Deposit Transfer'" class="w-3rem ml-1" :disabled="idItem === null || idItem === undefined || idItem === ''" icon="pi pi-lock" @click="openDialogPaymentDetailsByAction(idItemDetail, 'deposit-transfer')" />
      </IfCan>
      <IfCan :perms="['PAYMENT-MANAGEMENT:SPLIT-ANTI']">
        <Button v-tooltip.top="'Split ANTI'" class="w-3rem ml-1" :disabled="!enableSplitAction" @click="openDialogPaymentDetailsByAction(idItemDetail, 'split-deposit')">
          <template #icon>
            <span class="flex align-items-center justify-content-center p-0">
              <svg width="14" height="15" viewBox="0 0 14 15" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4.08337 12.75C5.04987 12.75 5.83337 11.9665 5.83337 11C5.83337 10.0335 5.04987 9.25 4.08337 9.25C3.11688 9.25 2.33337 10.0335 2.33337 11C2.33337 11.9665 3.11688 12.75 4.08337 12.75Z" stroke="white" stroke-linecap="round" stroke-linejoin="round" />
                <path d="M8.75004 9.25L4.08337 2.25M5.25004 9.25L7.00004 6.625M9.91671 2.25L8.16671 4.875" stroke="white" stroke-linecap="round" stroke-linejoin="round" />
                <path d="M9.91675 12.75C10.8832 12.75 11.6667 11.9665 11.6667 11C11.6667 10.0335 10.8832 9.25 9.91675 9.25C8.95025 9.25 8.16675 10.0335 8.16675 11C8.16675 11.9665 8.95025 12.75 9.91675 12.75Z" stroke="white" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
          </template>
        </Button>
      </IfCan>
      <IfCan :perms="['PAYMENT-MANAGEMENT:PRINT-DETAIL']">
        <Button v-tooltip.top="'Print'" class="w-3rem ml-1" :disabled="!paymentDetailsList || paymentDetailsList.length === 0" icon="pi pi-print" @click="openPrint = true" />
      </IfCan>
      <!-- <Button v-tooltip.top="'Payment to Print'" class="w-3rem ml-1" disabled icon="pi pi-print" @click="openPrint = true" /> -->
      <IfCan :perms="['PAYMENT-MANAGEMENT:ATTACHMENT']">
        <Button v-tooltip.top="'Attachment'" class="w-3rem ml-1" icon="pi pi-paperclip" severity="primary" @click="handleAttachmentDialogOpen" />
      </IfCan>
      <IfCan :perms="['PAYMENT-MANAGEMENT:IMPORT-EXCEL']">
        <Button v-tooltip.top="'Import from Excel'" class="w-3rem ml-1" :disabled="idItem === null || idItem === undefined || idItem === ''" icon="pi pi-file-import" @click="openDialogImportExcel(idItem)" />
      </IfCan>
      <IfCan :perms="['PAYMENT-MANAGEMENT:SHOW-HISTORY']">
        <Button v-tooltip.top="'Show History'" class="w-3rem ml-1" :disabled="idItem === null || idItem === undefined || idItem === ''" @click="openDialogStatusHistory">
          <template #icon>
            <span class="flex align-items-center justify-content-center p-0">
              <svg xmlns="http://www.w3.org/2000/svg" height="15px" viewBox="0 -960 960 960" width="15px" fill="#e8eaed"><path d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z" /></svg>
            </span>
          </template>
        </Button>
      </IfCan>
      <!-- <Button v-tooltip.top="'Edit Detail'" class="w-3rem" icon="pi pi-pen-to-square" severity="secondary" @click="deletePaymentDetail($event)" /> -->
      <IfCan :perms="['PAYMENT-MANAGEMENT:CREATE-DETAIL']">
        <Button v-tooltip.top="'Add New Detail'" class="w-3rem ml-1" icon="pi pi-plus" :disabled="idItem === null || idItem === undefined || idItem === ''" severity="primary" @click="openDialogPaymentDetails($event)" />
      </IfCan>
      <IfCan :perms="['PAYMENT-MANAGEMENT:DELETE-DETAIL']">
        <Button v-if="false" v-tooltip.top="'Delete'" class="w-3rem ml-1" outlined severity="danger" :disabled="idItemDetail === null || idItemDetail === undefined || idItemDetail === '' || disabledDeleteForPaymentWithChildren(idItemDetail)" :loading="loadingDelete" icon="pi pi-trash" @click="deleteItem(idItemDetail)" />
      </IfCan>
      <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="goToList" />
    </div>
    <div v-show="onOffDialogPaymentDetail">
      <DialogPaymentDetailForm
        :key="dialogPaymentDetailFormReload"
        :visible="onOffDialogPaymentDetail"
        :fields="fieldPaymentDetails"
        :item="itemDetails"
        :title="getNameByActions(actionOfModal)"
        :selected-payment="item"
        :is-split-action="isSplitAction"
        :action="actionOfModal"
        @apply-payment="openModalApplyPayment($event)"
        @update:visible="onCloseDialog($event)"
        @save="saveAndReload($event)"
      />
    </div>
    <div v-if="attachmentDialogOpen">
      <PaymentAttachmentDialog
        :is-create-or-edit-payment="route?.query?.id ? 'edit' : 'create'"
        :add-item="addAttachment"
        :close-dialog="() => { attachmentDialogOpen = false }"
        :is-creation-dialog="true"
        header="Manage Payment Attachment"
        :list-items="attachmentList"
        :open-dialog="attachmentDialogOpen"
        :update-item="updateAttachment"
        :selected-payment="item"
        @update:list-items="attachmentList = $event"
      />
    </div>
    <DialogPaymentDetailSummary
      title="Transactions ANTI Summary"
      :visible="onOffDialogPaymentDetailSummary"
      :selected-payment="item"
      @update:visible="onCloseDialogSummary($event)"
    />

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
        <div class="flex justify-content-between">
          <h5 class="m-0">
            Payment Status History
          </h5>
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
          />
        </div>
      </template>
    </Dialog>

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
          <DynamicTable
            class="card p-0"
            :data="applyPaymentList"
            :columns="applyPaymentColumns"
            :options="applyPaymentOptions"
            :pagination="applyPaymentPagination"
            @on-change-pagination="applyPaymentOnChangePage = $event"
            @on-change-filter="applyPaymentParseDataTableFilter"
            @on-sort-field="historyOnSortField"
            @on-row-double-click="onRowDoubleClickInDataTableApplyPayment"
          />
        </div>
      </template>
    </Dialog>

    <!-- PRINT -->
    <Dialog
      v-model:visible="openPrint"
      modal
      class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
      :style="{ width: '35%' }"
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
      @hide="openPrint = false"
    >
      <template #header>
        <div class="flex justify-content-between">
          <h5 class="m-0">
            Payment To Print
          </h5>
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
            @cancel="closeDialogPrint"
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
              <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times" @click="closeDialogPrint" />
            </template>
          </EditFormV2>
        </div>
      </template>
    </Dialog>

    <ContextMenu ref="contextMenu" :model="allMenuListItems" />
  </div>
</template>

<!-- paymentAndDetails: true,
  paymentSupport: false,
  allPaymentsSupport: false,
  invoiceRelated: false,
  invoiceRelatedWithSupport: false -->

  <style lang="scss">
  .row-deposit {
    background-color: #e0f2f194;
    color: #00695C;
  }
  .custom-dialog-history .p-dialog-content {
  background-color: #ffffff;
}
.custom-dialog-history .p-dialog-footer {
  background-color: #ffffff;
}
</style>
