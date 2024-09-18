<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import { v4 } from 'uuid'
import dayjs from 'dayjs'
import type { Container } from '~/components/form/EditFormV2WithContainer'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'
import InvoiceTotalTabView from '~/components/invoice/InvoiceTabView/InvoiceTotalTabView.vue'

const idItemCreated = ref('')
const Options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment',
  messageToDelete: 'Do you want to save the change?',
  loading: false,
  showFilters: false,
  actionsAsMenu: false,

})
const adjustmentList = ref<any[]>([])
const toast = useToast()
const totalHotelAmount = ref(0)
const totalInvoiceAmount = ref(0)
const idItemToLoadFirstTime = ref('')
const selectedInvoicing = ref<any>('')
let globalSelectedInvoicing = ''
const forceUpdate = ref(false)
const active = ref(0)
const route = useRoute()
const transactionTypeList = ref<any[]>([])
const ListItems = ref<any[]>([])
const { data: userData } = useAuth()

const selectedInvoice = ref({})
const selectedBooking = ref<string>('')
const selectedRoomRate = ref<string>('')
const totalAmount = ref(0)
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const agencyError = ref(false)
const hotelError = ref(false)

const bookingDialogOpen = ref<boolean>(false)
const roomRateDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)
const attachmentHistoryDialogOpen = ref<boolean>(false)
const showAdjustmentDialogFirstTime = ref(false)
const bookingList = ref<any[]>([])
const roomRateList = ref<any[]>([])
const loadedRoomRates = ref<any[]>([])

const attachmentList = ref<any[]>([])

const nightTypeRequired = ref(false)
const requiresFlatRate = ref(false)
const listItems = ref<any[]>([])
const invoiceAmountError = ref(false)
const invoiceAmountErrorMessage = ref('')

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])
const invoiceTypeList = ref<any[]>([])

const isSaveInTotalClone = ref(false)

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confClonationApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/total-clone-invoice',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confinvoiceTypeListtApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-type',
})

const confAttachmentApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-attachment',
})

const confAdjustmentsApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment',
})

const Fields = ref<FieldDefinitionType[]>([

  {
    field: 'invoiceId',
    header: 'ID',
    dataType: 'text',
    class: `field col-12  md:col-3 ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? '' : ''}`,
    disabled: true,

  },
  {
    field: 'invoiceDate',
    header: 'Invoice Date',
    dataType: 'date',

    class: 'field col-12 md:col-3 required ',
    validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-3 mb-5  required',

    disabled: true
  },
  {
    field: 'invoiceType',
    header: 'Invoice Type',
    dataType: 'select',
    class: 'field col-12 md:col-3 mb-5',

    disabled: true
  },

  {
    field: 'invoiceNumber',
    header: 'Invoice Number',
    dataType: 'text',

    class: 'field col-12 md:col-3  ',
    disabled: true,

  },

  {
    field: 'invoiceAmount',
    header: 'Invoice Amount',
    dataType: 'text',

    class: 'field col-12 md:col-3 required  ',
    disabled: true,
    ...(route.query.type === InvoiceType.OLD_CREDIT && { valdation: z.string().refine(val => +val < 0, 'Invoice amount must have negative values') })
  },

  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-3 mb-5   required',

    disabled: true
  },

  {
    field: 'status',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-2 mb-5 ',

    disabled: true
  },
  {
    field: 'isManual',
    header: 'Manual',
    dataType: 'check',
    class: `field col-12 md:col-1 mb-3 flex align-items-center pb-0 ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
    disabled: true
  },

])

function isLargeScreen(): any {
  return window.innerWidth >= 1024 // Considera pantallas de 1024px o más como grandes
}
// VARIABLES -----------------------------------------------------------------------------------------

//
const confirm = useConfirm()
const formReload = ref(0)

const invoiceAmount = ref(0)

const idItem = ref('')
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

  status: route.query.type === InvoiceType.CREDIT ? ENUM_INVOICE_STATUS[5] : ENUM_INVOICE_STATUS[2],
  invoiceType: route.query.type === InvoiceType.OLD_CREDIT ? ENUM_INVOICE_TYPE[0] : ENUM_INVOICE_TYPE.find((element => element.id === route.query.type)),
})

const itemTemp = ref<GenericObject>({
  invoiceId: 0,
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',

  invoiceType: route.query.type === InvoiceType.OLD_CREDIT ? ENUM_INVOICE_TYPE[0] : ENUM_INVOICE_TYPE.find((element => element.id === route.query.type)),
  status: route.query.type === InvoiceType.CREDIT ? ENUM_INVOICE_STATUS[5] : ENUM_INVOICE_STATUS[2]
})

const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
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

const bookingApi = {
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
}
const confRoomApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-room-rate',
})

// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

function handleDialogOpen() {
  bookingDialogOpen.value = true
}

function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

const existsAttachmentTypeInv = computed(() => {
  return attachmentList.value.some(attachment => attachment?.type?.code === 'INV')
})

function handleAttachmentHistoryDialogOpen() {
  attachmentHistoryDialogOpen.value = true
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
      hotelList.value = [...hotelList.value, { isNightType: iterator?.isNightType, id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}` }]
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
      agencyList.value = [...agencyList.value, { client: iterator?.client, id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}` }]
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
/*
async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    payload.invoiceId = item.invoiceId
    payload.invoiceNumber = item.invoiceNumber
    payload.invoiceDate = dayjs(item.invoiceDate).startOf('day').toISOString()
    payload.isManual = item.isManual
    payload.invoiceAmount = 0.00
    payload.hotel = item.hotel?.id
    payload.agency = item.agency?.id
    payload.invoiceType = route.query.type

    if (invoiceAmount.value === 0) {
      throw new Error('The Invoice amount field cannot be 0')
    }

    await getInvoiceAgency(item.agency?.id)
    await getInvoiceHotel(item.hotel?.id)

    const adjustments = []
    const bookings = []
    let roomRates = []
    const attachments = []

    bookingList.value?.forEach((booking) => {
      if (nightTypeRequired.value && !booking.nightType?.id) {
        throw new Error('The Night Type field is required for this client')
      }

      if (requiresFlatRate.value && +booking.hotelAmount <= 0) {
        throw new Error('The Hotel amount field must be greater than 0 for this hotel')
      }

      if (booking?.invoiceAmount !== 0) {
        bookings.push({
          ...booking,
          invoiceAmount: route.query.type === InvoiceType.CREDIT ? toNegative(booking?.invoiceAmount) : booking?.invoiceAmount,
          ratePlan: booking.ratePlan?.id,
          roomCategory: booking.roomCategory?.id,
          roomType: booking.roomType?.id,
          nightType: booking.nightType?.id,
          ...(booking?.invoice?.id && { invoice: booking?.invoice?.id }),
        })

        if (route.query.type === InvoiceType.CREDIT) {
          loadedRoomRates.value.push({
            checkIn: dayjs(booking?.checkIn).startOf('day').toISOString(),
            checkOut: dayjs(booking?.checkOut).startOf('day').toISOString(),
            invoiceAmount: route.query.type === InvoiceType.CREDIT ? toNegative(booking?.invoiceAmount) : booking?.invoiceAmount,
            roomNumber: booking?.roomNumber,
            adults: booking?.adults,
            children: booking?.children,
            rateAdult: booking?.rateAdult,
            rateChild: booking?.rateChild,
            hotelAmount: Number(booking?.hotelAmount),
            remark: booking?.description,
            booking: booking?.id,
            nights: dayjs(booking?.checkOut).diff(dayjs(booking?.checkIn), 'day', false),
            ratePlan: booking?.ratePlan,
            roomType: booking?.roomType,
            fullName: `${booking?.firstName ?? ''} ${booking?.lastName ?? ''}`,
            firstName: booking?.firstName,
            lastName: booking?.lastName,
            id: v4()
          })
        }
      }
    })

    for (let i = 0; i < roomRateList.value.length; i++) {
      if (requiresFlatRate.value && +roomRateList.value[i].hotelAmount <= 0) {
        throw new Error('The Hotel amount field must be greater than 0 for this hotel')
      }
    }

    for (let i = 0; i < adjustmentList?.value.length; i++) {
      adjustments.push({
        ...adjustmentList.value[i],
        transactionType: adjustmentList.value[i].transactionType?.id
      })
    }

    roomRates = []

    if (route.query.type === InvoiceType.CREDIT) {
      roomRates = loadedRoomRates.value
    }
    else {
      roomRates = roomRateList.value
    }

    for (let i = 0; i < attachmentList.value.length; i++) {
      const fileurl: any = await GenericService.getUrlByImage(attachmentList.value[i]?.file)
      attachments.push({
        ...attachmentList.value[i],
        type: attachmentList.value[i]?.type?.id,
        file: fileurl,
      })
    }

    const response = await GenericService.createBulk('invoicing', 'manage-invoice', { bookings, invoice: payload, roomRates, adjustments, attachments, employee: userData?.value?.user?.name })
    return response
  }
}
*/
async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.invoiceId = item.invoiceId
  payload.invoiceNumber = item.invoiceNumber
  payload.invoiceDate = item.invoiceDate
  payload.isManual = item.isManual
  payload.invoiceAmount = item.invoiceAmount
  payload.hotel = item.hotel.id
  payload.agency = item.agency.id
  payload.invoiceType = item.invoiceType?.id
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
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

async function getBookingClonationList() {
  try {
    const Payload: any = ({
      filter: [{

        key: 'invoice.id',
        operator: 'EQUALS',
        value: idItemCreated.value,
        logicalOperation: 'AND'

      }],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    })
    bookingList.value = []

    const response = await GenericService.search(bookingApi.moduleApi, bookingApi.uriApi, Payload)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      //    const id = v4()
      bookingList.value = [...bookingList.value, {
        ...iterator,
        //   id,
        loadingEdit: false,
        loadingDelete: false,
        //  bookingId:'',
        // hotelAmount: 0,
        //  dueAmount:0,
        bookingId: iterator.bookingId,
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
    Options.value.loading = false
  }
}

async function getRoomRateClonationList() {
  try {
    const Payload = {
      filter: [
        {
          key: 'booking.invoice.id',
          operator: 'EQUALS',
          value: idItemCreated.value,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    idItemToLoadFirstTime.value = ''

    roomRateList.value = []

    const response = await GenericService.search(confRoomApi.moduleApi, confRoomApi.uriApi, Payload)

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

      roomRateList.value = [...roomRateList.value, {
        ...iterator,

        // hotelAmount: 0,
        roomRateId: iterator?.roomRateId,
        invoiceAmount: iterator?.invoiceAmount,
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

    if (roomRateList.value.length > 0) {
      idItemToLoadFirstTime.value = roomRateList.value[0].id
    }
  }

  catch (error) {
    console.error(error)
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true

  try {
    const response: any = await createClonation(item)
    console.log('response clonado total', response)
    if (response && response.clonedInvoice) {
      isSaveInTotalClone.value = true
      idItemCreated.value = response.clonedInvoice

      const invoiceNo = response.clonedInvoiceNo
      toast.add({
        severity: 'info',
        summary: 'Confirmed',
        detail: `The clonation invoice ${invoiceNo} was created successfully`,
        life: 10000
      })
    }
  }
  catch (error: any) {
    successOperation = false
    isSaveInTotalClone.value = false
    console.log('Failed to create', error)
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: error?.data?.data?.error?.errorMessage || error?.message,
      life: 10000
    })
  }
  finally {
    loadingSaveAll.value = false
    if (successOperation) {
      // if(itemDetails && itemDetails.clonedInvoice) {
      await getBookingClonationList()
      // console.log(getBookingClonationList(), 'listado del booking clonado')
      await getRoomRateClonationList()
      // console.log(getRoomRateClonationList(), 'listado del roomrate clonado')

      await getItem(idItemCreated.value)
      calcInvoiceAmount()
      // console.log(getItem(idItemCreated.value), 'listado del get desp de clonar')

      // }
    }
    await new Promise(resolve => setTimeout(resolve, 5000))
    // navigateTo('/invoice')
  }
}

async function goToList() {
  isSaveInTotalClone.value = false
  await navigateTo('/invoice')
}

async function getRoomRateList(globalSelectedInvoicing: any) {
  try {
    const Payload = {
      filter: [
        {
          key: 'booking.invoice.id',
          operator: 'EQUALS',
          value: globalSelectedInvoicing,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    idItemToLoadFirstTime.value = ''

    roomRateList.value = []

    const response = await GenericService.search(confRoomApi.moduleApi, confRoomApi.uriApi, Payload)

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

      roomRateList.value = [...roomRateList.value, {
        ...iterator,
        roomRateId: '',
        //   invoiceAmount: iterator?.invoiceAmount || 0,
        // hotelAmount: 0,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        loadingEdit: false,
        loadingDelete: false,
        fullName: `${iterator.booking.firstName ? iterator.booking.firstName : ''} ${iterator.booking.lastName ? iterator.booking.lastName : ''}`,
        // bookingId: iterator.booking.bookingId,
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

    if (roomRateList.value.length > 0) {
      idItemToLoadFirstTime.value = roomRateList.value[0].id
    }

    /*   if (bookingList.value.length === 1) {
      showAdjustmentDialogFirstTime.value = true
    } */
  }
  catch (error) {
    console.error(error)
  }
}

async function getAdjustmentList() {
  try {
    idItemToLoadFirstTime.value = ''
    Options.value.loading = true
    ListItems.value = []
    totalAmount.value = 0

    const payload: any = {
      filter: [
        {
          key: 'roomRate.booking.invoice.id',
          operator: 'EQUALS',
          value: globalSelectedInvoicing,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    const response = await GenericService.search(confAdjustmentsApi.moduleApi, confAdjustmentsApi.uriApi, payload)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      let transaction = { ...iterator?.transaction, name: `${iterator?.transaction?.code || ''}-${iterator?.transaction?.name || ''}` }

      if (iterator?.invoice?.invoiceType === InvoiceType.INCOME) {
        transaction = { ...iterator?.paymentTransactionType, name: `${iterator?.paymentTransactionType?.code || ''}-${iterator?.paymentTransactionType?.name || ''}` }
      }

      ListItems.value = [...ListItems.value, {
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        adjustmentId: '',
        roomRateId: iterator?.roomRate?.roomRateId,
        date: iterator?.date,

      }]

      if (typeof +iterator?.amount === 'number') {
        totalAmount.value += Number(iterator?.amount)
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

function requireConfirmationToSave(item: any) {
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

async function getAttachments(globalSelectedInvoicing: any) {
  try {
    const attachments = await getAttachmentList(globalSelectedInvoicing)
    return attachments
  }
  catch (error) {
    console.error('Error al buscar los attachments asociados a la factura:', error)
    return [] // Devolver una lista de adjuntos vacía en caso de error
  }
}
async function createClonation(item: { [key: string]: any }) {
  try {
    loadingSaveAll.value = true

    // Crear el payload inicial
    const payload: { [key: string]: any } = { ...item }
    delete payload.invoiceId
    delete payload.invoiceNumber
    delete payload.isManual
    delete payload.status
    delete payload.invoiceType
    delete payload.invoiceAmount
    delete payload.invoiceDate

    payload.employeeName = userData?.value?.user?.name
    payload.invoiceToClone = globalSelectedInvoicing // ID del invoice
    payload.agency = item.agency.id // Asegúrate de que item.agency exista
    payload.employeeId = userData?.value?.user?.userId
    payload.hotel = item.hotel.id // Asegúrate de que item.hotel exista
    payload.invoiceDate = dayjs(item.invoiceDate).startOf('day').toISOString()

    // Obtener los attachments asociados al invoice
    const attachments = await getAttachments(globalSelectedInvoicing)
    payload.attachments = attachments.map(att => ({
      attachmentId: att.attachmentId,
      filename: att.filename,
      file: att.file,
      remark: att.remark,
      type: att.type.id,
      resourceType: 'INV-Invoice',
      employeeName: userData?.value?.user?.name,
      employeeId: userData?.value?.user?.userId,
    }))

    // Obtener bookings asociados al invoice
    const bookings = await findBookingByInvoiceId()
    payload.bookings = bookings // Asegurarse de que bookings sea un array

    // Imprimir el payload en la consola
    console.log('Payload:', payload)

    // Llamada al servicio genérico para enviar el payload al servidor
    const response = await GenericService.create(confClonationApi.moduleApi, confClonationApi.uriApi, payload)

    // Imprimir la respuesta en la consola
    console.log('Response:', response)

    return response
  }
  catch (error: any) {
    console.error('Error en createClonation:', error?.data?.data?.error?.errorMessage) // Imprimir el error en la consola
    toast.add({ severity: 'error', summary: 'Error', detail: error?.data?.data?.error?.errorMessage || error?.message, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

function openRoomRateDialog(booking?: any) {
  active.value = 1

  if (booking?.id) {
    selectedBooking.value = booking?.id
  }

  roomRateDialogOpen.value = true
}

function openAdjustmentDialog(roomRate?: any) {
  active.value = 2

  if (roomRate?.id) {
    selectedRoomRate.value = roomRate?.id
  }

  adjustmentDialogOpen.value = true
}

function sortBooking(event: any) {
  if (event) {
    bookingList.value.sort((a, b) => {
      if (event.sortOrder === 'ASC') {
        return a[event.sortField] - b[event.sortField]
      }
      else {
        return b[event.sortField] - a[event.sortField]
      }
    })
  }
}
function sortAdjustment(event: any) {
  if (event) {
    adjustmentList.value.sort((a, b) => {
      if (event.sortOrder === 'ASC') {
        return a[event.sortField] - b[event.sortField]
      }
      else {
        return b[event.sortField] - a[event.sortField]
      }
    })
  }
}
function sortRoomRate(event: any) {
  if (event) {
    roomRateList.value.sort((a, b) => {
      if (event.sortOrder === 'ASC') {
        return a[event.sortField] - b[event.sortField]
      }
      else {
        return b[event.sortField] - a[event.sortField]
      }
    })
  }
}

async function getInvoiceAgency(id) {
  try {
    const agency = await GenericService.getById(confagencyListApi.moduleApi, confagencyListApi.uriApi, id)

    if (agency) {
      nightTypeRequired.value = agency?.client?.isNightType
    }
  }
  catch (err) {

  }
}

async function getInvoiceHotel(id) {
  try {
    const hotel = await GenericService.getById(confhotelListApi.moduleApi, confhotelListApi.uriApi, id)

    if (hotel) {
      requiresFlatRate.value = hotel?.requiresFlatRate
    }
  }
  catch (err) {

  }
}

async function getItemById(id: any) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.id = response.id
        //   item.value.invoiceId = response.invoiceId
        //  const invoiceNumber = `${response?.invoiceNumber?.split('-')[0]}-${response?.invoiceNumber?.split('-')[2]}`

        //  item.value.invoiceNumber = response?.invoiceNumber?.split('-')?.length === 3 ? invoiceNumber : response.invoiceNumber
        item.value.invoiceDate = new Date(response.invoiceDate)
        item.value.isManual = response.isManual
        // item.value.invoiceAmount = 0
        item.value.hotel = response.hotel
        item.value.hotel.fullName = `${response.hotel.code} - ${response.hotel.name}`
        item.value.agency = response.agency
        item.value.agency.fullName = `${response.agency.code} - ${response.agency.name}`
        item.value.invoiceType = response.invoiceType ? ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType)) : ENUM_INVOICE_TYPE[0]
        //  item.value.status = response.status ? ENUM_INVOICE_STATUS.find((element => element.id === response?.status)) : ENUM_INVOICE_STATUS[0]

        await getInvoiceAgency(response.agency?.id)
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

async function getItem(id: any) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.id = response.id
        item.value.invoiceId = response.invoiceId
        console.log('aqui se muestra el invoiceId', item.value.invoiceId)
        // const invoiceNumber = `${response?.clonedInvoiceNo?.split('-')[0]}-${response?.clonedInvoiceNo?.split('-')[2]}`

        item.value.invoiceNumber = response.invoiceNumber
        console.log('aqui se muestra el invoiceNumber', item.value.invoiceNumber)

        item.value.invoiceDate = new Date(response.invoiceDate)
        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        item.value.hotel = response.hotel
        item.value.hotel.fullName = `${response.hotel.code} - ${response.hotel.name}`
        item.value.agency = response.agency
        item.value.agency.fullName = `${response.agency.code} - ${response.agency.name}`
        item.value.invoiceType = response.invoiceType ? ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType)) : ENUM_INVOICE_TYPE[0]
        //  item.value.status = response.status ? ENUM_INVOICE_STATUS.find((element => element.id === response?.status)) : ENUM_INVOICE_STATUS[0]

        //  await getInvoiceAgency(response.agency?.id)
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

async function getBookingList(clearFilter: boolean = false) {
  try {
    const Payload: any = ({
      filter: [{

        key: 'invoice.id',
        operator: 'EQUALS',
        value: route.query.selected,
        logicalOperation: 'AND'

      }],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.ASC
    })
    bookingList.value = []

    const response = await GenericService.search(bookingApi.moduleApi, bookingApi.uriApi, Payload)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      // const id = v4()
      bookingList.value = [...bookingList.value, {
        ...iterator,
        // id,
        bookingId: '',
        loadingEdit: false,
        loadingDelete: false,
        agency: iterator?.invoice?.agency,
        //  invoiceAmount: 0,
        //  originalAmount: iterator?.invoiceAmount,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        nightType: iterator.nightType ? iterator.nightType.id : null,
        ratePlan: iterator.ratePlan ? iterator.ratePlan.id : null,
        roomType: iterator.roomType ? iterator.roomType.id : null,
        roomCategory: iterator.roomCategory ? iterator.roomCategory.id : null,
        fullName: `${iterator.firstName ? iterator.firstName : ''} ${iterator.lastName ? iterator.lastName : ''}`
      }]
    }
    return bookingList.value
  }

  catch (error) {
    console.error(error)
    return []
  }
}
async function findBookingByInvoiceId() {
  try {
    // Obtener el listado completo de bookings
    const bookings = await getBookingList()
    return bookings // Retornar solo los bookings asociados
  }
  catch (error) {
    console.error('Error al buscar el booking asociado a la factura:', error)
    return [] // Retornar un array vacío en caso de error
  }
}

async function getAttachmentList(globalSelectedInvoicing: any) {
  try {
    // Inicializar variables y estados
    idItemToLoadFirstTime.value = ''
    listItems.value = []

    // Construir el payload para la búsqueda
    const payload: any = {
      filter: [
        {
          key: 'invoice.id',
          operator: 'EQUALS',
          value: globalSelectedInvoicing,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    // Realizar la búsqueda utilizando el GenericService
    const response = await GenericService.search(
      confAttachmentApi.moduleApi,
      confAttachmentApi.uriApi,
      payload
    )

    // Extraer los datos de la respuesta
    const { data: dataList, page, size, totalElements, totalPages } = response

    // Actualizar los valores de la paginación
    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    // Procesar y formatear los datos de la lista de adjuntos
    const attachments = dataList.map((item: any) => ({
      ...item,
      type: {
        ...item?.type,
        name: `${item?.type?.code}-${item?.type?.name}`
      },

      resource: globalSelectedInvoicing
    }))

    // Establecer el primer elemento de la lista como el que se cargará inicialmente
    if (attachments.length > 0) {
      idItemToLoadFirstTime.value = attachments[0].id
    }

    // Devolver la lista de adjuntos
    return attachments
  }
  catch (error) {
    console.error(error)
    return [] // Devolver una lista de adjuntos vacía en caso de error
  }
}

function calcBookingInvoiceAmount(roomRate: any) {
  const bookingIndex = bookingList.value.findIndex(b => b?.id === roomRate?.booking)

  bookingList.value[bookingIndex].invoiceAmount = 0

  const roomRates = roomRateList.value.filter((roomRate: any) => roomRate.booking === bookingList.value[bookingIndex]?.id)

  roomRates.forEach((roomRate) => {
    bookingList.value[bookingIndex].invoiceAmount += Number(roomRate.invoiceAmount)
  })
  calcInvoiceAmount()

  // if (bookingIndex > -1) {
  //   bookingList.value[bookingIndex].invoiceAmount = Number(bookingList.value[bookingIndex].invoiceAmount) + Number(roomRate.invoiceAmount)

  // }
}

function calcBookingHotelAmount(roomRate: any) {
  const bookingIndex = bookingList.value.findIndex(b => b?.id === roomRate?.booking)

  bookingList.value[bookingIndex].hotelAmount = 0

  const roomRates = roomRateList.value.filter((roomRate: any) => roomRate.booking === bookingList.value[bookingIndex]?.id)

  roomRates.forEach((roomRate) => {
    bookingList.value[bookingIndex].hotelAmount += Number(roomRate.hotelAmount)
  })
}
function calcRoomRateInvoiceAmount(newAdjustment: any) {
  const roomRateIndex = roomRateList.value.findIndex(rr => rr?.id === newAdjustment?.roomRate)
  const adjustmentIndex = adjustmentList.value.findIndex(rr => rr?.id === newAdjustment?.id)

  if (adjustmentIndex > -1) {
    roomRateList.value[roomRateIndex].invoiceAmount = Number(roomRateList.value[roomRateIndex].invoiceAmount) - Number(adjustmentList.value[adjustmentIndex]?.amount)
  }

  if (roomRateIndex > -1) {
    roomRateList.value[roomRateIndex].invoiceAmount = Number(roomRateList.value[roomRateIndex].invoiceAmount) + Number(newAdjustment.amount)
    calcBookingInvoiceAmount(roomRateList.value[roomRateIndex])
  }
}

function calcInvoiceAmount() {
  invoiceAmount.value = 0

  bookingList.value.forEach((b) => {
    invoiceAmount.value = Number(invoiceAmount.value) + Number(b?.invoiceAmount)
  })
}

function updateBooking(booking: any) {
  const index = bookingList.value.findIndex(item => item.id === booking.id)

  for (let i = 0; i < roomRateList.value.length; i++) {
    const element = roomRateList.value[i]

    if (element?.booking === booking?.id) {
      roomRateList.value[i] = {
        ...element,
        roomType: { ...booking.roomType, name: `${booking?.roomType?.code || ''}-${booking?.roomType?.name || ''}` },
        nightType: { ...booking.nightType, name: `${booking?.nightType?.code || ''}-${booking?.nightType?.name || ''}` },
        ratePlan: { ...booking.ratePlan, name: `${booking?.ratePlan?.code || ''}-${booking?.ratePlan?.name || ''}` },
        fullName: `${booking?.firstName ?? ''} ${booking?.lastName ?? ''}`,
        firstName: booking?.firstName,
        lastName: booking?.lastName,
        hotelAmount: booking?.hotelAmount,

      }
    }
  }

  bookingList.value[index] = {
    ...booking,
    checkIn: dayjs(booking?.checkIn).startOf('day').toISOString(),
    checkOut: dayjs(booking?.checkOut).startOf('day').toISOString(),
    roomType: { ...booking?.roomType, name: `${booking?.roomType?.code || ''}-${booking?.roomType?.name || ''}` },
    ratePlan: { ...booking?.ratePlan, name: `${booking?.ratePlan?.code || ''}-${booking?.ratePlan?.name || ''}` },
  }
  bookingList.value[index].nights = dayjs(booking?.checkOut).endOf('day').diff(dayjs(booking?.checkIn).startOf('day'), 'day', false)

  calcInvoiceAmount()
}

function disabledButtonSave() {
  let result = false
  if (adjustmentList.value.length === 0 || attachmentList.value.length === 0) {
    result = true
  }
  else {
    const listIds = roomRateList.value.map((roomRate: any) => roomRate.id)
    if (listIds.length === 0) {
      result = true
    }
    else {
      // recorrer la lista de ids y ver si todos los ids existen en la lista de adjustments y la propiedad a verificar se llama roomRate
      for (let i = 0; i < listIds.length; i++) {
        if (!adjustmentList.value.some((adjustment: any) => adjustment.roomRate === listIds[i])) {
          result = true
          break
        }
      }
    }
  }
  return result
}

function addAdjustment(adjustment: any) {
  calcRoomRateInvoiceAmount(adjustment)
  adjustmentList.value = [...adjustmentList.value, { ...adjustment, transaction: { ...adjustment?.transactionType, name: `${adjustment?.transactionType?.code || ''}-${adjustment?.transactionType?.name || ''}` }, date: dayjs(adjustment?.date).startOf('day').toISOString() }]
}

function updateAdjustment(adjustment: any) {
  const index = adjustmentList.value.findIndex(item => item.id === adjustment.id)
  calcRoomRateInvoiceAmount(adjustment)
  adjustmentList.value[index] = { ...adjustment, roomRate: adjustmentList.value[index]?.roomRate, transaction: { ...adjustment?.transactionType, name: `${adjustment?.transactionType?.code || ''}-${adjustment?.transactionType?.name || ''}` }, date: dayjs(adjustment?.date).startOf('day').toISOString() }
}

function addAttachment(attachment: any) {
  attachmentList.value = [...attachmentList.value, attachment]
}

function updateAttachment(attachment: any) {
  const index = attachmentList.value.findIndex(item => item.id === attachment.id)
  attachmentList.value[index] = attachment
}

watch(invoiceAmount, () => {
  console.log(item)

  invoiceAmountError.value = false

  if (route.query.type === InvoiceType.INVOICE) {
    if (+invoiceAmount.value <= 0) {
      invoiceAmountError.value = true
      invoiceAmountErrorMessage.value = 'The invoice amount field must be greater than 0'
    }
  }

  if (-invoiceAmount.value > +item.value.originalAmount) {
    invoiceAmountError.value = true
    invoiceAmountErrorMessage.value = 'New value must be less or equal than original amount'
  }
})

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  selectedInvoicing.value = route.query.selected
  globalSelectedInvoicing = selectedInvoicing.value
  item.value.invoiceType = ENUM_INVOICE_TYPE.find((element => element.id === route.query.type))

  await getItemById(route.query.selected)
  await getBookingList()
  await getRoomRateList(globalSelectedInvoicing)
  await getAdjustmentList()
  // calcInvoiceAmount()
  // }
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header">
    Clone Complete
  </div>
  <div class="p-4">
    <EditFormV2
      :key="formReload" :fields="Fields" :item="item" :show-actions="true" :loading-save="loadingSaveAll"
      :loading-delete="loadingDelete" container-class="grid pt-3" @cancel="clearForm"
      @delete="requireConfirmationToDelete($event)"
    >
      <template #field-invoiceDate="{ item: data, onUpdate }">
        <Calendar
          v-if="!loadingSaveAll" v-model="data.invoiceDate" date-format="yy-mm-dd" :max-date="new Date()"
          @update:model-value="($event) => {
            onUpdate('invoiceDate', $event)
          }"
        />
      </template>

      <template #field-invoiceAmount="{ onUpdate, item: data }">
        <InputText
          v-model="invoiceAmount" show-clear :disabled="true" @update:model-value="($event) => {
            invoiceAmountError = false
            onUpdate('invoiceAmount', $event)
          }"
        />
        <span v-if="invoiceAmountError" class="error-message p-error text-xs">{{ invoiceAmountErrorMessage }}</span>
      </template>
      <template #field-invoiceType="{ item: data, onUpdate }">
        <Dropdown
          v-if="!loadingSaveAll" v-model="data.invoiceType" :options="[...ENUM_INVOICE_TYPE]"
          option-label="name" return-object="false" show-clear disabled @update:model-value="($event) => {
            onUpdate('invoiceType', $event)
          }"
        >
          <template #option="props">
            {{ props.option?.code }}-{{ props.option?.name }}
          </template>
          <template #value="props">
            {{ props.value?.code }}-{{ props.value?.name }}
          </template>
        </Dropdown>
        <Skeleton v-else height="2rem" class="mb-2" />
      </template>
      <template #field-agency="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
          v-if="!loadingSaveAll" id="autocomplete" field="fullName" item-value="id"
          :model="data.agency" :disabled="String(route.query.type) as any === InvoiceType.CREDIT"
          :suggestions="agencyList" @change="($event) => {
            agencyError = false
            onUpdate('agency', $event)
          }" @load="($event) => getAgencyList($event)"
        >
          <template #option="props">
            <span>{{ props.item.fullName }}</span>
          </template>
          <template #chip="{ value }">
            <div>
              {{ value?.fullName }}
            </div>
          </template>
        </DebouncedAutoCompleteComponent>
        <span v-if="agencyError" class="error-message p-error text-xs">The agency field is required</span>
      </template>
      <template #field-hotel="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
          v-if="!loadingSaveAll" id="autocomplete" field="fullName" item-value="id"
          :disabled="invoiceStatus !== InvoiceStatus.PROCECSED" :model="data.hotel" :suggestions="hotelList" @change="($event) => {
            onUpdate('hotel', $event)
          }" @load="($event) => getHotelList($event)"
        >
          <template #option="props">
            <span>{{ props.item.fullName }}</span>
          </template>
          <template #chip="{ value }">
            <div>
              {{ value?.fullName }}
            </div>
          </template>
        </DebouncedAutoCompleteComponent>
      </template>
      <template #field-status="{ item: data, onUpdate }">
        <Dropdown
          v-if="!loadingSaveAll" v-model="data.status" :options="[...ENUM_INVOICE_STATUS]" option-label="name"
          return-object="false" show-clear disabled @update:model-value="($event) => {
            onUpdate('status', $event)
          }"
        >
          <template #option="props">
            {{ props.option?.code }}-{{ props.option?.name }}
          </template>
          <template #value="props">
            {{ props.value?.code }}-{{ props.value?.name }}
          </template>
        </Dropdown>
        <Skeleton v-else height="2rem" class="mb-2" />
      </template>

      <template #form-footer="props">
        <div style="width: 100%; height: 100%;">
          <InvoiceTotalTabView
            :requires-flat-rate="requiresFlatRate" :get-invoice-hotel="getInvoiceHotel"
            :invoice-obj-amount="invoiceAmount" :is-dialog-open="bookingDialogOpen"
            :close-dialog="() => { bookingDialogOpen = false }" :open-dialog="handleDialogOpen"
            :selected-booking="selectedBooking" :open-adjustment-dialog="openAdjustmentDialog"
            :force-update="forceUpdate" :sort-adjustment="sortAdjustment" :sort-booking="sortBooking"
            :sort-room-rate="sortRoomRate" :toggle-force-update="toggleForceUpdate" :room-rate-list="roomRateList"
            :is-creation-dialog="true" :selected-invoice="selectedInvoice as any" :booking-list="bookingList"
            :update-booking="updateBooking" :adjustment-list="ListItems" :add-adjustment="addAdjustment"
            :update-adjustment="updateAdjustment" :active="active" :set-active="($event) => {

              active = $event
            }"
          />

          <div>
            <div class="flex justify-content-end">
              <IfCan :perms="['INVOICE-MANAGEMENT:CREATE']">
                <Button
                  v-tooltip.top="'Save'" class="w-3rem mx-1" icon="pi pi-save" :loading="loadingSaveAll"
                  :disabled="false" @click="() => {

                    saveItem(props.item.fieldValues)
                  }"
                />
              </IfCan>

              <IfCan :perms="['INVOICE-MANAGEMENT:SHOW-BTN-ATTACHMENT']">
                <Button
                  v-tooltip.top="'Add Attachment'" class="w-3rem mx-1" icon="pi pi-paperclip"
                  :loading="loadingSaveAll" @click="handleAttachmentDialogOpen()"
                />
              </IfCan>
              <Button
                v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times"
                @click="goToList"
              />
            </div>
          </div>
        </div>
        <div v-if="attachmentDialogOpen">
          <AttachmentDialogTotal
            :close-dialog="() => {
              attachmentDialogOpen = false;
              getItemById(idItem)
            }"
            :is-creation-dialog="false"
            header="Manage Invoice Attachment"
            :open-dialog="attachmentDialogOpen"
            :selected-invoice="globalSelectedInvoicing"
            :selected-invoice-obj="item"
            :is-save-in-total-clone="isSaveInTotalClone"
          />
        </div>
      </template>
    </EditFormV2>
  </div>
</template>

<style scoped lang="scss">
.required {
  position: relative;
}

.required-indicator {
  color: rgba(199, 17, 17, 0.863);
  font-size: 16px;
  position: absolute;
  top: 0;
}
</style>
