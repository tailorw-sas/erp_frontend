<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { date, z } from 'zod'
import type { IData } from '../table/interfaces/IModelData'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import { getUrlOrIdByFile } from '~/composables/files'
import { applyFiltersAndSort } from '~/pages/payment/utils/helperFilters'

interface ResourceType {
  id: string
  code?: string
  name: string
  description?: string
  status: string
}

interface AttachmentType {
  id: string
  code?: string
  name: string
  description?: string
  status: string
  defaults?: boolean
}

export interface FileObject {
  id: string
  status: string
  resource: string
  resourceType: ResourceType
  attachmentType: AttachmentType
  fileName: string
  fileWeight: number | null
  path: string
  remark: string
}

const externalProps = defineProps({

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
  selectedPayment: {
    type: Object,
    required: true
  },
  isCreateOrEditPayment: {
    type: String,
    required: true
  }
})

const emits = defineEmits(['update:listItems', 'update:closeDialog', 'update:openDialog', 'update:isCreationDialog', 'update:isCreateOrEditPayment'])

const attachmentTypeList = ref<any[]>([])
const resourceTypeList = ref<any[]>([])
const employeeList = ref<any[]>([])
const pathFileLocal = ref('')

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const $primevue = usePrimeVue()
const formReload = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()
const { data: userData } = useAuth()

const openDialogHistory = ref(false)
const historyList = ref<any[]>([])
const clickedItem = ref<any>([])
const loadingDelete = ref(false)
const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')

const idItem = ref('')

const toast = useToast()

const fieldsV2: Array<FieldDefinitionType> = [
  {
    field: 'resource',
    header: 'Resource',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: false,
    hidden: true
  },
  {
    field: 'paymentId',
    header: 'Resource',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true,
  },
  {
    field: 'resourceType',
    header: 'Resource Type',
    dataType: 'number',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: false,
    validation: validateEntityStatus('Resource Type'),
  },

  {
    field: 'attachmentType',
    header: 'Attachment Type',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('Attachment Type'),

  },
  {
    field: 'employee',
    header: 'Employee',
    dataType: 'select',
    class: 'field col-12',
    headerClass: 'mb-1',
    hidden: true,
  },
  {
    field: 'path',
    header: 'Path',
    dataType: 'fileupload',
    class: 'field col-12 required',
    headerClass: 'mb-1',
  },
  {
    field: 'fileName',
    header: 'Filename',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    disabled: true,
    validation: z.string().trim().min(1, 'This is a required field')
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1',

  },

]

const item = ref<GenericObject>({

  paymentId: externalProps.selectedPayment.paymentId || '',
  resource: externalProps.selectedPayment.id || '',
  resourceType: null,
  attachmentType: null,
  employee: null,
  fileName: '',
  fileWeight: '',
  path: '',
  remark: '',
  status: 'ACTIVE'
})

const itemTemp = ref<GenericObject>({
  paymentId: externalProps.selectedPayment.paymentId || '',
  resource: externalProps.selectedPayment.id || '',
  resourceType: null,
  attachmentType: null,
  employee: null,
  fileName: '',
  fileWeight: '',
  path: '',
  remark: '',
  status: 'ACTIVE'
})

const objApis = ref({
  resourceType: { moduleApi: 'payment', uriApi: 'resource-type' },
  attachmentType: { moduleApi: 'payment', uriApi: 'attachment-type' },
  employee: { moduleApi: 'settings', uriApi: 'manage-employee' },
})

const Columns: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '100px' },
  { field: 'paymentId', header: 'Payment Id', type: 'text', width: '100px', sortable: false, showFilter: false },
  // { field: 'resourceType', header: 'Resource Type', type: 'select', width: '200px', objApi: { moduleApi: 'payment', uriApi: 'resource-type' } },
  { field: 'attachmentType', header: 'Type', type: 'select', width: '200px', objApi: { moduleApi: 'payment', uriApi: 'attachment-type' } },
  { field: 'fileName', header: 'Filename', type: 'text', width: '200px' },
  { field: 'remark', header: 'Remark', width: '200px', type: 'text' },
]
const columnsAttachment: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '100px', sortable: false, showFilter: false },
  { field: 'paymentId', header: 'Payment Id', type: 'text', width: '100px', sortable: false, showFilter: false },
  // { field: 'resourceType', header: 'Resource Type', type: 'select', width: '200px', objApi: { moduleApi: 'payment', uriApi: 'resource-type' } },
  { field: 'attachmentType', header: 'Type', type: 'select', width: '200px', objApi: { moduleApi: 'payment', uriApi: 'attachment-type' } },
  { field: 'fileName', header: 'Filename', type: 'text', width: '200px' },
  { field: 'remark', header: 'Remark', width: '200px', type: 'text' },
]

const historyColumns = ref<IColumn[]>([
  { field: 'attachmentId', header: 'Id', type: 'text', width: '50px' },
  { field: 'paymentId', header: 'Payment Id', type: 'text', width: 'auto', sortable: false, showFilter: false },
  { field: 'createdAt', header: 'Date', type: 'date', width: 'auto' },
  { field: 'employee', header: 'Employee', type: 'select', width: 'auto', localItems: [] },
  { field: 'description', header: 'Description', type: 'text', width: '200px' },
  { field: 'attachmentStatus', header: 'Status', type: 'text', width: '120px' },
])

const historyOptions = ref({
  tableName: 'Payment Attachment',
  moduleApi: 'payment',
  uriApi: 'attachment-status-history',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const dialogVisible = ref(externalProps.openDialog)
const options = ref({
  tableName: 'Payment Attachment',
  moduleApi: 'payment',
  uriApi: 'master-payment-attachment',
  loading: false,
  showDelete: false,
  showFilters: true,
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
const PaginationHistory = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const payloadOnChangePage = ref<PageState>()
const payloadOnChangePageHistory = ref<PageState>()
const payloadLocal = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'ASC'
})

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'ASC'
})
const payloadHistory = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'ASC'
})

const historyPagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

const ListItems = ref<any[]>([])
const listItemsLocal = ref<any[]>([...externalProps.listItems])
const listItemsLocalTemp = ref<any[]>([])
const idItemToLoadFirstTime = ref('')

async function ResetListItems() {
  payload.value.page = 0
}

function OnSortField(event: any) {
  if (event) {
    if (event.sortField === 'paymentId') {
      event.sortField = 'paymentId.id'
    }
    if (event.sortField === 'attachmentType') {
      event.sortField = 'attachmentType.name'
    }
    if (event.sortField === 'resourceType') {
      event.sortField = 'resourceType.name'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    ParseDataTableFilter(event.filter)
  }
}

function onSortFieldHistory(event: any) {
  if (event) {
    if (event.sortField === 'paymentId') {
      event.sortField = 'paymentId.id'
    }
    if (event.sortField === 'employee') {
      event.sortField = 'employee.firstName'
    }
    // if (event.sortField === 'attachmentStatus') {
    //   event.sortField = 'attachmentStatus.name'
    // }
    payloadHistory.value.sortBy = event.sortField
    payloadHistory.value.sortType = event.sortOrder
    historyParseDataTableFilter(event.filter)
  }
}

async function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  const filter: FilterCriteria[] = [
    {
      key: 'defaults',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: true,
    },
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
    },
  ]
  await getResourceTypeList(objApis.value.resourceType.moduleApi, objApis.value.resourceType.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filter)
  item.value.resourceType = resourceTypeList.value.length > 0 ? resourceTypeList.value[0] : null

  formReload.value++
}

async function getList() {
  try {
    idItem.value = ''
    options.value.loading = true
    idItemToLoadFirstTime.value = ''
    ListItems.value = []
    let attachmentList: any[] = []

    const objFilter = payload.value.filter.find(item => item.key === 'resource.id')

    if (objFilter) {
      objFilter.value = externalProps.selectedPayment?.id || ''
    }
    else {
      payload.value.filter.push({
        key: 'resource.id',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: externalProps.selectedPayment?.id || ''
      })
    }
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      attachmentList = [...attachmentList, { ...iterator, paymentId: iterator.resource?.paymentId || '' }]
    }

    ListItems.value = [...attachmentList]

    // for (const iterator of dataList) {
    //   ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false, roomRateId: iterator?.roomRate?.roomRateId }]
    // }

    if (ListItems.value.length > 0) {
      idItem.value = ListItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function historyGetList() {
  if (historyOptions.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    historyOptions.value.loading = true
    historyList.value = []
    const newListItems = []
    const objFilter = payloadHistory.value.filter.find(item => item.key === 'payment.id')

    if (objFilter) {
      objFilter.value = externalProps.selectedPayment.id || ''
    }
    else {
      payloadHistory.value.filter.push({
        key: 'payment.id',
        operator: 'EQUALS',
        value: externalProps.selectedPayment.id || '',
        logicalOperation: 'AND'
      })
    }

    const response = await GenericService.search(historyOptions.value.moduleApi, historyOptions.value.uriApi, payloadHistory.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    historyPagination.value.page = page
    historyPagination.value.limit = size
    historyPagination.value.totalElements = totalElements
    historyPagination.value.totalPages = totalPages

    const existingIds = new Set(historyList.value.map(item => item.id))

    for (const iterator of dataList) {
      iterator.paymentId = externalProps.selectedPayment.paymentId
      iterator.attachmentStatus = iterator.status

      if (Object.prototype.hasOwnProperty.call(iterator, 'employee')) {
        if (iterator.employee.firstName && iterator.employee.lastName) {
          iterator.employee = { id: iterator.employee?.id, name: `${iterator.employee?.firstName} ${iterator.employee?.lastName}` }
        }
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    historyList.value = [...historyList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    historyOptions.value.loading = false
  }
}

async function loadHistoryList() {
  await historyGetList()
  openDialogHistory.value = true
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.search = ''
  getList()
}

// paymentId

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: 'resource.paymentId',
      operator: 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'OR',
      type: 'filterSearch',
    }, {
      key: 'fileName',
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
async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  payload.value.filter = [...parseFilter || []]
  getList()
}

async function historyParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, historyColumns.value)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      if (parseFilter[i]?.key === 'employee') {
        parseFilter[i].key = 'employee.id'
      }
    }
  }
  payloadHistory.value.filter = [...parseFilter || []]
  historyGetList()
}

async function parseDataTableFilterLocal(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  payloadLocal.value.filter = [...parseFilter || []]

  listItemsLocal.value = JSON.parse(JSON.stringify(listItemsLocalTemp.value))
  listItemsLocal.value = [...applyFiltersAndSort(payloadLocal.value, listItemsLocal.value)]
}

function onSortFieldLocal(event: any) {
  if (event) {
    if (event.sortField === 'paymentId') {
      event.sortField = 'paymentId'
    }
    if (event.sortField === 'attachmentType') {
      event.sortField = 'attachmentType.name'
    }
    if (event.sortField === 'resourceType') {
      event.sortField = 'resourceType.name'
    }
    payloadLocal.value.sortBy = event.sortField
    payloadLocal.value.sortType = event.sortOrder
    parseDataTableFilterLocal(event.filter)
  }
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
  defaults?: boolean
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  defaults?: boolean
}

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.name}`,
    status: data.status,
    defaults: data?.defaults
  }
}
async function getResourceTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  resourceTypeList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
}

async function getAttachmentTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  attachmentTypeList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction)
}

interface DataListItemEmployee {
  id: string
  firstName: string
  lastName: string
  email: string
}

interface ListItemEmployee {
  id: string
  name: string
  status: boolean | string
}

function mapFunctionEmployee(data: DataListItemEmployee): ListItemEmployee {
  return {
    id: data.id,
    name: `${data.firstName} ${data.lastName}`,
    status: 'Active'
  }
}

async function getEmployeeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  employeeList.value = await getDataList<DataListItemEmployee, ListItemEmployee>(moduleApi, uriApi, filter, queryObj, mapFunctionEmployee)
  const columnEmployee = historyColumns.value.find(item => item.field === 'employee')
  if (columnEmployee) {
    columnEmployee.localItems = [...JSON.parse(JSON.stringify(employeeList.value))]
  }
}

const haveError = ref(false)

async function createItemLocal(item: any) {
  if (item) {
    const hasDefaultAttachmentType = listItemsLocal.value.some(item => item.attachmentType.defaults)

    if (hasDefaultAttachmentType && item.attachmentType.defaults) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Only a payment support by payment is allowed', life: 3000 })
      haveError.value = true
    }
    else {
      item.id = listItemsLocal.value.length + 1
      const payload: { [key: string]: any } = { ...item }
      payload.employee = userData?.value?.user?.userId
      if (typeof payload.path === 'object' && payload.path !== null && payload.path?.files && payload.path?.files.length > 0) {
        const file = payload.path.files[0]
        if (file) {
          const objFile = await getUrlOrIdByFile(file)
          payload.path = objFile && typeof objFile === 'object' ? objFile.url : objFile.id
        }
        else {
          payload.path = ''
        }
      }
      else {
        payload.path = ''
      }
      listItemsLocal.value = [...listItemsLocal.value, payload]
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
      haveError.value = false
    }
  }
  listItemsLocalTemp.value = JSON.parse(JSON.stringify(listItemsLocal.value))
}

async function updateItemLocal(item: any) {
  if (item) {
    const hasDefaultAttachmentType = listItemsLocal.value.some(itemLocal => itemLocal.id !== item.id && itemLocal.attachmentType.defaults)

    if (hasDefaultAttachmentType && item.attachmentType.defaults) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Only a payment support by payment is allowed', life: 3000 })
    }
    else {
      const index = listItemsLocal.value.findIndex((x: any) => x.id === item.id)
      const payload: { [key: string]: any } = { ...item }
      payload.employee = userData?.value?.user?.userId
      if (typeof payload.path === 'object' && payload.path !== null && payload.path?.files && payload.path?.files.length > 0) {
        const file = payload.path.files[0]
        if (file) {
          const objFile = await getUrlOrIdByFile(file)
          payload.path = objFile && typeof objFile === 'object' ? objFile.url : objFile.id
        }
        else {
          payload.path = ''
        }
      }
      else if (pathFileLocal.value !== null && pathFileLocal.value !== '') {
        payload.path = pathFileLocal.value
      }
      else {
        payload.path = ''
      }
      listItemsLocal.value[index] = payload
      listItemsLocalTemp.value[index] = JSON.parse(JSON.stringify(payload))
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
  }
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    if (typeof payload.path === 'object' && payload.path !== null && payload.path?.files && payload.path?.files.length > 0) {
      const file = payload.path.files[0]
      if (file) {
        const objFile = await getUrlOrIdByFile(file)
        payload.path = objFile && typeof objFile === 'object' ? objFile.url : objFile.id
      }
      else {
        payload.path = ''
      }
    }
    else {
      payload.path = ''
    }
    payload.attachmentType = item.attachmentType?.id
    payload.resourceType = item.resourceType?.id
    payload.status = 'ACTIVE'
    payload.employee = userData?.value?.user?.userId || ''
    payload.resource = externalProps.selectedPayment.id
    delete payload.event
    delete payload.paymentId
    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.employee = userData?.value?.user?.userId
  payload.attachmentType = item.attachmentType?.id
  payload.resourceType = item.resourceType?.id
  payload.resource = externalProps.selectedPayment.id
  if (typeof payload.path === 'object' && payload.path !== null && payload.path?.files && payload.path?.files.length > 0) {
    const file = payload.path.files[0]
    if (file) {
      const objFile = await getUrlOrIdByFile(file)
      payload.path = objFile && typeof objFile === 'object' ? objFile.url : objFile.id
    }
    else {
      payload.path = ''
    }
  }
  else if (pathFileLocal.value !== null && pathFileLocal.value !== '') {
    payload.path = pathFileLocal.value
  }
  else {
    payload.path = ''
  }
  delete payload.event
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    const employeeId = userData?.value?.user?.userId || ''
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id, 'employee', employeeId)
    clearForm()
    getList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete detail', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true
  if (idItem.value) {
    try {
      if (externalProps.isCreateOrEditPayment === 'create') {
        item.id = idItem.value
        updateItemLocal(item)
      }
      else {
        await updateItem(item)
        toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
      }
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
      if (externalProps.isCreateOrEditPayment === 'create') {
        try {
          createItemLocal(item)
        }
        catch (error) {
          successOperation = false
          toast.add({ severity: 'error', summary: 'Error', detail: 'Only a payment support by payment is allowed', life: 3000 })
        }
      }
      else {
        await createItem(item)
        toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
      }
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation && !haveError.value) {
    clearForm()
    getList()
  }
}

async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
  const file = event.files[0]
  listFields[fieldKey] = file
}

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

// function requireConfirmationToDelete(event: any) {
//   confirm.require({
//     target: event.currentTarget,
//     group: 'headless',
//     header: 'Save the record',
//     message: 'Do you want to save the change?',
//     acceptClass: 'p-button-danger',
//     rejectLabel: 'Cancel',
//     acceptLabel: 'Accept',
//     accept: () => {
//       deleteItem(idItem.value)
//       toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
//     },
//     reject: () => {
//       // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
//     }
//   })
// }

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    idItemToLoadFirstTime.value = id
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.resource = response.resource
        item.value.resourceType = response.resourceType
        item.value.attachmentType = response.attachmentType
        item.value.fileName = response.fileName
        item.value.file = response.file
        item.value.remark = response.remark
        item.value.invoice = response.invoice
        pathFileLocal.value = response.path
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

async function getItemByIdLocal(idItemLocal: string) {
  if (idItemLocal) {
    const objToEdit = listItemsLocal.value.find(x => x.id === idItemLocal)
    if (objToEdit) {
      idItem.value = idItemLocal
      loadingSaveAll.value = true
      try {
        item.value.id = idItemLocal
        item.value.resource = objToEdit.resource
        item.value.resourceType = objToEdit.resourceType
        item.value.attachmentType = objToEdit.attachmentType
        item.value.fileName = objToEdit.fileName
        // item.value.file = itemLocal.file
        item.value.remark = objToEdit.remark
        // item.value.invoice = itemLocal.invoice
        pathFileLocal.value = objToEdit.path

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

async function clearFormAndReload() {
  clearForm()
  // await loadDefaultsValues()
}

function requireConfirmationToDelete(event: any) {
  if (externalProps.isCreateOrEditPayment === 'create') {
    listItemsLocal.value = listItemsLocal.value.filter((item: any) => item.id !== idItem.value)
    clearFormAndReload()
  }
  else if (!useRuntimeConfig().public.showDeleteConfirm) {
    deleteItem(idItem.value)
  }
  else {
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
      },
      reject: () => {}
    })
  }
}

function openOrDownloadFile(url: string) {
  if (isValidUrl(url)) {
    window.open(url, '_blank')
  }
  else {
    console.error('Invalid URL')
  }
}

async function loadDefaultsValues() {
  const filterForEmployee: FilterCriteria[] = [
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
    },
  ]
  await getEmployeeList(objApis.value.employee.moduleApi, objApis.value.employee.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filterForEmployee)
  const filter: FilterCriteria[] = [
    {
      key: 'defaults',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: true,
    },
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
    },
  ]
  await getAttachmentTypeList(objApis.value.attachmentType.moduleApi, objApis.value.attachmentType.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filter)
  item.value.attachmentType = attachmentTypeList.value.length > 0 ? attachmentTypeList.value[0] : null

  await getResourceTypeList(objApis.value.resourceType.moduleApi, objApis.value.resourceType.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filter)
  item.value.resourceType = resourceTypeList.value.length > 0 ? resourceTypeList.value[0] : null

  formReload.value++
}

function disabledOrEnabledShowHistory() {
  if (externalProps.isCreateOrEditPayment === 'create') {
    return true
  }
  else if (idItem.value) {
    return false
  }
  else {
    return true
  }
}

function disabledOrEnabledResourceType() {
  if (externalProps.isCreateOrEditPayment === 'create') {
    if (listItemsLocal.value.length === 0) {
      return true
    }
    return false
  }
  else if (ListItems.value.length === 0) {
    return true
  }
  else {
    return false
  }
}

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

watch(() => listItemsLocal.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemByIdLocal(listItemsLocal.value[0].id)
  }
})

watch(() => idItem.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else if (externalProps.isCreateOrEditPayment !== 'create') {
    await getItemById(newValue)
  }
  else {
    const itemLocal = listItemsLocal.value.find((item: any) => item.id === newValue)
    if (itemLocal) {
      getItemByIdLocal(itemLocal)
    }
  }
})

watch(() => listItemsLocal.value, async (newValue) => {
  if (newValue) {
    getItemByIdLocal(newValue[0])

    emits('update:listItems', listItemsLocal.value)
  }
}, { deep: true })

onMounted(async () => {
  loadDefaultsValues()
  if (externalProps.isCreateOrEditPayment !== 'create') {
    await getList()
    if (idItem.value) {
      await getItemById(idItem.value)
    }
  }

  // else {
  // }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-screen"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" :style="{ width: '80%' }"
    @hide="closeDialog"
  >
    <template #default>
      <div class="grid p-fluid formgrid">
        <div class="col-12 order-1 md:order-0 md:col-9 pt-5">
          <div v-if="true" class="font-bold mb-3 flex justify-content-end">
            <div class="bg-primary px-3" style="border-radius: 5px; padding-top: 10px; padding-bottom: 10px">
              <strong class="mr-2">Payment:</strong>
              <span>{{ externalProps.selectedPayment.paymentId }}</span>
            </div>
          </div>
          <Accordion v-if="false" :active-index="0" class="mb-2 card p-0">
            <AccordionTab>
              <template #header>
                <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full">
                  <div>Filters</div>
                  <div>
                    <strong class="mx-2">Payment:</strong>
                    <span>{{ externalProps.selectedPayment.paymentId }}</span>
                  </div>
                </div>
              </template>

              <div class="flex gap-4 flex-column lg:flex-row">
                <div class="flex align-items-center gap-2">
                  <label for="email">Payment:</label>
                  <div class="w-full lg:w-auto">
                    <IconField icon-position="left" class="w-full">
                      <InputText v-model="filterToSearch.search" :disabled="externalProps.isCreateOrEditPayment === 'create'" type="text" placeholder="Search" class="w-full" />
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
            v-if="isCreateOrEditPayment === 'create'"
            class="card p-0"
            :data="listItemsLocal"
            :columns="columnsAttachment"
            :options="options"
            :pagination="Pagination"
            @on-change-filter="parseDataTableFilterLocal"
            @update:clicked-item="getItemByIdLocal($event)"
            @on-confirm-create="clearForm"
            @on-change-pagination="payloadOnChangePage = $event"
            @on-list-item="ResetListItems" @on-sort-field="onSortFieldLocal"
          />
          <DynamicTable
            v-else
            class="card p-0"
            :data="ListItems"
            :columns="Columns"
            :options="options"
            :pagination="Pagination"
            @on-change-filter="ParseDataTableFilter"
            @update:clicked-item="getItemById($event)"
            @on-confirm-create="clearForm"
            @on-change-pagination="payloadOnChangePage = $event"
            @on-list-item="ResetListItems" @on-sort-field="OnSortField"
          />
        </div>
        <div class="col-12 order-2 md:order-0 md:col-3 pt-5">
          <div>
            <div class="font-bold text-lg px-4 bg-primary custom-card-header">
              {{ formTitle }}
            </div>
            <div class="card">
              <EditFormV2
                :key="formReload"
                :fields="fieldsV2"
                :item="item"
                :show-actions="true"
                :loading-save="loadingSaveAll"
                @submit-form="requireConfirmationToSave"
                @on-confirm-create="clearForm"
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
                    :model="data.resourceType"
                    :suggestions="resourceTypeList"
                    :disabled="disabledOrEnabledResourceType()"
                    @change="($event) => {
                      onUpdate('resourceType', $event)
                    }" @load="($event) => {
                      const filter: FilterCriteria[] = [{
                        key: 'status',
                        logicalOperation: 'AND',
                        operator: 'EQUALS',
                        value: 'ACTIVE',
                      }]
                      getResourceTypeList(objApis.resourceType.moduleApi, objApis.resourceType.uriApi, $event, filter)
                    }"
                  >
                    <template #option="props">
                      <span>{{ props.item.name }}</span>
                    </template>
                  </DebouncedAutoCompleteComponent>
                </template>

                <template #field-attachmentType="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="data.attachmentType"
                    :suggestions="attachmentTypeList"
                    @change="($event) => {
                      onUpdate('attachmentType', $event)
                    }" @load="($event) => {
                      const filter: FilterCriteria[] = [{
                        key: 'status',
                        logicalOperation: 'AND',
                        operator: 'EQUALS',
                        value: 'ACTIVE',
                      }]
                      getAttachmentTypeList(objApis.attachmentType.moduleApi, objApis.attachmentType.uriApi, $event, filter)

                    }"
                  >
                    <template #option="props">
                      <span>{{ props.item.name }}</span>
                    </template>
                  </DebouncedAutoCompleteComponent>
                </template>

                <template #field-employee="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="data.employee"
                    :suggestions="employeeList"
                    @change="($event) => {
                      onUpdate('employee', $event)
                    }" @load="($event) => getEmployeeList(objApis.employee.moduleApi, objApis.employee.uriApi, $event)"
                  >
                    <template #option="props">
                      <span>{{ props.item.name }}</span>
                    </template>
                  </DebouncedAutoCompleteComponent>
                </template>

                <template #field-path="{ item: data, onUpdate }">
                  <FileUpload
                    v-if="!loadingSaveAll" :max-file-size="1000000" :disabled="idItem !== '' || idItem === null" :multiple="false" auto custom-upload accept="application/pdf"
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

                <template #form-footer="props">
                  <IfCan :perms="idItem ? ['PAYMENT-MANAGEMENT:EDIT-ATTACHMENT'] : ['PAYMENT-MANAGEMENT:CREATE-ATTACHMENT']">
                    <Button
                      v-tooltip.top="'Save'"
                      :disabled="idItem !== '' && idItem !== null" class="w-3rem sticky" icon="pi pi-save"
                      @click="props.item.submitForm($event)"
                    />
                  </IfCan>
                  <IfCan :perms="['PAYMENT-MANAGEMENT:VIEW-FILE-ATTACHMENT']">
                    <Button v-tooltip.top="'View File'" :disabled="!idItem" class="w-3rem ml-1 sticky" icon="pi pi-eye" @click="openOrDownloadFile(pathFileLocal)" />
                  </IfCan>
                  <IfCan :perms="['PAYMENT-MANAGEMENT:SHOW-HISTORY-ATTACHMENT']">
                    <Button
                      v-if="true"
                      v-tooltip.top="'Show History'" :disabled="disabledOrEnabledShowHistory()" class="w-3rem ml-1 sticky" icon="pi pi-book"
                      @click="loadHistoryList"
                    />
                  </IfCan>
                  <IfCan :perms="['PAYMENT-MANAGEMENT:CREATE-ATTACHMENT']">
                    <Button v-tooltip.top="'Add'" class="w-3rem ml-1 sticky" icon="pi pi-plus" @click="clearFormAndReload" />
                  </IfCan>
                  <IfCan :perms="['PAYMENT-MANAGEMENT:DELETE-ATTACHMENT']">
                    <Button v-tooltip.top="'Delete'" :disabled="!idItem" outlined severity="danger" class="w-3rem ml-1 sticky" icon="pi pi-trash" @click="props.item.deleteItem($event)" />
                  </IfCan>
                  <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times" @click="closeDialog" />
                </template>
                <!-- Save, View File, Show History, Add, Delete y Cancel -->
              </EditFormV2>
            </div>
          </div>
        </div>
      </div>
    </template>
  </Dialog>

  <Dialog
    v-model:visible="openDialogHistory"
    modal
    class="mx-3 sm:mx-0"
    content-class="border-round-bottom border-top-1 surface-border"
    :style="{ width: '60%' }"
    :pt="{
      root: {
        class: 'custom-dialog',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
      mask: {
        style: 'backdrop-filter: blur(5px)',
      },
    }"
    @hide="openDialogHistory = false"
  >
    <template #header>
      <div class="flex justify-content-between">
        <h5 class="m-0">
          Attachment Status History
        </h5>
      </div>
    </template>
    <template #default>
      <div class="p-fluid pt-3">
        <DynamicTable
          class="card p-0"
          :data="historyList"
          :columns="historyColumns"
          :options="historyOptions"
          :pagination="historyPagination"
          @on-change-pagination="payloadOnChangePageHistory = $event"
          @on-change-filter="historyParseDataTableFilter"
          @on-sort-field="onSortFieldHistory"
        />
      </div>
    </template>
  </Dialog>
</template>
