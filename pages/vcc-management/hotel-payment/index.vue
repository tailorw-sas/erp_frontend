<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import ContextMenu from 'primevue/contextmenu'
import dayjs from 'dayjs'
import { useToast } from 'primevue/usetoast'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import { formatNumber } from '~/pages/payment/utils/helperFilters'
import HotelPaymentStatusHistoryDialog from '~/components/vcc/history/HotelPaymentStatusHistoryDialog.vue'
// VARIABLES -----------------------------------------------------------------------------------------
const { data: userData } = useAuth()
const toast = useToast()
const listItems = ref<any[]>([])
const loadingSaveAll = ref(false)
const idItemToLoadFirstTime = ref('')
const hotelPaymentHistoryDialogVisible = ref<boolean>(false)
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

const menuListItems = ref<any[]>([])

enum MenuType {
  changeComplete, changeCancelled
}

const allMenuListItems = [
  {
    label: 'Status History',
    icon: 'pi pi-history',
    command: () => { hotelPaymentHistoryDialogVisible.value = true },
    disabled: false,
    default: true
  },
  {
    label: 'Change to Completed',
    type: MenuType.changeComplete,
    icon: 'pi pi-check-circle',
    command: () => { changeStatus(MenuType.changeComplete) },
    disabled: false,
    default: false
  },
  {
    label: 'Change to Cancelled',
    type: MenuType.changeCancelled,
    icon: 'pi pi-times-circle',
    command: () => { changeStatus(MenuType.changeCancelled) },
    disabled: false,
    default: false
  },
]

const hotelList = ref<any[]>([])
const statusList = ref<any[]>([])

const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'hotel-payment',
})

const confStatusListApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'manage-payment-transaction-status',
})

const confHotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const activeStatusFilter: IFilter[] = [
  {
    key: 'status',
    operator: 'EQUALS',
    value: 'ACTIVE',
    logicalOperation: 'AND'
  }
]

const legend = ref(
  [
    {
      name: 'In Progress',
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
    },
    {
      name: 'Applied',
      color: '#05D2FF',
      colClass: 'pr-3',
    }
  ]
)
////
const sClassMap: IStatusClass[] = [
  { status: 'In Progress', class: 'vcc-text-created' },
  { status: 'Completed', class: 'vcc-text-sent' },
  { status: 'Cancelled', class: 'vcc-text-cancelled' },
  { status: 'Applied', class: 'vcc-text-reconciled' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'icon', header: '', width: '25px', type: 'slot-icon', icon: 'pi pi-paperclip', sortable: false, showFilter: false },
  { field: 'hotelPaymentId', header: 'Id', type: 'text' },
  { field: 'transactionDate', header: 'Trans Date', type: 'date' },
  { field: 'manageHotel', header: 'Hotel', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel', filter: activeStatusFilter }, sortable: true },
  { field: 'merchantBankAccountNumber', header: 'Bank Account Number', type: 'text', sortable: true },
  { field: 'amount', header: 'Amount', type: 'number' },
  { field: 'commission', header: 'Commission', type: 'number' },
  { field: 'netAmount', header: 'Total', type: 'number' },
  { field: 'remark', header: 'Remark', type: 'text', maxWidth: '200px' },
  { field: 'status', header: 'Status', type: 'slot-select', frozen: true, statusClassMap: sClassMap, objApi: { moduleApi: 'creditcard', uriApi: 'manage-payment-transaction-status', filter: activeStatusFilter }, sortable: true },
]

const subTotals: any = ref({ amount: 0, commission: 0, net: 0 })
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'hotelPaymentId', name: 'Hotel Payment Id', filterOnlyByField: true, operator: 'EQUALS' },
  { id: 'transactionId', name: 'Transaction Id', filterOnlyByField: true, operator: 'EQUALS' },
  { id: 'transactions.referenceNumber', name: 'Transaction Reference', filterOnlyByField: false, operator: 'EQUALS' },
  { id: 'transactions.reservationNumber', name: 'Transaction Reservation Number', filterOnlyByField: false, operator: 'EQUALS' },
  { id: 'transactions.agency.code', name: 'Agency Code', filterOnlyByField: false, operator: 'EQUALS' },
  { id: 'remark', name: 'Remark', filterOnlyByField: false, operator: 'LIKE' },
]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Hotel Payment Management',
  moduleApi: 'creditcard',
  uriApi: 'hotel-payment',
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
function goToEditHotelPaymentInNewTab(item: any) {
  const id = item.hasOwnProperty('id') ? item.id : item
  const url = `/vcc-management/hotel-payment/form/${id}`
  navigateTo({ path: url, params: { id } }, { open: { target: '_blank' } })
}

async function getList() {
  const count = { amount: 0, commission: 0, net: 0 }
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
      if (Object.prototype.hasOwnProperty.call(iterator, 'manageHotel') && iterator.hotel) {
        iterator.manageHotel = { id: iterator.manageHotel.id, name: `${iterator.manageHotel.code} - ${iterator.manageHotel.name}` }
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
      if (Object.prototype.hasOwnProperty.call(iterator, 'manageBankAccount')) {
        iterator.merchantBankAccountNumber = iterator.manageBankAccount?.accountNumber
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

async function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  // Si el criterio es filterOnlyByField entonces solo se filtra por ese valor
  if (filterToSearch.value.criteria && filterToSearch.value.criteria.filterOnlyByField && filterToSearch.value.search) {
    newPayload.filter = [{
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: filterToSearch.value.criteria.operator || 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }]
  }
  else {
    newPayload.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
    // Si el criterio no es filterOnlyByField entonces se agrega a la lista de filtros
    if (filterToSearch.value.criteria && !filterToSearch.value.criteria.filterOnlyByField && filterToSearch.value.search) {
      newPayload.filter = [{
        key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
        operator: filterToSearch.value.criteria.operator || 'EQUALS',
        value: filterToSearch.value.search,
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    // Date
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
  options.value.loading = false
  await getList()
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

async function findStatus(status: MenuType) {
  try {
    const payload = {
      filter: [{
        key: status === MenuType.changeCancelled ? 'cancelled' : 'completed',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'AND'
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
    if (dataList.length > 0) {
      // Retornar el id del status
      return dataList[0].id
    }
  }
  catch (error) {
    console.error('Error getting status:', error)
  }
}

async function updateStatus(hotelPaymentId: string, statusId: string) {
  const payload: { [key: string]: any } = {}
  payload.status = statusId
  payload.employee = userData?.value?.user?.name
  const response: any = await GenericService.update(confApi.moduleApi, confApi.uriApi, hotelPaymentId, payload)
  if (response && response.id) {
    toast.add({ severity: 'info', summary: 'Confirmed', detail: `The Hotel Payment ${response.hotelPaymentId ?? ''} was updated successfully`, life: 10000 })
  }
  else {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was not successful', life: 10000 })
  }
}

async function changeStatus(changeTo: MenuType) {
  try {
    options.value.loading = true
    const hotelPaymentId = contextMenuTransaction.value.id
    const statusId = await findStatus(changeTo)
    if (statusId) {
      try {
        await updateStatus(hotelPaymentId, statusId)
        await searchAndFilter()
      }
      catch (error: any) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
      }
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Status could not be found', life: 10000 })
    }
  }
  finally {
    options.value.loading = false
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

function setChangeStatusOptions() {
  const newMenuItems = []
  const noDefaultItems: any[] = allMenuListItems.filter((element: any) => !element.default)
  const status = contextMenuTransaction.value.status
  for (let i = 0; i < noDefaultItems.length; i++) {
    const element = noDefaultItems[i]
    if ((element.type === MenuType.changeComplete || element.type === MenuType.changeCancelled) && status.inProgress) {
      newMenuItems.push(element)
    }
  }
  return newMenuItems
}

async function onRowRightClick(event: any) {
  menuListItems.value = [] // Limpiar elementos
  if (contextMenu.value?.visible) {
    contextMenu.value.hide()
  }
  contextMenuTransaction.value = event.data
  const defaultItems = allMenuListItems.filter((e: any) => e.default) // Agregar elementos por defecto
  const statusMenuItems = setChangeStatusOptions()
  menuListItems.value = [...defaultItems, ...statusMenuItems]
  if (menuListItems.value.length > 0) {
    await nextTick()
    contextMenu.value.show(event.originalEvent)
  }
}

function goToHotelPaymentInNewTab() {
  const url = `/vcc-management/hotel-payment/form`
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
    <h5 class="mb-0">
      Hotel Payment Management
    </h5>
    <IfCan :perms="['HOTEL-PAYMENT:CREATE']">
      <div class="my-2 flex justify-content-end px-0">
        <Button class="ml-2" icon="pi pi-plus" label="New" @click="goToHotelPaymentInNewTab()" />
      </div>
    </IfCan>
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
                    <div class="w-full" style=" z-index:5; overflow: hidden;">
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
        @on-row-double-click="goToEditHotelPaymentInNewTab($event)"
      >
        <template #expansion="{ data: item }">
          <!--          <pre>{{item}}</pre> -->
          <HotelPaymentTransactions
            :hotel-payment-id="item.id" :hide-bind-transaction-menu="item.status && (item.status.completed || item.status.cancelled)"
            @update:list="($event) => {
              getList()
            }"
          />
        </template>
        <template #column-icon="{ data: objData, column }">
          <div class="flex align-items-center justify-content-center p-0 m-0">
            <!-- <pre>{{ objData }}</pre> -->
            <Button
              v-if="objData.hasAttachments"
              :icon="column.icon"
              class="p-button-rounded p-button-text w-2rem h-2rem"
              aria-label="Submit"
              :style="{ color: '#000' }"
            />
          </div>
          <!-- style="color: #616161;" -->
          <!-- :style="{ 'background-color': '#00b816' }" -->
        </template>
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
              <Column :colspan="2" />
            </Row>
          </ColumnGroup>
        </template>
      </DynamicTable>
    </div>
    <ContextMenu ref="contextMenu" :model="menuListItems" />
    <div v-if="hotelPaymentHistoryDialogVisible">
      <HotelPaymentStatusHistoryDialog :close-dialog="() => { hotelPaymentHistoryDialogVisible = false }" :open-dialog="hotelPaymentHistoryDialogVisible" :selected-hotel-payment="contextMenuTransaction" :s-class-map="sClassMap" />
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
