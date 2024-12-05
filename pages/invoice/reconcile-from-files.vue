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

/* async function onChangeAttachFile(event: any) {
  if (event.target.files && event.target.files.length > 0) {
    const files: File[] = Array.from(event.target.files);
    const pdfFiles = files.filter(file => file.type === 'application/pdf');

    if (pdfFiles.length > 0) {
      const invalidFiles = pdfFiles.filter(file => {
        // Extrae el nombre del archivo sin extensión
        const fileName = file.name.split('.')[0];
        // Verifica si el nombre del archivo es un número
        return isNaN(Number(fileName)); // Retorna true si no es un número
      });

      if (invalidFiles.length > 0) {
        // Si hay archivos inválidos, muestra un toast con el detalle del error
        invalidFiles.forEach((file) => {
          toast.add({
            severity: 'error',
            summary: 'Error',
            detail: `El archivo '${file.name}' no es válido. Su nombre debe ser un número.`,
            life: 10000
          });
        });
        importModel.value.attachFile = []; // Limpia si hay archivos inválidos
      } else {
        // Si todos los archivos son válidos, almacena los archivos PDF
        importModel.value.attachFile = pdfFiles; // Almacena objetos File
        pdfFiles.forEach((file) => {
          console.log(`Archivo PDF seleccionado: ${file.name}`);
        });
      }
    } else {
      importModel.value.attachFile = []; // Limpia si no hay PDFs
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please select only PDF files.',
        life: 10000
      });
    }

    event.target.value = ''; // Limpia el input
    await activeImport();
  }
}
*/
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
  // { field: 'id', header: 'Id', type: 'text' },
  // { field: 'type', header: 'Type', type: 'text' },
  // { field: 'hotel', header: 'Hotel', type: 'text' },
  // { field: 'manageAgencyCode', header: 'Agency Cd', type: 'text' },
  // { field: 'agency', header: 'Agency', type: 'text' },
  // { field: 'invoiceNumber', header: 'Invoice No', type: 'text' },
  // { field: 'generationDate', header: 'Generation Date', type: 'date' },
  // { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  // { field: 'size', header: 'Att Size', type: 'text' },
  { field: 'filename', header: 'File Name', type: 'text' },
  { field: 'message', header: 'Import Status', type: 'slot-text', frozen: true, showFilter: false },

  // { field: 'status', header: 'Status', type: 'bool', },
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
  expandableRows: false,
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
    listItems.value = []
    const newListItems = []
    const response = await GenericService.importSearch(confInvoiceApi.moduleApi, confInvoiceApi.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push(
          {
            ...iterator,
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
    console.log(error)

    // toast.add({ severity: 'error', summary: 'Error', detail: error.data.error.errorMessage, life: 5000 })
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
  let count = 0
  listItems.value = []
  try {
    const selectedFiles = importModel.value.attachFile // Usa el modelo correcto

    if (!selectedFiles || selectedFiles.length === 0) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select at least one file', life: 10000 })
      return
    }

    // Filtrar archivos inválidos (nombres que no comienzan con números)
    // const invalidFiles = selectedFiles.filter((fileInput) => {
    //   return !/^\d/.test(fileInput.name) // Verifica que el nombre comience con un número
    // })

    // if (invalidFiles.length > 0) {
    //   const errorMessages = invalidFiles.map(file => `Error en archivo: ${file.name} - El nombre debe comenzar con un número.`)

    //   // Agregar errores a la lista para mostrar en la tabla
    //   listItems.value.push(...invalidFiles)

    //   // Log para verificar que se están añadiendo errores
    //   console.log('Errores añadidos a la lista:', invalidFiles)

    //   // Mostrar mensaje de error

    //   // return; // Detener el flujo si hay archivos inválidos
    // }

    const uuid = uuidv4()
    idItem.value = uuid
    const formData = new FormData()

    // Agregar archivos válidos al FormData
    for (const fileInput of selectedFiles) {
      formData.append('files', fileInput)
    }

    // Agrega información adicional al FormData
    formData.append('importProcessId', uuid)
    formData.append('employeeId', userData?.value?.user?.userId)
    formData.append('employee', userData?.value?.user?.name)

    // Envía los datos al servicio
    await GenericService.importReconcileFile(confApi.moduleApi, confApi.uriApi, formData)
    count = selectedFiles.length
  }
  catch (error: any) {
    successOperation = false
    uploadComplete.value = false
    options.value.loading = false

    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }

  if (successOperation) {
    await validateStatusImport() // Validar el estado después de la importación

    if (!haveErrorImportStatus.value) {
      await getErrorList()
      if (listItems.value.length === 0) {
        options.value.loading = false
        navigateTo('/invoice')
        const successMessage = `The files were uploaded successfully, total attachments imported: ${count}!`
        toast.add({ severity: 'info', summary: 'Confirmed', detail: successMessage, life: 0 })
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
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="clearForm"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      >
        <template #column-message="{ data }">
          <div id="fieldError">
            <span v-tooltip.bottom="data.message" style="color: red;">{{ data.message }}</span>
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
