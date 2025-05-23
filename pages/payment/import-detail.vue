<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { v4 as uuidv4 } from 'uuid'
import type { PageState } from 'primevue/paginator'
import { useRoute } from 'vue-router'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

const emit = defineEmits(['close'])
const route = useRoute()
const paymentId = route.query.paymentId

const { data: userData } = useAuth()
const toast = useToast()
const listItems = ref<any[]>([])
const fileUpload = ref()
const inputFile = ref()
const importModel = ref({
  importFile: '',
  employee: '',
})
const uploadComplete = ref(false)
const loadingSaveAll = ref(false)
const haveErrorImportStatus = ref(false)
const totalImportedRows = ref(0)

const confApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment-detail/import',
})

const confPaymentApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment-detail',
})
// VARIABLES -----------------------------------------------------------------------------------------
const idItem = ref('')

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'paymentId', header: 'Payment Id', type: 'text' },
  { field: 'coupon', header: 'Coupon No', type: 'text' },
  { field: 'invoiceNo', header: 'Invoice No', type: 'text' },
  { field: 'balance', header: 'Amount', type: 'text' },
  { field: 'transactionType', header: 'Trans. T', type: 'text' },
  { field: 'anti', header: 'ANTI', type: 'text' },
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
    value: ENUM_PAYMENT_IMPORT_TYPE.DETAIL,
    logicalOperation: 'AND'
  }],
  query: '',
  pageSize: 1000,
  page: 0,
  sortBy: 'row.rowNumber',
  sortType: ENUM_SHORT_TYPE.ASC
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
          rowError += `- ${err.message?.trim()}\n`
        }

        // const datTemp = new Date(iterator.row.transactionDate)
        newListItems.push(
          {
            ...iterator.row,
            id: iterator.id,
            balance: iterator.row.balance ? formatNumber(iterator.row.balance) : 0,
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
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 5000 })
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
    uploadComplete.value = false
    event.target.value = ''
    loadingSaveAll.value = false
  }
}

async function importFileDetail() {
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

    const formData = new FormData()
    formData.append('file', file)
    formData.append('importProcessId', uuid)
    formData.append('importType', ENUM_PAYMENT_IMPORT_TYPE.DETAIL)
    formData.append('employee', userData?.value?.user?.userId || '')
    if (paymentId && paymentId !== '') {
      formData.append('paymentId', paymentId.toString())
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
        toast.add({ severity: 'info', summary: 'Confirmed', detail: `The file was upload successful!. ${totalImportedRows.value ? `${totalImportedRows.value} rows imported.` : ''}`, life: 10000 })
        // options.value.loading = false
        onClose()
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
    }, 5000)
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
  await navigateTo(`/payment/form?id=${paymentId}`)
  // if (paymentId && paymentId !== '') {
  //   await navigateTo(`/payment/form?id=${paymentId}`)
  // }
  // else {
  //   await navigateTo('/payment')
  // }
}

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 50

  getErrorList()
})

onMounted(async () => {
  // getErrorList()
})
</script>

<template>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class="mt-3">
        <AccordionTab>
          <div class="grid p-0 m-0" style="margin: 0 auto;">
            <div class="col-12 md:col-6 lg:col-6 align-items-center my-0 py-0">
              <div class="flex align-items-center mb-2">
                <label class="w-16rem">Import Data (XLS or XLSX): </label>
                <div class="w-full">
                  <div class="p-inputgroup w-full">
                    <InputText
                      ref="fileUpload" v-model="importModel.importFile" placeholder="Choose file"
                      class="w-full" show-clear aria-describedby="inputtext-help"
                    />
                    <span class="p-inputgroup-addon p-0 m-0">
                      <Button
                        icon="pi pi-file-import" severity="secondary" class="w-2rem h-2rem p-0 m-0"
                        :disabled="loadingSaveAll"
                        @click="fileUpload.click()"
                      />
                    </span>
                    <span class="p-inputgroup-addon p-0 m-0 ml-1">
                      <Button
                        v-tooltip.top="'Import file'" class="w-3rem mx-2" icon="pi pi-check"
                        :loading="options.loading"
                        :disabled="uploadComplete || !inputFile"
                        @click="importFileDetail"
                      />
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
