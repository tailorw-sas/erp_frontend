<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import { useToast } from 'primevue/usetoast'
import ContextMenu from 'primevue/contextmenu'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { GenericService } from '~/services/generic-services'
import { formatCardNumber } from '~/components/vcc/vcc_utils'

const props = defineProps({
  bankReconciliationId: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['update:detailsAmount'])

const listItems = ref<any[]>([])
const toast = useToast()
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

const sClassMap: IStatusClass[] = [
  { status: 'Sent', class: 'vcc-text-sent' },
  { status: 'Created', class: 'vcc-text-created' },
  { status: 'Received', class: 'vcc-text-received' },
  { status: 'Declined', class: 'vcc-text-declined' },
  { status: 'Paid', class: 'vcc-text-paid' },
  { status: 'Cancelled', class: 'vcc-text-cancelled' },
  { status: 'Reconciled', class: 'vcc-text-reconciled' },
  { status: 'Refund', class: 'vcc-text-refund' },
]

const columns: IColumn[] = [
  { field: 'id', header: 'Id', type: 'text' },
  { field: 'enrolleCode', header: 'Enrollee Code', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'text' },
  { field: 'creditCardType', header: 'CC Type', type: 'text' },
  { field: 'cardNumber', header: 'Card Number', type: 'text' },
  { field: 'referenceNumber', header: 'Reference Number', type: 'text' },
  { field: 'amount', header: 'Amount', type: 'number' },
  { field: 'commission', header: 'Commission', type: 'number' },
  { field: 'netAmount', header: 'T.Amount', type: 'number' },
  { field: 'checkIn', header: 'Trans Date', type: 'date' },
  { field: 'categoryType', header: 'Trans Cat Type', type: 'text' },
  { field: 'status', header: 'Status', type: 'custom-badge', frozen: true, statusClassMap: sClassMap, showFilter: false },
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

async function resetListItems() {
  payload.value.page = 0
  getList()
}

function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [{
      key: 'reconciliation.id',
      operator: 'EQUALS',
      value: props.bankReconciliationId,
      logicalOperation: 'AND'
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

function getStatusFilter(element: any) {
  if (element && Array.isArray(element.constraints) && element.constraints.length > 0) {
    for (const iterator of element.constraints) {
      if (iterator.value) {
        const ketTemp = 'status.name'
        let operator: string = ''
        if ('matchMode' in iterator) {
          if (typeof iterator.matchMode === 'object') {
            operator = iterator.matchMode.id.toUpperCase()
          }
          else {
            operator = iterator.matchMode.toUpperCase()
          }
        }
        if (Array.isArray(iterator.value) && iterator.value.length > 0) {
          const objFilter: IFilter = {
            key: ketTemp,
            operator,
            value: iterator.value.length > 0 ? [...iterator.value.map((item: any) => item.name)] : [],
            logicalOperation: 'AND',
          }
          return objFilter
        }
      }
    }
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]

  const statusFilter: any = getStatusFilter(payloadFilter.status)
  if (statusFilter) {
    const index = payload.value.filter.findIndex((filter: IFilter) => filter.key === statusFilter.key)
    if (index !== -1) {
      payload.value.filter[index] = statusFilter
    }
    else {
      payload.value.filter.push(statusFilter)
    }
  }

  getList()
}

async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { transactionSearchResponse, transactionTotalResume } = response
    const { data: dataList, page, size, totalElements, totalPages } = transactionSearchResponse

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = iterator.status.name
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'agency') && iterator.agency) {
        iterator.agency = `${iterator.agency.code} - ${iterator.agency.name}`
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'categoryType') && iterator.categoryType) {
        iterator.categoryType = `${iterator.categoryType.code} - ${iterator.categoryType.name}`
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
      if (Object.prototype.hasOwnProperty.call(iterator, 'cardNumber') && iterator.cardNumber) {
        iterator.cardNumber = formatCardNumber(String(iterator.cardNumber))
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
  }
}

async function onRowRightClick(event: any) {
  contextMenu.value.hide()
  contextMenuTransaction.value = event.data
  contextMenu.value.show(event.originalEvent)
}

async function unbindTransactions() {
  try {
    const transactionsIds = [contextMenuTransaction.value.id]
    const payload: { [key: string]: any } = {}
    payload.bankReconciliation = props.bankReconciliationId
    payload.transactionsIds = transactionsIds

    const response: any = await GenericService.create('creditcard', 'bank-reconciliation/unbind', payload)
    if (response && response.detailsAmount) {
      emit('update:detailsAmount', response.detailsAmount)
    }
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

<style scoped lang="scss">

</style>
