<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import ContextMenu from 'primevue/contextmenu'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import type { MenuItem } from '~/components/menu/MenuItems'
// VARIABLES -----------------------------------------------------------------------------------------
const listItems = ref<any[]>([])
const newManualTransactionDialogVisible = ref(false)
const newAdjustmentTransactionDialogVisible = ref(false)
const newRefundDialogVisible = ref(false)
const loadingSaveAll = ref(false)
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const contextMenuTransaction = ref()
const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  merchant: '',
  ccType: '',
  hotel: '',
  status: '',
  from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
  to: new Date(),
})
const contextMenu = ref()
const menuItems2 = ref([
  {
    label: 'Refund',
    icon: 'pi pi-dollar',
    command: () => openNewRefundDialog()
  },
])

const hotelList = ref<any[]>([])
const statusList = ref<any[]>([])
const merchantList = ref<any[]>([])
const ccTypeList = ref<any[]>([])

const confStatusListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-transaction-status',
})
const confMerchantListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-merchant',
})

const confHotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confCCTypeListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-credit-card-type',
})

const legend = ref(
  [
    {
      name: 'Created',
      color: '#FF8D00',
      colClass: 'pr-3',
    },
    {
      name: 'Sent',
      color: '#006400',
      colClass: 'pr-3',
    },
    {
      name: 'Received',
      color: '#3403F9',
      colClass: 'pr-3',
    },
    {
      name: 'Declined',
      color: '#661E22',
      colClass: 'pr-3',
    },
    {
      name: 'Paid',
      color: '#2E892E',
      colClass: 'pr-3',
    },
    {
      name: 'Canceled',
      color: '#FF1405',
      colClass: 'pr-3',
    },
    {
      name: 'Reconciled',
      color: '#05D2FF',
      colClass: 'pr-3',
    },
    {
      name: 'Refund',
      color: '#666666',
      colClass: '',
    },
  ]
)

function getStatusClass(status: string) {
  switch (status) {
    case 'Sent':
      return 'text-sent'
    case 'Created':
      return 'text-created'
    case 'Received':
      return 'text-received'
    case 'Declined':
      return 'text-declined'
    case 'Paid':
      return 'text-paid'
    case 'Canceled':
      return 'text-canceled'
    case 'Reconciled':
      return 'text-reconciled'
    case 'Refund':
      return 'text-refund'
    default:
      return ''
  }
}
////

const createItems: Array<MenuItem> = ref([{
  label: 'Manual Transaction',
  command: () => openNewManualTransactionDialog(),
  disabled: false
}, {
  label: 'Adjustment Transaction',
  command: () => openNewAdjustmentTransactionDialog(),
  disabled: false
}])

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'id', header: 'Id', type: 'text' },
  { field: 'parent', header: 'Parent Id', type: 'text' },
  { field: 'enrolleCode', header: 'Enrollee Code', type: 'text' },
  { field: 'hotel', header: 'Hotel', type: 'text' },
  { field: 'cardNumber', header: 'Card Number', type: 'text' },
  { field: 'creditCardType', header: 'CC Type', type: 'text', },
  { field: 'referenceNumber', header: 'Reference', type: 'text' },
  { field: 'amount', header: 'Amount', type: 'text' },
  { field: 'commission', header: 'Commission', type: 'text' },
  { field: 'totalAmount', header: 'T.Amount', type: 'text' },
  { field: 'checkIn', header: 'Trans Date', type: 'date' },
  { field: 'status', header: 'Status', type: 'text' },
]
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'id', name: 'Id' },
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
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'ASC'
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
async function getList(query?: IQueryRequest) {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, (query) || payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount') && Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        iterator.totalAmount = iterator.amount - iterator.commission
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = iterator.status.name
        iterator.rowClass = getStatusClass(iterator.status)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'id')) {
        iterator.id = String(iterator.id)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        iterator.amount = String(iterator.amount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'commission')) {
        iterator.commission = String(iterator.commission)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'totalAmount')) {
        iterator.totalAmount = String(iterator.totalAmount)
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
    sortType: 'DES'
  };

  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    newPayload.filter = [{
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }
  else {
    newPayload.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
    if (filterToSearch.value.criterial && filterToSearch.value.search) {
      newPayload.filter = [...newPayload.filter, {
        key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
        operator: 'LIKE',
        value: filterToSearch.value.search,
        logicalOperation: 'AND',
        type: 'filterSearch',
      }]
    }
  }
  getList(newPayload)
}

async function getHotelList(query: string = '') {
  try {
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
      sortType: 'DES'
    }

    const response = await GenericService.search(confHotelListApi.moduleApi, confHotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getMerchantList(query: string = '') {
  try {
    const payload = {
      filter: [
        {
          key: 'description',
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
      sortType: 'DES'
    }

    const response = await GenericService.search(confMerchantListApi.moduleApi, confMerchantListApi.uriApi, payload)
    const { data: dataList } = response
    merchantList.value = []
    for (const iterator of dataList) {
      merchantList.value = [...merchantList.value, { id: iterator.id, name: iterator.description, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading merchant list:', error)
  }
}

async function getStatusList(query: string = '') {
  try {
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
      sortType: 'DES'
    }

    const response = await GenericService.search(confStatusListApi.moduleApi, confStatusListApi.uriApi, payload)
    const { data: dataList } = response
    statusList.value = []
    for (const iterator of dataList) {
      statusList.value = [...statusList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading status list:', error)
  }
}

async function getCCTypeList(query: string = '') {
  try {
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
      sortType: 'DES'
    }

    const response = await GenericService.search(confCCTypeListApi.moduleApi, confCCTypeListApi.uriApi, payload)
    const { data: dataList } = response
    ccTypeList.value = []
    for (const iterator of dataList) {
      ccTypeList.value = [...ccTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading credit card type list:', error)
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function openNewManualTransactionDialog() {
  newManualTransactionDialogVisible.value = true
}

async function openNewAdjustmentTransactionDialog() {
  newAdjustmentTransactionDialogVisible.value = true
}

async function openNewRefundDialog() {
  newRefundDialogVisible.value = true
}

async function onCloseNewManualTransactionDialog(isCancel: boolean) {
  newManualTransactionDialogVisible.value = false
  if (!isCancel) {
    getList()
  }
}

async function onCloseNewAdjustmentTransactionDialog() {
  newAdjustmentTransactionDialogVisible.value = false
}

async function onCloseNewRefundDialog() {
  newRefundDialogVisible.value = false
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

async function onRowRightClick(event: any) {
  contextMenu.value.show(event)
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
  filterToSearch.value.criterial = ENUM_FILTER[0]

  getList()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Virtual Credit Card Management
    </h3>
    <div class="my-2 flex justify-content-end px-0">
      <PopupNavigationMenu menu-id="vcc-menu" :items="createItems" icon="pi pi-plus" label="New" class="vcc-menu" />
      <Button class="ml-2" icon="pi pi-dollar" label="Payment" disabled />
      <Button class="ml-2" icon="pi pi-upload" label="Export" disabled />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 order-0">
      <div class="card p-0 mb-0">
        <Accordion :active-index="0">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
                <div>
                  Filters
                </div>
                <div>
                  <PaymentLegend :legend="legend" />
                </div>
              </div>
            </template>
            <div class="grid">
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label class="filter-label" for="">Merchant:</label>
                  <label class="filter-label" for="">Hotel:</label>
                </div>
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                    <div class="w-full" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete" class="w-full" field="name"
                        item-value="id" :model="filterToSearch.merchant" :suggestions="merchantList"
                        @load="($event) => getMerchantList($event)" @change="($event) => {
                          filterToSearch.merchant = $event
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete" class="w-full" field="name"
                        item-value="id" :model="filterToSearch.hotel" :suggestions="hotelList"
                        @load="($event) => getHotelList($event)" @change="($event) => {
                          filterToSearch.hotel = $event
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label class="filter-label" for="">CC Type:</label>
                  <label class="filter-label" for="">Status:</label>
                </div>

                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete" class="w-full" field="name"
                        item-value="id" :model="filterToSearch.ccType" :suggestions="ccTypeList" @change="($event) => {
                          filterToSearch.ccType = $event
                        }" @load="($event) => getCCTypeList($event)"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete" class="w-full" field="name"
                        item-value="id" :model="filterToSearch.status" :suggestions="statusList"
                        @load="($event) => getStatusList($event)" @change="($event) => {
                          filterToSearch.status = $event
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label class="filter-label" for="">From:</label>
                  <label class="filter-label" for="">To:</label>
                </div>

                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full" style=" z-index:5 ">
                      <Calendar
                        v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                        show-icon icon-display="input" class="w-full" :max-date="new Date()"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
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
                <div class="flex w-full">
                  <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                    <label class="filter-label" for="">Criteria:</label>
                    <label class="filter-label" for="">Search:</label>
                  </div>
                  <div class="flex flex-row w-full">
                    <div class="flex flex-column gap-2 w-full">
                      <div class="flex align-items-center" style=" z-index:5 ">
                        <div class="w-full">
                          <Dropdown
                            v-model="filterToSearch.criterial" :options="[...ENUM_FILTER]" option-label="name"
                            placeholder="Criteria" return-object="false" class="align-items-center w-full" show-clear
                          />
                        </div>
                      </div>
                      <div class="flex align-items-center">
                        <div class="w-full">
                          <IconField icon-position="left">
                            <InputText v-model="filterToSearch.search" type="text" style="width: 100% !important;" />
                            <InputIcon class="pi pi-search" />
                          </IconField>
                        </div>
                      </div>
                    </div>
                    <div class="flex align-items-center">
                      <Button
                        v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch"
                        :loading="loadingSearch" @click="searchAndFilter"
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
        @update:right-clicked-item="(event) => {
          console.log(event)
          contextMenuTransaction = event
        }"
      />
    </div>
    <ContextMenu ref="contextMenu" :model="menuItems2" />
    <VCCNewManualTransaction :open-dialog="newManualTransactionDialogVisible" @on-close-dialog="onCloseNewManualTransactionDialog($event)" />
    <VCCNewAdjustmentTransaction :open-dialog="newAdjustmentTransactionDialogVisible" @on-close-dialog="onCloseNewAdjustmentTransactionDialog" />
    <VCCNewRefund :open-dialog="newRefundDialogVisible" :parent-transaction="contextMenuTransaction" @on-close-dialog="onCloseNewRefundDialog" />
  </div>
</template>

<style scoped>
.filter-label {
  min-width: 55px;
  text-align: end;
}
</style>

<style lang="scss">
#vcc-menu_list {
  .p-menuitem .p-menuitem-content {
    padding: 6px 2px;
  }
  .p-menuitem .p-menuitem-content:hover {
    cursor: pointer;
  }
}

.text-sent {
  color: #006400;
}
.text-created {
  color: #FF8D00;
}
.text-received {
  color: #3403F9;
}
.text-declined {
  color: #661E22;
}
.text-paid {
  color: #2E892E;
}
.text-canceled {
  color: #FF1405;
}
.text-reconciled {
  color: #05D2FF;
}
.text-refund {
  color: #666666;
}
</style>
