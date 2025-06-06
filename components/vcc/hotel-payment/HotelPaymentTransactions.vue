<script setup lang="ts">
import { nextTick, onMounted, ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import { useToast } from 'primevue/usetoast'
import ContextMenu from 'primevue/contextmenu'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { GenericService } from '~/services/generic-services'
import { formatNumber } from '~/pages/payment/utils/helperFilters'

const props = defineProps({
  hotelPaymentId: {
    type: String,
    required: true
  },
  hideBindTransactionMenu: {
    type: Boolean,
    required: false,
    default: false,
  }
})

const emit = defineEmits(['update:list'])
const { status, data } = useAuth()
const isAdmin = (data.value?.user as any)?.isAdmin === true
const authStore = useAuthStore()
const listItems = ref<any[]>([])
const toast = useToast()
const contextMenu = ref()
const contextMenuTransaction = ref()
const transactionHistoryDialogVisible = ref<boolean>(false)
const subTotals: any = ref({ amount: 0, commission: 0, net: 0 })

enum MenuType {
  unBind
}

const allMenuListItems = [
  {
    label: 'Status History',
    icon: 'pi pi-history',
    iconSvg: '',
    width: '14px',
    height: '14px',
    command: () => { transactionHistoryDialogVisible.value = true },
    disabled: false,
  },
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

const sClassMap: IStatusClass[] = [
  { status: 'Sent', class: 'vcc-text-sent' },
  { status: 'Created', class: 'vcc-text-created' },
  { status: 'Received', class: 'vcc-text-received' },
  { status: 'Declined', class: 'vcc-text-declined' },
  { status: 'Paid', class: 'vcc-text-paid' },
  { status: 'Cancelled', class: 'vcc-text-cancelled' },
  { status: 'Canceled', class: 'vcc-text-cancelled' },
  { status: 'Reconciled', class: 'vcc-text-reconciled' },
  { status: 'Reconcilied', class: 'vcc-text-reconciled' },
  { status: 'Refund', class: 'vcc-text-refund' },
]

const columns: IColumn[] = [
  { field: 'id', header: 'Id', type: 'text', width: '20px', minWidth: '60px', maxWidth: '60px' },
  { field: 'enrolleCode', header: 'Enrollee Code', type: 'text', width: '20px', minWidth: '120px', maxWidth: '120px' },
  { field: 'agency', header: 'Agency', type: 'text', width: '20px', minWidth: '140px', maxWidth: '150px' },
  { field: 'creditCardType', header: 'CC Type', type: 'text', width: '20px', minWidth: '140px', maxWidth: '150px' },
  { field: 'cardNumber', header: 'Card Number', type: 'text', width: '20px', minWidth: '120px', maxWidth: '130px' },
  { field: 'referenceNumber', header: 'Reference Number', type: 'text', width: '220px', minWidth: '120px', maxWidth: '220px' },
  { field: 'amount', header: 'Amount', type: 'number', width: '20px', minWidth: '120px', maxWidth: '200px' },
  { field: 'commission', header: 'Comm', type: 'number', width: '20px', minWidth: '80px', maxWidth: '80px' },
  { field: 'netAmount', header: 'T.Amount', type: 'number', width: '20px', minWidth: '120px', maxWidth: '200px' },
  { field: 'checkIn', header: 'Trans Date', type: 'date', width: '20px', minWidth: '90px', maxWidth: '100px' },
  { field: 'methodType', header: 'Method', type: 'text', width: '20px', minWidth: '60px', maxWidth: '70px' },
  { field: 'categoryType', header: 'Type', type: 'text', width: '20px', minWidth: '80px', maxWidth: '120px' },
  { field: 'authNo', header: 'Auth No.', type: 'text', width: '20px', minWidth: '80px', maxWidth: '100px' },
  { field: 'couponNumber', header: 'Coupon No.', type: 'text', width: '20px', minWidth: '80px', maxWidth: '120px' },
  { field: 'status', header: 'Status', type: 'slot-select', frozen: true, statusClassMap: sClassMap, showFilter: false },
]

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

async function canEditHotelPayment() {
  return (status.value === 'authenticated' && (isAdmin || authStore.can(['HOTEL-PAYMENT:EDIT'])))
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [{
      key: 'hotelPayment.id',
      operator: 'EQUALS',
      value: props.hotelPaymentId,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  payload.value = newPayload
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
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
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { transactionSearchResponse } = response
    const { data: dataList, page, size, totalElements, totalPages } = transactionSearchResponse

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'agency') && iterator.agency) {
        iterator.agency = `${iterator.agency.code} - ${iterator.agency.name}`
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'categoryType') && iterator.categoryType) {
        iterator.categoryType = `${iterator.categoryType.name}`
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'creditCardType') && iterator.creditCardType) {
        iterator.creditCardType = `${iterator.creditCardType.code} - ${iterator.creditCardType.name}`
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'parent')) {
        iterator.parent = (iterator.parent) ? String(iterator.parent?.id) : null
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'id')) {
        iterator.id = String(iterator.id)
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

    listItems.value = [...listItems.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    subTotals.value = { ...count }
  }
}

async function onRowRightClick(event: any) {
  contextMenu.value.hide()
  contextMenuTransaction.value = event.data
  menuListItems.value = [...allMenuListItems]
  if (props.hideBindTransactionMenu || !await canEditHotelPayment()) {
    menuListItems.value = allMenuListItems.filter((item: any) => item.type !== MenuType.unBind)
  }
  if (menuListItems.value.length > 0) {
    await nextTick()
    contextMenu.value.show(event.originalEvent)
  }
}

async function unbindTransactions() {
  try {
    const transactionsIds = [contextMenuTransaction.value.id]
    const payload: { [key: string]: any } = {}
    payload.hotelPaymentId = props.hotelPaymentId
    payload.transactionsIds = transactionsIds

    await GenericService.create('creditcard', 'hotel-payment/unbind', payload)
    emit('update:list')
    toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Transaction ${transactionsIds.join(', ')} was unbounded successfully`, life: 10000 })
    getList()
    // Emit reload detail amount
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
}

onMounted(() => {
  searchAndFilter()
})
</script>

<template>
  <div class="p-0 m-0">
    <DynamicTable
      :data="listItems"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      @on-change-pagination="payloadOnChangePage = $event"
      @on-change-filter="parseDataTableFilter"
      @on-list-item="resetListItems"
      @on-sort-field="onSortField"
      @on-row-right-click="onRowRightClick"
    >
      <template #column-status="{ data, column }">
        <Badge
          v-tooltip.top="data.status.name.toString()"
          :value="data.status.name"
          :class="column.statusClassMap?.find((e: any) => e.status === data.status.name)?.class"
        />
      </template>
      <template #datatable-footer>
        <ColumnGroup type="footer" class="flex align-items-center">
          <Row>
            <Column footer="Totals:" :colspan="6" footer-style="text-align:right" />
            <Column :footer="formatNumber(subTotals.amount)" />
            <Column :footer="formatNumber(subTotals.commission)" />
            <Column :footer="formatNumber(subTotals.net)" />
            <Column :colspan="6" />
          </Row>
        </ColumnGroup>
      </template>
    </DynamicTable>
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
    <div v-if="transactionHistoryDialogVisible">
      <TransactionStatusHistoryDialog :close-dialog="() => { transactionHistoryDialogVisible = false }" :open-dialog="transactionHistoryDialogVisible" :selected-transaction="contextMenuTransaction" :s-class-map="sClassMap" />
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>
