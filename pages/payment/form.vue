<script setup lang="ts">
import dayjs from 'dayjs'
import { z } from 'zod'
import { useRoute } from 'vue-router'
import type { PageState } from 'primevue/paginator'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import DialogPaymentDetailForm from '~/components/payment/DialogPaymentDetailForm.vue'
import PaymentAttachmentDialog, { type FileObject } from '~/components/payment/PaymentAttachmentDialog.vue'
import type { TransactionItem } from '~/components/payment/interfaces'

const route = useRoute()
const toast = useToast()
const confirm = useConfirm()

const refForm: Ref = ref(null)
const idItem = ref('')
const idItemDetail = ref('')
const isSplitAction = ref(false)
const enableSplitAction = ref(false)
const formReload = ref(0)
const formReloadAgency = ref(0)
const dialogPaymentDetailFormReload = ref(0)
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const forceSave = ref(false)
const submitEvent = new Event('')
const paymentSourceList = ref<any[]>([])
const clientList = ref<any[]>([])
const agencyList = ref<any[]>([])
const hotelList = ref<any[]>([])
const bankAccountList = ref<any[]>([])
const paymentStatusList = ref<any[]>([])
const attachmentStatusList = ref<any[]>([])
const onOffDialogPaymentDetail = ref(false)
const onOffDialogPaymentDetailSummary = ref(false)
const hasBeenEdited = ref(0)
const hasBeenCreated = ref(0)

const attachmentDialogOpen = ref<boolean>(false)
const attachmentList = ref<any[]>([])

const paymentDetailsList = ref<any[]>([])

const confApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment',
})

const confApiPaymentDetail = reactive({
  moduleApi: 'payment',
  uriApi: 'payment-detail',
})

const objApis = ref({
  paymentSource: { moduleApi: 'settings', uriApi: 'manage-payment-source' },
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  bankAccount: { moduleApi: 'settings', uriApi: 'manage-bank-account' },
  paymentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  attachmentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' },
})

const columns: IColumn[] = [
  { field: 'transactionType', header: 'Transaction Category', width: '200px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' } },
  { field: 'amount', header: 'Amount', width: '200px', type: 'text' },
  { field: 'remark', header: 'Remark', width: '200px', type: 'text' },
  // { field: 'transactionType', header: 'Deposit', width: '200px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' } },

]

const formTitle = computed(() => {
  return idItem.value ? 'Edit Payment' : 'New Payment'
})

const decimalRegex = /^\d+(\.\d{1,2})?$/

const decimalSchema = z.object(
  {
    amount: z
      .string()
      .min(1, { message: 'The amount field is required' })
      .regex(decimalRegex, { message: 'The amount does not meet the correct format of n integer digits and 2 decimal digits' }),
    // .refine(value => Number.parseFloat(value) >= 1, { message: 'The amount field must be at least 1' }),
    paymentAmmount: z
      .string()
      .min(1, { message: 'The payment amount field is required' })
      .regex(decimalRegex, { message: 'The payment amount does not meet the correct format of n integer digits and 2 decimal digits' })
      .refine(value => Number.parseFloat(value) >= 1, { message: 'The payment amount field must be at least 1' })
  },
)

const fields: Array<FieldDefinitionType> = [
  {
    field: 'paymentId',
    header: 'Id',
    disabled: true,
    dataType: 'text',
    class: 'field col-12 md:col-3',
  },
  {
    field: 'client',
    header: 'Client',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('client'),
  },
  {
    field: 'paymentAmount',
    header: 'Payment Amount',
    dataType: 'text',
    disabled: false,
    class: 'field col-12 md:col-1 required',
    validation: decimalSchema.shape.paymentAmmount
  },
  {
    field: 'depositAmount',
    header: 'Deposit Amount',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'identified',
    header: 'Identified',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'otherDeductions',
    header: 'Other Deductions',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-3',
  },
  {
    field: 'paymentSource',
    header: 'Payment Source',
    dataType: 'select',
    class: 'field col-12 md:col-1 required',
    validation: validateEntityStatus('payment source'),
  },
  {
    field: 'paymentStatus',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-1 required',
    validation: validateEntityStatus('payment status'),
  },
  {
    field: 'transactionDate',
    header: 'Bank Dep. Date',
    dataType: 'date',
    class: 'field col-12 md:col-1 required ',
    headerClass: 'mb-1',

    validation: z.date({
      required_error: 'The Bank Deposit Date field is required',
      invalid_type_error: 'The Bank Deposit Date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The Bank Deposit Date field cannot be greater than current date')

  },
  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    disabled: true,
    validation: validateEntityStatus('agency'),
  },
  {
    field: 'paymentBalance',
    header: 'Payment Balance',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'depositBalance',
    header: 'Deposit Balance',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'notIdentified',
    header: 'Not Identified',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'attachmentStatus',
    header: 'Attachment Status',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('attachment status'),
  },
  {
    field: 'reference',
    header: 'Reference No.',
    dataType: 'text',
    class: 'field col-12 md:col-3',
  },

  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'bankAccount',
    header: 'Bank Account',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    validation: validateEntityStatus('bank account'),
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'text',
    class: 'field col-12 md:col-3',
  },

  // {
  //   field: 'status',
  //   header: 'Active',
  //   dataType: 'check',
  //   class: 'field col-12 md:col-1 mt-3 mb-3',
  //   headerClass: 'mb-1',
  // },

]

const item = ref({
  paymentId: '0',
  paymentSource: null,
  reference: '',
  transactionDate: '',
  paymentStatus: null,
  client: null,
  agency: null,
  hotel: null,
  bankAccount: null,
  attachmentStatus: null,
  paymentAmount: '2000',
  paymentBalance: '0',
  depositAmount: '0',
  depositBalance: '0',
  otherDeductions: '0',
  identified: '0',
  notIdentified: '',
  remark: '',
  status: '',
} as GenericObject)

const itemTemp = ref({
  paymentId: '0',
  paymentSource: null,
  reference: '',
  transactionDate: '',
  paymentStatus: null,
  client: null,
  agency: null,
  hotel: null,
  bankAccount: null,
  attachmentStatus: null,
  paymentAmount: '0',
  paymentBalance: '0',
  depositAmount: '0',
  depositBalance: '0',
  otherDeductions: '0',
  identified: '0',
  notIdentified: '',
  remark: '',
  status: '',
})

const fieldPaymentDetails = ref<FieldDefinitionType[]>([
  {
    field: 'transactionType',
    header: 'Transaction Category',
    dataType: 'select',
    class: 'field col-12',
    validation: validateEntityStatus('transaction category'),
  },
  {
    field: 'amount',
    header: 'Amount',
    dataType: 'text',
    disabled: false,
    class: 'field col-12 required',
    validation: decimalSchema.shape.amount
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'textarea',
    class: 'field col-12',
  },
])

const itemDetails = ref({
  payment: '',
  transactionType: null,
  amount: 0,
  remark: '',
  status: ''
})

const itemDetailsTemp = ref({
  payment: '',
  transactionType: null,
  amount: 0,
  remark: '',
  status: ''
})

const options = ref({
  tableName: 'Payment Details',
  moduleApi: 'payment',
  uriApi: 'payment-detail',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

function clearForm() {
  item.value = JSON.parse(JSON.stringify(itemTemp.value))
  idItem.value = ''
  formReload.value++
}

function clearFormDetails() {
  itemDetails.value = JSON.parse(JSON.stringify(itemDetailsTemp.value))
}

function openDialogPaymentDetails(event: any) {
  if (event) {
    itemDetails.value = { ...itemDetailsTemp.value }
    const objToEdit = paymentDetailsList.value.find(x => x.id === event.id)

    if (objToEdit) {
      itemDetails.value = { ...objToEdit }
    }

    onOffDialogPaymentDetail.value = true
  }

  onOffDialogPaymentDetail.value = true
}
function openDialogPaymentDetailsForSplit(idDetail: any, isSplit: boolean = false) {
  isSplitAction.value = isSplit
  if (idDetail) {
    itemDetails.value = { ...itemDetailsTemp.value }
    const objToEdit = paymentDetailsList.value.find(x => x.id === idDetail)

    if (objToEdit) {
      itemDetails.value = { ...objToEdit }
    }

    onOffDialogPaymentDetail.value = true
  }

  onOffDialogPaymentDetail.value = true
}

function dialogPaymentDetailSummary() {
  onOffDialogPaymentDetailSummary.value = true
}
function goToList() {
  navigateTo('/payment')
}

function goToForm(item: string) {
  navigateTo({ path: '/payment/form', query: { id: item } })
}

function transformObjects(inputArray: FileObject[]) {
  return inputArray.map(item => ({
    status: item.resourceType.status,
    resource: item.resource || 'default-resource-id', // Puedes cambiar "default-resource-id" por un valor por defecto si es necesario
    employee: item.employee,
    resourceType: item.resourceType.id,
    attachmentType: item.attachmentType.id,
    fileName: item.fileName,
    fileWeight: item.fileSize,
    path: item.path,
    remark: item.remark
  }))
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.paymentSource = Object.prototype.hasOwnProperty.call(payload.paymentSource, 'id') ? payload.paymentSource.id : payload.paymentSource
    payload.transactionDate = payload.transactionDate !== null ? dayjs(payload.transactionDate).format('YYYY-MM-DD') : ''
    payload.paymentStatus = Object.prototype.hasOwnProperty.call(payload.paymentStatus, 'id') ? payload.paymentStatus.id : payload.paymentStatus
    payload.attachmentStatus = Object.prototype.hasOwnProperty.call(payload.attachmentStatus, 'id') ? payload.attachmentStatus.id : payload.attachmentStatus
    payload.client = Object.prototype.hasOwnProperty.call(payload.client, 'id') ? payload.client.id : payload.client
    payload.agency = Object.prototype.hasOwnProperty.call(payload.agency, 'id') ? payload.agency.id : payload.agency
    payload.hotel = Object.prototype.hasOwnProperty.call(payload.hotel, 'id') ? payload.hotel.id : payload.hotel
    payload.bankAccount = Object.prototype.hasOwnProperty.call(payload.bankAccount, 'id') ? payload.bankAccount.id : payload.bankAccount
    payload.paymentAmount = Number(payload.paymentAmount)
    payload.status = statusToString(payload.status)
    if (attachmentList.value.length > 0) {
      payload.attachments = transformObjects(attachmentList.value)
    }
    else {
      delete payload.attachments
    }

    // delete payload.paymentSource
    // delete payload.reference
    // delete payload.transactionDate
    // delete payload.paymentStatus
    // delete payload.client
    // delete payload.agency
    // delete payload.hotel
    // delete payload.bankAccount
    // delete payload.attachmentStatus
    // delete payload.paymentAmount
    delete payload.paymentBalance
    delete payload.depositAmount
    delete payload.depositBalance
    delete payload.otherDeductions
    delete payload.identified
    delete payload.notIdentified
    delete payload.paymentId
    // delete payload.remark

    delete payload.id
    delete payload.event
    // delete payload.bankAccount

    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)

    if (response && response.payment) {
      idItem.value = response.payment.id
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
      goToForm(idItem.value)
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was not successful', life: 10000 })
    }
    loadingSaveAll.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  // const successOperation = true
  if (idItem.value) {
    try {
      await updateItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      // successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
      await createItem(item)
    }
    catch (error: any) {
      // successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  // if (successOperation) {
  //   clearForm()
  // }
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = id
        item.value.paymentId = response.paymentId
        item.value.reference = response.reference
        item.value.transactionDate = response.transactionDate
        const newDate = new Date(response.transactionDate)
        newDate.setDate(newDate.getDate() + 1)
        item.value.transactionDate = newDate || null

        item.value.paymentAmount = String(response.paymentAmount)
        item.value.paymentBalance = response.paymentBalance
        item.value.depositAmount = response.depositAmount
        item.value.depositBalance = response.depositBalance
        item.value.otherDeductions = response.otherDeductions
        item.value.identified = response.identified
        item.value.notIdentified = response.notIdentified
        item.value.remark = response.remark

        clientList.value = [response.client]
        item.value.client = response.client

        agencyList.value = [response.agency]
        item.value.agency = response.agency

        hotelList.value = [response.hotel]
        item.value.hotel = response.hotel

        bankAccountList.value = typeof response.bankAccount === 'object' ? [{ id: response.bankAccount.id, name: response.bankAccount.accountNumber, status: response.bankAccount.status }] : []
        item.value.bankAccount = typeof response.bankAccount === 'object' ? { id: response.bankAccount.id, name: response.bankAccount.accountNumber, status: response.bankAccount.status } : response.bankAccount

        attachmentStatusList.value = [response.attachmentStatus]
        item.value.attachmentStatus = response.attachmentStatus

        paymentStatusList.value = [response.paymentStatus]
        item.value.paymentStatus = response.paymentStatus

        paymentSourceList.value = [response.paymentSource]
        item.value.paymentSource = response.paymentSource
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1

      await getListPaymentDetail()
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Account type methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function getListPaymentDetail() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    paymentDetailsList.value = []
    const newListItems = []

    const objFilter = payload.value.filter.find(item => item.key === 'payment.id')

    if (objFilter) {
      objFilter.value = idItem.value
    }
    else {
      payload.value.filter.push({
        key: 'payment.id',
        operator: 'EQUALS',
        value: idItem.value,
        logicalOperation: 'AND'
      })
    }

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(paymentDetailsList.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        iterator.amount = (!Number.isNaN(iterator.amount) && iterator.amount !== null && iterator.amount !== '')
          ? Number.parseFloat(iterator.amount).toString()
          : '0'
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    paymentDetailsList.value = [...paymentDetailsList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

function hasDepositTransaction(mainId: string, items: TransactionItem[]): boolean {
  // Buscar el objeto principal por su id
  const mainItem = items.find(item => item.id === mainId)

  if (!mainItem) {
    return false // Si no se encuentra el objeto principal, devolver false
  }

  // Verificar si el objeto principal o alguno de sus hijos tiene una transacción de tipo deposit
  const hasDeposit = (item: TransactionItem): boolean => {
    if (item.transactionType.applyDeposit) {
      return true
    }

    // Verificar en los hijos del objeto
    if (item.children && item.children.length > 0) {
      return item.children.some(child => hasDeposit(child))
    }

    return false
  }

  return hasDeposit(mainItem)
}

async function createPaymentDetails(item: { [key: string]: any }) {
  if (item) {
    // loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.payment = idItem.value || ''
    payload.amount = Number.parseFloat(payload.amount)
    payload.transactionType = Object.prototype.hasOwnProperty.call(payload.transactionType, 'id') ? payload.transactionType.id : payload.transactionType

    if (isSplitAction.value === false) {
      confApiPaymentDetail.uriApi = 'payment-detail'
      await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payload)
    }
    else {
      const payloadSplit = {
        amount: item.amount.trim() !== '' && !Number.isNaN(item.amount) ? Number(item.amount) : 0,
        paymentDetail: item.id,
        remark: item.remark,
        transactionType: Object.prototype.hasOwnProperty.call(item.transactionType, 'id') ? item.transactionType.id : item.transactionType,
        status: 'ACTIVE'
      }
      confApiPaymentDetail.uriApi = 'payment-detail-split'
      await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payloadSplit)
      isSplitAction.value = false
    }

    onOffDialogPaymentDetail.value = false
    clearFormDetails()
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.paymentSource = Object.prototype.hasOwnProperty.call(payload.paymentSource, 'id') ? payload.paymentSource.id : payload.paymentSource
  payload.transactionDate = payload.transactionDate !== null ? dayjs(payload.transactionDate).format('YYYY-MM-DD') : ''
  payload.paymentStatus = Object.prototype.hasOwnProperty.call(payload.paymentStatus, 'id') ? payload.paymentStatus.id : payload.paymentStatus
  payload.attachmentStatus = Object.prototype.hasOwnProperty.call(payload.attachmentStatus, 'id') ? payload.attachmentStatus.id : payload.attachmentStatus
  payload.client = Object.prototype.hasOwnProperty.call(payload.client, 'id') ? payload.client.id : payload.client
  payload.agency = Object.prototype.hasOwnProperty.call(payload.agency, 'id') ? payload.agency.id : payload.agency
  payload.hotel = Object.prototype.hasOwnProperty.call(payload.hotel, 'id') ? payload.hotel.id : payload.hotel
  payload.bankAccount = Object.prototype.hasOwnProperty.call(payload.bankAccount, 'id') ? payload.bankAccount.id : payload.bankAccount
  payload.paymentAmount = Number(payload.paymentAmount)
  payload.status = statusToString(payload.status)

  // delete payload.paymentSource
  // delete payload.reference
  // delete payload.transactionDate
  // delete payload.paymentStatus
  // delete payload.client
  // delete payload.agency
  // delete payload.hotel
  // delete payload.bankAccount
  // delete payload.attachmentStatus
  // delete payload.paymentAmount
  delete payload.paymentBalance
  delete payload.depositAmount
  delete payload.depositBalance
  delete payload.otherDeductions
  delete payload.identified
  delete payload.notIdentified
  delete payload.paymentId
  // delete payload.remark

  delete payload.id
  delete payload.event
  // delete payload.bankAccount
  const id = route?.query?.id.toString()
  await GenericService.update(confApi.moduleApi, confApi.uriApi, id || '', payload)
  hasBeenEdited.value += 1
}

async function saveAndReload(item: { [key: string]: any }) {
  try {
    if (item?.id && !isSplitAction.value) {
      await updateItem(item)
    }
    else {
      await createPaymentDetails(item)
    }
    await getItemById(idItem.value)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
}

function requireConfirmationToSave(item: any) {
  confirm.require({
    target: submitEvent.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      await saveItem(item)
    },
    reject: () => {}
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
    accept: async () => {
      // await deleteItem(idItem.value)
    },
    reject: () => {}
  })
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status
  }
}

async function getPaymentSourceList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  paymentSourceList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
}

async function getDefaultPaymentSourceList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  const defaultPaymentSourceList = ref<ListItem[]>([])
  const filter: FilterCriteria[] = [
    {
      key: 'code',
      logicalOperation: 'AND',
      operator: 'LIKE',
      value: 'bk',
    },
    {
      key: 'name',
      logicalOperation: 'AND',
      operator: 'LIKE',
      value: 'bank',
    }
  ]
  defaultPaymentSourceList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...filter], queryObj, mapFunction)
  if (defaultPaymentSourceList.value.length > 0) {
    item.value.paymentSource = defaultPaymentSourceList.value[0]
  }
}

function mapFunctionForClient(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: data.name,
    status: data.status
  }
}

async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  clientList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunctionForClient)
}
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  agencyList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
}

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  hotelList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
}
async function getPaymentStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  paymentStatusList.value = await getDataList(moduleApi, uriApi, [], queryObj)
}

async function getDefaultPaymentStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  const defaultPaymentStatusList = ref<ListItem[]>([])
  const filter: FilterCriteria[] = [
    {
      key: 'code',
      logicalOperation: 'AND',
      operator: 'LIKE',
      value: 'con',
    },
    {
      key: 'name',
      logicalOperation: 'AND',
      operator: 'LIKE',
      value: 'confirmed',
    }
  ]
  defaultPaymentStatusList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [...filter], queryObj, mapFunction)
  if (defaultPaymentStatusList.value.length > 0) {
    item.value.paymentStatus = defaultPaymentStatusList.value[0]
  }
}

interface DataListItemForBankAccount {
  id: string
  accountNumber: string
  status: string
}

interface ListItemForBankAccount {
  id: string
  name: string
  status: boolean | string
}

function mapFunctionForBankAccountList(data: DataListItemForBankAccount): ListItemForBankAccount {
  return {
    id: data.id,
    name: data.accountNumber,
    status: data.status
  }
}
async function getBankAccountList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  bankAccountList.value = await getDataList<DataListItemForBankAccount, ListItemForBankAccount>(moduleApi, uriApi, filter, queryObj, mapFunctionForBankAccountList)
}

interface DataListItemForAttachmentStatus {
  id: string
  name: string
  code: string
  status: string
}

interface ListItemForAttachmentStatus {
  id: string
  name: string
  status: boolean | string
}

function mapFunctionForAttachmentStatusList(data: DataListItemForAttachmentStatus): ListItemForAttachmentStatus {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status
  }
}
async function getAttachmentStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  attachmentStatusList.value = await getDataList<DataListItemForAttachmentStatus, ListItemForAttachmentStatus>(moduleApi, uriApi, filter, queryObj, mapFunctionForAttachmentStatusList)
}

// Attachments
function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}
function addAttachment(attachment: any) {
  attachmentList.value = [...attachmentList.value, attachment]
}

function updateAttachment(attachment: any) {
  const index = attachmentList.value.findIndex(item => item.id === attachment.id)
  attachmentList.value[index] = attachment
}

async function rowSelected(rowData: any) {
  if (rowData !== null && rowData !== undefined && rowData !== '') {
    idItemDetail.value = rowData
    enableSplitAction.value = await hasDepositTransaction(rowData, paymentDetailsList.value)
  }
  else {
    idItemDetail.value = ''
    isSplitAction.value = false
  }
}

function onCloseDialog($event: any) {
  if ($event === false) {
    isSplitAction.value = false
  }
  onOffDialogPaymentDetail.value = $event
}

function onCloseDialogSummary($event: any) {
  onOffDialogPaymentDetailSummary.value = $event
}

async function loadDefaultsValues() {
  await getDefaultPaymentSourceList(objApis.value.paymentSource.moduleApi, objApis.value.paymentSource.uriApi, {
    query: '',
    keys: ['name', 'code'],
  })
  await getDefaultPaymentStatusList(objApis.value.paymentStatus.moduleApi, objApis.value.paymentStatus.uriApi, {
    query: '',
    keys: ['name', 'code'],
  })
  const newDate = new Date()
  item.value.transactionDate = newDate

  const filter: FilterCriteria[] = [
    {
      key: 'defaults',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: true,
    },
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
    },
  ]
  await getAttachmentStatusList(objApis.value.attachmentStatus.moduleApi, objApis.value.attachmentStatus.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filter)
  item.value.attachmentStatus = attachmentStatusList.value.length > 0 ? attachmentStatusList.value[0] : null
  formReload.value++
}

async function handleSave(event: any) {
  if (event) {
    await saveItem(event)
    forceSave.value = false
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...parseFilter || []]
  getListPaymentDetail()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'transactionType') {
      event.sortField = 'transactionType.name'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function deleteItem(id: string) {
  if (!id) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was failed', life: 3000 })
    return
  }
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    if (route?.query?.id) {
      const id = route.query.id.toString()
      await getItemById(id)
    }
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
    idItemDetail.value = ''
  }
}

watch(() => hasBeenEdited.value, async (newValue) => {
  if (newValue) {
    if (route?.query?.id) {
      const id = route.query.id.toString()
      await getItemById(id)
    }
  }
})

watch(() => hasBeenCreated.value, async (newValue) => {
  if (newValue) {
    if (route?.query?.id) {
      const id = route.query.id.toString()
      await getItemById(id)
    }
  }
})

watch(() => route?.query?.id, async (newValue) => {
  if (newValue) {
    const id = newValue.toString()
    await getItemById(id)
  }
})

onMounted(async () => {
  if (route?.query?.id) {
    const id = route.query.id.toString()
    await getItemById(id)
  }
  else {
    clearForm()
    loadDefaultsValues()
  }
})
</script>

<template>
  <div style="max-height: 100vh; height: 90vh">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      {{ formTitle }}
    </div>
    <div class="card p-4">
      <EditFormV2
        :key="formReload"
        ref="refForm"
        :fields="fields"
        :item="item"
        :show-actions="false"
        container-class="grid pt-3"
        :loading-save="loadingSaveAll"
        :loading-delete="loadingDelete"
        :force-save="forceSave"
        @cancel="clearForm"
        @force-save="forceSave = $event"
        @submit="handleSave($event)"
        @delete="requireConfirmationToDelete($event)"
      >
        <template #field-paymentAmount="{ item: data, onUpdate }">
          <InputText
            v-if="!loadingSaveAll"
            v-model="data.paymentAmount"
            show-clear
            @update:model-value="($event) => {
              onUpdate('paymentAmount', $event)
              if (idItem === '' && paymentDetailsList.length === 0) {
                onUpdate('notIdentified', $event)
              }
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-client="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.client"
            :suggestions="[...clientList]"
            @change="async ($event) => {
              onUpdate('client', $event)
              const filter: FilterCriteria[] = [
                {
                  key: 'client.id',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: data.client?.id,
                },
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, {
                query: '',
                keys: ['name', 'code'],
              }, filter)
              onUpdate('agency', agencyList.length > 0 ? agencyList[0] : null)
            }"
            @load="async($event) => {
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
              await getClientList(objApis.client.moduleApi, objApis.client.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-paymentSource="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.paymentSource"
            :suggestions="[...paymentSourceList]"
            @change="($event) => {
              onUpdate('paymentSource', $event)
            }"
            @load="async($event) => {
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
              await getPaymentSourceList(objApis.paymentSource.moduleApi, objApis.paymentSource.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-transactionDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.transactionDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('transactionDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" />
        </template>
        <!-- Agency -->
        <template #field-agency="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            :key="formReloadAgency"
            field="name"
            item-value="id"
            :model="data.agency"
            :suggestions="[...agencyList]"
            :disabled="!data.client"
            @change="($event) => {
              onUpdate('agency', $event)
            }"
            @load="async($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              const filter: FilterCriteria[] = [
                {
                  key: 'client.id',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: data.client?.id,
                },
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-hotel="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.hotel"
            :suggestions="[...hotelList]"
            @change="async ($event) => {
              onUpdate('hotel', $event)

              const filter: FilterCriteria[] = [
                {
                  key: 'manageHotel.id',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: $event?.id,
                },
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getBankAccountList(objApis.bankAccount.moduleApi, objApis.bankAccount.uriApi, {
                query: '',
                keys: ['accountNumber'],
              }, filter)

              onUpdate('bankAccount', bankAccountList.length > 0 ? bankAccountList[0] : null)
            }"
            @load="async($event) => {
              const filter: FilterCriteria[] = [
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              await getHotelList(objApis.hotel.moduleApi, objApis.hotel.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-bankAccount="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.bankAccount"
            :disabled="!data.hotel"
            :suggestions="[...bankAccountList]"
            @change="($event) => {
              onUpdate('bankAccount', $event)
            }"
            @load="async($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              const filter: FilterCriteria[] = [
                {
                  key: 'manageHotel.id',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: data.hotel?.id,
                },
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getBankAccountList(objApis.bankAccount.moduleApi, objApis.bankAccount.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-paymentStatus="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.paymentStatus"
            :suggestions="[...paymentStatusList]"
            @change="($event) => {
              onUpdate('paymentStatus', $event)
            }"
            @load="async($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              await getPaymentStatusList(objApis.paymentStatus.moduleApi, objApis.paymentStatus.uriApi, objQueryToSearch)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-attachmentStatus="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.attachmentStatus"
            :suggestions="[...attachmentStatusList]"
            @change="($event) => {
              onUpdate('attachmentStatus', $event)
            }"
            @load="async($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              await getAttachmentStatusList(objApis.attachmentStatus.moduleApi, objApis.attachmentStatus.uriApi, objQueryToSearch)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
      </EditFormV2>
    </div>
    <DynamicTable
      :data="paymentDetailsList"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      @on-change-pagination="payloadOnChangePage = $event"
      @update:clicked-item="rowSelected($event)"
      @on-row-double-click="openDialogPaymentDetails($event)"
      @on-change-filter="parseDataTableFilter"
      @on-sort-field="onSortField"
    />
    <!-- <pre>{{ paymentDetailsList }}</pre> -->
    <!-- @on-confirm-create="goToCreateForm"
      @on-list-item="resetListItems"
      -->

    <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
      <Button v-tooltip.top="'Save'" class="w-3rem" icon="pi pi-save" :loading="loadingSaveAll" @click="forceSave = true" />
      <Button v-tooltip.top="'Deposit Summary'" :disabled="idItem === null || idItem === undefined || idItem === ''" class="w-3rem ml-1" @click="dialogPaymentDetailSummary">
        <template #icon>
          <span class="flex align-items-center justify-content-center p-0">
            <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24">
              <path fill="currentColor" d="M10 4v4h4V4zm6 0v4h4V4zm0 6v4h4v-4zm0 6v4h4v-4zm-2 4v-4h-4v4zm-6 0v-4H4v4zm0-6v-4H4v4zm0-6V4H4v4zm2 6h4v-4h-4zM4 2h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H4c-1.08 0-2-.9-2-2V4a2 2 0 0 1 2-2" />
            </svg>
          </span>
        </template>
      </Button>
      <Button v-tooltip.top="'Deposit Transfer'" class="w-3rem ml-1" disabled icon="pi pi-lock" />
      <Button v-tooltip.top="'Split ANTI'" class="w-3rem ml-1" :disabled="!enableSplitAction" @click="openDialogPaymentDetailsForSplit(idItemDetail, true)">
        <template #icon>
          <span class="flex align-items-center justify-content-center p-0">
            <svg width="14" height="15" viewBox="0 0 14 15" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4.08337 12.75C5.04987 12.75 5.83337 11.9665 5.83337 11C5.83337 10.0335 5.04987 9.25 4.08337 9.25C3.11688 9.25 2.33337 10.0335 2.33337 11C2.33337 11.9665 3.11688 12.75 4.08337 12.75Z" stroke="white" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M8.75004 9.25L4.08337 2.25M5.25004 9.25L7.00004 6.625M9.91671 2.25L8.16671 4.875" stroke="white" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M9.91675 12.75C10.8832 12.75 11.6667 11.9665 11.6667 11C11.6667 10.0335 10.8832 9.25 9.91675 9.25C8.95025 9.25 8.16675 10.0335 8.16675 11C8.16675 11.9665 8.95025 12.75 9.91675 12.75Z" stroke="white" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </span>
        </template>
      </Button>
      <Button v-tooltip.top="'Print Attachment'" class="w-3rem ml-1" disabled icon="pi pi-print" />
      <Button v-tooltip.top="'Attachment'" class="w-3rem ml-1" icon="pi pi-paperclip" severity="primary" @click="handleAttachmentDialogOpen" />
      <Button v-tooltip.top="'Import from Excel'" class="w-3rem ml-1" disabled icon="pi pi-upload" />
      <Button v-tooltip.top="'Show History'" class="w-3rem ml-1" disabled>
        <template #icon>
          <span class="flex align-items-center justify-content-center p-0">
            <svg xmlns="http://www.w3.org/2000/svg" height="15px" viewBox="0 -960 960 960" width="15px" fill="#e8eaed"><path d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z" /></svg>
          </span>
        </template>
      </Button>
      <!-- <Button v-tooltip.top="'Edit Detail'" class="w-3rem" icon="pi pi-pen-to-square" severity="secondary" @click="deletePaymentDetail($event)" /> -->
      <Button v-tooltip.top="'Add New Detail'" class="w-3rem ml-1" icon="pi pi-plus" :disabled="idItem === null || idItem === undefined || idItem === ''" severity="primary" @click="openDialogPaymentDetails($event)" />
      <Button v-tooltip.top="'Delete'" class="w-3rem ml-1" outlined severity="danger" :disabled="idItemDetail === null || idItemDetail === undefined || idItemDetail === ''" :loading="loadingDelete" icon="pi pi-trash" @click="deleteItem(idItemDetail)" />
      <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="goToList" />
    </div>
    <DialogPaymentDetailForm
      :key="dialogPaymentDetailFormReload"
      :visible="onOffDialogPaymentDetail"
      :fields="fieldPaymentDetails"
      :item="itemDetails"
      :title="isSplitAction ? 'Split | Payment Detail' : 'New Payment Details'"
      :selected-payment="item"
      :is-split-action="isSplitAction"
      @update:visible="onCloseDialog($event)"
      @save="saveAndReload($event)"
    />
    <div v-if="attachmentDialogOpen">
      <PaymentAttachmentDialog
        :is-create-or-edit-payment="route?.query?.id ? 'edit' : 'create'"
        :add-item="addAttachment"
        :close-dialog="() => { attachmentDialogOpen = false }"
        :is-creation-dialog="true"
        header="Manager Payment Attachment"
        :list-items="attachmentList"
        :open-dialog="attachmentDialogOpen"
        :update-item="updateAttachment"
        :selected-payment="item"
        @update:list-items="attachmentList = $event"
      />
    </div>
    <DialogPaymentDetailSummary
      title="Payment Details Summary"
      :visible="onOffDialogPaymentDetailSummary"
      :selected-payment="item"
      @update:visible="onCloseDialogSummary($event)"
    />
  </div>
</template>
