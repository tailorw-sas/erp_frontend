<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import dayjs from 'dayjs'
import type { IData } from '~/components/table/interfaces/IModelData'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'

const formReload = ref(0)
const formReloadScheduler = ref(0)
const reloadIntervalTypeCombo = ref(0)
const reloadInterval = ref(0)
const reloadExecutionDateTypeCombo = ref(0)
const businessProcessVisible = ref(false)
const showOnlyActiveRecords = ref(false)
const loadingSearchBusinessProcess = ref(false)
const loadingSearchScheduler = ref(false)
const toast = useToast()
const idItemToLoadFirstTimeBP = ref('')
const idItemToLoadFirstTimeScheduler = ref('')
const listItemsBusinessProcess = ref<any[]>([])
const listItemsScheduler = ref<any[]>([])
const payloadOnChangePage = ref<PageState>()
const loadingSaveBusinessProcess = ref(false)
const loadingSaveScheduler = ref(false)
const loadingDelete = ref(false)
const idItemBusinessProcess = ref('')
const idItemScheduler = ref('')
const confirm = useConfirm()
const listBusinessProcessItems = ref<any[]>([])
const listFrequencyItems = ref<any[]>([])
const listIntervalTypeItems = ref<any[]>([])
const listExecutionDateTypeItems = ref<any[]>([])
const listProcessingDateTypeItems = ref<any[]>([])
const intervalList = ref<any[]>([])

const disableFrequency = ref(false)
const disableIntervalType = ref(false)
const disableExecutionDateType = ref(false)
const disableProcessingDateType = ref(false)
const disableInterval = ref(false)
const disableExecutionDateValue = ref(false)
const disableExecutionDate = ref(false)
const disableExecutionTime = ref(false)
const disableProcessingDateValue = ref(false)
const disableProcessingDate = ref(false)

const frequencySelected = ref<any>([])
const intervalTypeSelected = ref<any>([])
const interval = ref('')
const executionDateType = ref<any>([])
const executionDateValue = ref('')
// const executionDate = ref<Date>(new Date())
const executionDate = ref()
const executionTime = ref()
const processingDateType = ref<any>([])
const processingDateValue = ref('')
// const processingDate = ref<Date>(new Date())
const processingDate = ref()

const menu = ref()
const menuItems = ref([
  {
    label: 'Manage'
  }
])

// VARIABLES BUSINESS PROCESS
const formTitleBusinessProcess = computed(() => {
  if (idItemBusinessProcess.value !== '') {
    return 'Edit'
  }

  return 'Add'
})

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const payloadBusinessProcess = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const paginationBusinessProcess = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const itemBusinessProcess = ref<GenericObject>({
  id: '',
  code: '',
  processName: '',
  description: '',
  status: false
})

const itemTempBusinessProcess = ref<GenericObject>({
  id: '',
  code: '',
  processName: '',
  description: '',
  status: true
})

// VARIABLES SCHEDULER
const formTitleScheduler = computed(() => {
  if (idItemScheduler.value !== '') {
    return 'Edit'
  }

  return 'Add'
})

const payloadScheduler = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const paginationScheduler = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const itemScheduler = ref<GenericObject>({
  id: '',
  frequency: '',
  intervalType: '',
  interval: '',
  executionDateType: null,
  executionDate: '',
  executionTime: '',
  processingDateType: '',
  processingDateValue: '',
  processingDate: '',
  params: '',
  lastExecutionDatetime: '',
  isInProcess: '',
  process: null,
  allowsQueueing: '',
  status: false
})

const itemTempScheduler = ref<GenericObject>({
  id: '',
  frequency: '',
  intervalType: '',
  interval: '',
  executionDateType: null,
  executionDate: '',
  executionTime: '',
  processingDateType: '',
  processingDateValue: '',
  processingDate: '',
  params: '',
  lastExecutionDatetime: '',
  isInProcess: '',
  process: null,
  allowsQueueing: true,
  status: true
})

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const optionsBusinessProcess = ref({
  tableName: 'Scheduler Management',
  moduleApi: 'scheduler',
  uriApi: 'business-process',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Are you sure you want to delete this item?'
})

const optionsScheduler = ref({
  tableName: 'Scheduler Management',
  moduleApi: 'scheduler',
  uriApi: 'business-process-scheduler',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Are you sure you want to delete this item?'
})

const objApis = ref({
  frequencyCatalog: { moduleApi: 'scheduler', uriApi: 'frequency' },
  intervalTypeCatalog: { moduleApi: 'scheduler', uriApi: 'interval-type' },
  executionDateTypeCatalog: { moduleApi: 'scheduler', uriApi: 'executing-date-type' },
  processingDateTypeCatalog: { moduleApi: 'scheduler', uriApi: 'processing-date-type' },
  businessProcessSchedulerRule: { moduleApi: 'scheduler', uriApi: 'business-process-scheduler-rule' },
  businessProcessSchedulerProcessingRule: { moduleApi: 'scheduler', uriApi: 'business-process-scheduler-processing-rule' }
})

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'processName', name: 'Name' }
]

const columnsBusinessProcess: IColumn[] = [
  { field: 'code', header: 'Code', type: 'text' },
  { field: 'processName', header: 'Process Name', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]

const fieldsBusinessProcess: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    dataType: 'code',
    class: 'field col-12 required',
    disabled: false,
    validation: z.string().min(1, 'The code field is required').max(20, 'Maximum 1 character').regex(/^[\w-]+$/, 'Only letters and numbers are allowed')
  },
  {
    field: 'processName',
    header: 'Process Name',
    dataType: 'code',
    class: 'field col-12 required',
    disabled: false,
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12 required',
    disabled: false,
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    class: 'field col-12 required',
    disabled: true,
  }
]

// SCHEDULER
const columnsScheduler: IColumn[] = [
  { field: 'process', header: 'Business Process', type: 'select', objApi: { moduleApi: optionsBusinessProcess.value.moduleApi, uriApi: optionsBusinessProcess.value.uriApi, keyValue: 'processName' } },
  { field: 'frequency', header: 'Frequency', type: 'select', objApi: { moduleApi: objApis.value.frequencyCatalog.moduleApi, uriApi: objApis.value.frequencyCatalog.uriApi, keyValue: 'code' } },
  { field: 'intervalType', header: 'Interval Type', type: 'select', objApi: { moduleApi: objApis.value.intervalTypeCatalog.moduleApi, uriApi: objApis.value.intervalTypeCatalog.uriApi, keyValue: 'code' } },
  { field: 'interval', header: 'Interval', type: 'text' },
  { field: 'executionDateType', header: 'Execution Date', type: 'select', objApi: { moduleApi: objApis.value.executionDateTypeCatalog.moduleApi, uriApi: objApis.value.executionDateTypeCatalog.uriApi, keyValue: 'code' } },
  { field: 'processingDateType', header: 'Processing Date', type: 'select', objApi: { moduleApi: objApis.value.processingDateTypeCatalog.moduleApi, uriApi: objApis.value.processingDateTypeCatalog.uriApi, keyValue: 'code' } },
  { field: 'status', header: 'Active', type: 'bool' },
]

const fieldsScheduler: Array<FieldDefinitionType> = [
  {
    field: 'process',
    header: 'Business Process',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: false,
    // validation: validateEntityStatus('process')
  },
  {
    field: 'frequency',
    header: 'Frequency',
    dataType: 'select',
    class: 'field col-12 required',
    disabled: false,
  },
  {
    field: 'interval-custom',
    header: '',
    dataType: 'text',
    class: 'field col-12',
    disabled: false,
  },
  {
    field: 'executionDateType',
    header: 'Execution Date Type',
    dataType: 'select',
    class: 'field col-12',
    disabled: disableExecutionDateType.value,
  },
  {
    field: 'execution-custom',
    header: '',
    dataType: 'text',
    class: 'field col-12',
    disabled: false,
  },
  /* {
    field: 'executionDateValue',
    header: 'Execution Date Value',
    dataType: 'text',
    class: 'field col-12',
    disabled: disableExecutionDateValue.value,
  },
  {
    field: 'executionDate',
    header: 'Execution Date',
    dataType: 'text',
    class: 'field col-12',
    disabled: disableExecutionDate.value,
  },
  {
    field: 'executionTime',
    header: 'Execution Time',
    dataType: 'text',
    class: 'field col-12',
    disabled: disableExecutionTime.value,
  }, */
  {
    field: 'processingDateType',
    header: 'Processing Date Type',
    dataType: 'select',
    class: 'field col-12',
    disabled: disableProcessingDateType.value,
  },
  {
    field: 'processing-custom',
    header: '',
    dataType: 'text',
    class: 'field col-12',
    disabled: false,
  },
  /* {
    field: 'processingDateValue',
    header: 'Processing Date Value',
    dataType: 'text',
    class: 'field col-12',
    disabled: disableProcessingDateValue.value,
  },
  {
    field: 'processingDate',
    header: 'Processing Date',
    dataType: 'text',
    class: 'field col-12',
    disabled: disableProcessingDate.value,
  }, */
  {
    field: 'params',
    header: 'Additional Parameters',
    dataType: 'textarea',
    class: 'field col-12',
    disabled: false,
  },
  {
    field: 'allowsQueueing',
    header: 'Allows Queueing',
    dataType: 'check',
    class: 'field col-12',
    disabled: false,
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    class: 'field col-12',
    disabled: false,
  }
]

// FUNCTIONS -----------------------------------------------------------------------------------------
function showMenu(event: any) {
  menu.value.toggle(event)
}

function openBusinessProcessSettings(event: any) {
  businessProcessVisible.value = event
}

function addStatusFilter(filterArray: IFilter[]) {
  if (showOnlyActiveRecords.value) {
    filterArray.push({
      key: 'status',
      operator: 'EQUALS',
      value: 'ACTIVE',
      logicalOperation: 'AND',
      type: 'filterSearch',
    })
  }
}

async function getBusinessProcessFilter(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(optionsBusinessProcess.value.moduleApi, optionsBusinessProcess.value.uriApi, payload)

    const { data: dataList } = response
    listBusinessProcessItems.value = []
    const existingIds = new Set(listBusinessProcessItems.value.map(item => item.id))
    for (const iterator of dataList) {
      if (!existingIds.has(iterator.id)) {
        listBusinessProcessItems.value = [...listBusinessProcessItems.value, { id: iterator.id, name: `${iterator.code || ''} - ${iterator.processName || ''}`, status: iterator.status }]
      }
    }
  }
  catch (error) {
    console.error('Error on loading Frequency list:', error)
  }
}

async function getFrequencyFilter(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'code',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(objApis.value.frequencyCatalog.moduleApi, objApis.value.frequencyCatalog.uriApi, payload)

    const { data: dataList } = response
    listFrequencyItems.value = []
    const existingIds = new Set(listFrequencyItems.value.map(item => item.id))
    for (const iterator of dataList) {
      if (!existingIds.has(iterator.id)) {
        listFrequencyItems.value = [...listFrequencyItems.value, { id: iterator.id, name: iterator.code, status: iterator.status }]
      }
    }
  }
  catch (error) {
    console.error('Error on loading Frequency list:', error)
  }
}

// ON EVENT CLICK

async function onProcessSelected() {
  if (disableFrequency.value) {
    disableFrequency.value = false
  }
}

async function onFrequencySelected(event: any) {
  frequencySelected.value = event

  // Interval
  disableIntervalType.value = false
  intervalTypeSelected.value = {
    id: '',
    name: '',
    status: true
  }
  disableInterval.value = true
  interval.value = ''

  // Execution
  disableExecutionDateType.value = true
  disableExecutionDate.value = true
  disableExecutionTime.value = true
  disableExecutionDateValue.value = true

  executionDateType.value = {
    id: '',
    name: '',
    status: true
  }
  executionDateValue.value = ''
  executionDate.value = ''
  executionTime.value = ''

  // Processing
  disableProcessingDateType.value = false
  disableProcessingDateValue.value = true
  disableProcessingDate.value = true

  processingDateType.value = {
    id: '',
    name: '',
    status: true
  }

  processingDateValue.value = ''
  processingDate.value = ''
}

async function onIntervalTypeSelected(event: any) {
  intervalTypeSelected.value = event
  disableExecutionDateType.value = false
  interval.value = ''
  await loadInterval()
}

async function loadInterval() {
  try {
    listExecutionDateTypeItems.value = []
    const payload = {
      filter: [{
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }, {
        key: 'frequency.id',
        operator: 'EQUALS',
        value: frequencySelected.value.id,
        logicalOperation: 'AND'
      }, {
        key: 'intervalType.id',
        operator: 'EQUALS',
        value: intervalTypeSelected.value.id,
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 50,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await getSchedulerBusinessRules(payload)
    const { data: dataList } = response

    const intervalRuleList = new Set(dataList.map((rule: any) => rule.enableInterval))

    if (intervalRuleList.size > 1) {
      disableInterval.value = false
    }
    else {
      const valor = intervalRuleList.values().next().value
      disableInterval.value = !valor
    }

    const executionDateTypeEnableRules = dataList.map((rule: any) => rule.enableExecutionDateType)
    loadExecutionDateType(executionDateTypeEnableRules)

    const executionDateValueRules = dataList.map((rule: any) => rule.enableExecutionDateValue)
    loadExecutionDateValue(executionDateValueRules)

    const executionDateRules = dataList.map((rule: any) => rule.enableExecutionDate)
    loadExecutionDate(executionDateRules)

    const executionTimeRules = dataList.map((rule: any) => rule.enableExecutionTime)
    loadExecutionTime(executionTimeRules)

    reloadInterval.value += 1
  }
  catch (error) {
    console.error('Error on loading ExecutionDateType Items:', error)
  }
}

async function onprocessingDateType(event: any) {
  processingDateType.value = event
  processingDate.value = ''
  try {
    const payload = {
      filter: [{
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }, {
        key: 'frequency.id',
        operator: 'EQUALS',
        value: frequencySelected.value.id,
        logicalOperation: 'AND'
      }, {
        key: 'processingDateType.id',
        operator: 'EQUALS',
        value: processingDateType.value.id,
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 50,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(objApis.value.businessProcessSchedulerProcessingRule.moduleApi, objApis.value.businessProcessSchedulerProcessingRule.uriApi, payload)
    const { data: dataList } = response

    const enableProcessingDateValueRules = dataList.map((rule: any) => rule.enableProcessingDateValue)
    loadProcessingDateValue(enableProcessingDateValueRules)

    const enableProcessingDateRules = dataList.map((rule: any) => rule.enableProcessingDate)
    loadProcessingDate(enableProcessingDateRules)
  }
  catch (error) {
    console.error(`There is a problem when try to load processingDateValue and processingDate fields. Error: ${error}`)
  }
}

/// ///////LOAD SCHEDULER FIELDS

function loadExecutionDateType(results: any[]) {
  if (results.length > 1) {
    disableExecutionDateType.value = false
  }
  else {
    disableExecutionDateType.value = !results.values().next().value
  }
}

function loadExecutionDateValue(results: any[]) {
  if (results.length > 1) {
    disableExecutionDateValue.value = false
  }
  else {
    disableExecutionDateValue.value = !results.values().next().value
  }
}

function loadExecutionDate(results: any[]) {
  if (results.length > 1) {
    disableExecutionDate.value = false
  }
  else {
    disableExecutionDate.value = !results.values().next().value
  }
}

function loadExecutionTime(results: any[]) {
  if (results.length > 1) {
    disableExecutionTime.value = false
  }
  else if (results.length === 1) {
    disableExecutionTime.value = !results.values().next().value
  }
  else {
    disableExecutionTime.value = false
  }
}

function loadProcessingDateValue(results: any[]) {
  if (results.length > 1) {
    disableProcessingDateValue.value = true
  }
  else if (results.length === 1) {
    disableProcessingDateValue.value = !results[0]
  }
  else {
    disableProcessingDateValue.value = true // Si el array está vacío
  }
}

function loadProcessingDate(results: any[]) {
  if (results.length > 1) {
    disableProcessingDate.value = true
  }
  else if (results.length === 1) {
    disableProcessingDate.value = !results[0]
  }
  else {
    disableProcessingDate.value = true
  }
}

async function getSchedulerBusinessRules(payload: any) {
  try {
    const response = await GenericService.search(objApis.value.businessProcessSchedulerRule.moduleApi, objApis.value.businessProcessSchedulerRule.uriApi, payload)
    // const { data: dataList } = response

    return response
  }
  catch (error) {
    console.error('Error on loading ExecutionDateType Items:', error)
  }
}

/// ////////////////////FILTERS SEARCH

async function getIntervalTypeFilter(query: string) {
  try {
    listIntervalTypeItems.value = []
    const payload = {
      filter: [{
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }, {
        key: 'frequency.id',
        operator: 'EQUALS',
        value: frequencySelected.value.id,
        logicalOperation: 'AND'
      }, {
        key: 'intervalType.code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 50,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    // console.log(payload)
    const response = await GenericService.search(objApis.value.businessProcessSchedulerRule.moduleApi, objApis.value.businessProcessSchedulerRule.uriApi, payload)
    // console.log(response)

    const { data: dataList } = response
    const intervalTypeList = new Set(dataList.map(rule => rule.intervalType))
    // console.log(intervalTypeList)

    for (const iterator of intervalTypeList) {
      listIntervalTypeItems.value = [...listIntervalTypeItems.value, { id: iterator.id, name: iterator.code, status: iterator.status }]
    }

    reloadIntervalTypeCombo.value += 1
  }
  catch (error) {
    console.error('Error on loading Interval Type list:', error)
  }
}

function getIntervalList() {
  if (intervalTypeSelected.value.name === 'MINUTE') {
    for (let i = 0; i < 60; i++) {
      const element = {
        id: i,
        code: i.toString(),
        name: i.toString()
      }
      intervalList.value.push(element)
    }
  }
  else if (intervalTypeSelected.value.name === 'HOUR') {
    for (let i = 0; i < 12; i++) {
      const element = {
        id: i,
        code: i.toString(),
        name: i.toString()
      }
      intervalList.value.push(element)
    }
  }
}

async function getExecutionTypeFilter(query: string) {
  try {
    listExecutionDateTypeItems.value = []
    const payload = {
      filter: [{
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }, {
        key: 'frequency.id',
        operator: 'EQUALS',
        value: frequencySelected.value.id,
        logicalOperation: 'AND'
      }, {
        key: 'intervalType.id',
        operator: 'EQUALS',
        value: intervalTypeSelected.value.id,
        logicalOperation: 'AND'
      }, {
        key: 'executionDateType.code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(objApis.value.businessProcessSchedulerRule.moduleApi, objApis.value.businessProcessSchedulerRule.uriApi, payload)
    const { data: dataList } = response

    const executionDateTypeList = new Set(dataList.map(rule => rule.executionDateType))

    for (const iterator of executionDateTypeList) {
      listExecutionDateTypeItems.value = [...listExecutionDateTypeItems.value, { id: iterator.id, name: iterator.code, status: iterator.status }]
    }

    reloadExecutionDateTypeCombo.value += 1
  }
  catch (error) {
    console.error('Error on loading Execution Date Type list:', error)
  }
}

async function getProcessingDateTypeFilter(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }, {
        key: 'frequency.id',
        operator: 'EQUALS',
        value: frequencySelected.value.id,
        logicalOperation: 'AND'
      }, {
        key: 'processingDateType.code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(objApis.value.businessProcessSchedulerProcessingRule.moduleApi, objApis.value.businessProcessSchedulerProcessingRule.uriApi, payload)
    const { data: dataList } = response

    listProcessingDateTypeItems.value = []
    const existingIds = new Set(listProcessingDateTypeItems.value.map(item => item.id))
    for (const iterator of dataList) {
      if (!existingIds.has(iterator.id)) {
        listProcessingDateTypeItems.value = [...listProcessingDateTypeItems.value, { id: iterator.processingDateType?.id, name: iterator.processingDateType?.code, status: iterator.status }]
      }
    }
  }
  catch (error) {
    console.error('Error on loading Processing Date Type list:', error)
  }
}

// BUSINESS PROCESS

function searchAndFilterBusinessProcess() {
  if (!filterToSearch.value.criterial || filterToSearch.value.search.trim() === '') {
    toast.add({ severity: 'warn', summary: 'Alert', detail: 'Please fill out all the fields before searching.', life: 3000 })
    return
  }

  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 100,
    page: 0,
    sortBy: '',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  // Mantener los filtros existentes
  newPayload.filter = [
    ...payloadBusinessProcess.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')
  ]

  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    newPayload.filter.push({
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    })
  }

  payloadBusinessProcess.value = newPayload
  getListBusinessProcess()
}

async function getListBusinessProcess() {
  if (optionsBusinessProcess.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    addStatusFilter(payloadBusinessProcess.value.filter)
    idItemToLoadFirstTimeBP.value = ''
    optionsBusinessProcess.value.loading = true
    listItemsBusinessProcess.value = []
    const newListItems = []

    const response = await GenericService.search(optionsBusinessProcess.value.moduleApi, optionsBusinessProcess.value.uriApi, payloadBusinessProcess.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationBusinessProcess.value.page = page
    paginationBusinessProcess.value.limit = size
    paginationBusinessProcess.value.totalElements = totalElements
    paginationBusinessProcess.value.totalPages = totalPages

    const existingIds = new Set(listItemsBusinessProcess.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }
      const response = await GenericService.search(optionsBusinessProcess.value.moduleApi, optionsBusinessProcess.value.uriApi, payloadBusinessProcess.value)
      if (response) {
        // Verificar si el ID ya existe en la lista
        if (!existingIds.has(iterator.id)) {
          const newItem = {
            ...iterator,
            loadingEdit: false,
            loadingDelete: false,
          }
          newListItems.push(newItem)
          existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
        }
      }
    }

    listItemsBusinessProcess.value = [...listItemsBusinessProcess.value, ...newListItems]

    if (listItemsBusinessProcess.value.length > 0) {
      idItemToLoadFirstTimeBP.value = listItemsBusinessProcess.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    optionsBusinessProcess.value.loading = false
  }
}

async function getItemByIdBusinessProcess(id: string) {
  if (id) {
    try {
      const response = await GenericService.getById(optionsBusinessProcess.value.moduleApi, optionsBusinessProcess.value.uriApi, id)
      if (response) {
        itemBusinessProcess.value.id = response.id
        itemBusinessProcess.value.code = response.code
        itemBusinessProcess.value.processName = response.processName
        itemBusinessProcess.value.description = response.description
        itemBusinessProcess.value.status = statusToBoolean(response.status)
      }

      fieldsBusinessProcess[0].disabled = true
      updateFieldProperty(fieldsBusinessProcess, 'status', 'disabled', false)
      formReload.value += 1
      idItemBusinessProcess.value = itemBusinessProcess.value.id
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: `Innsist Connection could not be loaded: ${error}`, life: 3000 })
      }
    }
    finally {
      loadingSaveBusinessProcess.value = false
    }
  }
}

async function parseDataTableFilterBusinessProcess(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsBusinessProcess)
  payloadBusinessProcess.value.filter = [...payloadBusinessProcess.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payloadBusinessProcess.value.filter = [...payloadBusinessProcess.value.filter, ...parseFilter || []]
  getList()
}

function onSortFieldBusinessProcess(event: any) {
  if (event) {
    payloadBusinessProcess.value.sortBy = event.sortField
    payloadBusinessProcess.value.sortType = event.sortOrder
    parseDataTableFilterBusinessProcess(event.filter)
  }
}

function clearFilterToSearchBusinessProcess() {
  payloadBusinessProcess.value.filter = [...payloadBusinessProcess.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]

  // Reiniciar los valores de búsqueda a sus estados iniciales
  filterToSearch.value = {
    criterial: ENUM_FILTER[0],
    search: '',
  }
  listItemsBusinessProcess.value = []
  paginationBusinessProcess.value.totalElements = 0
  getListBusinessProcess()
}

function clearFormBusinessProcess() {
  itemBusinessProcess.value = { ...itemTempBusinessProcess.value }
  idItemBusinessProcess.value = ''
  updateFieldProperty(fieldsBusinessProcess, 'status', 'disabled', true)
  updateFieldProperty(fieldsBusinessProcess, 'code', 'disabled', false)
  formReload.value++
}

async function requireConfirmationToSaveBusinessProcess(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    await saveBusinessProcess(item)
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
        saveBusinessProcess(item)
      },
      reject: () => {
        toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
      }
    })
  }
}

function requireConfirmationToDeleteBusinessProcess(event: any) {
  if (!useRuntimeConfig().public.showDeleteConfirm) {
    deleteBusinessProcess(idItemBusinessProcess.value)
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
        deleteBusinessProcess(idItemBusinessProcess.value)
      },
      reject: () => { }
    })
  }
}

async function saveBusinessProcess(item: { [key: string]: any }) {
  loadingSaveBusinessProcess.value = true
  let successOperation = true
  if (idItemBusinessProcess.value) {
    try {
      item.id = idItemBusinessProcess.value
      await updateBusinessProcess(item)
      idItemBusinessProcess.value = ''
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    }
    catch (error: any) {
      successOperation = false
      const errorMessage = error?.data?.error?.errors?.[0]?.message || error?.data?.error?.errorMessage || 'An unexpected error occurred'
      toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 })
    }
  }
  else {
    try {
      await createBusinessProcess(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    }
    catch (error: any) {
      successOperation = false
      const errorMessage = error?.data?.error?.errors?.[0]?.message || error?.data?.error?.errorMessage || 'An unexpected error occurred'
      toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 })
    }
  }
  loadingSaveBusinessProcess.value = false
  if (successOperation) {
    await getListBusinessProcess()
  }
}

async function createBusinessProcess(item: { [key: string]: any }) {
  if (item) {
    loadingSaveBusinessProcess.value = true
    const payload = await itemToPayloadBP(item)
    const response = await GenericService.create(optionsBusinessProcess.value.moduleApi, optionsBusinessProcess.value.uriApi, payload)
    return response
  }
}
async function itemToPayloadBP(item: { [key: string]: any }) {
  const payloadApi = {
    code: item.code ? item.code : null,
    processName: item.processName,
    description: item.description,
    status: statusToString(item.status)
  }
  return payloadApi
}
async function updateBusinessProcess(item: { [key: string]: any }) {
  loadingSaveBusinessProcess.value = true
  const payload = await itemToPayloadBP(item)
  await GenericService.update(optionsBusinessProcess.value.moduleApi, optionsBusinessProcess.value.uriApi, item.id, payload)
}

async function deleteBusinessProcess(item: string) {
  loadingSaveBusinessProcess.value = true
  let successOperation = true
  if (item) {
    try {
      await GenericService.deleteItem(optionsBusinessProcess.value.moduleApi, optionsBusinessProcess.value.uriApi, item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: 'Error', life: 3000 })
    }
  }
  loadingSaveBusinessProcess.value = false
  if (successOperation) {
    clearFormBusinessProcess()
    await getList()
  }
}

// SCHEDULER

function searchAndFilterScheduler() {
  if (!filterToSearch.value.criterial || filterToSearch.value.search.trim() === '') {
    toast.add({ severity: 'warn', summary: 'Alert', detail: 'Please fill out all the fields before searching.', life: 3000 })
    return
  }

  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 100,
    page: 0,
    sortBy: '',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  // Mantener los filtros existentes
  newPayload.filter = [
    ...payloadScheduler.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')
  ]

  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    newPayload.filter.push({
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    })
  }

  payloadScheduler.value = newPayload
  getListScheduler()
}

async function getListScheduler() {
  if (optionsScheduler.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    addStatusFilter(payloadScheduler.value.filter)
    idItemToLoadFirstTimeScheduler.value = ''
    optionsScheduler.value.loading = true
    listItemsScheduler.value = []
    const newListItems = []

    const response = await GenericService.search(optionsScheduler.value.moduleApi, optionsScheduler.value.uriApi, payloadScheduler.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationScheduler.value.page = page
    paginationScheduler.value.limit = size
    paginationScheduler.value.totalElements = totalElements
    paginationScheduler.value.totalPages = totalPages

    const existingIds = new Set(listItemsScheduler.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }
      if (!existingIds.has(iterator.id)) {
        const newItem = {
          ...iterator,
          loadingEdit: false,
          loadingDelete: false,
          process: { ...listItemsScheduler.value.process, name: `${iterator.process?.processName || ''}` },
          frequency: { ...listItemsScheduler.value.frequency, name: `${iterator.frequency?.code || ''}` },
          intervalType: { ...listItemsScheduler.value.intervalType, name: `${iterator.intervalType?.code || ''}` },
          executionDateType: { ...listItemsScheduler.value.executionDateType, name: `${iterator.executionDateType?.code || ''}` },
          processingDateType: { ...listItemsScheduler.value.processingDateType, name: `${iterator.processingDateType?.code || ''}` },
        }
        newListItems.push(newItem)
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItemsScheduler.value = [...listItemsScheduler.value, ...newListItems]

    if (listItemsScheduler.value.length > 0) {
      idItemToLoadFirstTimeScheduler.value = listItemsScheduler.value[0].id
    }
    else {
      itemScheduler.value = { ...itemTempScheduler.value }
      formReloadScheduler.value += 1
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    optionsScheduler.value.loading = false
  }
}

async function getItemByIdScheduler(id: string) {
  if (id) {
    try {
      const response = await GenericService.getById(optionsScheduler.value.moduleApi, optionsScheduler.value.uriApi, id)
      if (response) {
        itemScheduler.value.id = response.id
        if (response.frequency) {
          frequencySelected.value = {
            id: response.frequency.id,
            name: response.frequency.code,
            status: response.frequency.status
          }
        }

        if (response.intervalType) {
          intervalTypeSelected.value = {
            id: response.intervalType.id,
            name: response.intervalType.code,
            status: response.intervalType.status
          }
        }
        interval.value = response.interval

        if (response.executionDateType) {
          executionDateType.value = {
            id: response.executionDateType.id,
            name: response.executionDateType.code,
            status: response.executionDateType.status
          }
        }
        else {
          executionDateType.value = null
        }
        executionDateValue.value = response.executionDateValue
        executionDate.value = response.executionDate
        executionTime.value = response.executionTime

        if (response.processingDateType) {
          processingDateType.value = {
            id: response.processingDateType.id,
            name: response.processingDateType.code,
            status: response.processingDateType.status
          }
        }
        processingDateValue.value = response.processingDateValue
        processingDate.value = response.processingDate
        itemScheduler.value.params = response.params
        itemScheduler.value.lastExecutionDatetime = response.lastExecutionDatetime
        itemScheduler.value.isInProcess = response.isInProcess
        if (response.process) {
          itemScheduler.value.process = {
            id: response.process.id,
            name: `${response.process?.code || ''} - ${response.process?.processName || ''}`,
            status: response.process.status
          }
        }

        itemScheduler.value.allowsQueueing = response.allowsQueueing
        itemScheduler.value.status = statusToBoolean(response.status)
      }

      updateFieldProperty(fieldsScheduler, 'process', 'disabled', true)
      updateFieldProperty(fieldsScheduler, 'allowsQueueing', 'disabled', false)
      updateFieldProperty(fieldsScheduler, 'status', 'disabled', false)
      formReloadScheduler.value += 1
      idItemScheduler.value = itemScheduler.value.id
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: `Scheduler item could not be loaded: ${error}`, life: 3000 })
      }
    }
    finally {
      loadingSaveScheduler.value = false
    }
  }
}

async function parseDataTableFilterScheduler(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsScheduler)
  payloadScheduler.value.filter = [...payloadScheduler.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payloadScheduler.value.filter = [...payloadScheduler.value.filter, ...parseFilter || []]
  getList()
}

function onSortFieldScheduler(event: any) {
  if (event) {
    payloadScheduler.value.sortBy = event.sortField
    payloadScheduler.value.sortType = event.sortOrder
    parseDataTableFilterScheduler(event.filter)
  }
}

function clearFilterToSearchScheduler() {
  payloadScheduler.value.filter = [...payloadScheduler.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]

  // Reiniciar los valores de búsqueda a sus estados iniciales
  filterToSearch.value = {
    criterial: ENUM_FILTER[0],
    search: '',
  }
  listItemsScheduler.value = []
  paginationScheduler.value.totalElements = 0
  getListScheduler()
}

function clearFormScheduler() {
  itemScheduler.value = { ...itemTempScheduler.value }
  idItemScheduler.value = ''

  disableFormFields()
  clearFormFields()

  formReloadScheduler.value++
}

function disableFormFields() {
  disableFrequency.value = true
  disableIntervalType.value = true
  disableInterval.value = true

  disableExecutionDateType.value = true
  disableExecutionDateValue.value = true
  disableExecutionDate.value = true
  disableExecutionTime.value = true

  disableProcessingDateType.value = true
  disableProcessingDateValue.value = true
  disableProcessingDate.value = true

  updateFieldProperty(fieldsScheduler, 'status', 'disabled', true)
  updateFieldProperty(fieldsScheduler, 'allowsQueueing', 'disabled', false)
}

function clearFormFields() {
  frequencySelected.value = null
  intervalTypeSelected.value = null
  interval.value = ''

  executionDateType.value = null
  executionDateValue.value = ''
  executionDate.value = ''
  executionTime.value = ''

  processingDateType.value = null
  processingDateValue.value = ''
  processingDate.value = ''
}

async function requireConfirmationToSaveScheduler(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    await saveScheduler(item)
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
        saveScheduler(item)
      },
      reject: () => {
        toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
      }
    })
  }
}

function requireConfirmationToDeleteScheduler(event: any) {
  if (!useRuntimeConfig().public.showDeleteConfirm) {
    deleteScheduler(idItemScheduler.value)
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
        deleteScheduler(idItemScheduler.value)
      },
      reject: () => { }
    })
  }
}

async function saveScheduler(item: { [key: string]: any }) {
  loadingSaveScheduler.value = true
  let successOperation = true
  if (idItemScheduler.value) {
    try {
      item.id = idItemScheduler.value
      await updateScheduler(item)
      idItemScheduler.value = ''
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    }
    catch (error: any) {
      successOperation = false
      const errorMessage = error?.data?.error?.errors?.[0]?.message || error?.data?.error?.errorMessage || 'An unexpected error occurred'
      toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 })
    }
  }
  else {
    try {
      await createScheduler(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    }
    catch (error: any) {
      successOperation = false
      const errorMessage = error?.data?.error?.errors?.[0]?.message || error?.data?.error?.errorMessage || 'An unexpected error occurred'
      toast.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 3000 })
    }
  }
  loadingSaveScheduler.value = false
  if (successOperation) {
    await getListScheduler()
  }
}

async function createScheduler(item: { [key: string]: any }) {
  if (item) {
    loadingSaveScheduler.value = true
    const payload = await itemToPayloadScheduler(item)
    // toast.add({ severity: 'info', detail: payload, life: 5000 })
    // const response = await GenericService.create(optionsScheduler.value.moduleApi, optionsScheduler.value.uriApi, payload)
    // return response
  }
}
async function itemToPayloadScheduler(item: { [key: string]: any }) {
  const payloadApi = {
    frequency: frequencySelected.value.id,
    intervalType: intervalTypeSelected.value.id,
    interval: interval.value,
    executionDateType: executionDateType.value.id,
    executionDateValue: executionDateValue.value,
    executionDate: executionDate.value,
    executionTime: executionTime.value,
    processingDateType: processingDateType.value.id,
    processingDateValue: processingDateValue.value,
    processingDate: processingDate.value,
    params: item.params,
    process: item.process.id,
    allowsQueueing: item.allowsQueueing,
    status: statusToString(item.status)
  }
  return payloadApi
}
async function updateScheduler(item: { [key: string]: any }) {
  loadingSaveScheduler.value = true
  const payload = await itemToPayloadScheduler(item)
  await GenericService.update(optionsScheduler.value.moduleApi, optionsScheduler.value.uriApi, item.id, payload)
}

async function deleteScheduler(item: string) {
  loadingSaveScheduler.value = true
  let successOperation = true
  if (item) {
    try {
      await GenericService.deleteItem(optionsScheduler.value.moduleApi, optionsScheduler.value.uriApi, item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: 'Error', life: 3000 })
    }
  }
  loadingSaveScheduler.value = false
  if (successOperation) {
    clearFormScheduler()
    await getListScheduler()
  }
}

watch(() => idItemToLoadFirstTimeBP.value, async (newValue) => {
  if (!newValue) {
    clearFormBusinessProcess()
  }
  else {
    await getItemByIdBusinessProcess(idItemToLoadFirstTimeBP.value)
  }
})

watch(() => idItemToLoadFirstTimeScheduler.value, async (newValue) => {
  if (!newValue) {
    clearFormScheduler()
  }
  else {
    await getItemByIdScheduler(idItemToLoadFirstTimeScheduler.value)
  }
})

onMounted(() => {
  getListBusinessProcess()
  getListScheduler()
})
</script>

<template>
  <div class="flex justify-content-between align-items-center mb-1">
    <h5 class="mb-0">
      {{ optionsScheduler.tableName }}
    </h5>
    <div class="flex mr-3">
      <div class="ml-2">
        <Button label="Business Process" severity="primary" aria-haspopup="true" aria-controls="overlay_bp_menu" icon="pi pi-cog" @click="showMenu($event)" />
        <Menu id="overlay_bp_menu" ref="menu" :model="menuItems" :popup="true" @click="openBusinessProcessSettings($event)" />
      </div>
      <div class="ml-2 mr-3">
        <Button label="Add" severity="primary" icon="pi pi-plus" @click="clearFormScheduler" />
      </div>
    </div>
  </div>

  <div class="grid w-full">
    <div class="col-12 order-0 md:order-1 md:col-6 xl:col-9 py-4">
      <div class="card p-0 mb-1">
        <Accordion :active-index="0" class="mb-1">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header">
                Filters
              </div>
            </template>

            <div style="display: flex; height: 23%;" class="responsive-height">
              <div class="p-5 m-0 py-0 px-0 mt-0 ml-0" style="flex: 1;">
                <div class="flex flex-wrap justify-between items-center gap-4">
                  <div class="flex align-items-center w-4 gap-2">
                    <label for="">Criteria</label>
                    <div class="w-full">
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
                  <div class="flex align-items-center w-6 gap-2">
                    <label for="">Search</label>
                    <div class="w-full">
                      <IconField icon-position="left" class="w-full">
                        <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                        <InputIcon class="pi pi-search" />
                      </IconField>
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearchScheduler" @click="searchAndFilterScheduler" />
                    <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :loading="loadingSearchScheduler" @click="clearFilterToSearchScheduler" />
                  </div>
                </div>
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>

      <DynamicTable
        :data="listItemsScheduler"
        :columns="columnsScheduler"
        :options="optionsScheduler"
        :pagination="paginationScheduler"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilterScheduler"
        @on-sort-field="onSortFieldScheduler"
        @update:clicked-item="getItemByIdScheduler($event)"
      />
    </div>

    <div class="col-12 order-1 md:order-0 md:col-6 xl:col-3 py-3">
      <div class="font-bold text-lg px-4 bg-primary custom-card-header mt-2">
        {{ formTitleScheduler }}
      </div>
      <div class="card p-4 mt-0">
        <EditFormV2
          :key="formReloadScheduler"
          :fields="fieldsScheduler"
          :item="itemScheduler"
          :show-actions="true"
          :loading-save="loadingSaveScheduler"
          :loading-delete="loadingDelete"
          @cancel="clearFormScheduler"
          @delete="requireConfirmationToDeleteScheduler($event)"
          @submit="requireConfirmationToSaveScheduler($event)"
        >
          <template #field-process="{ item: data, onUpdate }">
            <DebouncedAutoCompleteComponent
              v-if="!loadingSaveScheduler"
              id="autocomplete"
              field="name"
              item-value="id"
              :model="data.process"
              :suggestions="listBusinessProcessItems"
              @change="($event) => {
                onUpdate('process', $event)
                onProcessSelected()
              }"
              @load="($event) => getBusinessProcessFilter($event)"
            />
            <Skeleton v-else height="2rem" class="mb-2" />
          </template>

          <!-- F R E Q U E N C Y -->

          <template #field-frequency="{ onUpdate }">
            <DebouncedAutoCompleteComponent
              v-if="!loadingSaveScheduler"
              id="autocomplete"
              field="name"
              item-value="id"
              :disabled="disableFrequency"
              :model="frequencySelected"
              :suggestions="listFrequencyItems"
              class="mt-1"
              @change="($event) => {
                onUpdate('frequency', $event)
                onFrequencySelected($event)
              }"
              @load="($event) => getFrequencyFilter($event)"
            />
            <Skeleton v-else height="2rem" class="mb-2" />
          </template>

          <!-- I N T E R V A L   -   I N T E R V A L - T Y P E -->

          <template #field-interval-custom="{ onUpdate }">
            <div class="flex justify-content-between align-items-center mt-4 ml-3 mr-3 gap-6">
              <div class="grid">
                <div class="flex w-full">
                  <strong>Interval Type</strong>
                  <span class="p-error">*</span>
                </div>
                <div class="flex mt-1 w-full">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveScheduler"
                    id="autocomplete"
                    class="w-full"
                    field="name"
                    item-value="id"
                    :disabled="disableIntervalType"
                    :model="intervalTypeSelected"
                    :suggestions="listIntervalTypeItems"
                    @change="($event) => {
                      onUpdate('intervalType', $event)
                      onIntervalTypeSelected($event)
                    }"
                    @load="($event) => getIntervalTypeFilter($event)"
                  />
                  <Skeleton v-else height="2rem" />
                </div>
              </div>
              <div class="grid">
                <div class="flex w-full">
                  <strong>Interval</strong>
                  <span v-if="!disableInterval" class="p-error">*</span>
                </div>
                <div class="flex mt-1 w-full">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveScheduler"
                    id="autocomplete"
                    class="w-full"
                    field="name"
                    item-value="id"
                    :disabled="disableInterval"
                    :model="interval"
                    :suggestions="intervalList"
                    @load="($event) => getIntervalList()"
                  />
                  <Skeleton v-else height="2rem" />
                </div>
              </div>
            </div>
          </template>

          <!-- E X E C U T I O N   D A T E   T Y P E -->

          <template #field-executionDateType="{ item: data, onUpdate }">
            <DebouncedAutoCompleteComponent
              v-if="!loadingSaveScheduler"
              id="autocomplete"
              field="name"
              item-value="id"
              :disabled="disableExecutionDateType"
              :model="executionDateType"
              :suggestions="listExecutionDateTypeItems"
              class="mt-1"
              @change="($event) => {
                onUpdate('executionDateType', $event)
              }"
              @load="($event) => getExecutionTypeFilter($event)"
            />
            <Skeleton v-else height="2rem" class="mb-2" />
          </template>

          <!-- E X E C U T I O N   P A R A M S -->

          <template #field-execution-custom>
            <div class="flex justify-content-between align-items-center mt-4 ml-3 mr-3 gap-6">
              <div class="grid">
                <div class="flex w-full">
                  <strong>Execution Date</strong>
                  <span v-if="!disableExecutionDate" class="p-error">*</span>
                </div>
                <div class="flex mt-1 w-full">
                  <Calendar
                    v-if="!loadingSaveScheduler"
                    v-model="executionDate"
                    date-format="yy-mm-dd"
                    show-icon icon-display="input" icon="pi pi-calendar-plus" show-button-bar
                    :disabled="disableExecutionDate"
                  />
                  <Skeleton v-else height="2rem" />
                </div>
              </div>
              <div class="grid">
                <div class="flex w-full">
                  <strong>Execution Time</strong>
                  <span v-if="!executionTime" class="p-error">*</span>
                </div>
                <div class="flex mt-1 w-full">
                  <Calendar
                    v-if="!loadingSaveScheduler"
                    id="calendar-timeonly"
                    v-model="executionTime"
                    time-only
                    show-icon icon-display="input"
                    :disabled="disableExecutionTime"
                  >
                    <template #inputicon="{ clickCallback }">
                      <InputIcon class="pi pi-clock cursor-pointer" @click="clickCallback" />
                    </template>
                  </Calendar>
                  <Skeleton v-else height="2rem" />
                </div>
              </div>
              <div class="grid">
                <div class="flex w-full">
                  <strong>Execution Value</strong>
                  <span v-if="!disableExecutionDateValue" class="p-error">*</span>
                </div>
                <div class="flex mt-1 w-full">
                  <InputText
                    v-if="!loadingSaveScheduler"
                    v-model="executionDateValue"
                    show-clear
                    :disabled="disableExecutionDateValue"
                  />
                  <Skeleton v-else height="2rem" />
                </div>
              </div>
            </div>
          </template>

          <!-- P R O C E S S I N G   D A T E   T Y P E -->

          <template #field-processingDateType>
            <DebouncedAutoCompleteComponent
              v-if="!loadingSaveScheduler"
              id="autocomplete"
              field="name"
              item-value="id"
              :disabled="disableProcessingDateType"
              :model="processingDateType"
              :suggestions="listProcessingDateTypeItems"
              class="mt-1"
              @change="($event) => {
                onprocessingDateType($event)
              }"
              @load="($event) => getProcessingDateTypeFilter($event)"
            />
            <Skeleton v-else height="2rem" class="mb-2" />
          </template>

          <!-- P R O C E S S I N G   P A R A M S -->

          <template #field-processing-custom>
            <div class="flex justify-content-between align-items-center mt-4 ml-3 mr-3 gap-6">
              <div class="grid">
                <div class="flex w-full">
                  <strong>Processing Date</strong>
                  <span v-if="!disableProcessingDate" class="p-error">*</span>
                </div>
                <div class="flex mt-1 w-full">
                  <Calendar
                    v-if="!loadingSaveScheduler"
                    v-model="processingDate"
                    date-format="yy-mm-dd"
                    show-icon icon-display="input" icon="pi pi-calendar-plus"
                    :disabled="disableProcessingDate"
                  />
                  <Skeleton v-else height="2rem" />
                </div>
              </div>
              <div class="grid">
                <div class="flex w-full">
                  <strong>Processing Date Value</strong>
                  <span v-if="!disableProcessingDateValue" class="p-error">*</span>
                </div>
                <div class="flex mt-1 w-full">
                  <InputText
                    v-if="!loadingSaveScheduler"
                    v-model="processingDateValue"
                    show-clear
                    :disabled="disableProcessingDateValue"
                  />
                  <Skeleton v-else height="2rem" />
                </div>
              </div>
            </div>
          </template>

          <!-- <template #field-processingDateValue>
            <InputText
              v-if="!loadingSaveScheduler"
              v-model="processingDateValue"
              show-clear
              :disabled="disableProcessingDateValue"
            />
            <Skeleton v-else height="2rem" />
          </template>
          <template #field-processingDate>
            <InputText
              v-if="!loadingSaveScheduler"
              v-model="processingDate"
              show-clear
              :disabled="disableProcessingDate"
            />
            <Skeleton v-else height="2rem" />
          </template> -->
        </EditFormV2>
      </div>
    </div>
  </div>

  <Dialog v-model:visible="businessProcessVisible" modal header="Business Process Management" :style="{ width: '100rem' }">
    <div class="flex align-items-center justify-content-end gap-2 mt-3">
      <div class="flex justify-content-end px-0">
        <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearFormBusinessProcess" />
      </div>
    </div>
    <div class="grid">
      <div class="col-12 md:order-1 md:col-12 xl:col-9">
        <div class="card p-0 mt-2">
          <Accordion :active-index="0" class="mb-2">
            <AccordionTab>
              <template #header>
                <div class="text-white font-bold custom-accordion-header">
                  Filters
                </div>
              </template>

              <div style="display: flex; height: 23%;" class="responsive-height">
                <div class="p-5 m-0 py-0 px-0 mt-0 ml-0" style="flex: 1;">
                  <div class="flex flex-wrap justify-between items-center gap-4">
                    <div class="flex align-items-center w-4 gap-2">
                      <label for="">Criteria</label>
                      <div class="w-full">
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
                    <div class="flex align-items-center w-6 gap-2">
                      <label for="">Search</label>
                      <div class="w-full">
                        <IconField icon-position="left" class="w-full">
                          <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                          <InputIcon class="pi pi-search" />
                        </IconField>
                      </div>
                    </div>
                    <div class="flex align-items-center">
                      <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearchBusinessProcess" @click="searchAndFilterBusinessProcess" />
                      <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :loading="loadingSearchBusinessProcess" @click="clearFilterToSearchBusinessProcess" />
                    </div>
                  </div>
                </div>
              </div>
            </AccordionTab>
          </Accordion>
        </div>

        <DynamicTable
          :data="listItemsBusinessProcess"
          :columns="columnsBusinessProcess"
          :options="optionsBusinessProcess"
          :pagination="paginationBusinessProcess"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="parseDataTableFilterBusinessProcess"
          @on-sort-field="onSortFieldBusinessProcess"
          @update:clicked-item="getItemByIdBusinessProcess($event)"
        />
      </div>

      <div class="col-12 order-1 md:order-0 md:col-12 xl:col-3">
        <div class="font-bold text-lg px-4 bg-primary custom-card-header mt-2">
          {{ formTitleBusinessProcess }}
        </div>
        <div class="card p-4 mt-0">
          <EditFormV2
            :key="formReload"
            :fields="fieldsBusinessProcess"
            :item="itemBusinessProcess"
            :show-actions="true"
            :loading-save="loadingSaveBusinessProcess"
            :loading-delete="loadingDelete"
            @cancel="clearFormBusinessProcess"
            @delete="requireConfirmationToDeleteBusinessProcess($event)"
            @submit="requireConfirmationToSaveBusinessProcess($event)"
          />
        </div>
      </div>
    </div>
  </Dialog>
</template>

<style>

</style>
