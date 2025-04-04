<script setup lang="ts">
import { type PropType, ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import dayjs from 'dayjs'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { formatNumber } from '~/pages/payment/utils/helperFilters'
import type { IData } from '~/components/table/interfaces/IModelData'

const props = defineProps({
  header: {
    type: String,
    required: true
  },
  openDialog: {
    type: Boolean,
    required: true
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  currentHotelPayment: {
    type: Object,
    required: true
  },
  selectedItems: {
    type: Array as PropType<any[]>,
    required: false,
    default: () => []
  },
  validCollectionStatusList: {
    type: Array as PropType<any[]>,
    required: true
  }
})

const emit = defineEmits(['update:listItems', 'update:statusList'])

const subTotals: any = ref({ amount: 0, commission: 0, net: 0 })
const BindTransactionList = ref<any[]>([])
const collectionStatusReconciledList = ref<any[]>([])
const dialogVisible = ref(props.openDialog)
const selectedElements = ref<any[]>([])
const totalAmountSelected = ref(0)
const disabledConfirmButton = ref(true)

const filterToSearch = ref<IData>({
  from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
  to: new Date(),
})

const confStatusListApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'manage-transaction-status',
})

const activeStatusFilter: IFilter[] = [
  {
    key: 'status',
    operator: 'EQUALS',
    value: 'ACTIVE',
    logicalOperation: 'AND'
  }
]

const sClassMap: IStatusClass[] = [
  { status: 'Sent', class: 'vcc-text-sent' },
  { status: 'Created', class: 'vcc-text-created' },
  { status: 'Received', class: 'vcc-text-received' },
  { status: 'Declined', class: 'vcc-text-declined' },
  { status: 'Paid', class: 'vcc-text-paid' },
  { status: 'Cancelled', class: 'vcc-text-cancelled' },
  { status: 'Canceled', class: 'vcc-text-cancelled' },
  { status: 'Reconciled', class: 'vcc-text-reconciled' },
  { status: 'Refund', class: 'vcc-text-refund' },
]

const columns: IColumn[] = [
  { field: 'id', header: 'Id', type: 'text' },
  { field: 'merchant', header: 'Merchant', type: 'select', objApi: { moduleApi: 'creditcard', uriApi: 'manage-merchant', keyValue: 'description', filter: activeStatusFilter }, sortable: true },
  { field: 'creditCardType', header: 'CC Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-credit-card-type', filter: activeStatusFilter }, sortable: true },
  { field: 'referenceNumber', header: 'Reference', type: 'text', maxWidth: '250px' },
  { field: 'checkIn', header: 'Trans Date', type: 'date' },
  { field: 'amount', header: 'Amount', type: 'number' },
  { field: 'commission', header: 'Commission', type: 'number' },
  { field: 'netAmount', header: 'T.Amount', type: 'number' },
  { field: 'status', header: 'Status', type: 'slot-select', statusClassMap: sClassMap, objApi: { moduleApi: 'creditcard', uriApi: 'manage-transaction-status', filter: activeStatusFilter } },
]

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Transactions',
  moduleApi: 'creditcard',
  uriApi: 'transactions',
  loading: false,
  actionsAsMenu: false,
  selectionMode: 'multiple',
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

// FUNCTIONS ---------------------------------------------------------------------------------------------

async function getCollectionStatusList() {
  if (collectionStatusReconciledList.value.length > 0) {
    return collectionStatusReconciledList.value
  }
  try {
    const payload = {
      filter: [
        {
          key: 'reconciledStatus',
          operator: 'EQUALS',
          value: true,
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
      pageSize: 50,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confStatusListApi.moduleApi, confStatusListApi.uriApi, payload)
    const { data: dataList } = response
    collectionStatusReconciledList.value = dataList
    emit('update:statusList', dataList)
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
  return collectionStatusReconciledList.value
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

    const existingIds = new Set(BindTransactionList.value.map(item => item.id))

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
      if (Object.prototype.hasOwnProperty.call(iterator, 'parent')) {
        iterator.parent = (iterator.parent) ? String(iterator.parent?.id) : null
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'id')) {
        iterator.id = String(iterator.id)
        iterator.referenceId = String(iterator.id)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        count.amount += iterator.amount
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'commission')) {
        count.commission += iterator.commission
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'netAmount')) {
        count.net += iterator.netAmount
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

async function handleSave() {
  emit('update:listItems', selectedElements.value)
  props.closeDialog()
}

async function onSelectItem(event: any) {
  selectedElements.value = event
  if (selectedElements.value.length === 0) {
    disabledConfirmButton.value = true
    return totalAmountSelected.value = 0
  }
  totalAmountSelected.value = selectedElements.value.reduce((total, item) => {
    // Verificamos si invoiceAmount es un número válido
    if (typeof item.netAmount === 'number' && !Number.isNaN(item.netAmount)) {
      return total + item.netAmount
    }
    else {
      return total
    }
  }, 0) // 0 es el valor inicial
  disabledConfirmButton.value = false
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'status') {
      event.sortField = 'status.name'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function searchAndFilter() {
  await getCollectionStatusList() // Solo se llama al servicio la primera vez
  const statusIds = collectionStatusReconciledList.value.map((elem: any) => elem.id)

  const newPayload: IQueryRequest = {
    filter: [{
      key: 'hotel.id',
      operator: 'EQUALS',
      value: props.currentHotelPayment.manageHotel.id,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }, {
      key: 'hotelPayment',
      operator: 'IS_NULL',
      value: '',
      logicalOperation: 'AND',
      type: 'filterSearch'
    }, {
      key: 'status.id',
      operator: 'IN',
      value: statusIds,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  newPayload.filter = [...newPayload.filter, ...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.from) {
    newPayload.filter = [...newPayload.filter, {
      key: 'transactionDate',
      operator: 'GREATER_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    }]
  }
  if (filterToSearch.value.to) {
    newPayload.filter = [...newPayload.filter, {
      key: 'transactionDate',
      operator: 'LESS_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    }]
  }
  payload.value = newPayload
  getList()
}

function clearFilterToSearch() {
  filterToSearch.value = {
    from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
    to: new Date(),
  }
  searchAndFilter()
}
// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})

onMounted(async () => {
  selectedElements.value = [...props.selectedItems]
  collectionStatusReconciledList.value = [...props.validCollectionStatusList]
  searchAndFilter()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-screen"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true"
    :style="{ width: '80%' }"
    :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
    :pt="{
      root: {
        class: 'custom-dialog-history',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
    }"
    @hide="closeDialog()"
  >
    <div class="mt-4" />
    <div class="card p-0 mb-0">
      <Accordion :active-index="0" class="mb-2">
        <AccordionTab>
          <template #header>
            <div class="text-white font-bold custom-accordion-header">
              Filters
            </div>
          </template>
          <div class="flex gap-4 flex-column lg:flex-row">
            <div class="flex align-items-center gap-2" style=" z-index:5 ">
              <label class="filter-label font-bold" for="">From:</label>
              <div class="w-full" style=" z-index:5 ">
                <Calendar
                  v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                  show-icon icon-display="input" class="w-full" :max-date="new Date()"
                />
              </div>
            </div>
            <div class="flex align-items-center gap-2">
              <label class="filter-label font-bold" for="">To:</label>
              <div class="w-full">
                <Calendar
                  v-model="filterToSearch.to" date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                  icon-display="input" class="w-full" :max-date="new Date()" :min-date="filterToSearch.from"
                />
              </div>
            </div>
            <div class="flex align-items-center">
              <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" @click="searchAndFilter" />
              <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" @click="clearFilterToSearch" />
            </div>
          </div>
        </AccordionTab>
      </Accordion>
    </div>
    <DynamicTable
      :data="BindTransactionList"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      :selected-items="selectedElements"
      @on-change-pagination="payloadOnChangePage = $event"
      @on-change-filter="parseDataTableFilter"
      @on-sort-field="onSortField"
      @update:selected-items="onSelectItem($event)"
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
            <Column :colspan="1" />
          </Row>
        </ColumnGroup>
      </template>
    </DynamicTable>
    <div class="flex justify-content-between align-items-center mt-3 card p-2 bg-surface-500">
      <div>
        <Badge v-tooltip.top="'Total selected transactions amount'" :value="`Transaction Amount Selected: $${formatNumber(totalAmountSelected)}`" class="mr-1" />
      </div>
      <div>
        <Button v-tooltip.top="'Save'" class="w-3rem ml-1" icon="pi pi-check" :loading="options.loading" :disabled="BindTransactionList.length === 0 || disabledConfirmButton" @click="handleSave()" />
        <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="closeDialog()" />
      </div>
    </div>
  </Dialog>
</template>

<style lang="scss" scoped>
.filter-label {
  min-width: 55px;
  text-align: end;
}
</style>
