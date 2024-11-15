<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import ContextMenu from 'primevue/contextmenu'
import dayjs from 'dayjs'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import { formatNumber } from '~/pages/payment/utils/helperFilters'
// VARIABLES -----------------------------------------------------------------------------------------
const listItems = ref<any[]>([])
const loadingSaveAll = ref(false)
const idItemToLoadFirstTime = ref('')
const bankReconciliationHistoryDialogVisible = ref<boolean>(false)
const contextMenuTransaction = ref()
const allDefaultItem = { id: 'All', name: 'All', code: 'All' }
const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  merchantBankAccount: [allDefaultItem],
  ccType: [allDefaultItem],
  hotel: [allDefaultItem],
  status: [allDefaultItem],
  from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
  to: new Date(),
})
const accordionLoading = ref({
  hotel: false,
  merchantBankAccount: false,
  status: false,
})
const contextMenu = ref()

const menuListItems = [
  {
    label: 'Status History',
    icon: 'pi pi-history',
    command: () => { bankReconciliationHistoryDialogVisible.value = true },
    disabled: false,
  }
]

const hotelList = ref<any[]>([])
const statusList = ref<any[]>([])
const MerchantBankAccountList = ref<any[]>([])

const confStatusListApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'manage-reconcile-transaction-status',
})

const confHotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const legend = ref(
  [
    {
      name: 'Created',
      color: '#FF8D00',
      colClass: 'pr-3',
    },
    {
      name: 'Completed',
      color: '#006400',
      colClass: 'pr-3',
    },
    {
      name: 'Cancelled',
      color: '#888888',
      colClass: 'pr-3',
    }
  ]
)
////
const sClassMap: IStatusClass[] = [
  { status: 'Completed', class: 'vcc-text-sent' },
  { status: 'Created', class: 'vcc-text-created' },
  { status: 'Cancelled', class: 'vcc-text-cancelled' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'reconciliationId', header: 'Id', type: 'text' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' }, sortable: true },
  { field: 'merchantBankAccount', header: 'Bank Account', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-merchant-bank-account' }, sortable: true },
  { field: 'amount', header: 'Amount', type: 'text' },
  { field: 'detailsAmount', header: 'Details Amount', type: 'text' },
  { field: 'paidDate', header: 'Date', type: 'date' },
  { field: 'remark', header: 'Remark', type: 'text' },
  { field: 'statusName', header: 'Status', type: 'custom-badge', frozen: true, statusClassMap: sClassMap, objApi: { moduleApi: 'creditcard', uriApi: 'manage-reconcile-transaction-status' }, sortable: true },
]

const subTotals: any = ref({ amount: 0, details: 0 })
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'reconciliationId', name: 'Id' },
]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Management Bank Reconciliation',
  moduleApi: 'creditcard',
  uriApi: 'bank-reconciliation',
  loading: false,
  actionsAsMenu: false,
  expandableRows: true,
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

// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
function goToPaymentOfMerchantInNewTab(item: any) {
  const id = item.hasOwnProperty('id') ? item.id : item
  const url = `/vcc-management/bank-reconciliation/bank-payment-of-merchant/${id}`
  navigateTo({ path: url, params: { id } }, { open: { target: '_blank' } })
}

async function getList() {
  const count = { amount: 0, details: 0 }
  subTotals.value = { ...count }
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'reconcileStatus')) {
        iterator.statusName = iterator.reconcileStatus?.name
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel') && iterator.hotel) {
        iterator.hotel = { id: iterator.hotel.id, name: `${iterator.hotel.code} - ${iterator.hotel.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        count.amount += iterator.amount
        iterator.amount = formatNumber(iterator.amount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'detailsAmount')) {
        count.details += iterator.detailsAmount
        iterator.detailsAmount = formatNumber(iterator.detailsAmount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'merchantBankAccount')) {
        const merchantNames = iterator.merchantBankAccount.managerMerchant.map((item: any) => item.description).join(' - ')
        iterator.merchantBankAccount = { id: iterator.merchantBankAccount.id, name: `${merchantNames} - ${iterator.merchantBankAccount.description} - ${iterator.merchantBankAccount.accountNumber}` }
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

async function resetListItems() {
  payload.value.page = 0
  getList()
}

function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    newPayload.filter = [{
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }]
  }
  else {
    newPayload.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
    // Date
    if (filterToSearch.value.from) {
      newPayload.filter = [...newPayload.filter, {
        key: 'paidDate',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.to) {
      newPayload.filter = [...newPayload.filter, {
        key: 'paidDate',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.merchantBankAccount?.length > 0) {
      const filteredItems = filterToSearch.value.merchantBankAccount.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'merchantBankAccount.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    if (filterToSearch.value.hotel?.length > 0) {
      const filteredItems = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'hotel.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    if (filterToSearch.value.ccType?.length > 0) {
      const filteredItems = filterToSearch.value.ccType.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'creditCardType.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    if (filterToSearch.value.status?.length > 0) {
      const filteredItems = filterToSearch.value.status.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'reconcileStatus.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
  }
  payload.value = newPayload
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value = {
    criteria: null,
    search: '',
    merchantBankAccount: [allDefaultItem],
    ccType: [allDefaultItem],
    hotel: [allDefaultItem],
    status: [allDefaultItem],
    from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
    to: new Date(),
  }
  filterToSearch.value.criteria = ENUM_FILTER[0]
  searchAndFilter()
}

async function getHotelList(query: string = '') {
  try {
    accordionLoading.value.hotel = true
    const payload = {
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
          key: 'isApplyByVCC',
          operator: 'EQUALS',
          value: true,
          logicalOperation: 'AND'
        },
        {
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confHotelListApi.moduleApi, confHotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = [allDefaultItem]
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
  finally {
    accordionLoading.value.hotel = false
  }
}

async function getStatusList(query: string = '') {
  try {
    accordionLoading.value.status = true
    const payload = {
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
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confStatusListApi.moduleApi, confStatusListApi.uriApi, payload)
    const { data: dataList } = response
    statusList.value = [allDefaultItem]
    for (const iterator of dataList) {
      statusList.value = [...statusList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading status list:', error)
  }
  finally {
    accordionLoading.value.status = false
  }
}

async function getMerchantBankAccountList(query: string) {
  try {
    accordionLoading.value.merchantBankAccount = true
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
    MerchantBankAccountList.value = [allDefaultItem]

    for (const iterator of dataList) {
      const merchantNames = iterator.managerMerchant.map((item: any) => item.description).join(' - ')
      MerchantBankAccountList.value = [...MerchantBankAccountList.value, { id: iterator.id, name: `${merchantNames} - ${iterator.description} - ${iterator.accountNumber}` }]
    }
  }
  catch (error) {
    console.error('Error loading merchant bank account list:', error)
  }
  finally {
    accordionLoading.value.merchantBankAccount = false
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

function goToBankPaymentInNewTab(itemId?: string) {
  let url = `/vcc-management/bank-reconciliation/bank-payment-of-merchant`
  if (itemId) {
    url = `${url}?id=${encodeURIComponent(itemId)}`
  }
  window.open(url, '_blank')
}
// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})

// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criteria = ENUM_FILTER[0]
  // getList()
  searchAndFilter()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Management Bank Reconciliation
    </h3>
    <div class="my-2 flex justify-content-end px-0">
      <Button class="ml-2" icon="pi pi-plus" label="New" @click="goToBankPaymentInNewTab()" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 order-0">
      <div class="card p-0 mb-0">
        <Accordion id="accordion" :active-index="0">
          <AccordionTab content-class="p-0 m-0">
            <template #header>
              <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
                <div>
                  Search Fields
                </div>
                <div>
                  <PaymentLegend :legend="legend" />
                </div>
              </div>
            </template>
            <div class="grid">
              <div class="col-12 md:col-6 lg:col-3 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">Hotel:</label>
                    <DebouncedMultiSelectComponent
                      v-if="!loadingSaveAll"
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :max-selected-labels="3"
                      :model="filterToSearch.hotel"
                      :suggestions="hotelList"
                      :loading="accordionLoading.hotel"
                      @change="($event) => {
                        if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                      @load="($event) => getHotelList($event)"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedMultiSelectComponent>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <label class="filter-label font-bold" for="">Bank Account:</label>
                    <DebouncedMultiSelectComponent
                      v-if="!loadingSaveAll"
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :model="filterToSearch.merchantBankAccount"
                      :suggestions="MerchantBankAccountList"
                      :loading="accordionLoading.merchantBankAccount"
                      @change="($event) => {
                        if (!filterToSearch.merchantBankAccount.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.merchantBankAccount = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.merchantBankAccount = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                      @load="($event) => getMerchantBankAccountList($event)"
                    />
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
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
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">Criteria:</label>
                    <div class="w-full" style=" z-index:5 ">
                      <Dropdown
                        v-model="filterToSearch.criteria" :options="[...ENUM_FILTER]" option-label="name"
                        placeholder="Criteria" return-object="false" class="align-items-center w-full" show-clear
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <label class="filter-label font-bold" for="">Search:</label>
                    <div class="w-full">
                      <IconField icon-position="left">
                        <InputText v-model="filterToSearch.search" type="text" style="width: 100% !important;" />
                        <InputIcon class="pi pi-search" />
                      </IconField>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-3 flex pb-0">
                <div class="flex w-full">
                  <div class="flex flex-row w-full">
                    <div class="flex flex-column gap-2 w-full">
                      <div class="flex align-items-center gap-2" style=" z-index:5 ">
                        <label class="filter-label font-bold" for="">Status:</label>
                        <DebouncedMultiSelectComponent
                          v-if="!loadingSaveAll"
                          id="autocomplete"
                          field="name"
                          item-value="id"
                          :max-selected-labels="3"
                          :model="filterToSearch.status"
                          :suggestions="statusList"
                          :loading="accordionLoading.status"
                          @change="($event) => {
                            if (!filterToSearch.status.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                              filterToSearch.status = $event.filter((element: any) => element?.id === 'All')
                            }
                            else {
                              filterToSearch.status = $event.filter((element: any) => element?.id !== 'All')
                            }
                          }"
                          @load="($event) => getStatusList($event)"
                        >
                          <template #option="props">
                            <span>{{ props.item.code }} - {{ props.item.name }}</span>
                          </template>
                        </DebouncedMultiSelectComponent>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex w-full">
                  <div class="flex flex-row w-full">
                    <div class="flex align-items-center ml-2">
                      <Button
                        v-tooltip.top="'Filter'"
                        label=""
                        class="p-button-lg w-3rem h-3rem mr-2"
                        icon="pi pi-search"
                        @click="searchAndFilter"
                      />
                      <Button
                        v-tooltip.top="'Clear'"
                        label=""
                        outlined
                        class="p-button-lg w-3rem h-3rem"
                        icon="pi pi-filter-slash"
                        @click="clearFilterToSearch"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <!--      <div class="legend-container mb-1 grid grid-nogutter">
        <div class="text-white font-bold col-12 lg:col-3">
          Transactions
        </div>
      </div> -->
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
        @on-row-double-click="goToPaymentOfMerchantInNewTab($event)"
      >
        <template #expansion="{ data: item }">
          <!--          <pre>{{item}}</pre> -->
          <BankPaymentTransactions
            :bank-reconciliation-id="item.id" @update:details-amount="($event) => {
              item.detailsAmount = formatNumber($event)
            }"
          />
        </template>
        <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center">
            <Row>
              <Column footer="Totals:" :colspan="4" footer-style="text-align:right" />
              <Column :footer="formatNumber(subTotals.amount)" />
              <Column :footer="formatNumber(subTotals.details)" />
              <Column :colspan="3" />
            </Row>
          </ColumnGroup>
        </template>
      </DynamicTable>
    </div>
    <ContextMenu ref="contextMenu" :model="menuListItems" />
    <div v-if="bankReconciliationHistoryDialogVisible">
      <BankReconciliationStatusHistoryDialog :close-dialog="() => { bankReconciliationHistoryDialogVisible = false }" :open-dialog="bankReconciliationHistoryDialogVisible" :selected-bank-reconciliation="contextMenuTransaction" :s-class-map="sClassMap" />
    </div>
  </div>
</template>

<style scoped>
.filter-label {
  min-width: 55px;
  max-width: 55px;
  text-align: end;
}
</style>
