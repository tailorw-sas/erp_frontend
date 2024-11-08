<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { z } from 'zod'
import dayjs from 'dayjs'
import type { Ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import ContextMenu from 'primevue/contextmenu'
import { v4 } from 'uuid'
import BankPaymentMerchantBindTransactionsDialog
  from '~/components/vcc/bank-reconciliation/BankPaymentMerchantBindTransactionsDialog.vue'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { formatNumber } from '~/pages/payment/utils/helperFilters'
import { parseFormattedNumber } from '~/utils/helpers'

const toast = useToast()
const transactionsToBindDialogOpen = ref<boolean>(false)
const HotelList = ref<any[]>([])
const MerchantBankAccountList = ref<any[]>([])
const LocalBindTransactionList = ref<any[]>([])
const collectionStatusRefundReceivedList = ref<any[]>([])
const loadingSaveAll = ref(false)
const forceSave = ref(false)
const refForm: Ref = ref(null)
const formReload = ref(0)
const subTotals: any = ref({ amount: 0 })
const selectedElements = ref<any[]>([])
const idItem = ref('')
const newAdjustmentTransactionDialogVisible = ref(false)
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
  uriApi: 'bank-reconciliation',
})

const item = ref({
  merchantBankAccount: null,
  hotel: null,
  amount: 0,
  paidDate: '',
  remark: '',
} as GenericObject)

const itemTemp = ref({
  merchantBankAccount: null,
  hotel: null,
  amount: 0,
  paidDate: '',
  remark: '',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'merchantBankAccount',
    header: 'Bank Account',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('bank account'),
  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'amount',
    header: 'Amount',
    dataType: 'number',
    disabled: false,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-3 required',
    headerClass: 'mb-1',
    validation: z.number({
      invalid_type_error: 'The amount field must be a number',
      required_error: 'The amount field is required',
    })
      .refine(val => Number.parseFloat(String(val)) >= 0, {
        message: 'The amount must be zero or greater',
      })
  },
  {
    field: 'paidDate',
    header: 'Paid Date',
    dataType: 'date',
    class: 'field col-12 md:col-3 required ',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The paid date field is required',
      invalid_type_error: 'The paid date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The paid date field cannot be greater than current date')
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
  { field: 'amount', header: 'Amount', type: 'text' },
  { field: 'commission', header: 'Commission', type: 'text' },
  { field: 'netAmount', header: 'T.Amount', type: 'text' },
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
  const totalSelectedAmount = selectedElements.value.length > 0 ? selectedElements.value.reduce((sum, item) => sum + parseFormattedNumber(item.netAmount), 0) : 0
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

async function getMerchantBankAccountList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'accountNumber',
        operator: 'LIKE',
        value: query,
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
    const response: any = await GenericService.search('creditcard', 'manage-merchant-bank-account', payload)
    const { data: dataList } = response
    MerchantBankAccountList.value = []

    for (const iterator of dataList) {
      const merchantNames = iterator.managerMerchant.map((item: any) => item.description).join(' - ')
      MerchantBankAccountList.value = [...MerchantBankAccountList.value, { id: iterator.id, name: `${merchantNames} - ${iterator.description} - ${iterator.accountNumber}`, status: iterator.status, managerMerchant: iterator.managerMerchant, creditCardTypes: iterator.creditCardTypes }]
    }
  }
  catch (error) {
    console.error('Error loading merchant bank account list:', error)
  }
}

function unbindTransactions() {
  const transactionId = String(contextMenuTransaction.value.id)
  LocalBindTransactionList.value = LocalBindTransactionList.value.filter((item: any) => item.id !== transactionId)
  selectedElements.value = selectedElements.value.filter((item: any) => item.id !== transactionId)
  subTotals.value.amount -= parseFormattedNumber(contextMenuTransaction.value.amount)
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    const payload: { [key: string]: any } = { ...item }
    payload.paidDate = payload.paidDate ? dayjs(payload.paidDate).format('YYYY-MM-DDTHH:mm:ss') : ''
    payload.hotel = Object.prototype.hasOwnProperty.call(payload.hotel, 'id') ? payload.hotel.id : payload.hotel
    payload.merchantBankAccount = Object.prototype.hasOwnProperty.call(payload.merchantBankAccount, 'id') ? payload.merchantBankAccount.id : payload.merchantBankAccount
    payload.detailsAmount = subTotals.value.amount

    if (LocalBindTransactionList.value.length > 0) {
      payload.transactions = LocalBindTransactionList.value.filter((t: any) => !t.adjustment).map((i: any) => i.id)
      const adjustmentTransactions = LocalBindTransactionList.value.filter((t: any) => t.adjustment)
      payload.adjustmentTransactions = adjustmentTransactions.map((elem: any) => ({
        agency: typeof elem.agency === 'object' ? elem.agency.id : elem.agency,
        transactionCategory: typeof elem.transactionCategory === 'object' ? elem.transactionCategory.id : elem.transactionCategory,
        transactionSubCategory: typeof elem.transactionSubCategory === 'object' ? elem.transactionSubCategory.id : elem.transactionSubCategory,
        amount: parseFormattedNumber(elem.amount),
        reservationNumber: elem.reservationNumber,
        referenceNumber: elem.referenceNumber
      }))
    }
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    if (response && response.id) {
      // Guarda el id del elemento creado
      idItem.value = response.id
      LocalBindTransactionList.value = []
      toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Bank Payment of Merchant ${response.reconciliationId ?? ''} was created successfully`, life: 10000 })
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was not successful', life: 10000 })
    }
  }
}

async function saveItem(item: { [key: string]: any }) {
  if (subTotals.value.amount > item.amount) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Details amount must not exceed the reconciliation amount.', life: 10000 })
    return
  }
  // let successOperation = true
  loadingSaveAll.value = true
  try {
    await createItem(item)
    await navigateTo({ path: `/vcc-management/bank-reconciliation/bank-payment-of-merchant/${idItem.value}`, params: { id: idItem.value } })
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
  data.id = v4() // id temporal para poder eliminar de forma local
  data.checkIn = dayjs().format('YYYY-MM-DD')
  subTotals.value.amount += data.amount
  data.amount = formatNumber(data.amount)
  data.commission = formatNumber(0)
  data.netAmount = formatNumber(data.amount)
  data.adjustment = true
  if (data.transactionCategory) {
    data.categoryType = data.transactionCategory
  }
  if (data.transactionSubCategory) {
    data.subCategoryType = data.transactionSubCategory
  }
  return data
}

function bindTransactions(event: any[]) {
  removeUnbindSelectedTransactions(event)
  const adjustmentList = [...LocalBindTransactionList.value].filter((item: any) => item.adjustment)
  LocalBindTransactionList.value = [...event, ...adjustmentList]
  const totalAmount = LocalBindTransactionList.value.reduce((sum, item) => sum + parseFormattedNumber(item.amount), 0)
  subTotals.value.amount = totalAmount
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

async function onRowRightClick(event: any) {
  contextMenu.value.hide()
  contextMenuTransaction.value = event.data
  contextMenu.value.show(event.originalEvent)
}

function onChangeLocalPagination(event: any) {
  paginationFirstLocal.value = event.first
  pagination.value.limit = event.rows
}

watch(() => LocalBindTransactionList.value, async (newValue) => {
  if (newValue && !idItem.value) {
    pagination.value.totalElements = newValue?.length ?? 0
  }
})
</script>

<template>
  <div style="max-height: 100vh; height: 90vh">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      Bank Payment of Merchant
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
        <template #field-amount="{ item: data, onUpdate, fields: listFields, field }">
          <InputNumber
            v-if="!loadingSaveAll"
            v-model="data.amount"
            show-clear
            mode="decimal"
            :disabled="listFields.find((f: FieldDefinitionType) => f.field === field)?.disabled || false"
            :min-fraction-digits="2"
            :max-fraction-digits="4"
            @update:model-value="($event) => {
              onUpdate('amount', $event)
              item.amount = $event
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-paidDate="{ item: data, onUpdate, fields: listFields, field }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.paidDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            :disabled="listFields.find((f: FieldDefinitionType) => f.field === field)?.disabled || false"
            @update:model-value="($event) => {
              onUpdate('paidDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" />
        </template>
        <!-- Agency -->
        <template #field-hotel="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.hotel"
            :suggestions="HotelList"
            @change="($event) => {
              onUpdate('hotel', $event)
              item.hotel = $event
            }"
            @load="($event) => getHotelList($event)"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>

        <template #field-merchantBankAccount="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.merchantBankAccount"
            :suggestions="[...MerchantBankAccountList]"
            @change="($event) => {
              onUpdate('merchantBankAccount', $event)
              item.merchantBankAccount = $event
            }"
            @load="($event) => getMerchantBankAccountList($event)"
          />
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
    >
      <template #datatable-footer>
        <ColumnGroup type="footer" class="flex align-items-center">
          <Row>
            <Column footer="Totals:" :colspan="9" footer-style="text-align:right" />
            <Column :footer="formatNumber(subTotals.amount)" />
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
        <Button v-tooltip.top="'Bind Transaction'" class="w-3rem" :disabled="item.amount <= 0 || item.merchantBankAccount == null || item.hotel == null" icon="pi pi-link" @click="() => { transactionsToBindDialogOpen = true }" />
        <Button v-tooltip.top="'Add Adjustment'" class="w-3rem ml-1" icon="pi pi-dollar" @click="openNewAdjustmentTransactionDialog()" />
        <Button v-tooltip.top="'Save'" class="w-3rem ml-1" icon="pi pi-save" :loading="loadingSaveAll" @click="forceSave = true" />
        <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="() => { navigateTo('/vcc-management/bank-reconciliation') }" />
      </div>
    </div>
    <div v-if="transactionsToBindDialogOpen">
      <BankPaymentMerchantBindTransactionsDialog
        :close-dialog="() => { transactionsToBindDialogOpen = false }" header="Transaction Items" :selected-items="computedLocalBindTransactionList"
        :open-dialog="transactionsToBindDialogOpen" :current-bank-payment="item" :valid-collection-status-list="collectionStatusRefundReceivedList"
        @update:list-items="($event) => bindTransactions($event)"
        @update:status-list="($event) => collectionStatusRefundReceivedList = $event"
      />
    </div>
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
