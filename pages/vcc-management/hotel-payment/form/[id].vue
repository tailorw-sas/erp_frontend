<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import dayjs from 'dayjs'
import type { Ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import { useToast } from 'primevue/usetoast'
import { useRoute } from 'vue-router'
import ContextMenu from 'primevue/contextmenu'
import { v4 } from 'uuid'
import { z } from 'zod'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { formatNumber } from '~/pages/payment/utils/helperFilters'

const { data: userData } = useAuth()
const route = useRoute()
const toast = useToast()
const { status, data } = useAuth()
const isAdmin = (data.value?.user as any)?.isAdmin === true
const authStore = useAuthStore()
const transactionsToBindDialogOpen = ref<boolean>(false)
const HotelList = ref<any[]>([])
const MerchantBankAccountList = ref<any[]>([])
const StatusList = ref<any[]>([])
const BindTransactionList = ref<any[]>([])
const BankAccountList = ref<any[]>([])
const collectionStatusList = ref<any[]>([])
const loadingSaveAll = ref(false)
const loadingStatus = ref(false)
const forceSave = ref(false)
const refForm: Ref = ref(null)
const formReload = ref(0)
const subTotals: any = ref({ amount: 0, commission: 0, net: 0 })
const paymentAmount = ref(0)
const idItem = ref('')
const selectedTransactionId = ref('')
const newAdjustmentTransactionDialogVisible = ref(false)
const editManualTransactionDialogVisible = ref(false)
const editAdjustmentTransactionDialogVisible = ref(false)
const contextMenu = ref()
const contextMenuTransaction = ref()
const currentSavedPaymentStatus = ref() // Guardar estado actual en backend para listar las opciones de posibles estados a seleccionar

enum MenuType {
  unBind
}

const allMenuListItems = [
  {
    type: MenuType.unBind,
    label: 'Unbind Transaction',
    icon: 'pi pi-dollar',
    iconSvg: 'M304.1 405.9c4.7 4.7 4.7 12.3 0 17l-44.7 44.7c-59.3 59.3-155.7 59.3-215 0-59.3-59.3-59.3-155.7 0-215l44.7-44.7c4.7-4.7 12.3-4.7 17 0l39.6 39.6c4.7 4.7 4.7 12.3 0 17l-44.7 44.7c-28.1 28.1-28.1 73.8 0 101.8 28.1 28.1 73.8 28.1 101.8 0l44.7-44.7c4.7-4.7 12.3-4.7 17 0l39.6 39.6zm-56.6-260.2c4.7 4.7 12.3 4.7 17 0l44.7-44.7c28.1-28.1 73.8-28.1 101.8 0 28.1 28.1 28.1 73.8 0 101.8l-44.7 44.7c-4.7 4.7-4.7 12.3 0 17l39.6 39.6c4.7 4.7 12.3 4.7 17 0l44.7-44.7c59.3-59.3 59.3-155.7 0-215-59.3-59.3-155.7-59.3-215 0l-44.7 44.7c-4.7 4.7-4.7 12.3 0 17l39.6 39.6zm234.8 359.3l22.6-22.6c9.4-9.4 9.4-24.6 0-33.9L63.6 7c-9.4-9.4-24.6-9.4-33.9 0L7 29.7c-9.4 9.4-9.4 24.6 0 33.9l441.4 441.4c9.4 9.4 24.6 9.4 33.9 0z',
    viewBox: '0 0 512 512',
    width: '14px',
    height: '14px',
    command: () => unbindTransactions(),
    disabled: false,
  }
]

const menuListItems = ref<any[]>([])

const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'hotel-payment',
})

const confStatusListApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'manage-payment-transaction-status',
})

const item = ref({
  hotelPaymentId: '',
  manageBankAccount: null,
  manageHotel: null,
  remark: '',
  status: null
} as GenericObject)

const itemTemp = ref({
  hotelPaymentId: '',
  manageBankAccount: null,
  manageHotel: null,
  remark: '',
  status: null
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'hotelPaymentId',
    header: 'Id',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-2',
    headerClass: 'mb-1',
  },
  {
    field: 'manageHotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-4 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'manageBankAccount',
    header: 'Bank Account',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('bank account'),
  },
  {
    field: 'status',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('status'),
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'text',
    class: 'field col-12 md:col-6',
    headerClass: 'mb-1',
    validation: z.string().trim().max(255, 'Maximum 255 characters')
  },
]

const columns: IColumn[] = [
  { field: 'referenceId', header: 'Id', type: 'text' },
  { field: 'merchant', header: 'Merchant', type: 'select', objApi: { moduleApi: 'creditcard', uriApi: 'manage-merchant', keyValue: 'description' }, sortable: true },
  { field: 'creditCardType', header: 'CC Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-credit-card-type' }, sortable: true },
  { field: 'referenceNumber', header: 'Reference', type: 'text' },
  { field: 'categoryType', header: 'Category Type', type: 'select', objApi: { moduleApi: 'creditcard', uriApi: 'manage-vcc-transaction-type' } },
  { field: 'subCategoryType', header: 'Sub Category Type', type: 'select', objApi: { moduleApi: 'creditcard', uriApi: 'manage-vcc-transaction-type' } },
  { field: 'checkIn', header: 'Trans Date', type: 'date' },
  { field: 'amount', header: 'Amount', type: 'number' },
  { field: 'commission', header: 'Commission', type: 'number' },
  { field: 'netAmount', header: 'T.Amount', type: 'number' },
]

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Transactions',
  moduleApi: 'creditcard',
  uriApi: 'transactions',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
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

const computedDisabledItemsByStatus = computed(() => {
  return item.value?.status && (item.value?.status?.completed || item.value?.status?.cancelled)
})

// FUNCTIONS ---------------------------------------------------------------------------------------------

async function canEditHotelPayment() {
  return (status.value === 'authenticated' && (isAdmin || authStore.can(['HOTEL-PAYMENT:EDIT'])))
}

async function openNewAdjustmentTransactionDialog() {
  newAdjustmentTransactionDialogVisible.value = true
}

async function onCloseNewAdjustmentTransactionDialog(isCancel: boolean) {
  newAdjustmentTransactionDialogVisible.value = false
  if (!isCancel) {
    // guardar en memoria la transaccion de ajuste
  }
}

function clearForm() {
  item.value = JSON.parse(JSON.stringify(itemTemp.value))
  formReload.value++
}

// Obtener datos del payment of merchant
async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = id
        item.value.hotelPaymentId = response.hotelPaymentId
        item.value.remark = response.remark
        if (response.manageBankAccount) {
          response.manageBankAccount.name = `${response.manageBankAccount.accountNumber}${(response.manageBankAccount.description ? ` - ${response.manageBankAccount.description}` : '')}`
          item.value.manageBankAccount = response.manageBankAccount
          MerchantBankAccountList.value = [response.manageBankAccount]
        }
        if (response.manageHotel) {
          response.manageHotel.name = `${response.manageHotel.code} - ${response.manageHotel.name}`
          item.value.manageHotel = response.manageHotel
          HotelList.value = [response.manageHotel]
        }
        currentSavedPaymentStatus.value = response.reconcileStatus
        if (response.status) {
          response.status.fullName = `${response.status.code} - ${response.status.name}`
          item.value.status = response.status
          StatusList.value = [response.status]
        }
        if (response.status && (response.status.completed || response.status.cancelled)) {
          updateFieldProperty(fields, 'remark', 'disabled', true)
        }
      }
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Item could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  const count = { amount: 0, commission: 0, net: 0 }
  subTotals.value = { ...count }
  try {
    options.value.loading = true
    BindTransactionList.value = []
    const newListItems = []
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { transactionSearchResponse } = response
    const { data: dataList, page, size, totalElements, totalPages } = transactionSearchResponse

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(BindTransactionList.value.map((item: any) => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'merchant') && iterator.hotel) {
        iterator.merchant = { id: iterator.merchant.id, name: `${iterator.merchant.code} - ${iterator.merchant.description}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel') && iterator.hotel) {
        iterator.hotel = { id: iterator.hotel.id, name: `${iterator.hotel.code} - ${iterator.hotel.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'creditCardType') && iterator.creditCardType) {
        iterator.creditCardType = { id: iterator.creditCardType.id, name: `${iterator.creditCardType.code} - ${iterator.creditCardType.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'categoryType') && iterator.categoryType) {
        iterator.categoryType = { id: iterator.categoryType.id, name: `${iterator.categoryType.code} - ${iterator.categoryType.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'subCategoryType') && iterator.subCategoryType) {
        iterator.subCategoryType = { id: iterator.subCategoryType.id, name: `${iterator.subCategoryType.code} - ${iterator.subCategoryType.name}`, negative: iterator.subCategoryType.negative }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'parent')) {
        iterator.parent = (iterator.parent) ? String(iterator.parent?.id) : null
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'id')) {
        iterator.id = String(iterator.id)
        iterator.referenceId = String(iterator.id)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        if (iterator.subCategoryType && iterator.subCategoryType.negative) {
          count.amount -= iterator.amount
        }
        else {
          count.amount += iterator.amount
        }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'commission')) {
        if (iterator.subCategoryType && iterator.subCategoryType.negative) {
          count.commission -= iterator.commission
        }
        else {
          count.commission += iterator.commission
        }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'netAmount')) {
        if (iterator.subCategoryType && iterator.subCategoryType.negative) {
          count.net -= iterator.netAmount
        }
        else {
          count.net += iterator.netAmount
        }
      }
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false, invoiceDate: new Date(iterator?.invoiceDate) })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    BindTransactionList.value = [...BindTransactionList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    subTotals.value = { ...count }
  }
}

async function getHotelList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'name',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }, {
        key: 'isApplyByVCC',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'AND'
      }],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response: any = await GenericService.search('settings', 'manage-hotel', payload)
    const { data: dataList } = response
    HotelList.value = []

    for (const iterator of dataList) {
      HotelList.value = [...HotelList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getStatusList(query: string) {
  try {
    loadingStatus.value = true
    const payload = {
      filter: [{
        key: 'inProgress',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'OR'
      }, {
        key: 'cancelled',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'OR'
      }, {
        key: 'completed',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'OR'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      },],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response = await GenericService.search(confStatusListApi.moduleApi, confStatusListApi.uriApi, payload)
    const { data: dataList } = response
    StatusList.value = []
    for (const iterator of dataList) {
      StatusList.value = [...StatusList.value, { id: iterator.id, code: iterator.code, name: iterator.name, fullName: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
    // Para mantener la referencia del model en el selector simple
    const index = StatusList.value.findIndex((elem: any) => elem.id === item.value.status?.id)
    if (index >= 0) {
      StatusList.value[index] = item.value.status
    }
    else {
      StatusList.value.unshift(item.value.status)
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
  finally {
    loadingStatus.value = false
  }
}

async function getBankAccountList(query: string) {
  if (!item.value.manageHotel) {
    return
  }
  try {
    const payload = {
      filter: [{
        key: 'accountNumber',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'AND'
      }, {
        key: 'manageHotel.id',
        operator: 'EQUALS',
        value: item.value.manageHotel.id,
        logicalOperation: 'AND'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response: any = await GenericService.search('settings', 'manage-bank-account', payload)
    const { data: dataList } = response
    BankAccountList.value = []

    for (const iterator of dataList) {
      BankAccountList.value = [...BankAccountList.value, { id: iterator.id, name: `${iterator.accountNumber}${(iterator.description ? ` - ${iterator.description}` : '')}`, status: iterator.status, managerMerchant: iterator.managerMerchant, creditCardTypes: iterator.creditCardTypes }]
    }
  }
  catch (error) {
    console.error('Error loading merchant bank account list:', error)
  }
}

async function bindTransactions(transactions: any[]) {
  try {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = {}
    payload.hotelPaymentId = idItem.value
    if (transactions.length > 0) {
      payload.transactionIds = transactions.filter((t: any) => !t.adjustment).map((i: any) => i.id)
      const adjustmentTransactions = transactions.filter((t: any) => t.adjustment)
      payload.adjustmentRequests = adjustmentTransactions.map((elem: any) => ({
        agency: typeof elem.agency === 'object' ? elem.agency.id : elem.agency,
        transactionCategory: typeof elem.transactionCategory === 'object' ? elem.transactionCategory.id : elem.transactionCategory,
        transactionSubCategory: typeof elem.transactionSubCategory === 'object' ? elem.transactionSubCategory.id : elem.transactionSubCategory,
        amount: elem.netAmount,
        reservationNumber: elem.reservationNumber,
        referenceNumber: elem.referenceNumber
      }))
    }
    const response: any = await GenericService.create(confApi.moduleApi, `${confApi.uriApi}/add-transactions`, payload)
    if (response) {
      if (response.detailsAmount) {
        paymentAmount.value = response.detailsAmount
      }
      // Guarda el id del elemento creado
      const newIds = [...response.adjustmentIds || [], ...response.transactionIds || []]
      if (newIds.length === 1) {
        toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Transaction ${newIds[0]} was bounded successfully`, life: 10000 })
      }
      else {
        toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Transactions ${newIds.join(', ')} were bounded successfully`, life: 10000 })
      }
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was not successful', life: 10000 })
    }
    getList()
  }
  catch (error: any) {
    loadingSaveAll.value = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function unbindTransactions() {
  try {
    loadingSaveAll.value = true
    const transactionsIds = [contextMenuTransaction.value.id]
    const payload: { [key: string]: any } = {}
    payload.hotelPaymentId = idItem.value
    payload.transactionsIds = transactionsIds

    await GenericService.create(confApi.moduleApi, 'hotel-payment/unbind', payload)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Transaction ${transactionsIds.join(', ')} was unbounded successfully`, life: 10000 })
    getList()
  }
  catch (error: any) {
    loadingSaveAll.value = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function updateItem(item: { [key: string]: any }) {
  if (item) {
    const payload: { [key: string]: any } = {}
    payload.remark = item.remark || ''
    payload.employee = userData?.value?.user?.name
    payload.employeeId = userData?.value?.user?.userId
    payload.manageBankAccount = Object.prototype.hasOwnProperty.call(item.manageBankAccount, 'id') ? item.manageBankAccount.id : item.manageBankAccount
    payload.status = Object.prototype.hasOwnProperty.call(item.status, 'id') ? item.status.id : item.status
    const response: any = await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value, payload)
    if (response && response.id) {
      // Guarda el id del elemento creado
      idItem.value = response.id
      toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Hotel Payment ${response.hotelPaymentId ?? ''} was updated successfully`, life: 5000 })
      setTimeout(() => {
        window.close()
      }, 1500)
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was not successful', life: 10000 })
    }
  }
}

async function saveItem(item: { [key: string]: any }) {
  // let successOperation = true
  loadingSaveAll.value = true
  try {
    await updateItem(item)
    await getItemById(idItem.value)
  }
  catch (error: any) {
    loadingSaveAll.value = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function handleSave(event: any) {
  if (event) {
    await saveItem(event)
    forceSave.value = false
  }
}

function bindAdjustment(data: any) {
  const newAdjustment = formatAdjustment(data)
  bindTransactions([newAdjustment])
}

function formatAdjustment(data: any) {
  data.id = v4() // id temporal para poder eliminar de forma local
  data.checkIn = dayjs().format('YYYY-MM-DD')
  data.adjustment = true
  return data
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  if (parseFilter) {
    const index = parseFilter.findIndex((filter: IFilter) => filter.key === 'referenceId')
    if (index !== -1) {
      parseFilter[index].key = 'id'
    }
  }
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'merchant') {
      event.sortField = 'merchant.code'
    }
    if (event.sortField === 'creditCardType') {
      event.sortField = 'creditCardType.code'
    }
    if (event.sortField === 'categoryType') {
      event.sortField = 'transactionCategory.code'
    }
    if (event.sortField === 'subCategoryType') {
      event.sortField = 'transactionSubCategory.code'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function onRowRightClick(event: any) {
  contextMenu.value.hide()
  contextMenuTransaction.value = event.data
  menuListItems.value = [...allMenuListItems]
  if (!await canEditHotelPayment() || computedDisabledItemsByStatus.value) {
    menuListItems.value = allMenuListItems.filter((item: any) => item.type !== MenuType.unBind)
  }
  if (menuListItems.value.length > 0) {
    contextMenu.value.show(event.originalEvent)
  }
}

function onDoubleClick(item: any) {
  const id = Object.prototype.hasOwnProperty.call(item, 'id') ? item.id : item
  if (item.manual || item.parent) {
    selectedTransactionId.value = id
    editManualTransactionDialogVisible.value = true
  }
  if (item.adjustment) {
    selectedTransactionId.value = id
    editAdjustmentTransactionDialogVisible.value = true
  }
}

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  if (idItem.value) { // Aplicar paginacion onLine
    getList()
  }
})

onMounted(async () => {
  if (route?.params?.id || '') {
    const id = route.params?.id?.toString()
    idItem.value = id
    getItemById(id)
    payload.value.filter = [{
      key: 'hotelPayment.id',
      operator: 'EQUALS',
      value: idItem.value,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }]
    getList()
  }
})
</script>

<template>
  <div style="max-height: 100vh; height: 90vh">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      Edit Hotel Payment
    </div>
    <div class="card p-4 mb-0">
      <EditFormV2
        :key="formReload"
        ref="refForm"
        :fields="fields"
        :item="item"
        :show-actions="false"
        container-class="grid pt-3"
        :loading-save="loadingSaveAll"
        :force-save="forceSave"
        @cancel="clearForm"
        @force-save="forceSave = $event"
        @submit="handleSave($event)"
      >
        <template #field-manageHotel="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            disabled
            :model="data.manageHotel"
            :suggestions="HotelList"
            @change="($event) => {
              onUpdate('manageHotel', $event)
              item.manageHotel = $event
            }"
            @load="($event) => getHotelList($event)"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-manageBankAccount="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :disabled="!data.manageHotel || computedDisabledItemsByStatus"
            :model="data.manageBankAccount"
            :suggestions="[...BankAccountList]"
            @change="($event) => {
              onUpdate('manageBankAccount', $event)
              item.manageBankAccount = $event
            }"
            @load="($event) => getBankAccountList($event)"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-status="{ item: data, onUpdate }">
          <CustomSelectComponent
            v-if="!loadingSaveAll" id="autocomplete" :loading="loadingStatus"
            field="fullName" item-value="id" :model="data.status" :suggestions="StatusList"
            :disabled="computedDisabledItemsByStatus"
            @change="($event) => {
              console.log($event)
              onUpdate('status', $event)
              item.status = $event
            }" @load="($event) => getStatusList($event)"
          >
            <template #option="props">
              <span>{{ props.item.fullName }}</span>
            </template>
          </CustomSelectComponent>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
      </EditFormV2>
    </div>
    <!-- <pre>{{ selectedElements }}</pre> -->
    <DynamicTable
      :data="BindTransactionList"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      @on-change-pagination="payloadOnChangePage = $event"
      @on-change-filter="parseDataTableFilter"
      @on-sort-field="onSortField"
      @on-row-right-click="onRowRightClick"
      @on-row-double-click="onDoubleClick($event)"
    >
      <template #datatable-footer>
        <ColumnGroup type="footer" class="flex align-items-center">
          <Row>
            <Column footer="Totals:" :colspan="7" footer-style="text-align:right" />
            <Column :footer="formatNumber(subTotals.amount)" />
            <Column :footer="formatNumber(subTotals.commission)" />
            <Column :footer="formatNumber(subTotals.net)" />
          </Row>
        </ColumnGroup>
      </template>
    </DynamicTable>
    <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
      <div>
        <IfCan :perms="['HOTEL-PAYMENT:EDIT']">
          <Button v-tooltip.top="'Bind Transaction'" class="w-3rem" :disabled="item.manageHotel == null || computedDisabledItemsByStatus" icon="pi pi-link" @click="() => { transactionsToBindDialogOpen = true }" />
          <Button v-tooltip.top="'Add Adjustment'" class="w-3rem ml-1" icon="pi pi-dollar" :disabled="computedDisabledItemsByStatus" @click="openNewAdjustmentTransactionDialog()" />
          <Button v-tooltip.top="'Save'" class="w-3rem ml-1" icon="pi pi-save" :loading="loadingSaveAll" :disabled="computedDisabledItemsByStatus" @click="forceSave = true" />
        </IfCan>
        <!-- <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="() => { navigateTo('/vcc-management/hotel-payment') }" /> -->
      </div>
    </div>
    <div v-if="transactionsToBindDialogOpen">
      <HotelPaymentBindTransactionsDialog
        :close-dialog="() => { transactionsToBindDialogOpen = false }" header="Bind Transaction"
        :open-dialog="transactionsToBindDialogOpen" :current-hotel-payment="item" :valid-collection-status-list="collectionStatusList"
        @update:list-items="($event) => bindTransactions($event)"
        @update:status-list="($event) => collectionStatusList = $event"
      />
    </div>
    <VCCEditManualTransaction
      :open-dialog="editManualTransactionDialogVisible" :transaction-id="selectedTransactionId" @on-close-dialog="($event) => {
        editManualTransactionDialogVisible = false
        if (!$event) {
          getList()
        }
      }"
    />
    <VCCEditAdjustmentTransaction
      :open-dialog="editAdjustmentTransactionDialogVisible" :transaction-id="selectedTransactionId" :disable-amount-field="computedDisabledItemsByStatus"
      @on-close-dialog="($event) => {
        editAdjustmentTransactionDialogVisible = false
        if (!$event) {
          getList()
          getItemById(idItem)
        }
      }"
    />
    <VCCNewAdjustmentTransaction
      is-local :open-dialog="newAdjustmentTransactionDialogVisible"
      @on-close-dialog="onCloseNewAdjustmentTransactionDialog($event)" @on-save-local="($event) => bindAdjustment($event)"
    />
    <ContextMenu ref="contextMenu" :model="menuListItems">
      <template #itemicon="{ item }">
        <div v-if="item.iconSvg !== ''" class="w-2rem flex justify-content-center align-items-center">
          <svg xmlns="http://www.w3.org/2000/svg" :height="item.height" :viewBox="item.viewBox" :width="item.width" fill="#8d8faa">
            <path :d="item.iconSvg" />
          </svg>
        </div>
        <div v-else class="w-2rem flex justify-content-center align-items-center">
          <i v-if="item.icon" :class="item.icon" />
        </div>
      </template>
    </ContextMenu>
  </div>
</template>
