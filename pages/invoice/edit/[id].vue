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
import { client } from 'process'



const toast = useToast()
const { data: userData } = useAuth()

const forceSave = ref(false)
const forceUpdate = ref(false)
const active = ref(0)

const route = useRoute()
const invoiceType = ref<string>(route.query.type as string)

//@ts-ignore
const selectedInvoice = <string>ref(route.params.id.toString())
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

const idClientForAgencyFilter = ref<string>('')

const invoiceAgency = ref<any>(null)
const invoiceHotel = ref<any>(null)

const attachmentDialogOpen = ref<boolean>(false)

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])
const invoiceTypeList = ref<any[]>([])

const invoiceStatus = ref<any>(null)
const dueAmount = ref<number>(0)
const isInCloseOperation = ref<boolean>(false)

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


const fields: Array<Container> = [
  {
    childs: [
      {
        field: 'invoiceId',
        header: 'Id',
        dataType: 'text',
        class: `w-full px-3  ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
        disabled: true,
        containerFieldClass: 'ml-10'

      },
      {
        field: 'invoiceNumber',
        header: 'Invoice Number',
        dataType: 'text',
        class: 'w-full px-3 ',
        disabled: true,

      },


    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

  {
    childs: [

      {
        field: 'hotel',
        header: 'Hotel',
        dataType: 'select',
        class: 'w-full px-3 required',
        disabled: String(route.query.type) as any === InvoiceType.CREDIT

      },
      {
        field: 'agency',
        header: 'Agency',
        dataType: 'select',
        class: 'w-full px-3 required',
        disabled: String(route.query.type) as any === InvoiceType.CREDIT
      },

    ],

    containerClass: 'flex flex-column justify-content-evenly w-full '
  },
  {
    childs: [
      {
        field: 'invoiceDate',
        header: 'Invoice Date',
        dataType: 'date',
        class: 'w-full px-3  required',
        validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
      },
      {
        field: 'status',
        header: 'Status',
        dataType: 'select',
        class: 'w-full px-3 mb-5',
        containerFieldClass: '',
        disabled: true
      },


    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },
  {
    childs: [

      {
        field: 'invoiceType',
        header: 'Type',
        dataType: 'select',
        class: 'w-full px-3 mb-5',
        containerFieldClass: '',
        disabled: true
      },
      {
        field: 'invoiceAmount',
        header: 'Invoice Amount',
        dataType: 'text',
        class: 'w-full px-3  required',
        disabled: true,
        ...(route.query.type === InvoiceType.OLD_CREDIT && { valdation: z.string().refine(val => +val < 0, 'Invoice amount must have negative values') })
      },
      {
        field: 'isManual',
        header: 'Manual',
        dataType: 'check',
        class: `w-full px-3  ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
        disabled: true
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

]

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
    class: 'field col-12 md:col-3  required',
    validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    disabled: String(route.query.type) as any === InvoiceType.CREDIT,
    validation: z.object({
      id: z.string(),
      name: z.string(),

    })
      .required()
      .refine((value: any) => value && value.id && value.name, { message: `The Hotel field is required` })
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
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    disabled: String(route.query.type) as any === InvoiceType.CREDIT,
    validation: validateEntityForAgency('agency')
  },
  // {
  //   field: 'agency',
  //   header: 'Agency',
  //   dataType: 'select',
  //   class: 'field col-12 md:col-3 required',
  //   disabled: String(route.query.type) as any === InvoiceType.CREDIT,
  //   validation: z.object({
  //     id: z.string(),
  //     name: z.string(),

  //   }).required()
  //     .refine((value: any) => value && value.id && value.name, { message: `The agency field is required` })
  // },
  {
    field: 'invoiceStatus',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-2 mb-5',
    containerFieldClass: '',
    disabled: true
  },
  {
    field: 'isManual',
    header: 'Manual',
    dataType: 'check',
    class: `field col-12 md:col-1  flex align-items-center pb-2 ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
    disabled: true
  },
])



// VARIABLES -----------------------------------------------------------------------------------------

const objApis = ref({
  invoiceType: { moduleApi: 'settings', uriApi: 'manage-invoice-type' },
  invoiceStatus: { moduleApi: 'invoicing', uriApi: 'manage-invoice-status' },
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

const propsParentId = ref({
  id: '',
  isCloned: false,
  label: 'Cloned From:',
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
  dueAmount: '0.00'
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
  dueAmount: '0.00'
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
    const listFilter = invoiceStatus.value !== InvoiceStatus.PROCECSED ? [
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
              key: 'client.id',
              operator: 'EQUALS',
              value: `${idClientForAgencyFilter.value}`,
              logicalOperation: 'AND'
            },
            {
              key: 'client.status',
              operator: 'EQUALS',
              value: 'ACTIVE',
              logicalOperation: 'AND'
            },
            {
              key: 'status',
              operator: 'EQUALS',
              value: 'ACTIVE',
              logicalOperation: 'AND'
            }
          ] : [
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
          ]
    const payload
      = {
      filter: [
            ...listFilter
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
      console.log(iterator);
      
      agencyList.value = [
        ...agencyList.value, 
        { 
          id: iterator.id, 
          name: iterator.name, 
          code: iterator.code, 
          status: iterator.status, 
          fullName: `${iterator.code} - ${iterator.name}`,
          client: iterator.client
        }
      ]
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
      invoiceTypeList.value = [
        ...invoiceTypeList.value, 
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

function refetchInvoice() {
  console.log("REFETCH");
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
    status: data.status,
    processStatus: data.processStatus,
    sentStatus: data.sentStatus,
    reconciledStatus: data.reconciledStatus,
    canceledStatus: data.canceledStatus
  }
}

async function getInvoiceStatusListDefault(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objApisLoading.value.invoiceStatus = true
    const additionalFilter: FilterCriteria[] = [
      {
        key: 'processStatus',
        logicalOperation: 'OR',
        operator: 'EQUALS',
        value: 'true'
      },
      {
        key: 'canceledStatus',
        logicalOperation: 'OR',
        operator: 'EQUALS',
        value: 'true'
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

  // getInvoiceStatusListDefault(objApis.value.invoiceStatus.moduleApi, objApis.value.invoiceStatus.uriApi, objQueryToSearch, filter)
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
        propsParentId.value.id = response?.parent?.invoiceId
        propsParentId.value.isCloned = response?.isCloned
        item.value.id = response.id
        item.value.invoiceId = response.invoiceId
        item.value.dueDate = response.dueDate

        const invoiceNumber = `${response?.invoiceNumber?.split('-')[0]}-${response?.invoiceNumber?.split('-')[2]}`

        item.value.invoiceNumber = response?.invoiceNumber?.split('-')?.length === 3 ? invoiceNumber : response.invoiceNumber
        item.value.invoiceNumber = item.value.invoiceNumber.replace("OLD", "CRE")

        // item.value.invoiceDate = dayjs(response.invoiceDate).format("YYYY-MM-DD")
        const newDate = new Date(response.invoiceDate)
        newDate.setDate(newDate.getDate() + 1)
        item.value.invoiceDate = newDate || null

        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        invoiceAmount.value = response.invoiceAmount
        dueAmount.value = response.dueAmount
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
        
        item.value.invoiceStatus = response.manageInvoiceStatus ? {
          id: response.manageInvoiceStatus.id,
          name: `${response.manageInvoiceStatus.code} - ${response.manageInvoiceStatus.name}`,
          status: response.manageInvoiceStatus.status,
          processStatus: response.manageInvoiceStatus.processStatus,
          sentStatus: response.manageInvoiceStatus.sentStatus,
          reconciledStatus: response.manageInvoiceStatus.reconciledStatus,
          canceledStatus: response.manageInvoiceStatus.canceledStatus
        } : null
        idClientForAgencyFilter.value = response.agency?.client?.id
        
        await getInvoiceAgency(response.agency?.id)
        await getInvoiceHotel(response.hotel?.id)
        isInCloseOperation.value = response.isInCloseOperation

        // Esto se debe solucionar haciendo que en la respueta se envie el status del cliente
        if (response?.agency?.client?.id) {
          const objClient = await GenericService.getById('settings', 'manage-client', response?.agency?.client?.id) 
          if (objClient) {
            item.value.agency.client = {
              ...item.value.agency?.client,
              status: objClient?.status,
            }
          }
        }
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

    //await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

const nightTypeRequired = ref(false)

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
  payload.invoiceStatus = item.invoiceStatus?.id

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
      console.log(item);
      
      // toast.add({ severity: 'info', summary: 'Confirmed', detail: `The invoice ${`${item?.invoiceNumber?.split('-')[0]}-${item?.invoiceNumber?.split('-')[2]}`} was updated successfully`, life: 10000 })
      toast.add({ severity: 'info', summary: 'Confirmed', detail: `The invoice ${item.invoiceNumber} was updated successfully`, life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  // else {
  //   try {
  //     await createItem(item)
  //     toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
  //   }
  //   catch (error: any) {
  //     successOperation = false
  //     toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  //   }
  // }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
  }
}
const goToList = async () => await navigateTo('/invoice')

function requireConfirmationToSave(item: any) {
  saveItem(item)
  const { event } = item
  // confirm.require({
  //   target: event.currentTarget,
  //   group: 'headless',
  //   header: 'Save the record',
  //   message: 'Do you want to save the change?',
  //   rejectLabel: 'Cancel',
  //   acceptLabel: 'Accept',
  //   accept: () => {
  //     saveItem(item)
  //   },
  //   reject: () => {
  //     // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
  //   }
  // })
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
      nightTypeRequired.value = agency?.client?.isNightType
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
      key: 'processStatus',
      logicalOperation: 'OR',
      operator: 'EQUALS',
      value: 'true'
    },
    {
      key: 'canceledStatus',
      logicalOperation: 'OR',
      operator: 'EQUALS',
      value: 'true'
    }
  ]

  invoiceStatusList.value = await getDataList<any, any>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction)  
}

function disabledInvoiceStatus(payload: any) {  
  let result = true
  if (item.value.invoiceAmount !== dueAmount.value || !isInCloseOperation.value){
    result = true
  }
  //Verificar si esta en estado Sent o Reconciled (En estos estados solo se puede editar la agencia)
  else if (payload && (payload.sentStatus || payload.reconciledStatus)) {
    result = true
  } else if (payload && payload.processStatus) {
    result = false
  }

  return result
  
}

function disabledFieldAgency() {
  if (item.value?.invoiceType?.code === 'CRE' || item.value?.invoiceType?.code === 'INC') {
    
    return true
  } else {
    let result = true
    if (invoiceStatus.value !== InvoiceStatus.PROCECSED && invoiceStatus.value !== InvoiceStatus.SENT && invoiceStatus.value !== InvoiceStatus.RECONCILED) {
      result = true
    } else {
      result = false
    }
    return result
  } 
}

function disableBtnSave() {
  if (item.value?.invoiceType?.code === 'CRE' || item.value?.invoiceType?.code === 'INC') {
    return true
  } else {
    let result = true
    if ( invoiceStatus.value !== InvoiceStatus.PROCECSED && invoiceStatus.value !== InvoiceStatus.SENT && invoiceStatus.value !== InvoiceStatus.RECONCILED) {
      result = true
    } else {
      result = false
    }
    return result
  } 
}

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

onMounted(async () => {  
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (route.params && 'id' in route.params && route.params.id) {
    await getItemById(route.params.id.toString())
    await loadDefaultsValues()
  }
})
</script>

<template>
  <div class="justify-content-center align-center ">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header flex justify-content-between">
      <div>
        {{ OBJ_UPDATE_INVOICE_TITLE[String(item?.invoiceType)] || "Edit Invoice" }}
      </div>
      <div v-if="propsParentId.isCloned">
         {{propsParentId?.label}} {{ propsParentId?.id }}
      </div>
    </div>
    <div class="p-4">
      <EditFormV2 
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
        @submit="requireConfirmationToSave($event)"
        container-class="grid pt-3"
      >
        <template #field-invoiceDate="{ item: data, onUpdate }">
          <Calendar 
            v-if="!loadingSaveAll" 
            v-model="data.invoiceDate" 
            date-format="yy-mm-dd" 
            :max-date="new Date()" 
            :disabled="invoiceStatus !== InvoiceStatus.PROCECSED "
            @update:model-value="($event) => {
              onUpdate('invoiceDate', $event)
            }" 
          />
        </template>
        <template #field-invoiceAmount="{ onUpdate, item: data }">
          <InputNumber v-model="invoiceAmount" :disabled="true" v-if="!loadingSaveAll"/>
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
            :disabled="disabledInvoiceStatus(item.invoiceStatus)" 
            @change="async ($event) => {
              onUpdate('invoiceStatus', $event)
            }" 
            @load="async ($event) => {
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
              }
          ]
            await getInvoiceStatusList(objApis.invoiceStatus.moduleApi, objApis.invoiceStatus.uriApi, objQueryToSearch, filter)
          }" />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-status="{ item: data, onUpdate }">
          <!-- :disabled="data?.status?.id !== InvoiceStatus.PROCECSED"  -->
          <Dropdown 
            v-if="!loadingSaveAll" 
            v-model="data.status" 
            :options="[...invoiceStatusList]" 
            option-label="name"
            return-object="false" 
            show-clear 
            :disabled="(data.invoiceAmount !== dueAmount || !isInCloseOperation)"
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
          <Dropdown v-if="!loadingSaveAll" 
            v-model="data.invoiceType" 
            :options="[...ENUM_INVOICE_TYPE]"
            option-label="name" 
            return-object="false" 
            show-clear 
            disabled 
            @update:model-value="($event) => {
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
          <DebouncedAutoCompleteComponent 
            v-if="!loadingSaveAll" 
            id="autocomplete" 
            field="fullName" 
            item-value="id" 
            :disabled="invoiceStatus === InvoiceStatus.PROCECSED || invoiceStatus === InvoiceStatus.SENT || invoiceStatus === InvoiceStatus.RECONCILED || invoiceStatus === InvoiceStatus.CANCELED || invoiceStatus === 'CANCELED' "
            :model="data.hotel" 
            :suggestions="hotelList" 
            @change="($event) => {
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
          <DebouncedAutoCompleteComponent 
            v-if="!loadingSaveAll" 
            id="autocomplete" 
            field="fullName" 
            item-value="id" 
            :disabled="disabledFieldAgency()"
            :model="data.agency" 
            :suggestions="agencyList" 
            @change="($event) => {
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
              :invoiceType='invoiceType'
            />
            <div>
              <div class="flex justify-content-end">
                <IfCan :perms="['INVOICE-MANAGEMENT:EDIT']">
                  <Button 
                    v-tooltip.top="'Save'" 
                    class="w-3rem mx-1" 
                    icon="pi pi-save" 
                    :disabled="disableBtnSave()" 
                    :loading="loadingSaveAll" 
                    @click="props.item.submitForm($event)" 
                  />
                    <!-- @click="() => {
                        saveItem(props.item.fieldValues)
                      }" -->
                </IfCan>

                <Button 
                  v-tooltip.top="'Print'" 
                  class="w-3rem mx-1" 
                  icon="pi pi-print" 
                  :loading="loadingSaveAll"
                  @click="() => {
                    exportAttachmentsDialogOpen = true
                  }"
                />
           
                <IfCan :perms="['INVOICE-MANAGEMENT:SHOW-BTN-ATTACHMENT']">
                  <Button v-tooltip.top="'Add Attachment'" class="w-3rem mx-1" icon="pi pi-paperclip"
                    :loading="loadingSaveAll" @click="handleAttachmentDialogOpen()"/>
                  </IfCan>
                  <IfCan :perms="['INVOICE-MANAGEMENT:BOOKING-SHOW-HISTORY']"> 
                    <!-- :disabled="!item?.hasAttachments" -->
                    <Button v-tooltip.top="'Show History'" class="w-3rem mx-1" :loading="loadingSaveAll"
                      @click="handleAttachmentHistoryDialogOpen()">
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
                  <Button 
                    v-if="active === 0" 
                    v-tooltip.top="'Add Booking'" 
                    class="w-3rem mx-1" 
                    icon="pi pi-plus"
                    :loading="loadingSaveAll" 
                    @click="handleDialogOpen()" 
                    disabled
                  />
                  <!-- :disabled="item?.invoiceType?.id === InvoiceType.INCOME || invoiceStatus !== InvoiceStatus.PROCECSED"  -->
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
      <AttachmentDialog 
        :close-dialog="() => { attachmentDialogOpen = false; getItemById(idItem) }"
        :is-creation-dialog="false" 
        header="Manage Invoice Attachment" 
        :open-dialog="attachmentDialogOpen"
        :selected-invoice="selectedInvoice" 
        :selected-invoice-obj="item" 
      />
    </div>
  </div>
  <div v-if="attachmentHistoryDialogOpen">
    <AttachmentHistoryDialog 
      selected-attachment="" 
      :close-dialog="() => { attachmentHistoryDialogOpen = false }"
      header="Attachment Status History" 
      :open-dialog="attachmentHistoryDialogOpen" 
      :selected-invoice="selectedInvoice"
      :selected-invoice-obj="item" 
      :is-creation-dialog="false"
    />
  </div>
  <div v-if="exportAttachmentsDialogOpen">
    <PrintInvoiceDialog 
      :close-dialog="() => { exportAttachmentsDialogOpen = false }"
      :open-dialog="exportAttachmentsDialogOpen" 
      :invoice="item"
    />
  </div>
</template>
