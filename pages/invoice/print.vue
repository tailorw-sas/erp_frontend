<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'

import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

import type { IData } from '~/components/table/interfaces/IModelData'

const toast = useToast()
const listItems = ref<any[]>([])
const totalInvoiceAmount = ref(0)
const idItemToLoadFirstTime = ref('')
const listPrintItems = ref<any[]>([])

const totalDueAmount = ref(0)

const filterAllDateRange = ref(false)
const loadingSearch = ref(false)

const loadingSaveAll = ref(false)

const allDefaultItem = { id: 'All', name: 'All', code: 'All' }
const groupByClient = ref(false)
const invoiceSupport = ref(false)
const invoiceAndBookings = ref(true)

const objPayloadOfCheckBox = ref({
  groupByClient: false,
  invoiceSupport: false,
  invoiceAndBookings: true
})

const objPayloadOfCheckBoxTemp = {
  groupByClient: false,
  invoiceSupport: false,
  invoiceAndBookings: true
}

const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [allDefaultItem],
  hotel: [allDefaultItem],
  from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
  to: new Date(),
})

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])

const confApiPrint = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/external-report',
})

const confAgencyApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confinvoiceApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-type',
})

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

// VARIABLES -----------------------------------------------------------------------------------------

//
const idItem = ref('')
const ENUM_FILTER = [
  { id: 'id', name: 'Id' },
]
// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Id', type: 'text' },
  { field: 'manageInvoiceType', header: 'Type', type: 'select',objApi:confinvoiceApi },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: confhotelListApi },
  { field: 'agencyCd', header: 'Agency CD', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confagencyListApi },
  { field: 'invoiceNumber', header: 'Inv. No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen. Date', type: 'date' },
  { field: 'isManual', header: 'Manual', type: 'bool', tooltip: 'Manual' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'text' },
  { field: 'hasAttachments', header: 'Attachment', type: 'bool' },
  { field: 'aging', header: 'Aging', type: 'text' },
  { field: 'status', header: 'Status', width: '100px', frozen: true, type: 'slot-select', localItems: ENUM_INVOICE_STATUS, sortable: true },
]

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice to Print',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  actionsAsMenu: false,
  loading: false,
  showDelete: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  showFilters: true,
  expandableRows: false,
  messageToDelete: 'Do you want to save the change?'
})

const payload = ref<IQueryRequest>({
  filter: [{
    key: 'invoiceStatus',
    operator: 'IN', // Cambia a 'IN' para incluir varios valores
    value: ['RECONCILED', 'SENT'],
    logicalOperation: 'AND'
  }],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'invoiceId',
  sortType: ENUM_SHORT_TYPE.DESC
})

const payloadOnChangePage = ref<PageState>()
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const selectedElements = ref<string[]>([])
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
async function onMultipleSelect(data: any) {
  selectedElements.value = data
}

async function getPrintList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listPrintItems.value = []
    const newListItems = []

    totalInvoiceAmount.value = 0
    totalDueAmount.value = 0

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listPrintItems.value.map(item => item.id))

    for (const iterator of dataList) {
      let invoiceNumber
      if (iterator?.invoiceNumber?.split('-')?.length === 3) {
        invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`
      }
      else {
        invoiceNumber = iterator?.invoiceNumber
      }
      newListItems.push({
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        //  invoiceDate: new Date(iterator?.invoiceDate),
        agencyCd: iterator?.agency?.code,
        aging: 0,
        dueAmount: iterator?.dueAmount || 0,
        invoiceNumber: invoiceNumber ? invoiceNumber.replace('OLD', 'CRE') : '',
        hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}` },
        manageInvoiceType: { ...iterator?.manageInvoiceType, name: `${iterator?.manageInvoiceType?.code || ''}-${iterator?.manageInvoiceType?.name || ''}` }
      })
      existingIds.add(iterator.id)

      totalInvoiceAmount.value += iterator.invoiceAmount
      totalDueAmount.value += iterator.dueAmount ? Number(iterator.dueAmount) : 0
    }

    listPrintItems.value = newListItems
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function savePrint() {
  options.value.loading = true

  try {
    let nameOfPdf = ''
    const payloadTemp: any = {
      invoiceId: selectedElements.value.length ? selectedElements.value : [],
      invoiceType: [],
      groupByClient: objPayloadOfCheckBox.value.groupByClient
    }

    if (objPayloadOfCheckBox.value.invoiceAndBookings) {
      payloadTemp.invoiceType.push('INVOICE_AND_BOOKING')
    }

    if (objPayloadOfCheckBox.value.invoiceSupport) {
      payloadTemp.invoiceType.push('INVOICE_SUPPORT')
    }

    nameOfPdf = `invoice-list-${dayjs().format('YYYY-MM-DD')}.zip`

    const response: any = await GenericService.create(confApiPrint.moduleApi, confApiPrint.uriApi, payloadTemp)

    const url = window.URL.createObjectURL(new Blob([response]))
    const a = document.createElement('a')
    a.href = url
    a.download = nameOfPdf
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.message || 'Transaction was failed', life: 3000 })
  }
  finally {
    options.value.loading = false
    // selectedElements.value = []
    // objPayloadOfCheckBox.value = JSON.parse(JSON.stringify(objPayloadOfCheckBoxTemp))
  }
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
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = [allDefaultItem]
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getAgencyList(query: string = '') {
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
        },

      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confAgencyApi.moduleApi, confAgencyApi.uriApi, payload)
    const { data: dataList } = response
    agencyList.value = [allDefaultItem]
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}


async function clearForm() {
  await goToList()
}

async function resetListItems() {
  payload.value.page = 0
  getPrintList()
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...parseFilter || []]
  getPrintList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getPrintList()
  }
}
function getStatusName(code: string) {
  switch (code) {
    case 'PROCECSED': return 'Processed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Canceled'
    case 'PENDING': return 'Pending'

    default:
      return ''
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
        key: 'checkIn',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.to) {
      newPayload.filter = [...newPayload.filter, {
        key: 'checkIn',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.merchant?.length > 0) {
      const filteredItems = filterToSearch.value.agency.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
          key: 'agency.id',
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
  }
  payload.value = newPayload
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value = {
    criteria: null,
    search: '',
    agency: [allDefaultItem],
    hotel: [allDefaultItem],
    from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
    to: new Date(),
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  getList()
}

async function goToList() {
  await navigateTo('/invoice')
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criteria && filterToSearch.value.search)
  return false
})

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getPrintList()
})

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  getPrintList()
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header">
    Invoice to Print
  </div>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9 mt-2">
      <div class="p-fluid pt-3">
        <DynamicTable
          class="card p-0 "
          :data="listPrintItems"
          :columns="columns"
          :options="options"
          :pagination="pagination"
          @on-confirm-create="clearForm"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="parseDataTableFilter"
          @on-list-item="resetListItems"
          @on-sort-field="onSortField"
          @update:clicked-item="onMultipleSelect($event)"
        >
          <template #column-status="{ data: item }">
            <Badge
              :value="getStatusName(item?.status)"
              :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`"
            />
          </template>

          <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center " style="font-weight: 700">
              <Row>
                <Column footer="Totals:" :colspan="9" footer-style="text-align:right; font-weight: 700" />

                <Column :colspan="1"  :footer="`$${totalInvoiceAmount.toFixed(2)}`"footer-style="text-align:left; font-weight: 700" />
                <Column :colspan="1"  :footer="`$${totalDueAmount.toFixed(2)}`" footer-style="text-align:left; font-weight: 700" />
                <Column :colspan="3"  footer-style="text-align:right; font-weight: 700" />
    
              </Row>
            </ColumnGroup>
          </template>
        </DynamicTable>
      </div>
      <div class="flex justify-content-between">
        <div class="flex align-items-center">
          <div class="ml-2">
            <Checkbox
              id="invoiceAndBookings"
              v-model="objPayloadOfCheckBox.invoiceAndBookings"
              :binary="true"
              disabled
              @update:model-value="($event) => {
                objPayloadOfCheckBox.invoiceAndBookings = $event
              }"
            />
            <label for="invoiceAndBookings" class="ml-2 font-bold">
              Invoice And Bookings
            </label>
          </div>
          <div class="mx-4">
            <Checkbox
              id="invoiceSupport"
              v-model="objPayloadOfCheckBox.invoiceSupport"
              :binary="true"
              @update:model-value="($event) => {
                objPayloadOfCheckBox.invoiceSupport = $event
              }"
            />
            <label for="invoiceSupport" class="ml-2 font-bold">
              Invoice Supports
            </label>
          </div>
          <div>
            <Checkbox
              id="groupByClient"
              v-model="objPayloadOfCheckBox.groupByClient"
              :binary="true"
              @update:model-value="($event) => {
                objPayloadOfCheckBox.groupByClient = $event
              }"
            />
            <label for="groupbyClient" class="ml-2 font-bold">
              Group By Client
            </label>
          </div>
        </div>

        <div class="flex align-items-end justify-content-end">
          <Button v-tooltip.top="'Print'" class="w-3rem mx-2" icon="pi pi-print" @click="savePrint" />
          <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times" @click="clearForm" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.custom-file-upload {
  background-color: transparent !important;
  border: none !important;
  text-align: left !important;
}
.custom-width {
    width: 300px; /* Ajusta el ancho como desees, por ejemplo 300px, 50%, etc. */
}
.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  max-width: 150px; /* Ajusta el ancho máximo según tus necesidades */
}
</style>
