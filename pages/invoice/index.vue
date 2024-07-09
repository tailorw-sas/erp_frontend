<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import dayjs from 'dayjs'
import { filter } from 'lodash'
import Checkbox from 'primevue/checkbox'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean, statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'
import getUrlByImage from '~/composables/files'
import type { MenuItem } from '~/components/menu/MenuItems'
// VARIABLES -----------------------------------------------------------------------------------------
const menu = ref()
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const formReload = ref(0)
const invoiceTypeList = ref<any[]>()

const bookingDialogOpen = ref<boolean>(false)

const loadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const hotelError = ref(false)

const active = ref(0)

const loadingDelete = ref(false)
const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  client: [],
  agency: [],
  hotel: [],
  status: [{ id: 'PROCECSED', name: 'Procesed' }, { id: 'RECONCILED', name: 'Reconciled' }, { id: 'SENT', name: 'Sent' },],
  invoiceType: [{ id: 'All', name: 'All', code: 'All' }],
  from: dayjs(new Date()).startOf('month').toDate(),
  to: dayjs(new Date()).endOf('month').toDate(),

  includeInvoicePaid: true
})
const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
})

const disableClient = ref<boolean>(true)
const disableDates = ref<boolean>(true)

const expandedInvoice = ref('')

const hotelList = ref<any[]>([])
const statusList = ref<any[]>([])
const clientList = ref<any[]>([])
const agencyList = ref<any[]>([])

const confclientListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-client',
})

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confinvoiceTypeListtApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-type',
})

////
const computedexpandedInvoice = computed(() => {
  return expandedInvoice.value === ''
})

const createItems = ref([{
  label: 'Invoice',
  command: () => navigateTo(`invoice/create?type=${ENUM_INVOICE_TYPE[0].id}`),

}, {
  label: 'Income',
  command: () => navigateTo(`invoice/create?type=${ENUM_INVOICE_TYPE[1].id}`)

}, {
  label: 'Credit',
  command: () => navigateTo(`invoice/create?type=${ENUM_INVOICE_TYPE[2].id}`),
  disabled: computedexpandedInvoice

}, {
  label: 'Old Credit',
  command: () => navigateTo(`invoice/create?type=${ENUM_INVOICE_TYPE[3].id}`)
}])

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    disabled: false,
    dataType: 'code',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(5, 'Maximum 5 characters').regex(/^[a-z]+$/i, 'Only text characters allowed')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1'
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    class: 'field col-12  mb-3 mt-3',
  },
]

const item = ref<GenericObject>({
  image: '',
  name: '',
  code: '',
  description: '',
  status: true
})

const itemTemp = ref<GenericObject>({
  image: '',
  name: '',
  code: '',
  description: '',
  status: true
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'invoice_id', header: 'Id', type: 'text' },
  // { field: 'invoiceType', header: 'Type', type: 'select' },
  { field: 'hotel', header: 'Hotel', type: 'select' },
  { field: 'agencyCd', header: 'Agency CD', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select' },
  { field: 'invoiceNumber', header: 'Inv. No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen. Date', type: 'date' },
  { field: 'isManual', header: 'Manual', type: 'bool' },
  { field: 'invoiceAmount', header: 'Amount', type: 'text' },
  { field: 'invoiceAmount', header: 'Due Amount', type: 'text' },
  // { field: 'autoRec', header: 'Auto Rec', type: 'bool' },
  { field: 'status', header: 'Status', type: 'select' },
]
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'invoiceNumber', name: 'Invoice Number' },
  { id: 'invoiceDate', name: 'Invoice Date' },
  { id: 'invoiceAmount', name: 'Invoice Amount' },
]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  showFilters: false,
  actionsAsMenu: false,
  showEdit: false,
  showAcctions: false,
  messageToDelete: 'Do you want to save the change?',
  showTitleBar: false
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'code',
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
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fields[0].disabled = false
  formReload.value++
}

async function buildFilters() {}

async function getList() {
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
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false, invoiceDate: new Date(iterator?.invoiceDate), agencyCd: iterator?.agency?.code })
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

const openCreateDialog = async () => await navigateTo({ path: 'invoice/create' })

const openEditDialog = async (item: any) => await navigateTo({ path: `invoice/edit/${item}` })

async function resetListItems() {
  payload.value.page = 0
  getList()
}

function searchAndFilter() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: 'DES'
  }

  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }

  if (filterToSearch.value.client?.length > 0 && !filterToSearch.value.client.find(item => item.id === 'All')) {
    const filteredItems = filterToSearch.value.client.filter((item: any) => item?.id !== 'All')
    const itemIds = filteredItems?.map((item: any) => item?.id)

    payload.value.filter = [...payload.value.filter, {
      key: 'agency.client.id',
      operator: 'IN',
      value: itemIds,
      logicalOperation: 'AND'
    }]
  }
  if (filterToSearch.value.agency?.length > 0 && !filterToSearch.value.agency.find(item => item.id === 'All')) {
    const filteredItems = filterToSearch.value.agency.filter((item: any) => item?.id !== 'All')
    const itemIds = filteredItems?.map((item: any) => item?.id)
    payload.value.filter = [...payload.value.filter, {
      key: 'agency.id',
      operator: 'IN',
      value: itemIds,
      logicalOperation: 'AND'
    }]
  }
  if (filterToSearch.value.hotel?.length > 0) {
    const filteredItems = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All')
    if (filteredItems.length > 0) {
      const itemIds = filteredItems?.map((item: any) => item?.id)
      payload.value.filter = [...payload.value.filter, {
        key: 'hotel.id',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }
  }
  else {
    return hotelError.value = true
  }
  if (filterToSearch.value.status?.length > 0) {
    const filteredItems = filterToSearch.value.status.filter((item: any) => item?.id !== 'All')
    if (filteredItems.length > 0) {
      const itemIds = filteredItems?.map((item: any) => item?.id)
      payload.value.filter = [...payload.value.filter, {
        key: 'invoiceStatus',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }
  }
  if (filterToSearch.value.type?.length > 0) {
    const filteredItems = filterToSearch.value.type.filter((item: any) => item?.id !== 'All')
    if (filteredItems.length > 0) {
      const itemIds = filteredItems?.map((item: any) => item?.id)
      payload.value.filter = [...payload.value.filter, {
        key: 'invoiceType',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }
  }
  if (filterToSearch.value.from && !disableDates.value) {
    payload.value.filter = [...payload.value.filter, {
      key: 'invoiceDate',
      operator: 'GREATER_THAN_OR_EQUAL_TO',
      value: filterToSearch.value.from,
      logicalOperation: 'AND'
    }]
  }
  if (filterToSearch.value.to && !disableDates.value) {
    payload.value.filter = [...payload.value.filter, {
      key: 'invoiceDate',
      operator: 'LESS_THAN_OR_EQUAL_TO',
      value: filterToSearch.value.to,
      logicalOperation: 'AND'
    }]
  }

  getList()
}

function clearFilterToSearch() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: 'DES'
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getList()
}
async function getItemById(id: string) {
  openEditDialog(id)
}

function handleDialogOpen() {
  bookingDialogOpen.value = true
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.status = statusToString(payload.status)
    payload.image = typeof payload.image === 'object' ? await getUrlByImage(payload.image) : payload.image
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.image = typeof payload.image === 'object' ? await getUrlByImage(payload.image) : payload.image
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
    getList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true
  if (idItem.value) {
    try {
      await updateItem(item)
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
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
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

function requireConfirmationToDelete(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      deleteItem(idItem.value)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function getHotelList() {
  try {
    const payload
      = {
        filter: [
          {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }
        ],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    hotelList.value = [{ id: 'All', name: 'All', code: 'All' }]
    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getClientList() {
  try {
    const payload
      = {
        filter: [
          {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }
        ],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    clientList.value = [{ id: 'All', name: 'All', code: 'All' }]
    const response = await GenericService.search(confclientListApi.moduleApi, confclientListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      clientList.value = [...clientList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getStatusList() {
  try {
    statusList.value = [{ id: 'All', name: 'All', code: 'All' }, ...ENUM_INVOICE_STATUS]
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getInvoiceTypeList() {
  try {
    invoiceTypeList.value = [{ id: 'All', name: 'All', code: 'All' }, ...ENUM_INVOICE_TYPE]
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getAgencyList() {
  try {
    const payload
      = {
        filter: [
          {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }
        ] as any,
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    agencyList.value = [{ id: 'All', name: 'All', code: 'All' }]

    if (filterToSearch.value.client?.length === 0) {
      return agencyList.value = []
    }
    const clientIds: any[] = []

    filterToSearch.value?.client?.forEach((client: any) => clientIds.push(client?.id))

    payload.filter.push({
      key: 'client.id',
      operator: 'IN',
      value: clientIds,
      logicalOperation: 'AND'
    })

    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getList()
  }
}

function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCECSED': return '#FF8D00'

    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELED': return '#F90303'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

function getStatusName(code: string) {
  switch (code) {
    case 'PROCECSED': return 'Procesed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Canceled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})

function toggle(event) {
  menu.value.toggle(event)
}
// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getList()
})
watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

watch(disableClient, () => {
  if (disableClient.value) {
    filterToSearch.value.client = []
    filterToSearch.value.agency = []
  }
})

// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  getList()
})

const legend = ref(
  [
    {
      name: 'Processed',
      color: '#FF8D00',
      colClass: 'pr-4',
    },
    {
      name: 'Cancelled',
      color: '#686868',
      colClass: 'pr-4',
    },
    {
      name: 'Waiting',
      color: '#F90303',
      colClass: 'pr-4',
    },
    {
      name: 'Reconciled',
      color: '#005FB7',
      colClass: 'pr-4',
    },
    {
      name: 'Sent',
      color: '#006400',
      colClass: '',
    },
  ]
)

// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class=" col-12 align-items-center grid w-full">
    <div class="flex align-items-center justify-content-between w-full">
      <h3 class="mb-0 w-6">
        Invoice Management
      </h3>
      <div class="flex flex-row w-full place-content-center justify-center justify-content-end">
        <Button v-tooltip.left="'New'" label="New" icon="pi pi-plus" severity="primary" aria-haspopup="true" aria-controls="overlay_menu" @click="toggle" />
        <Menu id="overlay_menu" ref="menu" :model="createItems" :popup="true" />

        <PopupNavigationMenu v-if="false" :items="createItems" icon="pi pi-plus" label="New">
          <template #item="props">
            <button :disabled="props.props.label === 'Credit' && expandedInvoice === ''" style="border: none; width: 100%;">
              {{ props.props.label }}
            </button>
          </template>
        </PopupNavigationMenu>

        <Button class="ml-2" icon="pi pi-download" label="Import" />
        <Button class="ml-2" icon="pi pi-file-plus" label="Process" />
        <Button class="ml-2" icon="pi pi-copy" label="Rec Inv" />
        <Button class="ml-2" icon="pi pi-envelope" label="Send" />
        <Button class="ml-2" icon="pi pi-cog" label="Adjustment" disabled />
        <Button class="ml-2" icon="pi pi-print" label="Print" disabled />
        <Button class="ml-2" icon="pi pi-upload" label="Export" disabled />
      </div>
    </div>
  </div>
  <div class="grid w-full">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class=" p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
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
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="">Client</label>
                  <label for="">Agency</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete" multiple field="name"
                        item-value="id" :model="filterToSearch.client" :suggestions="clientList" placeholder=""
                        :disabled="disableClient" style="max-width: 400px;" @load="($event) => getClientList()"
                        @change="($event) => {

                          if (!filterToSearch.client.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                            filterToSearch.client = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {

                            filterToSearch.client = $event.filter((element: any) => element?.id !== 'All')
                          }

                          filterToSearch.agency = filterToSearch.client.length > 0 ? [{ id: 'All', name: 'All', code: 'All' }] : []
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete"
                        placeholder="" multiple field="name"
                        item-value="id" :model="filterToSearch.agency" :suggestions="agencyList" :disabled="disableClient" style="max-width: 400px;"
                        @change="($event) => {

                          if (!filterToSearch.agency.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                            filterToSearch.agency = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {

                            filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                          }

                        }"
                        @load="($event) => getAgencyList($event)"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>

              <div class="flex align-items-center gap-2 ">
                <Checkbox id="all-check-1" v-model="disableClient" :binary="true" style="z-index: 999;" />
                <label for="all-check-1">All</label>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="">Hotel</label>
                  <label for="">Status</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <div class="flex gap-2">
                        <DebouncedAutoCompleteComponent
                          v-if="!loadingSaveAll" id="autocomplete" field="name" multiple
                          item-value="id" :model="filterToSearch.hotel" :suggestions="hotelList" placeholder=""
                          style="max-width: 218px;" @load="($event) => getHotelList()"
                          @change="($event) => {

                            if (!filterToSearch.hotel.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                              filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                            }
                            else {

                              filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                            }
                            hotelError = false

                          }"
                        >
                          <template #option="props">
                            <span>{{ props.item.code }} - {{ props.item.name }}</span>
                          </template>
                        </DebouncedAutoCompleteComponent>
                        <div v-if="hotelError" class="flex align-items-center text-sm">
                          <span style="color: red; margin-right: 3px; margin-left: 3px;">You must select the "Hotel" field as required</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete" field="name" multiple
                        item-value="id" :model="filterToSearch.status" :suggestions="statusList" placeholder=""
                        @load="($event) => getStatusList($event)" @change="($event) => {
                          if (!filterToSearch.status.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                            filterToSearch.status = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {

                            filterToSearch.status = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="">From</label>
                  <label for="">To</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <Calendar
                        v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                        show-icon icon-display="input" style="max-width: 130px; max-height: 32px"
                        :disabled="disableDates" :max-date="dayjs(new Date()).endOf('month').toDate()"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <Calendar
                        v-model="filterToSearch.to" date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                        icon-display="input" style="max-width: 130px; max-height: 32px; overflow: auto;"
                        :invalid="filterToSearch.to < filterToSearch.from"
                        :disabled="disableDates" :max-date="dayjs(new Date()).endOf('month').toDate()" :min-date="filterToSearch.from"
                      />
                      <span v-if="filterToSearch.to < filterToSearch.from" style="color: red; margin-left: 2px;">Check date range</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex align-items-center gap-2 ">
                <Checkbox id="all-check-1" v-model="disableDates" :binary="true" style="z-index: 999;" />
                <label for="all-check-1">All</label>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="">Criteria:</label>
                  <label for="">Search:</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <Dropdown v-model="filterToSearch.criteria" show-clear option-label="name" style="min-width: 140px;" :options="[...ENUM_INVOICE_CRITERIA]">
                        <template #option="slotProps">
                          <div class="flex align-items-center gap-2">
                            <span>{{ slotProps.option.name }}</span>
                          </div>
                        </template>
                      </Dropdown>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto mr-3">
                      <IconField icon-position="left" style="min-width: 140px;">
                        <InputText v-model="filterToSearch.search" type="text" style="width: 140px;" />
                        <InputIcon class="pi pi-search" />
                      </IconField>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-start align-items-end mr-1">
                  <label for="">Type: </label>
                  <div class="w-full lg:w-auto" />
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete" field="name" multiple placeholder=""
                        item-value="id" :model="filterToSearch.invoiceType" :suggestions="invoiceTypeList"
                        style="max-width: 400px;" @load="($event) => getInvoiceTypeList()" @change="($event) => {
                          if (!filterToSearch.invoiceType.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                            filterToSearch.invoiceType = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {

                            filterToSearch.invoiceType = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2 w-full">
                    <Checkbox id="all-check-2" v-model="filterToSearch.includeInvoicePaid" disabled :binary="true" />
                    <label for="all-check-2">Include Invoice Paid</label>
                  </div>
                  <div class="flex align-items-center gap-2" />
                </div>
              </div>
              <div class="flex align-items-center">
                <Button
                  v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch"
                  :loading="loadingSearch" @click="searchAndFilter"
                />
              </div>
              <!-- <div class="col-12 md:col-3 sm:mb-2 flex align-items-center">
            </div> -->
              <!-- <div class="col-12 md:col-5 flex justify-content-end">
            </div> -->
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <ExpandableTable
        :data="listItems" :columns="columns" :options="options" :pagination="pagination"
        @on-confirm-create="clearForm" @open-edit-dialog="openEditDialog($event)"
        @on-change-pagination="payloadOnChangePage = $event" @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems" @on-sort-field="onSortField" @update:double-clicked="getItemById" @on-expand-field="($event) => { expandedInvoice = $event }"
      >
        <!-- <template #topbar-end>
          <div class="flex flex-row gap-4 align-items-center" style="font-weight: 600;">
            <span>
              Legend:
            </span>
            <div class="flex align-items-center gap-2">
              <span style="background-color: #FF8D00; width: 20px; height: 20px; border-radius: 6px;" />

              <span>Processed</span>
            </div>
            <div class="flex align-items-center gap-2">
              <span value="Canceled" style="background-color:#686868; width: 20px; height: 20px; border-radius: 6px;" />
              <span>Canceled</span>
            </div>
            <div class="flex align-items-center gap-2">
              <span value="Waiting" style="background-color: #F90303; width: 20px; height: 20px; border-radius: 6px;" />
              <span>Waiting</span>
            </div>
            <div class="flex align-items-center gap-2">
              <span
                value="Reconciled" style="background-color: #005FB7; width: 20px; height: 20px; border-radius: 6px;"
              />
              <span>Reconciled</span>
            </div>
            <div class="flex align-items-center gap-2">
              <span value="Sent" style="background-color: #006400; width: 20px; height: 20px; border-radius: 6px;" />
              <span>Sent</span>
            </div>
          </div>
        </template> -->
        <template #row-selector-body="">
          <span class="pi pi-paperclip" />
        </template>

        <template #expanded-item="props">
          <InvoiceTabView
            :selected-invoice="props.itemId" :is-dialog-open="bookingDialogOpen" :close-dialog="() => { bookingDialogOpen = false }"
            :open-dialog="handleDialogOpen" :active="active" :set-active="($event) => { active = $event }"
          />
        </template>

        <template #column-status="props">
          <Badge :value="getStatusName(props.item)" :style="`background-color: ${getStatusBadgeBackgroundColor(props?.item)}`" />
        </template>
      </ExpandableTable>
    </div>
  </div>
</template>
