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
import { validateEntityStatus } from '~/utils/schemaValidations'
import { statusToBoolean } from '~/utils/helpers'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const loadingSearch = ref(false)
const formReload = ref(0)

const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'manage-merchant-currency',
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'managerMerchant',
    header: 'Merchant',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('merchant'),
  },
  {
    field: 'managerCurrency',
    header: 'Currency',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('currency'),
  },
  {
    field: 'value',
    header: 'Value',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string()
      .min(1, 'The value field is required'),
    // .min(1, 'The value field is required').regex(/^\d+(\.\d+)?$/, 'Only numeric characters allowed'),
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12 ',
    headerClass: 'mb-1',
    validation: z.string().trim().max(250, 'Maximum 250 characters')
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mt-3 mb-3',
    headerClass: 'mb-1',
  },
]

const item = ref<GenericObject>({
  managerMerchant: null,
  managerCurrency: null,
  description: '',
  value: 0,
  status: true,
})

const itemTemp = ref<GenericObject>({
  managerMerchant: null,
  managerCurrency: null,
  description: '',
  value: 0,
  status: true,
})

const MerchantList = ref<any[]>([])

const CurrencyList = ref<any[]>([])
// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'managerMerchant', name: 'Merchant' },
  { id: 'value', name: 'Value' },
]

const columns: IColumn[] = [
  { field: 'managerMerchant', header: 'Merchant', type: 'select', objApi: { moduleApi: 'creditcard', uriApi: 'manage-merchant', keyValue: 'description' }, sortable: true },
  { field: 'managerCurrency', header: 'Currency', type: 'select', localItems: [], objApi: { moduleApi: 'settings', uriApi: 'manage-currency' }, sortable: true },
  { field: 'value', header: 'Value', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Merchant Currency',
  moduleApi: 'creditcard',
  uriApi: 'manage-merchant-currency',
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
  sortBy: 'id',
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
  fields[0].disabled = false
  fields[1].disabled = false
  updateFieldProperty(fields, 'status', 'disabled', true)
  formReload.value++
  MerchantList.value = []
}

async function getList(loadFirstItem: boolean = true) {
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
      iterator.value = iterator.value ? iterator.value.toString() : '0'
      iterator.managerMerchant = { id: iterator.managerMerchant.id, name: iterator.managerMerchant.description }
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

    if (listItems.value.length > 0 && loadFirstItem) {
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
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        const objMerchant = {
          id: response.managerMerchant.id,
          name: `${response.managerMerchant.code} ${response.managerMerchant.description ? `- ${response.managerMerchant.description}` : ''}`,
          status: response.managerMerchant.status
        }
        item.value.id = response.id
        item.value.value = String(response.value)
        item.value.description = response.description
        MerchantList.value = [objMerchant]
        item.value.managerMerchant = objMerchant
        item.value.managerCurrency = CurrencyList.value.find(i => i.id === response.managerCurrency.id)
        item.value.status = statusToBoolean(response.status)
      }
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Merchant could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}
function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: (filterToSearch.value.criterial && filterToSearch.value.criterial.id === 'managerMerchant') ? `${filterToSearch.value.criterial.id}.description` : (filterToSearch.value.criterial.id || ''),
      operator: 'LIKE',
      value: filterToSearch.value.search || '',
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
  await getList()
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.managerCurrency = typeof payload.managerCurrency === 'object' ? payload.managerCurrency.id : payload.managerCurrency
    payload.managerMerchant = typeof payload.managerMerchant === 'object' ? payload.managerMerchant.id : payload.managerMerchant
    payload.status = statusToString(payload.status)
    return await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.managerCurrency = typeof payload.managerCurrency === 'object' ? payload.managerCurrency.id : payload.managerCurrency
  payload.managerMerchant = typeof payload.managerMerchant === 'object' ? payload.managerMerchant.id : payload.managerMerchant
  payload.status = statusToString(payload.status)
  return await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
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
  let response: any
  if (idItem.value) {
    try {
      response = await updateItem(item)
      idItem.value = ''
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: 'Item could not be updated', life: 10000 })
    }
  }
  else {
    try {
      response = await createItem(item)
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
    await getList(false)
    if (response) {
      idItemToLoadFirstTime.value = response.id
    }
  }
}

async function getCurrencyList(query: string = '') {
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
        }

    const response = await GenericService.search('settings', 'manage-currency', payload)
    const { data: dataList } = response
    CurrencyList.value = []
    for (const iterator of dataList) {
      CurrencyList.value = [...CurrencyList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading  manage currency list:', error)
  }
}

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
    const response = await GenericService.search('creditcard', 'manage-merchant', payload)
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
// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
    document.title = 'Manage Merchant Currency'
  }
  getCurrencyList()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center mb-1">
    <h5 class="mb-0">
      Manage Merchant Currency
    </h5>
    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="flex justify-content-end px-0">
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 md:order-1 md:col-6 xl:col-9">
      <div class="card p-0 mb-0">
        <Accordion :active-index="0" class="mb-2 bg-white">
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
                :suggestions="[...CurrencyList]"
                @change="($event) => {
                  onUpdate('managerCurrency', $event)
                }"
                @load="($event) => getCurrencyList($event)"
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
