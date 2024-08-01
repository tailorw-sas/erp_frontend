<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { ref, watch } from 'vue'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import AttachmentIncomeHistoryDialog from '~/components/income/attachment/AttachmentIncomeHistoryDialog.vue'

const props = defineProps({

  header: {
    type: String,
    required: true
  },

  openDialog: {
    type: Boolean,
    required: true
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  isCreationDialog: {
    type: Boolean,
    required: true
  },
  listItems: {
    type: Array,
    required: false
  },
  addItem: {
    type: Function as any,
    required: false
  },
  updateItem: {
    type: Function as any,
    required: false
  },
  selectedInvoice: {
    type: String,
    required: true
  },
  selectedInvoiceObj: {
    type: Object,
    required: true
  }
})

const { data: userData } = useAuth()

const invoice = ref<any>(props.selectedInvoiceObj)
const attachmentHistoryDialogOpen = ref<boolean>(false)

const filterToSearch = ref({
  criteria: 'invoice.invoiceId',
  search: ''
})

const typeError = ref(false)

const attachmentTypeList = ref<any[]>([])
const resourceTypeList = ref<any[]>([])
const confattachmentTypeListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-attachment-type',
})
const confResourceTypeListApi = reactive({
  moduleApi: 'payment',
  uriApi: 'resource-type',
})

const formReload = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()

const loadingSearch = ref(false)

const idItem = ref('')
const item = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.incomeId,
  employee: userData?.value?.user?.name,
  employeeId: userData?.value?.user?.userId,
  resourceType: null
})

const itemTemp = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.incomeId,
  employee: userData?.value?.user?.name,
  employeeId: userData?.value?.user?.userId,
  resourceType: null
})
const toast = useToast()

const Fields: Array<FieldDefinitionType> = [
  {
    field: 'resource',
    header: 'Resource',
    dataType: 'number',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true
  },
  {
    field: 'resourceType',
    header: 'Resource Type',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
  },
  {
    field: 'type',
    header: 'Attachment Type',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string(),
      name: z.string(),

    })
      .refine((value: any) => value && value.id && value.name, { message: `The Transaction Type field is required` })
  },
  {
    field: 'file',
    header: 'Path',
    dataType: 'fileupload',
    class: 'field col-12 required',
    headerClass: 'mb-1',

  },
  {
    field: 'filename',
    header: 'Filename',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'textarea',
    class: 'field col-12 ',
    headerClass: 'mb-1',
  }
]

const Columns: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '70px' },
  { field: 'invoice.invoiceId', header: 'Income Id', type: 'text', width: '100px' },
  { field: 'type', header: 'Type', type: 'select', width: '100px' },
  { field: 'filename', header: 'Filename', type: 'text', width: '150px' },
  { field: 'remark', header: 'Remark', type: 'text', width: '100px', columnClass: 'w-10 overflow-hidden' },

]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-attachment',
  loading: false,
  showDelete: false,
  showFilters: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const PayloadOnChangePage = ref<PageState>()

const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const ListItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')

async function ResetListItems() {
  Payload.value.page = 0
}

function OnSortField(event: any) {
  if (event) {
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    getList()
  }
}

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
}

async function getList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    loadingSearch.value = true
    ListItems.value = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'invoice')) {
        iterator['invoice.invoiceId'] = iterator.invoice.invoiceId
      }
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false }]
    }

    if (ListItems.value.length > 0) {
      idItemToLoadFirstTime.value = ListItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    loadingSearch.value = false
  }
}
async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  getList()
}

async function getAttachmentTypeList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 20,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confattachmentTypeListApi.moduleApi, confattachmentTypeListApi.uriApi, payload)
    const { data: dataList } = response
    attachmentTypeList.value = []
    for (const iterator of dataList) {
      attachmentTypeList.value = [...attachmentTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
}

async function getResourceTypeList() {
  try {
    const payload = {
      filter: [],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confResourceTypeListApi.moduleApi, confResourceTypeListApi.uriApi, payload)
    const { data: dataList } = response
    resourceTypeList.value = []
    for (const iterator of dataList) {
      resourceTypeList.value = [...resourceTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
}

function searchAndFilter() {
  Payload.value.filter = [...Payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.search) {
    Payload.value.filter = [...Payload.value.filter, {
      key: 'filename',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'OR',
      type: 'filterSearch',
    }, {
      key: 'remark',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'OR',
      type: 'filterSearch',
    },]
  }
  getList()
}

function clearFilterToSearch() {
  Payload.value.filter = [...Payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.search = ''
  getList()
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file
    payload.invoice = props.selectedInvoice
    payload.file = file
    payload.type = item.type?.id
    payload.paymentResourceType = item.resourceType?.id
    payload.employee = userData?.value?.user?.name
    payload.employeeId = userData?.value?.user?.userId
    delete payload.resourceType
    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file
  payload.file = file
  payload.type = item.type?.id
  payload.paymentResourceType = item.resourceType?.id
  payload.employee = userData?.value?.user?.name
  payload.employeeId = userData?.value?.user?.userId
  delete payload.resourceType
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingSaveAll.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    clearForm()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingSaveAll.value = false
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  if (!item?.type?.id) {
    return typeError.value = true
  }
  loadingSaveAll.value = true
  let successOperation = true

  if (idItem.value) {
    try {
      if (props.isCreationDialog) {
        await props.updateItem(item)
        clearForm()
        return loadingSaveAll.value = false
      }
      await updateItem(item)
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      if (props.isCreationDialog) {
        await props.addItem(item)
        clearForm()
        return loadingSaveAll.value = false
      }
      await createItem(item)
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
    getList()
  }
}

function requireConfirmationToDelete(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      await deleteItem(idItem.value)
      getList()
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.id = response.id
        item.value.type = { ...response.type, fullName: `${response?.type?.code} - ${response?.type?.name}` }
        item.value.filename = response.filename
        item.value.file = response.file
        item.value.remark = response.remark
        item.value.invoice = response.invoice
        item.value.resource = response.invoice.invoiceId
        item.value.resourceType = response.paymenResourceType
      }

      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

const $primevue = usePrimeVue()

function formatSize(bytes: number) {
  const k = 1024
  const dm = 3
  const sizes = $primevue.config.locale?.fileSizeTypes || ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']

  if (bytes === 0) {
    return `0 ${sizes[0]}`
  }

  const i = Math.floor(Math.log(bytes) / Math.log(k))
  const formattedSize = Number.parseFloat((bytes / k ** i).toFixed(dm))

  return `${formattedSize} ${sizes[i]}`
}

function downloadFile() {
  if (item.value) {
    const link = document.createElement('a')
    link.href = item.value.file
    link.setAttribute('download', `${item.value.filename}`)
    link.setAttribute('target', '_blank')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

watch(() => props.selectedInvoiceObj, () => {
  invoice.value = props.selectedInvoiceObj

  if (invoice.value?.id) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: invoice.value?.id,
      logicalOperation: 'AND'
    }]
    getList()
  }
})

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

onMounted(() => {
  const invoice = props.selectedInvoice || props.selectedInvoiceObj?.id
  if (invoice) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: invoice,
      logicalOperation: 'AND'
    }]
  }
  if (!props.isCreationDialog && invoice) {
    getList()
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-screen"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" :style="{ width: '80%' }"
    @hide="closeDialog(ListItems.length)"
  >
    <div class="grid p-fluid formgrid">
      <div class="col-12 order-1 md:order-0 md:col-9 pt-5">
        <Accordion :active-index="0" class="mb-2 card p-0">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full">
                <div>Filters</div>
              </div>
            </template>

            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex align-items-center gap-2">
                <label for="email">Income:</label>
                <div class="w-full lg:w-auto">
                  <IconField icon-position="left" class="w-full">
                    <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                    <InputIcon class="pi pi-search" />
                  </IconField>
                </div>
              </div>
              <div class="flex align-items-center">
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" class="w-3rem" outlined icon="pi pi-filter-slash" :loading="loadingSearch" @click="clearFilterToSearch" />
              </div>
            </div>
          </AccordionTab>
        </Accordion>
        <DynamicTable
          :data="isCreationDialog ? listItems as any : ListItems" :columns="Columns" :options="options"
          :pagination="Pagination" @update:clicked-item="getItemById($event)"
          @open-edit-dialog="getItemById($event)" @on-confirm-create="clearForm"
          @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
          @on-list-item="ResetListItems" @on-sort-field="OnSortField"
        />
      </div>
      <div class="col-12 order-2 md:order-0 md:col-3 pt-5">
        <div>
          <div class="font-bold text-lg px-4 bg-primary custom-card-header">
            {{ idItem ? "Edit" : "Add" }}
          </div>
          <div class="card">
            <EditFormV2
              :key="formReload" :fields="Fields" :item="item"
              :show-actions="true" :loading-save="loadingSaveAll" @cancel="clearForm"
              @delete="requireConfirmationToDelete($event)" @submit="saveItem(item)"
            >
              <template #field-resourceType="{ item: data, onUpdate }">
                <DebouncedAutoCompleteComponent
                  v-if="!loadingSaveAll"
                  id="autocomplete"
                  field="name"
                  item-value="id"
                  :model="data.resourceType"
                  :suggestions="resourceTypeList"
                  @change="($event) => {
                    onUpdate('resourceType', $event)
                  }" @load="($event) => getResourceTypeList()"
                >
                  <template #option="props">
                    <span>{{ props.item.name }}</span>
                  </template>
                </DebouncedAutoCompleteComponent>
                <Skeleton v-else height="2rem" class="mb-2" />
              </template>
              <template #field-type="{ item: data, onUpdate }">
                <DebouncedAutoCompleteComponent
                  v-if="!loadingSaveAll" id="autocomplete" field="fullName"
                  item-value="id" :model="data.type" :suggestions="attachmentTypeList" @change="($event) => {
                    onUpdate('type', $event)
                    typeError = false
                  }" @load="($event) => getAttachmentTypeList($event)"
                >
                  <template #option="props">
                    <span>{{ props.item.fullName }}</span>
                  </template>
                </DebouncedAutoCompleteComponent>
                <Skeleton v-else height="2rem" class="mb-2" />
              </template>
              <template #field-file="{ onUpdate, item: data }">
                <FileUpload
                  :max-file-size="1000000" :multiple="false" auto custom-upload @uploader="(event: any) => {
                    const file = event.files[0]
                    onUpdate('file', file)
                    onUpdate('filename', data.file.name || data.file.split('/')[data.file.split('/')?.length - 1])
                  }"
                >
                  <template #header="{ chooseCallback }">
                    <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                      <div class="flex gap-2">
                        <Button id="btn-choose" class="p-2" icon="pi pi-plus" text @click="chooseCallback()" />
                        <Button
                          icon="pi pi-times" class="ml-2" severity="danger" :disabled="!data.file" text
                          @click="onUpdate('file', null)"
                        />
                      </div>
                    </div>
                  </template>
                  <template #content="{ files }">
                    <div class="w-full flex justify-content-center">
                      <ul v-if="files[0] || data.file" class=" p-0 m-0" style="width: 300px;  overflow: hidden;">
                        <li class=" surface-border flex align-items-center w-fit">
                          <div class="flex flex-column w-fit  text-overflow-ellipsis">
                            <span
                              class="text-900 font-semibold text-xl mb-2 text-overflow-clip overflow-hidden"
                              style="width: 300px;"
                            >{{ data.file.name
                              || data.file.split("/")[data.file.split("/")?.length - 1] }}</span>
                            <span v-if="data.file.size" class="text-900 font-medium">
                              <Badge severity="warning">
                                {{ formatSize(data.file.size) }}
                              </Badge>
                            </span>
                          </div>
                        </li>
                      </ul>
                    </div>
                  </template>
                </FileUpload>
              </template>
              <template #form-footer="props">
                <Button
                  v-tooltip.top="'Save'" class="w-3rem sticky" icon="pi pi-save"
                  :disabled="!props.item?.fieldValues?.file" @click="saveItem(props.item.fieldValues)"
                />
                <Button
                  v-tooltip.top="'View File'" class="w-3rem ml-1 sticky" icon="pi pi-eye"
                  :disabled="!idItem" @click="downloadFile"
                />
                <Button
                  v-if="true"
                  v-tooltip.top="'Show History'" class="w-3rem ml-1 sticky" icon="pi pi-book"
                  @click="() => attachmentHistoryDialogOpen = true"
                />
                <Button
                  v-tooltip.top="'Add'" class="w-3rem ml-1 sticky" icon="pi pi-plus" @click="() => {
                    idItem = ''
                    item = itemTemp
                    clearForm()
                  }"
                />
                <Button v-tooltip.top="'Delete'" outlined severity="danger" class="w-3rem ml-1 sticky" icon="pi pi-trash" :disabled="!idItem" @click="requireConfirmationToDelete" />
                <Button
                  v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times" @click="() => {
                    clearForm()
                    closeDialog(ListItems.length)
                  }"
                />
              </template>
            </EditFormV2>
          </div>
        </div>
      </div>
      <div v-if="attachmentHistoryDialogOpen">
        <AttachmentIncomeHistoryDialog :close-dialog="() => { attachmentHistoryDialogOpen = false }" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="invoice.id" :selected-invoice-obj="invoice" header="Attachment Status History" :attachment-type="item.type" />
      </div>
    </div>
  </Dialog>
</template>
