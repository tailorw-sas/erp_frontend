<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import { v4 } from 'uuid'
import dayjs from 'dayjs'
import getUrlByImage from '~/composables/files'
import { ModulesService } from '~/services/modules-services'
import { GenericService } from '~/services/generic-services'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'

const props = defineProps({
  isDialogOpen: {
    type: Boolean,
    required: true
  },
  forceUpdate: {
    type: Boolean,
    required: true
  },
  toggleForceUpdate: {
    type: Function as any,
    required: true,

  },
  selectedInvoice: {
    type: String,
    required: false
  },

  closeDialog: {
    type: Function as any,
    required: true
  },
  openDialog: {
    type: Function as any,
    required: true
  },
  openRoomRateDialog: {
    type: Function as any,
    required: true
  },
  isCreationDialog: {
    type: Boolean,
    required: true
  },
  listItems: {
    type: Array,
    required: false
  },
  addItem: {
    type: Function as any,
    required: false
  },
  updateItem: {
    type: Function as any,
    required: false
  },
  showTotals: {
    type: Boolean,
    required: false,
    default: false
  },
  isTabView: {
    type: Boolean,
    default: false
  },
  invoiceObj: {
    type: Object as any,
    required: false
  },

  invoiceAgency: {
    type: Object,
    required: true
  },
  invoiceHotel: {
    type: Object,
    required: true
  },
})

const toast = useToast()
const loadingSaveAll = ref(false)
const confirm = useConfirm()
const ListItems = ref<any[]>([])
const formReload = ref(0)

const idItem = ref('')
const idItemToLoadFirstTime = ref('')

const loadingDelete = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const nightTypeRequired = ref(props.invoiceHotel?.isNightType)

const couponNumberValidation = ref(props.invoiceAgency?.bookingCouponFormat)

const roomCategoryList = ref<any[]>([])
const roomTypeList = ref<any[]>([])
const nightTypeList = ref<any[]>([])
const ratePlanList = ref<any[]>([])

const totalInvoiceAmount = ref<number>(0)
const totalHotelAmount = ref<number>(0)

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
const confAgencyApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
  keyValue: 'name'
})
const confnightTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-night-type',
  keyValue: 'name'
})
const confratePlanApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-rate-plan',
  keyValue: 'name'
})

const Fields = ref<Array<Container>>([
  {
    childs: [
      {
        field: 'booking_id',
        header: 'Id',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        disabled: true

      },
      {
        field: 'hotelCreationDate',
        header: 'Hotel Creation Date',
        dataType: 'date',
        class: 'field col-12 md: required ',
        headerClass: 'mb-1',

        validation: z.date({
          required_error: 'The Hotel Creation Date field is required',
          invalid_type_error: 'The Hotel Creation Date field is required',
        }).max(dayjs().endOf('day').toDate(), 'The Hotel Creation Date field cannot be greater than current date')

      },

      {
        field: 'checkIn',
        header: 'Check In',
        dataType: 'date',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.date({
          required_error: 'The Check In field is required',
          invalid_type_error: 'The Check In field is required',
        })

      },

      {
        field: 'fullName',
        header: 'Full Name',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.string().min(1, 'The First Name field is required')

      },

      {
        field: 'invoiceAmount',
        header: 'Invoice Amount',
        dataType: 'number',
        class: 'field col-12 required',
        headerClass: 'mb-1',

        validation: z.string().min(0, 'The Invoice Amount field is required').refine((value: any) => !isNaN(value) && +value > 0, { message: 'The Invoice Amount field must not be negative' })

      },
      {
        field: 'roomType',
        header: 'Room Type',
        dataType: 'select',
        class: 'field col-12 ',
        headerClass: 'mb-1',
        validation: validateEntityStatusForNotRequiredField('Room Type')

      },
      {
        field: 'adults',
        header: 'Adults',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',
        validation: z.number().min(1, 'The Adults field must be greater than 0').nullable()

      },
      {
        field: 'rateAdult',
        header: 'Rate Adult',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',
        validation: z.number().min(0, 'The Rate Adult field must be greater or equal than 0')

      },
      {
        field: 'hotelInvoiceNumber',
        header: 'Hotel Invoice No',
        dataType: 'text',
        class: 'field col-12 ',
        headerClass: 'mb-1',
        validation: z.string().refine((val: string) => {
          if ((Number(val) < 0)) {
            return false
          }

          return true
        }, { message: 'The Hotel Invoice No. field must not be negative' }).nullable()

      },
      {
        field: 'couponNumber',
        header: 'Coupon Number',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        ...(couponNumberValidation.value ? { validation: z.string().min(1, 'The coupon number field is required').regex(new RegExp(couponNumberValidation.value), 'The coupon number field is invalid') } : { validation: z.string().min(1, 'The coupon number field is required') })

      },
      {
        field: 'ratePlan',
        header: 'Rate Plan',
        dataType: 'select',
        class: 'field col-12 ',
        headerClass: 'mb-1',
        validation: validateEntityStatusForNotRequiredField('Rate Plan')

      },

    ],
    containerClass: ''
  },
  {
    childs: [
      {
        field: 'bookingDate',
        header: 'Booking Date',
        dataType: 'date',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.date({
          required_error: 'The Booking Date field is required',
          invalid_type_error: 'The Booking Date field is required',
        })

      },
      {
        field: 'hotelBookingNumber',
        header: 'Hotel Booking No.',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.string().min(1, 'The Hotel Booking No. field is required').regex(/(I*G*) \d+\s\d+/, 'The Hotel Booking No. field has an invalid format')

      },
      {
        field: 'checkOut',
        header: 'Check Out',
        dataType: 'date',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.date({
          required_error: 'The Check Out field is required',
          invalid_type_error: 'The Check Out field is required',
        })

      },

      {
        field: 'roomCategory',
        header: 'Room Category',
        dataType: 'select',
        class: 'field col-12',
        headerClass: 'mb-1',
        validation: validateEntityStatusForNotRequiredField('Room Category')

      },
      {
        field: 'roomNumber',
        header: 'Room Number',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.string().min(1, 'The Room Number field is required')

      },
      {
        field: 'children',
        header: 'Children',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',
        validation: z.number().nonnegative('The Children field must not be negative').nullable()

      },
      {
        field: 'rateChild',
        header: 'Rate Child',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',
        validation: z.number().nonnegative('The Rate Child field must be greater than 0').nullable()

      },

      {
        field: 'folioNumber',
        header: 'Folio Number',
        dataType: 'text',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      },
      {
        field: 'hotelAmount',
        header: 'Hotel Amount',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.string().min(1, 'The Hotel Amount field is required').refine(val => +val <= 0, 'The Hotel Amount field must be greater than 0').nullable()

      },

      {
        field: 'nightType',
        header: 'Night Type',
        dataType: 'select',
        class: `field col-12 ${nightTypeRequired.value ? 'required' : ''}`,
        headerClass: 'mb-1',

      },

    ],
    containerClass: ''
  },
  {
    childs: [
      {
        field: 'description',
        header: 'Description',
        dataType: 'textarea',
        class: 'field  ',
        headerClass: 'mb-1',

      },
    ],
    containerClass: 'mx-7 w-full'
  }

])

const fieldsV2: Array<FieldDefinitionType> = [
  // Booking ID
  {
    field: 'booking_id',
    header: 'Id',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    disabled: true
  },

  // Booking Date
  {
    field: 'bookingDate',
    header: 'Booking Date',
    dataType: 'date',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The Booking Date field is required',
      invalid_type_error: 'The Booking Date field is required',
    })
  },
  {
    field: 'fullName',
    header: 'Full Name',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The First Name field is required')
  },

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
  // Check In
  {
    field: 'checkIn',
    header: 'Check In',
    dataType: 'date',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The Check In field is required',
      invalid_type_error: 'The Check In field is required',
    })
  },

  // Check Out
  {
    field: 'checkOut',
    header: 'Check Out',
    dataType: 'date',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The Check Out field is required',
      invalid_type_error: 'The Check Out field is required',
    })
  },

  // Hotel Booking No.
  {
    field: 'hotelBookingNumber',
    header: 'Hotel Booking No.',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The Hotel Booking No. field is required').regex(/(I*G*) \d+\s\d+/, 'The Hotel Booking No. field has an invalid format')
  },

  // First Name

  // Invoice Amount
  {
    field: 'invoiceAmount',
    header: 'Invoice Amount',
    dataType: 'number',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.number().min(0, 'The Invoice Amount field is required').nonnegative('The Invoice Amount field must be greater than 0')
  },

  // Room Number
  {
    field: 'roomNumber',
    header: 'Room Number',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The Room Number field is required')
  },

  // Night Type
  {
    field: 'nightType',
    header: 'Night Type',
    dataType: 'select',
    class: `field col-12 md:col-3 ${nightTypeRequired.value ? 'required' : ''}`,
    headerClass: 'mb-1',
  },

  // Coupon Number
  {
    field: 'couponNumber',
    header: 'Coupon Number',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    ...(couponNumberValidation.value ? { validation: z.string().min(1, 'The coupon number field is required').regex(new RegExp(couponNumberValidation.value), 'The coupon number field is invalid') } : { validation: z.string().min(1, 'The coupon number field is required') })
  },

  // Room Category
  {
    field: 'roomCategory',
    header: 'Room Category',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: validateEntityStatusForNotRequiredField('Room Category')
  },

  // Folio Number
  {
    field: 'folioNumber',
    header: 'Folio Number',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },

  // Adults
  {
    field: 'adults',
    header: 'Adults',
    dataType: 'number',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: z.number().min(1, 'The Adults field must be greater than 0').nullable()
  },

  // Children
  {
    field: 'children',
    header: 'Children',
    dataType: 'number',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: z.number().nonnegative('The Children field must not be negative').nullable()

  },

  // Rate Adult
  {
    field: 'rateAdult',
    header: 'Rate Adult',
    dataType: 'number',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: z.number().min(0, 'The Rate Adult field must be greater or equal than 0')
  },
  // Rate Child
  {
    field: 'rateChild',
    header: 'Rate Child',
    dataType: 'number',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: z.number().nonnegative('The Rate Child field must be greater than 0').nullable()
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

  // Room Type
  {
    field: 'roomType',
    header: 'Room Type',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: validateEntityStatusForNotRequiredField('Room Type')
  },

  // Rate Plan
  {
    field: 'ratePlan',
    header: 'Rate Plan',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: validateEntityStatusForNotRequiredField('Rate Plan')

  },

  // Hotel Amount
  {
    field: 'hotelAmount',
    header: 'Hotel Amount',
    dataType: 'number',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The Hotel Amount field is required').refine(val => +val > 0, 'The Hotel Amount field must be greater than 0').nullable()
  },
  // Description
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
]

const item = ref<GenericObject>({
  booking_id: '-',
  hotelCreationDate: new Date(),
  bookingDate: new Date(),
  checkIn: new Date(),
  checkOut: new Date(),
  hotelBookingNumber: '',
  fullName: '',

  invoiceAmount: 0,
  roomNumber: '',
  couponNumber: '',
  adults: 0,
  children: 0,
  rateAdult: 0,
  rateChild: 0,
  hotelInvoiceNumber: '',
  folioNumber: '',
  hotelAmount: 0,
  description: '',
  invoice: '',
  ratePlan: null,
  nightType: null,
  roomType: null,
  roomCategory: null,
  id: ''
})

const itemTemp = ref<GenericObject>({
  booking_id: '-',
  hotelCreationDate: new Date(),
  bookingDate: new Date(),
  checkIn: new Date(),
  checkOut: new Date(),
  hotelBookingNumber: '',
  fullName: '',

  invoiceAmount: 0,
  roomNumber: '',
  couponNumber: '',
  adults: 0,
  children: 0,
  rateAdult: 0,
  rateChild: 0,
  hotelInvoiceNumber: '',
  folioNumber: '',
  hotelAmount: '',
  description: '',
  invoice: '',
  ratePlan: null,
  nightType: null,
  roomType: null,
  roomCategory: null,
  id: ''
})

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

const Columns: IColumn[] = [
  { field: 'booking_id', header: 'Id', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confAgencyApi },
  { field: 'fullName', header: 'Full Name', type: 'text' },
  { field: 'couponNumber', header: 'Coupon Number', type: 'text' },
  { field: 'reservationNumber', header: 'Reservation Number', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'select', objApi: confroomTypeApi },
  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'nights', header: 'Nights', type: 'select', objApi: confnightTypeApi },
  { field: 'ratePlan', header: 'Rate Plan', type: 'select', objApi: confratePlanApi },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'text' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },

]

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]

async function openEditBooking(item: any) {
  props.openDialog()
  if (item?.id) {
    idItem.value = item?.id
    idItemToLoadFirstTime.value = item?.id
    await GetItemById(item?.id)
  }

  if (typeof item === 'string') {
    idItem.value = item
    idItemToLoadFirstTime.value = item
    await GetItemById(item)
  }
}

async function deleteBookingOption(item: any) {
  if (item?.id) {
    await deleteBooking(item?.id)
  }
}

const menuModel = ref([
  { label: 'Add Room Rate', command: props.openRoomRateDialog },
  { label: 'Edit booking', command: openEditBooking },

])

const Options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
  loading: false,
  showFilters: false,
  actionsAsMenu: false,
  showAcctions: true,
  showEdit: false,
  messageToDelete: 'Do you want to save the change?',
  showContextMenu: true,
  menuModel,
  showDelete: true,
  isTabView: props.isTabView
})

const PayloadOnChangePage = ref<PageState>()
const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'ASC'
})
const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const currentInvoice = ref(props.invoiceObj)

// FUNCTIONS ---------------------------------------------------------------------------------------------
function ClearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  formReload.value++
}

const OpenCreateDialog = async () => await navigateTo({ path: 'invoice/create' })

const OpenEditDialog = async (item: any) => await navigateTo({ path: `invoice/edit/${item}` })

async function getratePlanList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    ratePlanList.value = []
    const response = await GenericService.search(confratePlanApi.moduleApi, confratePlanApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      ratePlanList.value = [...ratePlanList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getRoomTypeList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    roomTypeList.value = []
    const response = await GenericService.search(confroomTypeApi.moduleApi, confroomTypeApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      roomTypeList.value = [...roomTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getNightTypeList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    nightTypeList.value = []
    const response = await GenericService.search(confnightTypeApi.moduleApi, confnightTypeApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      nightTypeList.value = [...nightTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getRoomCategoryList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    roomCategoryList.value = []
    const response = await GenericService.search(confroomCategoryApi.moduleApi, confroomCategoryApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      roomCategoryList.value = [...roomCategoryList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getBookingList(clearFilter: boolean = false) {
  try {
    idItemToLoadFirstTime.value = ''
    Options.value.loading = true
    ListItems.value = []

    const response = await GenericService.search(Options.value.moduleApi, Options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      if (iterator.hasOwnProperty('status')) {
        iterator.status = statusToBoolean(iterator.status)
      }
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false, agency: iterator?.invoice?.agency, nights: dayjs(iterator?.checkOut).diff(dayjs(iterator?.checkIn), 'day', false), checkIn: new Date(iterator?.checkIn), checkOut: new Date(iterator?.checkOut) }]
      if (typeof +iterator.invoiceAmount === 'number') {
        totalInvoiceAmount.value += +iterator.invoiceAmount
      }

      if (typeof +iterator.hotelAmount === 'number') {
        totalHotelAmount.value += +iterator.hotelAmount
      }
    }
    if (ListItems.value.length > 0) {
      idItemToLoadFirstTime.value = ListItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    Options.value.loading = false
  }
}

const goToList = async () => await navigateTo('/invoice')

async function ResetListItems() {
  Payload.value.page = 0
  getBookingList()
}

function searchAndFilter() {
  Payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: 'DES'
  }
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    Payload.value.filter = [...Payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }
  getBookingList()
}

function clearFilterToSearch() {
  Payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: 'DES'
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getBookingList()
}
async function GetItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true

    if (props.isCreationDialog) {
      // @ts-expect-error
      const element: any = props.listItems.find((item: any) => item.id === id)
      item.value.id = element.id
      item.value.booking_id = element.booking_id
      idItem.value = element?.id
      item.value.hotelCreationDate = new Date(element.hotelCreationDate)
      item.value.bookingDate = new Date(element.bookingDate)
      item.value.checkIn = new Date(element.checkIn)
      item.value.checkOut = new Date(element.checkOut)
      item.value.hotelBookingNumber = element.hotelBookingNumber
      item.value.fullName = element.fullName

      item.value.invoiceAmount = element.invoiceAmount ? element.invoiceAmount : 0
      item.value.roomNumber = element.roomNumber
      item.value.couponNumber = element.couponNumber
      item.value.adults = element.adults
      item.value.children = element.children
      item.value.rateAdult = element.rateAdult
      item.value.rateChild = element.rateChild
      item.value.hotelInvoiceNumber = element.hotelInvoiceNumber
      item.value.folioNumber = element.folioNumber
      item.value.hotelAmount = element.hotelAmount ? String(element?.hotelAmount) : '0'
      item.value.description = element.description
      item.value.invoice = element.invoice
      item.value.ratePlan = element.ratePlan
      item.value.nightType = element.nightType
      item.value.roomType = element.roomType
      item.value.roomCategory = element.roomCategory
      loadingSaveAll.value = false
      return formReload.value += 1
    }

    try {
      const response = await GenericService.getById(confApi.booking.moduleApi, confApi.booking.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.booking_id = response.booking_id
        idItem.value = response?.id
        item.value.hotelCreationDate = new Date(response.hotelCreationDate)
        item.value.bookingDate = new Date(response.bookingDate)
        item.value.checkIn = new Date(response.checkIn)
        item.value.checkOut = new Date(response.checkOut)
        item.value.hotelBookingNumber = response.hotelBookingNumber
        item.value.fullName = response.fullName

        item.value.invoiceAmount = response.invoiceAmount
        item.value.roomNumber = response.roomNumber
        item.value.couponNumber = response.couponNumber
        item.value.adults = response.adults
        item.value.children = response.children
        item.value.rateAdult = response.rateAdult
        item.value.rateChild = response.rateChild
        item.value.hotelInvoiceNumber = response.hotelInvoiceNumber
        item.value.folioNumber = response.folioNumber
        item.value.hotelAmount = String(response.hotelAmount)
        item.value.description = response.description
        item.value.invoice = response.invoice
        item.value.ratePlan = response.ratePlan
        item.value.nightType = response.nightType
        item.value.roomType = response.roomType
        item.value.roomCategory = response.roomCategory
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

async function createBooking(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.roomCategory = item.roomCategory?.id || null
    payload.roomType = item.roomType?.id || null
    payload.nightType = item.nightType?.id || null
    payload.ratePlan = item.ratePlan?.id || null

    await GenericService.create(confApi.booking.moduleApi, confApi.booking.uriApi, payload)
  }
}

async function updateBooking(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.roomCategory = item.roomCategory?.id || null
  payload.roomType = item.roomType?.id || null
  payload.nightType = item.nightType?.id || null
  payload.ratePlan = item.ratePlan?.id || null

  await GenericService.update(confApi.booking.moduleApi, confApi.booking.uriApi, idItem.value || item?.id || '', payload)
}

async function deleteBooking(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(Options.value.moduleApi, Options.value.uriApi, id)
    ClearForm()
    getBookingList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveBooking(item: { [key: string]: any }) {
  if (props.selectedInvoice) {
    item.invoice = props.selectedInvoice
  }

  loadingSaveAll.value = true
  let successOperation = true
  if (idItem.value || item?.id) {
    try {
      if (!item?.id) {
        item.id = idItem.value
      }
      if (props?.isCreationDialog) {
        props.updateItem(item)
      }
      else {
        await updateBooking(item)
      }

      idItem.value = ''
      props.closeDialog()
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
      if (props?.isCreationDialog) {
        item.id = v4()
        props.addItem(item)
      }
      else {
        await createBooking(item)
      }
      props.closeDialog()
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    ClearForm()
    if (!props.isCreationDialog) {
      getBookingList()
    }
  }
}

function requireConfirmationToSaveBooking(item: any) {
  const { event } = item
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      saveBooking(item)
    }
  })
}

function requireConfirmationToDeleteBooking(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      deleteBooking(idItem.value)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  getBookingList()
}

function OnSortField(event: any) {
  if (event) {
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    ParseDataTableFilter(event.filter)
  }
}

watch(() => props.invoiceObj, () => {
  currentInvoice.value = props.invoiceObj
}, { deep: true })

watch(() => props.invoiceAgency?.bookingCouponFormat, () => {
  couponNumberValidation.value = props.invoiceAgency?.bookingCouponFormat
})

watch(() => props.forceUpdate, () => {
  if (props.forceUpdate) {
    getBookingList()
    props.toggleForceUpdate()
  }
})

onMounted(() => {
  if (props.selectedInvoice) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: props.selectedInvoice,
      logicalOperation: 'AND'
    }]
  }

  if (!props.isCreationDialog) {
    getBookingList()
  }
})

// console.log('TAB COUPON VALIDATION', couponNumberValidation)
</script>

<template>
  <div>
    <DynamicTableWithContextMenu
      :data="isCreationDialog ? listItems as any : ListItems" :columns="Columns"
      :options="Options" :pagination="Pagination" @on-confirm-create="ClearForm"
      @open-edit-dialog="OpenEditDialog($event)"
      @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
      @on-list-item="ResetListItems" @on-sort-field="OnSortField" @update:double-clicked="($event) => {

        openEditBooking({ id: $event })

      }"
    >
      <template #row-selector-body="itemProps">
        <span class="pi pi-pen-to-square" @click="openEditBooking(itemProps.item)" />
      </template>

      <template v-if="showTotals" #pagination-right="props">
        <div
          class="flex align-items-center  rounded-full" style="height: 10px; margin-left: 40px;"
        >
          <Badge class="px-2 mr-7 flex align-items-center text-xs text-white" severity="contrast">
            <span class="font-bold">
              Total invoice amount: ${{ totalInvoiceAmount }}
            </span>
          </Badge>
          <Badge class="px-2 ml-1 mr-6  flex align-items-center text-xs text-white" severity="contrast">
            <span class="font-bold">
              Total hotel amount: ${{ totalHotelAmount }}
            </span>
          </Badge>
        </div>
      </template>
    </DynamicTableWithContextMenu>
  </div>

  <div v-if="isDialogOpen">
    <BookingDialog
      :fields="Fields" :fieldsv2="fieldsV2" :item="item" :open-dialog="isDialogOpen" :form-reload="formReload"
      :loading-save-all="loadingSaveAll" :clear-form="ClearForm"
      :require-confirmation-to-save="requireConfirmationToSaveBooking"
      :require-confirmation-to-delete="requireConfirmationToDeleteBooking" :header="isCreationDialog || !idItem ? 'New Booking' : 'Edit Booking'"
      :close-dialog="closeDialog" container-class="grid grid-cols-2 justify-content-around mx-4 my-2 w-full"
      class="h-full p-2 overflow-y-hidden" content-class="w-full" :night-type-list="nightTypeList"
      :rate-plan-list="ratePlanList" :room-category-list="roomCategoryList" :room-type-list="roomTypeList" :get-night-type-list="getNightTypeList" :get-room-category-list="getRoomCategoryList" :get-room-type-list="getRoomTypeList" :getrate-plan-list="getratePlanList"
      :invoice-agency="invoiceAgency" :invoice-hotel="invoiceHotel" :is-night-type-required="nightTypeRequired" :coupon-number-validation="couponNumberValidation"
    />
  </div>
</template>
