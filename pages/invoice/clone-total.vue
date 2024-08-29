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
import InvoiceTotalTabView from '~/components/invoice/InvoiceTabView/InvoiceTabView.vue'

const toast = useToast()
const openAdjustment=ref(true)
const forceUpdate = ref(false)
const active = ref(0)
const route = useRoute()
const transactionTypeList = ref<any[]>([])

const { data: userData } = useAuth()

const selectedInvoice = ref({})
const selectedBooking = ref<string>('')
const selectedRoomRate = ref<string>('')

const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const agencyError = ref(false)
const hotelError = ref(false)

const bookingDialogOpen = ref<boolean>(false)
const roomRateDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)
const attachmentHistoryDialogOpen = ref<boolean>(false)

const bookingList = ref<any[]>([])
const roomRateList = ref<any[]>([])
const loadedRoomRates = ref<any[]>([])
const adjustmentList = ref<any[]>([])
const attachmentList = ref<any[]>([])


const nightTypeRequired = ref(false)
const requiresFlatRate = ref(false)

const invoiceAmountError = ref(false)
const invoiceAmountErrorMessage = ref('')

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])
const invoiceTypeList = ref<any[]>([])

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

const CreditFields = ref<FieldDefinitionType[]>([
  {
    field: 'invoiceId',
    header: 'From Invoice',
    dataType: 'text',
    class: `field col-12 md:col-4  ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? '' : ''}`,
    disabled: true,

  },
  {
    field: 'invoiceType',
    header: 'Invoice Type',
    dataType: 'select',
    class: 'field col-12 md:col-4 ',
    containerFieldClass: '',
    disabled: true
  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-4 required',
    disabled: String(route.query.type) as any === InvoiceType.CREDIT

  },

  {
    field: 'invoiceNumber',
    header: 'Invoice Number',
    dataType: 'text',
    class: 'field col-12 md:col-4 ',
    disabled: true,

  },

  {
    field: 'originalAmount',
    header: 'Original Amount',
    dataType: 'text',
    class: 'field col-12 md:col-4  required',
    disabled: true,

  },
  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-4 required',
    disabled: String(route.query.type) as any === InvoiceType.CREDIT
  },
  {
    field: 'invoiceDate',
    header: 'Invoice Date',
    dataType: 'date',
    class: 'field col-12 md:col-4  required',
    validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
  {
    field: 'invoiceAmount',
    header: 'Invoice Amount',
    dataType: 'text',
    class: 'field col-12 md:col-4  required',

    ...(route.query.type === InvoiceType.OLD_CREDIT && { valdation: z.string().refine(val => +val < 0, 'Invoice amount must have negative values') })
  },
  {
    field: 'status',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-2',
    containerFieldClass: '',
    disabled: true
  },
  {
    field: 'isManual',
    header: 'Manual',
    dataType: 'check',
    class: `field col-12 md:col-1  flex align-items-end pb-2   ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
    disabled: true
  },

])

const Fields = ref<FieldDefinitionType[]>([

  {
    field: 'invoiceId',
    header: 'ID:',
    dataType: 'text',
    class: `field col-12  md:col-2 ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? '' : ''}`,
    disabled: true,
    
  },
  {
    field: 'invoiceDate',
    header: 'Invoice Date:',
    dataType: 'date',
  
    class: 'field col-12 md:col-2 required ',
    validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
 
  {
    field: 'isManual',
    header: 'Manual:',
    dataType: 'check',
    class: `field col-12 md:col-1  flex align-items-center pb-0 ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
    disabled: true
  },
  {
    field: 'invoiceAmount',
    header: 'Invoice Amount:',
    dataType: 'text',
 
    class: 'field col-12 md:col-2 required  ',
    disabled: true,
    ...(route.query.type === InvoiceType.OLD_CREDIT && { valdation: z.string().refine(val => +val < 0, 'Invoice amount must have negative values') })
  },
  {
    field: 'invoiceType',
    header: 'Invoice Type:',
    dataType: 'select',
    class: 'field col-12 md:col-2  mb-5',
 
    disabled: true
  },
  {
    field: 'agency',
    header: 'Agency:',
    dataType: 'select',
    class: 'field col-12 md:col-3 mb-5   required',
   
    disabled: true
  },
 
  {
    field: 'invoiceNumber',
    header: 'Invoice Number:',
    dataType: 'text',
    
    class: 'field col-12 md:col-2  ',
    disabled: true,

  },
  {
    field: 'dueDate',
    header: 'Due Date:',
    dataType: 'date',
  
    class: 'field col-12 md:col-2 required ',
    validation: z.date({ required_error: 'The Due Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Due Date field cannot be greater than current date')
  },
  {
    field: 'reSend',
    header: 'Re-Send:',
    dataType: 'check',
    class: `field col-12 md:col-1 mb-3    flex align-items-center pb-0 ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
    disabled: false
  },
  {
    field: 'reSenDate',
    header: 'Re-Send Date:',
    dataType: 'date',
  
    class: 'field col-12 md:col-2   required',
    validation: z.date({ required_error: 'The Due Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Due Date field cannot be greater than current date')
  },
  {
    field: 'hotel',
    header: 'Hotel:',
    dataType: 'select',
    class: 'field col-12 md:col-2 mb-5  required',
 
    disabled: true
  },
  {
    field: 'status',
    header: 'Status:',
    dataType: 'select',
    class: 'field col-12 md:col-3 mb-5  required',
 
    disabled: true
  },
 
])

function isLargeScreen():any {
  return window.innerWidth >= 1024; // Considera pantallas de 1024px o más como grandes
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
  invoiceId: 0,
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
const transactionTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-transaction-type',
  keyValue: 'name'
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

function handleDialogOpen() {
  bookingDialogOpen.value = true
}

function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

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

async function saveItem(item: { [key: string]: any }) {
  if (!item?.hotel?.id) {
    hotelError.value = true
  }
  if (!item?.agency?.id) {
    agencyError.value = true
  }

  if (hotelError.value || agencyError.value) {
    return null
  }

  if (invoiceAmountError.value) { return null }
  loadingSaveAll.value = true
  let successOperation = true

  try {
    const response: any = await createItem(item)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: `The invoice ${`${response?.invoiceNo?.split('-')[0]}-${response?.invoiceNo?.split('-')[2]}`} was created successfully`, life: 10000 })
    if (route.query.type === InvoiceType.CREDIT) { return navigateTo({ path: `/invoice` }) }
    navigateTo({ path: `/invoice/edit/${response?.id}` })
  }
  catch (error: any) {
    successOperation = false
    toast.add({ severity: 'error', summary: 'Error', detail: error?.data?.data?.error?.errorMessage || error?.message, life: 10000 })
  }

  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
  }
}
const goToList = async () => await navigateTo('/invoice')


async function getTransactionTypeList(query = '') {
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

    transactionTypeList.value = []
    const response = await GenericService.search(transactionTypeApi.moduleApi, transactionTypeApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      transactionTypeList.value = [...transactionTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, fullName: `${iterator?.code}-${iterator?.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
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

function addBooking(booking: any) {
  bookingList.value = [...bookingList.value, {
    ...booking,
    checkIn: dayjs(booking?.checkIn).startOf('day').toISOString(),
    checkOut: dayjs(booking?.checkOut).startOf('day').toISOString(),
    nights: dayjs(booking?.checkOut).endOf('day').diff(dayjs(booking?.checkIn).startOf('day'), 'day', false),
    roomType: { ...booking?.roomType, name: `${booking?.roomType?.code || ''}-${booking?.roomType?.name || ''}` },
    ratePlan: { ...booking?.ratePlan, name: `${booking?.ratePlan?.code || ''}-${booking?.ratePlan?.name || ''}` },
  }]

  console.log(booking)
  roomRateList.value = [...roomRateList.value, {
    checkIn: dayjs(booking?.checkIn).toISOString(),
    checkOut: dayjs(booking?.checkOut).toISOString(),
    invoiceAmount: String(booking?.invoiceAmount),
    roomNumber: booking?.roomNumber,
    adults: booking?.adults,
    children: booking?.children,
    rateAdult: booking?.rateAdult,
    rateChild: booking?.rateChild,
    hotelAmount: String(booking?.hotelAmount),
    remark: booking?.description,
    booking: booking?.id,
    nights: dayjs(booking?.checkOut).diff(dayjs(booking?.checkIn), 'day', false),
    roomType: { ...booking.roomType, name: `${booking?.roomType?.code || ''}-${booking?.roomType?.name || ''}` },
    nightType: { ...booking.nightType, name: `${booking?.nightType?.code || ''}-${booking?.nightType?.name || ''}` },
    ratePlan: { ...booking.ratePlan, name: `${booking?.ratePlan?.code || ''}-${booking?.ratePlan?.name || ''}` },
    fullName: `${booking?.firstName ?? ''} ${booking?.lastName ?? ''}`,
    firstName: booking?.firstName,
    lastName: booking?.lastName,
    id: v4()
  }]

  console.log(roomRateList)

  calcInvoiceAmount()
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
        item.value.invoiceId = response.invoiceId
        const invoiceNumber = `${response?.invoiceNumber?.split('-')[0]}-${response?.invoiceNumber?.split('-')[2]}`

        item.value.invoiceNumber = response?.invoiceNumber?.split('-')?.length === 3 ? invoiceNumber : response.invoiceNumber
        item.value.invoiceDate = new Date(response.invoiceDate)
        item.value.isManual = response.isManual
        item.value.invoiceAmount = toNegative(response.invoiceAmount)
        item.value.hotel = response.hotel
        item.value.hotel.fullName = `${response.hotel.code} - ${response.hotel.name}`
        item.value.agency = response.agency
        item.value.agency.fullName = `${response.agency.code} - ${response.agency.name}`
        item.value.invoiceType = response.invoiceType ? ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType)) : ENUM_INVOICE_TYPE[0]
        item.value.status = response.status ? ENUM_INVOICE_STATUS.find((element => element.id === response?.status)) : ENUM_INVOICE_STATUS[0]

        if (route.query.type === InvoiceType.CREDIT) {
          item.value.originalAmount = response.invoiceAmount
          item.value.invoiceDate = new Date()
        }
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
      const id = v4()
      bookingList.value = [...bookingList.value, {
        ...iterator,
        id,
        bookingId: '',
        loadingEdit: false,
        loadingDelete: false,
        agency: iterator?.invoice?.agency,
        invoiceAmount: 0,
        originalAmount: iterator?.invoiceAmount,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        fullName: `${iterator.firstName ? iterator.firstName : ''} ${iterator.lastName ? iterator.lastName : ''}`
      }]
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    // Options.value.loading = false
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

function addRoomRate(rate: any) {
  const booking = bookingList.value.find((b => b?.id === rate?.booking))

  roomRateList.value = [...roomRateList.value, {
    ...rate,
    nights: dayjs(rate?.checkOut).endOf('day').diff(dayjs(rate?.checkIn).startOf('day'), 'day', false),
    roomType: { ...booking.roomType, name: `${booking?.roomType?.code || ''}-${booking?.roomType?.name || ''}` },
    nightType: { ...booking.nightType, name: `${booking?.nightType?.code || ''}-${booking?.nightType?.name || ''}` },
    ratePlan: { ...booking.ratePlan, name: `${booking?.ratePlan?.code || ''}-${booking?.ratePlan?.name || ''}` },
    fullName: `${booking?.firstName ?? ''} ${booking?.lastName ?? ''}`,
    checkIn: dayjs(rate?.checkIn).startOf('day').toISOString(),
    checkOut: dayjs(rate?.checkOut).startOf('day').toISOString(),
    adults: booking?.adults,
    children: booking?.children,
    rateChild: booking?.rateChild,
    rateAdult: booking?.rateAdult


  }]
  calcBookingInvoiceAmount(rate)
  calcBookingHotelAmount(rate)
}

function updateRoomRate(roomRate: any) {
  console.log(roomRate)
  const index = roomRateList.value.findIndex(item => item.id === roomRate.id)

  const booking = bookingList.value.find((b => b?.id === roomRateList.value[index]?.booking))

  roomRateList.value[index] = {
    ...roomRate,
    booking: roomRateList.value[index]?.booking,
    roomType: { ...booking.roomType, name: `${booking?.roomType?.code || ''}-${booking?.roomType?.name || ''}` },
    nightType: { ...booking.nightType, name: `${booking?.nightType?.code || ''}-${booking?.nightType?.name || ''}` },
    ratePlan: { ...booking.ratePlan, name: `${booking?.ratePlan?.code || ''}-${booking?.ratePlan?.name || ''}` },
    fullName: `${booking?.firstName ?? ''} ${booking?.lastName ?? ''}`,
    firstName: booking?.firstName,
    lastName: booking?.lastName,
    checkIn: dayjs(booking?.checkIn).startOf('day').toISOString(),
    checkOut: dayjs(booking?.checkOut).startOf('day').toISOString(),
  }
  calcBookingHotelAmount(roomRate)
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

  item.value.invoiceType = ENUM_INVOICE_TYPE.find((element => element.id === route.query.type))

  if (route.query.type === InvoiceType.CREDIT && route.query.selected) {
    await getItemById(route.query.selected)
    await getBookingList()
    calcInvoiceAmount()
  }
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header">
   Clone Complete
  </div>
  <div class="p-4">
    <EditFormV2
      :key="formReload" :fields="route.query.type === InvoiceType.CREDIT ? CreditFields : Fields" :item="item"
      :show-actions="true" :loading-save="loadingSaveAll" :loading-delete="loadingDelete" container-class="grid pt-3"
      @cancel="clearForm" @delete="requireConfirmationToDelete($event)"
    >
      <template #field-invoiceDate="{ item: data, onUpdate }">
        <Calendar
          v-if="!loadingSaveAll" v-model="data.invoiceDate" date-format="yy-mm-dd" :max-date="new Date()"
          @update:model-value="($event) => {
            onUpdate('invoiceDate', $event)
          }"
        />
      </template>
      <template #field-dueDate="{ item: data, onUpdate }">
        <Calendar
          v-if="!loadingSaveAll" v-model="data.dueDate" date-format="yy-mm-dd" :max-date="new Date()"
          @update:model-value="($event) => {
            onUpdate('dueDate', $event)
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
          :model="data.hotel" :disabled="String(route.query.type) as any === InvoiceType.CREDIT"
          :suggestions="hotelList" @change="($event) => {
            hotelError = false
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
        <span v-if="hotelError" class="error-message p-error text-xs">The hotel field is required</span>
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
            :add-room-rate="addRoomRate" :update-room-rate="updateRoomRate" :is-creation-dialog="true"
            :selected-invoice="selectedInvoice as any" :booking-list="bookingList" :add-booking="addBooking"
            :update-booking="updateBooking" :adjustment-list="adjustmentList" :add-adjustment="addAdjustment"
            :update-adjustment="updateAdjustment" :active="active" :set-active="($event) => {

              active = $event
            }"
          />
         
   
          <div>
            <div class="flex justify-content-end">
              <IfCan :perms="['INVOICE-MANAGEMENT:CREATE']">
                <Button
                  v-tooltip.top="'Save'" class="w-3rem mx-1" icon="pi pi-save" :loading="loadingSaveAll"
                  :disabled="bookingList.length === 0 || attachmentList.length === 0" @click="() => {
                    saveItem(props.item.fieldValues)
                  }"
                />
              </IfCan>

             

              <IfCan :perms="['INVOICE-MANAGEMENT:SHOW-BTN-ATTACHMENT']">
                <Button
                  v-tooltip.top="'Add Attachment'" class="w-3rem mx-1" icon="pi pi-paperclip"
                  :loading="loadingSaveAll"  @click="handleAttachmentDialogOpen()"
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
            :add-item="addAttachment" :close-dialog="() => { attachmentDialogOpen = false }"
            :is-creation-dialog="true" header="Manage Invoice Attachment" :list-items="attachmentList"
            :open-dialog="attachmentDialogOpen" :update-item="updateAttachment" selected-invoice=""
            :selected-invoice-obj="{}"
          />
        </div>
        <div v-if="openAdjustment">
          <Dialog
      v-model:visible="openAdjustment"
      :id-item="idItem"
    
      modal
      class="mx-2 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
    :style="{width:'33%'}"
      :pt="{
        root: {
          class: 'custom-dialog',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5 rem',
        },
      
      }"
      @hide="openAdjustment = false"
    >
      <template #header>
        <div class="flex justify-content-between">
          <p class="mt-2 mb-0 text-lg">
           New Adjustment
          </p>
        </div>
      </template>
      <template #default>
      
    <form class="flex flex-column justify-content-center mx-4" >
      <div class="mt-3">
        <label for="amount" class="block font-bold mb-1  required">Amount <span class="required-indicator ml-1"> * </span></label>
    
           <InputText
          id="amount"
          v-model="item.amount"
          type="text"
       class="w-full"
       
          required
        />
      
      </div>
      <div class="mt-1">
        <label for="amount" class="block font-bold mb-1 required">Date <span class="required-indicator ml-1"> * </span></label>
        <Calendar 
  id="amount"
  
  v-model="item.date"
  date-format="yy-mm-dd"
  :max-date="dayjs().endOf('day').toDate()"
  :default-date="dayjs().startOf('day').toDate()"
  class="w-full"

  required
> </Calendar>
      
      </div>
      <div class="mt-1">
        <label for="amount" class="block font-bold mb-1  required">Transaction Type <span class="required-indicator ml-1"> * </span></label>
    
        <DebouncedAutoCompleteComponent
        class="w-full"
            v-if="!loadingSaveAll" id="autocomplete"  field="fullName" item-value="id"
            :model="transactionType" :suggestions="transactionTypeList" @change="($event) => {
              onUpdate('transactionType', $event)
            }" @load="($event) => getTransactionTypeList($event)"
          />
      </div>
      <div class="mt-2">
        <label for="amount" class="block font-bold mb-1  required">Remark </label>
    
           <InputText
          id="amount"
          v-model="item.amount"
          type="text"
        class="w-full"
         
       
          required
        />
      
      </div>
      <div class="field flex justify-content-end mt-4 mb-2">
        <Button  v-tooltip.top="'Save'"  label="Save" type="submit"  class="p-button-primary mr-2 w-6rem mx-1" icon="pi pi-save"/>
        <Button  v-tooltip.top="'Cancel'" label="Cancel" severity="secondary" class=" mr-2 w-6rem mx-1" @click="openAdjustment = false" icon="pi pi-times" />
      </div>
    
    </form>
 
  </template>
    </Dialog>
             
        
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