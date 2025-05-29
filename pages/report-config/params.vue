<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { v4 as uuidv4 } from 'uuid'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import { validateEntityStatus } from '~/utils/schemaValidations'

const props = defineProps({
  reportConfig: {
    type: Object,
    required: false
  },
})

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const reportList = ref<any[]>([])
const dependentFieldList = ref<any[]>([])
const localValuesFieldList = ref<any[]>([])

interface IobjLocalValue {
  id: string
  name: string
  slug: string
  defaultValue: boolean
}

const objLocalValue = ref<IobjLocalValue>({
  id: '',
  name: '',
  slug: '',
  defaultValue: false
})

const objLocalValueTemp = {
  id: '',
  name: '',
  slug: '',
  defaultValue: false
}
const editingRows = ref<any[]>([])
const formReload = ref(0)
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const errorsListParent = reactive<{ [key: string]: string[] }>({})
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const confApi = reactive({
  moduleApi: 'report',
  uriApi: 'jasper-report-template-parameter',
})

const confRegionApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-region',
})

const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'reportId',
    header: 'Report',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: true,
    validation: z.object({
      id: z.string().min(1, 'The report field is required'),
      name: z.string().min(1, 'The report field is required'),
    })
  },
  {
    field: 'type',
    header: 'Type',
    dataType: 'text',
    class: 'field col-12 required',
    disabled: true,
    validation: z.string().trim().min(1, 'The component type field is required').max(50, 'Maximum 100 characters')
  },
  {
    field: 'paramName',
    header: 'Param Name',
    dataType: 'text',
    class: 'field col-12 required',
    disabled: true,
    validation: z.string().trim().min(1, 'The param name field is required').max(50, 'Maximum 100 characters')
  },
  {
    field: 'label',
    header: 'Label',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The label field is required').max(50, 'Maximum 100 characters')
  },

  {
    field: 'componentType',
    header: 'Component Type',
    dataType: 'select',
    class: 'field col-12 required',
    validation: z.object({
      id: z.string().min(1, 'The type field is required'),
      name: z.string().min(1, 'The type field is required'),
    })
  },
  {
    field: 'dataValueStatic',
    header: 'Local Data',
    dataType: 'text',
    class: 'field col-12',
    hidden: true,
    validation: z.string().trim()
  },
  {
    field: 'module',
    header: 'Module',
    dataType: 'text',
    class: 'field col-12',
    hidden: false,
    validation: z.string().trim()
  },
  {
    field: 'service',
    header: 'Service',
    dataType: 'text',
    class: 'field col-12',
    hidden: false,
    validation: z.string().trim()
  },
  {
    field: 'dependentField',
    header: 'Dependen Field',
    dataType: 'select',
    class: 'field col-12',
    validation: z.object({
      id: z.string().min(1, 'The type field is required'),
      name: z.string().min(1, 'The type field is required'),
    }).nullable()
  },
  {
    field: 'filterKeyValue',
    header: 'Filter Key Value',
    dataType: 'text',
    class: 'field col-12',
    hidden: false,
    validation: z.string().trim()
  },
  {
    field: 'parameterPosition',
    header: 'Position',
    dataType: 'number',
    class: 'field col-12',
  },
  {
    field: 'reportClass',
    header: 'Class',
    dataType: 'text',
    class: 'field col-12',
    validation: z.string().trim()
  },
  {
    field: 'reportValidation',
    header: 'Validation',
    dataType: 'textarea',
    class: 'field col-12',
    validation: z.string().trim()
  },

]

const item = ref<GenericObject>(
  {
    paramName: '',
    componentType: '',
    type: '',
    module: '',
    service: '',
    label: '',
    reportId: '',
    reportClass: '',
    dependentField: null,
    filterKeyValue: '',
    parameterPosition: 0,
    reportValidation: '',
    dataValueStatic: ''
  }
)

const itemTemp = ref<GenericObject>(
  {
    paramName: '',
    componentType: '',
    type: '',
    module: '',
    service: '',
    label: '',
    reportId: props.reportConfig,
    reportClass: '',
    dependentField: null,
    filterKeyValue: '',
    parameterPosition: 0,
    reportValidation: '',
    dataValueStatic: ''
  }
)

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const columns: IColumn[] = [
  { field: 'reportId', header: 'Report', type: 'text', objApi: { moduleApi: 'report', uriApi: 'jasper-report-template' }, showFilter: false, sortable: false },
  { field: 'paramName', header: 'Param Name', type: 'text' },
  { field: 'label', header: 'Label', type: 'text' },
  { field: 'type', header: 'Type', type: 'text' },
  // { field: 'type', header: 'Type', type: 'local-select', localItems: FORM_FIELD_TYPE, sortable: true },
  // { field: 'service', header: 'Service', type: 'text' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Report Params',
  moduleApi: 'report',
  uriApi: 'jasper-report-template-parameter',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [
    {
      key: 'jasperReportTemplate.id',
      operator: 'LIKE',
      value: props.reportConfig?.id || '',
      logicalOperation: 'AND'
    },
  ],
  query: '',
  pageSize: 20,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 20,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------
// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  // Deep-clone de itemTemp como ya lo tienes
  item.value = JSON.parse(JSON.stringify(itemTemp.value))

  // <-- Aquí reseteas todo lo relacionado a los valores locales
  localValuesFieldList.value = []
  objLocalValue.value = { ...objLocalValueTemp }
  editingRows.value = []

  idItem.value = ''
  formReload.value++
  if (!props.reportConfig) {
    reportList.value = []
  }
  else {
    item.value.reportId = props.reportConfig
  }
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
      if (Object.prototype.hasOwnProperty.call(iterator, 'jasperReportTemplate')) {
        iterator.reportId = iterator.jasperReportTemplate.name
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
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  newPayload.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    newPayload.filter.push({
      key: 'manageRegion.id',
      operator: 'LIKE',
      value: filterToSearch.value.search.id,
      logicalOperation: 'AND',
      type: 'filterSearch',
    })
  }
  payload.value = newPayload
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  // filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''

  getList()
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

async function getReportList(query: string) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'name',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'AND'
            },
          ],
          query: '',
          pageSize: 20,
          page: 0,
          sortBy: 'name',
          sortType: ENUM_SHORT_TYPE.DESC
        }

    const response = await GenericService.search('report', 'jasper-report-template', payload)
    const { data: dataList } = response
    reportList.value = []
    for (const iterator of dataList) {
      reportList.value = [...reportList.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading hotels list:', error)
  }
}

async function getDependedFieldList(query: string, id: string) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'id',
              operator: 'NOT_EQUALS',
              value: id,
              logicalOperation: 'AND'
            },
            {
              key: 'jasperReportTemplate.id',
              operator: 'EQUALS',
              value: props.reportConfig?.id || '',
              logicalOperation: 'AND'
            },
            {
              key: 'paramName',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'AND'
            },
          ],
          query: '',
          pageSize: 20,
          page: 0,
          sortBy: 'paramName',
          sortType: ENUM_SHORT_TYPE.DESC
        }

    const response = await GenericService.search('report', 'jasper-report-template-parameter', payload)
    const { data: dataList } = response
    dependentFieldList.value = []
    for (const iterator of dataList) {
      dependentFieldList.value = [
        ...dependentFieldList.value,
        {
          id: iterator.id,
          name: iterator.paramName,
        }
      ]
    }
  }
  catch (error) {
    console.error('Error on loading hotels list:', error)
  }
}

function showAndHideFieldInGetItemById(type: string) {
  switch (type) {
    case 'local-select':
      updateFieldProperty(fields, 'dataValueStatic', 'class', 'field col-12 required')
      updateFieldProperty(fields, 'dataValueStatic', 'validation', z.string().trim().min(1, 'The local data field is required'))
      updateFieldProperty(fields, 'dataValueStatic', 'disabled', false)
      updateFieldProperty(fields, 'dataValueStatic', 'hidden', false)

      updateFieldProperty(fields, 'module', 'class', 'field col-12')
      updateFieldProperty(fields, 'module', 'validation', z.string().trim())
      updateFieldProperty(fields, 'module', 'disabled', true)
      updateFieldProperty(fields, 'module', 'hidden', true)
      delete errorsListParent.module

      updateFieldProperty(fields, 'service', 'class', 'field col-12')
      updateFieldProperty(fields, 'service', 'validation', z.string().trim())
      updateFieldProperty(fields, 'service', 'disabled', true)
      updateFieldProperty(fields, 'service', 'hidden', true)
      delete errorsListParent.service

      updateFieldProperty(fields, 'dependentField', 'disabled', true)
      updateFieldProperty(fields, 'dependentField', 'hidden', true)
      delete errorsListParent.dependentField

      updateFieldProperty(fields, 'filterKeyValue', 'validation', z.string().trim())
      updateFieldProperty(fields, 'filterKeyValue', 'disabled', true)
      updateFieldProperty(fields, 'filterKeyValue', 'hidden', true)
      delete errorsListParent.filterKeyValue
      break
    case 'text':
      updateFieldProperty(fields, 'dataValueStatic', 'class', 'field col-12')
      updateFieldProperty(fields, 'dataValueStatic', 'validation', z.string().trim())
      updateFieldProperty(fields, 'dataValueStatic', 'disabled', true)
      updateFieldProperty(fields, 'dataValueStatic', 'hidden', true)
      delete errorsListParent.dataValueStatic

      updateFieldProperty(fields, 'module', 'class', 'field col-12')
      updateFieldProperty(fields, 'module', 'validation', z.string().trim())
      updateFieldProperty(fields, 'module', 'disabled', true)
      updateFieldProperty(fields, 'module', 'hidden', true)
      delete errorsListParent.module

      updateFieldProperty(fields, 'service', 'class', 'field col-12')
      updateFieldProperty(fields, 'service', 'validation', z.string().trim())
      updateFieldProperty(fields, 'service', 'disabled', true)
      updateFieldProperty(fields, 'service', 'hidden', true)
      delete errorsListParent.service

      updateFieldProperty(fields, 'dependentField', 'disabled', true)
      updateFieldProperty(fields, 'dependentField', 'hidden', true)
      delete errorsListParent.dependentField

      updateFieldProperty(fields, 'filterKeyValue', 'validation', z.string().trim())
      updateFieldProperty(fields, 'filterKeyValue', 'disabled', true)
      updateFieldProperty(fields, 'filterKeyValue', 'hidden', true)
      delete errorsListParent.filterKeyValue
      break
  }
}

async function getItemById(id: string) {
  if (!id) { return }

  // 1) Prepara el estado antes de cargar
  idItem.value = id
  loadingSaveAll.value = true
  localValuesFieldList.value = [] // limpia valores locales
  objLocalValue.value = { ...objLocalValueTemp } // resetea el form de añadir valor
  editingRows.value = [] // limpia filas en edición

  try {
    const response = await GenericService.getById(
      confApi.moduleApi,
      confApi.uriApi,
      id
    )

    if (response) {
      // 2) Mapea todo lo demás
      item.value = { ...response }
      item.value.id = response.id
      item.value.componentType = FORM_FIELD_TYPE.find(x => x.id === response.componentType)
      item.value.paramName = response.paramName || ''
      item.value.type = response.type
      item.value.module = response.module || ''
      item.value.service = response.service || ''
      item.value.label = response.label || ''
      item.value.parameterPosition = response.parameterPosition
      item.value.reportClass = response.reportClass || ''
      item.value.reportValidation = response.reportValidation || ''
      item.value.dependentField = response.dependentField
        ? JSON.parse(response.dependentField)
        : null
      item.value.filterKeyValue = response.filterKeyValue || ''
      item.value.reportId = response.jasperReportTemplate
        ? {
            id: response.jasperReportTemplate.id,
            name: response.jasperReportTemplate.name,
            status: response.jasperReportTemplate.status
          }
        : null
      item.value.dataValueStatic = response.dataValueStatic || '[]'

      // 3) Solo si es local-select, parsea el JSON en la lista
      if (response.componentType === 'local-select') {
        const arr = JSON.parse(response.dataValueStatic || '[]')
        localValuesFieldList.value = Array.isArray(arr) ? arr : []
      }

      // 4) Ajusta la visibilidad de campos según tipo
      showAndHideFieldInGetItemById(response.componentType)

      // 5) Fuerza recarga del formulario
      formReload.value++
    }
  }
  catch (error: any) {
    console.error(error)
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Item could not be loaded',
      life: 3000
    })
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.reportId = typeof payload.reportId === 'object' ? payload.reportId.id : payload.reportId
    payload.type = typeof payload.type === 'object' ? payload.type.id : payload.type
    payload.componentType = typeof payload.componentType === 'object' ? payload.componentType.id : payload.componentType
    payload.dependentField = payload.dependentField ? JSON.stringify(payload.dependentField) : ''
    payload.dataValueStatic = payload.componentType === FORM_FIELD_TYPE.find(x => x.id === 'local-select')?.id ? JSON.stringify(localValuesFieldList.value) : ''
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.reportId = typeof payload.reportId === 'object' ? payload.reportId.id : payload.reportId
  payload.componentType = typeof payload.componentType === 'object' ? payload.componentType.id : payload.componentType
  payload.dependentField = payload.dependentField ? JSON.stringify(payload.dependentField) : ''
  payload.dataValueStatic = payload.componentType === FORM_FIELD_TYPE.find(x => x.id === 'local-select')?.id ? JSON.stringify(localValuesFieldList.value) : ''
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
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

// function initData() {
//   item.value.reportId = props.reportConfig
// }

function addItemInLocalValuesList(item: IobjLocalValue) {
  if (!item.name || item.name.trim().length === 0) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Name in local value is required', life: 6000 })
    return
  }

  if (localValuesFieldList.value.find((i: any) => i.id === item.id || i.name === item.name)) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Item already exist', life: 6000 })
    return
  }

  if (localValuesFieldList.value.some((i: any) => i.defaultValue === true) && item.defaultValue === true) {
    // Solo se puede establacer un valor por defecto
    toast.add({ severity: 'error', summary: 'Error', detail: 'Only one default value can be set.', life: 6000 })
    return
  }

  const existingSlugs = new Set(localValuesFieldList.value.map(item => item.slug))

  localValuesFieldList.value = [...localValuesFieldList.value, { ...item, id: uuidv4(), slug: generateSlug(item.name, existingSlugs) }]

  objLocalValue.value = JSON.parse(JSON.stringify(objLocalValueTemp))
}

function deleteItemInLocalValuesList(id: string) {
  localValuesFieldList.value = localValuesFieldList.value.filter((item: any) => item.id !== id)
}

function onCellEditComplete(event) {
  const { data, newValue, field } = event
  switch (field) {
    case 'name':
      if (localValuesFieldList.value.some((i: any) => i.id !== data.id && i.name === newValue)) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Item already exist', life: 6000 })
        event.preventDefault()
      }
      else { data[field] = newValue }
      break

    case 'defaultValue':
      if (localValuesFieldList.value.some((i: any) => i.id !== data.id && i.defaultValue === true) && newValue) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Only one default value can be set.', life: 6000 })
        event.preventDefault()
      }
      else { data[field] = newValue }
      break

    default:
      if (newValue.trim().length > 0) {
        data[field] = newValue
      }
      else { event.preventDefault() }
      break
  }
}

function showAndHideField(eventValue: any, fields: any, onUpdate: any) {
  if (eventValue && (eventValue?.id === 'select' || eventValue?.id === 'multiselect')) {
    updateFieldProperty(fields, 'module', 'class', 'field col-12 required')
    updateFieldProperty(fields, 'module', 'validation', z.string().trim().min(1, 'The module field is required').max(50, 'Maximum 50 characters'))
    updateFieldProperty(fields, 'module', 'disabled', false)
    updateFieldProperty(fields, 'module', 'hidden', false)

    updateFieldProperty(fields, 'service', 'class', 'field col-12 required')
    updateFieldProperty(fields, 'service', 'validation', z.string().trim().min(1, 'The service field is required').max(50, 'Maximum 50 characters'))
    updateFieldProperty(fields, 'service', 'disabled', false)
    updateFieldProperty(fields, 'service', 'hidden', false)

    updateFieldProperty(fields, 'dependentField', 'disabled', false)
    updateFieldProperty(fields, 'dependentField', 'hidden', false)

    updateFieldProperty(fields, 'dataValueStatic', 'class', 'field col-12')
    updateFieldProperty(fields, 'dataValueStatic', 'validation', z.string().trim())
    updateFieldProperty(fields, 'dataValueStatic', 'disabled', true)
    updateFieldProperty(fields, 'dataValueStatic', 'hidden', true)
    onUpdate('dataValueStatic', '')
    delete errorsListParent.dataValueStatic

    updateFieldProperty(fields, 'filterKeyValue', 'validation', z.string().trim())
    updateFieldProperty(fields, 'filterKeyValue', 'disabled', true)
    updateFieldProperty(fields, 'filterKeyValue', 'hidden', false)
    onUpdate('filterKeyValue', '')
    delete errorsListParent.filterKeyValue
  }
  else if (eventValue && eventValue?.id === 'local-select') {
    updateFieldProperty(fields, 'dataValueStatic', 'class', 'field col-12 required')
    updateFieldProperty(fields, 'dataValueStatic', 'validation', z.string().trim().min(1, 'The local data field is required'))
    updateFieldProperty(fields, 'dataValueStatic', 'disabled', false)
    updateFieldProperty(fields, 'dataValueStatic', 'hidden', false)

    updateFieldProperty(fields, 'module', 'class', 'field col-12')
    updateFieldProperty(fields, 'module', 'validation', z.string().trim())
    updateFieldProperty(fields, 'module', 'disabled', true)
    updateFieldProperty(fields, 'module', 'hidden', true)
    onUpdate('module', '')
    delete errorsListParent.module

    updateFieldProperty(fields, 'service', 'class', 'field col-12')
    updateFieldProperty(fields, 'service', 'validation', z.string().trim())
    updateFieldProperty(fields, 'service', 'disabled', true)
    updateFieldProperty(fields, 'service', 'hidden', true)
    onUpdate('service', '')
    delete errorsListParent.service

    updateFieldProperty(fields, 'dependentField', 'disabled', true)
    updateFieldProperty(fields, 'dependentField', 'hidden', true)
    onUpdate('dependentField', null)
    delete errorsListParent.dependentField

    updateFieldProperty(fields, 'filterKeyValue', 'validation', z.string().trim())
    updateFieldProperty(fields, 'filterKeyValue', 'disabled', true)
    updateFieldProperty(fields, 'filterKeyValue', 'hidden', true)
    onUpdate('filterKeyValue', '')
    delete errorsListParent.filterKeyValue
  }
  else {
    updateFieldProperty(fields, 'dataValueStatic', 'class', 'field col-12')
    updateFieldProperty(fields, 'dataValueStatic', 'validation', z.string().trim())
    updateFieldProperty(fields, 'dataValueStatic', 'disabled', true)
    updateFieldProperty(fields, 'dataValueStatic', 'hidden', true)
    onUpdate('dataValueStatic', '')
    delete errorsListParent.dataValueStatic

    updateFieldProperty(fields, 'module', 'class', 'field col-12')
    updateFieldProperty(fields, 'module', 'validation', z.string().trim())
    updateFieldProperty(fields, 'module', 'disabled', true)
    updateFieldProperty(fields, 'module', 'hidden', true)
    onUpdate('module', '')
    delete errorsListParent.module

    updateFieldProperty(fields, 'service', 'class', 'field col-12')
    updateFieldProperty(fields, 'service', 'validation', z.string().trim())
    updateFieldProperty(fields, 'service', 'disabled', true)
    updateFieldProperty(fields, 'service', 'hidden', true)
    onUpdate('service', '')
    delete errorsListParent.service

    updateFieldProperty(fields, 'filterKeyValue', 'validation', z.string().trim())
    updateFieldProperty(fields, 'filterKeyValue', 'disabled', true)
    updateFieldProperty(fields, 'filterKeyValue', 'hidden', true)
    onUpdate('filterKeyValue', '')
    delete errorsListParent.filterKeyValue

    updateFieldProperty(fields, 'dependentField', 'disabled', true)
    updateFieldProperty(fields, 'dependentField', 'hidden', true)
    onUpdate('dependentField', null)
    delete errorsListParent.dependentField
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
  clearForm()
  // filterToSearch.value.criterial = ENUM_FILTER[0]

  await getList()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      {{ options.tableName }}
    </h3>
    <IfCan :perms="['AGENCY:CREATE']">
      <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
        <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
      </div>
    </IfCan>
  </div>
  <div class="grid">
    <div class="col-12 md:order-1 md:col-6 xl:col-8">
      <DynamicTable
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        row-key="id"
        @on-confirm-create="clearForm"
        @update:clicked-item="getItemById($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      />
    </div>
    <div class="col-12 md:order-0 md:col-6 xl:col-4">
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
            :error-list="errorsListParent"
            :loading-save="loadingSaveAll"
            :loading-delete="loadingDelete"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
            @update:errors-list="errorsListParent = $event"
          >
            <template #field-reportId="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.reportId"
                :suggestions="reportList"
                :disabled="fields[0].disabled"
                @change="($event) => {
                  onUpdate('reportId', $event)
                }"
                @load="($event) => getReportList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-componentType="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll"
                v-model="data.componentType"
                :options="[...FORM_FIELD_TYPE]"
                option-label="name"
                :return-object="false"
                @update:model-value="($event) => {
                  onUpdate('componentType', $event)
                  // if ($event && $event?.id !== 'local-select') {
                  //   localValuesFieldList = []
                  // }

                  showAndHideField($event, fields, onUpdate)

                  // if ($event && ($event?.id === 'select' || $event?.id === 'multiselect')) {
                  //   updateFieldProperty(fields, 'module', 'class', 'field col-12 required')
                  //   updateFieldProperty(fields, 'module', 'validation', z.string().trim().min(1, 'The module field is required').max(50, 'Maximum 50 characters'))
                  //   updateFieldProperty(fields, 'module', 'disabled', false)

                  //   updateFieldProperty(fields, 'service', 'class', 'field col-12 required')
                  //   updateFieldProperty(fields, 'service', 'validation', z.string().trim().min(1, 'The service field is required').max(50, 'Maximum 50 characters'))
                  //   updateFieldProperty(fields, 'service', 'disabled', false)

                  //   updateFieldProperty(fields, 'dependentField', 'disabled', false)

                  //   updateFieldProperty(fields, 'dataValueStatic', 'class', 'field col-12')
                  //   updateFieldProperty(fields, 'dataValueStatic', 'validation', z.string().trim())
                  //   updateFieldProperty(fields, 'dataValueStatic', 'disabled', true)
                  //   updateFieldProperty(fields, 'dataValueStatic', 'hidden', true)
                  //   onUpdate('dataValueStatic', '')
                  //   delete errorsListParent.dataValueStatic
                  // }
                  // else if ($event && $event?.id === 'local-select') {
                  //   updateFieldProperty(fields, 'dataValueStatic', 'class', 'field col-12 required')
                  //   updateFieldProperty(fields, 'dataValueStatic', 'validation', z.string().trim().min(1, 'The local data field is required'))
                  //   updateFieldProperty(fields, 'dataValueStatic', 'disabled', false)
                  //   updateFieldProperty(fields, 'dataValueStatic', 'hidden', false)

                  //   updateFieldProperty(fields, 'module', 'class', 'field col-12')
                  //   updateFieldProperty(fields, 'module', 'validation', z.string().trim())
                  //   updateFieldProperty(fields, 'module', 'disabled', true)
                  //   updateFieldProperty(fields, 'module', 'hidden', true)
                  //   onUpdate('module', '')
                  //   delete errorsListParent.module

                  //   updateFieldProperty(fields, 'service', 'class', 'field col-12')
                  //   updateFieldProperty(fields, 'service', 'validation', z.string().trim())
                  //   updateFieldProperty(fields, 'service', 'disabled', true)
                  //   updateFieldProperty(fields, 'service', 'hidden', true)
                  //   onUpdate('service', '')
                  //   delete errorsListParent.service

                  //   updateFieldProperty(fields, 'dependentField', 'disabled', true)
                  //   updateFieldProperty(fields, 'dependentField', 'hidden', true)
                  //   onUpdate('dependentField', null)
                  //   delete errorsListParent.dependentField

                  //   updateFieldProperty(fields, 'filterKeyValue', 'validation', z.string().trim())
                  //   updateFieldProperty(fields, 'filterKeyValue', 'disabled', true)
                  //   updateFieldProperty(fields, 'filterKeyValue', 'hidden', true)
                  //   onUpdate('filterKeyValue', '')
                  //   delete errorsListParent.filterKeyValue
                  // }
                  // else {
                  //   updateFieldProperty(fields, 'dataValueStatic', 'class', 'field col-12')
                  //   updateFieldProperty(fields, 'dataValueStatic', 'validation', z.string().trim())
                  //   updateFieldProperty(fields, 'dataValueStatic', 'disabled', true)
                  //   updateFieldProperty(fields, 'dataValueStatic', 'hidden', true)
                  //   onUpdate('dataValueStatic', '')
                  //   delete errorsListParent.dataValueStatic

                  //   updateFieldProperty(fields, 'module', 'class', 'field col-12')
                  //   updateFieldProperty(fields, 'module', 'validation', z.string().trim())
                  //   updateFieldProperty(fields, 'module', 'disabled', true)
                  //   onUpdate('module', '')
                  //   delete errorsListParent.module

                  //   updateFieldProperty(fields, 'service', 'class', 'field col-12')
                  //   updateFieldProperty(fields, 'service', 'validation', z.string().trim())
                  //   updateFieldProperty(fields, 'service', 'disabled', true)
                  //   onUpdate('service', '')
                  //   delete errorsListParent.service

                  //   updateFieldProperty(fields, 'dependentField', 'disabled', true)
                  //   onUpdate('dependentField', null)
                  //   delete errorsListParent.dependentField
                  // }
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-dependentField="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.dependentField"
                :disabled="fields.find((f) => f.field === 'dependentField')?.disabled"
                :suggestions="dependentFieldList"
                @change="($event) => {
                  onUpdate('dependentField', $event)
                }"
                @load="($event) => getDependedFieldList($event, idItem)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>

            <template #field-dataValueStatic="{ item: data, onUpdate }">
              <div>
                <div class="flex justify-content-between align-items-center gap-2 my-1">
                  <InputText
                    v-model="objLocalValue.name"
                    placeholder="Type here the local value"
                    @keyup.enter="() => {
                      addItemInLocalValuesList(objLocalValue)
                      onUpdate('dataValueStatic', JSON.stringify(localValuesFieldList))
                    }"
                  />
                  <div class="flex align-items-center mr-2">
                    <Checkbox
                      v-model="objLocalValue.defaultValue"
                      v-tooltip.top="'Default'"
                      input-id="defaultValue"
                      :binary="true"
                      @keyup.enter="() => {
                        addItemInLocalValuesList(objLocalValue)
                        onUpdate('dataValueStatic', JSON.stringify(localValuesFieldList))
                      }"
                    />
                    <!-- <label for="defaultValue" class="ml-2"> Default </label> -->
                  </div>
                  <Button
                    v-tooltip.top="'Save'"
                    class="w-3rem"
                    icon="pi pi-plus"
                    :loading="loadingSaveAll"
                    @click="() => {
                      addItemInLocalValuesList(objLocalValue)
                      onUpdate('dataValueStatic', JSON.stringify(localValuesFieldList))
                    }"
                  />
                </div>

                <DataTable
                  v-model:editingRows="editingRows"
                  :value="localValuesFieldList"
                  show-gridlines
                  class="mb-3"
                  scrollable
                  scroll-height="200px"
                  edit-mode="cell"
                  data-key="id"
                  @cell-edit-complete="onCellEditComplete"
                >
                  <template #empty>
                    <div class="flex flex-column flex-wrap align-items-center justify-content-center py-4">
                      <span class="flex flex-column align-items-center justify-content-center">
                        <div class="row">
                          <i class="pi pi-trash mb-3" style="font-size: 1.5rem;" />
                        </div>
                        <div class="row">
                          <p> There are no local values </p>
                        </div>
                      </span>
                    </div>
                  </template>
                  <Column field="name" header="Name">
                    <template #editor="{ data: objData, field }">
                      <InputText v-model="objData[field]" />
                    </template>
                  </Column>
                  <Column field="defaultValue" header="Default" class="text-center" style="width: 5rem">
                    <template #body="{ data: objData }">
                      <i v-if="objData.defaultValue" class="pi" :class="{ 'pi-check-circle text-green-500': objData.defaultValue, 'pi-times-circle text-red-400': !objData.defaultValue }" />
                    </template>
                    <template #editor="{ data: objData, field }">
                      <Checkbox v-model="objData[field]" v-tooltip.top="'Default'" :binary="true" />
                    </template>
                  </Column>
                  <Column :exportable="false" style="width: 3rem; min-width:3rem">
                    <template #body="slotProps">
                      <span v-tooltip.top="`Delete ${slotProps.data.name}`" class="flex justify-content-center">
                        <i class="pi pi-trash text-red-400" style="font-size: 1rem" @click="deleteItemInLocalValuesList(slotProps.data.id)" />
                      </span>
                    </template>
                  </Column>
                </DataTable>
              </div>
            </template>

            <template #form-footer="props">
              <div class="flex justify-content-end">
                <IfCan :perms="idItem ? ['AGENCY:EDIT'] : ['AGENCY:CREATE']">
                  <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" @click="props.item.submitForm($event)" />
                </IfCan>
                <IfCan :perms="['AGENCY:DELETE']">
                  <Button
                    v-tooltip.top="'Delete'"
                    class="w-3rem"
                    severity="danger"
                    outlined
                    :disabled="idItem === null || idItem === undefined || idItem === ''"
                    :loading="loadingDelete"
                    icon="pi pi-trash"
                    @click="props.item.deleteItem($event)"
                  />
                </IfCan>
              </div>
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
  </div>
</template>
