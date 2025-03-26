<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'

import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { useRoute } from 'vue-router'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IQueryRequest, Pagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter } from '~/components/fields/interfaces/IFieldInterfaces'
import DynamicTablePrint from '~//components/table/DynamicTablePrint.vue'
import { formatNumber } from '~/utils/helpers'

import type { IData } from '~/components/table/interfaces/IModelData'

const emit = defineEmits(['close'])

const toast = useToast()
const totalInvoiceAmount = ref(0)
const idItemToLoadFirstTime = ref('')
const listPrintItems = ref<any[]>([])
const totalDueAmount = ref(0)

const allDefaultItem = { id: 'All', name: 'All', code: 'All' }

const objPayloadOfCheckBox = ref({
  groupByClient: false,
  invoiceSupport: false,
  invoiceAndBookings: true
})

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

const ENUM_FILTER = [
  { id: 'id', name: 'Id' },
]
const transformedData = listPrintItems.value.map(item => ({
  ...item,
  hotel: item.hotel?.name || 'N/A'
}))
// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'invoiceNo', header: 'Id', type: 'text', objApi: confagencyListApi },
  { field: 'manageInvoiceType', header: 'Type', type: 'select', objApi: confinvoiceApi },
  {
    field: 'hotel',
    header: 'Hotel',
    width: '80px', // Ancho estándar
    minWidth: '80px', // Mantiene un tamaño mínimo
    maxWidth: '120px', // Controla el tamaño máximo
    columnClass: 'truncate-text', // Clase CSS para truncar el texto
    type: 'select',
    objApi: confinvoiceApi
  },
  { field: 'agencyCd', header: 'Agency CD', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confagencyListApi },
  { field: 'invoiceNumber', header: 'Inv. No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen. Date', type: 'date' },
  { field: 'isManual', header: 'Manual', type: 'bool', tooltip: 'Manual' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'number' },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'number' },
  { field: 'hasAttachments', header: 'Attachment', type: 'bool' },
  { field: 'aging', header: 'Aging', type: 'text' },
  { field: 'status', header: 'Status', type: 'slot-select', localItems: ENUM_INVOICE_STATUS.map(item => ({
    ...item,
    name: item.name.toUpperCase() // Asegura coincidencia con "RECONCILED"
  })) }
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
  showSelectedItems: true,
  messageToDelete: 'Do you want to save the change?'
})
const initialLimit = 10
const route = useRoute()

const payload = ref({
  filter: [],
  query: '',
  pageSize: 0,
  page: 0,
  sortBy: '',
  sortType: ''
})
// Ahora esto será válido
const payloadInv = ref<IQueryRequest>({
  filter: [],
  page: 0,
  size: 50,
  sort: 'invoiceDate,desc'
})

const payloadLocalStorage = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'invoiceId',
  sortType: ENUM_SHORT_TYPE.DESC
})

const payloadOnChangePage = ref<PageState>()

const pagination = ref<Pagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
function handleSelectionChange(items: any[]) {
  console.log('Selección actualizada:', items)
  selectedElements.value = items
}

const selectedElements = ref<any[]>([])
// -------------------------------------------------------------------------------------------------------

onMounted(() => {
  const storedData = localStorage.getItem('invoiceViewData')
  if (storedData) {
    const parsedData = JSON.parse(storedData)

    // Mantener estas líneas (CRÍTICO)
    listPrintItems.value = parsedData.listItems
    totalInvoiceAmount.value = parsedData.totals.invoiceTotalAmount
    totalDueAmount.value = parsedData.totals.invoiceDueTotalAmount

    // Añadir esto (SOLUCIÓN CLAVE)
    pagination.value.totalElements = parsedData.totalElements // <- Usar el total de la paginación
    pagination.value.totalPages = 1 // Forzar 1 página
    pagination.value.totalPages = Math.ceil(parsedData.totalElements / pagination.value.limit)
  }
})

function handlePaginationChange(event: any) {
  pagination.value.page = event.page + 1
  pagination.value.limit = event.rows
}

// FUNCTIONS ---------------------------------------------------------------------------------------------

// async function getPrintList(totalFromIndex: number) {
//   try {
//     options.value.loading = true
//     listPrintItems.value = []

//     totalInvoiceAmount.value = totalFromIndex;

//     const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value);

//     const { data: dataList, page, size, totalElements, totalPages } = response;

//     pagination.value.page = page; // Convierte a 1-based
//     pagination.value.limit = size;
//     pagination.value.totalElements = listItems.length;
//     pagination.value.totalPages = totalPages;

//      const newListItems = dataList.map(iterator => {
//       let invoiceNumber;
//       if (iterator?.invoiceNumber?.split('-')?.length === 3) {
//         invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`;
//       } else {
//         invoiceNumber = iterator?.invoiceNumber;
//       }

//       return {
//         ...iterator,
//         loadingEdit: false,
//         loadingDelete: false,
//         agencyCd: iterator?.agency?.code,
//         hasAttachments: iterator.hasAttachments || false,
//         aging: iterator?.aging || 0,
//         dueAmount: iterator?.dueAmount ? formatNumber(iterator.dueAmount) : 0,
//         invoiceAmount: iterator?.invoiceAmount ? formatNumber(iterator.invoiceAmount) : 0,
//         invoiceNumber: invoiceNumber ? invoiceNumber.replace('OLD', 'CRE') : '',
//         hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}` },
//         manageInvoiceType: { ...iterator?.manageInvoiceType, name: `${iterator?.manageInvoiceType?.code || ''}-${iterator?.manageInvoiceType?.name || ''}` }
//       };
//     });

//     listPrintItems.value = newListItems; // Asigna los nuevos elementos a la lista
//   } catch (error) {
//     console.error(error);
//   } finally {
//     options.value.loading = false;
//   }
// }

async function savePrint() {
  options.value.loading = true
  const startTime = Date.now() // Captura el tiempo de inicio
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
    const endTime = Date.now() // Captura el tiempo de fin
    const totalTime = ((endTime - startTime) / 1000).toFixed(2) // Tiempo total en segundos
    const totalFiles = selectedElements.value.length // Total de archivos descargados

    // Mostrar el mensaje en el toast
    toast.add({
      severity: 'info',
      summary: 'Confirmed',
      detail: `The process was executed successfully, records printed  ${totalFiles}`,
      life: 5000 // Duración del toast en milisegundos
    })
    onClose()
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

function onClose() {
  emit('close')
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
  payload.value = localStorage.getItem('payloadOfInvoiceList')
    ? JSON.parse(localStorage.getItem('payloadOfInvoiceList') || '{}')
    : payload.value
  payload.value.page = 0
}

async function parseDataTableFilter(payloadFilter: any) {
  payload.value = localStorage.getItem('payloadOfInvoiceList')
    ? JSON.parse(localStorage.getItem('payloadOfInvoiceList') || '{}')
    : payload.value
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)

  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      /*   if (parseFilter[i]?.key === 'status') {
        parseFilter[i].key = 'invoiceStatus'
      }
*/
      if (parseFilter[i]?.key === 'status') {
        parseFilter[i].key = 'invoiceStatus'
      }

      if (parseFilter[i]?.key === 'invoiceNumber') {
        parseFilter[i].key = 'invoiceNumberPrefix'
      }
    }
  }

  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  // await getPrintList()
}
// payload.value.filter = [...parseFilter || []]
// getPrintList()
// }

async function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'hotel') {
      event.sortField = 'hotel.name'
    }
    if (event.sortField === 'status') {
      event.sortField = 'invoiceStatus'
    }
    if (event.sortField === 'agency') {
      event.sortField = 'agency.name'
    }
    if (event.sortField === 'agencyCd') {
      event.sortField = 'agency.code'
    }

    if (event.sortField === 'invoiceNumber') {
      event.sortField = 'invoiceNumberPrefix'
    }

    // payload.value = localStorage.getItem('payloadOfInvoiceList')
    //   ? JSON.parse(localStorage.getItem('payloadOfInvoiceList') || '{}')
    //   : payload.value
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    // await getPrintList()
  }
}
function getStatusName(code: string) {
  switch (code) {
    case 'PROCESSED': return 'Processed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Cancelled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}
function getStatusSeverity(code: string) {
  switch (code.toUpperCase()) {
    case 'RECONCILED': return 'success'
    case 'PENDING': return 'warning'
    case 'CANCELED': return 'danger'
    default: return 'info'
  }
}
function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCESSED': return '#FF8D00'
    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELED': return '#888888'
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

watch(payloadOnChangePage, async (newValue) => {
  payload.value = localStorage.getItem('payloadOfInvoiceList')
    ? JSON.parse(localStorage.getItem('payloadOfInvoiceList') || '{}')
    : payload.value
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 50
  // await getPrintList()
})

function onMultipleSelect(selectedItems: any[]) {
  selectedElements.value = Array.isArray(selectedItems)
    ? selectedItems
    : [selectedItems]
}
</script>

<template>
  <!-- <div class="font-bold text-lg px-4 bg-primary custom-card-header">
    Invoices to Print
  </div> -->
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9 mt-0">
      <div class="p-fluid pt-3">
        <div class="card p-0">
          <div v-if="pagination.totalElements === 0" class="no-data-message" />

          <DynamicTablePrint
            :data="listPrintItems"
            :columns="columns"
            :options="options"
            :pagination="pagination"
            data-key="id"
            @on-confirm-create="clearForm"
            @on-change-pagination="handlePaginationChange"
            @on-change-filter="parseDataTableFilter"
            @on-list-item="resetListItems"
            @on-sort-field="onSortField"
            @update:clicked-item="onMultipleSelect($event)"
            @update:selection="onMultipleSelect"
            @selection-change="onMultipleSelect"
          >
            <template #pagination-total>
              <span class="font-bold">
                {{ pagination.totalElements }}
              </span>
            </template>
            <Paginator
              :rows="pagination.limit"
              :total-records="pagination.totalElements"
              :rows-per-page-options="[10, 20, 30, 50]"
              @page="handlePaginationChange"
            />

            <template #body-hotel="{ data }">
              {{ data.hotel?.name || 'N/A' }} <!-- Muestra el nombre del hotel -->
            </template>

            <template #column-status="{ data: item }">
              <Badge
                :value="getStatusName(item?.status)"
                :severity="getStatusSeverity(item.status)"
                :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`"
              />
            </template>

            <template #datatable-footer>
              <ColumnGroup type="footer" class="flex align-items-center" style="font-weight: 700">
                <Row>
                  <Column
                    footer="Totals:"
                    :colspan="9"
                    footer-style="text-align:right; font-weight: 900"
                  />
                  <Column :colspan="1" :footer="`$${formatNumber(totalDueAmount)}`" footer-style="text-align:left; font-weight: 700" />
                  <Column :colspan="1" :footer="`$${formatNumber(totalDueAmount)}`" footer-style="text-align:left; font-weight: 700" />
                  <Column :colspan="3" footer-style="text-align:right; font-weight: 700" />
                </Row>
              </ColumnGroup>
            </template>
          </DynamicTablePrint>
        </div>
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
          <Button
            v-tooltip.top="'Print'"
            class="w-3rem mx-2" icon="pi pi-print" :disabled="selectedElements.length === 0" @click="savePrint"
          />
          <!-- <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times" @click="clearForm" /> -->
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
/* Estilos personalizados para el paginador */
.p-paginator .p-paginator-pages .p-paginator-page {
  opacity: 1 !important;
  pointer-events: auto !important;
}
</style>
