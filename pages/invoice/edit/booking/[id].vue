<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import getUrlByImage from '~/composables/files'
import { ModulesService } from '~/services/modules-services'
import { GenericService } from '~/services/generic-services'
import type { PageState } from 'primevue/paginator'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType, Container } from '~/components/form/EditFormV2WithContainer'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'
import dayjs from 'dayjs'
import AttachmentDialog from '~/components/invoice/attachment/AttachmentDialog.vue'
import AttachmentHistoryDialog from '~/components/invoice/attachment/AttachmentHistoryDialog.vue'



const toast = useToast()
const { data: userData } = useAuth()

const forceSave = ref(false)
const forceUpdate = ref(false)
const active = ref(0)

const route = useRoute()

//@ts-ignore
const selectedInvoice = <string>ref(route.params.id.toString())
const nightTypeRequired = ref(route.query?.nightTypeRequired as string)
const selectedBooking = ref<string>('')
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



// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------


function handleDialogOpen() {
  console.log(active);
  switch (active.value) {
    case 0:
      bookingDialogOpen.value = true
      break;

    case 1:
      roomRateDialogOpen.value = true
      break;

    case 2:
      adjustmentDialogOpen.value = true
      break;

    default:
      break;
  }

  console.log(bookingDialogOpen);
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

async function getInvoiceTypeList() {
  try {
    const payload
      = {
      filter: [],
      query: '',
      pageSize: 200,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confinvoiceTypeListtApi.moduleApi, confinvoiceTypeListtApi.uriApi, payload)
    const { data: dataList } = response
    invoiceTypeList.value = []
    for (const iterator of dataList) {
      invoiceTypeList.value = [...invoiceTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

function refetchInvoice() {
  getInvoiceAmountById(route.params.id as string)
  update()
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
    finally {

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
        item.value.invoiceNumber = item.value.invoiceNumber.replace("OLD", "CRE")


        item.value.invoiceDate = dayjs(response.invoiceDate).format("YYYY-MM-DD")
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

// const nightTypeRequired = ref(false)

async function updateItem(item: { [key: string]: any }) {




  loadingSaveAll.value = true
  const payload: { [key: string]: any } = {}
  payload.employee = userData?.value?.user?.name
  payload.invoiceDate = dayjs(item.invoiceDate).startOf('day').toISOString()
  payload.isManual = item.isManual
  payload.hotel = item.hotel.id
  payload.agency = item.agency.id
  payload.dueDate = item?.dueDate
  payload.reSend = item.reSend
  payload.reSendDate = item.reSendDate

  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
  navigateTo(
    '/invoice'
  )
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
      await updateItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      await createItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
  }
}
const goToList = async () => await navigateTo('/invoice')

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

function update() {
  forceUpdate.value = true

  setTimeout(() => {

    forceUpdate.value = false
  }, 100);
}


function openAdjustmentDialog(roomRate?: any) {
  active.value = 2

  if (roomRate?.id) {
    selectedRoomRate.value = roomRate?.id
  }

  adjustmentDialogOpen.value = true
}


async function getInvoiceHotel(id) {
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
async function getInvoiceAgency(id) {
  try {

    console.log(id);
    const agency = await GenericService.getById(confagencyListApi.moduleApi, confagencyListApi.uriApi, id)

    if (agency) {
      invoiceAgency.value = { ...agency }

      nightTypeRequired.value = agency?.client?.isNightType

      console.log(agency);
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


const fieldsV2: Array<FieldDefinitionType> = [
  // Booking Id

  {
    field: 'bookingId',
    header: 'Booking Id',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    disabled: true,
  },
  // Invoice Original Amount
  {
    field: 'invoiceOriginalAmount',
    header: 'Invoice Original Amount',
    dataType: 'text',
    class: 'field col-12 md:col-3 required',
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
      route.query.type === InvoiceType.OLD_CREDIT || 
      route.query.type === InvoiceType.CREDIT ? 
      { 
        validation: 
        z.string()
        .min(0, 'The Invoice Amount field is required')
        .refine((value: any) => !isNaN(value) && +value < 0, { message: 'The Invoice Amount field must be negative' }) 
      } : 
      { 
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
    class: `field col-12 md:col-3 ${nightTypeRequired.value ? 'required' : ''}`,
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
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
  },
  
  //  Contract
  {
    field: 'contract',
    header: 'Contract',
    dataType: 'text',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
    //validation: z.string().regex(/^[a-z0-9]+$/i, 'No se permiten caracteres especiales').nullable()
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


const ratePlanList = ref<any[]>([])
const roomCategoryList = ref<any[]>([])
const roomTypeList = ref<any[]>([])
const nightTypeList = ref<any[]>([])
const activeTab = ref(0)
  const confApi = reactive({
  booking: {
    moduleApi: 'invoicing',
    uriApi: 'manage-booking',
  },
  // roomRate: {
  //   moduleApi: 'invoicing',
  //   uriApi: 'manage-room-rate',
  // },
  // adjustment: {
  //   moduleApi: 'invoicing',
  //   uriApi: 'manage-adjustment',
  // },
  // invoice: {
  //   moduleApi: 'invoicing',
  //   uriApi: 'manage-invoice',
  // },
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
      console.log("response", response);
      
      if (response) {
        idItem.value = response?.id
        item2.value.id = response.id
        item2.value.bookingId = response.bookingId
        item2.value.hotelCreationDate = new Date(response.hotelCreationDate)
        item2.value.bookingDate = response.bookingDate ? new Date(response.bookingDate) : ''
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
        item2.value.ratePlan = response.ratePlan?.name == '-' ? null : response.ratePlan
        item2.value.nightType = response.nightType
        item2.value.roomType = response.roomType?.name == '-' ? null : response.roomType
        item2.value.roomCategory = response.roomCategory
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
    await getBookingItemById(route.params.id as string);
  }


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
        @submit="requireConfirmationToSave($event)"
      >
        <template #field-hotelCreationDate="{ item: data, onUpdate,  }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.hotelCreationDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('hotelCreationDate', $event)
            }"
          />
        </template>
        <template #field-invoiceAmount="{ onUpdate, item: data }">
          <InputText
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
        </template>
        <template #field-hotelAmount="{ onUpdate, item: data, fields, field }">
          <InputText
            v-model="data.hotelAmount"
            show-clear 
            :disabled="fields.find((f) => f.field === field)?.disabled"
            @update:model-value="onUpdate('hotelAmount', $event)"
          />
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
        </template>
        <template #form-footer="props">
          <div class="grid w-full p-0 m-0">
            <div class="col-12 mb-5 justify-content-center align-center">
              <div style="width: 100%; height: 100%;">
                <InvoiceTabView 
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
                />
              </div>
            </div>
            <div class="col-12 flex justify-content-end">
              <Button
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

      <EditFormV2 
        v-if="false"
        :key="formReload" 
        :fields="Fields" 
        :item="item" 
        :show-actions="true" 
        :loading-save="loadingSaveAll"  
        :loading-delete="loadingDelete" 
        @cancel="clearForm" 
        @delete="requireConfirmationToDelete($event)"
        :force-save="forceSave" 
        @force-save="forceSave = $event" 
        container-class="grid pt-3"
      >
        <template #field-invoiceDate="{ item: data, onUpdate }">
          <Calendar 
            v-if="!loadingSaveAll" 
            v-model="data.invoiceDate" 
            date-format="yy-mm-dd" 
            :max-date="new Date()" 
            :disabled="invoiceStatus !== InvoiceStatus.PROCECSED"
            @update:model-value="($event) => {
              onUpdate('invoiceDate', $event)
            }" />
        </template>

        <template #field-invoiceAmount="{ onUpdate, item: data }">
          <InputText 
            v-if="!loadingSaveAll"
            v-model="invoiceAmount" 
            :disabled="true" 
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
            }" @load="async ($event) => {
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
            }" />
            <Skeleton v-else height="2rem" class="mb-2" />
        </template>

        <template #field-status="{ item: data, onUpdate }">
          <Dropdown 
            v-if="!loadingSaveAll" 
            v-model="data.status" 
            :options="[ENUM_INVOICE_STATUS[0], ENUM_INVOICE_STATUS[2]]" 
            option-label="name"
            return-object="false" 
            show-clear 
            :disabled="data?.status?.id !== InvoiceStatus.PROCECSED" 
            @update:model-value="($event) => {
              onUpdate('status', $event)
            }">
            <template #option="props">
              {{ props.option?.code }}-{{ props.option?.name }}
            </template>
            <template #value="props">
              {{ props.value?.code }}-{{ props.value?.name }}
            </template>
          </Dropdown>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>

        <template #field-invoiceType="{ item: data, onUpdate }">
          <Dropdown v-if="!loadingSaveAll" v-model="data.invoiceType" :options="[...ENUM_INVOICE_TYPE]"
            option-label="name" return-object="false" show-clear disabled @update:model-value="($event) => {
        onUpdate('invoiceType', $event)
      }">
            <template #option="props">
              {{ props.option?.code }}-{{ props.option?.name }}
            </template>
            <template #value="props">
              {{ props.value?.code }}-{{ props.value?.name }}
            </template>
          </Dropdown>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-hotel="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete" field="fullName" item-value="id" :disabled="invoiceStatus !== InvoiceStatus.PROCECSED"
            :model="data.hotel" :suggestions="hotelList" @change="($event) => {
        onUpdate('hotel', $event)
      }" @load="($event) => getHotelList($event)">
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
        </template>
        <template #field-agency="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete" field="fullName" item-value="id" :disabled="invoiceStatus !== InvoiceStatus.PROCECSED"
            :model="data.agency" :suggestions="agencyList" @change="($event) => {
        onUpdate('agency', $event)
      }" @load="($event) => getAgencyList($event)">
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
        </template>

        <template #form-footer="props">
          <div style="width: 100%; height: 100%;">
            <InvoiceTabView 
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
              :invoice-obj="item" 
              :refetch-invoice="refetchInvoice"
              :is-creation-dialog="false" 
              :selected-invoice="selectedInvoice as any" 
              :active="active"
              :set-active="($event) => { active = $event }" 
              :showTotals="true"
              :night-type-required="nightTypeRequired" 
            />
            <div>
              <div class="flex justify-content-end">
                <IfCan :perms="['INVOICE-MANAGEMENT:EDIT']">
                  <Button v-tooltip.top="'Save'" class="w-3rem mx-1" icon="pi pi-save" :disabled="invoiceStatus !== InvoiceStatus.PROCECSED" :loading="loadingSaveAll" @click="() => {
                      saveItem(props.item.fieldValues)
                    }" />
                </IfCan>

              
                <Button v-tooltip.top="'Print'" class="w-3rem mx-1" icon="pi pi-print" :loading="loadingSaveAll"
                  @click="() => {
                    exportAttachmentsDialogOpen = true
                  }" />
           

                <IfCan :perms="['INVOICE-MANAGEMENT:SHOW-BTN-ATTACHMENT']">
                  <Button v-tooltip.top="'Add Attachment'" class="w-3rem mx-1" icon="pi pi-paperclip"
                    :loading="loadingSaveAll" @click="handleAttachmentDialogOpen()"  />
                  </IfCan>
                  <IfCan :perms="['INVOICE-MANAGEMENT:BOOKING-SHOW-HISTORY']"> 
                    <Button v-tooltip.top="'Show History'" class="w-3rem mx-1" :loading="loadingSaveAll"
                      @click="handleAttachmentHistoryDialogOpen()" :disabled="!item?.hasAttachments">
                      <template #icon>
                        <span class="flex align-items-center justify-content-center p-0">
                          <svg xmlns="http://www.w3.org/2000/svg" height="15px" viewBox="0 -960 960 960" width="15px"
                            fill="#e8eaed">
                            <path
                              d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z" />
                          </svg>
                        </span>
                      </template>
                    </Button>
                  </IfCan>

                <IfCan :perms="['INVOICE-MANAGEMENT:BOOKING-CREATE']">
                  <Button v-if="active === 0" v-tooltip.top="'Add Booking'" class="w-3rem mx-1" icon="pi pi-plus"
                    :loading="loadingSaveAll" @click="handleDialogOpen()" :disabled="item?.invoiceType?.id === InvoiceType.INCOME || invoiceStatus !== InvoiceStatus.PROCECSED" />
                </IfCan>
                
                <Button v-tooltip.top="'Import'" v-if="item?.invoiceType?.id === InvoiceType.INCOME" class="w-3rem ml-1"
                  disabled icon="pi pi-file-import"  />

                <Button v-tooltip.top="'Update'" class="w-3rem mx-1" icon="pi pi-replay" :loading="loadingSaveAll"
                  @click="update" />
                <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times"
                  @click="goToList" />
              </div>
            </div>
          </div>
        </template>
      </EditFormV2>
    </div>

    <div v-if="attachmentDialogOpen">
      <AttachmentDialog :close-dialog="() => { attachmentDialogOpen = false; getItemById(idItem) }"
        :is-creation-dialog="false" header="Manage Invoice Attachment" :open-dialog="attachmentDialogOpen"
        :selected-invoice="selectedInvoice" :selected-invoice-obj="item" />
    </div>
  </div>
  <div v-if="attachmentHistoryDialogOpen">
    <AttachmentHistoryDialog selected-attachment="" :close-dialog="() => { attachmentHistoryDialogOpen = false }"
      header="Attachment Status History" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="selectedInvoice"
      :selected-invoice-obj="item" :is-creation-dialog="false" />
  </div>
  <div v-if="exportAttachmentsDialogOpen">
    <PrintInvoiceDialog 
      :close-dialog="() => { exportAttachmentsDialogOpen = false }"
      :open-dialog="exportAttachmentsDialogOpen" 
      :invoice="item"
    />
  </div>
</template>
