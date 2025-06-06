<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import ReportParamPage from './params.vue'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import getUrlByImage from '~/composables/files'
import type { IData } from '~/components/table/interfaces/IModelData'
import { statusToBoolean, statusToString, updateFieldProperty } from '~/utils/helpers'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'
// VARIABLES -----------------------------------------------------------------------------------------

const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const dbConnectionList = ref<any[]>([])
const formReload = ref(0)
const loadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingDelete = ref(false)
const loadingSearch = ref(false)

const reportParams = ref<any>({})
const dialogReportParams = ref(false)

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const confApi = reactive({
  moduleApi: 'report',
  uriApi: 'jasper-report-template',
})

const ENUM_FILTER = [
  { id: 'templateCode', name: 'Code' },
  { id: 'templateName', name: 'Name' },

]

const item = ref<GenericObject>({
  file: '',
  code: '',
  name: '',
  status: true,
  type: '',
  query: '',
  rootIndex: '',
  menuPosition: 0.0,
  description: '',
  reportParams: '',
})

const itemTemp = ref<GenericObject>({
  file: '',
  code: '',
  name: '',
  type: '',
  query: '',
  rootIndex: '',
  menuPosition: 0.0,
  description: '',
  reportParams: '',
  status: true,
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'type',
    header: 'Type',
    dataType: 'select',
    class: 'field col-12 required',
    validation: z.object({
      id: z.string().min(1, 'The generation type field is required'),
      name: z.string().min(1, 'The generation type field is required'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'The type field is required' }),
  },
  {
    field: 'moduleSystems',
    header: 'Module',
    dataType: 'select',
    class: 'field col-12 required',
    validation: z.object({
      id: z.string().min(1, 'The generation type field is required'),
      name: z.string().min(1, 'The generation type field is required'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'The type field is required' }),
  },
  {
    field: 'dbConection',
    header: 'DB Connection',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('DB Connection'),
  },
  {
    field: 'reportParams',
    header: 'Report Params',
    hidden: true,
    dataType: 'text',
    class: 'field col-12',
  },
  {
    field: 'menuPosition',
    header: 'Menu Position',
    dataType: 'number',
    class: 'field col-12',
  },
  {
    field: 'query',
    header: 'Query',
    dataType: 'textarea',
    class: 'field col-12',
    hidden: true,
    validation: z.string().trim().min(0, 'The query field is required').max(250, 'Maximum 250 characters')
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    validation: z.string().trim().max(50, 'Maximum 50 characters')
  },
  {
    field: 'file',
    header: 'File',
    dataType: 'file',
    class: 'field col-12 required',
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mb-3 mt-3',
  },
]
// FORM CONFIG -------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns = ref<IColumn[]>([
  { field: 'code', header: 'Code', type: 'text' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'type', header: 'Tipo', type: 'local-select', localItems: [...ENUM_REPORT_TYPE] },

  { field: 'createdAt', header: 'Created At', type: 'date' },
])
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Report',
  moduleApi: 'report',
  uriApi: 'jasper-report-template',
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

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

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
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fields[0].disabled = false
  updateFieldProperty(fields, 'status', 'disabled', true)
  updateFieldProperty(fields, 'reportParams', 'hidden', true)
  formReload.value++
}

async function getList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)
    console.log(response)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      // iterator.type = iterator.type ? ENUM_REPORT_TYPE.find(x => x.id === iterator.type) : null
      // if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
      //   iterator.status = statusToBoolean(iterator.status)
      // }

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
        item.value.file = response.file
        item.value.code = response.code
        item.value.name = response.name
        item.value.type = ENUM_REPORT_TYPE.find(x => x.id === response.type)
        item.value.moduleSystems = ENUM_MODULES_SYSTEM.find(x => x.id === response.moduleSystems)
        item.value.rootIndex = response.rootIndex
        item.value.menuPosition = response.menuPosition
        item.value.description = response.description
        item.value.status = statusToBoolean(response.status)
        if (response.dbConection) {
          item.value.dbConection = {
            id: response.dbConection.id,
            name: response.dbConection.name,
            code: response.dbConection.code,
            status: response.dbConection.status
          }
        }
        item.value.query = response.query
      }
      updateFieldProperty(fields, 'reportParams', 'hidden', false)
      updateFieldProperty(fields, 'status', 'disabled', false)
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

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    let payload: { [key: string]: any } = { ...item }
    payload.status = statusToString(payload.status)
    payload.file = typeof payload.file === 'object' ? await getUrlByImage(payload.file) : payload.file
    payload.type = typeof payload.type === 'object' ? payload.type.id : payload.type
    payload.moduleSystems = typeof payload.moduleSystems === 'object' ? payload.moduleSystems.id : payload.moduleSystems
    payload.dbConection = typeof payload.dbConection === 'object' ? payload.dbConection.id : payload.dbConection
    payload = {
      ...payload,
      parentIndex: 0.0,
      // menuPosition: 0.0,
      lanPath: 'lanPath',
      web: false,
      subMenu: false,
      sendEmail: false,
      internal: false,
      highRisk: false,
      visible: false,
      cancel: false,
      rootIndex: 'rootIndex',
      language: 'language'
      , }

    return await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.type = typeof payload.type === 'object' ? payload.type.id : payload.type
  payload.moduleSystems = typeof payload.moduleSystems === 'object' ? payload.moduleSystems.id : payload.moduleSystems
  payload.dbConection = typeof payload.dbConection === 'object' ? payload.dbConection.id : payload.dbConection
  if (!payload.file) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'File is required', life: 3000 })
  }
  else {
    payload.file = typeof payload.file === 'object' ? await getUrlByImage(payload.file) : payload.file
    payload = {
      ...payload,
      parentIndex: 0.0,
      // menuPosition: 0.0,
      lanPath: 'lanPath',
      web: false,
      subMenu: false,
      sendEmail: false,
      internal: false,
      highRisk: false,
      visible: false,
      cancel: false,
      rootIndex: 'rootIndex',
      language: 'language',
    }
    return await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
  }
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
  let successOperation = null
  if (idItem.value) {
    try {
      const response = await updateItem(item)
      if (response) {
        successOperation = true
      }
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      const response = await createItem(item)
      if (response) {
        successOperation = true
      }
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
  saveItem(item)
}
function requireConfirmationToDelete(event: any) {
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
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  const templateCode = parseFilter?.find((item: IFilter) => item?.key === 'code')
  const templateName = parseFilter?.find((item: IFilter) => item?.key === 'name')
  const templateDescription = parseFilter?.find((item: IFilter) => item?.key === 'description')

  if (templateCode) {
    templateCode.key = 'templateCode'
  }
  if (templateName) {
    templateName.key = 'templateName'
  }
  if (templateDescription) {
    templateDescription.key = 'templateDescription'
  }

  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    switch (event.sortField) {
      case 'code':
        event.sortField = 'templateCode'
        break
      case 'name':
        event.sortField = 'templateName'
        break
      case 'description':
        event.sortField = 'templateDescription'
        break
      case 'type':
        if (event.sortOrder === 'ASC') {
          event.sortOrder = 'ASC'
        }
        else {
          event.sortOrder = ENUM_SHORT_TYPE.DESC
        }
        break
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

interface DataList {
  id: string
  name: string
  code: string
  status: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
}

function mapFunction(data: DataList): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status
  }
}
async function getdbConnectionList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  dbConnectionList.value = await getDataList<DataList, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'createdAt', sortType: ENUM_SHORT_TYPE.ASC })
}

async function reportParamsOpenDialog(item: any) {
  reportParams.value = {
    reportConfig: {
      id: item.id,
      name: item.name,
      status: 'ACTIVE'
    }
  }
  dialogReportParams.value = true
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
  updateFieldProperty(fields, 'reportParams', 'hidden', true)
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
    document.title = 'Report'
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Manage Report
    </h3>
    <div
      v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
      class="my-2 flex justify-content-end px-0"
    >
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
            :key="formReload" :fields="fields" :item="item" :show-actions="true"
            :loading-save="loadingSaveAll" @cancel="clearForm" @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-type="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll" v-model="data.type" :options="[...ENUM_REPORT_TYPE]" option-label="name"
                return-object="false" @update:model-value="($event) => {
                  onUpdate('type', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-moduleSystems="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll" v-model="data.moduleSystems" :options="[...ENUM_MODULES_SYSTEM]" option-label="name"
                return-object="false" @update:model-value="($event) => {
                  onUpdate('moduleSystems', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-dbConection="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.dbConection"
                :suggestions="[...dbConnectionList]"
                @change="($event) => {
                  onUpdate('dbConection', $event)
                }"
                @load="async($event) => {
                  const objQueryToSearch = {
                    query: $event,
                    keys: ['name', 'username', 'url'],
                  }
                  const filter: FilterCriteria[] = [
                    // {
                    //   key: 'status',
                    //   logicalOperation: 'AND',
                    //   operator: 'EQUALS',
                    //   value: 'ACTIVE',
                    // },
                  ]
                  await getdbConnectionList('report', 'db-connection', objQueryToSearch, filter)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-reportParams>
              <InputGroup v-if="!loadingSaveAll">
                <InputText placeholder="Create the parameters for this report." disabled />
                <Button
                  icon="pi pi-eye" type="button" text aria-haspopup="true" aria-controls="overlay_menu_filter"
                  @click="reportParamsOpenDialog(item)"
                />
              </InputGroup>
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <!-- <template #field-file="{ item: data, onUpdate }">
              <FileUpload
                v-if="!loadingSaveAll" :max-file-size="1000000" :disabled="idItem !== '' || idItem === null" :multiple="false" auto custom-upload accept=".jrxml"
                @uploader="($event: any) => {
                  // customBase64Uploader($event, fieldsV2, 'path');
                  onUpdate('path', $event)
                  if ($event && $event.files.length > 0) {
                    onUpdate('fileName', $event?.files[0]?.name)
                    onUpdate('fileSize', formatSize($event?.files[0]?.size))
                  }
                  else {
                    onUpdate('fileName', '')
                  }

                }"
              >
                <template #header="{ chooseCallback }">
                  <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                    <div class="flex gap-2">
                      <Button id="btn-choose" :disabled="idItem !== '' || idItem === null" class="p-2" icon="pi pi-plus" text @click="chooseCallback()" />
                      <Button
                        :disabled="idItem !== '' || idItem === null"
                        icon="pi pi-times" class="ml-2" severity="danger" text @click="() => {
                          onUpdate('path', null);
                          onUpdate('fileName', '');

                        }"
                      />
                    </div>
                  </div>
                </template>
                <template #content="{ files }">
                  <ul v-if="files[0] || (data.path && data.path?.files.length > 0)" class="list-none p-0 m-0">
                    <li class="p-3 surface-border flex align-items-start sm:align-items-center">
                      <div class="flex flex-column">
                        <span class="text-900 font-semibold text-xl mb-2">{{ data.path?.files[0].name }}</span>
                        <span class="text-900 font-medium">
                          <Badge severity="warning">
                            {{ formatSize(data.path?.files[0].size) }}
                          </Badge>
                        </span>
                      </div>
                    </li>
                  </ul>
                </template>
              </FileUpload>
            </template> -->
          </EditFormV2>
          <DynamicContentModal
            :visible="dialogReportParams"
            :component="ReportParamPage"
            :component-props="reportParams"
            :header="`Params for Report ${item.code} - ${item.name}`"
            @close="dialogReportParams = $event"
          />
        </div>
      </div>
    </div>
    <Toast position="top-center" :base-z-index="5" group="tc" />
    <ConfirmPopup group="headless" />
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
