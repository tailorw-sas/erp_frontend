<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import dayjs from 'dayjs'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import { merchantSchema } from '#imports'

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
  search: '',
  from: null,
  to: null,
})
const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-merchant-commission',
})
const decimalRegex = /^\d+(\.\d+)?$/

const commissionSchema = z.object({
  commission: z
    .string()
    .regex(decimalRegex, { message: 'The commission field must be a valid decimal or integer' })
    .refine(value => Number.parseFloat(value) >= 1, { message: 'The commission field must be at least 1' })
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
    validation: merchantSchema,

  },
  {
    field: 'manageCreditCartType',
    header: 'Credit Card Type',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: creditCardTypeSchema
  },
  {
    field: 'commission',
    header: 'Commission',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    // validation: commissionSchema.shape.commission
    validation: z.preprocess(
      (value) => {
        if (typeof value === 'string') {
          return Number.parseFloat(value)
        }
        return value
      },
      z.custom((value) => {
        const regex = /^-?\d+(\.\d+)?$/
        return typeof value === 'number' && regex.test(`${value}`)
      }, 'The commission field is required and must be a number')

    ),

  },
  {
    field: 'calculationType',
    header: 'Calculation Type',
    dataType: 'select',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'fromDate',
    header: 'From Date',
    dataType: 'date',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The from date field is required',
      invalid_type_error: 'The from date field is required',
    })
  },
  {
    field: 'toDate',
    header: 'To Date',
    dataType: 'date',
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1',
    validation: z.string().trim().max(250, 'Maximum 250 characters')
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mt-3 mb-3',
    headerClass: 'mb-1'
  },
]

const item = ref<GenericObject>({
  managerMerchant: null,
  manageCreditCartType: null,
  commission: null,
  calculationType: null,
  description: '',
  fromDate: '',
  toDate: '',
  status: true
})

const itemTemp = ref<GenericObject>({
  managerMerchant: null,
  manageCreditCartType: null,
  commission: null,
  calculationType: null,
  description: '',
  fromDate: '',
  toDate: '',
  status: true
})

const merchantList = ref<any[]>([])
const confMerchantApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-merchant',
})

const creditCardTypeList = ref<any[]>([])
const confCreditCardTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-credit-card-type',
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})
// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'managerMerchant', header: 'Merchant', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-merchant', keyValue: 'description' }, sortable: true },
  { field: 'manageCreditCartType', header: 'CC Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-credit-card-type', keyValue: 'name' }, sortable: true },
  { field: 'commission', header: 'Commission', type: 'text' },
  { field: 'calculationType', header: 'Calculation Type', type: 'text' },
  { field: 'fromDate', header: 'From', type: 'date' },
  { field: 'toDate', header: 'To', type: 'date' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Merchant Commission',
  moduleApi: 'settings',
  uriApi: 'manage-merchant-commission',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Are you sure you want to delete the Merchant commission {{name}}?'
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
      iterator.managerMerchant = { id: iterator.managerMerchant?.id, name: `${iterator.managerMerchant?.code} ${iterator.managerMerchant?.description ? `- ${iterator.managerMerchant?.description}` : ''}` }
      iterator.manageCreditCartType = { id: iterator.manageCreditCartType?.id, name: `${iterator.manageCreditCartType?.name}` }
      creditCardTypeList.value.find(i => i.id === iterator.manageCreditCartType?.id)

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

  if (filterToSearch.value.from) {
    payload.value.filter = [...payload.value.filter, {
      key: 'fromDate',
      operator: 'GREATER_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  if (filterToSearch.value.to) {
    payload.value.filter = [...payload.value.filter, {
      key: 'toDate',
      operator: 'LESS_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.search = ''
  filterToSearch.value.from = null
  filterToSearch.value.to = null
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
        merchantList.value = [objMerchant]
        item.value.id = response.id
        item.value.managerMerchant = merchantList.value.find(i => i.id === response.managerMerchant.id)
        item.value.commission = response.commission
        item.value.calculationType = ENUM_CALCULATION_TYPE.find(i => i.id === response.calculationType)
        item.value.description = response.description
        item.value.status = statusToBoolean(response.status)
        const newDate = new Date(response.fromDate)
        newDate.setDate(newDate.getDate() + 1)
        item.value.fromDate = newDate || null
        item.value.toDate = response.toDate ? dayjs(response.toDate).format('YYYY-MM-DD') : null

        if (response.manageCreditCartType) {
          item.value.manageCreditCartType = {
            id: response.manageCreditCartType.id,
            name: `${response.manageCreditCartType.code} - ${response.manageCreditCartType.name}`,
            status: response.manageCreditCartType.status
          }
        }
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Merchant Commission methods could not be loaded', life: 3000 })
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

    payload.fromDate = payload.fromDate ? dayjs(payload.fromDate).format('YYYY-MM-DD') : ''
    payload.toDate = payload.toDate ? dayjs(payload.toDate).format('YYYY-MM-DD') : ''
    payload.managerMerchant = typeof payload.managerMerchant === 'object' ? payload.managerMerchant.id : payload.managerMerchant
    payload.manageCreditCartType = typeof payload.manageCreditCartType === 'object' ? payload.manageCreditCartType.id : payload.manageCreditCartType
    payload.calculationType = payload.calculationType?.id
    payload.status = statusToString(payload.status)
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }

  payload.fromDate = payload.fromDate ? dayjs(payload.fromDate).format('YYYY-MM-DD') : ''
  payload.toDate = payload.toDate ? dayjs(payload.toDate).format('YYYY-MM-DD') : ''
  payload.managerMerchant = typeof payload.managerMerchant === 'object' ? payload.managerMerchant.id : payload.managerMerchant
  payload.manageCreditCartType = typeof payload.manageCreditCartType === 'object' ? payload.manageCreditCartType.id : payload.manageCreditCartType
  payload.calculationType = payload.calculationType?.id
  payload.status = statusToString(payload.status)
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
  toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
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
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
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

async function getCreditCardTypeList(query: string = '') {
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
          sortBy: 'code',
          sortType: ENUM_SHORT_TYPE.DESC
        }

    const response = await GenericService.search(confCreditCardTypeApi.moduleApi, confCreditCardTypeApi.uriApi, payload)
    const { data: dataList } = response
    creditCardTypeList.value = []
    for (const iterator of dataList) {
      creditCardTypeList.value = [...creditCardTypeList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading creditCardType list:', error)
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

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.from || filterToSearch.value.to)
})

async function getMerchantList(query: string) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'description',
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
          sortBy: 'description',
          sortType: ENUM_SHORT_TYPE.DESC
        }

    const response = await GenericService.search(confMerchantApi.moduleApi, confMerchantApi.uriApi, payload)
    const { data: dataList } = response
    merchantList.value = []
    for (const iterator of dataList) {
      merchantList.value = [...merchantList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.description}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading languages list:', error)
  }
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
onMounted(async () => {
  if (useRuntimeConfig().public.loadTableData) {
    await getList()
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h5 class="mb-0">
      Manage Merchant Commission
    </h5>
    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 order-0 md:order-1 md:col-6 xl:col-9">
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
                <label for="email">From</label>
                <Calendar v-model="filterToSearch.from" :max-date="filterToSearch.to ? filterToSearch.to : null" date-format="yy-mm-dd" type="date" />
                <label for="email">To</label>
                <Calendar v-model="filterToSearch.to" :min-date="filterToSearch.from" date-format="yy-mm-dd" type="date" />
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
            @update:item="($event) => item = $event"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-managerMerchant="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.managerMerchant"
                :suggestions="merchantList"
                @change="($event) => {
                  onUpdate('managerMerchant', $event)
                }"
                @load="($event) => getMerchantList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-calculationType="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll"
                v-model="data.calculationType"
                :options="[...ENUM_CALCULATION_TYPE]"
                option-label="name"
                return-object="false"
                show-clear
                @update:model-value="($event) => {
                  onUpdate('calculationType', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-manageCreditCartType="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageCreditCartType"
                :suggestions="[...creditCardTypeList]"
                @change="($event) => {
                  onUpdate('manageCreditCartType', $event)
                }"
                @load="($event) => getCreditCardTypeList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-fromDate="{ item: data, onUpdate }">
              <Calendar
                v-if="!loadingSaveAll"
                v-model="data.fromDate"
                date-format="yy-mm-dd"
                :min-date="new Date('2020-01-01')"
                :max-date="data.toDate ? new Date(data.toDate) : undefined"
                @update:model-value="($event) => {
                  onUpdate('fromDate', $event)
                }"
              />
              <Skeleton v-else height="2rem" />
            </template>

            <template #field-toDate="{ item: data, onUpdate }">
              <Calendar
                v-if="!loadingSaveAll"
                v-model="data.toDate"
                date-format="yy-mm-dd"
                :min-date="data.fromDate ? new Date(data.fromDate) : new Date('2020-01-01')"
                @update:model-value="($event) => {
                  onUpdate('toDate', $event)
                }"
              />
              <Skeleton v-else height="2rem" />
              <small id="field-toDate-help"> To date empty is equal to No Date </small>
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
  </div>
</template>
