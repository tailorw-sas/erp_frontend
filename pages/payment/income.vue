<script setup lang="ts">
import dayjs from 'dayjs'
import { z } from 'zod'
import { onMounted, reactive, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { useRoute } from 'vue-router'
import type { PageState } from 'primevue/paginator'
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import DialogPaymentDetailForm from '~/components/payment/DialogPaymentDetailForm.vue'
import AttachmentIncomeDialog from '~/components/income/attachment/AttachmentDialog.vue'
import AttachmentIncomeHistoryDialog from '~/components/income/attachment/AttachmentIncomeHistoryDialog.vue'
import { statusToString } from '~/utils/helpers'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'

const route = useRoute()
const toast = useToast()
const confirm = useConfirm()
const { data: userData } = useAuth()
const idItemToLoadFirstTime = ref('')
const refForm: Ref = ref(null)
const idItem = ref('')
const idItemDetail = ref('')
const isSplitAction = ref(false)
const formReload = ref(0)
const formReloadAgency = ref(0)
const dialogPaymentDetailFormReload = ref(0)
const loadingSaveAll = ref(false)
const loadingSaveAdjustment = ref(false)
const loadingDelete = ref(false)
const forceSave = ref(false)
const submitEvent = new Event('')
const invoiceAmount = ref(0)
const listItems = ref<any[]>([])
const invoiceTypeList = ref<any[]>([])
const invoiceStatusList = ref<any[]>([])
const agencyList = ref<any[]>([])
const hotelList = ref<any[]>([])
const bankAccountList = ref<any[]>([])
const bookingList = ref<any[]>([])
const roomRateList = ref<any[]>([])

const paymentStatusList = ref<any[]>([])
const attachmentStatusList = ref<any[]>([])
const onOffDialogPaymentDetail = ref(false)
const attachmentHistoryDialogOpen = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)
const attachmentList = ref<any[]>([])
const paymentDetailsList = ref<any[]>([])
const AdjustmentList = ref<any[]>([])
const AdjustmentTableList = ref<any[]>([])
const activeTab = ref(2)

const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'income',
})

const confApiPaymentDetail = reactive({
  moduleApi: 'invoicing',
  uriApi: 'income-adjustment',
})
const confRoomRateApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-room-rate'
})

const confAdjustmentApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment'
})

const confInvoiceApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice'
})

const confBookingApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-booking'
})

const bookingOptions = ref({
  tableName: 'Booking',
  loading: false,
  actionsAsMenu: false,
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',

})

const roomRateOptions = ref({
  tableName: 'roomRate',
  loading: false,
  actionsAsMenu: false,
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
})

const adjustmentOptions = ref({
  tableName: 'Adjustments',
  loading: false,
  actionsAsMenu: false,
})

function handleAttachmentHistoryDialogOpen() {
  attachmentHistoryDialogOpen.value = true
}

// Attachments
function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

const roomRatePayload = ref<IQueryRequest>({
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

const bookingPagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const roomRatePagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const bookingPayload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const adjustmentPayload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const objApis = ref({
  invoiceType: { moduleApi: 'settings', uriApi: 'manage-invoice-type' },
  invoiceStatus: { moduleApi: 'settings', uriApi: 'manage-invoice-status' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  bankAccount: { moduleApi: 'settings', uriApi: 'manage-bank-account' },
  paymentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  attachmentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' },
})

const columns: IColumn[] = [
  { field: 'bookingId', header: 'ID', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'fullName', header: 'Full Name', type: 'text' },
  { field: 'reservationNumber', header: 'Reservation No.', type: 'text' },
  { field: 'cuponNumber', header: 'Cupon No.', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-room-type' } },
  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'nights', header: 'Nights', type: 'text' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'text' },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'text' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },

]

const adjustmentColumns: IColumn[] = [
  { field: 'amount', header: 'Amount', type: 'text' },
  { field: 'date', header: 'Date', type: 'text' },
  { field: 'transaction', header: 'Transaction', type: 'text' },
  { field: 'remark', header: 'Description', type: 'text' },
]

const roomRateColumns: IColumn[] = [
  { field: 'roomRateId', header: 'Id', type: 'text' },
  { field: 'fullName', header: 'Full Name', type: 'text' },
  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'adults', header: 'Adults', type: 'text' },
  { field: 'children', header: 'Children', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-room-type' } },
  { field: 'nights', header: 'Nights', type: 'text' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'text' },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'text' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
]

const formTitle = computed(() => {
  return 'New Income'
})

const decimalRegex = /^\d+(\.\d{1,2})?$/

const decimalSchema = z.object(
  {
    amount: z
      .string()
      .min(1, { message: 'The amount field is required' })
      .regex(decimalRegex, { message: 'The amount does not meet the correct format of n integer digits and 2 decimal digits' }),
    // .refine(value => Number.parseFloat(value) >= 1, { message: 'The amount field must be at least 1' }),
    paymentAmmount: z
      .string()
      .min(1, { message: 'The payment amount field is required' })
      .regex(decimalRegex, { message: 'The payment amount does not meet the correct format of n integer digits and 2 decimal digits' })
      .refine(value => Number.parseFloat(value) >= 1, { message: 'The payment amount field must be at least 1' })
  },
)
/*
const fields: Array<FieldDefinitionType> = [
  {
    field: 'incomeId',
    header: 'Id',
    disabled: true,
    dataType: 'text',
    class: 'field col-12 md:col-1',
  },
  {
    field: 'invoiceNumber',
    header: 'Invoice Number',
    dataType: 'text',
    class: 'field col-12 md:col-2',
  },
  {
    field: 'manual',
    header: 'Manual',
    dataType: 'check',
    class: 'field col-12 md:col-1 mt-3 mb-3',
    headerClass: 'mb-1',
  },
  {
    field: 'invoiceAmount',
    header: 'Invoice Amount',
    dataType: 'text',
    disabled: false,
    class: 'field col-12 md:col-2 required',
    validation: decimalSchema.shape.paymentAmmount
  },
  {
    field: 'invoiceType',
    header: 'Invoice Type',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('Invoice Type'),
  },
  {
    field: 'invoiceStatus',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('Invoice Status'),
  },
  {
    field: 'dueDate',
    header: 'Due Date',
    dataType: 'date',
    class: 'field col-12 md:col-1 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Due Date field is required',
      invalid_type_error: 'The Due Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Due Date field cannot be greater than current date')
  },
  {
    field: 'invoiceDate',
    header: 'Invoice Date',
    dataType: 'date',
    class: 'field col-12 md:col-2 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Invoice Date field is required',
      invalid_type_error: 'The Invoice Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
  {
    field: 'reSend',
    header: 'Re-Send',
    dataType: 'check',
    class: 'field col-12 md:col-1 mt-3 mb-3',
    headerClass: 'mb-1',
  },
  {
    field: 'reSendDate',
    header: 'Re-Send Date',
    dataType: 'date',
    class: 'field col-12 md:col-2 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Re-Send Date field is required',
      invalid_type_error: 'The Re-Send Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Re-Send Date field cannot be greater than current date')
  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    disabled: false,
    validation: validateEntityStatus('agency'),
  },
] */

const fields: Array<FieldDefinitionType> = [
  {
    field: 'incomeId',
    header: 'ID',
    disabled: true,
    dataType: 'text',
    class: 'field col-12 md:col-1',
  },
  {
    field: 'dueDate',
    header: 'Due Date',
    dataType: 'date',
    class: 'field col-12 md:col-2 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Due Date field is required',
      invalid_type_error: 'The Due Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Due Date field cannot be greater than current date')
  },

  {
    field: 'manual',
    header: 'Manual',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 md:col-1 mt-3 mb-3',
    headerClass: 'mb-1',
  },
  {
    field: 'incomeAmount',
    header: 'Income Amount',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-2 required',

  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    disabled: false,
    validation: validateEntityStatus('agency'),
  },
  {
    field: 'invoiceNumber',
    header: 'Invoice Number',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'invoiceDate',
    header: 'Invoice Date',
    dataType: 'date',
    class: 'field col-12 md:col-2 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Invoice Date field is required',
      invalid_type_error: 'The Invoice Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
  {
    field: 'reSend',
    header: 'Re-Send',
    dataType: 'check',
    class: 'field col-12 md:col-1 mt-3 mb-3',
    headerClass: 'mb-1',
  },
  {
    field: 'reSendDate',
    header: 'Re-Send Date',
    dataType: 'date',
    class: 'field col-12 md:col-2',
    headerClass: 'mb-1',
    validation: z
      .union([z.date(), z.null()])
      .refine(date => !date || date <= dayjs().endOf('day').toDate(), {
        message: 'The Re-Send Date field cannot be greater than current date',
      })
  },

  {
    field: 'invoiceType',
    header: 'Invoice Type',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('Invoice Type'),
  },
  {
    field: 'invoiceStatus',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('Status'),
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    class: 'field col-12 md:col-1 mt-3 mb-3',
    headerClass: 'mb-1',
    disabled: true,
  },
]

const item = ref({
  incomeId: '0',
  invoiceType: null,
  invoiceStatus: null,
  invoiceNumber: '',
  transactionDate: '',
  paymentStatus: null,
  agency: null,
  hotel: null,
  invoiceDate: new Date(),
  dueDate: new Date(),
  reSendDate: null,
  reSend: false,
  bankAccount: null,
  attachmentStatus: null,
  incomeAmount: '0',
  paymentBalance: '0',
  depositAmount: '0',
  depositBalance: '0',
  otherDeductions: '0',
  identified: '0',
  notIdentified: '',
  remark: '',
  status: '',
  manual: true,
} as GenericObject)

const itemTemp = ref({
  incomeId: '0',
  invoiceType: null,
  invoiceStatus: null,
  invoiceNumber: '',
  reSendDate: null,
  reSend: false,
  invoiceDate: new Date(),
  dueDate: new Date(),
  transactionDate: '',
  paymentStatus: null,
  agency: null,
  hotel: null,
  bankAccount: null,
  attachmentStatus: null,
  incomeAmount: '0',
  paymentBalance: '0',
  payment: '0',
  depositAmount: '0',
  depositBalance: '0',
  otherDeductions: '0',
  identified: '0',
  notIdentified: '',
  remark: '',
  status: true,
  manual: true,

})

const fieldAdjustments = ref<FieldDefinitionType[]>([

  {
    field: 'amount',
    header: 'Amount',
    dataType: 'text',

    class: 'field col-12 required',
    validation: z.string().min(1, { message: 'The amount field is required' })
      .regex(/^-?\d+(\.\d{1,2})?$/, { message: 'The amount does not meet the correct format of n integer digits and 2 decimal digits' })
      .refine(value => Number.parseFloat(value) !== 0, { message: 'The amount field must be different from zero' }),
  },
  {
    field: 'date',
    header: 'Date',
    dataType: 'date',
    class: 'field col-12 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Date field is required',
      invalid_type_error: 'The Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Date field cannot be greater than current date')
  },
  {
    field: 'transactionType',
    header: 'Transaction',
    dataType: 'select',
    class: 'field col-12 ',

  },
  {
    field: 'remark',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
  },
])

const itemDetails = ref({
  payment: '',
  transactionType: null,
  income: null,
  amount: '0',
  date: new Date(),
  remark: '',
  status: true,
  employee: null,
})

const itemDetailsTemp = ref({
  payment: '',
  employee: null,
  transactionType: null,
  income: null,
  amount: '0',
  remark: '',
  date: new Date(),
  status: true,
})

const options = ref({
  tableName: 'Payment Income',
  moduleApi: 'invoicing',
  uriApi: 'income',
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

function clearForm() {
  item.value = { ...itemTemp.value }
  formReload.value++
}

function clearFormDetails() {
  itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
}

function openDialogPaymentDetails(event: any) {
  if (event) {
    itemDetails.value = { ...itemDetailsTemp.value }
    const objToEdit = paymentDetailsList.value.find(x => x.id === event.id)

    if (objToEdit) {
      itemDetails.value = { ...objToEdit }
    }

    onOffDialogPaymentDetail.value = true
  }

  onOffDialogPaymentDetail.value = true
}

// Listado de booking
async function getBookingList() {
  try {
    idItemToLoadFirstTime.value = ''
    bookingOptions.value.loading = true
    bookingList.value = []

    const response = await GenericService.search(confBookingApi.moduleApi, confBookingApi.uriApi, bookingPayload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    bookingPagination.value.page = page
    bookingPagination.value.limit = size
    bookingPagination.value.totalElements = totalElements
    bookingPagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      bookingList.value = [...bookingList.value, {
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        agency: iterator?.invoice?.agency,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        fullName: `${iterator.firstName ? iterator.firstName : ''} ${iterator.lastName ? iterator.lastName : ''}`
      }]
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    bookingOptions.value.loading = false
  }
}

async function getRoomRateList() {
  try {
    idItemToLoadFirstTime.value = ''
    roomRateOptions.value.loading = true
    roomRateList.value = []

    const response = await GenericService.search(confRoomRateApi.moduleApi, confRoomRateApi.uriApi, roomRatePayload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    roomRatePagination.value.page = page
    roomRatePagination.value.limit = size
    roomRatePagination.value.totalElements = totalElements
    roomRatePagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      roomRateList.value = [...roomRateList.value, {
        ...iterator,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        loadingEdit: false,
        loadingDelete: false,
        fullName: `${iterator.booking.firstName ? iterator.booking.firstName : ''} ${iterator.booking.lastName ? iterator.booking.lastName : ''}`,
        bookingId: iterator.booking.bookingId,
        roomType: iterator.booking.roomType,
        nightType: iterator.booking.nightType,
        ratePlan: iterator.booking.ratePlan,
        agency: iterator?.booking?.invoice?.agency
      }]
    }
    if (roomRateList.value.length > 0) {
      idItemToLoadFirstTime.value = roomRateList.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    roomRateOptions.value.loading = false
  }
}

// Listado de booking
async function getAdjustmentList() {
  try {
    adjustmentOptions.value.loading = true
    AdjustmentTableList.value = []

    const response = await GenericService.search(confAdjustmentApi.moduleApi, confAdjustmentApi.uriApi, adjustmentPayload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      AdjustmentTableList.value = [...AdjustmentTableList.value, {
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        date: iterator.date.substring(0, 10),
        transaction: `${iterator.transaction.code} - ${iterator.transaction.name}`,
        remark: iterator.description,
      }]
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    adjustmentOptions.value.loading = false
  }
}

watch(idItem, () => {
  if (idItem.value) {
    bookingPayload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: idItem.value,
      logicalOperation: 'AND'
    }]
    roomRatePayload.value.filter = [{
      key: 'booking.invoice.id',
      operator: 'EQUALS',
      value: idItem.value,
      logicalOperation: 'AND'
    }]
    adjustmentPayload.value.filter = [{
      key: 'roomRate.booking.invoice.id',
      operator: 'EQUALS',
      value: idItem.value,
      logicalOperation: 'AND'
    }]
  }
})

async function createItem(item: { [key: string]: any }) {
  if (item) {
    const payload: { [key: string]: any } = { ...item }
    payload.dueDate = payload.dueDate ? dayjs(payload.dueDate).format('YYYY-MM-DD') : ''
    payload.reSendDate = payload.reSendDate ? dayjs(payload.reSendDate).format('YYYY-MM-DD') : ''
    payload.invoiceDate = payload.invoiceDate ? dayjs(payload.invoiceDate).format('YYYY-MM-DD') : ''
    payload.invoiceType = Object.prototype.hasOwnProperty.call(payload.invoiceType, 'id') ? payload.invoiceType.id : payload.invoiceType
    payload.invoiceStatus = Object.prototype.hasOwnProperty.call(payload.invoiceStatus, 'id') ? payload.invoiceStatus.id : payload.invoiceStatus
    payload.agency = Object.prototype.hasOwnProperty.call(payload.agency, 'id') ? payload.agency.id : payload.agency
    payload.hotel = Object.prototype.hasOwnProperty.call(payload.hotel, 'id') ? payload.hotel.id : payload.hotel
    payload.status = statusToString(payload.status)

    let totalIncomeAmount = 0
    if (Array.isArray(payload.incomeAmount)) {
      totalIncomeAmount = payload.incomeAmount.reduce((acc, curr) => acc + Number(curr), 0)
    }
    else {
      totalIncomeAmount = Number(payload.incomeAmount)
    }

    // Asignación del invoiceAmount total al payload
    payload.incomeAmount = totalIncomeAmount
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    if (response && response.id) {
      // Guarda el id del elemento creado
      idItem.value = response.id
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was not successful', life: 10000 })
    }
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  // let successOperation = true
  if (idItem.value) {
    try {
      await updateItem(item)
      idItem.value = ''
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
      // Deshabilitar campos restantes del formulario
      updateFieldProperty(fields, 'reSend', 'disabled', true)
      updateFieldProperty(fields, 'reSendDate', 'disabled', true)
      if (AdjustmentList.value.length > 0) {
        await createPaymentDetails(AdjustmentList.value[0])
      }
      await getItemById(idItem.value)
    }
    catch (error: any) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById('invoicing', 'manage-invoice', id)
      if (response) {
        item.value.id = id
        item.value.incomeId = response.invoiceId
        item.value.invoiceNumber = response.invoiceNumber
        invoiceStatusList.value = [response.invoiceStatus]
        item.value.invoiceStatus = response.invoiceStatus

        agencyList.value = [response.agency]
        item.value.agency = response.agency

        hotelList.value = [response.hotel]
        item.value.hotel = response.hotel
        item.value.invoiceDate = new Date()
        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        item.value.invoiceType = response.invoiceType ? ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType)) : ENUM_INVOICE_TYPE[1]
      }
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Income could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}
/*
async function getListPaymentDetail() {
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
        iterator.amount = (!Number.isNaN(iterator.amount) && iterator.amount !== null && iterator.amount !== '')
          ? Number.parseFloat(iterator.amount).toString()
          : '0'
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
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
  }
}
*/
async function createPaymentDetails(item: { [key: string]: any }) {
  try {
    if (item) {
      loadingSaveAdjustment.value = true
      const payload: { [key: string]: any } = { ...item }
      payload.amount = Number.parseFloat(payload.amount)
      payload.employee = userData?.value?.user?.userId
      payload.transactionType = payload.transactionType && Object.prototype.hasOwnProperty.call(payload.transactionType, 'id') ? payload.transactionType.id : payload.transactionType
      payload.income = idItem.value // Usar el ID del registro de "income" creado anteriormente
      payload.remark = item.remark
      payload.status = 'ACTIVE'
      payload.date = payload.date ? dayjs(payload.date).format('YYYY-MM-DD') : ''

      await GenericService.create(confApiPaymentDetail.moduleApi, 'income-adjustment', payload)

      onOffDialogPaymentDetail.value = false
      loadingSaveAdjustment.value = false
      clearFormDetails()
      await getBookingList()
      await getRoomRateList()
      await getAdjustmentList()
    }
  }
  finally {
    loadingSaveAdjustment.value = false
  }
}
async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.payment = idItem.value || ''
  payload.amount = Number.parseFloat(payload.amount)
  payload.transactionType = typeof payload.transactionType === 'object' ? payload.transactionType.id : payload.transactionType

  await GenericService.update(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, item.id || '', payload)
  onOffDialogPaymentDetail.value = false
  clearFormDetails()
}

async function saveAndReload(itemP: { [key: string]: any }) {
  try {
    if (idItem.value) {
      await createPaymentDetails(itemP)
    }
    else {
      const data: { [key: string]: any } = { ...itemP }
      data.date = itemP.date ? dayjs(itemP.date).format('YYYY-MM-DD') : ''
      AdjustmentList.value = [data]
      AdjustmentTableList.value = [...AdjustmentList.value].map(({ transactionType, ...rest }) => ({
        transaction: transactionType?.name ?? '',
        ...rest
      }))
    }
    // await getItemById(idItem.value)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    onOffDialogPaymentDetail.value = false
  }
  /* try {
    if (item?.id && !isSplitAction.value) {
      await updateItem(item)
    }
    else {
      await createPaymentDetails(item)
    }
    await getItemById(idItem.value)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  } */
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
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status
  }
}

// async function getInvoiceTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
// invoiceTypeList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
// }

async function getInvoiceTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  const additionalFilter: FilterCriteria[] = [
    {
      key: 'name',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'Income'
    }
  ]

  const filteredList = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction)

  if (filteredList.length > 0) {
    invoiceTypeList.value = [filteredList[0]]
  }
  else {
    invoiceTypeList.value = []
  }
}

async function getDefaultInvoiceTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  const defaultInvoiceTypeList = ref<ListItem[]>([])
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
  defaultInvoiceTypeList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...filter], queryObj, mapFunction)
  if (defaultInvoiceTypeList.value.length > 0) {
    item.value.invoiceType = defaultInvoiceTypeList.value[0]
  }
}

function mapFunctionForClient(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: data.name,
    status: data.status
  }
}

/* async function getInvoiceStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  invoiceStatusList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
} */

async function getInvoiceStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  const additionalFilter: FilterCriteria[] = [
    {
      key: 'name',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'Sent'
    }
  ]

  const filteredList = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction)

  if (filteredList.length > 0) {
    invoiceStatusList.value = [filteredList[0]]
  }
  else {
    invoiceStatusList.value = []
  }
}
/*
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  agencyList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
}*/

async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  const additionalFilter: FilterCriteria[] = [
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE'
    }
  ];

  agencyList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction);
}
/*
async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  hotelList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [], queryObj, mapFunction)
}*/

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  const additionalFilter: FilterCriteria[] = [
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE'
    }
  ];

  hotelList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction);
}

function addAttachment(attachment: any) {
  attachmentList.value = [...attachmentList.value, attachment]
}

function updateAttachment(attachment: any) {
  const index = attachmentList.value.findIndex(item => item.id === attachment.id)
  attachmentList.value[index] = attachment
}

function rowSelected(rowData: any) {
  if (rowData !== null && rowData !== undefined && rowData !== '') {
    idItemDetail.value = rowData
  }
  else {
    idItemDetail.value = ''
    isSplitAction.value = false
  }
}

function onCloseDialog($event: any) {
  if ($event === false) {
    isSplitAction.value = false
  }
  onOffDialogPaymentDetail.value = $event
}

async function loadDefaultsValues() {
  await getDefaultInvoiceTypeList(objApis.value.invoiceType.moduleApi, objApis.value.invoiceType.uriApi, {
    query: '',
    keys: ['name', 'code'],
  })
  formReload.value++
}

async function handleSave(event: any) {
  if (event) {
    await saveItem(event)
    forceSave.value = false
  }
}

onMounted(async () => {
  // getBookingList()
  // getRoomRateList()
  clearForm()
  loadDefaultsValues()
  onOffDialogPaymentDetail.value = true
})
</script>

<template>
  <div style="max-height: 100vh; height: 90vh">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      {{ formTitle }}
    </div>
    <div class="card p-4 m-0">
      <EditFormV2
        :key="formReload"
        :fields="fields"
        :item="item"
        :show-actions="false"
        container-class="grid pt-3"
        :loading-save="loadingSaveAll"
        :loading-delete="loadingDelete"
        :force-save="forceSave"
        @force-save="forceSave = $event"
        @cancel="clearForm"
        @submit="handleSave($event)"
        @delete="requireConfirmationToDelete($event)"
      >
        <template #field-invoiceDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.invoiceDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            :disabled="idItem !== ''"
            @update:model-value="($event) => {
              onUpdate('invoiceDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" />
        </template>
        <template #field-dueDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.dueDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            :disabled="idItem !== ''"
            @update:model-value="($event) => {
              onUpdate('dueDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" />
        </template>
        <template #field-incomeAmount="{ item: data, onUpdate }">
          <InputText
            v-if="!loadingSaveAll"
            v-model="data.incomeAmount"
            show-clear :disabled="true"
            @update:model-value="($event) => {
              onUpdate('incomeAmount', $event)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-invoiceStatus="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.invoiceStatus"
            :suggestions="[...invoiceStatusList]"
            :disabled="idItem !== ''"
            @change="async ($event) => {
              onUpdate('invoiceStatus', $event)
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
              await getInvoiceStatusList(objApis.invoiceStatus.moduleApi, objApis.invoiceStatus.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-invoiceType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.invoiceType"
            :suggestions="[...invoiceTypeList]"
            :disabled="idItem !== ''"
            @change="($event) => {
              onUpdate('invoiceType', $event)
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
              await getInvoiceTypeList(objApis.invoiceType.moduleApi, objApis.invoiceType.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
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
            :disabled="idItem !== ''"
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
            :disabled="idItem !== ''"
            @change="async ($event) => {
              onUpdate('hotel', $event)
            }"
            @load="async($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              await getHotelList(objApis.hotel.moduleApi, objApis.hotel.uriApi, objQueryToSearch)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
      </EditFormV2>
    </div>
    <TabView id="tabView" v-model:activeIndex="activeTab" class="no-global-style">
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2 p-2" :style="`${activeTab === 0 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: auto`">
            <i class="pi pi-calendar" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap">Bookings</span>
          </div>
        </template>
        <DynamicTable
          :data="bookingList"
          :columns="columns"
          :options="bookingOptions"
          :pagination="bookingPagination"
          @on-change-pagination="payloadOnChangePage = $event"
          @update:clicked-item="rowSelected($event)"
          @on-row-double-click="openDialogPaymentDetails($event)"
        />
      </TabPanel>
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2 p-2" :style="`${activeTab === 1 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: auto`">
            <i class="pi pi-receipt" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap">Room Rates</span>
          </div>
        </template>
        <DynamicTable
          :data="roomRateList"
          :columns="roomRateColumns"
          :options="roomRateOptions"
          :pagination="roomRatePagination"
          @on-change-pagination="payloadOnChangePage = $event"
          @update:clicked-item="rowSelected($event)"
          @on-row-double-click="openDialogPaymentDetails($event)"
        />
      </TabPanel>
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2 p-2" :style="`${activeTab === 2 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: auto`">
            <i class="pi pi-sliders-v" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap"> New Adjustments</span>
          </div>
        </template>
        <DynamicTable
          :data="AdjustmentTableList"
          :columns="adjustmentColumns"
          :options="adjustmentOptions"
          :pagination="pagination"
          @on-change-pagination="payloadOnChangePage = $event"
          @update:clicked-item="rowSelected($event)"
          @on-row-double-click="openDialogPaymentDetails($event)"
        />
      </TabPanel>
    </TabView>
    <!-- @on-confirm-create="goToCreateForm"
      @on-change-filter="parseDataTableFilter"
      @on-list-item="resetListItems"
      @on-sort-field="onSortField" -->

    <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
      <Button v-tooltip.top="'Save'" class="w-3rem" icon="pi pi-save" @click="forceSave = true" />
      <Button v-tooltip.top="'Print'" class="w-3rem ml-1" icon="pi pi-print" />
      <Button v-tooltip.top="'Attachment'" class="w-3rem ml-1" icon="pi pi-paperclip" severity="primary" @click="handleAttachmentDialogOpen" />
      <Button v-tooltip.top="'Show history'" class="w-3rem ml-1" @click="handleAttachmentHistoryDialogOpen">
        <template #icon>
          <span class="flex align-items-center justify-content-center p-0">
            <svg xmlns="http://www.w3.org/2000/svg" height="15px" viewBox="0 -960 960 960" width="15px" fill="#e8eaed"><path d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z" /></svg>
          </span>
        </template>
      </Button>
      <Button v-tooltip.top="'Add booking'" class="w-3rem ml-1" disabled icon="pi pi-plus" />
      <Button v-tooltip.top="'New Adjustment'" class="w-3rem ml-1" icon="pi pi-dollar" severity="primary" @click="openDialogPaymentDetails($event)" />

      <!-- <Button v-tooltip.top="'Edit Detail'" class="w-3rem" icon="pi pi-pen-to-square" severity="secondary" @click="deletePaymentDetail($event)" /> -->
      <Button v-tooltip.top="'Import'" class="w-3rem ml-1" disabled icon="pi pi-download" />

      <Button v-tooltip.top="'Update'" class="w-3rem mx-1" icon="pi pi-replay" :loading="loadingSaveAll" />

      <Button v-tooltip.top="'Cancel'" class="w-3rem ml-1" icon="pi pi-times" severity="secondary" @click="goToList" />
    </div>
    <DialogPaymentDetailForm
      :key="dialogPaymentDetailFormReload"
      :visible="onOffDialogPaymentDetail"
      :fields="fieldAdjustments"
      :item="itemDetails"
      :loading-save-all="loadingSaveAdjustment"
      title=" New Adjustment"
      :selected-payment="item"
      :is-split-action="isSplitAction"
      @update:visible="onCloseDialog($event)"
      @save="saveAndReload($event)"
    />

    <div v-if="attachmentDialogOpen">
      <AttachmentIncomeDialog :close-dialog="() => { attachmentDialogOpen = false }" :is-creation-dialog="false" header="Manage Income Attachment" :open-dialog="attachmentDialogOpen" :selected-invoice="idItem" :selected-invoice-obj="item" />
    </div>

    <div v-if="attachmentHistoryDialogOpen">
      <AttachmentIncomeHistoryDialog :close-dialog="() => { attachmentHistoryDialogOpen = false }" header="Attachment Status History" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="idItem" :selected-invoice-obj="item" :is-creation-dialog="false" />
    </div>
  </div>
</template>

<style lang="scss">
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
    .p-tabview-nav-link:hover{
      border-bottom-color: transparent !important;
    }
  }

  .p-tabview-panels {
    padding: 0 !important;
  }
}

.no-global-style .p-tabview-nav li.p-highlight .p-tabview-nav-link {
    background: #d8f2ff;
    border-color: #0F8BFD;
    color: #0F8BFD;
}
</style>
