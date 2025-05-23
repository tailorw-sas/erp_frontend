<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { v4 as uuidv4 } from 'uuid'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

const emit = defineEmits(['close'])
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
  transactionType: '',
  attachFile: null,
  employee: '',
})
const uploadComplete = ref(false)
const loadingSaveAll = ref(false)
const haveErrorImportStatus = ref(false)
const totalImportedRows = ref(0)

const objLoading = ref({
  loadingTransactionType: false
})

const confApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment-detail/import',
})

const confPaymentApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment-detail',
})

const transactionStatusList = ref<any[]>([])
const confTransactionStatusApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-payment-transaction-type',
})
// VARIABLES -----------------------------------------------------------------------------------------
const idItem = ref('')

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'transactionId', header: 'Id', type: 'text' },
  { field: 'paymentId', header: 'Payment Id', type: 'text' },
  { field: 'transactionCategoryName', header: 'Trans. Cat.', tooltip: 'Transaction Category', type: 'text' },
  { field: 'amount', header: 'Amount', type: 'text' },
  { field: 'transactionCheckDepositBalance', header: 'Deposit Balance', type: 'text' },
  { field: 'transactionCheckDepositAmount', header: 'Amount', type: 'text' },
  { field: 'remarks', header: 'Remark', type: 'text' },
  { field: 'impSta', header: 'Imp. Status', tooltip: 'Import Status', type: 'slot-text', showFilter: false, minWidth: '150px' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Payment Import of Anti Income',
  moduleApi: 'payment',
  uriApi: 'payment-detail/import',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  showPagination: false,
  messageToDelete: 'Do you want to save the change?'
})

const payload = ref<IQueryRequest>({
  filter: [{
    key: 'importType',
    operator: 'EQUALS',
    value: ENUM_PAYMENT_IMPORT_TYPE.ANTI,
    logicalOperation: 'AND'
  }],
  query: '',
  pageSize: 1000,
  page: 0,
  sortBy: '',
  sortType: ENUM_SHORT_TYPE.ASC
})

const payloadOnChangePage = ref<PageState>()
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: 'row.rowNumber'
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
          rowError += `- ${err.message?.trim()}\n`
        }

        // const datTemp = new Date(iterator.row.transactionDate)
        newListItems.push(
          {
            ...iterator.row,
            id: iterator.id,
            amount: iterator.row.amount ? formatNumber(iterator.row.amount) : 0,
            impSta: `Warning row ${iterator.rowNumber}: \n${rowError}`,
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
  listItems.value = []
  if (event.target.files && event.target.files.length > 0) {
    loadingSaveAll.value = true
    inputFile.value = event.target.files[0]
    importModel.value.importFile = inputFile.value.name
    event.target.value = ''
    await activeImport()
    loadingSaveAll.value = false
  }
}

async function onChangeAttachFile(event: any) {
  listItems.value = []
  if (event.target.files && event.target.files.length > 0) {
    attachFile.value = event.target.files[0]
    importModel.value.attachFile = attachFile.value.name
    event.target.value = ''
    await activeImport()
  }
}

async function importAntiIncome() {
  loadingSaveAll.value = true
  let successOperation = true
  uploadComplete.value = true
  listItems.value = []
  options.value.loading = true
  try {
    if (!inputFile.value) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select a file', life: 10000 })
      options.value.loading = false
      return
    }
    const uuid = uuidv4()
    idItem.value = uuid
    const base64String: any = await fileToBase64(inputFile.value)
    const base64 = base64String.split('base64,')[1]
    const file = await base64ToFile(base64, inputFile.value.name, inputFile.value.type)

    const base64String1: any = await fileToBase64(attachFile.value)
    const base641 = base64String1.split('base64,')[1]
    const file1 = await base64ToFile(base641, attachFile.value.name, attachFile.value.type)

    const formData = new FormData()
    formData.append('file', file)
    formData.append('importProcessId', uuid)
    formData.append('importType', ENUM_PAYMENT_IMPORT_TYPE.ANTI)
    // formData.append('totalAmount', importModel.value.totalAmount.toFixed(0).toString())
    formData.append('transactionType', typeof importModel.value.transactionType === 'object' ? importModel.value.transactionType.id : importModel.value.transactionType)
    formData.append('employee', userData?.value?.user?.userId || '')
    formData.append('attachment', file1)

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
        toast.add({ severity: 'info', summary: 'Confirmed', detail: `The file was upload successful!. ${totalImportedRows.value ? `${totalImportedRows.value} rows imported.` : ''}`, life: 10000 })
        onClose()
        options.value.loading = false
        await clearForm()
      }
    }
  }
  loadingSaveAll.value = false
  options.value.loading = false
}

function onClose() {
  emit('close')
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

async function getTransactionStatusList() {
  try {
    const payload = {
      filter: [
        {
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        },
        {
          key: 'antiToIncome',
          operator: 'EQUALS',
          value: true,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 100,
      page: 0,
      // sortBy: 'name',
      // sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confTransactionStatusApi.moduleApi, confTransactionStatusApi.uriApi, payload)
    const { data: dataList } = response
    transactionStatusList.value = []
    for (const iterator of dataList) {
      transactionStatusList.value = [
        ...transactionStatusList.value,
        {
          id: iterator.id,
          code: iterator.code,
          name: `${iterator.code}- ${iterator.name}`,
          status: iterator.status
        }
      ]
    }
  }
  catch (error) {
    console.error('Error loading city state list:', error)
  }
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  code?: string
}

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    code: data.code,
  }
}

async function getTransactionStatusListForSelectField(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  try {
    objLoading.value.loadingTransactionType = true
    let listTemp: any[] = []
    transactionStatusList.value = []
    listTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    listTemp = [...new Set(listTemp)]
    transactionStatusList.value = [...transactionStatusList.value, ...listTemp]
  }
  catch (error) {
    objLoading.value.loadingTransactionType = false
  }
  finally {
    objLoading.value.loadingTransactionType = false
  }
}

async function activeImport() {
  if (importModel.value.transactionType !== '' && importModel.value.importFile !== '' && importModel.value.attachFile !== null) {
    uploadComplete.value = false
  }
  else {
    uploadComplete.value = true
  }
}

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 50

  getErrorList()
})

onMounted(async () => {
  // getErrorList()
  uploadComplete.value = true
  // await getTransactionStatusList()
})
</script>

<template>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class="mt-3">
        <!-- <Accordion :active-index="0" class="mb-2"> -->
        <AccordionTab>
          <!-- <template #header>
            <div
              class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center"
            >
              <div>
                Import Anti to Income
              </div>
            </div>
          </template> -->
          <div class="grid p-0 m-0" style="margin: 0 auto;">
            <div class="flex align-items-center mb-2">
              <label class="w-17rem">Transaction Category: <span class="p-error">*</span></label>
              <div class="w-full">
                <!-- <Dropdown
                      v-model="importModel.transactionType"
                      class="w-full" :options="transactionStatusList" option-label="name"
                      option-value="id" placeholder="Select Document" @change="activeImport()"
                    /> -->
                <DebouncedAutoCompleteComponent
                  id="autocomplete"
                  class="w-full h-2rem align-items-center"
                  field="name"
                  item-value="id"
                  :model="importModel.transactionType"
                  :loading="objLoading.loadingTransactionType"
                  :suggestions="[...transactionStatusList]"
                  @change="($event) => {
                    importModel.transactionType = $event
                  }"
                  @load="async($event) => {
                    const filter: FilterCriteria[] = [
                      {
                        key: 'antiToIncome',
                        operator: 'EQUALS',
                        value: true,
                        logicalOperation: 'AND',
                      },
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
                    await getTransactionStatusListForSelectField(confTransactionStatusApi.moduleApi, confTransactionStatusApi.uriApi, objQueryToSearch, filter)
                  }"
                />
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

            <div class="col-12 md:col-4 xs:col-6 align-items-center my-0 py-0">
              <div class="flex align-items-center mb-2">
                <label class="w-19rem">Import Data (XLS or XLSX): <span class="p-error">*</span></label>
                <div class="w-full">
                  <div class="p-inputgroup w-full">
                    <InputText
                      ref="fileUpload" v-model="importModel.importFile" placeholder="Choose file"
                      class="w-full" show-clear aria-describedby="inputtext-help"
                    />
                    <span class="p-inputgroup-addon p-0 m-0">
                      <Button icon="pi pi-file-import" severity="secondary" class="w-2rem h-2rem p-0 m-0" :disabled="loadingSaveAll" @click="fileUpload.click()" />
                    </span>
                  </div>

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
                <label class="w-11rem">Attach File ( PDF): <span class="p-error">*</span> </label>
                <div class="w-full">
                  <div class="p-inputgroup w-full">
                    <InputText
                      ref="attachUpload" v-model="importModel.attachFile" placeholder="Choose file"
                      class="w-full" show-clear aria-describedby="inputtext-help"
                    />
                    <span class="p-inputgroup-addon p-0 m-0">
                      <Button icon="pi pi-file-import" severity="secondary" class="w-2rem h-2rem p-0 m-0" @click="attachUpload.click()" />
                    </span>
                    <span class="p-inputgroup-addon p-0 m-0 ml-1">
                      <Button
                        v-tooltip.top="'Import file'" class="w-3rem mx-2" icon="pi pi-check"
                        :disabled="uploadComplete || !importModel.transactionType || !inputFile || !attachFile"
                        :loading="options.loading"
                        @click="importAntiIncome"
                      />
                    </span>
                  </div>

                  <input
                    ref="attachUpload" type="file" style="display: none;" accept="application/pdf"
                    @change="onChangeAttachFile($event)"
                  >
                </div>
              </div>
            </div>
          </div>
        </accordionTab>
      </div>

      <DynamicTable
        :data="listItems" :columns="columns" :options="options" :pagination="pagination"
        @on-confirm-create="clearForm" @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems" @on-sort-field="onSortField"
      >
        <template #column-impSta="{ data }">
          <div id="fieldError" v-tooltip.bottom="data.impSta" class="import-ellipsis-text">
            <span style="color: red;">{{ data.impSta }}</span>
          </div>
        </template>
      </DynamicTable>

      <div class="flex align-items-end justify-content-end mt-2" />
    </div>
  </div>
</template>

<style lang="scss">
.custom-file-upload {
  background-color: transparent !important;
  border: none !important;
  text-align: left !important;
}
</style>
