<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { v4 } from 'uuid'
import AttachmentHistoryPartial from './AttachmentHistoryPartial.vue'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import { applyFiltersAndSort } from '~/pages/payment/utils/helperFilters'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'

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
    type: Array<any>,
    required: false,
    default: []
  },
  addItem: {
    type: Function as any,
    required: false
  },
  updateItem: {
    type: Function as any,
    required: false
  },
  deleteItem: {
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
const resourceTypeSelected = ref<any>(null)
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
const confResourceTypeApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-resource-type',
})
const formReload = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()
const resourceTypeList = ref<any[]>([])
const loadingSearch = ref(false)
const pathFileLocal = ref('')

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
  resourceType: `${invoice.value.invoiceType?.name ? `${invoice.value.invoiceType?.name || ''}-${invoice.value.invoiceType?.name || ''}` : `${OBJ_ENUM_INVOICE_TYPE_CODE[invoice.value.invoiceType] || ''}-${OBJ_ENUM_INVOICE[invoice.value.invoiceType] || ''}`}`
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
  resourceType: `${invoice.value.invoiceType?.name ? `${invoice.value.invoiceType?.name || ''}-${invoice.value.invoiceType?.name || ''}` : `${OBJ_ENUM_INVOICE_TYPE_CODE[invoice.value.invoiceType] || ''}-${OBJ_ENUM_INVOICE[invoice.value.invoiceType] || ''}`}`
})
const toast = useToast()

const Fields: Array<FieldDefinitionType> = [
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
    dataType: 'select',
    class: 'field mb-3 col-12 md: required',
    headerClass: 'mb-1',
    disabled: true,
    validation: validateEntityStatus('Resource Type'),
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
    validation: validateFiles(100, ['application/pdf']),
  },
  {
    field: 'filename',
    header: 'Filename',
    dataType: 'text',
    class: 'field mb-3 col-12',
    headerClass: 'mb-1',
    hidden: true
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'textarea',
    class: 'field col-12 ',
    headerClass: 'mb-1',
    validation: z.string().trim().max(255, 'Maximum 255 characters')
  },
]

const attachmentHistoryDialogOpen = ref<boolean>(false)

const selectedAttachment = ref<string>('')

const Columns: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '70px' },
  { field: 'type', header: 'Type', type: 'select', width: '100px' },
  { field: 'filename', header: 'Filename', type: 'text', width: '150px' },
  { field: 'remark', header: 'Remark', type: 'text', width: '100px', maxWidth: '200px', columnClass: 'w-10 overflow-hidden' },

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
  sortType: ENUM_SHORT_TYPE.DESC
})

const ListItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')
const listItemsLocal = ref<any[]>([...props.listItems])

async function ResetListItems() {
  Payload.value.page = 0
}

async function parseDataTableFilterLocal(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  listItemsLocal.value = [...applyFiltersAndSort(Payload.value, listItemsLocal.value)]
}

function onSortFieldLocal(event: any) {
  if (event) {
    if (event.sortField === 'type') {
      event.sortField = 'type.name'
    }
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    parseDataTableFilterLocal(event.filter)
  }
}

function OnSortField(event: any) {
  if (event) {
    if (!props.isCreationDialog) {
      Payload.value.sortBy = event.sortField
      Payload.value.sortType = event.sortOrder
      getList()
    }
    else {
      onSortFieldLocal(event)
    }
  }
}

async function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  await getInvoiceSupportAttachment()
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
      ListItems.value = [...ListItems.value, {
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        type: {
          ...iterator?.type,
          name: `${iterator?.type?.code}-${iterator?.type?.name}`
        }
      }]
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
        filter: [
          {
            key: 'attachInvDefault',
            operator: 'EQUALS',
            value: true,
            logicalOperation: 'AND',
          },
          {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND',
          },
        ],
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
      // formReload.value++
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

async function getResourceTypeList(query = '') {
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
        sortBy: 'name',
        sortType: ENUM_SHORT_TYPE.ASC
      }

    resourceTypeList.value = []
    const response = await GenericService.search(confResourceTypeApi.moduleApi, confResourceTypeApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      resourceTypeList.value = [
        ...resourceTypeList.value,
        {
          id: iterator.id,
          name: `${iterator.code} - ${iterator.name}`,
          code: iterator.code,
          status: iterator.status
        }
      ]
    }
  }
  catch (error) {
    console.error('Error loading resource type list:', error)
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

    // const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file

    payload.invoice = props.selectedInvoice

    // payload.file = file

    payload.employee = userData?.value?.user?.name
    payload.employeeId = userData?.value?.user?.userId

    payload.type = item.type?.id
    if (typeof payload.file === 'object' && payload.file !== null && payload.file?.files && payload.file?.files.length > 0) {
      const file = payload.file.files[0]
      if (file) {
        const objFile = await getUrlOrIdByFile(file)
        payload.file = objFile && typeof objFile === 'object' ? objFile.url : objFile.id
      }
      else {
        payload.file = ''
      }
    }
    else if (pathFileLocal.value !== null && pathFileLocal.value !== '') {
      payload.file = pathFileLocal.value
    }
    else {
      payload.file = ''
    }
    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  // const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file

  // payload.file = file

  payload.employee = userData?.value?.user?.name
  payload.employeeId = userData?.value?.user?.userId

  payload.type = item.type?.id
  if (typeof payload.file === 'object' && payload.file !== null && payload.file?.files && payload.file?.files.length > 0) {
    const file = payload.file.files[0]
    if (file) {
      const objFile = await getUrlOrIdByFile(file)
      payload.file = objFile && typeof objFile === 'object' ? objFile.url : objFile.id
    }
    else {
      payload.file = ''
    }
  }
  else if (pathFileLocal.value !== null && pathFileLocal.value !== '') {
    payload.file = pathFileLocal.value
  }
  else {
    payload.file = ''
  }
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    if (props.isCreationDialog) {
      props.deleteItem(id)
    }
    else {
      await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id, 'employee', userData?.value?.user?.userId)
    }
    clearForm()
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    clearForm()
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
      item.resourceType = resourceTypeSelected.value
      if (props.isCreationDialog) {
        item.id = v4()
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

function canNotDeleteLastDefaultItem() {
  const countDefaultsItems = listItemsLocal.value.filter((item: any) => item.type?.attachInvDefault).length
  return props.isCreationDialog && countDefaultsItems === 1 && listItemsLocal.value.length > 1
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
      if (item.value.type?.attachInvDefault && canNotDeleteLastDefaultItem()) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Item cannot be deleted', life: 3000 })
      }
      else {
        await deleteItem(idItem.value)
        if (!props.isCreationDialog) {
          getList()
        }
      }
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

    if (props.isCreationDialog) {
      const data = listItemsLocal.value?.find((attachment: any) => attachment?.id === id) as any
      item.value = { ...data }
      formReload.value += 1
      return loadingSaveAll.value = false
    }

    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.id = response.id
        item.value.type = { ...response.type, fullName: `${response?.type?.code}-${response?.type?.name}` }
        item.value.filename = response.filename
        item.value.file = response.file
        item.value.remark = response.remark
        item.value.invoice = response.invoice
        item.value.resource = response.invoice.invoiceId
        item.value.resourceType = `${`${OBJ_ENUM_INVOICE_TYPE_CODE[response.invoice.invoiceType] || ''}-${OBJ_ENUM_INVOICE[response.invoice.invoiceType] || ''}`}`
        selectedAttachment.value = response.attachmentId
        pathFileLocal.value = response.file
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
  if (!useRuntimeConfig().public.showSaveConfirm) {
    saveItem(item)
  }
  else {
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
}

function showHistory() {
  attachmentHistoryDialogOpen.value = true
}

function downloadFile() {
  if (listItemsLocal.value?.length > 0) {
    // Selecciona el primer elemento automáticamente
    item.value = { ...listItemsLocal.value[0] } // Asigna el primer elemento a item.value
    idItemToLoadFirstTime.value = listItemsLocal.value[0]?.id // Carga el ID del primer elemento
  }

  if (props.isCreationDialog && item.value.file) {
    const url = URL.createObjectURL(item.value.file)

    // Abre el PDF en una nueva ventana
    const newWindow = window.open(url, '_blank')
    if (!newWindow) {
      alert('Por favor, habilita las ventanas emergentes para este sitio.')
    }

    // Libera el objeto URL después de usarlo
    newWindow.onload = () => {
      URL.revokeObjectURL(url)
    }

    return
  }

  if (item.value && item.value.file) {
    // Abre el archivo en una nueva ventana
    const newWindow = window.open(item.value.file, '_blank')
    if (!newWindow) {
      alert('Por favor, habilita las ventanas emergentes para este sitio.')
    }
  }
}

/*
function downloadFile() {
  if (props.isCreationDialog) {
    const reader = new FileReader()
    reader.readAsDataURL(item.value.file)
    reader.onload = (e) => {
      const tempLink = document.createElement('a')
      tempLink.style.display = 'none'
      tempLink.href = e.target.result as string
      tempLink.download = item.value.filename
      document.body.appendChild(tempLink)
      tempLink.click()
      document.body.removeChild(tempLink)
    }

    return
  }

  if (item.value) {
    const link = document.createElement('a')
    link.href = item.value.file
    link.setAttribute('download', `${item.value.filename}`)
    link.setAttribute('target', '_blank')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
} */

watch(() => props.selectedInvoiceObj, () => {
  invoice.value = props.selectedInvoiceObj
})

watch(() => props.listItems, async (newValue) => {
  if (newValue) {
    listItemsLocal.value = [...newValue]
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

watch(PayloadOnChangePage, (newValue) => {
  Payload.value.page = newValue?.page ? newValue?.page : 0
  Payload.value.pageSize = newValue?.rows ? newValue.rows : 50
  getList()
})

// Función que actualiza la propiedad de un campo específico
// Función que actualiza la propiedad de un campo específico
function updateFieldProperty(fields: Container[], fieldName: string, property: string, value: any) {
  fields.forEach((field) => {
    if (field.childs) {
      field.childs.forEach((child: any) => { // Especificar el tipo de child
        if (child.field === fieldName) {
          child[property] = value // Actualiza la propiedad deseada
        }
      })
    }
  })
}

watch(() => idItem.value, (newValue) => {
  if (newValue === '') {
    updateFieldProperty(Fields, 'filename', 'disabled', false)
    updateFieldProperty(Fields, 'remark', 'disabled', false)

    updateFieldProperty(Fields, 'type', 'disabled', true)
  }
  else {
    updateFieldProperty(Fields, 'filename', 'disabled', true)
    updateFieldProperty(Fields, 'remark', 'disabled', true)
  }
})

onMounted(async () => {
  await getResourceTypeList()
  await getInvoiceSupportAttachment()
  if (props.selectedInvoice) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: props.selectedInvoice,
      logicalOperation: 'AND'
    }]
  }
  if (!props.isCreationDialog) {
    await getList()

    if (listItemsLocal.value?.length === 0) {
      resourceTypeSelected.value = resourceTypeList.value.find((type: any) => type.code === 'INV')
    }
  }
  else {
    if (listItemsLocal.value?.length > 0) {
      idItemToLoadFirstTime.value = listItemsLocal.value[0]?.id
    }

    if (!route.query.type || (route.query.type && route.query.type !== OBJ_ENUM_INVOICE.INCOME)) {
      resourceTypeSelected.value = resourceTypeList.value.find((type: any) => type.code === 'INV')
      item.value.resourceType = resourceTypeList.value.find((type: any) => type.code === 'INV')
    }
    // item.value.resourceType = `${OBJ_ENUM_INVOICE_TYPE_CODE[route.query.type]}-${OBJ_ENUM_INVOICE[route.query.type]}`
  }
  formReload.value += 1
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header"
    class="h-fit w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit"
    :style="{ width: '80%' }"
    :block-scroll="true" @hide="closeDialog"
  >
    <template #header>
      <div class="inline-flex align-items-center justify-content-center gap-2">
        <span class="font-bold white-space-nowrap">{{ header }}</span>
        <!-- <strong class="mx-2">-</strong>
        <strong class="mr-1">Invoice Id:</strong>
        <strong>{{ selectedInvoiceObj.invoiceId }}</strong> -->
      </div>
    </template>
    <template #default>
      <div class="grid p-fluid formgrid">
        <div class="col-12 order-1 md:order-0 md:col-8 pt-5">
          <DynamicTable
            :data="isCreationDialog ? listItemsLocal : ListItems" :columns="Columns" :options="options"
            :pagination="Pagination" @update:clicked-item="getItemById($event)"
            @open-edit-dialog="getItemById($event)" @on-confirm-create="clearForm"
            @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
            @on-list-item="ResetListItems" @on-sort-field="OnSortField"
          >
            <template
              v-if="isCreationDialog"
              #pagination-total="props"
            >
              <span class="font-bold font">
                {{ listItemsLocal?.length }}
              </span>
            </template>
          </DynamicTable>
        </div>
        <div class="col-12 order-2 md:order-0 md:col-4 pt-5">
          <div class="mx-4 mb-4">
            <div class="font-bold text-lg px-4 bg-primary custom-card-header">
              {{ idItem ? "Edit" : "Add" }}
            </div>
            <div class="card">
              <EditFormV2
                :key="formReload"
                :fields="Fields"
                :item="item"
                :show-actions="true"
                :loading-save="loadingSaveAll"
                @on-confirm-create="clearForm"
                @submit-form="requireConfirmationToSave"
                @cancel="clearForm"
                @delete="requireConfirmationToDelete($event)"
                @submit="requireConfirmationToSave($event)"
              >
                <template #field-resourceType="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="resourceTypeSelected"
                    :disabled="resourceTypeSelected"
                    :suggestions="resourceTypeList"
                    @change="($event) => {
                      onUpdate('resourceType', $event)
                      typeError = false
                    }"
                    @load="($event) => getResourceTypeList($event)"
                  >
                    <template #option="props">
                      <span>{{ props.item.name }}</span>
                    </template>
                    <template #chip="{ value }">
                      <div>
                        {{ value?.name }}
                      </div>
                    </template>
                  </DebouncedAutoCompleteComponent>
                  <Skeleton v-else height="2rem" class="mb-2" />
                  <span v-if="typeError" class="error-message p-error text-xs">The Attachment type field is
                    required</span>
                </template>

                <template #field-type="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="fullName"
                    item-value="id"
                    :model="data.type"
                    :disabled=" idItem !== '' || !isCreationDialog ? !ListItems.some((item: any) => item.type?.attachInvDefault) : isCreationDialog ? !listItemsLocal.some((item: any) => item.type?.attachInvDefault) : false"

                    :suggestions="attachmentTypeList" @change="($event) => {
                      onUpdate('type', $event)
                      typeError = false
                    }"
                    @load="($event) => getAttachmentTypeList($event)"
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
                  <Skeleton v-else height="2rem" class="mb-2" />
                  <span v-if="typeError" class="error-message p-error text-xs">The Attachment type field is
                    required</span>
                </template>
                async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
                const file = event.files[0]
                objFile.value = file
                listFields[fieldKey] = file
                }

                <template #field-file="{ item: data, onUpdate }">
                  <InputGroup>
                    <InputText
                      v-if="!loadingSaveAll"
                      v-model="data.filename"
                      style="border-top-right-radius: 0; border-bottom-right-radius: 0;"
                      placeholder="Upload File"
                      disabled
                    />
                    <Skeleton v-else height="2rem" width="100%" class="mb-2" style="border-radius: 4px;" />
                    <FileUpload
                      v-if="!loadingSaveAll"
                      mode="basic"
                      :max-file-size="100000000"
                      :disabled="idItem !== '' || idItem === null"
                      :multiple="false"
                      auto
                      accept="application/pdf"
                      custom-upload
                      style="border-top-left-radius: 0; border-bottom-left-radius: 0;"
                      @uploader="($event: any) => {
                        customBase64Uploader($event, Fields, 'file');
                        onUpdate('file', $event)
                        if ($event && $event.files.length > 0) {
                          onUpdate('filename', $event?.files[0]?.name)
                          onUpdate('fileSize', formatSize($event?.files[0]?.size))
                        }
                        else {
                          onUpdate('fileName', '')
                        }
                      }"
                    />
                  </InputGroup>

                  <FileUpload
                    v-if="false" :max-file-size="100000000" :disabled="idItem !== '' || idItem === null" :multiple="false" auto custom-upload accept="application/pdf"
                    @uploader="($event: any) => {
                      customBase64Uploader($event, fieldsV2, 'path');
                      onUpdate('path', $event)
                      if ($event && $event.files.length > 0) {
                        onUpdate('fileName', $event?.files[0]?.name)
                        onUpdate('fileSize', formatSize($event?.files[0]?.size))
                      }
                      else {
                        onUpdate('fileName', '')
                      }
                    }"
                  >
                    <template #header="{ chooseCallback }">
                      <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                        <div class="flex gap-2">
                          <Button id="btn-choose" :disabled="idItem !== '' || idItem === null" class="p-2" icon="pi pi-plus" text @click="chooseCallback()" />
                          <Button
                            :disabled="idItem !== '' || idItem === null"
                            icon="pi pi-times" class="ml-2" severity="danger" text @click="() => {
                              onUpdate('path', null);
                              onUpdate('fileName', '');

                            }"
                          />
                        </div>
                      </div>
                    </template>
                    <template #content="{ files }">
                      <ul v-if="files[0] || (data.path && data.path?.files.length > 0)" class="list-none p-0 m-0">
                        <li class="p-3 surface-border flex align-items-start sm:align-items-center">
                          <div class="flex flex-column">
                            <span class="text-900 font-semibold text-xl mb-2">{{ data.path?.files[0].name }}</span>
                            <span class="text-900 font-medium">
                              <Badge severity="warning">
                                {{ formatSize(data.path?.files[0].size) }}
                              </Badge>
                            </span>
                          </div>
                        </li>
                      </ul>
                    </template>
                  </FileUpload>
                </template>

                <template #field-file1="{ onUpdate, item: data }">
                  <FileUpload
                    :disabled="idItem !== ''"
                    accept="application/pdf" :max-file-size="300 * 1024 * 1024" :multiple="false" auto
                    custom-upload @uploader="(event: any) => {
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
                  <IfCan
                    :perms="idItem ? ['INVOICE-MANAGEMENT:ATTACHMENT-EDIT'] : ['INVOICE-MANAGEMENT:ATTACHMENT-CREATE']"
                  >
                    <Button
                      v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
                      :loading="loadingSaveAll"
                      :disabled="!props.item?.fieldValues?.file || idItem !== '' || (!isCreationDialog && selectedInvoiceObj?.status?.id === InvoiceStatus.RECONCILED)"
                      @click="props.item.submitForm($event)"
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
                      v-tooltip.top="'View File'" class="w-3rem mx-2 sticky" icon="pi pi-eye" :disabled="listItemsLocal.length === 0"
                      @click="downloadFile"
                    />
                  </IfCan>

                  <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-SHOW-HISTORY']">
                    <Button
                      v-if="selectedInvoiceObj.invoiceType === InvoiceType.INCOME || route.query.type === InvoiceType.INCOME"
                      v-tooltip.top="'Show History'" class="w-3rem mx-2 sticky" icon="pi pi-book" :disabled="!idItem"
                      @click="showHistory"
                    />
                  </IfCan>

                  <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-DELETE']">
                    <Button
                      v-tooltip.top="'Delete'" outlined severity="danger" class="w-3rem mx-1" icon="pi pi-trash"
                      :disabled="!idItem || (!isCreationDialog && selectedInvoiceObj?.status?.id === InvoiceStatus.RECONCILED)"
                      @click="requireConfirmationToDelete"
                    />
                  </IfCan>
                  <!-- <Button
                    v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {

                      clearForm()
                      closeDialog()
                    }"
                  /> -->
                </template>
              </EditFormV2>
            </div>
          </div>
        </div>
      </div>
    </template>
  </Dialog>

  <div v-if="attachmentHistoryDialogOpen">
    <AttachmentHistoryPartial
      :selected-attachment="selectedAttachment"
      :close-dialog="() => { attachmentHistoryDialogOpen = false; selectedAttachment = '' }"
      header="Attachment Status History" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="selectedInvoice"
      :selected-invoice-obj="item" :is-creation-dialog="false"
    />
  </div>
</template>
