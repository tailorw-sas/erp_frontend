<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { ENUM_STATUS } from '~/utils/Enums'
import type { IData } from '~/components/table/interfaces/IModelData'
import { getEventFromTable } from '~/utils/helpers'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const formReload = ref(0)
const refRemarkRequired: Ref = ref(null)

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
  uriApi: 'manage-vcc-transaction-type',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    dataType: 'code',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(10, 'Maximum 10 characters').regex(/^[a-z]+$/i, 'Only text characters allowed')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'isDefault',
    header: 'Default',
    dataType: 'check',
    class: 'field col-12 required mt-3',
  },
  {
    field: 'negative',
    header: 'Negative',
    dataType: 'check',
    class: 'field col-12 required',
  },
  {
    field: 'policyCredit',
    header: 'Policy Credit',
    dataType: 'check',
    class: 'field col-12 required',
  },
  {
    field: 'subcategory',
    header: 'Subcategory',
    dataType: 'check',
    class: 'field col-12 required',
  },
  {
    field: 'onlyApplyNet',
    header: 'Only Apply Net Amount (VCC Module)',
    dataType: 'check',
    class: 'field col-12 required',
  },
  {
    field: 'remarkRequired',
    header: 'Remark Required',
    dataType: 'check',
    class: 'field col-12 required mb-3',
  },
  {
    field: 'minNumberOfCharacter',
    header: 'Min Number of Character',
    dataType: 'number',
    class: 'field col-12',
    headerClass: 'mb-1',
    disabled: true
  },
  {
    field: 'defaultRemark',
    header: 'Default Remark',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1'
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12 mt-3',
    headerClass: 'mb-1',
    validation: z.string().trim().max(250, 'Maximum 250 characters')
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
  name: '',
  code: '',
  status: true,
  isDefault: false,
  negative: false,
  policyCredit: false,
  remarkRequired: false,
  subcategory: false,
  onlyApplyNet: false,
  minNumberOfCharacter: 0,
  defaultRemark: '',
  description: '',
})

const itemTemp = ref<GenericObject>({
  name: '',
  code: '',
  status: true,
  isDefault: false,
  negative: false,
  policyCredit: false,
  remarkRequired: false,
  subcategory: false,
  onlyApplyNet: false,
  minNumberOfCharacter: 0,
  defaultRemark: '',
  description: '',
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'code', header: 'Code', type: 'text' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage VCC Transaction Type',
  moduleApi: 'settings',
  uriApi: 'manage-vcc-transaction-type',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Are you sure you want to delete the VCC Transaction Type: {{name}}?'
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
  refRemarkRequired.value.$emit('update:model-value', false)
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
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.name = response.name
        item.value.description = response.description
        item.value.status = statusToBoolean(response.status)
        item.value.code = response.code
        item.value.negative = response.negative
        item.value.isDefault = response.isDefault
        item.value.subcategory = response.subcategory
        item.value.onlyApplyNet = response.onlyApplyNet
        item.value.policyCredit = response.policyCredit
        item.value.remarkRequired = response.remarkRequired
        item.value.minNumberOfCharacter = response.minNumberOfCharacter
        item.value.defaultRemark = response.defaultRemark

        updateFieldProperty(fields, 'minNumberOfCharacter', 'disabled', !response.remarkRequired)
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'VCC Transaction Type methods could not be loaded', life: 1000 })
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
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 1000 })
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
  console.log(payload)

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
      Manage VCC Transaction Type
    </h3>
    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 order-0 md:order-1 md:col-6 xl:col-9">
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
            <!-- <div class="col-12 md:col-3 sm:mb-2 flex align-items-center">
            </div> -->
            <!-- <div class="col-12 md:col-5 flex justify-content-end">
            </div> -->
          </div>
        </AccordionTab>
      </Accordion>
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
            <template #field-remarkRequired="{ item: data, onUpdate }">
              <Checkbox
                ref="refRemarkRequired"
                v-model="data.remarkRequired"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('remarkRequired', $event)
                  onUpdate('minNumberOfCharacter', 0)
                  updateFieldProperty(fields, 'minNumberOfCharacter', 'disabled', !$event)
                }"
              />
              <label for="remarkRequired" class="ml-2">
                Remark Required
                <span :class="fields.find(field => field.field === 'remarkRequired')?.class.includes('required') ? 'p-error font-semibold' : ''" />
              </label>
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
    <Toast position="top-center" :base-z-index="5" group="tc" />
  </div>
</template>
