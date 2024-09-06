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
import { statusToBoolean, statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const navigateListItems = ref<any[]>([])
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
  uriApi: 'parameterization',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'transactionStatusCode',
    header: 'Default Transaction Status Code',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required')
  },
  {
    field: 'transactionCategory',
    header: 'Default Transaction Category Code',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required')
  },
  {
    field: 'transactionSubCategory',
    header: 'Default Transaction Subcategory Code',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required')
  },
  {
    field: 'refundTransactionStatusCode',
    header: 'Default Transaction Refund Code',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required')
  },
]

const item = ref<GenericObject>({
  id: '',
  isActive: '',
  transactionStatusCode: '',
  transactionCategory: '',
  transactionSubCategory: '',
  refundTransactionStatusCode: '',
})

const itemTemp = ref<GenericObject>({
  id: '',
  isActive: '',
  transactionStatusCode: '',
  transactionCategory: '',
  transactionSubCategory: '',
  refundTransactionStatusCode: '',
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]

const columns: IColumn[] = [
  { field: 'transactionStatusCode', header: 'Transaction Status', type: 'text' },
  { field: 'transactionCategory', header: 'Transaction Category', type: 'text' },
  { field: 'transactionSubCategory', header: 'Transaction Subcategory', type: 'text' },
  { field: 'refundTransactionStatusCode', header: 'Refund Status Code', type: 'text' },
]

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'VCC Parameterization',
  moduleApi: 'creditcard',
  uriApi: 'parameterization',
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

    const response = await GenericService.get(options.value.moduleApi, options.value.uriApi)

    pagination.value.page = 0
    pagination.value.limit = 1
    pagination.value.totalElements = 1
    pagination.value.totalPages = 1

    listItems.value = [response]

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

async function getForSelectNavigateList(id: string = '') {
  try {
    navigateListItems.value = []
    const payload = {
      filter: [
        {
          key: 'name',
          operator: 'LIKE',
          value: id,
          logicalOperation: 'OR'
        },
        {
          key: 'code',
          operator: 'LIKE',
          value: id,
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
    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload)
    const { data: dataList } = response

    navigateListItems.value = dataList
      .filter((item: any) => item.id !== id)
      .map((item: any) => ({
        id: item.id,
        name: `${item.code} - ${item.name}`,
        status: item.status
      }))
  }
  catch (error) {
    console.error(error)
  }
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
      const response = await GenericService.get(confApi.moduleApi, confApi.uriApi)
      if (response) {
        item.value.id = response.id
        item.value.transactionStatusCode = response.transactionStatusCode
        item.value.transactionCategory = response.transactionCategory
        item.value.transactionSubCategory = response.transactionSubCategory
        item.value.refundTransactionStatusCode = response.refundTransactionStatusCode
      }
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
    payload.navigate = payload.navigate.map((p: any) => p.id)
    delete payload.event
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  delete payload.event
  await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
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
      VCC Parameterization
    </h3>
  </div>
  <div class="grid">
    <div class="col-12 md:order-1 md:col-6 xl:col-9">
      <DynamicTable
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="clearForm"
        @update:clicked-item="getItemById($event)"
        @open-edit-dialog="getItemById($event)"
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
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-navigate="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :multiple="true"
                :model="data.navigate"
                :suggestions="[...navigateListItems]"
                @change="($event) => {
                  onUpdate('navigate', $event)
                }"
                @load="($event) => getForSelectNavigateList($event)"
              />

              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #form-footer="props">
              <div class="flex justify-content-end">
                <IfCan :perms="idItem ? ['INVOICE-STATUS:EDIT'] : ['INVOICE-STATUS:CREATE']">
                  <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" @click="props.item.submitForm($event)" />
                </IfCan>
              </div>
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
  </div>
</template>
