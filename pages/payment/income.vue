<script setup lang="ts">
import dayjs from 'dayjs'
import { z } from 'zod'
import { computed, onMounted, reactive, ref, watch } from 'vue'

import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { PageState } from 'primevue/paginator'
import InputNumber from 'primevue/inputnumber'
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import IncomeAdjustmentDialog from '~/components/income/IncomeAdjustmentDialog.vue'
import AttachmentIncomeDialog from '~/components/income/attachment/AttachmentIncomeDialog.vue'
import IncomeHistoryDialog from '~/components/income/IncomeHistoryDialog.vue'
import { statusToString } from '~/utils/helpers'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'

const toast = useToast()
const confirm = useConfirm()
const { data: userData } = useAuth()
const idItemToLoadFirstTime = ref('')
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
const invoiceTypeList = ref<any[]>([])
const invoiceStatusList = ref<any[]>([])
const agencyList = ref<any[]>([])
const hotelList = ref<any[]>([])
const bookingList = ref<any[]>([])
const roomRateList = ref<any[]>([])
const totalAmount = ref(0)
const totalHotel = ref(0)
const totalhotelbooking = ref(0)
const totalamountbooking = ref(0)
const totalInvoice = ref(0)
const onOffDialogPaymentDetail = ref(false)
const attachmentHistoryDialogOpen = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)
const exportAttachmentsDialogOpen = ref<boolean>(false)
const LocalAttachmentList = ref<any[]>([])
const AdjustmentList = ref<any[]>([])
const AdjustmentTableList = ref<any[]>([])
const activeTab = ref(2)

const selectedRoomRate = ref<any>()
const roomRateContextMenu = ref()
const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'income',
})

const confApiPaymentDetail = reactive({
  moduleApi: 'invoicing',
  uriApi: 'income-adjustment',
})
const confApiInvoiceAdjustment = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment',
})
const confRoomRateApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-room-rate'
})

const confAdjustmentApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment'
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
  showFilters: false,
})

const roomRateOptions = ref({
  tableName: 'roomRate',
  loading: false,
  actionsAsMenu: false,
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
  showFilters: false,
})

const adjustmentOptions = ref({
  tableName: 'Adjustments',
  loading: false,
  actionsAsMenu: false,
})

function handleAttachmentHistoryDialogOpen() {
  attachmentHistoryDialogOpen.value = true
}

function onRowRightClick(event: any) {
  selectedRoomRate.value = event.data
  roomRateContextMenu.value.show(event.originalEvent)
}

const menuModel = ref([
  { label: 'Add Adjustment', command: () => AdjustmentDialogOpen() },
])

// Attachments
function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

function AdjustmentDialogOpen() {
  openDialogPaymentDetails()
  activeTab.value = 2
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
  invoiceStatus: { moduleApi: 'invoicing', uriApi: 'manage-invoice-status' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  bankAccount: { moduleApi: 'settings', uriApi: 'manage-bank-account' },
  paymentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  attachmentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' },
})

const objApisLoading = ref({
  invoiceType: false,
  invoiceStatus: false,
})

const columns: IColumn[] = [
  { field: 'bookingId', header: 'ID', type: 'text' },
  // { field: 'agency', header: 'Agency', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'fullName', header: 'Full Name', type: 'text' },
  { field: 'hotelBookingNumber', header: 'Reservation No.', type: 'text' },
  { field: 'cuponNumber', header: 'Coupon No.', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-room-type' } },
  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'nights', header: 'Nights', type: 'text' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'text' },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'number' },
  { field: 'invoiceAmount', header: 'Booking Amount', type: 'number' },

]
const adjustmentColumns: IColumn[] = [
  { field: 'adjustmentId', header: 'ID', type: 'text' },
  { field: 'amount', header: 'Amount', type: 'number' },
  // { field: 'amountTemp', header: 'Amount', type: 'text' },
  { field: 'roomRateId', header: 'Room Rate', type: 'text' },
  { field: 'transaction', header: 'Category', type: 'text' },
  { field: 'date', header: 'Transaction Date', type: 'text' },
  { field: 'employee', header: 'Employee', type: 'text' },
  { field: 'remark', header: 'Description', type: 'text' },
]

const roomRateColumns: IColumn[] = [
  { field: 'roomRateId', header: 'Id', type: 'text' },
  // { field: 'fullName', header: 'Full Name', type: 'text' },
  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'adults', header: 'Adults', type: 'text' },
  { field: 'children', header: 'Children', type: 'text' },
  // { field: 'roomType', header: 'Room Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-room-type' } },
  { field: 'nights', header: 'Nights', type: 'text' },
  // { field: 'ratePlan', header: 'Rate Plan', type: 'text' },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'number' },
  { field: 'invoiceAmount', header: 'Rate Amount', type: 'number' },
]

const formTitle = computed(() => {
  return 'New Income'
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'incomeId',
    header: 'ID',
    disabled: true,
    dataType: 'text',
    class: 'field col-12 md:col-3',
  },
  // {
  //   field: 'dueDate',
  //   header: 'Due Date',
  //   dataType: 'date',
  //   class: 'field col-12 md:col-2 required ',
  //   validation: z.date({
  //     required_error: 'The Due Date field is required',
  //     invalid_type_error: 'The Due Date field is required',
  //   }).max(dayjs().endOf('day').toDate(), 'The Due Date field cannot be greater than current date')
  // },
  {
    field: 'invoiceDate',
    header: 'Invoice Date',
    dataType: 'date',
    class: 'field col-12 md:col-3 required ',
    validation: z.date({
      required_error: 'The Invoice Date field is required',
      invalid_type_error: 'The Invoice Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'invoiceType',
    header: 'Invoice Type',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    validation: validateEntityStatus('Invoice Type'),
    disabled: true,
  },
  {
    field: 'invoiceNumber',
    header: 'Invoice Number',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-3',
  },
  {
    field: 'incomeAmount',
    header: 'Invoice Amount',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-3 required',
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
    field: 'invoiceStatus',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-2',
    validation: validateEntityStatus('Status'),
    disabled: true,
  },
  // {
  //   field: 'reSend',
  //   header: 'Re-Send',
  //   dataType: 'check',
  //   class: 'field col-12 md:col-1 mt-3 mb-3',
  // },
  // {
  //   field: 'reSendDate',
  //   header: 'Re-Send Date',
  //   dataType: 'date',
  //   class: 'field col-12 md:col-2',
  //   validation: z
  //     .union([z.date(), z.null()])
  //     .refine(date => !date || date <= dayjs().endOf('day').toDate(), {
  //       message: 'The Re-Send Date field cannot be greater than current date',
  //     })
  // },
  {
    field: 'manual',
    header: 'Manual',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 md:col-1 flex align-items-end pb-2',
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
  hasAttachments: false
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
  hasAttachments: false
})

const fieldAdjustments = ref<FieldDefinitionType[]>([

  // {
  //   field: 'amount',
  //   header: 'Amount',
  //   dataType: 'text',
  //   class: 'field col-12 required',
  //   validation: z.string().min(1, { message: 'The amount field is required' })
  //     .regex(/^-?\d+(\.\d{1,2})?$/, { message: 'The amount does not meet the correct format of n integer digits and 2 decimal digits' })
  //     .refine(value => Number.parseFloat(value) !== 0, { message: 'The amount field must be different from zero' }),
  // },
  {
    field: 'amount',
    header: 'Amount',
    dataType: 'number',
    class: 'field col-12 required',
    validation: z.number()
      .positive({ message: 'The amount must be a positive number' })
      .refine(value => Number.isInteger(value * 100), { message: 'The amount must have up to 2 decimal places' })
      .refine(value => value !== 0, { message: 'The amount field must be different from zero' }),
  },

  {
    field: 'date',
    header: 'Date',
    dataType: 'date',
    class: 'field col-12 required ',
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
    header: 'Remark',
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
const payloadOnChangePage = ref<PageState>()

function clearForm() {
  item.value = { ...itemTemp.value }
  formReload.value++
}

function clearFormDetails() {
  itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
}

function openDialogPaymentDetails() {
  itemDetails.value = { ...itemDetailsTemp.value }
  onOffDialogPaymentDetail.value = true
  onOffDialogPaymentDetail.value = true
}

// Listado de booking
async function getBookingList() {
  try {
    idItemToLoadFirstTime.value = ''
    bookingOptions.value.loading = true
    bookingList.value = []
    totalhotelbooking.value = 0
    totalamountbooking.value = 0
    const response = await GenericService.search(confBookingApi.moduleApi, confBookingApi.uriApi, bookingPayload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    bookingPagination.value.page = page
    bookingPagination.value.limit = size
    bookingPagination.value.totalElements = totalElements
    bookingPagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      totalhotelbooking.value += iterator.hotelAmount // Asegúrate de que 'amount' esté definido en 'iterator'
      totalamountbooking.value += iterator.invoiceAmount // Asegúrate de que 'invoiceAmount' esté definido en 'iterator'

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
    const selectedRoomRateId = ref(null)

    totalHotel.value = 0
    totalInvoice.value = 0
    const response = await GenericService.search(confRoomRateApi.moduleApi, confRoomRateApi.uriApi, roomRatePayload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    roomRatePagination.value.page = page
    roomRatePagination.value.limit = size
    roomRatePagination.value.totalElements = totalElements
    roomRatePagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      totalHotel.value += iterator.hotelAmount // Asegúrate de que 'amount' esté definido en 'iterator'
      totalInvoice.value += iterator.invoiceAmount // Asegúrate de que 'invoiceAmount' esté definido en 'iterator'

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
      selectedRoomRateId.value = roomRateList.value[0].id
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
    totalAmount.value = 0
    const response = await GenericService.search(confAdjustmentApi.moduleApi, confAdjustmentApi.uriApi, adjustmentPayload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      totalAmount.value += iterator.amount
      AdjustmentTableList.value = [...AdjustmentTableList.value, {
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        date: iterator.date.substring(0, 10),
        transaction: iterator.paymentTransactionType ? `${iterator.paymentTransactionType.code} - ${iterator.paymentTransactionType.name}` : '',
        roomRateId: iterator.roomRate?.roomRateId,
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

async function validateIncomeCloseOperation(item: { [key: string]: any }) {
  if (item) {
    const hotelId = Object.prototype.hasOwnProperty.call(item.hotel, 'id') ? item.hotel.id : item.hotel
    const dateList: string[] = AdjustmentList.value.map((e: any) => e.date)
    if (item.invoiceDate) {
      dateList.push(dayjs(item.invoiceDate).format('YYYY-MM-DD'))
    }
    await validateCloseOperation(hotelId, dateList)
  }
}

async function validateCloseOperation(hotelId: string, dateList: string[]) {
  const payload = {
    hotelId,
    dates: dateList
  }
  await GenericService.create(confApi.moduleApi, 'check-dates', payload)
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    const payload: { [key: string]: any } = { ...item }
    payload.dueDate = payload.dueDate ? dayjs(payload.dueDate).format('YYYY-MM-DDTHH:mm:ss') : payload.invoiceDate ? dayjs(payload.invoiceDate).format('YYYY-MM-DDTHH:mm:ss') : ''
    payload.reSendDate = payload.reSendDate ? dayjs(payload.reSendDate).format('YYYY-MM-DDTHH:mm:ss') : ''
    payload.invoiceDate = payload.invoiceDate ? dayjs(payload.invoiceDate).format('YYYY-MM-DDTHH:mm:ss') : ''
    payload.invoiceType = Object.prototype.hasOwnProperty.call(payload.invoiceType, 'id') ? payload.invoiceType.id : payload.invoiceType
    payload.invoiceStatus = Object.prototype.hasOwnProperty.call(payload.invoiceStatus, 'id') ? payload.invoiceStatus.id : payload.invoiceStatus
    payload.agency = Object.prototype.hasOwnProperty.call(payload.agency, 'id') ? payload.agency.id : payload.agency
    payload.hotel = Object.prototype.hasOwnProperty.call(payload.hotel, 'id') ? payload.hotel.id : payload.hotel
    payload.status = statusToString(payload.status)
    payload.employee = userData?.value?.user?.name
    payload.employeeId = userData?.value?.user?.userId

    let totalIncomeAmount = 0
    if (Array.isArray(payload.incomeAmount)) {
      totalIncomeAmount = payload.incomeAmount.reduce((acc, curr) => acc + Number(curr), 0)
    }
    else {
      totalIncomeAmount = Number(payload.incomeAmount)
    }

    // Asignación del invoiceAmount total al payload
    payload.incomeAmount = totalIncomeAmount
    if (LocalAttachmentList.value.length > 0) {
      payload.attachments = LocalAttachmentList.value.map(item => ({
        filename: item.filename,
        file: item.file,
        remark: item.remark,
        type: item.type.id,
        employee: item.employee,
        employeeId: item.employeeId,
        paymentResourceType: item.paymentResourceType.id,
      }))
    }
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    if (response && response.id) {
      // Guarda el id del elemento creado
      idItem.value = response.id
      LocalAttachmentList.value = []
      toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Invoice ${response.invoiceNumber ?? ''} was created successfully`, life: 10000 })
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
      await validateIncomeCloseOperation(item)
      await createItem(item)
      // Deshabilitar campos restantes del formulario
      updateFieldProperty(fields, 'reSend', 'disabled', true)
      updateFieldProperty(fields, 'reSendDate', 'disabled', true)
      if (AdjustmentList.value.length > 0) {
        await createAdjustment(AdjustmentList.value)
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
        // if (response.invoiceStatus) {
        //   response.invoiceStatus.name = `${response.invoiceStatus.code} - ${response.invoiceStatus.name}`
        //   invoiceStatusList.value = [response.invoiceStatus]
        //   item.value.invoiceStatus = response.invoiceStatus
        // }
        item.value.reSend = response.reSend
        item.value.reSendDate = response.reSendDate ? dayjs(response.reSendDate).toDate() : response.reSendDate
        response.agency.status = 'ACTIVE'
        agencyList.value = [response.agency]
        item.value.agency = response.agency
        item.value.agency.name = `${response.agency.code} - ${response.agency.name}`
        item.value.incomeAmount = response.invoiceAmount
        hotelList.value = [response.hotel]
        item.value.hotel = response.hotel
        item.value.hotel.name = `${response.hotel.code} - ${response.hotel.name}`
        item.value.invoiceDate = dayjs(response.invoiceDate).toDate()
        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        item.value.hasAttachments = response.hasAttachments
        if (response.manageInvoiceType) {
          response.manageInvoiceType.name = `${response.manageInvoiceType.code} - ${response.manageInvoiceType.name}`
          response.manageInvoiceType.status = 'ACTIVE'
          invoiceTypeList.value = [response.manageInvoiceType]
          item.value.invoiceType = response.manageInvoiceType
        }
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

async function createAdjustment(items: any[]) {
  try {
    // console.log(item)
    if (items.length > 0) {
      loadingSaveAdjustment.value = true

      const transformed = {
        status: 'ACTIVE',
        income: idItem.value,
        employee: userData?.value?.user?.name,
        adjustments: items.map(item => ({
          transactionType: item.transactionType ? item.transactionType.id : null,
          amount: Number.parseFloat(item.amount),
          date: item.date ? dayjs(item.date).format('YYYY-MM-DD') : '',
          remark: item.remark
        }))
      }
      await GenericService.create(confApiPaymentDetail.moduleApi, 'income-adjustment', transformed)

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

async function createInvoiceAdjustment(item: { [key: string]: any }) {
  try {
    if (item) {
      loadingSaveAdjustment.value = true
      const payload: { [key: string]: any } = { ...item }
      payload.amount = Number.parseFloat(payload.amount)
      payload.employee = userData?.value?.user?.name
      payload.paymentTransactionType = payload.transactionType && Object.prototype.hasOwnProperty.call(payload.transactionType, 'id') ? payload.transactionType.id : payload.transactionType
      payload.description = item.remark
      payload.income = idItem.value // Usar el ID del registro de "income" creado anteriormente
      // payload.date = payload.date
      if (selectedRoomRate.value) {
        payload.roomRate = selectedRoomRate.value.id
      }
      delete payload.remark
      delete payload.transactionType
      await GenericService.create(confApiInvoiceAdjustment.moduleApi, confApiInvoiceAdjustment.uriApi, payload)

      onOffDialogPaymentDetail.value = false
      loadingSaveAdjustment.value = false
      getBookingList()
      getRoomRateList()
      getAdjustmentList()
      getItemById(idItem.value)
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

function saveLocal(itemP: { [key: string]: any }) {
  const localAdjustment: { [key: string]: any } = { ...itemP }
  const adjustmentDate = itemP.date ? dayjs(itemP.date).format('YYYY-MM-DD') : ''
  const itemAmount = Number(itemP.amount) + Number(item.value.incomeAmount)
  localAdjustment.date = adjustmentDate
  localAdjustment.employee = userData?.value?.user?.name
  localAdjustment.id = AdjustmentList.value.length
  localAdjustment.amountTemp = formatNumber(itemAmount)
  if (AdjustmentList.value.length > 0) {
    AdjustmentList.value.push(localAdjustment)
  }
  else {
    AdjustmentList.value = [localAdjustment]
  }
  AdjustmentTableList.value = [...AdjustmentList.value].map(({ transactionType, ...rest }) => ({
    transaction: transactionType?.name ?? '',
    ...rest
  }))
  pagination.value.totalElements = AdjustmentTableList.value.length
  totalAmount.value = itemAmount
  // BOOKING
  if (AdjustmentList.value.length === 1) {
    const localBooking: { [key: string]: any } = {}
    localBooking.checkIn = adjustmentDate
    localBooking.checkOut = adjustmentDate
    localBooking.nights = '0'
    localBooking.hotelAmount = '0'
    localBooking.invoiceAmount = itemAmount
    bookingList.value = [localBooking]
    bookingPagination.value.totalElements = bookingList.value.length
    // ROOM RATE
    const localRoomRate: { [key: string]: any } = {}
    localRoomRate.checkIn = adjustmentDate
    localRoomRate.checkOut = adjustmentDate
    localRoomRate.nights = '0'
    localRoomRate.hotelAmount = '0'
    localRoomRate.invoiceAmount = itemAmount
    roomRateList.value = [localRoomRate]
    roomRatePagination.value.totalElements = roomRateList.value.length
  }
  else {
    bookingList.value[0].invoiceAmount = itemAmount
    roomRateList.value[0].invoiceAmount = itemAmount
  }
  totalamountbooking.value = itemAmount
  totalInvoice.value = itemAmount
  item.value.incomeAmount = itemAmount
  formReload.value++
}

async function saveAndReload(itemP: { [key: string]: any }) {
  try {
    loadingSaveAdjustment.value = true
    if (idItem.value) {
      /* const hotelId = Object.prototype.hasOwnProperty.call(item.value.hotel, 'id') ? item.value.hotel.id : item.value.hotel
      const dateList: string[] = []
      if (itemP.date) {
        dateList.push(dayjs(itemP.date).format('YYYY-MM-DD'))
      }
      await validateCloseOperation(hotelId, dateList) */
      await createInvoiceAdjustment(itemP)
      onOffDialogPaymentDetail.value = false
    }
    else {
      saveLocal(itemP)
      onOffDialogPaymentDetail.value = false
    }
    // await getItemById(idItem.value)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAdjustment.value = false
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

async function getInvoiceTypeListDefault(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objApisLoading.value.invoiceType = true
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
      item.value.invoiceType = invoiceTypeList.value[0]
    }
    else {
      invoiceTypeList.value = []
    }
    formReload.value++
  }
  finally {
    objApisLoading.value.invoiceType = false
  }
}

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

async function getInvoiceStatusListDefault(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objApisLoading.value.invoiceStatus = true
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
      item.value.invoiceStatus = invoiceStatusList.value[0]
    }
    else {
      invoiceStatusList.value = []
    }
    formReload.value++
  }
  finally {
    objApisLoading.value.invoiceStatus = false
  }
}

async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  const additionalFilter: FilterCriteria[] = [
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE'
    }
  ]

  agencyList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction)
}

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  const additionalFilter: FilterCriteria[] = [
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE'
    }
  ]

  hotelList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction)
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

function onCloseAdjustmentDialog() {
  if (AdjustmentList.value.length === 0) {
    navigateTo('/payment')
  }
}

async function loadDefaultsValues() {
  const objQueryToSearch = {
    query: '',
    keys: ['name', 'code'],
  }
  const filter: FilterCriteria[] = [{
    key: 'status',
    logicalOperation: 'AND',
    operator: 'EQUALS',
    value: 'ACTIVE',
  }]
  getInvoiceTypeListDefault(objApis.value.invoiceType.moduleApi, objApis.value.invoiceType.uriApi, objQueryToSearch, filter)
  getInvoiceStatusListDefault(objApis.value.invoiceStatus.moduleApi, objApis.value.invoiceStatus.uriApi, objQueryToSearch, filter)
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
            @update:model-value="($event: any) => {
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
            @update:model-value="($event: any) => {
              onUpdate('dueDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" />
        </template>
        <template #field-incomeAmount="{ item: data, onUpdate }">
          <InputNumber
            v-if="!loadingSaveAll"
            v-model="data.incomeAmount"
            show-clear :disabled="true"
            @update:model-value="($event: any) => {
              onUpdate('incomeAmount', $event)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-invoiceStatus="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll && !objApisLoading.invoiceStatus"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.invoiceStatus"
            :suggestions="[...invoiceStatusList]"
            :disabled="true"
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
            v-if="!loadingSaveAll && !objApisLoading.invoiceType"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.invoiceType"
            :suggestions="[...invoiceTypeList]"
            :disabled="true"
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
              item.agency = $event
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
              item.hotel = $event
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
        >
          <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center ">
              <Row>
                <Column footer="Totals:" :colspan="9" footer-style="text-align:right; font-weight: bold; color:#ffffff; background-color:#0F8BFD;" />
                <Column :footer="formatNumber(totalhotelbooking)" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column :footer="formatNumber(totalamountbooking)" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
              </Row>
            </ColumnGroup>
          </template>
        </DynamicTable>
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
          @on-row-right-click="onRowRightClick($event)"
        >
          <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center ">
              <Row>
                <Column footer="Totals:" :colspan="6" footer-style="text-align:right; font-weight: bold; color:#ffffff; background-color:#0F8BFD;" />
                <Column :footer="formatNumber(totalHotel)" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column :footer="formatNumber(totalInvoice)" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
              </Row>
            </ColumnGroup>
          </template>
        </DynamicTable>
        <ContextMenu ref="roomRateContextMenu" :model="menuModel" />
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
        >
          <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center ">
              <Row>
                <Column footer="Total:" :colspan="1" footer-style="text-align:right; font-weight: bold; color:#ffffff; background-color:#0F8BFD;" />
                <Column :footer="formatNumber(totalAmount)" footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column :colspan="5" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
              </Row>
            </ColumnGroup>
          </template>
        </DynamicTable>
      </TabPanel>
    </TabView>
    <!-- @on-confirm-create="goToCreateForm"
      @on-change-filter="parseDataTableFilter"
      @on-list-item="resetListItems"
      @on-sort-field="onSortField" -->

    <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
      <Button v-tooltip.top="'Save'" class="w-3rem" icon="pi pi-save" :disabled="idItem.length > 0 || (idItem === '' && LocalAttachmentList.length === 0)" @click="forceSave = true" />
      <Button
        v-tooltip.top="'Print'" class="w-3rem ml-1" icon="pi pi-print" :disabled="!item.hasAttachments" @click="() => {
          exportAttachmentsDialogOpen = true
        }"
      />
      <Button v-tooltip.top="'Attachment'" class="w-3rem ml-1" icon="pi pi-paperclip" severity="primary" @click="handleAttachmentDialogOpen" />
      <Button v-tooltip.top="'Show history'" class="w-3rem ml-1" :disabled="idItem.length === 0" @click="handleAttachmentHistoryDialogOpen">
        <template #icon>
          <span class="flex align-items-center justify-content-center p-0">
            <svg xmlns="http://www.w3.org/2000/svg" height="15px" viewBox="0 -960 960 960" width="15px" fill="#e8eaed"><path d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z" /></svg>
          </span>
        </template>
      </Button>
      <Button v-tooltip.top="'Add booking'" class="w-3rem ml-1" disabled icon="pi pi-plus" />
      <!-- <Button v-tooltip.top="'New Adjustment'" class="w-3rem ml-1" icon="pi pi-dollar" severity="primary" @click="openDialogPaymentDetails()" /> -->

      <!-- <Button v-tooltip.top="'Edit Detail'" class="w-3rem" icon="pi pi-pen-to-square" severity="secondary" @click="deletePaymentDetail($event)" /> -->
      <Button v-tooltip.top="'Import'" class="w-3rem ml-1" disabled icon="pi pi-download" />

      <Button v-if="false" v-tooltip.top="'Update'" class="w-3rem mx-1" icon="pi pi-replay" :loading="loadingSaveAll" />

      <Button v-tooltip.top="'Cancel'" class="w-3rem ml-1" icon="pi pi-times" severity="secondary" @click="() => navigateTo('/payment')" />
    </div>
    <IncomeAdjustmentDialog
      :key="dialogPaymentDetailFormReload"
      :visible="onOffDialogPaymentDetail"
      :fields="fieldAdjustments"
      :item="itemDetails"
      :loading-save-all="loadingSaveAdjustment"
      title="New Adjustment"
      :selected-room-rate="selectedRoomRate"
      :is-split-action="isSplitAction"
      @update:visible="onCloseDialog($event)"
      @save="saveAndReload($event)"
      @on-close="onCloseAdjustmentDialog"
    />
    <div v-if="attachmentDialogOpen">
      <AttachmentIncomeDialog
        :close-dialog="(total: number) => {
          attachmentDialogOpen = false
          item.hasAttachments = total > 0
        }" :is-creation-dialog="idItem === ''" header="Manage Income Attachment" :open-dialog="attachmentDialogOpen" :selected-invoice="idItem" :selected-invoice-obj="item"
        :list-items="LocalAttachmentList" @update:list-items="($event) => LocalAttachmentList = [...LocalAttachmentList, $event]"
        @delete-list-items="($id) => LocalAttachmentList = LocalAttachmentList.filter(item => item.id !== $id)"
      />
    </div>

    <div v-if="attachmentHistoryDialogOpen">
      <IncomeHistoryDialog :close-dialog="() => { attachmentHistoryDialogOpen = false }" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="idItem" :selected-invoice-obj="item" header="Income Status History" />
    </div>
    <div v-if="exportAttachmentsDialogOpen">
      <PrintInvoiceDialog :close-dialog="() => { exportAttachmentsDialogOpen = false }" :open-dialog="exportAttachmentsDialogOpen" :invoice="item" />
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
.footer-table{
 background: #d8f2ff;
    border-color: #0F8BFD;
    color: #0F8BFD;
}
.no-global-style .p-tabview-nav li.p-highlight .p-tabview-nav-link {
    background: #d8f2ff;
    border-color: #0F8BFD;
    color: #0F8BFD;
}
</style>
