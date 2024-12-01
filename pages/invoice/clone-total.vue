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
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

const bookingEdited = ref(false)
const bookingEdit: any = ref([])
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

const objBookingsTotals = ref<{ totalHotelAmount: number, totalInvoiceAmount: number, totalDueAmount: number }>({
  totalHotelAmount: 0,
  totalInvoiceAmount: 0,
  totalDueAmount: 0
})

const objBookingsTotalsTemp = {
  totalHotelAmount: 0,
  totalInvoiceAmount: 0,
  totalDueAmount: 0
}

const objRoomRateTotals = ref<{ totalHotelAmount: number, totalInvoiceAmount: number }>({
  totalHotelAmount: 0,
  totalInvoiceAmount: 0,
})

const objRoomRateTotalsTemp = {
  totalHotelAmount: 0,
  totalInvoiceAmount: 0,
}

const objAdjustmentTotals = ref<{ totalAmount: number }>({
  totalAmount: 0,
})

const objAdjustmentTotalsTemp = {
  totalAmount: 0,
}

const adjustmentList = ref<any[]>([])
const toast = useToast()
const idItemToLoadFirstTime = ref('')
const selectedInvoicing = ref<any>('')
let globalSelectedInvoicing = ''
const forceUpdate = ref(false)
const active = ref(0)
const route = useRoute()
const transactionTypeList = ref<any[]>([])
const ListItems = ref<any[]>([])
const LocalAttachmentList = ref<any[]>([])
const { data: userData } = useAuth()

const selectedInvoice = ref({})
const selectedBooking = ref<string>('')
const selectedRoomRate = ref<string>('')
const totalAmount = ref(0)
const loadingSaveAll = ref(false)
const loadingAttachmentList = ref(false)
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
    disabled: true,
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

const dueAmount = ref(0)

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

const AttachmentsPayload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10000,
  page: 0,
  sortBy: 'attachmentId',
  sortType: ENUM_SHORT_TYPE.ASC
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

async function getHotelList(query = '', currentTradingCompany: any, currentHotelId: any) {
  try {
    const payload = {
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
        },
        {
          key: 'applyByTradingCompany', // Agregar filtro para applyByTradingCompany
          operator: 'EQUALS',
          value: true, // Solo hoteles donde applyByTradingCompany es true
          logicalOperation: 'AND'
        },
        {
          key: 'manageTradingCompanies.company',
          operator: 'EQUALS',
          value: currentTradingCompany,
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
      if (iterator.id !== currentHotelId) {
        hotelList.value.push({

          isNightType: iterator?.isNightType,
          id: iterator.id,
          name: iterator.name,
          code: iterator.code,
          status: iterator.status,
          fullName: `${iterator.code} - ${iterator.name}`,
          manageTradingCompanies: iterator.manageTradingCompanies
        })
      }
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

  if (invoiceAmountError.value) { return null }
  loadingSaveAll.value = true
  let successOperation = true

  try {
    const response: any = await createClonation(item)
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
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: error?.data?.data?.error?.errorMessage || error?.message,
      life: 10000
    })
  }
  finally {
    loadingSaveAll.value = false
    // if (successOperation) {
    //   await getBookingClonationList()
    //   await getRoomRateClonationList()

    //   await getItem(idItemCreated.value)
    //   calcInvoiceAmount()
    // }

    // await new Promise(resolve => setTimeout(resolve, 5000))
    navigateTo('/invoice')
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
    objRoomRateTotals.value = JSON.parse(JSON.stringify(objRoomRateTotalsTemp))

    for (const iterator of dataList) {
      countRR++

      roomRateList.value = [...roomRateList.value, {
        ...iterator,
        roomRateId: '',
        checkIn: iterator?.checkIn ? new Date(`${dayjs(iterator?.checkIn).format('YYYY-MM-DD')}T00:00:00`) : null,
        checkOut: iterator?.checkOut ? new Date(`${dayjs(iterator?.checkOut).format('YYYY-MM-DD')}T00:00:00`) : null,
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
        objRoomRateTotals.value.totalInvoiceAmount += Number(iterator.invoiceAmount)
      }

      if (typeof +iterator.hotelAmount === 'number') {
        objRoomRateTotals.value.totalHotelAmount += Number(iterator.hotelAmount)
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

    objAdjustmentTotals.value = JSON.parse(JSON.stringify(objAdjustmentTotalsTemp))

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
        objAdjustmentTotals.value.totalAmount += Number(iterator?.amount)
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

async function getParentAttachmentList(invoiceId: string) {
  try {
    loadingAttachmentList.value = true
    AttachmentsPayload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: invoiceId,
      logicalOperation: 'AND'
    }]
    const response = await GenericService.search('invoicing', 'manage-attachment', AttachmentsPayload.value)
    LocalAttachmentList.value = []
    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      LocalAttachmentList.value = [
        ...LocalAttachmentList.value,
        {
          ...iterator,
          attachmentId: '',
          // attachmentId: iterator?.attachmentId ?? '',
          fullName: `${iterator.code} - ${iterator.name}`,
          loadingEdit: false,
          loadingDelete: false,
          type: {
            ...iterator?.type,
            fullName: `${iterator?.type?.code}-${iterator?.type?.name}`
          }
        }
      ]
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    loadingAttachmentList.value = false
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

function compareObjects(obj1, obj2) {
  const differences = {}
  let isEqual = true

  // Obtener las claves de ambos objetos
  const keys1 = Object.keys(obj1)
  const keys2 = Object.keys(obj2)

  // Comprobar si las claves son las mismas
  const allKeys = new Set([...keys1, ...keys2])
  allKeys.forEach((key) => {
    if (!keys1.includes(key)) {
      differences[key] = { fromObj1: undefined, fromObj2: obj2[key] }
      isEqual = false
    }
    else if (!keys2.includes(key)) {
      differences[key] = { fromObj1: obj1[key], fromObj2: undefined }
      isEqual = false
    }
    else if (obj1[key] !== obj2[key]) {
      differences[key] = { fromObj1: obj1[key], fromObj2: obj2[key] }
      isEqual = false
    }
  })

  return { isEqual, differences }
}

async function assignRoomRatesToBookings(bookings: any[], roomRates: any[]) {
  // Crear un diccionario de roomRates agrupados por booking ID
  const roomRatesByBooking = roomRates.reduce((acc, roomRate) => {
    const bookingId = roomRate.booking
    if (!acc[bookingId]) {
      acc[bookingId] = []
    }
    acc[bookingId].push(roomRate)
    return acc
  }, {})

  // Asignar cada grupo de roomRates al booking correspondiente
  return bookings.map(booking => ({
    ...booking,
    roomRates: roomRatesByBooking[booking.id] || []
  }))
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
    /* const attachments = await getAttachments(globalSelectedInvoicing)
    payload.attachments = attachments.map(att => ({
      attachmentId: att.attachmentId,
      filename: att.filename,
      file: att.file,
      remark: att.remark,
      type: att.type.id,
      resourceType: 'INV-Invoice',
      employeeName: userData?.value?.user?.name,
      employeeId: userData?.value?.user?.userId,
      })) */
    if (LocalAttachmentList.value.length > 0) {
      payload.attachments = LocalAttachmentList.value.map(att => ({
        attachmentId: att.attachmentId,
        filename: att.filename,
        file: att.file,
        remark: att.remark,
        type: att.type.id,
        resourceType: 'INV-Invoice',
        employeeName: userData?.value?.user?.name,
        employeeId: userData?.value?.user?.userId,
      }))
    }

    const listBookingsTemp = JSON.parse(JSON.stringify(bookingList.value))
    const bookingsEdit = await filterEditFields(listBookingsTemp) || []

    payload.bookings = bookingsEdit
    // checkIn: item?.checkIn ? formatDate(item?.checkIn) : null,
    //   checkOut: item?.checkOut ? formatDate(item?.checkOut) : null,
    const payloadRoomRate = roomRateList.value.filter(item => item).map(item => ({
      id: item?.id,
      checkIn: typeof item?.checkIn === 'string' ? formatDate(item?.checkIn) : item?.checkIn,
      checkOut: typeof item?.checkOut === 'string' ? formatDate(item?.checkOut) : item?.checkOut,
      invoiceAmount: item?.invoiceAmount,
      roomNumber: item?.roomNumber,
      adults: item?.adults,
      children: item?.children,
      rateAdult: item?.rateAdult,
      rateChild: item?.rateChild,
      hotelAmount: item?.hotelAmount,
      remark: item?.remark,
      nights: item?.nights,
      fullName: item?.fullName,
      firstName: item?.booking?.firstName,
      lastName: item?.booking?.lastName,
      // rateChildren: item?.rateChildren,
      booking: item?.booking?.id,
    }))

    payload.bookings = [...await assignRoomRatesToBookings(payload.bookings, payloadRoomRate)]

    return await GenericService.create(confClonationApi.moduleApi, confClonationApi.uriApi, payload)
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
        // item.value.invoiceDate = new Date(response.invoiceDate)
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
        item.value.status = response.status ? ENUM_INVOICE_STATUS.find((element => element.id === response?.status)) : ENUM_INVOICE_STATUS[0]

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

    objBookingsTotals.value = JSON.parse(JSON.stringify(objBookingsTotalsTemp))

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      // const id = v4()

      bookingList.value = [...bookingList.value, {
        ...iterator,
        bookingId: '',
        loadingEdit: false,
        loadingDelete: false,
        hotelCreationDate: iterator?.hotelCreationDate ? iterator?.hotelCreationDate : new Date(),
        agency: iterator?.invoice?.agency,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        nightType: iterator.nightType ? iterator.nightType : null,
        ratePlan: iterator.ratePlan ? iterator.ratePlan : null,
        roomType: iterator.roomType ? iterator.roomType : null,
        roomCategory: iterator.roomCategory ? iterator.roomCategory : null,
        fullName: `${iterator.firstName ? iterator.firstName : ''} ${iterator.lastName ? iterator.lastName : ''}`
      }]

      // if (typeof +iterator.invoiceAmount === 'number') {
      //   objBookingsTotals.value.totalInvoiceAmount += Number(iterator.invoiceAmount)
      // }
      // if (typeof +iterator.originalAmount === 'number') {
      //   objBookingsTotals.value.totalDueAmount += Number(iterator.dueAmount)
      // }
      // if (typeof +iterator.hotelAmount === 'number') {
      //   objBookingsTotals.value.totalHotelAmount += Number(iterator.hotelAmount)
      // }
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

    // Filtrar solo la información necesaria para cada booking
    const filteredBookings = bookings.map(booking => ({
      id: booking.id,
      reservationNumber: booking.reservationNumber,
      hotelCreationDate: booking.hotelCreationDate,
      bookingDate: booking.bookingDate,
      checkIn: booking.checkIn,
      checkOut: booking.checkOut,
      hotelBookingNumber: booking.hotelBookingNumber,
      fullName: booking.fullName,
      firstName: booking.firstName,
      lastName: booking.lastName,
      roomNumber: booking.roomNumber,
      couponNumber: booking.couponNumber,
      adults: booking.adults,
      children: booking.children,
      hotelAmount: booking.hotelAmount,
      nights: dayjs(booking.checkOut).endOf('day').diff(dayjs(booking.checkIn).startOf('day'), 'day', false),

      nightType: booking.nightType ? booking.nightType.id : null,

      ratePlan: booking.ratePlan ? booking.ratePlan.id : null,
      roomType: booking.roomType ? booking.roomType.id : null,
      roomCategory: booking.roomCategory ? booking.roomCategory.id : null

    }))

    return filteredBookings // Retornar solo los bookings filtrados
  }
  catch (error) {
    console.error('Error al buscar el booking asociado a la factura:', error)
    return [] // Retornar un array vacío en caso de error
  }
}
function formatDate(date: string) {
  if (!date) {
    return ''
  }

  if (date.includes('T')) {
    return `${date.split('T')[0]}T00:00:00.000Z`
  }
  else {
    if (date.includes('T') && !date.includes('Z')) {
      return `${date.split('T')[0]}T00:00:00.000Z`
    }
    else if (!date.includes('T') && date.includes('Z')) {
      return `${date.split('T')[0]}T00:00:00.000Z`
    }
    else if (!date.includes('T') && !date.includes('Z')) {
      return `${date}T00:00:00.000Z`
    }
    else if (date.includes('T') && date.includes('Z')) {
      return `${date.split('T')[0]}T00:00:00.000Z`
    }
  }
}
async function filterEditFields(bookingEdit: any[]) {
  try {
    // Filtrar solo la información necesaria de los bookings editados
    const editFields = bookingEdit.map(booking => ({
      id: booking.id,
      reservationNumber: booking.reservationNumber,
      firstName: booking.firstName,
      lastName: booking.lastName,
      roomNumber: booking.roomNumber,
      rateAdult: booking?.rateAdult,
      rateChild: booking?.rateChild,
      folioNumber: booking?.folioNumber,
      couponNumber: booking.couponNumber,
      adults: booking.adults,
      children: booking.children,
      checkIn: booking.checkIn ? formatDate(booking.checkIn) : null,
      checkOut: booking.checkOut ? formatDate(booking.checkOut) : null,
      fullName: booking.fullName,
      hotelAmount: booking.hotelAmount,
      nights: dayjs(booking.checkOut).endOf('day').diff(dayjs(booking.checkIn).startOf('day'), 'day', false),
      nightType: booking.nightType ? booking.nightType.id : null,
      ratePlan: booking.ratePlan ? booking.ratePlan.id : null,
      roomType: booking.roomType ? booking.roomType.id : null,
      roomCategory: booking.roomCategory ? booking.roomCategory.id : null,
      agency: booking.agency ? booking.agency.id : null,
      hotelCreationDate: booking.hotelCreationDate ? formatDate(booking.hotelCreationDate) : null,
      bookingDate: booking.bookingDate ? formatDate(booking.bookingDate) : null,
      contract: booking?.contract,
      hotelBookingNumber: booking.hotelBookingNumber,
      hotelInvoiceNumber: booking.hotelInvoiceNumber,
      description: booking?.description

    }))

    return editFields
  }
  catch (error) {
    console.error('Error al filtrar los campos editados:', error)
    return []
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
function calcInvoiceAmountUpdate() {
  invoiceAmount.value = 0

  bookingList.value.forEach((b) => {
    invoiceAmount.value = Number(invoiceAmount.value) + Number(b?.invoiceAmount)
    dueAmount.value = Number(invoiceAmount.value) + Number(b?.invoiceAmount)
  })
}

// Cuando se le da al boton salvar del dialogo Editar Booking, esta es la funcion que actualiza el booking
function updateBookingLocal(booking: any) {
  if (booking && booking.id) {
    const bookingToEdit = bookingList.value.find(item => item.id === booking.id)

    if (bookingToEdit) {
      bookingToEdit.description = booking.description
      bookingToEdit.contract = booking.contract
      bookingToEdit.roomNumber = booking.roomNumber
      bookingToEdit.couponNumber = booking.couponNumber
      bookingToEdit.folioNumber = booking.folioNumber
      bookingToEdit.hotelBookingNumber = booking.hotelBookingNumber
      bookingToEdit.hotelInvoiceNumber = booking.hotelInvoiceNumber
      bookingToEdit.firstName = booking.firstName
      bookingToEdit.lastName = booking.lastName
      bookingToEdit.hotelCreationDate = booking.hotelCreationDate
      bookingToEdit.bookingDate = booking.bookingDate
      bookingToEdit.fullName = `${booking.firstName} ${booking.lastName}`

      bookingToEdit.roomType = booking.roomType
      bookingToEdit.nightType = booking.nightType
      bookingToEdit.ratePlan = booking.ratePlan
      bookingToEdit.roomCategory = booking.roomCategory
    }
  }
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
        roomCategory: { ...booking.roomCategory, name: `${booking?.roomCategory?.code || ''}-${booking?.roomCategory?.name || ''}` },
        fullName: `${booking?.firstName ?? ''} ${booking?.lastName ?? ''}`,
        firstName: booking?.firstName,
        rateAdult: booking?.rateAdult,
        rateChild: booking?.rateChild,
        folioNumber: booking?.folioNumber,
        lastName: booking?.lastName,
        hotelAmount: booking?.hotelAmount,
        adults: booking?.adults,
        children: booking?.children,
        hotelCreationDate: booking.hotelCreationDate,
        bookingDate: booking.bookingDate,
        hotelBookingNumber: booking?.hotelBookingNumber,
        hotelInvoiceNumber: booking?.hotelInvoiceNumber,
        contract: booking?.contract,
        description: booking?.description,
      }
    }
  }

  bookingList.value[index] = {
    ...booking,
    checkIn: dayjs(booking?.checkIn).startOf('day').toISOString(),
    checkOut: dayjs(booking?.checkOut).startOf('day').toISOString(),

    // roomType: { id: booking?.roomType?.id }, // Solo el ID
    // ratePlan: { id: booking?.ratePlan?.id }, // Solo el ID
    agency: bookingList.value[index]?.agency,
    dueAmount: booking?.invoiceAmount,
    // agency: { id: bookingList.value[index]?.agency?.id },
  }
  bookingList.value[index].nights = dayjs(booking?.checkOut).endOf('day').diff(dayjs(booking?.checkIn).startOf('day'), 'day', false)

  // Marcar que se ha editado un booking
  bookingEdited.value = true

  // Verificar si el booking ya está en bookingEdit
  const existingEditIndex = bookingEdit.value.findIndex((item: any) => item.id === booking.id)
  if (existingEditIndex === -1) {
    bookingEdit.value.push(booking) // Agregar el booking editado al array solo si no está presente
  }
  else {
    bookingEdit.value[existingEditIndex] = booking // Actualizar el booking existente en el array
  }

  // bookingEdit.value.push(booking); // Agregar el booking editado al array
  // bookingEdit.value = [booking]; //
  calcInvoiceAmountUpdate()

  // Actualizar el total de la tabla de bookingEdit
  objBookingsTotals.value = JSON.parse(JSON.stringify(objBookingsTotalsTemp))
  bookingList.value.forEach((booking: any) => {
    objBookingsTotals.value.totalInvoiceAmount += Number(booking.invoiceAmount)
    objBookingsTotals.value.totalDueAmount += Number(booking.dueAmount)
    objBookingsTotals.value.totalHotelAmount += Number(booking.hotelAmount)
  })
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

// validacion del hotel

//
function onSaveRoomRateInBookingEdit(itemObj: any) {
  if (itemObj && itemObj?.reactiveBookingObj?.id) {
    updateBookingLocal(itemObj?.reactiveBookingObj)
  }

  const formatDate = (date: string) => {
    return date.includes('T') ? date : `${date}T00:00:00.000Z`
  }

  if (itemObj && itemObj.roomRateList && itemObj.roomRateList.length > 0) {
    roomRateList.value.map((obj) => {
      if (obj.id === itemObj?.payload?.id) {
        return {
          ...obj,
          // ...itemObj?.payload,
          checkIn: formatDate(itemObj?.payload?.checkIn),
          checkOut: formatDate(itemObj?.payload?.checkOut),
          nights: dayjs(itemObj?.payload?.checkOut).endOf('day').diff(dayjs(itemObj?.payload?.checkIn).startOf('day'), 'day', false),
        }
      }
      return obj
    })
    // Actualizar el booking

    if (itemObj?.payload?.bookingId) {
      const objBookingSelected = bookingList.value.find((item: any) => item.id === itemObj?.payload?.bookingId)
      if (objBookingSelected) {
        const totalAdults = itemObj.roomRateList.reduce((sum, item: any) => Number(sum) + Number(item.adults), 0)
        const totalChildren = itemObj.roomRateList.reduce((sum, item: any) => Number(sum) + Number(item.children), 0)
        const totalHotelAmount = itemObj.roomRateList.reduce((sum, item: any) => Number(sum) + Number(item.hotelAmount), 0)
        const totalInvoiceAmount = itemObj.roomRateList.reduce((sum, item: any) => Number(sum) + Number(item.invoiceAmount), 0)

        objBookingSelected.children = Number(totalChildren)
        objBookingSelected.adults = Number(totalAdults)
        objBookingSelected.hotelAmount = Number(totalHotelAmount)
        objBookingSelected.invoiceAmount = Number(totalInvoiceAmount)
        objBookingSelected.dueAmount = Number(totalInvoiceAmount)

        objBookingSelected.checkIn = formatDate(itemObj?.payload?.checkIn)
        objBookingSelected.checkOut = formatDate(itemObj?.payload?.checkOut)
        objBookingSelected.nights = dayjs(objBookingSelected?.checkOut).endOf('day').diff(dayjs(objBookingSelected?.checkIn).startOf('day'), 'day', false)
      }
    }

    objRoomRateTotals.value = JSON.parse(JSON.stringify(objRoomRateTotalsTemp))
    for (const iterator of roomRateList.value) {
      if (typeof +iterator.invoiceAmount === 'number') {
        objRoomRateTotals.value.totalInvoiceAmount += Number(iterator.invoiceAmount)
      }

      if (typeof +iterator.hotelAmount === 'number') {
        objRoomRateTotals.value.totalHotelAmount += Number(iterator.hotelAmount)
      }
    }
  }

  if (bookingList.value && bookingList.value.length > 0) {
    objBookingsTotals.value = JSON.parse(JSON.stringify(objBookingsTotalsTemp))
    for (const iterator of bookingList.value) {
      if (typeof +iterator.invoiceAmount === 'number') {
        objBookingsTotals.value.totalInvoiceAmount += Number(iterator.invoiceAmount)
      }

      if (typeof +iterator.dueAmount === 'number') {
        objBookingsTotals.value.totalDueAmount += Number(iterator.dueAmount)
      }

      if (typeof +iterator.hotelAmount === 'number') {
        objBookingsTotals.value.totalHotelAmount += Number(iterator.hotelAmount)
      }
    }
  }
  calcInvoiceAmount()
}

watch(invoiceAmount, () => {
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

// watch(bookingList, () => {
//   if (bookingList.value && bookingList.value.length > 0) {
//     objBookingsTotals.value = JSON.parse(JSON.stringify(objBookingsTotalsTemp))
//     for (const iterator of bookingList.value) {
//       if (typeof +iterator.invoiceAmount === 'number') {
//         objBookingsTotals.value.totalInvoiceAmount += Number(iterator.invoiceAmount)
//       }

//       if (typeof +iterator.dueAmount === 'number') {
//         objBookingsTotals.value.totalDueAmount += Number(iterator.dueAmount)
//       }

//       if (typeof +iterator.hotelAmount === 'number') {
//         objBookingsTotals.value.totalHotelAmount += Number(iterator.hotelAmount)
//       }
//     }
//   }
// })

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  selectedInvoicing.value = route.query.selected
  globalSelectedInvoicing = selectedInvoicing.value
  item.value.invoiceType = ENUM_INVOICE_TYPE.find((element => element.id === route.query.type))

  await getItemById(route.query.selected)
  await getBookingList()
  await getRoomRateList(globalSelectedInvoicing)
  await getAdjustmentList()
  await getParentAttachmentList(globalSelectedInvoicing)
  calcInvoiceAmount()
  // }
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header">
    Clone Complete
  </div>
  <div class="p-4">
    <EditFormV2
      :key="formReload"
      :fields="Fields"
      :item="item"
      :show-actions="true"
      :loading-save="loadingSaveAll"
      :loading-delete="loadingDelete"
      container-class="grid pt-3"
      @cancel="clearForm"
      @delete="requireConfirmationToDelete($event)"
    >
      <template #field-invoiceDate="{ item: data, onUpdate }">
        <Calendar
          v-if="!loadingSaveAll" v-model="data.invoiceDate" date-format="yy-mm-dd" :max-date="new Date()" :disabled="true"
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
          :model="data.agency" :disabled="false" :suggestions="agencyList" @change="($event) => {
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
        <Skeleton v-else height="2rem" class="mb-2" />
        <span v-if="agencyError" class="error-message p-error text-xs">The agency field is required</span>
      </template>
      <template #field-hotel="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
          v-if="!loadingSaveAll" id="autocomplete" field="fullName" item-value="id"
          :disabled="false" :model="data.hotel" :suggestions="hotelList" @change="($event) => {
            const currentHotel = data.hotel

            if (currentHotel && $event) {
              const currentManageTradingCompany = currentHotel.manageTradingCompanies.company;
              console.log(currentManageTradingCompany, 'actual')
              const newManageTradingCompany = $event.manageTradingCompanies.company;
              console.log(newManageTradingCompany, 'nuevo')
              // Verificar si los nombres coinciden
              if (currentManageTradingCompany !== newManageTradingCompany) {
                hotelError = true; // Mostrar error
                console.log(hotelError, 'q hay aqui')

                return; // Salir sin actualizar
              }
              else {
                hotelError = false; // Resetear el error si coincide
              }
            }

            // Si la validación pasa, actualizar el hotel
            onUpdate('hotel', $event);
          }"
          @load="($event) => getHotelList($event, data.hotel.manageTradingCompanies.company, data.hotel.id)"
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
        <Skeleton v-else height="2rem" class="mb-2" />
        <span v-if="hotelError" class="error-message p-error text-xs">
          The hotel does not belong to the same trading company.
        </span>
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
            :requires-flat-rate="requiresFlatRate"
            :get-invoice-hotel="getInvoiceHotel"
            :invoice-obj-amount="invoiceAmount"
            :is-dialog-open="bookingDialogOpen"
            :close-dialog="() => { bookingDialogOpen = false }"
            :open-dialog="handleDialogOpen"
            :selected-booking="selectedBooking"
            :open-adjustment-dialog="openAdjustmentDialog"
            :force-update="forceUpdate"
            :sort-adjustment="sortAdjustment"
            :sort-booking="sortBooking"
            :sort-room-rate="sortRoomRate"
            :toggle-force-update="toggleForceUpdate"
            :room-rate-list="roomRateList"
            :is-creation-dialog="true"
            :selected-invoice="selectedInvoice"
            :booking-list="bookingList"
            :update-booking="updateBookingLocal"
            :adjustment-list="[]"
            :add-adjustment="addAdjustment"
            :update-adjustment="updateAdjustment"
            :active="active"
            :bookings-total-obj="objBookingsTotals"
            :room-rate-total-obj="objRoomRateTotals"
            :adjustment-total-obj="objAdjustmentTotals"
            :set-active="($event) => {
              active = $event
            }"
            @on-save-booking-edit="($event) => {
              updateBookingLocal($event)
            }"
            @on-save-room-rate-in-booking-edit="onSaveRoomRateInBookingEdit"
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
                  :loading="loadingSaveAll || loadingAttachmentList" @click="handleAttachmentDialogOpen()"
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
              //getItemById(idItem)
            }" :is-creation-dialog="idItemCreated === ''" header="Manage Invoice Attachment" :open-dialog="attachmentDialogOpen"
            :selected-invoice="globalSelectedInvoicing" :selected-invoice-obj="item" :list-items="LocalAttachmentList"
            :is-save-in-total-clone="isSaveInTotalClone" @update:list-items="($event) => LocalAttachmentList = [...LocalAttachmentList, $event]"
            @delete-list-items="($id) => LocalAttachmentList = LocalAttachmentList.filter(item => item.id !== $id)"
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
