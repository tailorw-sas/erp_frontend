<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { useDebounceFn } from '@vueuse/core'
import { filter } from 'lodash'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import createClientDialog from '~/components/clientForm/createClientDialog.vue'
import { validateEntityStatus } from '~/utils/schemaValidations'
import ContactAgencyPage from '~/pages/settings/agency-contact/index.vue'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const formReload = ref(0)
const formReloadClient = ref(0)
const loadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const loadingDelete = ref(false)
const contactDialogVisible = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const agencyContact = ref<any>({})
const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    disabled: false,
    dataType: 'code',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(20, 'Maximum 20 characters').regex(/^[a-z0-9]+$/i, 'Only letters and numbers are allowed') // .regex(/^[a-z]+$/i, 'Only letters are allowed')
  },
  {
    field: 'cif',
    header: 'CIF',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The cif field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'audit',
    header: 'Audit',
    dataType: 'check',
    class: 'field col-12 mb-3 mt-3',
    headerClass: 'mb-1',
  },
  {
    field: 'agencyType',
    header: 'Agency Type',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: validateEntityStatus('agency type'),
  },

  {
    field: 'contact',
    header: 'Send Agency Contact',
    hidden: true,
    dataType: 'text',
    class: 'field col-12',
  },
  {
    field: 'generationType',
    header: 'Generation Type',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string().min(1, 'The generation type field is required'),
      name: z.string().min(1, 'The generation type field is required'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'The generation type field is required' }),
  },
  {
    field: 'client',
    header: 'Client',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: validateEntityStatus('client'),
  },
  {
    field: 'agencyAlias',
    header: 'Agency Alias',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('agency alias'),
  },
  {
    field: 'country',
    header: 'Country',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: validateEntityStatus('country'),
  },
  {
    field: 'cityState',
    header: 'City State',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: validateEntityStatus('city state'),
  },
  {
    field: 'city',
    header: 'City',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'zipCode',
    header: 'Zip Code',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'address',
    header: 'Address',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'mailingAddress',
    header: 'Mailing Address',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'phone',
    header: 'Phone',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
    validation: z.string().regex(/^\+?\d+$/, 'Only numeric characters allowed').or(z.string().length(0))
  },
  {
    field: 'alternativePhone',
    header: 'Alternative Phone',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
    validation: z.string().regex(/^\+?\d+$/, 'Only numeric characters allowed').or(z.string().length(0))
  },
  {
    field: 'email',
    header: 'Email',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
    validation: z.string().email('Invalid email').or(z.string().length(0))
  },
  {
    field: 'alternativeEmail',
    header: 'Alternative Email',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
    validation: z.string().email('Invalid email').or(z.string().length(0))
  },
  {
    field: 'contactName',
    header: 'Contact Name',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'isDefault',
    header: 'Default',
    dataType: 'check',
    class: 'field col-12 mt-3',
    headerClass: 'mb-1',
  },
  {
    field: 'autoReconcile',
    header: 'Auto Reconcile',
    dataType: 'check',
    class: 'field col-12 mb-3',
    headerClass: 'mb-1',
  },
  {
    field: 'creditDay',
    header: 'Credit Day',
    dataType: 'number',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'sentB2BPartner',
    header: 'Sent B2BPartner',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: validateEntityStatus('sent b2b partner'),
  },
  {
    field: 'sentFileFormat',
    header: 'Sent File Format',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string().min(1, 'The sent file format field is required'),
      name: z.string().min(1, 'The sent file format field is required'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'The sent file format field is required' }),
  },
  {
    field: 'rfc',
    header: 'RFC',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'validateCheckout',
    header: 'Validate Checkout',
    dataType: 'check',
    class: 'field col-12 mt-3 mb-3',
    headerClass: 'mb-1',
  },
  {
    field: 'bookingCouponFormat',
    header: 'Booking Coupon Format',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 mt-3 mb-3',
  },
]

const item = ref<GenericObject>({
  code: '',
  name: '',
  status: true,
  cif: '',
  agencyAlias: null,
  audit: false,
  zipCode: '',
  address: '',
  address2: '',
  phone: '',
  alternativePhone: '',
  email: '',
  alternativeEmail: '',
  contactName: '',
  isDefault: false,
  autoReconcile: false,
  creditDay: 0,
  rfc: '',
  validateCheckout: false,
  bookingCouponFormat: '',
  description: '',
  city: '',
  generationType: null,
  sentFileFormat: null,
  agencyType: null,
  client: null,
  sentB2BPartner: null,
  country: null,
  cityState: null,
})

const itemTemp = ref<GenericObject>({
  code: '',
  name: '',
  status: true,
  cif: '',
  agencyAlias: null,
  audit: false,
  zipCode: '',
  address: '',
  address2: '',
  phone: '',
  alternativePhone: '',
  email: '',
  alternativeEmail: '',
  contactName: '',
  isDefault: false,
  autoReconcile: false,
  creditDay: 0,
  rfc: '',
  validateCheckout: false,
  bookingCouponFormat: '',
  description: '',
  city: '',
  generationType: null,
  sentFileFormat: null,
  agencyType: null,
  client: null,
  sentB2BPartner: null,
  country: null,
  cityState: null,
})

const agencyTypeList = ref<any[]>([])
const confAgencyTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency-type',
})

const refClient: Ref = ref(null)
const showClientDialog = ref(false)
const clientList = ref<any[]>([])
const confClientApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-client',
})

const b2BPartnerList = ref<any[]>([])
const confB2BPartnerApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-b2b-partner',
})

const countryList = ref<any[]>([])
const confCountryApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-country',
})

const cityStateList = ref<any[]>([])
const confCityStateApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-city-state',
})

const agencyAliasList = ref<any[]>([])
const defaultAgencyAlias = ref<any>({
  id: '000-MySelf',
  name: '000-MySelf',
  status: 'ACTIVE'
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
]
// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'code', header: 'Code', type: 'text' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'agencyAlias', header: 'Alias', type: 'text' },
  { field: 'client', header: 'Client', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-client', keyValue: 'name' }, sortable: true },
  // { field: 'audit', header: 'Audit', type: 'bool' },
  { field: 'autoReconcile', header: 'Auto Reconcile', type: 'bool' },
  { field: 'creditDay', header: 'Credit Day', type: 'text' },
  { field: 'sentB2BPartner', header: 'Sent B2B Partner', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-b2b-partner', keyValue: 'name' }, sortable: true },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Agency',
  moduleApi: 'settings',
  uriApi: 'manage-agency',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Are you sure you want to delete the agency: {{name}}?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  // clientObject.value = {}
  updateFieldProperty(fields, 'contact', 'hidden', true)
  updateFieldProperty(fields, 'status', 'disabled', true)
  fields[0].disabled = false
  formReload.value++
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
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
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

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.criterial = null
  filterToSearch.value.search = ''
  getList()
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)

      if (response) {
        item.value.id = response.id
        item.value.code = response.code
        item.value.name = response.name
        item.value.cif = response.cif
        item.value.agencyAlias = response.agencyAlias
        item.value.city = response.city
        item.value.description = response.description
        item.value.status = statusToBoolean(response.status)
        item.value.audit = response.audit
        item.value.zipCode = response.zipCode
        item.value.address = response.address
        item.value.address2 = response.address2
        item.value.mailingAddress = response.mailingAddress
        item.value.phone = response.phone
        item.value.alternativePhone = response.alternativePhone
        item.value.email = response.email
        item.value.alternativeEmail = response.alternativeEmail
        item.value.contactName = response.contactName
        item.value.isDefault = response.isDefault
        item.value.autoReconcile = response.autoReconcile
        item.value.creditDay = response.creditDay
        item.value.rfc = response.rfc
        item.value.validateCheckout = response.validateCheckout
        item.value.bookingCouponFormat = response.bookingCouponFormat
        item.value.sentFileFormat = ENUM_FILE_FORMAT.find(i => i.id === response.sentFileFormat)

        if (response.agencyType) {
          item.value.agencyType = {
            id: response.agencyType.id,
            name: `${response.agencyType.code} - ${response.agencyType.name}`,
            status: response.agencyType.status,
          }
        }

        if (response.client) {
          item.value.client = {
            id: response.client.id,
            name: `${response.client.code} - ${response.client.name}`,
            status: response.client.status,
          }
        }
        if (response.agencyAlias && response.agencyAlias !== '000-MySelf') {
          await GetAgenciesList(item.value.client.id)
          item.value.agencyAlias = agencyAliasList.value.find(i => i.name === response.agencyAlias)
        }
        else {
          agencyAliasList.value = []
          agencyAliasList.value = [defaultAgencyAlias.value]
          item.value.agencyAlias = defaultAgencyAlias.value
        }
        if (response.sentB2BPartner) {
          item.value.sentB2BPartner = {
            id: response.sentB2BPartner.id,
            name: `${response.sentB2BPartner.code} - ${response.sentB2BPartner.name}`,
            status: response.sentB2BPartner.status,
          }
        }
        countryList.value = [response.country]
        item.value.country = response.country

        if (response.cityState) {
          item.value.cityState = {
            id: response.cityState.id,
            name: `${response.cityState.code} - ${response.cityState.name}`,
            status: response.cityState.status,
          }
        }
        item.value.isDefault = response.isDefault ?? false

        item.value.generationType = ENUM_GENERATION_TYPE.find(i => i.id === response.generationType)
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'contact', 'hidden', false)
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Agency methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.agencyType = typeof payload.agencyType === 'object' ? payload.agencyType.id : payload.agencyType
    payload.client = payload.client !== null && typeof payload.client === 'object' ? payload.client.id : payload.client
    payload.agencyAlias = payload.agencyAlias !== null && typeof payload.agencyAlias === 'object' ? payload.agencyAlias.name : '000-MySelf'
    payload.sentB2BPartner = payload.sentB2BPartner !== null && typeof payload.sentB2BPartner === 'object' ? payload.sentB2BPartner.id : payload.sentB2BPartner
    payload.country = typeof payload.country === 'object' ? payload.country.id : payload.country
    payload.generationType = typeof payload.generationType === 'object' ? payload.generationType.id : payload.generationType
    payload.cityState = typeof payload.cityState === 'object' ? payload.cityState.id : payload.cityState
    payload.sentFileFormat = payload.sentFileFormat !== null && typeof payload.sentFileFormat === 'object' ? payload.sentFileFormat.id : payload.sentFileFormat
    payload.status = statusToString(payload.status)
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.agencyType = typeof payload.agencyType === 'object' ? payload.agencyType.id : payload.agencyType
  payload.client = payload.client !== null && typeof payload.client === 'object' ? payload.client.id : payload.client
  payload.agencyAlias = typeof payload.agencyAlias === 'object' ? payload.agencyAlias.name : payload.agencyAlias
  payload.sentB2BPartner = payload.sentB2BPartner !== null && typeof payload.sentB2BPartner === 'object' ? payload.sentB2BPartner.id : payload.sentB2BPartner
  payload.country = typeof payload.country === 'object' ? payload.country.id : payload.country
  payload.generationType = typeof payload.generationType === 'object' ? payload.generationType.id : payload.generationType
  payload.cityState = typeof payload.cityState === 'object' ? payload.cityState.id : payload.cityState
  payload.sentFileFormat = payload.sentFileFormat !== null && typeof payload.sentFileFormat === 'object' ? payload.sentFileFormat.id : payload.sentFileFormat
  payload.status = statusToString(payload.status)
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    clearForm()
    getList()
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
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
      await updateItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
      await createItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
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

// Get agency type list
async function getAgencyTypeList(query: string) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'code',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'OR'
            },
            {
              key: 'name',
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
          pageSize: 20,
          page: 0,
          sortBy: 'name',
          sortType: ENUM_SHORT_TYPE.ASC
        }

    const response = await GenericService.search(confAgencyTypeApi.moduleApi, confAgencyTypeApi.uriApi, payload)
    const { data: dataList } = response
    agencyTypeList.value = []
    for (const iterator of dataList) {
      agencyTypeList.value = [...agencyTypeList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading agency type list:', error)
  }
}

async function openClientDialog() {
  showClientDialog.value = true
}

async function handleClientSubmit(data: any) {
  data.name = `${data.code} - ${data.name}`
  clientList.value = [data]
  refClient.value.$emit('update:model-value', data)
  formReloadClient.value += 1
}

async function GetAgenciesList(param: any) {
  try {
    const clientResponse = await GenericService.getById(confClientApi.moduleApi, confClientApi.uriApi, param)
    const agencies = clientResponse.agencies

    agencyAliasList.value = []
    agencyAliasList.value.push(defaultAgencyAlias.value)
    for (const iterator of agencies) {
      agencyAliasList.value = [...agencyAliasList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
}

interface DataListItem {
  id: string
  code: string
  status: string | boolean
  name: string
}

interface ListItem {
  id: string
  code: string
  name: string
  status: string | boolean
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    code: data.code,
    status: data.status,
  }
}

async function getAgenciesListForSelect(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[], short?: IQueryToSort) {
  // Buscar en los filtro si viene el filtro de client.id, en caso de que si venga se ejecuta la funcion y en caso de que no, solo devolver el listado vacio
  const filterClientId = filter?.find(item => item.key === 'client.id')

  if (filterClientId && filterClientId.value) {
    agencyAliasList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, short)
    agencyAliasList.value.push(defaultAgencyAlias.value)
  }
  else {
    agencyAliasList.value = []
    agencyAliasList.value.push(defaultAgencyAlias.value)
  }
}

const debouncedClient = useDebounceFn(async (event) => {
  await getClientList(event.query)
}, 300, { maxWait: 5000 })

async function getClientList(param: any) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'code',
              operator: 'LIKE',
              value: param.toLowerCase(),
              logicalOperation: 'OR'
            },
            {
              key: 'name',
              operator: 'LIKE',
              value: param,
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
          pageSize: 20,
          page: 0,
          sortBy: 'code',
          sortType: ENUM_SHORT_TYPE.DESC
        }

    const response = await GenericService.search(confClientApi.moduleApi, confClientApi.uriApi, payload)
    const { data: dataList } = response
    clientList.value = []
    for (const iterator of dataList) {
      clientList.value = [...clientList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading client list:', error)
  }
}

async function getB2BPartnerList(param: any) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'code',
              operator: 'LIKE',
              value: param.toLowerCase(),
              logicalOperation: 'OR'
            },
            {
              key: 'name',
              operator: 'LIKE',
              value: param,
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
          pageSize: 20,
          page: 0,
          sortBy: 'code',
          sortType: ENUM_SHORT_TYPE.ASC
        }

    const response = await GenericService.search(confB2BPartnerApi.moduleApi, confB2BPartnerApi.uriApi, payload)
    const { data: dataList } = response
    b2BPartnerList.value = []
    for (const iterator of dataList) {
      b2BPartnerList.value = [...b2BPartnerList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading b2b partner list:', error)
  }
}

async function getCountryList(param: any) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'code',
              operator: 'LIKE',
              value: param,
              logicalOperation: 'OR'
            },
            {
              key: 'name',
              operator: 'LIKE',
              value: param,
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
          pageSize: 20,
          page: 0,
          sortBy: 'name',
          sortType: ENUM_SHORT_TYPE.DESC
        }

    const response = await GenericService.search(confCountryApi.moduleApi, confCountryApi.uriApi, payload)
    const { data: dataList } = response
    countryList.value = []
    for (const iterator of dataList) {
      countryList.value = [...countryList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading country list:', error)
  }
}

async function getCityStateList(countryId: string, query: string) {
  try {
    const payload = {
      filter: [{
        key: 'country.id',
        operator: 'EQUALS',
        value: countryId,
        logicalOperation: 'AND'
      }, {
        key: 'name',
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
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confCityStateApi.moduleApi, confCityStateApi.uriApi, payload)
    const { data: dataList } = response
    cityStateList.value = []
    for (const iterator of dataList) {
      cityStateList.value = [...cityStateList.value, { id: iterator.id, code: iterator.code, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading city state list:', error)
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
function requireConfirmationToDelete(event: any) {
  if (!useRuntimeConfig().public.showDeleteConfirm) {
    deleteItem(idItem.value)
  }
  else {
    confirm.require({
      target: event.currentTarget,
      group: 'headless',
      header: 'Delete the record',
      message: 'Do you want to delete the record?',
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

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField === 'client' ? 'client.name' : event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function openContactDialogVisible(item: any) {
  agencyContact.value = { agency: { id: item.id, name: item.name, status: 'ACTIVE' } }
  contactDialogVisible.value = true
}

// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Manage Agency
    </h3>
    <IFCan :perms="['AGENCY:CREATE']">
      <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
        <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
      </div>
    </IFCan>
  </div>
  <div class="grid">
    <div class="col-12 order-0 md:order-1 md:col-6 xl:col-9">
      <div class="card p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab header="Filters">
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex align-items-center gap-2">
                <label for="email3">Criteria</label>
                <div class="w-full lg:w-auto">
                  <Dropdown
                    v-model="filterToSearch.criterial"
                    :options="[...ENUM_FILTER]"
                    option-label="name"
                    placeholder="Criteria"
                    return-object="false"
                    class="align-items-center w-full"
                    show-clear
                  />
                </div>
              </div>
              <div class="flex align-items-center gap-2">
                <label for="email">Search</label>
                <div class="w-full lg:w-auto">
                  <IconField icon-position="left" class="w-full">
                    <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                    <InputIcon class="pi pi-search" />
                  </IconField>
                </div>
              </div>
              <div class="flex align-items-center">
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :loading="loadingSearch" @click="clearFilterToSearch" />
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
        @open-edit-dialog="getItemById($event)"
        @update:clicked-item="getItemById($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      />
    </div>
    <div class="col-12 order-1 md:order-0 md:col-6 xl:col-3">
      <div>
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          {{ formTitle }}
        </div>
        <div class="card">
          <EditFormV2
            :key="formReload"
            :fields="fields"
            :item="item"
            :show-actions="true"
            :loading-save="loadingSaveAll"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-agencyType="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.agencyType"
                :suggestions="[...agencyTypeList]"
                @change="($event) => {
                  onUpdate('agencyType', $event)
                }"
                @load="($event) => getAgencyTypeList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-contact>
              <InputGroup v-if="!loadingSaveAll">
                <InputText placeholder="Send Agency Contact" disabled />
                <Button
                  icon="pi pi-eye" type="button" text aria-haspopup="true" aria-controls="overlay_menu_filter"
                  @click="openContactDialogVisible(item)"
                />
              </InputGroup>
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-client="{ item: data, onUpdate }">
              <div class="flex">
                <InputGroup icon-position="left" class="w-full">
                  <AutoComplete
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    ref="refClient"
                    :key="formReloadClient"
                    v-model="data.client"
                    :suggestions="clientList"
                    field="name"
                    return-object="false"
                    dropdown
                    show-clear
                    force-selection
                    placeholder="Type to search"
                    @complete="debouncedClient($event)"
                    @update:model-value="($event) => {
                      if ($event && $event.id) {
                        GetAgenciesList($event.id)
                        onUpdate('client', $event)
                      }
                      else {
                        agencyAliasList = []
                        agencyAliasList.push(defaultAgencyAlias)
                      }

                    }"
                  />
                  <Skeleton v-else height="2rem" class="mb-2" />
                  <Button type="button" icon="pi pi-user-plus" text aria-haspopup="true" aria-controls="overlay_menu_filter" @click="openClientDialog" />
                </InputGroup>
              </div>
            </template>

            <template #field-agencyAlias="{ item: data, onUpdate }">
              <!-- <Dropdown
                v-if="!loadingSaveAll"
                v-model="data.agencyAlias"
                :options="[...agencyAliasList]"
                option-label="name"
                return-object="false"
                show-clear
                @update:model-value="($event) => {
                  onUpdate('agencyAlias', $event)
                }"
              /> -->
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocompleteAgencyAlias"
                :model="data.agencyAlias"
                field="name"
                item-value="id"
                :suggestions="agencyAliasList"
                @change="($event) => {
                  onUpdate('agencyAlias', $event)
                }"
                @load="async ($event) => {
                  const objQueryToSearch = {
                    query: $event,
                    keys: ['name', 'code'],
                  }
                  const filter: FilterCriteria[] = []
                  filter.push(
                    {
                      key: 'client.id',
                      logicalOperation: 'AND',
                      operator: 'EQUALS',
                      value: data.client?.id,
                    },
                  )
                  await getAgenciesListForSelect(options.moduleApi, options.uriApi, objQueryToSearch, filter)

                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-sentB2BPartner="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.sentB2BPartner"
                :suggestions="b2BPartnerList"
                @change="($event) => {
                  onUpdate('sentB2BPartner', $event)
                }"
                @load="($event) => getB2BPartnerList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-country="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.country"
                :suggestions="countryList"
                @change="($event) => {
                  cityStateList = []
                  if (typeof $event === 'object' && $event) {
                    getCityStateList($event.id, '')
                    onUpdate('cityState', null)
                  }
                  else {
                    cityStateList = []
                  }
                  onUpdate('country', $event)
                }"
                @load="($event) => getCountryList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-cityState="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.cityState"
                :suggestions="cityStateList"
                :disabled="!data.country"
                @change="($event) => {
                  onUpdate('cityState', $event)
                }"
                @load="($event) => getCityStateList(data.country.id, $event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-generationType="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll"
                v-model="data.generationType"
                :options="[...ENUM_GENERATION_TYPE]"
                option-label="name"
                return-object="false"
                filter
                show-clear
                @update:model-value="($event) => {
                  onUpdate('generationType', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-sentFileFormat="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll"
                v-model="data.sentFileFormat"
                :options="[...ENUM_FILE_FORMAT]"
                option-label="name"
                return-object="false"
                filter
                show-clear
                @update:model-value="($event) => {
                  onUpdate('sentFileFormat', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-phone="{ item: data, onUpdate }">
              <div class="flex">
                <InputGroup icon-position="left" class="w-full">
                  <InputText
                    v-if="!loadingSaveAll"
                    v-model="data.phone"
                    type="text"
                    placeholder="Phone"
                    show-clear
                    required
                    @update:model-value="onUpdate('phone', $event)"
                  />
                  <Skeleton v-else height="2rem" class="mb-2" />

                  <Button type="button" icon="pi pi-phone" text aria-haspopup="true" aria-controls="overlay_menu_filter" />
                </InputGroup>
              </div>
            </template>

            <template #field-alternativePhone="{ item: data, onUpdate }">
              <div class="flex">
                <InputGroup icon-position="left" class="w-full">
                  <InputText
                    v-if="!loadingSaveAll"
                    v-model="data.alternativePhone"
                    type="text"
                    placeholder="Alternative Phone"
                    show-clear
                    required
                    @update:model-value="onUpdate('alternativePhone', $event)"
                  />
                  <Skeleton v-else height="2rem" class="mb-2" />

                  <Button type="button" icon="pi pi-phone" text aria-haspopup="true" aria-controls="overlay_menu_filter" />
                </InputGroup>
              </div>
            </template>
            <template #form-footer="props">
              <div class="flex justify-content-end">
                <IfCan :perms="idItem ? ['AGENCY:EDIT'] : ['AGENCY:CREATE']">
                  <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" @click="props.item.submitForm($event)" />
                </IfCan>
                <IfCan :perms="['AGENCY:DELETE']">
                  <Button v-tooltip.top="'Delete'" class="w-3rem" severity="danger" outlined :loading="loadingDelete" icon="pi pi-trash" @click="props.item.deleteItem($event)" />
                </IfCan>
              </div>
            </template>
          </EditFormV2>
          <DynamicContentModal
            :visible="contactDialogVisible"
            :component="ContactAgencyPage"
            :component-props="agencyContact"
            :header="`Agency ${item.code} - ${item.name}`"
            @close="contactDialogVisible = $event"
          />
        </div>
      </div>
    </div>
  </div>

  <div v-if="showClientDialog" class="col-12">
    <createClientDialog :open-dialog="showClientDialog" @submit="handleClientSubmit" @close="showClientDialog = false" />
  </div>
</template>
