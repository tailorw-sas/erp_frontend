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

interface MenuItem {
  label: string
  icon: string // Opcional, ya que algunos elementos pueden no tener icono
  command: (event: { originalEvent: Event, item: MenuItem }) => void
}

interface MenuCategory {
  label: string
  items: MenuItem[]
}

const toast = useToast()
const confirm = useConfirm()
const listItemsMenu = ref<MenuCategory[]>([
  {
    label: '',
    items: []
  }
])
const listItems = ref<any[]>([])

const formReload = ref(0)
const loadingSaveAll = ref(false)
const loadingSaveAllGetReport = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')

const loadingSearch = ref(false)

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const confApi = reactive({
  moduleApi: 'report',
  uriApi: 'jasper-report-template',
  uriApiReportGenerate: 'reports/generate/template'
})
const ENUM_FILTER = [
  { id: 'templateCode', name: 'Code' },
  { id: 'templateName', name: 'Name' },

]

const item = ref<GenericObject>({
  reportFormatType: REPORT_FORMATS_TYPE[0],
  jasperReportCode: ''
})

const itemTemp = ref<GenericObject>({
  reportFormatType: REPORT_FORMATS_TYPE[0],
  jasperReportCode: ''
})

const fields = ref<FieldDefinitionType[]>([
  {
    field: 'reportFormatType',
    header: 'Format Type',
    dataType: 'select',
    class: 'field col-12 md:col-1 required',
    disabled: false,
    validation: z.object({
      id: z.string().min(1, 'This is a required field'),
      name: z.string().min(1, 'This is a required field'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'This is a required field' }),
  },
  {
    field: 'jasperReportCode',
    header: 'Report Code',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-1',
    validation: z.string().trim()
    // validation: z.string().trim().min(1, 'The report code field is required').max(50, 'Maximum 50 characters')
  },
])

const fieldsTemp = [
  {
    field: 'reportFormatType',
    header: 'Format Type',
    dataType: 'select',
    class: 'field col-12 md:col-1 required',
    validation: z.object({
      id: z.string().min(1, 'This is a required field'),
      name: z.string().min(1, 'This is a required field'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'This is a required field' }),
  },
  {
    field: 'jasperReportCode',
    header: 'Report Code',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-1',
    validation: z.string().trim()
    // validation: z.string().trim().min(1, 'The report code field is required').max(50, 'Maximum 50 characters')
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
  fields.value[0].disabled = false
  updateFieldProperty(fields.value, 'status', 'disabled', true)
  updateFieldProperty(fields.value, 'reportParams', 'hidden', true)
  formReload.value++
}

async function getList() {
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
      iterator.type = ENUM_REPORT_TYPE.find(x => x.id === iterator.type)
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

    for (const element of listItems.value) {
      listItemsMenu.value[0].items.push({
        label: `${element.code} - ${element.name}`,
        icon: 'pi pi-file-pdf',
        // ...element,
        command: () => loadParamsFieldByReportTemplate(element.id, element.code)
      })
    }

    // if (listItems.value.length > 0) {
    //   idItemToLoadFirstTime.value = listItems.value[0].id
    // }
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
      updateFieldProperty(fields.value, 'reportParams', 'hidden', false)
      updateFieldProperty(fields.value, 'status', 'disabled', false)
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

// {{baseUrl}}/report/api/reports/generate-template
// {
//   “parameters”: {
//     “invoiceIds”: [
//       “123e4567-e89b-12d3-a456-426614174000”,
//       “987e6543-b21a-34d5-c678-123456789abc”,
//       “1a2b3c4d-5e6f-7g8h-9i0j-123456789def”
//     ],
//     “paymentDate”: “2024-10-26”
//   },
//   “reportFormatType”: “PDF”,
//   “jasperReportCode”: “AAA21”
// }
const isDate = value => !Number.isNaN(Date.parse(value))

function formatToDateTimeZero(date) {
  const formattedDate = new Date(date).toISOString().split('T')[0]
  return `${formattedDate}T00:00:00.000Z`
};
async function saveItem(objItem: any) {
  if (objItem) {
    try {
      loadingSaveAllGetReport.value = true
      const payload = {
        parameters: {},
        reportFormatType: typeof objItem.value.reportFormatType === 'object' ? objItem.value.reportFormatType.id : objItem.value.reportFormatType,
        jasperReportCode: objItem.value.jasperReportCode
      } as any

      payload.parameters = Object.keys(objItem.value).reduce((acc: Record<string, any>, key) => {
        const value = objItem.value[key]

        if (isDate(value) && typeof value !== 'number') {
          acc[key] = formatToDateTimeZero(value) // Convertir a formato YYYY-MM-DDT00:00:00.000Z
        }
        else if (typeof value === 'object' && value !== null && value.id !== undefined) {
          acc[key] = value.id
        }
        else {
          acc[key] = value
        }
        return acc
      }, {} as Record<string, any>)
      delete payload?.parameters?.reportFormatType
      delete payload?.parameters?.jasperReportCode
      delete payload?.parameters?.event
      payload.parameters.paymentId = '2f440f61-1845-4ce4-a1e3-9038053f67b6'
      await GenericService.create(confApi.moduleApi, confApi.uriApiReportGenerate, payload)
      loadingSaveAllGetReport.value = false
    }
    catch (error) {
      console.error(error)
      loadingSaveAllGetReport.value = false
    }
  }
}

function requireConfirmationToSave(item2: any) {
  const { event } = item2
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      item.value = { ...item2 }
      saveItem(item)
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
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      // deleteItem(idItem.value)
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
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
  componentType: string
  label: string
  module: string
  paramName: string
  service: string
  type: string
}

interface ListItem {
  id: string
  type: string
  componentType: string
  paramName: string
  label: string
  module: string
  service: string
}

function mapFunction(data: DataList): ListItem {
  return {
    id: data.id,
    type: data.type,
    componentType: data.componentType,
    paramName: data.paramName,
    label: data.label,
    module: data.module,
    service: data.service
  }
}
async function getParamsByReport(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]): Promise<ListItem[]> {
  return await getDataList<DataList, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'createdAt', sortType: ENUM_SHORT_TYPE.ASC })
}
// {
//   field: 'code',
//   header: 'Code',
//   dataType: 'text',
//   class: 'field col-12 md:col-1 required',
//   validation: z.string().trim().min(1, 'The code field is required').max(50, 'Maximum 50 characters')
// },
async function loadParamsFieldByReportTemplate(id: string, code: string) {
  if (id) {
    try {
      const filter: FilterCriteria[] = [
        {
          key: 'jasperReportTemplate.id',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: `${id}`,
        },
      ]
      const objQueryToSearch = {
        query: '',
        keys: ['name', 'username', 'url'],
      }
      const result = await getParamsByReport('report', 'jasper-report-template-parameter', objQueryToSearch, filter)
      fields.value = [...fieldsTemp]
      if (result && result.length > 0) {
        for (const element of result) {
          item.value[element.paramName] = ''
          if (element.componentType === 'select') {
            fields.value = [...fields.value, {
              field: element.paramName,
              header: element.label,
              dataType: element.componentType,
              class: 'field col-12 md:col-2',
              objApi: {
                moduleApi: element.module,
                uriApi: element.service
              }
            }]
          }
          else {
            fields.value = [...fields.value, {
              field: element.paramName,
              header: element.label,
              dataType: element.componentType,
              class: 'field col-12 md:col-2'
            }]
          }
        }
      }

      if (code) {
        item.value.jasperReportCode = code
      }
      else {
        item.value.jasperReportCode = ''
      }
      formReload.value++
    }
    catch (error) {

    }
  }
}

const suggestionsData = ref<any[]>([])

async function getDinamicData(query: string, moduleApi: string, uriApi: string) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'code',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'OR'
            },
            {
              key: 'name',
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

    const response = await GenericService.search(moduleApi, uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      suggestionsData.value = [...suggestionsData.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading agency type list:', error)
  }
}

// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
// watch(payloadOnChangePage, (newValue) => {
//   payload.value.page = newValue?.page ? newValue?.page : 0
//   payload.value.pageSize = newValue?.rows ? newValue.rows : 10
//   getList()
// })

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
  updateFieldProperty(fields.value, 'reportParams', 'hidden', true)
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
  }
  // const objQueryToSearch = {
  //   query: '',
  //   keys: ['name', 'username', 'url'],
  // }
  // const result = await getParamsByReport('report', 'jasper-report-template-parameter', objQueryToSearch, [])
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <!-- <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Manage Report
    </h3>
  </div> -->
  <div class="grid">
    <div class="col-12 md:col-6 xl:col-2">
      <div>
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          Report List
        </div>
        <div class="card p-0">
          <Menu
            :model="listItemsMenu" :pt="{
              menuitem: {
                class: 'py-2',
              },
            }"
          />
        </div>
      </div>
    </div>
    <div class="col-12 md:col-6 xl:col-10">
      <div v-if="fields.length > 0">
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          Params
        </div>
        <div class="card">
          <Accordion v-if="false" :active-index="0" class="mb-2">
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
          <EditFormV2
            :key="formReload"
            :fields="fields"
            :item="item"
            :show-actions="false"
            :show-actions-inline="true"
            :loading-save="loadingSaveAllGetReport"
            container-class="grid pt-3 px-0"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template v-for="field in fields.filter(field => field.field !== 'reportFormatType' && field.dataType === 'select')" :key="field.field" #[`field-${field.field}`]="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data[field.field]"
                :suggestions="[...suggestionsData]"
                @change="($event) => {
                  onUpdate(field.field, $event)
                }"
                @load="($event) => getDinamicData($event, field.objApi?.moduleApi, field.objApi?.uriApi)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-reportFormatType="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAllGetReport"
                v-model="data.reportFormatType"
                :options="[...REPORT_FORMATS_TYPE]"
                option-label="name"
                @update:model-value="($event) => {
                  onUpdate('reportFormatType', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
          </EditFormV2>
        </div>
      </div>

      <div v-if="false" class="card p-2">
        <object data="https://static.kynsoft.net/689_2024-10-31_12-14-01.pdf" type="application/pdf" width="100%" height="800px">
          <p>Tu navegador no soporta PDF. <a href="https://static.kynsoft.net/689_2024-10-31_12-14-01.pdf">Descargar PDF</a>.</p>
        </object>
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
