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
  moduleApi: 'settings',
  uriApi: 'manage-bank-account',
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'accountNumber',
    header: 'Account Number',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    disabled: false,
    validation: z.string().trim().min(1, 'The account number field is required').max(20, 'Maximum 20 characters').regex(/^\d+$/, 'Only numeric characters allowed')
  },
  {
    field: 'manageBank',
    header: 'Bank',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('bank'),
  },
  {
    field: 'manageHotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'manageAccountType',
    header: 'Account Type',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('account type'),
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1',
    validation: z.string().trim().max(250, 'Maximum 250 characters'),
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
  manageHotel: null,
  manageAccountType: null,
  manageBank: null,
  description: '',
  accountNumber: '',
  status: true,
})

const itemTemp = ref<GenericObject>({
  manageHotel: null,
  manageAccountType: null,
  manageBank: null,
  description: '',
  accountNumber: '',
  status: true,
})

const HotelList = ref<any[]>([])

const BankList = ref<any[]>([])

const AccountTypeList = ref<any[]>([])
// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  // { id: 'description', name: 'Description' },
  { id: 'accountNumber', name: 'Account Number' },
]

const columns: IColumn[] = [
  { field: 'accountNumber', header: 'Account Number', type: 'text' },
  { field: 'manageBank', header: 'Bank', type: 'select', localItems: [], objApi: { moduleApi: 'settings', uriApi: 'manage-bank' }, sortable: true },
  { field: 'manageHotel', header: 'Hotel', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' }, sortable: true },
  { field: 'manageAccountType', header: 'Account Type', type: 'select', localItems: [], objApi: { moduleApi: 'settings', uriApi: 'manage-account-type' }, sortable: true },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Bank Account',
  moduleApi: 'settings',
  uriApi: 'manage-bank-account',
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

// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fields[0].disabled = false
  fields[1].disabled = false
  updateFieldProperty(fields, 'status', 'disabled', true)
  formReload.value++
  HotelList.value = []
  BankList.value = []
  AccountTypeList.value = []
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
      if (Object.prototype.hasOwnProperty.call(iterator, 'manageBank')) {
        iterator.manageBank = { id: iterator.manageBank.id, name: iterator.manageBank.name }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'manageHotel')) {
        iterator.manageHotel = { id: iterator.manageHotel.id, name: `${iterator.manageHotel.code} - ${iterator.manageHotel.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'manageAccountType')) {
        iterator.manageAccountType = { id: iterator.manageAccountType.id, name: iterator.manageAccountType.name }
      }
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
        item.value.id = response.id
        item.value.description = response.description
        item.value.accountNumber = response.accountNumber
        if (response.manageHotel) {
          item.value.manageHotel = { id: response.manageHotel.id, name: `${response.manageHotel.code} - ${response.manageHotel.name}`, status: response.manageHotel.status }
        }
        if (response.manageBank) {
          item.value.manageBank = { id: response.manageBank.id, name: `${response.manageBank.code} - ${response.manageBank.name}`, status: response.manageBank.status }
        }
        if (response.manageAccountType) {
          item.value.manageAccountType = { id: response.manageAccountType.id, name: `${response.manageAccountType.code} - ${response.manageAccountType.name}`, status: response.manageAccountType.status }
        }
        item.value.status = statusToBoolean(response.status)
      }
      fields[0].disabled = true
      fields[1].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Merchant Bank Account could not be loaded', life: 3000 })
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
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
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
    payload.status = statusToString(payload.status)
    payload.manageBank = typeof payload.manageBank === 'object' ? payload.manageBank.id : payload.manageBank
    payload.manageHotel = typeof payload.manageHotel === 'object' ? payload.manageHotel.id : payload.manageHotel
    payload.manageAccountType = typeof payload.manageAccountType === 'object' ? payload.manageAccountType.id : payload.manageAccountType
    delete payload.event
    return await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.manageBank = typeof payload.manageBank === 'object' ? payload.manageBank.id : payload.manageBank
  payload.manageHotel = typeof payload.manageHotel === 'object' ? payload.manageHotel.id : payload.manageHotel
  payload.manageAccountType = typeof payload.manageAccountType === 'object' ? payload.manageAccountType.id : payload.manageAccountType
  delete payload.event
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

async function getBankList(query: string) {
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
          pageSize: 20,
          page: 0,
          sortBy: 'name',
          sortType: ENUM_SHORT_TYPE.ASC
        }

    const response = await GenericService.search('settings', 'manage-bank', payload)

    const { data: dataList } = response
    BankList.value = []
    for (const iterator of dataList) {
      BankList.value = [...BankList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading manage bank list:', error)
  }
}

async function getAccountTypeList(query: string = '') {
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
      pageSize: 200,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }
    const response = await GenericService.search('settings', 'manage-account-type', payload)
    const { data: dataList } = response
    AccountTypeList.value = []

    for (const iterator of dataList) {
      AccountTypeList.value = [...AccountTypeList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading account type list:', error)
  }
}

async function getHotelList(query: string) {
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
    const response = await GenericService.search('settings', 'manage-hotel', payload)
    const { data: dataList } = response
    HotelList.value = []

    for (const iterator of dataList) {
      HotelList.value = [...HotelList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
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
    if (event.sortField === 'manageAccountType') {
      event.sortField = 'manageAccountType.name'
    }
    if (event.sortField === 'manageBank') {
      event.sortField = 'manageBank.name'
    }
    if (event.sortField === 'manageHotel') {
      event.sortField = 'manageHotel.name'
    }

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
    document.title = 'Manage Bank Account'
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center mb-1">
    <h5 class="mb-0">
      Manage Bank Account
    </h5>
    <IfCan :perms="['BANK-ACCOUNT:CREATE']">
      <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="flex justify-content-end px-0">
        <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
      </div>
    </IfCan>
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
                  v-tooltip.top="'Search'" class="w-3rem mx-2" :disabled="disabledSearch" icon="pi pi-search" :loading="loadingSearch"
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
            <template #field-manageBank="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageBank"
                :suggestions="BankList"
                @change="($event) => {
                  onUpdate('manageBank', $event)
                }"
                @load="($event) => getBankList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-manageHotel="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageHotel"
                :suggestions="HotelList"
                @change="($event) => {
                  onUpdate('manageHotel', $event)
                }"
                @load="($event) => getHotelList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-manageAccountType="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageAccountType"
                :suggestions="[...AccountTypeList]"
                @change="($event) => {
                  onUpdate('manageAccountType', $event)
                }"
                @load="($event) => getAccountTypeList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #form-footer="props">
              <div class="flex justify-content-end">
                <IfCan :perms="idItem ? ['BANK-ACCOUNT:EDIT'] : ['BANK-ACCOUNT:CREATE']">
                  <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" @click="props.item.submitForm($event)" />
                </IfCan>
                <IfCan :perms="['BANK-ACCOUNT:DELETE']">
                  <Button v-tooltip.top="'Delete'" class="w-3rem" severity="danger" outlined :loading="loadingDelete" icon="pi pi-trash" @click="props.item.deleteItem($event)" />
                </IfCan>
              </div>
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
    <Toast position="top-center" :base-z-index="5" group="tc" />
  </div>
</template>
