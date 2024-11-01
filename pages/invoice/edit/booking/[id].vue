<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import dayjs from 'dayjs'
import getUrlByImage from '~/composables/files'
import { ModulesService } from '~/services/modules-services'
import { GenericService } from '~/services/generic-services'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'
import AttachmentDialog from '~/components/invoice/attachment/AttachmentDialog.vue'
import AttachmentHistoryDialog from '~/components/invoice/attachment/AttachmentHistoryDialog.vue'
import { CALENDAR_MODE } from '~/utils/Enums'

const toast = useToast()
const { data: userData } = useAuth()

const forceUpdate = ref(false)
const active = ref(0)
const saveButton = ref(null)
const onSaveButtonByRef = ref(false)

const route = useRoute()

// @ts-expect-error
const selectedInvoice = ref<string>(route.params.id.toString())
const selectedRoomRate = ref<string>('')

const loadingSaveAll = ref(false)
const loadingDelete = ref(false)

const invoiceAmount = ref(0)
const requiresFlatRate = ref(false)

const bookingDialogOpen = ref<boolean>(false)
const roomRateDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)
const attachmentHistoryDialogOpen = ref<boolean>(false)
const exportAttachmentsDialogOpen = ref<boolean>(false)

const invoiceAgency = ref<any>(null)
const invoiceHotel = ref<any>(null)

const attachmentDialogOpen = ref<boolean>(false)

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])
const invoiceTypeList = ref<any[]>([])

const invoiceStatus = ref<any>(null)

const fieldsV2: Array<FieldDefinitionType> = [
  // Booking Id

  {
    field: 'bookingId',
    header: 'Booking Id',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    disabled: true,
  },
  // Invoice Original Amount
  {
    field: 'invoiceOriginalAmount',
    header: 'Invoice Original Amount',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    disabled: true,
    headerClass: 'mb-1',
  },

  // //Adults
  {
    field: 'adults',
    header: 'Adult',
    dataType: 'number',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    disabled: true,
    validation: z.number().min(1, 'The Adults field must be greater than 0').nullable()
  },

  // Room Type
  {
    field: 'roomType',
    header: 'Room Type',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },

  // Check In
  // Hotel Creation Date
  {
    field: 'hotelCreationDate',
    header: 'Hotel Creation Date',
    dataType: 'date',
    class: 'field col-12 md:col-3 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Hotel Creation Date field is required',
      invalid_type_error: 'The Hotel Creation Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Hotel Creation Date field cannot be greater than current date')

  },
  // Invoice Amount
  {
    field: 'invoiceAmount',
    header: 'Booking Amount',
    dataType: 'number',
    class: 'field col-12 md:col-3 required',
    disabled: true,
    headerClass: 'mb-1',
    ...(
      route.query.type === InvoiceType.OLD_CREDIT
      || route.query.type === InvoiceType.CREDIT
        ? {
            validation:
        z.string()
          .min(0, 'The Invoice Amount field is required')
          .refine((value: any) => !isNaN(value) && +value < 0, { message: 'The Invoice Amount field must be negative' })
          }
        : {
            validation:
        z.string()
          .min(0, 'The Invoice Amount field is required')
          .refine((value: any) => !isNaN(value) && +value >= 0, { message: 'The Invoice Amount field must be greater or equals than 0' })
          }
    )
  },
  // // Children
  {
    field: 'children',
    header: 'Children',
    dataType: 'number',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    disabled: true,
    validation: z.number().nonnegative('The Children field must not be negative').nullable()
  },

  // Rate Plan
  {
    field: 'ratePlan',
    header: 'Rate Plan',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },

  {
    field: 'bookingDate',
    header: 'Booking Date',
    dataType: 'date',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },

  {
    field: 'firstName',
    header: 'First Name',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The First Name field is required')
  },

  // Hotel Invoice Number
  {
    field: 'hotelInvoiceNumber',
    header: 'Hotel Invoice No',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: z.string().refine((val: string) => {
      if ((Number(val) < 0)) {
        return false
      }
      return true
    }, { message: 'The Hotel Invoice No. field must not be negative' }).nullable()
  },

  // Room Category
  {
    field: 'roomCategory',
    header: 'Room Category',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',

  },
  {
    field: 'checkIn',
    header: 'Check In',
    dataType: 'date',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    disabled: true,
    validation: z.date({
      required_error: 'The Check In field is required',
      invalid_type_error: 'The Check In field is required',
    })
  },
  {
    field: 'lastName',
    header: 'Last Name',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The Last Name field is required')
  },
  // Folio Number
  {
    field: 'folioNumber',
    header: 'Folio Number',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },
  // Hotel Amount
  {
    field: 'hotelAmount',
    header: 'Hotel Amount',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    disabled: true,
    validation: z.string().trim().regex(/^\d+$/, 'The Hotel Amount field must be greater than or equal to 0')
    // validation: z.string().trim().regex(/^\d+$/, 'Only numeric characters allowed')
  },
  // Check Out
  {
    field: 'checkOut',
    header: 'Check Out',
    dataType: 'date',
    disabled: true,
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The Check Out field is required',
      invalid_type_error: 'The Check Out field is required',
    })
  },

  // Room Number
  {
    field: 'roomNumber',
    header: 'Room Number',
    dataType: 'text',
    class: 'field col-12 md:col-3 ',
    headerClass: 'mb-1',
  },

  // Night Type
  {
    field: 'nightType',
    header: 'Night Type',
    dataType: 'select',
    class: `field col-12 md:col-3`,
    headerClass: 'mb-1',
  },

  // Booking Balance
  {
    field: 'dueAmount',
    header: 'Booking Balance',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    disabled: true,
    headerClass: 'mb-1',
  },
  // Hotel Booking No.
  {
    field: 'hotelBookingNumber',
    header: 'Hotel Booking No.',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The Hotel Booking No. field is required').regex(/^[IG] +\d+ +\d{2,}\s*$/, 'The Hotel Booking No. field has an invalid format. Examples of valid formats are I 3432 15 , G 1134 44')
  },

  // Coupon No.
  {
    field: 'couponNumber',
    header: 'Coupon No.',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },

  //  Contract
  {
    field: 'contract',
    header: 'Contract',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    // validation: z.string().regex(/^[a-z0-9]+$/i, 'No se permiten caracteres especiales').nullable()
  },

  // // Rate Adult
  // {
  //   field: 'rateAdult',
  //   header: 'Rate Adult',
  //   dataType: 'number',
  //   class: 'field col-12 md:col-3',
  //   headerClass: 'mb-1',
  //   validation: z.number().min(0, 'The Rate Adult field must be greater or equal than 0')
  // },
  // Rate Child
  // {
  //   field: 'rateChild',
  //   header: 'Rate Child',
  //   dataType: 'number',
  //   class: 'field col-12 md:col-3',
  //   headerClass: 'mb-1',
  //   validation: z.number().nonnegative('The Rate Child field must be greater than 0').nullable()
  // },

  // Description
  {
    field: 'description',
    header: 'Remark',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },
]

const item2 = ref<GenericObject>({
  bookingId: '-',
  hotelCreationDate: new Date(),
  bookingDate: new Date(),
  checkIn: new Date(),
  checkOut: new Date(),
  hotelBookingNumber: '',
  fullName: '',
  firstName: '',
  lastName: '',
  invoiceAmount: '0',
  roomNumber: '0',
  couponNumber: '',
  adults: 0,
  children: 0,
  rateAdult: 0,
  rateChild: 0,
  hotelInvoiceNumber: '',
  folioNumber: '',
  hotelAmount: '0',
  description: '',
  invoice: '',
  ratePlan: null,
  nightType: null,
  roomType: null,
  roomCategory: null,
  dueAmount: 0,
  contract: '',
  id: ''
})

const itemTemp2 = ref<GenericObject>({
  bookingId: '-',
  hotelCreationDate: new Date(),
  bookingDate: new Date(),
  checkIn: new Date(),
  checkOut: new Date(),
  hotelBookingNumber: '',
  fullName: '',
  firstName: '',
  lastName: '',

  invoiceAmount: '0',
  roomNumber: '0',
  couponNumber: '',
  adults: 0,
  children: 0,
  rateAdult: 0,
  rateChild: 0,
  hotelInvoiceNumber: '',
  folioNumber: '',
  hotelAmount: '0',
  description: '',
  invoice: '',
  ratePlan: null,
  nightType: null,
  roomType: null,
  roomCategory: null,
  dueAmount: 0,
  contract: '',
  id: ''
})

// const menuModel = ref([
//   { label: 'Add Adjustment', command: () => props.openAdjustmentDialog(selectedRoomRate.value), disabled: computedShowMenuItemAddAdjustment },
//   { label: 'Edit Room Rate', command: () => openEditDialog(selectedRoomRate.value), disabled: computedShowMenuItemEditRoomRate },

// ])

const roomRateList = ref<any[]>([])
const adjustmentList = ref<any[]>([])
const ratePlanList = ref<any[]>([])
const roomCategoryList = ref<any[]>([])
const roomTypeList = ref<any[]>([])
const nightTypeList = ref<any[]>([])
const activeTab = ref(0)
const requiresFlatRateCheck = ref(false)

const totalInvoiceAmount = ref<number>(0)
const totalHotelAmount = ref<number>(0)
const roomRateContextMenu = ref()

const totalAmountAdjustment = ref<number>(0)

const confApi = reactive({
  booking: {
    moduleApi: 'invoicing',
    uriApi: 'manage-booking',
  },
  roomRate: {
    moduleApi: 'invoicing',
    uriApi: 'manage-room-rate',
  },
  adjustment: {
    moduleApi: 'invoicing',
    uriApi: 'manage-adjustment',
  },
  invoice: {
    moduleApi: 'invoicing',
    uriApi: 'manage-invoice',
  },
})
const confratePlanApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-rate-plan',
  keyValue: 'name'
})
const confroomCategoryApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-room-category',
  keyValue: 'name'
})
const confroomTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-room-type',
  keyValue: 'name'
})
const confnightTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-night-type',
  keyValue: 'name'
})

const transactionTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-transaction-type',
  keyValue: 'name'
})

const optionsRoomRate = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-room-rate',
  loading: false,
  showFilters: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
})

const optionsAdjustment = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment',
  messageToDelete: 'Do you want to save the change?',
  loading: false,
  showFilters: false,
  actionsAsMenu: false,
})

const columnsRoomRate: IColumn[] = [
  { field: 'roomRateId', header: 'Id', type: 'text', sortable: false },
  // { field: 'fullName', header: 'Full Name', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'checkIn', header: 'Check In', type: 'date-editable', sortable: false, editable: true, props: { isRange: false, calendarMode: CALENDAR_MODE.DATE } },
  { field: 'checkOut', header: 'Check Out', type: 'date-editable', sortable: false, editable: true, props: { isRange: false, calendarMode: CALENDAR_MODE.DATE } },
  { field: 'adults', header: 'Adults', type: 'text', sortable: false, editable: true },
  { field: 'children', header: 'Children', type: 'text', sortable: false, editable: true },
  // { field: 'roomType', header: 'Room Type', type: 'select', objApi: confAgencyApi, sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'nights', header: 'Nights', type: 'text', sortable: false, editable: false },
  // { field: 'ratePlan', header: 'Rate Plan', type: 'select', objApi: confratePlanApi, sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'rateAdult', header: 'Rate Adult', type: 'text', sortable: false, editable: false },
  { field: 'rateChildren', header: 'Rate Children', type: 'text', sortable: false, editable: false },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'text', sortable: false, editable: true },
  { field: 'invoiceAmount', header: 'Rate Amount', type: 'text', sortable: false, editable: true },
]
const columnsAdjustment: IColumn[] = [
  { field: 'adjustmentId', header: 'Id', type: 'text', sortable: false },
  { field: 'amount', header: 'Amount', type: 'text', sortable: false },
  { field: 'roomRateId', header: 'Room Rate', type: 'text', sortable: false },
  { field: 'transaction', header: 'Category', type: 'select', objApi: transactionTypeApi, sortable: false },
  { field: 'date', header: 'Transaction Date', type: 'date', sortable: false },
  { field: 'employee', header: 'Employee', type: 'text', sortable: false },
  { field: 'description', header: 'Description', type: 'text', sortable: false },
]

const payloadOnChangePageRoomRate = ref<PageState>()
const payloadRoomRate = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'roomRateId',
  sortType: ENUM_SHORT_TYPE.DESC
})

const paginationRoomRate = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const payloadOnChangePageAdjustment = ref<PageState>()
const payloadAdjustment = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'adjustmentId',
  sortType: ENUM_SHORT_TYPE.DESC
})
const paginationAdjustment = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
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

// VARIABLES -----------------------------------------------------------------------------------------

const objApis = ref({
  invoiceType: { moduleApi: 'settings', uriApi: 'manage-invoice-type' },
  invoiceStatus: { moduleApi: 'settings', uriApi: 'manage-invoice-status' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  bankAccount: { moduleApi: 'settings', uriApi: 'manage-bank-account' },
  paymentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  attachmentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' },
})

//
const confirm = useConfirm()
const formReload = ref(0)

const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const item = ref<GenericObject>({
  invoiceId: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
})

const itemTemp = ref<GenericObject>({
  invoiceId: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
})

// -------------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
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
  messageToDelete: 'Do you want to save the change?'
})

const confBookingApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
})

// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

function handleDialogOpen() {
  switch (active.value) {
    case 0:
      bookingDialogOpen.value = true
      break

    case 1:
      roomRateDialogOpen.value = true
      break

    case 2:
      adjustmentDialogOpen.value = true
      break

    default:
      break
  }

  console.log(bookingDialogOpen)
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
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

function handleAttachmentHistoryDialogOpen() {
  attachmentHistoryDialogOpen.value = true
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
        ],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    agencyList.value = []
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
}

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
}

const objApisLoading = ref({
  invoiceType: false,
  invoiceStatus: false,
})

function mapFunction(data: any): any {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status
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
    const filteredList = await getDataList<any, any>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction)
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

  getInvoiceStatusListDefault(objApis.value.invoiceStatus.moduleApi, objApis.value.invoiceStatus.uriApi, objQueryToSearch, filter)
}

async function getInvoiceAmountById(id: string) {
  if (id) {
    idItem.value = id

    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.invoiceAmount = response.invoiceAmount
        invoiceAmount.value = response.invoiceAmount
      }
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice methods could not be loaded', life: 3000 })
      }
    }
  }
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.invoiceId = response.invoiceId
        item.value.dueDate = response.dueDate

        const invoiceNumber = `${response?.invoiceNumber?.split('-')[0]}-${response?.invoiceNumber?.split('-')[2]}`

        item.value.invoiceNumber = response?.invoiceNumber?.split('-')?.length === 3 ? invoiceNumber : response.invoiceNumber
        item.value.invoiceNumber = item.value.invoiceNumber.replace('OLD', 'CRE')

        item.value.invoiceDate = dayjs(response.invoiceDate).format('YYYY-MM-DD')
        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        invoiceAmount.value = response.invoiceAmount
        item.value.reSend = response.reSend
        item.value.reSendDate = response.reSendDate ? dayjs(response.reSendDate).toDate() : response.reSendDate
        item.value.hotel = response.hotel
        item.value.hotel.fullName = `${response.hotel.code} - ${response.hotel.name}`
        item.value.agency = response.agency
        item.value.hasAttachments = response.hasAttachments
        item.value.agency.fullName = `${response.agency.code} - ${response.agency.name}`
        item.value.invoiceType = response.invoiceType === InvoiceType.OLD_CREDIT ? ENUM_INVOICE_TYPE[0] : ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType))
        invoiceStatus.value = response.status
        item.value.status = response.status ? ENUM_INVOICE_STATUS.find((element => element.id === response?.status)) : ENUM_INVOICE_STATUS[0]
        await getInvoiceAgency(response.agency?.id)
        await getInvoiceHotel(response.hotel?.id)
      }

      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice methods could not be loaded', life: 3000 })
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

    payload.invoiceId = item.invoiceId
    payload.invoiceNumber = item.invoiceNumber
    payload.invoiceDate = dayjs(item.invoiceDate).startOf('day').toISOString()
    payload.isManual = item.isManual
    payload.invoiceAmount = 0.00
    payload.hotel = item.hotel.id
    payload.invoiceType = item?.invoiceType?.id
    payload.agency = item.agency.id

    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = {}

  if (item.hotelCreationDate) {
    const date = dayjs(item.hotelCreationDate).format('YYYY-MM-DD')
    payload.hotelCreationDate = new Date(`${date}T00:00:00`)
  }

  if (item.bookingDate) {
    const date = dayjs(item.bookingDate).format('YYYY-MM-DD')
    payload.bookingDate = new Date(`${date}T00:00:00`)
  }

  payload.id = route.params.id
  // payload.hotelCreationDate = item.hotelCreationDate ? dayjs(item.hotelCreationDate).toDate() : null
  // payload.bookingDate = item.bookingDate ? dayjs(item.bookingDate).toDate() : null
  payload.hotelBookingNumber = item.hotelBookingNumber
  payload.fullName = `${item.firstName} ${item.lastName}`
  payload.firstName = item.firstName
  payload.lastName = item.lastName
  payload.roomNumber = item.roomNumber
  payload.couponNumber = item.couponNumber
  payload.hotelInvoiceNumber = item.hotelInvoiceNumber
  payload.folioNumber = item.folioNumber
  payload.description = item.description
  payload.contract = item.contract
  payload.ratePlan = item.ratePlan ? item.ratePlan.id : null
  payload.nightType = item.nightType ? item.nightType.id : null
  payload.roomType = item.roomType ? item.roomType.id : null
  payload.roomCategory = item.roomCategory ? item.roomCategory.id : null

  await GenericService.update(confBookingApi.moduleApi, confBookingApi.uriApi, idItem.value || '', payload)
  navigateTo('/invoice')
}

async function updateItemByRef(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = {}

  if (item.hotelCreationDate) {
    const date = dayjs(item.hotelCreationDate).format('YYYY-MM-DD')
    payload.hotelCreationDate = new Date(`${date}T00:00:00`)
  }

  if (item.bookingDate) {
    const date = dayjs(item.bookingDate).format('YYYY-MM-DD')
    payload.bookingDate = new Date(`${date}T00:00:00`)
  }

  payload.id = route.params.id
  // payload.hotelCreationDate = item.hotelCreationDate ? dayjs(item.hotelCreationDate).format('YYYY-MM-DD') : null
  // payload.bookingDate = item.bookingDate ? dayjs(item.bookingDate).format('YYYY-MM-DD') : null
  payload.hotelBookingNumber = item.hotelBookingNumber
  payload.fullName = `${item.firstName} ${item.lastName}`
  payload.firstName = item.firstName
  payload.lastName = item.lastName
  payload.roomNumber = item.roomNumber
  payload.couponNumber = item.couponNumber
  payload.hotelInvoiceNumber = item.hotelInvoiceNumber
  payload.folioNumber = item.folioNumber
  payload.description = item.description
  payload.contract = item.contract
  payload.ratePlan = item.ratePlan ? item.ratePlan.id : null
  payload.nightType = item.nightType ? item.nightType.id : null
  payload.roomType = item.roomType ? item.roomType.id : null
  payload.roomCategory = item.roomCategory ? item.roomCategory.id : null

  await GenericService.update(confBookingApi.moduleApi, confBookingApi.uriApi, idItem.value || '', payload)
  onSaveButtonByRef.value = false
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
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
      if (onSaveButtonByRef.value === true) {
        await updateItemByRef(item)
      }
      else {
        await updateItem(item)
      }
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false

      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    formReload.value++

    // clearForm()
  }
}
const goToList = async () => await navigateTo('/invoice')

function requireConfirmationToSave(item: any) {
  if (onSaveButtonByRef.value === false) {
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
  else {
    updateItemByRef(item)
    // saveItem(item)
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

function toggleForceUpdate() {
  forceUpdate.value = !forceUpdate.value
}

function update() {
  forceUpdate.value = true

  setTimeout(() => {
    forceUpdate.value = false
  }, 100)
}

function openAdjustmentDialog(roomRate?: any) {
  active.value = 2

  if (roomRate?.id) {
    selectedRoomRate.value = roomRate?.id
  }

  adjustmentDialogOpen.value = true
}

async function getInvoiceHotel(id: string) {
  try {
    const hotel = await GenericService.getById(confhotelListApi.moduleApi, confhotelListApi.uriApi, id)

    if (hotel) {
      invoiceHotel.value = { ...hotel }

      requiresFlatRate.value = hotel?.requiresFlatRate
    }
  }
  catch (err) {

  }
}
async function getInvoiceAgency(id: string) {
  try {
    const agency = await GenericService.getById(confagencyListApi.moduleApi, confagencyListApi.uriApi, id)
    if (agency) {
      invoiceAgency.value = { ...agency }
    }
  }
  catch (err) {

  }
}

function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

const invoiceStatusList = ref<any[]>([])

async function getInvoiceStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  const additionalFilter: FilterCriteria[] = [
    {
      key: 'name',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'Sent'
    }
  ]

  const filteredList = await getDataList<any, any>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction)

  if (filteredList.length > 0) {
    invoiceStatusList.value = [filteredList[0]]
  }
  else {
    invoiceStatusList.value = []
  }
}

function clearFormRoomRate() {
  // item2.value = { ...itemTemp2.value }
  // idItem.value = ''
  // formReload.value++
  console.log('lImpiar Room Rate')
}

function clearFormAdjustment() {
  // item2.value = { ...itemTemp2.value }
  // idItem.value = ''
  // formReload.value++
  console.log('lImpiar Adjustment')
}

async function onCellEditRoomRate(event: any) {
  const { data, newValue, field, newData } = event
  const dataTemp = data[field].replace(/,/g, '')

  if (data[field] === newValue) { return }

  if (field === 'hotelAmount') {
    if (+newValue <= 0 && requiresFlatRateCheck.value) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Hotel Amount must be greater than 0', life: 3000 })
      return
    }
  }

  if (field === 'adults') {
    if (+newValue <= 0 && newData.children === 0) {
      // Mensaje de error: Almenos uno de los dos debe ser mayo que 0
      toast.add({ severity: 'error', summary: 'Error', detail: 'At least one of the fields Adults or Children must be greater than 0.', life: 3000 })
      return
    }
  }

  if (field === 'children') {
    if (+newValue <= 0 && newData.adults === 0) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'At least one of the fields Adults or Children must be greater than 0.', life: 3000 })
      return
    }
  }

  // const rateChildren = newData?.invoiceAmount ? newData.invoiceAmount / (newData.nights * newData.children) : 0

  const payload: { [key: string]: any } = {

    roomRateId: newData.roomRateId,
    roomNumber: newData.roomNumber,
    checkIn: dayjs(newData.checkIn).toISOString(),
    checkOut: dayjs(newData.checkOut).toISOString(),
    adults: newData.adults,
    children: newData.children,
    invoiceAmount: newData.invoiceAmount ? newData.invoiceAmount.replace(/,/g, '') : 0,
    hotelAmount: newData.hotelAmount ? newData.hotelAmount.replace(/,/g, '') : 0,
    rateAdult: newData.rateAdult ? newData.rateAdult.replace(/,/g, '') : 0,
    remark: newData.remark,
    nights: newData.nights,
    id: newData.id
  }
  try {
    data[field] = newValue
    item.value[field] = newValue
    await updateRoomRate(payload)
    onSaveButtonByRef.value = true
    if (saveButton.value) {
      saveButton.value.$el.click()
    }
    reloadBookingItem(idItem.value)
  }
  catch (error: any) {
    data[field] = dataTemp
    toast.add({ severity: 'error', summary: 'Error', detail: error?.response?._data?.data?.error?.errorMessage, life: 3000 })
  }
}

async function updateRoomRate(itemRoomRate: { [key: string]: any }) {
  const payload: { [key: string]: any } = { ...itemRoomRate }
  await GenericService.update(confApi.roomRate.moduleApi, confApi.roomRate.uriApi, itemRoomRate.id || '', payload)
}

function onRowRightClick(event: any) {
  // Si el estado de la factura no es procesada, no se puede editar
  // Pendiente

  // !props.isCreationDialog && props.invoiceObj?.status?.id !== InvoiceStatus.PROCECSED
  // if (!props.isCreationDialog && props.invoiceObj?.status?.id !== InvoiceStatus.PROCECSED) {
  //   return
  // }
  selectedRoomRate.value = event.data
  roomRateContextMenu.value.show(event.originalEvent)
}

async function parseDataTableFilterRoomRate(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsRoomRate)
  payloadRoomRate.value.filter = [...parseFilter || []]
  getRoomRateList()
}

function onSortFieldRoomRate(event: any) {
  if (event) {
    // if (props?.isCreationDialog) {
    //   return props.sortRoomRate(event)
    // }

    if (event.sortField === 'fullName') {
      payloadRoomRate.value.sortBy = 'booking.firstName'
    }
    else {
      payloadRoomRate.value.sortBy = event.sortField
    }

    payloadRoomRate.value.sortType = event.sortOrder
    getRoomRateList()
  }
}

function onSortFieldAdjustment(event: any) {
  if (event) {
    payloadAdjustment.value.sortBy = event.sortField
    payloadAdjustment.value.sortType = event.sortOrder
    getAdjustmentList()
  }
}

async function resetListItemsRoomRate() {
  payloadRoomRate.value.page = 0
  getRoomRateList()
}
async function getRoomRateList() {
  try {
    idItemToLoadFirstTime.value = ''
    optionsRoomRate.value.loading = true
    roomRateList.value = []
    payloadRoomRate.value.filter = [...payloadRoomRate.value.filter, {
      key: 'booking.id',
      operator: 'EQUALS',
      value: selectedInvoice.value,
      logicalOperation: 'OR'
    }]
    const response = await GenericService.search(optionsRoomRate.value.moduleApi, optionsRoomRate.value.uriApi, payloadRoomRate.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationRoomRate.value.page = page
    paginationRoomRate.value.limit = size
    paginationRoomRate.value.totalElements = totalElements
    paginationRoomRate.value.totalPages = totalPages

    let countRR = 0
    totalInvoiceAmount.value = 0
    totalHotelAmount.value = 0
    for (const iterator of dataList) {
      countRR++
      // Rate Adult= RateAmount/(Ctdad noches*Ctdad Adults) y Rate Children= RateAmount/(Ctdad noches*Ctdad Children)
      const rateAdult = iterator.invoiceAmount ? iterator.invoiceAmount / (iterator.nights * iterator.adults) : 0
      const rateChildren = iterator.invoiceAmount ? iterator.invoiceAmount / (iterator.nights * iterator.children) : 0

      roomRateList.value = [...roomRateList.value, {
        ...iterator,
        checkIn: iterator?.checkIn ? new Date(`${dayjs(iterator?.checkIn).format('YYYY-MM-DD')}T00:00:00`) : null,
        checkOut: iterator?.checkOut ? new Date(`${dayjs(iterator?.checkOut).format('YYYY-MM-DD')}T00:00:00`) : null,
        invoiceAmount: formatNumber(iterator?.invoiceAmount) || 0,
        hotelAmount: formatNumber(iterator?.hotelAmount) || 0,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        loadingEdit: false,
        loadingDelete: false,
        fullName: `${iterator.booking.firstName ? iterator.booking.firstName : ''} ${iterator.booking.lastName ? iterator.booking.lastName : ''}`,
        bookingId: iterator.booking.bookingId,
        roomType: { ...iterator.booking.roomType, name: `${iterator?.booking?.roomType?.code || ''}-${iterator?.booking?.roomType?.name || ''}` },
        nightType: { ...iterator.booking.nightType, name: `${iterator?.booking?.nightType?.code || ''}-${iterator?.booking?.nightType?.name || ''}` },
        ratePlan: { ...iterator.booking.ratePlan, name: `${iterator?.booking?.ratePlan?.code || ''}-${iterator?.booking?.ratePlan?.name || ''}` },
        agency: { ...iterator?.booking?.invoice?.agency, name: `${iterator?.booking?.invoice?.agency?.code || ''}-${iterator?.booking?.invoice?.agency?.name || ''}` },
        rateAdult: iterator.adults > 0 ? rateAdult.toFixed(2) : 0,
        rateChildren: iterator.children > 0 ? rateChildren.toFixed(2) : 0,
      }]

      if (typeof +iterator.invoiceAmount === 'number') {
        totalInvoiceAmount.value += Number(iterator.invoiceAmount)
      }

      if (typeof +iterator.hotelAmount === 'number') {
        totalHotelAmount.value += Number(iterator.hotelAmount)
      }
    }
    if (roomRateList.value.length > 0) {
      idItemToLoadFirstTime.value = roomRateList.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    optionsRoomRate.value.loading = false
  }
}

async function getAdjustmentList() {
  try {
    idItemToLoadFirstTime.value = ''
    optionsAdjustment.value.loading = true
    adjustmentList.value = []
    totalAmountAdjustment.value = 0

    payloadAdjustment.value.filter = [...payloadAdjustment.value.filter, {
      key: 'roomRate.booking.id',
      operator: 'EQUALS',
      value: selectedInvoice.value,
      logicalOperation: 'OR'
    }]
    const response = await GenericService.search(optionsAdjustment.value.moduleApi, optionsAdjustment.value.uriApi, payloadAdjustment.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationAdjustment.value.page = page
    paginationAdjustment.value.limit = size
    paginationAdjustment.value.totalElements = totalElements
    paginationAdjustment.value.totalPages = totalPages

    for (const iterator of dataList) {
      let transaction = { ...iterator?.transaction, name: `${iterator?.transaction?.code || ''}-${iterator?.transaction?.name || ''}` }

      if (iterator?.invoice?.invoiceType === InvoiceType.INCOME) {
        transaction = { ...iterator?.paymentTransactionType, name: `${iterator?.paymentTransactionType?.code || ''}-${iterator?.paymentTransactionType?.name || ''}` }
      }

      adjustmentList.value = [...adjustmentList.value, {
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        roomRateId: iterator?.roomRate?.roomRateId,
        date: iterator?.date
      }]

      if (typeof +iterator?.amount === 'number') {
        totalAmountAdjustment.value += Number(iterator?.amount)
      }
    }
    if (adjustmentList.value.length > 0) {
      idItemToLoadFirstTime.value = adjustmentList.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    optionsAdjustment.value.loading = false
  }
}

async function resetListItemsAdjustment() {
  payloadAdjustment.value.page = 0
  getAdjustmentList()
}

async function parseDataTableFilterAdjustment(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsAdjustment)
  payloadAdjustment.value.filter = [...parseFilter || []]
  getAdjustmentList()
}

async function openEditDialog(item: any) {

  // if (route.query.type === InvoiceType.CREDIT || props.invoiceObj?.invoiceType?.id === InvoiceType.CREDIT) {
  //   return null
  // }

  // props.openDialog()
  // if (item?.id) {
  //   idItem.value = item?.id
  //   idItemToLoadFirstTime.value = item?.id
  //   await GetItemById(item?.id)
  // }

  // if (typeof item === 'string') {
  //   idItem.value = item
  //   idItemToLoadFirstTime.value = item
  //   await GetItemById(item)
  // }
}

async function getratePlanList(query = '') {
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
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confratePlanApi.moduleApi, confratePlanApi.uriApi, payload)
    const { data: dataList } = response
    ratePlanList.value = []
    for (const iterator of dataList) {
      ratePlanList.value = [...ratePlanList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getRoomCategoryList(query = '') {
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
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confroomCategoryApi.moduleApi, confroomCategoryApi.uriApi, payload)
    const { data: dataList } = response
    roomCategoryList.value = []
    for (const iterator of dataList) {
      roomCategoryList.value = [
        ...roomCategoryList.value,
        {
          id: iterator.id,
          name: iterator.name,
          code: iterator.code,
          status: iterator.status
        }
      ]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}
async function getRoomTypeList(query = '') {
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
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confroomTypeApi.moduleApi, confroomTypeApi.uriApi, payload)
    const { data: dataList } = response
    roomTypeList.value = []
    for (const iterator of dataList) {
      roomTypeList.value = [...roomTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}
async function getNightTypeList(query = '') {
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
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confnightTypeApi.moduleApi, confnightTypeApi.uriApi, payload)
    const { data: dataList } = response
    nightTypeList.value = []
    for (const iterator of dataList) {
      nightTypeList.value = [...nightTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getBookingItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.booking.moduleApi, confApi.booking.uriApi, id)
      if (response) {
        if (response.hotelCreationDate) {
          const date = dayjs(response.hotelCreationDate).format('YYYY-MM-DD')
          item2.value.hotelCreationDate = new Date(`${date}T00:00:00`)
        }

        if (response.bookingDate) {
          const date = dayjs(response.bookingDate).format('YYYY-MM-DD')
          item2.value.bookingDate = new Date(`${date}T00:00:00`)
        }

        idItem.value = response?.id
        item2.value.id = response.id
        item2.value.bookingId = response.bookingId
        item2.value.contract = response.contract
        item2.value.dueAmount = response.dueAmount
        item2.value.checkIn = new Date(response.checkIn)
        item2.value.checkOut = new Date(response.checkOut)
        item2.value.hotelBookingNumber = response.hotelBookingNumber
        item2.value.fullName = response.fullName
        item2.value.firstName = response.firstName
        item2.value.lastName = response.lastName

        item2.value.invoiceAmount = response.invoiceAmount ? String(response?.invoiceAmount) : '0'
        item2.value.roomNumber = response.roomNumber
        item2.value.couponNumber = response.couponNumber
        item2.value.adults = response.adults
        item2.value.children = response.children
        item2.value.rateAdult = response.rateAdult
        item2.value.rateChild = response.rateChild
        item2.value.hotelInvoiceNumber = response.hotelInvoiceNumber
        item2.value.folioNumber = response.folioNumber
        item2.value.hotelAmount = String(response.hotelAmount)
        item2.value.description = response.description
        item2.value.invoice = response.invoice
        item2.value.invoiceOriginalAmount = response.invoice.invoiceAmount
        item2.value.ratePlan = response.ratePlan?.name === '-' ? null : response.ratePlan
        item2.value.nightType = response.nightType
        item2.value.roomType = response.roomType?.name === '-' ? null : response.roomType
        item2.value.roomCategory = response.roomCategory
      }

      // Validacion para el campo hotelInvoiceNumber
      if (response?.invoice?.hotel?.virtual) {
        const decimalSchema = z.object(
          {
            hotelInvoiceNumber:
            z.string()
              .min(1, 'The Hotel Invoice No. field is required')
              .refine((val: string) => {
                if ((Number(val) < 0)) {
                  return false
                }
                return true
              }, { message: 'The Hotel Invoice No. field must not be negative' }).nullable()
          },
        )
        const objField = fieldsV2.find(field => field.field === 'hotelInvoiceNumber')

        updateFieldProperty(fieldsV2, 'hotelInvoiceNumber', 'validation', decimalSchema.shape.hotelInvoiceNumber)
        updateFieldProperty(fieldsV2, 'hotelInvoiceNumber', 'class', `${objField?.class} required`)
        updateFieldProperty(fieldsV2, 'hotelInvoiceNumber', 'disabled', false)
      }
      else {
        const decimalSchema = z.object(
          {
            hotelInvoiceNumber: z
              .string()
              .refine((val: string) => {
                if ((Number(val) < 0)) {
                  return false
                }
                return true
              }, { message: 'The Hotel Invoice No. field must not be negative' }).nullable(),
          },
        )
        const objField = fieldsV2.find(field => field.field === 'hotelInvoiceNumber')
        updateFieldProperty(fieldsV2, 'hotelInvoiceNumber', 'validation', decimalSchema.shape.hotelInvoiceNumber)
        updateFieldProperty(fieldsV2, 'hotelInvoiceNumber', 'class', `${objField?.class}`)
        updateFieldProperty(fieldsV2, 'hotelInvoiceNumber', 'disabled', true)
      }

      requiresFlatRateCheck.value = response?.invoice?.hotel?.requiresFlatRate
      if (response?.invoice?.hotel?.requiresFlatRate) {
        const decimalSchema = z.object(
          {
            hotelAmount:
            z.string()
              .min(1, 'The Hotel Amount field is required')
              .refine((val: string) => {
                if ((Number(val) < 0)) {
                  return false
                }
                return true
              }, { message: 'The Hotel Amount field must not be negative' }).nullable()
          },
        )
        const objField = fieldsV2.find(field => field.field === 'hotelAmount')

        updateFieldProperty(fieldsV2, 'hotelAmount', 'validation', decimalSchema.shape.hotelAmount)
        updateFieldProperty(fieldsV2, 'hotelAmount', 'class', `${objField?.class} required`)
      }
      else {
        const decimalSchema = z.object(
          {
            hotelAmount: z
              .string()
              .refine((val: string) => {
                if ((Number(val) < 0)) {
                  return false
                }
                return true
              }, { message: 'The Hotel Invoice No. field must not be negative' }).nullable(),
          },
        )
        const objField = fieldsV2.find(field => field.field === 'hotelAmount')
        updateFieldProperty(fieldsV2, 'hotelAmount', 'validation', decimalSchema.shape.hotelAmount)
        updateFieldProperty(fieldsV2, 'hotelAmount', 'class', `${objField?.class}`)
      }
      if (response?.invoice?.agency?.client?.isNightType) {
        const objField = fieldsV2.find(field => field.field === 'nightType')
        const validations = z.object({
          id: z.string(),
          name: z.string(),
        }).nullable()
          .refine(value => value && value.id && value.name, { message: `The nightType field is required` })

        // validateEntityStatus('night type') // Esto es lo que va cuando me pongan el status en el ojbeto, solo llega id, code y name
        // updateFieldProperty(fieldsV2, 'nightType', 'validation', )
        updateFieldProperty(fieldsV2, 'nightType', 'validation', validations)
        updateFieldProperty(fieldsV2, 'nightType', 'class', `${objField?.class} required`)
      }
      else {
        const objField = fieldsV2.find(field => field.field === 'nightType')
        updateFieldProperty(fieldsV2, 'nightType', 'validation', z.string().nullable())
        updateFieldProperty(fieldsV2, 'nightType', 'class', `${objField?.class}`)
      }

      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function reloadBookingItem(id: string) {
  await getBookingItemById(id)
  getRoomRateList()
  getAdjustmentList()
}

// watch(() => idItemToLoadFirstTime.value, async (newValue) => {
//   if (!newValue) {
//     clearForm()
//   }
//   else {
//     await getItemById(newValue)
//   }
// })

onMounted(async () => {
  // filterToSearch.value.criterial = ENUM_FILTER[0]
  // //@ts-ignore
  // await getItemById(route.params.id.toString())

  // await loadDefaultsValues()

  if (route.params && 'id' in route.params && route.params.id) {
    await getBookingItemById(route.params.id as string)
    getRoomRateList()
    getAdjustmentList()
  }

  // saveButton.value.$el.click()
})
</script>

<template>
  <div class="justify-content-center align-center ">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      Edit Booking
    </div>
    <div class="p-4">
      <EditFormV2
        v-if="true"
        :key="formReload"
        :fields="fieldsV2"
        :item="item2"
        :show-actions="true"
        :loading-save="loadingSaveAll"
        container-class="grid pt-5"
        @cancel="clearForm"
        @delete="requireConfirmationToDelete($event)"
        @submit="saveItem($event)"
      >
        <template #field-hotelCreationDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.hotelCreationDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('hotelCreationDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-invoiceAmount="{ onUpdate, item: data }">
          <InputText
            v-if="!loadingSaveAll"
            v-model="data.invoiceAmount"
            show-clear
            :disabled="!!item2?.id && route.query.type !== InvoiceType.CREDIT"
            @update:model-value="($event) => {
              let value: any = $event
              if (route.query.type === InvoiceType.OLD_CREDIT || route.query.type === InvoiceType.CREDIT){
                value = toNegative(value)
              }
              else {
                value = toPositive(value)
              }
              onUpdate('invoiceAmount', String(value))
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-hotelAmount="{ onUpdate, item: data, fields, field }">
          <InputText
            v-if="!loadingSaveAll"
            v-model="data.hotelAmount"
            show-clear
            :disabled="fields.find((f) => f.field === field)?.disabled"
            @update:model-value="onUpdate('hotelAmount', $event)"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-checkIn="{ item: data, onUpdate, fields, field }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.checkIn"
            date-format="yy-mm-dd"
            :disabled="fields.find((f) => f.field === field)?.disabled"
            :max-date="data.checkOut ? new Date(data.checkOut) : undefined"
            @update:model-value="($event) => {
              onUpdate('checkIn', $event)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-checkOut="{ item: data, onUpdate, fields, field }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.checkOut"
            date-format="yy-mm-dd"
            :disabled="fields.find((f) => f.field === field)?.disabled"
            :min-date="data?.checkIn ? new Date(data?.checkIn) : new Date()"
            @update:model-value="($event) => {
              onUpdate('checkOut', $event)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-ratePlan="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.ratePlan"
            :suggestions="ratePlanList"
            @change="($event) => {
              onUpdate('ratePlan', $event)
            }"
            @load="($event) => getratePlanList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-roomCategory="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.roomCategory" :suggestions="roomCategoryList" @change="($event) => {
              onUpdate('roomCategory', $event)
            }" @load="($event) => getRoomCategoryList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-bookingDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.bookingDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('bookingDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-roomType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.roomType" :suggestions="roomTypeList" @change="($event) => {
              onUpdate('roomType', $event)
            }" @load="($event) => getRoomTypeList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>

        <!-- <template #header-nightType="{ field }">
          <strong>
            {{ typeof field.header === 'function' ? field.header() : field.header }}
          </strong>
          <span v-if="isNightTypeRequired" class="p-error">*</span>
        </template> -->

        <template #field-nightType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.nightType"
            :suggestions="nightTypeList"
            @change="($event) => {
              onUpdate('nightType', $event)
            }"
            @load="($event) => getNightTypeList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #form-footer="props">
          <div class="grid w-full p-0 m-0">
            <div class="col-12 mb-5 justify-content-center align-center">
              <div style="width: 100%; height: 100%;">
                <TabView id="tabView" v-model:activeIndex="activeTab" class="no-global-style">
                  <TabPanel>
                    <template #header>
                      <div class="flex align-items-center gap-2 p-2" :style="`${activeTab === 0 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
                        <i class="pi pi-receipt" style="font-size: 1.5rem" />
                        <span class="font-bold">
                          Room Rates
                          <!-- <i
                            v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                            style="font-size: 1.2rem"
                          /> -->
                        </span>
                      </div>
                    </template>
                    <DynamicTable
                      :data="roomRateList"
                      :columns="columnsRoomRate"
                      :options="optionsRoomRate"
                      :pagination="paginationRoomRate"
                      @on-confirm-create="clearFormRoomRate"
                      @on-change-pagination="payloadOnChangePageRoomRate = $event"
                      @on-row-right-click="onRowRightClick"
                      @on-change-filter="parseDataTableFilterRoomRate"
                      @on-list-item="resetListItemsRoomRate"
                      @on-sort-field="onSortFieldRoomRate"
                      @on-table-cell-edit-complete="onCellEditRoomRate($event)"
                    >
                      <template #datatable-footer>
                        <ColumnGroup type="footer" class="flex align-items-center">
                          <Row>
                            <Column
                              footer="Totals:" :colspan="8"
                              footer-style="text-align:right; font-weight: 700"
                            />
                            <Column :footer="formatNumber(Math.round((totalHotelAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: 700" />
                            <Column :footer="formatNumber(Math.round((totalInvoiceAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: 700" />
                          </Row>
                        </ColumnGroup>
                      </template>
                    </DynamicTable>
                  </TabPanel>
                  <TabPanel>
                    <template #header>
                      <div class="flex align-items-center gap-2 p-2" :style="`${activeTab === 1 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
                        <i class="pi pi-sliders-v" style="font-size: 1.5rem" />
                        <span class="font-bold white-space-nowrap">
                          Adjustments
                          <!-- <i
                            v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                            style="font-size: 1.2rem"
                          /> -->
                        </span>
                      </div>
                    </template>
                    <DynamicTable
                      :data="adjustmentList"
                      :columns="columnsAdjustment"
                      :options="optionsAdjustment"
                      :pagination="paginationAdjustment"
                      @on-confirm-create="clearFormAdjustment"
                      @on-change-pagination="payloadOnChangePageAdjustment = $event"
                      @on-row-right-click="onRowRightClick"
                      @on-change-filter="parseDataTableFilterAdjustment"
                      @on-list-item="resetListItemsAdjustment"
                      @on-sort-field="onSortFieldAdjustment"
                      @on-row-double-click="($event) => {}"
                    >
                      <template #datatable-footer>
                        <ColumnGroup type="footer" class="flex align-items-center">
                          <Row>
                            <Column footer="Totals:" :colspan="1" footer-style="text-align:right; font-weight: 700" />
                            <Column :footer="Number.parseFloat(totalAmountAdjustment.toFixed(2))" footer-style="font-weight: 700" />

                            <Column :colspan="6" />
                          </Row>
                        </ColumnGroup>
                      </template>
                    </DynamicTable>
                  </TabPanel>
                </TabView>

                <!-- <InvoiceTabView
                  :requires-flat-rate="requiresFlatRate"
                  :get-invoice-hotel="getInvoiceHotel"
                  :get-invoice-agency="getInvoiceAgency"
                  :invoice-obj-amount="invoiceAmount"
                  :is-dialog-open="bookingDialogOpen"
                  :close-dialog="() => { bookingDialogOpen = false }"
                  :open-dialog="handleDialogOpen"
                  :selected-booking="selectedBooking"
                  :force-update="forceUpdate"
                  :toggle-force-update="update"
                  :invoice-obj="item2"
                  :refetch-invoice="refetchInvoice"
                  :is-creation-dialog="false"
                  :selected-invoice="selectedInvoice as any"
                  :active="active"
                  :set-active="($event) => { active = $event }"
                  :showTotals="true"
                  :invoiceType="route.query?.type?.toString()"
                /> -->
              </div>
            </div>
            <div class="col-12 flex justify-content-end">
              <Button
                ref="saveButton"
                v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
                @click="props.item.submitForm($event)"
              />
              <Button
                v-tooltip.top="'Cancel'"
                severity="secondary"
                class="w-3rem mx-1"
                icon="pi pi-times"
                @click="navigateTo('/invoice')"
              />
            </div>
          </div>
        </template>
      </EditFormV2>
    </div>

    <div v-if="attachmentDialogOpen">
      <AttachmentDialog
        :close-dialog="() => { attachmentDialogOpen = false; getItemById(idItem) }"
        :is-creation-dialog="false" header="Manage Invoice Attachment" :open-dialog="attachmentDialogOpen"
        :selected-invoice="selectedInvoice" :selected-invoice-obj="item"
      />
    </div>
  </div>
  <div v-if="attachmentHistoryDialogOpen">
    <AttachmentHistoryDialog
      selected-attachment="" :close-dialog="() => { attachmentHistoryDialogOpen = false }"
      header="Attachment Status History" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="selectedInvoice"
      :selected-invoice-obj="item" :is-creation-dialog="false"
    />
  </div>
  <div v-if="exportAttachmentsDialogOpen">
    <PrintInvoiceDialog
      :close-dialog="() => { exportAttachmentsDialogOpen = false }"
      :open-dialog="exportAttachmentsDialogOpen"
      :invoice="item"
    />
  </div>

  <!-- <ContextMenu ref="roomRateContextMenu" :model="menuModel" /> -->
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
