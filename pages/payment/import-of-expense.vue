<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { v4 as uuidv4 } from 'uuid'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { base64ToFile } from '~/utils/helpers'

const toast = useToast()
const { data: userData } = useAuth()
const listItems = ref<any[]>([])
const fileUpload = ref()
const inputFile = ref()
const invoiceFile = ref('')

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
// VARIABLES -----------------------------------------------------------------------------------------
const idItem = ref('')

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'manageHotelCode', header: 'Hotel', type: 'text' },
  { field: 'manageClientCode', header: 'Client', type: 'text' },
  { field: 'manageAgencyCode', header: 'Agency', type: 'text' },
  { field: 'amount', header: 'Total Amount', type: 'text' },
  { field: 'transactionDate', header: 'Trans. Date', type: 'date' },
  { field: 'remarks', header: 'Remark', type: 'text' },
  { field: 'impSta', header: 'Imp. Status', type: 'slot-text', showFilter: false },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Payment Import of Expense',
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
    value: ENUM_PAYMENT_IMPORT_TYPE.EXPENSE,
    logicalOperation: 'AND'
  }
  ],
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
        const dateTemp = !iterator.row ? null : convertirAFechav2(iterator.row.transactionDate)
        newListItems.push(
          {
            ...iterator.row,
            id: iterator.id,
            transactionDate: dateTemp,
            impSta: `Warning row ${iterator.rowNumber}: \n ${rowError}`,
            amount: iterator.row.amount ? formatNumber(iterator.row.amount) : 0,
            loadingEdit: false,
            loadingDelete: false
          }
        )
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
    invoiceFile.value = inputFile.value.name
    uploadComplete.value = false
    event.target.value = ''
  }
}

async function importFile() {
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
    const base64String: any = await fileToBase64(inputFile.value)
    const base64 = base64String.split('base64,')[1]

    const file = await base64ToFile(base64, inputFile.value.name, inputFile.value.type)
    const formData = new FormData()
    formData.append('file', file)
    formData.append('importProcessId', uuid)
    formData.append('importType', ENUM_PAYMENT_IMPORT_TYPE.EXPENSE)
    formData.append('employeeId', userData?.value?.user?.userId)
    await GenericService.importFile(confApi.moduleApi, confApi.uriApi, formData)
  }
  catch (error: any) {
    successOperation = false
    uploadComplete.value = false
    options.value.loading = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }

  loadingSaveAll.value = false
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
        totalImportedRows.value = response.total ?? 0
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

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getErrorList()
})

onMounted(async () => {
  // getErrorList()
})
</script>

<template>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class=" p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
                <div>
                  Import Payment Of Expense From Excel
                </div>
              </div>
            </template>
            <div class="grid p-0 m-0" style="margin: 0 auto;">
              <div class="col-12 md:col-6 lg:col-6 align-items-center my-0 py-0">
                <div class="flex align-items-center mb-2">
                  <label class="w-8rem">Import Data: </label>

                  <div class="w-full">
                    <div class="p-inputgroup w-full">
                      <InputText
                        ref="fileUpload"
                        v-model="invoiceFile"
                        placeholder="Choose file"
                        class="w-full"
                        show-clear
                        aria-describedby="inputtext-help"
                      />
                      <span class="p-inputgroup-addon p-0 m-0">
                        <Button icon="pi pi-file-import" severity="secondary" class="w-2rem h-2rem p-0 m-0" @click="fileUpload.click()" />
                      </span>
                    </div>
                    <small id="username-help" style="color: #808080;">Select a file of type XLS or XLSX</small>
                    <input ref="fileUpload" type="file" style="display: none;" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" @change="onChangeFile($event)">
                  </div>
                </div>
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <DynamicTable
        :data="listItems" :columns="columns" :options="options" :pagination="pagination"
        @on-confirm-create="clearForm"
        @on-change-pagination="payloadOnChangePage = $event" @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems" @on-sort-field="onSortField"
      >
        <template #column-impSta="{ data }">
          <div id="fieldError">
            <span v-tooltip.bottom="data.impSta" style="color: red;">{{ data.impSta }}</span>
          </div>
        </template>
      </DynamicTable>

      <div class="flex align-items-end justify-content-end">
        <Button v-tooltip.top="'Import file'" class="w-3rem mx-2" icon="pi pi-check" :disabled="uploadComplete" @click="importFile" />
        <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times" @click="clearForm" />
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
