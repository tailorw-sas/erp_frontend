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

const emit = defineEmits(['close'])

const toast = useToast()
const totalInvoiceAmount = ref(0)
const listPrintItems = ref<any[]>([])
const totalDueAmount = ref(0)

const objPayloadOfCheckBox = ref({
  groupByClient: false,
  invoiceSupport: false,
  invoiceAndBookings: true
})

const confApiPrint = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/external-report',
})

const confinvoiceApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-type',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Id', type: 'text', objApi: confagencyListApi },
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

const payload = ref({
  filter: [],
  query: '',
  pageSize: 0,
  page: 0,
  sortBy: '',
  sortType: ''
})
const payloadOnChangePage = ref<PageState>()

const pagination = ref<Pagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

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

function getStatusName(code: string) {
  switch (code) {
    case 'PROCESSED': return 'Processed'

    case 'Reconciled': return 'Reconciled'
    case 'Sent': return 'Sent'
    case 'CANCELED': return 'Cancelled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}
function getStatusSeverity(code: string) {
  switch (code) {
    case 'REC': return 'success'
    case 'PENDING': return 'warning'
    case 'CANCELED': return 'danger'
    default: return 'info'
  }
}
function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCESSED': return '#FF8D00'
    case 'REC': return '#005FB7'
    case 'SEN': return '#006400'
    case 'CANCELED': return '#888888'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

async function goToList() {
  await navigateTo('/invoice')
}

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
                :value="getStatusName(item?.manageInvoiceStatus.name)"
                :severity="getStatusSeverity(item?.manageInvoiceStatus.code)"
                :style="`background-color: ${getStatusBadgeBackgroundColor(item.manageInvoiceStatus.code)}`"
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
