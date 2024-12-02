<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import dayjs from 'dayjs'
import type { IData } from '../table/interfaces/IModelData'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'

const externalProps = defineProps({

  header: {
    type: String,
    required: true
  },

  openDialog: {
    type: Boolean,
    required: true
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  isCreationDialog: {
    type: Boolean,
    required: true
  },
  listItems: {
    type: Array,
    required: false
  },
  addItem: {
    type: Function as any,
    required: false
  },
  updateItem: {
    type: Function as any,
    required: false
  },
  selectedPayment: {
    type: Object,
    required: true
  },
  isCreateOrEditPayment: {
    type: String,
    required: true
  }
})

const pathFileLocal = ref('')

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const $primevue = usePrimeVue()
const formReload = ref(0)
const formReloadToCreate = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()
const { data: userData } = useAuth()
const loadingDelete = ref(false)

const listItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')
const paymentSelect = ref<any>()

const idItem = ref('')

const toast = useToast()

// Esquema para un archivo válido
const fileSchema = z.object({
  name: z.string().nonempty('File name is required'),
  size: z.number().positive('File size must be positive'),
  type: z.string().nonempty('File type is required'),
})

// Esquema para un archivo o múltiples archivos
const validationSchema = z.union([
  fileSchema.nullable(), // Un solo archivo
  z.array(fileSchema).nonempty('At least one file is required').nullable(),
])
const fieldsV2: Array<FieldDefinitionType> = [

  {
    field: 'paymentId',
    header: 'Resource',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true,
    hidden: true
  },
  {
    field: 'shareFileYear',
    header: 'Year',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true
  },
  {
    field: 'shareFileMonth',
    header: 'Month',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true
  },
  {
    field: 'hotel',
    header: 'Hotel Code',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true,
  },
  {
    field: 'agency',
    header: 'Agency Code',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true,
  },
  // {
  //   field: 'employee',
  //   header: 'Employee',
  //   dataType: 'select',
  //   class: 'field col-12',
  //   headerClass: 'mb-1',
  //   hidden: true,
  // },
  {
    field: 'path',
    header: 'Path',
    dataType: 'fileupload',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateFiles(1)
  },
  {
    field: 'fileName',
    header: 'Filename',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
    hidden: true,
    disabled: true,
    validation: z.string().trim() // .min(1, 'The filename field is required')
  },
]

const fieldsV2ForCreate: Array<FieldDefinitionType> = [

  {
    field: 'paymentId',
    header: 'Resource',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true,
    hidden: true
  },
  {
    field: 'shareFileYear',
    header: 'Year',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true
  },
  {
    field: 'shareFileMonth',
    header: 'Month',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true
  },
  {
    field: 'hotel',
    header: 'Hotel Code',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: false,
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'agency',
    header: 'Agency Code',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true,
    validation: validateEntityStatus('agency'),
  },
  {
    field: 'path',
    header: 'Path',
    dataType: 'fileupload',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateFiles(1)
  },
  {
    field: 'fileName',
    header: 'Filename',
    dataType: 'text',
    class: 'field col-12',
    headerClass: 'mb-1',
    hidden: true,
    disabled: true,
    validation: z.string().trim() // .min(1, 'The filename field is required')
  },
]
const agencyList = ref<any[]>([])
const hotelList = ref<any[]>([])
const objApis = ref({
  paymentSource: { moduleApi: 'settings', uriApi: 'manage-payment-source' },
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  bankAccount: { moduleApi: 'settings', uriApi: 'manage-bank-account' },
  paymentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  attachmentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' },
  employee: { moduleApi: 'settings', uriApi: 'manage-employee' },
})

const item = ref<GenericObject>({

  paymentId: externalProps.selectedPayment.id || '',
  shareFileYear: dayjs().format('YYYY') || '',
  shareFileMonth: dayjs().format('MMMM') || '',
  hotel: externalProps.isCreateOrEditPayment === 'create' ? '' : `${externalProps.selectedPayment.hotel.name}` || '',
  agency: externalProps.isCreateOrEditPayment === 'create' ? '' : `${externalProps.selectedPayment.agency.name}` || '',
  fileName: externalProps.isCreateOrEditPayment === 'create' ? '' : externalProps.selectedPayment.paymentAmount ? `${externalProps.selectedPayment.paymentAmount}_${listItems.value.length + 1}.pdf` : '',
  path: '',
})

const itemTemp = ref<GenericObject>({
  paymentId: externalProps.selectedPayment.id || '',
  shareFileYear: dayjs().format('YYYY') || '',
  shareFileMonth: dayjs().format('MMMM') || '',
  hotel: externalProps.isCreateOrEditPayment === 'create' ? '' : `${externalProps.selectedPayment.hotel.name}` || '',
  agency: externalProps.isCreateOrEditPayment === 'create' ? '' : `${externalProps.selectedPayment.agency.name}` || '',
  fileName: externalProps.isCreateOrEditPayment === 'create' ? '' : externalProps.selectedPayment.paymentAmount ? `${externalProps.selectedPayment.paymentAmount}_${listItems.value.length + 1}.pdf` : '',
  path: '',
})

// const itemForCreate = ref<GenericObject>({

// paymentId: externalProps.selectedPayment.id || '',
// shareFileYear: dayjs().format('YYYY') || '',
// shareFileMonth: dayjs().format('MMMM') || '',
// hotel: externalProps.isCreateOrEditPayment === 'create' ? '' : `${externalProps.selectedPayment.hotel.name}` || '',
// agency: externalProps.isCreateOrEditPayment === 'create' ? '' : `${externalProps.selectedPayment.agency.name}` || '',
// fileName: externalProps.isCreateOrEditPayment === 'create' ? '' : externalProps.selectedPayment.paymentAmount ? `${externalProps.selectedPayment.paymentAmount}_${listItems.value.length + 1}.pdf` : '',
// path: '',
// })

// const itemTempForCreate = ref<GenericObject>({
// paymentId: externalProps.selectedPayment.id || '',
// shareFileYear: dayjs().format('YYYY') || '',
// shareFileMonth: dayjs().format('MMMM') || '',
// hotel: externalProps.isCreateOrEditPayment === 'create' ? '' : `${externalProps.selectedPayment.hotel.name}` || '',
// agency: externalProps.isCreateOrEditPayment === 'create' ? '' : `${externalProps.selectedPayment.agency.name}` || '',
// fileName: externalProps.isCreateOrEditPayment === 'create' ? '' : externalProps.selectedPayment.paymentAmount ? `${externalProps.selectedPayment.paymentAmount}_${listItems.value.length + 1}.pdf` : '',
// path: '',
// })

const Columns: IColumn[] = [
  { field: 'shareFileYear', header: 'Year', type: 'text', width: '100px', sortable: false, showFilter: false },
  { field: 'shareFileMonth', header: 'Month', type: 'text', width: '100px', sortable: false, showFilter: false },
  { field: 'fileName', header: 'Filename', type: 'text', width: '200px', },
]
// const columnsAttachment: IColumn[] = [
//   { field: 'attachmentId', header: 'Id', type: 'text', width: '100px', sortable: false, showFilter: false },
//   { field: 'paymentId', header: 'Payment Id', type: 'text', width: '100px', sortable: false, showFilter: false },
//   // { field: 'resourceType', header: 'Resource Type', type: 'select', width: '200px', objApi: { moduleApi: 'payment', uriApi: 'resource-type' } },
//   { field: 'attachmentType', header: 'Type', type: 'select', width: '200px', objApi: { moduleApi: 'payment', uriApi: 'attachment-type' } },
//   { field: 'fileName', header: 'Filename', type: 'text', width: '200px' },
//   { field: 'remark', header: 'Remark', width: '200px', type: 'text' },
// ]

const dialogVisible = ref(externalProps.openDialog)
const options = ref({
  tableName: 'Payment Share Files',
  moduleApi: 'payment',
  uriApi: 'share-file',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const payloadOnChangePage = ref<PageState>()

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'DESC'
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Add'
})

async function ResetListItems() {
  payload.value.page = 0
}

function OnSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    ParseDataTableFilter(event.filter)
  }
}

async function clearForm() {
  updateFieldProperty(fieldsV2, 'shareFileYear', 'disabled', true)
  updateFieldProperty(fieldsV2, 'shareFileMonth', 'disabled', true)
  updateFieldProperty(fieldsV2, 'hotel', 'disabled', true)
  updateFieldProperty(fieldsV2, 'agency', 'disabled', true)
  item.value = { ...itemTemp.value }
  item.value.fileName = externalProps.selectedPayment.paymentAmount ? `${externalProps.selectedPayment.paymentAmount}_${Pagination.value.totalElements + 1}.pdf` : ''
  idItem.value = ''

  formReload.value++
}

async function getList() {
  try {
    idItem.value = ''
    options.value.loading = true
    idItemToLoadFirstTime.value = ''
    listItems.value = []
    let shareFileList: any[] = []

    if (externalProps.isCreateOrEditPayment === 'edit') {
      payload.value.filter.push({
        key: 'payment.id',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: externalProps.selectedPayment?.id
      })
    }
    const response = await GenericService.searchShareFile(options.value.moduleApi, options.value.uriApi, payload.value)
    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      shareFileList = [...shareFileList, { ...iterator, paymentId: iterator.resource?.paymentId || '', shareFileMonth: iterator.shareFileMonth ? dayjs(`2024-${iterator.shareFileMonth}-01`).format('MMMM') || '' : '' }]
    }

    listItems.value = [...shareFileList]

    // for (const iterator of dataList) {
    //   listItems.value = [...listItems.value, { ...iterator, loadingEdit: false, loadingDelete: false, roomRateId: iterator?.roomRate?.roomRateId }]
    // }

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

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.search = ''
  getList()
}

// paymentId

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: 'payment.id',
      operator: 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'OR',
      type: 'filterSearch',
    }, {
      key: 'fileName',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'OR',
      type: 'filterSearch',
    },]
  }
  getList()
}
async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  payload.value.filter = [...parseFilter || []]
  getList()
}

const haveError = ref(false)

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    if (typeof payload.path === 'object' && payload.path !== null) {
      const file = payload.path.files[0]
      const formData = new FormData()
      formData.append('file', file)
      formData.append('paymentId', payload.paymentId)
      formData.append('fileName', payload.fileName)
      await GenericService.sendFormData(options.value.moduleApi, options.value.uriApi, formData)
    }
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  const file = payload.path.files[0]
  const formData = new FormData()
  formData.append('file', file)
  formData.append('paymentId', payload.paymentId)
  formData.append('fileName', payload.fileName)
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
    getList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete share file', life: 10000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  if (loadingSaveAll.value === true) { return } // Esto es para que no se ejecute dos veces el save
  loadingSaveAll.value = true
  let successOperation = true
  if (idItem.value) {
    try {
      await updateItem(item)
      loadingSaveAll.value = false
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
      loadingSaveAll.value = false
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  if (successOperation && !haveError.value) {
    clearForm()
    getList()
  }
  else {
    loadingSaveAll.value = false
  }
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
  defaults?: boolean
}

interface ListItem {
  id: string
  name: string
  code: string
  status: boolean | string
  defaults?: boolean
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    code: data.code,
    status: data.status,
    defaults: data?.defaults || false
  }
}

async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  agencyList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

interface DataListItemHotel {
  id: string
  name: string
  code: string
  status: string
  defaults?: boolean
  applyByTradingCompany?: boolean
}

interface ListItemHotel {
  id: string
  name: string
  status: boolean | string
  defaults?: boolean
  applyByTradingCompany?: boolean
}
function mapFunctionHotel(data: DataListItemHotel): ListItemHotel {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    defaults: data?.defaults || false,
    applyByTradingCompany: data.applyByTradingCompany
  }
}

async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  hotelList.value = await getDataList<DataListItemHotel, ListItemHotel>(moduleApi, uriApi, filter, queryObj, mapFunctionHotel, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}

async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
  const file = event.files[0]
  listFields[fieldKey] = file
}

function formatSize(bytes: number) {
  const k = 1024
  const dm = 3
  const sizes = $primevue.config.locale?.fileSizeTypes || ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']

  if (bytes === 0) {
    return `0 ${sizes[0]}`
  }

  const i = Math.floor(Math.log(bytes) / Math.log(k))
  const formattedSize = Number.parseFloat((bytes / k ** i).toFixed(dm))

  return `${formattedSize} ${sizes[i]}`
}

// function requireConfirmationToDelete(event: any) {
//   confirm.require({
//     target: event.currentTarget,
//     group: 'headless',
//     header: 'Save the record',
//     message: 'Do you want to save the change?',
//     acceptClass: 'p-button-danger',
//     rejectLabel: 'Cancel',
//     acceptLabel: 'Accept',
//     accept: () => {
//       deleteItem(idItem.value)
//       toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
//     },
//     reject: () => {
//       // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
//     }
//   })
// }

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    idItemToLoadFirstTime.value = id
    try {
      const response = await GenericService.searchShareFileById(options.value.moduleApi, options.value.uriApi, id)
      if (response) {
        item.value.id = response.payment.id
        item.value.paymentId = response.payment.paymentId
        item.value.shareFileYear = response.shareFileYear
        item.value.shareFileMonth = response.shareFileMonth ? dayjs(`2024-${response.shareFileMonth}-01`).format('MMMM') || '' : ''
        item.value.hotel = `${response.payment.hotel.code} - ${response.payment.hotel.name}`
        item.value.agency = `${response.payment.agency.code} - ${response.payment.agency.name}`
        item.value.fileName = response.fileName
        item.value.path = response.fileUrl // `https://static.kynsoft.net/${response.fileUrl}`
        pathFileLocal.value = response.path
        updateFieldProperty(fieldsV2, 'shareFileYear', 'disabled', true)
        updateFieldProperty(fieldsV2, 'shareFileMonth', 'disabled', true)
        updateFieldProperty(fieldsV2, 'hotel', 'disabled', true)
        updateFieldProperty(fieldsV2, 'agency', 'disabled', true)
      }

      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Could not load share file', life: 10000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

function disabledBtnSave(propsValue: any): boolean {
  if (item.value && item.value.id) {
    return true
  }
  else if (propsValue.item.fieldValues.path) {
    return false
  }
  else {
    return true
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

// function requireConfirmationToSave(item: any) {
//   const { event } = item
//   confirm.require({
//     target: event.currentTarget,
//     group: 'headless',
//     header: 'Save the record',
//     message: 'Do you want to save the change?',
//     rejectLabel: 'Cancel',
//     acceptLabel: 'Accept',
//     accept: () => {
//       saveItem(item)
//     },
//     reject: () => {
//       // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
//     }
//   })
// }

function requireConfirmationToDelete(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Delete the record',
    message: 'Do you want to delete the record?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      deleteItem(idItem.value)
    },
    reject: () => {}
  })
}

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})
onMounted(async () => {
  paymentSelect.value = externalProps.selectedPayment
  if (externalProps.isCreateOrEditPayment === 'create') {
    item.value.hotel = null
    item.value.agency = null
  }
  await getList()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-screen"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" :style="{ width: '80%' }"
    @hide="closeDialog"
  >
    <template #default>
      <div class="flex justify-content-between align-items-center pt-3">
        <h3 class="mb-0" />
        <!-- <IfCan :perms="['PAYMENT-MANAGEMENT:CREATE']"> -->
        <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
          <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
        </div>
        <!-- </IfCan> -->
      </div>
      <div class="grid">
        <div class="col-12 order-1 md:order-0 md:col-9 pt-3">
          <Accordion v-if="false" :active-index="0" class="mb-2 card p-0">
            <AccordionTab>
              <template #header>
                <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full">
                  <div>Filters</div>
                  <div>
                    <strong class="mx-2">Payment:</strong>
                    <span>{{ externalProps.selectedPayment.paymentId }}</span>
                  </div>
                </div>
              </template>

              <div class="flex gap-4 flex-column lg:flex-row">
                <div class="flex align-items-center gap-2">
                  <label for="email">Payment:</label>
                  <div class="w-full lg:w-auto">
                    <IconField icon-position="left" class="w-full">
                      <InputText v-model="filterToSearch.search" :disabled="externalProps.isCreateOrEditPayment === 'create'" type="text" placeholder="Search" class="w-full" />
                      <InputIcon class="pi pi-search" />
                    </IconField>
                  </div>
                </div>
                <div class="flex align-items-center">
                  <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearch" @click="searchAndFilter" />
                  <Button v-tooltip.top="'Clear'" class="w-3rem" outlined icon="pi pi-filter-slash" :loading="loadingSearch" @click="clearFilterToSearch" />
                </div>
              </div>
            </AccordionTab>
          </Accordion>
          <DynamicTable
            class="card p-0"
            :data="listItems"
            :columns="Columns"
            :options="options"
            :pagination="Pagination"
            @on-change-filter="ParseDataTableFilter"
            @update:clicked-item="getItemById($event)"
            @on-confirm-create="clearForm"
            @on-change-pagination="payloadOnChangePage = $event"
            @on-list-item="ResetListItems" @on-sort-field="OnSortField"
          />
        </div>
        <div class="col-12 order-2 md:order-0 md:col-3 pt-3">
          <div>
            <div class="font-bold text-lg px-4 bg-primary custom-card-header">
              {{ formTitle }}
            </div>
            <div class="card">
              <EditFormV2
                v-if="$props.isCreateOrEditPayment === 'edit'"
                :key="formReload"
                :fields="fieldsV2"
                :item="item"
                :show-actions="true"
                :loading-save="loadingSaveAll"
                @on-confirm-create="clearForm"
                @cancel="clearForm"
                @delete="requireConfirmationToDelete($event)"
                @submit="requireConfirmationToSave($event)"
              >
                <template #field-path="{ item: data, onUpdate }">
                  <InputGroup>
                    <InputText
                      v-model="data.fileName"
                      style="border-top-right-radius: 0; border-bottom-right-radius: 0;"
                      placeholder="Upload File"
                      disabled
                    />
                    <FileUpload
                      mode="basic"
                      :max-file-size="100000000"
                      :disabled="idItem !== '' || idItem === null"
                      :multiple="false"
                      auto
                      custom-upload
                      style="border-top-left-radius: 0; border-bottom-left-radius: 0;"
                      accept="application/pdf"
                      @uploader="($event: any) => {
                        customBase64Uploader($event, fieldsV2, 'path');
                        onUpdate('path', $event)
                        if ($event && $event.files.length > 0) {
                          onUpdate('fileName', $event?.files[0]?.name)
                          onUpdate('fileSize', formatSize($event?.files[0]?.size))
                        }
                        else {
                          onUpdate('fileName', '')
                        }
                      }"
                    />
                  </InputGroup>
                  <FileUpload
                    v-if="false"
                    :max-file-size="10000000"
                    :disabled="idItem !== '' || idItem === null"
                    :multiple="false"
                    auto
                    custom-upload
                    accept="application/pdf,text/plain,application/octet-stream"
                    @uploader="(event: any) => {
                      //customBase64Uploader($event, fieldsV2, 'path');
                      const file = event.files[0]
                      onUpdate('path', file)
                      onUpdate('fileName', `${paymentSelect.paymentAmount}_${Pagination.totalElements + 1}.${event?.files[0]?.name.split('.').pop()}`)

                    }"
                  >
                    <template #header="{ chooseCallback }">
                      <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                        <div class="flex gap-2">
                          <Button id="btn-choose" :disabled="false" class="p-2" icon="pi pi-plus" text @click="chooseCallback()" />
                          <Button
                            :disabled="idItem !== '' || idItem === null"
                            icon="pi pi-times" class="ml-2" severity="danger" text
                            @click="() => {
                              onUpdate('path', null);
                              clearForm();
                            }"
                          />
                        </div>
                      </div>
                    </template>
                    <template #content="{ files }">
                      <div class="w-full flex justify-content-center">
                        <ul v-if="files[0] || data.path" class=" p-0 m-0" style="width: 300px;  overflow: hidden;">
                          <li class=" surface-border flex align-items-center w-fit">
                            <div class="flex flex-column w-fit  text-overflow-ellipsis">
                              <span
                                class="text-900 font-semibold text-xl mb-2 text-overflow-clip overflow-hidden"
                                style="width: 300px;"
                              >{{ data.path ? data.fileName : '' }}</span>
                              <span v-if="data.path.size" class="text-900 font-medium">
                                <Badge severity="warning">
                                  {{ formatSize(data.path.size) }}
                                </Badge>
                              </span>
                            </div>
                          </li>
                        </ul>
                      </div>
                    </template>
                  </FileUpload>
                </template>

                <template #form-footer="props">
                  <!-- <IfCan :perms="idItem ? ['PAYMENT-MANAGEMENT:SAVE-SHARE-FILES'] : ['PAYMENT-MANAGEMENT:EDIT-SHARE-FILES']"> -->
                  <Button
                    v-tooltip.top="'Save'"
                    :loading="loadingSaveAll"
                    :disabled="disabledBtnSave(props)" class="w-3rem sticky" icon="pi pi-save"
                    @click="props.item.submitForm($event)"
                  />
                  <!-- </IfCan>
                  <IfCan :perms="['PAYMENT-MANAGEMENT:DELETE-SHARE-FILES']"> -->
                  <!-- <Button v-tooltip.top="'Delete'" :disabled="!idItem" outlined severity="danger" class="w-3rem ml-1 sticky" icon="pi pi-trash" @click="props.item.deleteItem($event)" /> -->
                  <!-- </IfCan> -->
                  <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times" @click="closeDialog" />
                </template>
                <!-- Save, View File, Show History, Add, Delete y Cancel -->
              </EditFormV2>
              <EditFormV2
                v-else
                :key="formReloadToCreate"
                :fields="fieldsV2ForCreate"
                :item="item"
                :show-actions="true"
                :loading-save="loadingSaveAll"
                @on-confirm-create="clearForm"
                @cancel="clearForm"
                @delete="requireConfirmationToDelete($event)"
                @submit="requireConfirmationToSave($event)"
              >
                <template #field-path="{ item: data, onUpdate }">
                  <InputGroup>
                    <InputText
                      v-model="data.fileName"
                      style="border-top-right-radius: 0; border-bottom-right-radius: 0;"
                      placeholder="Upload File"
                      disabled
                    />
                    <FileUpload
                      mode="basic"
                      :max-file-size="100000000"
                      :disabled="idItem !== '' || idItem === null"
                      :multiple="false"
                      auto
                      custom-upload
                      style="border-top-left-radius: 0; border-bottom-left-radius: 0;"
                      accept="application/pdf"
                      @uploader="($event: any) => {
                        customBase64Uploader($event, fieldsV2, 'path');
                        onUpdate('path', $event)
                        if ($event && $event.files.length > 0) {
                          onUpdate('fileName', $event?.files[0]?.name)
                          onUpdate('fileSize', formatSize($event?.files[0]?.size))
                        }
                        else {
                          onUpdate('fileName', '')
                        }
                      }"
                    />
                  </InputGroup>
                </template>

                <template #field-hotel="{ item: data, onUpdate, fields: listFields, field }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="data.hotel"
                    :suggestions="[...hotelList]"
                    :disabled="listFields.find((f: FieldDefinitionType) => f.field === field)?.disabled || false"
                    @change="async ($event) => {
                      onUpdate('hotel', $event)
                    }"
                    @load="async($event) => {
                      const filter: FilterCriteria[] = [
                        {
                          key: 'status',
                          logicalOperation: 'AND',
                          operator: 'EQUALS',
                          value: 'ACTIVE',
                        },
                      ]
                      const objQueryToSearch = {
                        query: $event,
                        keys: ['name', 'code'],
                      }
                      await getHotelList(objApis.hotel.moduleApi, objApis.hotel.uriApi, objQueryToSearch, filter)
                    }"
                  />
                  <Skeleton v-else height="2rem" class="mb-2" />
                </template>

                <!-- Agency -->
                <template #field-agency="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="data.agency"
                    :suggestions="[...agencyList]"
                    @change="($event) => {
                      onUpdate('agency', $event)
                    }"
                    @load="async($event) => {
                      const objQueryToSearch = {
                        query: $event,
                        keys: ['name', 'code'],
                      }
                      const filter: FilterCriteria[] = [
                        // {
                        //   key: 'client.id',
                        //   logicalOperation: 'AND',
                        //   operator: 'EQUALS',
                        //   value: data.client?.id,
                        // },
                        {
                          key: 'status',
                          logicalOperation: 'AND',
                          operator: 'EQUALS',
                          value: 'ACTIVE',
                        },
                      ]
                      await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, objQueryToSearch, filter)
                    }"
                  />
                  <Skeleton v-else height="2rem" class="mb-2" />
                </template>

                <template #form-footer="props">
                  <!-- <IfCan :perms="idItem ? ['PAYMENT-MANAGEMENT:SAVE-SHARE-FILES'] : ['PAYMENT-MANAGEMENT:EDIT-SHARE-FILES']"> -->
                  <Button
                    v-tooltip.top="'Save'"
                    :loading="loadingSaveAll"
                    :disabled="disabledBtnSave(props)" class="w-3rem sticky" icon="pi pi-save"
                    @click="props.item.submitForm($event)"
                  />
                  <!-- </IfCan>
                  <IfCan :perms="['PAYMENT-MANAGEMENT:DELETE-SHARE-FILES']"> -->
                  <!-- <Button v-tooltip.top="'Delete'" :disabled="!idItem" outlined severity="danger" class="w-3rem ml-1 sticky" icon="pi pi-trash" @click="props.item.deleteItem($event)" /> -->
                  <!-- </IfCan> -->
                  <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times" @click="closeDialog" />
                </template>
                <!-- Save, View File, Show History, Add, Delete y Cancel -->
              </EditFormV2>
            </div>
          </div>
        </div>
      </div>
    </template>
  </Dialog>
</template>
