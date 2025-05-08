<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import dayjs from 'dayjs'

import { GenericService } from '~/services/generic-services'

import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'
import InvoicePartialTabView from '~/components/invoice/InvoiceTabView/InvoicePartialTabView.vue'
import type { IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

const showAdjustmentDialogFirstTime = ref(false)

const totalInvoiceAmount = ref(0)
const idItemToLoadFirstTime = ref('')
const totalHotelAmount = ref(0)
const toast = useToast()
const forceUpdate = ref(false)
const active = ref(0)
const route = useRoute()

const { data: userData } = useAuth()

const selectedBooking = ref<string>('')
const selectedRoomRate = ref<string>('')
const parentInvoiceId = ref<any>('')
const selectedInvoicing = ref<any>('')
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
let globalSelectedInvoicing = ''
const bookingDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)

const bookingList = ref<any[]>([])
const roomRateList = ref<any[]>([])
const adjustmentList = ref<any[]>([])

const attachmentList = ref<any[]>([])

const confClonationPartialApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/partial-clone',
})

const requiresFlatRate = ref(false)

const invoiceAmountError = ref(false)

const invoiceAmountErrorMessage = ref('')

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confRoomApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-room-rate',
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
    class: `field col-12 md:col-1  flex align-items-end pb-1   ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
    disabled: true
  },

])

const Fields = ref<FieldDefinitionType[]>([
  {
    field: 'invoiceId',
    header: 'ID',
    dataType: 'text',
    class: `field col-12 md:col-3  ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? '' : ''}`,
    disabled: true,
  },
  {
    field: 'invoiceDate',
    header: 'Invoice Date',
    dataType: 'date',
    class: 'field col-12 md:col-3 required',
    validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
  {
    field: 'invoiceType',
    header: 'Invoice Type',
    dataType: 'select',
    class: 'field col-12 md:col-3 mb-5',
    containerFieldClass: '',
    disabled: true
  },
  {
    field: 'status',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-3 mb-5',
    containerFieldClass: '',
    disabled: true
  },

  {
    field: 'invoiceNumber',
    header: 'Invoice Number',
    dataType: 'text',
    class: 'field col-12 md:col-3 ',
    disabled: true,

  },

  {
    field: 'invoiceAmount',
    header: 'Invoice Amount',
    dataType: 'text',
    class: 'field col-12 md:col-3  required',
    disabled: true,
    ...(route.query.type === InvoiceType.OLD_CREDIT && { valdation: z.string().refine(val => +val < 0, 'Invoice amount must have negative values') })
  },
  {
    field: 'isManual',
    header: 'Manual',
    dataType: 'check',
    class: `field col-12 md:col-1  flex align-items-center pb-0 ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
    disabled: true
  },

])

// tablas

// VARIABLES -----------------------------------------------------------------------------------------

//
const confirm = useConfirm()
const formReload = ref(0)

const invoiceAmount = ref(0)

const idItem = ref('')
const idItemCreated = ref('')
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
  invoiceId: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  invoiceType: route.query.type === InvoiceType.OLD_CREDIT ? ENUM_INVOICE_TYPE[0] : ENUM_INVOICE_TYPE.find((element => element.id === route.query.type)),
  status: route.query.type === InvoiceType.CREDIT ? ENUM_INVOICE_STATUS[5] : ENUM_INVOICE_STATUS[2]
})

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 1000,
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

const paginationForBookingList = ref<IPagination>({
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

// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

function handleDialogOpen() {
  bookingDialogOpen.value = true
}

function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

// metodo de clonacion de factura parcial

async function getBookingList() {
  try {
    payload.value.filter = []
    payload.value.filter = [{

      key: 'invoice.id',
      operator: 'EQUALS',
      value: globalSelectedInvoicing,
      logicalOperation: 'AND'

    }]
    bookingList.value = []
    const response = await GenericService.search(bookingApi.moduleApi, bookingApi.uriApi, payload.value)
    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationForBookingList.value.page = page
    paginationForBookingList.value.limit = size
    paginationForBookingList.value.totalElements = totalElements
    paginationForBookingList.value.totalPages = totalPages

    for (const iterator of dataList) {
      //    const id = v4()
      iterator.hotelAmount = 0
      bookingList.value = [...bookingList.value, {
        ...iterator,
        //   id,
        loadingEdit: false,
        loadingDelete: false,
        bookingId: '',
        hotelAmount: 0,
        dueAmount: 0,
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
        hotelAmount: 0,
        //  dueAmount:0,
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
    // Options.value.loading = false
  }
}

async function getRoomRateClonationList(idItemCreated: any) {
  try {
    const Payload = {
      filter: [
        {
          key: 'booking.invoice.id',
          operator: 'EQUALS',
          value: idItemCreated,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 1000,
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

    totalInvoiceAmount.value = 0
    totalHotelAmount.value = 0

    for (const iterator of dataList) {
      roomRateList.value = [...roomRateList.value, {
        ...iterator,
        hotelAmount: 0,
        invoiceAmount: iterator?.invoiceAmount || 0,
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
      pageSize: 1000,
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

    totalInvoiceAmount.value = 0
    totalHotelAmount.value = 0

    for (const iterator of dataList) {
      roomRateList.value = [...roomRateList.value, {
        ...iterator,
        roomRateId: '',
        //   invoiceAmount: iterator?.invoiceAmount || 0,
        hotelAmount: 0,
        invoiceAmount: 0,
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

    if (bookingList.value.length === 1) {
      showAdjustmentDialogFirstTime.value = true
    }
  }
  catch (error) {
    console.error(error)
  }
}

// Variante 1
async function createPartialClonation(item: { [key: string]: any }) {
  try {
    loadingSaveAll.value = true

    const payload: { [key: string]: any } = { ...item }
    payload.employee = userData?.value?.user?.userId
    payload.invoice = globalSelectedInvoicing

    const adjustments: any = [...adjustmentList.value]
    const attachments = []

    // Agregar los attachments localmente al payload
    for (let i = 0; i < attachmentList.value.length; i++) {
      const fileurl: any = await GenericService.getUrlByImage(attachmentList.value[i]?.file.files[0])
      attachments.push({
        ...attachmentList.value[i],
        type: attachmentList.value[i]?.type?.id,
        id: attachmentList.value[i]?.id,
        resourceType: 'INV-Invoice',
        resource: globalSelectedInvoicing,
        file: fileurl,
        paymentResourceType: attachmentList.value[i]?.resourceType?.id || ''
      })
    }

    // Asignar los attachments al payload
    payload.attachments = attachments

    // Inicializar roomRateAdjustments como un array vacío
    payload.roomRateAdjustments = []

    if (adjustments && adjustments.length > 0) {
      for (let i = 0; i < adjustments.length; i++) {
        const adjustment = adjustments[i]

        payload.roomRateAdjustments.push({
          roomRate: adjustment.roomRate,
          adjustment: {
            id: adjustment.id,
            amount: adjustment.amount,
            transactionType: adjustment.transaction.id,
            description: adjustment.description,
            date: adjustment.date,
            employee: adjustment.employee
          }
        })
      }
    }

    // Llamada al servicio genérico para enviar el payload al servidor
    const response = await GenericService.create(confClonationPartialApi.moduleApi, confClonationPartialApi.uriApi, payload)

    return response
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error?.data?.data?.error?.errorMessage || error?.message, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
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

// Funcion para el cloando total
function disabledButtonSave() {
  if (adjustmentList.value.length === 0) {
    return true
  }

  const bookingIds = [...new Set(roomRateList.value.map(roomRate => roomRate.bookingId))]

  // Caso 4: Para más de un booking con más de un room rate, al menos uno de los room rates de cada booking debe tener un ajuste (OK)
  if (bookingIds.length > 1) {
    for (const bookingId of bookingIds) {
      const roomRatesForBooking = roomRateList.value.filter(roomRate => roomRate.bookingId === bookingId)
      const atLeastOneRateWithAdjustment = roomRatesForBooking.some(roomRate => adjustmentList.value.some(adjustment => adjustment.roomRate === roomRate.id))

      // Si al menos un room rate para un booking tiene un ajuste, continuar con los demás bookings
      if (atLeastOneRateWithAdjustment) {
        continue
      }
      else {
        return true // Si no se encontró ningún room rate con ajuste para un booking, deshabilitar el botón
      }
    }

    return false // Si al menos un room rate con ajuste fue encontrado para cada booking, habilitar el botón
  }
  // caso 3 mas de un booking con un roomrate cada uno (ok)
  if (bookingIds.length > 1) {
    for (const bookingId of bookingIds) {
      const roomRatesForBooking = roomRateList.value.filter(roomRate => roomRate.bookingId === bookingId)
      const roomRatesWithAdjustments = roomRatesForBooking.filter(roomRate => adjustmentList.value.some(adjustment => adjustment.roomRate === roomRate.id))

      // Si el número de room rates con ajustes es diferente del número total de room rates para un booking, deshabilitar botón
      if (roomRatesWithAdjustments.length !== roomRatesForBooking.length) {
        return true
      }
    }
  }

  return false
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let itemDetails = null

  try {
    const response: any = await createPartialClonation(item)

    if (response && response.cloned) {
      // Llamar a getItemById para actualizar los campos invoiceId e invoiceNo
      idItemCreated.value = response.cloned
      itemDetails = await getItemById(response.cloned)

      // Actualizar los campos del item con los detalles obtenidos
      const invoiceNo = itemDetails.invoiceNumber
      toast.add({
        severity: 'info',
        summary: 'Confirmed',
        detail: `The clonation invoice ${invoiceNo} was created successfully`,
        life: 5000
      })
      setTimeout(() => {
        window.close()
      }, 1500)
    }
  }
  catch (error: any) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: error?.data?.data?.error?.errorMessage || error?.message,
      life: 10000
    })
  }
}
async function goToList() {
  if (window.opener) {
    window.close()
  }
  else {
    await navigateTo('/invoice')
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
        item.value.invoiceNo = response.invoiceNo
        item.value.invoiceId = response.invoiceId
        parentInvoiceId.value = response.invoiceId
        const invoiceNumber = `${response?.invoiceNumber?.split('-')[0]}-${response?.invoiceNumber?.split('-')[2]}`

        item.value.invoiceNumber = response?.invoiceNumber?.split('-')?.length === 3 ? invoiceNumber : response.invoiceNumber
        item.value.invoiceDate = new Date(response.invoiceDate)
        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        item.value.hasAttachments = response.hasAttachments
        invoiceAmount.value = response.invoiceAmount

        item.value.invoiceType = response.invoiceType ? ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType)) : ENUM_INVOICE_TYPE[0]
        item.value.status = response.status ? ENUM_INVOICE_STATUS.find((element => element.id === response?.status)) : ENUM_INVOICE_STATUS[0]

        if (route.query.type === InvoiceType.CREDIT) {
          item.value.originalAmount = response.invoiceAmount
          item.value.invoiceDate = new Date()
        }
      }

      formReload.value += 1
      return response
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

function calcBookingInvoiceAmount(roomRate: any) {
  const bookingIndex = bookingList.value.findIndex(b => b?.id === roomRate?.booking?.id)

  bookingList.value[bookingIndex].invoiceAmount = 0

  const roomRates = roomRateList.value.filter((roomRate: any) => roomRate.booking === bookingList.value[bookingIndex]?.id)

  roomRates.forEach((roomRate) => {
    bookingList.value[bookingIndex].invoiceAmount += Number(roomRate.invoiceAmount)
  })
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

function calcInvoiceAmountByAdjustment() {
  invoiceAmount.value = 0

  adjustmentList.value.forEach((b) => {
    invoiceAmount.value = Number(invoiceAmount.value) + Number(b?.amount)
  })
}

function calcInvoiceAmountInBookingByRoomRate() {
  bookingList.value.forEach((b) => {
    const roomRates = roomRateList.value.find((roomRate: any) => roomRate.booking?.id === b?.id)
    if (roomRates) {
      b.invoiceAmount = Number(roomRates.invoiceAmount) || 0
      b.dueAmount = Number(roomRates.invoiceAmount) || 0
    }
  })
}

function updateRoomRate(roomRate: any) {
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
  adjustmentList.value = [
    ...adjustmentList.value,
    {
      ...adjustment,
      transaction: {
        ...adjustment?.transactionType,
        name: `${adjustment?.transactionType?.code || ''}-${adjustment?.transactionType?.name || ''}`
      },
      date: dayjs(adjustment?.date).startOf('day').toISOString(),

    }
  ]
  calcInvoiceAmountByAdjustment()
  calcInvoiceAmountInBookingByRoomRate()
}

function updateAdjustment(adjustment: any) {
  const index = adjustmentList.value.findIndex(item => item.id === adjustment.id)
  calcRoomRateInvoiceAmount(adjustment)
  adjustmentList.value[index] = { ...adjustment, roomRate: adjustmentList.value[index]?.roomRate, transaction: { ...adjustment?.transactionType, name: `${adjustment?.transactionType?.code || ''}-${adjustment?.transactionType?.name || ''}` }, date: dayjs(adjustment?.date).startOf('day').toISOString() }
}

function addAttachment(attachment: any) {
  const isDuplicate = attachmentList.value.some(
    item => JSON.stringify(item) === JSON.stringify(attachment)
  )

  if (!isDuplicate) {
    attachmentList.value = [...attachmentList.value, attachment]
  }
}

function deleteAttachment(id: string) {
  const newList = attachmentList.value.filter(attachment => attachment?.id !== id)

  attachmentList.value = newList
}

function updateAttachment(attachment: any) {
  const index = attachmentList.value.findIndex(item => item.id === attachment.id)
  attachmentList.value[index] = attachment
}

function onChangePage($event: any) {
  payload.value.page = $event?.page ? $event?.page : 0
  payload.value.pageSize = $event?.rows ? $event.rows : 1000
  getBookingList()
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

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  selectedInvoicing.value = route.query.selected
  globalSelectedInvoicing = selectedInvoicing.value
  parentInvoiceId.value = route.query.invoiceId || ''
  item.value.invoiceType = ENUM_INVOICE_TYPE.find((element => element.id === route.query.type))

  // if (route.query.type === InvoiceType.CREDIT && route.query.selected) {

  /* if (route.query.type === InvoiceType.INVOICE && route.query.selected) {
    await getItemById(route.query.selected); // Llamar a getItemById después de listar y crear la factura
    calcInvoiceAmount()
  } */

  if (route.query.type === InvoiceType.INVOICE && route.query.selected) {
    await getBookingList()
    await getRoomRateList(globalSelectedInvoicing)
    //   calcInvoiceAmount()
    calcInvoiceAmountByAdjustment()
    calcInvoiceAmountInBookingByRoomRate()
    //  await getItemById(route.query.selected)

    // }
  }
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header flex justify-content-start">
    <div v-if="!parentInvoiceId">
      Partial Clone
    </div>
    <div v-else>
      Partial Clone From Invoice {{ parentInvoiceId }}
    </div>
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
      <template #field-invoiceAmount="{ onUpdate, item: data }">
        <InputNumber
          v-model="invoiceAmount" :min-fraction-digits="2"
          :max-fraction-digits="4" show-clear :disabled="true" @update:model-value="($event) => {
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
          <InvoicePartialTabView
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
            :update-room-rate="updateRoomRate"
            :is-creation-dialog="idItemCreated === ''"
            :invoice-obj="item"
            :selected-invoice="idItemCreated"
            :booking-list="bookingList"
            :booking-pagination="paginationForBookingList"
            :adjustment-list="adjustmentList"
            :add-adjustment="addAdjustment"
            :update-adjustment="updateAdjustment"
            :active="active"
            :set-active="($event) => {
              active = $event
            }" :open-adjustment-dialog-first-time="showAdjustmentDialogFirstTime"
            @on-change-page="onChangePage"
          />

          <div>
            <div class="flex justify-content-end mt-2">
              <Button
                v-tooltip.top="'Save'" class="w-3rem mx-1" icon="pi pi-save" :loading="loadingSaveAll"
                :disabled="disabledButtonSave()" @click="() => {
                  saveItem(props.item.fieldValues)
                }"
              />

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
          <AttachmentDialogClone
            :add-item="addAttachment"
            :close-dialog="() => { attachmentDialogOpen = false; getItemById(idItem); }"
            :is-creation-dialog="idItemCreated === ''"
            header="Manage Invoice Attachment"
            :list-items="attachmentList"
            :open-dialog="attachmentDialogOpen"
            :update-item="updateAttachment"
            :selected-invoice="globalSelectedInvoicing"
            :selected-invoice-obj="item"
            :delete-item="deleteAttachment"
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
