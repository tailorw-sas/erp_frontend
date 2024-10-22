<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest, IStandardObject } from '~/components/fields/interfaces/IFieldInterfaces'
import { GenericService } from '~/services/generic-services'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'
import { ENUM_MAIL_TEMPLATE_TYPE, ENUM_SHORT_TYPE } from '~/utils/Enums'
import { validateEntityStatus } from '~/utils/schemaValidations'

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const LanguageList = ref<any[]>([])
const loadingSaveAll = ref(false)
const loadingLanguage = ref(false)
const loadingDelete = ref(false)
const listConfigMailjet = ref<any[]>([])
const idItemToLoadFirstTime = ref('')
const formReload = ref(0)
const idItem = ref('')

const confApi = reactive({
  moduleApi: 'cloudbridges',
  uriApi: 'template',
})

const loadingSearch = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const ENUM_FILTER = [
  { id: 'templateCode', name: 'Code' },
  { id: 'name', name: 'Name' },
]

const fields: Array<FieldDefinitionType> = [
  {
    field: 'templateCode',
    header: 'Code',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'This is a required field').max(50, 'Maximum 50 characters')
  },
  {
    field: 'mailjetConfigId',
    header: 'Mailjet Config',
    dataType: 'select',
    class: 'field col-12 required',
    validation: z.object({
      id: z.string().min(1, 'This is a required field'),
      name: z.string().min(1, 'This is a required field'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'This is a required field' }),
  },

  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'This is a required field').max(50, 'Maximum 50 characters')
  },
  {
    field: 'type',
    header: 'Type',
    dataType: 'select',
    class: 'field col-12 required',
    validation: z.object({
      id: z.string(),
      name: z.string(),
    }).nullable()
      .refine(value => value && value.id && value.name, { message: `The type field is required` })
  },
  {
    field: 'language',
    header: 'Language',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('language'),
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'This is a required field').max(150, 'Maximum 150 characters')
  },
]

const item = ref<GenericObject>({
  name: '',
  description: '',
  mailjetConfigId: '',
  templateCode: '',
  type: null,
  language: ''
})

const itemTemp = ref<GenericObject>({
  name: '',
  description: '',
  mailjetConfigId: '',
  templateCode: '',
  type: null,
  language: ''
})

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns = ref<IColumn[]>([
  { field: 'templateCode', header: 'Code', type: 'text' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'languageCode', header: 'Language Code', type: 'text' },
  { field: 'createdAt', header: 'Created At', type: 'date' },
])
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Templates',
  moduleApi: 'cloudbridges',
  uriApi: 'template',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Are you sure you want to delete the template: {{name}}?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0
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

function searchAndFilter() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'name',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }
  getList()
}

function clearFilterToSearch() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'name',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getList()
}

// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  formReload.value++
}
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

async function getListMeijetConfig() {
  try {
    let listItems: IStandardObject[] = []
    const payload: IQueryRequest = {
      filter: [],
      query: '',
      page: 0,
      pageSize: 1000
    }
    const response = await GenericService.search('cloudbridges', 'mailjet-config', payload)

    for (const iterator of response.data) {
      const item = {
        id: iterator.id,
        name: iterator.name,
      }
      listItems = [...listItems, item]
    }
    return listItems
  }
  catch (error) {
    console.error(error)
    return []
  }
}

async function getLanguagesList(filters: any[]) {
  try {
    const payload
        = {
          filter: [
            ...filters,
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

    const response = await GenericService.search('settings', 'manage-language', payload)
    const { data: dataList } = response
    LanguageList.value = []
    for (const iterator of dataList) {
      LanguageList.value = [...LanguageList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading languages list:', error)
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
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.mailjetConfigId = typeof response.mailjetConfigId === 'object' ? { id: response.mailjetConfigId.id, name: response.mailjetConfigId.name } : response.mailjetConfigId
        item.value.templateCode = response.templateCode
        item.value.name = response.name
        item.value.description = response.description
        item.value.type = ENUM_MAIL_TEMPLATE_TYPE.find((i: any) => i.id === response.type)
        if (response.languageCode) {
          getLanguageByCode(response.languageCode)
        }
        else {
          item.value.language = null
        }
      }
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Could not load mail configuration data', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function getLanguageByCode(code: string) {
  try {
    loadingLanguage.value = true
    const filters = [
      {
        key: 'code',
        operator: 'LIKE',
        value: code,
        logicalOperation: 'AND',
      }
    ]
    await getLanguagesList(filters)
    if (LanguageList.value.length > 0) {
      item.value.language = LanguageList.value[0]
      formReload.value++
    }
  }
  finally {
    loadingLanguage.value = false
  }
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.mailjetConfigId = typeof payload.mailjetConfigId === 'object' ? payload.mailjetConfigId.id : payload.mailjetConfigId
    payload.type = typeof payload.type === 'object' ? payload.type.id : payload.type
    payload.languageCode = typeof payload.language === 'object' ? payload.language.code : payload.language
    delete payload.language
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.mailjetConfigId = typeof payload.mailjetConfigId === 'object' ? payload.mailjetConfigId.id : payload.mailjetConfigId
  payload.type = typeof payload.type === 'object' ? payload.type.id : payload.type
  payload.languageCode = typeof payload.language === 'object' ? payload.language.code : payload.language
  delete payload.language
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
  if (idItem.value) {
    try {
      await updateItem(item)
    }
    catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Could not update email settings', life: 3000 })
    }
    idItem.value = ''
  }
  else {
    try {
      await createItem(item)
    }
    catch (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Could not create email settings', life: 3000 })
    }
  }
  loadingSaveAll.value = false
  clearForm()
  getList()
}

function requireConfirmationToSave(item: any) {
  const { event } = item
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Are you sure you want to save the record?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Save',
    accept: () => {
      saveItem(item)
      // toast.add({ severity: 'info', summary: 'Confirmed', detail: 'You have accepted', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}
function requireConfirmationToDelete(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Are you sure you want to delete this record?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Delete',
    accept: () => {
      deleteItem(idItem.value)
      // toast.add({ severity: 'info', summary: 'Confirmed', detail: 'You have accepted', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns.value)
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

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})
// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
  }
  listConfigMailjet.value = await getListMeijetConfig()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Manage Mailjet Template
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
                    @update:model-value="filterToSearch.search = ''"
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
        @update:clicked-item="getItemById($event)"
        @open-edit-dialog="getItemById($event)"
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
            <template #field-mailjetConfigId="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll"
                v-model="data.mailjetConfigId"
                :options="[...listConfigMailjet]"
                option-label="name"
                return-object
                placeholder="Select Config Mailjet"
                @update:model-value="($event) => {
                  onUpdate('mailjetConfigId', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-type="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll"
                v-model="data.type"
                :options="[...ENUM_MAIL_TEMPLATE_TYPE]"
                option-label="name"
                return-object="false"
                show-clear
                @update:model-value="($event) => {
                  onUpdate('type', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-language="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll && !loadingLanguage"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.language"
                :suggestions="LanguageList"
                @change="($event) => {
                  onUpdate('language', $event)
                }"
                @load="($event) => {
                  const filters = [{
                                     key: 'name',
                                     operator: 'LIKE',
                                     value: $event,
                                     logicalOperation: 'OR',
                                   },
                                   {
                                     key: 'code',
                                     operator: 'LIKE',
                                     value: $event,
                                     logicalOperation: 'OR',
                                   }]
                  getLanguagesList(filters)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
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
