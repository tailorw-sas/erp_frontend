<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean, statusToString, updateFieldProperty } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'
import ContactPage from '~/pages/settings/contact/index.vue'
import {
  validateEntityStatus
} from '~/utils/schemaValidations'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const loadingSearch = ref(false)
const listCountryItems = ref<any[]>([])
const listCityStateItems = ref<any[]>([])
const listCurrencyItems = ref<any[]>([])
const listRegionItems = ref<any[]>([])
const listTradingCompanyItems = ref<any[]>([])
const formReload = ref(0)
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const contactDialogVisible = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    disabled: false,
    dataType: 'code',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(20, 'Maximum 20 characters').regex(/^[a-z]+$/i, 'Only text characters allowed')
  },
  {
    field: 'babelCode',
    header: 'Bavel Code',
    disabled: false,
    dataType: 'text',
    class: 'field col-12',
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'manageCountry',
    header: 'Country',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('country')
  },
  {
    field: 'manageCityState',
    header: 'City State',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('city state'),
  },
  {
    field: 'city',
    header: 'City',
    dataType: 'text',
    class: 'field col-12',
  },
  {
    field: 'address',
    header: 'Address',
    dataType: 'text',
    class: 'field col-12',
  },
  {
    field: 'manageCurrency',
    header: 'Currency',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('currency'),
  },
  {
    field: 'contact',
    header: 'Contact',
    hidden: true,
    dataType: 'text',
    class: 'field col-12',
  },
  {
    field: 'manageRegion',
    header: 'Region',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('region'),
  },
  {
    field: 'manageTradingCompanies',
    header: 'Trading Company',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string(),
      company: z.string(),
      status: z.enum(['ACTIVE', 'INACTIVE'], { message: 'The trading company must be either ACTIVE or INACTIVE' })
    }).nullable()
      .refine(value => value && value.id && value.company, { message: `The Trading Company field is required` })
      .refine(value => value === null || value.status === 'ACTIVE', {
        message: 'This trading company is not active',
      })
  },
  {
    field: 'applyByTradingCompany',
    header: 'Apply By Trading Company',
    dataType: 'check',
    class: 'field col-12 mb-3 mt-3',
  },
  {
    field: 'prefixToInvoice',
    header: 'Prefix To Invoice',
    dataType: 'text',
    class: 'field col-12',
  },
  // {
  //   field: 'isNightType',
  //   header: 'Night Type',
  //   dataType: 'check',
  //   class: 'field col-12 mt-3',
  // },
  {
    field: 'autoApplyCredit',
    header: 'Auto Apply Credit',
    dataType: 'check',
    class: 'field col-12',
  },
  {
    field: 'isVirtual',
    header: 'Is Virtual',
    dataType: 'check',
    class: 'field col-12',
  },
  {
    field: 'requiresFlatRate',
    header: 'Requires Flat Rate',
    dataType: 'check',
    class: 'field col-12',
  },
  {
    field: 'isApplyByVCC',
    header: 'Is Apply By VCC',
    dataType: 'check',
    class: 'field col-12 mb-3',
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
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
  babelCode: '',
  name: '',
  status: true,
  manageCountry: null,
  manageCityState: null,
  city: '',
  address: '',
  manageCurrency: null,
  manageRegion: null,
  manageTradingCompanies: null,
  applyByTradingCompany: false,
  prefixToInvoice: '',
  // isNightType: false,
  autoApplyCredit: false,
  isVirtual: false,
  requiresFlatRate: false,
  isApplyByVCC: false,
  description: '',
})

const itemTemp = ref<GenericObject>({
  code: '',
  babelCode: '',
  name: '',
  status: true,
  manageCountry: null,
  manageCityState: null,
  city: '',
  address: '',
  manageCurrency: null,
  manageRegion: null,
  manageTradingCompanies: null,
  applyByTradingCompany: false,
  prefixToInvoice: '',
  // isNightType: false,
  autoApplyCredit: false,
  isVirtual: false,
  requiresFlatRate: false,
  isApplyByVCC: false,
  description: '',
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
]

const columns: IColumn[] = [
  { field: 'code', header: 'Code', type: 'text' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Hotel',
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
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
  updateFieldProperty(fields, 'code', 'disabled', false)
  updateFieldProperty(fields, 'contact', 'hidden', true)
  listCountryItems.value = []
  listCityStateItems.value = []
  listCurrencyItems.value = []
  listRegionItems.value = []
  listTradingCompanyItems.value = []
  updateFieldProperty(fields, 'status', 'disabled', true)
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
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getList()
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

async function getItemById(id: string) {
  if (id) {
    clearForm()
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.code = response.code
        item.value.name = response.name
        item.value.babelCode = response.babelCode
        item.value.status = statusToBoolean(response.status)
        item.value.city = response.city
        item.value.address = response.address
        item.value.applyByTradingCompany = response.applyByTradingCompany
        item.value.prefixToInvoice = response.prefixToInvoice
        item.value.autoApplyCredit = response.autoApplyCredit
        // item.value.isNightType = response.isNightType
        item.value.isVirtual = response.isVirtual
        item.value.requiresFlatRate = response.requiresFlatRate
        item.value.isApplyByVCC = response.isApplyByVCC
        item.value.description = response.description
        listCountryItems.value = [response.manageCountry]
        item.value.manageCountry = { id: response.manageCountry.id, name: `${response.manageCountry.code} - ${response.manageCountry.name}`, status: response.manageCountry.status }
        if (response.manageCountry) {
          listCityStateItems.value = [response.manageCityState]
          item.value.manageCityState = { id: response.manageCityState.id, name: `${response.manageCityState.code} - ${response.manageCityState.name}`, status: response.manageCityState.status }
        }
        listCurrencyItems.value = [response.manageCurrency]
        item.value.manageCurrency = { id: response.manageCurrency.id, name: `${response.manageCurrency.code} - ${response.manageCurrency.name}`, status: response.manageCurrency.status }
        listRegionItems.value = [response.manageRegion]
        item.value.manageRegion = { id: response.manageRegion.id, name: `${response.manageRegion.code} - ${response.manageRegion.name}`, status: response.manageRegion.status }
        if (response.manageTradingCompanies) {
          const objTradingCompany = { id: response.manageTradingCompanies.id, company: `${response.manageTradingCompanies.code} - ${response.manageTradingCompanies.company}`, status: response.manageTradingCompanies.status }
          listTradingCompanyItems.value = [objTradingCompany]
          item.value.manageTradingCompanies = objTradingCompany
        }
      }
      updateFieldProperty(fields, 'code', 'disabled', true)
      updateFieldProperty(fields, 'contact', 'hidden', false)
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Item could not be loaded', life: 3000 })
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
    payload.status = statusToString(payload.status)
    payload.manageCountry = typeof payload.manageCountry === 'object' ? payload.manageCountry.id : payload.manageCountry
    payload.manageCityState = typeof payload.manageCityState === 'object' ? payload.manageCityState.id : payload.manageCityState
    payload.manageCurrency = typeof payload.manageCurrency === 'object' ? payload.manageCurrency.id : payload.manageCurrency
    payload.manageRegion = typeof payload.manageRegion === 'object' ? payload.manageRegion.id : payload.manageRegion
    payload.manageTradingCompanies = payload.manageTradingCompanies && typeof payload.manageTradingCompanies === 'object' ? payload.manageTradingCompanies.id : payload.manageTradingCompanies
    delete payload.event
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.manageCountry = typeof payload.manageCountry === 'object' ? payload.manageCountry.id : payload.manageCountry
  payload.manageCityState = typeof payload.manageCityState === 'object' ? payload.manageCityState.id : payload.manageCityState
  payload.manageCurrency = typeof payload.manageCurrency === 'object' ? payload.manageCurrency.id : payload.manageCurrency
  payload.manageRegion = typeof payload.manageRegion === 'object' ? payload.manageRegion.id : payload.manageRegion
  payload.manageTradingCompanies = !payload.manageTradingCompanies ? '' : typeof payload.manageTradingCompanies === 'object' ? payload.manageTradingCompanies.id : payload.manageTradingCompanies
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
      idItem.value = ''
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

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function getCountriesList(query: string) {
  try {
    const payload = {
      filter: [{
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
      sortType: ENUM_SHORT_TYPE.ASC
    }

    const response = await GenericService.search('settings', 'manage-country', payload)
    const { data: dataList } = response
    listCountryItems.value = []
    for (const iterator of dataList) {
      listCountryItems.value = [...listCountryItems.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading country list:', error)
  }
}

async function getCityStatesList(countryId: string, query: string) {
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
      sortType: ENUM_SHORT_TYPE.ASC
    }

    const response = await GenericService.search('settings', 'manage-city-state', payload)
    const { data: dataList } = response
    listCityStateItems.value = []
    for (const iterator of dataList) {
      listCityStateItems.value = [...listCityStateItems.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading city states list:', error)
  }
}

async function getCurrencyList(query: string) {
  try {
    const payload = {
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
      pageSize: 20,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    const response = await GenericService.search('settings', 'manage-currency', payload)
    const { data: dataList } = response
    listCurrencyItems.value = []
    for (const iterator of dataList) {
      listCurrencyItems.value = [...listCurrencyItems.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading currency list:', error)
  }
}

async function getRegionList(query: string) {
  try {
    const payload = {
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
      pageSize: 20,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    const response = await GenericService.search('settings', 'manage-region', payload)
    const { data: dataList } = response
    listRegionItems.value = []
    for (const iterator of dataList) {
      listRegionItems.value = [...listRegionItems.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading region list:', error)
  }
}
async function getTradingCompanyList(query: string) {
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
      listTradingCompanyItems.value = [...listTradingCompanyItems.value, { id: iterator.id, company: `${iterator.code} - ${iterator.company}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading trading company list:', error)
  }
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})
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
      Manage Hotel
    </h3>
    <IfCan :perms="['HOTEL:CREATE']">
      <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
        <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
      </div>
    </IfCan>
  </div>
  <div class="grid">
    <div class="col-12 md:order-1 md:col-6 xl:col-9">
      <div class="card p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header">
                Filters
              </div>
            </template>
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
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :disabled="disabledClearSearch" :loading="loadingSearch" @click="clearFilterToSearch" />
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
    <div class="col-12 md:order-0 md:col-6 xl:col-3">
      <div>
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          {{ formTitle }}
        </div>
        <div class="card p-4">
          <EditFormV2
            :key="formReload"
            :fields="fields"
            :item="item"
            :show-actions="true"
            :loading-save="loadingSaveAll"
            :loading-delete="loadingDelete"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-manageCountry="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageCountry"
                :suggestions="listCountryItems"
                @change="($event) => {
                  listCityStateItems = []
                  if (typeof $event === 'object' && $event) {
                    getCityStatesList($event.id, '')
                    onUpdate('manageCityState', null)
                  }
                  else {
                    listCityStateItems = []
                  }
                  onUpdate('manageCountry', $event)
                }"
                @load="($event) => getCountriesList($event)"
              />

              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-manageCityState="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageCityState"
                :suggestions="listCityStateItems"
                :disabled="!data.manageCountry"
                @change="($event) => {
                  onUpdate('manageCityState', $event)
                }"
                @load="($event) => getCityStatesList(data.manageCountry.id, $event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-manageCurrency="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageCurrency"
                :suggestions="[...listCurrencyItems]"
                @change="($event) => {
                  onUpdate('manageCurrency', $event)
                }"
                @load="($event) => getCurrencyList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-manageRegion="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageRegion"
                :suggestions="listRegionItems"
                @change="($event) => {
                  onUpdate('manageRegion', $event)
                }"
                @load="($event) => getRegionList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-manageTradingCompanies="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="company"
                item-value="id"
                :model="data.manageTradingCompanies"
                :suggestions="listTradingCompanyItems"
                @change="($event) => {
                  onUpdate('manageTradingCompanies', $event)
                }"
                @load="($event) => getTradingCompanyList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-contact>
              <InputGroup v-if="!loadingSaveAll">
                <InputText placeholder="View Contacts" disabled />
                <Button
                  icon="pi pi-eye" type="button" text aria-haspopup="true" aria-controls="overlay_menu_filter"
                  @click="contactDialogVisible = true"
                />
              </InputGroup>
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #form-footer="props">
              <div class="flex justify-content-end">
                <IfCan :perms="idItem ? ['HOTEL:EDIT'] : ['HOTEL:CREATE']">
                  <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" @click="props.item.submitForm($event)" />
                </IfCan>
                <IfCan :perms="['HOTEL:DELETE']">
                  <Button v-tooltip.top="'Delete'" class="w-3rem" severity="danger" outlined :loading="loadingDelete" icon="pi pi-trash" @click="props.item.deleteItem($event)" />
                </IfCan>
              </div>
            </template>
          </EditFormV2>
          <DynamicContentModal
            :visible="contactDialogVisible" :component="ContactPage" :component-props="{ hotel: { id: item.id, name: item.name, status: 'ACTIVE' } }"
            :header="`Hotel ${item.name}`" @close="contactDialogVisible = $event"
          />
        </div>
      </div>
    </div>
  </div>
</template>
