<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import dayjs from 'dayjs'
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

interface TreeNode {
  key: string // Identificador único del nodo
  label: string // Etiqueta del nodo
  data: string // Información o descripción del nodo
  icon?: string // Clase de icono opcional (por ejemplo, de PrimeVue)
  children?: TreeNode[] // Hijos del nodo, de tipo recursivo
}

interface MenuItem {
  label: string
  icon: string // Opcional, ya que algunos elementos pueden no tener icono
  command: (event: { originalEvent: Event, item: MenuItem }) => void
}

interface MenuCategory {
  label: string
  items: MenuItem[]
}

const route = useRoute()
const toast = useToast()
const confirm = useConfirm()
const listItemsMenu = ref<MenuCategory[]>([
  {
    label: '',
    items: []
  }
])

const selectedKey = ref(undefined)

const listItemMenuTree = ref<TreeNode[]>([])
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
  uriApiReportGenerate: 'reports/execute-report'
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
    class: 'field col-12 md:col-1 required auto',
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
    hidden: true,
    class: 'field col-12 md:col-1 auto',
    validation: z.string().trim()
    // validation: z.string().trim().min(1, 'The report code field is required').max(50, 'Maximum 50 characters')
  },
])

const fieldsTemp = [
  {
    field: 'reportFormatType',
    header: 'Format Type',
    dataType: 'select',
    class: 'field col-12 md:col-1 required auto',
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
    hidden: true,
    class: 'field col-12 md:col-1 auto',
    validation: z.string().trim()
    // validation: z.string().trim().min(1, 'The report code field is required').max(50, 'Maximum 50 characters')
  },
]
// FORM CONFIG -------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------

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

const isDate = (value: string) => !Number.isNaN(Date.parse(value))

function formatToDateTimeZero(date: string | number | Date) {
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

        // 📆 Fecha válida
        if (isDate(value) && typeof value !== 'number') {
          acc[key] = dayjs(value).format('YYYY-MM-DD')
        }
        // 🔢 Multiselect (Array con objetos que tienen ID)
        else if (Array.isArray(value) && value.every(v => v && typeof v === 'object' && 'id' in v)) {
          acc[key] = value.map(v => v.id)
        }
        // ✅ Select simple
        else if (value && typeof value === 'object' && 'id' in value) {
          acc[key] = value.id
        }
        // 🔄 Default
        else {
          acc[key] = value
        }

        return acc
      }, {} as Record<string, any>)
      delete payload?.parameters?.reportFormatType
      delete payload?.parameters?.jasperReportCode
      delete payload?.parameters?.event
      // const payloadTemp = {
      //   jasperReportCode: 'KKKK',
      //   parameters: {
      //     ReportTitle: 'Reporte de Ventas',
      //   },
      //   reportFormatType: 'PDF'
      // }
      const response = await GenericService.create(confApi.moduleApi, confApi.uriApiReportGenerate, payload)
      if (response) {
        loadPDF(response.base64Report)
      }
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
  reportClass: string
  reportValidation: any
  dependentField: any
  parameterPosition: number
  filterKeyValue: string
  dataValueStatic: string
}

interface ListItem {
  id: string
  type: string
  componentType: string
  paramName: string
  label: string
  module: string
  service: string
  reportClass: string
  reportValidation: any
  dependentField: any
  parameterPosition: number
  filterKeyValue: string
  dataValueStatic: string
}

function mapFunction(data: DataList): ListItem {
  return {
    id: data.id,
    type: data.type,
    componentType: data.componentType,
    paramName: data.paramName,
    label: data.label,
    module: data.module,
    service: data.service,
    reportClass: data.reportClass,
    reportValidation: data.reportValidation,
    dependentField: data.dependentField,
    parameterPosition: data.parameterPosition,
    filterKeyValue: data.filterKeyValue,
    dataValueStatic: data.dataValueStatic
  }
}
async function getParamsByReport(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]): Promise<ListItem[]> {
  return await getDataList<DataList, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'createdAt', sortType: ENUM_SHORT_TYPE.ASC })
}

const showForm = ref(false)
async function loadParamsFieldByReportTemplate(id: string, code: string) {
  if (id) {
    try {
      showForm.value = false
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
          if (element.componentType === 'select' || element.componentType === 'multiselect') {
            fields.value = [...fields.value, {
              field: element.paramName,
              header: element.label,
              dataType: element.componentType,
              class: element.reportClass ? element.reportClass : 'field col-12 md:col-2',
              // validation: element.reportValidation ? element.reportValidation : z.object({}).nullable(),
              objApi: {
                moduleApi: element.module,
                uriApi: element.service
              },
              kwArgs: {
                ...element
              }
            }]
          }
          else {
            fields.value = [...fields.value, {
              field: element.paramName,
              header: element.label,
              dataType: element.componentType,
              class: element.reportClass ? element.reportClass : 'field col-12 md:col-2',
              // validation: element.reportValidation ? element.reportValidation : z.string().trim()
              kwArgs: {
                ...element
              }
            }]

            if (element.componentType === 'local-select') {
              const listValue = element.dataValueStatic ? JSON.parse(element.dataValueStatic) : []
              if (listValue && listValue.length > 0) {
                item.value[element.paramName] = listValue.find((x: any) => x.defaultValue)
              }
            }
          }
        }
      }
      formReload.value++

      if (code) {
        item.value.jasperReportCode = code
      }
      else {
        item.value.jasperReportCode = ''
      }

      showForm.value = true
    }
    catch (error) {
      console.log(error)
    }
  }
}

const suggestionsData = ref<any[]>([])

async function getDinamicData(query: string, moduleApi: string, uriApi: string, filter?: IFilter[]) {
  try {
    const payload
        = {
          filter: filter || [],
          query: '',
          pageSize: 2000,
          page: 0,
          sortBy: 'createdAt',
          sortType: ENUM_SHORT_TYPE.ASC
        }

    const response = await GenericService.search(moduleApi, uriApi, payload)
    const { data: dataList } = response
    suggestionsData.value = []
    for (const iterator of dataList) {
      suggestionsData.value = [...suggestionsData.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading agency type list:', error)
  }
}

function transformToTreeNode(data: Record<string, any[]>): TreeNode[] {
  return Object.entries(data).map(([moduleName, items]) => ({
    key: moduleName,
    label: moduleName,
    data: `${moduleName} Module`,
    icon: 'pi pi-folder', // Puedes cambiar el ícono según tus necesidades
    children: items.map(item => ({
      key: item.id,
      label: item.name,
      data: {
        ...item,
      },
      icon: item.highRisk ? 'pi pi-exclamation-triangle' : 'pi pi-file', // Ícono según si es de alto riesgo
    })),
  }))
}

async function getMenuItems(): Promise<TreeNode[]> {
  try {
    const response = await GenericService.get('report', 'report-menu/grouped')
    if (response) {
      const treeNodes: TreeNode[] = transformToTreeNode(response)
      return treeNodes
    }
    return []
  }
  catch (error) {
    console.error('Error loading menu items:', error)
    return []
  }
}

function onNodeSelect(node) {
  loadParamsFieldByReportTemplate(node.data.id, node.data.code)
}

function onNodeUnselect(node) {
  toast.add({ severity: 'warn', summary: 'Node Unselected', detail: node.label, life: 3000 })
}

// -------------------------------------------------------------------------------------------------------

const pdfUrl = ref('')
function loadPDF(base64Report: string) {
  pdfUrl.value = ''
  // Convertir base64 a un ArrayBuffer
  const byteCharacters = atob(base64Report)
  const byteNumbers = Array.from({ length: byteCharacters.length })
  for (let i = 0; i < byteCharacters.length; i++) {
    byteNumbers[i] = byteCharacters.charCodeAt(i)
  }
  const byteArray = new Uint8Array(byteNumbers)

  if (item.value?.reportFormatType?.id === 'XLS') {
    const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })

    // Crear una URL del Blob y asignarla a pdfUrl
    pdfUrl.value = URL.createObjectURL(blob)

    // Abrir el PDF en una nueva ventana
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `report-file-${dayjs().format('YYYY-MM-DD')}.xlsx`
    link.click()
  }
  else if (item.value?.reportFormatType?.id === 'CSV') {
    const blob = new Blob([byteArray], { type: 'text/csv' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `report-file-${dayjs().format('YYYY-MM-DD')}.csv`
    link.click()
  }
  else if (item.value?.reportFormatType?.id === 'PDF') {
    // Crear un Blob del ArrayBuffer con tipo MIME 'application/pdf'
    const blob = new Blob([byteArray], { type: 'application/pdf' })

    // Crear una URL del Blob y asignarla a pdfUrl
    pdfUrl.value = URL.createObjectURL(blob)

    // Abrir el PDF en una nueva ventana
    // window.open(pdfUrl.value, '_blank')
  }
}

async function loadReport() {
  try {
    const reportId = route.query.reportId ? route.query.reportId.toString() : ''
    const reportCode = route.query.reportCode ? route.query.reportCode.toString() : ''
    if (reportId && reportCode) {
      await loadParamsFieldByReportTemplate(reportId, reportCode)
    }
  }
  catch (error) {
    console.error('Error loading report:', error)
  }
}

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

watch(() => route.query, async (newValue) => {
  if (newValue) {
    loadReport()
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

  // listItemMenuTree.value = await getMenuItems()
  await loadReport()
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
    <div v-if="false" class="col-12 md:col-6 xl:col-2">
      <div>
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          Report List
        </div>
        <div class="card p-0">
          <!-- <Tree :value="listItemMenuTree" class="w-full md:auto" /> -->
          <Tree
            v-model:selectionKeys="selectedKey"
            :value="listItemMenuTree"
            selection-mode="single"
            :meta-key-selection="false"
            :filter="true"
            class="w-full md:w-auto"
            @node-select="onNodeSelect"
            @node-unselect="onNodeUnselect"
          />
          <!-- @node-expand="onNodeExpand"
            @node-collapse="onNodeCollapse" -->
          <!-- <Menu
            :model="listItemsMenu" :pt="{
              menuitem: {
                class: 'py-2',
              },
            }"
          /> -->
        </div>
      </div>
    </div>
    <div class="col-12">
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
            v-if="showForm"
            :key="formReload"
            :fields="fields"
            :item="item"
            :show-actions="false"
            :show-actions-inline="true"
            :loading-save="loadingSaveAllGetReport"
            container-class="grid pt-3 px-0"
            @reactive-update-field="item = $event"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template
              v-for="field in fields.filter(field => field.field !== 'reportFormatType' && (field.dataType === 'select' || field.dataType === 'multiselect' || field.dataType === 'local-select'))"
              :key="field.field" #[`field-${field.field}`]="{ item: data, onUpdate }"
            >
              <span v-if="field.dataType === 'local-select'">
                <Dropdown
                  v-model="data[field.field]"
                  :options="field.kwArgs.dataValueStatic ? [...JSON.parse(field.kwArgs.dataValueStatic)] : []"
                  option-label="name"
                  placeholder="Select a City"
                  class="w-full"
                />
              </span>
              <span v-if="field.dataType === 'select'">
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
                  @load="async ($event) => {
                    let keyValue = ''
                    let filter = []
                    // Esto es para buscar el keyValue del campo dependiente, para poder referenciarlo y obtener su valor
                    if (field.kwArgs && field.kwArgs.dependentField) {
                      keyValue = JSON.parse(field.kwArgs.dependentField).name
                      filter = [
                        {
                          key: field.kwArgs.filterKeyValue,
                          operator: 'EQUALS',
                          value: typeof data[keyValue] === 'object' ? data[keyValue].id : '',
                          logicalOperation: 'AND',
                        },
                        {
                          key: 'code',
                          operator: 'LIKE',
                          value: $event,
                          logicalOperation: 'OR',
                        },
                        {
                          key: 'name',
                          operator: 'LIKE',
                          value: $event,
                          logicalOperation: 'OR',
                        },
                        {
                          key: 'status',
                          operator: 'EQUALS',
                          value: 'ACTIVE',
                          logicalOperation: 'AND',
                        },
                      ]
                    }
                    else {
                      filter = [
                        {
                          key: 'code',
                          operator: 'LIKE',
                          value: $event,
                          logicalOperation: 'OR',
                        },
                        {
                          key: 'name',
                          operator: 'LIKE',
                          value: $event,
                          logicalOperation: 'OR',
                        },
                        {
                          key: 'status',
                          operator: 'EQUALS',
                          value: 'ACTIVE',
                          logicalOperation: 'AND',
                        },
                      ]
                    }
                    await getDinamicData($event, field.objApi?.moduleApi, field.objApi?.uriApi, filter)
                  }"
                />
                <Skeleton v-else height="2rem" class="mb-2" />
              </span>
              <span v-if="field.dataType === 'multiselect'">
                <DebouncedMultiSelectComponent
                  v-if="!loadingSaveAll"
                  id="autocomplete"
                  field="name"
                  item-value="id"
                  :max-selected-labels="3"
                  class="w-full h-2rem align-items-center"
                  :model="data[field.field]"
                  :suggestions="[...suggestionsData]"
                  @change="($event) => {
                    onUpdate(field.field, $event)
                  }"
                  @load="async ($event) => {
                    let keyValue = ''
                    let filter = []
                    // Esto es para buscar el keyValue del campo dependiente, para poder referenciarlo y obtener su valor
                    if (field.kwArgs && field.kwArgs.dependentField) {
                      keyValue = JSON.parse(field.kwArgs.dependentField).name
                      filter = [
                        {
                          key: field.kwArgs.filterKeyValue,
                          operator: 'EQUALS',
                          value: typeof data[keyValue] === 'object' ? data[keyValue].id : '',
                          logicalOperation: 'AND',
                        },
                        {
                          key: 'code',
                          operator: 'LIKE',
                          value: $event,
                          logicalOperation: 'OR',
                        },
                        {
                          key: 'name',
                          operator: 'LIKE',
                          value: $event,
                          logicalOperation: 'OR',
                        },
                        {
                          key: 'status',
                          operator: 'EQUALS',
                          value: 'ACTIVE',
                          logicalOperation: 'AND',
                        },
                      ]
                    }
                    else {
                      filter = [
                        {
                          key: 'code',
                          operator: 'LIKE',
                          value: $event,
                          logicalOperation: 'OR',
                        },
                        {
                          key: 'name',
                          operator: 'LIKE',
                          value: $event,
                          logicalOperation: 'OR',
                        },
                        {
                          key: 'status',
                          operator: 'EQUALS',
                          value: 'ACTIVE',
                          logicalOperation: 'AND',
                        },
                      ]
                    }
                    await getDinamicData($event, field.objApi?.moduleApi, field.objApi?.uriApi, filter)
                  }"
                />
                <Skeleton v-else height="2rem" class="mb-2" />
              </span>
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

      <div v-if="item?.reportFormatType?.id === 'PDF'" class="card p-2">
        <object :data="pdfUrl" type="application/pdf" width="100%" height="800px">
          <p>Your browser does not support PDF. <a :href="pdfUrl">Download PDF</a>.</p>
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
