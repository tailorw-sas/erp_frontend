<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import { v4 as uuidv4 } from 'uuid'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { itemMenuList } from '~/components/payment/indexBtns'

const toast = useToast()
const listItems = ref<any[]>([])
const fileUpload = ref()
const attachUpload = ref()
const inputFile = ref()
const attachFile = ref()
const importModel = ref({
  importFile: '',
  totalAmount: 0,
  documentId: null,
  attachFile: null
})
const documents = ref([])
const uploadComplete = ref(false)

const loadingSaveAll = ref(false)

const confApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment-detail/import',
})

const confErrorApi = reactive({
  moduleApi: 'payment',
  uriApi: 'payment-detail',
})
// VARIABLES -----------------------------------------------------------------------------------------
const idItem = ref('')

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'manageHotelCode', header: 'Hotel', type: 'text' },
  { field: 'manageClientCode', header: 'Client', type: 'text' },
  { field: 'manageAgencyCode', header: 'Agency', type: 'text' },
  { field: 'totalAmount', header: 'Total Amount', type: 'text' },
  { field: 'TransfDate', header: 'Trans. Date', type: 'date' },
  { field: 'remark', header: 'Remark', type: 'text' },
  { field: 'impSta', header: 'Imp. Status', type: 'slot-text', showFilter: false },
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
  messageToDelete: 'Do you want to save the change?'
})

const payload = ref<IQueryRequest>({
  filter: [],
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
    const param: IQueryRequest = {
      query: idItem.value,
      pageSize: 20,
      page: 0,
      filter: []
    }

    let rowError = ''
    listItems.value = []
    const newListItems = []
    const response = await GenericService.importSearch(confErrorApi.moduleApi, confErrorApi.uriApi, param)

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
        for (const err of iterator.errorField) {
          rowError += `- ${err.message} \n`
        }
        newListItems.push({ ...iterator.row, fullName: `${iterator.row?.firstName} ${iterator.row?.lastName}`, impSta: `Warning row ${iterator.rowNumber}: \n ${rowError}`, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]
    if (listItems.value.length === 0) {
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'The file was imported successfully', life: 3000 })
      await clearForm()
    }
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
    uploadComplete.value = false
  }
}

async function onChangeAttachFile(event: any) {
  if (event.target.files && event.target.files.length > 0) {
    attachFile.value = event.target.files[0]
    importModel.value.attachFile = attachFile.value.name
    uploadComplete.value = false
  }
}

async function importAntiIncome() {
  loadingSaveAll.value = true
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
    const objTemp = {
	    importProcessId: uuid,
      importType: 'NO_VIRTUAL',
      file: base64
    }
    await GenericService.create(confApi.moduleApi, confApi.uriApi, objTemp)
  }
  catch (error: any) {
    successOperation = false
    uploadComplete.value = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }

  loadingSaveAll.value = false
  if (successOperation) {
    await getErrorList()
    // clearForm()
  }
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
  await navigateTo('/invoice')
}

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  // getErrorList()
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
                  Import Anti to Income
                </div>
              </div>
            </template>
            <div class="flex flex-column lg:flex-row w-full">
              <div class="col-12 md:col-3 lg:col-3">
                <label class="w-7rem">Document: </label>
                <div>
                  <Dropdown v-model="importModel.documentId" :options="documents" option-label="label" option-value="value" placeholder="Select Document" />
                </div>
              </div>
              <div class="col-12 md:col-3 lg:col-3">
                <label class="w-7rem">Document: </label>
                <div>
                  <Dropdown v-model="importModel.documentId" :options="documents" option-label="label" option-value="value" placeholder="Select Document" />
                </div>
              </div>
              <div class="col-12 md:col-3 lg:col-3">
                <label class="w-7rem">Import Data: </label>
                <div class="col-12 w-full">
                  <div class="p-inputgroup">
                    <InputText
                      ref="fileUpload"
                      v-model="importModel.importFile"
                      placeholder="Choose file"
                      class="w-full"
                      show-clear
                      aria-describedby="inputtext-help"
                    />
                    <span class="p-inputgroup-addon">
                      <Button icon="pi pi-upload" severity="secondary" class="w-3rem" @click="fileUpload.click()" />
                    </span>
                  </div>
                  <small id="username-help" style="color: #808080;">Select a file of type XLS or XLSX</small>
                  <input ref="fileUpload" type="file" style="display: none;" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" @change="onChangeFile">
                </div>
              </div>
              <div class="flex flex-row w-full align-items-center">
                <label class="w-7rem">Attach File: </label>
                <div class="col-12 md:col-4 lg:col-4">
                  <div class="p-inputgroup">
                    <InputText
                      ref="attachUpload"
                      v-model="importModel.attachFile"
                      placeholder="Choose file"
                      class="w-full"
                      show-clear
                      aria-describedby="inputtext-help"
                    />
                    <span class="p-inputgroup-addon">
                      <Button icon="pi pi-upload" severity="secondary" class="w-3rem" @click="attachUpload.click()" />
                    </span>
                  </div>
                  <small id="username-help" style="color: #808080;">Select a file of type PDF</small>
                  <input ref="attachUpload" type="file" style="display: none;" accept="application/pdf" @change="onChangeAttachFile">
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
        <Button v-tooltip.top="'Import file'" class="w-3rem mx-2" icon="pi pi-check" :disabled="uploadComplete" @click="importAntiIncome" />
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
</style>
