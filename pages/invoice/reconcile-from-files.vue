<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { v4 as uuidv4 } from 'uuid'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { ENUM_INVOICE_IMPORT_TYPE } from '~/utils/Enums'

const toast = useToast()
const { data: userData } = useAuth()
const listItems = ref<any[]>([])
const fileUpload = ref()
const inputFile = ref()
const fileInput = ref()
const invoiceFile = ref('')
const attachFile = ref()
const uploadComplete = ref(false)
const loadingSaveAll = ref(false)
const haveErrorImportStatus = ref(false)
const openSuccessDialog = ref(false)
const messageDialog = ref('')
const importModel = ref({
  attachFile: [] as File[], // Almacena los archivos seleccionados
  employee: ''
})
const attachUpload = ref()

const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/import-reconcile',
})

const confInvoiceApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
})

const fileNames = computed(() => {
  return importModel.value.attachFile.map(file => file.name).join(', ')
})
// VARIABLES -----------------------------------------------------------------------------------------
async function onChangeAttachFile(event: any) {
  if (event.target.files && event.target.files.length > 0) {
    const files: File[] = Array.from(event.target.files)
    const pdfFiles = files.filter(file => file.type === 'application/pdf')

    if (pdfFiles.length > 0) {
      importModel.value.attachFile = pdfFiles // Almacena objetos File
      pdfFiles.forEach((file) => {
        console.log(`Archivo PDF seleccionado: ${file.name}`)
      })
    }
    else {
      importModel.value.attachFile = [] // Limpia si no hay PDFs
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select only PDF files.', life: 10000 })
    }

    event.target.value = '' // Limpia el input
    await activeImport()
  }
}

async function activeImport() {
  if (importModel.value.attachFile !== null) {
    uploadComplete.value = false
  }
  else {
    uploadComplete.value = true
  }
}
//
const idItem = ref('')

// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'id', header: 'Id', type: 'text' },
  { field: 'type', header: 'Type', type: 'text' },
  { field: 'hotel', header: 'Hotel', type: 'text' },
  { field: 'manageAgencyCode', header: 'Agency Cd', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'text' },
  { field: 'invoiceNumber', header: 'Invoice No', type: 'text' },
  { field: 'generationDate', header: 'Generation Date', type: 'date' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  { field: 'size', header: 'Att Size', type: 'text' },
  { field: 'impSta', header: 'Import Status', type: 'slot-text', frozen: true, showFilter: false },

  { field: 'status', header: 'Status', type: 'bool', },
]

const columnsExpandable: IColumn[] = [
  { field: 'firstName', header: 'First Name', type: 'text' },
  { field: 'lastName', header: 'Last Name', type: 'text' },
  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'nights', header: 'Nights', type: 'text' },
  { field: 'adults', header: 'Adults', type: 'text' },
  { field: 'children', header: 'Children', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'text' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'text' },
  { field: 'hotelInvoiceAmount', header: 'Hotel Amount', type: 'text' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  expandableRows: true,
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
    payload.value = { ...payload.value, query: idItem.value }
    let rowError = ''
    listItems.value = []
    const newListItems = []
    const response = await GenericService.importSearch(confInvoiceApi.moduleApi, confInvoiceApi.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response.paginatedResponse

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      rowError = ''
      const rowExpandable = []
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        for (const err of iterator.errorFields) {
          rowError += `- ${err.message} \n`
        }
        rowExpandable.push({ ...iterator.row })
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
  // Verifica que se hayan seleccionado archivos
  try {
    // Verifica que se hayan seleccionado archivos
    const selectedFiles = importModel.value.attachFile // Usa el modelo correcto
    console.log('Archivos seleccionados:', selectedFiles)

    if (!selectedFiles || selectedFiles.length === 0) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select at least one file', life: 10000 })
      return
    }

    // Filtrar archivos inválidos
    const invalidFiles = selectedFiles.filter((fileInput) => {
      return !/^\d+\.pdf$/.test(fileInput.name) || fileInput.type !== 'application/pdf'
    })

    if (invalidFiles.length > 0) {
      const errorMessages = invalidFiles.map(file => `Error en archivo: ${file.name} - Debe ser un PDF y su nombre debe ser solo números.`)
      toast.add({ severity: 'error', summary: 'Error', detail: errorMessages.join(', '), life: 10000 })
      return
    }

    const uuid = uuidv4()
    idItem.value = uuid
    const formData = new FormData()

    // Procesa cada archivo en el array de archivos
   /* const base64Array = []
    for (const fileInput of selectedFiles) {
      const base64String: any = await fileToBase64(fileInput)
      const base64 = base64String.split('base64,')[1]

      const file = await base64ToFile(base64, fileInput.name, fileInput.type)

      // Agrega cada archivo al FormData
      base64Array.push(file)
    }
    base64Array.forEach((file, index) => {
      formData.append('files[]', file)
    })
*/
for (const fileInput of selectedFiles) {
  formData.append('files', fileInput)
}
    // Agrega información adicional al FormData
    formData.append('importProcessId', uuid)
    formData.append('employeeId', userData?.value?.user?.userId)
    formData.append('employee', userData?.value?.user?.name)
    // Envía los datos al servicio
    await GenericService.importReconcileFile(confApi.moduleApi, confApi.uriApi, formData)
  }
  catch (error: any) {
    successOperation = false
    uploadComplete.value = false
    options.value.loading = false
    messageDialog.value = error.data?.data?.error?.errorMessage || 'Error desconocido'
    console.log(' aqui se detiene ')
    openSuccessDialog.value = true
  }

  if (successOperation) {
    console.log(' nunca entro ')
    await validateStatusImport()
    console.log('entre')
    if (!haveErrorImportStatus.value) {
      await getErrorList()
      if (listItems.value.length === 0) {
        options.value.loading = false
        messageDialog.value = 'The files were imported successfully'
        openSuccessDialog.value = true
      }
    }
  }

  loadingSaveAll.value = false
  options.value.loading = false
}

async function validateStatusImport() {
  options.value.loading = true
  console.log('Starting status validation...') // Agregar registro al inicio

  return new Promise<void>((resolve) => {
    let status = 'RUNNING'
    const intervalID = setInterval(async () => {
      try {
        const response = await GenericService.getById(confInvoiceApi.moduleApi, confInvoiceApi.uriApi, idItem.value, 'import-status')
        status = response.status
      }
      catch (error: any) {
        console.error('Error occurred:', error) // Agregar registro de error
        toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 20000 })
        haveErrorImportStatus.value = true
        clearInterval(intervalID)
        uploadComplete.value = false
        options.value.loading = false
        resolve() // Resuelve la promesa cuando el estado es FINISHED
      }

      if (status === 'FINISHED') {
        console.log('Import process finished.') // Agregar registro de finalización
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
  await navigateTo('/invoice')
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
              <div
                class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center"
              >
                <div>
                  Reconcile Invoice From Files
                </div>
              </div>
            </template>
            <div class="grid p-0 m-0" style="margin: 0 auto;">
              <div class="col-12 md:col-6 lg:col-6 align-items-center my-0 py-0">
                <div class="flex align-items-center ">
                  <label class="pr-2 mb-3">Browse Folder:</label>

                  <div style="flex: 1; max-width: 500px; min-width: 0;">
                    <div class="p-inputgroup w-full">
                      <InputText
                        ref="attachUpload" v-model="fileNames" placeholder="Choose file"
                        class="w-full" show-clear aria-describedby="inputtext-help"
                      />

                      <span class="p-inputgroup-addon p-0 m-0">
                        <Button
                          icon="pi pi-file-import" severity="secondary"
                          class="w-2rem h-2rem p-0 m-0" @click="attachUpload.click()"
                        />
                      </span>
                    </div>
                    <small id="username-help" style="color: #808080;">Select a file of type
                      PDF</small>
                    <input
                      ref="attachUpload" type="file" style="display: none;" webkitdirectory multiple
                      @change="onChangeAttachFile($event)"
                    >
                  </div>
                </div>
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <DynamicTable
        :data="listItems" :columns="columns" :options="options" :pagination="pagination"
        @on-confirm-create="clearForm" @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems" @on-sort-field="onSortField"
      >
        <template #column-impSta="{ data }">
          <div id="fieldError" v-tooltip.bottom="data.impSta" class="ellipsis-text">
            <span style="color: red;">{{ data.impSta }}</span>
          </div>
        </template>

        <template #expansion="slotProps">
          <div class="p-0 m-0">
            <DataTable :value="slotProps.data.rowExpandable" striped-rows>
              <Column
                v-for="column of columnsExpandable" :key="column.field" :field="column.field"
                :header="column.header" :sortable="column?.sortable"
              />
              <template #empty>
                <div class="flex flex-column flex-wrap align-items-center justify-content-center py-8">
                  <span
                    v-if="!options?.loading"
                    class="flex flex-column align-items-center justify-content-center"
                  >
                    <div class="row">
                      <i class="pi pi-trash mb-3" style="font-size: 2rem;" />
                    </div>

                  </span>
                  <span v-else class="flex flex-column align-items-center justify-content-center">
                    <i class="pi pi-spin pi-spinner" style="font-size: 2.6rem" />
                  </span>
                </div>
              </template>
            </DataTable>
          </div>
        </template>
      </DynamicTable>

      <div class="flex align-items-end justify-content-end">
        <Button
          v-tooltip.top="'Apply'" class="w-3rem  mx-1" icon="pi pi-check" :disabled="uploadComplete"
          @click="importFile"
        />

        <Button
          v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button mx-1" icon="pi pi-times"
          @click="clearForm"
        />
      </div>
    </div>
  </div>
  <Dialog
    v-model:visible="openSuccessDialog" modal class="mx-3 sm:mx-0 sm:w-full md:w-3"
    content-class="border-round-bottom border-top-1 surface-border" @hide="goToList"
  >
    <template #header>
      <div class="flex justify-content-between">
        <h5 class="m-0">
          Import Status
        </h5>
      </div>
    </template>
    <template #default>
      <div style="border-radius: 0px !important; ">
        <div class="col-12 order-1 md:order-0 justify-content-center align-items-center">
          <p
            class="m-0"
            style="border-bottom-left-radius: 5px !important; font-size: 16px; font-weight: 600; margin-bottom: 25px; margin-top: 20px; color: #0f8bfd; text-align: center"
          >
            {{ messageDialog }}
          </p>
        </div>
      </div>
    </template>
    <template #footer>
      <Button label="Close" class="flex justify-content-end align-items-end" @click="goToList()" />
    </template>
  </Dialog>
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
    max-width: 150px;
    /* Ajusta el ancho máximo según tus necesidades */
}

.border-round-bottom {
    border-bottom-left-radius: 0px !important;
    border-bottom-right-radius: 0px !important;
}
</style>
