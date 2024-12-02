<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { v4 } from 'uuid'
import AttachmentHistoryDialog from './AttachmentHistoryDialog.vue'
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
  },
  disableDeleteBtn: {
    type: Boolean,
    required: false
  },
  documentOptionHasBeenUsed: {
    type: Boolean,
    required: false,
    default: false
  }
})

const { data: userData } = useAuth()

const invoice = ref<any>(props.selectedInvoiceObj)
const defaultAttachmentType = ref<any>(null)
const disableDeleteBtn = ref(props.disableDeleteBtn)
const documentOptionHasBeenUsed = ref(props.documentOptionHasBeenUsed)

const route = useRoute()
const pathFileLocal = ref('')

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

const resourceTypeList = ref<any[]>([])
const resourceTypeSelected = ref<any>(null)

const confResourceTypeApi = reactive({
  moduleApi: 'payment',
  uriApi: 'resource-type',
})

const idItem = ref('')
const item = ref<GenericObject>({
  id: '',
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.invoiceId,
  employee: userData?.value?.user?.name,
  employeeId: userData?.value?.user?.userId,
  resourceType: null, // `${invoice.value.invoiceType?.name ? `${invoice.value.invoiceType?.name || ''}-${invoice.value.invoiceType?.name || ''}` : `${OBJ_ENUM_INVOICE_TYPE_CODE[invoice.value.invoiceType] || ''}-${OBJ_ENUM_INVOICE[invoice.value.invoiceType] || ''}`}`
})

const itemTemp = ref<GenericObject>({
  id: '',
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.invoiceId,
  employee: userData?.value?.user?.name,
  employeeId: userData?.value?.user?.userId,
  resourceType: null // `${invoice.value.invoiceType?.name ? `${invoice.value.invoiceType?.name || ''}-${invoice.value.invoiceType?.name || ''}` : `${OBJ_ENUM_INVOICE_TYPE_CODE[invoice.value.invoiceType] || ''}-${OBJ_ENUM_INVOICE[invoice.value.invoiceType] || ''}`}`
})
const toast = useToast()

const validationSchema = z.union([
  z.string().trim().min(1, 'File is required'),
  z.object({
    name: z.string().min(1),
    size: z.number().positive(),
  }),
])

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
    validation: validateFiles(),
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
  },

]

const attachmentHistoryDialogOpen = ref<boolean>(false)

const selectedAttachment = ref<string>('')

const Columns: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '70px' },
  { field: 'type', header: 'Type', type: 'select', width: '150px' },
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
  // if (props?.listItems?.length > 0) {
  //   idItemToLoadFirstTime.value = props?.listItems[0]?.id
  // }
  // await getList()
  formReload.value += 1
}

async function getList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    loadingSearch.value = true
    ListItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    // Pagination.value.page = page
    // Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    // Pagination.value.totalPages = totalPages

    const existingIds = new Set(ListItems.value.map(item => item.id))

    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push(
          {
            ...iterator,
            loadingEdit: false,
            loadingDelete: false,
            type: {
              ...iterator?.type,
              name: `${iterator?.type?.code}-${iterator?.type?.name}`
            }
          }
        )
        existingIds.add(iterator.id)
      }
      // ListItems.value = [...ListItems.value, {
      //   ...iterator,
      //   loadingEdit: false,
      //   loadingDelete: false,
      //   type: {
      //     ...iterator?.type,
      //     name: `${iterator?.type?.code}-${iterator?.type?.name}`
      //   }
      // }]
    }

    ListItems.value = [...ListItems.value, ...newListItems]

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
  await getList()
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
      resourceTypeList.value = [...resourceTypeList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading resource type list:', error)
  }
}

// function searchAndFilter() {
//   if (filterToSearch.value.criteria && filterToSearch.value.search) {
//     Payload.value.filter = [{
//       key: filterToSearch.value.criteria,
//       operator: 'LIKE',
//       value: filterToSearch.value.search,
//       logicalOperation: 'AND',
//       type: 'filterSearch',
//     }]
//   }
//   getList()
// }

async function createItem(item: { [key: string]: any }) {
  if (!item) { return }
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
  if (!item) { return }
  if (!idItem.value) { return }
  if (loadingSaveAll.value) { return }
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file

  payload.file = file

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
      await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)

      await getList()
    }
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
  if (loadingSaveAll.value) { return }
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
        await clearForm()

        if (listItemsLocal.value?.length > 0) {
          await getItemById(listItemsLocal.value[0].id)
        }

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
    await getList()
  }
}
// function disabledBtnDeleteAttachment() {
//   return (ListItems.value.length <= 1) || (disableDeleteBtn.value === false ? (!idItem.value || (!props.isCreationDialog && props.selectedInvoiceObj?.status?.id === InvoiceStatus.RECONCILED)) : true)
// }

function disabledBtnDeleteAttachment() {
  // Verifica si la lista de elementos tiene 1 o menos items
  if (ListItems.value.length <= 1) {
    return true
  }
  // Si disableDeleteBtn está en false, evalúa las condiciones adicionales
  if (disableDeleteBtn.value === false) {
    // Verifica si idItem es nulo o undefined
    if (!idItem.value) {
      return true
    }
    // Si no es un diálogo de creación y el estado de la factura seleccionada es 'RECONCILED', retorna true
    if (!props.isCreationDialog && props.selectedInvoiceObj?.status?.id === InvoiceStatus.RECONCILED) {
      return true
    }
    // Si ninguna condición se cumple, retorna false
    return false
  }
  // Si disableDeleteBtn está en true, retorna true
  return true
}

function disabledBtnDelete(): boolean {
  let listTem = []
  if (props.isCreationDialog) {
    listTem = JSON.parse(JSON.stringify(listItemsLocal.value))
  }
  else {
    listTem = JSON.parse(JSON.stringify(ListItems.value))
  }

  const cantItemAttachInvDefault = listTem.filter((attachment: any) => attachment?.type?.attachInvDefault)?.length
  const idObjSelected = listTem?.find((attachment: any) => attachment?.type?.attachInvDefault)?.id

  if (cantItemAttachInvDefault === 1 && (idItem.value === idObjSelected)) {
    return true
  }
  else if (listTem && listTem.length > 1) {
    return false
  }
  else {
    return true
  }
  // if (props.selectedInvoiceObj && props.selectedInvoiceObj?.invoiceStatus?.processStatus) {

  // }
  // else {
  //   return true
  // }
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
  // if (props.isCreationDialog) {
  //   const reader = new FileReader()
  //   reader.readAsDataURL(item.value.file)
  //   reader.onload = (e) => {
  //     const tempLink = document.createElement('a')
  //     tempLink.style.display = 'none'
  //     tempLink.href = e.target.result as string
  //     tempLink.download = item.value.filename
  //     document.body.appendChild(tempLink)
  //     tempLink.click()
  //     document.body.removeChild(tempLink)
  //   }

  //   return
  // }

  // if (item.value) {
  //   const link = document.createElement('a')
  //   link.href = item.value.file
  //   link.setAttribute('download', `${item.value.filename}`)
  //   link.setAttribute('target', '_blank')
  //   document.body.appendChild(link)
  //   link.click()
  //   document.body.removeChild(link)
  // }
}

function openFileInNewWindow() {
  if (item.value && item.value.file) {
    if (typeof item.value.file === 'string' && item.value.file.length > 0) {
      window.open(item.value.file, '_blank')
    }
    else if (typeof item.value.file === 'object') {
      const fileData = item.value.file
      const fileName = item.value.filename || 'downloaded_file'

      const blob = new Blob([fileData], { type: 'application/pdf' })
      const url = URL.createObjectURL(blob)

      window.open(url, '_blank')
    }
    else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'File not found', life: 3000 })
    }
  }
}

const haveAttachmentWithAttachmentTypeInv = computed(() => {
  return ListItems.value?.some((attachment: any) => attachment?.type?.code === 'INV')
})

function isFieldDisabled() {
  if (!props.isCreationDialog) {
    // !ListItems.value.some(item => item.type?.attachInvDefault)
    if (item.value && item.value.id) {
      return true
    }
    else {
      return !ListItems.value.some(item => item.type?.attachInvDefault)
    }
  }
  else if (props.isCreationDialog) {
    if (item.value && item.value.id) {
      return true
    }
    else {
      return !listItemsLocal.value.some(item => item.type?.attachInvDefault)
    }
  }
  return false
}

function disabledBtnSave(propsValue: any): boolean {
  if (item.value && item.value.id) {
    return true
  }
  else if (propsValue.item.fieldValues.file) {
    return false
  }
  else {
    return true
  }
}
function disabledFields(): boolean {
  if (item.value && item.value.id) {
    return true
  }
  else {
    return false
  }
}

function disabledBtnCreate(): boolean {
  return false
}

async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
  const file = event.files[0]
  listFields[fieldKey] = file
}

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

watch(PayloadOnChangePage, async (newValue) => {
  Payload.value.page = newValue?.page ? newValue?.page : 0
  Payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  await getList()
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
      await getItemById(idItemToLoadFirstTime.value)
    }
    if (!route.query.type || (route.query.type && route.query.type !== OBJ_ENUM_INVOICE.INCOME)) {
      resourceTypeSelected.value = resourceTypeList.value.find((type: any) => type.code === 'INV')
    }
    // item.value.resourceType = `${OBJ_ENUM_INVOICE_TYPE_CODE[route.query.type]}-${OBJ_ENUM_INVOICE[route.query.type]}`
  }
  formReload.value += 1
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    :header="header"
    class="h-fit w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit"
    :block-scroll="true"
    :style="{ width: '80%' }"
    @hide="closeDialog"
  >
    <template #header>
      <div class="inline-flex align-items-center justify-content-center gap-2">
        <span class="font-bold white-space-nowrap">{{ header }}</span>
        <strong class="mx-2">-</strong>
        <strong class="mr-1">Invoice:</strong>
        <strong>{{ selectedInvoiceObj.invoiceId }}</strong>
      </div>
    </template>
    <template #default>
      <div class="grid p-fluid formgrid">
        <div class="col-12 order-1 md:order-0 md:col-8 pt-5">
          <DynamicTable
            :data="isCreationDialog ? listItemsLocal as any : ListItems"
            :columns="Columns"
            :options="options"
            :pagination="Pagination"
            :is-custom-sorting="!isCreationDialog"
            @update:clicked-item="getItemById($event)"
            @open-edit-dialog="getItemById($event)"
            @on-confirm-create="clearForm"
            @on-change-filter="ParseDataTableFilter"
            @on-list-item="ResetListItems"
            @on-sort-field="OnSortField"
          >
            <template v-if="isCreationDialog" #pagination-total="props">
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
                    }" @load="($event) => getResourceTypeList($event)"
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
                <!-- :disabled="(documentOptionHasBeenUsed && invoice?.manageInvoiceStatus?.code === 'PROC') ? (!haveAttachmentWithAttachmentTypeInv) : (!isCreationDialog && ListItems.length > 0)" -->
                <template #field-type="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="fullName"
                    item-value="id"
                    :model="data.type"
                    :disabled="isFieldDisabled()"
                    :suggestions="attachmentTypeList"
                    @change="($event) => {
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
                  <Skeleton v-else height="2rem" class="mb-2" />
                  <span v-if="typeError" class="error-message p-error text-xs">The Attachment type field is
                    required</span>
                </template>

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

                <!-- <template #field-file="{ onUpdate, item: data }">
                  <FileUpload
                    v-if="false"
                    accept="application/pdf"
                    :max-file-size="300 * 1024 * 1024"
                    :multiple="false"
                    auto
                    custom-upload
                    @uploader="(event: any) => {
                      const file = event.files[0]
                      onUpdate('file', file)
                      onUpdate('filename', data.file.name || data.file.split('/')[data.file.split('/')?.length - 1])
                    }"
                  >
                    <template #header="{ chooseCallback }">
                      <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                        <div class="flex gap-2">
                          <Button
                            id="btn-choose" class="p-2" icon="pi pi-plus"
                            :disabled="disabledFields(data)" text @click="chooseCallback()"
                          />
                          <Button
                            icon="pi pi-times" class="ml-2" severity="danger"
                            :disabled="!data.file || !isCreationDialog && ListItems.length > 0" text
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
                </template> -->
                <template #field-filename="{ item: data }">
                  <InputText v-model="data.filename" field="filename" show-clear :disabled="true" />
                </template>

                <template #field-remark="{ item: data }">
                  <Textarea
                    v-model="data.remark"
                    field="remark"
                    :disabled="disabledFields()"
                    rows="5"
                  />
                </template>
                <template #form-footer="props">
                  <IfCan
                    :perms="idItem ? ['INVOICE-MANAGEMENT:ATTACHMENT-EDIT'] : ['INVOICE-MANAGEMENT:ATTACHMENT-CREATE']"
                  >
                    <Button
                      v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
                      :loading="loadingSaveAll"
                      :disabled="disabledBtnSave(props)"
                      @click="props.item.submitForm($event)"
                    />
                  </IfCan>

                  <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-CREATE']">
                    <!-- :disabled="(documentOptionHasBeenUsed && invoice?.manageInvoiceStatus?.code === 'PROC') ? false : (!isCreationDialog && ListItems.length > 0)"  -->
                    <Button
                      v-tooltip.top="'Add'" class="w-3rem mx-2 sticky" icon="pi pi-plus"
                      :disabled="disabledBtnCreate()"
                      @click="() => {
                        idItem = ''
                        item = itemTemp
                        clearForm()
                      }"
                    />
                  </IfCan>

                  <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-VIEW-FILE']">
                    <Button
                      v-tooltip.top="'View File'" class="w-3rem mx-2 sticky" icon="pi pi-eye" :disabled="!idItem"
                      @click="openFileInNewWindow"
                    />
                  </IfCan>

                  <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-SHOW-HISTORY']">
                    <Button
                      v-if="selectedInvoiceObj.invoiceType === InvoiceType.INCOME || route.query.type === InvoiceType.INCOME"
                      v-tooltip.top="'Show History'" class="w-3rem mx-2 sticky" icon="pi pi-book"
                      :disabled="!idItem || !isCreationDialog" @click="showHistory"
                    />
                  </IfCan>

                  <IfCan :perms="['INVOICE-MANAGEMENT:ATTACHMENT-DELETE']">
                    <Button
                      v-tooltip.top="'Delete'" outlined severity="danger" class="w-3rem mx-1" icon="pi pi-trash"
                      :disabled="disabledBtnDelete()"
                      @click="requireConfirmationToDelete"
                    />
                  </IfCan>
                  <Button
                    v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {

                      clearForm()
                      closeDialog()
                    }"
                  />
                </template>
              </EditFormV2>
            </div>
          </div>
        </div>
      </div>
    </template>
  </Dialog>

  <div v-if="attachmentHistoryDialogOpen">
    <AttachmentHistoryDialog
      :selected-attachment="selectedAttachment"
      :close-dialog="() => { attachmentHistoryDialogOpen = false; selectedAttachment = '' }"
      header="Attachment Status History" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="selectedInvoice"
      :selected-invoice-obj="item" :is-creation-dialog="false"
    />
  </div>
</template>
