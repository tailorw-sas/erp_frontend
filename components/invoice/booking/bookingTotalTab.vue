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
import BookingTotalDialog from './BookingPartialDialog.vue'

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
  isDetailView: {
    type: Boolean,
    required: false,
    default: false,
  },
  refetchInvoice: { type: Function, default: () => { } },
  getInvoiceAgency: { type: Function, default: () => { } },
  sortBooking: Function as any,
  nightTypeRequired: Boolean,
  bookingsTotalObj: {
    type: Object,
    required: false,
    default: () => {
      return {
        totalHotelAmount: 0,
        totalInvoiceAmount: 0,
        totalDueAmount: 0
      }
    }
  }
})

const toast = useToast()
const loadingSaveAll = ref(false)
const confirm = useConfirm()
const ListItems = ref<any[]>([])
const formReload = ref(0)

const route = useRoute()
const authStore = useAuthStore()
const { status, data } = useAuth()
const isAdmin = (data.value?.user as any)?.isAdmin === true

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

const bookingContextMenu = ref()
const selectedBooking = ref<any>()

const totalInvoiceAmount = ref<number>(0)
const totalHotelAmount = ref<number>(0)
const totalOriginalAmount = ref<number>(0)

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
        field: 'bookingId',
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
        header: 'Coupon No.',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        ...(couponNumberValidation.value ? { validation: z.string().min(1, 'The coupon no. field is required').regex(new RegExp(couponNumberValidation.value), 'The coupon no. field is invalid') } : { validation: z.string().min(1, 'The coupon no. field is required') })

      },
      {
        field: 'ratePlan',
        header: 'Rate Plan',
        dataType: 'select',
        class: 'field col-12 ',
        headerClass: 'mb-1',

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
        validation: z.string().min(1, 'The Hotel Booking No. field is required').regex(/^[IG] +\d+ +\d{2,}\s*$/, 'The Hotel Booking No. field has an invalid format')

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

      },
      {
        field: 'roomNumber',
        header: 'Room Number',
        dataType: 'text',
        class: 'field col-12',
        headerClass: 'mb-1',
        

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
  {
    field: 'firstName',
    header: 'First Name',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The First Name field is required')
  },
  {
    field: 'lastName',
    header: 'Last Name',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The Last Name field is required')
  },
  // Coupon No.
  {
    field: 'couponNumber',
    header: 'Coupon No.',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',

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

  // Adults
  {
    field: 'adults',
    header: 'Adult',
    dataType: 'number',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: z.number().min(1, 'The Adults field must be greater than 0').nullable()
  },

  // Children
  {
    field: 'children',
    header: 'Child',
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


  // Invoice Amount
  {
    field: 'invoiceAmount',
    header: 'Invoice Amount',
    dataType: 'number',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',

    ...(route.query.type === InvoiceType.OLD_CREDIT || route.query.type === InvoiceType.CREDIT || props.invoiceObj?.invoiceType?.id === InvoiceType.OLD_CREDIT || props.invoiceObj?.invoiceType?.id === InvoiceType.CREDIT ? { validation: z.string().min(0, 'The Invoice Amount field is required').refine((value: any) => !isNaN(value) && +value < 0, { message: 'The Invoice Amount field must be negative' }) } : { validation: z.string().min(0, 'The Invoice Amount field is required').refine((value: any) => !isNaN(value) && +value >= 0, { message: 'The Invoice Amount field must be greater or equals than 0' }) })
  },

  // Hotel Amount
  {
    field: 'hotelAmount',
    header: 'Hotel Amount',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    validation: z.string().trim().regex(/^\d+$/, 'The Hotel Amount field must be greater than or equal to 0')
    // validation: z.string().trim().regex(/^\d+$/, 'Only numeric characters allowed')
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

  {
    field: 'bookingDate',
    header: 'Booking Date',
    dataType: 'date',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',

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

  // Night Type
  {
    field: 'nightType',
    header: 'Night Type',
    dataType: 'select',
    class: `field col-12 md:col-3 ${nightTypeRequired.value ? 'required' : ''}`,
    headerClass: 'mb-1',
  },



  // Folio Number
  {
    field: 'folioNumber',
    header: 'Folio Number',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },


  // Room Number
  {
    field: 'roomNumber',
    header: 'Room Number',
    dataType: 'text',
    class: 'field col-12 md:col-3 ',
    headerClass: 'mb-1',
  },


  // Room Type
  {
    field: 'roomType',
    header: 'Room Type',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',

  },


  // Room Category
  {
    field: 'roomCategory',
    header: 'Room Category',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',

  },







  // Rate Plan
  {
    field: 'ratePlan',
    header: 'Rate Plan',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',

  },

  // Description
  {
    field: 'description',
    header: 'Remark',
    dataType: 'text',
    class: 'field col-12 md:col-9',
    headerClass: 'mb-1',
  },
]

const item = ref<GenericObject>({
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
  id: ''
})

const itemTemp = ref<GenericObject>({
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

  { field: 'bookingId', header: 'Id', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confAgencyApi, sortable: !props.isDetailView && !props.isCreationDialog },

  { field: 'fullName', header: 'Full Name', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'hotelBookingNumber', header: 'Reservation No.', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'couponNumber', header: 'Coupon No.', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'roomType', header: 'Room Type', type: 'select', objApi: confroomTypeApi, sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'checkIn', header: 'Check In', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'checkOut', header: 'Check Out', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'nights', header: 'Nights', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'ratePlan', header: 'Rate Plan', type: 'select', objApi: confratePlanApi, sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'invoiceAmount', header: 'Booking Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog, editable: route.query.type === InvoiceType.CREDIT && props.isCreationDialog },
  { field: 'dueAmount', header: 'Booking Balance', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

]

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]

const finalColumns = ref<IColumn[]>(Columns)

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

const menuModel = <any>ref()

const Options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',

  messageToDelete: 'Do you want to save the change?',
  loading: false,
  showFilters: false,
  actionsAsMenu: false,
})

const PayloadOnChangePage = ref<PageState>()
const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'bookingId',
  sortType: ENUM_SHORT_TYPE.ASC
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

    totalInvoiceAmount.value = 0
    totalHotelAmount.value = 0
    totalOriginalAmount.value = 0
    
    for (const iterator of dataList) {
      console.log('Si entro a este total', iterator);
      ListItems.value = [...ListItems.value, {
        ...iterator,
        roomType: { ...iterator?.roomType, name: iterator?.roomType?.code ? `${iterator?.roomType?.code || ""}-${iterator?.roomType?.name || ""}` : "" },
        ratePlan: { ...iterator?.ratePlan, name: iterator?.ratePlan?.code ? `${iterator?.ratePlan?.code || ""}-${iterator?.ratePlan?.name || ""}` : "" },
        loadingEdit: false,
        loadingDelete: false,
        agency: { ...iterator?.invoice?.agency, name: `${iterator?.invoice?.agency?.code}-${iterator?.invoice?.agency?.name}` },
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        fullName: `${iterator.firstName ? iterator.firstName : ""} ${iterator.lastName ? iterator.lastName : ''}`,
        originalAmount: iterator?.invoiceAmount
      }]
      if (typeof +iterator.invoiceAmount === 'number') {
        console.log('Si entro a este total', iterator.invoiceAmount);
        
        totalInvoiceAmount.value += Number(iterator.invoiceAmount)
      }

      if (typeof +iterator.originalAmount === 'number') {
        totalOriginalAmount.value += Number(iterator.originalAmount)
      }
      if (typeof +iterator.hotelAmount === 'number') {
        totalHotelAmount.value += Number(iterator.hotelAmount)
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
    sortType: ENUM_SHORT_TYPE.DESC
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
    sortType: ENUM_SHORT_TYPE.DESC
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
      item.value.bookingId = element.bookingId
      idItem.value = element?.id
      item.value.hotelCreationDate = dayjs(element.hotelCreationDate).startOf('day').toDate()
      item.value.bookingDate = element.bookingDate ? new Date(element.bookingDate) : new Date()
      item.value.checkIn = new Date(element.checkIn)
   
      item.value.checkOut = new Date(element.checkOut)
      item.value.hotelBookingNumber = element.hotelBookingNumber
      item.value.fullName = element.fullName
      item.value.firstName = element.firstName
      item.value.lastName = element.lastName

      item.value.invoiceAmount = element.invoiceAmount ? String(element.invoiceAmount) : '0'
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
        item.value.bookingId = response.bookingId
        idItem.value = response?.id
        item.value.hotelCreationDate = new Date(response.hotelCreationDate)
        item.value.bookingDate = response.bookingDate ? new Date(response.bookingDate) : ''
        item.value.checkIn = new Date(response.checkIn)
        item.value.checkOut = new Date(response.checkOut)
        item.value.hotelBookingNumber = response.hotelBookingNumber
        item.value.fullName = response.fullName
        item.value.firstName = response.firstName
        item.value.lastName = response.lastName

        item.value.invoiceAmount = response.invoiceAmount ? String(response?.invoiceAmount) : '0'
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
  console.log('asdasdasdasdasd');
  
  item.hotelBookingNumber = item.hotelBookingNumber.split(" ").filter((a: string) => a !== "").join(" ")
  item.checkIn = dayjs(item.checkIn).startOf('day').toDate()
  item.checkOut = dayjs(item.checkOut).startOf('day').toDate()
  item.hotelCreationDate = dayjs(item.hotelCreationDate).startOf('day').toDate()
  item.bookingDate = dayjs(item.bookingDate).startOf('day').toDate()

  if (props.selectedInvoice && typeof props.selectedInvoice === 'string') {
    item.invoice = props.selectedInvoice
  }

  console.log(props.nightTypeRequired);
  if (!props.isCreationDialog) {

    await props.getInvoiceAgency(props?.invoiceObj?.agency?.id)

    if (props.nightTypeRequired && !item?.nightType?.id) {


      return toast.add({ severity: 'error', summary: 'Error', detail: 'The Night Type field is required for this client', life: 10000 })
    }



  }

  item.fullName = `${item?.firstName ?? ''} ${item?.lastName ?? ''}`

  if (props.isCreationDialog) {
    const invalid: any = props?.listItems?.find((booking: any) => booking?.hotelBookingNumber === item?.hotelBookingNumber)

    if (invalid && invalid?.id !== idItem.value) {
      return toast.add({ severity: 'error', summary: 'Error', detail: 'The field Hotel booking No. is repeated', life: 10000 })
    }
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
      console.log(error)
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
      console.log(error)
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    ClearForm()
    if (!props.isCreationDialog) {
      props.refetchInvoice()
      // props?.toggleForceUpdate()
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

    if (props?.isCreationDialog) {
      return props.sortBooking(event)
    }





    Payload.value.sortBy = getSortField(event.sortField)


    Payload.value.sortType = event.sortOrder

    getBookingList()



  }
}

function getSortField(field: any) {

  switch (field) {
    case 'agency':
      return 'invoice.agency'

    case 'fullName':
      return 'firstName'


    default: return field

  }
}

watch(() => props.invoiceObj, () => {
  currentInvoice.value = props.invoiceObj
}, { deep: true })

watch(() => props.invoiceAgency?.bookingCouponFormat, () => {
  couponNumberValidation.value = props.invoiceAgency?.bookingCouponFormat
})

// watch(() => props.listItems, () => {
//   console.log('Entro a watch list items');
  
//   if (props.listItems && props?.listItems?.length > 0) {
//     totalHotelAmount.value = 0
//     totalInvoiceAmount.value = 0
//     totalOriginalAmount.value = 0
//     props?.listItems?.forEach((listItem: any) => {    
//       totalHotelAmount.value += listItem?.hotelAmount ? Number(listItem?.hotelAmount) : 0
//       totalInvoiceAmount.value += listItem?.invoiceAmount ? Number(listItem?.invoiceAmount) : 0
//       totalOriginalAmount.value += listItem?.dueAmount ? Number(listItem?.dueAmount) : 0
//     })
//     console.log(totalHotelAmount.value, totalInvoiceAmount.value, totalOriginalAmount.value);
//   }
  
// }, { deep: true })

// watch(() => props.listItems, () => {
//   if (props.isCreationDialog) {
//     totalHotelAmount.value = 0
//     totalInvoiceAmount.value = 0
//     totalOriginalAmount.value = 0
//     props?.listItems?.forEach((listItem: any) => {
//       totalHotelAmount.value += listItem?.hotelAmount ? Number(listItem?.hotelAmount) : 0
//       totalInvoiceAmount.value += listItem?.invoiceAmount ? Number(listItem?.invoiceAmount) : 0
//       totalOriginalAmount.value += listItem?.originalAmount ? Number(listItem?.originalAmount) : 0
//     })
//   }
// }, { deep: true })

watch(() => props.forceUpdate, () => {
  if (props.forceUpdate) {
    getBookingList()
  }
})

function onRowRightClick(event: any) {

  console.log(props.invoiceObj);
  if (route.query.type === InvoiceType.INCOME || props.invoiceObj?.invoiceType?.id === InvoiceType.INCOME || route.query.type === InvoiceType.CREDIT) {
    return;
  }

  if (!props.isCreationDialog && props.invoiceObj?.status?.id !== InvoiceStatus.PROCECSED) {
    return;
  }

  selectedBooking.value = event.data
  bookingContextMenu.value.show(event.originalEvent)
}

function onCellEditComplete(val: any) {

  if (props.isCreationDialog) {
    if (route.query.type === InvoiceType.CREDIT) {
      val.newData.invoiceAmount = toNegative(val.newData.invoiceAmount)

      console.log(val.newData);

      if (toPositive(val.newData.invoiceAmount) > toPositive(val.newData.originalAmount)) {
        toast.add({ severity: 'error', summary: 'Error', detail: "Booking invoice amount cannot be greater than original amount", life: 10000 })
        return null;
      }

    }
    return props.updateItem(val?.newData)
  }

  console.log(val);
}


watch(PayloadOnChangePage, (newValue) => {
  Payload.value.page = newValue?.page ? newValue?.page : 0
  Payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getBookingList()
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

  if (props.isDetailView) {
    finalColumns.value = [

      { field: 'bookingId', header: 'Id', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

      { field: 'fullName', header: 'Full Name', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'checkIn', header: 'Check In', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'checkOut', header: 'Check Out', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'adults', header: 'Adult', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'children', header: 'Children', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'couponNumber', header: 'Coupon No.', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'hotelBookingNumber', header: 'Reservation No.', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'hotelAmount', header: 'Hotel Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'invoiceAmount', header: 'Booking Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'dueAmount', header: 'Booking Balance', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

    ]
  }

  if (props?.isCreationDialog && route.query.type === InvoiceType.CREDIT) {

    finalColumns.value = [


      { field: 'agency', header: 'Agency', type: 'select', objApi: confAgencyApi, sortable: !props.isDetailView && !props.isCreationDialog },

      { field: 'fullName', header: 'Full Name', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'hotelBookingNumber', header: 'Reservation No.', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'couponNumber', header: 'Coupon No.', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

      { field: 'checkIn', header: 'Check In', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'checkOut', header: 'Check Out', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },

      { field: 'originalAmount', header: 'Original Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'invoiceAmount', header: 'Booking Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog, editable: route.query.type === InvoiceType.CREDIT && props.isCreationDialog },

    ]
  }

  const computedShowMenuItemAddRoomRate = computed(() => {
    return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:ROOM-RATE-CREATE'])))
  })
  const computedShowMenuItemEditBooking = computed(() => {
    return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:BOOKING-EDIT'])))
  })
  menuModel.value = [
   /* {
      label: 'Add Room Rate',
      command: () => props.openRoomRateDialog(selectedBooking.value),
      disabled: computedShowMenuItemAddRoomRate
    },*/
    {
      label: 'Edit booking',
      command: () => openEditBooking(selectedBooking.value),
   //   disabled: computedShowMenuItemEditBooking
    }
  ]

  if (route.query.type === InvoiceType.CREDIT || props.invoiceObj?.invoiceType?.id === InvoiceType.CREDIT) {
    menuModel.value = [
      {
        label: 'Edit booking',
        command: () => openEditBooking(selectedBooking.value),
        disabled: computedShowMenuItemEditBooking
      },
    ]
  }



  if (!props.isCreationDialog) {
    getBookingList()
  }
})

// console.log('TAB COUPON VALIDATION', couponNumberValidation)
</script>

<template>
  <div>
    <DynamicTable 
      :data="isCreationDialog ? listItems as any : ListItems" 
      :columns="finalColumns" 
      :options="Options"
      :pagination="Pagination" 
      @on-confirm-create="ClearForm" 
      @open-edit-dialog="OpenEditDialog($event)"
      @on-change-pagination="PayloadOnChangePage = $event" 
      @on-change-filter="ParseDataTableFilter"
      @on-list-item="ResetListItems" 
      @on-sort-field="OnSortField" 
      @on-row-right-click="onRowRightClick"
      @on-table-cell-edit-complete="onCellEditComplete" 
      @on-row-double-click="($event) => {

        // if (route.query.type === InvoiceType.OLD_CREDIT && isCreationDialog){ return }
        if (route.query.type === InvoiceType.INCOME || props.invoiceObj?.invoiceType?.id === InvoiceType.INCOME || route.query.type === InvoiceType.CREDIT) {

          return;
        }

        if (!props.isCreationDialog && props.invoiceObj?.status?.id !== InvoiceStatus.PROCECSED) {
          return;
        }

        if (!props.isDetailView) {
          openEditBooking($event)
        }
      }">
      <template #datatable-footer>
        <ColumnGroup type="footer" class="flex align-items-center">
          <Row>
            <Column 
              footer="Totals:"
              :colspan="isDetailView ? 8 : route.query.type === InvoiceType.CREDIT && props.isCreationDialog ? 6 : 10"
              footer-style="text-align:right; font-weight: 700"
            />
            
            <Column 
              v-if="!(route.query.type === InvoiceType.CREDIT && props.isCreationDialog)"
              :footer="props.bookingsTotalObj.totalHotelAmount"
              footer-style="font-weight: 700" 
            />

            <Column 
              v-if="(route.query.type === InvoiceType.CREDIT && props.isCreationDialog)"
              :footer="props.bookingsTotalObj.totalDueAmount" 
              footer-style="font-weight: 700"
            />
            <Column 
              :footer="props.bookingsTotalObj.totalInvoiceAmount"
              footer-style="font-weight: 700"
            />
            <Column v-if="!(route.query.type === InvoiceType.CREDIT && props.isCreationDialog)"
              :footer="props.bookingsTotalObj.totalInvoiceAmount" footer-style="font-weight: 700" />
          </Row>
        </ColumnGroup>
      </template>

      <template v-if="isCreationDialog" #pagination-total="props">
        <span class="font-bold font">
          {{ listItems?.length }}
        </span>
      </template>


    </DynamicTable>
  </div>
  <ContextMenu v-if="!isDetailView" ref="bookingContextMenu" :model="menuModel" />

  <div v-if="isDialogOpen" style="h-fit">
    <BookingTotalDialog :fields="Fields" :fieldsv2="fieldsV2" :item="item" :open-dialog="isDialogOpen"
      :form-reload="formReload" :loading-save-all="loadingSaveAll" :clear-form="ClearForm"
      :require-confirmation-to-save="saveBooking" :require-confirmation-to-delete="requireConfirmationToDeleteBooking"
      :header="isCreationDialog || !idItem ? 'Edit Booking' : 'Edit Booking'" :close-dialog="() => {
        ClearForm()
        closeDialog()
      }" container-class="grid grid-cols-2 justify-content-around mx-4 my-2 w-full" class="h-fit p-2 overflow-y-hidden"
      content-class="w-full" :night-type-list="nightTypeList" :rate-plan-list="ratePlanList"
      :room-category-list="roomCategoryList" :room-type-list="roomTypeList" :get-night-type-list="getNightTypeList"
      :get-room-category-list="getRoomCategoryList" :get-room-type-list="getRoomTypeList"
      :getrate-plan-list="getratePlanList" :invoice-agency="invoiceAgency" :invoice-hotel="invoiceHotel"
      :is-night-type-required="nightTypeRequired" :coupon-number-validation="couponNumberValidation"
      :invoice-obj="currentInvoice" />
  </div>
</template>

<style>
.p-datatable-tfoot {
  background-color: #42A5F5;

  tr td {
    color: #fff;
  }
}
</style>
