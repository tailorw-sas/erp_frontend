<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean, statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'

// VARIABLES -----------------------------------------------------------------------------------------
const allDefaultItem = { id: 'All', name: 'All', code: 'All' }
const activeTab = ref(0)
const allDefault = { id: 'All', name: 'All' }
const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [],
  hotel: [],
  client: null,
  clientName: '',
  clientStatus: '',
  creditDays: 0,
  language: '',
  primaryPhone: '',
  alternativePhone: '',
  email: '',
  contactName: '',
  country: '',
})
// agency: [allDefaultItem],
//   hotel: [allDefaultItem],

const objLoading = ref({
  loadingAgency: false,
  loadingClient: false,
  loadingHotel: false,
  loadingStatus: false
})

const objApis = ref({
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  status: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  transactionType: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' },
})

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})
const confClientApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-client',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const listItemsAgency = ref<any[]>([])
const listItemsInvoice = ref<any[]>([])
const loadingSearch = ref(false)
const formReload = ref(0)
const agencyList = ref<any[]>([])
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const hotelList = ref<any[]>([])
const clientList = ref<any[]>([])

const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency-type',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    disabled: false,
    dataType: 'code',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(5, 'Maximum 5 characters').regex(/^[a-z]+$/i, 'Only text characters allowed')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string()
      .trim()
      .min(1, 'The name field is required')
      .max(50, 'Maximum 50 characters')
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mb-3 mt-3',
  },
]

const item = ref<GenericObject>({
  name: '',
  code: '',
  status: true,
})

const itemTemp = ref<GenericObject>({
  name: '',
  code: '',
  status: true,
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
]

const columns: IColumn[] = [
  { field: 'icon', header: '', type: 'text', showFilter: false, icon: 'pi pi-paperclip', sortable: false, width: '30px' },
  { field: 'paymentId', header: 'Id', type: 'text' },
  { field: 'transactionDate', header: 'Trans. Date', type: 'text' },
  { field: 'hotel', header: 'Hotel', type: 'text' },
  { field: 'agency', header: 'Agencia', type: 'text' },
  { field: 'paymentAmount', header: 'P.Amount', type: 'text' },
  { field: 'depositBalance', header: 'D.Balance', type: 'text' },
  { field: 'applied', header: 'Applied', type: 'text' },
  { field: 'notapplied', header: 'Not Applied', type: 'text' },

]
const columnsInvoice: IColumn[] = [
  { field: 'icon', header: '', type: 'text', showFilter: false, icon: 'pi pi-paperclip', sortable: false, width: '30px' },
  { field: 'hotel', header: 'Hotel', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'text' },
  { field: 'invoiceNo', header: 'Inv.No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen.Date', type: 'text' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  { field: 'paymentAmount', header: 'P.Amount', type: 'text' },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'text' },
  { field: 'aging', header: 'Aging', type: 'text' },
  { field: 'invoiceStatus', header: 'Status', type: 'bool' },

]
const columnsAgency: IColumn[] = [
  { field: 'regions', header: 'Regions', type: 'text' },
  { field: 'email', header: 'Email Contact', type: 'text' },

]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Payment',
  moduleApi: 'payment',
  uriApi: 'manage-payment',
  loading: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  selectAllItemByDefault: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const optionsAgency = ref({
  tableName: 'Agency',
  moduleApi: 'settings',
  uriApi: 'manage-agency',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const optionsInvoice = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  selectAllItemByDefault: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const payloadOnChangePage = ref<PageState>()
const payloadOnChangePageAgency = ref<PageState>()
const payloadOnChangePageInv = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const payloadInv = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const payloadAgency = ref<IQueryRequest>({
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
const paginationInvoice = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const paginationAgency = ref<IPagination>({
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
  updateFieldProperty(fields, 'status', 'disabled', true)
  formReload.value++
}

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
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]

    if (listItems.value.length > 0) {
      idItemToLoadFirstTime.value = listItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getList()
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}
function goToInvoice() {
  navigateTo('/invoice')
}
function goToPayment() {
  navigateTo('/payment')
}
async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.name = response.name
        item.value.status = statusToBoolean(response.status)
        item.value.description = response.description
        item.value.code = response.code
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
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

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.status = statusToString(payload.status)
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    clearForm()
    getList()
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
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
      idItem.value = ''
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
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

function requireConfirmationToSave(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    saveItem(item)
  }
  else {
    const { event } = item
    confirm.require({
      target: event.currentTarget,
      group: 'headless',
      header: 'Save the record',
      message: 'Do you want to save the change?',
      rejectLabel: 'Cancel',
      acceptLabel: 'Accept',
      accept: () => {
        saveItem(item)
      },
      reject: () => {
        // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
      }
    })
  }
}
function requireConfirmationToDelete(event: any) {
  if (!useRuntimeConfig().public.showDeleteConfirm) {
    deleteItem(idItem.value)
  }
  else {
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
      },
      reject: () => { }
    })
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

async function getClientList(query = '') {
  try {
    const payload
            = {
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
              pageSize: 200,
              page: 0,
              sortBy: 'name',
              sortType: ENUM_SHORT_TYPE.ASC
            }

    clientList.value = []
    const response = await GenericService.search(confClientApi.moduleApi, confClientApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      clientList.value = [...clientList.value, {
        id: iterator.id,
        name: iterator.name,
        code: iterator.code,
        status: iterator.status
      }]
    }
  }
  catch (error) {
    console.error('Error loading client list:', error)
  }
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
  description?: string
  creditDay?: number
  country?: {
    id?: string
    name?: string
    code?: string
    status?: string
    managerLanguage?: {
      id?: string
      name?: string
      code?: string
      status?: string
    }
  }
  phone?: string
  alternativePhone?: string
  email?: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  code?: string
  description?: string
  creditDay?: number
  language?: string
  country?: string
  primaryPhone?: string
  alternativePhone?: string
  email?: string
}

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description,
    creditDay: data.creditDay,
    language: data.country?.managerLanguage?.name,
    country: data.country?.name,
    primaryPhone: data?.phone,
    alternativePhone: data?.alternativePhone,
    email: data?.email
  }
}

async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingAgency = true
    let agencyTemp: any[] = []
    agencyList.value = []
    agencyTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    agencyList.value = [...agencyList.value, ...agencyTemp]
  }
  catch (error) {
    objLoading.value.loadingAgency = false
  }
  finally {
    objLoading.value.loadingAgency = false
  }
}

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingHotel = true
    let hotelTemp: any[] = []
    hotelList.value = []
    hotelTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    hotelList.value = [...hotelList.value, ...hotelTemp]
  }
  catch (error) {
    objLoading.value.loadingHotel = false
  }
  finally {
    objLoading.value.loadingHotel = false
  }
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}
// || filterToSearch.value.agency || filterToSearch.value.hotel
const disabledSearch = computed(() => {
  return filterToSearch.value.client?.id === '' || filterToSearch.value.agency.length === 0 || filterToSearch.value.hotel.length === 0
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})
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
// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="grid p-0 m-0 my-0 py-0 px-0 mx-0">
    <div class="col-12 md:order-1 md:col-12 xl:col-6 lg:col-12 mt-0 px-1">
      <div class="flex justify-content-between align-items-center">
        <!-- Título a la derecha -->
        <div class="font-bold">
          <h3 class="mb-0 ">
            Collection Management
          </h3>
        </div>
        <div class="flex  align-items-center">
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="my-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'New'" label="New" icon="pi pi-plus" class="h-2.5rem w-6rem"
              severity="primary" @click="clearForm"
            />
          </div>
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="my-2 ml-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Share File'" label="Share File" icon="pi pi-share-alt"
              class="h-2.5rem w-8rem" severity="primary" @click="clearForm"
            />
          </div>
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="my-2 ml-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Export'" label="Export" icon="pi pi-download" class="h-2.5rem w-6rem"
              severity="primary" @click="clearForm"
            />
          </div>
        </div>
      </div>

      <!-- Accordion y campos del payment -->

      <div class="card p-0 m-0">
        <!-- Encabezado Completo -->
        <div class="font-bold text-lg bg-primary custom-card-header px-4">
          Client View
        </div>

        <!-- Contenedor de Contenido -->
        <div style="display: flex; height: 23%;" class="responsive-height">
          <!-- Sección Izquierda -->
          <div class="p-0 m-0 py-0 px-0 " style="flex: 1;">
            <div class="grid p-0 py-0 px-0 m-0">
              <!-- Selector de Cliente -->
              <div class="col-12 md:col-12 lg:col-12 xl:col-12 flex pb-0  w-full">
                <div class="flex flex-column gap-2 py-0 w-full">
                  <div class="flex align-items-center gap-2 px-0 py-0 ">
                    <label class="filter-label font-bold ml-3" for="client">Client<span
                      class="text-red"
                    >*</span></label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete"
                        :multiple="false"
                        field="name"
                        item-value="id"
                        class="w-full custom-input"
                        :model="filterToSearch.client"
                        :suggestions="clientList"
                        placeholder=""
                        @load="($event) => getClientList($event)"
                        @change="($event) => {
                          filterToSearch.client = $event
                          filterToSearch.agency = []
                          filterToSearch.clientName = $event?.name ? $event?.name : ''
                          filterToSearch.clientStatus = $event?.status ? $event?.status : ''
                          // filterToSearch.client = $event.filter(element => element?.id !== 'All');
                          // filterToSearch.agency = filterToSearch.client.length > 0 ? [{ id: 'All', name: 'All', code: 'All' }] : [];

                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>{{ value?.code }}</div>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Selector de Agencia -->
              <div class="col-12 pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 px-0 py-0">
                    <label class="filter-label font-bold " for="agency">Agency<span
                      class="text-red"
                    >*</span></label>
                    <div class="w-full ">
                      <DebouncedMultiSelectComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete-agency"
                        field="name"
                        item-value="id"
                        :max-selected-labels="3"
                        class="w-full agency-input"
                        :model="filterToSearch.agency"
                        :loading="objLoading.loadingAgency"
                        :suggestions="agencyList"
                        placeholder=""
                        @change="($event) => {
                          console.log($event, 'event');

                          filterToSearch.agency = $event;
                          const totalCreditDays = $event.reduce((sum, item) => sum + item.creditDay, 0);
                          filterToSearch.creditDays = totalCreditDays
                          filterToSearch.primaryPhone = $event[0]?.primaryPhone ? $event[0]?.primaryPhone : ''
                          filterToSearch.alternativePhone = $event[0]?.alternativePhone ? $event[0]?.alternativePhone : ''
                          filterToSearch.email = $event[0]?.email ? $event[0]?.email : ''
                        }"
                        @load="async ($event) => {
                          const filter: FilterCriteria[] = [
                            {
                              key: 'client.id',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: filterToSearch.client?.id ? filterToSearch.client?.id : '',
                            },
                            {
                              key: 'status',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                              logicalOperation: 'AND',
                            },
                            {
                              key: 'autoReconcile',
                              operator: 'EQUALS',
                              value: true,
                              logicalOperation: 'AND',
                            },
                          ]
                          await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, {
                            query: $event,
                            keys: ['name', 'code'],
                          }, filter)
                        }"
                      >
                        <!-- <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>{{ value?.code }}</div>
                        </template> -->
                      </DebouncedMultiSelectComponent>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Selector de Hotel -->
              <div class="col-12 pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 px-0 py-0">
                    <label class="filter-label font-bold ml-3" for="hotel">Hotel<span
                      class="text-red"
                    >*</span></label>
                    <div class="w-full">
                      <DebouncedMultiSelectComponent
                        v-if="!loadingSaveAll"
                        id="autocomplete-hotel"
                        field="name"
                        item-value="id"
                        class="w-full hotel-input"
                        :model="filterToSearch.hotel"
                        :loading="objLoading.loadingHotel"
                        :suggestions="hotelList"
                        placeholder=""
                        @change="($event) => {
                          filterToSearch.hotel = $event;
                        }"
                        @load="async($event) => {
                          const filter: FilterCriteria[] = [
                            {
                              key: 'status',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                            },
                          ]
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          await getHotelList(objApis.hotel.moduleApi, objApis.hotel.uriApi, objQueryToSearch, filter)
                        }"
                      >
                        <!-- <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                        <template #chip="{ value }">
                          <div>{{ value?.code }}</div>
                        </template> -->
                      </DebouncedMultiSelectComponent>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex col-12 justify-content-end mt-2 py-2 xl:mt-0 py-2 pb-3">
                <Button
                  v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search"
                  :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter"
                />
                <Button
                  v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                  :loading="loadingSearch" @click="clearFilterToSearch"
                />
              </div>
            </div>
          </div>

          <!-- Divisor Vertical -->
          <div style="width: 4px; background-color: #d3d3d3; height: auto; margin: 0;" />

          <!-- Sección Derecha -->
          <div class="px-2 py-0 m-0 my-0 mt-0" style="flex: 1; padding: 16px;">
            <div class="grid py-0 my-0 px-0" style="max-width: 1200px; margin: auto;">
              <!-- Fila para Cliente y Agencia -->
              <div class="col-12 mb-0 ">
                <div class="flex items-center w-full" style="flex-wrap: nowrap;">
                  <!-- Usar flex para alinear en una fila -->
                  <label
                    for="client" class="font-bold mb-0 mt-2 required"
                    style="margin-right: 8px; flex: 0 0 auto;"
                  >Client Name</label>
                  <InputText
                    id="client" v-model="filterToSearch.clientName" class="w-full "
                    style="flex: 1;"
                  />
                </div>
              </div>

              <div class="col-12 mb-0 py-0">
                <div class="flex items-center w-full" style="flex-wrap: nowrap;">
                  <!-- Usar flex para alinear en una fila -->
                  <label
                    for="client" class="font-bold mb-0 mr-1 mt-2 required"
                    style="margin-right: 8px; flex: 0 0 auto;"
                  >Client Status</label>
                  <InputText
                    id="client" v-model="filterToSearch.clientStatus" class="w-full "
                    style="flex: 1;"
                  />
                </div>
              </div>
              <div class="col-12 mb-0 ">
                <div class="flex items-center w-full" style="flex-wrap: nowrap;">
                  <!-- Usar flex para alinear en una fila -->
                  <label
                    for="credit" class="font-bold mb-0 ml-0 mt-2 "
                    style="margin-right: 9px; flex: 0 0 auto;"
                  >Credit
                    Days</label>
                  <InputText
                    id="client" v-model="filterToSearch.creditDays" class="w-full  "
                    style="flex: 1;"
                  />
                </div>
              </div>
              <div class="col-12 my-0 py-0 pb-0 xl:col-12 mb-2">
                <div class="flex items-center w-full " style="flex-wrap: nowrap;">
                  <!-- Usar flex para alinear en una fila -->
                  <label
                    for="language" class="font-bold mb-0 ml-3 mt-2"
                    style="margin-right: 8px; flex: 0 0 auto;"
                  >Language</label>
                  <div style="flex: 1; display: flex; gap: 8px; flex-wrap: nowrap;">
                    <!-- Contenedor para los dos inputs -->
                    <InputText id="language1" v-model="filterToSearch.language" class="w-full" />
                    <InputText
                      id="timezone" v-model="currentTime" placeholder="Time Zone 12:10"
                      class="w-full pr-1 timezone-input"
                      style="background-color: var(--primary-color); color: white;" readonly
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- Aqui termina -->
      <div class="grid grid-nogutter px-0 py-0 my-0 mt-0">
        <div class="col-12 flex align-items-center">
          <!-- Usar flex para alinear en la misma fila -->
          <div class="col-11 md:col-11 lg:col-11 xl:col-11 sm:col-11 py-0 px-0">
            <div class="card py-1 p-0 my-0 mr-1 ">
              <div
                class="header-content text-lg font-bold  "
                style="background-color: var(--primary-color); color: white; padding: 8px; border-top-left-radius: 8px; border-top-right-radius: 8px;"
              >
                Payment View
              </div>
            </div>
          </div>
          <div
            class="col-1 md:col-1 lg:col-1 sm:col-1 xl:col-1 flex align-items-center justify-content-end px-0 mx-0 py-0 mx-0 my-0 p-0 w-auto"
          >
            <Button
              v-tooltip.left="'More'" label="+More" style="height: 70%;text-decoration: underline;"
              severity="primary" class="mr-2 py-2 text-lg" @click="goToPayment()"
            />

            <!-- Añadido margen derecho para separación -->
          </div>
        </div>
      </div>
      <DynamicTable
        :data="listItems" :columns="columns" :options="options" :pagination="pagination"
        @on-confirm-create="clearForm" @open-edit-dialog="getItemById($event)"
        @update:clicked-item="getItemById($event)" @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems" @on-sort-field="onSortField"
      >
        <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center ">
            <Row>
              <Column
                footer="Total #:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; color:#ffffff; background-color:#0F8BFD;"
              />
              <Column
                footer="Total $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total Transit #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total Deposit #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />

              <Column
                :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
            </Row>
            <Row>
              <Column
                footer="Total Applied $:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; color:#000000; background-color:#ffffff;"
              />
              <Column
                footer="Total N.A $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total Transit $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total Deposit $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />

              <Column
                :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
            </Row>
            <Row>
              <Column
                footer="Total Applied %:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; color:#ffffff; background-color:#0F8BFD;"
              />
              <Column
                footer="Total N.A %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total Transit %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total Deposit %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />

              <Column
                :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
            </Row>
          </ColumnGroup>
        </template>
      </DynamicTable>
    </div>
    <!-- Section Invoice -->
    <div class="col-12 md:order-0 md:col-12 xl:col-6 lg:col-12 px-1 ">
      <div class="flex justify-content-end align-items-center">
        <div class="flex justify-content-end align-items-center">
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="my-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Email'" label="Email" icon="pi pi-envelope" class="h-2.5rem w-6rem"
              severity="primary" @click="clearForm"
            />
          </div>
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="my-2 ml-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Print'" label="Print" icon="pi pi-print" class="h-2.5rem w-5rem"
              severity="primary" @click="clearForm"
            />
          </div>
          <div
            v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
            class="my-2 ml-2 flex justify-content-end px-0"
          >
            <Button
              v-tooltip.left="'Export'" label="Export" icon="pi pi-download" class="h-2.5rem w-6rem"
              severity="primary" @click="clearForm"
            />
          </div>
        </div>
      </div>

      <div class="card px-1 m-0 py-0">
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          Client Details
        </div>
        <TabView id="tabView" v-model:activeIndex="activeTab" class="no-global-style">
          <TabPanel>
            <template #header>
              <div
                class=" tab-header flex align-items-center gap-2 p-1 tab-header"
                :style="`${activeTab === 0 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: auto`"
              >
                <span class="font-bold">Client Main Info</span>
              </div>
            </template>
            <!-- Contenido de la pestaña Client Main Info -->
            <div class="grid m-0 p-0 mb-0 my-0 py-0">
              <!-- Ajusta la altura según sea necesario -->
              <div class="col-12 xl:col-6 p-0 py-0 mb-0 my-1 mt-0">
                <div class="flex items-center w-full mb-0 mt-0" style="flex-wrap: nowrap;">
                  <!-- Ajuste de mt -->
                  <label
                    for="primaryPhone" class="font-bold ml-3 mt-2"
                    style="margin-right: 9px; flex: 0 0 auto;"
                  >Primary Phone</label>
                  <IconField class="w-full" icon-position="left">
                    <InputIcon class="pi pi-phone text-blue-500" />
                    <InputText
                      id="primaryPhone" v-model="filterToSearch.primaryPhone"
                      class="w-full" style="flex: 1;"
                    />
                  </IconField>
                </div>

                <div class="flex items-center w-full mb-2 mt-2" style="flex-wrap: nowrap;">
                  <!-- Ajuste de mt -->
                  <label
                    for="alternativePhone" class="font-bold ml-1 mb-0 mr-0 pl-0 mt-2"
                    style="margin-right: 9px; flex: 0 0 auto;"
                  >Alternative Phone</label>
                  <IconField class="w-full" icon-position="left">
                    <InputIcon class="pi pi-phone text-blue-500" />
                    <InputText
                      id="alternativePhone" v-model="filterToSearch.alternativePhone"
                      class="w-full ml-0" style="flex: 1;"
                    />
                  </IconField>
                </div>

                <div class="flex items-center w-full mb-0 mt-2" style="flex-wrap: nowrap;">
                  <!-- Ajuste de mt -->
                  <label
                    for="email" class="font-bold mb-0 pl-2 mt-2 ml-8"
                    style="margin-right: 9px; flex: 0 0 auto;"
                  >Email</label>
                  <IconField class="w-full" icon-position="left">
                    <InputIcon class="pi pi-envelope text-blue-500" />
                    <InputText
                      id="email" v-model="filterToSearch.email" class="w-full"
                      style="flex: 1;"
                    />
                  </IconField>
                </div>
              </div>

              <div class="col-12 xl:col-6 p-0 px-2 my-1">
                <div class="flex items-center w-full mb-2 mt-1" style="flex-wrap: nowrap;">
                  <!-- Ajuste de mt -->
                  <label
                    for="contactName" class="font-bold mb-0 mt-1 ml-6 mx-2"
                    style="margin-right: 9px; flex: 0 0 auto;"
                  >Contact Name</label>
                  <InputText
                    id="contactName" v-model="filterToSearch.contactName" class="w-full"
                    style="flex: 1;"
                  />
                </div>

                <div class="flex items-center w-full mb-2 mt-1" style="flex-wrap: nowrap;">
                  <!-- Ajuste de mt -->
                  <label
                    for="country" class="font-bold mb-0 ml-6 mt-1"
                    style="margin-right: 9px; flex: 0 0 auto;"
                  >Country</label>
                  <InputText
                    id="country" v-model="filterToSearch.country" class="w-full"
                    style="flex: 1;"
                  />
                </div>
              </div>
            </div>
          </TabPanel>
          <!-- Campos -->

          <TabPanel>
            <template #header>
              <div
                class="flex align-items-center gap-2 p-1 tab-header"
                :style="`${activeTab === 1 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: auto`"
              >
                <span class="font-bold">Agency Contact</span>
              </div>
            </template>
            <div class="grid py-1 ">
              <div class="col 12 xl:col-11 md:col-11 lg:col-11 ">
                <DynamicTable
                  :data="listItemsAgency" :columns="columnsAgency" :options="optionsAgency"
                  :pagination="paginationAgency" @on-confirm-create="clearForm"
                  @open-edit-dialog="getItemById($event)" @update:clicked-item="getItemById($event)"
                  @on-change-pagination="payloadOnChangePage = $event"
                  @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems"
                  @on-sort-field="onSortField"
                />
              </div>

              <div
                class="col 12 xl:col-1 md:col-1 lg:col-1 py-3 flex justify-content-end align-items-start"
              >
                <Button
                  v-tooltip.left="'Agency Contact'" icon="pi pi-user-plus"
                  class="p-button-text p-button-lg pl-3 pr-6" @click="clearForm"
                />
              </div>
            </div>
          </TabPanel>
        </TabView>
      </div>

      <div class="grid grid-nogutter px-0 py-0 mt-0">
        <div class="col-12 flex align-items-center">
          <!-- Usar flex para alinear en la misma fila -->
          <div class="col-11 md:col-11 lg:col-11 xl:col-11 py-0 px-0">
            <div class="card py-1 p-0 my-0">
              <div
                class="header-content text-lg mr-0 font-bold"
                style="background-color: var(--primary-color); color: white; padding: 8px; border-top-left-radius: 8px; border-top-right-radius: 8px;"
              >
                Invoice View
              </div>
            </div>
          </div>
          <div
            class="col-1 md:col-1 lg:col-1 sm:col-1 xl:col-1 flex align-items-center justify-content-end px-1 my-0 p-0 w-auto"
          >
            <Button
              v-tooltip.left="'More'" label="+More" style="height: 70%;text-decoration: underline;"
              severity="primary" class="mr-2 py-2 text-lg" @click="goToInvoice()"
            />
            <!-- Añadido margen derecho para separación -->
          </div>
        </div>
      </div>

      <DynamicTable
        :data="listItemsInvoice" :columns="columnsInvoice" :options="optionsInvoice"
        :pagination="paginationInvoice" @on-confirm-create="clearForm" @open-edit-dialog="getItemById($event)"
        @update:clicked-item="getItemById($event)" @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems" @on-sort-field="onSortField"
      >
        <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center ">
            <Row>
              <Column
                footer="Total #:" :colspan="4"
                footer-style="text-align:left; font-weight: bold; color:#ffffff; background-color:#0F8BFD;"
              />
              <Column
                footer="Total $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total Pending #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total Invoice B #:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
            </Row>
            <Row>
              <Column
                footer="Total 30 Days #:" :colspan="4"
                footer-style="text-align:left; font-weight: bold; color:#000000; background-color:#ffffff;"
              />
              <Column
                footer="Total 60 Days #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total 90 Days #:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total 120 Days #:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
            </Row>
            <Row>
              <Column
                footer="Total 30 Days $:" :colspan="4"
                footer-style="text-align:left; font-weight: bold; color:#ffffff; background-color:#0F8BFD;"
              />
              <Column
                footer="Total 60 Days $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total 90 Days $:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
              <Column
                footer="Total 120 Days $:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#0F8BFD; color:#ffffff;"
              />
            </Row>
            <Row>
              <Column
                footer="Total 30 Days %:" :colspan="4"
                footer-style="text-align:left; font-weight: bold; color:#000000; background-color:#ffffff;"
              />
              <Column
                footer="Total 60 Days %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total 90 Days %:" :colspan="2"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
              <Column
                footer="Total 120 Days %:" :colspan="3"
                footer-style="text-align:left; font-weight: bold; background-color:#ffffff; color:#000000;"
              />
            </Row>
          </ColumnGroup>
        </template>
      </DynamicTable>
    </div>
  </div>
</template>

<style lang="scss">
.timezone-input {
    background-color: var(--primary-color);
    color: white;
    font-weight: bold;
}

.timezone-input::placeholder {
    color: white;
    /* Cambia el color del placeholder a blanco */

}

.no-global-style .p-tabview-nav-container {
    padding-left: 0 !important;
    background-color: initial !important;
    border-top-left-radius: 0 !important;
    border-top-right-radius: 0 !important;
}

#tabView {
    .p-tabview-nav-container {
        .p-tabview-nav-link {
            color: var(--secondary-color) !important;
        }

        .p-tabview-nav-link:hover {
            border-bottom-color: transparent !important;
        }
    }
}

.tab-view {
    height: 10px;

}

.text-red {
    color: red;
    /* Define color rojo para el asterisco */
}

.tab-header {
    background-color: transparent !important;
    /* Fondo transparente para las pestañas */
    background-color: initial !important;
    /* Color azul para el texto */
    transition: color 0.3s;
    /* Transición suave para el color */
    padding: 6px;
    /* Menor padding para altura reducida */
}

.tab-header:hover {
    color: #0A6FB8 !important;
    /* Color más oscuro al pasar el mouse */
}

.header-card {
    border: 1px solid #ccc;
    /* Borde de la tarjeta */
    border-bottom-left-radius: 0;
    /* Borde inferior izquierdo recto */
    border-bottom-right-radius: 0;
    /* Borde inferior derecho recto */
    overflow: hidden;
    /* Asegura que los bordes redondeados se apliquen correctamente */
}

.custom-input {
    height: 30px;
    /* Altura por defecto */
}

.agency-input {
    height: 30px;
    /* Altura por defecto */
}

.hotel-input {
    height: 30px;
    /* Altura por defecto */
}

@media (max-width: 1199px) {

    /* Para pantallas grandes (lg) */
    .responsive-height {
        height: 23vh;
        /* Ajustar altura para pantallas grandes */
        margin: auto;
    }
}

/* Media query para pantallas grandes */
@media (min-width: 1200px) {
    .responsive-height {
        height: 25vh;
        /* Ajustar altura para pantallas extra largas */
    }

    .custom-input {
        height: 30px;
        /* Ajustar altura para pantallas grandes */
    }

    .agency-input {
        height: 30px;
        /* Ajustar altura para pantallas grandes */
    }

    .hotel-input {
        height: 30px;
        /* Altura por defecto */

    }

}
</style>
