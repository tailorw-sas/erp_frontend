<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'

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
  selectedInvoice: {
    type: String,
    required: true
  }
})

const attachmentTypeList = ref<any[]>([])
const confattachmentTypeListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-attachment-type',
})

const formReload = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()

const loadingDelete = ref(false)

const idItem = ref('')
const item = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachment_id: ''
})

const itemTemp = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachment_id: ''
})
const toast = useToast()

const Fields: Array<Container> = [
  {
    childs: [
      {
        field: 'invoice.invoice_id',
        header: 'Resource',
        dataType: 'number',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        disabled: true
      },
      {
        field: 'invoice.invoiceType',
        header: 'Resource Type',
        dataType: 'number',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        disabled: true
      },

      {
        field: 'type',
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
        field: 'filename',
        header: 'Filename',
        dataType: 'text',
        class: 'field col-12 required',
        headerClass: 'mb-1',

      },
      {
        field: 'file',
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

    ],
    containerClass: 'w-full'
  }

]

const fieldsV2: Array<FieldDefinitionType> = [
  {
    field: 'invoice.invoice_id',
    header: 'Resource',
    dataType: 'number',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true
  },
  {
    field: 'invoice.invoiceType',
    header: 'Resource Type',
    dataType: 'number',
    class: 'field col-12 md: required',
    headerClass: 'mb-1',
    disabled: true
  },

  {
    field: 'type',
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
    field: 'filename',
    header: 'Filename',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',

  },
  {
    field: 'file',
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

const Columns: IColumn[] = [
  { field: 'attachment_id', header: 'Id', type: 'text', width: '200px' },
  { field: 'type', header: 'Type', type: 'select', width: '200px' },
  { field: 'filename', header: 'Filename', type: 'text', width: '200px' },
  { field: 'remark', header: 'Remark', width: '200px' },

]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
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
  sortBy: 'code',
  sortType: 'ASC'
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

const ListItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')

async function ResetListItems() {
  Payload.value.page = 0
}

function OnSortField(event: any) {
  if (event) {
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
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

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, Payload.value)

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
async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  getList()
}

async function getAttachmentTypeList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    attachmentTypeList.value = []
    const response = await GenericService.search(confattachmentTypeListApi.moduleApi, confattachmentTypeListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      attachmentTypeList.value = [...attachmentTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    payload.type = item.type?.id
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

  console.log(idItem)

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
      if (props.isCreationDialog) {
        await props.addItem(item)
        clearForm()
        return loadingSaveAll.value = false
      }
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
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-screen w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true"
    style="width: 800px;" @hide="closeDialog"
  >
    <template #default>
      <div class="grid p-fluid formgrid">
        <div class="col-12 order-1 md:order-0 md:col-9 pt-5">
          <Accordion :active-index="0" class="mb-2 card p-0">
            <AccordionTab header="Filters">
              <div class="flex gap-4 flex-column lg:flex-row">
                <div class="flex align-items-center gap-2">
                  <label for="email">Invoice:</label>
                  <div class="w-full lg:w-auto">
                    <IconField icon-position="left" class="w-full">
                      <InputText type="text" placeholder="Search" class="w-full" />
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
            @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter" @on-list-item="ResetListItems" @on-sort-field="OnSortField"
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
                <template #field-type="{ item: data, onUpdate }">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
                    :model="data.type" :suggestions="attachmentTypeList" @change="($event) => {
                      onUpdate('type', $event)
                    }" @load="($event) => getAttachmentTypeList($event)"
                  >
                    <template #option="props">
                      <span>{{ props.item.code }} - {{ props.item.name }}</span>
                    </template>
                  </DebouncedAutoCompleteComponent>
                </template>
                <template #form-footer="props">
                  <Button
                    v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
                    @click="props.item.submitForm($event)"
                  />
                </template>
              </EditFormV2>
            </div>
          </div>

          <EditFormV2WithContainer
            v-if="false"
            :key="formReload"
            :fields-with-containers="Fields"
            :item="item"
            :show-actions="true"
            :loading-save="loadingSaveAll"
            class="w-fit h-fit m-4 card p-0"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-type="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
                :model="data.type" :suggestions="attachmentTypeList" @change="($event) => {
                  onUpdate('type', $event)
                }" @load="($event) => getAttachmentTypeList($event)"
              >
                <template #option="props">
                  <span>{{ props.item.code }} - {{ props.item.name }}</span>
                </template>
              </DebouncedAutoCompleteComponent>
            </template>
            <template #form-footer="props">
              <Button
                v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
                @click="props.item.submitForm($event)"
              />
            </template>
          </EditFormV2WithContainer>
        </div>
      </div>
    </template>
  </Dialog>
</template>
