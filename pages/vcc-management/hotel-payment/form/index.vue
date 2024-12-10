<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import dayjs from 'dayjs'
import type { Ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import ContextMenu from 'primevue/contextmenu'
import { v4 } from 'uuid'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { formatNumber } from '~/pages/payment/utils/helperFilters'
import type { FilterCriteria } from '~/composables/list'

const toast = useToast()
const transactionsToBindDialogOpen = ref<boolean>(false)
const HotelList = ref<any[]>([])
const BankAccountList = ref<any[]>([])
const StatusList = ref<any[]>([])
const LocalBindTransactionList = ref<any[]>([])
const collectionStatusList = ref<any[]>([])
const loadingSaveAll = ref(false)
const loadingDefaultStatus = ref(false)
const forceSave = ref(false)
const refForm: Ref = ref(null)
const formReload = ref(0)
const subTotals: any = ref({ amount: 0, commission: 0, net: 0 })
const selectedElements = ref<any[]>([])
const idItem = ref('')
const selectedTransactionId = ref('')
const selectedTransaction = ref()
const editAdjustmentTransactionDialogVisible = ref(false)
const newAdjustmentTransactionDialogVisible = ref(false)
const editManualTransactionDialogVisible = ref(false)
const contextMenu = ref()
const contextMenuTransaction = ref()

const menuListItems = [
  {
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

const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'hotel-payment',
})

const confStatusListApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'manage-payment-transaction-status',
})

const item = ref({
  paymentId: '0',
  manageBankAccount: null,
  manageHotel: null,
  remark: '',
  status: null
} as GenericObject)

const itemTemp = ref({
  paymentId: '0',
  manageBankAccount: null,
  manageHotel: null,
  remark: '',
  status: null
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'paymentId',
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
  },
]

const columns: IColumn[] = [
  { field: 'referenceId', header: 'Id', type: 'text' },
  { field: 'merchant', header: 'Merchant', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-merchant', keyValue: 'description' }, sortable: true },
  { field: 'creditCardType', header: 'CC Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-credit-card-type' }, sortable: true },
  { field: 'referenceNumber', header: 'Reference', type: 'text' },
  { field: 'categoryType', header: 'Category Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-vcc-transaction-type' } },
  { field: 'subCategoryType', header: 'Sub Category Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-vcc-transaction-type' } },
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
  showFilters: false,
  messageToDelete: 'Do you want to save the change?',
})
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
const paginationFirstLocal = ref(0) // Índice inicial de la página actual

const paginatedData = computed(() => {
  const start = paginationFirstLocal.value
  const end = start + pagination.value.limit
  return LocalBindTransactionList.value.slice(start, end)
})

const computedTransactionAmountSelected = computed(() => {
  const totalSelectedAmount = selectedElements.value.length > 0 ? selectedElements.value.reduce((sum, item) => sum + item.netAmount, 0) : 0
  return `Transaction Amount Selected: $${formatNumber(totalSelectedAmount)}`
})

const computedLocalBindTransactionList = computed(() => {
  return LocalBindTransactionList.value.filter((item: any) => !item.adjustment)
})

// FUNCTIONS ---------------------------------------------------------------------------------------------
async function openNewAdjustmentTransactionDialog() {
  newAdjustmentTransactionDialogVisible.value = true
}

async function onCloseNewAdjustmentTransactionDialog(isCancel: boolean) {
  newAdjustmentTransactionDialogVisible.value = false
  if (!isCancel) {
    // guardar en memoria la transaccion de ajuste
  }
}

async function onMultipleSelect(data: any) {
  // Crear un Set de IDs para los seleccionados globalmente y los seleccionados en la página actual
  const selectedIds = new Set(selectedElements.value.map((item: any) => item.id))
  const currentPageSelectedIds = new Set(data.map((item: any) => item.id))

  // de los que estan seleccionados globalmente, mantener los que vienen en la pagina actual, mas los seleccionados que no estan en este conjunto
  const selectedPreviously = selectedElements.value.filter((item: any) =>
    currentPageSelectedIds.has(item.id) || !paginatedData.value.some((pageItem: any) => pageItem.id === item.id)
  )
  // Agregar nuevos elementos seleccionados en la página actual
  const newElements = data.filter((item: any) => !selectedIds.has(item.id))
  // Crear un nuevo array que contenga la selección global optimizada
  // Actualizar selectedElements solo una vez
  selectedElements.value = [
    ...selectedPreviously,
    ...newElements
  ]
}

function clearForm() {
  item.value = JSON.parse(JSON.stringify(itemTemp.value))
  formReload.value++
}

async function updateCurrentManualTransaction() {
  const transaction = await getCurrentTransactionList()
  if (transaction) {
    const index = LocalBindTransactionList.value.findIndex((item: any) => item.id === selectedTransactionId.value)
    if (index !== -1) {
      // Reemplazar el objeto en la lista
      LocalBindTransactionList.value[index] = transaction
    }
  }
}

async function updateCurrentAdjustmentTransaction(transaction: any) {
  if (transaction) {
    const index = LocalBindTransactionList.value.findIndex((item: any) => item.id === transaction.id)
    if (index !== -1) {
      formatAdjustmentEdit(transaction)
    }
  }
}

async function getCurrentTransactionList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    const payload = {
      filter: [{
        key: 'id',
        operator: 'EQUALS',
        value: selectedTransactionId.value,
        logicalOperation: 'AND'
      }],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload)

    const { transactionSearchResponse } = response
    const { data: dataList, page, size, totalElements, totalPages } = transactionSearchResponse

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

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
    }

    return dataList[0] ?? null
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
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

async function getStatusList(isDefault: boolean = false, filter?: FilterCriteria[]) {
  try {
    if (isDefault) {
      loadingDefaultStatus.value = true
    }
    const payload = {
      filter: filter ?? [],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confStatusListApi.moduleApi, confStatusListApi.uriApi, payload)
    const { data: dataList } = response
    StatusList.value = []
    for (const iterator of dataList) {
      StatusList.value = [...StatusList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status ?? 'ACTIVE', fullName: `${iterator.code} - ${iterator.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
  finally {
    if (isDefault) {
      loadingDefaultStatus.value = false
    }
  }
}

async function loadDefaultStatus() {
  const filter: FilterCriteria[] = [
    {
      key: 'inProgress',
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
  await getStatusList(true, filter)
  item.value.status = StatusList.value.length > 0 ? StatusList.value[0] : null
  formReload.value++
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

function clearTransactions() {
  LocalBindTransactionList.value = []
  subTotals.value = { amount: 0, commission: 0, net: 0 }
}

function unbindTransactions() {
  const transactionId = String(contextMenuTransaction.value.id)
  LocalBindTransactionList.value = LocalBindTransactionList.value.filter((item: any) => item.id !== transactionId)
  selectedElements.value = selectedElements.value.filter((item: any) => item.id !== transactionId)
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    const payload: { [key: string]: any } = { ...item }
    payload.transactionDate = dayjs().format('YYYY-MM-DDTHH:mm:ss')
    payload.status = Object.prototype.hasOwnProperty.call(payload.status, 'id') ? payload.status.id : payload.status
    payload.manageHotel = Object.prototype.hasOwnProperty.call(payload.manageHotel, 'id') ? payload.manageHotel.id : payload.manageHotel
    payload.manageBankAccount = Object.prototype.hasOwnProperty.call(payload.manageBankAccount, 'id') ? payload.manageBankAccount.id : payload.manageBankAccount

    if (LocalBindTransactionList.value.length > 0) {
      payload.transactions = LocalBindTransactionList.value.filter((t: any) => !t.adjustment).map((i: any) => i.id)
      const adjustmentTransactions = LocalBindTransactionList.value.filter((t: any) => t.adjustment)
      payload.adjustmentTransactions = adjustmentTransactions.map((elem: any) => ({
        agency: typeof elem.agency === 'object' ? elem.agency.id : elem.agency,
        transactionCategory: typeof elem.transactionCategory === 'object' ? elem.transactionCategory.id : elem.transactionCategory,
        transactionSubCategory: typeof elem.transactionSubCategory === 'object' ? elem.transactionSubCategory.id : elem.transactionSubCategory,
        amount: elem.netAmount,
        reservationNumber: elem.reservationNumber,
        referenceNumber: elem.referenceNumber
      }))
    }
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    if (response && response.id) {
      // Guarda el id del elemento creado
      idItem.value = response.id
      LocalBindTransactionList.value = []
      toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Hotel Payment ${response.hotelPaymentId ?? ''} was created successfully`, life: 10000 })
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was not successful', life: 10000 })
    }
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  try {
    await createItem(item)
    await navigateTo({ path: `/vcc-management/hotel-payment/form/${idItem.value}`, params: { id: idItem.value } })
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
  LocalBindTransactionList.value = [...LocalBindTransactionList.value, newAdjustment]
}

function formatAdjustment(data: any) {
  const newAdjustment = JSON.parse(JSON.stringify(data))
  newAdjustment.id = v4() // id temporal para poder eliminar de forma local
  newAdjustment.checkIn = dayjs().format('YYYY-MM-DD')
  newAdjustment.commission = 0
  newAdjustment.netAmount = data.netAmount
  newAdjustment.amount = data.transactionCategory.onlyApplyNet ? 0 : data.netAmount
  newAdjustment.adjustment = true
  if (data.transactionCategory) {
    newAdjustment.categoryType = data.transactionCategory
  }
  if (data.transactionSubCategory) {
    newAdjustment.subCategoryType = data.transactionSubCategory
  }
  return newAdjustment
}

function formatAdjustmentEdit(data: any) {
  const editedAdjustment = JSON.parse(JSON.stringify(data))
  const prevAdjustment = LocalBindTransactionList.value.find(item => item.id === data.id)
  prevAdjustment.commission = 0
  prevAdjustment.netAmount = data.netAmount
  prevAdjustment.amount = prevAdjustment.categoryType.onlyApplyNet ? 0 : data.netAmount
  prevAdjustment.agency = data.agency
  prevAdjustment.referenceNumber = data.referenceNumber
  prevAdjustment.reservationNumber = data.reservationNumber
  return editedAdjustment
}

function bindTransactions(event: any[]) {
  removeUnbindSelectedTransactions(event)
  const adjustmentList = [...LocalBindTransactionList.value].filter((item: any) => item.adjustment)
  LocalBindTransactionList.value = [...event, ...adjustmentList]
}

function removeUnbindSelectedTransactions(newTransactions: any[]) {
  const ids = new Set(newTransactions.map(item => item.id))
  selectedElements.value = selectedElements.value.filter(item => ids.has(item.id) || !item.id)
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

  // TODO: FALTA HACER FILTRADO LOCAL
  // Aqui primero el filtro debe ser local y luego ya si con el api
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

function onDoubleClick(item: any) {
  if (item.manual || item.parent) {
    const id = Object.prototype.hasOwnProperty.call(item, 'id') ? item.id : item
    selectedTransactionId.value = id
    editManualTransactionDialogVisible.value = true
  }
  if (item.adjustment) {
    selectedTransaction.value = item
    editAdjustmentTransactionDialogVisible.value = true
  }
}

async function onRowRightClick(event: any) {
  contextMenu.value.hide()
  contextMenuTransaction.value = event.data
  contextMenu.value.show(event.originalEvent)
}

function onChangeLocalPagination(event: any) {
  paginationFirstLocal.value = event.first
  pagination.value.limit = event.rows
}

function recalculateTotals() {
  subTotals.value = { amount: 0, commission: 0, net: 0 }

  for (let i = 0; i < LocalBindTransactionList.value.length; i++) {
    const localTransaction = LocalBindTransactionList.value[i]
    if (localTransaction.adjustment && localTransaction.transactionSubCategory.negative) {
      subTotals.value.amount -= localTransaction.amount
      subTotals.value.commission -= localTransaction.commission
      subTotals.value.net -= localTransaction.netAmount
    }
    else {
      subTotals.value.amount += localTransaction.amount
      subTotals.value.commission += localTransaction.commission
      subTotals.value.net += localTransaction.netAmount
    }
  }
}

watch(() => LocalBindTransactionList.value, async (newValue) => {
  pagination.value.totalElements = newValue?.length ?? 0
  // recalcular totales si cambia la lista
  recalculateTotals()
})

onMounted(() => {
  loadDefaultStatus()
})
</script>

<template>
  <div style="max-height: 100vh; height: 90vh">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      New Hotel Payment
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
            :model="data.manageHotel"
            :suggestions="HotelList"
            @change="($event) => {
              if (item.manageHotel && $event.id !== item.manageHotel.id) {
                clearTransactions()
                // Limpiar selector de bank account
                onUpdate('manageBankAccount', null)
                item.manageBankAccount = null
              }
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
            :disabled="!data.manageHotel"
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
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll && !loadingDefaultStatus" id="autocomplete"
            field="fullName" item-value="id" disabled :model="data.status" :suggestions="StatusList"
            @change="($event) => {
              onUpdate('status', $event)
            }" @load="($event) => getStatusList()"
          >
            <template #option="props">
              <span>{{ props.item.fullName }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
      </EditFormV2>
    </div>
    <!-- <pre>{{ selectedElements }}</pre> -->
    <DynamicTable
      :data="paginatedData"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      show-local-pagination
      :selected-items="selectedElements"
      @on-change-filter="parseDataTableFilter"
      @on-sort-field="onSortField"
      @update:selected-items="onMultipleSelect($event)"
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
      <template #pagination>
        <Paginator
          :rows="Number(pagination.limit) || 50"
          :total-records="pagination.totalElements"
          :rows-per-page-options="[10, 20, 30, 50]"
          @page="onChangeLocalPagination($event)"
        />
      </template>
    </DynamicTable>
    <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
      <!-- <Badge
        v-tooltip.top="'Total selected transactions amount'" :value="computedTransactionAmountSelected"
      /> -->
      <div>
        <Button v-tooltip.top="'Bind Transaction'" class="w-3rem" :disabled="item.manageHotel == null" icon="pi pi-link" @click="() => { transactionsToBindDialogOpen = true }" />
        <Button v-tooltip.top="'Add Adjustment'" class="w-3rem ml-1" icon="pi pi-dollar" @click="openNewAdjustmentTransactionDialog()" />
        <Button v-tooltip.top="'Save'" class="w-3rem ml-1" icon="pi pi-save" :loading="loadingSaveAll" @click="forceSave = true" />
        <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="() => { navigateTo('/vcc-management/hotel-payment') }" />
      </div>
    </div>
    <div v-if="transactionsToBindDialogOpen">
      <HotelPaymentBindTransactionsDialog
        :close-dialog="() => { transactionsToBindDialogOpen = false }" header="Bind Transaction" :selected-items="computedLocalBindTransactionList"
        :open-dialog="transactionsToBindDialogOpen" :current-hotel-payment="item" :valid-collection-status-list="collectionStatusList"
        @update:list-items="($event) => bindTransactions($event)"
        @update:status-list="($event) => collectionStatusList = $event"
      />
    </div>
    <VCCEditManualTransaction
      :open-dialog="editManualTransactionDialogVisible" :transaction-id="selectedTransactionId" @on-close-dialog="($event) => {
        editManualTransactionDialogVisible = false
        if (!$event) {
          updateCurrentManualTransaction()
        }
      }"
    />
    <VCCNewAdjustmentTransaction
      is-local :open-dialog="newAdjustmentTransactionDialogVisible"
      @on-close-dialog="onCloseNewAdjustmentTransactionDialog($event)" @on-save-local="($event) => bindAdjustment($event)"
    />
    <VCCEditAdjustmentTransaction
      :open-dialog="editAdjustmentTransactionDialogVisible" :transaction-id="selectedTransactionId" :transaction="selectedTransaction" @on-close-dialog="($event) => {
        editAdjustmentTransactionDialogVisible = false
        if (!$event) {
          getList()
        }
        recalculateTotals()
      }" @on-save-local="($event) => updateCurrentAdjustmentTransaction($event)"
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
