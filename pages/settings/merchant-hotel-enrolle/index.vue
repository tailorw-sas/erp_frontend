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
import type { IData } from '~/components/table/interfaces/IModelData'
import { statusToBoolean, statusToString } from '~/utils/helpers'
import { validateEntityStatus } from '~/utils/schemaValidations'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const formReload = ref(0)
const loadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const loadingDelete = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-merchant-hotel-enrolle',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'managerMerchant',
    header: 'Merchant',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: validateEntityStatus('merchant'),
  },
  {
    field: 'managerHotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'managerCurrency',
    header: 'Currency',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: validateEntityStatus('currency'),
  },
  {
    field: 'enrrolle',
    header: 'Enrolle',
    disabled: false,
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string()
      .max(20, 'The enrolle field must be a string of up to 20 characters')
      .min(1, 'The enrolle field is required')
  },
  {
    field: 'key',
    header: 'Key',
    disabled: false,
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string()
      .min(1, 'The key field is required')
      .max(8000, 'The key field must be a string of up to 8000 characters')
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    validation: z.string().max(250, 'Maximum 250 characters')
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mt-3 mb-3',
  },
]

const item = ref<GenericObject>({
  managerMerchant: null,
  managerCurrency: null,
  managerHotel: null,
  enrrolle: '',
  key: '',
  description: '',
  status: true,
})

const itemTemp = ref<GenericObject>({
  managerMerchant: null,
  managerCurrency: null,
  managerHotel: null,
  enrrolle: '',
  key: '',
  description: '',
  status: true,
})

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'managerMerchant', name: 'Merchant' },
  { id: 'managerHotel', name: 'Hotel' },
  { id: 'enrrolle', name: 'Enrolle Code' },
]

const columns: IColumn[] = [
  { field: 'managerMerchant', header: 'Merchant', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-merchant', keyValue: 'description' }, sortable: true },
  { field: 'managerHotel', header: 'Hotel', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel', keyValue: 'name' }, sortable: true },
  { field: 'enrrolle', header: 'Enrolle', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Merchant Hotel Enrolle',
  moduleApi: 'settings',
  uriApi: 'manage-merchant-hotel-enrolle',
  loading: false,
  showDelete: false,
  showFilters: true,
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
const MerchantList = ref<any[]>([])
const HotelList = ref<any[]>([])
const CurrencyList = ref<any[]>([])

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})
// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fields[0].disabled = false
  updateFieldProperty(fields, 'status', 'disabled', true)
  formReload.value++
  MerchantList.value = []
  HotelList.value = []
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
      const obj = {
        managerMerchant: { id: iterator.managerMerchant.id, name: `${iterator.managerMerchant.code} - ${iterator.managerMerchant.description}` },
        managerHotel: { id: iterator.managerHotel.id, name: `${iterator.managerHotel.code} - ${iterator.managerHotel.name}` },
        managerCurrency: { id: iterator.managerCurrency.id, name: iterator.managerCurrency.description },
        id: iterator.id,
        enrrolle: iterator.enrrolle,
        key: iterator.key,
        description: iterator.description,
        status: Object.prototype.hasOwnProperty.call(iterator, 'status') ? statusToBoolean(iterator.status) : '',
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...obj, loadingEdit: false, loadingDelete: false })
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
  payload.value.filter = payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')

  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    let key
    if (filterToSearch.value.criterial.id === 'managerMerchant') {
      key = 'managerMerchant.description'
    }
    else if (filterToSearch.value.criterial.id === 'managerHotel') {
      key = 'managerHotel.name'
    }
    else {
      key = filterToSearch.value.criterial.id
    }

    payload.value.filter.push({
      key,
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    })
  }
  getList()
}

function clearFilterToSearch() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
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
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        const objMerchant = {
          id: response.managerMerchant.id,
          name: `${response.managerMerchant.code} ${response.managerMerchant.description ? `- ${response.managerMerchant.description}` : ''}`,
          status: response.managerMerchant.status
        }
        item.value.id = response.id
        item.value.enrrolle = response.enrrolle
        item.value.key = response.key
        item.value.description = response.description
        item.value.status = statusToBoolean(response.status)
        MerchantList.value = [objMerchant]
        item.value.managerMerchant = objMerchant
        CurrencyList.value = [response.managerCurrency]
        item.value.managerCurrency = response.managerCurrency
        HotelList.value = [response.managerHotel]
        item.value.managerHotel = response.managerHotel
      }
      fields[0].disabled = true
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
    payload.managerCurrency = typeof payload.managerCurrency === 'object' ? payload.managerCurrency.id : payload.managerCurrency
    payload.managerMerchant = typeof payload.managerMerchant === 'object' ? payload.managerMerchant.id : payload.managerMerchant
    payload.managerHotel = typeof payload.managerHotel === 'object' ? payload.managerHotel.id : payload.managerHotel
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.managerCurrency = typeof payload.managerCurrency === 'object' ? payload.managerCurrency.id : payload.managerCurrency
  payload.managerMerchant = typeof payload.managerMerchant === 'object' ? payload.managerMerchant.id : payload.managerMerchant
  payload.managerHotel = typeof payload.managerHotel === 'object' ? payload.managerHotel.id : payload.managerHotel
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

// List

async function getMerchantList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'description',
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
    }
    const response = await GenericService.search('settings', 'manage-merchant', payload)
    const { data: dataList } = response
    MerchantList.value = []

    for (const iterator of dataList) {
      MerchantList.value = [...MerchantList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.description}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading merchant list:', error)
  }
}

async function getCurrencyList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'name',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'AND'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
    }

    const response = await GenericService.search('settings', 'manage-currency', payload)
    const { data: dataList } = response
    CurrencyList.value = []
    for (const iterator of dataList) {
      CurrencyList.value = [...CurrencyList.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading  manage currency list:', error)
  }
}

async function getHotelList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'name',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'AND'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
    }

    const response = await GenericService.search('settings', 'manage-hotel', payload)
    const { data: dataList } = response
    HotelList.value = []
    for (const iterator of dataList) {
      HotelList.value = [...HotelList.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading  manage hotel list:', error)
  }
}
// Messages
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

// FILTER -------------------------------------------------------------------------------------------------------
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

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})

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
      Manage Merchant Hotel Enrolle
    </h3>
    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
    </div>
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
                    v-model="filterToSearch.criterial" :options="[...ENUM_FILTER]" option-label="name"
                    placeholder="Criteria" return-object="false" class="align-items-center w-full" show-clear
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
                <Button
                  v-tooltip.top="'Search'" :disabled="disabledSearch" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearch"
                  @click="searchAndFilter"
                />
                <Button
                  v-tooltip.top="'Clear'" outlined class="w-3rem" :disabled="disabledClearSearch" icon="pi pi-filter-slash" :loading="loadingSearch"
                  @click="clearFilterToSearch"
                />
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
        <div class="card">
          <EditFormV2
            :key="formReload" :fields="fields" :item="item" :show-actions="true"
            :loading-save="loadingSaveAll" :loading-delete="loadingDelete" @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)" @submit="requireConfirmationToSave($event)"
          >
            <template #field-managerMerchant="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.managerMerchant"
                :suggestions="MerchantList"
                @change="($event) => {
                  onUpdate('managerMerchant', $event)
                }"
                @load="($event) => getMerchantList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-managerCurrency="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.managerCurrency"
                :suggestions="CurrencyList"
                @change="($event) => {
                  onUpdate('managerCurrency', $event)
                }"
                @load="($event) => getCurrencyList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-managerHotel="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.managerHotel"
                :suggestions="HotelList"
                @change="($event) => {
                  onUpdate('managerHotel', $event)
                }"
                @load="($event) => getHotelList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
    <Toast position="top-center" :base-z-index="5" group="tc" />
  </div>
</template>
