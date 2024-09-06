<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import AttachmentHistoryTotal from './AttachmentHistoryTotal.vue'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'

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
const defaultAttachmentType = ref<any>(null)

const route = useRoute()

const filterToSearch = ref({
  criteria: 'invoice.invoiceId',
  search: invoice.value.invoiceId
})

const typeError = ref(false)

const attachmentTypeList = ref<any[]>([])
const confattachmentTypeListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-attachment-type',
})

const formReload = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()

const loadingSearch = ref(false)

const loadingDelete = ref(false)

const idItem = ref('')
const item = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.invoiceId,
  employee: userData?.value?.user?.name,
  employeeId: userData?.value?.user?.userId,
  // @ts-expect-error
  resourceType: `${invoice.value.invoiceType?.name ? `${invoice.value.invoiceType?.name}-${invoice.value.invoiceType?.name}` : `${OBJ_ENUM_INVOICE_TYPE_CODE[invoice.value.invoiceType]}-${OBJ_ENUM_INVOICE[invoice.value.invoiceType]}`}`
})

const itemTemp = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.invoiceId,
  employee: userData?.value?.user?.name,
  employeeId: userData?.value?.user?.userId,
  // @ts-expect-error
  resourceType: `${invoice.value.invoiceType?.name ? `${invoice.value.invoiceType?.name}-${invoice.value.invoiceType?.name}` : `${OBJ_ENUM_INVOICE_TYPE_CODE[invoice.value.invoiceType]}-${OBJ_ENUM_INVOICE[invoice.value.invoiceType]}`}`
})
const toast = useToast()

const Fields: Array<Container> = [
  {
    childs: [
      {
        field: 'resource',
        header: 'Resource',
        dataType: 'number',
        class: 'field mb-3 col-12 md: required',
        headerClass: 'mb-1',
        disabled: true
      },
      {
        field: 'resourceType',
        header: 'Resource Type',
        dataType: 'text',
        class: 'field mb-3 col-12 md: required',
        headerClass: 'mb-1',
        disabled: true
      },

      {
        field: 'type',
        header: 'Attachment Type',
        dataType: 'select',
        class: 'field mb-3 col-12 md: required',
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
        class: 'field mb-3 col-12 required',
        headerClass: 'mb-1',

      },
      {
        field: 'filename',
        header: 'Filename',
        dataType: 'text',
        class: 'field mb-3 col-12 required',
        headerClass: 'mb-1',

      },
      {
        field: 'remark',
        header: 'Remark',
        dataType: 'textarea',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      },

    ],
    containerClass: 'w-full'
  }

]

const attachmentHistoryDialogOpen = ref<boolean>(false)

const selectedAttachment = ref<string>('')

const Columns: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '70px' },
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
  sortBy: 'attachmentId',
  sortType: ENUM_SHORT_TYPE.ASC
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

  getInvoiceSupportAttachment()
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
      ListItems.value = [...ListItems.value, { ...iterator,attachmentId:'', loadingEdit: false, loadingDelete: false, type: {
        ...iterator?.type,
        name: `${iterator?.type?.code}-${iterator?.type?.name}`
      } }]
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

async function getInvoiceSupportAttachment() {
  try {
    const payload
      = {
        filter: [{
          key: 'code',
          operator: 'EQUALS',
          value: 'INV',
          logicalOperation: 'AND',

        }],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confattachmentTypeListApi.moduleApi, confattachmentTypeListApi.uriApi, payload)

    if (response?.data?.length > 0) {
      defaultAttachmentType.value = { ...response?.data[0], fullName: `${response?.data[0]?.code}-${response?.data[0]?.name}` }
      item.value.type = defaultAttachmentType.value
      formReload.value++
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
}

async function getAttachmentTypeList(query = '') {
  try {
    const payload
      = {
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
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    attachmentTypeList.value = []
    const response = await GenericService.search(confattachmentTypeListApi.moduleApi, confattachmentTypeListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      attachmentTypeList.value = [...attachmentTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
}

function searchAndFilter() {
  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    Payload.value.filter = [{
      key: filterToSearch.value.criteria,
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  getList()
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file

    payload.invoice = props.selectedInvoice

    payload.file = file

    payload.employee = userData?.value?.user?.name
    payload.employeeId = userData?.value?.user?.userId

    payload.type = item.type?.id
    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file

  payload.file = file

  payload.employee = userData?.value?.user?.name
  payload.employeeId = userData?.value?.user?.userId

  payload.type = item.type?.id
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
    getList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
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
    accept: () => {
      deleteItem(idItem.value)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
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
        item.value.type = { ...response.type, fullName: `${response?.type?.code}-${response?.type?.name}` }
        item.value.filename = response.filename
        item.value.file = response.file
        item.value.remark = response.remark
        item.value.invoice = response.invoice
     //   item.value.resource = response.invoice.invoiceId
        item.value.resourceType = `${`${OBJ_ENUM_INVOICE_TYPE_CODE[response.invoice.invoiceType]}-${OBJ_ENUM_INVOICE[response.invoice.invoiceType]}`}`
        selectedAttachment.value = response.attachmentId
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

function requireConfirmationToSave(item: any) {
  const { event } = item
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      saveItem(item)
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

function showHistory() {
  attachmentHistoryDialogOpen.value = true
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
})

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

watch(PayloadOnChangePage, (newValue) => {
  Payload.value.page = newValue?.page ? newValue?.page : 0
  Payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})


onMounted(() => {
  getInvoiceSupportAttachment()
  if (props.selectedInvoice) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: props.selectedInvoice,
      logicalOperation: 'AND'
    }]
  }
  if (!props.isCreationDialog) {
    getList()
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-fit w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" @hide="closeDialog"
  >
    <div class=" w-fit h-fit overflow-auto p-2">
      <div class="flex lg:flex-row flex-column align-items-start">
        <div class="flex flex-column" style="max-width: 900px;">
          <div class="  mb-2 flex justify-content-end">
            <div class="bg-primary w-fit flex gap-2 justify-center align-content-center align-items-center" style="border-radius: 5px; padding: 11px;">
              <span class="font-bold">Invoice: </span>
              <span>{{ filterToSearch.search }} </span>
            </div>
          </div>
          <div style="max-width: 700px; overflow: auto;">
            <DynamicTable
              :data="isCreationDialog ? listItems as any : ListItems" :columns="Columns" :options="options"
              :pagination="Pagination" @update:clicked-item="getItemById($event)"
              @open-edit-dialog="getItemById($event)" @on-confirm-create="clearForm"
              @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
              @on-list-item="ResetListItems" @on-sort-field="OnSortField"
            />
          </div>
        </div>

        <div class="mx-4 mb-4" style="max-width: 360px;">
          <div class="font-bold text-lg px-4 bg-primary custom-card-header">
            {{ idItem ? "Edit" : "Add" }}
          </div>
          <div class="card">
            <EditFormV2WithContainer
              :key="formReload" :fields-with-containers="Fields" :item="item"
              :show-actions="true" :loading-save="loadingSaveAll" class=" w-full " @cancel="clearForm"
              @delete="requireConfirmationToDelete($event)" @submit="saveItem(item)"
            >
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
                  <template #chip="{ value }">
                    <div>
                      {{ value?.fullName }}
                    </div>
                  </template>
                </DebouncedAutoCompleteComponent>
                <span v-if="typeError" class="error-message p-error text-xs">The Attachment type field is
                  required</span>
              </template>
              async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
              const file = event.files[0]
              objFile.value = file
              listFields[fieldKey] = file
              }

              <template #field-file="{ onUpdate, item: data }">
                <FileUpload
                  accept="application/pdf"
                  :max-file-size="300 * 1024 * 1024" :multiple="false" auto custom-upload @uploader="(event: any) => {
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
                <IfCan :perms="idItem ? ['INVOICE-MANAGEMENT:ATTACHMENT-EDIT'] : ['INVOICE-MANAGEMENT:ATTACHMENT-CREATE']">
                  <Button
                    v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
                    :disabled="!props.item?.fieldValues?.file || idItem !== ''" @click="saveItem(props.item.fieldValues)"
                  />
                </IfCan>

                <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-CREATE']">
                  <Button
                    v-tooltip.top="'Add'" class="w-3rem mx-2 sticky" icon="pi pi-plus" @click="() => {
                      idItem = ''
                      item = itemTemp
                      clearForm()
                    }"
                  />
                </IfCan>

                <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-VIEW-FILE']">
                  <Button
                    v-tooltip.top="'View File'" class="w-3rem mx-2 sticky" icon="pi pi-eye" :disabled="!idItem"
                    @click="downloadFile"
                  />
                </IfCan>

                <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-SHOW-HISTORY']">
                  <Button
                    v-if="selectedInvoiceObj.invoiceType === InvoiceType.INCOME || route.query.type === InvoiceType.INCOME" v-tooltip.top="'Show History'" class="w-3rem mx-2 sticky" icon="pi pi-book"
                    :disabled="!idItem" @click="showHistory"
                  />
                </IfCan>

                <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-DELETE']">
                  <Button
                    v-tooltip.top="'Delete'" outlined severity="danger" class="w-3rem mx-1" icon="pi pi-trash"
                    :disabled="!idItem" @click="requireConfirmationToDelete"
                  />
                </IfCan>
                <Button
                  v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {

                    clearForm()
                    closeDialog()
                  }"
                />
              </template>
            </EditFormV2WithContainer>
          </div>
        </div>
      </div>
    </div>
  </Dialog>

  <div v-if="attachmentHistoryDialogOpen">
    <AttachmentHistoryTotal
      :selected-attachment="selectedAttachment"
      :close-dialog="() => { attachmentHistoryDialogOpen = false; selectedAttachment = '' }"
      header="Attachment Status History" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="selectedInvoice"
      :selected-invoice-obj="item" :is-creation-dialog="false"
    />
  </div>
</template>