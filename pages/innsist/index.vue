<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { IData } from '~/components/table/interfaces/IModelData'
import type { GenericObject } from '~/types'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { validateEntityStatus } from '~/utils/schemaValidations'
import ConnectionPage from '~/pages/innsist/connections.vue'

// VARIABLES
const idItem = ref('')
const loadingSaveAll = ref(false)
const formReload = ref(0)
const listItems = ref<any[]>([])
const listItemsRoomType = ref<any[]>([])
const allDefaultItem = { id: 'All', name: 'All', status: 'ACTIVE' }
const toast = useToast()
const listTradingCompanyItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')
const idItemRoomTypeToLoadFirstTime = ref('')
const tradingCompanyHasConnection = ref(false)
const connectionsDialogVisible = ref(false)
const manageTradingCompanyRoomTypeDialogVisible = ref(false)
const totalHotelsByTradingCompany = ref(0)
const loadingDelete = ref(false)
const listHotelItems = ref<any[]>([])
const confirm = useConfirm()
const disableHotelRoomTpe = ref(false)
const formTitle = ref('')
const filterHotels = ref<IFilter[]>([{
  key: '',
  operator: '',
  value: '',
  logicalOperation: '',
  type: 'filterSearch'
}])

const filterToSearch = ref<GenericObject>({
  criterial: null,
  hotel: [allDefaultItem],
  tradingCompany: [allDefaultItem]
})
const filterToSearchTemp = ref<GenericObject>({
  criterial: null,
  hotel: [allDefaultItem],
  tradingCompany: [allDefaultItem]
})

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Innsist Parameters',
  moduleApi: 'innsist',
  // uriApi: 'innsist-trading-company-hotel',
  uriApi: 'manage-trading-company',
  loading: false,
  actionsAsMenu: false,
  selectAllItemByDefault: false,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?',
  showAcctions: true,
  showEdit: true,
  showDelete: false
})

const optionsRoomTypes = ref({
  tableName: 'Manage Innsist Parameters',
  moduleApi: 'innsist',
  // uriApi: 'innsist-trading-company-hotel',
  uriApi: 'manage-trading-company',
  loading: false,
  actionsAsMenu: false,
  selectAllItemByDefault: false,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?',
  showAcctions: true,
  showEdit: true,
  showDelete: false
})

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'company', header: 'Trading Company', type: 'text' },
  { field: 'innsistCode', header: 'Innsist Code', type: 'text' },
  // { field: 'status', header: 'Active', type: 'bool' },
  { field: 'hasConnection', header: 'Connection?', type: 'bool' },
  { field: 'innsistConnectionParam', header: 'B2B Partner Connection', type: 'select', objApi: { moduleApi: 'innsist', uriApi: 'innsist-connection-params', keyValue: 'dataBaseName' }, sortable: true }
]
const columnsRoomType: IColumn[] = [
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel', keyValue: 'name' }, sortable: true },
  { field: 'roomTypePrefix', header: 'Room Type Prefix', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' }
]

const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const paginationRoomTypes = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'description', name: 'Description' },
]

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 required'
  },
  {
    field: 'company',
    header: 'Trading Company',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 required'
  },
  {
    field: 'hasConnection',
    header: 'Has connection',
    dataType: 'check',
    disabled: true,
    class: 'field col-12'
  },
  {
    field: 'status',
    header: 'Trading Company Status',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mb-2',
  },
]
const fieldsRoomTpe: Array<FieldDefinitionType> = [
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('hotel')
  },
  {
    field: 'roomTypePrefix',
    header: 'Room Type Prefix',
    dataType: 'code',
    class: 'field col-12',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The code field is required').max(1, 'Maximum 1 character').regex(/^[a-z]+$/i, 'Only letters are allowed')
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 required mb-2',
  },
]
const item = ref<GenericObject>({
  id: '',
  code: '',
  company: '',
  innsistCode: '',
  status: true,
  innsistConnectionParam: null,
  hasConnection: false
})

const itemTemp = ref<GenericObject>({
  hotel: null,
  tradingCompany: null,
  roomTypePrefix: '',
  status: true
})
const itemRoomType = ref<GenericObject>({
  id: '',
  hotel: null,
  roomTypePrefix: '',
  status: true,
  hasConnection: false
})
const itemConnection = ref<GenericObject>({
  id: '',
  hostName: null
})
const itemConnectionTemp = ref<GenericObject>({
  id: '',
  hostName: null
})

const objApis = ref({
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  tradingCompany: { moduleApi: 'settings', uriApi: 'manage-trading-companies' },
  innsistTradingCompanyHotel: { moduleApi: 'innsist', uriApi: 'innsist-trading-company-hotel' },
  innsistConnectionParams: { moduleApi: 'innsist', uriApi: 'innsist-connection-params' }
})

const payloadOnChangePage = ref<PageState>()
const payloadRoomTypesOnChangePage = ref<PageState>()

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.ASC
})
const payloadTemp = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.ASC
})
const payloadRoomTypes = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.ASC
})

const tradingCompanySelected = ref({
  id: '',
  code: '',
  name: '',
  innsistCode: '',
  connectionId: '',
  status: true
})
const tradingCompanySelectedTemp = ref({
  id: '',
  code: '',
  name: '',
  innsistCode: '',
  connectionId: '',
  status: true
})

// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fields[0].disabled = false
  updateFieldProperty(fields, 'status', 'disabled', true)
  itemConnection.value = { ...itemConnectionTemp.value }
  formReload.value++
  tradingCompanyHasConnection.value = false
  tradingCompanySelected.value = { ...tradingCompanySelectedTemp.value }
  listItemsRoomType.value = []
}

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]

  // Trading Company
  if (filterToSearch.value.tradingCompany?.length > 0) {
    const filteredItems = filterToSearch.value.tradingCompany.filter((item: any) => item?.id !== 'All')
    if (filteredItems.length > 0) {
      const itemIds = filteredItems?.map((item: any) => item?.id)
      itemIds.forEach((element: any) => {
        payload.value.filter = [...payload.value.filter, {
          key: 'id',
          operator: 'EQUALS',
          value: element,
          logicalOperation: 'OR',
          type: 'filterSearch'
        }]
      })
    }
  }

  payload.value.filter.push({
    key: 'status',
    operator: 'EQUALS',
    value: 'ACTIVE',
    logicalOperation: 'AND',
    type: 'filterSearch'
  })

  options.value.selectAllItemByDefault = false
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value = JSON.parse(JSON.stringify(filterToSearchTemp.value))
  filterToSearch.value.criterial = ENUM_FILTER[0]
  searchAndFilter()
}

async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({
          ...iterator,
          loadingEdit: false,
          loadingDelete: false,
          company: `${iterator.code || ''} - ${iterator.company || ''}`,
          innsistConnectionParam: { ...iterator?.innsistConnectionParam, name: `${iterator?.innsistConnectionParam?.dataBaseName || ''}` }
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }

      tradingCompanyHasConnection.value = iterator.innsistConnectionParam !== null
    }

    listItems.value = [...listItems.value, ...newListItems]

    if (listItems.value.length > 0) {
      idItemToLoadFirstTime.value = listItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.code = response.code
        item.value.company = response.company
        item.value.innsistCode = response.innsistCode
        item.value.status = statusToBoolean(response.status)
        item.value.innsistConnectionParam = response.innsistConnectionParam
        item.value.hasConnection = response.hasConnection
        if (response.innsistConnectionParam) {
          tradingCompanyHasConnection.value = true
        }
        else {
          tradingCompanyHasConnection.value = false
        }
        totalHotelsByTradingCompany.value = await countHotelsByTradingCompany(item.value.id)
        setTradingCompanySelected(item.value.id, item.value.code, item.value.company, item.value.innsistCode, response.innsistConnectionParam?.id, item.value.status)
        await searchAndFilterRoomType(item.value.id)
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Innsist Parametrization methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

function setTradingCompanySelected(id: string, code: string, name: string, innsistCode: string, connectionId: string, status: boolean) {
  tradingCompanySelected.value.id = id
  tradingCompanySelected.value.name = name
  tradingCompanySelected.value.code = code
  tradingCompanySelected.value.innsistCode = innsistCode
  tradingCompanySelected.value.connectionId = connectionId
  tradingCompanySelected.value.status = status
}

async function countHotelsByTradingCompany(id: string) {
  const payloadApi = { ...payloadTemp.value }
  payloadApi.filter = [{
    key: 'manageTradingCompanies.id',
    operator: 'EQUALS',
    value: id,
    logicalOperation: 'AND',
    type: 'filterSearch'
  }, {
    key: 'status',
    operator: 'EQUALS',
    value: 'ACTIVE',
    logicalOperation: 'AND',
    type: 'filterSearch'
  }]
  const response = await GenericService.search(objApis.value.hotel.moduleApi, objApis.value.hotel.uriApi, payloadApi)
  if (response) {
    return response.totalElements
  }
  return 0
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  searchAndFilter()
}
async function resetListItems() {
  payload.value.page = 0
  searchAndFilter()
}
function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function getTradingCompanyFilter(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'company',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'company',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search('settings', 'manage-trading-companies', payload)
    const { data: dataList } = response
    listTradingCompanyItems.value = []
    for (const iterator of dataList) {
      listTradingCompanyItems.value = [...listTradingCompanyItems.value, { id: iterator.id, name: `${iterator.code} - ${iterator.company}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading trading company list:', error)
  }
}

async function onCloseTradingCompanyRoomTypeDialog() {
  await searchAndFilterRoomType(tradingCompanySelected.value.id)
  formReload.value += 1
}

async function searchAndFilterRoomType(id: string) {
  listItemsRoomType.value = []
  payloadRoomTypes.value.filter = [{
    key: 'hotel.manageTradingCompany.id',
    operator: 'EQUALS',
    value: id,
    logicalOperation: 'AND',
    type: 'filterSearch'
  }]

  filterHotels.value[0] = {
    key: 'manageTradingCompanies.id',
    operator: 'EQUALS',
    value: id,
    logicalOperation: 'AND'
  }

  optionsRoomTypes.value.selectAllItemByDefault = false

  await getListRoomType()
}
async function getListRoomType() {
  if (optionsRoomTypes.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    optionsRoomTypes.value.loading = true
    const newListItemsRoomType = []
    listItemsRoomType.value = []
    const responseRoomRate = await GenericService.search(optionsRoomTypes.value.moduleApi, 'innsist-trading-company-hotel', payloadRoomTypes.value)

    const { data: dataList, page, size, totalElements, totalPages } = responseRoomRate

    paginationRoomTypes.value.page = page
    paginationRoomTypes.value.limit = size
    paginationRoomTypes.value.totalElements = totalElements
    paginationRoomTypes.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        const newItem = {
          ...iterator,
          loadingEdit: false,
          loadingDelete: false,
          hotel: { ...iterator?.hotel, name: `${iterator?.hotel.code || ''} - ${iterator?.hotel.name || ''}` }
        }

        // if (itemRoomType.value.id === iterator.id) {
        // newListItemsRoomType.unshift(newItem)
        // }
        // else {
        newListItemsRoomType.push(newItem)
        // }

        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItemsRoomType.value = [...listItemsRoomType.value, ...newListItemsRoomType]

    if (listItemsRoomType.value.length > 0) {
      idItemRoomTypeToLoadFirstTime.value = listItemsRoomType.value[0].id
      await getItemByIdRoomTypes(idItemRoomTypeToLoadFirstTime.value)
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    optionsRoomTypes.value.loading = false
  }
}

async function getItemByIdRoomTypes(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(objApis.value.innsistTradingCompanyHotel.moduleApi, objApis.value.innsistTradingCompanyHotel.uriApi, id)
      if (response) {
        itemRoomType.value.id = response.id
        if (response.hotel) {
          itemRoomType.value.hotel = {
            id: response.hotel.id,
            code: response.hotel.code,
            name: response.hotel.name,
            status: response.hotel.status,
            deleted: response.hotel.deleted,
          }
        }
        itemRoomType.value.roomTypePrefix = response.roomTypePrefix
        itemRoomType.value.status = statusToBoolean(response.status)
        itemRoomType.value.code = response.code
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Innsist Parametrization methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}
async function parseDataTableFilterRoomTypes(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payloadRoomTypes.value.filter = [...payloadRoomTypes.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payloadRoomTypes.value.filter = [...payloadRoomTypes.value.filter, ...parseFilter || []]
  getListRoomType()
}
async function resetListItemsRoomTypes() {
  payloadRoomTypes.value.page = 0
  getListRoomType()
}
function onSortFieldRoomTypes(event: any) {
  listItemsRoomType.value = []
  if (event) {
    payloadRoomTypes.value.sortBy = event.sortField
    payloadRoomTypes.value.sortType = event.sortOrder
    parseDataTableFilterRoomTypes(event.filter)
  }
}

async function requireConfirmationToSave(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    await saveItem(item)
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
        toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
      }
    })
  }
}

function requireConfirmationToDelete(event: any) {
  if (!useRuntimeConfig().public.showDeleteConfirm) {
    deleteRoomTypeItem(idItem.value)
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
        deleteRoomTypeItem(idItem.value)
      },
      reject: () => { }
    })
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true
  if (!checkIfExistsRoomType(item.roomTypePrefix)) {
    if (idItem.value) {
      try {
        item.id = idItem.value
        await updateItem(item)
        idItem.value = ''
        toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
      }
      catch (error: any) {
        successOperation = false
        const errorMessage = error?.data?.error?.errors?.[0]?.message || error?.data?.error?.errorMessage || 'An unexpected error occurred'
        toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 })
      }
    }
    else {
      try {
        await createItem(item)
        toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
      }
      catch (error: any) {
        successOperation = false
        const errorMessage = error?.data?.error?.errors?.[0]?.message || error?.data?.error?.errorMessage || 'An unexpected error occurred'
        toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 })
      }
    }
    loadingSaveAll.value = false
    if (successOperation) {
      manageTradingCompanyRoomTypeDialogVisible.value = false
    }
  }
  else {
    loadingSaveAll.value = false
    toast.add({ severity: 'warn', summary: 'Warning', detail: 'The room type already exists assigned to another hotel', life: 3000 })
  }
}

function checkIfExistsRoomType(item: string) {
  const existingRoomTypes = new Set(listItemsRoomType.value.map(item => item.roomTypePrefix))
  if (existingRoomTypes.has(item)) {
    return true
  }
  return false
}
async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload = await itemToPayload(item)
    const response = await GenericService.create(objApis.value.innsistTradingCompanyHotel.moduleApi, objApis.value.innsistTradingCompanyHotel.uriApi, payload)
    return response
  }
}
async function itemToPayload(item: { [key: string]: any }) {
  const payloadApi = {
    id: item.id ? item.id : null,
    hotel: item.hotel ? item.hotel.id : null,
    roomTypePrefix: item.roomTypePrefix,
    status: statusToString(item.status)
  }
  itemRoomType.value = {
    id: item.id ? item.id : null,
    hotel: item.hotel ? item.hotel.id : null,
    roomTypePrefix: item.roomTypePrefix,
    status: statusToString(item.status)
  }
  return payloadApi
}
async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload = await itemToPayload(item)
  await GenericService.updateWithOutId(objApis.value.innsistTradingCompanyHotel.moduleApi, objApis.value.innsistTradingCompanyHotel.uriApi, payload)
}

async function deleteRoomTypeItem(item: string) {
  loadingSaveAll.value = true
  let successOperation = true
  if (item) {
    try {
      await GenericService.deleteItem(objApis.value.innsistTradingCompanyHotel.moduleApi, objApis.value.innsistTradingCompanyHotel.uriApi, item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: 'Error', life: 3000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    manageTradingCompanyRoomTypeDialogVisible.value = false
  }
}

async function getHotelFilter(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'manageTradingCompanies.id',
        operator: 'EQUALS',
        value: tradingCompanySelected?.value.id,
        logicalOperation: 'AND'
      }, {
        key: 'name',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search('settings', 'manage-hotel', payload)

    const { data: dataList } = response
    listHotelItems.value = []
    const existingIds = new Set(listItemsRoomType.value.map(item => item.hotel.id))
    for (const iterator of dataList) {
      if (!existingIds.has(iterator.id)) {
        listHotelItems.value = [...listHotelItems.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
      }
    }
  }
  catch (error) {
    console.error('Error on loading hotel list:', error)
  }
}

async function openEditTradingCompanyConnection(event: any) {
  await getItemById(event)
  connectionsDialogVisible.value = event
  formReload.value += 1
}

async function closeEditTradingCompanyConnection() {
  searchAndFilter()
  connectionsDialogVisible.value = false
}

function openAddManageTradingCompanyRoomTypeDialog() {
  itemRoomType.value = []
  itemRoomType.value.status = true
  disableHotelRoomTpe.value = false
  formTitle.value = 'Add'
  updateFieldProperty(fieldsRoomTpe, 'status', 'disabled', true)
  idItem.value = ''
  manageTradingCompanyRoomTypeDialogVisible.value = true
}

async function openEditManageTradingCompanyRoomTypeDialog(event: any) {
  await getItemByIdRoomTypes(event)
  formTitle.value = 'Edit'
  updateFieldProperty(fieldsRoomTpe, 'status', 'disabled', false)
  manageTradingCompanyRoomTypeDialogVisible.value = true
  disableHotelRoomTpe.value = true
}

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    // getList()
    searchAndFilter()
  }
})
</script>

<template>
  <div class="col-12 align-items-center grid w-full">
    <div class="flex align-items-center justify-content-between w-full">
      <h5 class="mb-3">
        {{ options.tableName }}
      </h5>
    </div>
  </div>

  <div class="grid">
    <div class="col-12 md:order-1 md:col-12 xl:col-6 lg:col-12 mt-0 px-1 py-0">
      <div class="card p-0">
        <div class="card px-1 m-0 py-0">
          <div class="font-bold text-lg px-4 bg-primary custom-card-header">
            Manage Trading Company Connections
          </div>
        </div>

        <div style="display: flex; height: 23%;" class="responsive-height">
          <div class="p-0 m-0 py-0 px-0 mt-3 ml-3" style="flex: 1;">
            <div class="grid align-items-center justify-content-center">
              <div class="col-12">
                <div class="flex align-items-center mb-0">
                  <label for="" class="font-bold mr-2 mb-0 mt-2" style="margin-right: 8px; flex: 0 0 auto;">
                    Trading Company:</label>
                  <div class="w-full">
                    <DebouncedAutoCompleteComponent
                      id="autocomplete"
                      class="w-full"
                      field="name"
                      item-value="id"
                      :multiple="true"
                      :model="filterToSearch.tradingCompany"
                      :suggestions="[...listTradingCompanyItems]"
                      @change="($event) => {
                        if (!filterToSearch.tradingCompany.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.tradingCompany = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.tradingCompany = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                      @load="($event) => getTradingCompanyFilter($event)"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Button filter -->
          <div class="col-12 md:col-1 flex align-items-center my-0 py-0 w-auto justify-content-center">
            <Button
              v-tooltip.top="'Filter'"
              label=""
              class="w-3rem mx-2"
              icon="pi pi-search"
              @click="searchAndFilter"
            />
            <Button
              v-tooltip.top="'Clear'"
              label=""
              outlined
              class="w-3rem mx-2"
              icon="pi pi-filter-slash"
              @click="clearFilterToSearch"
            />
          </div>
        </div>
      </div>
      <DynamicTable
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="clearForm"
        @open-edit-dialog="openEditTradingCompanyConnection($event)"
        @update:clicked-item="getItemById($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      />
    </div>

    <div class="col-12 md:order-1 md:col-12 xl:col-6 lg:col-12 mt-0 px-1 py-0">
      <div class="card px-1 m-0 py-0">
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          Manage Room Types by Hotel
        </div>
      </div>

      <div class="flex justify-content-between mt-0 mr-2">
        <div class="flex align-items-center gap-2">
          <div class="p-2 font-bold mt-3 ml-3" style="margin-right: 8px; flex: 0 0 auto;">
            {{ tradingCompanySelected.name }}
          </div>
        </div>
        <div class="flex align-items-center gap-2 mt-3">
          <div class="flex justify-content-end px-0">
            <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="openAddManageTradingCompanyRoomTypeDialog" />
          </div>
        </div>
      </div>

      <div class="col-12 order-0 md:order-1 md:col-12 xl:col-12 mt-3 ml-0">
        <DynamicTable
          :data="listItemsRoomType"
          :columns="columnsRoomType"
          :options="optionsRoomTypes"
          :pagination="paginationRoomTypes"
          @on-confirm-create="clearForm"
          @open-edit-dialog="openEditManageTradingCompanyRoomTypeDialog($event)"
          @update:clicked-item="getItemByIdRoomTypes($event)"
          @on-change-pagination="payloadRoomTypesOnChangePage = $event"
          @on-change-filter="parseDataTableFilterRoomTypes"
          @on-list-item="resetListItemsRoomTypes"
          @on-sort-field="onSortFieldRoomTypes"
        />
      </div>
    </div>

    <DynamicContentModal
      :visible="connectionsDialogVisible" :component="ConnectionPage" :component-props="{ tradingCompanySelected }"
      header="Manage Trading Company Connection" @close="closeEditTradingCompanyConnection"
    />
    <Dialog v-model:visible="manageTradingCompanyRoomTypeDialogVisible" modal header="Hotel Room Type Assignment" :style="{ width: '40rem' }" @hide="onCloseTradingCompanyRoomTypeDialog">
      <div class="font-bold text-lg px-4 bg-primary custom-card-header mt-2">
        {{ formTitle }}
      </div>
      <div class="card p-4 mt-0">
        <EditFormV2
          :key="formReload"
          :fields="fieldsRoomTpe"
          :item="itemRoomType"
          :show-actions="true"
          :loading-save="loadingSaveAll"
          :loading-delete="loadingDelete"
          @cancel="clearForm"
          @delete="requireConfirmationToDelete($event)"
          @submit="requireConfirmationToSave($event)"
        >
          <template #field-hotel="{ item: data, onUpdate }">
            <DebouncedAutoCompleteComponent
              v-if="!loadingSaveAll"
              id="autocomplete"
              field="name"
              item-value="id"
              :model="data.hotel"
              :suggestions="listHotelItems"
              :disabled="disableHotelRoomTpe"
              @change="($event) => {
                onUpdate('hotel', $event)
              }"
              @load="($event) => getHotelFilter($event)"
            />
            <Skeleton v-else height="2rem" class="mb-2" />
          </template>
        </EditFormV2>
      </div>
    </Dialog>
  </div>
</template>

<style></style>
