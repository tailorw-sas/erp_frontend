<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import { v4 } from 'uuid'
import dayjs from 'dayjs'
import RoomRateDialog from './RoomRateDialog.vue'
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

  selectedBooking: {
    type: String,
    required: true
  },
  openAdjustmentDialog: {
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

  isDetailView: {
    type: Boolean,
    required: false,
    default: false,
  },
  sortRoomRate: Function as any,
  refetchInvoice: { type: Function, default: () => { } },
  invoiceObj: {
    type: Object,
    required: true
  },
  bookingObj: {
    type: Object,
    required: true
  },
  getInvoiceHotel: { type: Function, default: () => { } },
  requiresFlatRate: Boolean,

})
const route = useRoute()
const totalInvoiceAmount = ref<number>(0)
const totalHotelAmount = ref<number>(0)

const authStore = useAuthStore()
const { status, data } = useAuth()
const isAdmin = (data.value?.user as any)?.isAdmin === true

const roomRateContextMenu = ref()
const selectedRoomRate = ref<any>()

const toast = useToast()
const loadingSaveAll = ref(false)
const confirm = useConfirm()
const ListItems = ref<any[]>([])
const formReload = ref(0)
const active = ref(0)
const forceSave = ref(false)
const LoadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const loadingDelete = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const bookingList = ref<any[]>([])

const confbookingListApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
})

const dialogOpen = ref<boolean>(props.isDialogOpen)

async function openEditDialog(item: any) {
  if (route.query.type === InvoiceType.CREDIT || props.invoiceObj?.invoiceType?.id === InvoiceType.CREDIT) {
    return null
  }

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

async function deleteRoomRateOption(item: any) {
  if (item?.id) {
    await deleteRoomRate(item?.id)
  }
}

const computedShowMenuItemAddAdjustment = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:ADJUSTMENT-CREATE'])))
})
const computedShowMenuItemEditRoomRate = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:ROOM-RATE-EDIT'])))
})

const menuModel = ref([
  { label: 'Add Adjustment', command: () => props.openAdjustmentDialog(selectedRoomRate.value), disabled: computedShowMenuItemAddAdjustment },
  { label: 'Edit Room Rate', command: () => openEditDialog(selectedRoomRate.value), disabled: computedShowMenuItemEditRoomRate },

])

const fields: Array<FieldDefinitionType> = [
  {
    field: 'roomRateId',
    header: 'Id',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    disabled: true

  },
  {
    field: 'roomNumber',
    header: 'Room Number',
    dataType: 'number',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.number({ invalid_type_error: 'The Room Number field must be greater than 0 ' }).min(1, 'The Room Number field must be greater than 0')

  },

  {
    field: 'checkIn',
    header: 'Check In',
    dataType: 'date',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The Check In field is required',
      invalid_type_error: 'The Check In field is required',
    })

  },
  {
    field: 'checkOut',
    header: 'Check Out',
    dataType: 'date',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The Check Out field is required',
      invalid_type_error: 'The Check Out field is required',
    })

  },

  {
    field: 'adults',
    header: 'Adults',
    dataType: 'number',
    class: 'field col-12 md:col-6',
    headerClass: 'mb-1',
    validation: z.number({ invalid_type_error: 'The Adults field must be greater than 0 ' }).min(1, 'The Adults field must be greater than 0')
  },
  {
    field: 'children',
    header: 'Children',
    dataType: 'number',
    class: 'field col-12 md:col-6',
    headerClass: 'mb-1',

  },
  {
    field: 'rateAdult',
    header: 'Rate Adult',
    dataType: 'number',
    class: 'field col-12 md:col-6',
    headerClass: 'mb-1',

  },
  {
    field: 'rateChild',
    header: 'Rate Child',
    dataType: 'number',
    class: 'field col-12 md:col-6',
    headerClass: 'mb-1',

  },

  {
    field: 'invoiceAmount',
    header: 'Rate Amount',
    dataType: 'number',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The Rate amount field is required').refine((val) => { return route.query.type === InvoiceType.INVOICE ? +val > 0 : true }, 'The Rate amount field must be greater than 0')

  },
  {
    field: 'hotelAmount',
    header: 'Hotel Amount',
    dataType: 'number',
    class: 'field col-12 md:col-6',
    headerClass: 'mb-1',
    validation: z.string().refine(val => +val >= 0, 'The Hotel Amount field cannot be negative').nullable()

  },
  {
    field: 'remark',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12 ',
    headerClass: 'mb-1',

  }
]

const Fields: Array<Container> = [
  {
    childs: [
      {
        field: 'roomRateId',
        header: 'Id',
        dataType: 'text',
        class: 'field col-12 md:col-6 required',
        headerClass: 'mb-1',
        disabled: true

      },

      {
        field: 'checkIn',
        header: 'Check In',
        dataType: 'date',
        class: 'field col-12 md:col-6 required',
        headerClass: 'mb-1',
        validation: z.date({
          required_error: 'The Check In field is required',
          invalid_type_error: 'The Check In field is required',
        })

      },
      {
        field: 'checkOut',
        header: 'Check Out',
        dataType: 'date',
        class: 'field col-12 md:col-6 required',
        headerClass: 'mb-1',
        validation: z.date({
          required_error: 'The Check Out field is required',
          invalid_type_error: 'The Check Out field is required',
        })

      },

      {
        field: 'invoiceAmount',
        header: 'Rate Amount',
        dataType: 'number',
        class: 'field col-12 md:col-6 required',
        headerClass: 'mb-1',
        validation: z.string().min(1, 'The Rate amount field is required').refine((val) => { return route.query.type === InvoiceType.INVOICE ? +val > 0 : true }, 'The Rate amount field must be greater than 0')

      },
      {
        field: 'roomNumber',
        header: 'Room Number',
        dataType: 'number',
        class: 'field col-12 md:col-6 required',
        headerClass: 'mb-1',
        validation: z.number({ invalid_type_error: 'The Room Number field must be greater than 0 ' }).min(1, 'The Room Number field must be greater than 0')

      },

      {
        field: 'adults',
        header: 'Adults',
        dataType: 'number',
        class: 'field col-12 md:col-6',
        headerClass: 'mb-1',
        validation: z.number({ invalid_type_error: 'The Adults field must be greater than 0 ' }).min(1, 'The Adults field must be greater than 0')
      },
      {
        field: 'children',
        header: 'Children',
        dataType: 'number',
        class: 'field col-12 md:col-6',
        headerClass: 'mb-1',

      },
      {
        field: 'rateAdult',
        header: 'Rate Adult',
        dataType: 'number',
        class: 'field col-12 md:col-6',
        headerClass: 'mb-1',

      },
      {
        field: 'rateChild',
        header: 'Rate Child',
        dataType: 'number',
        class: 'field col-12 md:col-6',
        headerClass: 'mb-1',

      },

      {
        field: 'hotelAmount',
        header: 'Hotel Amount',
        dataType: 'number',
        class: 'field col-12 md:col-6',
        headerClass: 'mb-1',
        validation: z.string().refine(val => +val >= 0, 'The Hotel Amount field cannot be negative').nullable()

      },
      {
        field: 'remark',
        header: 'Description',
        dataType: 'textarea',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      }

    ],
    containerClass: 'flex flex-column w-full'
  },

]

const item = ref<GenericObject>({
  roomRateId: '',
  checkIn: dayjs(props.bookingObj?.checkIn).toDate(),
  checkOut: dayjs(props.bookingObj?.checkOut).toDate(),
  invoiceAmount: '0',
  roomNumber: '0',
  adults: 0,
  children: 0,
  rateAdult: 0,
  rateChild: 0,
  hotelAmount: '0',
  remark: '',
  booking: props.selectedBooking,
})

const itemTemp = ref<GenericObject>({
  roomRateId: '',
  checkIn: dayjs(props.bookingObj?.checkIn).toDate(),
  checkOut: dayjs(props.bookingObj?.checkOut).toDate(),
  invoiceAmount: '0',
  roomNumber: '0',
  adults: 0,
  children: 0,
  rateAdult: 0,
  rateChild: 0,
  hotelAmount: '0',
  remark: '',
  booking: '',
})

const confApi = reactive({
  roomrate: {
    moduleApi: 'invoicing',
    uriApi: 'manage-room-rate',
  },

  invoice: {
    moduleApi: 'invoicing',
    uriApi: 'manage-invoice',
  },
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

const Columns: IColumn[] = [

  { field: 'roomRateId', header: 'Id', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

  { field: 'fullName', header: 'Full Name', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

  { field: 'checkIn', header: 'Check In', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'checkOut', header: 'Check Out', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'adults', header: 'Adults', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'children', header: 'Children', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'roomType', header: 'Room Type', type: 'select', objApi: confAgencyApi, sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'nights', header: 'Nights', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'ratePlan', header: 'Rate Plan', type: 'select', objApi: confratePlanApi, sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

]

const finalColumns = ref<IColumn[]>(Columns)

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]

const Options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-room-rate',
  loading: false,
  showFilters: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',

})

const PayloadOnChangePage = ref<PageState>()
const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

// FUNCTIONS ---------------------------------------------------------------------------------------------
function ClearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  formReload.value++
}

function onRowRightClick(event: any) {
  selectedRoomRate.value = event.data
  roomRateContextMenu.value.show(event.originalEvent)
}

const OpenCreateDialog = async () => await navigateTo({ path: 'invoice/create' })

const OpenEditDialog = async (item: any) => await navigateTo({ path: `invoice/edit/${item}` })

async function getRoomRateList() {
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

    let countRR = 0
    totalInvoiceAmount.value = 0
    totalHotelAmount.value = 0
    for (const iterator of dataList) {
      countRR++

      ListItems.value = [...ListItems.value, {
        ...iterator,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        loadingEdit: false,
        loadingDelete: false,
        fullName: `${iterator.booking.firstName ? iterator.booking.firstName : ''} ${iterator.booking.lastName ? iterator.booking.lastName : ''}`,
        bookingId: iterator.booking.bookingId,
        roomType: { ...iterator.booking.roomType, name: `${iterator?.booking?.roomType?.code || ''}-${iterator?.booking?.roomType?.name || ''}` },
        nightType: { ...iterator.booking.nightType, name: `${iterator?.booking?.nightType?.code || ''}-${iterator?.booking?.nightType?.name || ''}` },
        ratePlan: { ...iterator.booking.ratePlan, name: `${iterator?.booking?.ratePlan?.code || ''}-${iterator?.booking?.ratePlan?.name || ''}` },
        agency: { ...iterator?.booking?.invoice?.agency, name: `${iterator?.booking?.invoice?.agency?.code || ''}-${iterator?.booking?.invoice?.agency?.name || ''}` }
      }]

      if (typeof +iterator.invoiceAmount === 'number') {
        totalInvoiceAmount.value += Number(iterator.invoiceAmount)
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
  getRoomRateList()
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
  getRoomRateList()
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
  getRoomRateList()
}

const currentBooking = ref('')

async function GetItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true

    if (props.isCreationDialog) {
      // @ts-expect-error
      const element: any = props.listItems.find((item: any) => item.id === id)
      console.log(element)
      item.value.id = element.id
      item.value.roomRateId = element.roomRateId
      item.value.checkIn = new Date(element.checkIn)
      item.value.checkOut = new Date(element.checkOut)
      item.value.invoiceAmount = element.invoiceAmount ? element.invoiceAmount : 0
      item.value.roomNumber = +element.roomNumber
      item.value.adults = element.adults
      item.value.children = element.children
      item.value.rateAdult = element.rateAdult
      item.value.rateChild = element.rateChild
      item.value.hotelAmount = element.hotelAmount ? element.hotelAmount : 0
      item.value.remark = element.remark
      item.value.booking = element.booking
      currentBooking.value = element.booking

      return loadingSaveAll.value = false
    }

    try {
      const response = await GenericService.getById(confApi.roomrate.moduleApi, confApi.roomrate.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.roomRateId = response.roomRateId
        item.value.checkIn = new Date(response.checkIn)
        item.value.checkOut = new Date(response.checkOut)
        item.value.invoiceAmount = response.invoiceAmount ? String(response.invoiceAmount) : '0'
        item.value.roomNumber = +response.roomNumber
        item.value.adults = response.adults
        item.value.children = response.children
        item.value.rateAdult = response.rateAdult
        item.value.rateChild = response.rateChild
        item.value.hotelAmount = response.hotelAmount ? String(response.hotelAmount) : '0'
        item.value.remark = response.remark
        item.value.booking = response.booking?.id
        currentBooking.value = response.booking?.id
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

async function createRoomRate(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    await GenericService.create(confApi.roomrate.moduleApi, confApi.roomrate.uriApi, payload)
  }
}

async function updateRoomRate(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }

  await GenericService.update(confApi.roomrate.moduleApi, confApi.roomrate.uriApi, idItem.value || '', payload)
}

async function deleteRoomRate(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(Options.value.moduleApi, Options.value.uriApi, id)
    ClearForm()
    getRoomRateList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveRoomRate(item: { [key: string]: any }) {
  if (!props.isCreationDialog) {
    await props.getInvoiceHotel(props.invoiceObj?.hotel?.id)

    if (props.requiresFlatRate && item.hotelAmount <= 0) {
      return toast.add({ severity: 'error', summary: 'Error', detail: 'The Hotel amount field must be greater than 0 for this hotel', life: 10000 })
    }
  }

  console.log(item)
  loadingSaveAll.value = true
  let successOperation = true
  item.nights = dayjs(item.checkOut).endOf('day').diff(dayjs(item.checkIn).startOf('day'), 'day', false)

  if (idItem.value || item?.id) {
    try {
      // item.booking = currentBooking.value
      if (!item?.id) {
        item.id = idItem.value
      }

      if (props?.isCreationDialog) {
        props.updateItem(item)
      }
      else {
        await updateRoomRate(item)
      }

      props.closeDialog()
    }
    catch (error: any) {
      console.log(error)
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error?.data?.data?.error?.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      item.booking = props.selectedBooking
      if (props?.isCreationDialog) {
        item.id = v4()
        props.addItem(item)
      }
      else {
        await createRoomRate(item)
      }

      props.closeDialog()
    }
    catch (error: any) {
      console.log(error)
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error?.data?.data?.error?.errorMessage || error, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    ClearForm()
    if (!props.isCreationDialog) {
      props.refetchInvoice()
      // getRoomRateList()
    }
  }
}

function requireConfirmationToDeleteRoomRate(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      deleteRoomRate(idItem.value)
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
  getRoomRateList()
}

function OnSortField(event: any) {
  if (event) {
    if (props?.isCreationDialog) {
      return props.sortRoomRate(event)
    }

    if (event.sortField === 'fullName') {
      Payload.value.sortBy = 'booking.firstName'
    }
    else {
      Payload.value.sortBy = event.sortField
    }

    Payload.value.sortType = event.sortOrder
    getRoomRateList()
  }
}

watch(() => props.forceUpdate, () => {
  if (props.forceUpdate) {
    getRoomRateList()
    props.toggleForceUpdate()
  }
})

watch(() => props.listItems, () => {
  if (props.isCreationDialog) {
    totalHotelAmount.value = 0
    totalInvoiceAmount.value = 0
    props?.listItems?.forEach((listItem: any) => {
      totalHotelAmount.value += listItem?.hotelAmount ? Number(listItem?.hotelAmount) : 0
      totalInvoiceAmount.value += listItem?.invoiceAmount ? Number(listItem?.invoiceAmount) : 0
    })
  }
}, { deep: true })

onMounted(() => {
  if (props.selectedInvoice) {
    Payload.value.filter = [{
      key: 'booking.invoice.id',
      operator: 'EQUALS',
      value: props.selectedInvoice,
      logicalOperation: 'AND'
    }]
  }

  if (props.isDetailView) {
    finalColumns.value = [

      { field: 'roomRateId', header: 'Id', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'bookingId', header: 'Booking id', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'fullName', header: 'Full Name', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'checkIn', header: 'Check In', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'checkOut', header: 'Check Out', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'nights', header: 'Nights', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'adults', header: 'Adult', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'children', header: 'Children', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'roomType', header: 'Room Type', type: 'select', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'ratePlan', header: 'Rate Plan', type: 'select', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'hotelAmount', header: 'Hotel Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
      { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

    ]
  }

  if (route.query.type === InvoiceType.CREDIT || props.invoiceObj?.invoiceType?.id === InvoiceType.CREDIT || route.query.type === InvoiceType.INCOME || props.invoiceObj?.invoiceType?.id === InvoiceType.INCOME) {
    menuModel.value = [{ label: 'Add Adjustment', command: () => props.openAdjustmentDialog(selectedRoomRate.value) }]
  }

  if (!props.isCreationDialog) {
    getRoomRateList()
  }
})

watch(() => props.bookingObj, () => {
  item.value = {
    ...item.value,
    checkIn: dayjs(props.bookingObj?.checkIn).toDate(),
    checkOut: dayjs(props.bookingObj?.checkOut).toDate(),
  }
  console.log(item.value)
  console.log(props.bookingObj)
})
</script>

<template>
  <div>
    <DynamicTable
      :data="isCreationDialog ? listItems as any : ListItems" :columns="finalColumns" :options="Options"
      :pagination="Pagination" @on-confirm-create="ClearForm" @open-edit-dialog="OpenEditDialog($event)"
      @on-change-pagination="PayloadOnChangePage = $event" @on-row-right-click="onRowRightClick"
      @on-change-filter="ParseDataTableFilter" @on-list-item="ResetListItems" @on-sort-field="OnSortField"
      @on-row-double-click="($event) => {

        if (route.query.type === InvoiceType.INCOME || props.invoiceObj?.invoiceType?.id === InvoiceType.INCOME) {
          return;
        }
        openEditDialog($event)

      }"
    >
      <template v-if="isCreationDialog" #pagination-total="props">
        <span class="font-bold font">
          {{ listItems?.length }}
        </span>
      </template>
      <template #datatable-footer>
        <ColumnGroup type="footer" class="flex align-items-center">
          <Row>
            <Column
              footer="Totals:" :colspan="!isDetailView ? 9 : 10"
              footer-style="text-align:right; font-weight: 700"
            />
            <Column :footer="totalHotelAmount" footer-style="font-weight: 700" />
            <Column :footer="totalInvoiceAmount" footer-style="font-weight: 700" />
          </Row>
        </ColumnGroup>
      </template>
    </DynamicTable>
  </div>
  <ContextMenu v-if="!isDetailView" ref="roomRateContextMenu" :model="menuModel" />

  <div v-if="isDialogOpen">
    <RoomRateDialog
      :fields="fields" :item="item" :open-dialog="isDialogOpen" :form-reload="formReload"
      :loading-save-all="loadingSaveAll" :clear-form="ClearForm" :require-confirmation-to-save="saveRoomRate"
      :require-confirmation-to-delete="requireConfirmationToDeleteRoomRate"
      :header="!idItem ? 'New Room Rate' : 'Update Room Rate'" :close-dialog="() => {
        ClearForm()
        closeDialog()
      }" container-class="grid pt-3 w-full" class="h-fit p-2 overflow-y-hidden"
      content-class="w-full h-full" :booking-list="bookingList"
    />
  </div>
</template>
