<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import type { IData } from '../table/interfaces/IModelData'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import { getUrlOrIdByFile } from '~/composables/files'

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
  }
})

const attachmentTypeList = ref<any[]>([])
const resourceTypeList = ref<any[]>([])
const employeeList = ref<any[]>([])

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const $primevue = usePrimeVue()
const formReload = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()

const openDialogHistory = ref(false)

const loadingDelete = ref(false)

const idItem = ref('')

const toast = useToast()

const fieldsV2: Array<FieldDefinitionType> = [
  {
    field: 'resource',
    header: 'Resource',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: false,
    hidden: true
  },
  {
    field: 'paymentId',
    header: 'Resource',
    dataType: 'text',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: false,
  },
  {
    field: 'resourceType',
    header: 'Resource Type',
    dataType: 'number',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: false
  },

  {
    field: 'attachmentType',
    header: 'Attachment Type',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string(),
      name: z.string(),

    }).nullable()
      .refine((value: any) => value && value.id && value.name, { message: `The Transaction Type field is required` })
      .refine((value: any) => value.status !== 'ACTIVE', {
        message: `This Attachment Type is not active`
      })

  },
  {
    field: 'employee',
    header: 'Employee',
    dataType: 'select',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string(),
      name: z.string(),

    }).nullable()
      .refine((value: any) => value && value.id && value.name, { message: `The Employee field is required` })
  },
  {
    field: 'fileName',
    header: 'Filename',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',

  },
  {
    field: 'path',
    header: 'Path',
    dataType: 'fileupload',
    class: 'field col-12 required',
    headerClass: 'mb-1',

  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1',

  },

]

const item = ref<GenericObject>({
  paymentId: props.selectedPayment.paymentId || '',
  resource: props.selectedPayment.id || '',
  resourceType: null,
  attachmentType: null,
  employee: null,
  fileName: '',
  path: '',
  remark: '',
  status: 'ACTIVE'
})

const itemTemp = ref<GenericObject>({
  paymentId: props.selectedPayment.paymentId || '',
  resource: props.selectedPayment.id || '',
  resourceType: null,
  attachmentType: null,
  employee: null,
  fileName: '',
  path: '',
  remark: '',
  status: 'ACTIVE'
})

const objApis = ref({
  resourceType: { moduleApi: 'payment', uriApi: 'resource-type' },
  attachmentType: { moduleApi: 'payment', uriApi: 'attachment-type' },
  employee: { moduleApi: 'settings', uriApi: 'manage-employee' },
})

const Columns: IColumn[] = [
  { field: 'attachment_id', header: 'Id', type: 'text', width: '200px' },
  { field: 'type', header: 'Type', type: 'select', width: '200px' },
  { field: 'filename', header: 'Filename', type: 'text', width: '200px' },
  { field: 'remark', header: 'Remark', width: '200px' },

]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Payment Attachment',
  moduleApi: 'payment',
  uriApi: 'master-payment-attachment',
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
const payloadOnChangePage = ref<PageState>()

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'ASC'
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

const ListItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')

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

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
}

async function getList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    ListItems.value = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false, room_rate_id: iterator?.roomRate?.room_rate_id }]
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
async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  payload.value.filter = [...parseFilter || []]
  getList()
}

// async function getAttachmentTypeList() {
//   try {
//     const payload
//       = {
//         filter: [],
//         query: '',
//         pageSize: 200,
//         page: 0,
//         sortBy: 'createdAt',
//         sortType: 'DES'
//       }

//     attachmentTypeList.value = []
//     const response = await GenericService.search(confattachmentTypeListApi.moduleApi, confattachmentTypeListApi.uriApi, payload)
//     const { data: dataList } = response
//     for (const iterator of dataList) {
//       attachmentTypeList.value = [...attachmentTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
//     }
//   }
//   catch (error) {
//     console.error('Error loading Attachment Type list:', error)
//   }
// }

interface DataListItem {
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

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.name}`,
    status: data.status
  }
}
async function getResourceTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  resourceTypeList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [], queryObj, mapFunction)
}

async function getAttachmentTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  attachmentTypeList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [], queryObj, mapFunction)
}

interface DataListItemEmployee {
  id: string
  firstName: string
  lastName: string
  email: string
}

interface ListItemEmployee {
  id: string
  name: string
  status: boolean | string
}

function mapFunctionEmployee(data: DataListItemEmployee): ListItemEmployee {
  return {
    id: data.id,
    name: `${data.firstName} ${data.lastName}`,
    status: 'Active'
  }
}

async function getEmployeeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  attachmentTypeList.value = await getDataList<DataListItemEmployee, ListItemEmployee>(moduleApi, uriApi, [], queryObj, mapFunctionEmployee)
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    if (typeof payload.path === 'object' && payload.path !== null && payload.path?.files && payload.path?.files.length > 0) {
      const file = payload.path.files[0]
      if (file) {
        const objFile = await getUrlOrIdByFile(file)
        payload.path = objFile && typeof objFile === 'object' ? objFile.id : objFile.url
      }
      else {
        payload.path = ''
      }
    }
    else {
      payload.path = ''
    }
    payload.attachmentType = item.attachmentType?.id
    payload.resourceType = item.resourceType?.id
    payload.status = 'ACTIVE'
    payload.employee = typeof item.employee === 'object' ? item.employee.id : item.employee
    delete payload.event
    delete payload.paymentId
    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.type = item.type?.id
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
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

async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
  const file = event.files[0]
  // objFile.value = file
  listFields[fieldKey] = file
}
function clearFile(listField: any, fieldKey: any) {
  console.log(listField, fieldKey)

  // listField[fieldKey] = null
  // objFile.value = null
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
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.id = response.id
        item.value.type = response.type
        item.value.filename = response.filename
        item.value.file = response.file
        item.value.remark = response.remark
        item.value.invoice = response.invoice
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
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-screen"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" :style="{ width: '80%' }"
    @hide="closeDialog"
  >
    <template #default>
      <div class="grid p-fluid formgrid">
        <div class="col-12 order-1 md:order-0 md:col-9 pt-5">
          <Accordion :active-index="0" class="mb-2 card p-0">
            <AccordionTab>
              <template #header>
                <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full">
                  <div>Filters</div>
                  <div>
                    <strong class="mx-2">Payment:</strong>
                    <span>{{ props.selectedPayment.paymentId }}</span>
                  </div>
                </div>
              </template>

              <div class="flex gap-4 flex-column lg:flex-row">
                <div class="flex align-items-center gap-2">
                  <label for="email">Payment:</label>
                  <div class="w-full lg:w-auto">
                    <IconField icon-position="left" class="w-full">
                      <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                      <InputIcon class="pi pi-search" />
                    </IconField>
                  </div>
                </div>
                <div class="flex align-items-center">
                  <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearch" @click="searchAndFilter" />
                  <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :loading="loadingSearch" @click="clearFilterToSearch" />
                </div>
              </div>
            </AccordionTab>
          </Accordion>
          <DynamicTable
            class="card p-0"
            :data="isCreationDialog ? listItems as any : ListItems" :columns="Columns"
            :options="options" :pagination="Pagination"
            @update:clicked-item="getItemById($event)"
            @on-confirm-create="clearForm"
            @on-change-pagination="payloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter" @on-list-item="ResetListItems" @on-sort-field="OnSortField"
          />
        </div>
        <div class="col-12 order-2 md:order-0 md:col-3 pt-5">
          <div>
            <div class="font-bold text-lg px-4 bg-primary custom-card-header">
              {{ formTitle }}
            </div>
            <div class="card">
              <EditFormV2
                :key="formReload"
                :fields="fieldsV2"
                :item="item"
                :show-actions="true"
                :loading-save="loadingSaveAll"
                @submit-form="requireConfirmationToSave"
                @on-confirm-create="clearForm"
                @cancel="clearForm"
                @delete="requireConfirmationToDelete($event)"
                @submit="requireConfirmationToSave($event)"
              >
                <template #field-resourceType="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="data.resourceType"
                    :suggestions="resourceTypeList"
                    @change="($event) => {
                      onUpdate('resourceType', $event)
                    }" @load="($event) => getResourceTypeList(objApis.resourceType.moduleApi, objApis.resourceType.uriApi, $event)"
                  >
                    <template #option="props">
                      <span>{{ props.item.name }}</span>
                    </template>
                  </DebouncedAutoCompleteComponent>
                </template>

                <template #field-attachmentType="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="data.attachmentType"
                    :suggestions="attachmentTypeList"
                    @change="($event) => {
                      onUpdate('attachmentType', $event)
                    }" @load="($event) => getAttachmentTypeList(objApis.attachmentType.moduleApi, objApis.attachmentType.uriApi, $event)"
                  >
                    <template #option="props">
                      <span>{{ props.item.name }}</span>
                    </template>
                  </DebouncedAutoCompleteComponent>
                </template>

                <template #field-employee="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="data.employee"
                    :suggestions="attachmentTypeList"
                    @change="($event) => {
                      onUpdate('employee', $event)
                    }" @load="($event) => getEmployeeList(objApis.employee.moduleApi, objApis.employee.uriApi, $event)"
                  >
                    <template #option="props">
                      <span>{{ props.item.name }}</span>
                    </template>
                  </DebouncedAutoCompleteComponent>
                </template>

                <template #field-path="{ item: data, onUpdate }">
                  <FileUpload
                    v-if="!loadingSaveAll" :max-file-size="1000000" :multiple="false" auto custom-upload
                    @uploader="($event: any) => {
                      customBase64Uploader($event, fieldsV2, 'path');
                      onUpdate('path', $event)
                      if ($event && $event.files.length > 0) {
                        onUpdate('fileName', $event?.files[0]?.name)
                      }
                      else {
                        onUpdate('fileName', '')
                      }

                    }"
                  >
                    <template #header="{ chooseCallback }">
                      <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                        <div class="flex gap-2">
                          <Button id="btn-choose" class="p-2" icon="pi pi-plus" text @click="chooseCallback()" />
                          <Button
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
                </template>

                <template #form-footer="props">
                  <Button
                    v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
                    @click="props.item.submitForm($event)"
                  />
                  <Button
                    v-if="true"
                    v-tooltip.top="'Show History'" class="w-3rem sticky" icon="pi pi-book"
                    @click="openDialogHistory = true"
                  />
                </template>
              </EditFormV2>
            </div>
          </div>
        </div>
      </div>
    </template>
  </Dialog>

  <Dialog
    v-model:visible="openDialogHistory"
    modal
    class="mx-3 sm:mx-0"
    content-class="border-round-bottom border-top-1 surface-border"
    :style="{ width: '60%' }"
    @hide="openDialogHistory = false"
  >
    <template #header>
      <div class="flex justify-content-between">
        <h5 class="m-0">
          Attachment Status History
        </h5>
      </div>
    </template>
    <template #default>
      <div class="p-fluid pt-3">
        <DynamicTable
          class="card p-0"
          :data="isCreationDialog ? listItems as any : ListItems"
          :columns="Columns"
          :options="options"
          :pagination="Pagination"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="ParseDataTableFilter"
          @on-sort-field="OnSortField"
        />
      </div>
    </template>
  </Dialog>
</template>
