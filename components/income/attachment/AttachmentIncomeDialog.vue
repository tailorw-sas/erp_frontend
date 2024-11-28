<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { v4 } from 'uuid'
import { ref, watch } from 'vue'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import AttachmentIncomeHistoryDialog from '~/components/income/attachment/AttachmentIncomeHistoryDialog.vue'
import { updateFieldProperty } from '~/utils/helpers'
import { applyFiltersAndSort } from '~/pages/payment/utils/helperFilters'
import type { FilterCriteria } from '~/composables/list'

const props = defineProps({

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
    type: Array<any>,
    required: false,
    default: []
  },
  addItem: {
    type: Function as any,
    required: false
  },
  updateItem: {
    type: Function as any,
    required: false
  },
  selectedInvoice: {
    type: String,
    required: true
  },
  selectedInvoiceObj: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:listItems', 'deleteListItems'])

const { data: userData } = useAuth()

const invoice = ref<any>(props.selectedInvoiceObj)
const attachmentHistoryDialogOpen = ref<boolean>(false)
const selectedAttachment = ref<string>('')

const filterToSearch = ref({
  criteria: 'invoice.invoiceId',
  search: ''
})

const attachmentTypeList = ref<any[]>([])
const resourceTypeList = ref<any[]>([])
const confattachmentTypeListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-attachment-type',
})
const confResourceTypeListApi = reactive({
  moduleApi: 'payment',
  uriApi: 'resource-type',
})

const formReload = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()

const loadingSearch = ref(false)
const loadingDefaultResourceType = ref(false)
const loadingDefaultAttachmentType = ref(false)

const idItem = ref('')
const item = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.incomeId,
  employee: userData?.value?.user?.name,
  employeeId: userData?.value?.user?.userId ?? '',
  resourceType: null
})

const itemTemp = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.incomeId,
  employee: userData?.value?.user?.name,
  employeeId: userData?.value?.user?.userId ?? '',
  resourceType: null
})
const toast = useToast()

const Fields: Array<FieldDefinitionType> = [
  {
    field: 'resource',
    header: 'Resource',
    dataType: 'number',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true
  },
  {
    field: 'resourceType',
    header: 'Resource Type',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: false,
    validation: validateEntityStatus('Resource Type')
  },
  {
    field: 'type',
    header: 'Attachment Type',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('Attachment Type')
  },
  {
    field: 'file',
    header: 'Path',
    dataType: 'fileupload',
    class: 'field col-12 required',
    headerClass: 'mb-1',

  },
  {
    field: 'filename',
    header: 'Filename',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field')
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'textarea',
    class: 'field col-12 ',
    headerClass: 'mb-1',
  }
]

const Columns: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '70px' },
  { field: 'invoice.invoiceId', header: 'Income Id', type: 'text', width: '100px', minWidth: '100px' },
  { field: 'type', header: 'Type', type: 'select', width: '100px' },
  { field: 'filename', header: 'Filename', type: 'text', width: '150px' },
  { field: 'remark', header: 'Remark', type: 'text', width: '100px', columnClass: 'w-10 overflow-hidden' },

]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-attachment',
  loading: false,
  showDelete: false,
  showFilters: false,
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
const PayloadOnChangePage = ref<PageState>()

const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const ListItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')
const listItemsLocal = ref<any[]>([...(props.listItems || [])])

// Validar que no exista dicho attachment type por defecto en la lista
const computedHasDefaultAttachment = computed(() => {
  return props.isCreationDialog
    ? listItemsLocal.value.some(item => item.type?.isDefault)
    : true // Se toma como true, porque ya se debe haber validado previamente que haya un Invoice Support
})

const disableAttachmentTypeSelector = computed(() => {
  return idItem.value !== '' || !computedHasDefaultAttachment.value
})

async function ResetListItems() {
  Payload.value.page = 0
}

async function parseDataTableFilterLocal(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  listItemsLocal.value = [...applyFiltersAndSort(Payload.value, listItemsLocal.value)]
}

function onSortFieldLocal(event: any) {
  if (event) {
    if (event.sortField === 'type') {
      event.sortField = 'type.name'
    }
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    parseDataTableFilterLocal(event.filter)
  }
}

function OnSortField(event: any) {
  if (event) {
    if (!props.isCreationDialog) {
      Payload.value.sortBy = event.sortField
      Payload.value.sortType = event.sortOrder
      getList()
    }
    else {
      onSortFieldLocal(event)
    }
  }
}

async function clearForm() {
  const resourceTypeValue = item.value.resourceType
  item.value = { ...itemTemp.value }
  idItem.value = ''
  item.value.resourceType = resourceTypeValue
  listDefaultData()
  formReload.value++
}

async function getList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    loadingSearch.value = true
    ListItems.value = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'invoice')) {
        iterator['invoice.invoiceId'] = iterator.invoice.invoiceId
      }
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false }]
    }

    if (ListItems.value.length > 0) {
      idItemToLoadFirstTime.value = ListItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    loadingSearch.value = false
  }
}
async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  getList()
}

async function getAttachmentTypeList(isDefault: boolean = false, filter?: FilterCriteria[]) {
  try {
    if (isDefault) {
      loadingDefaultAttachmentType.value = true
    }
    const payload
      = {
        filter: filter ?? [],
        query: '',
        pageSize: 20,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confattachmentTypeListApi.moduleApi, confattachmentTypeListApi.uriApi, payload)
    const { data: dataList } = response
    attachmentTypeList.value = []
    for (const iterator of dataList) {
      attachmentTypeList.value = [...attachmentTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}`, isDefault: iterator.attachInvDefault }]
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
  finally {
    if (isDefault) {
      loadingDefaultAttachmentType.value = false
    }
  }
}

async function getResourceTypeList(isDefault: boolean = false, filter?: FilterCriteria[]) {
  try {
    if (isDefault) {
      loadingDefaultResourceType.value = true
    }
    const payload = {
      filter: filter ?? [],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confResourceTypeListApi.moduleApi, confResourceTypeListApi.uriApi, payload)
    const { data: dataList } = response
    resourceTypeList.value = []
    for (const iterator of dataList) {
      resourceTypeList.value = [...resourceTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status ?? 'ACTIVE', fullName: `${iterator.code} - ${iterator.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
  finally {
    if (isDefault) {
      loadingDefaultResourceType.value = false
    }
  }
}

async function loadDefaultResourceType() {
  if (!item.value.resourceType || !item.value.resourceType.status) {
    // Listar solo si el resource type esta en null, ya que no cambia. O si viene en null el status
    const filter: FilterCriteria[] = [
      {
        key: 'invoice',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: true,
      },
      {
        key: 'status',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: 'ACTIVE',
      },
    ]
    await getResourceTypeList(true, filter)
    item.value.resourceType = resourceTypeList.value.length > 0 ? resourceTypeList.value[0] : null
    formReload.value++
  }
}

async function loadDefaultAttachmentType() {
  const attachmentFilter: FilterCriteria[] = [
    {
      key: 'attachInvDefault',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: true,
    },
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
    },
  ]
  await getAttachmentTypeList(true, attachmentFilter)
  item.value.type = attachmentTypeList.value.length > 0 ? attachmentTypeList.value[0] : null
  updateFieldProperty(Fields, 'type', 'disabled', true)
  formReload.value++
}

function listDefaultData() {
  loadDefaultResourceType()
  loadDefaultAttachmentType()
}

function searchAndFilter() {
  Payload.value.filter = [...Payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.search) {
    Payload.value.filter = [...Payload.value.filter, {
      key: 'filename',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'OR',
      type: 'filterSearch',
    }, {
      key: 'remark',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'OR',
      type: 'filterSearch',
    },]
  }
  getList()
}

function clearFilterToSearch() {
  Payload.value.filter = [...Payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.search = ''
  getList()
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file
    payload.invoice = props.selectedInvoice
    payload.file = file
    payload.employee = userData?.value?.user?.name
    payload.employeeId = userData?.value?.user?.userId ?? ''
    delete payload.resourceType
    if (props.isCreationDialog) {
      payload.id = v4()
      payload.type = item.type
      payload.paymentResourceType = item.resourceType
      emit('update:listItems', payload)
    }
    else {
      payload.type = item.type?.id
      payload.paymentResourceType = item.resourceType?.id
      await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
    }
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file
  payload.file = file
  payload.type = item.type?.id
  payload.paymentResourceType = item.resourceType?.id
  payload.employee = userData?.value?.user?.name
  payload.employeeId = userData?.value?.user?.userId ?? ''
  delete payload.resourceType
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingSaveAll.value = true
    if (props.isCreationDialog) {
      emit('deleteListItems', id)
    }
    else {
      await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    }
    clearForm()
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
    loadingSaveAll.value = false
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  if (loadingSaveAll.value === true) { return } // Esto es para que no se ejecute dos veces el save
  loadingSaveAll.value = true
  let successOperation = true

  if (idItem.value) {
    try {
      if (props.isCreationDialog) {
        await props.updateItem(item)
        clearForm()
        return loadingSaveAll.value = false
      }
      await updateItem(item)
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      /* if (props.isCreationDialog) {
        await props.addItem(item)
        clearForm()
        return loadingSaveAll.value = false
      } */
      await createItem(item)
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
    if (!props.isCreationDialog) {
      getList()
    }
  }
}

function canNotDeleteLastDefaultItem() {
  const countDefaultsItems = listItemsLocal.value.filter((item: any) => item.type?.isDefault).length
  return props.isCreationDialog && countDefaultsItems === 1 && listItemsLocal.value.length > 1
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
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      if (item.value.type?.isDefault && canNotDeleteLastDefaultItem()) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Item cannot be deleted', life: 3000 })
      }
      else {
        await deleteItem(idItem.value)
        if (!props.isCreationDialog) {
          getList()
        }
      }
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function getItemById(id: string | null | undefined) {
  if (id === undefined || id === null) {
    return
  }
  if (id !== '') {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      if (props.isCreationDialog) {
        const data = listItemsLocal.value?.find((attachment: any) => attachment?.id === id) as any
        item.value.id = data.id
        item.value.type = { ...data.type, fullName: `${data?.type?.code} - ${data?.type?.name}` }
        item.value.filename = data.filename
        item.value.file = data.file
        item.value.remark = data.remark
        item.value.resourceType = data.paymentResourceType
      }
      else {
        const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

        if (response) {
          item.value.id = response.id
          item.value.type = { ...response.type, fullName: `${response?.type?.code} - ${response?.type?.name}` }
          item.value.filename = response.filename
          item.value.file = response.file
          item.value.remark = response.remark
          item.value.invoice = response.invoice
          item.value.resource = response.invoice.invoiceId
          item.value.resourceType = { ...response.paymenResourceType, fullName: `${response?.paymenResourceType?.code} - ${response?.paymenResourceType?.name}` }
          selectedAttachment.value = response.attachmentId
        }
      }
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

const $primevue = usePrimeVue()

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

function downloadFile() {
  if (listItemsLocal.value?.length > 0) {
    // Selecciona el primer elemento automáticamente
    item.value = { ...listItemsLocal.value[0] } // Asigna el primer elemento a item.value
    idItemToLoadFirstTime.value = listItemsLocal.value[0]?.id // Carga el ID del primer elemento
  }
  if (ListItems.value?.length > 0) {
    // Selecciona el primer elemento automáticamente
    item.value = { ...ListItems.value[0] } // Asigna el primer elemento a item.value
    idItemToLoadFirstTime.value = ListItems.value[0]?.id // Carga el ID del primer elemento
  }

  if (item.value) {
    const link = document.createElement('a')
    link.href = item.value.file
    link.setAttribute('download', `${item.value.filename}`)
    link.setAttribute('target', '_blank')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

watch(() => props.selectedInvoiceObj, () => {
  invoice.value = props.selectedInvoiceObj

  if (invoice.value?.id) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: invoice.value?.id,
      logicalOperation: 'AND'
    }]
    getList()
  }
})

watch(() => props.listItems, async (newValue) => {
  if (newValue) {
    listItemsLocal.value = [...newValue]
    if (props.isCreationDialog) {
      Pagination.value.totalElements = newValue?.length ?? 0
    }
  }
})

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

watch(() => idItem.value, async (newValue) => {
  if (newValue === '') {
    updateFieldProperty(Fields, 'filename', 'disabled', false)
    updateFieldProperty(Fields, 'remark', 'disabled', false)
  }
  else {
    updateFieldProperty(Fields, 'filename', 'disabled', true)
    updateFieldProperty(Fields, 'remark', 'disabled', true)
  }
})

onMounted(() => {
  const invoice = props.selectedInvoice || props.selectedInvoiceObj?.id
  if (invoice) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: invoice,
      logicalOperation: 'AND'
    }]
  }
  if (!props.isCreationDialog && invoice) {
    getList()
  }
  if (props.isCreationDialog) {
    Pagination.value.totalElements = listItemsLocal.value?.length ?? 0
  }
  listDefaultData()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-screen"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" :style="{ width: '80%' }"
    :pt="{
      root: {
        class: 'custom-dialog-history',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
    }"
    @hide="closeDialog(ListItems.length)"
  >
    <div class="grid p-fluid formgrid">
      <div class="col-12 order-1 md:order-0 md:col-9 pt-5">
        <div class="flex justify-content-end mb-1">
          <div class="pr-4 pl-4 pt-2 pb-2 font-bold bg-container text-white">
            Income: {{ props.selectedInvoiceObj.incomeId }}
          </div>
        </div>
        <Accordion v-if="false" :active-index="0" class="mb-2 card p-0">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full">
                <div>Filters</div>
              </div>
            </template>

            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex align-items-center gap-2">
                <label for="email">Income:</label>
                <div class="w-full lg:w-auto">
                  <IconField icon-position="left" class="w-full">
                    <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                    <InputIcon class="pi pi-search" />
                  </IconField>
                </div>
              </div>
              <div class="flex align-items-center">
                <Button
                  v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearch"
                  @click="searchAndFilter"
                />
                <Button
                  v-tooltip.top="'Clear'" class="w-3rem" outlined icon="pi pi-filter-slash"
                  :loading="loadingSearch" @click="clearFilterToSearch"
                />
              </div>
            </div>
          </AccordionTab>
        </Accordion>
        <DynamicTable
          :data="isCreationDialog ? listItemsLocal : ListItems" :columns="Columns" :options="options"
          :pagination="Pagination" :is-custom-sorting="!isCreationDialog" @update:clicked-item="getItemById($event)"
          @open-edit-dialog="getItemById($event)" @on-confirm-create="clearForm"
          @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
          @on-list-item="ResetListItems" @on-sort-field="OnSortField"
        />
      </div>
      <div class="col-12 order-2 md:order-0 md:col-3 pt-5">
        <div>
          <div class="font-bold text-lg px-4 bg-primary custom-card-header">
            {{ idItem !== '' ? "Edit" : "Add" }}
          </div>
          <div class="card">
            <EditFormV2
              :key="formReload" :fields="Fields" :item="item" :show-actions="true"
              :loading-save="loadingSaveAll" @cancel="clearForm" @delete="requireConfirmationToDelete($event)"
              @submit="requireConfirmationToSave($event)" @submit-form="requireConfirmationToSave"
            >
              <template #field-resourceType="{ item: data, onUpdate }">
                <DebouncedAutoCompleteComponent
                  v-if="!loadingSaveAll && !loadingDefaultResourceType" id="autocomplete"
                  field="fullName" item-value="id" disabled :model="data.resourceType" :suggestions="resourceTypeList"
                  @change="($event) => {
                    onUpdate('resourceType', $event)
                  }" @load="($event) => getResourceTypeList()"
                >
                  <template #option="props">
                    <span>{{ props.item.fullName }}</span>
                  </template>
                </DebouncedAutoCompleteComponent>
                <Skeleton v-else height="2rem" class="mb-2" />
              </template>
              <template #field-type="{ item: data, onUpdate }">
                <DebouncedAutoCompleteComponent
                  v-if="!loadingSaveAll && !loadingDefaultAttachmentType" id="autocomplete" field="fullName"
                  :disabled="disableAttachmentTypeSelector" item-value="id" :model="data.type"
                  :suggestions="attachmentTypeList" @change="($event) => {
                    onUpdate('type', $event)
                  }" @load="($event) => getAttachmentTypeList($event)"
                >
                  <template #option="props">
                    <span>{{ props.item.fullName }}</span>
                  </template>
                </DebouncedAutoCompleteComponent>
                <Skeleton v-else height="2rem" class="mb-2" />
              </template>
              <template #field-file="{ onUpdate, item: data }">
                <FileUpload
                  accept="application/pdf" :disabled="idItem !== ''" :max-file-size="1000000"
                  :multiple="false" auto custom-upload @uploader="(event: any) => {
                    const file = event.files[0]
                    onUpdate('file', file)
                    onUpdate('filename', data.file.name || data.file.split('/')[data.file.split('/')?.length - 1])
                  }"
                >
                  <template #header="{ chooseCallback }">
                    <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                      <div class="flex gap-2">
                        <Button id="btn-choose" class="p-2" icon="pi pi-plus" text @click="chooseCallback()" />
                        <Button
                          icon="pi pi-times" class="ml-2" severity="danger" :disabled="!data.file" text
                          @click="onUpdate('file', null)"
                        />
                      </div>
                    </div>
                  </template>
                  <template #content="{ files }">
                    <div class="w-full flex justify-content-center">
                      <ul v-if="files[0] || data.file" class=" p-0 m-0" style="width: 300px;  overflow: hidden;">
                        <li class=" surface-border flex align-items-center w-fit">
                          <div class="flex flex-column w-fit  text-overflow-ellipsis">
                            <span
                              class="text-900 font-semibold text-xl mb-2 text-overflow-clip overflow-hidden"
                              style="width: 300px;"
                            >{{ data.file.name
                              || data.file.split("/")[data.file.split("/")?.length - 1] }}</span>
                            <span v-if="data.file.size" class="text-900 font-medium">
                              <Badge severity="warning">
                                {{ formatSize(data.file.size) }}
                              </Badge>
                            </span>
                          </div>
                        </li>
                      </ul>
                    </div>
                  </template>
                </FileUpload>
              </template>
              <template #form-footer="footProps">
                <Button
                  v-tooltip.top="'Save'" class="w-3rem sticky" icon="pi pi-save" :disabled="idItem !== ''"
                  @click="footProps.item.submitForm($event)"
                />
                <Button
                  v-tooltip.top="'View File'" class="w-3rem ml-1 sticky" icon="pi pi-eye"
                  :disabled="listItemsLocal.length === 0 && ListItems.length === 0" @click="downloadFile"
                />
                <Button
                  v-tooltip.top="'Show History'" :disabled="props.isCreationDialog" class="w-3rem ml-1 sticky"
                  icon="pi pi-book" @click="() => attachmentHistoryDialogOpen = true"
                />
                <Button v-tooltip.top="'Add'" class="w-3rem ml-1 sticky" icon="pi pi-plus" @click="clearForm" />
                <Button
                  v-tooltip.top="'Delete'" outlined severity="danger" class="w-3rem ml-1 sticky"
                  icon="pi pi-trash" :disabled="idItem === ''" @click="requireConfirmationToDelete"
                />
                <Button
                  v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times"
                  @click="() => {
                    clearForm()
                    closeDialog(ListItems.length)
                  }"
                />
              </template>
            </EditFormV2>
          </div>
        </div>
      </div>
      <div v-if="attachmentHistoryDialogOpen">
        <AttachmentIncomeHistoryDialog
          :selected-attachment="selectedAttachment"
          :close-dialog="() => { attachmentHistoryDialogOpen = false; selectedAttachment = '' }"
          :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="invoice.id" :selected-invoice-obj="invoice"
          header="Attachment Status History" :attachment-type="item.type"
        />
      </div>
    </div>
  </Dialog>
</template>

<style scoped lang="scss">
.bg-container {
  background-color: var(--primary-color);
  border-radius: 4px;
}
</style>
