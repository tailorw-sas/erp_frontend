<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IData } from '~/components/table/interfaces/IModelData'
import { statusToBoolean, statusToString, updateFieldProperty } from '~/utils/helpers'
// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const listModulesItems = ref<any[]>([])
const formReload = ref(0)
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')

const loadingSearch = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]

const confApi = reactive({
  moduleApi: 'identity',
  uriApi: 'permission',
})
const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    dataType: 'text',
    class: 'col-12 required',
    disabled: true,
    validation: z.string().trim().min(3, 'This is a required field').max(50, 'Maximum 50 characters')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'isHighRisk',
    header: 'High Risk',
    dataType: 'check',
    class: 'field col-12 mt-3',
    headerClass: 'mb-1',
  },
  {
    field: 'isIT',
    header: 'IT',
    dataType: 'check',
    class: 'field col-12 mb-3',
    headerClass: 'mb-1',
  },
  {
    field: 'moduleId',
    header: 'Module',
    dataType: 'select',
    class: 'col-12 required',
    disabled: true,
    hidden: false,
    validation: validateEntityStatus('modules'),
  },
  {
    field: 'action',
    header: 'Action',
    dataType: 'select',
    class: 'col-12 required ',
    disabled: true,
    hidden: false,
    validation: z.object({
      id: z.string().min(1, 'This is a required field'),
      name: z.string().min(1, 'This is a required field'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'This is a required field' }),
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'col-12 ',
    validation: z.string().trim().max(150, 'Maximum 150 characters')
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    class: 'field col-12 mt-3 mb-3',
    headerClass: 'mb-1',
    disabled: true,
  },

]

const item = ref<GenericObject>({
  moduleId: null,
  action: null,
  code: '',
  name: '',
  isHighRisk: false,
  isIT: false,
  status: true,
  description: '',
})

const itemTemp = ref<GenericObject>({
  moduleId: null,
  action: null,
  code: '',
  name: '',
  isHighRisk: false,
  isIT: false,
  status: true,
  description: '',
})

// TABLE COLUMNS -----------------------------------------------------------------------------------------

/* const columns = ref<IColumn[]>([
  {
    field: 'code',
    header: 'Code',
    type: 'text',
    sortable: true
  },
  {
    field: 'moduleName',
    header: 'Module',
    type: 'select',
    localItems: [],
    sortable: true,
    objApi: {
      moduleApi: 'identity',
      uriApi: 'module',
    }
  },

  {
    field: 'action',
    header: 'Action',
    type: 'local-select',
    localItems: ENUM_PERMISSIONS,
    sortable: true
  },
  {
    field: 'description',
    header: 'Description',
    type: 'text'
  },
  {
    field: 'createdAt',
    header: 'Created At',
    type: 'date',
    sortable: true
  },
]) */
const columns: IColumn[] = [
  { field: 'code', header: 'Code', type: 'text' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Permission',
  moduleApi: 'identity',
  uriApi: 'permission',
  loading: false,
  search: false,
  actionsAsMenu: false,
  showDelete: false,
  messageToDelete: 'Are you sure you want to delete {{code}}?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'code',
  sortType: 'ASC'
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
async function getList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      if (iterator.hasOwnProperty('status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      listItems.value = [...listItems.value, { ...iterator, loadingEdit: false, loadingDelete: false }]
    }
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

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  updateFieldProperty(fields, 'status', 'disabled', true)
  formReload.value++
}
async function getModulesList(query: string = '') {
  try {
    const payload
        = {
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
          page: 0
        }

    const response = await GenericService.search('identity', 'module', payload)
    const { data: dataList } = response
    listModulesItems.value = []
    for (const iterator of dataList) {
      listModulesItems.value = [...listModulesItems.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error to get modules:', error)
  }
}

/* async function getModulesList() {
  try {
    const payload = {
      filter: [],
      query: '',
      pageSize: 200,
      page: 0
    };

    listModulesItems.value = [];
    const response = await GenericService.search('identity', 'module', payload);
    const { data: dataList } = response;

    // Create a Set to track unique names
    const uniqueNames = new Set();

    for (const iterator of dataList) {
      const moduleName = `${iterator.code} - ${iterator.name}` // Generate the name

      if (!uniqueNames.has(moduleName)) {
        uniqueNames.add(moduleName); // Add the name to the set
        listModulesItems.value = [
          ...listModulesItems.value,
          { id: iterator.id, name: moduleName }
        ]; // Add the module to the array
      }
    }
  } catch (error) {
    console.error('Error to get modules:', error);
  }
}
*/

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = response.id
        listModulesItems.value = [response.module]
        item.value.moduleId = response.module
        item.value.action = ENUM_PERMISSIONS.find(item => item.id === response.action)
        item.value.description = response.description
        item.value.code = response.code
        item.value.name = response.name
        item.value.isHighRisk = response.isHighRisk
        item.value.status = statusToBoolean(response.status)
        item.value.isIT = response.isIT
      }
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Could not load module data', life: 3000 })
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
    payload.moduleId = typeof payload.moduleId === 'object' ? payload.moduleId.id : payload.moduleId
    payload.action = typeof payload.action === 'object' ? payload.action.id : payload.action
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.moduleId = typeof payload.moduleId === 'object' ? payload.moduleId.id : payload.moduleId
  payload.action = typeof payload.action === 'object' ? payload.action.id : payload.action
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
    idItem.value = ''
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
  const { event } = item
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change ?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      await saveItem(item)
    },
    reject: () => { }
  })
}
function requireConfirmationToDelete(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Doy you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      await deleteItem(idItem.value)
    },
    reject: () => { }
  })
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
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})
// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
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
onMounted(async () => {
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
      Manage Permission
    </h3>
    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 md:col-6 xl:col-9">
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
    <div class="col-12 md:col-6 xl:col-3">
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
            <template #field-moduleId="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.moduleId"
                :suggestions="[...listModulesItems]"
                @change="($event) => {
                  onUpdate('moduleId', $event)
                  onUpdate('code', `${$event ? `${$event.name}:` : ''}${data.action ? data.action.name : ''}`)
                }"
                @load="($event) => getModulesList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-action="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll"
                v-model="data.action"
                :options="[...ENUM_PERMISSIONS]"
                option-label="name"
                return-object="false"
                class="align-items-center"
                show-clear
                @update:model-value="($event) => {
                  onUpdate('action', $event)
                  onUpdate('code', `${data.moduleId ? `${data.moduleId.name}:` : ''}${$event ? $event.name : ''}`)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
    <!-- <Toast position="top-center" :base-z-index="5" group="tc" /> -->
  </div>
</template>

<style scoped lang="scss">
  .custom-card-header {
    margin-bottom: 0px;
    border-top-left-radius: 5px;
    border-top-right-radius: 5px;
    padding-top: 10px;
    padding-bottom: 10px;
  }
</style>
