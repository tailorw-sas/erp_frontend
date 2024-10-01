<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { v4 as uuidv4 } from 'uuid'
import type { PageState } from 'primevue/paginator'
import { filter } from 'lodash'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

const { data: userData } = useAuth()
const toast = useToast()
const listItems = ref<any[]>([])
const fileUpload = ref()
const attachUpload = ref()
const inputFile = ref()
const attachFile = ref()
const importModel = ref({
  importFile: '',
  // totalAmount: 0,
  hotel: '',
  attachFile: [] as File[],
  employee: '',
})
const uploadComplete = ref(false)
const loadingSaveAll = ref(false)
const haveErrorImportStatus = ref(false)
const totalImportedRows = ref(0)

const confApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment/import',
})

const confPaymentApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment',
})

const hotelList = ref<any[]>([])
const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})
// VARIABLES -----------------------------------------------------------------------------------------
const idItem = ref('')

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'bookingId', header: 'Booking Id', type: 'text' },
  { field: 'InvoiceNo', header: 'Invoice No', type: 'text' },
  { field: 'FullName', header: 'Full Name', type: 'text' },
  { field: 'ReservationNo', header: 'Reservation No', type: 'text' },
  { field: 'couponNo', header: 'Coupon No', type: 'text' },
  { field: 'checkIn', header: 'Check In', type: 'text' },
  { field: 'checkOut', header: 'Check Out', type: 'text' },
  { field: 'adults', header: 'Adults', type: 'text' },
  { field: 'children', header: 'Children', type: 'text' },
  { field: 'amount', header: 'Amount', type: 'text' },
  { field: 'trans', header: 'Trans.', type: 'text' },
  { field: 'remarks', header: 'Remark', type: 'text' },
  { field: 'impSta', header: 'Imp. Status', type: 'slot-text', showFilter: false },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Payment Import Expense to booking',
  moduleApi: 'payment',
  uriApi: 'payment/import',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const payload = ref<IQueryRequest>({
  filter: [{
    key: 'importType',
    operator: 'EQUALS',
    value: ENUM_PAYMENT_IMPORT_TYPE.BOOKING,
    logicalOperation: 'AND'
  }],
  query: '',
  pageSize: 10,
  page: 0,
  // sortBy: 'name',
  // sortType: ENUM_SHORT_TYPE.ASC
})

const payloadOnChangePage = ref<PageState>()
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------
const fileNames = computed(() => {
  return importModel.value.attachFile.map(file => file.name).join(', ')
})
// FUNCTIONS ---------------------------------------------------------------------------------------------

async function getErrorList() {
  try {
    payload.value = { ...payload.value, query: idItem.value }

    let rowError = ''
    listItems.value = []
    const newListItems = []
    const response = await GenericService.importSearch(confPaymentApi.moduleApi, confPaymentApi.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response.paginatedResponse

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      rowError = ''
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        for (const err of iterator.errorFields) {
          rowError += `- ${err.message} \n`
        }

        // const datTemp = new Date(iterator.row.transactionDate)
        newListItems.push({ ...iterator.row, id: iterator.id, impSta: `Warning row ${iterator.rowNumber}: \n ${rowError}`, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]
  }
  catch (error) {
    console.error('Error loading file:', error)
  }
}

async function clearForm() {
  await goToList()
}

async function onChangeFile(event: any) {
  if (event.target.files && event.target.files.length > 0) {
    inputFile.value = event.target.files[0]
    importModel.value.importFile = inputFile.value.name
    event.target.value = ''
    await activeImport()
  }
}

async function onChangeAttachFile(event: any) {
  if (event.target.files && event.target.files.length > 0) {
    const files: File[] = Array.from(event.target.files)
    const pdfFiles = files.filter(file => file.type === 'application/pdf')

    if (pdfFiles.length > 0) {
      importModel.value.attachFile = pdfFiles // Almacena objetos File
    }
    else {
      importModel.value.attachFile = [] // Limpia si no hay PDFs
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select only PDF files.', life: 10000 })
    }

    event.target.value = '' // Limpia el input
    await activeImport()
  }
}

async function importExpenseBooking() {
  loadingSaveAll.value = true
  options.value.loading = true
  let successOperation = true
  uploadComplete.value = true
  try {
    if (!inputFile.value) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select a file', life: 10000 })
      return
    }
    const uuid = uuidv4()
    idItem.value = uuid
    const file = await inputFile.value
    // const base64String: any = await fileToBase64(inputFile.value)
    // const base64 = base64String.split('base64,')[1]
    // const file = await base64ToFile(base64, inputFile.value.name, inputFile.value.type)

    const pdfFiles = importModel.value.attachFile // Usa el modelo correcto

    if (!pdfFiles || pdfFiles.length === 0) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select at least one file', life: 10000 })
      return
    }

    const formData = new FormData()
    formData.append('file', file)
    formData.append('importProcessId', uuid)
    formData.append('importType', ENUM_PAYMENT_IMPORT_TYPE.BOOKING)
    // formData.append('totalAmount', importModel.value.totalAmount.toFixed(0).toString())
    formData.append('hotelId', importModel.value.hotel)
    formData.append('employeeId', userData?.value?.user?.userId || '')
    for (const fileInput of pdfFiles) {
      formData.append('attachments', fileInput)
    }

    await GenericService.importFile(confApi.moduleApi, confApi.uriApi, formData)
  }
  catch (error: any) {
    successOperation = false
    uploadComplete.value = false
    options.value.loading = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }

  if (successOperation) {
    await validateStatusImport()
    if (!haveErrorImportStatus.value) {
      await getErrorList()
      if (listItems.value.length === 0) {
        toast.add({ severity: 'info', summary: 'Confirmed', detail: `The file was upload successful!. ${totalImportedRows.value ? `${totalImportedRows.value} rows imported.` : ''}`, life: 0 })
        options.value.loading = false
        await clearForm()
      }
    }
  }
  loadingSaveAll.value = false
  options.value.loading = false
}

async function validateStatusImport() {
  options.value.loading = true
  return new Promise<void>((resolve) => {
    let status = 'RUNNING'
    const intervalID = setInterval(async () => {
      try {
        const response = await GenericService.getById(confPaymentApi.moduleApi, confPaymentApi.uriApi, idItem.value, 'import-status')
        status = response.status
        totalImportedRows.value = response.totalRows ?? 0
      }
      catch (error: any) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
        haveErrorImportStatus.value = true
        clearInterval(intervalID)
        uploadComplete.value = false
        options.value.loading = false
        resolve() // Resuelve la promesa cuando el estado es FINISHED
      }

      if (status === 'FINISHED') {
        clearInterval(intervalID)
        options.value.loading = false
        resolve() // Resuelve la promesa cuando el estado es FINISHED
      }
    }, 10000)
  })
}

async function resetListItems() {
  payload.value.page = 0
  getErrorList()
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...parseFilter || []]
  getErrorList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getErrorList()
  }
}

async function goToList() {
  await navigateTo('/payment')
}

async function getHotelList(query: string = '') {
  try {
    let payload = {
      filter: [{
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 100,
      page: 0,
      // sortBy: 'name',
      // sortType: ENUM_SHORT_TYPE.DESC
    }

    if (query !== '') {
      payload = {
        ...payload,
        filter: [...payload.filter, { key: 'name', operator: 'LIKE', value: query, logicalOperation: 'OR' }]
      }
    }

    const response = await GenericService.search(confHotelApi.moduleApi, confHotelApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, code: iterator.code, name: `${iterator.code}- ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading city state list:', error)
  }
}

async function activeImport() {
  if (importModel.value.hotel !== '' && importModel.value.importFile !== '' && importModel.value.attachFile !== null) {
    uploadComplete.value = false
  }
  else {
    uploadComplete.value = true
  }
}

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getErrorList()
})

onMounted(async () => {
  // getErrorList()
  uploadComplete.value = true
  await getHotelList('')
})
</script>

<template>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class=" p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div
                class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center"
              >
                <div>
                  Expense to Booking
                </div>
              </div>
            </template>
            <div class="grid p-0 m-0" style="margin: 0 auto;">
              <div class="col-12 md:col-4 xs:col-6 align-items-center my-0 py-0">
                <div class="flex align-items-center mb-2">
                  <label class="w-4rem pt-3">Hotel: <span class="p-error">*</span></label>
                  <div class="w-full">
                    <Dropdown
                      v-model="importModel.hotel"
                      class="w-full" :options="hotelList" option-label="name"
                      option-value="id" placeholder="Select Hotel" @change="activeImport()"
                    />
                  </div>
                </div>
              </div>

              <!-- <div class="col-12 md:col-3 align-items-center my-0 py-0">
                <div class="flex align-items-center mb-2">
                  <label class="w-7rem">Total Amount: </label>
                  <div class="w-full">
                    <InputNumber v-model="importModel.totalAmount" mode="currency" currency="USD" locale="en-US" class="w-full" />
                  </div>
                </div>
              </div> -->

              <div class="col-12 md:col-4 align-items-center my-0 py-0">
                <div class="flex align-items-center mb-2">
                  <label class="w-8rem">Import Data: <span class="p-error">*</span> </label>
                  <div class="w-full">
                    <div class="p-inputgroup w-full">
                      <InputText
                        ref="fileUpload" v-model="importModel.importFile" placeholder="Choose file"
                        class="w-full" show-clear aria-describedby="inputtext-help"
                      />
                      <span class="p-inputgroup-addon p-0 m-0">
                        <Button icon="pi pi-file-import" severity="secondary" class="w-2rem h-2rem p-0 m-0" @click="fileUpload.click()" />
                      </span>
                    </div>
                    <small id="username-help" style="color: #808080;">Select a file of type XLS or XLSX</small>
                    <input
                      ref="fileUpload" type="file" style="display: none;"
                      accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                      @change="onChangeFile($event)"
                    >
                  </div>
                </div>
              </div>

              <div class="col-12 md:col-4 xs:col-6 align-items-center my-0 py-0">
                <div class="flex align-items-center mb-2">
                  <label class="w-7rem">Attach File: <span class="p-error">*</span> </label>
                  <div class="w-full">
                    <div class="p-inputgroup w-full">
                      <InputText
                        ref="attachUpload" v-model="fileNames" placeholder="Choose file"
                        class="w-full" show-clear aria-describedby="inputtext-help"
                      />
                      <span class="p-inputgroup-addon p-0 m-0">
                        <Button icon="pi pi-file-import" severity="secondary" class="w-2rem h-2rem p-0 m-0" @click="attachUpload.click()" />
                      </span>
                    </div>
                    <small id="username-help" style="color: #808080;">Select a file of type PDF</small>
                    <input
                      ref="attachUpload" type="file" style="display: none;" accept="application/pdf" webkitdirectory multiple
                      @change="onChangeAttachFile($event)"
                    >
                  </div>
                </div>
              </div>
            </div>
          </accordionTab>
        </accordion>
      </div>

      <DynamicTable
        :data="listItems" :columns="columns" :options="options" :pagination="pagination"
        @on-confirm-create="clearForm" @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems" @on-sort-field="onSortField"
      >
        <template #column-impSta="{ data }">
          <div id="fieldError">
            <span v-tooltip.bottom="data.impSta" style="color: red;">{{ data.impSta }}</span>
          </div>
        </template>
      </DynamicTable>

      <div class="flex align-items-end justify-content-end">
        <Button
          v-tooltip.top="'Import file'" class="w-3rem mx-2" icon="pi pi-check" :disabled="uploadComplete"
          @click="importExpenseBooking"
        />
        <Button
          v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times"
          @click="clearForm"
        />
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

.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  max-width: 150px; /* Ajusta el ancho máximo según tus necesidades */
}
</style>
