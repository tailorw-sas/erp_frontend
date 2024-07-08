<script setup lang="ts">
import dayjs from 'dayjs'
import { z } from 'zod'
import { useRoute } from 'vue-router'
import type { PageState } from 'primevue/paginator'
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import DialogPaymentDetailForm from '~/components/payment/DialogPaymentDetailForm.vue'

const route = useRoute()
const toast = useToast()
const confirm = useConfirm()

const refForm: Ref = ref(null)
const idItem = ref('')
const formReload = ref(0)
const dialogPaymentDetailFormReload = ref(0)
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const forceSave = ref(false)
let submitEvent = new Event('')

const paymentSourceList = ref<any[]>([])
const clientList = ref<any[]>([])
const agencyList = ref<any[]>([])
const hotelList = ref<any[]>([])
const bankAccountList = ref<any[]>([])
const paymentStatusList = ref<any[]>([])
const attachmentStatusList = ref<any[]>([])
const onOffDialogPaymentDetail = ref(false)

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
  { field: 'transactionType', header: 'Transaction Type', width: '200px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' } },
  // { field: 'deposit', header: 'Deposit', width: '200px', type: 'bool' },
  { field: 'amount', header: 'Amount', width: '200px', type: 'text' },
  { field: 'remark', header: 'Remark', width: '200px', type: 'text' },
]

const formTitle = computed(() => {
  return idItem.value ? 'Payment Edit' : 'Payment Create'
})

const decimalRegex = /^-?\d+(\.\d+)?$/

const decimalSchema = z.object(
  {
    amount: z
      .string()
      .min(1, { message: 'The amount field is required' })
      .regex(decimalRegex, { message: 'The amount field must be a valid decimal or integer' }),
    // .refine(value => Number.parseFloat(value) >= 1, { message: 'The amount field must be at least 1' }),
    paymentAmmount: z
      .string()
      .min(1, { message: 'The payment amount field is required' })
      .regex(decimalRegex, { message: 'The payment amount field must be a valid decimal or integer' }),
    // .refine(value => Number.parseFloat(value) >= 1, { message: 'The payment amount field must be at least 1' })
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
    disabled: false,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'identified',
    header: 'Identified',
    dataType: 'text',
    disabled: false,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'otherDeductions',
    header: 'Other Deductions',
    dataType: 'text',
    disabled: false,
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
    header: 'Payment Status',
    dataType: 'select',
    class: 'field col-12 md:col-1 required',
    validation: validateEntityStatus('payment status'),
  },
  {
    field: 'transactionDate',
    header: 'Bank Deposit Date',
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
    validation: validateEntityStatus('agency'),
  },
  {
    field: 'paymentBalance',
    header: 'Payment Balance',
    dataType: 'text',
    disabled: false,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'depositBalance',
    header: 'Deposit Balance',
    dataType: 'text',
    disabled: false,
    class: 'field col-12 md:col-1',
  },
  {
    field: 'notIdentified',
    header: 'Not Identified',
    dataType: 'text',
    disabled: false,
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
  transactionDate: null,
  paymentStatus: null,
  client: null,
  agency: null,
  hotel: null,
  bankAccount: null,
  attachmentStatus: null,
  paymentAmount: '',
  paymentBalance: '',
  depositAmount: '',
  depositBalance: '',
  otherDeductions: '',
  identified: '',
  notIdentified: '',
  remark: '',
  status: '',
} as GenericObject)

const itemTemp = ref({
  paymentId: '0',
  paymentSource: null,
  reference: '',
  transactionDate: null,
  paymentStatus: null,
  client: null,
  agency: null,
  hotel: null,
  bankAccount: null,
  attachmentStatus: null,
  paymentAmount: '',
  paymentBalance: '',
  depositAmount: '',
  depositBalance: '',
  otherDeductions: '',
  identified: '',
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
  sortType: 'DES'
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
function goToList() {
  navigateTo('/payment')
}

function goToForm(item: string) {
  navigateTo({ path: '/payment/form', query: { id: item } })
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
    // delete payload.remark

    delete payload.id
    delete payload.event
    // delete payload.bankAccount

    const response = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)

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
  let successOperation = true
  if (idItem.value) {
    try {
      // await updateItem(item)
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
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
    getList()
  }
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.paymentId = response.paymentId
        item.value.reference = response.reference
        item.value.transactionDate = response.transactionDate

        item.value.paymentAmount = response.paymentAmount
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

        bankAccountList.value = typeof response.bankAccount === 'object' ? [{ id: response.bankAccount.id, name: response.bankAccount.accountNumber }] : []
        item.value.bankAccount = typeof response.bankAccount === 'object' ? { id: response.bankAccount.id, name: response.bankAccount.accountNumber } : response.bankAccount

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

function saveSubmit(event: Event) {
  forceSave.value = true
  submitEvent = event
}

function handleSave($event: any) {
  forceSave.value = false
  item.value = $event
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

async function createPaymentDetails(item: { [key: string]: any }) {
  if (item) {
    // loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.payment = idItem.value || ''
    payload.transactionType = Object.prototype.hasOwnProperty.call(payload.transactionType, 'id') ? payload.transactionType.id : payload.transactionType

    await GenericService.create(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, payload)
    onOffDialogPaymentDetail.value = false
    clearFormDetails()
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.payment = idItem.value || ''
  payload.transactionType = typeof payload.transactionType === 'object' ? payload.transactionType.id : payload.transactionType

  await GenericService.update(confApiPaymentDetail.moduleApi, confApiPaymentDetail.uriApi, item.id || '', payload)
  onOffDialogPaymentDetail.value = false
  clearFormDetails()
}

async function saveAndReload(item: { [key: string]: any }) {
  try {
    if (item?.id) {
      await updateItem(item)
    }
    else {
      await createPaymentDetails(item)
    }
    await getItemById(idItem.value)
  }
  catch (error) {
    console.error(error)
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

async function getPaymentSourceList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  paymentSourceList.value = await getDataList(moduleApi, uriApi, [], queryObj)
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
async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  clientList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [], queryObj, mapFunction)
}
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  agencyList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [], queryObj, mapFunction)
}

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  hotelList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [], queryObj, mapFunction)
}
async function getPaymentStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  paymentStatusList.value = await getDataList(moduleApi, uriApi, [], queryObj)
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
async function getBankAccountList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  bankAccountList.value = await getDataList<DataListItemForBankAccount, ListItemForBankAccount>(moduleApi, uriApi, [], queryObj, mapFunctionForBankAccountList)
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
async function getAttachmentStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  attachmentStatusList.value = await getDataList<DataListItemForAttachmentStatus, ListItemForAttachmentStatus>(moduleApi, uriApi, [], queryObj, mapFunctionForAttachmentStatusList)
}

watch(() => item.value, async (newValue) => {
  if (newValue) {
    requireConfirmationToSave(newValue)
  }
})

onMounted(async () => {
  if (route?.query?.id) {
    const id = route.query.id.toString()
    await getItemById(id)
  }
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header">
    {{ formTitle }}
  </div>
  <!-- <pre>{{ paymentDetailsList }}</pre> -->
  <!-- @delete="requireConfirmationToDelete($event)" -->
  <div class="card p-4">
    <EditFormV2
      :key="formReload"
      ref="refForm"
      :fields="fields"
      :item="item"
      :show-actions="false"
      container-class="grid pt-3"
      :force-save="forceSave"
      :loading-save="loadingSaveAll"
      :loading-delete="loadingDelete"
      @cancel="clearForm"
      @force-save="forceSave = $event"
      @submit="handleSave($event)"
    >
      <template #field-client="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
          v-if="!loadingSaveAll"
          id="autocomplete"
          field="name"
          item-value="id"
          :model="data.client"
          :suggestions="[...clientList]"
          @change="($event) => {
            onUpdate('client', $event)
          }"
          @load="async($event) => {
            const objQueryToSearch = {
              query: $event,
              keys: ['name', 'code'],
            }
            await getClientList(objApis.client.moduleApi, objApis.client.uriApi, objQueryToSearch)
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
            await getPaymentSourceList(objApis.paymentSource.moduleApi, objApis.paymentSource.uriApi, objQueryToSearch)
          }"
        />
        <Skeleton v-else height="2rem" class="mb-2" />
      </template>
      <!-- Agency -->
      <template #field-agency="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
          v-if="!loadingSaveAll"
          id="autocomplete"
          field="name"
          item-value="id"
          :model="data.agency"
          :suggestions="[...agencyList]"
          @change="($event) => {
            onUpdate('agency', $event)
          }"
          @load="async($event) => {
            const objQueryToSearch = {
              query: $event,
              keys: ['name', 'code'],
            }
            await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, objQueryToSearch)
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
          @change="($event) => {
            onUpdate('hotel', $event)
          }"
          @load="async($event) => {
            const objQueryToSearch = {
              query: $event,
              keys: ['name', 'code'],
            }
            await getHotelList(objApis.hotel.moduleApi, objApis.hotel.uriApi, objQueryToSearch)
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
          :suggestions="[...bankAccountList]"
          @change="($event) => {
            onUpdate('bankAccount', $event)
          }"
          @load="async($event) => {
            const objQueryToSearch = {
              query: $event,
              keys: ['name', 'code'],
            }
            await getBankAccountList(objApis.bankAccount.moduleApi, objApis.bankAccount.uriApi, objQueryToSearch)
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
  <!-- <pre>{{ itemDetails }}</pre> -->
  <DynamicTable
    :data="paymentDetailsList"
    :columns="columns"
    :options="options"
    :pagination="pagination"
    @on-change-pagination="payloadOnChangePage = $event"
    @on-row-double-click="openDialogPaymentDetails($event)"
  />
  <!-- @on-confirm-create="goToCreateForm"
    @on-change-filter="parseDataTableFilter"
    @on-list-item="resetListItems"
    @on-sort-field="onSortField" -->

  <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
    <Button v-tooltip.top="'Save'" class="w-3rem" icon="pi pi-save" :loading="loadingSaveAll" @click="saveSubmit($event)" />
    <Button v-tooltip.top="'New Detail'" class="w-3rem mx-1" icon="pi pi-plus" :disabled="idItem === null || idItem === undefined || idItem === ''" severity="secondary" @click="openDialogPaymentDetails" />
    <Button v-tooltip.top="'Delete'" class="w-3rem" severity="secondary" :disabled="item?.id === null || item?.id === undefined" :loading="loadingDelete" icon="pi pi-trash" />
    <!-- <Button v-tooltip.top="'Edit Detail'" class="w-3rem" icon="pi pi-pen-to-square" severity="secondary" @click="deletePaymentDetail($event)" /> -->
    <Button v-tooltip.top="'Attachment'" class="w-3rem mx-1" icon="pi pi-paperclip" severity="secondary" @click="goToList" />
    <Button v-tooltip.top="'Go Back'" class="w-3rem" icon="pi pi-times" severity="secondary" @click="goToList" />
  </div>
  <DialogPaymentDetailForm
    :key="dialogPaymentDetailFormReload"
    :visible="onOffDialogPaymentDetail"
    :fields="fieldPaymentDetails"
    :item="itemDetails"
    @update:visible="onOffDialogPaymentDetail = $event"
    @save="saveAndReload($event)"
  />
</template>
